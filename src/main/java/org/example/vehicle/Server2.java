package org.example.vehicle;

import javafx.scene.layout.VBox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Server2 {

    private ServerSocket serverSocket;
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

    public Server2(ServerSocket serverSocket, int WORKER_ID) {
        try {
            this.serverSocket = serverSocket;
            this.socket = serverSocket.accept();
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.WORKER_ID = WORKER_ID;

            String[] userInfo = bufferedReader.readLine().split(" ");

            this.CUSTOMER_ID = Integer.parseInt(userInfo[0]);
            this.customerName = userInfo[1];

            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet;

            try{
                connection = DriverManager.getConnection(db_url, db_user, db_pass);

                preparedStatement = connection.prepareStatement("SELECT username FROM worker WHERE wid = ?");
                preparedStatement.setInt(1, this.WORKER_ID);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()){
                    this.workerName = resultSet.getString("username");
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

            bufferedWriter.write(this.WORKER_ID + " " +  this.workerName);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            System.out.println("Worker Name: " + workerName);
            System.out.println("Worker Name: " + customerName);
        }
        catch (IOException e){
            e.printStackTrace();
            closeEveryThing(socket, bufferedReader, bufferedWriter);
        }
    }

    public void receiveMsgFromClient(VBox vBox){

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

    public void sendMsgToClient(String messageToClient){
        try {
            bufferedWriter.write(messageToClient);
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
