package me.jessyan.mvparms.demo.mvp.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.util.SimpleArrayMap;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.utils.UiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.jessyan.mvparms.demo.R;
import me.jessyan.mvparms.demo.di.component.AppComponent;
import me.jessyan.mvparms.demo.di.component.DaggerMainComponent;
import me.jessyan.mvparms.demo.di.module.MainModule;
import me.jessyan.mvparms.demo.mvp.contract.MainContract;
import me.jessyan.mvparms.demo.mvp.presenter.MainPresenter;
import me.jessyan.mvparms.demo.mvp.ui.common.WEActivity;
import me.jessyan.mvparms.demo.mvp.ui.fragment.MeiziFragment;
import me.jessyan.mvparms.demo.mvp.ui.fragment.TopNewsFragment;
import me.jessyan.mvparms.demo.mvp.ui.fragment.ZhihuFragment;
import me.jessyan.mvparms.demo.utils.AnimUtils;
import me.jessyan.mvparms.demo.utils.SharePreferenceUtil;
import me.jessyan.mvparms.demo.utils.ViewUtils;

import static me.jessyan.mvparms.demo.R.id.drawer;

/**
 * Created by zjl on 2016/12/26.
 */

public class MainActivity extends WEActivity<MainPresenter> implements MainContract.View {
    MenuItem currentMenuItem;
    Fragment currentFragment;

    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer)
    DrawerLayout drawer;

    SimpleArrayMap<Integer, String> mTitleArrayMap = new SimpleArrayMap<>();
    int mainColor;
    long exitTime = 0;
    int nevigationId;

    @Override
    protected View initView() {
        return LayoutInflater.from(this).inflate(R.layout.main_layout, null, false);
    }


    @Override
    protected void initData() {

    }

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);
        //当前版本大于Lollipop（api level 21）时能有动画
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animateToolbar();
        }
        addfragmentsAndTitle();

        /* View类提供了setSystemUiVisibility和getSystemUiVisibility方法，这两个方法实现对状态栏的动态显示或隐藏的操作，以及获取状态栏当前可见性。
   setSystemUiVisibility(int visibility)方法可传入的实参为：
    1. View.SYSTEM_UI_FLAG_VISIBLE：显示状态栏，Activity不全屏显示(恢复到有状态的正常情况)。
    2. View.INVISIBLE：隐藏状态栏，同时Activity会伸展全屏显示。
    3. View.SYSTEM_UI_FLAG_FULLSCREEN：Activity全屏显示，且状态栏被隐藏覆盖掉。
    4. View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN：Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住。
    5. View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION：效果同View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    6. View.SYSTEM_UI_LAYOUT_FLAGS：效果同View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    7. View.SYSTEM_UI_FLAG_HIDE_NAVIGATION：隐藏虚拟按键(导航栏)。有些手机会用虚拟按键来代替物理按键。
    8. View.SYSTEM_UI_FLAG_LOW_PROFILE：状态栏显示处于低能显示状态(low profile模式)，状态栏上一些图标显示会被隐藏。*/
        drawer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        if (savedInstanceState == null) {
            nevigationId = SharePreferenceUtil.getNevigationItem(this);
            if (nevigationId != -1) {
                currentMenuItem = navView.getMenu().findItem(nevigationId);
            }
            if (currentMenuItem == null) {
                currentMenuItem = navView.getMenu().findItem(R.id.zhihuitem);
            }
            if (currentMenuItem != null) {
                currentMenuItem.setChecked(true);
                Fragment fragment = getFragmentById(currentMenuItem.getItemId());
                String title = mTitleArrayMap.get((Integer) currentMenuItem.getItemId());
                if (fragment != null) {
                    switchFragment(fragment, title);
                }
            }
        } else {
            if (currentMenuItem != null) {
                Fragment fragment = getFragmentById(currentMenuItem.getItemId());
                String title = mTitleArrayMap.get((Integer) currentMenuItem.getItemId());
                if (fragment != null) {
                    switchFragment(fragment, title);
                }
            } else {
                switchFragment(new ZhihuFragment(), "");
                currentMenuItem = navView.getMenu().findItem(R.id.zhihuitem);

            }

        }

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (currentMenuItem != item && currentMenuItem != null) {
                    currentMenuItem.setChecked(false);
                    int id = item.getItemId();
                    SharePreferenceUtil.putNevigationItem(MainActivity.this, id);//存放当前选择显示页
                    currentMenuItem = item;
                    currentMenuItem.setChecked(true);
                    switchFragment(getFragmentById(currentMenuItem.getItemId()), mTitleArrayMap.get((Integer) currentMenuItem.getItemId()));
                }
                drawer.closeDrawer(GravityCompat.END,true);
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            drawer.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @SuppressLint("NewApi")
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    ViewGroup.MarginLayoutParams lpToolbar = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
                    lpToolbar.topMargin += insets.getSystemWindowInsetTop();
                    lpToolbar.rightMargin += insets.getSystemWindowInsetRight();
                    toolbar.setLayoutParams(lpToolbar);

                    mFragmentContainer.setPadding(mFragmentContainer.getPaddingLeft(),
                            insets.getSystemWindowInsetTop() + ViewUtils.getActionBarSize(MainActivity.this),
                            mFragmentContainer.getPaddingRight() + insets.getSystemWindowInsetRight(),
                            mFragmentContainer.getPaddingBottom() + insets.getSystemWindowInsetBottom()
                    );

                    View statusBarBackground = findViewById(R.id.status_bar_background);
                    FrameLayout.LayoutParams lpStatus = (FrameLayout.LayoutParams) statusBarBackground.getLayoutParams();
                    lpStatus.height = insets.getSystemWindowInsetTop();
                    statusBarBackground.setLayoutParams(lpStatus);
                    drawer.setOnApplyWindowInsetsListener(null);
                    return insets.consumeStableInsets();
                }
            });
        }

        int[][] state = new int[][]{
                new int[]{-android.R.attr.state_checked}, //unchecked
                new int[]{android.R.attr.state_checked}  //pressed
        };

        int[] color = new int[]{
                Color.BLACK, Color.BLACK};
        int[] iconcolor = new int[]{
                Color.GRAY, Color.BLACK};
        navView.setItemTextColor(new ColorStateList(state, color));
        navView.setItemIconTintList(new ColorStateList(state, iconcolor));
    }

    private void addfragmentsAndTitle() {
        mTitleArrayMap.put(R.id.zhihuitem, getResources().getString(R.string.zhihu));
        mTitleArrayMap.put(R.id.topnewsitem, getResources().getString(R.string.topnews));
        mTitleArrayMap.put(R.id.meiziitem, getResources().getString(R.string.meizi));
    }

    //Toolbar动画效果
    private void animateToolbar() {
        View t = toolbar.getChildAt(0);
        if (t != null && t instanceof TextView) {
            TextView title = (TextView) t;

            title.setAlpha(0f);
            title.setScaleX(0.8f);

            title.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .setStartDelay(500)
                    .setDuration(900)
                    .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(this)).start();
        }
        View amv = toolbar.getChildAt(1);
        if (amv != null & amv instanceof ActionMenuView) {
            ActionMenuView actions = (ActionMenuView) amv;
            popAnim(actions.getChildAt(0), 500, 200); // filter
            popAnim(actions.getChildAt(1), 700, 200); // overflow
        }
    }

    private void popAnim(View v, int startDelay, int duration) {
        if (v != null) {
            v.setAlpha(0f);
            v.setScaleX(0f);
            v.setScaleY(0f);

            v.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(startDelay)
                    .setDuration(duration)
                    .setInterpolator(AnimationUtils.loadInterpolator(this,
                            android.R.interpolator.overshoot)).start();
        }
    }

    private void switchFragment(Fragment fragment, String title) {

        if (currentFragment == null || !currentFragment.getClass().getName().equals(fragment.getClass().getName()))
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
                    .commit();
        currentFragment = fragment;

    }

    private Fragment getFragmentById(int id) {
        Fragment fragment = null;
        switch (id) {
            case R.id.zhihuitem:
                fragment = new ZhihuFragment();
                break;
           case R.id.topnewsitem:
                fragment=new TopNewsFragment();
                break;
            case R.id.meiziitem:
                fragment=new MeiziFragment();
                break;

        }
        return fragment;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

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


    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder().appComponent(appComponent).mainModule(new MainModule(this)).build().inject(this);
    }

    private void goAboutActivity() {
        Intent intent=new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        //END即gravity.right 从右向左显示   START即left  从左向右弹出显示
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再点一次，退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
        }
    }

    //    when recycle view scroll bottom,need loading more date and show the more view.
    public interface LoadingMore {

        void loadingStart();

        void loadingfinish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_open:
                    drawer.openDrawer(GravityCompat.END);
                    break;
                case R.id.menu_about:
                    goAboutActivity();
                    break;
            }
            return true;
        }
    };
}
