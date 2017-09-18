package com.nms.ui.ptn.business.testoam.dialog;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
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
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.OamTypeEnum;
import com.nms.drive.service.impl.CoderUtils;
import com.nms.model.ptn.oam.OamInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
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
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.business.testoam.controller.PwOamBusinessController;

public class PwOamBusinessDialog extends JPanel {
	private PwOamBusinessController controller;
	private static final long serialVersionUID = -1830839435398260186L;
	private JLabel lspIdLabel;
	private JTextField lspIdField;
	private JLabel pwNameLabel;
	private JComboBox pwNameComboBox;
	private JLabel lspTcLabel;
	private JComboBox lspTcComboBox;
	private JLabel pwTcLabel;
	private JComboBox pwTcComboBox;
	private JLabel melLabel;
	private JTextField melTextField;

	private JLabel localLabel;
	private JTextField localField;
	private JLabel remoteLabel;
	private JTextField remoteField;

	// private JLabel megIccLabel;
	// private JTextField megIccField;
	// private JLabel megUmcLabel;
	// private JTextField megUmcField;

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
	private JButton confirm;
	private JButton cancel;

	private JLabel vertifyLabel;

	private OamInfo oamInfo;//按需OAM
	private List<String[]> pwAndLspId = new ArrayList<String[]>();
	private OamInfo oam_temp;//主动OAM
	private OamMepInfo oammepInfoBefore;//记录修改前的数据，便于日志记录
	
	public PwOamBusinessDialog(PwInfo pw, OamInfo oamInfo_temp, TestOamMainDialog mainDialog) {
		initComponent();
		setComponentLayout();
		addListener();
		intalCombox();
		this.confirm = mainDialog.getBtnSave();
		this.cancel = mainDialog.getBtnCanel();
		controller = new PwOamBusinessController(this);
		this.oam_temp = oamInfo_temp;
		//pwName赋值
		this.initPwNameCombox(pw, oamInfo_temp);
		this.getTestOam(pw, oamInfo_temp);
		if (oamInfo != null) {
			try {
				oammepInfoBefore = new OamMepInfo();
				CoderUtils.copy(this.oamInfo.getOamMep(), oammepInfoBefore);
				setValue();
			} catch (Exception e) {
				ExceptionManage.dispose(e,this.getClass());
			}
		} else {
			try {
				//初始化值
				initValue();
			} catch (Exception e) {
				ExceptionManage.dispose(e,this.getClass());
			}
		}
	}

	private void getTestOam(PwInfo pw, OamInfo oam) {
		OamInfoService_MB oamInfoService = null;
		OamInfo oamInfo = null;
		OamMepInfo oamMepInfo = null;
		try {
			oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			oamInfo = new OamInfo();
			oamMepInfo = new OamMepInfo();
			if(oam.getOamMep().getSiteId() == pw.getASiteId()){
				oamMepInfo.setSiteId(pw.getASiteId());
				oamMepInfo.setObjId(pw.getPwId());
			}else{
				oamMepInfo.setSiteId(pw.getZSiteId());
				oamMepInfo.setObjId(pw.getPwId());
			}
			oamMepInfo.setObjType("PW_TEST");
			oamInfo.setOamMep(oamMepInfo);
			oamInfo = oamInfoService.queryByCondition(oamInfo, OamTypeEnum.AMEP);
			if(oamInfo.getOamMep() != null && oamInfo.getOamMep().getId() > 0){
				this.oamInfo = oamInfo;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(oamInfoService);
			oamInfo = null;
			oamMepInfo = null;
		}
	}

	private void initPwNameCombox(PwInfo pw, OamInfo oam) {
		String[] ids = null;
		DefaultComboBoxModel boxModel = null;
		try {
			pwNameComboBox.removeAllItems();
			boxModel = (DefaultComboBoxModel) pwNameComboBox.getModel();
			//把主动OAM的MEG等级,本端维护点Id,远端维护点Id赋给按需OAM对应的属性
			this.melTextField.setText(oam.getOamMep().getMel()+"");
			this.localField.setText(oam.getOamMep().getLocalMepId()+"");
			this.remoteField.setText(oam.getOamMep().getRemoteMepId()+"");
			ids = new String[2];
			ids[0] = pw.getPwName();
			ids[1] = pw.getTunnelId()+"";
			pwAndLspId.add(ids);
			boxModel.addElement(new ControlKeyValue( pw.getPwId() + "", pw.getPwName(), pw));
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			ids = null;
			boxModel = null;
		}
	}
	
//	private String getLspId(int id){
//		TunnelService tunnelService = null;
//		Tunnel tunnel = null;
//		int tunnelServiceId = 0;
//		try {
//			tunnelService = (TunnelService) ConstantUtil.serviceFactory
//					.newService(Services.Tunnel);
//			tunnel = new Tunnel();
//			tunnel.setTunnelId(id);
//			tunnel = tunnelService.select_nojoin(tunnel).get(0);
//			tunnelServiceId = tunnel.getLspParticularList().get(0)
//			.getASiteId() == ConstantUtil.siteId ? tunnel.getLspParticularList().get(0).getAlspbusinessid() :
//				 tunnel.getLspParticularList().get(0).getZlspbusinessid();
//			lspIdField.setText(tunnelServiceId + "");
//
//		} catch (Exception e) {
//			ExceptionManage.dispose(e,this.getClass());
//		} finally {
//			tunnelService = null;
//			tunnel = null;
//		}
//		return tunnelServiceId+"";
//	}

	private void initValue() {
		lspIdField.setEditable(false);
		comboBoxSelect(lspTcComboBox, "7");
		comboBoxSelect(pwTcComboBox, "7");
		lckCheckBox.setSelected(false);
//		comboBoxSelect(melTextField, "7");
		if(melTextField.getText() == null || melTextField.getText().equals("")){
			melTextField.setText("7");
		}
//		if (mepInfo.isRingEnable()) {
		loopEnableCheckBox.setSelected(false);
		localField.setText("1");
		remoteField.setText("1");
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

//		}
//		if (mepInfo.isTstEnable()) {
		tstEnableCheckBox.setSelected(false);
		comboBoxSelect(tstPeriodComboBox, "1");
		comboBoxSelect(tstTlvTypeComboBox, "11");
		tstTLVLengthField.setText("21");

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
		lbTTLField.setText("64");
	}

	private void initComponent() {
		vertifyLabel = new JLabel();
		vertifyLabel.setForeground(Color.red);
//		this.setTitle(ResourceUtil.srcStr(StringKeysObj.STRING_PW_OAM_CONFIG));
		componentPanel = new JPanel();
		lspIdLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LSP_ID));
		lspIdField = new JTextField();
		pwNameLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PW_NAME));
		pwNameComboBox = new JComboBox();
		// megIccLabel = new JLabel("MEG ICC");
		// megIccField = new JTextField();
		// megUmcLabel = new JLabel("MEG UMC");
		// megUmcField = new JTextField();

		localLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_LOCAL_POINT_ID));
		localField = new JTextField();
//		localField.setEditable(false);
		remoteLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_FAR_POINT_ID));
		remoteField = new JTextField();
//		remoteField.setEditable(false);
		melLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_MEG_LEVLE));
		melTextField = new JTextField();
		melTextField.setEditable(false);
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
		lspTcLabel = new JLabel("LSP TC");
		lspTcComboBox = new JComboBox();
		lmEnableLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_LM_SEND_ENABLED));
		pwTcLabel = new JLabel("PW TC");
		pwTcComboBox = new JComboBox();
		lmCheckBox = new JCheckBox();
		lmCycleLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_LM_SEND_PERIOD));
		lmCycleComboBox = new JComboBox();
		dmEnableLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_DM_SEND_ENABLED));
		dmCheckBox = new JCheckBox();
		dmCycleLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_DM_SEND_PERIOD));
		dmCycleComboBox = new JComboBox();
		lbTTlLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LBTTL));
		lbTTLField = new JTextField();
		
		buttonPanel = new JPanel();
//		confirm = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM));
//		cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		try {
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

		setAttribute();

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
		componentLayout.rowHeights = new int[] { 10, 10, 10, 10, 10, 10, 10,
				10, 10, 10, 10, 10, 10 };
		componentLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		componentPanel.setLayout(componentLayout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(pwNameLabel, c);
		componentPanel.add(pwNameLabel);
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(pwNameComboBox, c);
		componentPanel.add(pwNameComboBox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(lspIdLabel, c);
		componentPanel.add(lspIdLabel);
		c.gridx = 3;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(lspIdField, c);
		componentPanel.add(lspIdField);

		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(lspTcLabel, c);
		componentPanel.add(lspTcLabel);
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(lspTcComboBox, c);
		componentPanel.add(lspTcComboBox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(pwTcLabel, c);
		componentPanel.add(pwTcLabel);
		c.gridx = 3;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(pwTcComboBox, c);
		componentPanel.add(pwTcComboBox);

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
		componentLayout.setConstraints(lckLabel, c);
		componentPanel.add(lckLabel);
		c.gridx = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(lckCheckBox, c);
		componentPanel.add(lckCheckBox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(melLabel, c);
		componentPanel.add(melLabel);
		c.gridx = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(melTextField, c);
		componentPanel.add(melTextField);

		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(loopEnableLabel, c);
		componentPanel.add(loopEnableLabel);
		c.gridx = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(loopEnableCheckBox, c);
		componentPanel.add(loopEnableCheckBox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(loopPeriodLabel, c);
		componentPanel.add(loopPeriodLabel);
		c.gridx = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(loopPeriodComboBox, c);
		componentPanel.add(loopPeriodComboBox);

		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(loopTestWayLabel, c);
		componentPanel.add(loopTestWayLabel);
		c.gridx = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(loopTestWayCombox, c);
		componentPanel.add(loopTestWayCombox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(loopOffLineTestTLVLabel, c);
		componentPanel.add(loopOffLineTestTLVLabel);
		c.gridx = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(loopOffLineTestTLVCombox, c);
		componentPanel.add(loopOffLineTestTLVCombox);

		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(loopTlvLengthLabel, c);
		componentPanel.add(loopTlvLengthLabel);
		c.gridx = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(loopTlvLengthField, c);
		componentPanel.add(loopTlvLengthField);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(loopTLVInfoLabel, c);
		componentPanel.add(loopTLVInfoLabel);
		c.gridx = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(loopTLVInfoField, c);
		componentPanel.add(loopTLVInfoField);

		c.gridx = 0;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(tstEnableLabel, c);
		componentPanel.add(tstEnableLabel);
		c.gridx = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(tstEnableCheckBox, c);
		componentPanel.add(tstEnableCheckBox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(tstPeriodLabel, c);
		componentPanel.add(tstPeriodLabel);
		c.gridx = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(tstPeriodComboBox, c);
		componentPanel.add(tstPeriodComboBox);

		c.gridx = 0;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(tstTlvTypeLabel, c);
		componentPanel.add(tstTlvTypeLabel);
		c.gridx = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(tstTlvTypeComboBox, c);
		componentPanel.add(tstTlvTypeComboBox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(tstTLCLengthLabel, c);
		componentPanel.add(tstTLCLengthLabel);
		c.gridx = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(tstTLVLengthField, c);
		componentPanel.add(tstTLVLengthField);

		c.gridx = 0;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(lmEnableLabel, c);
		componentPanel.add(lmEnableLabel);
		c.gridx = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(lmCheckBox, c);
		componentPanel.add(lmCheckBox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(lmCycleLabel, c);
		componentPanel.add(lmCycleLabel);
		c.gridx = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(lmCycleComboBox, c);
		componentPanel.add(lmCycleComboBox);

		c.gridx = 0;
		c.gridy = 10;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(dmEnableLabel, c);
		componentPanel.add(dmEnableLabel);
		c.gridx = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(dmCheckBox, c);
		componentPanel.add(dmCheckBox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(dmCycleLabel, c);
		componentPanel.add(dmCycleLabel);
		c.gridx = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(dmCycleComboBox, c);
		componentPanel.add(dmCycleComboBox);

		c.gridx = 0;
		c.gridy = 11;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(lbTTlLabel, c);
		componentPanel.add(lbTTlLabel);
		c.gridx = 1;
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
//		buttonPanel.add(confirm);
//		buttonPanel.add(cancel);
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
//		confirm.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				try {
//					if(pwNameComboBox.getSelectedItem() == null){
//						tip();
//					}else{
//						if ((loopEnableCheckBox.isSelected() == true)
//								&& (loopTlvLengthField.getText().length() == 0 || loopTLVInfoField
//										.getText().length() == 0)) {
//							vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_FULL_DATA));
//							return;
//						}
//						if ((tstEnableCheckBox.isSelected() == true)
//								&& (tstTLVLengthField.getText().length() == 0)) {
//							vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_FULL_DATA));
//							return;
//						}
//						collectData();
////						List<OamInfo> oamInfoList = new ArrayList<OamInfo>();
////						oamInfoList.add(oamInfo);
//						DispatchUtil dispath = new DispatchUtil(RmiKeys.RMI_TMCOAMCONFIG);
//						if(oamInfo.getOamMep().getId() > 0){
//							dispath.excuteUpdate(oamInfo);
//						}else{
//							dispath.excuteInsert(oamInfo);
//						}
//
//						DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
//						dispose();
//					}
//
//				} catch (Exception e) {
//					ExceptionManage.dispose(e,this.getClass());
//					dispose();
//				}
//
//			}
//		});
//		cancel.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				dispose();
//			}
//		});

		pwNameComboBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == 1){
					String pwName = pwNameComboBox.getSelectedItem().toString();
					for (String[] s : pwAndLspId) {
						if(pwName.equals(s[0])){
							lspIdField.setText(s[1]+"");
						}
					}
//					String lspName = getlspName();
//					lspNameField.setText(lspName);
				}
				
			}
		});
	}
	
	/**
	 * 设置属性
	 */
	private void setAttribute() {
		// megIccField.setEditable(false);
		// megUmcField.setEditable(false);
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

	private void intalLspIdFile(JTextField lspIdField) throws Exception {
		PwInfo pwInfo = null;
		PwInfoService_MB pwInfoService = null;
		TunnelService_MB tunnelService = null;
		Tunnel tunnel = null;
		try {
			pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			pwInfo = new PwInfo();
			pwInfo.setPwId(oamInfo.getOamMep().getObjId());
			pwInfo = pwInfoService.selectBypwid_notjoin(pwInfo);
			tunnel = new Tunnel();
			tunnel.setTunnelId(pwInfo.getTunnelId());
			tunnel = tunnelService.select_nojoin(tunnel).get(0);
//			int tunnelServiceId = tunnel.getLspParticularList().get(0)
//			.getASiteId() == ConstantUtil.siteId ? tunnel.getLspParticularList().get(0).getAlspbusinessid() :
//				 tunnel.getLspParticularList().get(0).getZlspbusinessid();
			lspIdField.setText(tunnel.getTunnelId() + "");

		} catch (Exception e) {
			throw e;
		} finally {
			pwInfo = null;
			UiUtil.closeService_MB(tunnelService);
			UiUtil.closeService_MB(pwInfoService);
			tunnel = null;
		}
	}

	private void setValue() throws Exception {
		OamMepInfo mepInfo = oamInfo.getOamMep();

		intalLspIdFile(lspIdField);
		lspIdField.setEditable(false);
//		pwNameComboBox.setText(oamInfo.getOamMep().getObjId() + "");
		PwInfo pw = this.getpwName(mepInfo);
		comboBoxSelect(pwNameComboBox, oamInfo.getOamMep().getObjId() + "");
		DefaultComboBoxModel boxModel = (DefaultComboBoxModel) pwNameComboBox.getModel();
		boxModel.addElement(new ControlKeyValue(pw.getPwId() + "", pw.getPwName(), pw));
		pwNameComboBox.setModel(boxModel);
		pwNameComboBox.setEnabled(false);
		this.initValueToId();
		localField.setText(mepInfo.getLocalMepId()+"");
		remoteField.setText(mepInfo.getRemoteMepId()+"");
		comboBoxSelect(lspTcComboBox, mepInfo.getLspTc() + "");
		comboBoxSelect(pwTcComboBox, mepInfo.getPwTc() + "");
		lckCheckBox.setSelected(mepInfo.isLck());
		melTextField.setText(mepInfo.getMel() + "");
//		comboBoxSelect(melComboBox,mepInfo.getMel()+"");
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
			oamMepInfo.setSiteId(this.oamInfo.getOamMep().getSiteId());
			oamMepInfo.setServiceId(Integer.parseInt(((ControlKeyValue)this.pwNameComboBox.getSelectedItem()).getId()));
			oamMepInfo.setObjType("PW");
			oamInfo.setOamMep(oamMepInfo);
			oamInfo = oamInfoService.queryByCondition(oamInfo, OamTypeEnum.AMEP);
			if(oamInfo != null && oamInfo.getOamMep() != null){
				melTextField.setText(oamInfo.getOamMep().getMel() + "");
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(oamInfoService);
		}
	}

	/**
	 * 通过pwId获取pwName
	 * @param mepInfo
	 * @return
	 */
	private PwInfo getpwName(OamMepInfo mepInfo) {
		PwInfoService_MB service = null;
		PwInfo pw = null;
		try {
			service = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			pw = new PwInfo();
			pw.setPwId(mepInfo.getObjId());
			pw = service.queryByPwId(pw);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
		return pw;
	}

	private void intalCombox() {
		intalCycleCombox(loopPeriodComboBox);
		intalCycleTestWayCombox(loopTestWayCombox);
		intalTVLTypeCombox(loopOffLineTestTLVCombox);
		intalCycleCombox(tstPeriodComboBox);
		intalTVLTypeCombox(tstTlvTypeComboBox);
		intalLmAndDmCombox(lmCycleComboBox);
		intalLmAndDmCombox(dmCycleComboBox);
		intalTcAndMel(lspTcComboBox);
		intalTcAndMel(pwTcComboBox);

	}

	private void intalCycleTestWayCombox(JComboBox combox) {
		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		map.put(0, ResourceUtil.srcStr(StringKeysObj.STRING_ONLINE));
		map.put(1, ResourceUtil.srcStr(StringKeysObj.STRING_OFFLINE));
		DefaultComboBoxModel boxModel = (DefaultComboBoxModel) combox
				.getModel();
		for (Integer key : map.keySet()) {
			boxModel.addElement((new ControlKeyValue(key.toString(), map
					.get(key))));
		}
	}

	private void intalCycleCombox(JComboBox combox) {

		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		map.put(1, "3.33ms");
		map.put(10, "10ms");
		map.put(11, "100ms");
		map.put(100, "1s");
		DefaultComboBoxModel boxModel = (DefaultComboBoxModel) combox
				.getModel();
		for (Integer key : map.keySet()) {
			boxModel.addElement((new ControlKeyValue(key.toString(), map
					.get(key))));
		}

	}

	private void intalTVLTypeCombox(JComboBox combox) {

		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		map.put(01, ResourceUtil.srcStr(StringKeysObj.STRING_ALL_0));
		map.put(11, ResourceUtil.srcStr(StringKeysObj.STRING_RANDOM));
		DefaultComboBoxModel boxModel = (DefaultComboBoxModel) combox
				.getModel();
		for (Integer key : map.keySet()) {
			boxModel.addElement((new ControlKeyValue(key.toString(), map
					.get(key))));
		}

	}

	private void intalLmAndDmCombox(JComboBox combox) {

		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		map.put(0, "1s");
		map.put(1, "100ms");
		DefaultComboBoxModel boxModel = (DefaultComboBoxModel) combox
				.getModel();
		for (Integer key : map.keySet()) {
			boxModel.addElement((new ControlKeyValue(key.toString(), map
					.get(key))));
		}

	}

	private void intalTcAndMel(JComboBox combox) {
		DefaultComboBoxModel boxModel = (DefaultComboBoxModel) combox
				.getModel();
		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		for (int i = 0; i < 8; i++) {
			map.put(i, i + "");
		}

		for (Integer key : map.keySet()) {
			boxModel.addElement((new ControlKeyValue(key.toString(), map
					.get(key))));
		}

	}

	public String collectData() throws Exception {
		OamMepInfo oamMep = null;
		if (oamInfo == null) {
			oamInfo = new OamInfo();
			oamMep = new OamMepInfo();
			oamInfo.setOamMep(oamMep);
		} else {
			oamMep = oamInfo.getOamMep();
		}
		oamInfo.setOamType(OamTypeEnum.AMEP);
		oamMep.setObjType("PW_TEST");
		oamMep.setObjNameLog(((ControlKeyValue) (pwNameComboBox.getSelectedItem())).getName());
		oamMep.setObjId((Integer.parseInt(((ControlKeyValue) (pwNameComboBox.getSelectedItem())).getId())));
		oamMep.setLspTc(Integer.parseInt(((ControlKeyValue) (lspTcComboBox
				.getSelectedItem())).getId()));
		oamMep.setLbTTL(Integer.parseInt(lbTTLField.getText().trim()));
		oamMep.setMel(Integer.parseInt(melTextField.getText().trim()));
		oamMep.setPwTc(Integer.parseInt(((ControlKeyValue) (pwTcComboBox
				.getSelectedItem())).getId()));
		oamMep.setLocalMepId(Integer.parseInt(localField.getText().trim()));
		oamMep.setRemoteMepId(Integer.parseInt(remoteField.getText().trim()));
		oamMep.setRingEnable(loopEnableCheckBox.isSelected() ? true : false);
		if (loopEnableCheckBox.isSelected() || oamInfo.getId()>0) {
			oamMep.setRingCycle(Integer
					.parseInt(((ControlKeyValue) (loopPeriodComboBox
							.getSelectedItem())).getId()));
			oamMep.setRingTestWay(Integer
					.parseInt(((ControlKeyValue) (loopTestWayCombox
							.getSelectedItem())).getId()));
			oamMep.setOffLineTestTLV(Integer
					.parseInt(((ControlKeyValue) (loopOffLineTestTLVCombox
							.getSelectedItem())).getId()));
			if (loopTlvLengthField.getText().trim() != null)
				oamMep.setRingTLVLength(Integer.parseInt(loopTlvLengthField
						.getText().trim()));
			if (loopTLVInfoField.getText().trim() != null)
				oamMep.setRingTLVInfo(Integer.parseInt(loopTLVInfoField
						.getText().trim()));
		}else{
			oamMep.setRingCycle(1);
			oamMep.setRingTestWay(0);
			oamMep.setOffLineTestTLV(11);
			oamMep.setRingTLVLength(1);
			oamMep.setRingTLVInfo(0);
		}
		
		oamMep.setTstEnable(tstEnableCheckBox.isSelected() ? true : false);
		if (tstEnableCheckBox.isSelected() || oamInfo.getId()>0) {
			oamMep.setTstCycle(Integer
					.parseInt(((ControlKeyValue) (tstPeriodComboBox
							.getSelectedItem())).getId()));
			oamMep.setTstTLVType(Integer
					.parseInt(((ControlKeyValue) (tstTlvTypeComboBox
							.getSelectedItem())).getId()));
			if (tstTLVLengthField.getText().trim() != null)
				oamMep.setTstTLVLength(Integer.parseInt(tstTLVLengthField
						.getText().trim()));
		}else{
			oamMep.setTstCycle(1);
			oamMep.setTstTLVType(11);
			oamMep.setTstTLVLength(21);
		}
		
		oamMep.setLck(lckCheckBox.isSelected() ? true : false);
		oamMep.setLm(lmCheckBox.isSelected() ? true : false);
		if (lmCheckBox.isSelected() || oamInfo.getId()>0) {
			oamMep.setLmCycle(Integer
					.parseInt(((ControlKeyValue) (lmCycleComboBox
							.getSelectedItem())).getId()));
		}else{
			oamMep.setLmCycle(0);
		}
		
		oamMep.setDm(dmCheckBox.isSelected());
		if (dmCheckBox.isSelected() || oamInfo.getId()>0){
			oamMep.setDmCycle(Integer.parseInt(((ControlKeyValue) (dmCycleComboBox.getSelectedItem())).getId()));
		}else{
			oamMep.setDmCycle(0);
		}
		
		oamInfo.getOamMep().setSiteId(this.oam_temp.getOamMep().getSiteId());

		//下发数据
		String result = null;
		DispatchUtil dispath = new DispatchUtil(RmiKeys.RMI_TMCOAMCONFIG);
		if(oamInfo.getOamMep().getId() > 0){
			result = dispath.excuteUpdate(oamInfo);
			oammepInfoBefore.setObjNameLog(oamInfo.getOamMep().getObjNameLog());
			oammepInfoBefore.setLmCycle(oammepInfoBefore.getLmCycle()==0?100:11);
			oammepInfoBefore.setDmCycle(oammepInfoBefore.getDmCycle()==0?100:11);
			oammepInfoBefore.setOffLineTestTLV(oammepInfoBefore.getOffLineTestTLV()==1?0:1);
			oammepInfoBefore.setTstTLVType(oammepInfoBefore.getTstTLVType()==1?0:1);
		}else{
			result = dispath.excuteInsert(oamInfo);
		}
		oamInfo.getOamMep().setLmCycle(oamInfo.getOamMep().getLmCycle()==0?100:11);
		oamInfo.getOamMep().setDmCycle(oamInfo.getOamMep().getDmCycle()==0?100:11);
		oamInfo.getOamMep().setOffLineTestTLV(oamInfo.getOamMep().getOffLineTestTLV()==1?0:1);
		oamInfo.getOamMep().setTstTLVType(oamInfo.getOamMep().getTstTLVType()==1?0:1);
		AddOperateLog.insertOperLog(confirm, EOperationLogType.TESTOAMPWUPDATE.getValue(), result, 
				oammepInfoBefore, oamInfo.getOamMep(), oamInfo.getOamMep().getSiteId(), oamInfo.getOamMep().getObjNameLog(), "testPwOAM");
		return result;
		// // -------megICC megUcc 赋值
		// oamInfoService = (OamInfoService) ConstantUtil.serviceFactory
		// .newService(Services.OamInfo);
		// String megId = oamInfoService.generateMegId();
		// oamMep.setMegIcc(megId.substring(0, megId.toString().indexOf("*")));
		// oamMep.setMegUmc(megId.substring(megId.lastIndexOf("*") + 1, megId
		// .length()));

	}

	public void comboBoxSelect(JComboBox jComboBox, String selectId) {
		for (int i = 0; i < jComboBox.getItemCount(); i++) {
			if (((ControlKeyValue) jComboBox.getItemAt(i)).getId().equals(
					selectId)) {
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

	public JButton getConfirm() {
		return confirm;
	}

	public JButton getCancel() {
		return cancel;
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

	public PwOamBusinessController getController() {
		return controller;
	}

	public void setController(PwOamBusinessController controller) {
		this.controller = controller;
	}

	public JTextField getMelField() {
		return melTextField;
	}

	public void setMelField(JTextField melTextField) {
		this.melTextField = melTextField;
	}

	public JTextField getLocalField() {
		return localField;
	}

	public JTextField getRemoteField() {
		return remoteField;
	}

	public JTextField getLbTTLField() {
		return lbTTLField;
	}

	public void setLbTTLField(JTextField lbTTLField) {
		this.lbTTLField = lbTTLField;
	}

	public boolean checkIsFull() {
		if(pwNameComboBox.getSelectedItem() == null){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NO_PW));
			return false;
		}else{
			if ((loopEnableCheckBox.isSelected() == true)
					&& (loopTlvLengthField.getText().length() == 0 || loopTLVInfoField
							.getText().length() == 0)) {
				vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_FULL_DATA));
				return false;
			}
			if ((tstEnableCheckBox.isSelected() == true)
					&& (tstTLVLengthField.getText().length() == 0)) {
				vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_FULL_DATA));
				return false;
			}
			if(checkData()){
				return false;
			}
		}
		return true;
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
}
