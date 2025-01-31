package am.mmtobacco.mm_tobacco_application.model;

import jakarta.persistence.*;
import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Entity
@Table(name = "contacts")
public class Contacts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Имя обязательно")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    private String lastName;

    @NotBlank(message = "E-mail обязателен")
    @Email(message = "E-mail должен быть корректным")
    private String email;

    @NotBlank(message = "Номер телефона обязателен")
    @Pattern(regexp = "\\+?[0-9]{7,15}", message = "Номер телефона должен быть корректным")

    private String phone;

    @NotBlank(message = "Выбор мессенджера обязателен")
    private String messenger;
    
    private String message;

    public Contacts(){}

    public Contacts(String firstName, String lastName, String email, String phone, String messenger, String message) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.messenger = messenger;
        this.message = null;
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
}
