package shop.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class homeController {

    private int count = 0;

    @GetMapping("/myShop/home")
    public String homePage() {
        return "홈페이지";
    }

    @GetMapping("/myShop/test")
    public String test() {
        count++;
        return "Call count = "+count;
    }
}
