package me.jessyan.mvparms.demo.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;
import me.jessyan.mvparms.demo.di.module.ZhihuDescribeModule;
import me.jessyan.mvparms.demo.mvp.ui.activity.ZhihuDescribeActivity;

/**
 * Created by zjl on 2016/12/27.
 */
@ActivityScope
@Component(modules = ZhihuDescribeModule.class,dependencies = AppComponent.class)
public interface ZhihuDescribeComponent {
    void inject(ZhihuDescribeActivity activity);
}
