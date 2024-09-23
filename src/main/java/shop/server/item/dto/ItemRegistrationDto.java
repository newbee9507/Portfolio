package shop.server.item.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ItemRegistrationDto {

    @NotEmpty
    private String name;

    @NotEmpty
    private String company;

    @NotNull
    @Range(min = 0, max = Integer.MAX_VALUE)
    private Integer price;

    @NotNull
    @Range(min = 1,max = 9999)
    private Integer stock;
}
