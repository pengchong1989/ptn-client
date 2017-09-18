﻿package com.nms.ui.ptn.telnetManage;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import com.nms.db.enums.EOperationLogType;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.CheckingUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.TelnetUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.xmlbean.Telnet;


public class TelnetDialog extends PtnDialog {
	
	private static final long serialVersionUID = -1740678601593949447L;
	private PtnButton confirm;
	private JButton cancel;
	private JPanel buttonJPanel;
	private int weight;
	private GridBagConstraints gridBagConstraints = null;
	private GridBagLayout gridBagLayout = null;
    private JLabel  UserName;//服务器所在电脑管理员用户名
    private JLabel  UserPassword;//服务器所在电脑密码
    private JLabel  NePassword;//设备密码
    private JLabel  ServerIp;//服务器IP
    private JTextField UserNameJText;
    private JTextField ServerIpJText;
    private JPasswordField UserPasswordJText;
    private JPasswordField NePasswordJText;
    private Telnet telnetinfo = null;
    private JLabel  Message;
    
        
    
    
	public TelnetDialog() {
		init();
		addListener();
	}


	private void init() {
		try {
			gridBagLayout = new GridBagLayout();
			gridBagConstraints = new GridBagConstraints();
			this.setTitle(ResourceUtil.srcStr(StringKeysMenu.MENU_TELNETMANAGE_T));  
			weight = 400;
			UserName = new JLabel(ResourceUtil.srcStr(StringKeysTip.TIP_COMPUTERNAME));
			UserNameJText = new JTextField();
			UserPassword = new JLabel(ResourceUtil.srcStr(StringKeysTip.TIP_COMPUTER_PASSWORD));
			UserPasswordJText = new JPasswordField();
			
			ServerIp = new JLabel(ResourceUtil.srcStr(StringKeysTip.TIP_SERVER_IP));
			ServerIpJText = new JTextField();
			NePassword = new JLabel(ResourceUtil.srcStr(StringKeysTip.TIP_NE_PASSWORD));
			NePasswordJText = new JPasswordField();
			
			Message = new JLabel(ResourceUtil.srcStr(StringKeysTip.TIP_IP_MESSAGE));
			
			confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM));
			cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
			buttonJPanel = new JPanel();
			buttonJPanel.add(confirm);
			buttonJPanel.add(cancel);
		
			setCompentLayout();
			telnetinfo = new Telnet();
			this.add(UserName);
			this.add(UserNameJText);
			this.add(UserPassword);
			this.add(UserPasswordJText);
			this.add(ServerIp);
			this.add(ServerIpJText);
			this.add(NePassword);
			this.add(NePasswordJText);
			this.add(Message);
			this.add(confirm);
			this.add(cancel);						
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void addListener() {
		// TODO Auto-generated method stub
		try {
			confirm.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					confirmSave();
				}
			});

			cancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					cancel();
				}
			});

		
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
    //取消事件
	private void cancel() {
		this.dispose();
	}

	
	
	
	private void confirmSave() {
		try {
			String serverIp=this.ServerIpJText.getText().trim();
			String username=this.UserNameJText.getText().trim();
			String userPassword=this.UserPasswordJText.getText().trim();
			String nePassword=this.NePasswordJText.getText().trim();
			if(("").equals(serverIp) || ("").equals(username) || ("").equals(userPassword)
				|| ("").equals(nePassword)){
				 DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NOT_FULL));
				 return;
			}
			if(!serverIp.matches(CheckingUtil.IP_REGULAR)){
				 DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.SERVER_IP_MATCH));
				 return;
			}
			telnetinfo = new Telnet();
			telnetinfo.setUsername(username);
			telnetinfo.setServiceIp(serverIp);
			telnetinfo.setPassword(userPassword);
			telnetinfo.setNePassword(nePassword);
			TelnetUtil telnetUtil=new TelnetUtil();
			telnetUtil.writeLoginConfig(telnetinfo);
			this.insertOpeLog(EOperationLogType.TELNETMANAGE.getValue(), ResultString.CONFIG_SUCCESS, null, null);	
			this.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			telnetinfo = null;
		}
	}
	
	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		AddOperateLog.insertOperLog(confirm, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysMenu.MENU_TELNETMANAGE_T),"");		
	}


	private void setCompentLayout() {
		try {
			gridBagLayout.columnWidths = new int[] {70,70,70};
			gridBagLayout.columnWeights = new double[] { 0, 0, 0 };
			gridBagLayout.rowHeights = new int[] {20,20,20,20,20,20};
			gridBagLayout.rowWeights = new double[] { 0, 0,0,0,0,0 };
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.fill = GridBagConstraints.BOTH;

			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(UserName, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 2;
			gridBagLayout.setConstraints(UserNameJText, gridBagConstraints);
					
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;			
			gridBagLayout.setConstraints(UserPassword, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 2;
			gridBagLayout.setConstraints(UserPasswordJText, gridBagConstraints);
					   			
		    gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(ServerIp, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 2;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 2;
			gridBagLayout.setConstraints(ServerIpJText, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(NePassword, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 3;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 2;
			gridBagLayout.setConstraints(NePasswordJText, gridBagConstraints);
		
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 4;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 3;
			gridBagLayout.setConstraints(Message, gridBagConstraints);
			
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 5;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 1;
			gridBagLayout.setConstraints(confirm, gridBagConstraints);

			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 5;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 1;
			gridBagLayout.setConstraints(cancel, gridBagConstraints);
			this.setLayout(gridBagLayout);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
	
}
