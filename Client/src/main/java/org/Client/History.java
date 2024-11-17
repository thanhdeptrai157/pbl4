package org.Client;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class History {
    private String date;
    private String url;

    public History() {}
    public History(String date, String url) {
        this.date = date;
        this.url = url;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        ZonedDateTime dateTime = ZonedDateTime.parse(date.trim().substring(6), formatter);
        DateTimeFormatter shortFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(shortFormatter);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        String urlTmp = url.substring(36);
        if(urlTmp.length() > 2 ){
            urlTmp = urlTmp.substring(0, urlTmp.length()-3);
        }
        return urlTmp;
    }

    @Override
    public String toString() {
        return "History{" + "date=" + date + ", url=" + url + '}';
    }
}
