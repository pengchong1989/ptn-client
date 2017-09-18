﻿package com.nms.ui.ptn.portlag.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import twaver.Card;
import twaver.Element;
import twaver.Node;
import twaver.Port;
import twaver.TDataBox;
import twaver.table.TTreeTable;

import com.nms.db.bean.equipment.card.CardInst;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.port.PortLagInfo;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EActiveStatus;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.card.CardService_MB;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.port.PortLagService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.frame.ViewDataTable;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.portlag.AbstractLagDialog;
import com.nms.ui.ptn.portlag.LagPortInfo;

public class AddLagDialog extends AbstractLagDialog {
	
	private boolean isUpdate = false;
	private PortLagPanel panel;

	public AddLagDialog(JPanel panel) {
		try {
			this.panel = (PortLagPanel) panel;
			this.setModal(true);
			init();
			this.portLagInfo=new PortLagInfo();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	public AddLagDialog(String isUpdate, JPanel panel) throws Exception {
		this.panel = (PortLagPanel) panel;
		this.setModal(true);
		this.isUpdate = true;
		init();
	}

	private void init() throws Exception {
		initComponents();
		setDialogLayout();
		addActionListener();

	}

	@Override
	public PortLagInfo get() {
//		portLagInfo = new PortLagInfo();
		List<PortInst> portInsts = hasSelectTable.getBox().getAllElements();
		List<PortInst> portList = new ArrayList<PortInst>();
		for (PortInst portInst : portInsts) {
			PortInst port = portInst;
			portList.add(port);
		}
		portLagInfo.setType(1);
		portLagInfo.setPortList(portList);
		portLagInfo.setMeangerStatus(Integer.parseInt(((ControlKeyValue) (cbEnable.getSelectedItem())).getId()));
		portLagInfo.setRole(cbRole.getSelectedItem() + "");
		portLagInfo.setsMac(tfLocalAddress.getText());
		portLagInfo.setLagMode(Integer.parseInt(((ControlKeyValue) (cbArithmetic.getSelectedItem())).getId()));
		portLagInfo.setVlanIC(Integer.parseInt(tfVlanid.getText()));
		portLagInfo.setVlanPriority(Integer.parseInt(tfVlanPri.getText()));
		portLagInfo.setInportLimitation(Integer.parseInt(((JSpinner.NumberEditor) (spInbanwidth.getComponents()[2])).getTextField().getText().replaceAll(",", "")));
		portLagInfo.setPortLimitation(Integer.parseInt(((JSpinner.NumberEditor) (spOutbandwidth.getComponents()[2])).getTextField().getText().replaceAll(",", "")));
		portLagInfo.setMaxFrameLength(Integer.parseInt(tfMaxframe.getText()));
		portLagInfo.setMsgLoop(cbMessRing.isSelected() ? 0 : 1);
		portLagInfo.setResumeModel(cbResumeModel.isSelected() ? 0 : 1);
		portLagInfo.setLblNetWrap(Integer.parseInt(((ControlKeyValue) (cbNetWrap.getSelectedItem())).getId()));
		portLagInfo.setLblVlanTpId(Integer.parseInt(((ControlKeyValue) (cbVlanTpId.getSelectedItem())).getId()));
		portLagInfo.setLblouterTpid(Integer.parseInt(((ControlKeyValue) (cbouterTpid.getSelectedItem())).getId()));
		portLagInfo.setLblNetVlanMode(Integer.parseInt(((ControlKeyValue) (cbNetVlanMode.getSelectedItem())).getId()));
		portLagInfo.setFloodFlux(Integer.parseInt(((JSpinner.NumberEditor) (spSingularMess.getComponents()[2])).getTextField().getText().replaceAll(",", "")));
		portLagInfo.setBroadcastFlux(Integer.parseInt(((JSpinner.NumberEditor) (spBroadMess.getComponents()[2])).getTextField().getText().replaceAll(",", "")));
		portLagInfo.setGroupBroadcastFlux(Integer.parseInt(((JSpinner.NumberEditor) (spCentoMess.getComponents()[2])).getTextField().getText().replaceAll(",", "")));
		portLagInfo.setSiteId(ConstantUtil.siteId);
		if(this.isUpdate != true){
			portLagInfo.setLagStatus(EActiveStatus.ACTIVITY.getValue());
		}
		
	//	portLagInfo.setPortId(portList.g);
//		portLagInfo.setLagID(getLagId());
		return portLagInfo;
	}

	private int getLagId() {
		PortLagService_MB portLagService = null;
		int lagId = 0;
		try {
			portLagService = (PortLagService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORTLAG);
			lagId = portLagService.selectLagCountByNeId(ConstantUtil.siteId);
			lagId = lagId + 1;
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(portLagService);
		}
		return lagId;
	}

	@Override
	public void initData(PortLagInfo portLagInfo) {

		setDefalutInit(portLagInfo);

		if (null != portLagInfo) {
			this.portLagInfo=portLagInfo;

			super.getComboBoxDataUtil().comboBoxSelect(this.cbEnable, portLagInfo.getMeangerStatus() + "");
			super.getComboBoxDataUtil().comboBoxSelect(this.cbArithmetic, portLagInfo.getLagMode() + "");
			super.getComboBoxDataUtil().comboBoxSelect(this.cbNetWrap, portLagInfo.getLblNetWrap() + "");
			super.getComboBoxDataUtil().comboBoxSelect(this.cbVlanTpId, portLagInfo.getLblVlanTpId() + "");
			super.getComboBoxDataUtil().comboBoxSelect(this.cbouterTpid, portLagInfo.getLblouterTpid() + "");
			super.getComboBoxDataUtil().comboBoxSelect(this.cbNetVlanMode, portLagInfo.getLblNetVlanMode() + "");
			spSingularMess.setToolTipText(portLagInfo.getFloodFlux() + "");
			spBroadMess.setToolTipText(portLagInfo.getBroadcastFlux() + "");
			spCentoMess.setToolTipText(portLagInfo.getGroupBroadcastFlux() + "");
			tfLocalAddress.setText(portLagInfo.getsMac());
			tfVlanPri.setText(portLagInfo.getVlanIC() + "");
			tfVlanPri.setText(portLagInfo.getVlanRelating() + "");
			spOutbandwidth.setToolTipText(portLagInfo.getPortLimitation() + "");
			spInbanwidth.setToolTipText(portLagInfo.getInportLimitation() + "");
			tfMaxframe.setText(portLagInfo.getMaxFrameLength() + "");
			cbMessRing.setSelected(portLagInfo.getMsgLoop() == 0 ? false : true);
			cbResumeModel.setSelected(portLagInfo.getResumeModel() == 0 ? false : true);
			spBroadMess.setValue(portLagInfo.getBroadcastFlux());// 广播报文抑制
			spSingularMess.setValue(portLagInfo.getFloodFlux());//单播抑制报文
			spCentoMess.setValue(portLagInfo.getGroupBroadcastFlux());//组播报文抑制
			hasSelectTable.clear();
			List<Integer> idList = new ArrayList<Integer>();
			for (PortInst portInst : portLagInfo.getPortList()) {
				idList.add(portInst.getPortId());
			}

			List<Element> elelments = box.getAllElements();
			for (Element ment : elelments) {
				if (ment instanceof Port) {
					PortInst port = (PortInst) ment.getBusinessObject();
					if (idList.contains(port.getPortId())) {
						ment.setVisible(false);
						hasSelectTable.add(port);
					}
				}
			}
		}

	}

	private void addActionListener() {
		addMem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Element element = box.getLastSelectedElement();
				if (element == null || !(element instanceof Port)) {
					DialogBoxUtil.errorDialog(AddLagDialog.this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_PORT));
					return;
				}
				if (element instanceof Port) {
					PortInst portInst = (PortInst) element.getBusinessObject();
					hasSelectTable.add(portInst);
					element.setVisible(false);
					// 过滤掉速率不相同的端口
					List<Element> elelments = box.getAllElements();
					for (Element ment : elelments) {
						if (ment instanceof Port) {
							PortInst port = (PortInst) ment.getBusinessObject();
							// if(port.getSpeed() != null &&
							// !port.getSpeed().equals(portInst.getSpeed())){
							// ment.setVisible(false);
							// }
						}
					}
				}
			}
		});

		removeMem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				PortInst removePort = hasSelectTable.getSelect();
				if (removePort == null) {
					DialogBoxUtil.errorDialog(AddLagDialog.this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_REMOVE));
					return;
				}
				hasSelectTable.getBox().removeElement(removePort);
				List<Element> elelments = box.getAllElements();
				for (Element ment : elelments) {
					if (ment instanceof Port) {
						PortInst port = (PortInst) ment.getBusinessObject();
						if (port.getPortId() == removePort.getPortId()) {
							ment.setVisible(true);
						}
					}
				}
			}
		});

		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				AddLagDialog.this.dispose();
			}
		});

		confirm.addActionListener(new MyActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				savaLag();

			}

			@Override
			public boolean checking() {
				
				return true;
			}
		});

		cbArithmetic.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				if (evt.getStateChange() == 1) {
					try {
						Code code = (Code) (((ControlKeyValue) cbArithmetic.getSelectedItem()).getObject());
						if ("0".equals(code.getCodeValue())) {
							cbResumeModel.setEnabled(true);
						} else {
							cbResumeModel.setSelected(false);
							cbResumeModel.setEnabled(false);
						}

					} catch (Exception e) {
						ExceptionManage.dispose(e,this.getClass());
					}
				}
			}
		});
	}

	public void savaLag() {
		portLagInfo = get();
		String result = "";
		try {
			
			if(null==this.portLagInfo.getPortList() || this.portLagInfo.getPortList().size() == 0){
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_MUSTNETWORK_BEFORE));
				return;
			}
			
			DispatchUtil portLagDispatch = new DispatchUtil(RmiKeys.RMI_PORTLAG);
			if (isUpdate) {				
				result = portLagDispatch.excuteUpdate(portLagInfo);
				this.confirm.setOperateKey(EOperationLogType.PORTLAGUPDATE.getValue());
			} else {
				result = portLagDispatch.excuteInsert(portLagInfo);
				this.confirm.setOperateKey(EOperationLogType.PORTLAGINSERT.getValue());
			}
			//添加日志记录
			int operationResult=0;
			if(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS).equals(result)){
				operationResult=1;
			}else{
				operationResult=2;
			}
			confirm.setResult(operationResult);
			DialogBoxUtil.succeedDialog(this, result);
			this.panel.getController().refresh();
			this.dispose();
		} catch (Exception e) {
			DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_FAIL));
		}
	}

	private void setDialogLayout() {
		Dimension dimension = new Dimension(410, 500);
		this.setPreferredSize(dimension);
		this.setMinimumSize(dimension);
		setTopPanelLayout();
		setMidPanelLayout();
		setBottomPanelLayout();
		// 主面板布局
		GridBagLayout layout = new GridBagLayout();
		this.setLayout(layout);
		layout.rowHeights = new int[] { 280 };
		layout.rowWeights = new double[] { 1.0 };
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		layout.addLayoutComponent(tabPanel, c);
		this.add(tabPanel);
		//lag基本属性面板布局
		GridBagLayout mainLayout = new GridBagLayout();
		lagBasicPanel.setLayout(mainLayout);
		mainLayout.rowHeights = new int[] { 200, 180, 30 };
		mainLayout.rowWeights = new double[] { 0.2, 0.4, 0 };
		mainLayout.columnWeights = new double[] { 1.0 };
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 0, 10, 0);
		mainLayout.addLayoutComponent(topPanel, c);
		lagBasicPanel.add(topPanel);
		c.gridy = 1;
		c.insets = new Insets(10, 0, 0, 0);
		mainLayout.addLayoutComponent(midPanel, c);
		lagBasicPanel.add(midPanel);
		c.gridy = 2;
		c.insets = new Insets(0, 0, 5, 0);
		mainLayout.addLayoutComponent(bottomPanel, c);
		lagBasicPanel.add(bottomPanel);
		setUniPanelLayout();
	}

	//UNI面板
	private void setUniPanelLayout() {
		GridBagLayout restrainlayout = new GridBagLayout();
		restrainPanel.setLayout(restrainlayout);
		restrainlayout.columnWidths = new int[] { 60, 120 };
		restrainlayout.columnWeights = new double[] { 0.5, 0.5 };
		restrainlayout.rowHeights = new int[] { 20, 20, 20, 20 };
		restrainlayout.rowWeights = new double[] { 0.1, 0.1, 0.1, 0.1 };
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(10, 5, 0, 5);
		restrainlayout.addLayoutComponent(lblBroadMess, c);
		restrainPanel.add(lblBroadMess);
		c.gridx = 1;
		c.insets = new Insets(10, 5, 0, 25);
		restrainlayout.addLayoutComponent(spBroadMess, c);
		restrainPanel.add(spBroadMess);
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(10, 5, 0, 5);
		restrainlayout.addLayoutComponent(lblSingularMess, c);
		restrainPanel.add(lblSingularMess);
		c.gridx = 1;
		c.insets = new Insets(10, 5, 0, 25);
		restrainlayout.addLayoutComponent(spSingularMess, c);
		restrainPanel.add(spSingularMess);
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(10, 5, 0, 5);
		restrainlayout.addLayoutComponent(lblCentoMess, c);
		restrainPanel.add(lblCentoMess);
		c.gridx = 1;
		c.insets = new Insets(10, 5, 0, 25);
		restrainlayout.addLayoutComponent(spCentoMess, c);
		restrainPanel.add(spCentoMess);

		GridBagLayout layout = new GridBagLayout();
		uniPanel.setLayout(layout);
		layout.columnWidths = new int[] { 60, 120, 20, 60, 120 };
		layout.columnWeights = new double[] { 0.1, 0.1, 0, 0.1, 0.1 };
		layout.rowHeights = new int[] { 20, 20, 20, 20, 220 };
		layout.rowWeights = new double[] { 0.1, 0.1, 0.1, 0.1, 0.6 };
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(10, 5, 0, 5);
		layout.addLayoutComponent(lblNetWrap, c);
		uniPanel.add(lblNetWrap);
		c.gridx = 1;
		layout.addLayoutComponent(cbNetWrap, c);
		uniPanel.add(cbNetWrap);
		c.gridy = 1;
		c.gridx = 0;
		layout.addLayoutComponent(lblVlanTpId, c);
		uniPanel.add(lblVlanTpId);
		c.gridx = 1;
		layout.addLayoutComponent(cbVlanTpId, c);
		uniPanel.add(cbVlanTpId);
		c.gridy = 2;
		c.gridx = 0;
		layout.addLayoutComponent(lblouterTpid, c);
		uniPanel.add(lblouterTpid);
		c.gridx = 1;
		layout.addLayoutComponent(cbouterTpid, c);
		uniPanel.add(cbouterTpid);
		c.gridy = 3;
		c.gridx = 0;
		layout.addLayoutComponent(lblNetVlanMode, c);
		uniPanel.add(lblNetVlanMode);
		c.gridx = 1;
		layout.addLayoutComponent(cbNetVlanMode, c);
		uniPanel.add(cbNetVlanMode);
		c.gridx = 3;
		c.gridy = 0;
		c.gridheight = 4;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		layout.addLayoutComponent(restrainPanel, c);
		uniPanel.add(restrainPanel);
	}

	private void setBottomPanelLayout() {
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		bottomPanel.add(confirm);
		bottomPanel.add(cancel);
	}

	private void setMidPanelLayout() {
		GridBagLayout layout = new GridBagLayout();
		midPanel.setLayout(layout);
		layout.rowHeights = new int[] { 40, 20, 20, 20, 20 };
		layout.rowWeights = new double[] { 0.2, 0.1, 0.1, 0.1, 0.1 };
		layout.columnWidths = new int[] { 180, 40, 180 };
		layout.columnWeights = new double[] { 0.2, 0, 0.2 };
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 5;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(10, 5, 0, 5);
		layout.addLayoutComponent(selectScroll, c);
		midPanel.add(selectScroll);

		c.gridx = 2;
		c.gridy = 0;
		c.gridheight = 5;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(10, 5, 0, 5);
		layout.addLayoutComponent(hasSelectScroll, c);
		midPanel.add(hasSelectScroll);

		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 5, 10, 5);
		layout.addLayoutComponent(rbHandWork, c);
		midPanel.add(rbHandWork);
		c.gridy = 2;
		layout.addLayoutComponent(rbStaticWork, c);
		midPanel.add(rbStaticWork);
		c.gridy = 3;
		layout.addLayoutComponent(addMem, c);
		midPanel.add(addMem);
		c.gridy = 4;
		c.insets = new Insets(0, 5, 10, 5);
		layout.addLayoutComponent(removeMem, c);
		midPanel.add(removeMem);

	}

	private void setTopPanelLayout() {
		GridBagLayout layout = new GridBagLayout();
		topPanel.setLayout(layout);
		layout.rowHeights = new int[] { 20, 20, 20, 20, 20, 20 };
		layout.rowWeights = new double[] { 0.1, 0.1, 0.1, 0.1, 0.1, 0.1 };
		layout.columnWidths = new int[] { 80, 120, 80, 120 };
		layout.columnWeights = new double[] { 0.2, 0.2, 0.2, 0.2 };
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(10, 10, 0, 10);
		layout.addLayoutComponent(lblEnable, c);
		topPanel.add(lblEnable);
		c.gridx = 1;
		c.insets = new Insets(10, 0, 0, 0);
		layout.addLayoutComponent(cbEnable, c);
		topPanel.add(cbEnable);
		c.gridx = 2;
		c.insets = new Insets(10, 10, 0, 10);
		layout.addLayoutComponent(lblRole, c);
		topPanel.add(lblRole);
		c.gridx = 3;
		c.insets = new Insets(10, 0, 0, 0);
		layout.addLayoutComponent(cbRole, c);
		topPanel.add(cbRole);

		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(10, 10, 0, 10);
		layout.addLayoutComponent(lblLocalAddress, c);
		topPanel.add(lblLocalAddress);
		c.gridx = 1;
		c.insets = new Insets(10, 0, 0, 0);
		layout.addLayoutComponent(tfLocalAddress, c);
		topPanel.add(tfLocalAddress);
		c.gridx = 2;
		c.insets = new Insets(10, 10, 0, 10);
		layout.addLayoutComponent(lblArithmetic, c);
		topPanel.add(lblArithmetic);
		c.gridx = 3;
		c.insets = new Insets(10, 0, 0, 0);
		layout.addLayoutComponent(cbArithmetic, c);
		topPanel.add(cbArithmetic);

		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(10, 10, 0, 10);
		layout.addLayoutComponent(lblVlanid, c);
		topPanel.add(lblVlanid);
		c.gridx = 1;
		c.insets = new Insets(10, 0, 0, 0);
		layout.addLayoutComponent(tfVlanid, c);
		topPanel.add(tfVlanid);
		c.gridx = 2;
		c.insets = new Insets(10, 10, 0, 10);
		layout.addLayoutComponent(lblVlanPri, c);
		topPanel.add(lblVlanPri);
		c.gridx = 3;
		c.insets = new Insets(10, 0, 0, 0);
		layout.addLayoutComponent(tfVlanPri, c);
		topPanel.add(tfVlanPri);

		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(10, 10, 0, 10);
		layout.addLayoutComponent(lblInbandwidth, c);
		topPanel.add(lblInbandwidth);
		c.gridx = 1;
		c.insets = new Insets(10, 0, 0, 0);
		layout.addLayoutComponent(spInbanwidth, c);
		topPanel.add(spInbanwidth);
		c.gridx = 2;
		c.insets = new Insets(10, 10, 0, 10);
		layout.addLayoutComponent(lblWorkport, c);
		topPanel.add(lblWorkport);
		c.gridx = 3;
		c.insets = new Insets(10, 0, 0, 0);
		layout.addLayoutComponent(cbWorkport, c);
		topPanel.add(cbWorkport);

		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(10, 10, 0, 10);
		layout.addLayoutComponent(lblOutbandwidth, c);
		topPanel.add(lblOutbandwidth);
		c.gridx = 1;
		c.insets = new Insets(10, 0, 0, 0);
		layout.addLayoutComponent(spOutbandwidth, c);
		topPanel.add(spOutbandwidth);
		c.gridx = 2;
		c.insets = new Insets(10, 10, 0, 10);
		layout.addLayoutComponent(lblMaxframe, c);
		topPanel.add(lblMaxframe);
		c.gridx = 3;
		c.insets = new Insets(10, 0, 0, 0);
		layout.addLayoutComponent(tfMaxframe, c);
		topPanel.add(tfMaxframe);

		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(10, 10, 0, 10);
		layout.addLayoutComponent(cbMessRing, c);
		topPanel.add(cbMessRing);
		c.gridx = 1;
		c.insets = new Insets(10, 10, 0, 0);
		layout.addLayoutComponent(cbResumeModel, c);
		topPanel.add(cbResumeModel);

	}

	private void initComponents() throws Exception {
		lagBasicPanel = new JPanel();
		lblEnable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MANAGE_STATUS));
		cbEnable = new JComboBox();
		super.getComboBoxDataUtil().comboBoxData(cbEnable, "ENABLEDSTATUE");	 //从code取 端口使能状态
		cbEnable.setSelectedIndex(0);
		lblRole = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ROLE));
//		cbRole = new JComboBox(new String[] { "UNI", "NNI" });
		cbRole = new JComboBox(new String[] { "UNI"});
		lblLocalAddress = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LOCAL_MAC));
		tfLocalAddress = new JTextField();
		tfLocalAddress.setEditable(false);
		lblArithmetic = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LINK_AGGREGATION_PACKET_ALGORITHM));
		cbArithmetic = new JComboBox();
		super.getComboBoxDataUtil().comboBoxData(cbArithmetic, "LAGMODE");	 //从code取 	链路聚合端口报文方法算法
		lblVlanid = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DEFAULT_VLAN_ID));
		tfVlanid = new JTextField("1");
		lblVlanPri = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DEFAULT_VLAN_PRIORITY));
		tfVlanPri = new JTextField("0");
		lblInbandwidth = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_IN_BANDWIDTH_LIMIT));
		spInbanwidth = new JSpinner();
		((JSpinner.NumberEditor) (spInbanwidth.getComponents()[2])).getTextField().setText("-1");
		lblWorkport = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_WORK_PORT));
		cbWorkport = new JComboBox();
		lblOutbandwidth = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OUT_BANDWIDTH_LIMIT));
		spOutbandwidth = new JSpinner();
		((JSpinner.NumberEditor) (spOutbandwidth.getComponents()[2])).getTextField().setText("-1");
		lblMaxframe = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MAX_FRAME_WORDS));
		tfMaxframe = new JTextField("1518");
		cbMessRing = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_IS_MESSAGE_LOOP));
		cbMessRing.setSelected(true);
		cbResumeModel = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_11_RECOVERY_MODE));
		cbResumeModel.setSelected(false);
		rbHandWork = new JRadioButton(ResourceUtil.srcStr(StringKeysLbl.LBL_HANDWORK));
		rbHandWork.setSelected(true);
		rbStaticWork = new JRadioButton(ResourceUtil.srcStr(StringKeysLbl.LBL_STATIC));
		rbStaticWork.setSelected(false);
		group = new ButtonGroup();
		group.add(rbHandWork);
		group.add(rbStaticWork);
		addMem = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_ADD_MEMBER));
		removeMem = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_REMOVE_MEMBER));
		confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),false);
		cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		topPanel = new JPanel();
		midPanel = new JPanel();
		midPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTitle.TIT_SELECT_MEMBER)));
		bottomPanel = new JPanel();
		box = new TDataBox();
		selectTable = new TTreeTable(box);
		selectTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		hasSelectTable = new ViewDataTable<PortInst>("addLagMemTable");
		hasSelectTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectScroll = new JScrollPane();
		selectScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		selectScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		selectScroll.setViewportView(selectTable);
		hasSelectScroll = new JScrollPane();
		hasSelectScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		hasSelectScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		hasSelectScroll.setViewportView(hasSelectTable);
		setTreeTableProperty();

		//UNI面板控件初始化
		uniPanel = new JPanel();
		lblNetWrap = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ETHERNET_PACKAGE));
		cbNetWrap = new JComboBox();
		super.getComboBoxDataUtil().comboBoxData(cbNetWrap, "LAGNETWRAP");	 //从code取 	以太网封装
//		cbNetWrap.addItem("1Q");
//		cbNetWrap.addItem("1AD");
		lblVlanTpId = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_VLAN_TPID));
		cbVlanTpId = new JComboBox();
		super.getComboBoxDataUtil().comboBoxData(cbVlanTpId, "LAGVLANTPID");	 //从code取 	运营商VLAN_TPID
		lblouterTpid = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OUTER_VLAN_ID));
		cbouterTpid = new JComboBox();
		super.getComboBoxDataUtil().comboBoxData(cbouterTpid, "LAGOUTERTPID");	 //从code取 	OUTER VLAN的TPID
		lblNetVlanMode = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ETHERNET_VLAN_MODEL));
		cbNetVlanMode = new JComboBox();
		super.getComboBoxDataUtil().comboBoxData(cbNetVlanMode, "LAGNETVLANMODE");	 //从code取  以太网VLAN模式
		restrainPanel = new JPanel();
		restrainPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.LBL_MES_CONTROL)));
		lblBroadMess = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_BROADCAST_SUPPRESSION));
		lblSingularMess = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_UNICAST_SUPPRESSION));
		lblCentoMess = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MULTICAST_SUPPRESSION));
		spBroadMess = new JSpinner();
		spSingularMess = new JSpinner();
		spCentoMess = new JSpinner();
		((JSpinner.NumberEditor) (spBroadMess.getComponents()[2])).getTextField().setText("-1");
		((JSpinner.NumberEditor) (spSingularMess.getComponents()[2])).getTextField().setText("-1");
		((JSpinner.NumberEditor) (spCentoMess.getComponents()[2])).getTextField().setText("-1");
		
		tabPanel = new JTabbedPane();
		tabPanel.addTab(ResourceUtil.srcStr(StringKeysPanel.PANEL_LAG_INFORMATION), lagBasicPanel);
		tabPanel.addTab(ResourceUtil.srcStr(StringKeysPanel.PANEL_UNI_INFORMATION), uniPanel);
	}

	private void setModel(JComboBox comboBox, Map<Integer, String> keyValues) {
		DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBox.getModel();

		for (Integer key : keyValues.keySet()) {
			comboBoxModel.addElement(new ControlKeyValue(key.toString(), keyValues.get(key)));
		}
	}

	private void setTreeTableProperty() {
//		LagPortLoader loader = new LagPortLoader(box);
		// selectTable.getTree().setLazyLoader(loader);
		selectTable.getTree().setRootVisible(false);
		selectTable.setElementClass(LagPortInfo.class);
		selectTable.registerElementClassXML(LagPortInfo.class, "en_US".equals(ResourceUtil.language) ? "/com/nms/ui/ptn/portlag/LagPortElement_us.xml" : "/com/nms/ui/ptn/portlag/LagPortElement.xml");
		selectTable.setRowHeight(18);
		selectTable.setShowGrid(false);
		selectTable.setEditable(false);
		selectTable.setTreeColumnDisplayName(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_NUM));

	}

	public void setDefalutInit(PortLagInfo portLagInfo) {
		int siteId = ConstantUtil.siteId;
		SiteInst siteInst = null;
		SiteService_MB service = null;
		try {
			service = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			siteInst = service.select(siteId);
			if (siteInst != null) {
				Node node = new Node();
				node.setBusinessObject(siteInst);
				node.setUserObject(false);
				node.setVisible(true);
				node.setSelected(false);
				node.setName(siteInst.getCellId());
				box.addElement(node);

				this.createCard(node,portLagInfo);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}

	}

	/**
	 * 添加板卡对象
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private void createCard(Node parent,PortLagInfo portLagInfo) {
		SiteInst site = (SiteInst) parent.getBusinessObject();
		CardService_MB service = null;
		List<CardInst> cardList = new ArrayList<CardInst>();
		CardInst cardInst = null;
		try {
			service = (CardService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CARD);
			cardInst = new CardInst();
			cardInst.setSiteId(site.getSite_Inst_Id());
			cardList = service.select(cardInst);
			for (CardInst obj : cardList) {
				if (!"FAN".equals(obj.getCardName())&&!"PSU".equals(obj.getCardName())) {
					Card card = new Card();
					card.setSelected(false);
					card.setVisible(true);
					card.setBusinessObject(obj);
					card.setUserObject(false);
					card.setParent(parent);
					card.setName(obj.getCardName());
					box.addElement(card);
					this.createPort(card,portLagInfo);
				}

			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
			cardList = null;
			cardInst = null;
		}
	}

	/**
	 * 添加端口对象
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private void createPort(Card parent,PortLagInfo portLagInfo) {
		CardInst cardInst = (CardInst) parent.getBusinessObject();
		PortInst portInst = null;
		List<PortInst> portList = null;
		PortService_MB service = null;
		try {
			addLagPort(cardInst, parent,portLagInfo);
			service = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			portInst = new PortInst();
			portInst.setSiteId(cardInst.getSiteId());
			portInst.setCardId(cardInst.getId());
			portInst.setPortType("NONE");
			portList = service.select(portInst);
			for (PortInst portinst : portList) {
				addPort(portinst, parent);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			portInst = null;
			portList = null;
			UiUtil.closeService_MB(service);
		}
	}

	/**
	 * 添加lag中的端口
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	private void addLagPort(CardInst cardInst, Card parent,PortLagInfo portLagInfo) throws Exception {
		if (null != portLagInfo) {

			for (PortInst portInst : portLagInfo.getPortList()) {
				if (cardInst.getId() == this.getSlotId(portInst)) {
					addPort(portInst, parent);
				}
			}

		}
	}
	
	/**
	 * 获取槽位对象主键
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private int getSlotId(PortInst portInst) throws Exception {

		CardService_MB cardService = null;
		CardInst cardinst = null;
		List<CardInst> cardInstList = null;
		try {
			cardService = (CardService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CARD);
			cardinst = new CardInst();
			cardinst.setId(portInst.getCardId());
			cardInstList = cardService.select(cardinst);

			if (cardInstList.size() == 1) {
				return cardInstList.get(0).getId();
			} else {
				return 0;
			}

		} catch (Exception e) {
			throw e;
		} finally{
			UiUtil.closeService_MB(cardService);
			cardinst = null;
			cardInstList = null;
		}
	}
	
	private void addPort(PortInst portinst , Card parent){
		Port port = new Port();
		port.setUserObject(false);
		port.setBusinessObject(portinst);
		port.setName(portinst.getPortName());
		port.setParent(parent);
		port.setSelected(false);
		port.setVisible(true);
		box.addElement(port);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6026386239905146316L;

	private JTabbedPane tabPanel;
	private JPanel lagBasicPanel;
	private JLabel lblEnable; // 使能状态
	private JComboBox cbEnable;
	private JLabel lblRole; // 角色
	private JComboBox cbRole;
	private JLabel lblLocalAddress; // 本地MAC地址
	private JTextField tfLocalAddress;
	private JLabel lblArithmetic;// 链路聚合报文分发算法
	private JComboBox cbArithmetic;
	private JLabel lblVlanid; // 缺省VLAN ID
	private JTextField tfVlanid;
	private JLabel lblVlanPri; // 缺省VLAN 优先级
	private JTextField tfVlanPri;
	private JLabel lblInbandwidth; // 入口带宽限制
	private JSpinner spInbanwidth;
	private JLabel lblWorkport; // 工作端口
	private JComboBox cbWorkport;
	private JLabel lblOutbandwidth; // 出口带宽限制
	private JSpinner spOutbandwidth;
	private JLabel lblMaxframe; // 最大帧长
	private JTextField tfMaxframe;
	private JCheckBox cbMessRing;// 是否允许报文环回
	private JCheckBox cbResumeModel; // 1:1恢复模式
	private JRadioButton rbHandWork; // 手工
	private JRadioButton rbStaticWork; // 静态
	private ButtonGroup group;
	private JButton addMem; // 添加成员
	private JButton removeMem; // 移除成员
	private PtnButton confirm;
	private JButton cancel;
	private JPanel topPanel; // 顶部面板
	private JPanel midPanel; // 中间成员面板
	private JPanel bottomPanel; // 底部确定和取消按钮面板
	private JScrollPane selectScroll;
	private JScrollPane hasSelectScroll;
	private TDataBox box;
	private TTreeTable selectTable;
	private ViewDataTable<PortInst> hasSelectTable;
	private LagPortInfo info;
	private PortLagInfo portLagInfo;

	//UNI属性
	private JPanel uniPanel;
	private JLabel lblNetWrap;//以太网封装
	private JComboBox cbNetWrap;
	private JLabel lblVlanTpId; //运营商VLAN_TPID
	private JComboBox cbVlanTpId;
	private JLabel lblouterTpid; //OUTER VLAN的TPID
	private JComboBox cbouterTpid;
	private JLabel lblNetVlanMode; //以太网VLAN模式
	private JComboBox cbNetVlanMode;
	private JPanel restrainPanel; //报文抑制面板
	private JLabel lblBroadMess;// 广播报文抑制
	private JSpinner spBroadMess;
	private JLabel lblSingularMess;//单播抑制报文
	private JSpinner spSingularMess;
	private JLabel lblCentoMess;//组播报文抑制
	private JSpinner spCentoMess;
}
