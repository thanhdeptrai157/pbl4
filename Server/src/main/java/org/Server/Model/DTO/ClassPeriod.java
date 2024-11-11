package org.Server.Model.DTO;

import java.sql.Date;

public class ClassPeriod {
    private int id_class_period;
    private int id_class;
    private Date time_start;
    private Date time_end;

    public ClassPeriod(){}

    public ClassPeriod(int id_class_period, int id_class, Date time_start, Date time_end){
        this.id_class = id_class;
        this.id_class_period = id_class_period;
        this.time_start = time_start;
        this.time_end = time_end;
    }

    public void setId_class(int id_class) {
        this.id_class = id_class;
    }
    public int getId_class() {
        return id_class;
    }
    public void setTime_end(Date time_end) {
        this.time_end = time_end;
    }
    public Date getTime_end() {
        return time_end;
    }
    public void setTime_start(Date time_start) {
        this.time_start = time_start;
    }
    public Date getTime_start() {
        return time_start;
    }
    public void setId_class_period(int id_class_period) {
        this.id_class_period = id_class_period;
    }
    public int getId_class_period() {
        return id_class_period;
    }

}
