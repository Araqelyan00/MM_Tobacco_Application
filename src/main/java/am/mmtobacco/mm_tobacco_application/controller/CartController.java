package am.mmtobacco.mm_tobacco_application.controller;

import am.mmtobacco.mm_tobacco_application.model.CartItem;
import am.mmtobacco.mm_tobacco_application.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /** 📌 Рендеринг страницы корзины (Thymeleaf) */
    @GetMapping
    public String cartPage() {
        return "cart"; // Возвращает HTML-страницу корзины
    }

    /** 📌 API: Обновление корзины */
    @PostMapping("/update")
    @ResponseBody
    public List<CartItem> updateCart(@RequestBody List<CartItem> cartItems) {
        return cartService.updateCart(cartItems);
    }

    /** 📌 API: Подсчет итоговой суммы корзины */
    @PostMapping("/total")
    @ResponseBody
    public double calculateTotal(@RequestBody List<CartItem> cartItems) {
        return cartService.calculateTotal(cartItems);
    }
}


