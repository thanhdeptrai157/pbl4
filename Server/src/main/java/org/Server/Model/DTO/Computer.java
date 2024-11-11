package org.Server.Model.DTO;

public class Computer {
    private int id_computer;
    private String name_computer;
    private String ip_network;

    public Computer(){}
    
    public Computer(int id_computer, String name_computer, String ip_network){
        this.id_computer = id_computer;
        this.name_computer = name_computer;
        this.ip_network = ip_network;
    }

    public void setId_computer(int id_computer) {
        this.id_computer = id_computer;
    } 
    public int getId_computer() {
        return id_computer;
    }
    public void setIp_network(String ip_network) {
        this.ip_network = ip_network;
    }
    public String getIp_network() {
        return ip_network;
    }
    public void setName_computer(String name_computer) {
        this.name_computer = name_computer;
    }
    public String getName_computer() {
        return name_computer;
    }
}
