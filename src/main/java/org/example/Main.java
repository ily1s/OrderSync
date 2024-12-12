package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    // Informations de connexion à la base de données
    private static final String DB_URL = "jdbc:mysql://localhost:3306/OrderSync";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "SqlRoot0000";

    public static void main(String[] args) {
        System.out.println("[INFO] Démarrage de l'application...");
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            if (connection != null) {
                System.out.println("[INFO] Connexion à la base de données réussie.");
            }

            FileProcess fileProcess = new FileProcess();
            OrderProcess orderProcess = new OrderProcess(connection, fileProcess);

            System.out.println("[INFO] Lancement du processus de traitement des commandes...");
            orderProcess.start();

        } catch (SQLException e) {
            System.out.println("[ERROR] Erreur de connexion à la base de données.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("[ERROR] Une erreur inattendue s'est produite.");
            e.printStackTrace();
        }
    }
}
