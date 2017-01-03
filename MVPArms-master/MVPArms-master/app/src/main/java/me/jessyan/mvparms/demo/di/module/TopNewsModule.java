package me.jessyan.mvparms.demo.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;
import me.jessyan.mvparms.demo.app.api.cache.CacheManager;
import me.jessyan.mvparms.demo.app.api.service.ServiceManager;
import me.jessyan.mvparms.demo.mvp.contract.TopNewsContract;
import me.jessyan.mvparms.demo.mvp.model.TopNewsModel;

/**
 * Created by zjl on 2016/12/27.
 */
@Module
public class TopNewsModule {
    private TopNewsContract.View view;

    /**
     * 构建UserModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     * @param view
     */
    public TopNewsModule(TopNewsContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    TopNewsContract.View provideTopNewsView(){
        return this.view;
    }

    @ActivityScope
    @Provides
    TopNewsContract.Model provideTopNewsModel(ServiceManager serviceManager, CacheManager cacheManager){
        return new TopNewsModel(serviceManager,cacheManager);
    }
}
