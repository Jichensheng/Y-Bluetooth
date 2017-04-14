package com.heshun.bounceanimator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * author：Jics
 * 2017/2/13 12:56
 */
public class FruitsView extends RelativeLayout {
	//弹跳高度
	public static final int VERTICAL_HEIGHT =100;
	private ImageView imv;
	private BezierTextView bv;
	private Bitmap bitmap;
	private int i = -1;

	public FruitsView(Context context) {
		super(context);
		initView(context);
	}

	public FruitsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public FruitsView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}
	private void initView(Context context) {

		View localView = LayoutInflater.from(context).inflate(R.layout.fruits_layout, this);
		imv = (ImageView) localView.findViewById(R.id.img_main);
		bv = (BezierTextView) localView.findViewById(R.id.bezier_text);

		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.p3);

		RelativeLayout.LayoutParams imvParams= (LayoutParams) imv.getLayoutParams();
		imvParams.topMargin=5;
		imvParams.leftMargin=(bv.getTextWidth()-bitmap.getWidth())/2;
		imv.setLayoutParams(imvParams);

		RelativeLayout.LayoutParams bvParams= (LayoutParams) bv.getLayoutParams();
		bvParams.topMargin=bitmap.getHeight()+5;
		bv.setLayoutParams(bvParams);
		startProperAnim();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//此处不可省
		super.onMeasure(widthMeasureSpec,heightMeasureSpec);
		setMeasuredDimension(bv.getTextWidth(),bv.getTextSize()+bitmap.getHeight()+VERTICAL_HEIGHT+10);
	}

	/**
	 * 抛水果动画
	 */
	private void startProperAnim() {
		AnimatorSet set =new AnimatorSet();
		//旋转的属性动画
		ObjectAnimator animRotation = ObjectAnimator.ofFloat(imv, "rotation", 0f, 359f);
		animRotation.setRepeatCount(-1);
		animRotation.setDuration(1200);
		//上下位移的属性动画
		ObjectAnimator animTranslationy=ObjectAnimator.ofFloat(imv,"translationY",VERTICAL_HEIGHT,0);
		animTranslationy.setRepeatCount(-1);
		animTranslationy.setRepeatMode(ValueAnimator.REVERSE);
		animTranslationy.setInterpolator(new DecelerateInterpolator());
		animTranslationy.setDuration(600);
		animTranslationy.addListener(new Animator.AnimatorListener() {

			final int[] fruts={R.drawable.p7,R.drawable.p3,R.drawable.p5,R.drawable.p1};
			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {

			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				if(i++%2==0){
					imv.setImageResource(fruts[(i/2)%fruts.length]);
					bv.resetGoingTime();
				}
			}
		});
		set.playTogether(animRotation,animTranslationy);
		set.start();
	}
}
