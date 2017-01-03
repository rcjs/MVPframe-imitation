package me.jessyan.mvparms.demo.app.api.service;



import me.jessyan.mvparms.demo.app.entity.zhihu.ZhihuDaily;
import me.jessyan.mvparms.demo.app.entity.zhihu.ZhihuStory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;
import rx.Observable;

public interface ZhihuApi {

    @GET("/api/4/news/latest")
    Observable<ZhihuDaily> getLastDaily();

    @GET("/api/4/news/before/{date}")
    Observable<ZhihuDaily> getTheDaily( @Path("date") String date);

    @GET("/api/4/news/{id}")
    Observable<ZhihuStory> getZhihuStory(@Path("id") String id);



}
