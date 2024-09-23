package shop.server.member.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.server.auth.dto.MemberDetailDto;
import shop.server.basket.entity.Basket;
import shop.server.basket.entity.BasketItem;
import shop.server.member.dtos.MemberResponseDto;
import shop.server.member.dtos.MemberSaveDto;
import shop.server.member.entity.Member;
import shop.server.order.entity.Order;
import shop.server.order.entity.OrderItem;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static shop.server.basket.entity.QBasket.basket;
import static shop.server.basket.entity.QBasketItem.basketItem;
import static shop.server.member.entity.QMember.member;
import static shop.server.order.entity.QOrder.order;
import static shop.server.order.entity.QOrderItem.orderItem;

@Slf4j
@RequiredArgsConstructor
public class DslMemberRepositoryImpl implements DslMemberRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public HashMap<Boolean, String> checkExistIdOrNickName(MemberSaveDto saveDto) {
        HashMap<Boolean, String> result = new HashMap<>();
        String id = saveDto.getId();
        String nickName = saveDto.getNickName();

        List<Tuple> fetch = queryFactory.select(member.id, member.nickName)
                .from(member)
                .where(idEq(id).or(nickNameEq(nickName)))
                .fetch();
        if(fetch.size() == 0) return result;

        for (Tuple tuple : fetch) {
            String str1 = tuple.get(member.id);
            String str2 = tuple.get(member.nickName);
            if (str1.equals(id)) result.put(Boolean.TRUE, "idError");
            else if (str2.equals(nickName)) result.put(Boolean.TRUE, "nickError");
        }
        return result;
    }

    @Override
    public MemberDetailDto findMemberToDetailDto(String id) {
        return queryFactory.select(Projections.fields(
                        MemberDetailDto.class,
                        member.memberId,
                        Expressions.asString(id).as("id"),
                        member.password,
                        member.roles))
                .from(member)
                .where(member.id.eq(id))
                .fetchOne();
    }

    @Override
    public Optional<MemberResponseDto> findMemberToResponseDto(Long memberId) {
        MemberResponseDto result =  queryFactory.select(Projections.fields(
                                                MemberResponseDto.class,
                                                Expressions.asNumber(memberId).as("memberId"),
                                                member.id,
                                                member.nickName,
                                                member.point
                                                )).from(member).where(member.memberId.eq(memberId))
                                                .fetchOne();

        return Optional.ofNullable(result);
    }

    public void selectDelete(Long memberId) {
        // 기능실험.txt에 있는 쿼리는 맴버 - 오더 - 오더아이템(서브쿼리로한번에) 바스켓 - 바스켓아이템 -> 여기는 언제삭제쿼리를? -> 다 찾고 가장 마지막에
        Member targetMember = queryFactory.select(member)
                .from(member)
                .leftJoin(member.orderList).fetchJoin() // 외부조인하지 않으면, 주문내역이 없는 회원을 찾아오지 못함
                .join(member.basket).fetchJoin()
                .join(basketItem.basket).fetchJoin()
                .where(member.memberId.eq(memberId)).fetchOne();
        //12:27시점에는 맴버오더리스트 - 오더아이템(서브쿼리로 한번에) - 오더아이템 삭제 - 오더 삭제 -
        /**
         * 쿼리
         * [Hibernate]
         *     select
         *         m1_0.member_id,
         *         m1_0.basket_basket_id,
         *         b1_0.basket_id,
         *         m1_0.id,
         *         m1_0.nick_name,
         *         ol1_0.member_id,
         *         ol1_0.order_id,
         *         ol1_0.address,
         *         ol1_0.arrival_time,
         *         ol1_0.order_time,
         *         ol1_0.total_price,
         *         m1_0.pass_word,
         *         m1_0.point,
         *         m1_0.roles
         *     from
         *         member m1_0
         *     left join
         *         orders ol1_0
         *             on m1_0.member_id=ol1_0.member_id
         *     join
         *         basket b1_0
         *             on b1_0.basket_id=m1_0.basket_basket_id
         *     where
         *         m1_0.member_id=?
         */
        log.info("멤버오더리스트==================================================================");

        /**
         *  오더먼저 삭제시 오더아이템 외래키 제약조건 위배
         *  List<Long> orderIdList = member1.getOrderList().stream().map(Order::getOrderId).toList();
         *  queryFactory.delete(order).where(order.orderId.in(orderIdList)).execute();
         */
        List<Long> OrderItemIdList =
                targetMember.getOrderList().stream().map(Order::getItemList).flatMap(List::stream).map(OrderItem::getOrderItemId).toList();
        long oicount = queryFactory.delete(orderItem).where(orderItem.orderItemId.in(OrderItemIdList)).execute();
        /** 쿼리
         * [Hibernate]
         *     select
         *         il1_0.order_id,
         *         il1_0.order_item_id,
         *         il1_0.item_id,
         *         il1_0.quantity
         *     from
         *         order_item il1_0
         *     where
         *         il1_0.order_id in (select
         *             ol1_0.order_id
         *         from
         *             member m1_0
         *         join
         *             orders ol1_0
         *                 on m1_0.member_id=ol1_0.member_id
         *         where
         *             m1_0.member_id=?)
         * [Hibernate]
         *     delete oi1_0
         *     from
         *         order_item oi1_0
         *     where
         *         oi1_0.order_item_id in (?, ?, ?)
         */
        log.info("삭제된 오더아이템 = {},======================================================================", oicount);

        List<Long> orderIdList = targetMember.getOrderList().stream().map(Order::getOrderId).toList();
        long ocount = queryFactory.delete(order).where(order.orderId.in(orderIdList)).execute();
        log.info("삭제된 오더 = {}==============================================================",ocount);

        Basket targetBasket = targetMember.getBasket();
        log.info("targetBasket = {}", targetBasket.getBasketId());
        List<BasketItem> basketItems = targetBasket.getBasketItems();
        for (BasketItem item : basketItems) {
            log.info("basketItemList = {}", item.getBasketItemId());
        }
        /**
         * 페치조인 있을떄 + @Fetch(FetchMode.SUBSELECT) 여부에 따라 다름
         * 오더들삭제===========================================================================
         * 2024-09-21T13:00:24.851+09:00  INFO 4100 --- [nio-8080-exec-2] s.s.m.r.DslMemberRepositoryImpl          : targetBasket = 4
         * [Hibernate]
         *     select
         *         bi1_0.basket_id,
         *         bi1_0.basket_item_id,
         *         bi1_0.item_id,
         *         bi1_0.quantity
         *     from
         *         basket_item bi1_0
         *     where                                @Fetch가 없으면 단순하게 bi1_0.basket_id=?로 쿼리를 날림
         *         bi1_0.basket_id in (select
         *             b1_0.basket_id
         *         from
         *             member m1_0
         *         join
         *             orders ol1_0
         *                 on m1_0.member_id=ol1_0.member_id
         *         join
         *             basket b1_0
         *                 on b1_0.basket_id=m1_0.basket_basket_id
         *         where
         *             m1_0.member_id=?)
         */
        /**
         * 페치조인없을떄
         * 오더들삭제===========================================================================
         * 2024-09-21T12:59:16.273+09:00  INFO 9492 --- [nio-8080-exec-2] s.s.m.r.DslMemberRepositoryImpl          : targetBasket = 4
         * [Hibernate]
         *     select
         *         b1_0.basket_id
         *     from
         *         basket b1_0
         *     where
         *         b1_0.basket_id=?
         * [Hibernate]
         *     select
         *         bi1_0.basket_id,
         *         bi1_0.basket_item_id,
         *         bi1_0.item_id,
         *         bi1_0.quantity
         *     from
         *         basket_item bi1_0
         *     where
         *         bi1_0.basket_id=?
         * 2024-09-21T12:59:16.280+09:00  INFO 9492 --- [nio-8080-exec-2] s.s.m.r.DslMemberRepositoryImpl          : basketItemList = 22
         * 2024-09-21T12:59:16.280+09:00  INFO 9492 --- [nio-8080-exec-2] s.s.m.r.DslMemberRepositoryImpl          : basketItemList = 26
         * 2024-09-21T12:59:16.280+09:00  INFO 9492 --- [nio-8080-exec-2] s.s.m.r.DslMemberRepositoryImpl          : basketItemList = 27
         * 2024-09-21T12:59:16.280+09:00  INFO 9492 --- [nio-8080-exec-2] s.s.m.r.DslMemberRepositoryImpl          :
         */
        long basketitemcount = queryFactory.delete(basketItem).where(basketItem.in(basketItems)).execute();
        log.info("삭제된 바스켓아이템 = {},=====================================================================",basketitemcount);
        queryFactory.delete(basket).where(basket.basketId.eq(targetMember.getMemberId()));
        log.info("바스켓삭제===========================================================================");
        queryFactory.delete(member).execute();
    }

    @Override
    public MemberResponseDto delete(Long memberId) {
        Member targetMember = queryFactory.select(member)
                .from(member)
                .join(member.basket, basket).fetchJoin()
                .join(basket.basketItems, basketItem).fetchJoin()
                .where(member.memberId.eq(memberId)).fetchOne();

        List<Order> orderList = queryFactory.select(order)
                .from(order)
                .join(member).on(order.in(member.orderList))
                .leftJoin(order.itemList, orderItem).fetchJoin()
                .where(order.client.memberId.eq(memberId)).fetch();

        Basket targetBasket = targetMember.getBasket();
        List<BasketItem> basketItems = targetBasket.getBasketItems();
        List<OrderItem> orderItemList = orderList.stream().map(Order::getItemList).flatMap(List::stream).toList();

        queryFactory.delete(basketItem).where(basketItem.in(basketItems)).execute();
        queryFactory.delete(orderItem).where(orderItem.in(orderItemList)).execute();
        queryFactory.delete(order).where(order.in(orderList)).execute();
        queryFactory.delete(member).where(member.memberId.eq(memberId)).execute();
        queryFactory.delete(basket).where(basket.basketId.eq(memberId)).execute();

        return new MemberResponseDto(targetMember.getMemberId(), targetMember.getId(),
                targetMember.getNickName(), targetMember.getPoint());
    }

    @Override
    public Member findByIdFetchOrderList(Long memberId) {

        return queryFactory.select(member)
                .from(member)
                .leftJoin(member.orderList).fetchJoin()
                .where(member.memberId.eq(memberId))
                .fetchOne();
    }

    private BooleanExpression idEq(String id) {
        return id != null ? member.id.eq(id) : null;
    }

    private BooleanExpression nickNameEq(String nickName) {
        return nickName != null ? member.nickName.eq(nickName) : null;
    }

}
