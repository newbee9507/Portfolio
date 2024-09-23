package shop.server.basket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.server.basket.entity.Basket;

import java.util.List;
import java.util.Optional;

public interface BasketRepository extends JpaRepository<Basket, Long>, DslBasketRepository {

    @Modifying
    @Query("delete from BasketItem bi where bi.id in :ids")
    int deleteAllBasketItems(@Param("ids") List<Long> ids);

}
