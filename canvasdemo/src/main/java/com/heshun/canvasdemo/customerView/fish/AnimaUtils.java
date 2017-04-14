package com.heshun.canvasdemo.customerView.fish;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jcs on 2017/2/25.
 */

public class AnimaUtils {
	public static final int LINE_D = 0x0001;
	public static final int LINE_A = 0x0002;
	public static final int LINE_B = 0x0003;
	public static final int LINE_C = 0x0004;
	public static final int LINE_DRI = 0x0005;

	/**
	 * 规划路线关键点核心代码
	 * @param startPoint
	 * @param endPoint
	 * @param lineType
	 * @param delta      父角度
	 * @return
	 */
	public static List<PointF> getBezierContralPoint(PointF startPoint, PointF endPoint, int lineType, float delta,float bodyLength) {
		List<PointF> pointFList = new ArrayList<>();
		PointF contralPoint_0;
		PointF contralPoint_1;
		float angle = caculatAngle(startPoint, endPoint);
		float lineLength = caculatLineLength(startPoint,endPoint);
		switch (lineType) {
			case LINE_A://左下
				contralPoint_0 = calculatPoint(startPoint, bodyLength * 2.0f, angle+delta);
				contralPoint_1 = calculatPoint(endPoint, bodyLength, angle+delta);
				break;
			case LINE_B://左
				contralPoint_0 = calculatPoint(startPoint, bodyLength, angle+delta);
				contralPoint_1 = calculatPoint(endPoint, bodyLength, 360-angle -delta);
				break;
			case LINE_C://右下
				contralPoint_0 = calculatPoint(startPoint, bodyLength * 2.0f,angle+delta);
				contralPoint_1 = calculatPoint(endPoint, bodyLength, 180-angle-delta);
				break;
			case LINE_D://右
				contralPoint_0 = calculatPoint(startPoint, bodyLength , angle+delta );
				contralPoint_1 = calculatPoint(endPoint, bodyLength, 180-angle-delta);
				break;
			case LINE_DRI://直线
				contralPoint_0 = calculatPoint(startPoint, lineLength * 0.2f, angle);
				contralPoint_1 = calculatPoint(endPoint, lineLength * 0.2f, angle);
			default://默认直线
				contralPoint_0 = calculatPoint(startPoint, 1, angle);
				contralPoint_1 = calculatPoint(startPoint, 1, angle);
		}
		pointFList.add(contralPoint_0);
		pointFList.add(contralPoint_1);
		return pointFList;
	}

	/**
	 * 计算两点连线角度
	 *
	 * @param startPoint
	 * @param endPoint
	 * @return
	 */
	public static float caculatAngle(PointF startPoint, PointF endPoint) {
		float deltaY = endPoint.y - startPoint.y;
		float deltaX = endPoint.x - startPoint.x;
		return (float) Math.toDegrees(Math.atan((deltaY / (deltaX == 0 ? 0.1 : deltaX))));
	}

	/**
	 * 起点长度角度计算终点
	 *
	 * @param startPoint
	 * @param length
	 * @param angle      顺时针角度
	 * @return
	 */
	public static PointF calculatPoint(PointF startPoint, float length, float angle) {
		float deltaX = (float) Math.cos(Math.toRadians(angle)) * length;
		float deltaY = (float) Math.sin(Math.toRadians(angle)) * length;
		return new PointF(startPoint.x + deltaX, startPoint.y + deltaY);
	}

	/**
	 * 线长
	 * @param startPoint
	 * @param endPoint
	 * @return
	 */
	public static float caculatLineLength(PointF startPoint,PointF endPoint){
		float deltaY = endPoint.y - startPoint.y;
		float deltaX = endPoint.x - startPoint.x;
		return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
	}
}
