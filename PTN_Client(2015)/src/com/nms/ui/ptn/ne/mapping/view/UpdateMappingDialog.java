package com.nms.ui.ptn.ne.mapping.view;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.nms.db.bean.ptn.qos.QosMappingAttr;
import com.nms.db.bean.ptn.qos.QosMappingMode;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.ptn.qos.QosMappingModeService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
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
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTitle;

public class UpdateMappingDialog extends PtnDialog{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3866081193407401162L;
	private PhbMappingExpInfoPanel panel;
	private QosMappingMode qosMappingMode;
	private List<QosMappingAttr> mappingAttrList;
	
	private PtnButton btnSave; // 确定按钮
	private JButton btnCanel; // 取消按钮
	private JPanel panelBtn; // 按钮布局
	
	private JTable table; // 数据表
	private JScrollPane tableScroller;//数据表滚动条
	
	private JLabel lblName;// 名称标签
	private JTextField txtName;//名称对话框
	private JPanel panelName; //表布局
	
	public UpdateMappingDialog(Frame parent, boolean modal) {

	}
	
	public UpdateMappingDialog(QosMappingMode qosMappingMode, PhbMappingExpInfoPanel panel) {
		this.panel = panel;
		this.qosMappingMode = qosMappingMode;
		this.setModal(true);
		super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATAMAPPING));
		try {
			initComponents();
			initTableUI();
			UiUtil.jTableColumsHide(table, 1);
			setLayout();
			this.initData();
			addListeners();
			UiUtil.showWindow(this, 700, 400);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	@SuppressWarnings("serial")
	private void initComponents()throws Exception{
		this.btnCanel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		this.btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),true);
		this.panelBtn = new JPanel();
		//数据表格
		this.table = new JTable(); 
		table.setModel(new DefaultTableModel(
				new Object[][]{},
				new String[]{"",ResourceUtil.srcStr(StringKeysObj.ORDER_NUM),ResourceUtil.srcStr(StringKeysObj.OBJ_LEVEL),
						ResourceUtil.srcStr(StringKeysLbl.LBL_NAME),ResourceUtil.srcStr(StringKeysObj.OBJ_LEVEL_CODE)})//序号,等级,名称,等级值
		{
			/**
			 * 
			 */
			@SuppressWarnings("unchecked")
			Class[] types = new Class[] {java.lang.Object.class,
										 java.lang.Object.class,
										 java.lang.Object.class, 
										 java.lang.Object.class,
										 java.lang.Object.class};
			

			@SuppressWarnings("unchecked")
			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if (columnIndex == 1 || columnIndex == 2 || columnIndex == 3)
					return false;
				return true;
			}
		}
		);
		
		table.getTableHeader().setResizingAllowed(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		
		TableColumn c = table.getColumnModel().getColumn(0);
		c.setPreferredWidth(30);
		c.setMaxWidth(30);
		c.setMinWidth(30);
		
		this.tableScroller = new JScrollPane();
		this.tableScroller.setViewportView(this.table);
		
		this.lblName = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MAPPING_TABLE_NAME));
		this.txtName = new JTextField();
		this.panelName = new JPanel();
		
	}

	/**
	 * 让表格中的每个等级的值成为下拉列表的格式
	 * @throws Exception
	 */
	private void initTableUI()throws Exception {
		JComboBox cmbValue = new JComboBox();
		
		DefaultComboBoxModel defaultComboBoxModel = new DefaultComboBoxModel();
		int grade = 8;
		for (int i = 0; i < grade; i++) {
			defaultComboBoxModel.addElement(i+"");
		}
		cmbValue.setModel(defaultComboBoxModel);
		TableColumn valueColumn = table.getColumn(ResourceUtil.srcStr(StringKeysObj.OBJ_LEVEL_CODE));
		valueColumn.setCellEditor(new DefaultCellEditor(cmbValue));
	}
	
	/**
	 * 设置布局
	 */
	private void setLayout()throws Exception {
		this.setButtonLayout();
		this.setTextLayout();
		
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 40,620,40 };
		componentLayout.columnWeights = new double[] { 0, 0 };
		componentLayout.rowHeights = new int[] { 40, 260, 40 };
		componentLayout.rowWeights = new double[] { 0, 0.2 };
		this.setLayout(componentLayout);                                                                                             

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);

		c.gridx = 1;
		c.gridy = 0;
		componentLayout.setConstraints(this.panelName, c);
		this.add(this.panelName);

		c.gridy = 1;
		componentLayout.setConstraints(this.tableScroller, c);
		this.add(this.tableScroller);
		
		c.gridy = 2;
		componentLayout.setConstraints(this.panelBtn, c);
		this.add(this.panelBtn);
		
	}

	/**
	 * 设置按钮布局
	 */
	private void setButtonLayout() throws Exception{
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 570, 50 };
		componentLayout.columnWeights = new double[] { 0, 0 };
		componentLayout.rowHeights = new int[] { 40 };
		componentLayout.rowWeights = new double[] { 0 };
		this.panelBtn.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 0;
		c.gridy = 0;
		componentLayout.setConstraints(this.btnSave, c);
		this.panelBtn.add(this.btnSave);

		c.gridx = 1;
		c.anchor = GridBagConstraints.CENTER;
		componentLayout.setConstraints(this.btnCanel, c);
		this.panelBtn.add(this.btnCanel);
	}

	/**
	 * 设置文本框布局
	 */
	private void setTextLayout()throws Exception {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 50, 570 };
		componentLayout.columnWeights = new double[] { 0, 0 };
		componentLayout.rowHeights = new int[] { 40 };
		componentLayout.rowWeights = new double[] { 0 };
		this.panelName.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 0;
		componentLayout.setConstraints(this.lblName, c);
		this.panelName.add(this.lblName);

		c.gridx = 1;
		c.ipadx = 150;
		c.anchor = GridBagConstraints.WEST;
		componentLayout.setConstraints(this.txtName, c);
		this.panelName.add(this.txtName);
		
	}

	/**
	 * 初始化数据
	 * @throws Exception
	 */
	private void initData() throws Exception{
		try {
			mappingAttrList = this.qosMappingMode.getQosMappingAttrList();
			if(mappingAttrList.size()==0 || mappingAttrList == null){
				throw new Exception("mappingAttrList is null");
			}else{
				initTableData(mappingAttrList);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	private void initTableData(List<QosMappingAttr> mappingAttrList)  throws Exception{
		this.txtName.setText(this.qosMappingMode.getName());
		
		DefaultTableModel defaultTableModel = null;
		defaultTableModel = (DefaultTableModel)table.getModel();
		defaultTableModel.getDataVector().clear();
		defaultTableModel.fireTableDataChanged();
		
		Object[] obj = null;
		if (mappingAttrList != null && !mappingAttrList.isEmpty()) {
			for (int i = 0; i < mappingAttrList.size(); i++) {
				obj = new Object[] { mappingAttrList.get(i),i+1,mappingAttrList.get(i).getGrade(),
									 mappingAttrList.get(i).getName(),String.valueOf(mappingAttrList.get(i).getValue())};
				defaultTableModel.addRow(obj);
			}
			table.setModel(defaultTableModel);
		}
	}

	/**
	 * 添加监听事件
	 * @throws Exception
	 */
	private void addListeners() throws Exception{
		this.btnCanel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				dispose();
			}
		});

		this.btnSave.addActionListener(new MyActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					btnSaveListener();
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
	 * 保存按钮事件
	 * @throws Exception
	 */
	protected void btnSaveListener() throws Exception{
		//收集界面数据
		DispatchUtil phbMappingExpDispatch = null;
		QosMappingModeService_MB mappingService = null;
		List<QosMappingMode> infoList = new ArrayList<QosMappingMode>();
		QosMappingMode qosMappingCondition = null;
		try {
			//获取修改前的数据
			mappingService = (QosMappingModeService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosMappingModeService);
			qosMappingCondition = new QosMappingMode();
			qosMappingCondition.setSiteId(ConstantUtil.siteId);
			qosMappingCondition.setTypeName("PHB_EXP");
			qosMappingCondition.setId(qosMappingMode.getId());
			infoList = mappingService.queryByCondition(qosMappingCondition);
			QosMappingMode qosMappingModeBefore = infoList.get(0);
			this.setValue4Log(qosMappingModeBefore.getQosMappingAttrList());
			//获取修改后的数据
			List<QosMappingMode> infos = getTableData();
			phbMappingExpDispatch = new DispatchUtil(RmiKeys.RMI_PHBMAPPINGEXP);
			String result = phbMappingExpDispatch.excuteUpdate(infos);
			this.setValue4Log(qosMappingMode.getQosMappingAttrList());
			qosMappingMode.setTableName(qosMappingMode.getName());
			qosMappingMode.setName(null);
			qosMappingModeBefore.setTableName(qosMappingModeBefore.getName());
			qosMappingModeBefore.setName(null);
			AddOperateLog.insertOperLog(btnSave, EOperationLogType.UPDATEPHB.getValue(), result,
					qosMappingModeBefore, qosMappingMode, ConstantUtil.siteId, qosMappingMode.getTableName(), "phb2exp");
			DialogBoxUtil.succeedDialog(this,result);
			this.panel.getController().refresh(); 
			this.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(mappingService);
		}
	}
	
	private void setValue4Log(List<QosMappingAttr> modeList){
		if(modeList != null && modeList.size() > 0){
			for (QosMappingAttr mode : modeList) {
				mode.setValueLog(mode.getGrade()+"("+mode.getName()+") = "+mode.getValue());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<QosMappingMode> getTableData() throws Exception{
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		Vector vector = tableModel.getDataVector();
		Iterator it = vector.iterator();
		int i = 1;
 		while (it.hasNext()) {
			Vector temp = (Vector) it.next();
			mappingAttrList.get(i-1).setSiteId(ConstantUtil.siteId);
			if (temp.get(0) != null) {
				mappingAttrList.get(i-1).setId(((QosMappingAttr) temp.get(0)).getId());
			}
			mappingAttrList.get(i-1).setPhbId(qosMappingMode.getId());
			if (temp.get(2) != null) {
				mappingAttrList.get(i-1).setGrade(temp.get(2)+"");
			}
			if (temp.get(3) != null) {
				mappingAttrList.get(i-1).setName(temp.get(3)+"");
			}
			if (temp.get(4) != null) {
				mappingAttrList.get(i-1).setValue(Integer.parseInt(temp.get(4).toString()));
			}
			
			i += 1;
		}
		qosMappingMode.setName(txtName.getText());
		qosMappingMode.setQosMappingAttrList(mappingAttrList);
		List<QosMappingMode> modeList = new ArrayList<QosMappingMode>();
		modeList.add(qosMappingMode);
		return modeList;
	}
}
