package am.mmtobacco.mm_tobacco_application.controller;

import am.mmtobacco.mm_tobacco_application.model.CartItem;
import am.mmtobacco.mm_tobacco_application.service.CartService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/update")
    public List<CartItem> updateCart(@RequestBody List<CartItem> cartItems) {
        return cartService.updateCart(cartItems);
    }

    @PostMapping("/total")
    public double calculateTotal(@RequestBody List<CartItem> cartItems) {
        return cartService.calculateTotal(cartItems);
    }
}

