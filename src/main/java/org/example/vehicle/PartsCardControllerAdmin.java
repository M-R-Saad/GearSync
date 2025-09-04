package org.example.vehicle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.*;
import java.util.Optional;

public class PartsCardControllerAdmin {

    @FXML
    private ImageView partImage;

    @FXML
    private Label partName;

    @FXML
    private Label partDescription;

    @FXML
    private Label stockStatus;

    @FXML
    private Spinner<Double> priceSpinner;

    @FXML
    Button updatePriceButton;

    @FXML
    private Button restockButton;

    private int partId; // unique ID to identify part for DB updates
    String db_url = Constants.DATABASE_URL;
    String db_user = Constants.DATABASE_USERNAME;
    String db_pass = Constants.DATABASE_PASSWORD;

    public void setPartData(int id, String name, String desc, double price, int stock, String imagePath) {
        this.partId = id;
        partName.setText(name);
        partDescription.setText(desc);
        stockStatus.setText("Stock: " + stock);
        partImage.setImage(new Image(imagePath));

        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 100000, price, 10);
        priceSpinner.setValueFactory(valueFactory);
    }

    @FXML
    private void handleUpdatePrice(ActionEvent event) {
        double newPrice = priceSpinner.getValue();
        try (Connection conn = DriverManager.getConnection(db_url, db_user, db_pass)) {
            String query = "UPDATE parts SET price = ? WHERE pid = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDouble(1, newPrice);
            stmt.setInt(2, partId);
            int updated = stmt.executeUpdate();

            if (updated > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Price updated successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Price update failed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database error during price update.");
        }
    }

    @FXML
    private void handleRestock(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Restock Parts");
        dialog.setHeaderText("Add more units to stock");
        dialog.setContentText("Enter quantity to add:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            try {
                int additionalStock = Integer.parseInt(input);
                if (additionalStock <= 0) {
                    showAlert(Alert.AlertType.WARNING, "Quantity must be greater than 0.");
                    return;
                }

                try (Connection conn = DriverManager.getConnection(db_url, db_user, db_pass)) {
                    String updateQuery = "UPDATE parts SET available_unit = available_unit + ? WHERE pid = ?";
                    PreparedStatement stmt = conn.prepareStatement(updateQuery);
                    stmt.setInt(1, additionalStock);
                    stmt.setInt(2, partId);
                    int updated = stmt.executeUpdate();

                    if (updated > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Stock updated successfully!");
                        // Optional: Update the label text if needed
                        updateStockStatusLabel(conn);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Stock update failed.");
                    }
                }

            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid number entered.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database error during restocking.");
            }
        });
    }

    private void updateStockStatusLabel(Connection conn) throws SQLException {
        String query = "SELECT available_unit FROM parts WHERE pid = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, partId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            int newStock = rs.getInt("available_unit");
            stockStatus.setText("Stock: " + newStock);
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

