package com.example.frame.etc;
//import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



//@Generated("jsonschema2pojo")
public class DataResult {

        @SerializedName("seq")
        @Expose
        private String seq;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("startDate")
        @Expose
        private String startDate;
        @SerializedName("endDate")
        @Expose
        private String endDate;
        @SerializedName("place")
        @Expose
        private String place;
        @SerializedName("realmName")
        @Expose
        private String realmName;
        @SerializedName("area")
        @Expose
        private String area;
        @SerializedName("thumbnail")
        @Expose
        private String thumbnail;
        @SerializedName("gpsX")
        @Expose
        private String gpsX;
        @SerializedName("gpsY")
        @Expose
        private String gpsY;

        public String getSeq() {
            return seq;
        }

        public void setSeq(String seq) {
            this.seq = seq;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getPlace() {
            return place;
        }

        public void setPlace(String place) {
            this.place = place;
        }

        public String getRealmName() {
            return realmName;
        }

        public void setRealmName(String realmName) {
            this.realmName = realmName;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getGpsX() {
            return gpsX;
        }

        public void setGpsX(String gpsX) {
            this.gpsX = gpsX;
        }

        public String getGpsY() {
            return gpsY;
        }

        public void setGpsY(String gpsY) {
            this.gpsY = gpsY;
        }

    }

