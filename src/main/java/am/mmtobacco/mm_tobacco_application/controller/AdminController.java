package am.mmtobacco.mm_tobacco_application.controller;

import am.mmtobacco.mm_tobacco_application.model.Contacts;
import am.mmtobacco.mm_tobacco_application.model.Product;
import am.mmtobacco.mm_tobacco_application.service.ContactFormService;
import am.mmtobacco.mm_tobacco_application.service.ProductService;
import org.springdoc.webmvc.core.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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

        Page<Product> productPage = productService.getProducts(PageRequest.of(page, size));

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());

        return "admin/products";
    }

    @GetMapping("/requests")
    public String manageRequests(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(defaultValue = "all") String status,
                                 @RequestParam(required = false) String day,
                                 Model model) {

        LocalDate date = null;
        if (day != null && !day.isEmpty()) {
            try {
                date = LocalDate.parse(day);
            } catch (DateTimeParseException e) {
                model.addAttribute("error", "Invalid date format");
            }
        }

        Page<Contacts> requestPage = contactFormService.getRequests(status, date, page, size);

        model.addAttribute("requests", requestPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", requestPage.getTotalPages());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedDate", day);

        return "admin/requests";
    }

    @GetMapping("/request-details/{id}")
    public String requestDetails(@PathVariable Long id, Model model) {
        Contacts request = contactFormService.getRequestById(id);
        if (request == null) {
            return "redirect:/admin/requests";
        }
        model.addAttribute("request", request);
        return "admin/requestDetails";
    }

    @PostMapping("/update-request/{id}")
    public String updateRequestStatus(@PathVariable Long id,
                                      @RequestParam("status") String status,
                                      @RequestParam("message") String message) {

        Contacts request = contactFormService.getRequestById(id);
        if (request != null) {
            request.setStatus(status);
            request.setMessage(message);
            contactFormService.updateForm(request);
        }
        return "redirect:/admin/request-details/" + id;
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

        String uploadDir = "src/main/resources/static/uploads/";
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        Files.write(filePath, image.getBytes());

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setImageUrl("/uploads/" + fileName);

        productService.createProduct(product);

        model.addAttribute("imageUrl", "/uploads/" + fileName);
        model.addAttribute("message", "Product added successfully!");

        return "/admin/addProduct";
    }

    @GetMapping("/product-details/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/admin/products";
        }
        model.addAttribute("product", product);
        return "admin/productDetails";
    }

    @GetMapping("/edit-product/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/admin/products";
        }
        model.addAttribute("product", product);
        return "admin/editProduct";
    }

    @PostMapping("/update-product/{id}")
    public String updateProduct(@PathVariable Long id,
                                @RequestParam("name") String name,
                                @RequestParam("description") String description,
                                @RequestParam("price") double price,
                                @RequestParam("category") String category,
                                @RequestParam(value = "image", required = false) MultipartFile image,
                                Model model) throws IOException {

        Product existingProduct = productService.getProductById(id);
        if (existingProduct == null) {
            model.addAttribute("errorMessage", "Product not found!");
            return "/admin/productDetails";
        }

        existingProduct.setName(name);
        existingProduct.setDescription(description);
        existingProduct.setCategory(category);
        existingProduct.setPrice(price);

        if (image != null && !image.isEmpty()) {
            String uploadDir = "src/main/resources/static/uploads/";
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.write(filePath, image.getBytes());

            existingProduct.setImageUrl("/uploads/" + fileName);
        }

        productService.createProduct(existingProduct);

        model.addAttribute("message", "Product updated successfully!");
        return "redirect:/admin/products";
    }




    @DeleteMapping("/delete-product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Product deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting product: " + e.getMessage());
        }
    }
}
