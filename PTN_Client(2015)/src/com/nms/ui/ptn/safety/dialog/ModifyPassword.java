package com.nms.ui.ptn.safety.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import com.nms.db.bean.system.user.UserInst;
import com.nms.model.system.user.UserInstServiece_Mb;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnPasswordField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;

public class ModifyPassword extends PtnDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PtnButton btnSave;
	private JButton btnCanel;
	private JLabel lblMessage;
	private JLabel lblNewPassword;//新密码
	private JPasswordField txtNewPassword;//新密码
	private JLabel userpasswordage;//确认密码
	private JPasswordField userpasswordtextage;//确认密码
	private JPanel btnPanel;
	private UserInst userInst;
	private boolean isSucess;
	private String timeType;
	
	public ModifyPassword(UserInst userInst, String type)
	{
		this.setModal(true);
		this.setUserInst(userInst);
		this.setTimeType(type);
		addListener();	
		try
		{
			initComponents(type);
			setLayout();
			this.showWindow();
		}
		catch (Exception e)
		{
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	
	private void initComponents(String type) throws Exception {
		this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_MODIFY_PASSWORD));
		this.lblMessage = new JLabel();
		this.btnPanel=new JPanel();
		lblNewPassword = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NEW_PASSWORD));
		txtNewPassword = new PtnPasswordField(true,this.lblMessage,this.btnSave,this);
		userpasswordage = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PASSWORD_AFFIRM));
		userpasswordtextage = new PtnPasswordField(true,this.lblMessage,this.btnSave,this);
		if(type.equals("before"))
		{
			lblMessage.setText(ResourceUtil.srcStr(StringKeysLbl.LBL_TIP_MODIY));
		}
		else
		{
			lblMessage.setText(ResourceUtil.srcStr(StringKeysLbl.LBL_TIP_EXPIRES));
		}
	}
	
	private void addListener() {
		btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),false);
		btnCanel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		btnCanel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(ModifyPassword.this.getTimeType().equals("before"))
				{
					ModifyPassword.this.setSucess(true);
				}
				else
				{
					ModifyPassword.this.setSucess(false);
				}
				ModifyPassword.this.dispose();
			}
		});
		
		btnSave.addActionListener(new MyActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ModifyPassword.this.modifyPassword();
			}

			@Override
			public boolean checking()
			{
				return true;
			}
		});
	}
	
	private void setLayout() {
		this.setBtnLayout();
		Dimension dimension = new Dimension(300,250);
		this.setPreferredSize(dimension);
		this.setMinimumSize(dimension);

		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 50, 150 };
		layout.columnWeights = new double[] { 0, 0.1 };
		layout.rowHeights = new int[] { 25,35,35,35,35};
		layout.rowWeights = new double[] {0, 0, 0, 0, 0};
		setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		/** 第一行 用户名 */
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(this.lblMessage, c);
		add(this.lblMessage);

		/** 第一行 新密码 */
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 5, 5, 5);
		layout.setConstraints(lblNewPassword, c);
		add(lblNewPassword);
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.addLayoutComponent(txtNewPassword, c);
		add(txtNewPassword);

		/** 第二行 确认密码 */
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(userpasswordage, c);
		add(userpasswordage);
		c.gridx = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(userpasswordtextage, c);
		add(userpasswordtextage);

		/** 第三行 确定按钮空出一行 */
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		c.gridheight=1;
		layout.setConstraints(this.btnPanel, c);
		this.add(this.btnPanel);
	}
	
	/**
	 * 设置按钮布局
	 */
	private void setBtnLayout(){
		GridBagLayout layout=new GridBagLayout();
		layout.columnWidths = new int[] { 100, 50, 50 };
		layout.columnWeights=new double[] {0.1,0,0};
		layout.rowHeights=new int[] {40};
		layout.rowWeights =new double[]{0};
		this.btnPanel.setLayout(layout);
		
		GridBagConstraints c=new GridBagConstraints();
		c.gridx=1;
		c.gridy=0;
		c.insets=new Insets(5,5,5,5);
		layout.addLayoutComponent(this.btnSave, c);
		this.btnPanel.add(this.btnSave);
		c.gridx=2;
		layout.addLayoutComponent(this.btnCanel, c);
		this.btnPanel.add(this.btnCanel);
	}
	
	private void showWindow() {
		this.setLocation(UiUtil.getWindowWidth(this.getWidth()), UiUtil.getWindowHeight(this.getHeight()));
		this.setVisible(true);
	}
	
	private void modifyPassword()
	{
		UserInstServiece_Mb userInstServiece = null;
		int userid = 0;

		try
		{
			if (this.txtNewPassword.getText().trim().length() == 0 || this.userpasswordtextage.getText().trim().length() == 0) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PASSWORDERROR));
				return;
			}
			if (!this.txtNewPassword.getText().trim().equals(this.userpasswordtextage.getText().trim())) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PASSWORDERROR));
				return;
			}
			if(this.userInst.getUser_Pass().trim().equals(txtNewPassword.getText().trim()))
			{
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.LBL_TIP_DIFF));
				return;
			}
			
			userInstServiece = (UserInstServiece_Mb) ConstantUtil.serviceFactory.newService_MB(Services.UserInst);
			this.userInst.setUser_Pass(txtNewPassword.getText());
			userid = userInstServiece.saveOrUpdate(userInst);
			userInst.setUser_Id(userid);
			this.setSucess(true);
			this.dispose();
		
		}
		catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(userInstServiece);
		}
		
	}
	
	public UserInst getUserInst()
	{
		return userInst;
	}

	public void setUserInst(UserInst userInst)
	{
		this.userInst = userInst;
	}
	
	public boolean isSucess()
	{
		return isSucess;
	}

	public void setSucess(boolean isSucess)
	{
		this.isSucess = isSucess;
	}
	
	public String getTimeType()
	{
		return timeType;
	}

	public void setTimeType(String timeType)
	{
		this.timeType = timeType;
	}
}
	
