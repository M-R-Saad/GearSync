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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Objects;
import java.util.ResourceBundle;

public class AdminCWStatsController implements Initializable {

    @FXML
    Stage stage;

    @FXML
    Scene scene;

    @FXML
    Parent root;

    @FXML
    AnchorPane anchorPane;

    @FXML
    AnchorPane customerPane;

    @FXML
    AnchorPane workerPane;

    @FXML
    TableView<Customer> customerTable;

    @FXML
    TableColumn<Customer, Integer> cidColumn;

    @FXML
    TableColumn<Customer, String> cUsernameColumn;

    @FXML
    TableColumn<Customer, String> cFullNameColumn;

    @FXML
    TableColumn<Customer, String> cEmailColumn;

    @FXML
    TableColumn<Customer, String> cPhoneColumn;

    @FXML
    TableColumn<Customer, String> cAddressColumn;

    @FXML
    TableView<Worker> workerTable;

    @FXML
    TableColumn<Worker, Integer> widColumn;

    @FXML
    TableColumn<Worker, String> wUsernameColumn;

    @FXML
    TableColumn<Worker, String> wFullNameColumn;

    @FXML
    TableColumn<Worker, String> wEmailColumn;

    @FXML
    TableColumn<Worker, String> wPhoneColumn;

    @FXML
    TableColumn<Worker, String> wStatusColumn;

    @FXML
    PieChart cPieChart;

    @FXML
    PieChart wPieChart;

    @FXML
    LineChart<String, Integer> cLineChart;

    @FXML
    LineChart<String, Integer> wLineChart;

    @FXML
    TextField cTopBuyer1, cTopBuyer2, cTopBuyer3;

    @FXML
    TextField cTopService1, cTopService2, cTopService3;

    @FXML
    TextField wTopWorker1, wTopWorker2, wTopWorker3;

    String db_url = Constants.DATABASE_URL;
    String db_user = Constants.DATABASE_USERNAME;
    String db_pass = Constants.DATABASE_PASSWORD;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerPane.setVisible(true);
        workerPane.setVisible(false);

        loadCustomerData();
        loadCustomerLineChart();
        loadCustomerPieChart();
        loadTopBuyers();
        loadTopServiceRequesters();

        loadWorkerData();
        loadWorkerLineChart();
        loadWorkerPieChart();
        loadTopWorkers();
    }

    public void showCustomerPane(ActionEvent actionEvent) {
        customerPane.setVisible(true);
        workerPane.setVisible(false);
    }

    public void showWorkerPane(ActionEvent actionEvent) {
        customerPane.setVisible(false);
        workerPane.setVisible(true);
    }


    public void loadCustomerData() {
        ObservableList<Customer> customerList = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(db_url, db_user, db_pass)) {
            String query = "SELECT * FROM customer";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                customerList.add(new Customer(
                        resultSet.getInt("cid"),
                        resultSet.getString("username"),
                        resultSet.getString("fullName"),
                        resultSet.getString("email"),
                        resultSet.getString("address"),
                        resultSet.getString("phone"),
                        resultSet.getString("picture"),
                        resultSet.getString("created_at")
                ));
            }

            cidColumn.setCellValueFactory(new PropertyValueFactory<>("cid"));
            cUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
            cFullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
            cEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            cPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
            cAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

            customerTable.setItems(customerList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void loadCustomerLineChart() {
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("Customer Registrations");

        try (Connection connection = DriverManager.getConnection(db_url, db_user, db_pass)) {
            String query = "SELECT DATE_FORMAT(created_at, '%Y-%m-%d') AS reg_date, COUNT(*) AS count FROM customer GROUP BY reg_date";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                series.getData().add(new XYChart.Data<>(resultSet.getString("reg_date"), resultSet.getInt("count")));
            }

            cLineChart.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void loadCustomerPieChart() {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(db_url, db_user, db_pass)) {
            String query = "SELECT " +
                    "SUM(CASE WHEN NOT EXISTS (SELECT 1 FROM invoice WHERE invoice.cid = customer.cid) " +
                    "AND NOT EXISTS (SELECT 1 FROM service WHERE service.cid = customer.cid) THEN 1 ELSE 0 END) AS no_activity, " +
                    "SUM(CASE WHEN EXISTS (SELECT 1 FROM invoice WHERE invoice.cid = customer.cid) " +
                    "AND NOT EXISTS (SELECT 1 FROM service WHERE service.cid = customer.cid) THEN 1 ELSE 0 END) AS only_bought, " +
                    "SUM(CASE WHEN NOT EXISTS (SELECT 1 FROM invoice WHERE invoice.cid = customer.cid) " +
                    "AND EXISTS (SELECT 1 FROM service WHERE service.cid = customer.cid) THEN 1 ELSE 0 END) AS only_service, " +
                    "SUM(CASE WHEN EXISTS (SELECT 1 FROM invoice WHERE invoice.cid = customer.cid) " +
                    "AND EXISTS (SELECT 1 FROM service WHERE service.cid = customer.cid) THEN 1 ELSE 0 END) AS both_activity " +
                    "FROM customer";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                pieData.add(new PieChart.Data("No Activity", resultSet.getInt("no_activity")));
                pieData.add(new PieChart.Data("Only Bought", resultSet.getInt("only_bought")));
                pieData.add(new PieChart.Data("Only Service", resultSet.getInt("only_service")));
                pieData.add(new PieChart.Data("Both Activities", resultSet.getInt("both_activity")));
            }

            cPieChart.setData(pieData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void loadTopBuyers() {
        try (Connection connection = DriverManager.getConnection(db_url, db_user, db_pass)) {
            // Step 1: Get top 3 customer IDs based on invoice count
            String query = "SELECT cid, COUNT(iid) AS invoice_count " +
                    "FROM invoice " +
                    "WHERE MONTH(STR_TO_DATE(created_at, '%Y-%m-%d %H:%i:%s')) = MONTH(CURRENT_DATE()) " +
                    "GROUP BY cid " +
                    "ORDER BY invoice_count DESC " +
                    "LIMIT 3";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            int[] topBuyerIds = new int[3];
            int index = 0;

            while (resultSet.next() && index < topBuyerIds.length) {
                topBuyerIds[index] = resultSet.getInt("cid");
                index++;
            }

            // Step 2: Fetch usernames for the top buyer IDs
            TextField[] topBuyers = {cTopBuyer1, cTopBuyer2, cTopBuyer3};
            for (int i = 0; i < topBuyerIds.length; i++) {
                if (topBuyerIds[i] != 0) {
                    String usernameQuery = "SELECT username FROM customer WHERE cid = ?";
                    PreparedStatement usernameStmt = connection.prepareStatement(usernameQuery);
                    usernameStmt.setInt(1, topBuyerIds[i]);
                    ResultSet usernameResult = usernameStmt.executeQuery();

                    if (usernameResult.next()) {
                        System.out.println("Top Buyer " + (i + 1) + ": " + usernameResult.getString("username"));
                        topBuyers[i].setText(usernameResult.getString("username"));
                    } else {
                        topBuyers[i].setText("");
                        System.out.println("Top Buyer " + (i + 1) + ": Not found");
                    }
                } else {
                    topBuyers[i].setText("");
                    System.out.println("2ndTop Buyer " + (i + 1) + ": No data");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void loadTopServiceRequesters() {
        try (Connection connection = DriverManager.getConnection(db_url, db_user, db_pass)) {
            String query = "SELECT customer.username, COUNT(service.sid) AS service_count " +
                    "FROM customer " +
                    "JOIN service ON customer.cid = service.cid " +
                    "WHERE MONTH(service.created_at) = MONTH(CURRENT_DATE()) " +
                    "GROUP BY customer.username " +
                    "ORDER BY service_count DESC " +
                    "LIMIT 3";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            TextField[] topServiceRequesters = {cTopService1, cTopService2, cTopService3};
            int index = 0;

            while (resultSet.next() && index < topServiceRequesters.length) {
                topServiceRequesters[index].setText(resultSet.getString("username"));
                index++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadWorkerData() {
        ObservableList<Worker> workerList = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(db_url, db_user, db_pass)) {
            String query = "SELECT * FROM worker";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                workerList.add(new Worker(
                        resultSet.getInt("wid"),
                        resultSet.getString("username"),
                        resultSet.getString("fullname"),
                        resultSet.getString("email"),
                        resultSet.getString("status"),
                        resultSet.getString("phone"),
                        resultSet.getString("picture"),
                        resultSet.getString("created_at")
                ));
            }

            widColumn.setCellValueFactory(new PropertyValueFactory<>("wid"));
            wUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
            wFullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullname"));
            wEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            wPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
            wStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

            workerTable.setItems(workerList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void loadWorkerLineChart() {
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("Worker Registrations");

        try (Connection connection = DriverManager.getConnection(db_url, db_user, db_pass)) {
            String query = "SELECT DATE_FORMAT(created_at, '%Y-%m-%d') AS reg_date, COUNT(*) AS count FROM worker GROUP BY reg_date";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                series.getData().add(new XYChart.Data<>(resultSet.getString("reg_date"), resultSet.getInt("count")));
            }

            wLineChart.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void loadWorkerPieChart() {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(db_url, db_user, db_pass)) {
            String query = "SELECT " +
                    "SUM(CASE WHEN status = 'Verified' THEN 1 ELSE 0 END) AS verified, " +
                    "SUM(CASE WHEN status = 'Suspended' THEN 1 ELSE 0 END) AS suspended, " +
                    "SUM(CASE WHEN status = 'Pending' THEN 1 ELSE 0 END) AS pending " +
                    "FROM worker";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                pieData.add(new PieChart.Data("Verified", resultSet.getInt("verified")));
                pieData.add(new PieChart.Data("Suspended", resultSet.getInt("suspended")));
                pieData.add(new PieChart.Data("Pending", resultSet.getInt("pending")));
            }

            wPieChart.setData(pieData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadTopWorkers() {
        try (Connection connection = DriverManager.getConnection(db_url, db_user, db_pass)) {
            String query = "SELECT worker.username, COUNT(service.sid) AS work_count " +
                    "FROM worker " +
                    "JOIN service ON worker.wid = service.wid " +
                    "WHERE MONTH(service.created_at) = MONTH(CURRENT_DATE()) " +
                    "GROUP BY worker.username " +
                    "ORDER BY work_count DESC " +
                    "LIMIT 3";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            TextField[] topWorkers = {wTopWorker1, wTopWorker2, wTopWorker3};
            int index = 0;

            while (resultSet.next() && index < topWorkers.length) {
                topWorkers[index].setText(resultSet.getString("username"));
                index++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void verifyWorker() {
        Worker selectedWorker = workerTable.getSelectionModel().getSelectedItem();
        if (selectedWorker != null && "Pending".equals(selectedWorker.getStatus())) {
            try (Connection connection = DriverManager.getConnection(db_url, db_user, db_pass)) {
                String query = "UPDATE worker SET status = 'Verified' WHERE wid = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, selectedWorker.getWid());
                int rowsUpdated = statement.executeUpdate();

                if (rowsUpdated > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Worker verified successfully!");
                    loadWorkerData();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a pending worker.");
        }
    }

    public void suspendWorker() {
        Worker selectedWorker = workerTable.getSelectionModel().getSelectedItem();
        if (selectedWorker != null && "Verified".equals(selectedWorker.getStatus())) {
            try (Connection connection = DriverManager.getConnection(db_url, db_user, db_pass)) {
                String query = "UPDATE worker SET status = 'Suspended' WHERE wid = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, selectedWorker.getWid());
                int rowsUpdated = statement.executeUpdate();

                if (rowsUpdated > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Worker suspended successfully!");
                    loadWorkerData();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a worker to suspend.");
        }
    }

    public void activateWorker() {
        Worker selectedWorker = workerTable.getSelectionModel().getSelectedItem();
        if (selectedWorker != null && "Suspended".equals(selectedWorker.getStatus())) {
            try (Connection connection = DriverManager.getConnection(db_url, db_user, db_pass)) {
                String query = "UPDATE worker SET status = 'Verified' WHERE wid = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, selectedWorker.getWid());
                int rowsUpdated = statement.executeUpdate();

                if (rowsUpdated > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Worker activated successfully!");
                    loadWorkerData(); // Refresh the worker table
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a suspended worker.");
        }
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
