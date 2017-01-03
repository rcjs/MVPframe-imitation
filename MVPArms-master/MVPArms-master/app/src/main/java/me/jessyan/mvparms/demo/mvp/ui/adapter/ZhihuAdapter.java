package me.jessyan.mvparms.demo.mvp.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import me.jessyan.mvparms.demo.R;
import me.jessyan.mvparms.demo.app.entity.meizi.Gank;
import me.jessyan.mvparms.demo.app.entity.meizi.Meizi;
import me.jessyan.mvparms.demo.app.entity.zhihu.ZhihuDailyItem;
import me.jessyan.mvparms.demo.dao.ZhihuDao;
import me.jessyan.mvparms.demo.mvp.ui.activity.MainActivity;
import me.jessyan.mvparms.demo.mvp.ui.activity.MeiziPhotoDescribeActivity;
import me.jessyan.mvparms.demo.mvp.ui.activity.ZhihuDescribeActivity;
import me.jessyan.mvparms.demo.utils.DensityUtil;
import me.jessyan.mvparms.demo.utils.DribbbleTarget;
import me.jessyan.mvparms.demo.utils.Help;
import me.jessyan.mvparms.demo.utils.ObservableColorMatrix;
import me.jessyan.mvparms.demo.widget.BadgedFourThreeImageView;


/**
 * Created by zjl on 2016/12/22.
 */

public class ZhihuAdapter extends BaseRecyclerViewAdapter implements MainActivity.LoadingMore{

    private static final int TYPE_LOADING_MORE = -1;
    private static final int NORMAL_ITEM = 1;
    private float width;
    private int widthPx;
    private int heighPx;
    private Context mContext;
    private ArrayList<ZhihuDailyItem> zhihuDailyItems = new ArrayList<>();
    private boolean showLoadingMore;
    private String mImageUrl;

    public ZhihuAdapter(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDimension(R.dimen.image_width);
        widthPx = DensityUtil.dip2px(mContext, width);
        heighPx = widthPx * 3 / 4;
    }



    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case NORMAL_ITEM:
                return new ZhihuViewHolder(LayoutInflater.from(mContext).inflate(R.layout.zhihu_layout_item, parent, false));
            case TYPE_LOADING_MORE:
                return new LoadingMoreHolder(LayoutInflater.from(mContext).inflate(R.layout.infinite_loading, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case NORMAL_ITEM:
                bindViewHolderNormal((ZhihuViewHolder) holder, position);
                break;
            case TYPE_LOADING_MORE:
                bindLoadingViewHold((LoadingMoreHolder) holder, position);
                break;
        }
    }

    private void bindLoadingViewHold(LoadingMoreHolder holder, int position) {
        holder.progressBar.setVisibility(showLoadingMore == true ? View.VISIBLE : View.INVISIBLE);
    }

    private void bindViewHolderNormal(final ZhihuViewHolder holder, final int position) {
        final ZhihuDailyItem zhihuDailyItem = zhihuDailyItems.get(holder.getAdapterPosition());
        if (ZhihuDao.getDB(mContext).isRead( zhihuDailyItem.getId(), 1))
            holder.textView.setTextColor(Color.GRAY);
        else
            holder.textView.setTextColor(Color.BLACK);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goDescribeActivity(holder, zhihuDailyItem);
            }
        });

        holder.textView.setText(zhihuDailyItem.getTitle());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goDescribeActivity(holder, zhihuDailyItem);
            }
        });

        Glide.with(mContext)
                .load(zhihuDailyItems.get(position).getImages()[0])
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @SuppressLint("NewApi")
                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (!zhihuDailyItem.hasFadedIn) {
                            holder.imageView.setHasTransientState(true);
                            final ObservableColorMatrix cm = new ObservableColorMatrix();
                            final ObjectAnimator animator = ObjectAnimator.ofFloat(cm, ObservableColorMatrix.SATURATION, 0f, 1f);
                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    holder.imageView.setColorFilter(new ColorMatrixColorFilter(cm));
                                }
                            });
                            animator.setDuration(2000L);
                            animator.setInterpolator(new AccelerateDecelerateInterpolator());
                            animator.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    holder.imageView.clearColorFilter();
                                    holder.imageView.setHasTransientState(false);
                                    animator.start();
                                    zhihuDailyItem.hasFadedIn = true;
                                }
                            });
                        }
                        return false;
                    }
                }).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop().override(widthPx, heighPx)
                .into(new DribbbleTarget(holder.imageView, false));


    }


    private void goDescribeActivity(ZhihuViewHolder holder, ZhihuDailyItem zhihuDailyItem) {

       ZhihuDao.getDB(mContext).insertHasRead(zhihuDailyItem.getId(), 1);
        holder.textView.setTextColor(Color.GRAY);
       Intent intent = new Intent(mContext, ZhihuDescribeActivity.class);
        intent.putExtra("id", zhihuDailyItem.getId());
        intent.putExtra("title", zhihuDailyItem.getTitle());
        intent.putExtra("image", mImageUrl);
        mContext.startActivity(intent);

    }


    @Override
    public int getItemCount() {
        return null == zhihuDailyItems ? 0 : zhihuDailyItems.size();
    }

    public void addItems(ArrayList<ZhihuDailyItem> list) {
        int n = list.size();
        zhihuDailyItems.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if (position < getDataItemCount() && getDataItemCount() > 0) {
            return NORMAL_ITEM;
        }
        return TYPE_LOADING_MORE;
    }

    private int getDataItemCount() {
        return zhihuDailyItems.size();
    }

    @Override
    public void loadingStart() {
        if (showLoadingMore) return;
        showLoadingMore = true;
        notifyItemInserted(getLoadingMoreItemPosition());
    }
    @Override
    public void loadingfinish() {
        if (!showLoadingMore) return;
        final int loadingPos = getLoadingMoreItemPosition();
        showLoadingMore = false;
        notifyItemRemoved(loadingPos);
    }

    public void clearData() {
        zhihuDailyItems.clear();
        notifyDataSetChanged();
    }

    private int getLoadingMoreItemPosition() {
        return showLoadingMore ? getItemCount() - 1 : RecyclerView.NO_POSITION;
    }




    class LoadingMoreHolder extends BaseRecyclerViewHolder {
        ProgressBar progressBar;

        public LoadingMoreHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView;
        }
    }

    class ZhihuViewHolder extends BaseRecyclerViewHolder {
        final TextView textView;
        final LinearLayout linearLayout;
        BadgedFourThreeImageView imageView;

        public ZhihuViewHolder(View itemView) {
            super(itemView);
            imageView = (BadgedFourThreeImageView) itemView.findViewById(R.id.item_image_id);
            textView = (TextView) itemView.findViewById(R.id.item_text_id);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.zhihu_item_layout);
        }
    }
}
