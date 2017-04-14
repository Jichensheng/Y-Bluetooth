package com.heshun.bounceanimator;


/**
 * author：Jics
 * 2017/2/22 15:27
 */
public class TestMain {
	public static void main(String[] args) {
//		calculatPoint(0,0,100,45);
//		calculatPoint(0,0,100,-45);
//		new TestMain().makeSegmentsLong(new Poin(300,500),100,0.4f);
		//deltaY，deltaX
		System.out.println(Math.toDegrees(Math.atan2(0,-50)));
	}
/*	private static  void calculatPoint(float x,float y, float length, float angle) {
		float deltaX = (float) Math.cos(Math.toRadians(angle)) * length;
		float deltaY = (float) Math.sin(Math.toRadians(angle) )* length;
		System.out.println(String.format("%.3f     %.3f",x+deltaX,y+deltaY));
		System.out.println(Math.sin(Math.toRadians(10.48812)));
	}*/
	/**
	 *起点长度角度计算终点
	 * @return
	 */
	private  void makeSegmentsLong(Poin poin, float segmentRadius, float MP) {
		float angle=90;//中心轴与x轴
		//身长
		float segementLenght = segmentRadius * (MP + 2.3f);
		Poin endPoint=calculatPoint(poin,segementLenght,angle);

		Poin point1, point2, point3, point4;
		point1=calculatPoint(poin,segmentRadius,90+angle);
		point2=calculatPoint(endPoint,segmentRadius * MP,90+angle);
		point3=calculatPoint(endPoint,segmentRadius * MP,angle-90);
		point4=calculatPoint(poin,segmentRadius,angle-90);
		System.out.println("*************"+ point1.getX()+"           "+point1.getY());
		System.out.println("*************"+point2.getX()+"           "+point2.getY());
		System.out.println("*************"+point3.getX()+"           "+point3.getY());
		System.out.println("*************"+point4.getX()+"           "+point4.getY());

	}
	private Poin calculatPoint(Poin poin, float length, float angle) {
		float deltaX = (float) Math.cos(Math.toRadians(angle)) * length;
		float deltaY = (float) Math.sin(Math.toRadians(angle) )* length;
		System.out.println(String.format("%.3f     %.3f",poin.getX()+deltaX,poin.getY()+deltaY));
		return  new Poin(poin.getX()+deltaX, poin.getY()+deltaY);
	}
}
class Poin{
	private float x;
	private float y;
	public Poin(float x,float y){
		this.x=x;
		this.y=y;
	}
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
}