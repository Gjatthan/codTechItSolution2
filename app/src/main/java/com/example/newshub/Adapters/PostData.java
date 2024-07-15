package com.example.newshub.Adapters;

public class PostData {
    public String uimgurl,uid,date,incimgurl,title,description,parent;

    public PostData(String uimgurl, String uid, String date, String incimgurl, String title, String description,String parent) {
        this.uimgurl = uimgurl;
        this.uid = uid;
        this.date = date;
        this.incimgurl = incimgurl;
        this.title = title;
        this.description = description;
        this.parent=parent;
    }
}
