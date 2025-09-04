package org.example.vehicle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class WorkerInfoController {

    @FXML
    private Label fullNameLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private ImageView workerImageView;

    private Stage stage;

    public void setWorkerDetails(String fullName, String phone, String email, String status, String imagePath) {
        fullNameLabel.setText(fullName);
        phoneLabel.setText(phone);
        emailLabel.setText(email);
        statusLabel.setText(status);

        if (imagePath != null && !imagePath.isEmpty()) {
            Image workerImage = new Image(imagePath, false);
            workerImageView.setImage(workerImage);
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void closeWindow() {
        if (stage != null) {
            stage.close();
        }
    }
}