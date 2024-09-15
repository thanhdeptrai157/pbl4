package org.Network;

public class ACKData {
    private int id; // dung de xac nhan goi tin
    private Class<?> classT; 
    private byte[] data;

    public ACKData(int id, Class<?> classT, byte[] data){
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
    public void setClassT(Class<?> classT) {
        this.classT = classT;
    }
    public Class<?> getClassT() {
        return classT;
    }
}
