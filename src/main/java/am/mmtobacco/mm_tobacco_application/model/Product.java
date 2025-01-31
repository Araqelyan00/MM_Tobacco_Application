package am.mmtobacco.mm_tobacco_application.model;

import jakarta.persistence.*;
import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Название продукта обязательно")
    @Size(max = 100, message = "Название продукта должно быть не длиннее 100 символов")
    private String name;

    @NotNull(message = "Цена обязательна")
    @Min(value = 0, message = "Цена не может быть отрицательной")
    private Double price;

    @NotBlank(message = "Категория обязательна")
    private String category;

    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    private String description;

    private String imageUrl;

    public Product() {}

    public Product(Long id, String name, String description, double price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

