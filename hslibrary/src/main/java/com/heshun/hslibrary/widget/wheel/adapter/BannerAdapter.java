package com.heshun.hslibrary.widget.wheel.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.heshun.hslibrary.R;
import com.heshun.hslibrary.widget.wheel.entity.BannerItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Random;

/**
 * 
 * 广告栏viewpager适配器
 * 
 * @author huangxz
 *
 * @param <T>
 */
public class BannerAdapter<T> extends PagerAdapter {
	private OnBannerItemClickListener onBannerItemClickListener;
	private DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
			.cacheInMemory(false)
			.showImageForEmptyUri(R.drawable.banner_default_icon)
			.showImageOnLoading(R.drawable.banner_default_icon)
			.showImageOnFail(R.drawable.banner_default_icon)
			.cacheOnDisk(true)
			.build();

	private Context mContext;

	private List<T> mData;

	public BannerAdapter(Context c) {
		this.mContext = c;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getCount() {
		return null == mData ? 0 : mData.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	public void setData(List<T> data) {
		this.mData = data;
	}
	public interface OnBannerItemClickListener {
		void onBannerClick(BannerItem item);
	}
	public void setOnBannerItemClickListener(OnBannerItemClickListener onBannerItemClickListener){
		this.onBannerItemClickListener=onBannerItemClickListener;
	}
	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		final T item = mData.get(position);

		View view = LayoutInflater.from(mContext).inflate(R.layout.banner_item_pic, null, false);
		//TODO 测试
		String[] urls=new String[]{"http://p1.bpimg.com/4851/ff67630b2ecda6de.jpg"
				,"http://p1.bpimg.com/4851/a72e024faccf7fb4.jpg"
				,"http://p1.bqimg.com/4851/7e4af72d320aecd6.jpg"
				,"http://p1.bqimg.com/4851/62136dc0bd7a87da.jpg"
		};
		if (item instanceof BannerItem) {
			BannerItem banner = (BannerItem) item;
			ImageView imageView = (ImageView) view.findViewById(R.id.img_pic);
			ImageLoader.getInstance().displayImage(urls[new Random().nextInt(4)], imageView, mOptions);
//			ImageLoader.getInstance().displayImage(Config.UPLOAD.concat(banner.indexImage), imageView, mOptions);
			container.addView(view, 0);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (item != null) {
						onBannerItemClickListener.onBannerClick((BannerItem) item);
					}
				}
			});
		}
		return view;
	}

}
