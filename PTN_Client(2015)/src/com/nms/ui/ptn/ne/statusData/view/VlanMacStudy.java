﻿package com.nms.ui.ptn.ne.statusData.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.oamStatus.OamStatusInfo;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.clock.view.cx.time.TextFiledKeyListener;
import com.nms.ui.ptn.clock.view.cx.time.TextfieldFocusListener;

public class VlanMacStudy extends JPanel{
	
	 private JLabel vlanLable;//vlanID
	 private JTextField vlanIdField;
	 private JLabel macCountLable;//mac学习数目
	 private JTextField portMaccount;//端口mac学习数目
	 private JButton findButton ;
	 private JPanel buttonJpan;//按钮布局
	 
	 public VlanMacStudy(){
		 try {
			 init();
			 setsetLayout();
			 addKeyListenerForTextfield();
			 addFocusListenerForTextfield();/*textfield聚焦事件监听，当离开此textfield判断值是否在指定范围内*/
			 addActionListener();
		} catch (Exception e) {
			ExceptionManage.dispose(e, VlanMacStudy.class);
		}
	 }

	private void init() {
		try {
		   this.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysObj.VLAN_STUDYMAC_COUNT)));
			vlanLable = new JLabel("VLAN ID");
			findButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM));
			vlanIdField = new JTextField();
			vlanIdField.setText("1");
			macCountLable = new JLabel(ResourceUtil.srcStr(StringKeysObj.STUDYMACCOUNT));
			portMaccount = new JTextField();
			portMaccount.setEditable(false);
			buttonJpan = new JPanel();
		} catch (Exception e) {
			ExceptionManage.dispose(e, VlanMacStudy.class);
		}
	}
	
	/**
	 * 设置布局
	 */
	public void setsetLayout() {
		setButtonLayout();
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] {10,150,20,150};
		layout.columnWeights = new double[] { 0, 0, 0, 0,0,0.4};
		layout.rowHeights = new int[] {100};
		layout.rowWeights = new double[] { 0 };
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		this.setLayout(layout);
		// 操作菜单按钮布局
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(vlanLable, c);
		this.add(vlanLable);
		
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 5, 0, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(vlanIdField, c);
		this.add(vlanIdField); 
		
		c.gridx = 2;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(macCountLable, c);
		this.add(macCountLable);
		
		c.gridx = 3;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 5, 0, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(portMaccount, c);
		this.add(portMaccount); 
		
		c.fill = GridBagConstraints.EAST;
		c.gridx = 3;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 50, 0, 0);
		layout.setConstraints(buttonJpan, c);
		this.add(buttonJpan); 
	}
	
	private void setButtonLayout() {
		
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] {20,20};
		layout.columnWeights = new double[] { 0, 0};
		layout.rowHeights = new int[] {15};
		layout.rowWeights = new double[] { 0 };
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		buttonJpan.setLayout(layout);
		// 操作菜单按钮布局
		c.fill = GridBagConstraints.EAST;
		c.gridx = 4;
		c.gridy = 0;
		c.gridheight = 1;
		c.insets = new Insets(5, 5, 5, 0);
		layout.setConstraints(findButton, c);
		buttonJpan.add(findButton);
	}
	
	private void addActionListener() {
		findButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
              confirm();				
			}
		});
	}
	
	/**
	 * 保存事件
	 */
	private void confirm() {
		
		DispatchUtil siteDispatch = null;
		SiteService_MB siteServie = null;
		SiteInst siteInfo = null;
		try {
			siteDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
			siteServie = (SiteService_MB)ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			siteInfo = siteServie.select(ConstantUtil.siteId);
			siteInfo.setStatusMark(65);
			siteInfo.setParameter(Integer.parseInt(vlanIdField.getText().trim()));
			OamStatusInfo oamStatusInfo	= siteDispatch.oamStatus(siteInfo);
			if(oamStatusInfo != null && oamStatusInfo.getMacStudyBean()!= null){
				DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
				portMaccount.setText(oamStatusInfo.getMacStudyBean().getVlanId()+"");
			}else{
				DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_FIND_DETAIL_FAIL));
			}
//			if(oamStatusInfo.getMacStudyBean()!= null){
//				portMaccount.setText(oamStatusInfo.getMacStudyBean().getVlanId()+"");
//			}else{
//				portMaccount.setText(0+"");
//			}
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, VlanMacStudy.class);
		}finally{
			siteDispatch = null;
			 siteInfo = null;
			 UiUtil.closeService_MB(siteServie);
		}
		
	}
	/**
	 * <p>
	 * textfield添加监听，只允许输入数字
	 * </p>
	 * @throws Exception
	 */
	private void addKeyListenerForTextfield()throws Exception{
		
		TextFiledKeyListener textFIledKeyListener=null;
		try{
			/* 为1：只接受数字 **/
			textFIledKeyListener = new TextFiledKeyListener(1);
			vlanIdField.addKeyListener(textFIledKeyListener);
		}catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * <p>
	 * 判断输入数值是否在指定区间内
	 * </p>
	 * @throws Exception
	 */
	private void addFocusListenerForTextfield()throws Exception{
		
		TextfieldFocusListener textfieldFocusListener=null;
		String whichTextTield=null;
		try{
			whichTextTield=ResourceUtil.srcStr(StringKeysLbl.LBL_VLAN_ID);
			textfieldFocusListener = new TextfieldFocusListener(whichTextTield,22,vlanIdField);
			vlanIdField.addFocusListener(textfieldFocusListener);
		}catch(Exception e){
			throw e;
		}
	}
}
