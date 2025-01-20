package am.mmtobacco.mm_tobacco_application.controller.page_controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductPageController {
    @GetMapping("/product.html")
    public String productPage() {
        return "product";
    }
}
