package shop.server.basket.repository;

import shop.server.basket.entity.Basket;

public interface DslBasketRepository {

    Basket findByIdFetchBasketItem(Long basketId);
}
