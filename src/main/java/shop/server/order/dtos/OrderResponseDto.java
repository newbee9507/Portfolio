package shop.server.order.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponseDto {

    private Long orderId;

    private String customerId;

    private List<Long> itemList;

    private String address;

    private String orderTime;

    private String arrivalTime;

}
