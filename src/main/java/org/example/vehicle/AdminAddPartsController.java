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
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;

public class AdminAddPartsController implements Initializable {

    @FXML
    private Stage stage;

    @FXML
    private Scene scene;

    @FXML
    private Parent root;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private FlowPane flowPane_parts;

    @FXML
    private TextField partsName;

    @FXML
    private TextArea partsDescription;

    @FXML
    private TextField partsPrice;

    @FXML
    private Spinner<Integer> stockSpinner;

    @FXML
    private Label partImageLabel;

    @FXML
    private ImageView partsImage;

    String db_url = Constants.DATABASE_URL;
    String db_user = Constants.DATABASE_USERNAME;
    String db_pass = Constants.DATABASE_PASSWORD;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SpinnerValueFactory<Integer> stockFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 0);
        stockSpinner.setValueFactory(stockFactory);

        loadAllPartsForAdmin();
    }

    private void loadAllPartsForAdmin() {
        flowPane_parts.getChildren().clear();
        try (Connection conn = DriverManager.getConnection(db_url, db_user, db_pass)) {
            String query = "SELECT * FROM parts";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int pid = rs.getInt("pid");
                String name = rs.getString("name");
                String desc = rs.getString("description");
                double price = rs.getDouble("price");
                int stock = rs.getInt("available_unit");
                String imagePath = rs.getString("picture");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("parts_card2.fxml"));
                Parent card = loader.load();

                PartsCardControllerAdmin controller = loader.getController();

                // Adjust image path if needed
                if (!imagePath.startsWith("file:") && new File(imagePath).exists()) {
                    imagePath = "file:///" + imagePath.replace("\\", "/");
                }

                controller.setPartData(pid, name, desc, price, stock, imagePath);
                flowPane_parts.getChildren().add(card);
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }


    public void addPicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Part Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // Prepare destination path
                String partNameInput = partsName.getText().trim().replaceAll(" ", "_").toLowerCase();

                // Get file extension
                String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));

                // Generate unique suffix (UUID)
                String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8); // short unique ID
                String finalFileName = partNameInput + "_" + uniqueSuffix + extension;

                // Target directory
                String targetDirPath = "E:/IntelliJ/Vehicle/vehicle_parts";
                File targetDir = new File(targetDirPath);
                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                }

                // Final destination file
                File destFile = new File(targetDir, finalFileName);
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Convert to proper file URI for ImageView
                String imagePath = "file:///" + destFile.getAbsolutePath().replace("\\", "/");
                Image image = new Image(imagePath);
                partsImage.setImage(image);
                partImageLabel.setText(imagePath); // Save to DB

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Image Error", "Failed to copy and set image.");
            }
        }
    }



    public void confirmAddParts() {
        String name = partsName.getText();
        String description = partsDescription.getText();
        String priceText = partsPrice.getText();
        String imagePath = partImageLabel.getText();
        int stock = stockSpinner.getValue();

        if (name.isEmpty() || description.isEmpty() || priceText.isEmpty() || imagePath.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Missing Data", "Please fill all fields and select an image.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);

            try (Connection conn = DriverManager.getConnection(db_url, db_user, db_pass)) {
                String query = "INSERT INTO parts (name, description, price, available_unit, picture) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setString(2, description);
                stmt.setDouble(3, price);
                stmt.setInt(4, stock);
                stmt.setString(5, imagePath);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "New part added successfully!");
                    partsName.clear();
                    partsDescription.clear();
                    partsPrice.clear();
                    partsImage.setImage(null);
                    partImageLabel.setText("");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to insert part.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Price", "Please enter a valid price.");
        }
        loadAllPartsForAdmin();
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
