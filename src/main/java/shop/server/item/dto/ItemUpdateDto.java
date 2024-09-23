package shop.server.item.dto;

import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ItemUpdateDto {

    private String name;

    private String company;

    @Positive
    private Integer price;

    @Positive
    @Range(max = 9999)
    private Integer stock;
}
