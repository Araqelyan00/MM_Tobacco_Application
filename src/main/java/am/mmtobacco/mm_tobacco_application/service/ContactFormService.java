package am.mmtobacco.mm_tobacco_application.service;

import am.mmtobacco.mm_tobacco_application.model.Contacts;
import am.mmtobacco.mm_tobacco_application.repository.ContactFormRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public Contacts updateForm(Contacts form) {
        return contactFormRepository.save(form);
    }

    public List<Contacts> getAllRequests(){
        return contactFormRepository.findAll();
    }

    public List<Contacts> getLast10Contacts() {
        return contactFormRepository.findTop10ByOrderByDateDesc();
    }

    public Page<Contacts> getRequests(String status, LocalDate date, int page, int size) {
        return contactFormRepository.findByStatusAndDate(
                status.equals("all") ? null : status, date, PageRequest.of(page, size));
    }

    public Contacts getRequestById(Long id) {
        return contactFormRepository.findById(id).orElse(null);
    }
}

