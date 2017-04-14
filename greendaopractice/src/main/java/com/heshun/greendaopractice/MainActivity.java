package com.heshun.greendaopractice;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import db.DaoSession;

public class MainActivity extends AppCompatActivity {
private DaoSession daoSession;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/*TextView textView= (TextView) findViewById(R.id.tv);
		ScaleAnimation animation =new ScaleAnimation(0f,1.4f,0f,1.4f, Animation.RELATIVE_TO_SELF,0.1f,Animation.RELATIVE_TO_SELF,0.1f);
		animation.setDuration(1000);
		TranslateAnimation translateAnimation =new TranslateAnimation(0,150,0,0);
		translateAnimation.setRepeatCount(1);
		translateAnimation.setRepeatMode(Animation.REVERSE);
		translateAnimation.setDuration(1000);
		Animation setss= AnimationUtils.loadAnimation(this,R.anim.alpha);
		AnimationSet set=new AnimationSet(true);
//		set.addAnimation(animation);
		set.addAnimation(setss);
//		set.addAnimation(translateAnimation);
		textView.setAnimation(set);
		set.start();*/
	/*	initDB();
		UserDao userDao=daoSession.getUserDao();
		int rand=new Random().nextInt(9999);
		User user=new User(null,"jichensheng"+rand,"jcs"+rand);
		userDao.insert(user);*/

	}
	/*private void initDB(){
		DaoMaster.DevOpenHelper devOpenHelper=new DaoMaster.DevOpenHelper(getApplicationContext(),"jcs.db",null);
		DaoMaster daoMaster=new DaoMaster(devOpenHelper.getWritableDb());
		daoSession=daoMaster.newSession();

	}*/
}
