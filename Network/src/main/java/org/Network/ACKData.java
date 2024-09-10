package org.Network;

public class ACKData {
    private int id;
    private byte[] data;

    public ACKData(int id, byte[] data){
        this.id = id;
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
}
