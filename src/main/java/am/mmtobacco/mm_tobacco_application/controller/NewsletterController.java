package am.mmtobacco.mm_tobacco_application.controller;

import am.mmtobacco.mm_tobacco_application.model.NewsletterSubscriber;
import am.mmtobacco.mm_tobacco_application.service.EmailService;
import am.mmtobacco.mm_tobacco_application.service.NewsletterSubscriberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/newsletter")
public class NewsletterController {
    private final EmailService emailService;
    private final NewsletterSubscriberService newsletterSubscriberService;

    public NewsletterController(EmailService emailService, NewsletterSubscriberService newsletterSubscriberService) {
        this.emailService = emailService;
        this.newsletterSubscriberService = newsletterSubscriberService;
    }

    @PostMapping
    public String signUpForNewsletter(@RequestParam String email,
                                      RedirectAttributes redirectAttributes,
                                      @RequestHeader(value = "referer", required = false) String referer) {
        if (email == null || email.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "❌ Email is required.");
            return referer != null ? "redirect:" + referer : "redirect:/"; // Redirect back or to home
        }

        newsletterSubscriberService.save(new NewsletterSubscriber(email));

        String userEmailBody = "Hi!\n\nYou have successfully registered to receive our news. We will inform you about new products!";
        emailService.sendEmail(email, "Your request has been accepted.", userEmailBody);

        String adminEmailBody = String.format("New request:\n\nE-mail: %s \nSubscribed to receive information about new products.", email);
        emailService.sendEmail("companies.and.employees@gmail.com", "New request on the site", adminEmailBody);

        redirectAttributes.addFlashAttribute("message", "✅ You have successfully subscribed to the newsletter!");

        return referer != null ? "redirect:" + referer : "redirect:/"; // Redirect back to the page user was on
    }
}