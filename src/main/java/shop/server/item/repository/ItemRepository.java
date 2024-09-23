package shop.server.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shop.server.item.entity.Item;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

public interface ItemRepository extends JpaRepository<Item, Long>, DslItemRepository {

    boolean existsByName(String name);

    @Query("select i.id from Item i where i.id = :itemId")
    Optional<Long> checkExistsAndGetId(@Param("itemId") Long itemId);

    @Query("select i from Item i where i.id in :ids")
    List<Item> findByIds(@Param("ids") List<Long> ids);


}
