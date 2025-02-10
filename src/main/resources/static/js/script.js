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
// const checkoutBtn = document.querySelector(".checkout-btn");
// if (checkoutBtn) {
//     checkoutBtn.addEventListener("click", function () {
//         let cart = getCart();
//         if (cart.length === 0) {
//             alert("❌ Ваша корзина пуста.");
//             return;
//         }
//         alert("✅ Оформление заказа...");
//         localStorage.removeItem("cart");
//         updateCart();
//     });
// }
const checkoutBtn = document.querySelector(".checkout-btn");
if (checkoutBtn) { // ✅ Check if the element exists
    checkoutBtn.addEventListener("click", function () {
        let cart = getCart();
        if (cart.length === 0) {
            alert("❌ Your cart is empty.");
            return;
        }

        // ✅ Save cart data before redirecting
        localStorage.setItem("cartData", JSON.stringify(cart));

        // ✅ Redirect to contact form page
        window.location.href = "/api/contact";
    });
}


////////////////////////////////////////////////
document.addEventListener("DOMContentLoaded", function () {
    let cart = JSON.parse(localStorage.getItem("cartData")) || [];
    const cartDataInput = document.getElementById("cartData"); // ✅ Check if exists

    if (cartDataInput && cart.length > 0) { // ✅ Ensure cartData exists before setting value
        fetch("/api/catalogue/products")
            .then(response => response.json())
            .then(products => {
                let cartSummary = cart.map(item => {
                    let product = products.find(p => p.id == item.productId);
                    return product ? `${product.name} x ${item.quantity} ($${(product.price * item.quantity).toFixed(2)})` : "";
                }).join(", ");

                console.log("Cart Summary:", cartSummary); // ✅ Debugging

                // ✅ Save cart items in hidden input
                cartDataInput.value = cartSummary;
            })
            .catch(error => console.error("❌ Error loading products:", error));
    } else {
        console.log("Cart is empty or cartData input not found.");
    }
});


document.addEventListener("DOMContentLoaded", function () {
    const contactForm = document.getElementById("contactForm");
    if (contactForm) { // ✅ Check if form exists
        contactForm.addEventListener("submit", function () {
            localStorage.removeItem("cartData"); // ✅ Clear cart data on submission
            localStorage.removeItem("cart"); // ✅ Clear cart data on submission
        });
    }
});
////////////////////////////////////////////////

document.addEventListener("DOMContentLoaded", function () {
    // ✅ Fetch the last 10 requests for the table
    fetch('/api/contact/latest') // Ensure this endpoint returns the last 10 requests
        .then(response => response.json())
        .then(data => {
            const tableBody = document.getElementById("requestsTableBody");
            tableBody.innerHTML = ""; // Clear previous data

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
        })
        .catch(error => console.error("❌ Error loading recent requests:", error));

    // ✅ Fetch all requests for the graph
    const ctx = document.getElementById('requestsChart').getContext('2d');
    let chartInstance; // To store the graph instance

    function fetchRequests(view = "monthly") {
        fetch('/api/contact/all')
            .then(response => response.json())
            .then(data => {
                if (!data || data.length === 0) {
                    console.error("❌ No request data available for graph");
                    return;
                }

                let labels = [];
                let requestsCount = [];

                if (view === "daily") {
                    ({ labels, requestsCount } = processDailyData(data));
                } else if (view === "weekly") {
                    ({ labels, requestsCount } = processWeeklyData(data));
                } else {
                    ({ labels, requestsCount } = processMonthlyData(data));
                }

                renderChart(labels, requestsCount);
            })
            .catch(error => console.error("❌ Error loading requests for graph:", error));
    }

    function processDailyData(data) {
        let dailyCounts = {};
        data.forEach(contact => {
            let date = contact.date.split("T")[0]; // Format YYYY-MM-DD
            dailyCounts[date] = (dailyCounts[date] || 0) + 1;
        });

        let labels = Object.keys(dailyCounts).sort();
        let requestsCount = labels.map(date => dailyCounts[date]);

        return { labels, requestsCount };
    }

    function processWeeklyData(data) {
        function getWeekNumber(date) {
            const tempDate = new Date(date);
            tempDate.setHours(0, 0, 0, 0);
            tempDate.setDate(tempDate.getDate() + 3 - (tempDate.getDay() + 6) % 7);
            const firstWeek = new Date(tempDate.getFullYear(), 0, 4);
            return (
                tempDate.getFullYear() + "-W" +
                String(Math.round(((tempDate.getTime() - firstWeek.getTime()) / (7 * 86400000)) + 1)).padStart(2, "0")
            );
        }

        let weeklyCounts = {};
        data.forEach(contact => {
            let week = getWeekNumber(contact.date);
            weeklyCounts[week] = (weeklyCounts[week] || 0) + 1;
        });

        let labels = Object.keys(weeklyCounts).sort();
        let requestsCount = labels.map(week => weeklyCounts[week]);

        return { labels, requestsCount };
    }

    function processMonthlyData(data) {
        let monthlyCounts = {};
        data.forEach(contact => {
            let date = new Date(contact.date);
            let month = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, "0")}`;
            monthlyCounts[month] = (monthlyCounts[month] || 0) + 1;
        });

        let labels = Object.keys(monthlyCounts).sort();
        let requestsCount = labels.map(month => monthlyCounts[month]);

        return { labels, requestsCount };
    }

    function renderChart(labels, data) {
        if (chartInstance) {
            chartInstance.destroy(); // Destroy old chart instance
        }

        chartInstance = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Requests',
                    data: data,
                    borderColor: '#007bff',
                    borderWidth: 2,
                    pointRadius: 4,
                    fill: false,
                    tension: 0.3
                }]
            },
            options: {
                responsive: true,
                scales: {
                    x: {
                        ticks: {
                            autoSkip: true,
                            maxRotation: 45,
                            minRotation: 30
                        }
                    },
                    y: {
                        beginAtZero: true,
                        suggestedMax: Math.max(...data) + 5,
                        ticks: {
                            stepSize: 1
                        }
                    }
                }
            }
        });
    }

    // Fetch initial data (Monthly)
    fetchRequests("monthly");

    // Event Listener for Dropdown Change
    document.getElementById("timeFilter").addEventListener("change", function () {
        fetchRequests(this.value);
    });
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


function deleteProduct(productId) {
    if (!confirm("Are you sure you want to delete this product?")) {
        return;
    }

    fetch(`/admin/delete-product/${productId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(response => {
            if (response.ok) {
                alert("✅ Product deleted successfully!");
                window.location.reload(); // Reload page after successful deletion
            } else {
                response.text().then(errorMsg => alert("❌ Error: " + errorMsg));
            }
        })
        .catch(error => console.error("❌ Error deleting product:", error));
}

function updateProduct() {
    let productId = document.getElementById("productId").value;
    let formData = new FormData(document.getElementById("updateProductForm"));

    fetch(`/admin/update-product/${productId}`, {
        method: "POST",  // Forms only support POST, not PUT
        body: formData
    })
        .then(response => {
            if (response.ok) {
                alert("✅ Product updated successfully!");
                window.location.href = "/admin/products"; // Redirect after update
            } else {
                alert("❌ Error updating product.");
            }
        })
        .catch(error => console.error("❌ Error:", error));
}

// ✅ Preview image before uploading
document.getElementById("productImage").addEventListener("change", function (event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function (e) {
            document.getElementById("previewImage").src = e.target.result;
        };
        reader.readAsDataURL(file);
    }
});

function logoutUser() {
    fetch('/admin/logout', { method: 'POST' }) // Force a POST request
        .then(() => {
            window.location.href = '/index'; // Redirect after logout
        })
        .catch(error => console.error('Logout failed:', error));
}



