package com.paibo.sniff.wifi.model;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import com.paibo.sniff.utils.Utils;

public final class WlanFrame {

	private static final String BROAD_CAST_ADDR = "ffffffffffff";
	public final static int FC_LEN = 2; /* 单位：字节（B,byte） */
	public final static int DI_LEN = 2;
	public final static int ADDR_LEN = 6;
	public final static int SC_LEN = 2;
	
	private byte[] origData;
	
	private FrameControl frameControl;
	private byte[] durationId;
	private byte[] addr1;
	private byte[] addr2;
	private byte[] addr3;
	private Wep wep;
	private byte[] sequenceControl;
	private byte[] addr4;
	
	private boolean parsed;
	
	public WlanFrame() {
		this.frameControl = new FrameControl();
		this.durationId = new byte[DI_LEN];
		this.addr1 = new byte[ADDR_LEN];
		this.addr2 = new byte[ADDR_LEN];
		this.addr3 = new byte[ADDR_LEN];
		this.sequenceControl = new byte[SC_LEN];
		this.addr4 = new byte[ADDR_LEN];
		this.wep = new Wep();
	}

	public WlanFrame(byte[] origData) {
		this();
		this.origData = origData;
	}
	
	public FrameControl getFrameControl() {
		return frameControl;
	}

	public void setFrameControl(FrameControl frameControl) {
		this.frameControl = frameControl;
	}

	public int getDurationId() {
		return ByteBuffer.wrap(this.durationId)
							.order(ByteOrder.LITTLE_ENDIAN)
							.getShort();
	}

	public void setDurationId(byte[] durationId) {
		this.durationId = durationId;
	}

	public String getAddr1() {
		return Utils.bytes2HexStr(this.addr1);
	}

	public void setAddr1(byte[] addr1) {
		this.addr1 = addr1;
	}

	public String getAddr2() {
		return Utils.bytes2HexStr(this.addr2);
	}

	public void setAddr2(byte[] addr2) {
		this.addr2 = addr2;
	}

	public String getAddr3() {
		return Utils.bytes2HexStr(this.addr3);
	}

	public void setAddr3(byte[] addr3) {
		this.addr3 = addr3;
	}

	public int getSequenceControl() {
		return ByteBuffer.wrap(this.sequenceControl)
							.getShort();
	}

	public void setSequenceControl(byte[] sequenceControl) {
		this.sequenceControl = sequenceControl;
	}

	public String getAddr4() {
		return Utils.bytes2HexStr(this.addr4);
	}

	public void setAddr4(byte[] addr4) {
		this.addr4 = addr4;
	}
	
	public Wep getWep() {
		return wep;
	}
	
	public void setWep(Wep wep) {
		this.wep = wep;
	}

	public static final class FrameControl {
		List fields;
		static byte[] fieldsLen;
		
		static {
			fieldsLen = new byte[]{
				2, 2, 4, 1, 1, 1, 1, 1, 1, 1, 1
			};
		}
		
		public final static int PROTOCOL_VERSION_DEFAULT = 0;
		
		public final class FrameType {
			public final static int TYPE_MGR = 0x00;
			public final static int TYPE_CTRL = 0x01;
			public final static int TYPE_DATA = 0x02;
			
			public final class TypeMgr {
				public final static int ASSO_REQ = 0x00;
				public final static int ASSO_RES = 0x01;
				public final static int REASSO_REQ = 0x02;
				public final static int REASSO_RES = 0x03;
				public final static int PROBE_REQ = 0x04;
				public final static int PROBE_RES = 0x05;
				public final static int BEACON = 0x08;
				public final static int ATIM = 0x09;
				public final static int DISASSO = 0x0a;
				public final static int AUTH = 0x0b;
				public final static int DEAUTH = 0x0c;
			}
			
			public final class TypeCtrl {
				public final static int PS_POLL = 0x0a;
				public final static int RTS = 0x0b;
				public final static int CTS = 0x0c;
				public final static int ACK = 0x0d;
				public final static int CF_END = 0xe;
				public final static int CF_END_ACK = 0x0f;
			}
			
			public final class TypeData {
				public final static int NORMAL = 0x00;
				public final static int ACK = 0x01;
				public final static int POLL = 0x02;
				public final static int ACK_POLL = 0x03;
				public final static int NO_DATA = 0x04;
				public final static int NO_DATA_ACK = 0x05;
				public final static int NO_DATA_POLL = 0x06;
				public final static int NO_DATA_ACK_POLL = 0x07;
			}
		}
		
		public final class DsStatus {
			public final static int TO_DS_NOT = 0x00;
			public final static int TO_DS = 0x01;
			public final static int FROM_DS_NOT = 0x00;
			public final static int FROM_DS = 0x01;
		}
		
		public FrameControl() {
			fields = new ArrayList();
			for(int i=0; i<fieldsLen.length; i++) {
				fields.add(0);
			}
		}
		
		public FrameControl(byte[] val) {
			if(val == null || val.length != FC_LEN) {
				throw new IllegalArgumentException("帧字段长度错误，"
						+ "应该为 " + FC_LEN + " 字节，"
						+ " 实际长度为 " + val.length + "字节");
			}
			fields = new ArrayList();
			fields.add(val[0] & 0x03);
			fields.add((val[0] & 0x0c) >> 2);
			fields.add((val[0] & 0xf0) >> 4);
			for(int i=0; i<8; i++) {
				fields.add((val[1] & (byte)(Math.pow(2, i))) >> i);
			}
		}
		
		public int getProtocolVersion() {
			return (Integer) fields.get(0);
		}
		
		public int getType() {
			return (Integer) fields.get(1);
		}
		
		public int getSubType() {
			return (Integer) fields.get(2);
		}
		
		public boolean toDs() {
			return (Integer) fields.get(3) == 1;
		}
		
		public boolean fromDs() {
			return (Integer) fields.get(4) == 1;
		}
		
		public boolean hasMoreFrag() {
			return (Integer)fields.get(5) == 1;
		}
		
		public boolean isRetryFrame() {
			return (Integer)fields.get(6) == 1;
		}
		
		public boolean isPowerSave() {
			return (Integer)fields.get(7) == 1;
		}
		
		public boolean hasMoreData() {
			return (Integer)fields.get(8) == 1;
		}
		
		public boolean isEncrypted() {
			return (Integer)fields.get(9) == 1;
		}
		
	}
	
	public final class Wep {
		
		public final static int IV_LEN = 4; /* 单位：字节（B,byte） */
		public final static int ICV_LEN = 4;
		
		byte[] iv;
		byte[] data;
		byte[] icv;
		
		public Wep() {
			this.iv = new byte[IV_LEN];
			this.data = new byte[]{};
			this.icv = new byte[ICV_LEN];
		}
		
		public byte[] getData() {
			return data;
		}

		public void setData(byte[] data) {
			this.data = data;
		}
		
		public byte[] getIv() {
			return iv;
		}

		public void setIv(byte[] iv) {
			this.iv = iv;
		}

		public byte[] getIcv() {
			return icv;
		}

		public void setIcv(byte[] icv) {
			this.icv = icv;
		}

		public void load(byte[] d) throws WlanFrameParseException {
			if(d == null || d.length == 0) {
				return;
			}
			BufferedInputStream bis = null;
			try {
				bis = new BufferedInputStream(new ByteArrayInputStream(d));
				if(getFrameControl().isEncrypted()) {
					bis.read(this.iv);
				}
				int dLen = d.length - Wep.IV_LEN - Wep.ICV_LEN;
				byte[] dBuf = new byte[dLen];
				bis.read(dBuf);
				this.data = dBuf;
				bis.read(this.icv);
			} catch (IOException e) {
				throw new WlanFrameParseException(e);
			} finally {
				if(bis!=null) {
					try {
						bis.close();
					} catch (IOException e) {
						throw new WlanFrameParseException(e);
					}
				}
			}
			 
		}
		
	}
	
	public void parse() throws WlanFrameParseException {
		BufferedInputStream bis = null;
		bis = new BufferedInputStream(new ByteArrayInputStream(origData));
		byte[] buf = new byte[FC_LEN];
		try {
			// FC
			bis.read(buf);
			FrameControl fc = new FrameControl(buf);
			setFrameControl(fc);
			
			// just parse data frame(type=data,subtype=data)
			if(this.frameControl.getType() == FrameControl.FrameType.TYPE_DATA) {
				bis.read(this.durationId);
				bis.read(this.addr1);
				bis.read(this.addr2);
				bis.read(this.addr3);
				bis.read(this.sequenceControl);
				if (isWd()) {
					bis.read(this.addr4);
				}
				byte[] d = new byte[origData.length - (FC_LEN + DI_LEN + ADDR_LEN * (isWd()?4:3) + SC_LEN)];
				bis.read(d);
				this.wep.load(d);
			}
			parsed = true;
		} catch (IOException e) {
			throw new WlanFrameParseException(e);
		} finally {
			if(bis!=null) {
				try {
					bis.close();
				} catch (IOException e) {
					throw new WlanFrameParseException(e);
				}
			}
		}
	}

	/** 是否是无线分布式局域网（DS=(1,1)） */
	private boolean isWd() {
		return getFrameControl().toDs() && getFrameControl().fromDs();
	}
	
	private void checkParsed() throws WlanFrameParseException {
		if(!parsed) {
			throw new WlanFrameParseException("先调用 parse() 进行解析才能获取数据");
		}
	}
	
	/** 返回高层数据 
	 * @throws WlanFrameParseException */
	public byte[] getData() throws WlanFrameParseException {
		checkParsed();
		return getWep().getData();
	}
	
	/** 是否是普通数据帧 
	 * @throws WlanFrameParseException */
	public boolean isNorDataFrame() throws WlanFrameParseException {
		checkParsed();
		return this.frameControl.getType() == FrameControl.FrameType.TYPE_DATA;
	}
	
	/** 数据是否加密 
	 * @throws WlanFrameParseException */
	public boolean encrypted() throws WlanFrameParseException {
		checkParsed();
		return getFrameControl().isEncrypted();
	}
	
	/** 是否从AP发出的数据 
	 * @throws WlanFrameParseException */
	public boolean transFromAp() throws WlanFrameParseException {
		checkParsed();
		return getFrameControl().fromDs();
	}
	
	/** 是否是发往AP的数据 
	 * @throws WlanFrameParseException */
	public boolean transToAp() throws WlanFrameParseException {
		checkParsed();
		return getFrameControl().toDs();
	}
	
	/** 是否是广播帧 
	 * @throws WlanFrameParseException */
	public boolean broadcast() throws WlanFrameParseException {
		return BROAD_CAST_ADDR.equals(getDa());
	}
	
	/** 返回源地址 
	 * @throws WlanFrameParseException */
	public String getSa() throws WlanFrameParseException {
		checkParsed();
		if(!this.frameControl.toDs() 
				&& !this.frameControl.fromDs()) {
			return getAddr2();
		} else if((!this.frameControl.toDs() && this.frameControl.fromDs()
				|| (!this.frameControl.fromDs() && this.frameControl.toDs()))) {
			return getAddr3();
		} else {
			return getAddr4();
		}
	}
	
	/** 返回目标地址 
	 * @throws WlanFrameParseException */
	public String getDa() throws WlanFrameParseException {
		checkParsed();
		if(this.frameControl.toDs() && this.frameControl.fromDs()) {
			return getAddr3();
		} else if(this.frameControl.toDs() && !this.frameControl.fromDs()) {
			return getAddr2();
		} else {
			return getAddr1();
		}
	}
	
	/** 返回发送器的地址 
	 * @throws WlanFrameParseException */
	public String getTa() throws WlanFrameParseException {
		checkParsed();
		return getAddr2();
	}
	
	/** 返回接收器的地址 
	 * @throws WlanFrameParseException */
	public String getRa() throws WlanFrameParseException {
		checkParsed();
		return getAddr1();
	}
	
	public String getBSSID() throws WlanFrameParseException {
		checkParsed();
		if(!this.frameControl.toDs()
				&& !this.frameControl.fromDs()) {
			return getAddr3();
		} else if (!this.frameControl.toDs()
				&& this.frameControl.fromDs()) {
			return getAddr2();
		} else if (this.frameControl.toDs()
				&& !this.frameControl.fromDs()) {
			return getAddr1();
		}
		return null;
	}
	
	public class WlanFrameParseException extends Exception {

		public WlanFrameParseException(String message, Throwable cause) {
			super(message, cause);
		}

		public WlanFrameParseException(String message) {
			super(message);
		}

		public WlanFrameParseException(Throwable cause) {
			super(cause);
		}
	}
	
}
