package com.example.backendfix.service;

import com.example.backendfix.entity.Order;
import com.example.backendfix.entity.Product;
import com.example.backendfix.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("OrderService Tests")
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    private Product testProduct;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        // Create test product
        testProduct = Product.builder()
                .name("Test Product")
                .description("A test product for unit tests")
                .build();

        // Create test order
        testOrder = Order.builder()
                .product(testProduct)
                .quantity(5)
                .price(BigDecimal.valueOf(99.99))
                .build();
    }

    @Test
    @DisplayName("getAllOrders should return paginated orders")
    void testGetAllOrdersReturnsPaginatedData() {
        // Arrange
        Order savedOrder = orderService.createOrder(testOrder);
        assertNotNull(savedOrder.getId(), "Order should be saved with an ID");

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Order> result = orderService.getAllOrders(pageable);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.getTotalElements() > 0, "Should have at least one order");
        assertEquals(1, result.getContent().size(), "First page should have one order");
        assertEquals(savedOrder.getId(), result.getContent().get(0).getId(), "Order ID should match");
    }

    @Test
    @DisplayName("getAllOrders pagination should work correctly with multiple pages")
    void testGetAllOrdersPaginationWithMultiplePages() {
        // Arrange
        // Create 15 orders
        for (int i = 0; i < 15; i++) {
            Order order = Order.builder()
                    .product(testProduct)
                    .quantity(i + 1)
                    .price(BigDecimal.valueOf(100.00 + i))
                    .build();
            orderService.createOrder(order);
        }

        // Act - Get first page with 10 items per page
        Pageable page1 = PageRequest.of(0, 10);
        Page<Order> firstPage = orderService.getAllOrders(page1);

        // Act - Get second page
        Pageable page2 = PageRequest.of(1, 10);
        Page<Order> secondPage = orderService.getAllOrders(page2);

        // Assert
        assertEquals(15, firstPage.getTotalElements(), "Total elements should be 15");
        assertEquals(10, firstPage.getContent().size(), "First page should have 10 items");
        assertEquals(5, secondPage.getContent().size(), "Second page should have 5 items");
        assertEquals(2, firstPage.getTotalPages(), "Should have 2 pages");
        assertTrue(firstPage.isFirst(), "First page should be marked as first");
        assertTrue(secondPage.isLast(), "Second page should be marked as last");
    }

    @Test
    @DisplayName("createOrder should save and return order with ID")
    void testCreateOrderSavesAndReturnsOrderWithId() {
        // Act
        Order savedOrder = orderService.createOrder(testOrder);

        // Assert
        assertNotNull(savedOrder, "Saved order should not be null");
        assertNotNull(savedOrder.getId(), "Saved order should have an ID");
        assertTrue(savedOrder.getId() > 0, "ID should be positive");
        assertEquals(5, savedOrder.getQuantity(), "Quantity should match");
        assertEquals(BigDecimal.valueOf(99.99), savedOrder.getPrice(), "Price should match");
        assertNotNull(savedOrder.getCreatedAt(), "CreatedAt should be set");
        assertNotNull(savedOrder.getUpdatedAt(), "UpdatedAt should be set");
    }

    @Test
    @DisplayName("getAllOrders should load product data (JOIN FETCH)")
    void testGetAllOrdersLoadsProductData() {
        // Arrange
        Order savedOrder = orderService.createOrder(testOrder);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Order> result = orderService.getAllOrders(pageable);
        Order retrievedOrder = result.getContent().get(0);

        // Assert
        assertNotNull(retrievedOrder.getProduct(), "Product should be loaded");
        assertNotNull(retrievedOrder.getProduct().getName(), "Product name should not be null");
        assertEquals("Test Product", retrievedOrder.getProduct().getName(), "Product name should match");
    }

}
