package org.Network;

public class ImageData {
    private int ordinal;
    private byte[] data;

    public ImageData(int ordinal, byte[] data){
        this.ordinal = ordinal;
        this.data = data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }
    public byte[] getData() {
        return data;
    }
    public int getOrdinal() {
        return ordinal;
    }
}
