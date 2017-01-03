package me.jessyan.mvparms.demo.mvp.presenter;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.jess.arms.base.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;

import javax.inject.Inject;

import me.jessyan.mvparms.demo.mvp.contract.MeiziPhotoDescribeContract;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;

/**
 * Created by zjl on 2016/12/27.
 */

public class MeiziPhotoDescribePresenter extends BasePresenter<MeiziPhotoDescribeContract.Model, MeiziPhotoDescribeContract.View> {

    private RxErrorHandler mErrorHandler;
    private RxPermissions mRxPermissions;
    private AppManager mAppManager;
    private Application mApplication;

    @Inject
    public MeiziPhotoDescribePresenter(MeiziPhotoDescribeContract.Model model, MeiziPhotoDescribeContract.View rootView, RxErrorHandler handler
            , RxPermissions rxPermissions, AppManager appManager, Application application) {
        super(model, rootView);
        this.mApplication = application;
        this.mErrorHandler = handler;
        this.mRxPermissions = rxPermissions;
        this.mAppManager = appManager;
    }


    public void setupPhotoAttacher() {
        mRootView.setupPhotoAttacher();
    }


    public void saveImage(Bitmap drawingCache, Context context, View view) {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File directory = new File(externalStorageDirectory, "LookLook");
        if (!directory.exists()) {
            directory.mkdir();
            try {
                File file = new File(directory, new Date().getTime() + ".jpg");
                FileOutputStream fos = new FileOutputStream(file);
                drawingCache.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(file);
                intent.setData(uri);
                context.sendBroadcast(intent);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Snackbar.make(view, "阿偶出错了呢", Snackbar.LENGTH_SHORT).show();
            }

        }
    }

}
