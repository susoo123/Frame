package com.example.frame.etc;

import java.util.ArrayList;

public class DataEvent {
    private String event_title;
    private String event_contents;
    private String event_start_date;
    private String event_end_date;
    private ArrayList<String> images;
    private String poster;
    private String num_people;
    private String event_id;

    public DataEvent(String poster, String event_title, String event_contents, String event_start_date, String event_end_date, String num_people, String event_id) {
        this.event_title = event_title;
        this.event_contents = event_contents;
        this.event_start_date = event_start_date;
        this.event_end_date = event_end_date;
       // this.images = images;
        this.poster = poster;
        this.num_people = num_people ;
        this.event_id = event_id ;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getNum_people() {
        return num_people;
    }

    public void setNum_people(String num_people) {
        this.num_people = num_people;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getEvent_title() {
        return event_title;
    }

    public void setEvent_title(String event_title) {
        this.event_title = event_title;
    }

    public String getEvent_contents() {
        return event_contents;
    }

    public void setEvent_contents(String event_contents) {
        this.event_contents = event_contents;
    }

    public String getEvent_start_date() {
        return event_start_date;
    }

    public void setEvent_start_date(String event_start_date) {
        this.event_start_date = event_start_date;
    }

    public String getEvent_end_date() {
        return event_end_date;
    }

    public void setEvent_end_date(String event_end_date) {
        this.event_end_date = event_end_date;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}
