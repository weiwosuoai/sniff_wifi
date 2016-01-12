package com.paibo.sniff.utils;

import java.util.Arrays;


/**
 * 工具类
 * 
 * @author Chen
 *
 */
public class Utils {

	/**
	 * 字节数组转16进制字符数组
	 * @param bytes
	 */
	public static char[] bytes2HexChars(byte[] bytes) {
		if(bytes == null) {
			return null;
		}
		char[] hexChar = new char[bytes.length * 2]; // 1byte -> 2character
		String hexStr = "";
		for(int i=0;i<bytes.length; i++) {
			hexStr = String.format("%02x", bytes[i]);
			hexChar[2 * i] = hexStr.charAt(0);
			hexChar[2* i + 1] = hexStr.charAt(1);
		}
		return hexChar;
	}
	
	/**
	 * 字节数组转16进制字符串
	 * @param bytes
	 * @return
	 */
	public static String bytes2HexStr(byte[] bytes) {
		char[] hexChars = bytes2HexChars(bytes);
		if(hexChars == null) {
			return null;
		}
		return String.valueOf(hexChars, 0, hexChars.length);
	}
	
	/**
	 * 16进制字符串转字节数组
	 * @param hexStr
	 * @return
	 */
	public static byte[] hexChars2bytes(char[] hexChars) {
		if(hexChars == null) {
			return null;
		}
		int len = hexChars.length; // 2 4 6
		byte[] bytes = new byte[len / 2]; // 1 2 3
		for(int i=0; i<len; i=i+2) {
			bytes[i / 2] = (byte) ((Character.digit(hexChars[i], 16) << 4) // byte[0] = 1 << 4 = 1 * 16
							+ Character.digit(hexChars[i + 1], 16)); // + 2 = 18
		}
		return bytes;
	}
	
	/**
	 * 16进制字符串转字节数组
	 * @param hexStr
	 * @return
	 */
	public static byte[] hexStr2bytes(String hexStr) {
		if(hexStr == null) {
			return null;
		}
		return hexChars2bytes(hexStr.toCharArray());
	}
	
	
	public static void main(String[] args) {
		byte[] bytes = new byte[]{
				-124, 0, -54, 1, -128, -10, 46, -78, -91, 34, -128, 113, 122, -28, 5, 74, 4, 0, -96, 8
		};
		String hexStr = "8400ca0180f62eb2a52280717ae4054a0400a008";
		
		char[] hexChars = hexStr.toCharArray();
		bytes = Utils.hexChars2bytes(hexChars);
		
//		hexStr = Utils.bytes2HexStr(bytes);
		
		System.out.println(Arrays.toString(bytes));
		System.out.println(hexStr);
	}
	
}
