package am.mmtobacco.mm_tobacco_application.service;

import am.mmtobacco.mm_tobacco_application.model.CartItem;
import am.mmtobacco.mm_tobacco_application.model.Product;
import am.mmtobacco.mm_tobacco_application.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    private final ProductRepository productRepository;

    public CartService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<CartItem> updateCart(List<CartItem> cartItems) {
        for (CartItem item : cartItems) {
            if (!productRepository.existsById(item.getProductId())) {
                throw new RuntimeException("Product with ID " + item.getProductId() + " does not exist");
            }
        }
        return cartItems;
    }

    public double calculateTotal(List<CartItem> cartItems) {
        double total = 0;
        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));
            total += product.getPrice() * item.getQuantity();
        }
        return total;
    }
}

