package com.example.pocketdrabbles;

public class Story {

    private String title;
    private String description;
    private String cover;

    public Story() {
    }

    public Story(String title, String description, String coverImage) {
        this.title = title;
        this.description = description;
        this.cover = coverImage;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getCover() {
        return cover;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
