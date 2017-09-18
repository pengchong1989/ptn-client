﻿package com.nms.ui.ptn.ne.site;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnCalendarField;
import com.nms.ui.manager.control.PtnDateDocument;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

public class SiteAttributePanel extends JPanel {

	private static final long serialVersionUID = 2934469265263389739L;
	private SiteInst siteInst = null;
	/**
	 * 创建一个新的实例
	 */
	public SiteAttributePanel() 
	{
		initComponents();
		this.setMainLayout();
		this.setLayout();
		this.addListener();
		this.btnShow.doClick();
	}

	/**
	 * 监听事件
	 */
	private void addListener() {
		this.btnShow.addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					SiteAttributePanel.this.showSite();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}

			@Override
			public boolean checking() {
				return true;
			}
		});
		this.btnSave.addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					SiteAttributePanel.this.saveSite();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}

			@Override
			public boolean checking() {
				return true;
			}
		});

	}

	/**
	 * 显示网元信息
	 * @throws Exception
	 */
	private void showSite() throws Exception {
		DispatchUtil siteDispatch = null;
		SiteInst siteInst = null;
		try {
	
			siteDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
			siteInst = siteDispatch.selectSite(ConstantUtil.siteId);	
			//添加操作日记记录
			this.btnShow.setOperateKey(EOperationLogType.SITESELECT.getValue());
			this.btnShow.setResult(1);
			
			this.siteInst = siteInst;
			this.initData(siteInst);
		} catch (Exception e) {
			throw e;
		} finally {
		}
	}

	/**
	 * 页面展示
	 * @param siteInst
	 */
	private void initData(SiteInst siteInst) {
		this.txtDescribe.setText(siteInst.getCellIdentifier());
		this.txtIcccode.setText(siteInst.getCellIcccode());
		this.txtIp.setText(siteInst.getCellDescribe());
		this.txtTpoam.setText(siteInst.getCellTPoam());
		this.txtType.setText(siteInst.getCellType());
		this.txtVersions.setText(siteInst.getVersions());

		if (!this.txtIp.getText().equals("10.1.1.254") && !this.txtIp.getText().equals("10.0.1.254")) {
			this.txtIp.setEditable(false);
		}
		this.txtType.setEditable(false);
		this.txtVersions.setEditable(false);
	}

	/**
	 * 保存网元
	 * @throws Exception
	 */
	private void saveSite() throws Exception {
		if (this.siteInst == null) {
			throw new Exception("siteInst is null");
		}
		DispatchUtil siteDispatch = null;
		String result = null;
		try {
			if(!this.txtIp.getText().equals(this.siteInst.getCellDescribe())){
				//验证网元IP是否存在
				if (isNeIDExist(this.txtIp.getText().trim())) {
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SITE_ID_EXIST));
					return;
				}
			}
			this.siteInst.setCellIdentifier(this.txtDescribe.getText());
			this.siteInst.setCellIcccode(this.txtIcccode.getText());
			this.siteInst.setCellDescribe(this.txtIp.getText());
			this.siteInst.setCellTPoam(this.txtTpoam.getText());
			siteDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
			result = siteDispatch.excuteUpdate(siteInst);	
			DialogBoxUtil.succeedDialog(this, result);
			//添加操作日记记录
			this.btnSave.setOperateKey(EOperationLogType.SITEUPDATE.getValue());
			int operationResult=0;
			if(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS).equals(result)){
				operationResult=1;
			}else{
				operationResult=2;
			}
			this.btnSave.setResult(operationResult);
			this.insertOpeLog(EOperationLogType.SITEUPDATE.getValue(), ResultString.CONFIG_SUCCESS, null, null);	
		} catch (Exception e) {
			throw e;
		} finally {
		}

	}

	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		AddOperateLog.insertOperLog(btnSave, operationType, result, oldMac, newMac, ConstantUtil.siteId,ResourceUtil.srcStr(StringKeysPanel.PANEL_SITE_ATTRIBUTE),"");		
	}
	
	/**
	 * 验证网元ID是否存在
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private boolean isNeIDExist(String text) {
		SiteService_MB siteService = null;
		SiteInst siteInst = null;
		List<SiteInst> list = null;

		try {
			// int id = Integer.valueOf(text.trim());

			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			siteInst = new SiteInst();
			siteInst.setCellDescribe(text);

			list = siteService.select(siteInst);
			if (list != null && list.size() > 0) {
				return true;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
		}
		return false;
	}

	/**
	 * 初始化控件
	 */
	private void initComponents() {
		this.tabPanel = new JTabbedPane();
		this.contentPanel = new JPanel();
		this.lblDescribe = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SITE_DESC));
		this.txtDescribe = new JTextField();
		this.lblIp = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SITE_ID));
		this.txtIp = new JTextField();
		this.lblType = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SITE_TYPE));
		this.txtType = new JTextField();
		this.lblVersions = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SITE_VERSIONS));
		this.txtVersions = new JTextField();
		this.lblIcccode = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SITE_ICCCODE));
		this.txtIcccode = new JTextField();
		this.lblTpoam = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SITE_TP_OAM_TYPE));
		this.txtTpoam = new JTextField();
		this.lblTimezone = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SITE_TIMEZONE));
		this.cbbTimezone = new JComboBox();
		this.lblTime = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SITE_TIME));
		this.txtTime = new PtnCalendarField(new PtnDateDocument());
		this.lblTimeServer = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SITE_TIME_SERVE));
		this.txtTimeServer = new JTextField();
		this.btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),false,RootFactory.CORE_MANAGE);
		this.btnShow = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SELECT),false);
	}

	/**
	 * 布局
	 */
	private void setMainLayout() {
		this.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_SITE_ATTRIBUTE)));
		this.tabPanel.add(ResourceUtil.srcStr(StringKeysTab.TAB_BASIC_INFO), this.contentPanel);

		GridBagLayout contentLayout = new GridBagLayout();
		this.setLayout(contentLayout);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(0, 10, 0, 10);
		c.fill = GridBagConstraints.BOTH;
		contentLayout.setConstraints(this.tabPanel, c);
		this.add(this.tabPanel);

	}

	/**
	 * 布局
	 */
	private void setLayout() {
		this.contentPanel.setBackground(Color.WHITE);

		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 150, 210, 40, 40 };
		layout.columnWeights = new double[] { 0, 0, 0, 0.2 };
		layout.rowHeights = new int[] { 10, 35, 35, 35, 35, 35, 35, 35, 35, 35, 20, 35, 35 };
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.2 };
		this.contentPanel.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();

		/** 第二行 网元描述 */
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.lblDescribe, c);
		this.contentPanel.add(this.lblDescribe);
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.txtDescribe, c);
		this.contentPanel.add(this.txtDescribe);

		/** 第二行 网元ip */
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.lblIp, c);
		this.contentPanel.add(this.lblIp);
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.txtIp, c);
		this.contentPanel.add(this.txtIp);

		/** 第三行 网元类型 */
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.lblType, c);
		this.contentPanel.add(this.lblType);
		c.gridx = 1;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.txtType, c);
		this.contentPanel.add(this.txtType);

		/** 第四行 网元版本 */
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.lblVersions, c);
		this.contentPanel.add(this.lblVersions);
		c.gridx = 1;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.txtVersions, c);
		this.contentPanel.add(this.txtVersions);

		/** 第五行 ICCCODE */
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.lblIcccode, c);
		this.contentPanel.add(this.lblIcccode);
		c.gridx = 1;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.txtIcccode, c);
		this.contentPanel.add(this.txtIcccode);

		/** 第六行 tp-oam通道类型 */
		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.lblTpoam, c);
		this.contentPanel.add(this.lblTpoam);
		c.gridx = 1;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.txtTpoam, c);
		this.contentPanel.add(this.txtTpoam);

		/** 第七行 时区 */
		c.gridx = 0;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.lblTimezone, c);
		this.contentPanel.add(this.lblTimezone);
		c.gridx = 1;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.cbbTimezone, c);
		this.contentPanel.add(this.cbbTimezone);

		/** 第八行 网元时间 */
		c.gridx = 0;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.lblTime, c);
		this.contentPanel.add(this.lblTime);
		c.gridx = 1;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.txtTime, c);
		this.contentPanel.add(this.txtTime);

		/** 第九行 网元时间服务器 */
		c.gridx = 0;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.lblTimeServer, c);
		this.contentPanel.add(this.lblTimeServer);
		c.gridx = 1;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.txtTimeServer, c);
		this.contentPanel.add(this.txtTimeServer);

		/** 第十行 按钮 */
		c.gridx = 1;
		c.gridy = 11;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(this.btnSave, c);
		this.contentPanel.add(this.btnSave);
		c.gridx = 2;
		c.gridy = 11;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.btnShow, c);
		this.contentPanel.add(this.btnShow);

	}

	private JTabbedPane tabPanel;
	private JPanel contentPanel;
	private JLabel lblDescribe;
	private JTextField txtDescribe;
	private JLabel lblIp;
	private JTextField txtIp;
	private JLabel lblType;
	private JTextField txtType;
	private JLabel lblVersions;
	private JTextField txtVersions;
	private JLabel lblIcccode;
	private JTextField txtIcccode;
	private JLabel lblTpoam;
	private JTextField txtTpoam;
	private JLabel lblTimezone;
	private JComboBox cbbTimezone;
	private JLabel lblTime;
	private PtnCalendarField txtTime;
	private JLabel lblTimeServer;
	private JTextField txtTimeServer;
	private PtnButton btnSave;
	private PtnButton btnShow;

}
