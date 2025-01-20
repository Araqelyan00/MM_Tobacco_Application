package am.mmtobacco.mm_tobacco_application.service;

import am.mmtobacco.mm_tobacco_application.model.ContactForm;
import am.mmtobacco.mm_tobacco_application.repository.ContactFormRepository;
import org.springframework.stereotype.Service;

@Service
public class ContactFormService {
    private final ContactFormRepository contactFormRepository;

    public ContactFormService(ContactFormRepository contactFormRepository) {
        this.contactFormRepository = contactFormRepository;
    }

    public ContactForm saveForm(ContactForm form) {
        return contactFormRepository.save(form);
    }
}

