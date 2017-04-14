package com.heshun.curveanimation;

/**
 * author：Jics
 * 2017/2/9 14:12
 */
public class ViewPoint {
	float x, y;
	float x1, y1;
	float x2, y2;
	int operation;

	public ViewPoint(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public ViewPoint(float x, float y, int operation) {
		this.x = x;
		this.y = y;
		this.operation = operation;
	}

	public ViewPoint(float x, float y, float x1, float y1, float x2, float y2, int operation) {
		this.x=x;
		this.y=y;
		this.x1=x1;
		this.x2=x2;
		this.y1=y1;
		this.y2=y2;
		this.operation=operation;
	}

	public ViewPoint(float x, float y, float x1, float y1, int operation) {
		this.x=x;
		this.y=y;
		this.x1=x1;
		this.y1=y1;
		this.operation=operation;
	}

	//起点
	public static ViewPoint moveTo(float x, float y, int operation) {
		return new ViewPoint(x, y, operation);
	}

	//移动
	public static ViewPoint lineTo(float x, float y, int operation) {
		return new ViewPoint(x, y, operation);
	}

	//三阶
	public static ViewPoint curveTo(float x, float y, float x1, float y1, float x2, float y2, int operation) {
		return new ViewPoint(x, y, x1, y1, x2, y2, operation);
	}
	//二阶
	public static ViewPoint quadTo(float x,float y,float x1,float y1,int operation){
		return new  ViewPoint(x,y,x1,y1,operation);
	}
}
