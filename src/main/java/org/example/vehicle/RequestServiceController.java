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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;

public class RequestServiceController implements Initializable {

    @FXML
    Stage stage;

    @FXML
    Scene scene;

    @FXML
    Parent root;

    @FXML
    AnchorPane anchorPane;

    @FXML
    Label ImagePath;

    @FXML
    TextField Title;

    @FXML
    TextArea Description;

    @FXML
    ChoiceBox<String> VehicleType;

    @FXML
    ImageView VehicleImage;

    String db_url = Constants.DATABASE_URL;
    String db_user = Constants.DATABASE_USERNAME;
    String db_pass = Constants.DATABASE_PASSWORD;

    final int currentUserId = SessionManager.getCurrentUserId();
    final String currentUserType = SessionManager.getCurrentUserType();

    String file_vehicle_type = "E:\\IntelliJ\\Vehicle\\src\\main\\resources\\files\\vehicle.txt";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

       loadVehiclesFromFile();

        VehicleType.setOnAction(event -> {
            String selected = VehicleType.getValue();
            if ("Add New...".equals(selected)) {
                addNewVehicle();
            }
        });
    }

    private void loadVehiclesFromFile() {
        VehicleType.getItems().clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(file_vehicle_type))) {
            String line;
            while ((line = reader.readLine()) != null) {
                VehicleType.getItems().add(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        VehicleType.getItems().add("Add New...");
    }

    private void addNewVehicle() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Vehicle");
        dialog.setHeaderText("Enter the new vehicle name:");
        dialog.setContentText("Vehicle:");

        dialog.showAndWait().ifPresent(vehicle -> {
            if (!vehicle.isEmpty() && !VehicleType.getItems().contains(vehicle)) {
                VehicleType.getItems().add(VehicleType.getItems().size() - 1, vehicle);
                saveVehicleToFile(vehicle);
            }
        });
    }

    private void saveVehicleToFile(String vehicle) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file_vehicle_type, true))) {
            writer.write(vehicle);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void uploadVehicleImage() {
        FileChooser fileChooser = new FileChooser();
        File chosenFile = fileChooser.showOpenDialog(null);

        if (chosenFile != null) {
            String originalPath = chosenFile.getAbsolutePath();
            System.out.println("Selected Image: " + originalPath);

            try {
                String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss-SSS"));
                String fileName = currentUserType + "_" + currentUserId + "_" + currentDateTime + ".jpg";
                String destinationPath = "E:\\IntelliJ\\Vehicle\\vehicle_images\\" + fileName;


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
                ImagePath.setText(dbImagePath);

                Image vehicleImage = new Image(dbImagePath, false);
                VehicleImage.setImage(vehicleImage);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void submitRequest(ActionEvent actionEvent) throws SQLException {
        Connection connection = null;
        PreparedStatement psInsertValue = null;

        String title = Title.getText();
        String description = Description.getText();
        String vehicleType = VehicleType.getValue();
        String imagePath = ImagePath.getText();

        try {
            connection = DriverManager.getConnection(db_url, db_user, db_pass);

            psInsertValue = connection.prepareStatement("INSERT INTO service (cid, title, description, vehicle_type, status, picture) VALUES(?, ?, ?, ?, ?, ?)");
            psInsertValue.setInt(1, currentUserId);
            psInsertValue.setString(2, title);
            psInsertValue.setString(3, description);
            psInsertValue.setString(4, vehicleType);
            psInsertValue.setString(5, "Pending");
            psInsertValue.setString(6, imagePath);
            psInsertValue.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Request Submission Successful!");
            alert.setContentText("Your request for the servicing has been submitted successfully!");
            alert.show();

            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("customer_home.fxml")));
            stage = (Stage) ((Node)(actionEvent.getSource())).getScene().getWindow();
            scene = new Scene(root);
            stage.setTitle("Customer HomePage");
            stage.setScene(scene);
            stage.show();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (psInsertValue != null) psInsertValue.close();
            if (connection != null) connection.close();
        }
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
