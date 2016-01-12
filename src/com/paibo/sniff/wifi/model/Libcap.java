package com.paibo.sniff.wifi.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;


/**
 * libcap保存截取和注入网络流量数据的文件格式。该格式是事实上的保存网络流量数据的文件格式标准。
 * 
 * <p>Libcap文件内容包含三部分：Global Header以及由Packet Header和Packet Data构成的Packet。</p>
 * 
 * <p>Global Header包含libcap文件信息和捕获的网络流量数据的信息。其中，
 * 文件信息包括文件格式的标识（根据该标识可以是被该文件类型）、文件格式的版本；
 * 捕获的网络流程数据包括截取的数据的网络类型、网络流量发生的时间等。</p>
 * 
 * <p>具体的文件格式字段如下：</p>
 * 
 * TODO 补全文件字段格式
 * 
 * @author Chen
 *
 */
public final class Libcap {
	
	private File file;
	
	public Libcap() {
		
	}
	
	public Libcap(File file) throws LibcapParseException {
		this.file = file;
		if(!this.file.isFile()) {
			throw new LibcapParseException("文件: " + file.getName() + " 不存在或不是文件");
		}
		if(!isLibcapFile(file)) {
			throw new LibcapParseException("文件: " + file.getName() + " 不是Libcap文件");
		}
	}
	
	public Libcap(String fileName) throws LibcapParseException {
		this.file = new File(fileName);
		if(!this.file.isFile()) {
			throw new LibcapParseException("文件: " + file.getName() + " 不存在或不是文件");
		}
		if(!isLibcapFile(file)) {
			throw new LibcapParseException("文件: " + file.getName() + " 不是Libcap文件");
		}
	}
	
	public boolean isLibcapFile(File file) throws LibcapParseException {
		if(file==null || !file.isFile()) {
		}
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			byte[] buf = new byte[GlobalHeader.MAGIC_NUM_LEN];
			bis.read(buf);
			int fileType = ByteBuffer.wrap(buf).getInt();
			if(fileType == GlobalHeader.FILE_TYPE) {
				return true;
			}
		} catch (FileNotFoundException e1) {
			throw new LibcapParseException("文件: " + file.getName() + " 不存在", e1);
		} catch (IOException e2) {
			throw new LibcapParseException("读取文件: " + file.getName() + " 错误", e2);
		} finally {
			if(bis!=null) {
				try {
					bis.close();
				} catch (IOException e) {
					throw new LibcapParseException("文件: " + file.getName() + " 关闭失败", e);
				}
			}
		}
		return false;
	}
	
	public final class GlobalHeader {
		
		final static byte MAGIC_NUM_LEN = 4;
		final static byte VER_MAJOR_LEN = 2;
		final static byte VER_MINOR_LEN = 2;
		final static byte THIS_ZONE_LEN = 4;
		final static byte SIG_FIGS_LEN = 4;
		final static byte SNAP_LENGTH = 4;
		final static byte NETWORK_LEN = 4;
		final static byte TOTAL_LEN = MAGIC_NUM_LEN + VER_MAJOR_LEN
										+ VER_MINOR_LEN + THIS_ZONE_LEN
										+ SIG_FIGS_LEN + SNAP_LENGTH
										+ NETWORK_LEN;
		
		public final static int FILE_TYPE = 0xd4c3b2a1;
		final static int FILE_V_MAX = 2;
		final static int FILE_V_MIN = 4;
		
		private byte[] magicNumber;
		private byte[] versionMajor;
		private byte[] versionMinor;
		private byte[] thisZone;
		private byte[] sigFigs;
		private byte[] snapLen;
		private byte[] network;
		
		public GlobalHeader() {
			this.magicNumber = new byte[MAGIC_NUM_LEN];
			this.versionMajor = new byte[VER_MAJOR_LEN];
			this.versionMinor = new byte[VER_MINOR_LEN];
			this.thisZone = new byte[THIS_ZONE_LEN];
			this.sigFigs = new byte[SIG_FIGS_LEN];
			this.snapLen = new byte[SNAP_LENGTH];
			this.network = new byte[NETWORK_LEN];
		}

		public byte[] getMagicNumber() {
			return magicNumber;
		}
		
		public int getMagicNum() {
			return ByteBuffer.wrap(this.magicNumber)
								.getInt();
		}

		public byte[] getVersionMajor() {
			return versionMajor;
		}
		
		public int getVerMajor() {
			return ByteBuffer.wrap(this.versionMajor)
								.order(ByteOrder.LITTLE_ENDIAN)
								.getShort();
		}

		public byte[] getVersionMinor() {
			return versionMinor;
		}
		
		public int getVerMinor() {
			return ByteBuffer.wrap(this.versionMinor)
							.order(ByteOrder.LITTLE_ENDIAN)
							.getShort();
		}

		public byte[] getThisZone() {
			return thisZone;
		}

		public byte[] getSigFigs() {
			return sigFigs;
		}

		public byte[] getSnapLen() {
			return snapLen;
		}
		
		public int getSnapLength() {
			return ByteBuffer.wrap(this.snapLen)
								.order(ByteOrder.LITTLE_ENDIAN)
								.getInt();
		}

		public byte[] getNetwork() {
			return network;
		}
		
		public int getNetType() {
			return ByteBuffer.wrap(this.network)
								.order(ByteOrder.LITTLE_ENDIAN)
								.getInt();
		}
		
	}
	
	public final class Packet {
		
		public final class Header {
			
			final static byte TS_SEC_LEN = 4;
			final static byte TS_USEC_LEN = 4;
			final static byte INCL_LEN = 4;
			final static byte ORIG_LEN = 4;
			final static byte TOTAL_LEN = TS_SEC_LEN + TS_USEC_LEN
											+ INCL_LEN + ORIG_LEN;
			
			private byte[] tsSec;
			private byte[] tsUsec;
			private byte[] inclLen;
			private byte[] origLen;
			
			public Header() {
				this.tsSec = new byte[TS_SEC_LEN];
				this.tsUsec = new byte[TS_USEC_LEN];
				this.inclLen = new byte[INCL_LEN];
				this.origLen = new byte[ORIG_LEN];
			}
			
			public byte[] getTsSec() {
				return tsSec;
			}

			public byte[] getTsUsec() {
				return tsUsec;
			}

			public byte[] getInclLen() {
				return inclLen;
			}
			
			public int getInclLength() {
				return ByteBuffer.wrap(getHeader().getInclLen())
						.order(ByteOrder.LITTLE_ENDIAN)
						.getInt();
			}

			public byte[] getOrigLen() {
				return origLen;
			}
			
		}
		
		public Packet() {
			
		}
		
		public Packet(byte[] hBytes) {
			header = new Header();
			System.arraycopy(hBytes, 0, header.tsSec, 0, header.TS_SEC_LEN);
			System.arraycopy(hBytes, header.TS_SEC_LEN, header.tsUsec, 0, header.TS_USEC_LEN);
			System.arraycopy(hBytes, header.TS_SEC_LEN + header.TS_USEC_LEN, header.inclLen, 0, header.INCL_LEN);
			System.arraycopy(hBytes, header.TS_SEC_LEN + header.TS_USEC_LEN + header.INCL_LEN, header.origLen, 0, header.ORIG_LEN);
		}
		
		private Header header;
		
		private byte[] data;

		public Header getHeader() {
			return header;
		}

		public byte[] getData() {
			return data;
		}
	}
	
	private GlobalHeader gHeader;
	
	private List<Packet> packets;
	
	/**
	 * 版本格式：x.y.z
	 * @return
	 * @throws LibcapParseException
	 */
	public String getFileVersion() throws LibcapParseException {
		getGlobalHeader();
		return gHeader.getVerMajor() + "." + gHeader.getVerMinor();
	}
	
	public int getPacketCount() throws LibcapParseException {
		if(packets == null) {
			return getPackets().size();
		}
		return 0;
	}
	
	public Packet getPacket(int pos) throws LibcapParseException {
		if(packets == null) {
			return getPackets().get(pos);
		}
		return null;
	}
	
	public GlobalHeader getGlobalHeader() throws LibcapParseException {
		if(this.gHeader == null) {
			this.gHeader = new GlobalHeader();
			BufferedInputStream bis = null;
			try {
				bis = new BufferedInputStream(new FileInputStream(file));
				bis.read(this.gHeader.getMagicNumber());
				bis.read(this.gHeader.getVersionMajor());
				bis.read(this.gHeader.getVersionMinor());
				bis.read(this.gHeader.getThisZone());
				bis.read(this.gHeader.getSigFigs());
				bis.read(this.gHeader.getSnapLen());
				bis.read(this.gHeader.getNetwork());
			} catch (FileNotFoundException e) {
				throw new LibcapParseException(e);
			} catch (IOException e) {
				throw new LibcapParseException(e);
			} finally {
				if(bis!=null) {
					try {
						bis.close();
					} catch (IOException e) {
						throw new LibcapParseException(e);
					}
				}
			}
		}
		return gHeader;
	}

	public List<Packet> getPackets() throws LibcapParseException {
		if(packets == null) {
			BufferedInputStream bis = null;
			try {
				bis = new BufferedInputStream(new FileInputStream(file));
				bis.skip(GlobalHeader.TOTAL_LEN);
				
				byte[] pHeader = new byte[Packet.Header.TOTAL_LEN];
				byte[] pData = null;
				int pHeaderLen = -1;
				int pDataLen = -1;
				while(true) {
					if(packets == null) {
						packets = new ArrayList<Packet>();
					}
					pHeaderLen = bis.read(pHeader);
					if(pHeaderLen < Packet.Header.TOTAL_LEN) {
						break;
					}
					Packet p = new Packet(pHeader);
					pDataLen = p.getHeader().getInclLength();
					pData = new byte[pDataLen];
					bis.read(pData);
					p.data = pData;
					packets.add(p);
				}
			} catch (FileNotFoundException e) {
				throw new LibcapParseException(e);
			} catch (IOException e) {
				throw new LibcapParseException(e);
			} finally {
				if(bis!=null) {
					try {
						bis.close();
					} catch (IOException e) {
						throw new LibcapParseException(e);
					}
				}
			}
		}
		return packets;
	}

	public class LibcapParseException extends Exception {

		public LibcapParseException(String message, Throwable cause) {
			super(message, cause);
		}

		public LibcapParseException(String message) {
			super(message);
		}

		public LibcapParseException(Throwable cause) {
			super(cause);
		}
		
	}
	
}
