package am.mmtobacco.mm_tobacco_application.controller.page_controller;

import am.mmtobacco.mm_tobacco_application.model.Product;
import am.mmtobacco.mm_tobacco_application.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CataloguePageController {
    private final ProductService productService;

    public CataloguePageController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/catalogue.html")
    public String catalogue(Model model) {
        List<Product> products = productService.getAllProducts(); // Fetch products
        model.addAttribute("products", products); // Pass products to Thymeleaf template
        return "catalogue"; // Renders catalogue.html
    }
}
