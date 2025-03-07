package am.mmtobacco.mm_tobacco_application.repository;

import am.mmtobacco.mm_tobacco_application.model.NewsletterSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsletterSubscriberRepository extends JpaRepository<NewsletterSubscriber, Integer> {


}
