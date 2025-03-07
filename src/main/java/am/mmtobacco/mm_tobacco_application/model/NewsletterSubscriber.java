package am.mmtobacco.mm_tobacco_application.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="newsletter_subscribers")
public class NewsletterSubscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    public NewsletterSubscriber() {}

    public NewsletterSubscriber(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

}
