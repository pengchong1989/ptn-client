﻿package com.nms.ui.ptn.ne.eth.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.nms.db.bean.equipment.port.PortDiscardInfo;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.CommonBean;
import com.nms.db.enums.EOperationLogType;
import com.nms.drive.service.impl.CoderUtils;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.ptn.PortDiscardService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTip;

public class PortDiscardDialog extends PtnDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5334348855786581203L;
	private PortDiscardInfo portDiscardInfo;
	private PtnButton confirmButton;
	private JButton cancelButton;
	private List<JCheckBox> buttonList = new ArrayList<JCheckBox>();
	private JPanel contentPanel;
	private JPanel buttonPanel;
	private JScrollPane scrollPanel;
	private JLabel lblMessage;
	private List<CommonBean> portNameListLog;//记录修改前的数据，便于log日志记录

	public PortDiscardDialog() {

		setModal(true);
		initComponent();
		setLayout();
		addListener();
		initData();
	}

	private void initComponent() {
		try {
			this.setTitle(ResourceUtil.srcStr(StringKeysPanel.DISCARDFLOWMANAGE));
			lblMessage = new JLabel();
			contentPanel = new JPanel();
			buttonPanel = new JPanel();
			confirmButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM), false);
			cancelButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
			getAllPort();
			contentPanel = new JPanel();
			scrollPanel = new JScrollPane();
			scrollPanel.setViewportView(contentPanel);
			scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 
	 * 修改，初始化查询的数据
	 */
	private void initData() {
		PortDiscardService_MB portDiscardServiceMB = null;
		List<PortDiscardInfo> portDiscardInfos = null;
		try {
			portDiscardServiceMB = (PortDiscardService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PortDiscardService);
			portDiscardInfos = portDiscardServiceMB.select(ConstantUtil.siteId);
			if(portDiscardInfos.size()>0){
				this.portNameListLog = new ArrayList<CommonBean>();
				portDiscardInfo = portDiscardInfos.get(0);
				List<Integer> userPortList = selectPort();
				for (int i = 0; i < buttonList.size(); i++) {
					for (int j = 0; j < userPortList.size(); j++) {
						if (i == userPortList.get(j)) {
							buttonList.get(i).setSelected(true);
							CommonBean portName = new CommonBean();
							portName.setAcName(buttonList.get(i).getText());
							this.portNameListLog.add(portName);
						}
					}
				}
			}else{
				portDiscardInfo = new PortDiscardInfo();
			}
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(portDiscardServiceMB);
		}
	}

	public void setLayout() {
		// title面板布局
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		layout.rowHeights = new int[] { 10 };
		layout.rowWeights = new double[] { 0 };
		layout.columnWidths = new int[] { 30, 100 };
		layout.columnWeights = new double[] { 0, 1.0 };
		// 主面板布局
		layout = new GridBagLayout();
		layout.rowHeights = new int[] { 60, 300, 60 };
		layout.rowWeights = new double[] { 0, 0.7, 0 };
		layout.columnWidths = new int[] { ConstantUtil.INT_WIDTH_THREE };
		layout.columnWeights = new double[] { 1 };
		this.setLayout(layout);

		addComponentJDialog(this, scrollPanel, 0, 1, 0, 0.2, 1, 1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, c);
		addComponentJDialog(this, buttonPanel, 0, 2, 0, 0, 1, 1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, c);

		// content面板布局
		setClockRorateButton();
		// button面板布局
		setButtonLayout();
	}

	private void addListener() {
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});

		confirmButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				confirm();
			}
		});

	}

	/**
	 * 保存按钮事件
	 * 
	 */
	private void confirm() {

		char[] portLine1 = { '0', '0', '0', '0', '0', '0', '0', '0' };
		char[] portLine2 = { '0', '0', '0', '0', '0', '0', '0', '0' };
		char[] portLine3 = { '0', '0', '0', '0', '0', '0', '0', '0' };
		char[] portLine4 = { '0', '0', '0', '0', '0', '0', '0', '0' };
		DispatchUtil dispath = null;
		String label = "0";
		try {

			if (buttonList.size() == 0) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_USER_ERROR));
				return;
			}
			List<CommonBean> portNameList = new ArrayList<CommonBean>();
			JCheckBox jcheck = null;
			for (int i = 0; i < buttonList.size(); i++) {
				jcheck = buttonList.get(i);
				if (jcheck.isSelected()) {
					label = "1";
					CommonBean portName = new CommonBean();
					portName.setAcName(jcheck.getText());
					portNameList.add(portName);
				} else {
					label = "0";
				}
				if (i < 8) {
					portLine1[7 - i] = label.charAt(0);
				} else if (i > 7 && i < 16) {
					portLine2[15 - i] = label.charAt(0);
				} else if (i > 15 && i < 24) {
					portLine3[23 - i] = label.charAt(0);
				} else {
					portLine4[31 - i] = label.charAt(0);
				}
			}

			portDiscardInfo.setPortLine1(CoderUtils.convertAlgorism(portLine1));
			portDiscardInfo.setPortLine2(CoderUtils.convertAlgorism(portLine2));
			portDiscardInfo.setPortLine3(CoderUtils.convertAlgorism(portLine3));
			portDiscardInfo.setPortLine4(CoderUtils.convertAlgorism(portLine4));
			portDiscardInfo.setSiteId(ConstantUtil.siteId);
			portDiscardInfo.setPortNameList(portNameList);
			dispath = new DispatchUtil(RmiKeys.RMI_PORTDISCARD);
			String result = "";
			if(portDiscardInfo.getId() == 0){
				result = dispath.excuteInsert(portDiscardInfo);
				// 添加日志记录
				AddOperateLog.insertOperLog(confirmButton, EOperationLogType.PORTDISCARD.getValue(), result, 
						null, portDiscardInfo, ConstantUtil.siteId, ResourceUtil.srcStr(StringKeysMenu.MENU_PORT_DISCOED), "portDiscard");
			}else{
				result = dispath.excuteUpdate(portDiscardInfo);
				// 添加日志记录
				PortDiscardInfo portDiscardBefore = new PortDiscardInfo();
				portDiscardBefore.setPortNameList(portNameListLog);
				AddOperateLog.insertOperLog(confirmButton, EOperationLogType.PORTDISCARD.getValue(), result, 
						portDiscardBefore, portDiscardInfo, ConstantUtil.siteId, ResourceUtil.srcStr(StringKeysMenu.MENU_PORT_DISCOED), "portDiscard");
			}
			DialogBoxUtil.succeedDialog(this, result);
			this.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			portLine1 = null;
			portLine2 = null;
			portLine3 = null;
			portLine4 = null;
			dispath = null;
		}
	}

	/**
	 * 取消按钮事件
	 */
	private void cancel() {
		this.dispose();
	}

	/**
	 * 保存，取消按钮布局
	 */
	private void setButtonLayout() {
		GridBagLayout buttonLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		buttonLayout.columnWidths = new int[] { 300, 50, 50 };
		buttonLayout.columnWeights = new double[] { 0, 0, 0 };
		buttonLayout.rowHeights = new int[] { 30 };
		buttonLayout.rowWeights = new double[] { 0.0 };
		buttonPanel.setLayout(buttonLayout);
		addComponent(buttonPanel, lblMessage, 0, 0, 0, 1, 1, 1, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), GridBagConstraints.WEST, c);
		addComponent(buttonPanel, confirmButton, 1, 0, 0, 1, 1, 1, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), GridBagConstraints.WEST, c);
		addComponent(buttonPanel, cancelButton, 2, 0, 0, 1, 1, 1, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), GridBagConstraints.WEST, c);
	}

	public void setClockRorateButton() {
		GridBagLayout contentLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		contentPanel.setLayout(contentLayout);
		Insets insert = new Insets(5, 50, 5, 5);
		for (int i = 0; i < buttonList.size(); i++) {
			if (i % 2 == 1) {
				this.addComponent(contentPanel, buttonList.get(i), 1, (i + 1) / 2, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
			} else {
				this.addComponent(contentPanel, buttonList.get(i), 0, (i + 2) / 2, 0.2, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, c);
			}
		}
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

	public void addComponentJDialog(JDialog panel, JComponent component, int gridx, int gridy, double weightx, double weighty, int gridwidth, int gridheight, int fill, Insets insets, int anchor, GridBagConstraints gridBagConstraints) {
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

	private List<Integer> selectPort() {

		List<Integer> selectPort = new ArrayList<Integer>();
		String port = setVlue(CoderUtils.convertBinary(portDiscardInfo.getPortLine1()));
		String port2 = setVlue(CoderUtils.convertBinary(portDiscardInfo.getPortLine2()));
		String port3 = setVlue(CoderUtils.convertBinary(portDiscardInfo.getPortLine3()));
		String port4 = setVlue(CoderUtils.convertBinary(portDiscardInfo.getPortLine4()));
		selectPort.addAll(allSlectPort(port, 0));
		selectPort.addAll(allSlectPort(port2, 8));
		selectPort.addAll(allSlectPort(port3, 16));
		selectPort.addAll(allSlectPort(port4, 24));
		return selectPort;
	}

	private String setVlue(String port) {
		String ss = "";
		if (port.length() < 8) {
			for (int i = 0; i < 8 - port.length(); i++) {
				ss += "0";
			}
		}
		return ss + port;
	}

	private List<Integer> allSlectPort(String value, int count) {
		List<Integer> selectPort = new ArrayList<Integer>();
		try {
			for (int i = value.length(); i > 0; i--) {
				if (value.substring(i - 1, i).equals("1")) {
					selectPort.add(8 - i + count);
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return selectPort;
	}

	private void getAllPort() {
		PortService_MB portService = null;
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			PortInst portInst = new PortInst();
			portInst.setSiteId(ConstantUtil.siteId);
			List<PortInst> portInfoList = portService.select(portInst);
			if (portInfoList != null && portInfoList.size() > 0) {
				for (int i = 0; i < portInfoList.size(); i++) {
					if (portInfoList.get(i).getPortType().equalsIgnoreCase("NNI") || portInfoList.get(i).getPortType().equalsIgnoreCase("UNI") || portInfoList.get(i).getPortType().equalsIgnoreCase("NONE")) {
						JCheckBox ge = new JCheckBox(portInfoList.get(i).getPortName());
						buttonList.add(ge);
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(portService);
		}
	}
}
