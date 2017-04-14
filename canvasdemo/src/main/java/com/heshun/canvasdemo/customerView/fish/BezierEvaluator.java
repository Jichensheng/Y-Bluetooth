package com.heshun.canvasdemo.customerView.fish;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

public class BezierEvaluator implements TypeEvaluator<PointF> {
	//三阶贝塞尔曲线的两个控制点
	private PointF pointF1;
	private PointF pointF2;
	private FishView fishView;

	public BezierEvaluator(PointF pointF1, PointF pointF2, FishView fishView) {
		this.pointF1 = pointF1;
		this.pointF2 = pointF2;
		this.fishView = fishView;
	}

	/**
	 * 三阶贝塞尔曲线
	 *
	 * @param time
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	@Override
	public PointF evaluate(float time, PointF startValue, PointF endValue) {
		float timeLeft = 1.0f - time;
		float slopeX;
		float slopeY;
		float angle;
		PointF point = new PointF();// 结果

		point.x = timeLeft * timeLeft * timeLeft * (startValue.x) + 3
				* timeLeft * timeLeft * time * (pointF1.x) + 3 * timeLeft
				* time * time * (pointF2.x) + time * time * time * (endValue.x);

		point.y = timeLeft * timeLeft * timeLeft * (startValue.y) + 3
				* timeLeft * timeLeft * time * (pointF1.y) + 3 * timeLeft
				* time * time * (pointF2.y) + time * time * time * (endValue.y);

		//鱼儿身体角度
		slopeX = (-3 * startValue.x * timeLeft * timeLeft) +
				(3 * pointF1.x * timeLeft * timeLeft - 6 * pointF1.x * time * timeLeft) +
				(6 * pointF2.x * time * timeLeft - 3 * pointF2.x * time * time) +
				(3 * endValue.x * time * time);
		slopeY = (-3 * startValue.y * timeLeft * timeLeft) +
				(3 * pointF1.y * timeLeft * timeLeft - 6 * pointF1.y * time * timeLeft) +
				(6 * pointF2.y * time * timeLeft - 3 * pointF2.y * time * time) +
				(3 * endValue.y * time * time);
		if (slopeX * slopeY > 0) {
			if (slopeX > 0) {
				angle = 180 + (float) Math.toDegrees(Math.abs(Math.atan(slopeY / slopeX)));
			} else {
				angle = (float) Math.toDegrees(Math.abs(Math.atan(slopeY / slopeX)));
			}
		} else {
			if (slopeX > 0) {
				angle = 180 - (float) Math.toDegrees(Math.abs(Math.atan(slopeY / slopeX)));
			} else {
				angle = -(float) Math.toDegrees(Math.abs(Math.atan(slopeY / slopeX)));
			}
		}
		fishView.setFatherAngle(angle);
		return point;
	}
}