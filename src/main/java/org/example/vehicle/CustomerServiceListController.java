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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Document;


public class CustomerServiceListController implements Initializable {

    @FXML
    Stage stage;

    @FXML
    Scene scene;

    @FXML
    Parent root;

    @FXML
    AnchorPane anchorPane;

    @FXML
    TextField Title, VehicleType, WorkerName, Phone, Status, PaymentAmount, RequestedAt, UpdatedAt;

    @FXML
    TextArea Description;

    @FXML
    ImageView VehicleImage, ProgressImage;

    @FXML
    TableView<Service> serviceTable;

    @FXML
    TableColumn<Service, Integer> sidColumn;

    @FXML
    TableColumn<Service, Integer> widColumn;

    @FXML
    TableColumn<Service, String> titleColumn;

    @FXML
    TableColumn<Service, String> statusColumn;

    @FXML
    TableColumn<Service, String> createdAtColumn;

    private final int currentUserId = SessionManager.getCurrentUserId();
    private final String currentUserType = SessionManager.getCurrentUserType();

    String db_url = Constants.DATABASE_URL;
    String db_user = Constants.DATABASE_USERNAME;
    String db_pass = Constants.DATABASE_PASSWORD;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        ArrayList<Service> services = null;

        try {
            connection = DriverManager.getConnection(db_url, db_user, db_pass);

            preparedStatement = connection.prepareStatement("SELECT * FROM service WHERE cid = ?");
            preparedStatement.setInt(1, currentUserId);
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
            for (Service service : services) {
                System.out.println(service.toString());
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

        assert services != null;

        ObservableList<Service> serviceList = FXCollections.observableArrayList(services);

        sidColumn.setCellValueFactory(new PropertyValueFactory<>("sid"));
        widColumn.setCellValueFactory(new PropertyValueFactory<>("wid"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("created_at"));

        serviceTable.setItems(serviceList);

        serviceTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Service service = serviceTable.getSelectionModel().getSelectedItem();
                int sid = service.getSid();
                System.out.println(sid);
                try {
                    loadAppointmentDetails(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void loadAppointmentDetails(MouseEvent event) throws IOException {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Service selectedService = serviceTable.getSelectionModel().getSelectedItem();

        if (serviceTable != null) {
            int sid = selectedService.getSid();

            try {
                connection = DriverManager.getConnection(db_url, db_user, db_pass);

                preparedStatement = connection.prepareStatement("SELECT * FROM service WHERE sid = ?");
                preparedStatement.setInt(1, sid);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {

                    int wid = resultSet.getInt("wid");
                    if (wid == 0){
                        WorkerName.setText("No worker selected for your work yet!");
                        Phone.setText("No Contact number!");
                        UpdatedAt.setText("--:--:--");
                        PaymentAmount.setText("0.0");
                    }
                    else {
                        loadWorkerDetails(wid);
                        UpdatedAt.setText(resultSet.getString("updated_at"));
                        PaymentAmount.setText(resultSet.getString("payment_amount"));
                    }

                    Status.setText(resultSet.getString("status"));
                    if (Status.getText().equals("Pending")) {
                        Image progressImage = new Image("file:///E:/IntelliJ/Vehicle/images/pending_nobg.png", false);
                        ProgressImage.setImage(progressImage);
                    }
                    else if (Status.getText().equals("In Progress")) {
                        Image progressImage = new Image("file:///E:/IntelliJ/Vehicle/images/wip_nobg.png", false);
                        ProgressImage.setImage(progressImage);
                    }
                    else if (Status.getText().equals("Completed")) {
                        Image progressImage = new Image("file:///E:/IntelliJ/Vehicle/images/completed_nobg.png", false);
                        ProgressImage.setImage(progressImage);
                    }

                    Title.setText(resultSet.getString("title"));
                    Description.setText(resultSet.getString("description"));
                    VehicleType.setText(resultSet.getString("vehicle_type"));
                    RequestedAt.setText(resultSet.getString("created_at"));

                    Image vehicleImage = new Image(resultSet.getString("picture"), false);
                    VehicleImage.setImage(vehicleImage);
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
    }

    public void downloadPDF(ActionEvent actionEvent) {
        Service selectedService = serviceTable.getSelectionModel().getSelectedItem();

        if (selectedService != null) {
            String pdfPath = "E:\\IntelliJ\\Vehicle\\pdfs\\" + selectedService.getTitle() + ".pdf";

            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
                document.open();

                // Add title
                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
                Paragraph title = new Paragraph("Service Details", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);

                document.add(new Paragraph("\n"));

                // Add service attributes
                document.add(new Paragraph("Title: " + Title.getText()));
                document.add(new Paragraph("Description: " + Description.getText()));
                document.add(new Paragraph("Vehicle Type: " + VehicleType.getText()));
                document.add(new Paragraph("Worker Name: " + WorkerName.getText()));
                document.add(new Paragraph("Contact No: " + Phone.getText()));
                document.add(new Paragraph("Status: " + Status.getText()));
                document.add(new Paragraph("Payment Amount: " + PaymentAmount.getText()));
                document.add(new Paragraph("Requested At: " + RequestedAt.getText()));
                document.add(new Paragraph("Completed At: " + UpdatedAt.getText()));

                document.add(new Paragraph("\n"));

                //import com.itextpdf.text.Image;
                // Add picture
                if (VehicleImage.getImage() != null) {
                    Image vehicleImage = VehicleImage.getImage();
                    String imagePath = vehicleImage.getUrl().replace("file:///", "");
                    com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(imagePath);
                    img.scaleToFit(300, 300);
                    img.setAlignment(Element.ALIGN_CENTER);
                    document.add(img);
                }

                document.close();

                showAlert(Alert.AlertType.INFORMATION, "Success", "PDF generated successfully at: " + pdfPath);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate PDF: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a service to generate the PDF.");
        }
    }


    public void loadWorkerDetails(int wid) throws IOException, SQLException {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(db_url, db_user, db_pass);

            preparedStatement = connection.prepareStatement("SELECT fullname, phone FROM worker WHERE wid = ?");
            preparedStatement.setString(1, String.valueOf(wid));
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                WorkerName.setText(resultSet.getString("fullname"));
                Phone.setText(resultSet.getString("phone"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
        }
    }


    public void viewWorkerProfile(ActionEvent actionEvent) throws IOException, SQLException {
        Service selectedService = serviceTable.getSelectionModel().getSelectedItem();

        if (selectedService != null) {
            int wid = selectedService.getWid();

            if (wid != 0) {
                Connection connection = null;
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;

                try {
                    connection = DriverManager.getConnection(db_url, db_user, db_pass);

                    preparedStatement = connection.prepareStatement("SELECT fullname, phone, email, status, picture FROM worker WHERE wid = ?");
                    preparedStatement.setInt(1, wid);
                    resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        String fullName = resultSet.getString("fullname");
                        String phone = resultSet.getString("phone");
                        String email = resultSet.getString("email");
                        String status = resultSet.getString("status");
                        String picture = resultSet.getString("picture");

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("worker_info.fxml"));
                        Parent root = loader.load();

                        WorkerInfoController controller = loader.getController();
                        controller.setWorkerDetails(fullName, phone, email, status, picture);

                        Stage stage = new Stage();
                        controller.setStage(stage);

                        stage.setTitle("Worker Information");
                        stage.setScene(new Scene(root));
                        stage.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (resultSet != null) resultSet.close();
                    if (preparedStatement != null) preparedStatement.close();
                    if (connection != null) connection.close();
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "No worker is associated with this service.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a service.");
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
