package org.example.vehicle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.*;

public class CarWashCardController2 {

    @FXML private ImageView carImage;
    @FXML private Label planName;
    @FXML private Label cwid;
    @FXML private Label cid;
    @FXML private Label carLocation;
    @FXML private Label carModel;
    @FXML private Label date_time;
    @FXML private Label price;
    @FXML private Label createdAt;
    @FXML private Button takeJobButton;

    String db_url = Constants.DATABASE_URL;
    String db_user = Constants.DATABASE_USERNAME;
    String db_pass = Constants.DATABASE_PASSWORD;

    private int rid;

    public void setData(CarWash carWash) {
        this.rid = carWash.getRid();
        planName.setText("Plan: " + carWash.getPlanName());
        cwid.setText("Plan ID: " + carWash.getCwid());
        cid.setText("Customer ID: " + carWash.getCid());
        carLocation.setText("Location: " + carWash.getCarLocation());
        carModel.setText("Car Model: " + carWash.getCarModel());
        date_time.setText("Date & Time: " + carWash.getDateTime());
        price.setText("Price: ৳" + getPriceFromDatabase(carWash.getCwid()));
        createdAt.setText("Created at: " + carWash.getCreatedAt());

        if (carWash.getPicture() != null && !carWash.getPicture().isEmpty()) {
            carImage.setImage(new Image(carWash.getPicture()));
        }
    }

    private double getPriceFromDatabase(int cwid) {
        String query = "SELECT price FROM car_wash WHERE cwid = ?";
        try (Connection connection = DriverManager.getConnection(db_url, db_user, db_pass);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cwid);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public void handleTakeJob() {
        AvailableServiceController.takeCarWashJob(rid);
    }
}
