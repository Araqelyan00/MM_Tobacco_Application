package am.mmtobacco.mm_tobacco_application.controller;

import am.mmtobacco.mm_tobacco_application.model.Contacts;
import am.mmtobacco.mm_tobacco_application.service.ContactFormService;
import am.mmtobacco.mm_tobacco_application.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/api/contact")
public class ContactFormController {
    private final ContactFormService contactFormService;
    private final EmailService emailService;

    public ContactFormController(ContactFormService contactFormService, EmailService emailService) {
        this.contactFormService = contactFormService;
        this.emailService = emailService;
    }

    /** 📌 Рендеринг страницы "Контакты" */
    @GetMapping
    public String contactPage() {
        return "contact"; // Возвращает HTML-страницу
    }

    /** 📌 API: Обработка формы контакта */
    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<Contacts> submitForm(@Valid @ModelAttribute Contacts form) {
        // 📌 Сохранение данных в базе
        Contacts savedForm = contactFormService.saveForm(form);

        // 📌 Отправка email пользователю
        String userEmailBody = String.format(
                "Здравствуйте, %s %s!\n\nВаш запрос был успешно отправлен. Мы скоро свяжемся с вами!",
                form.getFirstName(), form.getLastName()
        );
        emailService.sendEmail(form.getEmail(), "Ваш запрос принят", userEmailBody);

        // 📌 Отправка уведомления администратору
        String adminEmailBody = String.format(
                "Новый запрос:\n\nИмя: %s %s\nТелефон: %s\nМессенджер: %s\nE-mail: %s\nСообщение: %s",
                form.getFirstName(), form.getLastName(), form.getPhone(), form.getMessenger(), form.getEmail(), form.getMessage()
        );
        emailService.sendEmail("companies.and.employees@gmail.com", "Новый запрос на сайте", adminEmailBody);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedForm);
    }

    @GetMapping("/latest")
    @ResponseBody
    public List<Contacts> getLatestContacts() {
        return contactFormService.getLast10Contacts();
    }
}
