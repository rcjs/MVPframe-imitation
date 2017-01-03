package me.jessyan.mvparms.demo.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;
import me.jessyan.mvparms.demo.app.api.cache.CacheManager;
import me.jessyan.mvparms.demo.app.api.service.ServiceManager;
import me.jessyan.mvparms.demo.mvp.contract.MeiziContract;
import me.jessyan.mvparms.demo.mvp.model.MeiziModel;

/**
 * Created by zjl on 2016/12/23.
 */
@Module
public class MeiziModule {
    private MeiziContract.View view;

    /**
     * 构建MeiziModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     * @param view
     */
    public MeiziModule(MeiziContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    MeiziContract.View provideMeiziView(){
        return this.view;
    }

    @ActivityScope
    @Provides
    MeiziContract.Model provideMeiziModel(ServiceManager serviceManager, CacheManager cacheManager){
        return new MeiziModel(serviceManager,cacheManager);
    }
}
