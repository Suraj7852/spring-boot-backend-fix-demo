package com.example.backendfix.service.impl;

import com.example.backendfix.entity.Order;
import com.example.backendfix.repository.OrderRepository;
import com.example.backendfix.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

}
