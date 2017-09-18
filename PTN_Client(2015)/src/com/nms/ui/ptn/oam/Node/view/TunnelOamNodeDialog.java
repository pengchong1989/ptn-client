package com.nms.ui.ptn.oam.Node.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.oam.OamMepInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.OamTypeEnum;
import com.nms.drive.service.impl.CoderUtils;
import com.nms.model.ptn.oam.OamInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
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
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.oam.Node.controller.TunnelOamNodeController;

public class TunnelOamNodeDialog extends PtnDialog {

	private TunnelOamNodeController controller;
	private static final long serialVersionUID = -1830839435398260186L;
	private JLabel lspLabel;
	private JComboBox lspNameComboBox;
	private JLabel tcLabel;
	private JComboBox tcComboBox;
	private JLabel melLabel;
	private JTextField melField;

	private JLabel localLabel;
	private JTextField localField;
	private JLabel remoteLabel;
	private JTextField remoteField;

	private JLabel loopEnableLabel;
	private JCheckBox loopEnableCheckBox;
	private JLabel loopPeriodLabel;
	private JComboBox loopPeriodComboBox;
	private JLabel loopTestWayLabel;
	private JComboBox loopTestWayCombox;
	private JLabel loopOffLineTestTLVLabel;
	private JComboBox loopOffLineTestTLVCombox;
	private JLabel loopTlvLengthLabel;
	private JTextField loopTlvLengthField;
	private JLabel loopTLVInfoLabel;
	private JTextField loopTLVInfoField;

	private JLabel tstEnableLabel;
	private JCheckBox tstEnableCheckBox;
	private JLabel tstPeriodLabel;
	private JComboBox tstPeriodComboBox;
	private JLabel tstTlvTypeLabel;
	private JComboBox tstTlvTypeComboBox;
	private JLabel tstTLCLengthLabel;
	private JTextField tstTLVLengthField;

	private JLabel lckLabel;
	private JCheckBox lckCheckBox;
	private JLabel lmEnableLabel;
	private JCheckBox lmCheckBox;
	private JLabel lmCycleLabel;
	private JComboBox lmCycleComboBox;
	private JLabel dmEnableLabel;
	private JCheckBox dmCheckBox;
	private JLabel dmCycleLabel;
	private JComboBox dmCycleComboBox;
	private JLabel lbTTlLabel;//lbTTL 
	private JTextField lbTTLField;//lb生命周期

	private JPanel componentPanel;

	private JPanel buttonPanel;
	private PtnButton confirm;
	private JButton cancel;
	
	private JLabel vertifyLabel;

	private OamInfo oamInfo;

	private JLabel ltEnable;//lt使能
	private JComboBox ltComboBox;
	private JLabel ltExp;//leEXP等级
	private JComboBox ltExpComboBox;
	private JLabel ltTTl;//ltTTL 
	private JTextField lttField;
	private OamMepInfo oammepInfoBefore;//记录修改前的数据，便于日志记录
	
	public TunnelOamNodeDialog(OamInfo oamInfo) {
		this.setModal(true);
		this.initComponent();
		this.setComponentLayout();
		this.addListener();
		this.intalCombox();
		this.controller = new TunnelOamNodeController(this);
		if (oamInfo != null) {
			this.oamInfo = oamInfo;
			oammepInfoBefore = new OamMepInfo();
			CoderUtils.copy(this.oamInfo.getOamMep(), oammepInfoBefore);
			this.setValue();
		} else {
			try {
				//lspName赋值
				this.initLspNameCombox();
				//初始化值
				this.initValue();
			} catch (Exception e) {
				ExceptionManage.dispose(e,this.getClass());
			}
		}
	}

	private void initLspNameCombox() {
		lspNameComboBox.removeAllItems();
		OamInfoService_MB oamInfoService = null;
		List<OamInfo> oamList = null;
		List<OamInfo> oamList_temp = null;
		OamInfo oamInfo = null;
		List<Tunnel> tunnelList = null;
		TunnelService_MB tunnelServiceMB = null;
		OamMepInfo oamMepInfo = null;
		try {
			tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			oamInfo = new OamInfo();
			oamMepInfo = new OamMepInfo();
			oamMepInfo.setSiteId(ConstantUtil.siteId);
			oamMepInfo.setObjType("TUNNEL_TEST");
			oamInfo.setOamMep(oamMepInfo);
			oamList = oamInfoService.queryBySiteId(oamInfo, OamTypeEnum.AMEP);
			oamInfo.getOamMep().setObjType("TUNNEL");
			oamList_temp = oamInfoService.queryBySiteId(oamInfo, OamTypeEnum.AMEP);
			tunnelList = tunnelServiceMB.queryTunnelBySiteId(ConstantUtil.siteId);
		
			boolean b = false;
			DefaultComboBoxModel boxModel = (DefaultComboBoxModel) lspNameComboBox.getModel();
			for (Tunnel inst : tunnelList) {
				for (OamInfo info : oamList) {
					if (info.getOamMep().getObjId() == inst.getTunnelId()) {
						b = true;
					}
				}
				if (!b) {
					for (OamInfo info_temp : oamList_temp) {
						//如果此条tunnel上没有主动OAM,就不允许配按需OAM,就把这条路径过滤掉
						if (info_temp.getOamMep().getServiceId() == inst.getTunnelId()) {
							//把主动OAM的MEG等级,本端维护点Id,远端维护点Id赋给按需OAM对应的属性
//							this.melField.setText(info_temp.getOamMep().getMel()+"");
//							this.localField.setText(info_temp.getOamMep().getLocalMepId()+"");
//							this.remoteField.setText(info_temp.getOamMep().getRemoteMepId()+"");
							boxModel.addElement(new ControlKeyValue(inst.getTunnelId() + "", inst.getTunnelName(), inst));
						}
					}
				}
				b = false;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(oamInfoService);
			UiUtil.closeService_MB(tunnelServiceMB);
			oamList = null;
			oamList_temp = null;
			oamInfo = null;
			tunnelList = null;
		}
		
	}

	private void initValue() {
		comboBoxSelect(tcComboBox, "7");
		lckCheckBox.setSelected(false);
		if(melField.getText() == null || melField.getText().equals("")){
			melField.setText("7");
		}
		super.getComboBoxDataUtil().comboBoxSelectByValue(ltComboBox,"0");
		super.getComboBoxDataUtil().comboBoxSelectByValue(ltExpComboBox, "0");
		lttField.setText("64");
		localField.setText("1");
		remoteField.setText("1");
//		if (mepInfo.isRingEnable()) {
		loopEnableCheckBox.setSelected(false);
		comboBoxSelect(loopPeriodComboBox, "1");
		comboBoxSelect(loopTestWayCombox, "0");
		comboBoxSelect(loopOffLineTestTLVCombox, "11");
		loopTlvLengthField.setText("1");
		loopTLVInfoField.setText("0");

		loopPeriodComboBox.setEnabled(false);
		loopTestWayCombox.setEnabled(false);
		loopOffLineTestTLVCombox.setEnabled(false);
		loopTlvLengthField.setEnabled(false);
		loopTLVInfoField.setEnabled(false);
		lbTTLField.setText("64");
//		}
//		if (mepInfo.isTstEnable()) {
		tstEnableCheckBox.setSelected(false);
		comboBoxSelect(tstPeriodComboBox,  "1");
		comboBoxSelect(tstTlvTypeComboBox, "11");
		tstTLVLengthField.setText("25");

		tstPeriodComboBox.setEnabled(false);
		tstTlvTypeComboBox.setEnabled(false);
		tstTLVLengthField.setEnabled(false);
//		}
//		if (mepInfo.isLm()) {
		lmCheckBox.setSelected(false);
		comboBoxSelect(lmCycleComboBox, "0");
		lmCycleComboBox.setEnabled(false);
//		}
//		if (mepInfo.isDm()) {
		dmCheckBox.setSelected(false);
		comboBoxSelect(dmCycleComboBox, "0");
		dmCycleComboBox.setEnabled(false);
//		}
		
	}

	private void initComponent() {
		vertifyLabel = new JLabel();
		vertifyLabel.setForeground(Color.red);
		this.setTitle(ResourceUtil.srcStr(StringKeysObj.STRING_TUNNEL_OAM_CONFIG));
		componentPanel = new JPanel();
		lspLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_SERVICE_PATH_NAME));
		lspNameComboBox = new JComboBox();
		lspNameComboBox.setPreferredSize(new Dimension(330,5));
		localLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_LOCAL_MEP_ID));
		localField = new JTextField();
		remoteLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_FAR_MEP_MIP_ID));
		remoteField = new JTextField();
		melLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_MEG_LEVLE));
		melField = new JTextField();
		loopEnableLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_LOOP_FRAME_SEND_ENABLED));
		loopEnableCheckBox = new JCheckBox();
		loopPeriodLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_LOOP_FRAME_PERIOD));
		loopPeriodComboBox = new JComboBox();
		loopTestWayLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_LOOP_TEST_TYPE));
		loopTestWayCombox = new JComboBox();
		loopOffLineTestTLVLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_OFFLINE_TLV_TYPE));
		loopOffLineTestTLVCombox = new JComboBox();
		loopTlvLengthLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_LOOP_TLV_LENGTH));
		loopTlvLengthField = new JTextField();
		loopTLVInfoLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_TLV_TEST_CONTENT));
		loopTLVInfoField = new JTextField();

		tstEnableLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_TST_SEND_ENABLED));
		tstEnableCheckBox = new JCheckBox();
		tstPeriodLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_TST_FRAME_SEND_PERIOD));
		tstPeriodComboBox = new JComboBox();
		tstTlvTypeLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_TLV_TYPE));
		tstTlvTypeComboBox = new JComboBox();
		tstTLCLengthLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_TST_TLV_LENGTH));
		tstTLVLengthField = new JTextField();
		lckLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_LOCK_YESORNO));
		lckCheckBox = new JCheckBox();
		tcLabel = new JLabel("TC");
		tcComboBox = new JComboBox();
		lmEnableLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_LM_SEND_ENABLED));
		lmCheckBox = new JCheckBox();
		lmCycleLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_LM_SEND_PERIOD));
		lmCycleComboBox = new JComboBox();
		dmEnableLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_DM_SEND_ENABLED));
		dmCheckBox = new JCheckBox();
		dmCycleLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_DM_SEND_PERIOD));
		dmCycleComboBox = new JComboBox();
		ltEnable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LTENABLE));
		ltComboBox = new JComboBox();
		ltExp = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LTEXP));
		ltExpComboBox = new JComboBox();
		ltTTl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LTTTL)); 
		lttField = new JTextField();
		lbTTlLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LBTTL));
		lbTTLField = new JTextField();
		
		buttonPanel = new JPanel();
		confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),true);
		cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		try {
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

//		setAttribute();

	}

	private void setComponentLayout() {
		setOamInfoLayout();
		setButtonLayout();
		GridBagLayout layout = new GridBagLayout();
		layout.rowHeights = new int[] { 180, 20 };
		layout.rowWeights = new double[] { 1.0, 0.0 };
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 0, 0, 0);
		layout.setConstraints(componentPanel, c);
		this.add(componentPanel);
		c.gridy = 1;
		c.fill = GridBagConstraints.EAST;
		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(buttonPanel, c);
		this.add(buttonPanel);
	}

	private void setOamInfoLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 60, 150, 60, 150 };
		componentLayout.columnWeights = new double[] { 1.0, 1.0 };
		componentLayout.rowHeights = new int[] { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10 };
		componentLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		componentPanel.setLayout(componentLayout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(lspLabel, c);
		componentPanel.add(lspLabel);
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(lspNameComboBox, c);
		componentPanel.add(lspNameComboBox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(tcLabel, c);
		componentPanel.add(tcLabel);
		c.gridx = 3;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(tcComboBox, c);
		componentPanel.add(tcComboBox);

		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(lckLabel, c);
		componentPanel.add(lckLabel);
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(lckCheckBox, c);
		componentPanel.add(lckCheckBox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(melLabel, c);
		componentPanel.add(melLabel);
		c.gridx = 3;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(melField, c);
		componentPanel.add(melField);

		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(localLabel, c);
		componentPanel.add(localLabel);
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(localField, c);
		componentPanel.add(localField);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(remoteLabel, c);
		componentPanel.add(remoteLabel);
		c.gridx = 3;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(remoteField, c);
		componentPanel.add(remoteField);

		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(loopEnableLabel, c);
		componentPanel.add(loopEnableLabel);
		c.gridx = 1;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(loopEnableCheckBox, c);
		componentPanel.add(loopEnableCheckBox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(loopPeriodLabel, c);
		componentPanel.add(loopPeriodLabel);
		c.gridx = 3;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(loopPeriodComboBox, c);
		componentPanel.add(loopPeriodComboBox);

		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(loopOffLineTestTLVLabel, c);
		componentPanel.add(loopOffLineTestTLVLabel);
		c.gridx = 1;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(loopOffLineTestTLVCombox, c);
		componentPanel.add(loopOffLineTestTLVCombox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(loopTestWayLabel, c);
		componentPanel.add(loopTestWayLabel);
		c.gridx = 3;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(loopTestWayCombox, c);
		componentPanel.add(loopTestWayCombox);

		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(loopTlvLengthLabel, c);
		componentPanel.add(loopTlvLengthLabel);
		c.gridx = 1;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(loopTlvLengthField, c);
		componentPanel.add(loopTlvLengthField);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(loopTLVInfoLabel, c);
		componentPanel.add(loopTLVInfoLabel);
		c.gridx = 3;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(loopTLVInfoField, c);
		componentPanel.add(loopTLVInfoField);

		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(tstEnableLabel, c);
		componentPanel.add(tstEnableLabel);
		c.gridx = 1;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(tstEnableCheckBox, c);
		componentPanel.add(tstEnableCheckBox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(tstPeriodLabel, c);
		componentPanel.add(tstPeriodLabel);
		c.gridx = 3;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(tstPeriodComboBox, c);
		componentPanel.add(tstPeriodComboBox);

		c.gridx = 0;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(tstTlvTypeLabel, c);
		componentPanel.add(tstTlvTypeLabel);
		c.gridx = 1;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(tstTlvTypeComboBox, c);
		componentPanel.add(tstTlvTypeComboBox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(tstTLCLengthLabel, c);
		componentPanel.add(tstTLCLengthLabel);
		c.gridx = 3;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(tstTLVLengthField, c);
		componentPanel.add(tstTLVLengthField);

		c.gridx = 0;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(lmEnableLabel, c);
		componentPanel.add(lmEnableLabel);
		c.gridx = 1;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(lmCheckBox, c);
		componentPanel.add(lmCheckBox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(lmCycleLabel, c);
		componentPanel.add(lmCycleLabel);
		c.gridx = 3;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(lmCycleComboBox, c);
		componentPanel.add(lmCycleComboBox);

		c.gridx = 0;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(dmEnableLabel, c);
		componentPanel.add(dmEnableLabel);
		c.gridx = 1;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(dmCheckBox, c);
		componentPanel.add(dmCheckBox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(dmCycleLabel, c);
		componentPanel.add(dmCycleLabel);
		c.gridx = 3;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(dmCycleComboBox, c);
		componentPanel.add(dmCycleComboBox);
		
		c.gridx = 0;
		c.gridy = 10;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(ltEnable, c);
		componentPanel.add(ltEnable);
		c.gridx = 1;
		c.gridy = 10;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(ltComboBox, c);
		componentPanel.add(ltComboBox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 10;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(ltExp, c);
		componentPanel.add(ltExp);
		c.gridx = 3;
		c.gridy = 10;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(ltExpComboBox, c);
		componentPanel.add(ltExpComboBox);

		c.gridx = 0;
		c.gridy = 11;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(ltTTl, c);
		componentPanel.add(ltTTl);
		c.gridx = 1;
		c.gridy = 11;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(lttField, c);
		componentPanel.add(lttField);

		c.gridx = 2;
		c.gridy = 11;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(lbTTlLabel, c);
		componentPanel.add(lbTTlLabel);
		c.gridx = 3;
		c.gridy = 11;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(lbTTLField, c);
		componentPanel.add(lbTTLField);
	}

	private void setButtonLayout() {
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		buttonPanel.setLayout(flowLayout);
		buttonPanel.add(vertifyLabel);
		buttonPanel.add(confirm);
		buttonPanel.add(cancel);
	}

	private void addListener() {
		loopEnableCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (loopEnableCheckBox.isSelected()) {
					loopPeriodComboBox.setEnabled(true);
					loopTestWayCombox.setEnabled(true);
					loopOffLineTestTLVCombox.setEnabled(true);
					loopTlvLengthField.setEnabled(true);
					loopTLVInfoField.setEnabled(true);

				} else {
					loopPeriodComboBox.setEnabled(false);
					loopTestWayCombox.setEnabled(false);
					loopOffLineTestTLVCombox.setEnabled(false);
					loopTlvLengthField.setEnabled(false);
					loopTLVInfoField.setEnabled(false);
				}
			}
		});
		tstEnableCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (tstEnableCheckBox.isSelected()) {
					tstPeriodComboBox.setEnabled(true);
					tstTlvTypeComboBox.setEnabled(true);
					tstTLVLengthField.setEnabled(true);
				} else {
					tstPeriodComboBox.setEnabled(false);
					tstTlvTypeComboBox.setEnabled(false);
					tstTLVLengthField.setEnabled(false);
				}

			}
		});
		lmCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (lmCheckBox.isSelected()) {
					lmCycleComboBox.setEnabled(true);
				} else {
					lmCycleComboBox.setEnabled(false);
				}
			}
		});
		dmCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (dmCheckBox.isSelected()) {
					dmCycleComboBox.setEnabled(true);
				} else {
					dmCycleComboBox.setEnabled(false);
				}
			}
		});
		confirm.addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if(lspNameComboBox.getSelectedItem() == null){
						tip();
					}else{
						if ((loopEnableCheckBox.isSelected() == true) && (loopTlvLengthField.getText().length() == 0 || loopTLVInfoField.getText().length() == 0)) {
							vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_FULL_DATA));
							return;
						}
						if ((tstEnableCheckBox.isSelected() == true) && (tstTLVLengthField.getText().length() == 0)) {
							vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_FULL_DATA));
							return;
						}
						if(checkData()){
							return;
						}
						collectData();

						DispatchUtil dispath = new DispatchUtil(RmiKeys.RMI_TMPOAMCONFIG);
//						List<OamInfo> list = new ArrayList<OamInfo>();
//						list.add(oamInfo);
						String result = "";
						int operationValue = 0;
						if(oamInfo.getOamMep().getId() > 0){
							result = dispath.excuteUpdate(oamInfo);
							operationValue = EOperationLogType.TUNNELOAMTESTSUP.getValue();
							oammepInfoBefore.setObjNameLog(oamInfo.getOamMep().getObjNameLog());
							oammepInfoBefore.setLmCycle(oammepInfoBefore.getLmCycle()==0?100:11);
							oammepInfoBefore.setDmCycle(oammepInfoBefore.getDmCycle()==0?100:11);
							oammepInfoBefore.setOffLineTestTLV(oammepInfoBefore.getOffLineTestTLV()==1?0:1);
							oammepInfoBefore.setTstTLVType(oammepInfoBefore.getTstTLVType()==1?0:1);
						}else{
							result = dispath.excuteInsert(oamInfo);
							operationValue = EOperationLogType.TUNNELOAMTESTSINGINSERT.getValue();
						}
						oamInfo.getOamMep().setLmCycle(oamInfo.getOamMep().getLmCycle()==0?100:11);
						oamInfo.getOamMep().setDmCycle(oamInfo.getOamMep().getDmCycle()==0?100:11);
						oamInfo.getOamMep().setOffLineTestTLV(oamInfo.getOamMep().getOffLineTestTLV()==1?0:1);
						oamInfo.getOamMep().setTstTLVType(oamInfo.getOamMep().getTstTLVType()==1?0:1);
						AddOperateLog.insertOperLog(confirm, operationValue, result, 
								oammepInfoBefore, oamInfo.getOamMep(), ConstantUtil.siteId, oamInfo.getOamMep().getObjNameLog(), "testTunnelOAM");
						DialogBoxUtil.succeedDialog(null, result);
						dispose();
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}

			@Override
			public boolean checking() {
				// TODO Auto-generated method stub
				return true;
			}
		});
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
	}
	
	/**
	 * true/false = 验证不通过/验证通过
	 */
	private boolean checkData() {
		if(this.controller.checkLbTTL()){
			return true;
		}
		if(this.controller.checkLocalId()){
			return true;
		}
		if(this.controller.checkremoteId()){
			return true;
		}
		if(this.controller.checkLtTTL()){
			return true;
		}
		if(this.controller.checkMEL()){
			return true;
		}
		if(this.controller.checkTVLLength()){
			return true;
		}
		if(this.controller.checkTVLData()){
			return true;
		}
		if(this.controller.checkTSTTLVLength()){
			return true;
		}
		return false;
	}

	protected void tip() {
		DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NO_JOB_PATH));
		return;
	}

	/**
	 * 设置属性
	 */
	private void setAttribute() {
	
		if (loopEnableCheckBox.isSelected()) {
			loopPeriodComboBox.setEnabled(true);
			loopTestWayCombox.setEnabled(true);
			loopOffLineTestTLVCombox.setEnabled(true);
			loopTlvLengthField.setEnabled(true);
			loopTLVInfoField.setEnabled(true);

		} else {
			loopPeriodComboBox.setEnabled(false);
			loopTestWayCombox.setEnabled(false);
			loopOffLineTestTLVCombox.setEnabled(false);
			loopTlvLengthField.setEnabled(false);
			loopTLVInfoField.setEnabled(false);
		}
		if (tstEnableCheckBox.isSelected()) {
			tstPeriodComboBox.setEnabled(true);
			tstTlvTypeComboBox.setEnabled(true);
			tstTLVLengthField.setEnabled(true);
		} else {
			tstPeriodComboBox.setEnabled(false);
			tstTlvTypeComboBox.setEnabled(false);
			tstTLVLengthField.setEnabled(false);
		}
		if (dmCheckBox.isSelected()) {
			dmCycleComboBox.setEnabled(true);
		} else {
			dmCycleComboBox.setEnabled(false);
		}
		if (lmCheckBox.isSelected()) {
			lmCycleComboBox.setEnabled(true);
		} else {
			lmCycleComboBox.setEnabled(false);
		}

	}

	private void setValue() {
		OamMepInfo mepInfo = oamInfo.getOamMep();
		Tunnel tunnel = this.getLspName(mepInfo);
		DefaultComboBoxModel boxModel = (DefaultComboBoxModel) lspNameComboBox.getModel();
		boxModel.addElement(new ControlKeyValue(tunnel.getTunnelId() + "", tunnel.getTunnelName(), tunnel));
		lspNameComboBox.setModel(boxModel);
		// comboBoxSelect(tunnelIdComboBox, mepInfo.getServiceId() + "");
		// tunnelIdComboBox.setEnabled(false);
//		comboBoxSelect(lspNameComboBox,lspName);
		lspNameComboBox.setEnabled(false);
		comboBoxSelect(tcComboBox, mepInfo.getLspTc() + "");
		lckCheckBox.setSelected(mepInfo.isLck());
//		melField.setText(mepInfo.getMel() + "");
//		this.initValueToId();
		this.melField.setText(mepInfo.getMel()+"");
		localField.setText(mepInfo.getLocalMepId()+"");
		remoteField.setText(mepInfo.getRemoteMepId()+"");
		super.getComboBoxDataUtil().comboBoxSelectByValue(ltComboBox, mepInfo.getLtEnable()+"");
		super.getComboBoxDataUtil().comboBoxSelectByValue(ltExpComboBox, mepInfo.getLtEXP()+""); 
		lttField.setText(mepInfo.getLtTTL()+"");
		
		loopEnableCheckBox.setSelected(mepInfo.isRingEnable());
		comboBoxSelect(loopPeriodComboBox, mepInfo.getRingCycle() + "");
		comboBoxSelect(loopTestWayCombox, mepInfo.getRingTestWay() + "");
		comboBoxSelect(loopOffLineTestTLVCombox, mepInfo.getOffLineTestTLV() + "");
		loopTlvLengthField.setText(mepInfo.getRingTLVLength() + "");
		loopTLVInfoField.setText(mepInfo.getRingTLVInfo() + "");
		if (mepInfo.isRingEnable()) {
			loopPeriodComboBox.setEnabled(true);
			loopTestWayCombox.setEnabled(true);
			loopOffLineTestTLVCombox.setEnabled(true);
			loopTlvLengthField.setEnabled(true);
			loopTLVInfoField.setEnabled(true);
		}else{
			loopPeriodComboBox.setEnabled(false);
			loopTestWayCombox.setEnabled(false);
			loopOffLineTestTLVCombox.setEnabled(false);
			loopTlvLengthField.setEnabled(false);
			loopTLVInfoField.setEnabled(false);
		}
	
		tstEnableCheckBox.setSelected(mepInfo.isTstEnable());
		comboBoxSelect(tstPeriodComboBox, mepInfo.getTstCycle() + "");
		comboBoxSelect(tstTlvTypeComboBox, mepInfo.getTstTLVType() + "");
		tstTLVLengthField.setText(mepInfo.getTstTLVLength() + "");
		if (mepInfo.isTstEnable()) {
			tstPeriodComboBox.setEnabled(true);
			tstTlvTypeComboBox.setEnabled(true);
			tstTLVLengthField.setEnabled(true);
		}else{
			tstPeriodComboBox.setEnabled(false);
			tstTlvTypeComboBox.setEnabled(false);
			tstTLVLengthField.setEnabled(false);
		}
		
		lmCheckBox.setSelected(mepInfo.isLm());
		comboBoxSelect(lmCycleComboBox, mepInfo.getLmCycle() + "");
		if (mepInfo.isLm()) {
			lmCycleComboBox.setEnabled(true);
		}else{
			lmCycleComboBox.setEnabled(false);
		}
		
		dmCheckBox.setSelected(mepInfo.isDm());
		comboBoxSelect(dmCycleComboBox, mepInfo.getDmCycle() + "");
		if (mepInfo.isDm()) {
			dmCycleComboBox.setEnabled(true);
		}else{
			dmCycleComboBox.setEnabled(false);
		}
		lbTTLField.setText(mepInfo.getLbTTL()+"");
	}

	private void initValueToId() {
		OamInfoService_MB oamInfoService = null;
		OamInfo oamInfo = null;
		OamMepInfo oamMepInfo = null;
		try {
			oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			oamInfo = new OamInfo();
			oamMepInfo = new OamMepInfo();
			oamMepInfo.setSiteId(ConstantUtil.siteId);
			oamMepInfo.setServiceId(Integer.parseInt(((ControlKeyValue)this.lspNameComboBox.getSelectedItem()).getId()));
			oamMepInfo.setObjType("TUNNEL");
			oamInfo.setOamMep(oamMepInfo);
			oamInfo = oamInfoService.queryByCondition(oamInfo, OamTypeEnum.AMEP);
			if(oamInfo != null && oamInfo.getOamMep() != null){
				melField.setText(oamInfo.getOamMep().getMel() + "");
//				localField.setText(oamList.get(0).getOamMep().getLocalMepId() + "");
//				remoteField.setText(oamList.get(0).getOamMep().getRemoteMepId() + "");
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(oamInfoService);
		}
	}

//	private String getLspId(int tunnelId) {
//		TunnelService tunnelService = null;
//		Tunnel tunnel = null;
//		int tunnelServiceId = 0;
//		try {
//			tunnelService = (TunnelService) ConstantUtil.serviceFactory
//					.newService(Services.Tunnel);
//			tunnel = new Tunnel();
//			tunnel.setTunnelId(tunnelId);
//			tunnel = tunnelService.select_nojoin(tunnel).get(0);
//			tunnelServiceId = tunnel.getLspParticularList().get(0)
//			.getASiteId() == ConstantUtil.siteId ? tunnel.getLspParticularList().get(0).getAlspbusinessid() :
//				 tunnel.getLspParticularList().get(0).getZlspbusinessid();
//
//		} catch (Exception e) {
//			ExceptionManage.dispose(e,this.getClass());
//		} finally {
//			tunnelService = null;
//			tunnel = null;
//		}
//		return tunnelServiceId+"";
//	}

	/**
	 * 通过tunnelId获取tunnelName
	 * @param mepInfo
	 * @return
	 */
	private Tunnel getLspName(OamMepInfo mepInfo) {
		TunnelService_MB sevice = null;
		Tunnel tunnel = null;
		List<Tunnel> list = null;
		try {
			sevice = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			tunnel = new Tunnel();
			tunnel.setTunnelId(mepInfo.getObjId());
			list = sevice.selectNodeByTunnelId(tunnel);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			tunnel = null;
			UiUtil.closeService_MB(sevice);
		}
		return list.get(0);
	}

	private void intalCombox() {
		intalCycleCombox(loopPeriodComboBox);
		intalCycleTestWayCombox(loopTestWayCombox);
		intalTVLTypeCombox(loopOffLineTestTLVCombox);
		intalCycleCombox(tstPeriodComboBox);
		intalTVLTypeCombox(tstTlvTypeComboBox);
		intalLmAndDmCombox(lmCycleComboBox);
		intalLmAndDmCombox(dmCycleComboBox);
		intalTcAndMel(tcComboBox);
		try {
			super.getComboBoxDataUtil().comboBoxData(ltComboBox, "ENABLEDSTATUE");
			super.getComboBoxDataUtil().comboBoxData(ltExpComboBox, "Distribute_PHB");
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}

	private void intalCycleTestWayCombox(JComboBox combox) {
		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		map.put(0, ResourceUtil.srcStr(StringKeysObj.STRING_ONLINE));
		map.put(1, ResourceUtil.srcStr(StringKeysObj.STRING_OFFLINE));
		DefaultComboBoxModel boxModel = (DefaultComboBoxModel) combox.getModel();
		for (Integer key : map.keySet()) {
			boxModel.addElement((new ControlKeyValue(key.toString(), map.get(key))));
		}
	}

	private void intalCycleCombox(JComboBox combox) {

		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		map.put(1, "3.33ms");
		map.put(10, "10ms");
		map.put(11, "100ms");
		map.put(100, "1s");
		DefaultComboBoxModel boxModel = (DefaultComboBoxModel) combox.getModel();
		for (Integer key : map.keySet()) {
			boxModel.addElement((new ControlKeyValue(key.toString(), map.get(key))));
		}

	}

	private void intalTVLTypeCombox(JComboBox combox) {

		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		map.put(01, ResourceUtil.srcStr(StringKeysObj.STRING_ALL_0));
		map.put(11, ResourceUtil.srcStr(StringKeysObj.STRING_RANDOM));
		DefaultComboBoxModel boxModel = (DefaultComboBoxModel) combox.getModel();
		for (Integer key : map.keySet()) {
			boxModel.addElement((new ControlKeyValue(key.toString(), map.get(key))));
		}

	}

	private void intalLmAndDmCombox(JComboBox combox) {

		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		map.put(0, "1s");
		map.put(1, "100ms");
		DefaultComboBoxModel boxModel = (DefaultComboBoxModel) combox.getModel();
		for (Integer key : map.keySet()) {
			boxModel.addElement((new ControlKeyValue(key.toString(), map.get(key))));
		}

	}

	private void intalTcAndMel(JComboBox combox) {
		DefaultComboBoxModel boxModel = (DefaultComboBoxModel) combox.getModel();
		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		for (int i = 0; i < 8; i++) {
			map.put(i, i + "");
		}

		for (Integer key : map.keySet()) {
			boxModel.addElement((new ControlKeyValue(key.toString(), map.get(key))));
		}

	}

	private void collectData() throws Exception {
		OamMepInfo oamMep = null;
//		OamMipInfo oamMip = null;

		if (oamInfo == null) {
			oamInfo = new OamInfo();
			oamInfo.setOamType(OamTypeEnum.AMEP);
			oamMep = new OamMepInfo();
//			oamMip = new OamMipInfo();
			oamInfo.setOamMep(oamMep);
//			oamInfo.setOamMip(oamMip);
		} else {
			oamMep = oamInfo.getOamMep();
//			oamMip = oamInfo.getOamMip();
		}
		oamMep.setObjNameLog(((ControlKeyValue) (lspNameComboBox.getSelectedItem())).getName());
		oamMep.setObjType("TUNNEL_TEST");
//		oamMep.setMegIcc("123456");
//		oamMep.setMegUmc("789ABC");
		oamMep.setLocalMepId(Integer.parseInt(localField.getText()));
		oamMep.setRemoteMepId(Integer.parseInt(remoteField.getText()));
		oamMep.setObjId((Integer.parseInt(((ControlKeyValue) (lspNameComboBox.getSelectedItem())).getId())));
		oamMep.setLspTc(Integer.parseInt(((ControlKeyValue) (tcComboBox.getSelectedItem())).getId()));
		oamMep.setMel(Integer.parseInt(melField.getText().trim()));
		oamMep.setRingEnable(loopEnableCheckBox.isSelected() ? true : false);
		if (loopEnableCheckBox.isSelected() || oamInfo.getId()>0) {
			oamMep.setRingCycle(Integer.parseInt(((ControlKeyValue) (loopPeriodComboBox.getSelectedItem())).getId()));
			oamMep.setRingTestWay(Integer.parseInt(((ControlKeyValue) (loopTestWayCombox.getSelectedItem())).getId()));
			oamMep.setOffLineTestTLV(Integer.parseInt(((ControlKeyValue) (loopOffLineTestTLVCombox.getSelectedItem())).getId()));
			if (loopTlvLengthField.getText().trim() != null)
				oamMep.setRingTLVLength(Integer.parseInt(loopTlvLengthField.getText().trim()));
			if (loopTLVInfoField.getText().trim() != null)
				oamMep.setRingTLVInfo(Integer.parseInt(loopTLVInfoField.getText().trim()));
		}else{
			oamMep.setRingCycle(1);
			oamMep.setRingTestWay(0);
			oamMep.setOffLineTestTLV(11);
			oamMep.setRingTLVLength(1);
			oamMep.setRingTLVInfo(0);
			
		}
		
		oamMep.setTstEnable(tstEnableCheckBox.isSelected() ? true : false);
		if (tstEnableCheckBox.isSelected() || oamInfo.getId()>0) {
			oamMep.setTstCycle(Integer.parseInt(((ControlKeyValue) (tstPeriodComboBox.getSelectedItem())).getId()));
			oamMep.setTstTLVType(Integer.parseInt(((ControlKeyValue) (tstTlvTypeComboBox.getSelectedItem())).getId()));
			if (tstTLVLengthField.getText().trim() != null)
				oamMep.setTstTLVLength(Integer.parseInt(tstTLVLengthField.getText().trim()));
		}else{
			//赋默认值
			oamMep.setTstCycle(1);
			oamMep.setTstTLVType(11);
			oamMep.setTstTLVLength(25);
		}
		
		oamMep.setLck(lckCheckBox.isSelected() ? true : false);
		oamMep.setLm(lmCheckBox.isSelected() ? true : false);
		if (lmCheckBox.isSelected() || oamInfo.getId()>0) {
			oamMep.setLmCycle(Integer.parseInt(((ControlKeyValue) (lmCycleComboBox.getSelectedItem())).getId()));
		}else{
			//赋默认值
			oamMep.setLmCycle(0);
		}
		
		oamMep.setDm(dmCheckBox.isSelected() ? true : false);
		if (dmCheckBox.isSelected() || oamInfo.getId()>0){
			oamMep.setDmCycle(Integer.parseInt(((ControlKeyValue) (dmCycleComboBox.getSelectedItem())).getId()));
		}else{
			//赋默认值
			oamMep.setDmCycle(0);
		}
		ControlKeyValue key_enable = (ControlKeyValue)ltComboBox.getSelectedItem();
		oamMep.setLtEnable(Integer.parseInt(((Code)key_enable.getObject()).getCodeValue()));
		
		ControlKeyValue key_status = (ControlKeyValue)ltExpComboBox.getSelectedItem();
		oamMep.setLtEXP(Integer.parseInt(((Code)key_status.getObject()).getCodeValue()));
		oamMep.setLtTTL(Integer.parseInt(lttField.getText().trim()));
		oamMep.setLbTTL(Integer.parseInt(lbTTLField.getText().trim()));
		oamInfo.getOamMep().setSiteId(ConstantUtil.siteId);

	}

	public void comboBoxSelect(JComboBox jComboBox, String selectId) {
		for (int i = 0; i < jComboBox.getItemCount(); i++) {
			if (((ControlKeyValue) jComboBox.getItemAt(i)).getId().equals(selectId)) {
				jComboBox.setSelectedIndex(i);
				return;
			}

		}
	}

	public JLabel getVertifyLabel() {
		return vertifyLabel;
	}

	public void setVertifyLabel(JLabel vertifyLabel) {
		this.vertifyLabel = vertifyLabel;
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

	public JTextField getTstTLVLengthField() {
		return tstTLVLengthField;
	}

	public void setTstTLVLengthField(JTextField tstTLVLengthField) {
		this.tstTLVLengthField = tstTLVLengthField;
	}

	public JCheckBox getTstEnableCheckBox() {
		return tstEnableCheckBox;
	}

	public void setTstEnableCheckBox(JCheckBox tstEnableCheckBox) {
		this.tstEnableCheckBox = tstEnableCheckBox;
	}

	public JCheckBox getLoopEnableCheckBox() {
		return loopEnableCheckBox;
	}

	public void setLoopEnableCheckBox(JCheckBox loopEnableCheckBox) {
		this.loopEnableCheckBox = loopEnableCheckBox;
	}

	public JTextField getLoopTlvLengthField() {
		return loopTlvLengthField;
	}

	public void setLoopTlvLengthField(JTextField loopTlvLengthField) {
		this.loopTlvLengthField = loopTlvLengthField;
	}

	public JTextField getLoopTLVInfoField() {
		return loopTLVInfoField;
	}

	public void setLoopTLVInfoField(JTextField loopTLVInfoField) {
		this.loopTLVInfoField = loopTLVInfoField;
	}

	public TunnelOamNodeController getController() {
		return controller;
	}

	public void setController(TunnelOamNodeController controller) {
		this.controller = controller;
	}

	public JTextField getMelField() {
		return melField;
	}

	public void setMelField(JTextField melField) {
		this.melField = melField;
	}

	public JTextField getLocalField() {
		return localField;
	}

	public void setLocalField(JTextField localField) {
		this.localField = localField;
	}

	public JTextField getRemoteField() {
		return remoteField;
	}

	public void setRemoteField(JTextField remoteField) {
		this.remoteField = remoteField;
	}

	public JTextField getLttField() {
		return lttField;
	}

	public void setLttField(JTextField lttField) {
		this.lttField = lttField;
	}

	public JTextField getLbTTLField() {
		return lbTTLField;
	}

	public void setLbTTLField(JTextField lbTTLField) {
		this.lbTTLField = lbTTLField;
	}
}