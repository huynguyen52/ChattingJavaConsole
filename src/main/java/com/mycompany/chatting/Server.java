/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.chatting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author huyng
 */
public class Server {

    private int port;
    //mảng chứa ds client kết nối đến server
    public static ArrayList<Socket> listSK;

    public Server(int port) {
        this.port = port;
    }

    private void execute() {
        try {
            ServerSocket server = new ServerSocket(port);
            WriteServer write = new WriteServer();
            write.start();
            System.out.println("Server is running....");
            while (true) {
                //nhận kết nối từ client và thêm vào list
                Socket socket = server.accept();
                System.out.println("Connected with " + socket);
                Server.listSK.add(socket);
                ReadServer read = new ReadServer(socket);
                read.start();
            }

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        Server.listSK = new ArrayList<>();
        Server server = new Server(8888);
        server.execute();
    }
}

class ReadServer extends Thread {

    private Socket socket;

    public ReadServer(Socket server) {
        this.socket = server;
    }

    @Override
    public void run() {
        DataInputStream din = null;
        try {
            din = new DataInputStream(socket.getInputStream());
            while (true) {
                String msg = din.readUTF();
                if(msg.contains("exit")){
                    Server.listSK.remove(socket);
                    System.out.println("Ngắt kết nối với " + socket);
                    din.close();
                    socket.close();
                    continue;
                }
                for (Socket item : Server.listSK) {
                    //server gửi lại message cho các máy client trừ máy gửi
                    if (item.getPort() != socket.getPort()) {
                        DataOutputStream dout = new DataOutputStream(item.getOutputStream());
                        dout.writeUTF(msg);
                    }
                }
                System.out.println(msg);
            }
        } catch (Exception e) {
            //Ngắt kết nối khi có lỗi xảy ra
            try {
                din.close();
                socket.close();
            } catch (IOException ex) {
                System.out.println("Ngắt kết nối với Server");
            }
        }
    }
}

class WriteServer extends Thread {

    @Override
    public void run() {
        DataOutputStream dout = null;
        Scanner sc = new Scanner(System.in);
        while (true) {
            String msg = sc.nextLine();
            for (Socket item : Server.listSK) {
                try {
                    dout = new DataOutputStream(item.getOutputStream());
                } catch (IOException ex) {
                    Logger.getLogger(WriteServer.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    dout.writeUTF("Server: " + msg);
                } catch (IOException ex) {
                    Logger.getLogger(WriteServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
