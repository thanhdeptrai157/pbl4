package org.Network;

public class InfoPacket {
    private short id;
    private short count;
    private short sizeElementPacket;

    public  InfoPacket(short id, short count, short sizeElementpacket){
        this.id = id;
        this.count = count;
        this.sizeElementPacket = sizeElementpacket;
    }

    public void setId(short id) {
        this.id = id;
    }
    public void setCount(short count) {
        this.count = count;
    }
    public void setSizeElementPacket(short sizeElementPacket) {
        this.sizeElementPacket = sizeElementPacket;
    }
    public short getId() {
        return id;
    }
    public short getCount() {
        return count;
    }
    public short getSizeElementPacket() {
        return sizeElementPacket;
    }

    @Override
    public String toString(){
        return "InfoPacket{id='"+id+"', count='"+count+"', sizeElementPacket='"+sizeElementPacket+"'}";
    }
}
