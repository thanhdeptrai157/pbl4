package com.example;
public class Infor {
    private int idImage;
    private int order;
    private String data;

    public Infor(int idImage, int order, String data){
        this.idImage = idImage;
        this.order = order;
        this.data = data;
    }

    public void setData(String data) {
        this.data = data;
    }
    public void setOrder(int order) {
        this.order = order;
    }
    public void setIdImage(int idImage) {
        this.idImage = idImage;
    }
    public String getData() {
        return data;
    }
    public int getOrder() {
        return order;
    }
    public int getIdImage() {
        return idImage;
    }
}
