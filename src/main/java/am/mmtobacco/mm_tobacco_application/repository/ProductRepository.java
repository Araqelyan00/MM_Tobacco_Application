package am.mmtobacco.mm_tobacco_application.repository;

import am.mmtobacco.mm_tobacco_application.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {

    @Query("{'price': {$gte: ?0, $lte: ?1}, 'category': {$regex: ?2, $options: 'i'}}")
    List<Product> findProductsByFilters(double minPrice, double maxPrice, String category);

    @Query("{'price': {$gte: ?0, $lte: ?1}, 'category': {$regex: ?2, $options: 'i'}}")
    Page<Product> findProductsByFiltersWithPagination(double minPrice, double maxPrice, String category, Pageable pageable);

}

