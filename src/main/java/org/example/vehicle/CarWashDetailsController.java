package org.example.vehicle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CarWashDetailsController {

    @FXML
    Stage stage;

    @FXML
    Scene scene;

    @FXML
    Parent root;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private VBox cardVBox;
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
    private TextField customerName;
    @FXML
    private TextField customerPhone;
    @FXML
    private TextField customerEmail;
    @FXML
    private TextField customerAddress;

    @FXML
    private TextField carLocation;
    @FXML
    private TextField carModel;
    @FXML
    private TextField pickTime;
    @FXML
    private DatePicker pickDate;

    @FXML
    private ImageView carImage;
    @FXML
    private Label carImageLabel;
    private int carWashId;
    private double price;

    private final int currentUserId = SessionManager.getCurrentUserId();
    private final String currentUserType = SessionManager.getCurrentUserType();
    String db_url = Constants.DATABASE_URL;
    String db_user = Constants.DATABASE_USERNAME;
    String db_pass = Constants.DATABASE_PASSWORD;

    public void initData(int carWashId, String name, String description, String services, String vehicle, double price, String imagePath) {
        this.carWashId = carWashId;
        planName.setText(name);
        planDescription.setText("Details: " + description);
        planServices.setText("Services: " + services);
        vehicleType.setText("Vehicle Type: " + vehicle);
        planPrice.setText("Price: ৳" + price);
        planImage.setImage(new Image(imagePath));
        this.price = price;

        loadUserInfo();
    }

    private void loadUserInfo() {
        try (Connection conn = DriverManager.getConnection(db_url, db_user, db_pass)) {
            String query = "SELECT * FROM customer WHERE cid = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                customerName.setText(rs.getString("username"));
                customerPhone.setText(rs.getString("phone"));
                customerEmail.setText(rs.getString("email"));
                customerAddress.setText(rs.getString("address"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load user information");
        }
    }

    @FXML
    public void addCarImage() {
        FileChooser fileChooser = new FileChooser();
        File chosenFile = fileChooser.showOpenDialog(null);

        if (chosenFile != null) {
            String originalPath = chosenFile.getAbsolutePath();
            System.out.println("Selected Image: " + originalPath);

            try {
                String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss-SSS"));
                String fileName = currentUserType + "_" + currentUserId + "_" + currentDateTime + ".jpg";
                String destinationPath = "E:\\IntelliJ\\Vehicle\\vehicle_wash\\" + fileName;


                InputStream is = new FileInputStream(originalPath);
                OutputStream os = new FileOutputStream(destinationPath);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                is.close();
                os.close();

                String dbImagePath = "file:///" + destinationPath.replace("\\", "/");
                carImageLabel.setText(dbImagePath);

                Image vehicleImage = new Image(dbImagePath, false);
                carImage.setImage(vehicleImage);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleConfirmService() {
        if (validateInputs()) {
            saveBooking();
        }
    }

    private boolean validateInputs() {
        if (carLocation.getText().isEmpty() || carModel.getText().isEmpty() ||
                pickTime.getText().isEmpty() || pickDate.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Please fill in all required fields");
            return false;
        }
        return true;
    }

    private void saveBooking() {
        String db_url = Constants.DATABASE_URL;
        String db_user = Constants.DATABASE_USERNAME;
        String db_pass = Constants.DATABASE_PASSWORD;

        try (Connection conn = DriverManager.getConnection(db_url, db_user, db_pass)) {
            String query = "INSERT INTO receipt (cid, cwid, car_location, car_model, time, date, picture) VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, SessionManager.getCurrentUserId());
            stmt.setInt(2, carWashId);
            stmt.setString(3, carLocation.getText());
            stmt.setString(4, carModel.getText());
            stmt.setString(5, pickTime.getText());
            stmt.setDate(6, Date.valueOf(pickDate.getValue()));
            stmt.setString(7, carImageLabel.getText());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Booking confirmed successfully!");
                goBackToPlans();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save booking");
        }
    }

    @FXML
    private void goBackToPlans() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("customer_car_wash.fxml"));
            Stage stage = (Stage) carLocation.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Car Wash Plans");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void goToHomePage(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("customer_home.fxml"));
        root = loader.load();
        scene = new Scene(root);
        stage = (Stage)anchorPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void goToProfile(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("customer_profile.fxml"));
        root = loader.load();
        scene = new Scene(root);
        stage = (Stage)anchorPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void goToRequestService(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("request_service.fxml"));
        root = loader.load();
        scene = new Scene(root);
        stage = (Stage)anchorPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void goToServiceList(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("customer_service_list.fxml"));
        root = loader.load();
        scene = new Scene(root);
        stage = (Stage)anchorPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void chatWithAdmin(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("customer_worker_chat_cwta.fxml"));
        root = loader.load();
        scene = new Scene(root);
        stage = (Stage)anchorPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void chatWithWorker(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("customer_chat.fxml"));
        root = loader.load();
        scene = new Scene(root);
        stage = (Stage)anchorPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void goToVehicleParts(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("vehicle_parts_customer.fxml"));
        root = loader.load();
        scene = new Scene(root);
        stage = (Stage)anchorPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void goToInvoice(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("customer_invoices.fxml"));
        root = loader.load();
        scene = new Scene(root);
        stage = (Stage)anchorPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void goToCarWash(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("customer_car_wash.fxml"));
        root = loader.load();
        scene = new Scene(root);
        stage = (Stage)anchorPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void logOut(ActionEvent actionEvent) throws IOException {

        SessionManager.clearSession();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("login.fxml"));
        root = loader.load();
        scene = new Scene(root);
        stage = (Stage)anchorPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}