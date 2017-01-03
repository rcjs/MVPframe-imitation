package me.jessyan.mvparms.demo.mvp.contract;

import com.jess.arms.mvp.BaseView;
import com.jess.arms.mvp.IModel;

import java.util.ArrayList;

import me.jessyan.mvparms.demo.app.entity.meizi.Gank;
import me.jessyan.mvparms.demo.app.entity.meizi.Meizi;
import me.jessyan.mvparms.demo.app.entity.meizi.MeiziData;
import me.jessyan.mvparms.demo.app.entity.meizi.VedioData;
import rx.Observable;

/**
 * Created by zjl on 2016/12/23.
 */

public interface MeiziContract {
    //对于经常使用的关于UI的方法可以定义到BaseView中,如显示隐藏进度条,和显示文字消息
    interface View extends BaseView {
        void updateMeiziData(ArrayList<Meizi> list);
        void updateVedioData(ArrayList<Gank> list);

        void startLoadMore();
        void endLoadMore();
        void showError(String error);
    }
    //Model层定义接口,外部只需关心model返回的数据,无需关心内部细节,及是否使用缓存
    interface Model extends IModel {

        Observable<MeiziData> getMeizhiData(int page);

        Observable<VedioData> getVedioData(int page);
    }
}
