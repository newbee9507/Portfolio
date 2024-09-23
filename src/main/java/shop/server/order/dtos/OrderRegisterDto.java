package shop.server.order.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRegisterDto {

    @NotEmpty
    private List<OrderItemDto> dtoList;

    @NotEmpty
    private String address;
}
