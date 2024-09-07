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
        String imagePath = "/home/giapwibu/wallpaper/frieren10.jpg";

        try {
            BufferedImage bufferedImage = ImageIO.read(new File(imagePath));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", baos);

            baos.flush();

            byte[] imageInBytes = baos.toByteArray();
            baos.close();
        } catch (Exception e) {
        }

        SendData sendData = new SendData("localhost", 5000);

        InfoPacket info = new InfoPacket((short)2, (short)2, (short)10);
        Gson gson = new Gson();
        System.out.println("Json: " +gson.toJson(info).toString());
        System.out.println("Json: " + new String(gson.toJson(info).getBytes()));
        sendData.Send(gson.toJson(info).getBytes());
        InfoPacket inf = gson.fromJson(new String(gson.toJson(info).getBytes()), InfoPacket.class); 
        System.out.println(inf.toString());
    }
}
