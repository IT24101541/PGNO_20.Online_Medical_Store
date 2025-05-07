package com.getmed.pgno20.util;

import com.getmed.pgno20.model.Order;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderFileUtil {
    private static final String DIRECTORY_PATH = "data";
    private static final String FILE_PATH = DIRECTORY_PATH + "/orders.txt";

    public static List<Order> readOrdersFromFile() {
        List<Order> orders = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) return orders;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    Order order = new Order();
                    order.setId(Integer.parseInt(parts[0]));
                    order.setCustomerEmail(parts[1]);
                    // Parse medicine quantities (format: "name:quantity;name:quantity")
                    if (!parts[2].isEmpty()) {
                        String[] medQtyPairs = parts[2].split(";");
                        for (String pair : medQtyPairs) {
                            String[] medQty = pair.split(":");
                            if (medQty.length == 2) {
                                order.getMedicineQuantities().put(medQty[0], Integer.parseInt(medQty[1]));
                            }
                        }
                    }
                    order.setTotalQuantity(Integer.parseInt(parts[3]));
                    order.setOrderDate(parts[4]);
                    order.setPrescriptionUrl(parts[5].isEmpty() ? null : parts[5]);
                    order.setReceiptUrl(parts[6].isEmpty() ? null : parts[6]);
                    order.setStatus(parts[7]);
                    orders.add(order);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading orders: " + e.getMessage());
        }
        return orders;
    }

    public static void writeOrdersToFile(List<Order> orders) {
        File dir = new File(DIRECTORY_PATH);
        if (!dir.exists()) dir.mkdirs();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Order order : orders) {
                String medicineQuantities = order.getMedicineQuantities().entrySet().stream()
                        .map(entry -> entry.getKey() + ":" + entry.getValue())
                        .collect(Collectors.joining(";"));
                String prescriptionUrl = order.getPrescriptionUrl() != null ? order.getPrescriptionUrl() : "";
                String receiptUrl = order.getReceiptUrl() != null ? order.getReceiptUrl() : "";
                bw.write(order.getId() + "," + order.getCustomerEmail() + "," +
                        medicineQuantities + "," + order.getTotalQuantity() + "," +
                        order.getOrderDate() + "," + prescriptionUrl + "," + receiptUrl + "," +
                        order.getStatus());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing orders: " + e.getMessage(), e);
        }
    }
}