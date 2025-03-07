package am.mmtobacco.mm_tobacco_application.service;


import am.mmtobacco.mm_tobacco_application.model.NewsletterSubscriber;
import am.mmtobacco.mm_tobacco_application.repository.NewsletterSubscriberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsletterSubscriberService {

    private NewsletterSubscriberRepository newsletterSubscriberRepository;

    public NewsletterSubscriberService(NewsletterSubscriberRepository newsletterSubscriberRepository) {
        this.newsletterSubscriberRepository = newsletterSubscriberRepository;
    }

    public void save(NewsletterSubscriber newsletterSubscriber) {
        newsletterSubscriberRepository.save(newsletterSubscriber);
    }

    public Page<NewsletterSubscriber> getSubscribers(int page, int size) {
        return newsletterSubscriberRepository.findAll(PageRequest.of(page, size));
    }

    public List<NewsletterSubscriber> getAllSubscribers() {
        return newsletterSubscriberRepository.findAll();
    }
}
