package org.example.vehicle;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    Stage stage;

    @FXML
    Scene scene;

    @FXML
    Parent root;

    @FXML
    AnchorPane anchorPane;

    @FXML
    RadioButton customer, worker;

    @FXML
    TextField UserName;

    @FXML
    PasswordField Password;

    @FXML
    ToggleGroup radioButtonGroup;

    @FXML
    ImageView logoImage, backgroundImage;

    String db_url = Constants.DATABASE_URL;
    String db_user = Constants.DATABASE_USERNAME;
    String db_pass = Constants.DATABASE_PASSWORD;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        logoImage.setImage(new Image("file:///E:/IntelliJ/Vehicle/design/logo1.png"));

        int currentIndex = 0;
        String[] imagePaths = {
                "file:///E:/IntelliJ/Vehicle/design/login_bg5x.jpg",
                "file:///E:/IntelliJ/Vehicle/design/login_bg6.jpg",
                "file:///E:/IntelliJ/Vehicle/design/login_bg7x.jpg",
                "file:///E:/IntelliJ/Vehicle/design/login_bg8.jpg",
                "file:///E:/IntelliJ/Vehicle/design/login_bg9x.jpg",
                "file:///E:/IntelliJ/Vehicle/design/login_bg10x.jpg",
                "file:///E:/IntelliJ/Vehicle/design/login_bg11.jpg",
                "file:///E:/IntelliJ/Vehicle/design/login_bg12.jpg",
                "file:///E:/IntelliJ/Vehicle/design/login_bg13.jpg",
                "file:///E:/IntelliJ/Vehicle/design/login_bg14.jpg",
                "file:///E:/IntelliJ/Vehicle/design/login_bg15.jpg",
                "file:///E:/IntelliJ/Vehicle/design/login_bg16.jpg",
                "file:///E:/IntelliJ/Vehicle/design/login_bg1.jpg",
                "file:///E:/IntelliJ/Vehicle/design/login_bg2x.jpg",
                "file:///E:/IntelliJ/Vehicle/design/login_bg3x.jpg",
                "file:///E:/IntelliJ/Vehicle/design/login_bg4x.jpg"
        };

        playBackgroundSlideshow(currentIndex, imagePaths);
    }

    private void playBackgroundSlideshow(int initialIndex, String[] imagePaths) {
        if (imagePaths.length == 0) return;

        int[] currentIndex = {initialIndex}; // Use an array to make it effectively final

        Image initialImage = new Image(imagePaths[currentIndex[0]]);
        backgroundImage.setImage(initialImage);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            // Fade out the current image
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), backgroundImage);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                // Change the image once fade-out is complete
                currentIndex[0] = (currentIndex[0] + 1) % imagePaths.length;
                Image nextImage = new Image(imagePaths[currentIndex[0]]);
                backgroundImage.setImage(nextImage);

                // Fade in the new image
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), backgroundImage);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
            });
            fadeOut.play();
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void logIn(ActionEvent actionEvent) throws IOException, SQLException {

        String username = UserName.getText();
        String password = Password.getText();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        if(username.equals("admin") && password.equals("admin")){
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("admin_home.fxml")));
            stage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
            scene = new Scene(root);
            stage.setTitle("Admin Homepage");
            stage.setScene(scene);
            stage.show();
        }
        else{
            if(customer.isSelected()){
                if (username.equals("") || password.equals("")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Empty text fields!");
                    alert.setContentText("Please fill up all the information!");
                    alert.show();
                }
                else {
                    try {
                        connection = DriverManager.getConnection(db_url, db_user, db_pass);

                        preparedStatement = connection.prepareStatement("SELECT * FROM customer WHERE username = ?");
                        preparedStatement.setString(1, username);
                        resultSet = preparedStatement.executeQuery();

                        if (!resultSet.isBeforeFirst()) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText("Customer not Found!");
                            alert.setContentText("Well, this is awkward. Did you forget your username or did your cat walk across your keyboard?");
                            alert.show();
                        }
                        else {
                            while (resultSet.next()) {
                                String retrievedPassword = resultSet.getString("password");
                                int retrievedUserID = resultSet.getInt("cid");

                                if (retrievedPassword.equals(password)) {
                                    SessionManager.setCurrentUserId(retrievedUserID);
                                    SessionManager.setCurrentUserType(customer.getText().toLowerCase());
                                    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("customer_home.fxml")));
                                    stage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
                                    scene = new Scene(root);
                                    stage.setTitle("Customer HomePage");
                                    stage.setScene(scene);
                                    stage.show();
                                }
                                else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setHeaderText("Wrong Password!");
                                    alert.setContentText("Provided password for the username is incorrect!");
                                    alert.show();
                                }
                            }
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
            }
            else if(worker.isSelected()){
                if (username.equals("") || password.equals("")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Empty text fields!");
                    alert.setContentText("Please fill up all the information!");
                    alert.show();
                }
                else {
                    try {
                        connection = DriverManager.getConnection(db_url, db_user, db_pass);

                        preparedStatement = connection.prepareStatement("SELECT * FROM worker WHERE username = ?");
                        preparedStatement.setString(1, username);
                        resultSet = preparedStatement.executeQuery();

                        if (!resultSet.isBeforeFirst()) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText("User not Found!");
                            alert.setContentText("Well, this is awkward. Did you forget your username or did your cat walk across your keyboard?");
                            alert.show();
                        }
                        else {
                            while (resultSet.next()) {
                                String retrievedPassword = resultSet.getString("password");
                                int retrievedUserID = resultSet.getInt("wid");
                                String accStatus = resultSet.getString("status");

                                if (retrievedPassword.equals(password) && accStatus.equals("Verified")) {
                                    SessionManager.setCurrentUserId(retrievedUserID);
                                    SessionManager.setCurrentUserType(worker.getText().toLowerCase());
                                    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("worker_home.fxml")));
                                    stage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
                                    scene = new Scene(root);
                                    stage.setTitle("Worker HomePage");
                                    stage.setScene(scene);
                                    stage.show();
                                }
                                else if (retrievedPassword.equals(password) && accStatus.equals("Pending")) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setHeaderText("Account not Verified!");
                                    alert.setContentText("This account is yet to be verified by admin!");
                                    alert.show();
                                }
                                else if (retrievedPassword.equals(password) && accStatus.equals("Suspended")) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setHeaderText("Account Suspended!");
                                    alert.setContentText("This account is currently under suspension!");
                                    alert.show();
                                }
                                else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setHeaderText("Wrong Password!");
                                    alert.setContentText("Provided password for the username is incorrect!");
                                    alert.show();
                                }
                            }
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
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("User type not selected!");
                alert.setContentText("Please select what type of user you are!");
                alert.show();
            }
        }
    }


    public void goToSignUp(ActionEvent actionEvent) throws IOException {

        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("signup.fxml")));
        stage = (Stage) ((Node)(actionEvent.getSource())).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
