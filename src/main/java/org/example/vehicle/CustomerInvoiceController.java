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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Objects;
import java.util.ResourceBundle;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Document;

public class CustomerInvoiceController implements Initializable {

    @FXML
    Stage stage;

    @FXML
    Scene scene;

    @FXML
    Parent root;

    @FXML
    AnchorPane anchorPane;

    @FXML
    Label planNameLabel;

    @FXML
    TextArea descriptionArea;

    @FXML
    TextArea servicesArea;

    @FXML
    Label vehicleTypeLabel;

    @FXML
    Label priceLabel;

    @FXML
    Label carModelLabel;

    @FXML
    Label carLocationLabel;

    @FXML
    Label dateTimeLabel;

    @FXML
    Label customerNameLabel;

    @FXML
    Label customerMailLabel;

    @FXML
    Label csutomerPhoneLabel;

    @FXML
    ImageView carImage;

    @FXML
    ImageView planImage;

    @FXML
    TableView<CarWash> carWashTable;

    @FXML
    TableColumn<CarWash, Integer> ridColumn;

    @FXML
    TableColumn<CarWash, Integer> cidColumn;

    @FXML
    TableColumn<CarWash, Integer> widColumn;

    @FXML
    TableColumn<CarWash, Integer> cwidColumn;

    @FXML
    TableColumn<CarWash, String> locationColumn;

    @FXML
    TableColumn<CarWash, String> carModelColumn;

    @FXML
    TableColumn<CarWash, String> dateTimeColumn;

    @FXML
    TableColumn<CarWash, String> createdAtColumn;

    private static final int currentUserId = SessionManager.getCurrentUserId();
    private final String currentUserType = SessionManager.getCurrentUserType();

    String db_url = Constants.DATABASE_URL;
    String db_user = Constants.DATABASE_USERNAME;
    String db_pass = Constants.DATABASE_PASSWORD;

    ObservableList<CarWash> receiptList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadCarWashReceipts();

        carWashTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadReceiptDetails(newSelection);
            }
        });
    }

    public void loadCarWashReceipts() {
        receiptList.clear();
        try (Connection conn = DriverManager.getConnection(db_url, db_user, db_pass)) {
            String query = "SELECT * FROM receipt WHERE cid = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CarWash r = new CarWash(
                        rs.getInt("rid"),
                        rs.getInt("cid"),
                        rs.getInt("wid"),
                        rs.getInt("cwid"),
                        rs.getString("car_location"),
                        rs.getString("car_model"),
                        rs.getString("date") + " " + rs.getString("time"),
                        rs.getString("picture"),
                        rs.getString("created_at")
                );
                receiptList.add(r);
            }

            ridColumn.setCellValueFactory(new PropertyValueFactory<>("rid"));
            cidColumn.setCellValueFactory(new PropertyValueFactory<>("cid"));
            widColumn.setCellValueFactory(new PropertyValueFactory<>("wid"));
            cwidColumn.setCellValueFactory(new PropertyValueFactory<>("cwid"));
            locationColumn.setCellValueFactory(new PropertyValueFactory<>("carLocation"));
            carModelColumn.setCellValueFactory(new PropertyValueFactory<>("carModel"));
            dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
            createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

            carWashTable.setItems(receiptList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadReceiptDetails(CarWash receipt) {
        try (Connection conn = DriverManager.getConnection(db_url, db_user, db_pass)) {
            String planQuery = "SELECT * FROM car_wash WHERE cwid = ?";
            PreparedStatement stmt = conn.prepareStatement(planQuery);
            stmt.setInt(1, receipt.getCwid());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                planNameLabel.setText(rs.getString("plan_name"));
                descriptionArea.setText(rs.getString("description"));
                servicesArea.setText(rs.getString("services"));
                vehicleTypeLabel.setText(rs.getString("vehicle_type"));
                priceLabel.setText(String.valueOf(rs.getDouble("price")));
                planImage.setImage(new Image(rs.getString("picture")));
            }

            String customerQuery = "SELECT fullname, email, phone FROM customer WHERE cid = ?";
            PreparedStatement stmt2 = conn.prepareStatement(customerQuery);
            stmt2.setInt(1, currentUserId);
            ResultSet rs2 = stmt2.executeQuery();
            if (rs2.next()) {
                customerNameLabel.setText(rs2.getString("fullname"));
                customerMailLabel.setText(rs2.getString("email"));
                csutomerPhoneLabel.setText(rs2.getString("phone"));
            }

            carModelLabel.setText(receipt.getCarModel());
            carLocationLabel.setText(receipt.getCarLocation());
            dateTimeLabel.setText(receipt.getDateTime());

            carImage.setImage(new Image(receipt.getPicture()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadPDF() {
        CarWash selectedReceipt = carWashTable.getSelectionModel().getSelectedItem();

        if (selectedReceipt != null) {
            String pdfPath = "E:\\IntelliJ\\Vehicle\\pdfs\\CarWashReceipt_" + selectedReceipt.getRid() + ".pdf";

            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
                document.open();

                // Add title
                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
                Paragraph title = new Paragraph("Car Wash Receipt", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);

                document.add(new Paragraph("\n"));

                // Add receipt details
                document.add(new Paragraph("Receipt ID: " + selectedReceipt.getRid()));

                document.add(new Paragraph("\n")); // Add spacing before the header
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
                Paragraph header = new Paragraph("Selected Plan Info:", headerFont);
                header.setAlignment(Element.ALIGN_LEFT);
                document.add(header);
                document.add(new Paragraph("\n"));

                document.add(new Paragraph("Plan Name: " + planNameLabel.getText()));
                document.add(new Paragraph("Description: " + descriptionArea.getText()));
                document.add(new Paragraph("Services: " + servicesArea.getText()));
                document.add(new Paragraph("Vehicle Type: " + vehicleTypeLabel.getText()));
                document.add(new Paragraph("Price: ৳" + priceLabel.getText()));

                // Add plan image
                if (planImage.getImage() != null) {
                    String planImagePath = planImage.getImage().getUrl().replace("file:///", "");
                    com.itextpdf.text.Image planImg = com.itextpdf.text.Image.getInstance(planImagePath);
                    planImg.scaleToFit(300, 300);
                    planImg.setAlignment(Element.ALIGN_CENTER);
                    document.add(planImg);
                }

                document.add(new Paragraph("\n"));

                document.add(new Paragraph("\n")); // Add spacing before the header
                Font headerFont1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
                Paragraph header1 = new Paragraph("Customer & Car Info:", headerFont1);
                header1.setAlignment(Element.ALIGN_LEFT);
                document.add(header1);
                document.add(new Paragraph("\n"));

                // Add customer details
                document.add(new Paragraph("Customer Name: " + customerNameLabel.getText()));
                document.add(new Paragraph("Email: " + customerMailLabel.getText()));
                document.add(new Paragraph("Phone: " + csutomerPhoneLabel.getText()));

                document.add(new Paragraph("\n"));

                // Add car details
                document.add(new Paragraph("Car Model: " + carModelLabel.getText()));
                document.add(new Paragraph("Car Location: " + carLocationLabel.getText()));
                document.add(new Paragraph("Service Date & Time: " + dateTimeLabel.getText()));

                document.add(new Paragraph("\n"));

                // Add car image
                if (carImage.getImage() != null) {
                    String carImagePath = carImage.getImage().getUrl().replace("file:///", "");
                    com.itextpdf.text.Image carImg = com.itextpdf.text.Image.getInstance(carImagePath);
                    carImg.scaleToFit(300, 300);
                    carImg.setAlignment(Element.ALIGN_CENTER);
                    document.add(carImg);
                }

                document.close();

                showAlert(Alert.AlertType.CONFIRMATION, "Success", "PDF generated successfully at: " + pdfPath);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate PDF: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a receipt to generate the PDF.");
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
