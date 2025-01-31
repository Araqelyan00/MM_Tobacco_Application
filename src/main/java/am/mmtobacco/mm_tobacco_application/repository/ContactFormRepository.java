package am.mmtobacco.mm_tobacco_application.repository;

import am.mmtobacco.mm_tobacco_application.model.Contacts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactFormRepository extends JpaRepository<Contacts, String> {
}

