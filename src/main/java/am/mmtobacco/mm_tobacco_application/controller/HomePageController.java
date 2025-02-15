package am.mmtobacco.mm_tobacco_application.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import am.mmtobacco.mm_tobacco_application.model.Product;
import am.mmtobacco.mm_tobacco_application.service.ProductService;
import org.springframework.ui.Model;
import java.util.List;

@Controller
public class HomePageController {
    private final ProductService productService;

    public HomePageController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String redirectToIndex() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String homePage(Model model) {
        List<Product> latestProducts = productService.getLatestProducts(3);
        model.addAttribute("latestProducts", latestProducts);
        return "/index";
    }

    @GetMapping("/age-restricted")
    public String showAccessDeniedPage() {
        return "/age-restricted";
    }
}