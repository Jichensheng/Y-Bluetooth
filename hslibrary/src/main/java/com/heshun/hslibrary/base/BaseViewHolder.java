package com.heshun.hslibrary.base;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;

/**
 * 基础的ViewHolder，统一继承，方便增加公用实现
 * 
 * @author huangxz
 *
 */
public abstract class BaseViewHolder extends ViewHolder {

	public BaseViewHolder(View itemView) {
		super(itemView);
	}
}
