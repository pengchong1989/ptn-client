package com.nms.ui.manager.control;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jtattoo.plaf.BaseBorders.TextFieldBorder;
import com.nms.ui.manager.CheckingUtil;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.keys.StringKeysTip;

public class PtnPasswordField extends JPasswordField {

	private static final long serialVersionUID = 1L;

	private final boolean mustFill; // 是否必填
	private final JLabel lblMessage; // 提示文本控件
	private final JButton btnSave; // 保存按钮
	private final PtnDialog dialog;
	/** 文本框样式 */
	private final Color color = Color.RED; // 颜色
	private final Border textFieldBorder = new TextFieldBorder(); // 默认文本框边框
	private final Border border = BorderFactory.createLineBorder(color); // 带颜色的文本框边框
	private String errorMessage;
	private final int MAXLENGTH = 18;
	private final int MINLENGTH = 6;

	private int ADMINMINLENGTH = 0; // 最小值
	/**
	 * 
	 * 创建一个新的实例 String类型的 PtnPasswordField.
	 * 
	 * @param mustFill
	 *            是否必填
	 * @param maxLength
	 *            最大长度
	 * @param lblMessage
	 *            提示label控件
	 * @param btnSave
	 *            保存按钮
	 * @throws Exception
	 */
	public PtnPasswordField(boolean mustFill, JLabel lblMessage, JButton btnSave, PtnDialog dialog) throws Exception {
		if (null == lblMessage) {
			throw new Exception("create text error : lblMessage is null");
		}
		if (null == btnSave) {
			throw new Exception("create text error : btnSave is null");
		}
		if (null == dialog) {
			throw new Exception("create text error : dialog is null");
		}
		this.mustFill = mustFill;
		this.lblMessage = lblMessage;
		this.btnSave = btnSave;
		this.dialog = dialog;

		this.init();
	}
	
	public PtnPasswordField(boolean mustFill, JLabel lblMessage, JButton btnSave, PtnDialog dialog, int minLength) throws Exception {
		if (null == lblMessage) {
			throw new Exception("create text error : lblMessage is null");
		}
		if (null == btnSave) {
			throw new Exception("create text error : btnSave is null");
		}
		if (null == dialog) {
			throw new Exception("create text error : dialog is null");
		}
		this.mustFill = mustFill;
		this.lblMessage = lblMessage;
		this.btnSave = btnSave;
		this.dialog = dialog;
		this.ADMINMINLENGTH = minLength;

		this.init();
	}

	/**
	 * init(初始化控件)
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	@SuppressWarnings("deprecation")
	private void init() {
		lblMessage.setForeground(color);
		if (this.mustFill) {
			if (this.getText().trim().length() == 0) {
				this.style(ResourceUtil.srcStr(StringKeysTip.TIP_NOT_FULL), this.border);
			} else {
				this.style("", this.textFieldBorder);
			}
		}
		this.addListener();
		dialog.setPtnPasswordFieldList(this);
	}

	/**
	 * addListener(添加监听)
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private void addListener() {
		this.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent e) {
				checking();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				checking();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				checking();
			}
		});
	}

	/**
	 * 
	 * checking(校验)
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	@SuppressWarnings("deprecation")
	private void checking() {

		// 判断是否填写
		if (this.mustFill && this.getText().trim().length() == 0) {

			this.style(ResourceUtil.srcStr(StringKeysTip.TIP_NOT_FULL), this.border);

		} else if (0 == this.ADMINMINLENGTH&&(this.getText().trim().length() > this.MAXLENGTH || this.getText().trim().length() < this.MINLENGTH)) { // 判断长度

			this.style(ResourceUtil.srcStr(StringKeysTip.MESSAGE_PASSWORD_LENG), this.border);

		} else if (0 != this.ADMINMINLENGTH&&(this.getText().trim().length() > this.MAXLENGTH || this.getText().trim().length() < this.ADMINMINLENGTH)) { // 判断ADMIN的密码长度

			this.style(ResourceUtil.srcStr(StringKeysTip.MESSAGE_ADMIN_PASSWORD_LENG), this.border);

		}else if (0 == this.ADMINMINLENGTH&&!CheckingUtil.checking(this.getText(), CheckingUtil.PASSWORD_REGULAR)) { // 验证密码格式
			this.style(ResourceUtil.srcStr(StringKeysTip.MESSAGE_PASSWORD), this.border);
		} else {
			this.style("", this.textFieldBorder);
		}

	}
	

	/**
	 * 
	 * style(设置样式)
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private void style(String message, Border border) {
		// String messageStr = null;
		// if ("".equals(message)) {
		// messageStr = "";
		// } else {
		// messageStr = ResourceUtil.srcStr(message);
		// }

		this.lblMessage.setText(message);
		this.setErrorMessage(message);
		this.setBorder(border);
		if ("".equals(this.getErrorMessage())) {
			lblMessage.setText(dialog.checkingMessage());
			if ("".equals(lblMessage.getText())) {
				this.btnSave.setEnabled(true);
			} else {
				this.btnSave.setEnabled(false);
			}
		} else {
			this.btnSave.setEnabled(false);
		}
	}


	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
