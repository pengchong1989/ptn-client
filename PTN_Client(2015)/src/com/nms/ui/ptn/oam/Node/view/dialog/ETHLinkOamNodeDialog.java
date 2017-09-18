﻿package com.nms.ui.ptn.oam.Node.view.dialog;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.oam.OamLinkInfo;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EServiceType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.ptn.oam.OamInfoService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
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
import com.nms.ui.ptn.oam.Node.controller.ETHLinkOAMController;

public class ETHLinkOamNodeDialog extends PtnDialog {
	private static final long serialVersionUID = -5677652809768711073L;
	private JLabel portLabel;
	private JComboBox portComboBox;
	private JPanel oamConfigPanel;
	private JButton configRecover;
	private JLabel oamEnableLabel;
	private JCheckBox oamEnableCheckBox;
	private JLabel workWayLabel;
	private JComboBox workWayComboBox;
	private JLabel remoteLoopLabel;
	private JCheckBox remoteLoopCheckBox;
	private JLabel loopTimeOutLabel;
	private JTextField loopTimeOutField;
	private JLabel linkEnableLabel;
	private JCheckBox linkEnableCheckBox;
	private JLabel variableLabel;
	private JCheckBox variableCheckBox;
	private JLabel oamMessageLengthLabel;
	private JTextField oamMessageLengthField;
	private JLabel organicIdLabel;// 组织Id;
	private JTextField organicIdField;
	private JLabel factoryInfoLabel;
	private JTextField factoryInfoField;
	private JLabel sendCycleLabel;
	private JComboBox sendCycleComboBox;
	private JLabel linkfailCycleLabel;
	private JTextField linkfailField;

	private JPanel oamEventPanel;
	private JButton eventRecoverButton;
	private JLabel errorSignEventCycleLabel;
	private JTextField errorSignEventCycleField;
	private JLabel errorSignEventLimitLabel;
	private JTextField errorSignEventLimitField;
	private JLabel errorFrameEventCycleLabel;
	private JTextField errorFrameEventCycleField;
	private JLabel errorFrameEventLimitLabel;
	private JTextField errorFrameEventLimitField;
	private JLabel errorFrameCycleEventCycleLabel;
	private JTextField errorFrameCycleEventCycleField;
	private JLabel errorFrameCycleEventLimitLabel;
	private JTextField errorFrameCycleEventLimitField;
	private JLabel errorSecondEventCycleLabel;
	private JTextField errorSecondEventCycleField;
	private JLabel errorSecondEventLimitLabel;
	private JTextField errorSecondEventLimitField;

	private JPanel buttonPanel;
	private PtnButton confirm;
	private JButton cancel;

	private ETHLinkOAMController controller;
	private OamInfo oamInfo;

	public ETHLinkOamNodeDialog(OamInfo oamInfo) {
		this.setModal(true);
		if (oamInfo != null) {
			this.oamInfo = oamInfo;
		}
		initComponent();
		setComponentLayout();
		addListener();
		controller = new ETHLinkOAMController(this);
		intalCombox();
		configRecover.doClick();
		eventRecoverButton.doClick();
		if (oamInfo != null) {
			setValue();
			this.setTitle(ResourceUtil.srcStr(StringKeysBtn.BTN_UPDATE) + "  "+ ResourceUtil.srcStr(StringKeysLbl.LBL_ETHERNET_LINK_OAM) + " OAM");
		} else {
			this.setTitle(ResourceUtil.srcStr(StringKeysBtn.BTN_UPDATE) + "  "+ ResourceUtil.srcStr(StringKeysLbl.LBL_ETHERNET_LINK_OAM) + " OAM");
		}

	}

	private void initComponent() {
		portLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT));
		portComboBox = new JComboBox();
		oamConfigPanel = new JPanel();
		oamConfigPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_CONFIGOAM)));
		configRecover = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SET_DEFAULT));
		oamEnableLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_AGREEMENT));
		oamEnableCheckBox = new JCheckBox();
		workWayLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_WORK_MODEL));
		workWayComboBox = new JComboBox();
		remoteLoopLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_FAR_LOOP_YESORNO));
		remoteLoopCheckBox = new JCheckBox();
		loopTimeOutLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_LOOP_TIME));
		loopTimeOutField = new JTextField();
		linkEnableLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_LINK_EVENT));
		linkEnableCheckBox = new JCheckBox();
		variableLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_VARIABLE_QUERY));
		variableCheckBox = new JCheckBox();
		oamMessageLengthLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_MAX_OAM_MESSAGE));
		oamMessageLengthField = new JTextField();
		organicIdLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_ORG_ID));
		organicIdField = new JTextField();
		factoryInfoLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_VENDOR_INFO));
		factoryInfoField = new JTextField();
		sendCycleLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_CONTRACT_AWARDING_PERIOD));
		sendCycleComboBox = new JComboBox();
		linkfailCycleLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_LINKFAIL_PERIOD));
		linkfailField = new JTextField();

		oamEventPanel = new JPanel();
		oamEventPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_EVENT)));
		eventRecoverButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SET_DEFAULT));
		errorSignEventCycleLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_ERROR_SIGNAL_EVENT_PERIOD));
		errorSignEventCycleField = new JTextField();
		errorSignEventLimitLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_ERROR_SIGNAL_EVENT_THRESHOLD));
		errorSignEventLimitField = new JTextField();
		errorFrameEventCycleLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_ERROR_FRAME_EVENT_PERIOD));
		errorFrameEventCycleField = new JTextField();
		errorFrameEventLimitLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_ERROR_FRAME_EVENT_THRESHOLD));
		errorFrameEventLimitField = new JTextField();
		errorFrameCycleEventCycleLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_ERROR_FRAME_PERIOD_EVENT_PERIOD));
		errorFrameCycleEventCycleField = new JTextField();
		errorFrameCycleEventLimitLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_ERROR_FRAME_PERIOD_EVENT_THRESHOLD));
		errorFrameCycleEventLimitField = new JTextField();
		errorSecondEventCycleLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_ERROR_SECONDS_EVENT_PERIOD));
		errorSecondEventCycleField = new JTextField();
		errorSecondEventLimitLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_ERROR_SECONDS_EVENT_THRESHOLD));
		errorSecondEventLimitField = new JTextField();

		buttonPanel = new JPanel();
		cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM), true);

	}

	private void setComponentLayout() {
		buttonPanelLayout();
		setOamConfigPanelLayout();
		setOamEventPanelLayout();
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 30, 150, 30, 150 };
		layout.columnWeights = new double[] { 0, 0.2, 0, 0.2 };
		layout.rowHeights = new int[] { 50, 150, 30 };
		layout.rowWeights = new double[] { 0.5, 1.0, 0.0 };
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(15, 10, 5, 5);
		layout.setConstraints(portLabel, c);
		this.add(portLabel);
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(15, 5, 5, 5);
		layout.setConstraints(portComboBox, c);
		this.add(portComboBox);

		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(oamConfigPanel, c);
		this.add(oamConfigPanel);
		c.gridx = 2;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(oamEventPanel, c);
		this.add(oamEventPanel);

		c.gridx = 3;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(buttonPanel, c);
		this.add(buttonPanel);

	}

	private void setOamConfigPanelLayout() {
		GridBagLayout configLayout = new GridBagLayout();
		configLayout.columnWidths = new int[] { 30, 150 };
		configLayout.columnWeights = new double[] { 0, 0.2 };
		configLayout.rowHeights = new int[] { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10 };
		configLayout.rowWeights = new double[] { 0.0, 0.03, 0.03, 0.03, 0.03, 0.03, 0.03, 0.03, 0.03, 0.03, 0.03, 0.03 };
		oamConfigPanel.setLayout(configLayout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(5, 5, 5, 5);
		configLayout.setConstraints(configRecover, c);
		oamConfigPanel.add(configRecover);
		c.insets = new Insets(5, 5, 5, 5);
		c.gridx = 0;
		c.gridy = 1;
		configLayout.setConstraints(oamEnableLabel, c);
		oamConfigPanel.add(oamEnableLabel);
		c.gridx = 1;
		c.gridy = 1;
		configLayout.setConstraints(oamEnableCheckBox, c);
		oamConfigPanel.add(oamEnableCheckBox);

		c.gridx = 0;
		c.gridy = 2;
		configLayout.setConstraints(workWayLabel, c);
		oamConfigPanel.add(workWayLabel);
		c.gridx = 1;
		c.gridy = 2;
		configLayout.setConstraints(workWayComboBox, c);
		oamConfigPanel.add(workWayComboBox);

		c.gridx = 0;
		c.gridy = 3;
		configLayout.setConstraints(remoteLoopLabel, c);
		oamConfigPanel.add(remoteLoopLabel);
		c.gridx = 1;
		c.gridy = 3;
		configLayout.setConstraints(remoteLoopCheckBox, c);
		oamConfigPanel.add(remoteLoopCheckBox);

		c.gridx = 0;
		c.gridy = 4;
		configLayout.setConstraints(loopTimeOutLabel, c);
		oamConfigPanel.add(loopTimeOutLabel);
		c.gridx = 1;
		c.gridy = 4;
		configLayout.setConstraints(loopTimeOutField, c);
		oamConfigPanel.add(loopTimeOutField);

		c.gridx = 0;
		c.gridy = 5;
		configLayout.setConstraints(linkEnableLabel, c);
		oamConfigPanel.add(linkEnableLabel);
		c.gridx = 1;
		c.gridy = 5;
		configLayout.setConstraints(linkEnableCheckBox, c);
		oamConfigPanel.add(linkEnableCheckBox);

		c.gridx = 0;
		c.gridy = 6;
		configLayout.setConstraints(variableLabel, c);
		oamConfigPanel.add(variableLabel);
		c.gridx = 1;
		c.gridy = 6;
		configLayout.setConstraints(variableCheckBox, c);
		oamConfigPanel.add(variableCheckBox);

		c.gridx = 0;
		c.gridy = 7;
		configLayout.setConstraints(oamMessageLengthLabel, c);
		oamConfigPanel.add(oamMessageLengthLabel);
		c.gridx = 1;
		c.gridy = 7;
		configLayout.setConstraints(oamMessageLengthField, c);
		oamConfigPanel.add(oamMessageLengthField);

		c.gridx = 0;
		c.gridy = 8;
		configLayout.setConstraints(organicIdLabel, c);
		oamConfigPanel.add(organicIdLabel);
		c.gridx = 1;
		c.gridy = 8;
		configLayout.setConstraints(organicIdField, c);
		oamConfigPanel.add(organicIdField);

		c.gridx = 0;
		c.gridy = 9;
		configLayout.setConstraints(factoryInfoLabel, c);
		oamConfigPanel.add(factoryInfoLabel);
		c.gridx = 1;
		c.gridy = 9;
		configLayout.setConstraints(factoryInfoField, c);
		oamConfigPanel.add(factoryInfoField);

		c.gridx = 0;
		c.gridy = 10;
		configLayout.setConstraints(sendCycleLabel, c);
		oamConfigPanel.add(sendCycleLabel);
		c.gridx = 1;
		c.gridy = 10;
		configLayout.setConstraints(sendCycleComboBox, c);
		oamConfigPanel.add(sendCycleComboBox);

		c.gridx = 0;
		c.gridy = 11;
		configLayout.setConstraints(linkfailCycleLabel, c);
		oamConfigPanel.add(linkfailCycleLabel);
		c.gridx = 1;
		c.gridy = 11;
		configLayout.setConstraints(linkfailField, c);
		oamConfigPanel.add(linkfailField);
	}

	private void setOamEventPanelLayout() {
		GridBagLayout eventPanelLayout = new GridBagLayout();
		eventPanelLayout.columnWidths = new int[] { 30, 150 };
		eventPanelLayout.columnWeights = new double[] { 0, 0.2 };
		eventPanelLayout.rowHeights = new int[] { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10 };
		eventPanelLayout.rowWeights = new double[] { 0.0, 0.03, 0.03, 0.03, 0.03, 0.03, 0.03, 0.03, 0.03, 0.03, 0.03,
				0.03 };
		oamEventPanel.setLayout(eventPanelLayout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 110);
		eventPanelLayout.setConstraints(eventRecoverButton, c);
		oamEventPanel.add(eventRecoverButton);
		c.insets = new Insets(5, 5, 5, 5);
		c.gridx = 0;
		c.gridy = 1;
		eventPanelLayout.setConstraints(errorSignEventCycleLabel, c);
		oamEventPanel.add(errorSignEventCycleLabel);
		c.gridx = 1;
		c.gridy = 1;
		eventPanelLayout.setConstraints(errorSignEventCycleField, c);
		oamEventPanel.add(errorSignEventCycleField);

		c.gridx = 0;
		c.gridy = 2;
		eventPanelLayout.setConstraints(errorSignEventLimitLabel, c);
		oamEventPanel.add(errorSignEventLimitLabel);
		c.gridx = 1;
		c.gridy = 2;
		eventPanelLayout.setConstraints(errorSignEventLimitField, c);
		oamEventPanel.add(errorSignEventLimitField);

		c.gridx = 0;
		c.gridy = 3;
		eventPanelLayout.setConstraints(errorFrameEventCycleLabel, c);
		oamEventPanel.add(errorFrameEventCycleLabel);
		c.gridx = 1;
		c.gridy = 3;
		eventPanelLayout.setConstraints(errorFrameEventCycleField, c);
		oamEventPanel.add(errorFrameEventCycleField);

		c.gridx = 0;
		c.gridy = 4;
		eventPanelLayout.setConstraints(errorFrameEventLimitLabel, c);
		oamEventPanel.add(errorFrameEventLimitLabel);
		c.gridx = 1;
		c.gridy = 4;
		eventPanelLayout.setConstraints(errorFrameEventLimitField, c);
		oamEventPanel.add(errorFrameEventLimitField);

		c.gridx = 0;
		c.gridy = 5;
		eventPanelLayout.setConstraints(errorFrameCycleEventCycleLabel, c);
		oamEventPanel.add(errorFrameCycleEventCycleLabel);
		c.gridx = 1;
		c.gridy = 5;
		eventPanelLayout.setConstraints(errorFrameCycleEventCycleField, c);
		oamEventPanel.add(errorFrameCycleEventCycleField);

		c.gridx = 0;
		c.gridy = 6;
		eventPanelLayout.setConstraints(errorFrameCycleEventLimitLabel, c);
		oamEventPanel.add(errorFrameCycleEventLimitLabel);
		c.gridx = 1;
		c.gridy = 6;
		eventPanelLayout.setConstraints(errorFrameCycleEventLimitField, c);
		oamEventPanel.add(errorFrameCycleEventLimitField);

		c.gridx = 0;
		c.gridy = 7;
		eventPanelLayout.setConstraints(errorSecondEventCycleLabel, c);
		oamEventPanel.add(errorSecondEventCycleLabel);
		c.gridx = 1;
		c.gridy = 7;
		eventPanelLayout.setConstraints(errorSecondEventCycleField, c);
		oamEventPanel.add(errorSecondEventCycleField);

		c.gridx = 0;
		c.gridy = 8;
		eventPanelLayout.setConstraints(errorSecondEventLimitLabel, c);
		oamEventPanel.add(errorSecondEventLimitLabel);
		c.gridx = 1;
		c.gridy = 8;
		eventPanelLayout.setConstraints(errorSecondEventLimitField, c);
		oamEventPanel.add(errorSecondEventLimitField);

	}

	private void buttonPanelLayout() {
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		buttonPanel.setLayout(flowLayout);
		buttonPanel.add(confirm);
		buttonPanel.add(cancel);
	}

	private void addListener() {
		configRecover.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				workWayComboBox.setSelectedIndex(0);
				loopTimeOutField.setText("5");
				linkEnableCheckBox.setSelected(true);
				oamMessageLengthField.setText("1518");
				organicIdField.setText("0");
				factoryInfoField.setText("0");
				sendCycleComboBox.setSelectedIndex(0);
				linkfailField.setText("5");
				oamEnableCheckBox.setSelected(false);
				remoteLoopCheckBox.setSelected(false);
				variableCheckBox.setSelected(false);
			}
		});
		eventRecoverButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				errorSignEventCycleField.setText("125000000");
				errorSignEventLimitField.setText("1");
				errorFrameEventCycleField.setText("1");
				errorFrameEventLimitField.setText("1");
				errorFrameCycleEventCycleField.setText("14880000");
				errorFrameCycleEventLimitField.setText("1");
				errorSecondEventCycleField.setText("60");
				errorSecondEventLimitField.setText("1");
			}
		});
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		confirm.addActionListener(new MyActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				collectData();
				dispose();

			}

			@Override
			public boolean checking() {

				return true;
			}
		});

	}

	private void intalCombox() {

		try {
			intalPortCombox(portComboBox);
			super.getComboBoxDataUtil().comboBoxData(workWayComboBox, "WORKMODELOFOAM");
			super.getComboBoxDataUtil().comboBoxData(sendCycleComboBox, "CONTRACTPERIOD");
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void intalPortCombox(JComboBox combox) {
		DefaultComboBoxModel boxModel = (DefaultComboBoxModel) combox.getModel();
		PortInst portInst = null;
		PortService_MB portService = null;
		List<PortInst> portList = null;
		OamInfoService_MB oamInfoService = null;
		OamLinkInfo linkInfo = null;
		OamInfo oamInfo = null;
		List<OamInfo> oamInfoList = null;
		try {
			portInst = new PortInst();
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			if (null == this.oamInfo) {
				oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
				linkInfo = new OamLinkInfo();
				oamInfo = new OamInfo();
				portInst.setSiteId(ConstantUtil.siteId);
				portInst.setPortType("UNI");
				portInst.setIsEnabled_code(0);
				portList = portService.select(portInst);
				linkInfo.setSiteId(ConstantUtil.siteId);
				linkInfo.setObjType(EServiceType.LINKOAM.toString());
				oamInfo.setOamLinkInfo(linkInfo);
				oamInfoList = oamInfoService.queryLinkOAMBySiteId(oamInfo);
			} else {
				portInst.setPortId(this.oamInfo.getOamLinkInfo().getObjId());
				portList = portService.select(portInst);
			}

			for (PortInst inst : portList) {
				// 判断端口是否被占用，如果被占用不显示
				if (null != oamInfoList && oamInfoList.size() > 0) {
					for (OamInfo oamInfoEixt : oamInfoList) {
						if (inst.getPortId() != oamInfoEixt.getOamLinkInfo().getObjId()) {
							boxModel.addElement(new ControlKeyValue(inst.getPortId() + "", inst.getPortName(), inst));
						}
					}
				} else {
					boxModel.addElement(new ControlKeyValue(inst.getPortId() + "", inst.getPortName(), inst));
				}
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			portInst = null;
			UiUtil.closeService_MB(portService);
			UiUtil.closeService_MB(oamInfoService);
			portList = null;

		}

	}

	/*
	 * private void intalWorkWayCombox(JComboBox comboBox) {
	 * DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBox
	 * .getModel(); Map<Integer, String> map = new LinkedHashMap<Integer,
	 * String>(); map.put(0,
	 * ResourceUtil.srcStr(StringKeysObj.STRING_INITIATIVE)); map.put(1,
	 * ResourceUtil.srcStr(StringKeysObj.STRING_PASSIVE)); for (Integer key :
	 * map.keySet()) { comboBoxModel .addElement(new ControlKeyValue(key + "",
	 * map.get(key))); } }
	 * 
	 * private void intalSendCycleCombox(JComboBox comboBox) {
	 * DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBox
	 * .getModel(); Map<Integer, String> map = new LinkedHashMap<Integer,
	 * String>(); map.put(0, "1s"); map.put(1, "100ms"); for (Integer key :
	 * map.keySet()) { comboBoxModel .addElement(new ControlKeyValue(key + "",
	 * map.get(key))); }
	 * 
	 * }
	 */
	private void setValue() {
		try {
			OamLinkInfo oamLinkInfo = this.oamInfo.getOamLinkInfo();
			// duankou Combox
			super.getComboBoxDataUtil().comboBoxSelect(portComboBox, oamLinkInfo.getObjId() + "");
			portComboBox.setEnabled(false);
			oamEnableCheckBox.setSelected(oamLinkInfo.isOamEnable());
			super.getComboBoxDataUtil().comboBoxSelect(workWayComboBox, oamLinkInfo.getMode() + "");
			remoteLoopCheckBox.setSelected(oamLinkInfo.getRemoteLoop() == 1 ? true : false);
			loopTimeOutField.setText(oamLinkInfo.getResponseOutTimeThreshold() + "");
			linkEnableCheckBox.setSelected(oamLinkInfo.getLinkEvent() == 1 ? true : false);
			variableCheckBox.setSelected(oamLinkInfo.getMib() == 1 ? true : false);
			oamMessageLengthField.setText(oamLinkInfo.getMaxFrameLength() + "");
			organicIdField.setText(oamLinkInfo.getOrganicId() + "");
			factoryInfoField.setText(oamLinkInfo.getFactoryInfo() + "");
			super.getComboBoxDataUtil().comboBoxSelect(sendCycleComboBox, oamLinkInfo.getSendCycle() + "");
			linkfailField.setText(oamLinkInfo.getLinkfailCycle() + "");

			errorSignEventCycleField.setText(oamLinkInfo.getErrorSymboEventCycle() + "");
			errorSignEventLimitField.setText(oamLinkInfo.getErrorSymboEventThreshold() + "");
			errorFrameEventCycleField.setText(oamLinkInfo.getErrorFrameEventCycle() + "");
			errorFrameEventLimitField.setText(oamLinkInfo.getErrorFrameEventThreshold() + "");
			errorFrameCycleEventCycleField.setText(oamLinkInfo.getErrorFrameCycleEventCycle() + "");
			errorFrameCycleEventLimitField.setText(oamLinkInfo.getErrorFrameCycleEventThreshold() + "");
			errorSecondEventCycleField.setText(oamLinkInfo.getErrorFrameSecondEventCycle() + "");
			errorSecondEventLimitField.setText(oamLinkInfo.getErrorFrameSecondEventThreshold() + "");
			intalPortCombox(portComboBox);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}

	private void collectData() {
		OamLinkInfo oamLinkInfo = null;
		OamInfoService_MB oamInfoService = null;
		DispatchUtil ethLinkOamDispatch = null;
		String result = null;
		if (oamInfo == null) {
			oamInfo = new OamInfo();
		}
		if (oamInfo.getOamLinkInfo() == null) {
			oamLinkInfo = new OamLinkInfo();
		}

		oamLinkInfo = new OamLinkInfo();
		if (null != this.oamInfo.getOamLinkInfo()) {
			oamLinkInfo.setId(this.oamInfo.getOamLinkInfo().getId());
		}
		oamLinkInfo.setObjId(Integer.parseInt(((ControlKeyValue) portComboBox.getSelectedItem()).getId()));
		oamLinkInfo.setObjType(EServiceType.LINKOAM.toString());
		oamLinkInfo.setOamEnable(oamEnableCheckBox.isSelected());
		oamLinkInfo.setMode(Integer.parseInt(((ControlKeyValue) workWayComboBox.getSelectedItem()).getId()));
		oamLinkInfo.setRemoteLoop(remoteLoopCheckBox.isSelected() ? 1 : 0);
		oamLinkInfo.setResponseOutTimeThreshold(Integer.parseInt(loopTimeOutField.getText().trim()));

		oamLinkInfo.setLinkEvent(linkEnableCheckBox.isSelected() ? 1 : 0);
		oamLinkInfo.setMib(variableCheckBox.isSelected() ? 1 : 0);
		oamLinkInfo.setMaxFrameLength(Integer.parseInt(oamMessageLengthField.getText()));
		oamLinkInfo.setOrganicId(Integer.parseInt(organicIdField.getText()));
		oamLinkInfo.setFactoryInfo(Integer.parseInt(factoryInfoField.getText()));
		oamLinkInfo.setSendCycle(Integer.parseInt(((ControlKeyValue) (sendCycleComboBox.getSelectedItem())).getId()));
		oamLinkInfo.setLinkfailCycle(Integer.parseInt(linkfailField.getText()));

		oamLinkInfo.setErrorSymboEventCycle(Integer.parseInt(errorSignEventCycleField.getText()));
		oamLinkInfo.setErrorSymboEventThreshold(Integer.parseInt(errorSignEventLimitField.getText()));
		oamLinkInfo.setErrorFrameEventCycle(Integer.parseInt(errorFrameEventCycleField.getText()));
		oamLinkInfo.setErrorFrameEventThreshold(Integer.parseInt(errorFrameEventLimitField.getText()));
		oamLinkInfo.setErrorFrameCycleEventCycle(Integer.parseInt(errorFrameCycleEventCycleField.getText()));
		oamLinkInfo.setErrorFrameCycleEventThreshold(Integer.parseInt(errorFrameCycleEventLimitField.getText()));
		oamLinkInfo.setErrorFrameSecondEventCycle(Integer.parseInt(errorSecondEventCycleField.getText()));
		oamLinkInfo.setErrorFrameSecondEventThreshold(Integer.parseInt(errorSecondEventLimitField.getText()));
		oamLinkInfo.setSiteId(ConstantUtil.siteId);
		oamInfo.setOamLinkInfo(oamLinkInfo);
		try {
			int operation = 0;
			oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			ethLinkOamDispatch = new DispatchUtil(RmiKeys.RMI_ETHLINKOAMCONFIG);
			if (0 != oamInfo.getId()) {
				result = ethLinkOamDispatch.excuteUpdate(oamInfo);
				operation = EOperationLogType.ETHUNIOAMUPDATE.getValue();
			} else {
				result = ethLinkOamDispatch.excuteInsert(oamInfo);
				operation = EOperationLogType.ETHUNIOAMINSERT.getValue();
			}
			// 添加日志记录
			DialogBoxUtil.succeedDialog(this, result);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(oamInfoService);
		}
	}

	public ETHLinkOAMController getController() {
		return controller;
	}

	public void setController(ETHLinkOAMController controller) {
		this.controller = controller;
	}

}
