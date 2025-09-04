package org.example.vehicle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class AvailableServiceController implements Initializable {

    @FXML
    Stage stage;

    @FXML
    Scene scene;

    @FXML
    Parent root;

    @FXML
    AnchorPane anchorPane;

    @FXML
    ScrollPane scrollPane, scrollPane2;

    @FXML
    FlowPane flowPane_services, flowPane_carWash;

    @FXML
    TableView<Service> serviceTable;

    @FXML
    TableColumn<Service, Integer> sidColumn;

    @FXML
    TableColumn<Service, Integer> cidColumn;

    @FXML
    TableColumn<Service, String> titleColumn;

    @FXML
    TableColumn<Service, String> statusColumn;

    @FXML
    TableColumn<Service, String> createdAtColumn;

    @FXML
    TableView<CarWash> carWashTable;

    @FXML
    TableColumn<CarWash, Integer> ridColumn;

    @FXML
    TableColumn<CarWash, Integer> cwidColumn;

    @FXML
    TableColumn<CarWash, String> locationColumn;

    @FXML
    TableColumn<CarWash, String> carModelColumn;

    @FXML
    TableColumn<CarWash, String> dateTimeColumn;

    @FXML
    TableColumn<CarWash, String> createdAtColumn2;

    private static final int currentUserId = SessionManager.getCurrentUserId();
    private final String currentUserType = SessionManager.getCurrentUserType();

    String db_url = Constants.DATABASE_URL;
    String db_user = Constants.DATABASE_USERNAME;
    String db_pass = Constants.DATABASE_PASSWORD;

    Service selectedService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadAvailableServices();
        loadTakenServices();

        loadAvailableCarWashJob();
        loadTakenCarWashJob();
    }

    public void loadAvailableServices() {
        try {
            Connection connection = DriverManager.getConnection(db_url, db_user, db_pass);
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM service WHERE status = 'pending'");

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {

                int sid = resultSet.getInt("sid");
                int cid = resultSet.getInt("cid");
                int wid = resultSet.getInt("wid");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String vehicleType = resultSet.getString("vehicle_type");
                double payment_amount = resultSet.getDouble("payment_amount");
                String status = resultSet.getString("status");
                String picture = resultSet.getString("picture");
                String created_at = resultSet.getString("created_at");
                String updated_at = resultSet.getString("updated_at");

                Service service = new Service(sid, cid, wid, title, description, vehicleType, payment_amount, status, picture, created_at, updated_at);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("service_card.fxml"));
                AnchorPane serviceCard = loader.load();

                ServiceCardController controller = loader.getController();
                controller.setData(service, this, currentUserId);

                flowPane_services.getChildren().add(serviceCard);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadTakenServices() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        ArrayList<Service> services = null;

        try {
            connection = DriverManager.getConnection(db_url, db_user, db_pass);

            preparedStatement = connection.prepareStatement("SELECT * FROM service WHERE wid = ?");
            preparedStatement.setInt(1, currentUserId);  // Use worker's ID
            resultSet = preparedStatement.executeQuery();

            services = new ArrayList<>();

            while (resultSet.next()) {
                int sid = resultSet.getInt("sid");
                int cid = resultSet.getInt("cid");
                int wid = resultSet.getInt("wid");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String vehicleType = resultSet.getString("vehicle_type");
                double payment_amount = resultSet.getDouble("payment_amount");
                String status = resultSet.getString("status");
                String picture = resultSet.getString("picture");
                String created_at = resultSet.getString("created_at");
                String updated_at = resultSet.getString("updated_at");

                Service service = new Service(sid, cid, wid, title, description, vehicleType, payment_amount, status, picture, created_at, updated_at);
                services.add(service);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if (services != null) {
            ObservableList<Service> serviceList = FXCollections.observableArrayList(services);

            sidColumn.setCellValueFactory(new PropertyValueFactory<>("sid"));
            cidColumn.setCellValueFactory(new PropertyValueFactory<>("cid"));
            titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("created_at"));

            serviceTable.setItems(serviceList);

            serviceTable.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1) {
                    selectedService = serviceTable.getSelectionModel().getSelectedItem();
                    if (selectedService != null) {
                        System.out.println("Selected service ID: " + selectedService.getSid());
                    }
                }
            });
        }
    }


    public void removeServiceCard(AnchorPane serviceCard) {
        flowPane_services.getChildren().remove(serviceCard);
    }


    public void loadAvailableCarWashJob() {
        flowPane_carWash.getChildren().clear();
        String query = "SELECT r.*, c.plan_name, c.price, c.created_at AS plan_created_at " +
                "FROM receipt r JOIN car_wash c ON r.cwid = c.cwid WHERE r.wid IS NULL";

        try (Connection connection = DriverManager.getConnection(db_url, db_user, db_pass);
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int rid = rs.getInt("rid");
                int cid = rs.getInt("cid");
                int wid = rs.getInt("wid");
                int cwid = rs.getInt("cwid");
                String planName = rs.getString("plan_name");
                String location = rs.getString("car_location");
                String carModel = rs.getString("car_model");
                String time = rs.getString("time");
                String date = rs.getString("date");
                String picture = rs.getString("picture");
                String createdAt = rs.getString("plan_created_at");
                double price = rs.getDouble("price");

                String dateTime = date + " " + time;

                CarWash carWash = new CarWash(rid, cid, wid, cwid, planName, location, carModel, dateTime, picture, createdAt);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("available_car_wash_card.fxml"));
                Parent card = loader.load();
                CarWashCardController2 controller = loader.getController();
                controller.setData(carWash);
                flowPane_carWash.getChildren().add(card);
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void loadTakenCarWashJob() {
        ObservableList<CarWash> carWashList = FXCollections.observableArrayList();
        String query = "SELECT r.*, c.plan_name, c.price, c.created_at AS plan_created_at " +
                "FROM receipt r JOIN car_wash c ON r.cwid = c.cwid WHERE r.wid = ?";

        try (Connection connection = DriverManager.getConnection(db_url, db_user, db_pass);
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CarWash carWash = new CarWash(
                        rs.getInt("rid"),
                        rs.getInt("cid"),
                        rs.getInt("wid"),
                        rs.getInt("cwid"),
                        rs.getString("plan_name"),
                        rs.getString("car_location"),
                        rs.getString("car_model"),
                        rs.getString("date") + " " + rs.getString("time"),
                        rs.getString("picture"),
                        rs.getString("plan_created_at")
                );
                carWashList.add(carWash);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        ridColumn.setCellValueFactory(new PropertyValueFactory<>("rid"));
        cwidColumn.setCellValueFactory(new PropertyValueFactory<>("cwid"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("carLocation"));
        carModelColumn.setCellValueFactory(new PropertyValueFactory<>("carModel"));
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        createdAtColumn2.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        carWashTable.setItems(carWashList);
    }


    public static void takeCarWashJob(int rid) {
        String update = "UPDATE receipt SET wid = ?, created_at = NOW() WHERE rid = ?";
        try (Connection conn = DriverManager.getConnection(Constants.DATABASE_URL, Constants.DATABASE_USERNAME, Constants.DATABASE_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(update)) {
            ps.setInt(1, currentUserId); // assumed static or passed in
            ps.setInt(2, rid);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Success!");
        alert.setHeaderText(null);
        alert.setContentText("Car wash job has been taken successfully.");
        alert.showAndWait();
    }


    @FXML
    private void handleServiceComplete(ActionEvent event) {
        if (selectedService == null) {
            System.out.println("No service selected.");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning!");
            alert.setHeaderText(null);
            alert.setContentText("Please select a service to mark as complete.");
            alert.showAndWait();
            return;
        }

        if (!"In Progress".equalsIgnoreCase(selectedService.getStatus())) {
            System.out.println("Only 'In Progress' services can be marked as complete.");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning!");
            alert.setHeaderText(null);
            alert.setContentText("Only 'In Progress' services can be marked as complete.");
            alert.showAndWait();
            return;
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DriverManager.getConnection(db_url, db_user, db_pass);
            String query = "UPDATE service SET status = ?, updated_at = ? WHERE sid = ?";
            preparedStatement = connection.prepareStatement(query);

            String updatedAt = java.time.LocalDateTime.now().toString();

            preparedStatement.setString(1, "Completed");
            preparedStatement.setString(2, updatedAt);
            preparedStatement.setInt(3, selectedService.getSid());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Service marked as completed.");
                selectedService.setStatus("Completed");
                selectedService.setUpdated_at(updatedAt);
                serviceTable.refresh(); // visually update table

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Success!");
                alert.setHeaderText(null);
                alert.setContentText("Service has been marked as completed successfully.");
                alert.showAndWait();
            }
            else {
                System.out.println("Failed to update service status.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText(null);
                alert.setContentText("Failed to completed the service. Please try again.");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @FXML
    public void goToHomePage(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("worker_home.fxml"));
        root = loader.load();
        scene = new Scene(root);
        stage = (Stage)anchorPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void goToProfile(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("worker_profile.fxml"));
        root = loader.load();
        scene = new Scene(root);
        Stage stage = (Stage)anchorPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void chatWithAdmin(ActionEvent actionEvent) throws IOException {

//        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("customer_worker_chat_cwta.fxml")));
//        stage = (Stage) ((Node)(actionEvent.getSource())).getScene().getWindow();
//        scene = new Scene(root);
//        stage.setTitle("Worker Message");
//        stage.setScene(scene);
//        stage.show();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("customer_worker_chat_cwta.fxml"));
        root = loader.load();
        scene = new Scene(root);
        stage = (Stage)anchorPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void chatWithCustomer(ActionEvent actionEvent) throws IOException {

//        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("worker_chat.fxml")));
//        stage = (Stage) ((Node)(actionEvent.getSource())).getScene().getWindow();
//        scene = new Scene(root);
//        stage.setTitle("Worker Message");
//        stage.setScene(scene);
//        stage.show();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("worker_chat.fxml"));
        root = loader.load();
        scene = new Scene(root);
        stage = (Stage)anchorPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void availableServiceList(ActionEvent actionEvent) throws IOException {

//        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("available_service.fxml")));
//        stage = (Stage) ((Node)(actionEvent.getSource())).getScene().getWindow();
//        scene = new Scene(root);
//        stage.setTitle("Available Service List");
//        stage.setScene(scene);
//        stage.show();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("available_service.fxml"));
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
