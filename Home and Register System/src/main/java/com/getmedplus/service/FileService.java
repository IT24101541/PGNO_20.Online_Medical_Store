package com.getmedplus.service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileService {

    private final String userFile = "users.txt";

    public void saveUser(String data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile, true))) {
            writer.write(data);
            writer.newLine();
        }
    }

    public List<String> readUsers() throws IOException {
        List<String> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                users.add(line);
            }
        }
        return users;
    }
}
