package am.mmtobacco.mm_tobacco_application.repository;

import am.mmtobacco.mm_tobacco_application.model.Contacts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactFormRepository extends JpaRepository<Contacts, String> {
    List<Contacts> findTop10ByOrderByDateDesc();
}

