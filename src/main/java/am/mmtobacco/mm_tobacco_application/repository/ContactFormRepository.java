package am.mmtobacco.mm_tobacco_application.repository;

import am.mmtobacco.mm_tobacco_application.model.Contacts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContactFormRepository extends JpaRepository<Contacts, Long> {

    // ✅ Get last 10 requests
    List<Contacts> findTop10ByOrderByDateDesc();

    // ✅ Fetch requests with pagination
    Page<Contacts> findAll(Pageable pageable);

    // ✅ Filter requests by status and date
    @Query("SELECT c FROM Contacts c WHERE (:status IS NULL OR c.status = :status) " +
            "AND (:date IS NULL OR DATE(c.date) = :date)")
    Page<Contacts> findByStatusAndDate(String status, LocalDate date, Pageable pageable);
}


