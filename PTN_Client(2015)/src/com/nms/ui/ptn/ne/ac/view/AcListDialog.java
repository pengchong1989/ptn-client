﻿/*
 * AddElanDialog.java
 *
 * Created on __DATE__, __TIME__
 */

package com.nms.ui.ptn.ne.ac.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import twaver.Element;
import twaver.Node;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.path.eth.ElanInfo;
import com.nms.db.bean.ptn.path.eth.EtreeInfo;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.enums.EActiveStatus;
import com.nms.db.enums.EManufacturer;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.port.AcPortInfoService_MB;
import com.nms.model.util.Services;
import com.nms.ui.frame.ViewDataTable;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.business.dialog.dualpath.AddDualMenu;
import com.nms.ui.ptn.ne.ac.controller.AcHandlerController;

/**
 * 
 * @author __USER__
 */
public class AcListDialog extends javax.swing.JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SiteInst siteInst;
	private JScrollPane jscrollPane_ac;
	private ViewDataTable<AcPortInfo> acTable; // 选择的叶子table
	private final String ACTABLENAME = "acTableWH";
	private JPanel buttonPanel;
	private JButton btnAdd;
	private JButton btnSelect;
	private AcPortInfo acPortInfo;
	private List<AcPortInfo> acPortInfoList;
	private AddDualMenu addDualMenu;
	private Element element;
	private Node node;
	private AcListDialog acListDialog;
	private int label = 0;// (0)用于标记是elan/etree(支持多条AC)  还是dual(只需要一条AC)label ==1
	private Object object = null;
	

	public AcListDialog(boolean modal, Element siteElement,int label) {
		try {
			super.setModal(modal);
			this.label = label;
			this.setTitle(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_LIST));
			initComponents();
			this.setLayout();
			SiteInst siteInst = (SiteInst) siteElement.getUserObject();
			this.siteInst = siteInst;
			initAcListData();
			this.addListeners();
			UiUtil.showWindow(this, 600, 350);

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	public AcListDialog(boolean modal, Element siteElement,int label,Node node,AddDualMenu addDualMenu) {
		try {
			acListDialog = this;
			this.addDualMenu = addDualMenu;
			this.node = node;
			this.element = siteElement;
			super.setModal(modal);
			this.label = label;
			this.setTitle(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_LIST));
			initComponents();
			this.setLayout();
			SiteInst siteInst = (SiteInst) siteElement.getUserObject();
			this.siteInst = siteInst;
			initAcListData();
			this.addListeners();
			UiUtil.showWindow(this, 600, 350);

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}
	
	public AcListDialog(boolean modal, Element siteElement,Object object) {
		try {
			super.setModal(modal);
			this.setTitle(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_LIST));
			initComponents();
			this.setLayout();
			SiteInst siteInst = (SiteInst) siteElement.getUserObject();
			this.siteInst = siteInst;
			this.object = object;
			initAcListData();
			this.addListeners();

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}
	
	public AcListDialog(boolean modal, Element siteElement,int label,Object object) {
		try {
			super.setModal(modal);
			this.label = label;
			this.setTitle(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_LIST));
			initComponents();
			this.setLayout();
			SiteInst siteInst = (SiteInst) siteElement.getUserObject();
			this.siteInst = siteInst;
			this.object = object;
			initAcListData();
			this.addListeners();
			UiUtil.showWindow(this, 600, 350);

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	/**
	 * 刷新界面数据
	 */
	private void initAcListData() {
		AcPortInfoService_MB acInfoService = null;
		List<AcPortInfo> acPortInfoList = null;
		List<AcPortInfo> addAcPortInfos = null;
		AcPortInfo acPortInfo;
		List<AcPortInfo> acPortUser = null;
		try {
			this.acTable.clear();
			acPortInfo = new AcPortInfo();
			acPortInfo.setSiteId(this.siteInst.getSite_Inst_Id());
			acPortInfo.setAcStatus(EActiveStatus.ACTIVITY.getValue());
			acInfoService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			acPortInfoList = acInfoService.queryByAcPortInfo(acPortInfo);
			/*******修改Elan和Etree业务时不影响原来的端口******/
			acPortUser = getUserAc(acInfoService);
			
			if (acPortInfoList != null && acPortInfoList.size() > 0) {
				addAcPortInfos = new ArrayList<AcPortInfo>();
				for (AcPortInfo acInfo : acPortInfoList) {
					if (acInfo.getIsUser() == 0) {
						addAcPortInfos.add(acInfo);
					}
				}
				if(!acPortUser.isEmpty())
				{
					addAcPortInfos.addAll(acPortUser);
				}
				this.acTable.initData(addAcPortInfos);
			}
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(acInfoService);
		}
	}

	
	private List<AcPortInfo> getUserAc(AcPortInfoService_MB acInfoService )
	{
		UiUtil uiUtil = null;
		List<AcPortInfo> acPortUser = new ArrayList<AcPortInfo>();
		List<Integer> acIds = null;
		Set<Integer> acSet  = null;
		try {
			/*******修改Elan业务不影响端口******/
			acSet = new HashSet<Integer>();
			uiUtil = new UiUtil();
			/********修改etree业务不影响端口*********/
		    if(this.object != null && this.object instanceof List)
			{
				Object obj = ((List)this.object).get(0);
				if(obj instanceof EtreeInfo)
				{
					List<EtreeInfo> etreeInfos = (List<EtreeInfo>) this.object;
					for(EtreeInfo info : etreeInfos)
					{
						if(info.getRootSite() == this.siteInst.getSite_Inst_Id())
						{
							acSet.addAll(uiUtil.getAcIdSets(info.getAmostAcId()));
						}else if(info.getBranchSite() == this.siteInst.getSite_Inst_Id())
						{
							acSet.addAll(uiUtil.getAcIdSets(info.getZmostAcId()));
						}
					}	
				}else if(obj instanceof ElanInfo)
				{
					List<ElanInfo> elanInfos = (List<ElanInfo>) this.object;
					for(ElanInfo info : elanInfos)
					{
						if(this.siteInst.getSite_Inst_Id() == info.getaSiteId())
						{
							acSet.addAll(uiUtil.getAcIdSets(info.getAmostAcId()));
						}else if(this.siteInst.getSite_Inst_Id() == info.getzSiteId())
						{
							acSet.addAll(uiUtil.getAcIdSets(info.getZmostAcId()));
						}
					}	
				}
			}
			acIds = new ArrayList<Integer>(acSet);
			if(acIds != null && !acIds.isEmpty())
			{
				acPortUser = acInfoService.select(acIds);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}finally
		{
			uiUtil = null;	
			acIds = null;
			acSet = null;
		}
		return acPortUser;
	}
	
	
	
	/**
	 * 添加监听
	 */
	private void addListeners() {
		this.btnSelect.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				okBtnActionPerformed();
			}
		});
		this.btnAdd.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				createAc();
			}
		});
		
		if(addDualMenu != null){
			acListDialog.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					addDualMenu.cancleConfig(element, node);
					acListDialog.dispose();
				}
			});
		}
	}

	/**
	 * 新建按钮事件，创建AC
	 */
	private void createAc() {
		AddACDialog dialog = null;
		AcHandlerController addacController = null;
		SiteService_MB siteService = null;
		try {
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			dialog = new AddACDialog(null,this.siteInst.getSite_Inst_Id());
			addacController = new AcHandlerController(null, dialog, this.siteInst.getSite_Inst_Id());
			addacController.initData(true,this.siteInst.getSite_Inst_Id());
			if (siteService.getManufacturer(siteInst.getSite_Inst_Id()) == EManufacturer.WUHAN.getValue()) {
				dialog.getStep1().setVisible(true);
			} else {
				dialog.getStep1_cx().setVisible(true);
			}

			// 刷新列表
			this.initAcListData();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
		}
	}

	private void initComponents() {
		
		
		// ac端口的tabel
		this.acTable = new ViewDataTable<AcPortInfo>(this.ACTABLENAME);
		this.acTable.getTableHeader().setResizingAllowed(true);
		this.acTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		this.acTable.setTableHeaderPopupMenuFactory(null);
		this.acTable.setTableBodyPopupMenuFactory(null);
		this.jscrollPane_ac = new JScrollPane();
		this.jscrollPane_ac.setViewportView(this.acTable);

		this.buttonPanel = new JPanel();
		this.btnAdd = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_ADD));
		this.btnSelect = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM));

	}

	/**
	 * 设置布局
	 */
	private void setLayout() {
		this.setButtonLayout();
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 600 };
		layout.columnWeights = new double[] { 0.1 };
		layout.rowHeights = new int[] { 300, 40 };
		layout.rowWeights = new double[] { 0.1, 0 };
		this.setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(this.jscrollPane_ac, c);
		this.add(this.jscrollPane_ac);

		c.fill = GridBagConstraints.NONE;
		c.gridy = 1;
		layout.setConstraints(this.buttonPanel, c);
		this.add(this.buttonPanel);
	}

	/**
	 * 设置按钮布局
	 */
	private void setButtonLayout() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 460, 70, 70 };
		layout.columnWeights = new double[] { 1, 0, 0 };
		layout.rowHeights = new int[] { 40 };
		layout.rowWeights = new double[] { 0 };
		this.buttonPanel.setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(this.btnAdd, c);
		this.buttonPanel.add(this.btnAdd);

		c.gridx = 2;
		layout.setConstraints(this.btnSelect, c);
		this.buttonPanel.add(this.btnSelect);
	}
	
	public ViewDataTable<AcPortInfo> getAcTable() {
		return acTable;
	}

	public void setAcTable(ViewDataTable<AcPortInfo> acTable) {
		this.acTable = acTable;
	}

	/**
	 * 确认按钮事件
	 */
	private void okBtnActionPerformed() {
		//是dual
		if(label == 1)
		{
			if (this.acTable.getAllSelect().size() == 1) {
				this.setAcPortInfo(this.acTable.getSelect());
				this.dispose();
			} else {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
				return;
			}
		}else
		{
			if (this.acTable.getAllSelect().size() >0) {
//				this.setAcPortInfo(this.acTable.getSelect());
				this.setAcPortInfoList(this.acTable.getAllSelect());
				this.dispose();
			} else {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
				return;
			}
		}
	}

	public AcPortInfo getAcPortInfo() {
		return acPortInfo;
	}

	public void setAcPortInfo(AcPortInfo acPortInfo) {
		this.acPortInfo = acPortInfo;
	}

	public List<AcPortInfo> getAcPortInfoList() {
		return acPortInfoList;
	}

	public void setAcPortInfoList(List<AcPortInfo> acPortInfoList) {
		this.acPortInfoList = acPortInfoList;
	}
	

}