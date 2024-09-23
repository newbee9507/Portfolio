package shop.server.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shop.server.auth.memberdetails.MemberDetails;
import shop.server.exception.error.item.ItemExMessage;
import shop.server.exception.error.item.ItemException;
import shop.server.item.dto.ItemRegistrationDto;
import shop.server.item.dto.ItemResponseDto;
import shop.server.item.dto.ItemUpdateDto;
import shop.server.item.service.ItemService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/myShop/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService service;

    private final String[] conditions = new String[]{"name", "company", "minPrice", "maxPrice"};

    @GetMapping("/info/{itemId}")
    public ResponseEntity<ItemResponseDto> getItemInfo(@PathVariable @Positive Long itemId) {
        ItemResponseDto result = service.information(itemId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/find/conditions")
    public ResponseEntity<List<ItemResponseDto>> findItemsByConditions(
            /** params == conditions */
            @RequestParam Map<String, Object> paramMap) {

        for (String condition : conditions) {
            paramMap.put(condition, paramMap.get(condition));
        }

        List<ItemResponseDto> result = service.findByConditions(paramMap);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/regist")
    public ResponseEntity<ItemResponseDto> addNewItem(@AuthenticationPrincipal MemberDetails admin,
                                                      @RequestBody @Valid ItemRegistrationDto registrationDto) {
        ItemResponseDto result = service.addNewItem(registrationDto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PatchMapping("/update/{itemId}")
    public ResponseEntity<ItemResponseDto> updateItemInfo(@AuthenticationPrincipal MemberDetails admin,
                                                          @RequestBody @Valid ItemUpdateDto updateDto,
                                                          @PathVariable Long itemId) {
        if (updateDto == null) {
            throw new ItemException(HttpStatus.BAD_REQUEST, ItemExMessage.PLEASE_INSERT_DATA);
        }
        ItemResponseDto result = service.updateItem(updateDto, itemId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{itemId}")
    public ResponseEntity<ItemResponseDto> deleteItem(@AuthenticationPrincipal MemberDetails admin,
                                                      @PathVariable Long itemId) {
        ItemResponseDto result = service.deleteItem(itemId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
