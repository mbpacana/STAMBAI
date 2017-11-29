package dao;

public class Announcement {
    String text;

    public Announcement(String announcement) {
        this.text = announcement;
    }

    public String getAnnouncement() {
        return text;
    }

    public void setAnnouncement(String announcement) {
        this.text = announcement;
    }
}
