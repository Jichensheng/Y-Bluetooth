package com.heshun.hslibrary.project.guidemask;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;

import com.heshun.hslibrary.R;


public abstract class GuideManager {
	protected static String TAG = "";
	protected static final int LEFT_ARROW = R.drawable.left_arrow;
	protected static final int RIGHT_ARROW = R.drawable.right;
	protected static final int DOWN_ARROW = R.drawable.down_arrow;
	private Activity mActivity;
	private SharedPreferences sp;
	private GuideLayout mGuideLayout;
	protected int mType;
	private Activity activity;
	public GuideManager(Activity activity, int type) {
		mGuideLayout = new GuideLayout(activity);
		mActivity = activity;
		mType = type;
		this.activity=activity;
	}

	public GuideManager addView(View view, int shape) {
		mGuideLayout.addHighLightView(view, shape);
		return this;
	}

	public void setTAG(String TAG){
		this.TAG = TAG;
		sp = activity.getSharedPreferences(TAG, Activity.MODE_PRIVATE);
	}
	public void show() {
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(TAG + mType, false);
		editor.apply();
	}

	public void makeMask(int imgId, int imgX, int imgY, int textY,String content) {
		mGuideLayout.setEveryWhereTouchable(false).addIndicateImg(imgId, ScreenUtils.dpToPx(mActivity, imgX), ScreenUtils.dpToPx(mActivity,
				imgY)).addMsgAndKnowTv(content, ScreenUtils.dpToPx(mActivity, textY)).show();
	}

	public void showWithListener( GuideLayout.OnGuideChangedListener onGuideChangedListener) {
		mGuideLayout.setOnGuideChangedListener(onGuideChangedListener);
		show();
	}

	/**
	 * 判断新手引导也是否已经显示了
	 * true显示
	 */
	public static boolean isNeedShowed(Activity activity, int type) {

		return activity.getSharedPreferences(TAG, Activity.MODE_PRIVATE).getBoolean(TAG + type, true);
	}


}
