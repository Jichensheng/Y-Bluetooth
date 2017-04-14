package com.heshun.YModem;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * author：Jics
 * 2017/3/16 15:38
 */
public class Ymodem {
	//请求字符
	public static final byte CHAR_C = (byte) 'C';
	// 开始(128B)
	public static final byte SOH = 0x01;
	// 开始(1024B)
	public static final byte STX = 0x02;
	// 结束
	public static final byte EOT = 0x04;
	// 应答
	public static final byte ACK = 0x06;
	// 重传
	public static final byte NAK = 0x15;
	// 无条件结束
	public static final byte CAN = 0x18;
	// 填充数据包
	public static final byte EOF = 0x1A;
	//单帧数据位大小
	private static final int SECTOR_SIZE = 1024;
	//第一次请求
	public static final int FIRST = 0x2A;
	//发完了
	public static final int STATE_COMPLETE = 0x3A;
	//下一包
	public static final int STATE_NEXT = 0x4A;
	//强停
	public static final int STATE_FORCE_STOP = 0x5A;
	//超过10次
	public static final int STATE_MORE_RETRY = 0x6A;
	//最后一次等C
	public static final int STATE_WAIT_CHAR_C = 0x7A;
	//握手结束
	public static final int STATE_HANDSHAKE_OVER = 0x8A;

	//容错次数<=10次
	private int retryCount = 0;
	// 输出流，用于发送串口数据
	private OutputStream clientOutputStream;
	//下次的帧号从下标开始，因为下标0是文件的info，非文件体
	private int nextFrameNum = 1;
	//发送完成
	private boolean isComplete = false;

	//---针对接收端
	// 错误包数
	private int errorCount = 0;
	// 包序号
	private byte blocknumber = 0x01;

	//第一次收到C 标志
	private boolean firstGetC = true;
	//正式开始下标是1的帧
	private boolean startFirstFrame = false;
	//握手结束标志
	private boolean handshakeOver = false;

	//是否是主动开始发文件的
	public static boolean initiativeStart=false;


	public Ymodem() {

	}

	public void setClientOutputStream(OutputStream clientOutputStream) {
		this.clientOutputStream = clientOutputStream;
	}

	/**
	 * @param inputStream
	 * @return
	 */
	public static List<byte[]> getPackage(InputStream inputStream, String name, double size) {
		List<byte[]> byteList = new ArrayList<>();

		//第 0 帧 head frame
		byte[] head = new byte[133];
		head[0] = SOH;
		head[1] = (byte) 0;
		head[2] = (byte) ~head[1];
		byte[] data = new byte[128];
		byte[] info = (name + '\0' + (int)size).getBytes();//文件名”，“空字符”“文件大小”  空字符'\0'隔开
		for (int j = 0; j < info.length; j++) {
			data[j] = info[j];
		}
		for (int i = info.length; i < 128; i++) {
			data[i] = 0x00;
		}
		System.arraycopy(data, 0, head, 3, data.length);
		System.arraycopy(CRC16.calcByteArray(data), 0, head, 131, 2);//crc填充131 132
		byteList.add(head);

		//第 1 帧到最后 rest frame
		int blockNumber = 1;
		int nbytes;
		//封包

		byte[] sector = new byte[SECTOR_SIZE];
		try {
			while ((nbytes = inputStream.read(sector)) > 0) {
				//数据帧长度为1+1+1+1024+2=1029
				byte[] temp = new byte[1029];
				//最后一份不足1024的用0补足
				if (nbytes < SECTOR_SIZE) {
					for (int i = nbytes; i < SECTOR_SIZE; i++) {
						sector[i] = 0x00;
					}
				}
				temp[0] = STX;
				temp[1] = (byte) blockNumber;
				temp[2] = (byte) ~blockNumber;
				System.arraycopy(sector, 0, temp, 3, sector.length);
				System.arraycopy(CRC16.calcByteArray(sector), 0, temp, 1027, 2);
//				Log.e("------------原始---------", String.format("head%s   block%s   _block%s  crc_L%s   crc_H%s", temp[0], temp[1], temp[2], temp[1027], temp[1028]));
				byteList.add(temp);
				blockNumber++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	/*	for (int i = 0; i < byteList.size(); i++) {
			if (i == 0) {
				Log.e("-------package-----", "getPackage: " + new String(byteList.get(i), 3, 130).trim());
				Log.e("-------package-----", "getPackage: " + Arrays.toString(byteList.get(i)));
			} else
				Log.e("-------package-----", "getPackage: " + Arrays.toString(byteList.get(i)));
		}*/
		return byteList;
	}


	/**
	 * 发送
	 * 正常结束或者强停的话会重置nextFrameNum、retryCount、isComplete
	 *
	 * @param packages
	 * @param flag
	 * @return true代表还要发，false代表此文件不发了
	 */
	public int send(List<byte[]> packages, int flag) {
		if (initiativeStart) {
			//不被停或不主动停就继续
			if (flag != CAN && flag != EOT) {
				//收到应答或者第一次就正常发
				if (flag == ACK || flag == CHAR_C) {
					//前三个if只有isComplete为真才触发
					if (flag == ACK && isComplete && !handshakeOver) {
						//不做重置标志操作，继续等待C
						return STATE_WAIT_CHAR_C;
					}
					if (flag == CHAR_C && isComplete) {
						try {
							clientOutputStream.write(makeEmptyFrame());
							handshakeOver = true;
						} catch (IOException e) {
							e.printStackTrace();
						}
						return STATE_HANDSHAKE_OVER;
					}
					if (flag == ACK && isComplete && handshakeOver) {
						resetSendState();
						return STATE_COMPLETE;
					}
					//开始发
					try {
						if (flag == CHAR_C && firstGetC) {//第一次收到C就发info帧
							clientOutputStream.write(packages.get(0));
							firstGetC = false;
						} else {//不是第一次收到C就发正常帧
							if (flag == ACK && !startFirstFrame) {
								retryCount = 0;//head的重发次数置0
								return STATE_WAIT_CHAR_C;
							} else {//包括ACK或者第二次C的情况
								isComplete = false;//没发完
								retryCount = 0;//重发次数置0
								startFirstFrame = true;
								if (nextFrameNum >= packages.size()) {
									isComplete = true;//文件发完了
									clientOutputStream.write(EOT);
								} else
									clientOutputStream.write(packages.get(nextFrameNum));
								nextFrameNum++;
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					//发完后帧号加一

				}
				//收到重发命令
				//发出去一帧帧号就加一，回滚的时候再减一
				if (flag == NAK) {
					if (!startFirstFrame) {//重试head帧
						retryCount++;
						if (retryCount < 10) {
							try {
								clientOutputStream.write(packages.get(0));
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							resetSendState();//重置状态
							return STATE_MORE_RETRY;
						}

					} else {//重试正常文件帧
						nextFrameNum--;//回滚一次
						retryCount++;
						isComplete = false;//没发完
						if (retryCount < 10) {
							try {
								if (nextFrameNum >= packages.size()) {
									//文件发完了
									isComplete = true;
									clientOutputStream.write(EOT);
								} else {
									clientOutputStream.write(packages.get(nextFrameNum));
									nextFrameNum++;
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							resetSendState();//重置状态
							return STATE_MORE_RETRY;
						}
					}
				}
			} else {
				resetSendState();//重置状态
				return STATE_FORCE_STOP;
			}
//		Log.e("---------------------", String.format("flag=%s   RetryCount=%s", flag, retryCount));
			return STATE_NEXT;
		}else
			return STATE_WAIT_CHAR_C;

	}


	/**
	 *
	 * @param initiative
	 */
	public static void setInitiativeStart(boolean initiative) {
		initiativeStart = initiative;
	}

	/**
	 * 文件发完、强停、重试次数超过十次触发状态重置
	 */
	public void resetSendState() {
		initiativeStart=false;
		firstGetC = true;//第一次拿到C
		nextFrameNum = 1;//强停，重置帧号
		retryCount = 0;//重发次数置0
		isComplete = false;//重置完成标志
		handshakeOver = false;//最后一次握手
		startFirstFrame = false;//是否开始下标是1的帧
	}

	/**
	 * 构建结为空包
	 *
	 * @return
	 */
	private byte[] makeEmptyFrame() {
		byte[] empty = new byte[133];
		empty[0] = SOH;
		empty[1] = (byte) 0;
		empty[2] = (byte) ~empty[1];
		byte[] data = new byte[128];
		System.arraycopy(data, 0, empty, 3, 128);
		System.arraycopy(CRC16.calcByteArray(data), 0, empty, 131, 2);
		return empty;
	}

	/**
	 * 接收方以NAK为信号，通知发送方发送第一帧
	 */
	public void requestFirstFrame() {
		try {
			clientOutputStream.write(CHAR_C);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getFrameNumber() {
		return nextFrameNum;
	}

	/**
	 * 接收STX单帧
	 *
	 * @param singleFrame
	 * @return
	 */
	public boolean reciveSTX(byte[] singleFrame) {
		boolean isCurrect = false;

		byte head = singleFrame[0];
		byte blockNum = singleFrame[1];
		byte _blockNum = singleFrame[2];
		byte[] data = new byte[SECTOR_SIZE];
		System.arraycopy(singleFrame, 3, data, 0, SECTOR_SIZE);
		byte crc_L = singleFrame[1027];
		byte crc_H = singleFrame[1028];


		try {
			if (head != EOT) {//发送方请求结束？
				byte[] crc = CRC16.calcByteArray(data);//计算收到的包的crc
				Log.e("--------STX--------",
						String.format("head=%s %s   blockNum=%s   _blockNum=%s   crc_L=%s  crc_H=%s "
								, head, head == STX, blockNum == blocknumber, _blockNum == ~blocknumber, crc[0] == crc_L, crc[1] == crc_H));
				if (head != STX || blockNum != blocknumber || _blockNum != ~blocknumber || crc[0] != crc_L || crc[1] != crc_H) {//前三字节及CRC检测，失败就重试
					if (errorCount++ < 10) {
						//请求重试
						clientOutputStream.write(NAK);
						isCurrect = false;
					} else {
						//结束
						clientOutputStream.write(CAN);
						errorCount = 0;
						// 包序号
						blocknumber = 0x01;
						isCurrect = false;
					}
				} else {//校验通过
					blocknumber++;
					errorCount = 0;
					clientOutputStream.write(ACK);
					isCurrect = true;
				}
			} else {//发送方强停过程（发送方——EOT——>接收方——ACK——>发送方）
				clientOutputStream.write(ACK);
				clientOutputStream.write(CHAR_C);//第三个C
				// 包序号
				blocknumber = 0x01;
				isCurrect = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isCurrect;
	}

	/**
	 * 接收SOH单帧
	 */
	public boolean reciveSOH(byte[] singleFrame) {
		boolean isCurrect = false;//是否正确

		byte head = singleFrame[0];
		byte blockNum = singleFrame[1];
		byte _blockNum = singleFrame[2];
		byte[] data = new byte[128];
		System.arraycopy(singleFrame, 3, data, 0, 128);
		byte crc_L = singleFrame[131];
		byte crc_H = singleFrame[132];


		try {
			byte[] crc = CRC16.calcByteArray(data);//计算收到的包的crc
			Log.e("--------SOH---------",
					String.format("head=%s %s   blockNum=%s   _blockNum=%s   crc_L=%s  crc_H=%s "
							, head, head == SOH, blockNum == 0x00, _blockNum == ~(0x00), crc[0] == crc_L, crc[1] == crc_H));
			if (blockNum == 0x00 && _blockNum == ~(0x00) && crc[0] == crc_L && crc[1] == crc_H) {//前三字节及CRC检测
				errorCount = 0;
				clientOutputStream.write(ACK);
				if (crc_L!=(byte)0||crc_H!=(byte)0) {//判断是否为最后一个全零包
					clientOutputStream.write(CHAR_C);//第二个C
				}
				isCurrect = true;

			} else {//校验不通过
				if (errorCount++ < 10) {
					//请求重试
					clientOutputStream.write(NAK);
					isCurrect = false;
				} else {
					//结束
					clientOutputStream.write(CAN);
					errorCount = 0;
					isCurrect = false;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return isCurrect;
	}

}
