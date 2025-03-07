package am.mmtobacco.mm_tobacco_application.model;

import jakarta.persistence.*;
import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "contacts")
public class Contacts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String firstName;

    @NotBlank(message = "Surname is required")
    private String lastName;

    @NotBlank(message = "E-mail is required")
    @Email(message = "E-mail must be correct")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\+?[0-9]{7,15}", message = "Phone number must be correct")

    private String phone;

    @NotBlank(message = "Choosing a messenger is mandatory")
    private String messenger;
    
    private String message;

    private LocalDateTime date;

    private String status;

    public Contacts(){}

    public Contacts(String firstName, String lastName, String email, String phone, String messenger, String message, LocalDateTime date, String status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.messenger = messenger;
        this.message = message;
        this.date = date;
        this.status = status;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessenger() {
        return messenger;
    }

    public void setMessenger(String messenger) {
        this.messenger = messenger;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
