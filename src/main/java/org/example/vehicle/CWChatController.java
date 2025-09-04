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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.sql.*;
import java.util.Objects;
import java.util.ResourceBundle;

public class CWChatController implements Initializable {

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

    String db_url = Constants.DATABASE_URL;
    String db_user = Constants.DATABASE_USERNAME;
    String db_pass = Constants.DATABASE_PASSWORD;

    final private int currentUserId = SessionManager.getCurrentUserId();
    final private String currentUserType = SessionManager.getCurrentUserType();

    private Client client;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            client = new Client(new Socket("localhost", 33333));
        }
        catch (Exception e){
            e.printStackTrace();
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(db_url, db_user, db_pass);

            if (currentUserType.equals("customer")) {
                preparedStatement = connection.prepareStatement("SELECT * FROM msg_to_admin WHERE cid = ? ORDER BY created_at ASC");
            }
            else if (currentUserType.equals("worker")) {
                preparedStatement = connection.prepareStatement("SELECT * FROM msg_to_admin WHERE wid = ? ORDER BY created_at ASC");
            }
            preparedStatement.setInt(1, currentUserId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){

                String message = resultSet.getString("message");
                int messageFrom = resultSet.getInt("cw_or_admin");

                if(messageFrom == 1){

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

                    textFlow.setStyle("-fx-background-color: rgb(233, 233, 235);" + "-fx-background-radius: 20px;");
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
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        vBox_message.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                scrollPane.setVvalue((Double) oldValue);
            }
        });

        client.receiveMsgFromServer(vBox_message);

        send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                String messageToSend = messageField.getText();

                Connection connection = null;
                PreparedStatement preparedStatement = null;

                try{
                    connection = DriverManager.getConnection(db_url, db_user, db_pass);

                    if (currentUserType.equals("customer")) {
                        preparedStatement = connection.prepareStatement("INSERT INTO msg_to_admin (message, cw_or_admin, cid) VALUES(?, ?, ?)");
                    }
                    else if (currentUserType.equals("worker")) {
                        preparedStatement = connection.prepareStatement("INSERT INTO msg_to_admin (message, cw_or_admin, wid) VALUES(?, ?, ?)");
                    }

                    preparedStatement.setString(1, messageToSend);
                    preparedStatement.setInt(2, 1);
                    preparedStatement.setInt(3, currentUserId);
                    preparedStatement.executeUpdate();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    try {
                        if (preparedStatement != null) preparedStatement.close();
                        if (connection != null) connection.close();
                    }
                    catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

                if(!messageToSend.isEmpty()){

                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER_RIGHT);
                    hBox.setPadding(new Insets(5, 5, 5, 10));

                    Text text = new Text(messageToSend);
                    TextFlow textFlow = new TextFlow(text);

                    textFlow.setStyle("-fx-color: rgb(239, 242, 255);" + "-fx-background-color: rgb(135,77,255);" + "-fx-background-radius: 20px;");
                    textFlow.setPadding(new Insets(5, 10 , 5, 10));
                    text.setFill(Color.color(0.934, 0.945, 0.996));

                    hBox.getChildren().add(textFlow);
                    vBox_message.getChildren().add(hBox);

                    client.sendMsgToServer(messageToSend);
                    messageField.clear();
                }
            }
        });
    }

    public static void addLabel(String messageFromServer, VBox vBox){

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5, 5, 5, 10));

        Text text = new Text(messageFromServer);
        TextFlow textFlow = new TextFlow(text);

        textFlow.setStyle("-fx-background-color: rgb(233, 233, 235);" + "-fx-background-radius: 20px;");
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

        if (currentUserType.equals("customer")) {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("customer_home.fxml")));
        }
        else if (currentUserType.equals("worker")) {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("worker_home.fxml")));
        }

        stage = (Stage) ((Node)(actionEvent.getSource())).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Admin HomePage");
        stage.setScene(scene);
        stage.show();
    }
}

