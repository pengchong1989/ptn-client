package com.nms.ui.thread;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.wait.WaitDialog;

/**
 * 等待窗口线程
 * 
 * @author Administrator
 * 
 */
public class WaitWindowThread implements Runnable {
	
	private WaitDialog waitDialog = null;
	private Thread thread = null;
	private int lable = 0;//用于标记判断该进度条正常启动  0:未启动；1:启动
	
	public WaitWindowThread() {
		try {
			Thread.currentThread().setName("WaitWindowThread");
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	@Override
	public void run() {
		try {
			lable = 1;
			waitDialog.showWait();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * 线程暂停
	 */
	public void setSuspendFlag() {
		try {
			if(waitDialog != null){
				waitDialog.closeWait();
				waitDialog.getGif().setRun(false);
				lable = 0;
				waitDialog = null;
			}
			if(thread != null){
				if(!thread.isInterrupted()){
					thread.stop();
				}
				thread = null;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}finally{
			thread = null;
			lable = 0;
			waitDialog = null;
		}
		
	}

	/**
	 * 唤醒线程
	 */
	public  void setResume(JDialog dialog) {
		if (null == thread) {
			waitDialog = new WaitDialog(dialog);
			thread = new Thread(this);
		}
		thread.start();
		//SwingUtilities.invokeLater()在把可运行的对象放入队列后就返回，
	   //SwingUtilities.invokeLater(this);
      //ExceptionManage.infor("进度条开始", this.getClass());
	}
	
	/**
	 * 唤醒线程
	 */
	public  void setResume(JPanel dialog) {
		if (null == thread) {
			waitDialog = new WaitDialog(dialog);
			thread = new Thread(this);
		}
		thread.start();
	}
	
	/**
	 * 唤醒线程
	 */
	public  void setResume() {
		if (null == thread) {
			waitDialog = new WaitDialog();
			thread = new Thread(this);
		}
		thread.start();
		//SwingUtilities.invokeLater()在把可运行的对象放入队列后就返回，
	//	SwingUtilities.invokeLater(this);
		ExceptionManage.infor("进度条开始", this.getClass());
	}
	
	public int getLable() {
		return lable;
	}

	public void setLable(int lable) {
		this.lable = lable;
	}
}
