package com.paibo.sniff.wifi.model;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Radiotap {

	private byte[] origData;
	
	private Header header;
	
	private byte[] data;

	public Radiotap() {
		this.header = new Header();
		this.data = new byte[]{};
	}
	
	public Radiotap(byte[] origData) {
		this();
		this.origData = origData;
	}
	
	public Header getHeader() throws RadiotapParseException {
		readHeader();
		return header;
	}

	public byte[] getData() throws RadiotapParseException {
		readData();
		return data;
	}

	public void read() throws RadiotapParseException {
		readHeader();
		readStandPresent();
		readExtension();
		readData();
	}

	public void readHeader() throws RadiotapParseException {
		BufferedInputStream bis = null;
		if(this.header == null) {
			this.header = new Header();
		}
		try {
			bis = new BufferedInputStream(new ByteArrayInputStream(origData));
			readHeader(bis);
		} finally {
			if(bis!=null) {
				try {
					bis.close();
				} catch (IOException e) {
					throw new RadiotapParseException(e);
				}
			}
		}
	}
	
	private void readHeader(InputStream is) throws RadiotapParseException {
		try {
			is.read(this.header.itVersion);
			is.read(this.header.itPad);
			is.read(this.header.itLen);
			is.read(this.header.itPresent);
		} catch (IOException e) {
			throw new RadiotapParseException(e);
		}
	}

	public void readStandPresent() throws RadiotapParseException {
		BufferedInputStream bis = null;
		if(this.header==null) {
			this.header = new Header();
		}
		try {
			bis = new BufferedInputStream(new ByteArrayInputStream(origData));
			readStandPresent(bis);
		} finally {
			if(bis!=null) {
				try {
					bis.close();
				} catch (IOException e) {
					throw new RadiotapParseException(e);
				}
			}
		}
	}
	
	private void readStandPresent(InputStream is) throws RadiotapParseException {
		try {
			is.skip(this.header.itVersion.length + this.header.itPad.length + this.header.itLen.length);
			is.read(this.header.itPresent);
			
			byte[] pb = ByteBuffer.wrap(this.header.itPresent)
					.order(ByteOrder.LITTLE_ENDIAN)
					.array();
			
			int standBitFlag = 0;
			int standBitPos = 0;
			byte[] standBitVal = null;
			for(int byteIdx=0; byteIdx<pb.length; byteIdx++) {
				for(int bitIdx=0; bitIdx<8; bitIdx++) {
					standBitFlag = (pb[byteIdx] & (byte)(Math.pow(2, bitIdx))) >> bitIdx;
						if(standBitFlag == 1) {
							standBitPos = byteIdx * 8 + bitIdx;
							standBitVal = this.header.standBit.data.get(standBitPos);
							is.read(standBitVal);
							this.header.standBit.len += standBitVal.length;
						}
				}
			}
		} catch (IOException e) {
			throw new RadiotapParseException(e);
		}
	} 

	public void readExtension() throws RadiotapParseException {
		BufferedInputStream bis = null;
		if(this.header==null) {
			this.header = new Header();
		}
		try {
			bis = new BufferedInputStream(new ByteArrayInputStream(origData));
			readExtension(bis);
		} finally {
			if(bis!=null) {
				try {
					bis.close();
				} catch (IOException e) {
					throw new RadiotapParseException(e);
				}
			}
		}
	}
	
	private void readExtension(InputStream is) throws RadiotapParseException {
		try {
			// skip
			if(this.header.extensions.size() == 0) {
				skipStandHeader(is, false);
			} else {
				skipStandHeader(is);
				is.skip(this.header.extensions.size() * 4);
				
			}
			
			// read
			byte[] ext = new byte[4];
			is.read(ext);
			if(this.header.extensions.size() == 0) {
				this.header.itPresent = ext;
			} else {
				this.header.extensions.add(ext);
			}
			
			// check
			byte[] presentBits = ByteBuffer.wrap(ext)
									.order(ByteOrder.LITTLE_ENDIAN)
									.array();
			boolean hasExtension = (presentBits[0] & 0x80) >> 6 == 1;
			if(hasExtension) {
				readExtension(is);
			}
		} catch (IOException e) {
			throw new RadiotapParseException(e);
		}
	}
	
	public void readData() throws RadiotapParseException {
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new ByteArrayInputStream(origData));
			readData(bis);
		} finally {
			if(bis!=null) {
				try {
					bis.close();
				} catch (IOException e) {
					throw new RadiotapParseException(e);
				}
			}
		}
	}

	private void readData(InputStream is) throws RadiotapParseException {
		readHeader();
		int headerLen = ByteBuffer.wrap(this.header.itLen)
         		.order(ByteOrder.LITTLE_ENDIAN)
         		.getShort();
		int dataLen = this.origData.length - headerLen;
		try {
			is.skip(headerLen);
			byte[] block = new byte[dataLen];
			is.read(block);
			this.data = block;
		} catch (IOException e) {
			throw new RadiotapParseException(e);
		}
	}
	
	/**
	 * 包含itPresent及数据部分
	 */
	private void skipStandHeader(InputStream is) throws RadiotapParseException {
		skipStandHeader(is, true);
	}
	
	private void skipStandHeader(InputStream is, boolean itPresentInc) throws RadiotapParseException {
		readStandPresent(); // 跳过基本头
		try {
			is.skip(this.header.itVersion.length
					+ this.header.itPad.length
					+ this.header.itLen.length
					+ (itPresentInc?(this.header.itPresent.length):0)
					+ (itPresentInc?(this.header.standBit.len):0));
		} catch (IOException e) {
			throw new RadiotapParseException(e);
		}
	}		
	
	public final class Header {
		private byte[] itVersion;
		private byte[] itPad;
		private byte[] itLen;
		private byte[] itPresent;
		private StandBit standBit;
		private List<byte[]> extensions;
		
		public Header() {
			this.itVersion = new byte[1];
			this.itPad = new byte[1];
			this.itLen = new byte[2];
			this.itPresent = new byte[4];
			this.standBit = new StandBit();
			this.extensions = new ArrayList<byte[]>();
		}

		public int getItVersion() {
			return ByteBuffer.wrap(this.itVersion)
								.order(ByteOrder.LITTLE_ENDIAN)
								.get();
		}

		public int getItPad() {
			return ByteBuffer.wrap(this.itPad)
								.order(ByteOrder.LITTLE_ENDIAN)
								.get();
		}

		public int getItLen() {
			return ByteBuffer.wrap(this.itLen)
								.order(ByteOrder.LITTLE_ENDIAN)
								.getShort();
		}

		public int getItPresent() {
			return ByteBuffer.wrap(this.itPresent)
								.order(ByteOrder.LITTLE_ENDIAN)
								.getInt();
		}
		
		public StandBit getStandBit() {
			return standBit;
		}

		public List<byte[]> getExtensions() {
			return extensions;
		}
		
	}
	
	@Override
	public String toString() {
		return "Radiotap [origData=" + Arrays.toString(origData) + ", header="
				+ header + ", data=" + Arrays.toString(data) + "]";
	}

	/** 标准位数据 */
	public final static class StandBit {
		
		private int len; 					/** 标准位数据总长度 */
		private List<byte[]> data; 			/** 标准位数据 */
		private static byte[] dataLens; 	/** 各个标准位数据长度 */
		
		static {
			dataLens = new byte[]{
				8, 1, 1, 4, 	3, 1, 1, 2,
				2, 2, 1, 1, 	1, 1, 2, 0,
				0, 0, 0, 3,		8, 9, 0, 0,
				0, 0, 0, 0,		0, 0, 4, 0
			};
		}
		
		public StandBit() {
			data = new ArrayList<byte[]>();
			for(int i=0;i<dataLens.length;i++) {
				data.add(new byte[dataLens[i]]);
			}
		}

		public int getLen() {
			return len;
		}

		public List<byte[]> getData() {
			return data;
		}

		public static byte[] getDataLens() {
			return dataLens;
		}
		
	}
	
	
	public class RadiotapParseException extends Exception {

		public RadiotapParseException(String message, Throwable cause) {
			super(message, cause);
		}

		public RadiotapParseException(String message) {
			super(message);
		}

		public RadiotapParseException(Throwable cause) {
			super(cause);
		}
		
	}
	
}
