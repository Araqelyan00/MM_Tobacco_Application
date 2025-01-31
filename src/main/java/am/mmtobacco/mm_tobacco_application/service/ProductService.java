package am.mmtobacco.mm_tobacco_application.service;

import am.mmtobacco.mm_tobacco_application.model.Product;
import am.mmtobacco.mm_tobacco_application.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> getProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }

    public List<Product> filterProducts(Double minPrice, Double maxPrice, String category) {
        double min = minPrice != null ? minPrice : 0;
        double max = maxPrice != null ? maxPrice : Double.MAX_VALUE;
        String cat = category != null ? category : "";
        return productRepository.findProductsByFilters(min, max, cat);
    }

    public Page<Product> filterProductsWithPagination(Double minPrice, Double maxPrice, String category, int page, int size) {
        double min = minPrice != null ? minPrice : 0;
        double max = maxPrice != null ? maxPrice : Double.MAX_VALUE;
        String cat = category != null ? category : "";
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findProductsByFiltersWithPagination(min, max, cat, pageable);
    }


    @Cacheable("products")
    public List<Product> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll();
    }

    @Cacheable(value = "product", key = "#id")
    public Product getProductById(String id) {
        log.info("Fetching product by ID: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(String id, Product updatedProduct) {
        Product product = getProductById(id);
        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        product.setImageUrl(updatedProduct.getImageUrl());
        return productRepository.save(product);
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    public List<Product> getLatestProducts(int limit) {
        return productRepository.findLatestProducts(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id")));
    }
}

