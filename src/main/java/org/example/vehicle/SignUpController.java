package org.example.vehicle;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Objects;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {

    @FXML
    Stage stage;

    @FXML
    Scene scene;

    @FXML
    Parent root;

    @FXML
    AnchorPane anchorPane;

    @FXML
    Node customerSection; // Left-side elements

    @FXML
    Node workerSection;

    @FXML
    TextField UserName, UserName2, FullName, FullName2, Email, Email2;

    @FXML
    PasswordField Password, Password2;

    @FXML
    ImageView divider;

    String db_url = Constants.DATABASE_URL;
    String db_user = Constants.DATABASE_USERNAME;
    String db_pass = Constants.DATABASE_PASSWORD;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        workerSection.setOpacity(0);
    }

    public void workerScene(ActionEvent actionEvent) {
        // Fade out customer section
        FadeTransition fadeOutCustomer = new FadeTransition(Duration.seconds(1), customerSection);
        fadeOutCustomer.setFromValue(1);
        fadeOutCustomer.setToValue(0);

        // Fade in worker section
        FadeTransition fadeInWorker = new FadeTransition(Duration.seconds(1), workerSection);
        fadeInWorker.setFromValue(0);
        fadeInWorker.setToValue(1);

        // Play transitions
        fadeOutCustomer.play();
        fadeInWorker.play();
    }

    public void customerScene(ActionEvent actionEvent) {
        // Fade out worker section
        FadeTransition fadeOutWorker = new FadeTransition(Duration.seconds(1), workerSection);
        fadeOutWorker.setFromValue(1);
        fadeOutWorker.setToValue(0);

        // Fade in customer section
        FadeTransition fadeInCustomer = new FadeTransition(Duration.seconds(1), customerSection);
        fadeInCustomer.setFromValue(0);
        fadeInCustomer.setToValue(1);

        // Play transitions
        fadeOutWorker.play();
        fadeInCustomer.play();
    }

    public void signUpCustomer(ActionEvent actionEvent) throws IOException, SQLException {

        String username = UserName.getText();
        String fullname = FullName.getText();
        String email = Email.getText();
        String password = Password.getText();

        Connection connection = null;
        PreparedStatement psInsertValue = null;
        PreparedStatement psCheckUserExist = null;
        ResultSet resultName = null;

        if (username.equals("") || fullname.equals("") || email.equals("") || password.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Empty text fields!");
            alert.setContentText("Please fill up all the information!");
            alert.show();
        }
        else {
            try {
                connection = DriverManager.getConnection(db_url, db_user, db_pass);

                psCheckUserExist = connection.prepareStatement("SELECT * FROM customer WHERE username = ?");
                psCheckUserExist.setString(1, username);
                resultName = psCheckUserExist.executeQuery();

                if (resultName.isBeforeFirst()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("You can't use this username!");
                    alert.setContentText("Username is already taken! Choose a new username.");
                    alert.show();
                }
                else {
                    psInsertValue = connection.prepareStatement("INSERT INTO customer (username, fullName, email, password) VALUES(?, ?, ?, ?)");
                    psInsertValue.setString(1, username);
                    psInsertValue.setString(2, fullname);
                    psInsertValue.setString(3, email);
                    psInsertValue.setString(4, password);
                    psInsertValue.executeUpdate();

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setHeaderText("Signup Successful!");
                    alert.setContentText("Your account has been created successfully!");
                    alert.show();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (resultName != null) resultName.close();
                if (psCheckUserExist != null) psCheckUserExist.close();
                if (psInsertValue != null) psInsertValue.close();
                if (connection != null) connection.close();
            }
        }
    }


    public void signUpWorker(ActionEvent actionEvent) throws IOException, SQLException {

        String username = UserName2.getText();
        String fullname = FullName2.getText();
        String email = Email2.getText();
        String password = Password2.getText();

        Connection connection = null;
        PreparedStatement psInsertValue = null;
        PreparedStatement psCheckUserExist = null;
        ResultSet resultName = null;

        if (username.equals("") || fullname.equals("") || email.equals("") || password.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Empty text fields!");
            alert.setContentText("Please fill up all the information!");
            alert.show();
        }
        else {
            try {
                connection = DriverManager.getConnection(db_url, db_user, db_pass);

                psCheckUserExist = connection.prepareStatement("SELECT * FROM worker WHERE username = ?");
                psCheckUserExist.setString(1, username);
                resultName = psCheckUserExist.executeQuery();

                if (resultName.isBeforeFirst()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("You can't use this username!");
                    alert.setContentText("Username is already taken! Choose a new username.");
                    alert.show();
                }
                else {
                    psInsertValue = connection.prepareStatement("INSERT INTO worker (username, fullName, email, password, status) VALUES(?, ?, ?, ?, ?)");
                    psInsertValue.setString(1, username);
                    psInsertValue.setString(2, fullname);
                    psInsertValue.setString(3, email);
                    psInsertValue.setString(4, password);
                    psInsertValue.setString(5, "Pending");
                    psInsertValue.executeUpdate();

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setHeaderText("Signup Successful!");
                    alert.setContentText("Your account has been created successfully!");
                    alert.show();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (resultName != null) resultName.close();
                if (psCheckUserExist != null) psCheckUserExist.close();
                if (psInsertValue != null) psInsertValue.close();
                if (connection != null) connection.close();
            }
        }
    }


    public void backToLogin(ActionEvent actionEvent) throws IOException {

        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
        stage = (Stage) ((Node)(actionEvent.getSource())).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
