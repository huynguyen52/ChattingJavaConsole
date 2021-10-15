/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.chatting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author huyng
 */
public class Client {

    private InetAddress host;
    private int port;

    public Client(InetAddress host, int port) {
        this.host = host;
        this.port = port;
    }

    private void execute() {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("Nhập tên của bạn: ");
            String name = sc.nextLine();
            
            
            Socket client = new Socket(host, port);
            ReadClient read = new ReadClient(client);
            read.start();
            WriteClient write = new WriteClient(client, name);
            write.start();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        Client client;
        try {
            client = new Client(InetAddress.getLocalHost(), 8888);
            client.execute();
        } catch (UnknownHostException ex) {
            Logger.getLogger(ReadClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}

class ReadClient extends Thread {

    private Socket client;

    public ReadClient(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        DataInputStream din = null;
        try {
            din = new DataInputStream(client.getInputStream());
            while (true) {
                String msg = din.readUTF();
                System.out.println(msg);
            }
        } catch (Exception e) {
            //Ngắt kết nối khi có lỗi xảy ra
            try {
                din.close();
                client.close();
            } catch (IOException ex) {
                System.out.println("Ngắt kết nối với Server");
            }
        }
    }
}

class WriteClient extends Thread {

    private Socket client;
    private String name;

    public WriteClient(Socket client, String name) {
        this.client = client;
        this.name = name;
    }

    @Override
    public void run() {
        DataOutputStream dout = null;
        Scanner sc = null;
        try {
            dout = new DataOutputStream(client.getOutputStream());
            //Nhập msg gửi lên
            sc = new Scanner(System.in);
            while (true) {
                String msg = sc.nextLine();
                dout.writeUTF(name + ": " + msg);
            }
        } catch (Exception e) {
            //Ngắt kết nối khi có lỗi xảy ra
            try {
                dout.close();
                client.close();
            } catch (IOException ex) {
                System.out.println("Ngắt kết nối với Server");
            }
        }
    }
}
