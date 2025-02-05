package am.mmtobacco.mm_tobacco_application.controller;

import am.mmtobacco.mm_tobacco_application.model.Product;
import am.mmtobacco.mm_tobacco_application.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/api/catalogue")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /** 📌 Страница каталога с пагинацией (Thymeleaf) */
    @GetMapping
    public String catalogue(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {

        Page<Product> productPage = productService.getProducts(page, size);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());

        return "catalogue"; // Возвращает Thymeleaf-шаблон
    }

    /** 📌 API: Получить все продукты */
    @GetMapping("/products")
    @ResponseBody
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    /** 📌 API: Получить продукт по ID */
    @GetMapping("/products/{id}")
    @ResponseBody
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    /** 📌 API: Создать продукт */
    @PostMapping("/products")
    @ResponseBody
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        Product savedProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    /** 📌 API: Обновить продукт */
    @PutMapping("/products/{id}")
    @ResponseBody
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    /** 📌 API: Удалить продукт */
    @DeleteMapping("/products/{id}")
    @ResponseBody
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    /** 📌 API: Получить продукты с пагинацией */
    @GetMapping("/products/paginated")
    @ResponseBody
    public Page<Product> getPaginatedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        return productService.getProducts(page, size);
    }

    /** 📌 API: Фильтр продуктов */
    @GetMapping("/products/filter")
    @ResponseBody
    public List<Product> filterProducts(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String category) {
        return productService.filterProducts(minPrice, maxPrice, category);
    }

    /** 📌 API: Фильтр + пагинация */
    @GetMapping("/products/paginated-filtered")
    @ResponseBody
    public Page<Product> getFilteredAndPaginatedProducts(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        return productService.filterProductsWithPagination(minPrice, maxPrice, category, page, size);
    }
}

