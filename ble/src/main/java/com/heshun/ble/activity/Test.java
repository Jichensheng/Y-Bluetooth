package com.heshun.ble.activity;

/**
 * author：Jics
 * 2017/4/14 13:20
 */
public class Test {
	public static void main(String[] args){
		String ss="+ALARM2017-03-08 10:11:32";
		String[] array=ss.split(" ");
		for (int i = 0; i < array.length; i++) {
			if (i==array.length-1){
				System.out.print(" "+array[i]);
			}else
			System.out.print(array[i]);
		}

	}
}
