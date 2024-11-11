package org.Server.Model.DTO;

import java.sql.Date;

public class HistoryWeb {
    private int id_class_period;
    private int id_computer;
    private String url;
    private Date time_search;

    public HistoryWeb(){}

    public HistoryWeb(int id_class_period, int id_computer, String url, Date time_search){
        this.id_class_period = id_class_period;
        this.id_computer = id_computer;
        this.url = url;
        this.time_search = time_search;
    }

    public void setId_class_period(int id_class_period) {
        this.id_class_period = id_class_period;
    }
    public int getId_class_period() {
        return id_class_period;
    }
    public void setId_computer(int id_computer) {
        this.id_computer = id_computer;
    }
    public int getId_computer() {
        return id_computer;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }
    public void setTime_search(Date time_search) {
        this.time_search = time_search;
    }
    public Date getTime_search() {
        return time_search;
    }

}
