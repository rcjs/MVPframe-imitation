package me.jessyan.mvparms.demo.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;
import me.jessyan.mvparms.demo.di.module.MeiziPhotoDescribeModule;
import me.jessyan.mvparms.demo.mvp.ui.activity.MeiziPhotoDescribeActivity;

/**
 * Created by zjl on 2016/12/27.
 */
@ActivityScope
@Component(modules = MeiziPhotoDescribeModule.class,dependencies = AppComponent.class)
public interface MeiziPhotoDescribeComponent {
    void inject(MeiziPhotoDescribeActivity activity);
}
