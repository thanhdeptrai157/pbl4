package org.Network;

public class ACKData {
    private int id; // dung de xac nhan goi tin
    private String classT; 
    private byte[] data;

    public ACKData(int id, String classT, byte[] data){
        this.id = id;
        this.classT = classT;
        this.data = data;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
    public int getId() {
        return id;
    }
    public byte[] getData() {
        return data;
    }
    public void setClassT(String classT) {
        this.classT = classT;
    }
    public String getClassT() {
        return classT;
    }
}
