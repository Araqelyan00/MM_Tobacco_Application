// 📌 Функция получения корзины из LocalStorage
function getCart() {
    try {
        let cart = JSON.parse(localStorage.getItem("cart")) || [];

        // Конвертация старого формата (если нужно)
        if (Array.isArray(cart) && typeof cart[0] === "number") {
            cart = cart.map(id => ({productId: id, quantity: 1}));
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
        cart.push({productId, quantity: 1});
    }

    saveCart(cart);
    showToast("✅ Товар добавлен в корзину!"); // Используем всплывающее сообщение
    updateCart();
}

// 📌 Функция удаления продукта из корзины (без `alert`)
function removeFromCart(productId) {
    let cart = getCart().filter(item => item.productId !== productId);
    saveCart(cart);

    // ✅ Удаляем продукт из DOM (если он есть)
    let cartItemElement = document.querySelector(`.cart-item[data-id="${productId}"]`);
    if (cartItemElement) {
        cartItemElement.remove();
    }

    showToast("❌ Товар удалён из корзины!", "error");

    // ✅ Обновляем корзину и общие суммы
    updateCart();
}


// 📌 Открытие модального окна с деталями товара
function openProductModal(productId) {
    fetch(`/api/catalogue/products/${productId}`) // 📌 Исправлен путь API
        .then(response => response.json())
        .then(product => {
            document.getElementById("modalDetails").innerHTML = `
                <div class="modal-product">
                    <div class="modal-product-image">
                        <img src="${product.imageUrl}" alt="${product.name}">
                    </div>
                    <div class="modal-product-info">
                        <h3 style="font-size: 1.5rem">${product.name}</h3>
                        <p>${product.description}</p>
                        <p><strong>Price : $${product.price.toFixed(2)}</strong></p>
                        <button class="btn add-to-cart" data-id="${product.id}">Add to Cart</button>
                        
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

    if (!cartContainer) return;

    cartContainer.innerHTML = "";

    if (cart.length === 0) {
        cartContainer.innerHTML = "<p>🛒 Ваша корзина пуста.</p>";
        updateTotals(0); // ✅ Устанавливаем 0, если корзина пуста
        return;
    }

    let subtotal = 0;
    let promises = [];

    cart.forEach((item) => {
        let productPromise = fetch(`/api/catalogue/products/${item.productId}`)
            .then(response => response.json())
            .then(product => {
                let itemTotal = product.price * item.quantity;
                subtotal += itemTotal;

                let cartItem = document.createElement("div");
                cartItem.classList.add("cart-item");
                cartItem.setAttribute("data-id", product.id); // ✅ Привязываем ID для удаления
                cartItem.innerHTML = `
                    <span>${product.name}</span>
                    <span>$${product.price.toFixed(2)}</span>
                    <input type="number" value="${item.quantity}" min="1" class="cart-quantity" data-id="${product.id}">
                    <span class="cart-item-total">$${itemTotal.toFixed(2)}</span>
                    <button class="removeButton remove-btn" data-id="${product.id}">Remove</button>
                `;
                cartContainer.appendChild(cartItem);

                // ✅ Изменение количества
                cartItem.querySelector(".cart-quantity").addEventListener("input", function () {
                    let newQuantity = parseInt(this.value);
                    if (newQuantity < 1) newQuantity = 1;

                    let cart = getCart();
                    let item = cart.find(i => i.productId === Number(this.dataset.id));
                    if (item) item.quantity = newQuantity;
                    saveCart(cart);

                    // ✅ Пересчитываем сумму для одного товара
                    let itemTotalElement = cartItem.querySelector(".cart-item-total");
                    let newItemTotal = newQuantity * product.price;
                    itemTotalElement.textContent = `$${newItemTotal.toFixed(2)}`;

                    updateTotals(); // ✅ Пересчет всех сумм
                });

                // ✅ Удаление товара
                cartItem.querySelector(".remove-btn").addEventListener("click", function () {
                    removeFromCart(Number(this.dataset.id));
                });
            })
            .catch(error => console.error("❌ Ошибка при получении товара:", error));

        promises.push(productPromise);
    });

    // ✅ Ждём, пока все товары загрузятся, и пересчитываем итоговую сумму
    Promise.all(promises).then(() => {
        updateTotals();
    });
}


// 📌 Функция обновления итоговой суммы корзины
function updateTotals() {
    let cart = getCart();
    let subtotal = 0;

    cart.forEach(item => {
        fetch(`/api/catalogue/products/${item.productId}`)
            .then(response => response.json())
            .then(product => {
                subtotal += product.price * item.quantity;

                let tax = subtotal * 0.10;
                let total = subtotal + tax;

                document.getElementById("subtotal").textContent = `$${subtotal.toFixed(2)}`;
                document.getElementById("tax").textContent = `$${tax.toFixed(2)}`;
                document.getElementById("total").textContent = `$${total.toFixed(2)}`;
            })
            .catch(error => console.error("❌ Ошибка при обновлении итогов:", error));
    });
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

    document.addEventListener("DOMContentLoaded", function () {
    fetch('/api/contact/latest')
        .then(response => response.json())
        .then(data => {
            const tableBody = document.getElementById("requestsTableBody");
            tableBody.innerHTML = ""; // Очищаем перед добавлением

            const statusClasses = {
                "New": "status in-progress",
                "Cancelled": "status cancelled",
                "Completed": "status completed"
            };

            data.forEach(contact => {
                const row = `<tr>
                        <td>${contact.id}</td>
                        <td>${new Date(contact.date).toLocaleDateString()}</td>
                        <td>${contact.firstName} ${contact.lastName}</td>
                        <td class="${statusClasses[contact.status] || "status"}">${contact.status}</td>
                    </tr>`;
                tableBody.innerHTML += row;
            });

            // Подготовка данных для графика
            const months = ["JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"];
            const requestsPerMonth = new Array(12).fill(0);

            data.forEach(contact => {
                const monthIndex = new Date(contact.date).getMonth();
                requestsPerMonth[monthIndex]++;
            });

            // Рендер графика
            const ctx = document.getElementById('requestsChart').getContext('2d');
            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: months,
                    datasets: [{
                        label: 'Requests',
                        data: requestsPerMonth,
                        borderColor: '#007bff',
                        fill: false
                    }]
                }
            });
        })
        .catch(error => console.error("Ошибка загрузки заявок:", error));
});

function showSuccessMessage() {
    // Показываем сообщение пользователю
    document.getElementById("successMessage").style.display = "block";

    // Очищаем форму после отправки
    document.getElementById("contactForm").reset();

    // Через 5 секунд скрываем сообщение
    setTimeout(() => {
        document.getElementById("successMessage").style.display = "none";
    }, 5000);
}

function previewImage(event) {
    const preview = document.getElementById("preview");
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            preview.src = e.target.result;
            preview.classList.remove("hidden");
        };
        reader.readAsDataURL(file);
    }
}