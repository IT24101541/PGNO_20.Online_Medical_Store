package com.getmed.pgno20.util;

import com.getmed.pgno20.model.Order;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
                if (parts.length == 5) {
                    Order order = new Order(
                            Integer.parseInt(parts[0]),
                            parts[1],
                            parts[2],
                            Integer.parseInt(parts[3]),
                            parts[4]
                    );
                    orders.add(order);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading orders: " + e.getMessage());
        }
        return orders;
    }

    public static void writeOrdersToFile(List<Order> orders) {
        File dir = new File(DIRECTORY_PATH);
        if (!dir.exists()) dir.mkdirs();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Order order : orders) {
                bw.write(order.getId() + "," + order.getCustomerName() + "," + order.getMedicineName() + ","
                        + order.getQuantity() + "," + order.getOrderDate());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing orders: " + e.getMessage());
        }
    }
}
