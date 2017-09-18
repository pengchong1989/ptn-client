package com.nms.ui.manager.wait;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysTitle;

public class WaitDialog extends PtnDialog {

	private static final long serialVersionUID = 1L;
	
	public WaitDialog() {
		try {
			this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_PLEASE_WAIT));
			this.setModal(true);
			this.initComponent();
			this.setLayout();
			addListen();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	public WaitDialog(JDialog dialog) {
		try {
			this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_PLEASE_WAIT));
			this.setModal(true);
			this.initComponent();
			this.setLayout();
			addListen();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	public WaitDialog(JPanel dialog) {
		try {
			this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_PLEASE_WAIT));
			this.setModal(true);
			this.initComponent();
			this.setLayout();
			addListen();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void addListen() {
//		this.addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosing(WindowEvent e) {
//				setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
//			}
//		});
	}

	public void showWait() {
		UiUtil.showWindow(this, 350, 80);
	}

	public void closeWait() {
		this.dispose();
	}

	/**
	 * 设置布局
	 */
	private void setLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 300 };
		componentLayout.columnWeights = new double[] { 0 };
		componentLayout.rowHeights = new int[] { 50 };
		componentLayout.rowWeights = new double[] { 0 };
		this.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(20, 10, 5, 5);
		componentLayout.setConstraints(this.gif, c);
		this.add(this.gif);

	}

	/**
	 * 初始化控件
	 * 
	 * @throws Exception
	 */
	private void initComponent() throws Exception {
		this.gif = new Gif(WaitDialog.class.getResourceAsStream("/com/nms/ui/images/gif/13221819.gif"), 105);
	}

	private Gif gif;

	public Gif getGif() {
		return gif;
	}

	public void setGif(Gif gif) {
		this.gif = gif;
	}
	
}
