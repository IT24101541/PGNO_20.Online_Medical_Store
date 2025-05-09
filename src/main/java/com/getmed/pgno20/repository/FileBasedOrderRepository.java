package com.getmed.pgno20.repository;

import com.getmed.pgno20.model.Order;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FileBasedOrderRepository {

    private static final String ORDER_FILE_PATH = "src/main/resources/data/orders.txt";
    private LinkedList<Order> orderQueue;

    public FileBasedOrderRepository() {
        this.orderQueue = loadOrdersFromFile();
    }

    public LinkedList<Order> getOrderQueue() {
        return new LinkedList<>(orderQueue);
    }

    public void addOrder(Order order) {
        int maxId = orderQueue.stream().mapToInt(Order::getId).max().orElse(0);
        order.setId(maxId + 1);
        order.setOrderDate(java.time.LocalDate.now().toString()); // Set current date
        orderQueue.add(order);
        saveOrdersToFile();
    }

    public void updateOrder(Order order) {
        orderQueue.removeIf(o -> o.getId() == order.getId());
        order.setOrderDate(java.time.LocalDate.now().toString()); // Update date on edit
        orderQueue.add(order);
        saveOrdersToFile();
    }

    public void deleteOrder(int id) {
        orderQueue.removeIf(o -> o.getId() == id);
        saveOrdersToFile();
    }

    private LinkedList<Order> loadOrdersFromFile() {
        LinkedList<Order> orders = new LinkedList<>();
        File file = new File(ORDER_FILE_PATH);
        if (!file.exists()) {
            File directory = file.getParentFile();
            if (!directory.exists() && !directory.mkdirs()) {
                System.err.println("Failed to create directory: " + directory.getAbsolutePath());
            }
            return orders;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 8) { // id,customerEmail,medicineQuantities,totalQuantity,orderDate,prescriptionUrl,receiptUrl,status
                    try {
                        Order order = new Order();
                        order.setId(Integer.parseInt(data[0].trim()));
                        order.setCustomerEmail(data[1].trim());
                        // Parse medicine quantities (format: "name:quantity|name:quantity")
                        if (!data[2].isEmpty()) {
                            String[] medQtyPairs = data[2].split("\\|");
                            for (String pair : medQtyPairs) {
                                String[] parts = pair.split(":");
                                if (parts.length == 2) {
                                    order.getMedicineQuantities().put(parts[0], Integer.parseInt(parts[1]));
                                }
                            }
                        }
                        order.setTotalQuantity(Integer.parseInt(data[3].trim()));
                        order.setOrderDate(data[4].trim());
                        order.setPrescriptionUrl(data[5].trim());
                        order.setReceiptUrl(data[6].trim());
                        order.setStatus(data[7].trim());
                        orders.add(order);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing order data: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading orders: " + e.getMessage());
        }
        return orders;
    }

    private void saveOrdersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDER_FILE_PATH))) {
            for (Order order : orderQueue) {
                String medicineQuantities = order.getMedicineQuantities().entrySet().stream()
                        .map(entry -> entry.getKey() + ":" + entry.getValue())
                        .collect(Collectors.joining("|"));
                writer.write(String.format("%d,%s,%s,%d,%s,%s,%s,%s%n",
                        order.getId(),
                        order.getCustomerEmail(),
                        medicineQuantities,
                        order.getTotalQuantity(),
                        order.getOrderDate(),
                        order.getPrescriptionUrl() != null ? order.getPrescriptionUrl() : "",
                        order.getReceiptUrl() != null ? order.getReceiptUrl() : "",
                        order.getStatus()));
            }
        } catch (IOException e) {
            System.err.println("Error writing orders: " + e.getMessage());
        }
    }
}