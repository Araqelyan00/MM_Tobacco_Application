package am.mmtobacco.mm_tobacco_application.service;

import am.mmtobacco.mm_tobacco_application.model.Contacts;
import am.mmtobacco.mm_tobacco_application.repository.ContactFormRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactFormService {
    private final ContactFormRepository contactFormRepository;

    public ContactFormService(ContactFormRepository contactFormRepository) {
        this.contactFormRepository = contactFormRepository;
    }

    public Contacts saveForm(Contacts form) {
        form.setDate(LocalDateTime.now());
        form.setStatus("New");
        return contactFormRepository.save(form);
    }

    public List<Contacts> getAllRequests(){
        return contactFormRepository.findAll();
    }

    public List<Contacts> getLast10Contacts() {
        return contactFormRepository.findTop10ByOrderByDateDesc();
    }
}

