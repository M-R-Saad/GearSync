package org.example.vehicle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.*;

public class OrderConfirmationController {

    @FXML
    private Label userInfoLabel;

    @FXML
    private TableView<Parts> productTable;

    @FXML
    private TableColumn<Parts, String> productNameColumn;

    @FXML
    private TableColumn<Parts, Integer> quantityColumn;

    @FXML
    private TableColumn<Parts, Double> pricePerItemColumn;

    @FXML
    private TableColumn<Parts, Double> netPriceColumn;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private RadioButton cashRadioButton;

    @FXML
    private RadioButton cardRadioButton;

    @FXML
    private RadioButton mobileRadioButton;

    private ObservableList<Parts> selectedProducts;
    private double totalPrice;

    String db_url = Constants.DATABASE_URL;
    String db_user = Constants.DATABASE_USERNAME;
    String db_pass = Constants.DATABASE_PASSWORD;

    public void initData(ObservableList<Parts> products, double total) {
        this.selectedProducts = products;
        this.totalPrice = total;

        // Set user information
        int customerId = SessionManager.getCurrentUserId();
        try (Connection connection = DriverManager.getConnection(db_url, db_user, db_pass)) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM customer WHERE cid = ?");
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String userInfo = String.format("Name: %s\nAddress: %s\nPhone: %s\nEmail: %s",
                        rs.getString("fullname"), rs.getString("address"),
                        rs.getString("phone"), rs.getString("email"));
                userInfoLabel.setText(userInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set product table
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        pricePerItemColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        netPriceColumn.setCellValueFactory(new PropertyValueFactory<>("netPrice"));
        productTable.setItems(selectedProducts);

        // Set total price
        totalPriceLabel.setText("Total Price: ৳" + String.format("%.2f", totalPrice));
    }

    @FXML
    private void handlePurchase() {
        if (mobileRadioButton.isSelected() || cashRadioButton.isSelected() || cardRadioButton.isSelected()) {
            try (Connection connection = DriverManager.getConnection(db_url, db_user, db_pass)) {
                // Insert into invoice table
                PreparedStatement invoiceStmt = connection.prepareStatement("INSERT INTO invoice (cid, total_price) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
                invoiceStmt.setInt(1, SessionManager.getCurrentUserId());
                invoiceStmt.setDouble(2, totalPrice);
                invoiceStmt.executeUpdate();

                ResultSet keys = invoiceStmt.getGeneratedKeys();
                int invoiceId = -1;
                if (keys.next()) {
                    invoiceId = keys.getInt(1);
                }

                // Insert into invoice_parts table
                for (Parts part : selectedProducts) {
                    PreparedStatement ipStmt = connection.prepareStatement("INSERT INTO invoice_parts (iid, pid, quantity, price) VALUES (?, ?, ?, ?)");
                    ipStmt.setInt(1, invoiceId);
                    ipStmt.setInt(2, part.getPid());
                    ipStmt.setInt(3, part.getQuantity());
                    ipStmt.setDouble(4, part.getNetPrice());
                    ipStmt.executeUpdate();

                    // Update parts stock
                    PreparedStatement updatePartStmt = connection.prepareStatement("UPDATE parts SET available_unit = available_unit - ? WHERE pid = ?");
                    updatePartStmt.setInt(1, part.getQuantity());
                    updatePartStmt.setInt(2, part.getPid());
                    updatePartStmt.executeUpdate();
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Purchase Successful");
                alert.setContentText("Your order has been placed successfully!");
                alert.showAndWait();

                // Close the popup
                Stage stage = (Stage) productTable.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Purchase Failed");
                alert.setContentText("An error occurred while processing your purchase. Please try again.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Payment Method Not Selected");
            alert.setContentText("Please select a payment method to proceed.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) productTable.getScene().getWindow();
        stage.close();
    }
}