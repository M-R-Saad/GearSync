package org.example.vehicle;

import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Client2 {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public int WORKER_ID;
    public int CUSTOMER_ID;
    public String workerName;
    public String customerName;

    String db_url = Constants.DATABASE_URL;
    String db_user = Constants.DATABASE_USERNAME;
    String db_pass = Constants.DATABASE_PASSWORD;

    public Client2(Socket socket, int CUSTOMER_ID) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.CUSTOMER_ID = CUSTOMER_ID;

            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet;

            try{
                connection = DriverManager.getConnection(db_url, db_user, db_pass);

                preparedStatement = connection.prepareStatement("SELECT username FROM customer WHERE cid = ?");
                preparedStatement.setInt(1, this.CUSTOMER_ID);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()){
                    this.customerName = resultSet.getString("username");
                }
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

            bufferedWriter.write(this.CUSTOMER_ID + " " +  this.customerName);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            String[] workerInfo = bufferedReader.readLine().split(" ");

            this.WORKER_ID = Integer.parseInt(workerInfo[0]);
            this.workerName = workerInfo[1];

            System.out.println("Psychiatrist Name: " + workerName);
            System.out.println("User Name: " + customerName);
        }
        catch (IOException e){
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
                        WorkerChatController.addLabel(messageFromClient, vBox);
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
