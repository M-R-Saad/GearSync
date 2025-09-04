package org.example.vehicle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.vehicle.CarWashDetailsController;

public class CarWashCardController {

    @FXML
    private ImageView planImage;

    @FXML
    private Label planName;

    @FXML
    private Label planDescription;

    @FXML
    private Label planServices;

    @FXML
    private Label vehicleType;

    @FXML
    private Label planPrice;

    @FXML
    private Button selectButton;

    private int carWashId;
    private String imagePathStr;

    public void setCarWashData(int cwid, String name, String description, String services,
                               String vehicle, double price, String imagePath) {
        this.carWashId = cwid;
        planName.setText(name);
        planDescription.setText("Details: " + description);
        planServices.setText("Services: " + services);
        vehicleType.setText("Vehicle Type: " + vehicle);
        planPrice.setText("Price: ৳" + price);
        planImage.setImage(new Image(imagePath, false));
        this.imagePathStr = imagePath;

        selectButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("car_wash_details.fxml"));
                Parent root = loader.load();

                CarWashDetailsController controller = loader.getController();
                controller.initData(carWashId, name, description, services, vehicle, price, imagePath);

                Stage stage = (Stage) selectButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Car Wash Details");
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}