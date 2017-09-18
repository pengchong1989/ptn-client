﻿package com.nms.ui.ptn.oam.Node.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.oam.OamEthernetInfo;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EOperationLogType;
import com.nms.drive.service.impl.CoderUtils;
import com.nms.model.equipment.port.PortService_MB;
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
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.oam.Node.controller.OamNodeController;

public class EthernetOamNodeDialog extends PtnDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1843689992328309865L;
	private OamNodeController controller;
	private JLabel lblMessage;
	private JLabel thernetOAMEnableLabel;// 以太网OAM 使能
	private JComboBox thernetOAMEnableComboBox;
	private JLabel mdMLevelLabel;// 默认MD LEVEL
	private PtnTextField mdMLevelLabelTextField;
	private JLabel mpLable;// MP属性：
	private JComboBox mpLableComboBox;
	private JLabel mdNameLabel;// MD Name
	private JTextField mdNameLabelTextField;
	private JLabel mdLevelLabel;// MD Level
	private PtnTextField mdLevelLabelTextField;
	private JLabel maName;// MA Name
	private PtnTextField maNameTextfield;
	private JLabel ccmsendLabel;// CCM发送间隔
	private JComboBox ccmsendLabelComboBox;
	private JLabel vlanLable;// VLAN
	private PtnTextField vlanLableTextField;
	private JLabel mepIDLable;// MEP ID
	private PtnTextField mepIDLableTextField;
	private JLabel mepTypeLabel;// MEP类型
	private JComboBox mepTypeLabelComboBox;
	private JLabel portLabel;// 端口号
	private JComboBox portLabelComboBox;
	private JLabel ccmSendEnable;// CCM发送使能
	private JComboBox ccmSendEnableComboBox;
	private JLabel ccmReceiveEnable;// CCM接受使能
	private JComboBox ccmReceiveEnableComboBox;
	private JLabel ccmPriorityLabel;// CCM优先级
	private PtnTextField ccmPriorityLabelTextField;
	private JLabel lbmTlvTypeLable;// LBM数据TLV类型
	private JComboBox lbmTlvTypeLableComboBox;
	private JLabel lbmTlvTypeLengthLabel;// LBM数据TLV长度
	private PtnTextField lbmTlvTypeLengthLabelTextField;
	private JLabel lbmPriorityLabel;// LBM优先级
	private PtnTextField lbmPriorityLabelTextField;
	private JLabel lbmDiscardLable;// LBM丢弃适用性
	private JComboBox lbmDiscardLableComboBox;
	private JLabel ltmPriorityLabel;// LTM优先级
	private PtnTextField ltmPriorityLabelTextField;
	private JLabel aisSendEnabelLable;// AIS发送使能
	private JComboBox aisSendEnabelLableComboBox;
	private JLabel clientMdLevelLabel;// 客户MD Level
	private PtnTextField clientMdLevelLabelTextField;
	private JLabel aisPriorityLabel;// AIS优先级
	private PtnTextField aisPriorityLabelTextField;
	private JLabel lckSendEnabelLable;// LCK发送使能
	private JComboBox lckSendEnabelLableComboBox;
	private JLabel lckPriorityLabel;// LCK优先级
	private PtnTextField lckPriorityLabelTextField;
	private JLabel aisLckSendCycleLable;// AIS/LCK发送周期
	private JComboBox aisLckSendcycleComboBox;
	private JLabel tstSendEnabelLable;// TST发送使能
	private JComboBox tstSendEnabelLableComboBox;
	private JLabel tstSendLevelLabel;// TST发送MDLevel
	private PtnTextField tstSendLevelLabelTextField;
	private JLabel tstPurposeMepMacLabel;// TST目的MEP MAC
	private PtnTextField tstPurposeMepMacLabelTextField;
	private JLabel tstPriorityLabel;// TST优先级
	private PtnTextField tstPriorityLabelTextField;
	private JLabel sendWaylLable;// 发送方式
	private JComboBox sendWaylLableComboBox;
	private JLabel tstDiscardlLable;// TST丢弃适用性
	private JComboBox tstDiscardlLableComboBox;
	private JLabel tstTlvTypelLable;// TST数据TLV类型
	private JComboBox tstTlvTypelLableComboBox;
	private JLabel tstTlvLengthLable;// TST数据TLV长度
	private PtnTextField tstTlvLengthLabletext;
	private JLabel tstSendCycleLable;// TST发送周期
	private JComboBox tstSendCycleLableComboBox;
	private JLabel lmENableLable;// LM使能
	private JComboBox lmENableLableComboBox;
	private JLabel lmPriorityLabel;// LM优先级
	private PtnTextField lmPriorityLabeltext;
	private JLabel lmSendCycleLable;// LM发送周期
	private JComboBox lmSendCycleLableComboBox;
	private JLabel dmENableLable;// DM使能
	private JComboBox dmENableLableComboBox;
	private JLabel dmPriorityLabel;// DM优先级
	private PtnTextField dmPriorityLabeltext;
	private JLabel dmSendCycleLable;// DM发送周期
	private JComboBox dmSendCycleLableComboBox;
	private JLabel remoteMepId1Label;// Remote MEP ID 1：1-8191
	private PtnTextField remoteMepIdLabelTextField;
	private JLabel macAddress1Label;// MAC 1地址
	private PtnTextField macAddress1LabelTextField;
	private JLabel remoteMepId2Label;// Remote MEP ID 2
	private PtnTextField remoteMepId2LabelTextField;
	private JLabel macAddress2Label;// MAC 2地址
	private PtnTextField macAddress2LabelTextField;
	private JLabel remoteMepId3Label;// Remote MEP ID 3
	private PtnTextField remoteMepId3LabelTextField;
	private JLabel macAddress3Label;// MAC 3地址
	private PtnTextField macAddress3LabelTextField;
	private JLabel remoteMepId4Label;// Remote MEP ID 4
	private PtnTextField remoteMepId4LabelTextField;
	private JLabel macAddress4Label;// MAC 4地址
	private PtnTextField macAddress4LabelTextField;
	private JLabel mipCreateLable;// MIP有效性
	private JComboBox mipCreateLableComboBox;
	private JLabel mipPortLable;// MIP端口号
	private JComboBox mipPortLableComboBox;
	private PtnButton confirm;// 保存
	private JButton cancel;// 取消
	private JButton btnBack;//上一步
	private JButton btnNext;//下一步
	private JPanel buttonPanel = null;
	private EthernetOamNodeDialog dialog;
	private OamInfo oamInfo;
	private JCheckBox Remote1Box;
	private JCheckBox Remote2Box;
	private JCheckBox Remote3Box;
	private JCheckBox Remote4Box;
	
	private JPanel remoteJcheck1;
	private JPanel remoteJcheck2;
	private JPanel remoteJcheck3;
	private JPanel remoteJcheck4;
	
	private JPanel panelContent;
	private JPanel mdJpanel;
	private JPanel maJpanel;
	private JPanel mepJpanel;
	
	private JPanel lbmJpanel;
	private JPanel tstJpanel;
	private JPanel lmJpanel;
	private JPanel dmJpanel;
	private JPanel lckJpanel;
	private JPanel aisJpanel;
	private JPanel remoteMEPJpanel;
	private JPanel mipJpanel;
	
	public EthernetOamNodeDialog(OamInfo oamInfo, OamNodeController controller) {
		this.oamInfo = oamInfo;
		this.controller = controller;
		setModal(true);
		initComponent();
		dialog = this;
		// 修改以太网界面赋初始值
		if (oamInfo != null) {
			initData();
		}
	}

	/**
	 * //修改以太网界面赋初始值
	 */
	private void initData() {
		OamEthernetInfo oamEthernetInfo = null;
		try {
			oamEthernetInfo = new OamEthernetInfo();
			oamEthernetInfo = oamInfo.getOamEthernetInfo();
			// 以太网OAM 使能
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.thernetOAMEnableComboBox, String.valueOf(oamEthernetInfo.getThernetOAMEnabl()));
			// 默认MD LEVEL
			mdMLevelLabelTextField.setText(oamEthernetInfo.getMdLevel());
			// MP属性：
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.mpLableComboBox, String.valueOf(oamEthernetInfo.getMpLable()));
			// MD Name
			mdNameLabelTextField.setText(oamEthernetInfo.getMdName());
			// MD Level
			mdLevelLabelTextField.setText(oamEthernetInfo.getMdLevel());
			// MA Name
			maNameTextfield.setText(oamEthernetInfo.getMaName());
			// CCM发送间隔
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.ccmsendLabelComboBox, String.valueOf(oamEthernetInfo.getCcmsend()));
			// VLAN
			vlanLableTextField.setText(oamEthernetInfo.getVlan());
			// MEP ID
			mepIDLableTextField.setText(oamEthernetInfo.getMepId());
			// MEP类型
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.mepTypeLabelComboBox, String.valueOf(oamEthernetInfo.getMepType()));
			// 端口号
			this.comboBoxSelect(this.portLabelComboBox, this.oamInfo.getOamEthernetInfo().getPort() + "");
			// MIP端口号
			this.comboBoxSelect(this.mipPortLableComboBox, this.oamInfo.getOamEthernetInfo().getMipPort() + "");
			// MEP类型
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.ccmSendEnableComboBox, String.valueOf(oamEthernetInfo.getCcmSendEnable()));
			// CCM接受使能
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.ccmReceiveEnableComboBox, String.valueOf(oamEthernetInfo.getCcmReceiveEnable()));
			
			// CCM优先级
			ccmPriorityLabelTextField.setText(oamEthernetInfo.getCcmPriority());
			// LBM数据TLV类型
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.lbmTlvTypeLableComboBox, String.valueOf(oamEthernetInfo.getLbmTlvType()));
			// LBM数据TLV长度
			lbmTlvTypeLengthLabelTextField.setText(oamEthernetInfo.getLbmTlvTypeLength());
			// LBM优先级
			lbmPriorityLabelTextField.setText(oamEthernetInfo.getLbmPriority());
			// LBM丢弃适用性
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.lbmDiscardLableComboBox, String.valueOf(oamEthernetInfo.getLbmDiscard()));
			// LTM优先级
			ltmPriorityLabelTextField.setText(oamEthernetInfo.getLtmPriority());
			// AIS发送使能
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.aisSendEnabelLableComboBox, String.valueOf(oamEthernetInfo.getAisSendEnabel()));
			// 客户MD Level
			clientMdLevelLabelTextField.setText(oamEthernetInfo.getClientMdLevel());
			// AIS优先级
			aisPriorityLabelTextField.setText(oamEthernetInfo.getAisPriority());
			// AIS发送使能
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.lckSendEnabelLableComboBox, String.valueOf(oamEthernetInfo.getLckSendEnabel()));
			// LCK优先级
			lckPriorityLabelTextField.setText(oamEthernetInfo.getLckPriority());
			// AIS/LCK发送周期
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.aisLckSendcycleComboBox, String.valueOf(oamEthernetInfo.getAisLckSendCycle()));
			// TST发送使能
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.tstSendEnabelLableComboBox, String.valueOf(oamEthernetInfo.getTstSendEnabel()));
			// TST发送MDLevel
			tstSendLevelLabelTextField.setText(oamEthernetInfo.getTstSendLevel());
			// TST目的MEP MAC
			tstPurposeMepMacLabelTextField.setText(oamEthernetInfo.getTstPurposeMepMac());
			// TST优先级
			tstPriorityLabelTextField.setText(oamEthernetInfo.getTstPriority());
			// TST发送使能
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.sendWaylLableComboBox, String.valueOf(oamEthernetInfo.getSendWay()));
			// TST丢弃适用性
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.tstDiscardlLableComboBox, String.valueOf(oamEthernetInfo.getTstDiscard()));
			// TST数据TLV类型
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.tstTlvTypelLableComboBox, String.valueOf(oamEthernetInfo.getTstTlvType()));
			// TST数据TLV长度
			tstTlvLengthLabletext.setText(oamEthernetInfo.getTstTlvLength());
			// TST发送周期
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.tstSendCycleLableComboBox, String.valueOf(oamEthernetInfo.getTstSendCycle()));
			// LM使能
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.lmENableLableComboBox, String.valueOf(oamEthernetInfo.getLmENable()));
			// LM优先级
			lmPriorityLabeltext.setText(oamEthernetInfo.getLmPriority());
			// LM发送周期
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.lmSendCycleLableComboBox, String.valueOf(oamEthernetInfo.getLmSendCycle()));
			// DM使能
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.dmENableLableComboBox, String.valueOf(oamEthernetInfo.getDmENable()));
			// DM优先级
			dmPriorityLabeltext.setText(oamEthernetInfo.getDmPriority());
			// DM发送周期
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.dmSendCycleLableComboBox, String.valueOf(oamEthernetInfo.getDmSendCycle()));
			if(oamEthernetInfo.getRemoteMepId1().trim().equals("0")){
				Remote1Box.setSelected(false);
				remoteMepIdLabelTextField.setEnabled(false);
				macAddress1LabelTextField.setEnabled(false);
			}else{
				Remote1Box.setSelected(true);
				remoteMepIdLabelTextField.setEnabled(true);
				macAddress1LabelTextField.setEnabled(true);
			}
			
			if(oamEthernetInfo.getRemoteMepId2().trim().equals("0")){
				Remote2Box.setSelected(false);
				remoteMepId2LabelTextField.setEnabled(false);
				macAddress2LabelTextField.setEnabled(false);
			}else{
				Remote2Box.setSelected(true);
				remoteMepId2LabelTextField.setEnabled(true);
				macAddress2LabelTextField.setEnabled(true);
			}
			
			if(oamEthernetInfo.getRemoteMepId3().trim().equals("0")){
				Remote3Box.setSelected(false);
				remoteMepId3LabelTextField.setEnabled(false);
				macAddress3LabelTextField.setEnabled(false);
			}else{
				Remote3Box.setSelected(true);
				remoteMepId3LabelTextField.setEnabled(true);
				macAddress3LabelTextField.setEnabled(true);
			}
			
			if(oamEthernetInfo.getRemoteMepId4().trim().equals("0")){
				Remote4Box.setSelected(false);
				remoteMepId4LabelTextField.setEnabled(false);
				macAddress4LabelTextField.setEnabled(false);
			}else{
				Remote4Box.setSelected(true);
				remoteMepId4LabelTextField.setEnabled(true);
				macAddress4LabelTextField.setEnabled(true);
			}
			// Remote MEP ID1
			remoteMepIdLabelTextField.setText(oamEthernetInfo.getRemoteMepId1());
			// MAC 1地址
			macAddress1LabelTextField.setText(oamEthernetInfo.getMacAddress1());
			// Remote MEP ID2
			remoteMepId2LabelTextField.setText(oamEthernetInfo.getRemoteMepId2());
			// MAC 2地址
			macAddress2LabelTextField.setText(oamEthernetInfo.getMacAddress2());
			// Remote MEP ID3
			remoteMepId3LabelTextField.setText(oamEthernetInfo.getRemoteMepId3());
			// MAC 3地址
			macAddress3LabelTextField.setText(oamEthernetInfo.getMacAddress3());
			// Remote MEP ID4
			remoteMepId4LabelTextField.setText(oamEthernetInfo.getRemoteMepId4());
			// MAC 4地址
			macAddress4LabelTextField.setText(oamEthernetInfo.getMacAddress4());
			// MIP生成规则
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.mipCreateLableComboBox, String.valueOf(oamEthernetInfo.getMipCreate()));
			isValue();

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			oamEthernetInfo = null;
		}
	}

	private void initComponent() {
		try {
			panelContent=new JPanel();
			confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),true);
			confirm.setVisible(false);
			cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
			this.btnBack = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_BACK));
			this.btnBack.setVisible(false);
			this.btnNext = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_NEXT));
			lblMessage = new JLabel();
			thernetOAMEnableLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAMENABEL));// 以太网OAM 使能
			thernetOAMEnableComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(thernetOAMEnableComboBox, "ENABLEDSTATUE");
			mdMLevelLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MDMLEVEL));// 默认MD LEVEL
			mdMLevelLabelTextField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);
			setValidate(mdMLevelLabelTextField,ConstantUtil.LABELmdMLevel_MAXVALUE,ConstantUtil.LABELWaitTime_MINVALUE);
			mdMLevelLabelTextField.setText("0"); 
			mpLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LMP));// MP属性： 
			mpLableComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(mpLableComboBox, "MPattribute");
			mdNameLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MDNAME));// MD Name
			mdNameLabelTextField = new PtnTextField(false,PtnTextField.TYPE_STRING_NUMBER,PtnTextField.STRING_NUMBER_MAXLENGTH,this.lblMessage, this.confirm, this);
			mdLevelLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MDLEVEL));// MD Level
			mdLevelLabelTextField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);
			setValidate(mdLevelLabelTextField,ConstantUtil.LABELmdMLevel_MAXVALUE,ConstantUtil.LABELWaitTime_MINVALUE);
			mdLevelLabelTextField.setText("0"); 
			maName = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MANAME));// MA Name
			maNameTextfield = new PtnTextField(false,PtnTextField.TYPE_STRING_NUMBER,PtnTextField.STRING_NUMBER_MAXLENGTH,this.lblMessage, this.confirm, this);
			ccmsendLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CCMSEND));// CCM发送间隔
			ccmsendLabelComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(ccmsendLabelComboBox, "ccmsend");
			vlanLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_VLAN));// VLAN
			vlanLableTextField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);
			setValidate(vlanLableTextField,ConstantUtil.LABOAMETNVLAN_MAXVALUE,ConstantUtil.LABOAMETNVLAN_MINVALUE);
			vlanLableTextField.setText("1");
			mepIDLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MEPIP));// MEP ID
			mepIDLableTextField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);
			setValidate(mepIDLableTextField,ConstantUtil.LABOAMETNMEPID_MAXVALUE,ConstantUtil.LABOAMETNVLAN_MINVALUE);
			mepIDLableTextField.setText("1");
			mepTypeLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MPTYPE));// MEP类型
			mepTypeLabelComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(mepTypeLabelComboBox, "MEPTYPE");
			portLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_NUM));// 端口号
			portLabelComboBox = new JComboBox();
			mipPortLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MIPPORT));// MIP端口号
			mipPortLableComboBox = new JComboBox();
			comboBoxData(portLabelComboBox, 1);
			comboBoxData(mipPortLableComboBox, 0);
			ccmSendEnable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CCM));// CCM发送使能
			ccmSendEnableComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(ccmSendEnableComboBox, "ENABLEDSTATUE");
			ccmReceiveEnable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CCMRECEIVEL));// CCM接受使能
			ccmReceiveEnableComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(ccmReceiveEnableComboBox, "ENABLEDSTATUE");
			ccmPriorityLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CCMPRIORITY));// CCM优先级
			ccmPriorityLabelTextField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);
			setValidate(ccmPriorityLabelTextField,ConstantUtil.LABELmdMLevel_MAXVALUE,ConstantUtil.LABELWaitTime_MINVALUE);
			ccmPriorityLabelTextField.setText("0");
			lbmTlvTypeLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LBMTLVTYPE));// LBM数据TLV类型
			lbmTlvTypeLableComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(lbmTlvTypeLableComboBox, "LBMDATATLVTYPE");
			lbmTlvTypeLengthLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LBMTLVTYPELENGTH));// LBM数据TLV长度
			lbmTlvTypeLengthLabelTextField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);
			setValidate(lbmTlvTypeLengthLabelTextField,ConstantUtil.LABOAMETHLBMTVL_MAXVALUE,ConstantUtil.LABOAMETHLBMTVL_MINVALUE);
			lbmTlvTypeLengthLabelTextField.setText("41");
			lbmPriorityLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LBMPRIORITY));// LBM优先级
			lbmPriorityLabelTextField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);
			setValidate(lbmPriorityLabelTextField,ConstantUtil.LABELmdMLevel_MAXVALUE,ConstantUtil.LABELWaitTime_MINVALUE);
			lbmPriorityLabelTextField.setText("0");
			lbmDiscardLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LBMDISCARD));// LBM丢弃适用性
			lbmDiscardLableComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(lbmDiscardLableComboBox, "LBMDISCARD");
			ltmPriorityLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LTMPRIORITY));// LTM优先级
			ltmPriorityLabelTextField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);
			setValidate(ltmPriorityLabelTextField,ConstantUtil.LABELmdMLevel_MAXVALUE,ConstantUtil.LABELWaitTime_MINVALUE);
			ltmPriorityLabelTextField.setText("0");
			aisSendEnabelLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_AISSENDENABEL));// AIS发送使能
			aisSendEnabelLableComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(aisSendEnabelLableComboBox, "ENABLEDSTATUE");
			clientMdLevelLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CLIENTMDLEVEL));// 客户MD Level
			clientMdLevelLabelTextField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);
			setValidate(clientMdLevelLabelTextField,ConstantUtil.LABELmdMLevel_MAXVALUE,ConstantUtil.LABELWaitTime_MINVALUE);
			clientMdLevelLabelTextField.setText("0");
			aisPriorityLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_AISPRIORITY));// AIS优先级
			aisPriorityLabelTextField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);
			setValidate(aisPriorityLabelTextField,ConstantUtil.LABELmdMLevel_MAXVALUE,ConstantUtil.LABELWaitTime_MINVALUE);
			aisPriorityLabelTextField.setText("0");
			lckSendEnabelLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LCKSENDENABEL));// LCK发送使能
			lckSendEnabelLableComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(lckSendEnabelLableComboBox, "ENABLEDSTATUE");
			lckPriorityLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LCKPRIORITY));// LCK优先级
			lckPriorityLabelTextField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);
			setValidate(lckPriorityLabelTextField,ConstantUtil.LABELmdMLevel_MAXVALUE,ConstantUtil.LABELWaitTime_MINVALUE);
			lckPriorityLabelTextField.setText("0");
			aisLckSendCycleLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_AISLCKSENDCYCLE));// AIS/LCK发送周期
			aisLckSendcycleComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(aisLckSendcycleComboBox, "AISLCKSENDCYCLE");
			tstSendEnabelLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_TST_SEND_ENABLED));// TST发送使能
			tstSendEnabelLableComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(tstSendEnabelLableComboBox, "ENABLEDSTATUE");
			tstSendLevelLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TSTSENDLEVEL));// TST发送MDLevel
			tstSendLevelLabelTextField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);
			setValidate(tstSendLevelLabelTextField,ConstantUtil.LABELmdMLevel_MAXVALUE,ConstantUtil.LABELWaitTime_MINVALUE);
			tstSendLevelLabelTextField.setText("0");
			tstPurposeMepMacLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TSTPURPOSEMEPMAC));// TST目的MEP MAC
			tstPurposeMepMacLabelTextField = new PtnTextField(true,PtnTextField.TYPE_MAC,PtnTextField.MAC_MAXLENGTH,this.lblMessage, this.confirm, this);
			tstPurposeMepMacLabelTextField.setText("00-00-00-00-00-01");
			tstPriorityLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TSTPRIORITY));// TST优先级
			tstPriorityLabelTextField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);
			setValidate(tstPriorityLabelTextField,ConstantUtil.LABELmdMLevel_MAXVALUE,ConstantUtil.LABELWaitTime_MINVALUE);
			tstPriorityLabelTextField.setText("0");
			sendWaylLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SENDWAY));// 发送方式
			sendWaylLableComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(sendWaylLableComboBox, "SENDWAY");
			tstDiscardlLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TSTDISCARDL));// TST丢弃适用性
			tstDiscardlLableComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(tstDiscardlLableComboBox, "LBMDISCARD");
			tstTlvTypelLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TSTTLVTYPE));// TST数据TLV类型
			tstTlvTypelLableComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(tstTlvTypelLableComboBox, "LBMDATATLVTYPE");
			tstTlvLengthLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TSTTLVLENGTH));// TST数据TLV长度
			tstTlvLengthLabletext = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);
			setValidate(tstTlvLengthLabletext,ConstantUtil.LABOAMETHTSTTLVLENGTH_MAXVALUE,ConstantUtil.LABOAMETHTSTTLVLENGTH_MINVALUE);
			tstTlvLengthLabletext.setText("45");
			tstSendCycleLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TSTSENDCYCEL));// TST发送周期
			tstSendCycleLableComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(tstSendCycleLableComboBox, "ccmsend");
			lmENableLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LMENABEL));// LM使能
			lmENableLableComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(lmENableLableComboBox, "ENABLEDSTATUE");
			lmPriorityLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LMPRIORITY));// LM优先级
			lmPriorityLabeltext = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);
			setValidate(lmPriorityLabeltext,ConstantUtil.LABELmdMLevel_MAXVALUE,ConstantUtil.LABELWaitTime_MINVALUE);
			lmPriorityLabeltext.setText("0");
			lmSendCycleLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_LM_SEND_PERIOD));// LM发送周期
			lmSendCycleLableComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(lmSendCycleLableComboBox, "sendcycle");
			dmENableLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DMENABLE));// DM使能
			dmENableLableComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(dmENableLableComboBox, "ENABLEDSTATUE");
			dmPriorityLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DMPRIORITY));// DM优先级
			dmPriorityLabeltext = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);
			setValidate(dmPriorityLabeltext,ConstantUtil.LABELmdMLevel_MAXVALUE,ConstantUtil.LABELWaitTime_MINVALUE);
			dmPriorityLabeltext.setText("0");
			dmSendCycleLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_DM_SEND_PERIOD));// DM发送周期
			dmSendCycleLableComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(dmSendCycleLableComboBox, "sendcycle");
			Remote1Box = new JCheckBox();
			Remote1Box.setSelected(false);
			Remote2Box = new JCheckBox();
			Remote2Box.setSelected(false);
			Remote3Box = new JCheckBox();
			Remote3Box.setSelected(false);
			Remote4Box = new JCheckBox();
			Remote4Box.setSelected(false);
			remoteMepId1Label = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_REMOTEMEPID1));// Remote MEP ID
			// 1：1-8191
			remoteMepIdLabelTextField = new PtnTextField(false,Remote1Box,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);
			setValidate(remoteMepIdLabelTextField,ConstantUtil.LABOAMETNMEPID_MAXVALUE,ConstantUtil.LABOAMETNVLAN_MINVALUE);
			remoteMepIdLabelTextField.setText("0");
			remoteMepIdLabelTextField.setEnabled(false);
			macAddress1Label = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MACADDRESS1));// MAC 1地址
			macAddress1LabelTextField = new PtnTextField(true,PtnTextField.TYPE_MAC,PtnTextField.MAC_MAXLENGTH,this.lblMessage, this.confirm, this);
			macAddress1LabelTextField.setText("00-00-00-00-00-01");
			macAddress1LabelTextField.setEnabled(false);
			remoteMepId2Label = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_REMOTEMEPID2));// Remote MEP ID 2
			remoteMepId2LabelTextField = new PtnTextField(false,Remote2Box,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);
			setValidate(remoteMepId2LabelTextField,ConstantUtil.LABOAMETNMEPID_MAXVALUE,ConstantUtil.LABOAMETNVLAN_MINVALUE);
			remoteMepId2LabelTextField.setText("0");
			remoteMepId2LabelTextField.setEnabled(false);
			macAddress2Label = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MACADDRESS2));// MAC 2地址
			macAddress2LabelTextField = new PtnTextField(true,PtnTextField.TYPE_MAC,PtnTextField.MAC_MAXLENGTH,this.lblMessage, this.confirm, this);
			macAddress2LabelTextField.setText("00-00-00-00-00-01");
			macAddress2LabelTextField.setEnabled(false);
			remoteMepId3Label = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_REMOTEMEPID3));// Remote MEP ID 3
			remoteMepId3LabelTextField = new PtnTextField(false,Remote3Box,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);
			setValidate(remoteMepId3LabelTextField,ConstantUtil.LABOAMETNMEPID_MAXVALUE,ConstantUtil.LABOAMETNVLAN_MINVALUE);
			remoteMepId3LabelTextField.setText("0");
			remoteMepId3LabelTextField.setEnabled(false);
			macAddress3Label = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MACADDRESS3));// MAC 3地址
			macAddress3LabelTextField = new PtnTextField(true,PtnTextField.TYPE_MAC,PtnTextField.MAC_MAXLENGTH,this.lblMessage, this.confirm, this);
			macAddress3LabelTextField.setText("00-00-00-00-00-01");
			macAddress3LabelTextField.setEnabled(false);
			remoteMepId4Label = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_REMOTEMEPID4));// Remote MEP ID 4
			remoteMepId4LabelTextField = new PtnTextField(false,Remote4Box,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage, this.confirm, this);
			setValidate(remoteMepId4LabelTextField,ConstantUtil.LABOAMETNMEPID_MAXVALUE,ConstantUtil.LABOAMETNVLAN_MINVALUE);
			remoteMepId4LabelTextField.setText("0");
			remoteMepId4LabelTextField.setEnabled(false);
			macAddress4Label = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MACADDRESS4));// MAC 4地址
			macAddress4LabelTextField = new PtnTextField(true,PtnTextField.TYPE_MAC,PtnTextField.MAC_MAXLENGTH,this.lblMessage, this.confirm, this);
			macAddress4LabelTextField.setText("00-00-00-00-00-01");
			macAddress4LabelTextField.setEnabled(false);
			mipCreateLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MIPCREATE));// MIP生成规则
			mipCreateLableComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(mipCreateLableComboBox, "MIPCREATE");
			remoteJcheck1 = new JPanel();
			remoteJcheck2 = new JPanel();
			remoteJcheck3 = new JPanel();
			remoteJcheck4 = new JPanel();
			setRomote1Layout(Remote1Box,remoteMepId1Label,remoteJcheck1);
			setRomote1Layout(Remote2Box,remoteMepId2Label,remoteJcheck2);
			setRomote1Layout(Remote3Box,remoteMepId3Label,remoteJcheck3);
			setRomote1Layout(Remote4Box,remoteMepId4Label,remoteJcheck4);
			remoteJcheck1.add(Remote1Box);
			remoteJcheck1.add(remoteMepId1Label);
			remoteJcheck2.add(Remote2Box);
			remoteJcheck2.add(remoteMepId2Label);
			remoteJcheck3.add(Remote3Box);
			remoteJcheck3.add(remoteMepId3Label);
			remoteJcheck4.add(remoteMepId4Label);
			remoteJcheck4.add(Remote4Box);
			buttonPanel=new JPanel();
			
			mdJpanel=new JPanel();
			this.mdJpanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.MD_LABEL)));
			setMdJpanelComponentLayout();
			maJpanel=new JPanel();
			this.maJpanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.MA_LABEL)));
			setMaJpanelComponentLayout();
			mepJpanel=new JPanel();  
			this.mepJpanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.MEP_LABEL)));
			setMepJpanelComponentLayout();
			lbmJpanel=new JPanel();
			this.lbmJpanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.LBM_LABEL)));
			setLbmJpanelComponentLayout();
			lmJpanel=new JPanel();
			this.lmJpanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.LM_LABEL)));
			lmJpanel.setVisible(false);
			setLmJpanelComponentLayout();
			aisJpanel=new JPanel();
			
			this.aisJpanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.AIS_LABEL)));
			setAisJpanelComponentLayout();
			aisJpanel.setVisible(false);
			
			lckJpanel=new JPanel(); 
			this.lckJpanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.LCK_LABEL)));
			setLckJpanelComponentLayout();
			
			tstJpanel=new JPanel();  
			this.tstJpanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.TST_LABEL)));
			tstJpanel.setVisible(false);
			setTstJpanelComponentLayout();
			
			dmJpanel=new JPanel();  
			this.dmJpanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.DM_LABEL)));
			dmJpanel.setVisible(false);
			setDmJpanelComponentLayout();
			remoteMEPJpanel=new JPanel();  
			
			this.remoteMEPJpanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.REMOT_MEP_LABEL)));	
			setRemoteMEPJpanelComponentLayout();
			remoteMEPJpanel.setVisible(false);
			mipJpanel=new JPanel();
			this.mipJpanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.MIP_LABEL)));
			mipJpanel.setVisible(false);
			setMipJpanelComponentLayout();
			this.buttonPanel.setPreferredSize(new Dimension(600, 40));
			setButtonLayout();/* 按钮所在panel布局 */
			this.setLayout(new BorderLayout());
			
			this.panelContent.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_ETHOAM_FIRST)));
			mdJpanel.add(thernetOAMEnableLabel);
			mdJpanel.add(thernetOAMEnableComboBox);
			mdJpanel.add(mdMLevelLabel);
			mdJpanel.add(mdMLevelLabelTextField);
			mdJpanel.add(mdNameLabel);
			mdJpanel.add(mdNameLabelTextField);
			mdJpanel.add(mdLevelLabel);
			mdJpanel.add(mdLevelLabelTextField);
			mdJpanel.add(mpLable);
			mdJpanel.add(mpLableComboBox);	
			
			maJpanel.add(maName);
			maJpanel.add(maNameTextfield);
			maJpanel.add(vlanLable);
			maJpanel.add(vlanLableTextField);
			
			mepJpanel.add(mepIDLable);
			mepJpanel.add(mepIDLableTextField);
			mepJpanel.add(mepTypeLabel);
			mepJpanel.add(mepTypeLabelComboBox);
			mepJpanel.add(portLabel);
			mepJpanel.add(portLabelComboBox);
			mepJpanel.add(ccmSendEnable);
			mepJpanel.add(ccmSendEnableComboBox);
			mepJpanel.add(ccmReceiveEnable);
			mepJpanel.add(ccmReceiveEnableComboBox);
			mepJpanel.add(ccmPriorityLabel);
			mepJpanel.add(ccmPriorityLabelTextField);
			mepJpanel.add(ccmsendLabel);
			mepJpanel.add(ccmsendLabelComboBox);

			lbmJpanel.add(lbmTlvTypeLable);
			lbmJpanel.add(lbmTlvTypeLableComboBox);
			lbmJpanel.add(lbmTlvTypeLengthLabel);
			lbmJpanel.add(lbmTlvTypeLengthLabelTextField);
			lbmJpanel.add(lbmPriorityLabel);
			lbmJpanel.add(lbmPriorityLabelTextField);
			lbmJpanel.add(lbmDiscardLable);
			lbmJpanel.add(lbmDiscardLableComboBox);
			lbmJpanel.add(ltmPriorityLabel);
			lbmJpanel.add(ltmPriorityLabelTextField);
			
			aisJpanel.add(aisSendEnabelLable);
			aisJpanel.add(aisSendEnabelLableComboBox);
			aisJpanel.add(aisPriorityLabel);
			aisJpanel.add(aisPriorityLabelTextField);
			aisJpanel.add(aisLckSendCycleLable);
			aisJpanel.add(aisLckSendcycleComboBox);
			aisJpanel.add(clientMdLevelLabel);
			aisJpanel.add(clientMdLevelLabelTextField);
			
			lckJpanel.add(lckSendEnabelLable);			
			lckJpanel.add(lckSendEnabelLableComboBox);			
			lckJpanel.add(lckPriorityLabel);			
			lckJpanel.add(lckPriorityLabelTextField);			

			lmJpanel.add(lmENableLable);
			lmJpanel.add(lmENableLableComboBox);
			lmJpanel.add(lmPriorityLabel);
			lmJpanel.add(lmPriorityLabeltext);
			lmJpanel.add(lmSendCycleLable);
			lmJpanel.add(lmSendCycleLableComboBox);

			dmJpanel.add(dmENableLable);
			dmJpanel.add(dmENableLableComboBox);
			dmJpanel.add(dmPriorityLabel);
			dmJpanel.add(dmPriorityLabeltext);
			dmJpanel.add(dmSendCycleLable);
			dmJpanel.add(dmSendCycleLableComboBox);
			
			remoteMEPJpanel.add(remoteJcheck1);
			remoteMEPJpanel.add(remoteMepIdLabelTextField);
			remoteMEPJpanel.add(macAddress1Label);
			remoteMEPJpanel.add(macAddress1LabelTextField);
			remoteMEPJpanel.add(remoteJcheck2);
			remoteMEPJpanel.add(remoteMepId2LabelTextField);
			remoteMEPJpanel.add(macAddress2Label);
			remoteMEPJpanel.add(macAddress2LabelTextField);
			remoteMEPJpanel.add(remoteJcheck3);
			remoteMEPJpanel.add(remoteMepId3LabelTextField);
			remoteMEPJpanel.add(macAddress3Label);
			remoteMEPJpanel.add(macAddress3LabelTextField);
			remoteMEPJpanel.add(remoteJcheck4);
			remoteMEPJpanel.add(remoteMepId4LabelTextField);
			remoteMEPJpanel.add(macAddress4Label);
			remoteMEPJpanel.add(macAddress4LabelTextField);
		
			mipJpanel.add(mipCreateLable);
			mipJpanel.add(mipCreateLableComboBox);
			mipJpanel.add(mipPortLable);
			mipJpanel.add(mipPortLableComboBox);
			setBorDerLayout();
			this.setTitle(ResourceUtil.srcStr(StringKeysObj.STRING_ETHERNET_OAM_CONFIG));
			addListener();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void setBorDerLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		
		componentLayout.columnWidths = new int[] {400};
		componentLayout.columnWeights = new double[] { 0, 0, 0 };
		componentLayout.rowHeights = new int[] { 10, 10};
		componentLayout.rowWeights = new double[] {};
		this.panelContent.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.mdJpanel, c);
		this.panelContent.add(this.mdJpanel);
		
		componentLayout.setConstraints(this.tstJpanel, c);
		this.panelContent.add(this.tstJpanel);
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.maJpanel, c);
		this.panelContent.add(this.maJpanel);
		componentLayout.setConstraints(this.lmJpanel, c);
		this.panelContent.add(this.lmJpanel);
		
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.mepJpanel, c);
		this.panelContent.add(this.mepJpanel);
		componentLayout.setConstraints(this.dmJpanel, c);
		this.panelContent.add(this.dmJpanel);
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.lbmJpanel, c);
		this.panelContent.add(this.lbmJpanel);
		componentLayout.setConstraints(this.mipJpanel, c);
		this.panelContent.add(this.mipJpanel);
		
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.lckJpanel, c);
		this.panelContent.add(this.lckJpanel);
		componentLayout.setConstraints(this.aisJpanel, c);
		this.panelContent.add(this.aisJpanel);
		
		c.gridx = 0;
		c.gridy =5;
		c.gridheight = 1;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.remoteMEPJpanel, c);
		this.panelContent.add(this.remoteMEPJpanel);
		
		this.add(this.panelContent, BorderLayout.CENTER);
		this.add(this.buttonPanel, BorderLayout.SOUTH);
	}

	private void addListener() {
		try {
			mdNameLabelTextField.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(FocusEvent arg0) {
					isValue();
				}
				@Override
				public void focusGained(FocusEvent arg0) {

				}
			});
			confirm.addActionListener(new MyActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					btnSave();
				}

				@Override
				public boolean checking() {
					
					return true;
				}
			});
			cancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					dialog.dispose();
				}
			});
			
			this.btnNext.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						panelContent.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_ETHOAM_SECOND)));
					
						tstJpanel.setVisible(true);
						lmJpanel.setVisible(true);
						dmJpanel.setVisible(true);
						mipJpanel.setVisible(true);
						remoteMEPJpanel.setVisible(true);
						aisJpanel.setVisible(true);
						
						mdJpanel.setVisible(false);
						maJpanel.setVisible(false);
						mepJpanel.setVisible(false);
						lbmJpanel.setVisible(false);
						lckJpanel.setVisible(false);
						
						btnBack.setVisible(true);
						btnNext.setVisible(false);
						confirm.setVisible(true);
					} catch (Exception e) {
						ExceptionManage.dispose(e,this.getClass());
					}
				}
			});
			this.btnBack.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					panelContent.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_ETHOAM_FIRST)));
					tstJpanel.setVisible(false);
					lmJpanel.setVisible(false);
					dmJpanel.setVisible(false);
					mipJpanel.setVisible(false);
					remoteMEPJpanel.setVisible(false);
					aisJpanel.setVisible(false);
					
					mdJpanel.setVisible(true);
					maJpanel.setVisible(true);
					mepJpanel.setVisible(true);
					lbmJpanel.setVisible(true);
					lckJpanel.setVisible(true);
					btnBack.setVisible(false);
					btnNext.setVisible(true);
					confirm.setVisible(false);

				}
			});
			Remote1Box.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(Remote1Box.isSelected()){
						remoteMepIdLabelTextField.setText("1");
						remoteMepIdLabelTextField.setEnabled(true);
						macAddress1LabelTextField.setEnabled(true);
	                 }else{
	                	 remoteMepIdLabelTextField.setText("0");
	                	 remoteMepIdLabelTextField.setEditable(false);
	                	 remoteMepIdLabelTextField.setEnabled(false);
	                	 macAddress1LabelTextField.setText("00-00-00-00-00-01");
	                	 macAddress1LabelTextField.setEnabled(false);
	                 }
				}
			});
			Remote2Box.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(Remote2Box.isSelected()){
						remoteMepId2LabelTextField.setText("1");
						remoteMepId2LabelTextField.setEnabled(true);
						 macAddress2LabelTextField.setEnabled(true);
	                 }else{
	                	 remoteMepId2LabelTextField.setText("0");
	                	 remoteMepId2LabelTextField.setEnabled(false);
	                	 macAddress2LabelTextField.setEnabled(false);
	                	 macAddress2LabelTextField.setText("00-00-00-00-00-01");
	                 }
				}
			});
			Remote3Box.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(Remote3Box.isSelected()){
						remoteMepId3LabelTextField.setText("1");
						remoteMepId3LabelTextField.setEnabled(true);
						 macAddress3LabelTextField.setEnabled(true);
	                 }else{
	                	 remoteMepId3LabelTextField.setText("0");
	                	 remoteMepId3LabelTextField.setEnabled(false);
	                	 macAddress3LabelTextField.setEnabled(false);
	                	 macAddress3LabelTextField.setText("00-00-00-00-00-01");
	                 }
				}
			});
			Remote4Box.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(Remote4Box.isSelected()){
						remoteMepId4LabelTextField.setText("1");
						remoteMepId4LabelTextField.setEnabled(true);
						 macAddress4LabelTextField.setEnabled(true);
	                 }else{
	                	 remoteMepId4LabelTextField.setText("0");
	                	 remoteMepId4LabelTextField.setEnabled(false);
	                	 macAddress4LabelTextField.setEnabled(false);
	                	 macAddress4LabelTextField.setText("00-00-00-00-00-01"); 
	                 } 
				}
			});
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}

	private void btnSave() {
		OamEthernetInfo oamEthernetInfo = null;
		List<OamInfo> oamInfoList = null;
		OamInfo oamIfoDispath =null;
		DispatchUtil dispath = null;
		String result =null;
		PortService_MB portService = null;
		try {
			dispath = new DispatchUtil(RmiKeys.RMI_ETHOAMCONFIG);
			oamInfoList = new ArrayList<OamInfo>();
			oamIfoDispath = new OamInfo();
			OamEthernetInfo ethOAMBefore = null;
			if (oamInfo != null && oamInfo.getOamEthernetInfo() != null) {
				oamEthernetInfo = oamInfo.getOamEthernetInfo();
				ethOAMBefore = new OamEthernetInfo();
				CoderUtils.copy(oamEthernetInfo, ethOAMBefore);
			}else{
				oamEthernetInfo = new OamEthernetInfo();
			}
			// ID
			if (portLabelComboBox.getSelectedItem() == null || mipPortLableComboBox.getSelectedItem() == null) {
				DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_MUSTNETWORK_BEFORE));
				return;
			}
			if(maNameTextfield.getText().trim().equals("")){
				DialogBoxUtil.succeedDialog(null,ResourceUtil.srcStr(StringKeysTip.TIP_MANULL)); 
				return ;
			}
			
			ControlKeyValue selecttunnel = (ControlKeyValue) portLabelComboBox.getSelectedItem();
			PortInst portInst = (PortInst) selecttunnel.getObject();
			ControlKeyValue selecttunnels = (ControlKeyValue) mipPortLableComboBox.getSelectedItem();
			PortInst portInsts = (PortInst) selecttunnels.getObject();
			// MIP端口号
//			oamEthernetInfo.setMipPortName(portInsts.getPortName());
			oamEthernetInfo.setMipPort(portInsts.getNumber() + "");
			oamEthernetInfo.setMipSlot(portInsts.getSlotNumber());
			// 端口和槽位号
			oamEthernetInfo.setPort(portInst.getNumber() + "");
			oamEthernetInfo.setSlot(portInst.getSlotNumber());
			oamEthernetInfo.setSiteId(ConstantUtil.siteId);
			// 以太网OAM 使能
			ControlKeyValue thernetOAMEnablekey_broad = (ControlKeyValue) this.thernetOAMEnableComboBox.getSelectedItem();
			oamEthernetInfo.setThernetOAMEnabl(Integer.parseInt(((Code) thernetOAMEnablekey_broad.getObject()).getCodeValue()));
			// 默认MD LEVEL
			oamEthernetInfo.setMdMLevel(mdMLevelLabelTextField.getText().trim());
			// MP属性：
			ControlKeyValue mpLablekey_broad = (ControlKeyValue) this.mpLableComboBox.getSelectedItem();
			oamEthernetInfo.setMpLable(Integer.parseInt(((Code) mpLablekey_broad.getObject()).getCodeValue()));
			// MD Name
			oamEthernetInfo.setMdName(mdNameLabelTextField.getText().trim());
			// MD Level
			oamEthernetInfo.setMdLevel(mdLevelLabelTextField.getText().trim());
			// MA Name
			oamEthernetInfo.setMaName(maNameTextfield.getText().trim());
			// CCM发送间隔
			ControlKeyValue ccmsendLabelkey_broad = (ControlKeyValue) this.ccmsendLabelComboBox.getSelectedItem();
			oamEthernetInfo.setCcmsend(Integer.parseInt(((Code) ccmsendLabelkey_broad.getObject()).getCodeValue()));
			// VLAN
			oamEthernetInfo.setVlan(vlanLableTextField.getText().trim());
			// MEP ID
			oamEthernetInfo.setMepId(mepIDLableTextField.getText().trim());
			// MEP类型
			ControlKeyValue mepTypeLabelkey_broad = (ControlKeyValue) this.mepTypeLabelComboBox.getSelectedItem();
			oamEthernetInfo.setMepType(Integer.parseInt(((Code) mepTypeLabelkey_broad.getObject()).getCodeValue()));

			// CCM发送使能
			ControlKeyValue ccmSendEnablekey_broad = (ControlKeyValue) this.ccmSendEnableComboBox.getSelectedItem();
			oamEthernetInfo.setCcmSendEnable(Integer.parseInt(((Code) ccmSendEnablekey_broad.getObject()).getCodeValue()));
			// CCM接受使能
			ControlKeyValue ccmReceiveEnablekey_broad = (ControlKeyValue) this.ccmReceiveEnableComboBox.getSelectedItem();
			oamEthernetInfo.setCcmReceiveEnable(Integer.parseInt(((Code) ccmReceiveEnablekey_broad.getObject()).getCodeValue()));
			// CCM优先级
			oamEthernetInfo.setCcmPriority(ccmPriorityLabelTextField.getText().trim());
			// LBM数据TLV类型
			ControlKeyValue lbmTlvTypeLablekey_broad = (ControlKeyValue) this.lbmTlvTypeLableComboBox.getSelectedItem();
			oamEthernetInfo.setLbmTlvType(Integer.parseInt(((Code) lbmTlvTypeLablekey_broad.getObject()).getCodeValue()));
			// LBM数据TLV长度
			oamEthernetInfo.setLbmTlvTypeLength(lbmTlvTypeLengthLabelTextField.getText().trim());
			// LBM优先级
			oamEthernetInfo.setLbmPriority(lbmPriorityLabelTextField.getText().trim());
			// LBM丢弃适用性
			ControlKeyValue lbmDiscardLablekey_broad = (ControlKeyValue) this.lbmDiscardLableComboBox.getSelectedItem();
			oamEthernetInfo.setLbmDiscard(Integer.parseInt(((Code) lbmDiscardLablekey_broad.getObject()).getCodeValue()));
			// LTM优先级
			oamEthernetInfo.setLtmPriority(ltmPriorityLabelTextField.getText().trim());
			// AIS发送使能
			ControlKeyValue aisSendEnabelkey_broad = (ControlKeyValue) this.aisSendEnabelLableComboBox.getSelectedItem();
			oamEthernetInfo.setAisSendEnabel(Integer.parseInt(((Code) aisSendEnabelkey_broad.getObject()).getCodeValue()));
			// 客户MD Level
			oamEthernetInfo.setClientMdLevel(clientMdLevelLabelTextField.getText().trim());
			// AIS优先级
			oamEthernetInfo.setAisPriority(aisPriorityLabelTextField.getText().trim());
			// LCK发送使能
			ControlKeyValue lckSendEnabelkey_broad = (ControlKeyValue) this.lckSendEnabelLableComboBox.getSelectedItem();
			oamEthernetInfo.setLckSendEnabel(Integer.parseInt(((Code) lckSendEnabelkey_broad.getObject()).getCodeValue()));
			// LCK优先级
			oamEthernetInfo.setLckPriority(lckPriorityLabelTextField.getText().trim());
			// AIS/LCK发送周期
			ControlKeyValue aisLckSendCyclekey_broad = (ControlKeyValue) this.aisLckSendcycleComboBox.getSelectedItem();
			oamEthernetInfo.setAisLckSendCycle(Integer.parseInt(((Code) aisLckSendCyclekey_broad.getObject()).getCodeValue()));
			// TST发送使能
			ControlKeyValue tstSendEnabelkey_broad = (ControlKeyValue) this.tstSendEnabelLableComboBox.getSelectedItem();
			oamEthernetInfo.setTstSendEnabel(Integer.parseInt(((Code) tstSendEnabelkey_broad.getObject()).getCodeValue()));
			// TST发送MDLevel
			oamEthernetInfo.setTstSendLevel(tstSendLevelLabelTextField.getText().trim());
			// TST目的MEP MAC
			oamEthernetInfo.setTstPurposeMepMac(tstPurposeMepMacLabelTextField.getText().trim());
			// TST优先级
			oamEthernetInfo.setTstPriority(tstPriorityLabelTextField.getText().trim());
			// TST发送使能
			ControlKeyValue sendWaylkey_broad = (ControlKeyValue) this.sendWaylLableComboBox.getSelectedItem();
			oamEthernetInfo.setSendWay(Integer.parseInt(((Code) sendWaylkey_broad.getObject()).getCodeValue()));
			// TST丢弃适用性
			ControlKeyValue tstDiscardkey_broad = (ControlKeyValue) this.tstDiscardlLableComboBox.getSelectedItem();
			oamEthernetInfo.setTstDiscard(Integer.parseInt(((Code) tstDiscardkey_broad.getObject()).getCodeValue()));
			// TST数据TLV类型
			ControlKeyValue tstTlvTypekey_broad = (ControlKeyValue) this.tstTlvTypelLableComboBox.getSelectedItem();
			oamEthernetInfo.setTstTlvType(Integer.parseInt(((Code) tstTlvTypekey_broad.getObject()).getCodeValue()));
			// TST数据TLV长度
			oamEthernetInfo.setTstTlvLength(tstTlvLengthLabletext.getText().trim());
			// TST发送周期
			ControlKeyValue tstSendCyclekey_broad = (ControlKeyValue) this.tstSendCycleLableComboBox.getSelectedItem();
			oamEthernetInfo.setTstSendCycle(Integer.parseInt(((Code) tstSendCyclekey_broad.getObject()).getCodeValue()));
			// LM使能
			ControlKeyValue lmENablekey_broad = (ControlKeyValue) this.lmENableLableComboBox.getSelectedItem();
			oamEthernetInfo.setLmENable(Integer.parseInt(((Code) lmENablekey_broad.getObject()).getCodeValue()));
			// LM优先级
			oamEthernetInfo.setLmPriority(lmPriorityLabeltext.getText().trim());
			// LM发送周期
			ControlKeyValue lmSendCyclekey_broad = (ControlKeyValue) this.lmSendCycleLableComboBox.getSelectedItem();
			oamEthernetInfo.setLmSendCycle(Integer.parseInt(((Code) lmSendCyclekey_broad.getObject()).getCodeValue()));
			// DM使能
			ControlKeyValue dmENablekey_broad = (ControlKeyValue) this.dmENableLableComboBox.getSelectedItem();
			oamEthernetInfo.setDmENable(Integer.parseInt(((Code) dmENablekey_broad.getObject()).getCodeValue()));
			// DM优先级
			oamEthernetInfo.setDmPriority(dmPriorityLabeltext.getText().trim());
			// DM发送周期
			ControlKeyValue dmSendCyclekey_broad = (ControlKeyValue) this.dmSendCycleLableComboBox.getSelectedItem();
			oamEthernetInfo.setDmSendCycle(Integer.parseInt(((Code) dmSendCyclekey_broad.getObject()).getCodeValue()));
			// Remote MEP ID 1
			oamEthernetInfo.setRemoteMepId1(remoteMepIdLabelTextField.getText().trim());
			// MAC 1地址
			oamEthernetInfo.setMacAddress1(macAddress1LabelTextField.getText().trim());
			// Remote MEP ID 2
			oamEthernetInfo.setRemoteMepId2(remoteMepId2LabelTextField.getText().trim());
			// MAC 2地址
			oamEthernetInfo.setMacAddress2(macAddress2LabelTextField.getText().trim());
			// Remote MEP ID 3
			oamEthernetInfo.setRemoteMepId3(remoteMepId3LabelTextField.getText().trim());
			// MAC3地址
			oamEthernetInfo.setMacAddress3(macAddress3LabelTextField.getText().trim());
			// Remote MEP ID4
			oamEthernetInfo.setRemoteMepId4(remoteMepId4LabelTextField.getText().trim());
			// MAC 4地址
			oamEthernetInfo.setMacAddress4(macAddress4LabelTextField.getText().trim());
			// MIP生成规则
			ControlKeyValue mipCreatekey_broad = (ControlKeyValue) this.mipCreateLableComboBox.getSelectedItem();
			oamEthernetInfo.setMipCreate(Integer.parseInt(((Code) mipCreatekey_broad.getObject()).getCodeValue()));

			// 下适配
			oamIfoDispath.setOamEthernetInfo(oamEthernetInfo);
			oamInfoList.add(oamIfoDispath);
			result = dispath.excuteInsert(oamInfoList);
			//添加日志记录
			oamEthernetInfo.setPort(portInst.getPortName());
			oamEthernetInfo.setMipPort(portInsts.getPortName());
			if(ethOAMBefore != null){
				portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
				List<Integer> numberList = new ArrayList<Integer>();
				numberList.add(Integer.parseInt(ethOAMBefore.getPort()));
				ethOAMBefore.setPort(portService.getAllPortNameByNumber(numberList, ConstantUtil.siteId).get(0));
				numberList.clear();
				numberList.add(Integer.parseInt(ethOAMBefore.getMipPort()));
				ethOAMBefore.setMipPort(portService.getAllPortNameByNumber(numberList, ConstantUtil.siteId).get(0));
				AddOperateLog.insertOperLog(confirm, EOperationLogType.ETHERNETOAMUPDATE.getValue(), result,
						ethOAMBefore, oamEthernetInfo, ConstantUtil.siteId, oamEthernetInfo.getPort()+"_CFM", "ethOAM");
			}else{
				AddOperateLog.insertOperLog(confirm, EOperationLogType.ETHERNETOAMINSERT.getValue(), result,
						ethOAMBefore, oamEthernetInfo, ConstantUtil.siteId, oamEthernetInfo.getPort()+"_CFM", "ethOAM");
			}
			// 更新界面
			controller.refresh();
			// 隐藏界面
			dialog.dispose();
			DialogBoxUtil.succeedDialog(this, result);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(portService);
		}
	}
	
	private void setButtonLayout() {
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

		c.anchor = GridBagConstraints.EAST;
		componentLayout.setConstraints(this.btnBack, c);
		this.buttonPanel.add(this.btnBack);

		c.gridx = 1;
		componentLayout.setConstraints(this.btnNext, c);
		this.buttonPanel.add(this.btnNext);

		c.gridx = 1;
		componentLayout.setConstraints(this.confirm, c);
		this.buttonPanel.add(this.confirm);

		c.gridx = 2;
		componentLayout.setConstraints(this.cancel, c);
		this.buttonPanel.add(this.cancel);
	}
	
	private void setMipJpanelComponentLayout() {
		GridBagConstraints gridBagConstraints = null;
		GridBagLayout  gridBagLayout=null;
		try {
			gridBagConstraints = new GridBagConstraints();
			gridBagLayout=new GridBagLayout();
			gridBagLayout.columnWidths = new int[] { 10,90,10,90, 10,90 };
			gridBagLayout.columnWeights = new double[] { 0, 0, 0 };
			gridBagLayout.rowHeights = new int[] { 10, 10};
			gridBagLayout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			this.mipJpanel.setLayout(gridBagLayout);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(mipCreateLable, gridBagConstraints);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(mipCreateLableComboBox, gridBagConstraints);
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(mipPortLable, gridBagConstraints);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(mipPortLableComboBox, gridBagConstraints);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	private void setRemoteMEPJpanelComponentLayout() {
		GridBagConstraints gridBagConstraints = null;
		GridBagLayout  gridBagLayout=null;
		try {
			gridBagConstraints = new GridBagConstraints();
			gridBagLayout=new GridBagLayout();
			gridBagLayout.columnWidths = new int[] { 10, 120, 10, 120, 10, 120 };
			gridBagLayout.columnWeights = new double[] { 0, 0, 0 };
			gridBagLayout.rowHeights = new int[] { 10, 10};
			gridBagLayout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			this.remoteMEPJpanel.setLayout(gridBagLayout);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(remoteJcheck1, gridBagConstraints);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(remoteMepIdLabelTextField, gridBagConstraints);
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(macAddress1Label, gridBagConstraints);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(macAddress1LabelTextField, gridBagConstraints);
			gridBagConstraints.gridx = 4;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(remoteJcheck2, gridBagConstraints);
			gridBagConstraints.gridx = 5;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(remoteMepId2LabelTextField, gridBagConstraints);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(macAddress2Label, gridBagConstraints);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(macAddress2LabelTextField, gridBagConstraints);
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(remoteJcheck3, gridBagConstraints);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(remoteMepId3LabelTextField, gridBagConstraints);
			gridBagConstraints.gridx = 4;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(macAddress3Label, gridBagConstraints);
			gridBagConstraints.gridx = 5;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(macAddress3LabelTextField, gridBagConstraints);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(remoteJcheck4, gridBagConstraints);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(remoteMepId4LabelTextField, gridBagConstraints);
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(macAddress4Label, gridBagConstraints);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(macAddress4LabelTextField, gridBagConstraints);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		}
	
	private void setDmJpanelComponentLayout() {
		GridBagConstraints gridBagConstraints = null;
		GridBagLayout  gridBagLayout=null;
		try {
			gridBagConstraints = new GridBagConstraints();
			gridBagLayout=new GridBagLayout();
			gridBagLayout.columnWidths = new int[] { 10,90,10,90, 10,90 };
			gridBagLayout.columnWeights = new double[] { 0, 0, 0 };
			gridBagLayout.rowHeights = new int[] { 10, 10};
			gridBagLayout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			this.dmJpanel.setLayout(gridBagLayout);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(dmENableLable, gridBagConstraints);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(dmENableLableComboBox, gridBagConstraints);
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(dmPriorityLabel, gridBagConstraints);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(dmPriorityLabeltext, gridBagConstraints);
			gridBagConstraints.gridx = 4;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(dmSendCycleLable, gridBagConstraints);
			gridBagConstraints.gridx = 5;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(dmSendCycleLableComboBox, gridBagConstraints);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		}
 
	private void setLmJpanelComponentLayout() {
		GridBagConstraints gridBagConstraints = null;
		GridBagLayout  gridBagLayout=null;
		try {
			gridBagConstraints = new GridBagConstraints();
			gridBagLayout=new GridBagLayout();
			gridBagLayout.columnWidths = new int[] { 10, 120, 10, 120, 10, 120 };
			gridBagLayout.columnWeights = new double[] { 0, 0, 0 };
			gridBagLayout.rowHeights = new int[] { 10, 10};
			gridBagLayout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			this.lmJpanel.setLayout(gridBagLayout);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(lmENableLable, gridBagConstraints);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(lmENableLableComboBox, gridBagConstraints);
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(lmPriorityLabel, gridBagConstraints);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(lmPriorityLabeltext, gridBagConstraints);
			gridBagConstraints.gridx = 4;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(lmSendCycleLable, gridBagConstraints);
			gridBagConstraints.gridx = 5;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(lmSendCycleLableComboBox, gridBagConstraints);
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		}
			
	private void setTstJpanelComponentLayout() {
		GridBagConstraints gridBagConstraints = null;
		GridBagLayout  gridBagLayout=null;
		try {
			gridBagConstraints = new GridBagConstraints();
			gridBagLayout=new GridBagLayout();
			gridBagLayout.columnWidths = new int[] { 10, 120, 10, 120, 10, 120 };
			gridBagLayout.columnWeights = new double[] { 0, 0, 0 };
			gridBagLayout.rowHeights = new int[] { 10, 10};
			gridBagLayout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			this.tstJpanel.setLayout(gridBagLayout);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(tstSendEnabelLable, gridBagConstraints);
			tstJpanel.add(tstSendEnabelLable);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(tstSendEnabelLableComboBox, gridBagConstraints);
			tstJpanel.add(tstSendEnabelLableComboBox);
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(tstSendLevelLabel, gridBagConstraints);
			tstJpanel.add(tstSendLevelLabel);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(tstSendLevelLabelTextField, gridBagConstraints);
			tstJpanel.add(tstSendLevelLabelTextField);
			gridBagConstraints.gridx = 4;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(tstPurposeMepMacLabel, gridBagConstraints);
			tstJpanel.add(tstPurposeMepMacLabel);
			gridBagConstraints.gridx = 5;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(tstPurposeMepMacLabelTextField, gridBagConstraints);
			tstJpanel.add(tstPurposeMepMacLabelTextField);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(tstPriorityLabel, gridBagConstraints);
			tstJpanel.add(tstPriorityLabel);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(tstPriorityLabelTextField, gridBagConstraints);
			tstJpanel.add(tstPriorityLabelTextField);
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(tstDiscardlLable, gridBagConstraints);
			tstJpanel.add(tstDiscardlLable);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(tstDiscardlLableComboBox, gridBagConstraints);
			tstJpanel.add(tstDiscardlLableComboBox);
			
			gridBagConstraints.gridx = 4;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(tstTlvTypelLable, gridBagConstraints); 
			tstJpanel.add(tstTlvTypelLable);
			
			gridBagConstraints.gridx = 5;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(tstTlvTypelLableComboBox, gridBagConstraints);
			tstJpanel.add(tstTlvTypelLableComboBox);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(tstTlvLengthLable, gridBagConstraints);
			tstJpanel.add(tstTlvLengthLable);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(tstTlvLengthLabletext, gridBagConstraints);
			tstJpanel.add(tstTlvLengthLabletext);
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(tstSendCycleLable, gridBagConstraints);
			tstJpanel.add(tstSendCycleLable);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(tstSendCycleLableComboBox, gridBagConstraints);
			tstJpanel.add(tstSendCycleLableComboBox);
			gridBagConstraints.gridx = 4;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(sendWaylLable, gridBagConstraints);
			tstJpanel.add(sendWaylLable);
			gridBagConstraints.gridx = 5;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(sendWaylLableComboBox, gridBagConstraints);
			tstJpanel.add(sendWaylLableComboBox);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		}

	private void setLckJpanelComponentLayout() {
		GridBagConstraints gridBagConstraints = null;
		GridBagLayout  gridBagLayout=null;
		try {
			gridBagConstraints = new GridBagConstraints();
			gridBagLayout=new GridBagLayout();
			gridBagLayout.columnWidths = new int[] { 10, 120, 10, 120, 10, 120 };
			gridBagLayout.columnWeights = new double[] { 0, 0, 0 };
			gridBagLayout.rowHeights = new int[] { 10, 10};
			gridBagLayout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			this.lckJpanel.setLayout(gridBagLayout);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(lckSendEnabelLable, gridBagConstraints);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(lckSendEnabelLableComboBox, gridBagConstraints);
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(lckPriorityLabel, gridBagConstraints);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(lckPriorityLabelTextField, gridBagConstraints);
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		}
			
	private void setAisJpanelComponentLayout() {
		GridBagConstraints gridBagConstraints = null;
		GridBagLayout  gridBagLayout=null;
		try {
			gridBagConstraints = new GridBagConstraints();
			gridBagLayout=new GridBagLayout();
			gridBagLayout.columnWidths = new int[] { 10, 120, 10, 120, 10, 120 };
			gridBagLayout.columnWeights = new double[] { 0, 0, 0 };
			gridBagLayout.rowHeights = new int[] { 10, 10};
			gridBagLayout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			this.aisJpanel.setLayout(gridBagLayout);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(aisSendEnabelLable, gridBagConstraints);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(aisSendEnabelLableComboBox, gridBagConstraints);
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(aisPriorityLabel, gridBagConstraints);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(aisPriorityLabelTextField, gridBagConstraints);
			gridBagConstraints.gridx = 4;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(aisLckSendCycleLable, gridBagConstraints);
			gridBagConstraints.gridx = 5;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(aisLckSendcycleComboBox, gridBagConstraints);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(clientMdLevelLabel, gridBagConstraints);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(clientMdLevelLabelTextField, gridBagConstraints);
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		}
	
	private void setLbmJpanelComponentLayout() {
		GridBagConstraints gridBagConstraints = null;
		GridBagLayout gridBagLayout=null;
		try {
			gridBagConstraints = new GridBagConstraints();
			gridBagLayout=new GridBagLayout();
			gridBagLayout.columnWidths = new int[] { 10, 120, 10, 120, 10, 120 };
			gridBagLayout.columnWeights = new double[] { 0, 0, 0 };
			gridBagLayout.rowHeights = new int[] { 10, 10,10};
			gridBagLayout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			this.lbmJpanel.setLayout(gridBagLayout);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(lbmTlvTypeLable, gridBagConstraints);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(lbmTlvTypeLableComboBox, gridBagConstraints);
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(lbmTlvTypeLengthLabel, gridBagConstraints);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(lbmTlvTypeLengthLabelTextField, gridBagConstraints);
			gridBagConstraints.gridx = 4;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(lbmPriorityLabel, gridBagConstraints);
			gridBagConstraints.gridx = 5;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(lbmPriorityLabelTextField, gridBagConstraints);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(lbmDiscardLable, gridBagConstraints);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(lbmDiscardLableComboBox, gridBagConstraints);
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(ltmPriorityLabel, gridBagConstraints);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(ltmPriorityLabelTextField, gridBagConstraints);
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		}
	
	private void setMepJpanelComponentLayout() {
		GridBagConstraints gridBagConstraints = null;
		GridBagLayout gridBagLayout=null;
		try {
			gridBagConstraints = new GridBagConstraints();
			gridBagLayout=new GridBagLayout();
			gridBagLayout.columnWidths = new int[] { 10, 120, 10, 120, 10, 120 };
			gridBagLayout.columnWeights = new double[] { 0, 0, 0 };
			gridBagLayout.rowHeights = new int[] { 10, 10,10};
			gridBagLayout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			this.mepJpanel.setLayout(gridBagLayout);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(mepIDLable, gridBagConstraints);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(mepIDLableTextField, gridBagConstraints);
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(mepTypeLabel, gridBagConstraints);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(mepTypeLabelComboBox, gridBagConstraints);
			gridBagConstraints.gridx = 4;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(portLabel, gridBagConstraints);
			gridBagConstraints.gridx = 5;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(portLabelComboBox, gridBagConstraints);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(ccmSendEnable, gridBagConstraints);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(ccmSendEnableComboBox, gridBagConstraints);
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(ccmReceiveEnable, gridBagConstraints);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(ccmReceiveEnableComboBox, gridBagConstraints);
			gridBagConstraints.gridx = 4;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(ccmPriorityLabel, gridBagConstraints);
			gridBagConstraints.gridx = 5;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(ccmPriorityLabelTextField, gridBagConstraints);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(ccmsendLabel, gridBagConstraints);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(ccmsendLabelComboBox, gridBagConstraints);
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		}
	
	private void setMdJpanelComponentLayout() {
		GridBagConstraints gridBagConstraints = null;
		GridBagLayout  gridBagLayout=null;
		try {
			gridBagConstraints = new GridBagConstraints();
			gridBagLayout = new GridBagLayout();
			gridBagLayout.columnWidths = new int[] { 10, 120, 10, 120, 10, 120 };
			gridBagLayout.columnWeights = new double[] { 0, 0, 0 };
			gridBagLayout.rowHeights = new int[] { 10, 10};
			gridBagLayout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			this.mdJpanel.setLayout(gridBagLayout);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(thernetOAMEnableLabel, gridBagConstraints);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(thernetOAMEnableComboBox, gridBagConstraints);
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(mdMLevelLabel, gridBagConstraints);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(mdMLevelLabelTextField, gridBagConstraints);
			gridBagConstraints.gridx = 4;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(mpLable, gridBagConstraints);
			gridBagConstraints.gridx = 5;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(mpLableComboBox, gridBagConstraints);

			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(mdNameLabel, gridBagConstraints);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(mdNameLabelTextField, gridBagConstraints);
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(mdLevelLabel, gridBagConstraints);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(mdLevelLabelTextField, gridBagConstraints);
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		}
	
	private void setMaJpanelComponentLayout() {
		GridBagConstraints gridBagConstraints = null;
		GridBagLayout gridBagLayout=null;
		try {
			gridBagLayout=new GridBagLayout();
			gridBagConstraints = new GridBagConstraints();
			gridBagLayout.columnWidths = new int[] { 10, 120, 10, 120, 10, 120 };
			gridBagLayout.columnWeights = new double[] { 0, 0, 0 };
			gridBagLayout.rowHeights = new int[] { 10};
			gridBagLayout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			this.maJpanel.setLayout(gridBagLayout);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(maName, gridBagConstraints);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(maNameTextfield, gridBagConstraints);
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(vlanLable, gridBagConstraints);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(vlanLableTextField, gridBagConstraints);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
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

	public void isValue() {
		if (mdNameLabelTextField.getText() != null && !"".equals(mdNameLabelTextField.getText())) {
			/**
			 * TST系列
			 */
			tstSendEnabelLableComboBox.setEnabled(false);
			tstSendLevelLabelTextField.setEnabled(false);
			tstPurposeMepMacLabelTextField.setEnabled(false);
			tstPriorityLabelTextField.setEnabled(false);
			tstDiscardlLableComboBox.setEnabled(false);
			tstTlvTypelLableComboBox.setEnabled(false);
			tstTlvLengthLabletext.setEnabled(false);
			tstSendCycleLableComboBox.setEnabled(false);
			clientMdLevelLabelTextField.setEnabled(false);
			sendWaylLableComboBox.setEnabled(false);
			/**
			 * AIS 系列
			 */
			aisSendEnabelLableComboBox.setEnabled(false);
			aisPriorityLabelTextField.setEnabled(false);
			aisLckSendcycleComboBox.setEnabled(false);
			/**
			 *LCK系列
			 */
			lckSendEnabelLableComboBox.setEnabled(false);
			lckPriorityLabelTextField.setEnabled(false);
			/**
			 * LM/DM系列
			 */
			lmENableLableComboBox.setEnabled(false);
			lmPriorityLabeltext.setEnabled(false);
			lmSendCycleLableComboBox.setEnabled(false);

			dmENableLableComboBox.setEnabled(false);
			dmPriorityLabeltext.setEnabled(false);
			dmSendCycleLableComboBox.setEnabled(false);
		} else {
			/**
			 * TST系列
			 */
			tstSendEnabelLableComboBox.setEnabled(true);
			tstSendLevelLabelTextField.setEnabled(true);
			tstPurposeMepMacLabelTextField.setEnabled(true);
			tstPriorityLabelTextField.setEnabled(true);
			tstDiscardlLableComboBox.setEnabled(true);
			tstTlvTypelLableComboBox.setEnabled(true);
			tstTlvLengthLabletext.setEnabled(true);
			tstSendCycleLableComboBox.setEnabled(true);
			clientMdLevelLabelTextField.setEnabled(true);
			sendWaylLableComboBox.setEnabled(true);
			/**
			 * AIS 系列
			 */
			aisSendEnabelLableComboBox.setEnabled(true);
			aisPriorityLabelTextField.setEnabled(true);
			aisLckSendcycleComboBox.setEnabled(true);
			/**
			 *LCK系列
			 */
			lckSendEnabelLableComboBox.setEnabled(true);
			lckPriorityLabelTextField.setEnabled(true);
			/**
			 * LM/DM系列
			 */
			lmENableLableComboBox.setEnabled(true);
			lmPriorityLabeltext.setEnabled(true);
			lmSendCycleLableComboBox.setEnabled(true);

			dmENableLableComboBox.setEnabled(true);
			dmPriorityLabeltext.setEnabled(true);
			dmSendCycleLableComboBox.setEnabled(true);
		}
	}

	/**
	 * 
	 * 按钮所在panel布局
	 */
	private void setRomote1Layout(JCheckBox jcheckBox,JLabel label,JPanel jpanel) throws Exception {
		GridBagConstraints gridBagConstraints = null;
		GridBagLayout gridBagLayout = null;
		try {
			gridBagLayout = new GridBagLayout();
			gridBagConstraints = new GridBagConstraints();
			gridBagLayout.columnWidths = new int[] { 20, 5 };
			gridBagLayout.columnWeights = new double[] { 0, 0 };
			gridBagLayout.rowHeights = new int[] { 21 };
			gridBagLayout.rowWeights = new double[] { 0 };

			gridBagConstraints.insets = new Insets(5, 10, 5, 0);
			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(jcheckBox, gridBagConstraints);

			gridBagConstraints.insets = new Insets(5, 0, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(label, gridBagConstraints);

			jpanel.setLayout(gridBagLayout);
		} catch (Exception e) {
			throw e;
		}
	}

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
	public PtnButton getConfirm() {
		return confirm;
	}

	public void setConfirm(PtnButton confirm) {
		this.confirm = confirm;
	}

	public JButton getCancel() {
		return cancel;
	}

	public void setCancel(JButton cancel) {
		this.cancel = cancel;
	}

}
