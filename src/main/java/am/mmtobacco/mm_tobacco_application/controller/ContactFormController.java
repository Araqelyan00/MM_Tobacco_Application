package am.mmtobacco.mm_tobacco_application.controller;

import am.mmtobacco.mm_tobacco_application.model.ContactForm;
import am.mmtobacco.mm_tobacco_application.service.ContactFormService;
import am.mmtobacco.mm_tobacco_application.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/contact")
public class ContactFormController {
    private final ContactFormService contactFormService;
    private final EmailService emailService;

    public ContactFormController(ContactFormService contactFormService, EmailService emailService) {
        this.contactFormService = contactFormService;
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<ContactForm> submitForm(@Valid @RequestBody ContactForm form) {
        // Сохранение данных в базе
        ContactForm savedForm = contactFormService.saveForm(form);

        // Отправка e-mail пользователю
        String userEmailBody = String.format("Здравствуйте, %s %s!\n\nВаш запрос был успешно отправлен. Мы скоро свяжемся с вами!",
                form.getFirstName(), form.getLastName());
        emailService.sendEmail(form.getEmail(), "Ваш запрос принят", userEmailBody);

        // Отправка копии администратору
        String adminEmailBody = String.format("Новый запрос:\n\nИмя: %s %s\nТелефон: %s\nМессенджер: %s\nE-mail: %s\nСообщение: %s",
                form.getFirstName(), form.getLastName(), form.getPhone(), form.getMessenger(), form.getEmail(), form.getMessage());
        emailService.sendEmail("admin@example.com", "Новый запрос на сайте", adminEmailBody);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedForm);
    }
}

