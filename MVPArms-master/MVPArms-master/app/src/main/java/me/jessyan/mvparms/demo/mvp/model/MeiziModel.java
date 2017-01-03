package me.jessyan.mvparms.demo.mvp.model;

import com.jess.arms.mvp.BaseModel;

import java.util.List;

import io.rx_cache.DynamicKey;
import io.rx_cache.EvictDynamicKey;
import io.rx_cache.Reply;
import me.jessyan.mvparms.demo.app.api.Api;
import me.jessyan.mvparms.demo.app.api.cache.CacheManager;
import me.jessyan.mvparms.demo.app.api.service.ServiceManager;
import me.jessyan.mvparms.demo.app.entity.User;
import me.jessyan.mvparms.demo.app.entity.meizi.MeiziData;
import me.jessyan.mvparms.demo.app.entity.meizi.VedioData;
import me.jessyan.mvparms.demo.mvp.contract.MeiziContract;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static me.jessyan.mvparms.demo.mvp.model.UserModel.USERS_PER_PAGE;

/**
 * Created by zjl on 2016/12/23.
 */

public class MeiziModel extends BaseModel<ServiceManager, CacheManager> implements MeiziContract.Model {


    public MeiziModel(ServiceManager serviceManager, CacheManager cacheManager) {
        super(serviceManager, cacheManager);
    }


    @Override
    public Observable<MeiziData> getMeizhiData(int page) {
        return  mServiceManager.getGankApi().getMeizhiData(page);
    }

    @Override
    public Observable<VedioData> getVedioData(int page) {
        return  mServiceManager.getGankApi().getVedioData(page);
    }
}
