﻿/*
 * addPwNniBuffer.java
 *
 * Created on __DATE__, __TIME__
 */

package com.nms.ui.ptn.ne.pwnni.view;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.pw.PwNniInfo;
import com.nms.db.bean.system.code.Code;
import com.nms.db.bean.system.code.CodeGroup;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.ptn.path.pw.PwNniInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
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
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.business.pw.PwBusinessPanel;

/**
 * 
 * @author __USER__
 */
public class AddPwNniBuffer extends PtnDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 751147116397503551L;
	private PwNniInfo pwNniBuffer = null;
	private PwInfo pwInfo = null;
	
	/** Creates new form addPwNniBuffer */
	public AddPwNniBuffer(java.awt.Frame parent, boolean modal) {
		initComponents();
		addMyListner();
	}

	public AddPwNniBuffer(boolean b, PwInfo info) {
		this.setModal(b);
		super.setTitle(ResourceUtil.srcStr(StringKeysPanel.PANEL_PW_PORT_CONFIGE));
		initComponents();
		setLayout();
		addMyListner();
		pwInfo = info;
//			if (info.getASiteId() == ConstantUtil.siteId) {
			this.pwNniBuffer = pwInfo.getaPwNniInfo();// 后期会分A,Z端
//			} else {
//				this.pwNniBuffer = pwInfo.getzPwNniInfo();// 后期会分A,Z端
//			}
		try {
			super.getComboBoxDataUtil().comboBoxData(jComboBox3, "TAGRECOGNITION");
			super.getComboBoxDataUtil().comboBoxData(jComboBox4, "PORTTAGBEHAVIOR");
			super.getComboBoxDataUtil().comboBoxData(jComboBox5, "MACLEARN");
			super.getComboBoxDataUtil().comboBoxData(jComboBox6, "VCTRAFFICPOLICING");
			super.getComboBoxDataUtil().comboBoxData(jComboBoxcontrolEnabl, "ENABLEDSTATUE");
			intialPortComBox(jComboBox2, ConstantUtil.siteId);

			if (this.pwNniBuffer != null) {
				// jLabel10.setText("修改流配置");
				bindPortCombox(jComboBox2, pwNniBuffer);
				jComboBox2.setEnabled(false);
				jTextField1.setText(pwNniBuffer.getName());
				super.getComboBoxDataUtil().comboBoxSelect(jComboBox3, this.pwNniBuffer.getTagAction() + "");
				super.getComboBoxDataUtil().comboBoxSelect(jComboBox4, this.pwNniBuffer.getExitRule() + "");
				jTextField2.setText(String.valueOf(this.pwNniBuffer.getSvlan()));
				jTextField3.setText(String.valueOf(this.pwNniBuffer.getVlanpri()));
				super.getComboBoxDataUtil().comboBoxSelect(jComboBox5, this.pwNniBuffer.getMacAddressLearn() + "");
				super.getComboBoxDataUtil().comboBoxSelect(jComboBox6, this.pwNniBuffer.getHorizontalDivision() + "");
				super.getComboBoxDataUtil().comboBoxSelect(jComboBoxcontrolEnabl, this.pwNniBuffer.getControlEnable() + "");

			} else {
				jTextField1.setText("");
				this.comboBoxSelect(jComboBox3, "TAGRECOGNITION", 0);
				this.comboBoxSelect(jComboBox4, "PORTTAGBEHAVIOR", 0);
				jTextField2.setText("0");
				jTextField3.setText("0");
				this.comboBoxSelect(jComboBox5, "MACLEARN", 0);
				this.comboBoxSelect(jComboBox6, "VCTRAFFICPOLICING", 0);
				this.comboBoxSelect(jComboBoxcontrolEnabl, "ENABLEDSTATUE", 1);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void bindPortCombox(JComboBox jComboBox, PwNniInfo pwNni) {
		PortService_MB portService = null;
		PortInst port = null;
		DefaultComboBoxModel nniDefaultComboBoxModel = null;
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			nniDefaultComboBoxModel = (DefaultComboBoxModel) jComboBox.getModel();

			port = new PortInst();
			// port.setPortId(pwNni.getPortId());
			port.setSiteId(pwNni.getSiteId());
			port = portService.select(port).get(0);
			nniDefaultComboBoxModel.setSelectedItem(new ControlKeyValue(port.getPortId() + "", port.getPortName() + "/" + port.getNumber(), port));
			jComboBox.setModel(nniDefaultComboBoxModel);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(portService);
			port = null;
			nniDefaultComboBoxModel = null;
		}
	}

	private void intialPortComBox(JComboBox jComboBox, int siteId) {
		PortService_MB portService = null;
		TunnelService_MB tunnelService = null;
		PortInst port = null;
		DefaultComboBoxModel nniDefaultComboBoxModel = null;

		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			List<Integer> portIds = tunnelService.getPortIdsBySiteId(siteId);

			nniDefaultComboBoxModel = (DefaultComboBoxModel) jComboBox.getModel();
			for (int portId : portIds) {
				port = new PortInst();
				port.setPortId(portId);
				port.setSiteId(siteId);
				port.setPortType("NNI");
				port = portService.select(port).get(0);
				nniDefaultComboBoxModel.addElement(new ControlKeyValue(port.getPortId() + "", port.getPortName(), port));
			}

			jComboBox.setModel(nniDefaultComboBoxModel);

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(portService);
			UiUtil.closeService_MB(tunnelService);
		}
	}

	private void addMyListner() {
		this.jComboBox4.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == 1){
					int id = ((Code)((ControlKeyValue)e.getItem()).getObject()).getId();
					if(id == 567 || id == 568){
						jLabel7.setText(ResourceUtil.srcStr(StringKeysLbl.LBL_REPLACE_VLAN_PRI_UP));//上话替换VLAN PRI
						jLabel6.setText(ResourceUtil.srcStr(StringKeysLbl.LBL_REPLACE_VLAN_ID_UP));//上话替换VLAN ID
					}else{
						jLabel7.setText(ResourceUtil.srcStr(StringKeysLbl.LBL_ADD_VLANPRI_UP));
						jLabel6.setText(ResourceUtil.srcStr(StringKeysLbl.LBL_ADD_VLANID_UP));
					}
				}
			}
		});
		
		Confirm.addActionListener(new MyActionListener() {
			@Override
			public boolean checking() {
				return true;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				ConfirmActionPerformed(e);
				
			}
		});

		Cancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				CancelActionPerformed(evt);
			}
		});
	}

	private void comboBoxSelect(JComboBox jComboBox, String identity, int value) throws Exception {

		CodeGroup codeGroup = null;
		List<Code> codeList = null;
		DefaultComboBoxModel defaultComboBoxModel = null;
		try {
			codeGroup = UiUtil.getCodeGroupByIdentity(identity);
			codeList = codeGroup.getCodeList();
			for (Code obj : codeList) {
				if (Integer.valueOf(obj.getCodeValue()) == value) {
					defaultComboBoxModel = (DefaultComboBoxModel) jComboBox.getModel();
					defaultComboBoxModel.setSelectedItem(new ControlKeyValue(obj.getId() + "", obj.getCodeName(), obj));
					jComboBox.setModel(defaultComboBoxModel);
					break;
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			codeList = null;
			defaultComboBoxModel = null;
		}
	}

	private void initialComp() {

	}

	private void initComponents() {

		mainPanel = new JPanel();
		buttonPanel = new JPanel();
		jLabel2 = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_NAME));
		jComboBox2 = new javax.swing.JComboBox();
		jLabel3 = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_STREAM_NAME));
		jTextField1 = new javax.swing.JTextField();
		jLabel4 = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TAG_DOWM_WUHAN));
		jLabel5 = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TAG_ACTION_WUHAN));
		jLabel6 = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ADD_VLANID_UP));
		jLabel7 = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ADD_VLANPRI_UP));
		jLabel8 = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MAC_LEARN));
		jLabel9 = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_HORIZONAL));
		jComboBox3 = new javax.swing.JComboBox();
		// jLabel10 = new javax.swing.JLabel("创建流配置");
		jComboBox4 = new javax.swing.JComboBox();
		jTextField2 = new javax.swing.JTextField();
		jTextField3 = new javax.swing.JTextField();
		jComboBox5 = new javax.swing.JComboBox();
		jComboBox6 = new javax.swing.JComboBox();
		Confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE));
		Cancel = new javax.swing.JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		jComboBoxcontrolEnabl = new JComboBox();
		jLabelcontrolEnabl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CONTROLENABLE));
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

	}

	public void addComponent(JPanel panel, JComponent component, int gridx, int gridy, double weightx, double weighty, int gridwidth, int gridheight, int fill, Insets insets, int anchor, GridBagConstraints gridBagConstraints) {
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;
		gridBagConstraints.weightx = weightx;
		gridBagConstraints.weighty = weighty;
		gridBagConstraints.gridwidth = gridwidth;
		gridBagConstraints.gridheight = gridheight;
		gridBagConstraints.fill = fill;
		gridBagConstraints.insets = insets;
		gridBagConstraints.anchor = anchor;
		panel.add(component, gridBagConstraints);
	}

	@SuppressWarnings("static-access")
	private void setLayout() {

		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 50, 50, 50, 140 };
		layout.columnWeights = new double[] { 0, 0, 0, 0 };
		layout.rowHeights = new int[] { 30, 30, 30, 30, 30, 30 };
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0.2 };

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		mainPanel.setLayout(layout);
		// addComponent(mainPanel, jLabel10, 0, 0, 1.0, 0.001, 1, 1, gridBagConstraints.BOTH, new Insets(20, 80, 10, 5), gridBagConstraints.NORTHWEST, gridBagConstraints);

		// addComponent(mainPanel, jLabel2, 0, 0, 1.0, 0.001, 1, 1, gridBagConstraints.BOTH, new Insets(20, 80, 10, 5), gridBagConstraints.NORTHWEST, gridBagConstraints);
		// addComponent(mainPanel, jComboBox2, 1, 0, 1.0, 0.001, 1, 1, gridBagConstraints.BOTH, new Insets(20, 10, 10, 20), gridBagConstraints.NORTHWEST, gridBagConstraints);

		// addComponent(mainPanel, jLabel3, 0, 1, 1.0, 0.001, 1, 1, gridBagConstraints.BOTH, new Insets(10, 80, 10, 5), gridBagConstraints.NORTHWEST, gridBagConstraints);
		// addComponent(mainPanel, jTextField1, 1, 1, 1.0, 0.001, 1, 1, gridBagConstraints.BOTH, new Insets(10, 10, 10, 20), gridBagConstraints.NORTHWEST, gridBagConstraints);

		addComponent(mainPanel, jLabelcontrolEnabl, 0, 0, 1.0, 0.001, 1, 1, gridBagConstraints.BOTH, new Insets(50, 80, 5, 5), gridBagConstraints.NORTHWEST, gridBagConstraints);
		addComponent(mainPanel, jComboBoxcontrolEnabl, 1, 0, 1.0, 0.001, 1, 1, gridBagConstraints.BOTH, new Insets(50, 5, 5, 20), gridBagConstraints.NORTHWEST, gridBagConstraints);
		
		addComponent(mainPanel, jLabel6, 2, 0, 1.0, 0.001, 1, 1, gridBagConstraints.BOTH, new Insets(50, 50, 5, 5), gridBagConstraints.NORTHWEST, gridBagConstraints);
		addComponent(mainPanel, jTextField2, 3, 0, 1.0, 0.001, 1, 1, gridBagConstraints.BOTH, new Insets(50, 5, 5, 75), gridBagConstraints.NORTHWEST, gridBagConstraints);
		
		
		addComponent(mainPanel, jLabel9, 0, 1, 1.0, 0.001, 1, 1, gridBagConstraints.BOTH, new Insets(10, 80, 5, 5), gridBagConstraints.NORTHWEST, gridBagConstraints);
		addComponent(mainPanel, jComboBox6, 1, 1, 1.0, 0.001, 1, 1, gridBagConstraints.BOTH, new Insets(10, 5, 5, 20), gridBagConstraints.NORTHWEST, gridBagConstraints);
		
		addComponent(mainPanel, jLabel7, 2, 1, 1.0, 0.001, 1, 1, gridBagConstraints.BOTH, new Insets(10, 50, 5, 5), gridBagConstraints.NORTHWEST, gridBagConstraints);
		addComponent(mainPanel, jTextField3, 3, 1, 1.0, 0.001, 1, 1, gridBagConstraints.BOTH, new Insets(10, 5, 5, 75), gridBagConstraints.NORTHWEST, gridBagConstraints);
		
		
		addComponent(mainPanel, jLabel4, 0, 2, 1.0, 0.001, 1, 1, gridBagConstraints.BOTH, new Insets(10, 80, 5, 5), gridBagConstraints.NORTHWEST, gridBagConstraints);
		addComponent(mainPanel, jComboBox3, 1, 2, 1.0, 0.001, 1, 1, gridBagConstraints.BOTH, new Insets(10, 5, 5, 20), gridBagConstraints.NORTHWEST, gridBagConstraints);

		addComponent(mainPanel, jLabel8, 2, 2, 1.0, 0.001, 1, 1, gridBagConstraints.BOTH, new Insets(10, 50, 5, 5), gridBagConstraints.NORTHWEST, gridBagConstraints);
		addComponent(mainPanel, jComboBox5, 3, 2, 1.0, 0.001, 1, 1, gridBagConstraints.BOTH, new Insets(10, 5, 5, 75), gridBagConstraints.NORTHWEST, gridBagConstraints);
		
		
		addComponent(mainPanel, jLabel5, 0, 3, 1.0, 0.001, 1, 1, gridBagConstraints.BOTH, new Insets(10, 80, 5, 5), gridBagConstraints.NORTHWEST, gridBagConstraints);
		addComponent(mainPanel, jComboBox4, 1, 3, 1.0, 0.001, 1, 1, gridBagConstraints.BOTH, new Insets(10, 5, 5, 20), gridBagConstraints.NORTHWEST, gridBagConstraints);
		
		addComponent(mainPanel, buttonPanel, 0, 5, 1.0, 0.1, 4, 1, gridBagConstraints.BOTH, new Insets(10, 5, 5, 75), gridBagConstraints.NORTHWEST, gridBagConstraints);

		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		buttonPanel.setLayout(flowLayout);
		buttonPanel.add(Confirm);
		buttonPanel.add(Cancel);

		this.add(mainPanel);
	}

	protected void CancelActionPerformed(ActionEvent evt) {
		this.dispose();

	}

	protected void ConfirmActionPerformed(ActionEvent evt) {

		PwNniInfoService_MB pwNniService = null;
		String beforeName = null;
		try {
			ControlKeyValue port = (ControlKeyValue) jComboBox2.getSelectedItem();
			ControlKeyValue recognition = (ControlKeyValue) jComboBox3.getSelectedItem();
			ControlKeyValue behavior = (ControlKeyValue) jComboBox4.getSelectedItem();
			ControlKeyValue maclearn = (ControlKeyValue) jComboBox5.getSelectedItem();
			ControlKeyValue splitHori = (ControlKeyValue) jComboBox6.getSelectedItem();
			ControlKeyValue controlEnabl = (ControlKeyValue) jComboBoxcontrolEnabl.getSelectedItem();
			pwNniService = (PwNniInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwNniBuffer);

			if (pwNniBuffer == null) {
				pwNniBuffer = new PwNniInfo();
			}
			if (jTextField2.getText().trim().length() == 0 || jTextField3.getText().trim().length() == 0) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NOT_FULL));
				return;
			}
			// if(this.pwNniBuffer.getId()!=0){
			// beforeName=this.pwNniBuffer.getName();
			// }
			// if(VerifyNameUtil.verifyName(EServiceType.PWNNIBUFFER.getValue(), this.jTextField1.getText().trim(), beforeName)){
			// DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
			// return;
			// }
			// pwNniBuffer.setPortId(Integer.valueOf(port.getId()));
			pwNniBuffer.setPwId(pwNniBuffer.getPwId());
			pwNniBuffer.setExitRule(Integer.parseInt(behavior.getId()));
			pwNniBuffer.setSvlan(jTextField2.getText().trim());
			pwNniBuffer.setVlanpri(jTextField3.getText().trim());
			pwNniBuffer.setHorizontalDivision(Integer.parseInt(splitHori.getId()));
			pwNniBuffer.setMacAddressLearn(Integer.parseInt(maclearn.getId()));
			pwNniBuffer.setTagAction(Integer.parseInt(recognition.getId()));
			pwNniBuffer.setControlEnable(Integer.parseInt(controlEnabl.getId()));
			
			// pwNniBuffer.setSiteId(pwInfo.getASiteId());
			// pwNniBuffer.setType(2);
			// pwNniBuffer.setTagRecognition(Integer.valueOf(((Code) recognition.getObject()).getCodeValue()));
			// pwNniBuffer.setTagBehavior(Integer.valueOf(((Code) behavior.getObject()).getCodeValue()));
			// pwNniBuffer.setTagValnId(Integer.valueOf(jTextField2.getText()));
			// pwNniBuffer.setTagValnPri(Integer.valueOf(jTextField3.getText()));
			// pwNniBuffer.setMacAddresslearn(Integer.valueOf(((Code) maclearn.getObject()).getCodeValue()));
			// pwNniBuffer.setPortSplitHorizon(Integer.valueOf(((Code) splitHori.getObject()).getCodeValue()));
			// pwNniBuffer.setControlEnabl(Integer.valueOf(((Code) controlEnabl.getObject()).getCodeValue()));
			PwNniInfo zpwNniBuffer = pwInfo.getzPwNniInfo();
			zpwNniBuffer.setPwId(pwNniBuffer.getPwId());
			zpwNniBuffer.setExitRule(Integer.parseInt(behavior.getId()));
			zpwNniBuffer.setSvlan(jTextField2.getText().trim());
			zpwNniBuffer.setVlanpri(jTextField3.getText().trim());
			zpwNniBuffer.setHorizontalDivision(Integer.parseInt(splitHori.getId()));
			zpwNniBuffer.setMacAddressLearn(Integer.parseInt(maclearn.getId()));
			zpwNniBuffer.setTagAction(Integer.parseInt(recognition.getId()));
			zpwNniBuffer.setControlEnable(Integer.parseInt(controlEnabl.getId()));
			List<PwNniInfo> infos = new ArrayList<PwNniInfo>();
			infos.add(pwNniBuffer);
			infos.add(zpwNniBuffer);
			DispatchUtil pwBufferOperationImpl = new DispatchUtil(RmiKeys.RMI_PWBUFFER);
			String str = "";
			str = pwBufferOperationImpl.excuteUpdate(infos);
			//添加日志记录
			this.Confirm.setResult(1);
			this.Confirm.setOperateKey(EOperationLogType.PWVLAN.getValue());
			DialogBoxUtil.succeedDialog(null, str);
			new PwBusinessPanel().getController().refresh();
			this.dispose();

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(pwNniService);
		}
	}

	// GEN-END:initComponents
	// GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JButton Cancel;
	private PtnButton Confirm;
	private javax.swing.JComboBox jComboBox2;
	private javax.swing.JComboBox jComboBox3;
	private javax.swing.JComboBox jComboBox4;
	private javax.swing.JComboBox jComboBox5;
	private javax.swing.JComboBox jComboBox6;
	private javax.swing.JLabel jLabel10;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JTextField jTextField1;
	private javax.swing.JTextField jTextField2;
	private javax.swing.JTextField jTextField3;
	private JPanel mainPanel;
	private JPanel buttonPanel;
	private javax.swing.JComboBox jComboBoxcontrolEnabl;
	private javax.swing.JLabel jLabelcontrolEnabl;
	// End of variables declaration//GEN-END:variables

}