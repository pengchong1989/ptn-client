﻿package com.nms.ui.ptn.basicinfo.dialog.site;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.system.Field;
import com.nms.model.system.FieldService_MB;
import com.nms.model.system.SubnetService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysTitle;

/**
 * 网元复制
 * 
 * @author Dzy
 * 
 */
public class CopySiteDialog extends AddSiteDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7884294925019584076L;
	private JLabel fieldLabel; // 域JLabel
	private JComboBox fieldCombo; // 域下拉菜单
	private SiteInst searchSite;
	private SiteSearchTablePanel siteSearchTablePanel;
	/**
	 * 创建一个新的实例
	 * 
	 * @param modal
	 * @param siteInst
	 *            网元对象 ，新建时为空
	 * @param siteSearchTablePanel
	 *            主面板
	 */
	public CopySiteDialog(boolean modal, SiteInst siteInst,
			SiteSearchTablePanel siteSearchTablePanel) {
		super(siteInst.getSite_Inst_Id()+"");
		try {
			if (null != siteSearchTablePanel) {
				this.siteSearchTablePanel = siteSearchTablePanel;
				this.searchSite = siteSearchTablePanel.getSelect();

			}
			if (null != siteInst) {
				super.siteInst = siteInst;
			}
			this.setModal(modal);
			initCopyComponents();
			setCopyLayout();
			addCopyListener();
			initCopyData();
			UiUtil.showWindow(this, 450, 550);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 初始化数据
	 * 
	 * @throws Exception
	 */
	private void initCopyData() throws Exception {
		super.txtSiteIp.setEnabled(true);
		super.setTitle(ResourceUtil.srcStr(StringKeysMenu.MENU_COPYSITE));
		initFieldCombo(this.fieldCombo);
		initSubnetCombo(super.subnetCombo);
		super.username.setEnabled(false);
		super.userpwd.setEnabled(false);
		if(null != super.siteInst)
			userpwd.setText(super.siteInst.getUserpwd());
		
		if (null != searchSite) {
			super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_CREATE_SITE));
			this.siteInst = new SiteInst();
			super.getComboBoxDataUtil().comboBoxSelect(cmbSiteManufacturer,this.searchSite.getCellEditon());
			this.equipmentTypeDate();
			super.getComboBoxDataUtil().comboBoxSelect(cmbSiteType, this.searchSite.getCellType());
			txtSiteIp.setEnabled(false);
			cmbSiteType.setEnabled(false);
			cmbSiteManufacturer.setEnabled(false);
			super.username.setText("admin");
			super.userpwd.setText("cMPC_pxn");
			this.txtSiteIp.setText(this.searchSite.getCellDescribe());
			siteInst.setVersions(this.searchSite.getVersions());
		}
		initSubnetAndField();
		super.siteInst.setSite_Inst_Id(0);
	}

	/**
	 * 初始化域和子网信息
	 */
	private void initSubnetAndField() {
		FieldService_MB fieldService = null;
		List<Field> fieldList;
		List<Integer> list ;
		Field field;
		try {
			list = new ArrayList<Integer>();
			list.add(super.siteInst.getFieldID());
			fieldService = (FieldService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Field);
			fieldList = fieldService.selectfieldidByid(list);
			if(fieldList.size() != 1)
				return;
			
			field = fieldList.get(0);
			if("field".equals(field.getObjectType())){
				super.getComboBoxDataUtil().comboBoxSelect(fieldCombo, this.siteInst.getFieldID()+"");
			}else{
				super.getComboBoxDataUtil().comboBoxSelect(fieldCombo, field.getParentId()+"");
				super.getComboBoxDataUtil().comboBoxSelect(subnetCombo, this.siteInst.getFieldID()+"");
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		} finally {
			UiUtil.closeService_MB(fieldService);
		}
	}

	/**
	 * 按钮监听
	 */
	public void addCopyListener() {

		// 与下拉列表监听
		fieldCombo.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(java.awt.event.ItemEvent e) {
				subnetComboActionPerformed(e);
			}
		});
	}


	/**
	 * 布局
	 */
	public void setCopyLayout() {

		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 70, 180, 50 };
		layout.columnWeights = new double[] { 0, 0, 0 };
		layout.rowHeights = new int[] { 25, 40, 40, 40, 40, 40, 40, 40, 40, 15,
				40, 40 };
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.2 };
		super.jPanel1.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		/** 第0行 */
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(lblMessage, c);
		this.jPanel1.add(lblMessage);
		/** 第一行 */
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 15, 5);
		layout.setConstraints(jLabel3, c);
		this.jPanel1.add(jLabel3);
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(txtSiteName, c);
		this.jPanel1.add(txtSiteName);
		/** 第二行 */
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(jLabel2, c);
		this.jPanel1.add(jLabel2);
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(txtSiteIp, c);
		this.jPanel1.add(txtSiteIp);
		/** 第三行 */
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(jLabel5, c);
		this.jPanel1.add(jLabel5);
		c.gridx = 1;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(cmbSiteManufacturer, c);
		this.jPanel1.add(cmbSiteManufacturer);
		/** 第四行 */
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(jLabel4, c);
		this.jPanel1.add(jLabel4);
		c.gridx = 1;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(cmbSiteType, c);
		this.jPanel1.add(cmbSiteType);

		// 所属子网
		c.gridx = 0;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(belongSubnet, c);
		this.jPanel1.add(belongSubnet);
		c.gridx = 1;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(subnetCombo, c);
		this.jPanel1.add(subnetCombo);

		// 所属域
		c.gridx = 0;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(fieldLabel, c);
		this.jPanel1.add(fieldLabel);
		c.gridx = 1;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(fieldCombo, c);
		this.jPanel1.add(fieldCombo);

		// 网元属性
		c.gridx = 0;
		c.gridy = 10;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(siteType, c);
		this.jPanel1.add(siteType);
		c.gridx = 1;
		c.gridy = 10;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(siteTypeCombo, c);
		this.jPanel1.add(siteTypeCombo);

		/** 第10行 */
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(this.lblSiteId_wh, c);
		this.jPanel1.add(this.lblSiteId_wh);
		c.gridx = 1;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(this.txtSiteId_wh, c);
		this.jPanel1.add(this.txtSiteId_wh);

		/** 第12行 */
		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(jLabel14, c);
		this.jPanel1.add(jLabel14);
		c.gridx = 1;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(username, c);
		this.jPanel1.add(username);
		/** 第13行 */
		c.gridx = 0;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(jLabel15, c);
		this.jPanel1.add(jLabel15);
		c.gridx = 1;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(userpwd, c);
		this.jPanel1.add(userpwd);

		/** 是否是网关网元 */
		c.gridx = 0;
		c.gridy = 11;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(this.lblIsGateway, c);
		this.jPanel1.add(this.lblIsGateway);
		c.gridx = 1;
		c.gridy = 11;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(this.chkIsGateway, c);
		this.jPanel1.add(this.chkIsGateway);

		c.fill = GridBagConstraints.NONE;
		/** 第14行 */
		c.gridx = 1;
		c.gridy = 12;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(jButton1, c);
		this.jPanel1.add(jButton1);
		c.gridx = 2;
		c.gridy = 12;
		c.gridheight = 1;
		c.gridwidth = 1;
		// c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(jButton2, c);
		this.jPanel1.add(jButton2);

		this.add(jPanel1);

	}

	/**
	 * 初始化空间
	 * 
	 * @throws Exception
	 */
	public void initCopyComponents() throws Exception {

		this.fieldCombo = new JComboBox();
		this.fieldLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_FIELD_BELONG));
	
	}

	/**
	 * 下拉菜单监听
	 * 
	 * @param e
	 *            事件
	 */
	private void subnetComboActionPerformed(ItemEvent e) {
		try {
			initSubnetCombo(this.subnetCombo);
		} catch (Exception e1) {
			ExceptionManage.dispose(e1, this.getClass());
		}
	}

	/**
	 * 初始化域下来列表
	 * 
	 * @param fieldCombo
	 *            JComboBox组件
	 */
	private void initFieldCombo(JComboBox fieldCombo) {
		FieldService_MB service = null;
		List<Field> fieldList = null;
		Field f = new Field();
		f.setFieldName("ALL");
		DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) fieldCombo
				.getModel();
		try {
			service = (FieldService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Field);
			fieldList = service.queryByUserId(ConstantUtil.user);
			for (Field field : fieldList) {
				defaultComboBoxModel.addElement(new ControlKeyValue(field
						.getId() + "", field.getFieldName(), field));
			}
			fieldCombo.setModel(defaultComboBoxModel);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
	}

	/**
	 * * 初始化域下来列表
	 * 
	 * @param subnetComboBox
	 *            JComboBox组件
	 */
	public void initSubnetCombo(JComboBox subnetComboBox) {
		DefaultComboBoxModel subnetComboBoxModel = null;
		SubnetService_MB service = null;
		List<Field> subnetList = new ArrayList<Field>();
		Field fieldCombo;
		try {
			if (null != this.fieldCombo.getSelectedItem()) {
				fieldCombo = (Field) ((ControlKeyValue) this.fieldCombo.getSelectedItem()).getObject();
				subnetComboBoxModel = new DefaultComboBoxModel();
				service = (SubnetService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SUBNETSERVICE);
				subnetList = (service.searchAndrefreshdata(fieldCombo));
				subnetComboBoxModel.addElement(new ControlKeyValue("",ResourceUtil.srcStr(StringKeysLbl.LBL_FIELD), null));
				for (Field subnet : subnetList) {
					subnetComboBoxModel.addElement(new ControlKeyValue(subnet.getId() + "", subnet.getFieldName(), subnet));
				}
				subnetComboBox.setModel(subnetComboBoxModel);
			}
			ControlKeyValue field = (com.nms.ui.manager.ControlKeyValue) this.fieldCombo.getSelectedItem();
			super.fieldId = Integer.parseInt(field.getId());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
	}
}