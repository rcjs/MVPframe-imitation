package me.jessyan.mvparms.demo.mvp.contract;

import com.jess.arms.mvp.BaseView;
import com.jess.arms.mvp.IModel;

import me.jessyan.mvparms.demo.app.entity.meizi.MeiziData;
import me.jessyan.mvparms.demo.app.entity.meizi.VedioData;
import me.jessyan.mvparms.demo.app.entity.zhihu.ZhihuDaily;
import me.jessyan.mvparms.demo.app.entity.zhihu.ZhihuStory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by zjl on 2016/12/27.
 */

public interface ZhihuContract {
    //对于经常使用的关于UI的方法可以定义到BaseView中,如显示隐藏进度条,和显示文字消息
    interface View extends BaseView {
        void updateList(ZhihuDaily zhihuDaily);
        void startLoadMore();
        void endLoadMore();
        void showError(String error);
    }
    //Model层定义接口,外部只需关心model返回的数据,无需关心内部细节,及是否使用缓存
    interface Model extends IModel {

        Observable<ZhihuDaily> getLastDaily();

        Observable<ZhihuDaily> getTheDaily(String date);

    }
}
