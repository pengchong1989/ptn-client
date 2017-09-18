﻿package com.nms.ui.ptn.ne.blackAndWhiteMacManagement.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import com.nms.db.bean.ptn.BlackAndwhiteMacInfo;
import com.nms.db.bean.ptn.path.eth.ElanInfo;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.ptn.BlackWhiteMacService_MB;
import com.nms.model.ptn.path.eth.ElanInfoService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;

public class AddBalckAndWhiteMacDialog extends PtnDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3666883742001674590L;

	private JLabel vsId;
	private JComboBox vsIdComBox;
	private JLabel nameListLabel;
	private JComboBox nameList;// 名单模式
	private JLabel macLab;
	private PtnTextField macTxt;// mac地址
	private JLabel messageLab;
	private PtnButton saveBtn;
	private JButton cancelBtn;

	private BlackAndWhiteMacManagementPanel view;

	private BlackAndwhiteMacInfo macInfo;

	public AddBalckAndWhiteMacDialog(BlackAndwhiteMacInfo info, BlackAndWhiteMacManagementPanel macPanel) {
		this.initCompoment();
		setTopLayout();
		this.setListener();
		this.initComboBoxAndTxt();
		if (info == null) {
			// 新建
			this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_ADD_BALCK_WHITE_MAC));
			macInfo = new BlackAndwhiteMacInfo();
		} else {
			// 修改
			this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_BALCK_WHITE_MAC));
			macInfo = info;
			this.setValue();
		}
		this.view = macPanel;
		UiUtil.showWindow(this, 400, 290);
	}

	/**
	 * 初始化组件
	 */
	private void initCompoment() {
		this.vsId = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SERVICENAME_CODE));
		this.vsIdComBox = new JComboBox();
		this.nameListLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAMELIST_CODE));
		this.macLab = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_BLACK_MAC));
		this.nameList = new JComboBox();
		this.saveBtn = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE));
		this.cancelBtn = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		try {
			this.messageLab = new JLabel();
			this.macTxt = new PtnTextField(true, PtnTextField.TYPE_MAC, PtnTextField.MAC_MAXLENGTH, messageLab,saveBtn, this);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void setTopLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 50, 200, 50 };
		componentLayout.columnWeights = new double[] { 0, 0, 0 };
		componentLayout.rowHeights = new int[] { 25, 40, 40, 40};
		componentLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.2 };
		this.setLayout(componentLayout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		// 第一行
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(15, 5, 15, 5);
		componentLayout.setConstraints(this.vsId, c);
		this.add(this.vsId);
		
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.vsIdComBox, c);
		this.add(this.vsIdComBox);
		// 第二行
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(15, 5, 15, 5);
		componentLayout.setConstraints(this.nameListLabel, c);
		this.add(this.nameListLabel);
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.nameList, c);
		this.add(this.nameList);
		// 第三行
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(15, 5, 15, 5);
		componentLayout.setConstraints(this.macLab, c);
		this.add(this.macLab);
		
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.macTxt, c);
		this.add(this.macTxt);
		
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 1;
		c.gridy = 4;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 0, 10);
		componentLayout.setConstraints(this.saveBtn, c);
		this.add(this.saveBtn);
		c.gridx = 2;
		c.insets = new Insets(5, 5, 0, 10);
		componentLayout.setConstraints(this.cancelBtn, c);
		this.add(this.cancelBtn);
	}

	/**
	 * 添加监听
	 */
	private void setListener() {
		this.saveBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveMacInfo();
			}
		});

		this.cancelBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
	}

	/**
	 * 收集界面数据,下发入库
	 */
	private void saveMacInfo() {
		DispatchUtil macDispatch = null;
		String result = null;
		ControlKeyValue controlKeyValue = null;
		BlackAndwhiteMacInfo macBefore=null;
		BlackWhiteMacService_MB service = null;
		try {
			service = (BlackWhiteMacService_MB) ConstantUtil.serviceFactory.newService_MB(Services.BLACKWHITEMAC);
			if(macInfo.getId()>0){
				BlackAndwhiteMacInfo b=new BlackAndwhiteMacInfo();
				b.setId(macInfo.getId());
				macBefore=service.selectByBlackAndwhiteMacInfo(b).get(0);			
			}
			if (!this.checkIsFull()) {
				return;
			}
			controlKeyValue = (ControlKeyValue)this.vsIdComBox.getSelectedItem();
			if(controlKeyValue == null ){
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CREATE_ELAN));
				return ;
			}
			macInfo.setSiteId(ConstantUtil.siteId);
			macInfo.setElanBussinessId(Integer.parseInt(controlKeyValue.getId()));
			macInfo.setVplsServiceName(controlKeyValue.getName());
			controlKeyValue = (ControlKeyValue)this.nameList.getSelectedItem();
			macInfo.setNameList(((Code)controlKeyValue.getObject()).getId());
			macInfo.setMac(this.macTxt.getText());
			
			macDispatch = new DispatchUtil(RmiKeys.RMI_BLACKWHITEMAC);
			if (macInfo.getId() == 0) {
				// 新建
				result = macDispatch.excuteInsert(macInfo);
				this.insertOpeLog(EOperationLogType.BLACKANDWHITEMACINSERT.getValue(), result, null,macInfo);
			} else {
				// 修改
				result = macDispatch.excuteUpdate(macInfo);
				this.insertOpeLog(EOperationLogType.BLACKANDWHITEUPDATE.getValue(), result, macBefore,macInfo);
			}
			DialogBoxUtil.succeedDialog(this, result);
			
			this.view.getController().refresh();
			this.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			 macDispatch = null;
			 result = null;
			 controlKeyValue = null;
		}
	}

	private void insertOpeLog(int operationType, String result, BlackAndwhiteMacInfo oldMac, BlackAndwhiteMacInfo newMac){
	   AddOperateLog.insertOperLog(saveBtn, operationType, result, oldMac, newMac, newMac.getSiteId(),ResourceUtil.srcStr(StringKeysTitle.TIT_BLACK_WHILTE_MAC_MANAGE),"blackAndWhiteMacInfo");		
	}
	
	/**
	 * 检查数据是否为空,是否合理
	 */
	private boolean checkIsFull() {
		BlackWhiteMacService_MB service = null;
		List<BlackAndwhiteMacInfo>  blackAndwhiteMacInfoList = null;
		BlackAndwhiteMacInfo blackAndwhiteMacInfo = null;
		try {
			// 判断该网元下的黑名单是否超过五十个
			if (macInfo.getId() == 0) {
				blackAndwhiteMacInfo = new BlackAndwhiteMacInfo();
				blackAndwhiteMacInfo.setSiteId(ConstantUtil.siteId);
				service = (BlackWhiteMacService_MB) ConstantUtil.serviceFactory.newService_MB(Services.BLACKWHITEMAC);
				blackAndwhiteMacInfoList = service.selectByBlackAndwhiteMacInfo(blackAndwhiteMacInfo);
				if (blackAndwhiteMacInfoList!=null && blackAndwhiteMacInfoList.size()>50) {
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_BLACKWHITEMAX_MAC));
					return false;
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(service);
		}

		return true;
	}

	/**
	 * 初始化下拉列表和文本框
	 */
	private void initComboBoxAndTxt() {
		try {
			super.getComboBoxDataUtil().comboBoxData(nameList, "NAMELIST");
			this.macTxt.setText("00-00-00-00-00-00");
			initVsIDJComboBox();

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 给VSID赋初始值 关联elan业务
	 */
	private void initVsIDJComboBox() throws Exception {
		DefaultComboBoxModel model = null;
		ElanInfoService_MB elanInfoService = null;
		Map<String, List<ElanInfo>> elanMap = null;
		try {
			elanInfoService = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
			model = (DefaultComboBoxModel) vsIdComBox.getModel();
			elanMap = elanInfoService.selectSiteNodeBy(ConstantUtil.siteId);
			for (Map.Entry<String, List<ElanInfo>> entrySet : elanMap.entrySet()) {
				ElanInfo elanInfo = entrySet.getValue().get(0);
				if (elanInfo.getAxcId() > 0) {
					model.addElement(new ControlKeyValue(elanInfo.getAxcId() + "", elanInfo.getName(), elanInfo));
				} else {
					model.addElement(new ControlKeyValue(elanInfo.getZxcId() + "", elanInfo.getName(), elanInfo));
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			 model = null;
			 UiUtil.closeService_MB(elanInfoService);
			 elanMap = null;
		}
	}

	/**
	 * 修改时,给界面赋值
	 */
	
	private void setValue() {
		try {
			//名单模式
			super.getComboBoxDataUtil().comboBoxSelectByValue(nameList,UiUtil.getCodeById(macInfo.getNameList()).getCodeValue());
			//mac地址
			this.macTxt.setText(macInfo.getMac());
			//业务名称
			this.comboBoxSelectByValue(vsIdComBox, macInfo.getVplsServiceName());
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	/**
	 * 下拉列表选中
	 * 
	 * @param jComboBox
	 *            下拉列表对象
	 * @param values
	 *            选中的对象的名称
	 */
	private  void comboBoxSelectByValue(JComboBox jComboBox, String values) {
		ControlKeyValue controlKeyValue = null;
		String value = null;
		for (int i = 0; i < jComboBox.getItemCount(); i++) {
			controlKeyValue = (ControlKeyValue) jComboBox.getItemAt(i);
			value = controlKeyValue.getName();
			if (value.equals(values)) {
				jComboBox.setSelectedIndex(i);
				return;
			}
		}
	}
	
}
