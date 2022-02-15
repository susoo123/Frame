package com.example.frame.etc;

import java.io.Serializable;
import java.util.ArrayList;

public class DataTicket implements Serializable {

    //private String title;
    private String event_id_ticket;
    private String event_start_date_ticket;
    private String event_end_date_ticket;
    private ArrayList<DataFeedImg> ticket_img_ticket;
    private String event_title_ticket;


    public DataTicket(String event_id_ticket, String event_start_date_ticket, String event_end_date_ticket, ArrayList<DataFeedImg> ticket_img_ticket, String event_title_ticket) {
        this.event_id_ticket = event_id_ticket;
        this.event_start_date_ticket = event_start_date_ticket;
        this.event_end_date_ticket = event_end_date_ticket;
        this.ticket_img_ticket = ticket_img_ticket;
        this.event_title_ticket = event_title_ticket;
    }

    public String getEvent_id_ticket() {
        return event_id_ticket;
    }

    public void setEvent_id_ticket(String event_id_ticket) {
        this.event_id_ticket = event_id_ticket;
    }

    public String getEvent_start_date_ticket() {
        return event_start_date_ticket;
    }

    public void setEvent_start_date_ticket(String event_start_date_ticket) {
        this.event_start_date_ticket = event_start_date_ticket;
    }

    public String getEvent_end_date_ticket() {
        return event_end_date_ticket;
    }

    public void setEvent_end_date_ticket(String event_end_date_ticket) {
        this.event_end_date_ticket = event_end_date_ticket;
    }


    public String getEvent_title_ticket() {
        return event_title_ticket;
    }

    public void setEvent_title_ticket(String event_title_ticket) {
        this.event_title_ticket = event_title_ticket;
    }

    public ArrayList<DataFeedImg> getTicket_img_ticket() {
        return ticket_img_ticket;
    }

    public void setTicket_img_ticket(ArrayList<DataFeedImg> ticket_img_ticket) {
        this.ticket_img_ticket = ticket_img_ticket;
    }
}
