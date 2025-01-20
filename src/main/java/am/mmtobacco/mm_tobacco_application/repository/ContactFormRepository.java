package am.mmtobacco.mm_tobacco_application.repository;

import am.mmtobacco.mm_tobacco_application.model.ContactForm;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactFormRepository extends MongoRepository<ContactForm, String> {
}

