package org.example.vehicle;

import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;
import java.sql.*;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    final public int currentUserId = SessionManager.getCurrentUserId();
    final public String currentUserType = SessionManager.getCurrentUserType();
    private String clientInfo;

    String db_url = Constants.DATABASE_URL;
    String db_user = Constants.DATABASE_USERNAME;
    String db_pass = Constants.DATABASE_PASSWORD;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet;

            try{
                connection = DriverManager.getConnection(db_url, db_user, db_pass);

                if (currentUserType.equals("customer")){
                    preparedStatement = connection.prepareStatement("SELECT * FROM customer WHERE cid = ?");
                }
                else if (currentUserType.equals("worker")){
                    preparedStatement = connection.prepareStatement("SELECT * FROM worker WHERE wid = ?");
                }

                preparedStatement.setInt(1, currentUserId);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()){
                    String username = resultSet.getString("username");
                    this.clientInfo = currentUserId + " " + currentUserType + " " + username;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            }

            bufferedWriter.write(this.clientInfo);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
        catch (IOException | SQLException e){
            e.printStackTrace();
            closeEveryThing(socket, bufferedReader, bufferedWriter);
        }
    }

    public void receiveMsgFromServer(VBox vBox){

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()) {
                    try {
                        String messageFromClient = bufferedReader.readLine();
                        AdminChatController.addLabel(messageFromClient, vBox);
                    } catch (IOException e) {
                        e.printStackTrace();
                        closeEveryThing(socket, bufferedReader, bufferedWriter);
                        break;
                    }
                }
            }
        }).start();
    }

    public void sendMsgToServer(String messageToServer){
        try {
            bufferedWriter.write(messageToServer);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
        catch (IOException e){
            e.printStackTrace();
            closeEveryThing(socket, bufferedReader, bufferedWriter);
        }
    }

    public void closeEveryThing(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try {
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
