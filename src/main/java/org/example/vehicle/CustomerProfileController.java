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
import java.util.Objects;
import java.util.ResourceBundle;

public class CustomerProfileController implements Initializable {

    @FXML
    Stage stage;

    @FXML
    Scene scene;

    @FXML
    Parent root;

    @FXML
    AnchorPane anchorPane;

    @FXML
    TextField UserName, FullName, Address, Email, Phone;

    @FXML
    ImageView profileView;

    @FXML
    Image profilePicture;

    private final int currentUserId = SessionManager.getCurrentUserId();
    private final String currentUserType = SessionManager.getCurrentUserType();

    String db_url = Constants.DATABASE_URL;
    String db_user = Constants.DATABASE_USERNAME;
    String db_pass = Constants.DATABASE_PASSWORD;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println(currentUserId);
        System.out.println(currentUserType);

        UserName.setEditable(false);

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement psInsertValue = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(db_url, db_user, db_pass);

            preparedStatement = connection.prepareStatement("SELECT * FROM customer WHERE cid = ?");
            preparedStatement.setInt(1, currentUserId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String fullname = resultSet.getString("fullname");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String address = resultSet.getString("address");
                String image = resultSet.getString("picture");

                UserName.setText(username);
                FullName.setText(fullname);
                Email.setText(email);
                Phone.setText(phone);
                Address.setText(address);

                if(image == null){
                    String imagePath = "file:///E:/IntelliJ/Vehicle/src/main/resources/images/default_avatar.jpg";
                    profilePicture = new Image(imagePath);

                    psInsertValue = connection.prepareStatement("UPDATE customer SET picture = ? WHERE cid = ?");
                    psInsertValue.setString(1, imagePath);
                    psInsertValue.setString(2, String.valueOf(currentUserId));
                    psInsertValue.executeUpdate();
                }
                else {
                    profilePicture = new Image(image);
                }
                profileView.setImage(profilePicture);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (psInsertValue != null) psInsertValue.close();
                if (connection != null) connection.close();
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


//    public void updateProfilePicture() throws SQLException {
//
//        Connection connection = null;
//        PreparedStatement preparedStatement = null;
//        PreparedStatement psInsertValue = null;
//        ResultSet resultSet = null;
//
//        try {
//            connection = DriverManager.getConnection(db_url, db_user, db_pass);
//
//            FileChooser fileChooser = new FileChooser();
//            File chosenFile = fileChooser.showOpenDialog(null);
//            String imagePath = null;
//            String newImagePath = null;
//
//            if (chosenFile != null) {
//                imagePath = chosenFile.getAbsolutePath();
//                System.out.println(imagePath);
//
//                try {
//                    InputStream is = new FileInputStream(imagePath);
//                    OutputStream os = new FileOutputStream("E:\\IntelliJ\\Vehicle\\src\\main\\resources\\images\\" + currentUserType + currentUserId + ".jpg");
//                    byte[] buffer = new byte[1024];
//                    int length;
//                    while ((length = is.read(buffer)) > 0) {
//                        os.write(buffer, 0, length);
//                    }
//                    is.close();
//                    os.close();
//                }
//                catch (Exception e){
//                    e.printStackTrace();
//                }
//
//                newImagePath = "file:///E:/IntelliJ/SoulLift_init/src/main/resources/images/" + currentUserType + currentUserId + ".jpg";
//            }
//            else {
//                preparedStatement = connection.prepareStatement("SELECT picture FROM customer WHERE cid = ?");
//                preparedStatement.setInt(1, currentUserId);
//                resultSet = preparedStatement.executeQuery();
//
//                while (resultSet.next()) {
//                    imagePath = resultSet.getString("picture");
//                }
//
//                newImagePath = imagePath;
//            }
//
//            //Storing the image path in the picture variable.
//            psInsertValue = connection.prepareStatement("UPDATE customer SET picture = ? WHERE cid = ?");
//            psInsertValue.setString(1, newImagePath);
//            psInsertValue.setString(2, String.valueOf(currentUserId));
//            psInsertValue.executeUpdate();
//
//            preparedStatement = connection.prepareStatement("SELECT picture FROM customer WHERE cid = ?");
//            preparedStatement.setInt(1, currentUserId);
//            resultSet = preparedStatement.executeQuery();
//
//            while (resultSet.next()) {
//                String image = resultSet.getString("picture");
//                profilePicture = new Image(image);
//                profileView.setImage(profilePicture);
//            }
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//        finally {
//            resultSet.close();
//            preparedStatement.close();
//            connection.close();
//        }
//    }


    public void updateProfilePicture() throws SQLException {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement psInsertValue = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(db_url, db_user, db_pass);

            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
            File chosenFile = fileChooser.showOpenDialog(null);

            String newImagePath = null;

            if (chosenFile != null) {
                String saveDirectory = "E:\\IntelliJ\\Vehicle\\user_images\\";
                File destFile = new File(saveDirectory + currentUserType + currentUserId + ".jpg");

                try (InputStream is = new FileInputStream(chosenFile);
                     OutputStream os = new FileOutputStream(destFile))
                {
                    byte[] buffer = new byte[1024];
                    int length;

                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                newImagePath = destFile.toURI().toString(); // Convert to proper JavaFX format
            }
            else {
                preparedStatement = connection.prepareStatement("SELECT picture FROM customer WHERE cid = ?");
                preparedStatement.setInt(1, currentUserId);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    newImagePath = resultSet.getString("picture");
                }
            }

            if (newImagePath != null) {
                psInsertValue = connection.prepareStatement("UPDATE customer SET picture = ? WHERE cid = ?");
                psInsertValue.setString(1, newImagePath);
                psInsertValue.setInt(2, currentUserId);
                psInsertValue.executeUpdate();

                profilePicture = new Image(newImagePath, false);
                profileView.setImage(profilePicture);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (psInsertValue != null) psInsertValue.close();
            if (connection != null) connection.close();
        }
    }


    public void updateProfile(ActionEvent actionEvent) throws IOException, SQLException {

        String fullname = FullName.getText();
        String email = Email.getText();
        String phone = Phone.getText();
        String address = Address.getText();

        Connection connection = null;
        PreparedStatement psInsertValue = null;

        if (fullname.equals("") || email.equals("") || phone.equals("") || address.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Text Field Empty!");
            alert.setContentText("Please fill up all the details!");
            alert.show();
        }
        else {
            try {
                connection = DriverManager.getConnection(db_url, db_user, db_pass);

                psInsertValue = connection.prepareStatement("UPDATE customer SET fullname = ?, email = ?, phone = ?, address = ? WHERE cid = ?");
                psInsertValue.setString(1, fullname);
                psInsertValue.setString(2, email);
                psInsertValue.setString(3, phone);
                psInsertValue.setString(4, address);
                psInsertValue.setString(5, String.valueOf(currentUserId));
                psInsertValue.executeUpdate();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Update Successful!");
                alert.setContentText("Your account has been updated successfully!");
                alert.show();
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
            finally {
                if (psInsertValue != null) psInsertValue.close();
                if (connection != null) connection.close();
            }
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
