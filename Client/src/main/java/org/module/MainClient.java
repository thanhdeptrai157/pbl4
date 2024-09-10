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
        String imagePath = "D:\\Video Android\\anh1.jpg";
        String imagePath1 = "D:\\Video Android\\anh.jpg";
        byte[] imageInBytes = null;
        byte[] imageInBytes1 = null;

        try {
            BufferedImage bufferedImage = ImageIO.read(new File(imagePath));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", baos);
            
            baos.flush();

            imageInBytes = baos.toByteArray();
            baos.close();
            BufferedImage bufferedImage1 = ImageIO.read(new File(imagePath1));
            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage1, "jpg", baos1);
            
            baos1.flush();

            imageInBytes1 = baos1.toByteArray();
            baos1.close();
        } catch (Exception e) {
        }
        
        int time = 30 * 10;

        SendData sendData = new SendData("10.10.36.104", 5000);
        for(int i = 0; i < time; ++i){
            System.out.println("gui thong diep");
            if(i % 2 == 0)
                sendData.Send(imageInBytes1);
            else 
                sendData.Send(imageInBytes);

            System.out.println(imageInBytes.length);
            Thread.sleep(30);
        }


    }

}
