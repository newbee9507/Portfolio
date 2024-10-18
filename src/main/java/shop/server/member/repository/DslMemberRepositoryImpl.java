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

import java.util.ArrayList;
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

    @Override
    public String notOptimizationDelete(Long memberId) {
        Member targetMember = queryFactory.select(member)
                .from(member)
                .where(member.memberId.eq(memberId)).fetchOne();

        Basket targetBasket = targetMember.getBasket();
        List<Order> orderList = targetMember.getOrderList();
        List<BasketItem> basketItems = targetBasket.getBasketItems();

        basketItems.clear();
        queryFactory.delete(basket).where(basket.basketId.eq(memberId));
        orderList.stream().map(Order::getItemList).forEach(List::clear);
        queryFactory.delete(order);
        queryFactory.delete(member).where(member.memberId.eq(memberId));
        return "ok";
    }

    @Override
    public MemberResponseDto delete(Long memberId) {
        Member targetMember = queryFactory.select(member)
                .from(member)
                .innerJoin(member.basket, basket).fetchJoin()
                .leftJoin(basket.basketItems, basketItem).fetchJoin()
                .where(member.memberId.eq(memberId)).fetchOne();
        List<Order> orderList = queryFactory.select(order)
                .from(order)
                .innerJoin(member).on(order.in(member.orderList))
                .innerJoin(order.itemList, orderItem).fetchJoin()
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
                .innerJoin(member.orderList).fetchJoin()
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
