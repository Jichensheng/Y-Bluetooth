package com.heshun.curveanimation;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
	private ImageButton imageButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageButton= (ImageButton) findViewById(R.id.imagebtn);
	}
	public void onImgClick(View view){
		ViewPath path=new ViewPath();//偏移坐标 即属性的开始点，中途变化点，结束点的具体值
		path.moveTo(0,0);
		path.lineTo(0,500);
		path.quadTo(-300,200,-800,600);
		path.curveTo(-800,500,-300,200,-800,100);
		path.lineTo(-800,0);
		ObjectAnimator anim =ObjectAnimator.ofObject(this,"jcs",new ViewPathEvaluator(),path.getPoints().toArray());
		anim.setInterpolator(new AccelerateDecelerateInterpolator());
		anim.setDuration(3000);
		anim.start();
	}

	/**
	 * 反射调用此方法，方法名要和ofObject的第二个参数对应
	 * @param newLoc
	 */
	public void setJcs(ViewPoint newLoc){
		imageButton.setTranslationX(newLoc.x);
		imageButton.setTranslationY(newLoc.y);
	}
}
