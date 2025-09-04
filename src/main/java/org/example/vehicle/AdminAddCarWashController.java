package org.example.vehicle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;

public class AdminAddCarWashController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private TextField planName;

    @FXML
    private TextArea planDescription;

    @FXML
    private TextArea planServices;

    @FXML
    private TextField vehicleType;

    @FXML
    private TextField planPrice;

    @FXML
    private Label carWashImageLabel;

    @FXML
    private ImageView planImage;

    @FXML
    private FlowPane flowPane_carWash;

    String db_url = Constants.DATABASE_URL;
    String db_user = Constants.DATABASE_USERNAME;
    String db_pass = Constants.DATABASE_PASSWORD;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadAllCarWashPlans();
    }

    private void loadAllCarWashPlans() {
        flowPane_carWash.getChildren().clear();
        try (Connection conn = DriverManager.getConnection(db_url, db_user, db_pass)) {
            String query = "SELECT * FROM car_wash";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int cwid = rs.getInt("cwid");
                String name = rs.getString("plan_name");
                String desc = rs.getString("description");
                String services = rs.getString("services");
                double price = rs.getDouble("price");
                String imagePath = rs.getString("picture");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("car_wash_card2.fxml"));
                Parent card = loader.load();

                CarWashCardControllerAdmin controller = loader.getController();

//                if (!imagePath.startsWith("file:") && new File(imagePath).exists()) {
//                    imagePath = "file:///" + imagePath.replace("\\", "/");
//                }

                controller.setCarWashData(cwid, name, desc, services, price, imagePath);
                flowPane_carWash.getChildren().add(card);
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void addPicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Car Wash Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                String planNameInput = planName.getText().trim().replaceAll(" ", "_").toLowerCase();
                String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
                String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
                String finalFileName = planNameInput + "_" + uniqueSuffix + extension;

                String targetDirPath = "E:/IntelliJ/Vehicle/car_wash";
                File targetDir = new File(targetDirPath);
                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                }

                File destFile = new File(targetDir, finalFileName);
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                String imagePath = "file:///" + destFile.getAbsolutePath().replace("\\", "/");
                Image image = new Image(imagePath);
                planImage.setImage(image);
                carWashImageLabel.setText(imagePath);

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Image Error", "Failed to copy and set image.");
            }
        }
    }

    public void confirmAddCarWash() {
        String name = planName.getText();
        String description = planDescription.getText();
        String services = planServices.getText();
        String vehicle_type = vehicleType.getText();
        String priceText = planPrice.getText();
        String imagePath = carWashImageLabel.getText();

        if (name.isEmpty() || description.isEmpty() || services.isEmpty() || priceText.isEmpty() || imagePath.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Missing Data", "Please fill all fields and select an image.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);

            try (Connection conn = DriverManager.getConnection(db_url, db_user, db_pass)) {
                String query = "INSERT INTO car_wash (plan_name, description, services, vehicle_type, price, picture) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setString(2, description);
                stmt.setString(3, services);
                stmt.setString(4, vehicle_type);
                stmt.setDouble(5, price);
                stmt.setString(6, imagePath);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "New car wash plan added successfully!");
                    planName.clear();
                    planDescription.clear();
                    planServices.clear();
                    planPrice.clear();
                    planImage.setImage(null);
                    carWashImageLabel.setText("");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to insert car wash plan.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Price", "Please enter a valid price.");
        }
        loadAllCarWashPlans();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void goToHomePage(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("admin_home.fxml"));
        root = loader.load();
        scene = new Scene(root);
        stage = (Stage)anchorPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void chatWithCW(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("admin_chat_atcw.fxml"));
        root = loader.load();
        scene = new Scene(root);
        stage = (Stage)anchorPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    public void cwStatistics(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("admin_cw_statistics.fxml"));
        root = loader.load();
        scene = new Scene(root);
        stage = (Stage)anchorPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void addParts(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("admin_add_parts.fxml"));
        root = loader.load();
        scene = new Scene(root);
        stage = (Stage)anchorPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void addCarWash(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("admin_add_carWash.fxml"));
        root = loader.load();
        scene = new Scene(root);
        stage = (Stage)anchorPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    public void logOut(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("login.fxml"));
        root = loader.load();
        scene = new Scene(root);
        stage = (Stage)anchorPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}