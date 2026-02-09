package com.example.backendfix.service.impl;

import com.example.backendfix.entity.Order;
import com.example.backendfix.repository.OrderRepository;
import com.example.backendfix.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public Page<Order> getAllOrders(Pageable pageable) {
        // PERFORMANCE OPTIMIZATION: Using JOIN FETCH query instead of standard findAll()
        // This prevents the N+1 query problem by eagerly loading Product entities
        // in a single query. Without this optimization, accessing order.getProduct()
        // would trigger lazy-load queries, resulting in 1 + N database queries.
        // With JOIN FETCH: 1 query gets all orders and their products together.
        List<Order> orders = orderRepository.findAllOrdersWithProducts();
        
        // Convert to Page for pagination support
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), orders.size());
        
        List<Order> pageContent = orders.subList(start, end);
        return new PageImpl<>(pageContent, pageable, orders.size());
    }

    @Override
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

}
