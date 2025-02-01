// 📌 Функция получения корзины из LocalStorage
function getCart() {
    try {
        let cart = JSON.parse(localStorage.getItem("cart")) || [];

        // Конвертация старого формата (если нужно)
        if (Array.isArray(cart) && typeof cart[0] === "number") {
            cart = cart.map(id => ({ productId: id, quantity: 1 }));
            saveCart(cart);
        }
        return cart;
    } catch (error) {
        console.error("❌ Ошибка при разборе корзины:", error);
        localStorage.removeItem("cart");
        return [];
    }
}

// 📌 Функция сохранения корзины в LocalStorage
function saveCart(cart) {
    localStorage.setItem("cart", JSON.stringify(cart));
}

// 📌 Функция для показа всплывающего уведомления
function showToast(message, type = "success") {
    let toast = document.createElement("div");
    toast.className = `toast toast-${type}`;
    toast.textContent = message;

    document.body.appendChild(toast);

    setTimeout(() => {
        toast.classList.add("fade-out");
        setTimeout(() => toast.remove(), 500);
    }, 2000);
}

// 📌 Функция добавления продукта в корзину (без `alert`)
function addToCart(productId) {
    let cart = getCart();
    let existingProduct = cart.find(item => item.productId === productId);

    if (existingProduct) {
        existingProduct.quantity++;
    } else {
        cart.push({ productId, quantity: 1 });
    }

    saveCart(cart);
    showToast("✅ Товар добавлен в корзину!"); // Используем всплывающее сообщение
    updateCart();
}

// 📌 Функция удаления продукта из корзины (без `alert`)
function removeFromCart(productId) {
    let cart = getCart().filter(item => item.productId !== productId);
    saveCart(cart);
    showToast("❌ Товар удалён из корзины!", "error"); // Уведомление об удалении
    updateCart();
}

// 📌 Открытие модального окна с деталями товара
function openProductModal(productId) {
    fetch(`/catalogue/api/products/${productId}`) // 📌 Исправлен путь API
        .then(response => response.json())
        .then(product => {
            document.getElementById("modalDetails").innerHTML = `
                <div class="modal-product">
                    <div class="modal-product-image">
                        <img src="${product.imageUrl}" alt="${product.name}">
                    </div>
                    <div class="modal-product-info">
                        <h3>${product.name}</h3>
                        <p>${product.description}</p>
                        <p><strong>$${product.price.toFixed(2)}</strong></p>
                        <button class="btn add-to-cart" data-id="${product.id}">Add to Cart</button>
                        <button class="btn remove-from-cart" data-id="${product.id}">Remove from Cart</button>
                    </div>
                </div>
            `;

            document.getElementById("productModal").classList.remove("hidden");

            // 📌 Добавление обработчиков событий для кнопок внутри модального окна
            document.querySelector(".add-to-cart").addEventListener("click", () => addToCart(product.id));
            document.querySelector(".remove-from-cart").addEventListener("click", () => removeFromCart(product.id));
        })
        .catch(error => console.error("❌ Ошибка при получении продукта:", error));
}

// 📌 Обработчики кликов на карточках товаров
document.querySelectorAll(".product-card").forEach(card => {
    card.addEventListener("click", () => {
        const productId = Number(card.dataset.id);
        openProductModal(productId);
    });
});

// 📌 Закрытие модального окна
const closeModalBtn = document.querySelector(".close-btn");
if (closeModalBtn) {
    closeModalBtn.addEventListener("click", () => {
        document.getElementById("productModal").classList.add("hidden");
    });
}

// 📌 Обновление отображения корзины
function updateCart() {
    let cart = getCart();
    let cartContainer = document.getElementById("cartItems");

    if (!cartContainer) return; // 📌 Защита от ошибок, если корзины нет на странице

    cartContainer.innerHTML = "";

    if (cart.length === 0) {
        cartContainer.innerHTML = "<p>🛒 Ваша корзина пуста.</p>";
        updateTotals(0);
        return;
    }

    let subtotal = 0;

    cart.forEach((item, index) => {
        fetch(`/catalogue/api/products/${item.productId}`)
            .then(response => response.json())
            .then(product => {
                let itemTotal = product.price * item.quantity;
                subtotal += itemTotal;

                let cartItem = document.createElement("div");
                cartItem.classList.add("cart-item");
                cartItem.innerHTML = `
                    <span>${product.name}</span>
                    <span>$${product.price.toFixed(2)}</span>
                    <input type="number" value="${item.quantity}" min="1" class="cart-quantity" data-id="${product.productId}">
                    <span class="cart-item-total">$${itemTotal.toFixed(2)}</span>
                    <button class="btn remove-btn" data-id="${product.productId}">Remove</button>
                `;
                cartContainer.appendChild(cartItem);

                // 📌 Обработчик изменения количества товара
                cartItem.querySelector(".cart-quantity").addEventListener("change", function () {
                    let newQuantity = parseInt(this.value);
                    if (newQuantity < 1) newQuantity = 1;
                    let cart = getCart();
                    let item = cart.find(i => i.productId === Number(this.dataset.id));
                    if (item) item.quantity = newQuantity;
                    saveCart(cart);
                    updateCart();
                });

                // 📌 Обработчик удаления товара
                cartItem.querySelector(".remove-btn").addEventListener("click", function () {
                    removeFromCart(Number(this.dataset.id));
                });

                updateTotals(subtotal);
            })
            .catch(error => console.error("❌ Ошибка при получении данных товара:", error));
    });
}

// 📌 Функция обновления итоговой суммы корзины
function updateTotals(subtotal) {
    let tax = subtotal * 0.10;
    let total = subtotal + tax;

    document.getElementById("subtotal").textContent = `$${subtotal.toFixed(2)}`;
    document.getElementById("tax").textContent = `$${tax.toFixed(2)}`;
    document.getElementById("total").textContent = `$${total.toFixed(2)}`;
}

// 📌 Обновление корзины при загрузке страницы
document.addEventListener("DOMContentLoaded", updateCart);

// 📌 Обработчик кнопки оформления заказа
const checkoutBtn = document.querySelector(".checkout-btn");
if (checkoutBtn) {
    checkoutBtn.addEventListener("click", function () {
        let cart = getCart();
        if (cart.length === 0) {
            alert("❌ Ваша корзина пуста.");
            return;
        }
        alert("✅ Оформление заказа...");
        localStorage.removeItem("cart");
        updateCart();
    });
}
