package com.getmed.pgno20.controller;

import com.getmed.pgno20.model.Order;
import com.getmed.pgno20.util.OrderFileUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @GetMapping
    public String viewOrders(Model model) {
        List<Order> orders = OrderFileUtil.readOrdersFromFile();
        model.addAttribute("orders", orders);
        return "order_list";
    }

    @GetMapping("/new")
    public String showOrderForm(Model model) {
        model.addAttribute("order", new Order());
        return "order_form";
    }

    @PostMapping("/save")
    public String saveOrder(@ModelAttribute Order order) {
        List<Order> orders = OrderFileUtil.readOrdersFromFile();

        if (order.getId() == 0) {
            int maxId = orders.stream().mapToInt(Order::getId).max().orElse(0);
            order.setId(maxId + 1);
            orders.add(order);
        } else {
            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i).getId() == order.getId()) {
                    orders.set(i, order);
                    break;
                }
            }
        }

        OrderFileUtil.writeOrdersToFile(orders);
        return "redirect:/orders";
    }

    @GetMapping("/edit/{id}")
    public String editOrder(@PathVariable int id, Model model) {
        List<Order> orders = OrderFileUtil.readOrdersFromFile();
        for (Order order : orders) {
            if (order.getId() == id) {
                model.addAttribute("order", order);
                return "order_form";
            }
        }
        return "redirect:/orders";
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable int id) {
        List<Order> orders = OrderFileUtil.readOrdersFromFile();
        orders.removeIf(order -> order.getId() == id);
        OrderFileUtil.writeOrdersToFile(orders);
        return "redirect:/orders";
    }
}
