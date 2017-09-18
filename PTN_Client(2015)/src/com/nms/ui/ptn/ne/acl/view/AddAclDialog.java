﻿package com.nms.ui.ptn.ne.acl.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.AclInfo;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EOperationLogType;
import com.nms.drive.service.impl.CoderUtils;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.ptn.AclService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnSpinner;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.ptn.ne.acl.controller.AclController;

public class AddAclDialog extends PtnDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5817477240950123085L;
	
	private JLabel actJlabel ; //动作
	private JComboBox actJComboBox ;
	private JLabel directionJLabel ;//入出方向
	private JComboBox directionJComboBox ; 
	private JLabel portJlJLabel ; //端口号
	private JComboBox protJcomboBox ;
	private JLabel vlanJLabel ; //vlanID
	private PtnTextField vlanField ;	
	private JCheckBox isSourceMac ;
	private PtnTextField sourceMacAddressField ;
	private JCheckBox isPurposeMac ;//是否匹配目的MAC地址
	private PtnTextField purposeMacAddressField ;
	private JCheckBox isSourceIp ;//是否匹配源IP地址
	private PtnTextField sourceIpAddressField ;
	private JCheckBox isPurposeIp ;//是否匹配目的IP地址:
	private PtnTextField purposeIpAddressField ;
	private JCheckBox isMatchCos;//是否匹配cos
	private PtnTextField matchCosField ;
	private JCheckBox isMatchDSCP ; // 是否匹配DSCP  
	private PtnTextField matchDSCPField ;
	private JCheckBox isMatchTOS ; //是否匹配TOS
	private PtnTextField matchTOSField ;
	private JCheckBox isSourcePort ;//是否匹配源端口 
	private PtnTextField sourcePortField ;
	private JCheckBox isPurposePort ;//是否匹配目的端口 
	private PtnTextField purposePortField ;	
	private JLabel ruleObjectJLabel ;//规则应用对象
	private JComboBox ruleObjectJcomBox ;	
	private JLabel transportLayerTypeJlabel;//传输层协议类型
	private JComboBox transportLayerTypeJcomBox ;	
	private JLabel lblMessage;
	private PtnButton confirm;// 保存
	private JButton cancel;// 取消
	private AclController controller;
	private AddAclDialog dialog ;
	private AclInfo aclInfo ;
	private JPanel  panelContent ;
	private JPanel  buttonPanel ;
	private JLabel number;
	private PtnSpinner numberPtnSpinner;
	private int isAdd;
	private JCheckBox isSourceMacJCheckBox;
	private JCheckBox isTargetMacJCheckBox;
	private JCheckBox isSourceIpJCheckBox;
	private JCheckBox isTargetIpJCheckBox;
	public AddAclDialog(AclInfo aclInfo, AclController controller) {
		if (aclInfo != null) {
			isAdd = 1;
		}
		this.aclInfo = aclInfo;
		this.controller = controller;
		setModal(true);
		initComponent();
		dialog = this;
		addListener();
		// 修改ACL配置界面赋初始值
		initData();
		if(this.aclInfo == null){
			this.aclInfo = new AclInfo();
		}
	}

/**
 * 初始化控件
 */
	private void initComponent() {
	try {
		confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),true);
		cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		lblMessage = new JLabel();
		actJlabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ACL_ACT));// 动作
		actJComboBox = new JComboBox();
		super.getComboBoxDataUtil().comboBoxData(actJComboBox, "ACLACT");
		directionJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ACL_DIRECTION));// 入出方向
		directionJComboBox = new JComboBox();
		super.getComboBoxDataUtil().comboBoxData(directionJComboBox, "ACLDIRECTION");
		portJlJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_NUM));// 端口号 
		protJcomboBox = new JComboBox();
		comboBoxData(protJcomboBox);
		vlanJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ACL_VLANID));// VLANID
		vlanField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);
		setValidate(vlanField,ConstantUtil.LABOAMETNVLAN_MAXVALUE,ConstantUtil.LABOAMETNVLAN_MINVALUE);
		vlanField.setText("1"); 
		isSourceMac = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_ACL_SOURCEMAC));//是否匹配源MAC地址
		sourceMacAddressField = new PtnTextField(true,PtnTextField.TYPE_MAC,PtnTextField.MAC_MAXLENGTH,this.lblMessage, this.confirm, this);//源MAC地址
		sourceMacAddressField.setText("00-00-00-00-00-01");
		isPurposeMac = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_ACL_PURPOSEMAC));//是否匹配目的MAC地址
		purposeMacAddressField = new PtnTextField(true,PtnTextField.TYPE_MAC,PtnTextField.MAC_MAXLENGTH,this.lblMessage, this.confirm, this);//目的MAC地址
		purposeMacAddressField.setText("00-00-00-00-00-01");
		
		isSourceIp = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_ACL_SOURCEIP));//是否匹配源IP地址
		sourceIpAddressField = new PtnTextField(true,PtnTextField.TYPE_IP,PtnTextField.IP_MAXLENGTH,this.lblMessage, this.confirm, this);//源IP地址
		sourceIpAddressField.setText("0.0.0.0");
		isPurposeIp = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_ACL_PURPOSEIP));//是否匹配目的P地址
		purposeIpAddressField = new PtnTextField(true,PtnTextField.TYPE_IP,PtnTextField.IP_MAXLENGTH,this.lblMessage, this.confirm, this);//目的IP
		purposeIpAddressField.setText("0.0.0.0");
		
		isMatchCos = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_ACL_MATCHCOS));//是否匹配cos
		matchCosField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);//cos值
		setValidate(matchCosField,ConstantUtil.LABELmdMLevel_MAXVALUE,ConstantUtil.LABELDelayTime_MINVALUE);
		matchCosField.setText("0");
		isMatchDSCP = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_ACL_MATCHDSCP));//是否匹配DSCP 
		matchDSCPField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);//cos值
		setValidate(matchDSCPField,ConstantUtil.LABACL_DSCPMAXVALUE,ConstantUtil.LABELDelayTime_MINVALUE);
		matchDSCPField.setText("0");
		isMatchTOS = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_ACL_MATCHDTOS));//是否匹配TOS
		matchTOSField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);//cos值
		setValidate(matchTOSField,ConstantUtil.LABELmdMLevel_MAXVALUE,ConstantUtil.LABELDelayTime_MINVALUE);
		matchTOSField.setText("0");
		isSourcePort = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_ACL_SOURCEPORT));//是否匹配源端口 
		sourcePortField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);//源端口号
		setValidate(sourcePortField,ConstantUtil.LABACL_MAXVALUE,ConstantUtil.LABOAMETNVLAN_MINVALUE);
		sourcePortField.setText("1");
		isPurposePort = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_ACL_PURPOSEPORT));//是否匹配目的端口 
		purposePortField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);//目的端口号
		setValidate(purposePortField,ConstantUtil.LABACL_MAXVALUE,ConstantUtil.LABOAMETNVLAN_MINVALUE);
		purposePortField.setText("1");
		ruleObjectJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ACL_RULEOBJECT));// 规则应用对象
		ruleObjectJcomBox = new JComboBox();		
	    transportLayerTypeJlabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TREATYTYPE));//传输层协议类型
	    transportLayerTypeJcomBox = new JComboBox();
	    super.getComboBoxDataUtil().comboBoxData(transportLayerTypeJcomBox, "TransportLayerType");
		number = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CREATE_NUM));
		numberPtnSpinner= new PtnSpinner(1024, 0, 1,ResourceUtil.srcStr(StringKeysLbl.LBL_CREATE_NUM));
		numberPtnSpinner.getTxt().setText("1");
		super.getComboBoxDataUtil().comboBoxData(ruleObjectJcomBox, "ACLRULEOBJECT");
		this.setTitle(ResourceUtil.srcStr(StringKeysPanel.PANEL_ACL_MANAGE));
	    panelContent = new JPanel();
		buttonPanel =new JPanel();
		isSourceMacJCheckBox = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_SOURCE_MAC_ADD));
		isTargetMacJCheckBox = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_TARGET_MAC_ADD));
		isSourceIpJCheckBox = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_SOURCE_IP_ADD));
		isTargetIpJCheckBox = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_TARGET_IP_ADD));
		this.buttonPanel.setPreferredSize(new Dimension(600, 40));
		setbuttonLouyContent();
		this.setLayout(new BorderLayout());
		setCenterLouyContent();
		this.add(this.panelContent, BorderLayout.CENTER);
		this.add(this.buttonPanel, BorderLayout.SOUTH);
	} catch (Exception e) {
		ExceptionManage.dispose(e,this.getClass());
	}
	}
	//修改 ：将界面的数据放在对应的数据里
	private void initData() {
		if(aclInfo != null){
		try {
			//动作
			super.getComboBoxDataUtil().comboBoxSelectByValue(actJComboBox, aclInfo.getAct()+"");
			//入出方向
			super.getComboBoxDataUtil().comboBoxSelectByValue(directionJComboBox, aclInfo.getDirection()+"");
			//端口号
			this.comboBoxSelect(protJcomboBox, aclInfo.getPortNumber()+"");
			// vlanID
			vlanField.setText(aclInfo.getVlanId()+"");
			// 是否匹配源MAC地址
			sourceMacAddressField.setText(aclInfo.getSourceMac());
			if(aclInfo.getIsSourceMac() == 1 ){
				isSourceMac.setSelected(true);
			}
			// 是否匹目的MAC地址:
			purposeMacAddressField.setText(aclInfo.getPurposeMac());
			if(aclInfo.getIsPurposeMac() == 1 ){
				isPurposeMac.setSelected(true);
			}
			
			//是否匹配源IP地址
			sourceIpAddressField.setText(aclInfo.getSourceIp());
			if(aclInfo.getIsSourceIp() == 1 ){
				isSourceIp.setSelected(true);
			}
			//是否匹目的IP地址:
			purposeIpAddressField.setText(aclInfo.getPurposeIp());
			if(aclInfo.getIsPurposeIp() == 1 ){
				isPurposeIp.setSelected(true);
			}
			
			//是否匹配cos：        0/1=否/是
			matchCosField.setText(aclInfo.getCosValue()+"");
			if(aclInfo.getIsMatchCos() == 1 ){
				isMatchCos.setSelected(true);
			}
			//是否匹配DSCP 
			matchDSCPField.setText(aclInfo.getDscpValue()+"");
			if(aclInfo.getIsMatchDSCP() == 1 ){
				isMatchDSCP.setSelected(true);
			}
			//是否匹配TOS
			matchTOSField.setText(aclInfo.getTosValue()+"");
			if(aclInfo.getIsMatchTOS() == 1 ){
				isMatchTOS.setSelected(true);
			}
			
			//是否匹配源端口：     0/1=否/是
			sourcePortField.setText(aclInfo.getSourcePort()+"");
			if(aclInfo.getIsSourcePort() == 1 ){
				isSourcePort.setSelected(true);
			}
			//是否匹配TOS
			purposePortField.setText(aclInfo.getPurposePort()+"");
			if(aclInfo.getIsPurposePort() == 1 ){
				isPurposePort.setSelected(true);
			}
			//规则应用对象
			super.getComboBoxDataUtil().comboBoxSelectByValue(ruleObjectJcomBox, aclInfo.getRuleObject()+"");
			super.getComboBoxDataUtil().comboBoxSelectByValue(transportLayerTypeJcomBox, aclInfo.getTreatyType()+"");
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	}
 /**
  * 
  * 设置布局
  */
	private void setbuttonLouyContent() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 410, 60, 60 };
		componentLayout.columnWeights = new double[] { 0, 0, 0 };
		componentLayout.rowHeights = new int[] { 30 };
		componentLayout.rowWeights = new double[] { 0.0 };
		this.buttonPanel.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;

		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.lblMessage, c);
		this.buttonPanel.add(this.lblMessage);

		c.gridx = 1;
		componentLayout.setConstraints(this.confirm, c);
		this.buttonPanel.add(this.confirm);

		c.gridx = 2;
		componentLayout.setConstraints(this.cancel, c);
		this.buttonPanel.add(this.cancel);
	}
	/**
	 * 
	 * 设置布局
	 * 
	 */
	private void setCenterLouyContent() {
		GridBagConstraints gridBagConstraints = null;
		GridBagLayout  gridBagLayout=null;
		try {
			gridBagConstraints = new GridBagConstraints();
			gridBagLayout=new GridBagLayout();
			gridBagLayout.columnWidths = new int[] { 10, 120, 10, 120};
			gridBagLayout.columnWeights = new double[] { 0, 0, 0 };
			gridBagLayout.rowHeights = new int[] { 5,5};
			gridBagLayout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			this.panelContent.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_ACL_MANAGE)));
			this.panelContent.setLayout(gridBagLayout);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(actJlabel, gridBagConstraints);
			panelContent.add(actJlabel);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(actJComboBox, gridBagConstraints);
			panelContent.add(actJComboBox);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(directionJLabel, gridBagConstraints);
			panelContent.add(directionJLabel);
			
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(directionJComboBox, gridBagConstraints);
			panelContent.add(directionJComboBox);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(portJlJLabel, gridBagConstraints);
			panelContent.add(portJlJLabel);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(protJcomboBox, gridBagConstraints);
			panelContent.add(protJcomboBox);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(vlanJLabel, gridBagConstraints);
			panelContent.add(vlanJLabel);
			
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(vlanField, gridBagConstraints);
			panelContent.add(vlanField);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(isSourceMac, gridBagConstraints);
			panelContent.add(isSourceMac);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(sourceMacAddressField, gridBagConstraints);
			panelContent.add(sourceMacAddressField);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(isPurposeMac, gridBagConstraints);
			panelContent.add(isPurposeMac);
			
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(purposeMacAddressField, gridBagConstraints);
			panelContent.add(purposeMacAddressField);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(isSourceIp, gridBagConstraints);
			panelContent.add(isSourceIp);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(sourceIpAddressField, gridBagConstraints);
			panelContent.add(sourceIpAddressField);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy =3;
			gridBagLayout.setConstraints(isPurposeIp, gridBagConstraints);
			panelContent.add(isPurposeIp);
			
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(purposeIpAddressField, gridBagConstraints);
			panelContent.add(purposeIpAddressField);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(isMatchCos, gridBagConstraints);
			panelContent.add(isMatchCos);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(matchCosField, gridBagConstraints);
			panelContent.add(matchCosField);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(isMatchDSCP, gridBagConstraints);
			panelContent.add(isMatchDSCP);
			
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(matchDSCPField, gridBagConstraints);
			panelContent.add(matchDSCPField);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(isMatchTOS, gridBagConstraints);
			panelContent.add(isMatchTOS);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(matchTOSField, gridBagConstraints);
			panelContent.add(matchTOSField);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(isSourcePort, gridBagConstraints);
			panelContent.add(isSourcePort);
			
			
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(sourcePortField, gridBagConstraints);
			panelContent.add(sourcePortField);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 6;
			gridBagLayout.setConstraints(isPurposePort, gridBagConstraints);
			panelContent.add(isPurposePort);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 6;
			gridBagLayout.setConstraints(purposePortField, gridBagConstraints);
			panelContent.add(purposePortField);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 6;
			gridBagLayout.setConstraints(ruleObjectJLabel, gridBagConstraints);
			panelContent.add(ruleObjectJLabel);
			
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 6;
			gridBagLayout.setConstraints(ruleObjectJcomBox, gridBagConstraints);
			panelContent.add(ruleObjectJcomBox);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 7;
			gridBagLayout.setConstraints(transportLayerTypeJlabel, gridBagConstraints);
			panelContent.add(transportLayerTypeJlabel);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 7;
			gridBagLayout.setConstraints(transportLayerTypeJcomBox, gridBagConstraints);
			panelContent.add(transportLayerTypeJcomBox);
			
			if(isAdd == 0){
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 8;
				gridBagLayout.setConstraints(isSourceIpJCheckBox, gridBagConstraints);
				panelContent.add(isSourceIpJCheckBox);
				
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 8;
				gridBagLayout.setConstraints(isTargetIpJCheckBox, gridBagConstraints);
				panelContent.add(isTargetIpJCheckBox);
				
				gridBagConstraints.gridx = 2;
				gridBagConstraints.gridy = 8;
				gridBagLayout.setConstraints(isSourceMacJCheckBox, gridBagConstraints);
				panelContent.add(isSourceMacJCheckBox);
				
				gridBagConstraints.gridx = 3;
				gridBagConstraints.gridy = 8;
				gridBagLayout.setConstraints(isTargetMacJCheckBox, gridBagConstraints);
				panelContent.add(isTargetMacJCheckBox);
				
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 9;
				gridBagLayout.setConstraints(number, gridBagConstraints);
				panelContent.add(number);
				
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 9;
				gridBagLayout.setConstraints(numberPtnSpinner, gridBagConstraints);
				panelContent.add(numberPtnSpinner);
				
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		}

	// 为 下来列表赋值
	private void comboBoxData(JComboBox jComboBox) throws Exception {
		DefaultComboBoxModel defaultComboBoxModel = null;
		PortService_MB portService = null;
		PortInst portInst = null;
		List<PortInst> allportInstList = null;
		try {

			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			portInst = new PortInst();
			portInst.setSiteId(ConstantUtil.siteId);
			portInst.setPortType(ConstantUtil.portType);
			allportInstList = portService.select(portInst);
			defaultComboBoxModel = (DefaultComboBoxModel) jComboBox.getModel();
			for (PortInst portInsts : allportInstList) {
					if (portInsts.getPortType().equalsIgnoreCase("uni")) {
						defaultComboBoxModel.addElement(new ControlKeyValue(portInsts.getNumber() + "", portInsts.getPortName(), portInsts));
					}
				}
			jComboBox.setModel(defaultComboBoxModel);

		} catch (Exception e) {
			throw e;
		} finally {
			defaultComboBoxModel = null;
			UiUtil.closeService_MB(portService);
			portInst = null;
			allportInstList = null;
		}
	}
	
   private  void addListener(){
	   try {
		   
		   // 取消按钮事件
		   cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		 });
		   
       confirm.addActionListener(new MyActionListener() {
	       @Override
	     public void actionPerformed(ActionEvent e) {
		  confirmAddListener();
	     }

		@Override
		public boolean checking() {
			// TODO Auto-generated method stub
			return true;
		}
      });	   
		   
	} catch (Exception e) {
		ExceptionManage.dispose(e,this.getClass());
	}
	   
   }
   

	private void confirmAddListener() {
		ControlKeyValue controlKeyValue = null;
		DispatchUtil dispath = null; 
		String result = null;
		AclService_MB aclService = null;
		AclInfo aclbefore = null;
		try {
			aclService = (AclService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ACLSERCVICE);
			if(aclInfo.getId()>0){
			  aclbefore=aclService.selectById(aclInfo.getId());	
			}
			if(protJcomboBox.getSelectedItem()==null){
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_MANAGE_PORT));
				return;
			}
			//SiteId
			aclInfo.setSiteId(ConstantUtil.siteId); 
			//动作
			controlKeyValue = (ControlKeyValue)actJComboBox.getSelectedItem();
			aclInfo.setAct(Integer.parseInt(((Code)controlKeyValue.getObject()).getCodeValue()));
			//入出方向
			controlKeyValue = (ControlKeyValue)directionJComboBox.getSelectedItem();
			aclInfo.setDirection(Integer.parseInt(((Code)controlKeyValue.getObject()).getCodeValue()));
			//端口号
			controlKeyValue = (ControlKeyValue)protJcomboBox.getSelectedItem();
			aclInfo.setPortNumber(((PortInst)controlKeyValue.getObject()).getNumber());
			// vlanID
			aclInfo.setVlanId(Integer.parseInt(vlanField.getText().trim()));
			// 是否匹配源MAC地址
			aclInfo.setIsSourceMac(isSourceMac.isSelected()?1:0);
			///源MAC地址
			aclInfo.setSourceMac(sourceMacAddressField.getText().trim());
			// 是否匹目的MAC地址:
			aclInfo.setIsPurposeMac(isPurposeMac.isSelected()?1:0);
			///目的MAC地址
			aclInfo.setPurposeMac(purposeMacAddressField.getText().trim());
			//是否匹配源IP地址
			aclInfo.setIsSourceIp(isSourceIp.isSelected()?1:0);
			////源ip地址
			aclInfo.setSourceIp(sourceIpAddressField.getText().trim());
			
			//是否匹目的IP地址:
			aclInfo.setIsPurposeIp(isPurposeIp.isSelected()?1:0);
			//目的IP地址
			aclInfo.setPurposeIp(purposeIpAddressField.getText().trim());
			
			//是否匹配cos：        0/1=否/是
			aclInfo.setIsMatchCos(isMatchCos.isSelected()?1:0);
			//cos值 ：             0-7
			aclInfo.setCosValue(Integer.parseInt(matchCosField.getText().trim()));
			
			//是否匹配DSCP 
			aclInfo.setIsMatchDSCP(isMatchDSCP.isSelected()?1:0);
			//DSCP   
			aclInfo.setDscpValue(Integer.parseInt(matchDSCPField.getText().trim()));
			
			//是否匹配TOS
			aclInfo.setIsMatchTOS(isMatchTOS.isSelected()?1:0);
			//TOS
			aclInfo.setTosValue(Integer.parseInt(matchTOSField.getText().trim()));
			
			//是否匹配源端口：     0/1=否/是
			aclInfo.setIsSourcePort(isSourcePort.isSelected()?1:0);
			//源端口号            1-65535
			aclInfo.setSourcePort(Integer.parseInt(sourcePortField.getText().trim()));
			
			//是否匹配TOS
			aclInfo.setIsPurposePort(isPurposePort.isSelected()?1:0);
			//TOS
			aclInfo.setPurposePort(Integer.parseInt(purposePortField.getText().trim()));
			
			//规则应用对象
			controlKeyValue = (ControlKeyValue)ruleObjectJcomBox.getSelectedItem();
			aclInfo.setRuleObject(Integer.parseInt(((Code)controlKeyValue.getObject()).getCodeValue()));
			
			//传输层协议类型
			controlKeyValue = (ControlKeyValue)transportLayerTypeJcomBox.getSelectedItem();
			aclInfo.setTreatyType(Integer.parseInt(((Code)controlKeyValue.getObject()).getCodeValue()));
			
			int operateKey = 0;
			//添加日志记录
			if(aclInfo.getId() > 0)
			{
				operateKey = EOperationLogType.UPDATEACL.getValue();
			}else
			{
				operateKey = EOperationLogType.CREATEACL.getValue();
			}
			
			
			List<AclInfo> aclInfos = new ArrayList<AclInfo>();
			if(isAdd == 0){
				int number = Integer.parseInt(numberPtnSpinner.getTxt().getText().replace(",", ""));
				int id = 0;
				
				for (int i = 0; i < number; i++) {
					AclInfo aclInfo = new AclInfo();
					CoderUtils.copy(this.aclInfo, aclInfo);
					aclInfo.setVlanId(Integer.parseInt(vlanField.getText())+i);
					if(i>0){
						if(isSourceMacJCheckBox.isSelected()){
							aclInfo.setSourceMac(addMAC_IP(aclInfos.get(aclInfos.size()-1).getSourceMac(),"MAC"));
						}else{
							aclInfo.setSourceMac(sourceMacAddressField.getText());
						}
						if(isTargetMacJCheckBox.isSelected()){
							aclInfo.setPurposeMac(addMAC_IP(aclInfos.get(aclInfos.size()-1).getPurposeMac(),"MAC"));
						}else{
							aclInfo.setPurposeMac(purposeMacAddressField.getText());
						}
						
						if(isSourceIpJCheckBox.isSelected()){
							aclInfo.setSourceIp(addMAC_IP(aclInfos.get(aclInfos.size()-1).getSourceIp(),"IP"));
						}else{
							aclInfo.setSourceIp(sourceIpAddressField.getText());
						}
						if(isTargetIpJCheckBox.isSelected()){
							aclInfo.setPurposeIp(addMAC_IP(aclInfos.get(aclInfos.size()-1).getPurposeIp(),"IP"));
						}else{
							aclInfo.setPurposeIp(purposeIpAddressField.getText());
						}
					}
					aclInfos.add(aclInfo);
				}
				if(aclInfos.size()>0){
					aclService.batchSave(aclInfos);					
					aclInfo = aclInfos.get(aclInfos.size()-1);
				}else{
					aclInfo.setId(id);
				}
			}
			
			dispath = new DispatchUtil(RmiKeys.RMI_ACL);
			result = dispath.excuteInsert(aclInfo);
			if(operateKey == EOperationLogType.UPDATEACL.getValue()){	
				aclInfo.setPortName(this.selectProt(aclInfo.getPortNumber()+"", ConstantUtil.siteId, 0));			
				aclbefore.setPortName(this.selectProt(aclbefore.getPortNumber()+"", ConstantUtil.siteId, 0));
				this.insertOpeLog(operateKey, result, aclbefore, aclInfo);				
			}else{
				if(aclInfos.size()>0){
					for(int i=0;i<aclInfos.size();i++){
						aclInfos.get(i).setPortName(this.selectProt(aclInfos.get(i).getPortNumber()+"", ConstantUtil.siteId, 0));
					   this.insertOpeLog(operateKey, result, null, aclInfos.get(i));
					}			
				}else{
					aclInfo.setId(0);
					aclInfo.setPortName(this.selectProt(aclInfo.getPortNumber()+"", ConstantUtil.siteId, 0));
					this.insertOpeLog(operateKey, result, null, aclInfo);
				}	
			}
			// 跟新界面
			controller.refresh();
			// 隐藏界面
			dialog.dispose();
			DialogBoxUtil.succeedDialog(this, result);
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			controlKeyValue = null;
			dispath = null; 
			UiUtil.closeService_MB(aclService);
		}
	}
	
	private void insertOpeLog(int operationType, String result, AclInfo oldMac, AclInfo newMac){
		AddOperateLog.insertOperLog(confirm, operationType, result, oldMac, newMac, newMac.getSiteId(),ResourceUtil.srcStr(StringKeysPanel.PANEL_ACL_MANAGE),"aclInfo");		
	}
	
	private String selectProt(String number,int siteId,int portId) {
		PortService_MB portService = null;
		PortInst portInst = null;
		List<PortInst> allportInstList = null;
		String portName = null;
		try {
			allportInstList=new ArrayList<PortInst>();
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			portInst = new PortInst();
			portInst.setSiteId(siteId);
			portInst.setNumber(Integer.parseInt(number));
			portInst.setPortId(portId);
			allportInstList = portService.select(portInst);
			if(allportInstList!=null&&allportInstList.size()>0){
				 portName = allportInstList.get(0).getPortName();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(portService);
			portInst = null;
			allportInstList = null;
		}
		return portName;
	}
	
	private String addMAC_IP(String value,String type){
		List<Integer> integers = new ArrayList<Integer>();
		String[] macs = null;
		if("MAC".equals(type)){
			macs = value.split("-");
			for(String str : macs){
				integers.add(Integer.parseInt(str, 16));
			}
		}else if("IP".equals(type)){
			macs = value.split("\\.");
			for(String str : macs){
				integers.add(Integer.parseInt(str));
			}
		}
		addIng(integers,integers.size()-1);
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < integers.size(); i++) {
			if("MAC".equals(type)){
				if(i+1 == integers.size()){
					stringBuffer.append(toMAC(Integer.toHexString(integers.get(i))));
				}else{
					stringBuffer.append(toMAC(Integer.toHexString(integers.get(i)))+"-");
				}
			}else if("IP".equals(type)){
				if(i+1 == integers.size()){
					stringBuffer.append(integers.get(i));
				}else{
					stringBuffer.append(integers.get(i)+".");
				}
			}
			
		}
		value = stringBuffer.toString();
		return value;
	}
	
	private String toMAC(String value){
		if(value.length() ==1){
			value = "0"+value;
		}
		return value.toUpperCase();
	}
	
	
	private void addIng(List<Integer> integers,int in){
		if(in >= 0 && integers.get(in)==255){
			if(in >0){
				addIng(integers, in-1);
			}else{
				addIng(integers, integers.size());
			}
			integers.set(in, 0);
		}else{
			integers.set(in, integers.get(in)+1);
		}
	}
	
	
	// 根据ID来配置下拉列表所选的值
	public void comboBoxSelect(JComboBox jComboBox, String selectId) {
		for (int i = 0; i < jComboBox.getItemCount(); i++) {
			PortInst portInfo=(PortInst)(((ControlKeyValue)jComboBox.getItemAt(i)).getObject());
			if (portInfo.getNumber()==Integer.parseInt(selectId)) {
				jComboBox.setSelectedIndex(i);
				return;
			}
		}
	}
	
	
/**
 * 为文本控件赋最大值和最小值
 * @param txtField
 * @param max
 * @param min
 * @throws Exception
 */
	private void setValidate(PtnTextField txtField,int max,int min) throws Exception{
		try {
			txtField.setCheckingMaxValue(true);
			txtField.setCheckingMinValue(true);
			txtField.setMaxValue(max);
			txtField.setMinValue(min);
		} catch (Exception e) {
			throw e;
		}
	}
	
}