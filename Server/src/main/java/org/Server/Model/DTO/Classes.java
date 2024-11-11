package org.Server.Model.DTO;

public class Classes {
    private int id_class;
    private String name_class;
    private String password;

    public Classes(){}

    public Classes(int id_class, String name_class, String password){
        this.id_class = id_class;
        this.name_class = name_class;
        this.password = password;
    }

    public void setId_class(int id_class) {
        this.id_class = id_class;
    }
    public int getId_class() {
        return id_class;
    }
    public void setName_class(String name_class) {
        this.name_class = name_class;
    }
    public String getName_class() {
        return name_class;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }

}
