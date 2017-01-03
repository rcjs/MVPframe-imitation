package me.jessyan.mvparms.demo.mvp.ui.adapter;


import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

/**
 * 添加了监听器的Adapter
 * @author Yue
 */
public abstract class BaseRecyclerViewAdapter extends Adapter<BaseRecyclerViewAdapter.BaseRecyclerViewHolder> {

	public interface OnItemClickListener {
		public void onItemClick(View view, int position);
	}
	public interface OnItemSelectedListener {
		public void onItemSelected(View view, boolean hasFocus, int position);
	}
	
	public interface OnItemLongClickListener {
		public void onItemLongClick(View view, int position);
	}
	public interface OnkeyListener{
		public boolean onKey(View view, int keyCode, KeyEvent event, int position) ;
	}
	
	private OnItemClickListener onItemClickListener;
	private OnItemSelectedListener onItemSelectedListener;
	private OnItemLongClickListener onItemLongClickListener;
	private OnkeyListener onKeyListener ;
	
	public void setOnItemClickListener(OnItemClickListener onItemClickListener){
		this.onItemClickListener = onItemClickListener;
	}
	
	
	public void setOnItemSelectedListener(OnItemSelectedListener onItemFocusListener){
		this.onItemSelectedListener = onItemFocusListener;
	}
	
	public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
		this.onItemLongClickListener = onItemLongClickListener;
	}
	
	public void setOnkeyListener(OnkeyListener onKeyListener){
		this.onKeyListener = onKeyListener ;
	}
	
	
	public class BaseRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener,View.OnFocusChangeListener, OnKeyListener{

		public BaseRecyclerViewHolder(View view) {
			super(view);
			view.setOnClickListener(this);
			view.setOnFocusChangeListener(this);
			view.setOnLongClickListener(this);
			view.setOnKeyListener(this);
			
		}

		@Override
		public boolean onLongClick(View v) {
			if (onItemLongClickListener != null){
				onItemLongClickListener.onItemLongClick(v, getPosition());
			}
			return true;
		}

		@Override
		public void onClick(View v) {
			if (onItemClickListener != null){
				onItemClickListener.onItemClick(v, getPosition());
			}
		}

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (onItemSelectedListener != null){
				onItemSelectedListener.onItemSelected(v, hasFocus, getPosition());
			}
		}

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if(onKeyListener != null){
				return onKeyListener.onKey(v, keyCode, event, getPosition());
			}
			return false;
		}
		
	}

}
