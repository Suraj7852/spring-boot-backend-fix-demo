package com.example.backendfix.service;

import com.example.backendfix.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    Page<Order> getAllOrders(Pageable pageable);

    Order createOrder(Order order);

}
