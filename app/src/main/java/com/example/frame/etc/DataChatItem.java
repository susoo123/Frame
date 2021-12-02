package com.example.frame.etc;

public class DataChatItem {


        private String item_chat_content;
        private String item_chat_user;
        private String item_chat_time;
        private String receiver;
        private int viewType;

        public DataChatItem(String item_chat_content, String item_chat_user , String item_chat_time,String receiver, int viewType) {
            this.item_chat_content = item_chat_content;
            this.item_chat_user = item_chat_user;
            this.item_chat_time = item_chat_time;
            this.receiver=receiver;
            this.viewType = viewType;
        }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getItem_chat_content() {
        return item_chat_content;
    }

    public void setItem_chat_content(String item_chat_content) {
        this.item_chat_content = item_chat_content;
    }

    public String getItem_chat_user() {
        return item_chat_user;
    }

    public void setItem_chat_user(String item_chat_user) {
        this.item_chat_user = item_chat_user;
    }

    public String getItem_chat_time() {
        return item_chat_time;
    }

    public void setItem_chat_time(String item_chat_time) {
        this.item_chat_time = item_chat_time;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public int getViewType() {
            return viewType;
        }


}
