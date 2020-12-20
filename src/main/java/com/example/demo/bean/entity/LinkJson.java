package com.example.demo.bean.entity;

public class LinkJson {

    private String link;
    private String path;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LinkJson(String link, String path) {
        this.link = link;
        this.path = path;
    }
}
