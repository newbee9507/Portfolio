package shop.server.item.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import shop.server.item.dto.ItemResponseDto;
import shop.server.item.entity.Item;

import java.util.List;
import java.util.Map;

import static shop.server.item.entity.QItem.*;

@RequiredArgsConstructor
public class DslItemRepositoryImpl implements DslItemRepository{

    private final JPAQueryFactory queryFactory;
    private final String[] conditions = new String[]{"name", "company", "minPrice", "maxPrice"};

    @Override
    public List<ItemResponseDto> findByCondition(Map<String, Object> paramMap) {
        /** pramMap.keySet == conditions */
        String name = (String) paramMap.get("name");
        String company = (String) paramMap.get("company");
        String minPrice = (String) paramMap.get("minPrice");
        String maxPrice = (String) paramMap.get("maxPrice");

        return queryFactory.select(Projections.fields(
                        ItemResponseDto.class,
                        item.itemId,
                        item.name,
                        item.company,
                        item.price,
                        item.stock))
                .from(item)
                .where(isBetweenPrice(minPrice, maxPrice)
                        .and(isNameContains(name))
                        .and(isCompanyContains(company)))
                .orderBy(item.price.asc())
                .fetch();
    }


    private BooleanExpression isBetweenPrice(String min, String max) {
        int minPrice = min == null ? 0 : Integer.parseInt(min);
        int maxPrice = max == null ? Integer.MAX_VALUE : Integer.parseInt(max);
        return item.price.between(minPrice, maxPrice);
    }

    private BooleanExpression isNameContains(String name) {
        return name != null ? item.name.containsIgnoreCase(name) : null;
    }

    private BooleanExpression isCompanyContains(String company) {
        return company != null ? item.company.containsIgnoreCase(company) : null;
    }

}

