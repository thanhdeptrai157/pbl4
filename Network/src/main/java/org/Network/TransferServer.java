package org.Network;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.awt.*;

public class TransferServer extends JFrame implements ActionListener {

    private static TextField txtPort, txtFile;
    private JButton btnChoose, btnSend,btnInit;
    private File selectedFile;
    private static  ServerSocket serverSocket;// Server socket thành thuộc tính
    private static Socket socket;
    private static boolean isConnected = false;

    public void GUI() {
        JLabel labelPort;
        JPanel pnTotal, pnTop, pnCenter;

        labelPort = new JLabel("PORT: ");
        txtPort = new TextField(15);
        btnChoose = new JButton("Choose");
        btnChoose.addActionListener(this);
        btnInit = new JButton("Init Server");
        btnInit.addActionListener(this);
        txtFile = new TextField(15);
        btnSend = new JButton("Send file");
        btnSend.addActionListener(this);

        pnTop = new JPanel(new FlowLayout(10));
        pnCenter = new JPanel(new FlowLayout(40, 40, 40));
        pnTotal = new JPanel(new BorderLayout());

        pnTop.add(labelPort);
        pnTop.add(txtPort);
        pnTop.add(btnInit);


        pnCenter.add(btnChoose);
        pnCenter.add(txtFile);
        pnCenter.add(btnSend);

        pnTotal.add(pnTop, BorderLayout.NORTH);
        pnTotal.add(pnCenter, BorderLayout.CENTER);

        add(pnTotal);
        setBounds(200, 200, 500, 350);
        setVisible(true);
    }

    public TransferServer(String st) {
        super(st);
        GUI();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new TransferServer("Server");

        String filePathReceive = "E:\\DuAnTruongHoc\\Ky5\\TransferFile\\FileServerReceive\\";

        while(true){
            Thread.sleep(200);
            if(isConnected) break;
        }

        new Thread(() -> {
            while (isConnected && socket != null && !socket.isClosed()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }

                ReceiverTransfer receiveFile = new ReceiverTransfer(filePathReceive, socket);
                receiveFile.start();
                try {
                    receiveFile.join(); // Wait for the file reception to complete
                    System.out.println("Received file from client.");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnChoose) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn một file để gửi");
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                txtFile.setText(selectedFile.getName());
                System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            }
        }
        else  if (e.getSource() == btnSend)
        {

            /*
            Server Main tạo serversocket ở bên ngoài và khi connect được với client sẽ giữ một socket để duy tri
            Gửi và nhận file sẽ trên socket đó

             */

            if (socket != null && selectedFile != null) {
                // Tạo luồng gửi file và bắt đầu gửi
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                SenderTransfer senderFile = new SenderTransfer(selectedFile, socket);
                senderFile.start();
                try {
                    senderFile.join(); // Đợi quá trình gửi hoàn tất
                    System.out.println("Đã gửi file: " + selectedFile.getName());
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                System.out.println("Chưa chọn file hoặc chưa kết nối client");
            }


        }
        else if(e.getSource() == btnInit){
            if(txtPort.getText().trim() !=""){
                int portServer = Integer.parseInt(txtPort.getText().toString().trim());
                try {
                    serverSocket = new ServerSocket(portServer);
                    System.out.println("Server đã được tạo ");
                    socket = serverSocket.accept();
                    if(socket != null){
                        isConnected = true;
                        btnInit.setEnabled(false);
                    }

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }


}
