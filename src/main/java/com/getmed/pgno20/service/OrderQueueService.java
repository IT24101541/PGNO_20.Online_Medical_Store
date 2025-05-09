package com.getmed.pgno20.service;

import com.getmed.pgno20.model.Order;
import com.getmed.pgno20.repository.FileBasedOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class OrderQueueService {

    private final FileBasedOrderRepository orderRepository;

    @Autowired
    public OrderQueueService(FileBasedOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void addOrder(Order order) {
        orderRepository.addOrder(order);
    }

    public List<Order> getAllOrders() {
        return new LinkedList<>(orderRepository.getOrderQueue());
    }

    public void updateOrder(Order order) {
        orderRepository.updateOrder(order);
    }

    public void deleteOrder(int id) {
        orderRepository.deleteOrder(id);
    }
}