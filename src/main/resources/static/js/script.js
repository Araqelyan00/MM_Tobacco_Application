const productCards = document.querySelectorAll('.product-card');
const modal = document.getElementById('productModal');
const modalDetails = document.getElementById('modalDetails');
const closeModal = document.querySelector('.close-btn');

// Dummy product data with image URLs
const products = {
    1: {
        name: 'Product 1',
        description: 'This is product 1.',
        price: '$10.00',
        image: 'https://via.placeholder.com/300'
    },
    2: {
        name: 'Product 2',
        description: 'This is product 2.',
        price: '$15.00',
        image: 'https://via.placeholder.com/300'
    },
    3: {
        name: 'Product 3',
        description: 'This is product 3.',
        price: '$20.00',
        image: 'https://static.parma.am/origin/product/1024/66283(1).jpg?v=1736998112'
    },
};

// Open modal with product details
productCards.forEach(card => {
    card.addEventListener('click', () => {
        const productId = card.dataset.id;
        const product = products[productId];
        modalDetails.innerHTML = `
            <div class="modal-product">
                <div class="modal-product-image">
                    <img src="${product.image}" alt="${product.name}">
                </div>
                <div class="modal-product-info">
                    <h3>${product.name}</h3>
                    <p>${product.description}</p>
                    <p><strong>${product.price}</strong></p>
                    <button class="btn add-to-cart">Add to Cart</button>
                </div>
            </div>
        `;
        modal.classList.remove('hidden');
    });
});

// Close modal
closeModal.addEventListener('click', () => {
    modal.classList.add('hidden');
});
