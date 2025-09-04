package org.example.vehicle;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class CustomerHomeController implements Initializable {

    @FXML
    Stage stage;

    @FXML
    Scene scene;

    @FXML
    Parent root;

    @FXML
    AnchorPane anchorPane;

    @FXML
    ImageView backgroundImage;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        int currentIndex = 0;
        String[] imagePaths = {
                "file:///E:/IntelliJ/Vehicle/design/customer_home1.png",
                "file:///E:/IntelliJ/Vehicle/design/customer_home2.png",
                "file:///E:/IntelliJ/Vehicle/design/customer_home3.png",
                "file:///E:/IntelliJ/Vehicle/design/customer_home6.png",
                "file:///E:/IntelliJ/Vehicle/design/customer_home4.png",
                "file:///E:/IntelliJ/Vehicle/design/customer_home5.png"
        };

        playBackgroundSlideshow(currentIndex, imagePaths);
    }

//    private void playBackgroundSlideshow(int initialIndex, String[] imagePaths) {
//        if (imagePaths.length == 0) return;
//
//        int[] currentIndex = {initialIndex}; // Use an array to make it effectively final
//
//        Image initialImage = new Image(imagePaths[currentIndex[0]]);
//        backgroundImage.setImage(initialImage);
//
//        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
//            // Fade out the current image
//            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), backgroundImage);
//            fadeOut.setFromValue(1);
//            fadeOut.setToValue(0);
//            fadeOut.setOnFinished(e -> {
//                // Change the image once fade-out is complete
//                currentIndex[0] = (currentIndex[0] + 1) % imagePaths.length;
//                Image nextImage = new Image(imagePaths[currentIndex[0]]);
//                backgroundImage.setImage(nextImage);
//
//                // Fade in the new image
//                FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), backgroundImage);
//                fadeIn.setFromValue(0);
//                fadeIn.setToValue(1);
//                fadeIn.play();
//            });
//            fadeOut.play();
//        }));
//
//        timeline.setCycleCount(Timeline.INDEFINITE);
//        timeline.play();
//    }

    private void playBackgroundSlideshow(int initialIndex, String[] imagePaths) {
        if (imagePaths.length == 0) return;

        int[] currentIndex = {initialIndex}; // Use an array to make it effectively final

        Image initialImage = new Image(imagePaths[currentIndex[0]]);
        backgroundImage.setImage(initialImage);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            // Fade out and slide out the current image
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), backgroundImage);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);

            TranslateTransition slideOut = new TranslateTransition(Duration.seconds(1.5), backgroundImage);
            slideOut.setFromX(0);
            slideOut.setToX(-backgroundImage.getFitWidth());

            fadeOut.setOnFinished(e -> {
                // Change the image once fade-out and slide-out are complete
                currentIndex[0] = (currentIndex[0] + 1) % imagePaths.length;
                Image nextImage = new Image(imagePaths[currentIndex[0]]);
                backgroundImage.setImage(nextImage);

                // Reset position for slide-in
                backgroundImage.setTranslateX(backgroundImage.getFitWidth());

                // Fade in and slide in the new image
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), backgroundImage);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);

                TranslateTransition slideIn = new TranslateTransition(Duration.seconds(0.5), backgroundImage);
                slideIn.setFromX(backgroundImage.getFitWidth());
                slideIn.setToX(0);

                fadeIn.play();
                slideIn.play();
            });

            fadeOut.play();
            slideOut.play();
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
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
