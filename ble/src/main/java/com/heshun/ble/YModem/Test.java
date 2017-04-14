package com.heshun.ble.YModem;

import java.util.Arrays;

/**
 * author：Jics
 * 2017/3/27 19:59
 */
public class Test {
	private static int packSize = 5;

	public static void main(String[] args) {
		byte[] bytes = new byte[]{(byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1};
		subPack(bytes);
	}

	private static void subPack(byte[] data) {
		int length = data.length;
		int packCount = length % packSize == 0 ? length / packSize : length / packSize + 1;
		for (int i = 1; i <= packCount; i++) {
			if (i == packCount) {
				byte[] bytes = new byte[length % packSize == 0 ? packSize : length % packSize];
				System.arraycopy(data, (i - 1) * packSize, bytes, 0, bytes.length);
				System.out.println(i + "     " + Arrays.toString(bytes));
			} else {
				byte[] bytes = new byte[packSize];
				System.arraycopy(data, (i - 1) * packSize, bytes, 0, packSize);
				System.out.println(i + "     " + Arrays.toString(bytes));
			}
		}
	}
}
