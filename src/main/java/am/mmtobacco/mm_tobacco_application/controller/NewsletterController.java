package am.mmtobacco.mm_tobacco_application.controller;

import am.mmtobacco.mm_tobacco_application.service.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/newsletter")
public class NewsletterController {
    private final EmailService emailService;

    public NewsletterController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public String signUpForNewsletter(@RequestParam String email, RedirectAttributes redirectAttributes) {
        if (email == null || email.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "❌ Email is required.");
            return "redirect:/"; // 🔄 Перенаправление обратно на главную (или на ту же страницу)
        }

        // Отправка e-mail пользователю
        String userEmailBody = "Здравствуйте!\n\nВы успешно зарегистрированы для рассылки наших новинок. Мы проинформируем вас о новинках!";
        emailService.sendEmail(email, "Ваш запрос принят", userEmailBody);

        // Отправка e-mail администратору
        String adminEmailBody = String.format("Новый запрос:\n\nE-mail: %s \nПодписался для информации о новинках.", email);
        emailService.sendEmail("companies.and.employees@gmail.com", "Новый запрос на сайте", adminEmailBody);

        redirectAttributes.addFlashAttribute("message", "✅ Вы успешно подписались на рассылку!");
        return "redirect:/"; // 🔄 Вернуть пользователя на ту же страницу, где он был
    }
}