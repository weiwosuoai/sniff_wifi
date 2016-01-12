package com.paibo.sniff.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;
import android.view.KeyEvent;

/**
 * 执行 Shell 指令的工具类
 * 
 * <p>借助 Runtime.getRuntime()。exe() ，开启独立的 linux 进程执行指令</p>
 * <p>注意：使用上述方法执行指令时，需要从开启的进程中不断读取进程执行的输出（包括错误输出）。</p>
 * <p>如果没有及时读取，可能会导致进程缓存溢出而终止执行。表现的结果是，执行指令的进程挂起（Suspended）。</p>
 */
public class ShellUtil {
	
	private final static String TAG = ShellUtil.class.getSimpleName();
	
	private final static String ROLE_ROOT = "su";
	private final static String ROLE_GUEST = "sh";
	
	/**
	 * @see {@link ShellUtil#execommand(List, boolean, boolean)}}
	 */
	public static CommandResult execommand(String cmd, boolean isRoot, boolean needRs) {
		List<String> cmds = new ArrayList<String>();
		cmds.add(cmd);
		return execommand(cmds, isRoot, needRs);
	}
	
	/**
	 * 执行 Shell 命令
	 * @param cmds 要执行的命令列表
	 * @param isRoot 是否需要以 root 身份执行命令
	 * @param needRs 是否需要返回执行结果
	 * @return 执行结果  @see {@link CommandResult}
	 */
	public static CommandResult execommand(List<String> cmds, boolean isRoot, boolean needRs) {
		
		Process proc = null;
		OutputStream stdIn = null;
		InputStream stdOut = null;
		InputStream stdErr = null;
		String outMsg = "";
		String errMsg = "";
		int rsCode = -1;
		StreamGobbler outGobbler = null;
		StreamGobbler errGobbler = null;
		
		try {
			proc = Runtime.getRuntime().exec(isRoot?ROLE_ROOT:ROLE_GUEST);
			stdIn = proc.getOutputStream();
			stdOut = proc.getInputStream();
			stdErr = proc.getErrorStream();

			/**
			 * 开始执行指令前启动缓存读取线程，防止进程阻塞
			 */
			outGobbler = new StreamGobbler(stdOut, outMsg, StreamGobbler.TYPE_OUT);
			errGobbler = new StreamGobbler(stdErr, errMsg, StreamGobbler.TYPE_ERR);
			outGobbler.start();
			errGobbler.start();
			
			for(String cmd:cmds) {
				stdIn.write(cmd.getBytes());
				stdIn.write("\n".getBytes());	// 注意每条指令执行完毕后输出换行标识（这是本地方法中的要求）
				stdIn.flush();	// 每次写入要生效都要flush，否则线程一直等待
			}
			stdIn.write("exit\n".getBytes()); 
			stdIn.flush(); 
			
			rsCode = proc.waitFor();
			
			/**
			 * 执行指令的主线程必须等待消化主线程的缓存的线程执行完毕后才能继续，
			 * 否则可能出现缓存没有及时读取完毕的情况
			 */
			outGobbler.join();
			errGobbler.join();
			
			Log.i(TAG, "rsCode: " + rsCode);
			
			// 终止开启的进程
			proc.destroy();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if(stdIn!=null) {
				try {
					stdIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return needRs?new CommandResult(rsCode, outMsg, errMsg):null;
	}
	
	
	/**
	 * 是否具有 root 权限
	 * 
	 * 备注：Linux echo 指令用于输出信息，这是 echo root 是向标准输出中输出root字符串，
	 * 需要注意的是，这里通过 execommand(sting,boolean,boolean)，第二个参数设为true表示，
	 * 以root身份执行该指令，如果该指令执行成功，则表明当前应用被赋予root权限
	 * 
	 * @return true 具有 root权限， false 没有 root权限
	 */
	public static boolean hasRootPermission() {
		return execommand("echo root", true, true).getRsCode() == CommandResult.RESULT_SUCCESS;
	}
	
	
	/**
	 * 申请 root权限
	 * @return true 获取root权限成功, false 获取root权限失败
	 */
	public static boolean upgradeRootPermission(String pkg) {
		String cmd = "chmod 755 " + pkg;
		return execommand(cmd, true, true).getRsCode() == CommandResult.RESULT_SUCCESS;
	}
	
	
	/**
	 * 用于读取进程执行过程中缓存中产生的数据
	 * 防止进程阻塞
	 */
	private static class StreamGobbler extends Thread {
		
		public static final String TYPE_OUT = "OUT - ";
		public static final String TYPE_ERR = "ERR - ";
		
		private String out;
		private InputStream is;
		private String type;
		
		public StreamGobbler(InputStream is, String out, String type) {
			super();
			this.is = is;
			this.out = out;
			this.type = type;
		}

		@Override
		public void run() {
			super.run();
			Log.i(TAG, type + " started");
			BufferedReader br = null;
			br = new BufferedReader(new InputStreamReader(is));
			StringBuffer temp = new StringBuffer();
			String line = "";
			try {
				while((line = br.readLine())!=null) {
					temp.append(line);
				}
				out = temp.toString();
				Log.i(TAG, type + out);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(br!=null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
	/**
	 * Shell 命令的执行结果
	 */
	public static class CommandResult {
		/**
		 * 进程退出代码
		 * eg. 0  正常退出
		 */
		private int rsCode;
		/**
		 * 命令执行过程中产生的输出信息
		 */
		private String outMsg;
		/**
		 * 命令执行过程中产生的错误信息
		 */
		private String errMsg;

		public final static int RESULT_SUCCESS = 0;
		
		public CommandResult(int rsCode, String outMsg, String errMsg) {
			super();
			this.rsCode = rsCode;
			this.outMsg = outMsg;
			this.errMsg = errMsg;
		}

		public CommandResult() {
			super();
		}

		public int getRsCode() {
			return rsCode;
		}

		public String getOutMsg() {
			return outMsg;
		}

		public String getErrMsg() {
			return errMsg;
		}

		@Override
		public String toString() {
			return "CommandResult [rsCode=" + rsCode + ", outMsg=" + outMsg
					+ ", errMsg=" + errMsg + "]";
		}
		
	}

}
