// 📌 Get cart form LocalStorage
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
        console.error("❌ Սխալ՝ զամբյուղը վերլուծելիս:", error);
        localStorage.removeItem("cart");
        return [];
    }
}

// 📌 Save cart to LocalStorage
function saveCart(cart) {
    localStorage.setItem("cart", JSON.stringify(cart));
}

// 📌 Show notification
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

// 📌 Add product to cart function
function addToCart(productId) {
    let cart = getCart();
    let existingProduct = cart.find(item => item.productId === productId);

    if (existingProduct) {
        existingProduct.quantity++;
    } else {
        cart.push({productId, quantity: 1});
    }

    saveCart(cart);
    showToast("✅ Ապրանքը ավելացվել է զամբյուղին։");
    updateCart();
}

// 📌 Function to remove a product from the cart
function removeFromCart(productId) {
    let cart = getCart().filter(item => item.productId !== productId);
    saveCart(cart);

    let cartItemElement = document.querySelector(`.cart-item[data-id="${productId}"]`);
    if (cartItemElement) {
        cartItemElement.remove();
    }

    showToast("❌ Ապրանքը հեռացվել է զամբյուղից։", "error");
    updateCart();
}


// 📌 Opening a modal window with product details
function openProductModal(productId) {
    fetch(`/api/catalogue/products/${productId}`)
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
                        <p><strong>Գինը : ${product.price.toFixed(2)} Դ</strong></p>
                        <button class="btn add-to-cart" data-id="${product.id}">Ավելացնել զամբյուղ</button>
                        
                    </div>
                </div>
            `;

            document.getElementById("productModal").classList.remove("hidden");

            document.querySelector(".add-to-cart").addEventListener("click", () => addToCart(product.id));
        })
        .catch(error => console.error("❌ Սխալ՝ ապրանքի ստացման ժամանակ:", error));
}

// 📌 Product Card Click Handlers
document.querySelectorAll(".product-card").forEach(card => {
    card.addEventListener("click", () => {
        const productId = Number(card.dataset.id);
        openProductModal(productId);
    });
});

// 📌 Closing the modal window
const closeModalBtn = document.querySelector(".close-btn");
if (closeModalBtn) {
    closeModalBtn.addEventListener("click", () => {
        document.getElementById("productModal").classList.add("hidden");
    });
}

// 📌 Updating the cart display
function updateCart() {
    let cart = getCart();
    let cartContainer = document.getElementById("cartItems");

    if (!cartContainer) return;

    cartContainer.innerHTML = "";

    if (cart.length === 0) {
        cartContainer.innerHTML = "<p>🛒 Ձեր զամբյուղը դատարկ է։</p>";
        updateTotals(0);
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
                cartItem.setAttribute("data-id", product.id);
                cartItem.innerHTML = `
                    <span>${product.name}</span>
                    <span>$${product.price.toFixed(2)}</span>
                    <input type="number" value="${item.quantity}" min="1" class="cart-quantity" data-id="${product.id}">
                    <span class="cart-item-total">$${itemTotal.toFixed(2)}</span>
                    <button class="removeButton remove-btn" data-id="${product.id}">Հեռացնել</button>
                `;
                cartContainer.appendChild(cartItem);
                cartItem.querySelector(".cart-quantity").addEventListener("input", function () {
                    let newQuantity = parseInt(this.value);
                    if (newQuantity < 1) newQuantity = 1;

                    let cart = getCart();
                    let item = cart.find(i => i.productId === Number(this.dataset.id));
                    if (item) item.quantity = newQuantity;
                    saveCart(cart);

                    let itemTotalElement = cartItem.querySelector(".cart-item-total");
                    let newItemTotal = newQuantity * product.price;
                    itemTotalElement.textContent = `$${newItemTotal.toFixed(2)}`;

                    updateTotals();
                });

                cartItem.querySelector(".remove-btn").addEventListener("click", function () {
                    removeFromCart(Number(this.dataset.id));
                });
            })
            .catch(error => console.error("❌ Սխալ՝ ապրանքը ստանալու ժամանակ։", error));

        promises.push(productPromise);
    });

    Promise.all(promises).then(() => {
        updateTotals();
    });
}


// 📌 Cart Total Update Function
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

                document.getElementById("subtotal").textContent = `${subtotal.toFixed(2)}`;
                document.getElementById("tax").textContent = `${tax.toFixed(2)}`;
                document.getElementById("total").textContent = `${total.toFixed(2)}`;
            })
            .catch(error => console.error("❌ Ընդհանուր թվերի թարմացման սխալ։", error));
    });
}




// 📌 Refresh cart on page load
document.addEventListener("DOMContentLoaded", updateCart);
const checkoutBtn = document.querySelector(".checkout-btn");
if (checkoutBtn) {
    checkoutBtn.addEventListener("click", function () {
        let cart = getCart();
        if (cart.length === 0) {
            alert("❌ Ձեր զամբյուղը դատարկ է։");
            return;
        }

        localStorage.setItem("cartData", JSON.stringify(cart));

        window.location.href = "/api/contact";
    });
}


////////////////////////////////////////////////
document.addEventListener("DOMContentLoaded", function () {
    let cart = JSON.parse(localStorage.getItem("cartData")) || [];
    const cartDataInput = document.getElementById("cartData");

    if (cartDataInput && cart.length > 0) {
        fetch("/api/catalogue/products")
            .then(response => response.json())
            .then(products => {
                let cartSummary = cart.map(item => {
                    let product = products.find(p => p.id == item.productId);
                    return product ? `${product.name} x ${item.quantity} ($${(product.price * item.quantity).toFixed(2)})` : "";
                }).join(", ");

                console.log("Cart Summary:", cartSummary);

                cartDataInput.value = cartSummary;
            })
            .catch(error => console.error("❌ Սխալ՝ ապրանքներ բեռնելիս։", error));
    } else {
        console.log("Զամբյուղը դատարկ է կամ cartData մուտքագրումը չի գտնվել։");
    }
});


document.addEventListener("DOMContentLoaded", function () {
    const contactForm = document.getElementById("contactForm");
    if (contactForm) {
        contactForm.addEventListener("submit", function () {
            localStorage.removeItem("cartData");
            localStorage.removeItem("cart");
        });
    }
});
////////////////////////////////////////////////

document.addEventListener("DOMContentLoaded", function () {
    fetch('/api/contact/latest')
        .then(response => response.json())
        .then(data => {
            const tableBody = document.getElementById("requestsTableBody");
            tableBody.innerHTML = "";

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
        .catch(error => console.error("❌ Վերջին հարցումները բեռնելիս սխալ առաջացավ։", error));

    const ctx = document.getElementById('requestsChart').getContext('2d');
    let chartInstance;

    function fetchRequests(view = "monthly") {
        fetch('/api/contact/all')
            .then(response => response.json())
            .then(data => {
                if (!data || data.length === 0) {
                    console.error("❌ Գրաֆիկի համար հարցման տվյալներ հասանելի չեն։");
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
            .catch(error => console.error("❌ Գրաֆիկի համար հարցման տվյալներ հասանելի չեն:", error));
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

    fetchRequests("monthly");

    document.getElementById("timeFilter").addEventListener("change", function () {
        fetchRequests(this.value);
    });
});



function showSuccessMessage() {
    document.getElementById("successMessage").style.display = "block";

    document.getElementById("contactForm").reset();

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
        method: "POST",
        body: formData
    })
        .then(response => {
            if (response.ok) {
                alert("✅ Product updated successfully!");
                window.location.href = "/admin/products";
            } else {
                alert("❌ Error updating product.");
            }
        })
        .catch(error => console.error("❌ Error:", error));
}


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
    fetch('/admin/logout', { method: 'POST' })
        .then(() => {
            window.location.href = '/index';
        })
        .catch(error => console.error('Logout failed:', error));
}

function toggleUpload(type) {
    if (type === 'file') {
        document.getElementById("fileUpload").style.display = "block";
        document.getElementById("urlUpload").style.display = "none";
    } else {
        document.getElementById("fileUpload").style.display = "none";
        document.getElementById("urlUpload").style.display = "block";
    }
}

function previewFile() {
    const file = document.getElementById("productImage").files[0];
    const preview = document.getElementById("previewImage");

    if (file) {
        const reader = new FileReader();
        reader.onload = function (e) {
            preview.src = e.target.result;
        };
        reader.readAsDataURL(file);
    }
}

function previewUrl() {
    const url = document.getElementById("imageUrl").value;
    const preview = document.getElementById("previewImage");

    if (url) {
        preview.src = url;
    } else {
        preview.src = "";
    }
}

/////////////////////
document.addEventListener("DOMContentLoaded", async function () {
    const totalElement = document.getElementById("total");
    const currencySelect = document.getElementById("currency");
    let exchangeRates = {}; // Храним курсы валют

    async function fetchExchangeRates() {
        try {
            const response = await fetch("https://api.exchangerate-api.com/v4/latest/USD"); // API с курсами валют
            const data = await response.json();
            exchangeRates = data.rates; // Сохраняем курсы валют
        } catch (error) {
            console.error("Error fetching exchange rates:", error);
        }
    }

    async function updateCurrency() {
        const selectedCurrency = currencySelect.value;
        const usdTotal = parseFloat(totalElement.dataset.usd); // Получаем сумму в USD
        if (!isNaN(usdTotal) && exchangeRates[selectedCurrency]) {
            const convertedTotal = (usdTotal * exchangeRates[selectedCurrency]).toFixed(2);
            totalElement.textContent = `${convertedTotal} ${selectedCurrency}`;
        }
    }

    // Загружаем курсы валют при загрузке страницы
    await fetchExchangeRates();

    // Обновляем сумму при смене валюты
    currencySelect.addEventListener("change", updateCurrency);

    // Пример: Обновление суммы в USD (замените 100 на реальную сумму корзины)
    function updateTotalAmount(usdAmount) {
        totalElement.dataset.usd = usdAmount; // Сохраняем сумму в USD
        totalElement.textContent = `${usdAmount} USD`; // Отображаем в USD
        updateCurrency(); // Пересчитываем в выбранную валюту
    }

    // Симуляция обновления суммы корзины (подставьте свою логику)
    updateTotalAmount(100); // Например, сумма корзины = 100$
});