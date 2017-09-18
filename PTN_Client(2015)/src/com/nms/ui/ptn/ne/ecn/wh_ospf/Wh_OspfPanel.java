﻿package com.nms.ui.ptn.ne.ecn.wh_ospf;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.ecn.OSPFinfo_wh;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.ecn.Ospf_whService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.CheckingUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnPanel;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;

public class Wh_OspfPanel extends PtnPanel{
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private JLabel portJLabel;
	private JTable portJTable;
	private JScrollPane portJScrollPane;
	private JButton portaddButton;
	private JButton portdeleteButton;
	private JLabel fieldJLabel;
	private JTable fieldJTable;
	private JScrollPane fieldJScrollPane;
	private JButton fieldaddButton;
	private JButton fielddeleteButton;
	private List<OSPFinfo_wh> ospFinfoWhs;
	private PtnButton okButton;
	private PtnButton syncButton;
	private JPanel contentPanel;
	private JTabbedPane tabPanel;
	private JPanel buttonPanel;
	private PtnButton routePtnButton;
	private long time = 0;
	
	public Wh_OspfPanel() {
		super.setName(ResourceUtil.srcStr(StringKeysMenu.MENU_PORT_2LAYER_ATTR));
		try {
			this.initComponents();
			this.setLayout();
			this.initData();
			this.addListeners();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void initComponents() {
		this.tabPanel = new JTabbedPane();
		contentPanel = new JPanel();
		buttonPanel = new JPanel();
		portJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_OSPF));
		portJTable = new JTable();
		portJScrollPane = new  JScrollPane();
		portJScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		portJScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		portJScrollPane.setViewportView(portJTable);
		portaddButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_ADD));
		portdeleteButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_DELETE));
		fieldJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_OSPF));
		fieldJTable = new JTable();
		fieldJScrollPane = new JScrollPane();
		fieldJScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		fieldJScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		fieldJScrollPane.setViewportView(fieldJTable);
		fieldaddButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_ADD));
		fielddeleteButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_DELETE));
		
		okButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIG), true);
		syncButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SYNCHRO), true);
		routePtnButton = new PtnButton(ResourceUtil.srcStr(StringKeysLbl.LBL_ROUTE_IN), true);
		initTable();
		setTbaleModel();
	}

	private void initTable(){
		portJTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] { ResourceUtil.srcStr(StringKeysObj.ORDER_NUM), "IP","MASK","PORT NAME" ,"VLAN","FLOW","MODEL"}) {

			private static final long serialVersionUID = 714435537424144075L;
			Class[] types = new Class[] { java.lang.Object.class, java.lang.Object.class ,java.lang.Object.class, java.lang.Object.class ,java.lang.Object.class, java.lang.Object.class ,java.lang.Object.class};

			@Override
			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if (columnIndex == 0 )
					return false;
				return true;
			}

		});
		portJTable.getTableHeader().setResizingAllowed(true);
		portJTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);

		TableColumn c = portJTable.getColumnModel().getColumn(0);
		c.setPreferredWidth(30);
		c.setMaxWidth(30);
		c.setMinWidth(30);
		
		fieldJTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] { ResourceUtil.srcStr(StringKeysObj.ORDER_NUM),"AREA","PORT NAME","ENABLE" }) {

			private static final long serialVersionUID = 714435537424144075L;
			Class[] types = new Class[] { java.lang.Object.class, java.lang.Object.class ,java.lang.Object.class,java.lang.Object.class};

			@Override
			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if (columnIndex == 0)
					return false;
				return true;
			}

		});
		fieldJTable.getTableHeader().setResizingAllowed(true);
		fieldJTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		TableColumn c1 = fieldJTable.getColumnModel().getColumn(0);
		c1.setPreferredWidth(30);
		c1.setMaxWidth(30);
		c1.setMinWidth(30);
	}
	
	private void setTbaleModel() {
		JComboBox nameJComboBox = new JComboBox();
		nameJComboBox.addItem("LOOPback0");
		nameJComboBox.addItem("NMS");
		nameJComboBox.addItem("PCIE2");
		nameJComboBox.addItem("PCIE3");
		TableColumn nameCountColumnport = portJTable.getColumn("PORT NAME");
		nameCountColumnport.setCellEditor(new DefaultCellEditor(nameJComboBox));
		JComboBox modelJComboBox = new JComboBox();
		modelJComboBox.addItem("POINTOMULTIPOINT");
		modelJComboBox.addItem("OTHER");
		TableColumn pinCountColumn = portJTable.getColumn("MODEL");
		pinCountColumn.setCellEditor(new DefaultCellEditor(modelJComboBox));
		
		
		TableColumn nameCountColumnfield = fieldJTable.getColumn("PORT NAME");
		nameCountColumnfield.setCellEditor(new DefaultCellEditor(nameJComboBox));
		JComboBox jCheckBox = new JComboBox();
		jCheckBox.addItem("Enable");
		jCheckBox.addItem("Disenable");
		TableColumn enableCountColumn = fieldJTable.getColumn("ENABLE");
		enableCountColumn.setCellEditor(new DefaultCellEditor(jCheckBox));
	}
	
	

	
	private void setLayout() {
		setMainLayout();
		setGridBagLayout();
	}
	
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
	
	private void setGridBagLayout() {
		this.contentPanel.setBackground(Color.WHITE);

		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 40, 40, 40, 40 ,40,40};
		layout.columnWeights = new double[] { 0, 0, 0.5, 0,0,0 };
		layout.rowHeights = new int[] { 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 20, 35, 35 };
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.2 };
		this.contentPanel.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();

		/** 第一行 网元描述 */
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.portJLabel, c);
		this.contentPanel.add(this.portJLabel);
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 3;
		c.gridwidth = 3;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.BOTH;
//		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.portJScrollPane, c);
		this.contentPanel.add(this.portJScrollPane);
		
		c.gridx = 4;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.NONE;
		layout.setConstraints(this.portaddButton, c);
		this.contentPanel.add(this.portaddButton);
		
		c.gridx = 4;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.NONE;
		layout.setConstraints(this.portdeleteButton, c);
		this.contentPanel.add(this.portdeleteButton);

		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.fieldJLabel, c);
		this.contentPanel.add(this.fieldJLabel);
		c.gridx = 1;
		c.gridy = 6;
		c.gridheight = 3;
		c.gridwidth =3;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.BOTH;
//		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.fieldJScrollPane, c);
		this.contentPanel.add(this.fieldJScrollPane);
		
		c.gridx = 4;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.NONE;
		layout.setConstraints(this.fieldaddButton, c);
		this.contentPanel.add(this.fieldaddButton);
		
		c.gridx = 4;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.NONE;
		layout.setConstraints(this.fielddeleteButton, c);
		this.contentPanel.add(this.fielddeleteButton);

		c.gridx = 3;
		c.gridy = 11;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.NONE;
		layout.setConstraints(this.buttonPanel, c);
		this.contentPanel.add(this.buttonPanel);
		buttonPanel.add(routePtnButton);
		buttonPanel.add(syncButton);
		buttonPanel.add(okButton);
	}

	private void initData() {
		Ospf_whService_MB ospfWhServiceMB = null;
		try {
			ospfWhServiceMB = (Ospf_whService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OSPF_WH);
			ospFinfoWhs = ospfWhServiceMB.selectBySiteid(ConstantUtil.siteId);
			if(ospFinfoWhs.size()>0){
				DefaultTableModel portmodel = (DefaultTableModel) portJTable.getModel();
				DefaultTableModel fieldmodel = (DefaultTableModel) fieldJTable.getModel();
				for (int i = 0; i < ospFinfoWhs.size(); i++) {
					OSPFinfo_wh ospFinfoWh = ospFinfoWhs.get(i);
					Code portNameCode = UiUtil.getCodeByValue("OSPFNAME", ospFinfoWh.getPortType()+"");
					if(ospFinfoWh.getOspfType() ==11){
						Code modelCode = UiUtil.getCodeByValue("OSPFMODEL", ospFinfoWh.getPortModel()+"");
						Object[] object = new Object[]{""+(portmodel.getRowCount()+1),ospFinfoWh.getIp(),ospFinfoWh.getMask(),portNameCode.getCodeName(),
								ospFinfoWh.getVlanValues(),ospFinfoWh.getFolw(),modelCode.getCodeName()};
						portmodel.addRow(object);
					}else{
						Code code = UiUtil.getCodeByValue("ENABLEDSTATUE", ospFinfoWh.getEnable()+"");
						Object[] object = new Object[]{""+(fieldmodel.getRowCount()+1),ospFinfoWh.getIp(),portNameCode.getCodeName(),code.getCodeENName()};
						fieldmodel.addRow(object);
					}
				}
				portJTable.setModel(portmodel);
				fieldJTable.setModel(fieldmodel);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(ospfWhServiceMB);
		}
	}
	
	private void addListeners() {
		portaddButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model = (DefaultTableModel) portJTable.getModel();
				Object[] object = new Object[]{""+(model.getRowCount()+1),"0.0.0.0","32","LOOPback0","2","3","OTHER"};
				model.addRow(object);
				portJTable.setModel(model);
			}
		});
		
		portdeleteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model = (DefaultTableModel) portJTable.getModel();
				int[] integers = portJTable.getSelectedRows();
				for (int i = 0; i < integers.length; i++) {
					model.removeRow(integers[i]-i);
				}
				model.fireTableDataChanged();
				portJTable.updateUI();
			}
		});
		
		fieldaddButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model = (DefaultTableModel) fieldJTable.getModel();
				Object[] object = new Object[]{""+(model.getRowCount()+1),"0.0.0.0","LOOPback0","Enable"};
				model.addRow(object);
				fieldJTable.setModel(model);
			}
		});
		
		fielddeleteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model = (DefaultTableModel) fieldJTable.getModel();
				int[] integers = fieldJTable.getSelectedRows();
				for (int i = 0; i < integers.length; i++) {
					model.removeRow(integers[i]-i);
				}
				model.fireTableDataChanged();
				fieldJTable.updateUI();
			}
		});
		
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		
		syncButton.addActionListener(new MyActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				syncAction();
			}
			
			@Override
			public boolean checking() {
				return true;
			}
		});
		
		routePtnButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				routeIn();
			}
		});
	}

	private void routeIn(){
		try {
			DispatchUtil dispatchUtil = new DispatchUtil(RmiKeys.RMI_SITE);
			String reslut = dispatchUtil.routeIn();
			AddOperateLog.insertOperLog(routePtnButton, EOperationLogType.OSPFSYNCHRO.getValue(), reslut, 
					null, "", ConstantUtil.siteId, "OSPF", "OSPF");
			DialogBoxUtil.succeedDialog(this, reslut);
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	private void syncAction() {
		try {
			DispatchUtil dispatchUtil = new DispatchUtil(RmiKeys.RMI_OSPFINFO);
			String reslut = dispatchUtil.synchro(ConstantUtil.siteId);
			AddOperateLog.insertOperLog(okButton, EOperationLogType.OSPFSYNCHRO.getValue(), reslut, 
					null, "", ConstantUtil.siteId, "OSPF", "OSPF");
			DialogBoxUtil.succeedDialog(this, reslut);
			DefaultTableModel portmodel = (DefaultTableModel) portJTable.getModel();
			DefaultTableModel fieldmodel = (DefaultTableModel) fieldJTable.getModel();
			int rowcount = portmodel.getRowCount();
			for (int i = 0; i < rowcount; i++) {
				portmodel.removeRow(0);
			}
			int rowcount2 = fieldmodel.getRowCount();
			for (int i = 0; i < rowcount2; i++) {
				fieldmodel.removeRow(0);
			}
			this.initData();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	private void save(){
		if(portJTable.isEditing()){
			portJTable.getCellEditor().stopCellEditing();
		}
		if(fieldJTable.isEditing()){
			fieldJTable.getCellEditor().stopCellEditing();
		}
		if(time>0 && !(System.currentTimeMillis() - time>1000*30)){
			DialogBoxUtil.errorDialog(this,ResourceUtil.srcStr(StringKeysLbl.LBL_NOT_ACTION));
			return;
		}
		SiteService_MB siteServiceMB = null;
		try {
			siteServiceMB = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			SiteInst siteInst = siteServiceMB.selectById(ConstantUtil.siteId);
			if(siteInst.getSn()== null || siteInst.getSn().equals("")){
				DialogBoxUtil.errorDialog(this, "NO SN");
				return;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(siteServiceMB);
		}
		DefaultTableModel ports = (DefaultTableModel) portJTable.getModel();
		Vector vector = ports.getDataVector();
		Iterator iterator = vector.iterator();
		List<OSPFinfo_wh> ospFinfoWhs = new ArrayList<OSPFinfo_wh>();
		while(iterator.hasNext()){
			Vector temp = (Vector) iterator.next();
			OSPFinfo_wh ospFinfoWh = new OSPFinfo_wh();
			if(!(temp.get(1)+"").matches("^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$")){
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_IP_ERROR));
				return ;
			}
			ospFinfoWh.setIp(temp.get(1)+"");
			int value = 0;
			try {
				value = Integer.parseInt(temp.get(2)+"");
				if(value<1 && value>32){
					DialogBoxUtil.errorDialog(this, "MASK:1-32");
					return ;
				}
			} catch (Exception e) {
				DialogBoxUtil.errorDialog(this, "MASK:1-32");
				return ;
			}
			
			ospFinfoWh.setMask(temp.get(2)+"");
			ospFinfoWh.setPortType(getPortName(temp.get(3)+""));
			try {
				value = Integer.parseInt(temp.get(4)+"");
				if(value<2 && value>4094){
					DialogBoxUtil.errorDialog(this, "VLAN:2-4094");
					return ;
				}
			} catch (Exception e) {
				DialogBoxUtil.errorDialog(this, "VLAN:2-4094");
				return ;
			}
			ospFinfoWh.setVlanValues(Integer.parseInt(temp.get(4)+""));
			try {
				value = Integer.parseInt(temp.get(5)+"");
				if(value<2 && value>13){
					DialogBoxUtil.errorDialog(this, "FLOW:3-12");
					return ;
				}
			} catch (Exception e) {
				DialogBoxUtil.errorDialog(this, "FLOW:3-12");
				return ;
			}
			
			ospFinfoWh.setFolw(Integer.parseInt(temp.get(5)+""));
			ospFinfoWh.setPortModel(getPortModel(temp.get(6)+""));
			ospFinfoWh.setSiteId(ConstantUtil.siteId);
			ospFinfoWh.setOspfType(11);
			ospFinfoWhs.add(ospFinfoWh);
		}
		
		DefaultTableModel field = (DefaultTableModel) fieldJTable.getModel();
		Vector vectorfield = field.getDataVector();
		Iterator iteratorfield = vectorfield.iterator();
		while(iteratorfield.hasNext()){
			Vector temp = (Vector) iteratorfield.next();
			OSPFinfo_wh ospFinfoWh = new OSPFinfo_wh();
			if(!(temp.get(1)+"").matches("^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$")){
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NO_AREA));
				return ;
			}
			ospFinfoWh.setIp(temp.get(1)+"");
			ospFinfoWh.setPortType(getPortName(temp.get(2)+""));
			ospFinfoWh.setEnable((temp.get(3)+"").equals("Enable")?1:0);
			ospFinfoWh.setSiteId(ConstantUtil.siteId);
			ospFinfoWh.setOspfType(23);
			ospFinfoWhs.add(ospFinfoWh);
		}
		if(ospFinfoWhs.size() == 0){
			OSPFinfo_wh ospFinfoWh = new OSPFinfo_wh();
			ospFinfoWh.setSiteId(ConstantUtil.siteId);
			ospFinfoWh.setHas(false);
			ospFinfoWhs.add(ospFinfoWh);
		}
		try {
			DispatchUtil dispatchUtil = new DispatchUtil(RmiKeys.RMI_OSPFINFO);
			String reslut = dispatchUtil.excuteInsert(ospFinfoWhs);
			time = System.currentTimeMillis();
			AddOperateLog.insertOperLog(okButton, EOperationLogType.OSPFINSERT.getValue(), reslut, 
					null, ospFinfoWhs, ConstantUtil.siteId, "OSPF", "ospf_wh");
			DialogBoxUtil.succeedDialog(this, reslut);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		
	}
	
	private boolean checkIP(String ip){
		boolean b = false;
		
		return b;
	}
	private int getPortName(String value){
		int i = 0;
		if(value.equals("LOOPback0")){
			i = 1;
		}else if(value.equals("NMS")){
			i = 2;
		}else if(value.equals("PCIE2")){
			i = 3;
		}else if(value.equals("PCIE3")){
			i = 4;
		}
		return i;
	}
	
	private int getPortModel(String value){
		int i = 0;
		if(value.equals("POINTOMULTIPOINT")){
			i = 4;
		}else if(value.equals("OTHER")){
			i = 0;
		}
		return i;
	}
	
}
