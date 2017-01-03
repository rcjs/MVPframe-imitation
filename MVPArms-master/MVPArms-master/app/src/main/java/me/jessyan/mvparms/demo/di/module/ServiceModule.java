package me.jessyan.mvparms.demo.di.module;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.jessyan.mvparms.demo.app.api.service.CommonService;
import me.jessyan.mvparms.demo.app.api.service.GankApi;
import me.jessyan.mvparms.demo.app.api.service.TopNewsApi;
import me.jessyan.mvparms.demo.app.api.service.UserService;
import me.jessyan.mvparms.demo.app.api.service.ZhihuApi;
import retrofit2.Retrofit;

/**
 * Created by zhiyicx on 2016/3/30.
 */
@Module
public class ServiceModule {

    @Singleton
    @Provides
    CommonService provideCommonService(Retrofit retrofit) {
        return retrofit.create(CommonService.class);
    }

    @Singleton
    @Provides
    UserService provideUserService(Retrofit retrofit) {
        return retrofit.create(UserService.class);
    }

    @Singleton
    @Provides
    GankApi provideGankApi(Retrofit retrofit) {
        return retrofit.create(GankApi.class);
    }

    @Singleton
    @Provides
    ZhihuApi provideZhihuApi(Retrofit retrofit) {
        return retrofit.create(ZhihuApi.class);
    }


    @Singleton
    @Provides
    TopNewsApi provideTopNewsApi(Retrofit retrofit) {
        return retrofit.create(TopNewsApi.class);
    }

}
