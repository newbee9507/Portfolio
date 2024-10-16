package shop.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class homeController {

    @GetMapping("/myShop/home")
    public String homePage() {
        return "홈페이지";
    }

    @GetMapping("/myShop/test")
    public String test() {
        return "good!";
    }
}
