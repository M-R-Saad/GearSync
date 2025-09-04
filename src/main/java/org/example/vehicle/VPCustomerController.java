package org.example.vehicle;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Objects;
import java.util.ResourceBundle;

public class VPCustomerController implements Initializable {

    @FXML
    Stage stage;

    @FXML
    Scene scene;

    @FXML
    Parent root;

    @FXML
    AnchorPane anchorPane;

    @FXML
    ScrollPane scrollPane;

    @FXML
    FlowPane flowPane_parts;

    @FXML
    TableView<Parts> partsTable;

    @FXML
    TableColumn<Parts, Integer> pidColumn;

    @FXML
    TableColumn<Parts, String> nameColumn;

    @FXML
    TableColumn<Parts, Integer> quantityColumn;

    @FXML
    TableColumn<Parts, Double> pricePerItemColumn;

    @FXML
    TableColumn<Parts, Double> netPriceColumn;

    @FXML
    Label TotalPriceLabel;

    String db_url = Constants.DATABASE_URL;
    String db_user = Constants.DATABASE_USERNAME;
    String db_pass = Constants.DATABASE_PASSWORD;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        pidColumn.setCellValueFactory(new PropertyValueFactory<>("pid"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        pricePerItemColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        netPriceColumn.setCellValueFactory(cellData -> {
            Parts part = cellData.getValue();
            return new ReadOnlyObjectWrapper<>(part.getPrice() * part.getQuantity());
        });

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(db_url, db_user, db_pass);

            preparedStatement = connection.prepareStatement("SELECT * FROM parts");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int pid = resultSet.getInt("pid");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                double price = resultSet.getDouble("price");
                int available_unit = resultSet.getInt("available_unit");
                String picture = resultSet.getString("picture");

                Parts parts = new Parts(pid, name, description, price, available_unit, picture);

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("parts_card.fxml"));

                Parent card = fxmlLoader.load();
                card.setUserData(fxmlLoader);

                PartsCardController cardController = fxmlLoader.getController();
                cardController.setData(parts, this);

                flowPane_parts.getChildren().add(card);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void addToCartFromCard(Parts part, int quantity) {
        Parts cartItem = new Parts(
                part.getPid(),
                part.getName(),
                part.getDescription(),
                part.getPrice(),
                quantity,
                part.getPicture()
        );

        partsTable.getItems().add(cartItem);
        System.out.println(cartItem.toString());

        double total = 0;
        for (Parts p : partsTable.getItems()) {
            total += p.getPrice() * p.getQuantity();
        }

        TotalPriceLabel.setText("Total price: " + String.format("%.2f", total));
    }


    public void removeFromCart(ActionEvent event) {
        Parts selectedPart = partsTable.getSelectionModel().getSelectedItem();

        if (selectedPart == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No part is selected!");
            alert.setContentText("Please select a part from the table to remove.");
            alert.showAndWait();
            return;
        }
        partsTable.getItems().remove(selectedPart);

        for (Node node : flowPane_parts.getChildren()) {
            FXMLLoader loader = (FXMLLoader) node.getUserData();
            PartsCardController controller = loader.getController();

            if (controller != null && controller.getParts().getPid() == selectedPart.getPid()) {
                int currentStock = controller.getParts().getQuantity();
                int returnedQty = selectedPart.getQuantity();

                controller.setAvailableStock(currentStock + returnedQty);
                break;
            }
        }
        updateTotalPrice();
    }


//    public void confirmOrder(ActionEvent actionEvent) throws IOException {
//        int customerId = SessionManager.getCurrentUserId();
//        double total = 0.0;
//
//        if (partsTable.getItems().isEmpty()) {
//            Alert alert = new Alert(Alert.AlertType.WARNING);
//            alert.setTitle("Warning");
//            alert.setHeaderText("Empty Order!");
//            alert.setContentText("Your order is empty. Please add parts before confirming.");
//            alert.showAndWait();
//            return;
//        }
//
//        for (Parts p : partsTable.getItems()) {
//            total += p.getNetPrice();
//        }
//
//        try{
//            Connection connection = DriverManager.getConnection(db_url, db_user, db_pass);
//
//            PreparedStatement invoiceStmt = connection.prepareStatement("INSERT INTO invoice (cid, total_price) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
//            invoiceStmt.setInt(1, customerId);
//            invoiceStmt.setDouble(2, total);
//            invoiceStmt.executeUpdate();
//
//            ResultSet keys = invoiceStmt.getGeneratedKeys();
//            int invoiceId = -1;
//            if (keys.next()) {
//                invoiceId = keys.getInt(1);
//            }
//
//            for (Parts p : partsTable.getItems()) {
//                PreparedStatement updatePartStmt = connection.prepareStatement("UPDATE parts SET available_unit = available_unit - ? WHERE pid = ?");
//                updatePartStmt.setInt(1, p.getQuantity());
//                updatePartStmt.setInt(2, p.getPid());
//                updatePartStmt.executeUpdate();
//
//                PreparedStatement ipStmt = connection.prepareStatement("INSERT INTO invoice_parts (iid, pid, quantity, price) VALUES (?, ?, ?, ?)");
//                ipStmt.setInt(1, invoiceId);
//                ipStmt.setInt(2, p.getPid());
//                ipStmt.setInt(3, p.getQuantity());
//                ipStmt.setDouble(4, p.getNetPrice());
//                ipStmt.executeUpdate();
//            }
//
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle("Confirmation");
//            alert.setHeaderText("Order Confirmed!");
//            alert.setContentText("Your order has been placed successfully!");
//            alert.showAndWait();
//
//            partsTable.getItems().clear();
//            updateTotalPrice();
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    public void confirmOrder(ActionEvent actionEvent) throws IOException {
        if (partsTable.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Empty Order!");
            alert.setContentText("Your order is empty. Please add parts before confirming.");
            alert.showAndWait();
            return;
        }

        // Calculate total price
        double total = partsTable.getItems().stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();

        // Load the order confirmation popup
        FXMLLoader loader = new FXMLLoader(getClass().getResource("order_confirmation.fxml"));
        Parent root = loader.load();

        // Pass data to the OrderConfirmationController
        OrderConfirmationController controller = loader.getController();
        controller.initData(partsTable.getItems(), total);

        // Show the popup
        Stage stage = new Stage();
        stage.setTitle("Order Confirmation");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void updateTotalPrice() {
        double total = 0;
        for (Parts p : partsTable.getItems()) {
            total += p.getPrice() * p.getQuantity();
        }
        TotalPriceLabel.setText("Total: " + total + " ৳");
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
