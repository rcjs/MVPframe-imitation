package me.jessyan.mvparms.demo.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;
import me.jessyan.mvparms.demo.app.api.cache.CacheManager;
import me.jessyan.mvparms.demo.app.api.service.ServiceManager;
import me.jessyan.mvparms.demo.mvp.contract.MeiziPhotoDescribeContract;
import me.jessyan.mvparms.demo.mvp.model.MeiziPhotoDescribeModel;

/**
 * Created by zjl on 2016/12/27.
 */
@Module
public class MeiziPhotoDescribeModule {
    private MeiziPhotoDescribeContract.View view;

    /**
     * 构建MeiziPhotoDescribeModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     * @param view
     */
    public MeiziPhotoDescribeModule(MeiziPhotoDescribeContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    MeiziPhotoDescribeContract.View provideMeiziPhotoDescribeView(){
        return this.view;
    }

    @ActivityScope
    @Provides
    MeiziPhotoDescribeContract.Model provideMeiziPhotoDescribeModel(ServiceManager serviceManager, CacheManager cacheManager){
        return new MeiziPhotoDescribeModel(serviceManager,cacheManager);
    }
}
