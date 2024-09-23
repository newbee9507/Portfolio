package shop.server.item.repository;

import shop.server.item.dto.ItemResponseDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DslItemRepository{

    List<ItemResponseDto> findByCondition(Map<String, Object> paramMap);


}
