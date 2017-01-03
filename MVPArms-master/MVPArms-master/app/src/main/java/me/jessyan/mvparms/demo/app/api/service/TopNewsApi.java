package me.jessyan.mvparms.demo.app.api.service;


import me.jessyan.mvparms.demo.app.entity.news.NewsList;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by zjl on 2016/11/21.
 */
public interface TopNewsApi {


    @GET("http://c.m.163.com/nc/article/headline/T1348647909107/{id}-20.html")
    Observable<NewsList> getNews(@Path("id") int id);

    @GET("http://c.m.163.com/nc/article/{id}/full.html")
    Observable<String> getNewsDetail(@Path("id") String id);

}
