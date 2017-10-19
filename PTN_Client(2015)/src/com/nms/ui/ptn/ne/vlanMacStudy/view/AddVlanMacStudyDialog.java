﻿package com.nms.ui.ptn.ne.vlanMacStudy.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.SecondMacStudyService_MB;
import com.nms.model.ptn.path.eth.ElanInfoService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.CheckingUtil;
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
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.db.bean.ptn.CommonBean;
import com.nms.db.bean.ptn.SsMacStudy;
import com.nms.db.bean.ptn.VlanMacStudyInfo;
import com.nms.db.bean.ptn.path.eth.ElanInfo;
import com.nms.ui.ptn.ne.ssMacStudy.controller.StaticSecondStudyController;
import com.nms.ui.ptn.ne.vlanMacStudy.controller.VlanMcStudyController;


public class AddVlanMacStudyDialog extends PtnDialog {

	private static final long serialVersionUID = 1L;
	private JLabel portChoice;//端口
	private JComboBox portCom;
	private JLabel upLabel; // 错误消息文本显示label
	private JLabel vlanLabel; // vlan值	
	private PtnTextField vlanField;// vlan文本框
	private JLabel evlanLabel; // 
	private PtnTextField evlanField;// 
	
	private JLabel macStartLabel; // 
	private PtnTextField macStartField;// 
	
	private JLabel macNumLabel; // 
	private PtnTextField macNumField;// 
	
	private PtnButton confirm; // 确认按钮
	private JButton cancel; // 取消按钮
	private JButton addButton; // 增加按钮
	private JButton delButton; // 删除按钮
	private VlanMacStudyInfo mac = null;
	private JPanel btnPanel = null; // 按钮的面板
	// 规则详情
	private JScrollPane contentPanel;
	//private StaticSecondMacPanel sspanel;
	private JTable table;
	private JPanel conPanel;
	private JScrollPane tablePane;
	private JPanel btnPanel1 = null; // 按钮的面板
	private DefaultTableModel dtm = null;
	private PtnTextField ptnText= null;//mac地址
	private VlanMcStudyController controller;
	private AddVlanMacStudyDialog dialog ;
	/**
	 * 创建一个新的实例
	 * 
	 * @param panel
	 * 
	 * @param loginmanager
	 *            接入设置bean对象 如果是修改操作，传入对象。 新增传入null;
	 * @param loginmanagerPanel
	 *            接入设置列表页面
	 */
	public AddVlanMacStudyDialog(VlanMacStudyInfo mac, VlanMcStudyController controller) {
		this.setModal(true);
		try {
			this.mac = mac;
			this.controller = controller;			
			dialog = this;
			this.setModal(true);
			this.initComponents();
			this.setLayout();
			this.addListener();
//			this.initData();
//			this.bindingData();
			if (mac.getId() == 0) {
				this.setTitle(ResourceUtil.srcStr(StringKeysTip.TIT_CREATE_STATIC_MAC));
				initPortValue();
			} else {
				this.setTitle(ResourceUtil.srcStr(StringKeysTip.TIT_UPDATE_STATIC_MAC));
			}
			
			UiUtil.showWindow(this, 600, 320);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	
	
	
	private void initPortValue() {
		DefaultComboBoxModel defaultComboBoxModel = new DefaultComboBoxModel();
		ElanInfoService_MB elanInfoService_MB = null;
		try {
			elanInfoService_MB = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
			List<ElanInfo> elanInfos = elanInfoService_MB.selectVpls(ConstantUtil.siteId);
			for(ElanInfo elanInfo:elanInfos){
				defaultComboBoxModel.addElement(new ControlKeyValue(elanInfo.getId() + "",elanInfo.getName(), elanInfo));
			}
			this.portCom.setModel(defaultComboBoxModel);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(elanInfoService_MB);
		}
	}
	/**
	 * 修改时 绑定数据
	 * 
	 * @throws Exception
	 */
	private void bindingData() throws Exception {

		if (mac.getId()>0) {
			this.vlanField.setText(this.mac.getVlan()+"");
			this.setValueToPort();
		}

	}

	
	private void setValueToPort() {
		PortService_MB portService = null;
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			PortInst portInst1 = new PortInst();
			portInst1.setSiteId(ConstantUtil.siteId);
			portInst1.setPortId(mac.getPortId());
			portInst1 = portService.select(portInst1).get(0);		
			DefaultComboBoxModel defaultComboBoxModel = new DefaultComboBoxModel();			
			PortInst portInst = new PortInst();
			portInst.setSiteId(ConstantUtil.siteId);
			List<PortInst> portInstList = portService.select(portInst);
			for (PortInst inst : portInstList) {
				if(inst.getPortType().equals("NONE")|| inst.getPortType().equals("UNI") ||inst.getPortType().equals("NNI")){
						defaultComboBoxModel.addElement(new ControlKeyValue(inst.getPortId() + "",inst.getPortName(), inst));
				}
			}			
			this.portCom.setModel(defaultComboBoxModel);
			this.portCom.getModel().setSelectedItem(new ControlKeyValue(portInst1.getPortId() + "", portInst1.getPortName(), portInst1));
	
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(portService);
		}
	}
	/**
	 * 界面初始化数据数据
	 * 
	 * @throws Exception
	 */
	private void initData() throws Exception {
		initType();

	}

	/*
	 * 初始化下拉菜单
	 */
	private void initType() {
		

		table.setModel(new DefaultTableModel(new Object[][] {

		},

		new String[] { ResourceUtil.srcStr(StringKeysTip.TIP_MAC_ADDRESS) }));
		if (mac.getId()>0) {
			dtm = (DefaultTableModel) table.getModel();
			String []macs= mac.getMacAddress().split("\\|");
			for(int i = 0; i< macs.length; i++)
			{				
				dtm.addRow(new Object[]{macs[i]});
			}
		
			dtm.fireTableDataChanged();				
			ptnText  = new PtnTextField();				
			TableColumn tableColum1 = table.getColumnModel().getColumn(0);
			tableColum1.setCellEditor(new DefaultCellEditor(ptnText));

		}

	}

	/**
	 * 初始化控件
	 */
	public void initComponents() {

		try {
			
			this.upLabel = new javax.swing.JLabel("");
			this.vlanLabel = new javax.swing.JLabel("lan id");
			this.evlanLabel = new javax.swing.JLabel("elan id");
			this.confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM), true);					
			vlanField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.upLabel, this.confirm, this);
			setValidate(vlanField,ConstantUtil.LABOAMETNVLAN_MAXVALUE,ConstantUtil.LABOAMETNVLAN_MINVALUE);
			vlanField.setText("1"); 
			evlanField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.upLabel, this.confirm, this);
			setValidate(evlanField,ConstantUtil.LABOAMETNVLAN_MAXVALUE,ConstantUtil.LABOAMETNVLAN_MINVALUE);
			evlanField.setText("1"); 
			
			macStartLabel = new javax.swing.JLabel("mac start");
			macStartField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.upLabel, this.confirm, this);
			setValidate(macStartField,ConstantUtil.LABOAMETNVLAN_MAXVALUE,ConstantUtil.LABELWaitTime_MINVALUE);
			macStartField.setText("0"); 
			
			macNumLabel = new javax.swing.JLabel("mac num");
			macNumField = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.upLabel, this.confirm, this);
			setValidate(macNumField,ConstantUtil.LABOAMETNVLAN_MAXVALUE,ConstantUtil.LABOAMETNVLAN_MINVALUE);
			macNumField.setText("1");
			
			
			this.cancel = new javax.swing.JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
			this.btnPanel = new JPanel();
			this.btnPanel1 = new JPanel();
			portChoice = new JLabel(ResourceUtil.srcStr(StringKeysBtn.BTN_PORT_SELECT));
			portCom = new JComboBox();			
//			
//			this.addButton = new javax.swing.JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_ADD)); // 增加按钮
//			this.delButton = new javax.swing.JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_REMOVE)); // 删除按钮			
//			table = new JTable();
//			tablePane = new JScrollPane(table);
			conPanel = new JPanel();
			contentPanel = new JScrollPane();
//			table.getTableHeader().setResizingAllowed(true);
//			tablePane.setPreferredSize(new Dimension(320, 200));
//			tablePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
//			tablePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//			contentPanel.setViewportView(conPanel);
//			contentPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTitle.TIT_MAC_ADDRESS)));
//			contentPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
//			contentPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}

	/**
	 * 设置布局
	 */
	private void setLayout() {
		this.setBtnLayout();
//		this.setContBagLayout();
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 40, 40, 40, 40,40,40 };
		layout.columnWeights = new double[] { 0, 0, 0, 0,0,0.1 };
		layout.rowHeights = new int[] { 20, 20, 20, 20, 20,20,40 };
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0,0 };
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;

		// 第一行 错误消息
	    c.gridx =1;
		c.gridy =0;
		layout.addLayoutComponent(this.upLabel, c);
		this.add(upLabel);

		// 第二行 端口名称
		c.gridx = 0;
		c.gridy = 1;
		layout.setConstraints(this.portChoice, c);
		this.add(portChoice);

		c.gridx = 1;
		c.gridy = 1;
		layout.setConstraints(this.portCom, c);
		this.add(portCom);


		// 第3行 vlan值
		c.gridx = 0;
		c.gridy = 2;
		layout.setConstraints(this.vlanLabel, c);
		this.add(vlanLabel);
		c.gridx = 1;
		layout.setConstraints(this.vlanField, c);
		this.add(vlanField);
		
		// 第4行 vlan值
		c.gridx = 0;
		c.gridy = 3;
		layout.setConstraints(this.evlanLabel, c);
		this.add(evlanLabel);
		c.gridx = 1;
		layout.setConstraints(this.evlanField, c);
		this.add(evlanField);
		
		c.gridx = 0;
		c.gridy = 4;
		layout.setConstraints(this.macStartLabel, c);
		this.add(macStartLabel);
		c.gridx = 1;
		layout.setConstraints(this.macStartField, c);
		this.add(macStartField);
		
		c.gridx = 0;
		c.gridy = 5;
		layout.setConstraints(this.macNumLabel, c);
		this.add(macNumLabel);
		c.gridx = 1;
		layout.setConstraints(this.macNumField, c);
		this.add(macNumField);
		

		// 第五行 按钮
		c.gridy = 6;
		layout.setConstraints(this.btnPanel, c);
		this.add(btnPanel);

	}

	// conPanel布局
	private void setContBagLayout() {
//		this.setBtn1Layout();
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 120, 20 };
		layout.columnWeights = new double[] { 0, 0.1 };
		layout.rowHeights = new int[] { 40 };
		layout.rowWeights = new double[] { 0 };
		this.conPanel.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		layout.setConstraints(tablePane, c);
		this.conPanel.add(tablePane);

		c.gridx = 1;
		c.insets = new Insets(0, 5, 5, 0);
		layout.setConstraints(btnPanel1, c);
		this.conPanel.add(btnPanel1);

	}

	private void setBtn1Layout() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 30 };
		layout.columnWeights = new double[] { 0.1 };
		layout.rowHeights = new int[] { 40, 40, 40, 40 };
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0 };
		this.btnPanel1.setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;

		layout.addLayoutComponent(this.addButton, c);
		this.btnPanel1.add(addButton);
		c.gridy = 1;

		layout.addLayoutComponent(this.delButton, c);
		this.btnPanel1.add(delButton);
		c.gridy = 2;

	}

	/**
	 * 设置按钮布局
	 */
	private void setBtnLayout() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 200, 70, 70 };
		layout.columnWeights = new double[] { 0.1, 0, 0 };
		layout.rowHeights = new int[] { 40 };
		layout.rowWeights = new double[] { 0 };
		this.btnPanel.setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		layout.addLayoutComponent(this.confirm, c);
		this.btnPanel.add(confirm);
		c.gridx = 2;
		layout.addLayoutComponent(this.cancel, c);
		this.btnPanel.add(cancel);
	}

	public PtnButton getConfirm() {
		return confirm;
	}

	/**
	 * 添加监听
	 */
	private void addListener() {

		confirm.addActionListener(new MyActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				dialog.saveUser();

			}

			@Override
			public boolean checking() {
				return true;
			}
		});

//		// 添加一行
//		addButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// TODO Auto-generated method stub
//				dtm = (DefaultTableModel) table.getModel();
//				int rowcount = table.getRowCount() + 1;
//				dtm.addRow(new Object[rowcount]);	
//				ptnText = new PtnTextField();					
//				TableColumn tableColum1 = table.getColumnModel().getColumn(0);
//				tableColum1.setCellEditor(new DefaultCellEditor(ptnText));
//			    table.setValueAt("00-00-00-00-00-00", table.getRowCount()-1, 0);
//				table.invalidate();
//				dtm.fireTableDataChanged();
//			}
//		});
//
//		// 删除行
//		delButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				int row = table.getSelectedRow();
//				dtm = (DefaultTableModel) table.getModel();
//				if (row == -1) {
//					DialogBoxUtil.confirmDialog(conPanel, ResourceUtil.srcStr(StringKeysLbl.LBL_NAMERULE_SELECT));
//					return;
//				} else {
//					dtm.removeRow(row);
//					dtm.fireTableDataChanged();
//					dtm.fireTableStructureChanged();
//				}
//			}
//		});
//
//	
//
//		
//
		// 取消按钮
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();

			}
		});
	}

		
	// 保存信息用户信息
	private void saveUser() {
		SiteService_MB siteService = null;
		SiteInst siteInst = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			siteInst = siteService.select(ConstantUtil.siteId);
			String vsid = ((ControlKeyValue) this.portCom.getSelectedItem()).getId();		
			String vlan = this.vlanField.getText().toString();
			String evlan = this.evlanField.getText().toString();
			String macstart = this.macStartField.getText().toString();
			String macnum = this.macNumField.getText().toString();
			
			List<String> values = new ArrayList<String>();
			values.add(macstart);
			values.add(macnum);
			values.add(vsid);
			values.add(vlan);
			values.add(evlan);
			
			try {
				DispatchUtil smsDispatch = new DispatchUtil(RmiKeys.RMI_SITE);	
				String result = smsDispatch.vlanMac(siteInst, values);	
			} catch (Exception e) {
				// TODO: handle exception
			}
			// 跟新界面
			controller.refresh();	
			// 隐藏界面
			dialog.dispose();
		} catch (Exception e1) {
			e1.printStackTrace();
		}finally {
			UiUtil.closeService_MB(siteService);
		}
	}
	
	private void insertOpeLog(int operationType, String result, VlanMacStudyInfo oldMac, VlanMacStudyInfo newMac){
		AddOperateLog.insertOperLog(confirm, operationType, result, oldMac, newMac, newMac.getSiteId(),ResourceUtil.srcStr(StringKeysTitle.TIT_STATIC_SECOND_MAC),"ssMacStudy");		
	}
	
	
	/*private int getMacId(SsMacStudy ssMacStudy)
	{
		List<Integer> macIds = null;
		SecondMacStudyService_MB secondMacStudyService = null;
		try {
			secondMacStudyService = (SecondMacStudyService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SECONDMACSTUDY);
			// 根据siteId查询数据库
			macIds = secondMacStudyService.queryMacId(ssMacStudy.getSiteId());
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(secondMacStudyService);
		}

		if(macIds.size() == 0)
		{
			return 1;
		}

		for(int i = 1; i <= macIds.size(); i++)
		{
			if(macIds.contains(i))
			{
				continue;
		}
			else
			{
				return i;
			}
		}
		
		return macIds.size()+1;
	}*/
	/**
	 * 为文本控件赋最大值和最小值
	 * @param txtField
	 * @param max
	 * @param min
	 * @throws Exception
	 */
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
}
