package com.example;

import java.awt.List;
import java.util.ArrayList;
import java.util.Random;

public class Packet{

    public int sizeData = 1024;

    public int idImage;
    public int size;
    public ArrayList<Infor> infors;

    public Packet(byte[] bytes){
        this.idImage = new Random().nextInt();
        int size = bytes.length / sizeData + 1;
        this.infors = new ArrayList<Infor>(size);

    }

    public int getIdImage() {
        return idImage;
    }
    public int getSize() {
        return size;
    }
    public ArrayList getInfors() {
        return infors;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public void setIdImage(int idImage) {
        this.idImage = idImage;
    }
    public void setInfors(ArrayList<Infor> infors) {
        this.infors = infors;
    }

}
