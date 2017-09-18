﻿package com.nms.ui.ptn.ne.macManagement.view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.MacManagementInfo;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.ptn.MacManageService_MB;
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

public class AddMacDialog extends PtnDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3666883742001674590L;
	
	private JLabel portLab;
	private JComboBox portCom;//端口下拉列表
//	private JLabel modelLab;
//	private JComboBox modelCom;//mac学习限制模式
//	private JLabel enabledLab;
//	private JComboBox enabledCom;//黑名单使能
//	private JLabel vlanIdLab;
//	private JTextField vlanIdTxt;//vlan值
//	private JLabel countLab;
//	private JTextField countTxt;//mac地址学习限制数目
//	private JLabel associateVlanLab;
//	private JTextField associateVlanTxt;//黑名单关联vlan值
	private JLabel macLab;
	private PtnTextField macTxt;//黑名单mac地址
	private JLabel messageLab;
	private JPanel topPanel;
	private PtnButton saveBtn;
	private JButton cancelBtn;
	private JPanel btnPanel;
	
	private MacManagementPanel view;
	
	private MacManagementInfo macInfo;
	private String mac = "";//修改时判断mac地址是否有改变
	
	public AddMacDialog(MacManagementInfo info, MacManagementPanel macPanel) {
		this.initCompoment();
		this.setLayout();
		this.setListener();
		this.initComboBoxAndTxt();
		if(info == null){
			//新建
			this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_ADD_MAC));
			macInfo = new MacManagementInfo();
			this.initPortValue();
		}else{
			//修改
			this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_MAC));
			macInfo = info;
			this.mac = macInfo.getMac();
			this.setValue();
		}
		this.view = macPanel;
		UiUtil.showWindow(this, 330, 250);
	}

	/**
	 * 初始化组件
	 */
	private void initCompoment() {
		this.portLab = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_NAME));
		this.portCom = new JComboBox();
//		this.vlanIdLab = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_VLAN_CODE));
		this.macLab = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_BLACK_MAC));
//		this.vlanIdTxt = new JTextField();
		this.saveBtn = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE));
		this.cancelBtn = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		this.topPanel = new JPanel();
		this.btnPanel = new JPanel();
		try {
			this.messageLab = new JLabel();
			this.macTxt = new PtnTextField(true,PtnTextField.TYPE_MAC,PtnTextField.MAC_MAXLENGTH,messageLab,saveBtn,this);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * 界面布局
	 */
	private void setLayout() {
		this.setTopLayout();
		this.setBtnLayout();
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 300};
		componentLayout.columnWeights = new double[] { 0, 0.2, 0 };
		componentLayout.rowHeights = new int[] { 130, 50};
		componentLayout.rowWeights = new double[] { 0.1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  0.1 };
		this.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		// 第一行
		c.gridx = 0;
		c.gridy = 0;
		componentLayout.setConstraints(this.topPanel, c);
		this.add(this.topPanel);
		// 第一行
		c.gridx = 0;
		c.gridy = 1;
		componentLayout.setConstraints(this.btnPanel, c);
		this.add(this.btnPanel);
		
	}

	private void setTopLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 50, 250};
		componentLayout.rowHeights = new int[] { 50, 30, 30, 30, 30};
		componentLayout.rowWeights = new double[] { 0, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0, 0, 0, 0};
		this.topPanel.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		// 第一行
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.portLab, c);
		this.topPanel.add(this.portLab);
		
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		componentLayout.setConstraints(this.portCom, c);
		this.topPanel.add(this.portCom);
		
		// 第二行
//		c.gridx = 0;
//		c.gridy = 1;
//		componentLayout.setConstraints(this.vlanIdLab, c);
//		this.topPanel.add(this.vlanIdLab);
//		
//		c.gridx = 1;
//		c.gridy = 1;
//		c.fill = GridBagConstraints.HORIZONTAL;
//		componentLayout.setConstraints(this.vlanIdTxt, c);
//		this.topPanel.add(this.vlanIdTxt);
		
		// 第三行
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		componentLayout.setConstraints(this.macLab, c);
		this.topPanel.add(this.macLab);
		
		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		componentLayout.setConstraints(this.macTxt, c);
		this.topPanel.add(this.macTxt);
	}

	private void setBtnLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 230, 20};
		componentLayout.rowHeights = new int[] { 25};
		this.btnPanel.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		// 第一行
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 25);
		c.anchor = GridBagConstraints.EAST;
		componentLayout.setConstraints(this.saveBtn, c);
		this.btnPanel.add(this.saveBtn);
		
		c.gridx = 1;
		c.gridy = 0;
		componentLayout.setConstraints(this.cancelBtn, c);
		this.btnPanel.add(this.cancelBtn);
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
		
//		this.vlanIdTxt.addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyReleased(KeyEvent a) {
//				if (checkLength(1, 4094, vlanIdTxt)) {
//					return;
//				}
//			}
//		});
	}

	/**
	 * 检验文本框输入的值是否合理
	 * @param min值
	 * @param max值
	 * @param 要验证文本框
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean checkLength(int min, int max, JTextField text) {
		try {
			if ("".equals(text.getText().trim())
					|| Integer.valueOf(text.getText().trim()) > max
						|| Integer.valueOf(text.getText().trim()) < min) {
				return this.returnErrorTip(min, max, text);
			}
		} catch (Exception e) {
			return this.returnErrorTip(min, max, text);
		}
		this.messageLab.setText("   ");
		text.setBorder(new LineBorder(Color.gray));
		this.saveBtn.setEnabled(true);
		this.cancelBtn.setEnabled(true);
		return false;
	}
	
	private boolean returnErrorTip(int min, int max, JTextField text) {
		this.messageLab.setText(ResourceUtil.srcStr(StringKeysTip.TIP_OUT_LIMIT) + min+"-"+max);
		text.setBorder(new LineBorder(Color.RED));
		this.saveBtn.setEnabled(false);
		this.cancelBtn.setEnabled(false);
		return true;
	}

	/**
	 * 收集界面数据,下发入库
	 */
	private void saveMacInfo() { 
		macInfo.setPortId(Integer.parseInt(((ControlKeyValue) this.portCom.getSelectedItem()).getId()));
		macInfo.setSiteId(ConstantUtil.siteId);
//		macInfo.setVlanId(Integer.parseInt(this.vlanIdTxt.getText().trim()));
		macInfo.setMac(this.macTxt.getText().trim());
		if(!this.checkIsFull()){
			return;
		}
		MacManagementInfo mac=null;
		String result = "";
		MacManageService_MB macManageService = null;
		try {
			DispatchUtil macDispatch = new DispatchUtil(RmiKeys.RMI_BLACKLISTMAC);
			if(macInfo.getId() == 0){
				//新建
				macInfo.setPortNa(getPortName(macInfo.getPortId()));
				result = macDispatch.excuteInsert(macInfo);
				this.insertOpeLog(EOperationLogType.MACMANAGMENTCREATE.getValue(), result, null,macInfo);
			}else{
				//修改
				macManageService = (MacManageService_MB) ConstantUtil.serviceFactory.newService_MB(Services.MACMANAGE);
				macInfo.setPortNa(getPortName(macInfo.getPortId()));
				mac=macManageService.selectById(macInfo.getId());
				mac.setPortNa(getPortName(mac.getPortId()));
				result = macDispatch.excuteUpdate(macInfo);
				this.insertOpeLog(EOperationLogType.MACMANAGMENTUPDATE.getValue(), result, mac,macInfo);
			}
			DialogBoxUtil.succeedDialog(this, result);
			this.view.getController().refresh();
			this.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	
	private String getPortName(int portId) {
		PortService_MB portService = null;
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			return portService.selectPortybyid(portId).getPortName();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		finally
		{
			UiUtil.closeService_MB(portService);
		}
		return "";
	}

	private void insertOpeLog(int operationType, String result, MacManagementInfo oldMac, MacManagementInfo newMac){		
	   AddOperateLog.insertOperLog(saveBtn, operationType, result, oldMac, newMac, newMac.getSiteId(),ResourceUtil.srcStr(StringKeysTitle.TIT_BLACK_MAC_MANAGE),"macManagementInfo");		
	}
	
	/**
	 * 检查数据是否为空,是否合理
	 */
	private boolean checkIsFull() {
		if(this.portCom.getSelectedItem() == null){
			//如果端口为空,说明没有可用的端口
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.LBL_NO_PORT));
			return false;
		}
//		if(this.vlanIdTxt.getText() == null || this.macTxt.getText() == null){
//			//如果有其中一项没有填写完整,则提示
//			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NOT_FULL));
//			return false;
//		}
		MacManageService_MB macManageService = null;
		try {
			macManageService = (MacManageService_MB) ConstantUtil.serviceFactory.newService_MB(Services.MACMANAGE);
			boolean flag = true;
			//判断该网元下的mac是否重复
			if(!(this.macTxt.getText().trim()).equals(mac)){
				try {
					flag = macManageService.checkVlanAndMac(macInfo);
				} catch (Exception e) {
					//提示用户,查询数据库出错
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_QUERY_DATA_ERROR));
					return false;
				}
				if(flag){
					//提示用户, mac重复
					DialogBoxUtil.errorDialog(this, "MAC地址重复");
					return false;
				}
			}
			//判断该网元下的黑名单是否超过五十个
			if(macInfo.getId() == 0){
				try {
					flag = macManageService.selectCountBySiteId(macInfo.getSiteId());
				} catch (Exception e) {
					//提示用户,查询数据库出错
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_QUERY_DATA_ERROR));
					return false;
				}
				if(flag){
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_MAX_MAC));
					return false;
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(macManageService);
		}
		return true;
	}
	
	/**
	 * 初始化端口信息
	 */
	private void initPortValue() {
		DefaultComboBoxModel defaultComboBoxModel = new DefaultComboBoxModel();
		PortService_MB portService = null;
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			PortInst portInst = new PortInst();
			portInst.setSiteId(ConstantUtil.siteId);
			List<PortInst> portInstList = portService.select(portInst);
			for (PortInst inst : portInstList) {
				if(inst.getPortType().equals("NONE")|| inst.getPortType().equals("UNI") ||inst.getPortType().equals("NNI")){
					defaultComboBoxModel.addElement(new ControlKeyValue(inst.getPortId() + "",inst.getPortName(), inst));
				}
			}
			this.portCom.setModel(defaultComboBoxModel);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(portService);
		}
	}

	/**
	 * 初始化下拉列表和文本框
	 */
	private void initComboBoxAndTxt() {
		try {
//			this.vlanIdTxt.setText("1");
			this.macTxt.setText("00-00-00-00-00-00");
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * 修改时,给界面赋值
	 */
	private void setValue() {
		this.setValueToPort();
		this.portCom.setEnabled(false);
		this.macTxt.setText(macInfo.getMac());
	}
	
	private void setValueToPort() {
		PortService_MB portService = null;
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			PortInst portInst = new PortInst();
			portInst.setPortId(macInfo.getPortId());
			portInst = portService.select(portInst).get(0);
			DefaultComboBoxModel defaultComboBoxModel = new DefaultComboBoxModel();
			defaultComboBoxModel.addElement(new ControlKeyValue(portInst.getPortId() + "", portInst.getPortName(), portInst));
			this.portCom.setModel(defaultComboBoxModel);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(portService);
		}
	}
}
