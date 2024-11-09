package org.Network;


import java.util.*;
import java.net.*;
import java.io.*;

public class ReceiverTransfer extends Thread{

    private String filePathReceive;
    private Socket socket;

    public String getFilePathReceive() {
        return filePathReceive;
    }

    public void setFilePathReceive(String filePathReceive) {
        this.filePathReceive = filePathReceive;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ReceiverTransfer(String filePathReceive, Socket socket){
        this.filePathReceive = filePathReceive;
        this.socket = socket;
    }

    public static void getFile(String filePathReceive, Socket socket){
        try {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());

            // Nhận tên tệp
            String fileName = inputStream.readUTF();
            System.out.println("Tên tệp nhận: " + fileName);

            // Nhận kích thước tệp
            long expectedFileSize = inputStream.readLong();
            System.out.println("Kích thước tệp dự kiến: " + expectedFileSize + " bytes");

            File saveFile = new File(filePathReceive + fileName);
            if (saveFile.exists()) {
                String newNameFile = createNewFile(saveFile);
                saveFile = new File(filePathReceive + newNameFile);
            }

            try (FileOutputStream writeToReceiveFile = new FileOutputStream(saveFile)) {
                byte[] readByte = new byte[4096];
                int byteLength;
                long totalBytesReceived = 0;

                while (totalBytesReceived < expectedFileSize && (byteLength = inputStream.read(readByte)) != -1) {
                    writeToReceiveFile.write(readByte, 0, byteLength);
                    totalBytesReceived += byteLength; // Cập nhật tổng số byte đã nhận
                }

                writeToReceiveFile.flush(); // Đảm bảo toàn bộ dữ liệu được ghi

                // Kiểm tra kích thước nhận được
                if (totalBytesReceived == expectedFileSize) {
                    System.out.println("Đã nhận tệp thành công.");
                } else {
                    System.out.println("Kích thước nhận được không khớp với kích thước dự kiến.");
                }

            } catch (IOException e) {
                System.out.println("Lỗi khi ghi tệp: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Lỗi từ ReceiveFile: " + e.getMessage());
        }
    }

    public static String createNewFile(File oldFile){
        String newName ="";
        String fileInfo = oldFile.getName();
        String nameOldFile ="";
        String extensionOldFile = "";

        for(int i = 0 ;i < fileInfo.length() ;i++){
            if(fileInfo.charAt(i) == '.'){
                nameOldFile = fileInfo.substring(0,i);
                extensionOldFile = fileInfo.substring(i);
                break;
            }
        }

        int counter = 1;
        do{
            newName="";
            newName += nameOldFile + "_"+counter + extensionOldFile;
            counter++;
        }while(new File(oldFile.getParent(),newName).exists()); // nếu file tồn tại thì tiếp tục lặp counter

        return newName;
    }

    @Override
    public synchronized void run() {

        getFile(filePathReceive,socket);
    }
}
