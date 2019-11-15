package com.example.dugarolo;

import org.joda.time.LocalDate;
public class Request implements Comparable<Request>{

    private String name;
    private String status;
    private LocalDate date;

    public Request(String name, LocalDate localDate, String status) {
        this.name = name;
        this.status = status;
        this.date = localDate;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String toString(){
        return this.name;
    }

    @Override
    public int compareTo(Request o) {
       return this.date.toDate().compareTo(o.getDate().toDate());
    }
}
