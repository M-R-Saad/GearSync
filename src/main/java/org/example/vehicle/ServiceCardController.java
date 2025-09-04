package org.example.vehicle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class ServiceCardController {

    @FXML
    private AnchorPane serviceCard;

    @FXML
    private ImageView img_vehicle;

    @FXML
    private Label lbl_title, lbl_description, lbl_vehicleType, lbl_customerId;

    @FXML
    private TextField txt_payment;

    @FXML
    private Button btn_takeJob;

    private int serviceId;
    private AvailableServiceController parentController;
    private int currentWorkerId;

    String db_url = Constants.DATABASE_URL;
    String db_user = Constants.DATABASE_USERNAME;
    String db_pass = Constants.DATABASE_PASSWORD;

    public void setData(Service service, AvailableServiceController parent, int workerId) {
        lbl_title.setText(service.getTitle());
        lbl_description.setText(service.getDescription());
        lbl_vehicleType.setText(service.getVehicleType());
        lbl_customerId.setText("Customer ID: " + service.getCid());

        img_vehicle.setImage(new Image(service.getPicture()));
        
        this.serviceId = service.getSid();
        this.parentController = parent;
        this.currentWorkerId = workerId;
    }

    @FXML
    void onTakeJob(ActionEvent event) {
        String paymentText = txt_payment.getText();

        if (paymentText.isEmpty()) {
            System.out.println("Please enter payment amount.");
            return;
        }

        try {
            double paymentAmount = Double.parseDouble(paymentText);

            Connection conn = DriverManager.getConnection(db_url, db_user, db_pass);
            PreparedStatement stmt = conn.prepareStatement("UPDATE service SET wid = ?, payment_amount = ?, status = ?, updated_at = ? WHERE sid = ?");
            stmt.setInt(1, currentWorkerId);
            stmt.setDouble(2, paymentAmount);
            stmt.setString(3, "In Progress");
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(5, serviceId);

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Service taken successfully.");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success!");
                alert.setHeaderText(null);
                alert.setContentText("Service has been taken successfully.");
                alert.showAndWait();
                parentController.removeServiceCard(serviceCard);
                parentController.loadTakenServices();
            }
            else {
                System.out.println("Failed to take service.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText(null);
                alert.setContentText("Failed to take service. Please try again.");
                alert.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
