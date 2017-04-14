package com.heshun.testAndbackup;

import com.heshun.entity.ElectricityParameter;
import com.heshun.tools.FileUtils;

import java.util.List;

/**
 * author：Jics
 * 2017/3/17 15:44
 */
public class TestChar {

	public static void main(String[] args) {
		byte[] src;
		/*try {
  			src="客户端".getBytes("utf-8");
   		System.out.println(new String(src,0,src.length,"utf-8").startsWith("客"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}*/
//			System.out.println((byte)'\0');
//			System.out.println(FileUtils.getFileOrFilesSize("D:/fish.swf",FileUtils.SIZETYPE_B));

		/*List<byte[]> packageFrames;
		File file = new File("D:/fish.swf");
		try {
			InputStream inputStream = new FileInputStream(file);
			packageFrames = Ymodem.getPackage(inputStream, file.getName(), FileUtils.getFileOrFilesSize("D:/fish.swf", FileUtils.SIZETYPE_B));
			System.out.println((byte) '\0' + "  " + Arrays.toString("\0".getBytes()));
			for (int i = 0; i < packageFrames.size(); i++) {
				if (i == 0) {
					System.out.println(new String(packageFrames.get(i)));
					System.out.println(Arrays.toString(packageFrames.get(i)));
				} else {
					System.out.println(Arrays.toString(packageFrames.get(i)));

				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}*/
		/*byte[] empty = new byte[133];
		empty[0] = 0x01;
		empty[1] = (byte) 0;
		empty[2] = (byte) ~empty[1];
		byte[] data = new byte[128];

		System.arraycopy(data, 0, empty, 3, 128);
		System.arraycopy(new byte[]{0x11,0x11}, 0, empty, 131, 2);
		System.out.println(Arrays.toString(empty));*/
	/*	byte[] data = new byte[128];
		data[10]=0x14;
		System.out.println(Arrays.toString(CRC16.calcByteArray(data)));*/
		List<ElectricityParameter> electricityParameters= FileUtils.analysisWords2entity("JLZ=VOL:220.0V,COR:16.3A,ELC:3.41Kwh,TIME:23MIN,STATE:0=JLZ");
		for(ElectricityParameter ep:electricityParameters){
			System.out.println(String.format("名字 %s  数值 %s  单位 %s",ep.getName(),ep.getValue(),ep.getUnit()));
		}
	}
}
