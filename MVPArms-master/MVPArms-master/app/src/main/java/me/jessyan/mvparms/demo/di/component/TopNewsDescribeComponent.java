package me.jessyan.mvparms.demo.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;
import me.jessyan.mvparms.demo.di.module.TopNewsDescribeModule;
import me.jessyan.mvparms.demo.mvp.ui.activity.TopNewsDescribeActivity;

/**
 * Created by zjl on 2016/12/27.
 */
@ActivityScope
@Component(modules = TopNewsDescribeModule.class,dependencies = AppComponent.class)
public interface TopNewsDescribeComponent {
    void inject(TopNewsDescribeActivity activity);
}
