package me.jessyan.mvparms.demo.app.entity.news;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by xinghongfei on 16/8/17.
 */
public class NewsList {

    @SerializedName("T1348647909107")
    ArrayList<NewsBean> newsList;
    public ArrayList<NewsBean> getNewsList() {
        return newsList;
    }
    public void setNewsList(ArrayList<NewsBean> newsList) {
        this.newsList = newsList;
    }
}
