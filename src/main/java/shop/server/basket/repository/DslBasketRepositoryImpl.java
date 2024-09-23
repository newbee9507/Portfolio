package shop.server.basket.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import shop.server.basket.entity.Basket;

import static shop.server.basket.entity.QBasket.basket;
import static shop.server.basket.entity.QBasketItem.basketItem;

@RequiredArgsConstructor
public class DslBasketRepositoryImpl implements DslBasketRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Basket findByIdFetchBasketItem(Long basketId) {

        return queryFactory.select(basket)
                .from(basket)
                .leftJoin(basket.basketItems, basketItem).fetchJoin()
                .leftJoin(basketItem.item).fetchJoin()
                .where(basket.basketId.eq(basketId))
                .fetchOne();


    }
}
