package org.example.vehicle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.ResourceBundle;

public class WorkerChatController implements Initializable {

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
    VBox vBox_message;

    @FXML
    TextField messageField;

    @FXML
    Button send;

    @FXML
    Label customerNameLabel;

    @FXML
    ImageView profileView;

    @FXML
    Image profilePicture;

    final public int WORKER_ID = SessionManager.getCurrentUserId();

    String db_url = Constants.DATABASE_URL;
    String db_user = Constants.DATABASE_USERNAME;
    String db_pass = Constants.DATABASE_PASSWORD;

    private Server2 server;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            server = new Server2(new ServerSocket(Constants.SERVER_PORT), WORKER_ID);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        customerNameLabel.setText(server.customerName);

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(db_url, db_user, db_pass);

            preparedStatement = connection.prepareStatement("SELECT * FROM customer WHERE cid = ?");
            preparedStatement.setInt(1, server.CUSTOMER_ID);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String picture = resultSet.getString("picture");
                System.out.println(picture);
                profilePicture = new Image(picture);
                profileView.setImage(profilePicture);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        connection = null;
        preparedStatement = null;
        resultSet = null;

        try {
            connection = DriverManager.getConnection(db_url, db_user, db_pass);

            preparedStatement = connection.prepareStatement("SELECT * FROM msg_wtc WHERE cid = ? AND wid = ? ORDER BY created_at ASC");
            preparedStatement.setInt(1, server.CUSTOMER_ID);
            preparedStatement.setInt(2, WORKER_ID);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){

                String message = resultSet.getString("message");
                int messageFrom = resultSet.getInt("cstmr_or_wrkr");

                if(messageFrom == 0){

                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER_RIGHT);
                    hBox.setPadding(new Insets(5, 5, 5, 10));

                    Text text = new Text(message);
                    TextFlow textFlow = new TextFlow(text);

                    textFlow.setStyle("-fx-color: rgb(255,255,255);" + "-fx-background-color: rgb(0,0,0);" + "-fx-background-radius: 20px;");
                    textFlow.setPadding(new Insets(5, 10 , 5, 10));
                    text.setFill(Color.color(0.934, 0.945, 0.996));

                    hBox.getChildren().add(textFlow);
                    vBox_message.getChildren().add(hBox);
                }
                else {

                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    hBox.setPadding(new Insets(5, 5, 5, 10));

                    Text text = new Text(message);
                    TextFlow textFlow = new TextFlow(text);

                    textFlow.setStyle("-fx-background-color: rgb(255,255,255);" + "-fx-background-radius: 20px;");
                    textFlow.setPadding(new Insets(5, 10 , 5, 10));

                    hBox.getChildren().add(textFlow);

                    vBox_message.getChildren().add(hBox);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        vBox_message.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                scrollPane.setVvalue((Double) oldValue);
            }
        });

        server.receiveMsgFromClient(vBox_message);

        send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                String messageToSend = messageField.getText();

                Connection connection = null;
                PreparedStatement preparedStatement = null;

                try{
                    connection = DriverManager.getConnection(db_url, db_user, db_pass);

                    preparedStatement = connection.prepareStatement("INSERT INTO msg_wtc (cid, wid, message, cstmr_or_wrkr) VALUES(?, ?, ?, ?)");
                    preparedStatement.setInt(1, server.CUSTOMER_ID);
                    preparedStatement.setInt(2, WORKER_ID);
                    preparedStatement.setString(3, messageToSend);
                    preparedStatement.setInt(4, 0);
                    preparedStatement.executeUpdate();

                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    if (preparedStatement != null) {
                        try {
                            preparedStatement.close();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        try {
                            connection.close();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                if(!messageToSend.isEmpty()){

                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER_RIGHT);
                    hBox.setPadding(new Insets(5, 5, 5, 10));

                    Text text = new Text(messageToSend);
                    TextFlow textFlow = new TextFlow(text);

                    textFlow.setStyle("-fx-color: rgb(255,255,255);" + "-fx-background-color: rgb(135,77,255);" + "-fx-background-radius: 20px;");
                    textFlow.setPadding(new Insets(5, 10 , 5, 10));
                    text.setFill(Color.color(0.934, 0.945, 0.996));

                    hBox.getChildren().add(textFlow);
                    vBox_message.getChildren().add(hBox);

                    server.sendMsgToClient(messageToSend);
                    messageField.clear();
                }
            }
        });

    }

    public static void addLabel(String messageFromClient, VBox vBox){

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5, 5, 5, 10));

        Text text = new Text(messageFromClient);
        TextFlow textFlow = new TextFlow(text);

        textFlow.setStyle("-fx-background-color: rgb(255,255,255);" + "-fx-background-radius: 20px;");
        textFlow.setPadding(new Insets(5, 10 , 5, 10));

        hBox.getChildren().add(textFlow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vBox.getChildren().add(hBox);
            }
        });
    }

    public void goToHomePage(ActionEvent actionEvent) throws IOException {

        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("worker_home.fxml")));
        stage = (Stage) ((Node)(actionEvent.getSource())).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Worker HomePage");
        stage.setScene(scene);
        stage.show();
    }

}
