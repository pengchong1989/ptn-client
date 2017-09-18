﻿package com.nms.ui.ptn.ne.allConfig.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.AllConfigInfo;
import com.nms.db.bean.system.code.Code;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.CheckingUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnPanel;
import com.nms.ui.manager.control.PtnSpinner;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.ne.allConfig.controller.AllConfigController;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

public class AllConifgPanel extends PtnPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel singleMACAddressLabel;// 单盘MAC地址
	private JTextField singleMACAddressFiled;
	private JLabel addressAgeSwitchLabel;// 地址老化开关
	private JComboBox addressAgeSwitchBox;
	private JLabel macAddressAgeDateLabel;// MAC地址老化时间
	private JTextField macAddressAgeDateFiled;
	private JLabel throwWrapDateGapLabel;// 丢包率统计时间间隔
	private JTextField throwWrapDateGapField;
	private JLabel fdiBIT0Label;// FDI帧 帧发送使能BIT0:0/1=不使能/使能
	private JComboBox fdiBIT0Box;
	private JLabel fdiB1IT3Label;// FDI帧 FDI帧MEL:BIT3-BIT1: 0/1/2..6/7
	private JTextField fdiB1IT3Field;
	private JLabel apsRecoverTimeLabel;// aps等待恢复时间
//	private JTextField apsRecoverTimeField;
	private PtnSpinner apsRecoverTimeField;
	private JLabel crcVerifyLabel;// CRC校验错门限
	private JTextField crcVerifyfField;
	private JLabel throwWrapLabel;// 丢包个数门限
	private JTextField throwWrapField;
	private JLabel receiveBadWrapLabel;// 收坏包数门限
	private JTextField receiveBadWrapField;
	private JLabel tmsWorsenLabel;// TMS通道信号劣化门限
	private JTextField tmsWorsenField;
	private JLabel tmsLoseLabel;// TMS通道信号失效门限
	private JTextField tmsLoseField;
	private JLabel alignLabel;// 对齐错门限
	private JTextField alignField;
	private JLabel mirrorModelLabel;// 镜像模式
	private JComboBox mirrorModelCheckBox;
	private JLabel mirrorByPortLabel;// 被镜像端口
	private JComboBox mirrorByPortBox;
	private JLabel mirrorPortLabel;// 镜像端口
	private JComboBox mirrorportBox;
	private JLabel mplsTPControlLabel;// MPLS-TP控制字使能
	private JComboBox mplsTPControlBox;
	private JLabel channelTypeLabel;// Channel Type
	private JTextField channelTypeField;
	private JLabel tmcfdiBIT0Label;// FDI帧 帧发送使能BIT0:0/1=不使能/使能
	private JComboBox tmcfdiBIT0Box;
	private JLabel tmcfdiB1IT3Label;// FDI帧 FDI帧MEL:BIT3-BIT1: 0/1/2..6/7
	private JTextField tmcfdiB1IT3Field;
	private PtnButton okButton;
	private PtnButton syncButton;
	private JPanel buttonPanel;
	private JLabel lblTitle;
	private JPanel titlePanel;
	private JScrollPane scrollPanel;
	private JPanel contentPanel;
	private AllConfigController allConfigController = null;
	private AllConfigInfo info = null;
	private JComboBox apsModel;//APS模式
	private JLabel apsJLabel;
	private JLabel roundtest;//环路检测功能
	private JComboBox roundtestComboBox;
	private JLabel vlanMAC;//基于vlan的MAC学习限制
	private JComboBox vlanMAComboBox;
	private JLabel vlanJLabel;//MAC学习限制基于vlan值
	private JTextField vlanField;
	private JLabel macNumber;//MAC学习限制数
	private JTextField macNumberField;
	private JLabel lacpJLabel;//LACP协议开关
	private JComboBox lacpJComboBox;
	private JLabel priorityJLabel;//设备优先级
	private JTextField priorityJTextField;
	private JLabel dhcpJLabel;//dhcp模式
	private JComboBox dhcpComboBox;
	private JLabel loop;//环路检测开关
	private JComboBox loopJComboBox;
	private JLabel ssmJLabel;//ssm帧模式
	private JComboBox ssmJComboBox;
	private JLabel jbltwoLayer;//二层功能模式开关
	private JComboBox jcmbtwoLayer;
	private JLabel alarmModelLBL;//掉电告警上联模式 : 1/2/3 NNI侧模式/UNI侧模式/802.3ah模式  （默认NNI侧模式）
	private JComboBox alarmModelCbox;
	private JLabel alarmPortLBL;//掉电告警上联端口 1/2/3/4/5/6/7 GE1.1/GE1.2/GE1.3/GE1.4/FE1.1/FE1.2 （默认GE1.1）
	private JComboBox alarmPortCbox;
	private JLabel loopAvoidLBL;//环路避免功能开关：0/1 = 关/开
	private JComboBox loopAvoidCbox;
	
	
	/**
	 * 创建一个新的实例
	 */
	public AllConifgPanel() {
		init();
		allConfigController = new AllConfigController(this);
	}

	/**
	 * 初始化
	 */
	private void init() {
		try {
			initComponents();
			setLayout();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}
	/**
	 * 初始化控件
	 */
	private void initComponents()  throws Exception {
		lblTitle = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ALL_CONFIG));
		titlePanel = new JPanel();
		titlePanel.setBorder(BorderFactory.createEtchedBorder());
		titlePanel.setSize(60, ConstantUtil.INT_WIDTH_THREE);
		scrollPanel = new JScrollPane();
		contentPanel = new JPanel();
		scrollPanel.setViewportView(contentPanel);
		scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		singleMACAddressLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SINGLE_MAC));
		singleMACAddressFiled = new JTextField();
		addressAgeSwitchLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ADDRESS_SWITCH));
		addressAgeSwitchBox = new JComboBox();
		addressAgeSwitchBox.addItem(ResourceUtil.srcStr(StringKeysObj.ALLCONFIG_CLOSE));
		addressAgeSwitchBox.addItem(ResourceUtil.srcStr(StringKeysObj.ALLCONFIG_OPEN));
		macAddressAgeDateLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ADDRESS_TIME));
		macAddressAgeDateFiled = new JTextField();
		throwWrapDateGapLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PACKET_LOSS_INTERVAL));
		throwWrapDateGapField = new JTextField();
		fdiBIT0Label = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_FDI_ENABLED));
		fdiBIT0Box = new JComboBox();
		fdiBIT0Box.addItem(ResourceUtil.srcStr(StringKeysObj.ALLCONFIG_FID_ENABLED_NO));
		fdiBIT0Box.addItem(ResourceUtil.srcStr(StringKeysObj.ALLCONFIG_FID_ENABLED));
		fdiB1IT3Label = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_FDI_MEL));
		fdiB1IT3Field = new JTextField();
		tmcfdiBIT0Label = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TMC_FDI_ENABLED));
		tmcfdiBIT0Box = new JComboBox();
		tmcfdiBIT0Box.addItem(ResourceUtil.srcStr(StringKeysObj.ALLCONFIG_FID_ENABLED_NO));
		tmcfdiBIT0Box.addItem(ResourceUtil.srcStr(StringKeysObj.ALLCONFIG_FID_ENABLED));
		tmcfdiB1IT3Label = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TMC_FDI_MEL));
		tmcfdiB1IT3Field = new JTextField();
		apsRecoverTimeLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_APS_TIME));
		this.apsRecoverTimeField=new PtnSpinner(12,1,1,ResourceUtil.srcStr(StringKeysLbl.LBL_APS_TIME));
		crcVerifyLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CRC_VERIFY));
		crcVerifyfField = new JTextField();
		throwWrapLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PACKET_LOSS_NUM));
		throwWrapField = new JTextField();
		receiveBadWrapLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PACKET_BAD));
		receiveBadWrapField = new JTextField();
		tmsWorsenLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TMS_DEGRADATION));
		tmsWorsenField = new JTextField();
		tmsLoseLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TMS_LOSE_EFFICACY));
		tmsLoseField = new JTextField();
		alignLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ALIGNING));
		alignField = new JTextField();
		mirrorModelLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_IMAGE_MODAL));
		mirrorModelCheckBox = new JComboBox();
		mirrorModelCheckBox.addItem(ResourceUtil.srcStr(StringKeysObj.ALLCONFIG_CLOSE));
		mirrorModelCheckBox.addItem(ResourceUtil.srcStr(StringKeysObj.IN_PORT_IMAGE));
		mirrorModelCheckBox.addItem(ResourceUtil.srcStr(StringKeysObj.OUT_PORT_IMAGE));
		mirrorModelCheckBox.addItem(ResourceUtil.srcStr(StringKeysObj.BOTHWAY_IMAGE));
		mirrorByPortLabel = new JLabel(ResourceUtil.srcStr(StringKeysObj.BY_IMAGE));
		mirrorByPortBox = new JComboBox();
		/*获取网元的port信息*/
		mirrorPortLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_IMAGE_PORT));
		mirrorportBox = new JComboBox();
		
		comboBoxData(mirrorByPortBox,1);
		comboBoxData(mirrorportBox,0);
		mplsTPControlLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MPLS_TP_ENABLED));
		mplsTPControlBox = new JComboBox();
		mplsTPControlBox.addItem(ResourceUtil.srcStr(StringKeysObj.ALLCONFIG_FID_ENABLED_NO));
		mplsTPControlBox.addItem(ResourceUtil.srcStr(StringKeysObj.ALLCONFIG_FID_ENABLED));
		channelTypeLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CHANNEL_TYPE));
		channelTypeField = new JTextField();
		apsModel = new JComboBox();
		apsJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_APS_MODEL));
		super.getComboBoxDataUtil().comboBoxData(apsModel, "APSMODEL");
		roundtest = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ROUNDTEST));
		roundtestComboBox = new JComboBox();
		super.getComboBoxDataUtil().comboBoxData(roundtestComboBox, "ENABLEDSTATUE");
		
		vlanMAC = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_VLAN_MAC));
		vlanMAComboBox = new JComboBox();
		super.getComboBoxDataUtil().comboBoxData(vlanMAComboBox, "ENABLEDSTATUE");
		vlanJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_VLAN_VALUE));
		vlanField = new JTextField("1");
		macNumber = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MAC_NUMBER));
		macNumberField = new JTextField();
		lacpJLabel = new JLabel("LACP"+ResourceUtil.srcStr(StringKeysLbl.VCTRAFFICPOLICING));//LACP协议开关
		lacpJComboBox = new JComboBox();
		super.getComboBoxDataUtil().comboBoxData(lacpJComboBox, "VCTRAFFICPOLICING");
		priorityJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.EQUIPMENT_PRIORITY));//设备优先级
		priorityJTextField = new JTextField(1);
		dhcpJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DHCP_MODEL));
		dhcpComboBox = new JComboBox();
		loop = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LOOP_CHECK));
		loopJComboBox = new JComboBox();
		super.getComboBoxDataUtil().comboBoxData(loopJComboBox, "VCTRAFFICPOLICING");
		ssmJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.SSM_FRAME));
		ssmJComboBox = new JComboBox();
		super.getComboBoxDataUtil().comboBoxData(ssmJComboBox, "SSMMODEL");
		jbltwoLayer = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TWOLAYER));
		jcmbtwoLayer = new JComboBox();
		super.getComboBoxDataUtil().comboBoxData(jcmbtwoLayer, "VCTRAFFICPOLICING");
		
		super.getComboBoxDataUtil().comboBoxData(dhcpComboBox, "DHCPMODEL");
		okButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),true,RootFactory.CORE_MANAGE);
		syncButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SYNCHRO),true,RootFactory.CORE_MANAGE);
		buttonPanel = new JPanel();
		this.alarmModelLBL = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_INTERRUPT_ALARM_MODEL));//掉电告警上联模式
		this.alarmModelCbox = new JComboBox();
		super.getComboBoxDataUtil().comboBoxData(this.alarmModelCbox, "ALARMMODEL");
		this.alarmPortLBL = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ALARM_PORT));//掉电告警上联端口
		this.alarmPortCbox = new JComboBox();
		this.comboBoxData(this.alarmPortCbox, 0);
		this.loopAvoidLBL = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LOOP_AVOID_SWITCH));
		this.loopAvoidCbox = new JComboBox();
		super.getComboBoxDataUtil().comboBoxData(this.loopAvoidCbox, "VCTRAFFICPOLICING");
	}

	
	public void setModel(JComboBox cbBox, Map<Integer, String> keyValuse) {
		DefaultComboBoxModel model = (DefaultComboBoxModel) cbBox.getModel();
		for (Integer key : keyValuse.keySet()) {
			model.addElement(new ControlKeyValue(key.toString(), keyValuse.get(key)));
		}
	}

	/**
	 * 布局管理
	 */
	private void setLayout() {
		// title面板布局
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		layout.rowHeights = new int[] { 40 };
		layout.rowWeights = new double[] { 0 };
		layout.columnWidths = new int[] { 60, ConstantUtil.INT_WIDTH_THREE - 60 };
		layout.columnWeights = new double[] { 0, 1.0 };
		titlePanel.setLayout(layout);
		addComponent(titlePanel, lblTitle, 0, 0, 0, 0, 1, 1, GridBagConstraints.BOTH, new Insets(5, 20, 5, 5), GridBagConstraints.CENTER, c);
		// 主面板布局
		layout = new GridBagLayout();
		layout.rowHeights = new int[] { 60, 300, 60 };
		layout.rowWeights = new double[] { 0, 0.7, 0 };
		layout.columnWidths = new int[] { ConstantUtil.INT_WIDTH_THREE };
		layout.columnWeights = new double[] { 1 };
		this.setLayout(layout);
		addComponent(this, titlePanel, 0, 0, 0, 0, 1, 1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, c);
		addComponent(this, scrollPanel, 0, 1, 0, 0.2, 1, 1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, c);
		addComponent(this, buttonPanel, 0, 2, 0, 0, 1, 1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, c);

		// content面板布局
		GridBagLayout contentLayout = new GridBagLayout();
		contentPanel.setLayout(contentLayout);
		contentPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.LBL_ALL_CONFIG)));
		Insets insert = new Insets(5, 50, 5, 5);
		addComponent(contentPanel, singleMACAddressLabel, 0, 0, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, singleMACAddressFiled, 1, 0, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, addressAgeSwitchLabel, 0, 1, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, addressAgeSwitchBox, 1, 1, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, macAddressAgeDateLabel, 0, 2, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, macAddressAgeDateFiled, 1, 2, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, throwWrapDateGapLabel, 0, 3, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, throwWrapDateGapField, 1, 3, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, fdiBIT0Label, 0, 4, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, fdiBIT0Box, 1, 4, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, fdiB1IT3Label, 0, 5, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, fdiB1IT3Field, 1, 5, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, apsRecoverTimeLabel, 0, 6, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, apsRecoverTimeField, 1, 6, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, apsJLabel, 0, 7, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.EAST, c);
		addComponent(contentPanel, apsModel, 1, 7, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, vlanMAC, 0, 8, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.EAST, c);
		addComponent(contentPanel, vlanMAComboBox, 1, 8, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, vlanJLabel, 0, 9, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.EAST, c);
		addComponent(contentPanel, vlanField, 1, 9, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, lacpJLabel, 0, 10, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.EAST, c);
		addComponent(contentPanel, lacpJComboBox, 1, 10, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, loop, 0, 11, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.EAST, c);
		addComponent(contentPanel, loopJComboBox, 1, 11, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		
		
		addComponent(contentPanel, mirrorModelLabel, 3, 0, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, mirrorModelCheckBox, 4, 0, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, mirrorByPortLabel, 3, 1, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, mirrorByPortBox, 4, 1, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, mirrorPortLabel, 3, 2, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, mirrorportBox, 4, 2, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, mplsTPControlLabel, 3, 3, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, mplsTPControlBox, 4, 3, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, channelTypeLabel, 3, 4, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, channelTypeField, 4, 4, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, tmcfdiB1IT3Label, 3, 5, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, tmcfdiB1IT3Field, 4, 5, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, tmcfdiBIT0Label, 3, 6, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.EAST, c);
		addComponent(contentPanel, tmcfdiBIT0Box, 4, 6, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, roundtest, 3, 7, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.EAST, c);
		addComponent(contentPanel, roundtestComboBox, 4, 7, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, macNumber, 3, 8, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.EAST, c);
		addComponent(contentPanel, macNumberField, 4, 8, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, priorityJLabel, 3, 9, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.EAST, c);
		addComponent(contentPanel, priorityJTextField, 4, 9, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, dhcpJLabel, 3, 10, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.EAST, c);
		addComponent(contentPanel, dhcpComboBox, 4, 10, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, ssmJLabel, 3, 11, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.EAST, c);
		addComponent(contentPanel, ssmJComboBox, 4, 11, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		
		addComponent(contentPanel, jbltwoLayer, 0, 12, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.EAST, c);
		addComponent(contentPanel, jcmbtwoLayer, 1, 12, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, this.alarmModelLBL, 3, 12, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.EAST, c);
		addComponent(contentPanel, this.alarmModelCbox, 4, 12, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, this.alarmPortLBL, 0, 13, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.EAST, c);
		addComponent(contentPanel, this.alarmPortCbox, 1, 13, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, this.loopAvoidLBL, 3, 13, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		addComponent(contentPanel, this.loopAvoidCbox, 4, 13, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
		// button面板布局
		GridBagLayout buttonLayout = new GridBagLayout();
		buttonLayout.columnWidths = new int[] { 60, 60, 60, 6 };
		buttonLayout.columnWeights = new double[] { 1.0, 0, 0, 0 };
		buttonLayout.rowHeights = new int[] { 60 };
		buttonLayout.rowWeights = new double[] { 1 };
		buttonPanel.setLayout(buttonLayout);
		addComponent(buttonPanel, syncButton, 1, 0, 0, 0, 1, 1, GridBagConstraints.WEST, new Insets(5, 5, 5, 20), GridBagConstraints.WEST, c);
		addComponent(buttonPanel, okButton, 2, 0, 0, 0, 1, 1, GridBagConstraints.WEST, new Insets(5, 5, 5, 20), GridBagConstraints.WEST, c);
	}

	/**
	 * 初始化界面
	 * 
	 * @param info
	 * @throws Exception
	 */
	public void refresh(AllConfigInfo info) throws Exception {
		if(info!=null){
			singleMACAddressFiled.setText(info.getSingleMACAddress());
			addressAgeSwitchBox.setSelectedIndex(info.getAddressAgeSwitch());
			macAddressAgeDateFiled.setText(info.getMacAddressAgeDate() + "");
			throwWrapDateGapField.setText(info.getThrowWrapDateGap() + "");
			fdiBIT0Box.setSelectedIndex(info.getFdiBIT0());
			fdiB1IT3Field.setText(info.getFdiB1IT3() + "");
			apsRecoverTimeField.getTxt().setText(info.getApsRecoverTime() + "");
			crcVerifyfField.setText(info.getCrcVerify() + "");
			throwWrapField.setText(info.getThrowWrap() + "");
			receiveBadWrapField.setText(info.getReceiveBadWrap() + "");
			tmsWorsenField.setText(info.getTmsWorsen() + "");
			tmsLoseField.setText(info.getTmsLose() + "");
			alignField.setText(info.getAlign() + "");
			mirrorModelCheckBox.setSelectedIndex(info.getMirrorModel());
//			mirrorByPortBox.setSelectedIndex(info.getMirrorByPort());
//			mirrorportBox.setSelectedIndex(info.getMirrorPort());
			mplsTPControlBox.setSelectedIndex(info.getMplsTPControl());
			channelTypeField.setText(info.getChannelType() + "");
			tmcfdiBIT0Box.setSelectedIndex(info.getTmcfdiBIT0());
			tmcfdiB1IT3Field.setText(info.getTmcfdiB1IT3() + "");
			super.getComboBoxDataUtil().comboBoxSelectByValue(apsModel, info.getApsModel()+"");
			super.getComboBoxDataUtil().comboBoxSelectByValue(roundtestComboBox, info.getRoundEnable()+"");
			super.getComboBoxDataUtil().comboBoxSelectByValue(vlanMAComboBox, info.getVlanMAC()+"");
			super.getComboBoxDataUtil().comboBoxSelectByValue(dhcpComboBox, info.getDhcpModel()+"");
			if(info.getMirrorByPort()!=0){
				// 被镜像端口
				this.comboBoxSelect(this.mirrorByPortBox, info.getMirrorByPort());
			}
			if(info.getMirrorPort()!=0){
				// 镜像端口
				this.comboBoxSelect(this.mirrorportBox, info.getMirrorPort());
			}
			vlanField.setText(info.getVlanValue()+"");
			macNumberField.setText(info.getMacNumber()+"");
			super.getComboBoxDataUtil().comboBoxSelectByValue(lacpJComboBox, info.getLacp()+"");
			priorityJTextField.setText(info.getEquipmentPriority()+"");
			super.getComboBoxDataUtil().comboBoxSelectByValue(loopJComboBox, info.getLoopCheck()+"");
			super.getComboBoxDataUtil().comboBoxSelectByValue(ssmJComboBox, info.getSsmModel()+"");
			super.getComboBoxDataUtil().comboBoxSelectByValue(jcmbtwoLayer, info.getTwoLayer()+"");
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.alarmModelCbox, info.getAlarmModel()+"");
			this.comboBoxSelect(this.alarmPortCbox, info.getAlarmPort());
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.loopAvoidCbox, info.getLoopAvoid()+"");
		}
		
	}
	
	// 根据ID来配置下拉列表所选的值
	public void comboBoxSelect(JComboBox jComboBox, int selectId) {
		for (int i = 0; i < jComboBox.getItemCount(); i++) {
			PortInst portinst=(PortInst)((ControlKeyValue) jComboBox.getItemAt(i)).getObject();
			if (portinst.getNumber()==selectId) {
				jComboBox.setSelectedIndex(i);
				return;
			}
		}
	}
	
	
	// 为 下来列表赋值
	private void comboBoxData(JComboBox jComboBox, int lebel) throws Exception {
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
			// defaultComboBoxModel.addElement(new ControlKeyValue("", "", new
			// PortInst()));
			if (lebel == 1) {
				for (PortInst portInsts : allportInstList) {
					if (portInsts.getPortType().equalsIgnoreCase("nni") || portInsts.getPortType().equalsIgnoreCase("uni")
							|| portInsts.getPortType().equalsIgnoreCase("NONE")) {
						defaultComboBoxModel.addElement(new ControlKeyValue(portInsts.getID() + "", portInsts.getPortName(), portInsts));
					}
				}
			} else {
				for (PortInst portInsts : allportInstList) {
					if (portInsts.getPortType().equalsIgnoreCase("nni") || portInsts.getPortType().equalsIgnoreCase("uni")
							|| portInsts.getPortType().equalsIgnoreCase("NONE")) {
						defaultComboBoxModel.addElement(new ControlKeyValue(portInsts.getNumber() + "", portInsts.getPortName(), portInsts));
					}
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
	
	/**
	 * 收集界面数据
	 * 
	 * @return
	 */
	public AllConfigInfo pageSetValue(AllConfigInfo info) {
		try {
			info.setSingleMACAddress(singleMACAddressFiled.getText());
			info.setAddressAgeSwitch(addressAgeSwitchBox.getSelectedIndex());
			info.setMacAddressAgeDate(Integer.parseInt(macAddressAgeDateFiled.getText()));
			info.setThrowWrapDateGap(Integer.parseInt(throwWrapDateGapField.getText()));
			info.setFdiBIT0(fdiBIT0Box.getSelectedIndex());
			info.setFdiB1IT3(Integer.parseInt(fdiB1IT3Field.getText()));
			info.setTmcfdiBIT0(tmcfdiBIT0Box.getSelectedIndex());
			info.setTmcfdiB1IT3(Integer.parseInt(tmcfdiB1IT3Field.getText()));
			info.setApsRecoverTime(Integer.parseInt(apsRecoverTimeField.getTxtData()));
			info.setCrcVerify(Integer.parseInt(crcVerifyfField.getText()));
			info.setThrowWrap(Integer.parseInt(throwWrapField.getText()));
			info.setReceiveBadWrap(Integer.parseInt(receiveBadWrapField.getText()));
			info.setTmsWorsen(Integer.parseInt(tmsWorsenField.getText()));
			info.setTmsLose(Integer.parseInt(tmsLoseField.getText()));
			info.setAlign(Integer.parseInt(alignField.getText()));
			info.setMirrorModel(mirrorModelCheckBox.getSelectedIndex());
			info.setMirrorModelLog(mirrorModelCheckBox.getSelectedItem().toString());
			ControlKeyValue selecttunnel = (ControlKeyValue) mirrorByPortBox.getSelectedItem();
			PortInst portInst = (PortInst) selecttunnel.getObject();
			
			ControlKeyValue selecttunnels = (ControlKeyValue) mirrorportBox.getSelectedItem();
			PortInst portInsts = (PortInst) selecttunnels.getObject();
			// 被镜像端口
			info.setMirrorByPort(portInst.getNumber());
			info.setMirrorByPortNameLog(portInst.getPortName());
			// 镜像端口
			info.setMirrorPort(portInsts.getNumber());
			info.setMirrorPortNameLog(portInsts.getPortName());
			
			info.setMplsTPControl(mplsTPControlBox.getSelectedIndex());
			info.setChannelType(Integer.parseInt(channelTypeField.getText()));
			info.setSiteId(ConstantUtil.siteId);
			ControlKeyValue controlKeyValue = (ControlKeyValue) this.apsModel.getSelectedItem();
			info.setApsModel(Integer.parseInt(((Code) controlKeyValue.getObject()).getCodeValue()));
			controlKeyValue = (ControlKeyValue) this.roundtestComboBox.getSelectedItem();
			info.setRoundEnable(Integer.parseInt(((Code) controlKeyValue.getObject()).getCodeValue()));
			controlKeyValue = (ControlKeyValue) this.vlanMAComboBox.getSelectedItem();
			info.setVlanMAC(Integer.parseInt(((Code) controlKeyValue.getObject()).getCodeValue()));
			info.setVlanValue(Integer.parseInt(vlanField.getText()));
			info.setMacNumber(Integer.parseInt(macNumberField.getText()));
			ControlKeyValue lacp = (ControlKeyValue) this.lacpJComboBox.getSelectedItem();
			info.setLacp(Integer.parseInt(((Code) lacp.getObject()).getCodeValue()));
			info.setEquipmentPriority(Integer.parseInt(priorityJTextField.getText()));
			ControlKeyValue dhcp = (ControlKeyValue) this.dhcpComboBox.getSelectedItem();
			info.setDhcpModel(Integer.parseInt(((Code) dhcp.getObject()).getCodeValue()));
			
			ControlKeyValue loop = (ControlKeyValue) this.loopJComboBox.getSelectedItem();
			info.setLoopCheck(Integer.parseInt(((Code) loop.getObject()).getCodeValue()));
			
			ControlKeyValue ssm = (ControlKeyValue) this.ssmJComboBox.getSelectedItem();
			info.setSsmModel(Integer.parseInt(((Code) ssm.getObject()).getCodeValue()));
			
			ControlKeyValue twoLayer = (ControlKeyValue) this.jcmbtwoLayer.getSelectedItem();
			info.setTwoLayer(Integer.parseInt(((Code) twoLayer.getObject()).getCodeValue()));
			
			controlKeyValue = (ControlKeyValue) this.alarmModelCbox.getSelectedItem();
			info.setAlarmModel(Integer.parseInt(((Code) controlKeyValue.getObject()).getCodeValue()));
			controlKeyValue = (ControlKeyValue) this.alarmPortCbox.getSelectedItem();
			info.setAlarmPort(Integer.parseInt(controlKeyValue.getId()));
			info.setAlarmPortNameLog(controlKeyValue.getName());
			
			ControlKeyValue loopAvoid = (ControlKeyValue) this.loopAvoidCbox.getSelectedItem();
			info.setLoopAvoid(Integer.parseInt(((Code) loopAvoid.getObject()).getCodeValue()));
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return info;
	}
	
	public boolean check(){
		if(!CheckingUtil.checking(singleMACAddressFiled.getText(), CheckingUtil.MAC_REGULAR)){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_MACERROR));
			return false;
		}
		if(!checkInt(macAddressAgeDateFiled,10,65535)){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_ADDRESS_TIME)+ResourceUtil.srcStr(StringKeysTip.TIP_SCOPE)+":10-65535");
			return false;
		}
		
		if(!checkInt(throwWrapDateGapField,1,255)){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_PACKET_LOSS_INTERVAL)+ResourceUtil.srcStr(StringKeysTip.TIP_SCOPE)+":1-255");
			return false;
		}
		if(!checkInt(channelTypeField,0,65535)){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_CHANNEL_TYPE)+ResourceUtil.srcStr(StringKeysTip.TIP_SCOPE)+":0-65535");
			return false;
		}
		if(!checkInt(fdiB1IT3Field,0,7)){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_FDI_MEL)+ResourceUtil.srcStr(StringKeysTip.TIP_SCOPE)+":0-7");
			return false;
		}
		if(!checkInt(vlanField,1,4094)){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_VLAN_VALUE)+ResourceUtil.srcStr(StringKeysTip.TIP_SCOPE)+":1-4094");
			return false;
		}
		if(!checkInt(tmcfdiB1IT3Field,0,7)){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_TMC_FDI_MEL)+ResourceUtil.srcStr(StringKeysTip.TIP_SCOPE)+":0-7");
			return false;
		}
		if(!checkInt(macNumberField,0,32762)){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_MAC_NUMBER)+ResourceUtil.srcStr(StringKeysTip.TIP_SCOPE)+":0-32762");
			return false;
		}
		if(!checkInt(priorityJTextField,0,65535)){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.EQUIPMENT_PRIORITY)+ResourceUtil.srcStr(StringKeysLbl.LBL_MAC_NUMBER)+ResourceUtil.srcStr(StringKeysTip.TIP_SCOPE)+":0-65535");
			return false;
		}
		return true;
	}
	
	private boolean checkInt(JTextField jTextField,int min,int max){
		try{
			if (CheckingUtil.checking(jTextField.getText(), CheckingUtil.NUMBER_REGULAR)){
				if(Integer.parseInt(jTextField.getText())>=min && Integer.parseInt(jTextField.getText())<=max){
					return true;
				}
			}
		}catch(Exception e)
		{
			ExceptionManage.dispose(e, this.getClass());
			return false;
		}
		return false;
	}

	public JLabel getSingleMACAddressLabel() {
		return singleMACAddressLabel;
	}

	public void setSingleMACAddressLabel(JLabel singleMACAddressLabel) {
		this.singleMACAddressLabel = singleMACAddressLabel;
	}

	public JTextField getSingleMACAddressFiled() {
		return singleMACAddressFiled;
	}

	public void setSingleMACAddressFiled(JTextField singleMACAddressFiled) {
		this.singleMACAddressFiled = singleMACAddressFiled;
	}

	public JLabel getAddressAgeSwitchLabel() {
		return addressAgeSwitchLabel;
	}

	public void setAddressAgeSwitchLabel(JLabel addressAgeSwitchLabel) {
		this.addressAgeSwitchLabel = addressAgeSwitchLabel;
	}

	public JLabel getMacAddressAgeDateLabel() {
		return macAddressAgeDateLabel;
	}

	public void setMacAddressAgeDateLabel(JLabel macAddressAgeDateLabel) {
		this.macAddressAgeDateLabel = macAddressAgeDateLabel;
	}

	public JTextField getMacAddressAgeDateFiled() {
		return macAddressAgeDateFiled;
	}

	public void setMacAddressAgeDateFiled(JTextField macAddressAgeDateFiled) {
		this.macAddressAgeDateFiled = macAddressAgeDateFiled;
	}

	public JLabel getThrowWrapDateGapLabel() {
		return throwWrapDateGapLabel;
	}

	public void setThrowWrapDateGapLabel(JLabel throwWrapDateGapLabel) {
		this.throwWrapDateGapLabel = throwWrapDateGapLabel;
	}

	public JTextField getThrowWrapDateGapField() {
		return throwWrapDateGapField;
	}

	public void setThrowWrapDateGapField(JTextField throwWrapDateGapField) {
		this.throwWrapDateGapField = throwWrapDateGapField;
	}

	public JLabel getFdiBIT0Label() {
		return fdiBIT0Label;
	}

	public void setFdiBIT0Label(JLabel fdiBIT0Label) {
		this.fdiBIT0Label = fdiBIT0Label;
	}

	public JLabel getFdiB1IT3Label() {
		return fdiB1IT3Label;
	}

	public void setFdiB1IT3Label(JLabel fdiB1IT3Label) {
		this.fdiB1IT3Label = fdiB1IT3Label;
	}

	public JTextField getFdiB1IT3Field() {
		return fdiB1IT3Field;
	}

	public void setFdiB1IT3Field(JTextField fdiB1IT3Field) {
		this.fdiB1IT3Field = fdiB1IT3Field;
	}

	public JLabel getApsRecoverTimeLabel() {
		return apsRecoverTimeLabel;
	}

	public void setApsRecoverTimeLabel(JLabel apsRecoverTimeLabel) {
		this.apsRecoverTimeLabel = apsRecoverTimeLabel;
	}

	public PtnSpinner getApsRecoverTimeField() {
		return apsRecoverTimeField;
	}

	public void setApsRecoverTimeField(PtnSpinner apsRecoverTimeField) {
		this.apsRecoverTimeField = apsRecoverTimeField;
	}

	public JLabel getCrcVerifyLabel() {
		return crcVerifyLabel;
	}

	public void setCrcVerifyLabel(JLabel crcVerifyLabel) {
		this.crcVerifyLabel = crcVerifyLabel;
	}

	public JTextField getCrcVerifyfField() {
		return crcVerifyfField;
	}

	public void setCrcVerifyfField(JTextField crcVerifyfField) {
		this.crcVerifyfField = crcVerifyfField;
	}

	public JLabel getThrowWrapLabel() {
		return throwWrapLabel;
	}

	public void setThrowWrapLabel(JLabel throwWrapLabel) {
		this.throwWrapLabel = throwWrapLabel;
	}

	public JTextField getThrowWrapField() {
		return throwWrapField;
	}

	public void setThrowWrapField(JTextField throwWrapField) {
		this.throwWrapField = throwWrapField;
	}

	public JLabel getReceiveBadWrapLabel() {
		return receiveBadWrapLabel;
	}

	public void setReceiveBadWrapLabel(JLabel receiveBadWrapLabel) {
		this.receiveBadWrapLabel = receiveBadWrapLabel;
	}

	public JTextField getReceiveBadWrapField() {
		return receiveBadWrapField;
	}

	public void setReceiveBadWrapField(JTextField receiveBadWrapField) {
		this.receiveBadWrapField = receiveBadWrapField;
	}

	public JLabel getTmsWorsenLabel() {
		return tmsWorsenLabel;
	}

	public void setTmsWorsenLabel(JLabel tmsWorsenLabel) {
		this.tmsWorsenLabel = tmsWorsenLabel;
	}

	public JTextField getTmsWorsenField() {
		return tmsWorsenField;
	}

	public void setTmsWorsenField(JTextField tmsWorsenField) {
		this.tmsWorsenField = tmsWorsenField;
	}

	public JLabel getTmsLoseLabel() {
		return tmsLoseLabel;
	}

	public void setTmsLoseLabel(JLabel tmsLoseLabel) {
		this.tmsLoseLabel = tmsLoseLabel;
	}

	public JTextField getTmsLoseField() {
		return tmsLoseField;
	}

	public void setTmsLoseField(JTextField tmsLoseField) {
		this.tmsLoseField = tmsLoseField;
	}

	public JLabel getAlignLabel() {
		return alignLabel;
	}

	public void setAlignLabel(JLabel alignLabel) {
		this.alignLabel = alignLabel;
	}

	public JTextField getAlignField() {
		return alignField;
	}

	public void setAlignField(JTextField alignField) {
		this.alignField = alignField;
	}

	public JLabel getMirrorModelLabel() {
		return mirrorModelLabel;
	}

	public void setMirrorModelLabel(JLabel mirrorModelLabel) {
		this.mirrorModelLabel = mirrorModelLabel;
	}

	public JLabel getMirrorByPortLabel() {
		return mirrorByPortLabel;
	}

	public void setMirrorByPortLabel(JLabel mirrorByPortLabel) {
		this.mirrorByPortLabel = mirrorByPortLabel;
	}

	public JLabel getMirrorPortLabel() {
		return mirrorPortLabel;
	}

	public void setMirrorPortLabel(JLabel mirrorPortLabel) {
		this.mirrorPortLabel = mirrorPortLabel;
	}

	public JLabel getMplsTPControlLabel() {
		return mplsTPControlLabel;
	}

	public void setMplsTPControlLabel(JLabel mplsTPControlLabel) {
		this.mplsTPControlLabel = mplsTPControlLabel;
	}

	public JLabel getChannelTypeLabel() {
		return channelTypeLabel;
	}

	public void setChannelTypeLabel(JLabel channelTypeLabel) {
		this.channelTypeLabel = channelTypeLabel;
	}

	public JTextField getChannelTypeField() {
		return channelTypeField;
	}

	public void setChannelTypeField(JTextField channelTypeField) {
		this.channelTypeField = channelTypeField;
	}

	public PtnButton getOkButton() {
		return okButton;
	}
	
	public PtnButton getSynchroButton() {
		return syncButton;
	}

	public void setOkButton(PtnButton okButton) {
		this.okButton = okButton;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public JComboBox getAddressAgeSwitchBox() {
		return addressAgeSwitchBox;
	}

	public void setAddressAgeSwitchBox(JComboBox addressAgeSwitchBox) {
		this.addressAgeSwitchBox = addressAgeSwitchBox;
	}

	public JComboBox getFdiBIT0Box() {
		return fdiBIT0Box;
	}

	public void setFdiBIT0Box(JComboBox fdiBIT0Box) {
		this.fdiBIT0Box = fdiBIT0Box;
	}

	public JComboBox getMirrorModelCheckBox() {
		return mirrorModelCheckBox;
	}

	public void setMirrorModelCheckBox(JComboBox mirrorModelCheckBox) {
		this.mirrorModelCheckBox = mirrorModelCheckBox;
	}

	public JComboBox getMirrorByPortBox() {
		return mirrorByPortBox;
	}

	public void setMirrorByPortBox(JComboBox mirrorByPortBox) {
		this.mirrorByPortBox = mirrorByPortBox;
	}

	public JComboBox getMirrorportBox() {
		return mirrorportBox;
	}

	public void setMirrorportBox(JComboBox mirrorportBox) {
		this.mirrorportBox = mirrorportBox;
	}

	public JComboBox getMplsTPControlBox() {
		return mplsTPControlBox;
	}

	public void setMplsTPControlBox(JComboBox mplsTPControlBox) {
		this.mplsTPControlBox = mplsTPControlBox;
	}

	public AllConfigController getAllConfigController() {
		return allConfigController;
	}

	public void setAllConfigController(AllConfigController allConfigController) {
		this.allConfigController = allConfigController;
	}

	public AllConfigInfo getInfo() {
		return info;
	}

	public void setInfo(AllConfigInfo info) {
		this.info = info;
	}

	public JLabel getTmcfdiBIT0Label() {
		return tmcfdiBIT0Label;
	}

	public void setTmcfdiBIT0Label(JLabel tmcfdiBIT0Label) {
		this.tmcfdiBIT0Label = tmcfdiBIT0Label;
	}

	public JComboBox getTmcfdiBIT0Box() {
		return tmcfdiBIT0Box;
	}

	public void setTmcfdiBIT0Box(JComboBox tmcfdiBIT0Box) {
		this.tmcfdiBIT0Box = tmcfdiBIT0Box;
	}

	public JLabel getTmcfdiB1IT3Label() {
		return tmcfdiB1IT3Label;
	}

	public void setTmcfdiB1IT3Label(JLabel tmcfdiB1IT3Label) {
		this.tmcfdiB1IT3Label = tmcfdiB1IT3Label;
	}

	public JTextField getTmcfdiB1IT3Field() {
		return tmcfdiB1IT3Field;
	}

	public void setTmcfdiB1IT3Field(JTextField tmcfdiB1IT3Field) {
		this.tmcfdiB1IT3Field = tmcfdiB1IT3Field;
	}

	public JLabel getLoop() {
		return loop;
	}

	public void setLoop(JLabel loop) {
		this.loop = loop;
	}

	public JComboBox getLoopJComboBox() {
		return loopJComboBox;
	}

	public void setLoopJComboBox(JComboBox loopJComboBox) {
		this.loopJComboBox = loopJComboBox;
	}

	public JLabel getSsmJLabel() {
		return ssmJLabel;
	}

	public void setSsmJLabel(JLabel ssmJLabel) {
		this.ssmJLabel = ssmJLabel;
	}

	public JComboBox getSsmJComboBox() {
		return ssmJComboBox;
	}

	public void setSsmJComboBox(JComboBox ssmJComboBox) {
		this.ssmJComboBox = ssmJComboBox;
	}
}
