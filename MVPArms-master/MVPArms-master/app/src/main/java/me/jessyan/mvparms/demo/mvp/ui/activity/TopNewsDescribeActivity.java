package me.jessyan.mvparms.demo.mvp.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jess.arms.utils.UiUtils;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.mvparms.demo.R;
import me.jessyan.mvparms.demo.app.entity.news.NewsDetailBean;
import me.jessyan.mvparms.demo.di.component.AppComponent;
import me.jessyan.mvparms.demo.di.component.DaggerTopNewsDescribeComponent;
import me.jessyan.mvparms.demo.di.module.TopNewsDescribeModule;
import me.jessyan.mvparms.demo.mvp.contract.TopNewsDescribeContract;
import me.jessyan.mvparms.demo.mvp.presenter.TopNewsDescribePresenter;
import me.jessyan.mvparms.demo.mvp.ui.common.WEActivity;
import me.jessyan.mvparms.demo.utils.AnimUtils;
import me.jessyan.mvparms.demo.utils.ColorUtils;
import me.jessyan.mvparms.demo.utils.DensityUtil;
import me.jessyan.mvparms.demo.utils.GlideUtils;
import me.jessyan.mvparms.demo.utils.ViewUtils;
import me.jessyan.mvparms.demo.widget.ElasticDragDismissFrameLayout;
import me.jessyan.mvparms.demo.widget.ParallaxScrimageView;
import me.jessyan.mvparms.demo.widget.TranslateYTextView;

/**
 * Created by zjl on 2016/12/28.
 */

public class TopNewsDescribeActivity extends WEActivity<TopNewsDescribePresenter> implements TopNewsDescribeContract.View {
    private static final float SCRIM_ADJUSTMENT = 0.075f;
    int[] mDeviceInfo;
    int width;
    int heigh;
    @BindView(R.id.progress)
    ProgressBar mProgress;
    @BindView(R.id.htNewsContent)
    HtmlTextView mHtNewsContent;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.draggable_frame)
    ElasticDragDismissFrameLayout mDraggableFrame;
    @BindView(R.id.nest)
    NestedScrollView mNest;
    @BindView(R.id.title)
    TranslateYTextView mTextView;
    @BindView(R.id.shot)
    ParallaxScrimageView mShot;
    private String id;
    private String title;
    private String mImageUrl;
    private ElasticDragDismissFrameLayout.SystemChromeFader chromeFader;
    private NestedScrollView.OnScrollChangeListener scrollListener;
    private Transition.TransitionListener mReturnHomeListener;
    private Transition.TransitionListener mEnterTrasitionListener;

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerTopNewsDescribeComponent.builder().appComponent(appComponent).topNewsDescribeModule(new TopNewsDescribeModule(this)).build().inject(this);
    }

    @Override
    protected View initView() {
        return LayoutInflater.from(this).inflate(R.layout.topnews_describe, null, false);
    }

    @Override
    protected void initData() {
        mDeviceInfo = DensityUtil.getDeviceInfo(this);
        width = mDeviceInfo[0];
        heigh = width * 3 / 4;
        setSupportActionBar(mToolbar);
        initDatas();
        initViews();
        getData();
        enterAnimation();
        chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementReturnTransition().addListener(mReturnHomeListener);
            getWindow().getSharedElementEnterTransition().addListener(mEnterTrasitionListener);
        }

    }

    private void initViews() {
        mNest.setAlpha(0.5f);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNest.smoothScrollTo(0, 0);
            }
        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandImageAndFinish();
            }
        });
    }

    private void getData() {
        mPresenter.getDescrible(id);
    }

    private void initDatas() {

        id = getIntent().getStringExtra("docid");
        title = getIntent().getStringExtra("title");
        mTextView.setText(title);
        mImageUrl = getIntent().getStringExtra("image");
        scrollListener = new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (oldScrollY < 168) {
                    mShot.setOffset(-oldScrollY);
                    mTextView.setOffset(-oldScrollY);
                }
            }
        };
        Glide.with(this)
                .load(mImageUrl)
                .override(width, heigh)
                .listener(glideLoadListener)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mShot);


        mNest.setOnScrollChangeListener(scrollListener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
            mShot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @SuppressLint("NewApi")
                @Override
                public boolean onPreDraw() {
                    mShot.getViewTreeObserver().removeOnPreDrawListener(this);
                    startPostponedEnterTransition();
                    return true;
                }
            });
        }
        mReturnHomeListener =
                new AnimUtils.TransitionListenerAdapter() {
                    @Override
                    public void onTransitionStart(Transition transition) {
                        super.onTransitionStart(transition);
                        // hide the fab as for some reason it jumps position??  TODO work out why
                        mToolbar.animate()
                                .alpha(0f)
                                .setDuration(100)
                                .setInterpolator(new AccelerateInterpolator());
                        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                            mShot.setElevation(1f);
                            mToolbar.setElevation(0f);
                        }
                        mNest.animate()
                                .alpha(0f)
                                .setDuration(50)
                                .setInterpolator(new AccelerateInterpolator());
                    }
                };
        mEnterTrasitionListener =
                new AnimUtils.TransitionListenerAdapter() {
                    @Override
                    public void onTransitionEnd(Transition transition) {
                        super.onTransitionEnd(transition);
//                    解决5.0 shara element bug
                        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100).setDuration(100);
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                            mShot.setOffset((Integer) valueAnimator.getAnimatedValue() * 10);
                                mNest.smoothScrollTo((Integer) valueAnimator.getAnimatedValue() / 10, 0);

                            }
                        });
                        valueAnimator.start();
//                    mShot.setAlpha(0.5f);
//                    mShot.animate().alpha(1f).setDuration(800L).start();
                    }
                    @Override
                    public void onTransitionResume(Transition transition) {
                        super.onTransitionResume(transition);

                    }
                };
    }

    @Override
    public void upListItem(NewsDetailBean newsList) {
        mProgress.setVisibility(View.INVISIBLE);
        mHtNewsContent.setHtmlFromString(newsList.getBody(), new HtmlTextView.LocalImageGetter());
    }

    @Override
    public void showError(String error) {
        Snackbar.make(mDraggableFrame, getString(R.string.snack_infor), Snackbar.LENGTH_INDEFINITE).setAction("重试", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        }).show();
    }

    @Override
    public void showLoading() {
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showMessage(String message) {
        UiUtils.SnackbarText(message);
    }

    @Override
    public void launchActivity(Intent intent) {
        UiUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }


    private void expandImageAndFinish() {
        if (mShot.getOffset() != 0f) {
            Animator expandImage = ObjectAnimator.ofFloat(mShot, ParallaxScrimageView.OFFSET, 0f);
            expandImage.setDuration(80);
            expandImage.setInterpolator(new AccelerateInterpolator());
            expandImage.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        finishAfterTransition();
                    } else {
                        finish();
                    }
                }
            });
            expandImage.start();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        expandImageAndFinish();

    }

    @OnClick(R.id.shot)
    public void onClick() {
        mNest.smoothScrollTo(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mDraggableFrame.addListener(chromeFader);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mDraggableFrame.removeListener(chromeFader);

    }

    @Override
    protected void onDestroy() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementReturnTransition().removeListener(mReturnHomeListener);
            getWindow().getSharedElementEnterTransition().removeListener(mEnterTrasitionListener);
        }

        super.onDestroy();

    }

    private void enterAnimation() {
        float offSet = mToolbar.getHeight();
        LinearInterpolator interpolator = new LinearInterpolator();
        AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
        viewEnterAnimation(mToolbar, offSet, interpolator);
        viewEnterAnimationNest(mNest, 0f, accelerateInterpolator);

    }

    private void viewEnterAnimation(View view, float offset, Interpolator interp) {
        view.setTranslationY(-offset);
        view.setAlpha(0.6f);
        view.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600L)
                .setInterpolator(interp)
                .setListener(null)
                .start();
    }

    private void viewEnterAnimationNest(View view, float offset, Interpolator interp) {
        view.setTranslationY(-offset);
        view.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(50L)
                .setInterpolator(interp)
                .setListener(null)
                .start();
    }

    private RequestListener glideLoadListener = new RequestListener<String, GlideDrawable>() {
        @SuppressLint("NewApi")
        @Override
        public boolean onResourceReady(GlideDrawable resource, String model,
                                       Target<GlideDrawable> target, boolean isFromMemoryCache,
                                       boolean isFirstResource) {
            final Bitmap bitmap = GlideUtils.getBitmap(resource);
            final int twentyFourDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    24, TopNewsDescribeActivity.this.getResources().getDisplayMetrics());
            Palette.from(bitmap)
                    .maximumColorCount(3)
                    .clearFilters() /* by default palette ignore certain hues
                        (e.g. pure black/white) but we don't want this. */
                    .setRegion(0, 0, bitmap.getWidth() - 1, twentyFourDip) /* - 1 to work around
                        https://code.google.com/p/android/issues/detail?id=191013 */
                    .generate(new Palette.PaletteAsyncListener() {
                        @SuppressLint("NewApi")
                        @Override
                        public void onGenerated(Palette palette) {
                            boolean isDark;
                            @ColorUtils.Lightness int lightness = ColorUtils.isDark(palette);
                            if (lightness == ColorUtils.LIGHTNESS_UNKNOWN) {
                                isDark = ColorUtils.isDark(bitmap, bitmap.getWidth() / 2, 0);
                            } else {
                                isDark = lightness == ColorUtils.IS_DARK;
                            }
                            // color the status bar. Set a complementary dark color on L,
                            // light or dark color on M (with matching status bar icons)
                              int statusBarColor = getWindow().getStatusBarColor();
                            final Palette.Swatch topColor =
                                    ColorUtils.getMostPopulousSwatch(palette);
                            if (topColor != null &&
                                    (isDark || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                                statusBarColor = ColorUtils.scrimify(topColor.getRgb(),
                                        isDark, SCRIM_ADJUSTMENT);
                                // set a light status bar on M+
                                if (!isDark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    ViewUtils.setLightStatusBar(mShot);
                                }
                            }

                            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {

                                if (statusBarColor != getWindow().getStatusBarColor()) {
                                    mShot.setScrimColor(statusBarColor);
                                    ValueAnimator statusBarColorAnim = ValueAnimator.ofArgb(
                                            getWindow().getStatusBarColor(), statusBarColor);
                                    statusBarColorAnim.addUpdateListener(new ValueAnimator
                                            .AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            getWindow().setStatusBarColor(
                                                    (int) animation.getAnimatedValue());
                                        }
                                    });
                                    statusBarColorAnim.setDuration(1000L);
                                    statusBarColorAnim.setInterpolator(
                                            new AccelerateInterpolator());
                                    statusBarColorAnim.start();
                                }
                            }
                        }
                    });

            Palette.from(bitmap)
                    .clearFilters()
                    .generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {

                            // slightly more opaque ripple on the pinned image to compensate
                            // for the scrim
                            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                                mShot.setForeground(ViewUtils.createRipple(palette, 0.3f, 0.6f,
                                        ContextCompat.getColor(TopNewsDescribeActivity.this, R.color.mid_grey),
                                        true));
                            }

                        }
                    });

            // TODO should keep the background if the image contains transparency?!
            mShot.setBackground(null);
            return false;
        }

        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                   boolean isFirstResource) {
            return false;
        }
    };
}
