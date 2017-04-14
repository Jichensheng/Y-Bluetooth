package com.heshun.canvasdemo.customerView.fish;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;

import com.heshun.canvasdemo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * author：Jics
 * 2017/2/24 15:17
 */
public class FishLayout extends RelativeLayout implements FoodView.OnFoodClickListener {
	private FishView fishView;
	private FoodView foodView;
	private LineView lineView;
	private PointF pointF;
	private PointF viewStartPoint;
	private PointF endPoint;
	//行走动画
	private Animator animator;


	private long animatorDuration = 1000;

	private List<PointF> resultList;

	private Handler ha = new Handler();

	public FishLayout(Context context) {
		super(context);
		initView(context);
	}

	public FishLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public FishLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	public void initView(Context context) {
		View localView = LayoutInflater.from(context).inflate(R.layout.fish_layout, this);
		fishView = (FishView) localView.findViewById(R.id.fish_view);
		lineView = (LineView) localView.findViewById(R.id.line_view);
		//初始化鱼儿的位置信息
		viewStartPoint = new PointF(fishView.TOTAL_LENGTH, fishView.TOTAL_LENGTH);
		foodView = (FoodView) localView.findViewById(R.id.food_view);
		foodView.setListener(this);

 /*
 //自动
 new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(new Random().nextInt(20)*1000);
                        onFoodClick(new Random().nextFloat()*fishView.mScreenWidth,new Random().nextFloat()*fishView.mScreenHeight);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/

	}


	private Animator getAnimator(FishView target, List<PointF> listPoint) {
		AnimatorSet set = new AnimatorSet();

		ValueAnimator bezierValueAnimator = getBezierValueAnimator(target, listPoint);

		AnimatorSet finalSet = new AnimatorSet();
		finalSet.playSequentially(set);
		finalSet.playSequentially(set, bezierValueAnimator);
		finalSet.setInterpolator(new AccelerateInterpolator());
		finalSet.setTarget(target);
		return finalSet;
	}

	/**
	 * @param target
	 * @return
	 */
	private ValueAnimator getBezierValueAnimator(FishView target, List<PointF> listPoint) {

		// 初始化一个贝塞尔计算器- - 传入两个控制点
		BezierEvaluator evaluator = new BezierEvaluator(listPoint.get(2),
				listPoint.get(3), fishView);

		//估值器  底部中点  顶部随机点——》f估（低点，顶点）以时间为自变量PointF为因变量
		ValueAnimator animator = ValueAnimator.ofObject(evaluator, listPoint.get(0),
				listPoint.get(1));
		animator.addUpdateListener(new BezierListenr(target));
		animator.setTarget(target);
		animator.setDuration(animatorDuration);
		animator.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {
				fishView.setWaveFrequence(2f);
			}

			@Override
			public void onAnimationEnd(Animator animator) {
				fishView.setWaveFrequence(0.5f);
			}

			@Override
			public void onAnimationCancel(Animator animator) {
				fishView.setWaveFrequence(0.5f);
			}

			@Override
			public void onAnimationRepeat(Animator animator) {
				fishView.setWaveFrequence(2f);
			}
		});
		return animator;
	}

	/**
	 * huidiao回调
	 *
	 * @param x
	 * @param y
	 */
	@Override
	public void onFoodClick(final float x, final float y) {
		ha.postDelayed(new Runnable() {
			@Override
			public void run() {

				ObjectAnimator finsAnimator = fishView.getFinsAnimator();
				finsAnimator.setRepeatCount(new Random().nextInt(3));
				finsAnimator.setDuration((long) ((new Random().nextInt(1) + 1) * 500));
				finsAnimator.start();
				if (endPoint == null) {
					endPoint = new PointF(x, y);
				} else {
					endPoint.x = x;
					endPoint.y = y;
				}
				if (animator != null) {
					animator.cancel();
				}
				int[] lineTypes = {AnimaUtils.LINE_D, AnimaUtils.LINE_A, AnimaUtils.LINE_B, AnimaUtils.LINE_C};
				List<PointF> contralPoint;
				float fatherAngle = fishView.getFatherAngle();
				float bodyLength = fishView.BODY_LENGHT;
				float lineLength = AnimaUtils.caculatLineLength(new PointF(viewStartPoint.x - fishView.TOTAL_LENGTH, viewStartPoint.y - fishView.TOTAL_LENGTH), endPoint);

				animatorDuration = (long) (1000 * (lineLength / bodyLength < 0.25 ? 0.25 : lineLength / bodyLength / 7));
				animatorDuration = animatorDuration < 1500 ? 2500 : animatorDuration;

				float newAngle = AnimaUtils.caculatAngle(new PointF(viewStartPoint.x - fishView.TOTAL_LENGTH, viewStartPoint.y - fishView.TOTAL_LENGTH), endPoint);
				float totalAngel = newAngle + fatherAngle;
				if (totalAngel >= 180 && totalAngel < 270) {
					contralPoint = AnimaUtils.getBezierContralPoint(viewStartPoint, endPoint, lineTypes[0], fatherAngle, bodyLength);
					Log.e("*********", "run: " + "左下");
				} else if (totalAngel >= 270 && totalAngel < 360) {
					contralPoint = AnimaUtils.getBezierContralPoint(viewStartPoint, endPoint, lineTypes[1], fatherAngle, bodyLength);
					Log.e("*********", "run: " + "左");
				} else if (totalAngel >= 90 && totalAngel < 180) {
					Log.e("*********", "run: " + "右下");
					contralPoint = AnimaUtils.getBezierContralPoint(viewStartPoint, endPoint, lineTypes[2], fatherAngle, bodyLength);
				} else {
					Log.e("*********", "run: " + "右");
					contralPoint = AnimaUtils.getBezierContralPoint(viewStartPoint, endPoint, lineTypes[3], fatherAngle, bodyLength);
				}

				resultList = new ArrayList<>();
				resultList.add(viewStartPoint);
				resultList.add(endPoint);
				resultList.add(contralPoint.get(0));
				resultList.add(contralPoint.get(1));

				lineView.setResultList(resultList);

				animator = getAnimator(fishView, resultList);
				animator.start();
			}
		}, 500);
	}


	/**
	 * 动画回调：控制估值器控制鱼的位置和角度
	 */
	private class BezierListenr implements ValueAnimator.AnimatorUpdateListener {

		private FishView target;

		public BezierListenr(FishView target) {
			this.target = target;
		}

		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			//动画过程中估值的过程值在这里
			pointF = (PointF) animation.getAnimatedValue();

			//设置位置
			target.setX(pointF.x - fishView.TOTAL_LENGTH);
			target.setY(pointF.y - fishView.TOTAL_LENGTH);

			//不停更新鱼儿view当前位置
			viewStartPoint.x = pointF.x;
			viewStartPoint.y = pointF.y;
		}
	}
}
