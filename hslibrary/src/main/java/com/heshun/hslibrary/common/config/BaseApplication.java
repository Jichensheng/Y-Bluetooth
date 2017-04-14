package com.heshun.hslibrary.common.config;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.LinkedList;
import java.util.List;

/**
 * application
 * 
 * @author huangxz
 *
 */
public class BaseApplication extends android.app.Application {
	private List<Activity> activityList = new LinkedList();
	private static BaseApplication instance;
	private static Context context;
	public BaseApplication(Context context) {
		this.context= context;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		initApplication();
		if (Config.getNumCores() != 0)
			Config.THREAD_COUNT = 2 * Config.getNumCores();
		initImageLoader();
		initCrash();
	}
	private void initCrash() {
		CrashHandler.getInstance().init(context.getApplicationContext());
		Thread.setDefaultUncaughtExceptionHandler(CrashHandler.getInstance());
	}

	private void initApplication() {

		try {
			PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			Config.setLocalVersionCode(pkgInfo.versionCode);
			Config.setServerVersionCode(pkgInfo.versionCode);// 初始化为与本地版本号一致
			Config.setLocalVersionName(pkgInfo.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void initImageLoader() {
		ImageLoader imageLoader = ImageLoader.getInstance();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context.getApplicationContext())
				.threadPoolSize(Config.THREAD_COUNT).threadPriority(Thread.NORM_PRIORITY - 1)
				.denyCacheImageMultipleSizesInMemory().memoryCache(new FIFOLimitedMemoryCache(2 * 1024 * 1024)).build();
		imageLoader.init(config);
	}

	public static Context getContextInstance() {
		return context;
	}
	public static BaseApplication getInstance(){
		return instance;
	}

	@Override
	public void onLowMemory() {
		ImageLoader.getInstance().clearMemoryCache();
		super.onLowMemory();
	}


	//添加Activity到容器中
	public void addActivity(Activity activity)
	{
		activityList.add(activity);
	}
	//遍历所有Activity并finish
	public void exit()
	{

		for(Activity activity:activityList)
		{
			activity.finish();
		}

		System.exit(0);

	}

}
