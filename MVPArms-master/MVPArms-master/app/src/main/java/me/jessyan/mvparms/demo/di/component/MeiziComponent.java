package me.jessyan.mvparms.demo.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;
import me.jessyan.mvparms.demo.di.module.MeiziModule;
import me.jessyan.mvparms.demo.mvp.ui.fragment.MeiziFragment;

/**
 * Created by zjl on 2016/12/23.
 */
@ActivityScope
@Component(modules = MeiziModule.class,dependencies = AppComponent.class)
public interface MeiziComponent {
    void inject(MeiziFragment fragment);
}
