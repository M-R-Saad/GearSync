package org.example.vehicle;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Optional;

public class CarWashCardControllerAdmin {

    @FXML
    private ImageView planImage;

    @FXML
    private Label planName;

    @FXML
    private Label planDescription;

    @FXML
    private Label planServices;

    @FXML
    private Label planPrice;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    private int carWashId;

    String db_url = Constants.DATABASE_URL;
    String db_user = Constants.DATABASE_USERNAME;
    String db_pass = Constants.DATABASE_PASSWORD;

    public void setCarWashData(int cwid, String name, String description, String services, double price, String imagePath) {
        this.carWashId = cwid;
        planName.setText(name);
        planDescription.setText("Description: " + description);
        planServices.setText("Services: " + services);
        planPrice.setText("Price: ৳" + price);

        if (imagePath != null && !imagePath.isEmpty()) {
            planImage.setImage(new Image(imagePath));
        }
    }

    @FXML
    private void handleEdit() {
        // Show a TextInputDialog to update the price
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Update Price");
        dialog.setHeaderText("Update Price for Plan: " + planName.getText());
        dialog.setContentText("Enter the new price:");

        // Get the user input
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newPrice -> {
            try {
                double price = Double.parseDouble(newPrice);

                // Update the price in the database
                try (Connection conn = DriverManager.getConnection(db_url, db_user, db_pass)) {
                    String query = "UPDATE car_wash SET price = ? WHERE cwid = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setDouble(1, price);
                    stmt.setInt(2, carWashId);

                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        planPrice.setText("Price: ৳" + price);
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Price updated successfully!");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to update the price.");
                    }
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for the price.");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while updating the price.");
            }
        });
    }

    @FXML
    private void handleDelete() {
        try (Connection conn = DriverManager.getConnection(db_url, db_user, db_pass)) {
            String query = "DELETE FROM car_wash WHERE cwid = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, carWashId);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Car wash plan deleted successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete the car wash plan.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while deleting the car wash plan.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}