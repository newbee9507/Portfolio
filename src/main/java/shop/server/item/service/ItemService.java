package shop.server.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.server.exception.error.item.ItemExMessage;
import shop.server.exception.error.item.ItemException;
import shop.server.item.dto.*;
import shop.server.item.entity.Item;
import shop.server.item.mapper.ItemMapper;
import shop.server.item.repository.ItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemMapper mapper;
    private final ItemRepository repository;

    @Transactional(readOnly = true)
    public ItemResponseDto information(Long itemId) {
        Item findItem = findById(itemId);
        return mapper.ItemToItemResponseDto(findItem);
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDto> findByConditions(Map<String, Object> paramMap) {
        List<ItemResponseDto> result = repository.findByCondition(paramMap);
        if (result.isEmpty()) {
            throw new ItemException(HttpStatus.NOT_FOUND, ItemExMessage.NOT_EXIST_CONDITION);
        }
        return result;
    }

    public ItemResponseDto addNewItem(ItemRegistrationDto registrationDto) {
        if (repository.existsByName(registrationDto.getName())) {
            throw new ItemException(HttpStatus.BAD_REQUEST, ItemExMessage.ALREADY_EXIST);
        }
        Item newItem = repository.save(mapper.ItemRegistrationDtoToItem(registrationDto));
        return mapper.ItemToItemResponseDto(newItem);
    }

    public ItemResponseDto updateItem(ItemUpdateDto requestDto, Long itemId) {
        if (repository.existsByName(requestDto.getName())) {
            throw new ItemException(HttpStatus.BAD_REQUEST, ItemExMessage.ALREADY_EXIST);
        }
        Item findItem = findById(itemId);
        ItemUpdateDto updateDto = mapper.injectionValueIfNull(findItem, requestDto);
        findItem.updateData(updateDto);
        return mapper.ItemToItemResponseDto(findItem);
    }

    public ItemResponseDto deleteItem(Long itemId) {
        Item findItem = findById(itemId);
        repository.delete(findItem);
        return mapper.ItemToItemResponseDto(findItem);
    }

    public boolean checkStock(Item item, int quantity) {
        List<Item> stockErrorList = new ArrayList<>();
        if (item.getStock() < quantity) {
            stockErrorList.add(item);
            throw new ItemException(HttpStatus.BAD_REQUEST, ItemExMessage.NOT_ENOUGH_STOCK);
        }
        return true;
    }

    public Item findById(Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() -> new ItemException(HttpStatus.NOT_FOUND, ItemExMessage.NOT_EXIST));
    }

    public List<Item> findByIds(List<Long> ids) {
        return repository.findByIds(ids);
    }

    public Long checkExistsAndGetId(Long itemId) {
        return repository.checkExistsAndGetId(itemId)
                .orElseThrow(() -> new ItemException(HttpStatus.BAD_REQUEST, ItemExMessage.NOT_EXIST));
    }
}
