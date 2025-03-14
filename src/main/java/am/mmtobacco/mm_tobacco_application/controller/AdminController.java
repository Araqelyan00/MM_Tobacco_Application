package am.mmtobacco.mm_tobacco_application.controller;

import am.mmtobacco.mm_tobacco_application.model.Contacts;
import am.mmtobacco.mm_tobacco_application.model.NewsletterSubscriber;
import am.mmtobacco.mm_tobacco_application.model.Product;
import am.mmtobacco.mm_tobacco_application.service.ContactFormService;
import am.mmtobacco.mm_tobacco_application.service.EmailService;
import am.mmtobacco.mm_tobacco_application.service.NewsletterSubscriberService;
import am.mmtobacco.mm_tobacco_application.service.ProductService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final EmailService emailService;
    private final ProductService productService;
    private final ContactFormService contactFormService;
    private final NewsletterSubscriberService newsletterSubscriberService;

    @Autowired
    public AdminController(EmailService emailService, ProductService productService, ContactFormService contactFormService, NewsletterSubscriberService newsletterSubscriberService) {
        this.emailService = emailService;
        this.productService = productService;
        this.contactFormService = contactFormService;
        this.newsletterSubscriberService = newsletterSubscriberService;
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

    @GetMapping("subscribers")
    public String manageSubscribers(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    Model model) {
        Page<NewsletterSubscriber> subscribers = newsletterSubscriberService.getSubscribers(page, size);
        model.addAttribute("subscribers", subscribers.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", subscribers.getTotalPages());

        return "admin/newsSubscribers";
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
                             @RequestParam(value = "image", required = false) MultipartFile image,
                             @RequestParam(value = "imageUrl", required = false) String imageUrl,
                             Model model) throws IOException {

        String finalImageUrl = "";

        if (image != null && !image.isEmpty()) {
            String uploadDir = "src/main/resources/static/uploads/";
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.write(filePath, image.getBytes());

            finalImageUrl = "/uploads/" + fileName;
        } else if (imageUrl != null && !imageUrl.isBlank()) {
            finalImageUrl = imageUrl;
        } else {
            model.addAttribute("message", "Please provide an image file or URL.");
            return "/admin/addProduct";
        }

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setImageUrl(finalImageUrl);

        productService.createProduct(product);

        model.addAttribute("imageUrl", finalImageUrl);
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


    @PostMapping("/notify-all")
    public String notifyAllSubscribers(@RequestParam String subject,
                                       @RequestParam String message,
                                       @RequestParam(value = "attachment", required = false) MultipartFile attachment,
                                       RedirectAttributes redirectAttributes) {
        List<NewsletterSubscriber> subscribers = newsletterSubscriberService.getAllSubscribers();

        if (subscribers.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "❌ No subscribers found.");
            return "redirect:/admin/subscribers";
        }

        byte[] attachmentBytes = null;
        String attachmentName = null;

        if (attachment != null && !attachment.isEmpty()) {
            try {
                attachmentBytes = attachment.getBytes();
                attachmentName = attachment.getOriginalFilename();
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "❌ Failed to process attachment.");
                return "redirect:/admin/subscribers";
            }
        }

        for (NewsletterSubscriber subscriber : subscribers) {
            emailService.sendEmail(subscriber.getEmail(), subject, message, attachmentBytes, attachmentName);
        }

        redirectAttributes.addFlashAttribute("message", "✅ Newsletter sent successfully!");
        return "redirect:/admin/subscribers";
    }


    @GetMapping("/notify-page")
    public String notifyPage() {
        return "admin/notification";
    }

    @PostMapping("/subscribers/delete")
    public String deleteSubscriber(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            newsletterSubscriberService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "✅ Subscriber deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Failed to delete subscriber.");
        }
        return "redirect:/admin/subscribers";
    }

}
