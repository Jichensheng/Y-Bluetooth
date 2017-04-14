package com.heshun.testAndbackup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * author：Jics
 * 2017/3/16 15:23
 */
public class Test {

	public static void main(String[] args) {
	/*	byte[] bytes={(byte)'C',(byte)'C',(byte)'C',0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
		try {
			System.out.println(new String(bytes,0,bytes.length,"utf-8").trim().length());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}*/
		File file = new File("D:/android-studio-ide-143.2739321-windows/workspace/TestDemo/bluetoothrecive/src/main/assets/a.txt");
		try {
			InputStream inputStream = new FileInputStream(file);
			byte[] bytes1 = new byte[1029];
			inputStream.read(bytes1);
			for (byte b : bytes1) {
				System.out.println((char) b);
			}
//			Ymodem.getPackage(inputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//VOL:220.3V,COR:16.3A,ELC:3.41Kwh,TIME:23MIN,STATE:0
		String s = "JLZ=VOL:220.3V,COR:16.3A,ELC:3.41Kwh,TIME:23MIN,STATE:0";
		analysisWords(s);
	}

	public static void analysisWords(String s) {
		String temp = s.substring(s.indexOf("JLZ=") + "JLZ=".length());

		for (String ss : temp.split(",")) {
			int cuser = 0;
			boolean isNum = false;
			for (int i = 0; i < ss.length(); i++) {
				if (ss.charAt(i) >= '0' && ss.charAt(i) <= '9') {
					isNum = true;
				} else if (isNum && (((ss.charAt(i) >= 'A' && ss.charAt(i) <= 'Z')) || ((ss.charAt(i) >= 'a' && ss.charAt(i) <= 'z')))) {
					cuser = i;
					isNum = false;
					break;
				} else
					isNum = false;
			}
			StringBuffer sb = new StringBuffer(ss);
			if (cuser != 0) {
				sb.insert(cuser, "\t");
			}
			sb.insert(sb.indexOf(":") + 1, "\t");
			System.out.println(sb);
		}
	}
}
