package am.mmtobacco.mm_tobacco_application.controller;

import am.mmtobacco.mm_tobacco_application.model.Contacts;
import am.mmtobacco.mm_tobacco_application.model.Product;
import am.mmtobacco.mm_tobacco_application.service.ContactFormService;
import am.mmtobacco.mm_tobacco_application.service.ProductService;
import org.springdoc.webmvc.core.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final ProductService productService;
    private final ContactFormService contactFormService;

    @Autowired
    public AdminController(ProductService productService, ContactFormService contactFormService) {
        this.productService = productService;
        this.contactFormService = contactFormService;
    }

    @GetMapping("/login")
    public String adminLogin() {
        return "admin/login";
    }

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String manageProducts(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "6") int size,
                                 Model model) {

        // Fetch products with pagination
        Page<Product> productPage = productService.getProducts(PageRequest.of(page, size));

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());

        return "admin/products";
    }

    @GetMapping("/requests")
    public String manageRequests(Model model) {
        List<Contacts> requests = contactFormService.getAllRequests();
        model.addAttribute("requests", requests);
        return "admin/requests";
    }

    @GetMapping("/add-product")
    public String addProductForm() {
        return "admin/addProduct";
    }

    @PostMapping("/add-product")
    public String addProduct(@RequestParam("name") String name,
                             @RequestParam("description") String description,
                             @RequestParam("price") double price,
                             @RequestParam("category") String category,
                             @RequestParam("image") MultipartFile image,
                             Model model) throws IOException {

        // 📌 Step 1: Save Image in `static/uploads`
        String uploadDir = "src/main/resources/static/uploads/";
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        // Generate unique file name
        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        Files.write(filePath, image.getBytes());

        // 📌 Step 2: Save Product in Database
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setImageUrl("/uploads/" + fileName); // Relative URL for frontend

        productService.createProduct(product);

        // 📌 Step 3: Send Image Path to `add-product.html`
        model.addAttribute("imageUrl", "/uploads/" + fileName);
        model.addAttribute("message", "Product added successfully!");

        return "/admin/addProduct";
    }
}
