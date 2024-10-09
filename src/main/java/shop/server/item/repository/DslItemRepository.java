package shop.server.item.repository;

import org.springframework.stereotype.Component;
import shop.server.item.dto.ItemResponseDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public interface DslItemRepository{

    List<ItemResponseDto> findByCondition(Map<String, Object> paramMap);


}
