package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.sql.*;

class OrderProcess extends Thread {
    private Connection connection;
    private FileProcess fileProcess;
    private ObjectMapper objectMapper;

    public OrderProcess(Connection connection, FileProcess fileProcess) {
        this.connection = connection;
        this.fileProcess = fileProcess;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    System.out.println("[INFO] Lecture des fichiers dans le dossier input...");

                    // Appeler la méthode pour traiter les fichiers
                    File[] files = fileProcess.getInputFiles();
                    if (files != null) {
                        for (File file : files) {
                            processFile(file);
                        }
                    }

                    System.out.println("[INFO] Tous les fichiers dans le dossier input ont été traités.");
                    System.out.println("[INFO] En attente avant le prochain cycle...");

                    // Pause pendant une heure
                    Thread.sleep(3600 * 1000);
                }
            } catch (InterruptedException e) {
                System.out.println("[ERROR] Le processus a été interrompu.");
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("[ERROR] Une erreur s'est produite lors du traitement des commandes.");
                e.printStackTrace();
            }
        }
    }

    private void processFile(File file) {
        System.out.println("[INFO] Traitement du fichier : " + file.getName());
        try {
            JsonNode orderNode = objectMapper.readTree(file);

            int customerId = orderNode.get("customer_id").asInt();
            if (checkCustomerExists(customerId)) {
                System.out.println("[INFO] Le client avec l'ID " + customerId + " existe. Insertion de la commande...");
                insertOrder(orderNode);
                fileProcess.moveFileToOutput(file);
                System.out.println("[INFO] Fichier déplacé vers le dossier output : " + file.getName());
            } else {
                System.out.println("[WARNING] Le client avec l'ID " + customerId + " n'existe pas. Déplacement du fichier vers errors...");
                fileProcess.moveFileToError(file);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Erreur lors du traitement du fichier : " + file.getName());
            e.printStackTrace();
            try {
                fileProcess.moveFileToError(file);
                System.out.println("[INFO] Fichier déplacé vers le dossier errors : " + file.getName());
            } catch (IOException ioException) {
                System.out.println("[ERROR] Impossible de déplacer le fichier vers errors : " + file.getName());
                ioException.printStackTrace();
            }
        }
    }

    private boolean checkCustomerExists(int customerId) throws SQLException {
        System.out.println("[INFO] Vérification de l'existence du client avec l'ID : " + customerId);
        String query = "SELECT COUNT(*) FROM customer WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                boolean exists = rs.next() && rs.getInt(1) > 0;
                System.out.println("[INFO] Client " + (exists ? "existe" : "n'existe pas") + " dans la base de données.");
                return exists;
            }
        }
    }

    private void insertOrder(JsonNode order) throws SQLException {
        System.out.println("[INFO] Insertion de la commande dans la base de données : " + order.toString());
        String query = "INSERT INTO `order` (id, date, amount, customer_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, order.get("id").asInt());
            stmt.setString(2, order.get("date").asText());
            stmt.setDouble(3, order.get("amount").asDouble());
            stmt.setInt(4, order.get("customer_id").asInt());
            stmt.executeUpdate();
            System.out.println("[INFO] Commande insérée avec succès dans la base de données.");
        }
    }
}