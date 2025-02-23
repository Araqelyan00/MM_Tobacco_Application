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

    @GetMapping
    public String contactPage() {
        return "contact";
    }

    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<Contacts> submitForm(@Valid @ModelAttribute Contacts form) {
        Contacts savedForm = contactFormService.saveForm(form);

        if(form.getMessage() == null || form.getMessage().isEmpty()) {
            String userEmailBody = String.format(
                    "Hello %s %s,\n\nYour request has been submitted. We will contact you soon!",
                    form.getFirstName(), form.getLastName()
            );
            emailService.sendEmail(form.getEmail(), "Your Request is Received", userEmailBody);

            String adminEmailBody = String.format(
                    "New Order Request:\n\nName: %s %s\nPhone number: %s\nMessenger: %s\nE-mail: %s",
                    form.getFirstName(), form.getLastName(), form.getPhone(), form.getMessenger(), form.getEmail()
            );
            emailService.sendEmail("companies.and.employees@gmail.com", "New Order Received", adminEmailBody);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedForm);
        }

        String userEmailBody = String.format(
                "Hello %s %s,\n\nYour request has been submitted. We will contact you soon!\n\nYour Order:\n%s",
                form.getFirstName(), form.getLastName(), form.getMessage()
        );
        emailService.sendEmail(form.getEmail(), "Your Request is Received", userEmailBody);

        String adminEmailBody = String.format(
                "New Order Request:\n\nName: %s %s\nPhone number: %s\nMessenger: %s\nE-mail: %s\nMessage: %s",
                form.getFirstName(), form.getLastName(), form.getPhone(), form.getMessenger(), form.getEmail(), form.getMessage()
        );
        emailService.sendEmail("companies.and.employees@gmail.com", "New Order Received", adminEmailBody);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedForm);
    }

    @GetMapping("/latest")
    @ResponseBody
    public List<Contacts> getLatestContacts() {
        return contactFormService.getLast10Contacts();
    }

    @GetMapping("/all")
    @ResponseBody
    public List<Contacts> allContacts() {
        return contactFormService.getAllRequests();
    }
}
