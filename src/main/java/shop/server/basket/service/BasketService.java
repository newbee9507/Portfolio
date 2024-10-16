package shop.server.basket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.server.basket.dto.BasketResponseDto;
import shop.server.basket.entity.Basket;
import shop.server.basket.mapper.BasketMapper;
import shop.server.basket.repository.BasketRepository;
import shop.server.basket.dto.BasketItemResponseDto;
import shop.server.basket.entity.BasketItem;
import shop.server.exception.error.basket.BasketExMessage;
import shop.server.exception.error.basket.BasketException;
import shop.server.exception.error.member.MemberExMessage;
import shop.server.exception.error.member.MemberException;
import shop.server.item.entity.Item;
import shop.server.item.service.ItemService;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BasketService {

    private final BasketRepository repository;
    private final BasketMapper mapper;
    private final ItemService itemService;

    @Transactional(readOnly = true)
    public BasketResponseDto info(Long id) {
        Basket basket = repository.findByIdFetchBasketItem(id);
        List<BasketItemResponseDto> list = mapper.basketToBasketItemResponseDtoList(basket);
        return mapper.basketToBasketResponseDto(basket, list);
    }

    public BasketResponseDto input(Long basketId, Long itemId, Integer quantity) {
        Basket memberBasket = repository.findByIdFetchBasketItem(basketId);
        Item item = itemService.findById(itemId);
        BasketItem newBasketItem = new BasketItem();
        newBasketItem.mappingItem(item, quantity);

        if (memberBasket.isAlreadyHave(newBasketItem)) {
            List<BasketItem> realBasket = memberBasket.getBasketItems();

            for (BasketItem oldBasketItem : realBasket) {
                if (oldBasketItem.equals(newBasketItem)) {
                    oldBasketItem.modifyQuantity(newBasketItem.getQuantity());
                    return BasketResponseDto(memberBasket);
                }
            }
        }
        newBasketItem.mappingToBasket(memberBasket);
        return BasketResponseDto(memberBasket);
    }

    public BasketResponseDto modifyQuantity(Long basketId, Long itemId, Integer quantity) {
        Basket basket = repository.findByIdFetchBasketItem(basketId);
        Long targetItemId = itemService.checkExistsAndGetId(itemId);
        List<BasketItem> basketItems = basket.getBasketItems();

        for (BasketItem basketItem : basketItems) {
            Item itemInBasket = basketItem.getItem();
            if (itemInBasket.getItemId().equals(targetItemId)) {
                Integer afterQuantity = basketItem.modifyQuantity(quantity);
                if(afterQuantity<=0) basketItem.modifyQuantity( Math.abs(afterQuantity) + 1);

                return BasketResponseDto(basket);
            }
        }
        throw new BasketException(HttpStatus.BAD_REQUEST, BasketExMessage.NOT_EXIST);
    }

    public BasketResponseDto removeToBasket(Long basketId, Long itemId) {
        Basket basket = repository.findByIdFetchBasketItem(basketId);
        Long targetItemId = itemService.checkExistsAndGetId(itemId);
        List<BasketItem> basketItems = basket.getBasketItems();

        for (BasketItem basketItem : basketItems) {
            Item itemInBasket = basketItem.getItem();
            if (itemInBasket.getItemId().equals(targetItemId)) {
                basketItems.remove(mapper.itemToBasketItem(itemInBasket));

                return BasketResponseDto(basket);
            }
        }
        throw new BasketException(HttpStatus.BAD_REQUEST, BasketExMessage.NOT_EXIST);
    }

    public BasketResponseDto clearBasket(Long id) {
        Basket basket = repository.findByIdFetchBasketItem(id);
        List<BasketItem> basketItems = basket.getBasketItems();

        List<Long> basketItemIds = basketItems.stream().map(BasketItem::getBasketItemId).toList();
        repository.deleteAllBasketItems(basketItemIds);

        return BasketResponseDto(basket);
    }

    private BasketResponseDto BasketResponseDto(Basket basket) {
        return mapper.basketToBasketResponseDto(basket, mapper.basketToBasketItemResponseDtoList(basket));
    }
}
