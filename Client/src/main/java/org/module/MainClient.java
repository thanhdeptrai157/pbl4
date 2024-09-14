package org.module;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import javax.imageio.ImageIO;

import org.Network.InfoPacket;
import org.Network.SendData;

import com.google.gson.Gson;

public class MainClient {

    public static void main(String[] args) throws AWTException, InterruptedException {
//        String imagePath = "D:\\Video Android\\anh1.jpg";
//        String imagePath1 = "D:\\Video Android\\anh.jpg";

        byte[] imageInBytes = null;
        //byte[] imageInBytes1 = null;

        Robot robot = new Robot();
        Rectangle rect = new Rectangle(0, 0, 1200, 600);
        SendData sendData = new SendData("10.10.0.238", 5000);

        while (true) {
            try {


                BufferedImage screenShot = robot.createScreenCapture(rect);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(screenShot, "jpg", baos); //

                imageInBytes = baos.toByteArray();

                baos.close();
            } catch (Exception e) {
                System.out.println("Lỗi từ MainClient  : " + e.getMessage());

            }

//            int time = 30 * 10;
//
//
//            //  SendData sendData = new SendData("localhost", 5000);
//            for (int i = 0; i < time; ++i) {
//                System.out.println("gui thong diep");
//                sendData.Send(imageInBytes);
//
//                System.out.println(imageInBytes.length);
//                Thread.sleep(30);
//            }

            System.out.println("gui hinh anh : ");
            sendData.Send(imageInBytes);

            System.out.println(imageInBytes.length);
              Thread.sleep(1000);


        }



    }

}