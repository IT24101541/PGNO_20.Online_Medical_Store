package com.getmed.pgno20.service;

import com.getmed.pgno20.model.Order;
import com.getmed.pgno20.repository.FileBasedOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderQueueService {

    private final FileBasedOrderRepository orderRepository;

    @Autowired
    public OrderQueueService(FileBasedOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void addOrder(Order order) {
        orderRepository.addOrder(order); // Adds to shared queue, accessible by both admin and customer
    }

    public List<Order> getAllOrders() {
        return new LinkedList<>(orderRepository.getOrderQueue()); // Admin queue: all orders
    }

    public List<Order> getCustomerOrders(String customerEmail) {
        return new LinkedList<>(orderRepository.getOrderQueue()).stream()
                .filter(order -> order.getCustomerEmail().equals(customerEmail))
                .collect(Collectors.toList()); // Customer queue: filtered by email
    }

    public void updateOrder(Order order) {
        orderRepository.updateOrder(order);
    }

    public void deleteOrder(int id) {
        orderRepository.deleteOrder(id);
    }
}