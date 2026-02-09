package com.example.backendfix.repository;

import com.example.backendfix.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Fetch all orders with their associated products using JOIN FETCH.
     * 
     * WHY THIS FIXES PERFORMANCE ISSUES:
     * - Prevents N+1 Query Problem: Without JOIN FETCH, fetching 100 orders would execute
     *   1 query to get orders + 100 queries to lazy-load each product (101 total queries).
     *   With JOIN FETCH, we get all data in a SINGLE query using a JOIN.
     * 
     * - Reduces Network Roundtrips: Combines order and product data in one query result,
     *   reducing database round trips from O(N) to O(1).
     * 
     * - Eager Loading: Products are loaded immediately with orders, eliminating the need
     *   for separate lazy-load queries when accessing order.getProduct().
     * 
     * JPQL Query: "SELECT DISTINCT o FROM Order o JOIN FETCH o.product"
     * - Translates to: SELECT DISTINCT o.*, p.* FROM orders o 
     *   INNER JOIN products p ON o.product_id = p.id
     */
    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.product ORDER BY o.id DESC")
    List<Order> findAllOrdersWithProducts();

}

