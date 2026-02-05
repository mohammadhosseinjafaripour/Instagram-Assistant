package com.jfp.instagram.Model;

public class InstaModel {
    public String is_video;
    public String url;
    public String caption;


    public InstaModel(String is_video, String url, String caption) {
        this.is_video = is_video;
        this.url = url;
        this.caption = caption;
    }

    public InstaModel(String is_video, String url) {
        this.is_video = is_video;
        this.url = url;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getIs_video() {
        return is_video;
    }

    public void setIs_video(String is_video) {
        this.is_video = is_video;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
