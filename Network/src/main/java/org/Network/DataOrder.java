package org.Network;

public class DataOrder {
    private int ordinal;
    private byte[] data;
    private int ipAddress;

    public DataOrder(int ipAddress, int ordinal, byte[] data){
        this.ipAddress = ipAddress;
        this.ordinal = ordinal;
        this.data = data;
    }
    public void setIpAddress(int ipAddress) {
        this.ipAddress = ipAddress;
    }
    public int getIpAddress() {
        return ipAddress;
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
