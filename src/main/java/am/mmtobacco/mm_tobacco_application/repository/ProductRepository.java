package am.mmtobacco.mm_tobacco_application.repository;

import am.mmtobacco.mm_tobacco_application.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%'))")
    List<Product> findProductsByFilters(@Param("minPrice") double minPrice,
                                        @Param("maxPrice") double maxPrice,
                                        @Param("category") String category);

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%'))")
    Page<Product> findProductsByFiltersWithPagination(@Param("minPrice") double minPrice,
                                                      @Param("maxPrice") double maxPrice,
                                                      @Param("category") String category,
                                                      Pageable pageable);

}

