package com.heshun.curveanimation;

import android.animation.TypeEvaluator;

/**
 * 实现TypeEvaluator接口，实现evaluate方法
 * 在方法中给出属性改变的具体实现过程，以达到预期动画效果
 * 它反映了属性变化的具体过程
 * author：Jics
 * 2017/2/9 14:43
 */
public class ViewPathEvaluator implements TypeEvaluator<ViewPoint> {
	public ViewPathEvaluator(){

	}
	@Override
	public ViewPoint evaluate(float t, ViewPoint startValue, ViewPoint endValue) {
		float x,y;
		float startX,startY;
		//startValue 前一个操作路径 endValue后一个操作路径 t 操作进度（0 -> 1）
		//startValue 和endValue为传入路经济和数组相邻的两个路经
		if(endValue.operation == ViewPath.LINE){

			//起点判断：
			startX = (startValue.operation==ViewPath.QUAD)?startValue.x1:startValue.x;

			startX = (startValue.operation == ViewPath.CURVE)?startValue.x2:startX;

			startY = (startValue.operation==ViewPath.QUAD)?startValue.y1:startValue.y;

			startY = (startValue.operation == ViewPath.CURVE)?startValue.y2:startY;

			//返回的偏移坐标计算：根据公式
			x = startX + t * (endValue.x - startX);
			y = startY+ t * (endValue.y - startY);


		}else if(endValue.operation == ViewPath.CURVE){

			//起点判断：
			startX = (startValue.operation==ViewPath.QUAD)?startValue.x1:startValue.x;
			startY = (startValue.operation==ViewPath.QUAD)?startValue.y1:startValue.y;

			float oneMinusT = 1 - t;

			//返回的偏移坐标计算：根据公式
			x = oneMinusT * oneMinusT * oneMinusT * startX +
					3 * oneMinusT * oneMinusT * t * endValue.x +
					3 * oneMinusT * t * t * endValue.x1+
					t * t * t * endValue.x2;

			y = oneMinusT * oneMinusT * oneMinusT * startY +
					3 * oneMinusT * oneMinusT * t * endValue.y +
					3 * oneMinusT * t * t * endValue.y1+
					t * t * t * endValue.y2;


		}else if(endValue.operation == ViewPath.MOVE){

			x = endValue.x;
			y = endValue.y;


		}else if(endValue.operation == ViewPath.QUAD){

			//起点判断：
			startX = (startValue.operation==ViewPath.CURVE)?startValue.x2:startValue.x;
			startY = (startValue.operation==ViewPath.CURVE)?startValue.y2:startValue.y;

			//返回的偏移坐标计算：根据公式
			float oneMinusT = 1 - t;
			x = oneMinusT * oneMinusT *  startX +
					2 * oneMinusT *  t * endValue.x +
					t * t * endValue.x1;

			y = oneMinusT * oneMinusT * startY +
					2  * oneMinusT * t * endValue.y +
					t * t * endValue.y1;


		}else {
			// 其他
			x = endValue.x;
			y = endValue.y;
		}


		return new ViewPoint(x,y);
	}
}
