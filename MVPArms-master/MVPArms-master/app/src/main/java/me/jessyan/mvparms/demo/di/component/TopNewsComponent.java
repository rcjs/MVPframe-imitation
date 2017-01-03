package me.jessyan.mvparms.demo.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;
import me.jessyan.mvparms.demo.di.module.TopNewsModule;
import me.jessyan.mvparms.demo.di.module.ZhihuModule;
import me.jessyan.mvparms.demo.mvp.ui.fragment.TopNewsFragment;
import me.jessyan.mvparms.demo.mvp.ui.fragment.ZhihuFragment;

/**
 * Created by zjl on 2016/12/27.
 */
@ActivityScope
@Component(modules =TopNewsModule.class,dependencies = AppComponent.class)
public interface TopNewsComponent {
    void inject(TopNewsFragment fragment);
}
