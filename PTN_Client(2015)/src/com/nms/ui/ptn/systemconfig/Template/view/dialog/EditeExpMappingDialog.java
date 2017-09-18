package com.nms.ui.ptn.systemconfig.Template.view.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import twaver.Port;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.port.PortLagInfo;
import com.nms.db.bean.ptn.qos.QosMappingAttr;
import com.nms.db.bean.ptn.qos.QosMappingMode;
import com.nms.db.enums.EMappingColorEnum;
import com.nms.db.enums.QosCosLevelEnum;
import com.nms.db.enums.QosTemplateTypeEnum;
import com.nms.model.ptn.port.PortLagService_MB;
import com.nms.model.ptn.qos.QosMappingModeAttrService_MB;
import com.nms.model.ptn.qos.QosMappingModeService_MB;
import com.nms.model.ptn.qos.QosMappingTemplateService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.VerifyNameUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.systemconfig.Template.view.ExpMappingPanel;
import com.nms.ui.ptn.systemconfig.Template.view.PortTreePanel;
/**
 * EXP映射页面
 * @author dzy
 *
 */
public class EditeExpMappingDialog extends PtnDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 653240278079048714L;
	private JTable legTable;  //table
	private JScrollPane legJScrollPanel; //table面板
	private QosMappingMode qosMappingMode; //模板
	private List<QosMappingAttr> mappingAttrList; //exp参数List
	private JPanel upPanel;//选这项的面板
	private PtnButton btnSave; // 确定按钮
	private JButton btnCanel;//取消按钮
	private JPanel panelBtn; // 按钮布局
	private PortTreePanel portTree; // 端口面板
	private ExpMappingPanel expMappingPanel;//exp面板
	private String action;//exp类型
	private String column; //列   通过EXP类型   分别赋予列名称
	private int lspType; //exp类型  用于新建时初始化
	private List<String> typeList;//网元树下，要显示的端口类型集合
	
	/**
	 * 创建一个实例
	 * @param qosMappingMode 模板
	 * @param expMappingPanel exp面板
	 * @param action 操作方法
	 * @param lspType exp类型
	 * @throws Exception
	 */
	public EditeExpMappingDialog(QosMappingMode qosMappingMode,ExpMappingPanel expMappingPanel,String action,int lspType)throws Exception{
		try {
			this.expMappingPanel = expMappingPanel;
			this.qosMappingMode = qosMappingMode;
			this.action = action;
			this.lspType = lspType;
			initComponents();
			initComponentsTable();
			configE1LegTableUI();
			UiUtil.jTableColumsHide(legTable, 1);
			setUpLayout();
			setButtonLayout();
			setLayout();
			initData();
			addListener();
			UiUtil.showWindow(this, 950, 550);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	/**
	 * 初始化列表控件
	 * @throws Exception 
	 */
	@SuppressWarnings("serial")
	private void initComponentsTable() throws Exception {
		//根据EXP类型赋予 列名称
		if(lspType==UiUtil.getCodeByValue("EXPTYPE", "0").getId()){
			column = ResourceUtil.srcStr(StringKeysLbl.LBL_COLOR);
		}else{
			column = "COS";
		}
		
		legJScrollPanel = new JScrollPane();
		legJScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		legJScrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		legJScrollPanel.setSize(550, 400);
		legTable=new JTable();
		legTable.getTableHeader().setResizingAllowed(true);
		legTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		legTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "",ResourceUtil.srcStr(StringKeysObj.ORDER_NUM), "EXP", column }) {
			@SuppressWarnings("rawtypes")
			Class[] types = new Class[] { java.lang.Object.class, java.lang.Object.class, java.lang.Object.class ,java.lang.Object.class };
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if(ResourceUtil.srcStr(StringKeysBtn.BTN_BATCH_DOWNLOAD).equals(action)){
					return false;
				}else{
				return true;
				}
			}
		}

		);
		legJScrollPanel.setViewportView(legTable);
	}

	/**
	 * 初始化数据	
	 * @throws Exception 
	 */
	private void initData() throws Exception{
		super.getComboBoxDataUtil().comboBoxData(this.cmbDirection, "EXPDIRECTION");
		super.getComboBoxDataUtil().comboBoxData(this.cmbType, "EXPTYPE");
		initTableDate();
		//修改
		if(null!=qosMappingMode){
			this.txtName.setText(qosMappingMode.getName());
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbDirection, qosMappingMode.getQosMappingAttrList().get(0).getDirection()+"");
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbType, qosMappingMode.getQosMappingAttrList().get(0).getModel()+"");
			if(null!=qosMappingMode&&qosMappingMode.getQosMappingAttrList().size()>0){
				mappingAttrList=qosMappingMode.getQosMappingAttrList();
			}
		}
		//新建
		else{
			if(0==this.lspType||this.lspType==UiUtil.getCodeByValue("EXPTYPE", "0").getId()){
				super.getComboBoxDataUtil().comboBoxSelect(this.cmbType, "555");
			}else{
				super.getComboBoxDataUtil().comboBoxSelect(this.cmbType, "556");
			}
			qosMappingMode = new QosMappingMode();
		}
		
	}
	
	/**
	 * 初始化控件
	 * @throws Exception
	 */
	private void initComponents() throws Exception {
		
		panelBtn=new JPanel();
		upPanel=new JPanel();
		if(null==qosMappingMode){
			this.setTitle(ResourceUtil.srcStr(StringKeysLbl.LBL_EXPMODEL_INSERT));
		}else{
			this.setTitle(ResourceUtil.srcStr(StringKeysLbl.LBL_EXPMODEL_UPDATE));
		}
		this.btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE));
		this.btnCanel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		this.lblMessage = new JLabel();
		lblName=new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAME));
		txtName=new PtnTextField(true, PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.btnSave, this);
		lblType=new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TYPE));
		cmbType=new JComboBox();
		lblDirection=new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DIR));
		cmbDirection=new JComboBox();
		
		this.typeList=new ArrayList<String>();
		this.typeList.add("ETH");
		portTree = new PortTreePanel(action,typeList,1);
		//如果修改 下列控件设置为不可用
		if(null!=qosMappingMode){
			this.cmbDirection.setEnabled(false);
			this.cmbType.setEnabled(false);
		}
		if(ResourceUtil.srcStr(StringKeysBtn.BTN_BATCH_DOWNLOAD).equals(action)){
			this.txtName.setEnabled(false);
		}
	}
	
	/**
	 * 设置主布局
	 */
	private void setLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] {650,200 };
		componentLayout.columnWeights = new double[] { 0.1,0 };
		componentLayout.rowHeights = new int[] { 100,350,50};
		componentLayout.rowWeights = new double[] { 0,0,0};
		this.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
//		第一行选项面板
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;        
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.upPanel, c);
		this.add(this.upPanel);
//		第二行 列表面板
		c.gridy=1;
		componentLayout.setConstraints(this.legJScrollPanel, c);
		this.add(this.legJScrollPanel);
//		第二列 网元和端口树
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 2;     
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.portTree, c);
		this.add(this.portTree);
//		第三行 按钮面板
		c.gridx=1;
		c.gridy=2;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.panelBtn, c);
		this.add(this.panelBtn);
		
	}
	
	/**
	 *  设置布局
	 */
	private void setUpLayout() {
		
		GridBagLayout componentLayout = new GridBagLayout();// 网格布局
		componentLayout.columnWidths = new int[] { 70, 550, 0 };
		componentLayout.columnWeights = new double[] { 0, 0.3, 0 };
		componentLayout.rowHeights = new int[] { 30, 30, 30};
		componentLayout.rowWeights = new double[] { 0, 0, 0};
		this.upPanel.setLayout(componentLayout);
		
		GridBagConstraints gridCon = new GridBagConstraints();
		gridCon.fill = GridBagConstraints.HORIZONTAL;
		gridCon.insets = new Insets(5, 5, 5, 5);
		gridCon.anchor = GridBagConstraints.CENTER;
//		第一行名称
		gridCon.gridx = 0;
		gridCon.gridy = 0;
		componentLayout.setConstraints(this.lblName, gridCon);
		this.upPanel.add(this.lblName);
		gridCon.gridx = 1;
		componentLayout.setConstraints(this.txtName, gridCon);
		this.upPanel.add(this.txtName);
//		第二行类型
		gridCon.gridx = 0;
		gridCon.gridy = 1;
		componentLayout.setConstraints(this.lblType, gridCon);
		this.upPanel.add(this.lblType);
		gridCon.gridx = 1;
		componentLayout.setConstraints(this.cmbType, gridCon);
		this.upPanel.add(this.cmbType);
//		第三行方向
		gridCon.gridx = 0;
		gridCon.gridy = 2;
		componentLayout.setConstraints(this.lblDirection, gridCon);
		this.upPanel.add(this.lblDirection);
		gridCon.gridx = 1;
		componentLayout.setConstraints(this.cmbDirection, gridCon);
		this.upPanel.add(this.cmbDirection);
	}
	
	/**
	 * 设置按钮布局
	 */
	private void setButtonLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] {120,50,50};
		componentLayout.columnWeights = new double[] { 0, 0 ,0};
		componentLayout.rowHeights = new int[] { 30 };
		componentLayout.rowWeights = new double[] { 0 };
		this.panelBtn.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
//		保存
		componentLayout.setConstraints(this.btnSave, c);
		this.panelBtn.add(this.btnSave);
		c.gridx = 2;
//		取消
		componentLayout.setConstraints(this.btnCanel, c);
		this.panelBtn.add(this.btnCanel);
	}
	
	/**
	 * 选取默认值填入支路信息表中
	 */
	private void initTableDate() {
		DefaultTableModel defaultTableModel = null;
		defaultTableModel = (DefaultTableModel) legTable.getModel();
		defaultTableModel.getDataVector().clear();
		defaultTableModel.fireTableDataChanged();
		Object[] obj = null;
		try {
			if(null!=qosMappingMode&&null!=qosMappingMode.getQosMappingAttrList()){
				List<QosMappingAttr> qosMappingAttrList=qosMappingMode.getQosMappingAttrList();
				for(int i=0;i<qosMappingAttrList.size();i++){
					if(UiUtil.getCodeByValue("EXPTYPE", "0").getId()==UiUtil.getCodeById(this.expMappingPanel.getSelect().getQosMappingAttrList().get(0).getModel()).getId()){
						obj = new Object[] { qosMappingAttrList.get(i),i+1,"EXP"+String.valueOf(qosMappingAttrList.get(i).getValue()),"YELLOW".equals(EMappingColorEnum.from(qosMappingAttrList.get(i).getColor()).toString())?ResourceUtil.srcStr(StringKeysObj.YELLOW):ResourceUtil.srcStr(StringKeysObj.GREEN)};
					}else{
						obj = new Object[] { qosMappingAttrList.get(i),i+1,"EXP"+String.valueOf(qosMappingAttrList.get(i).getValue()),QosCosLevelEnum.from(Integer.parseInt(qosMappingAttrList.get(i).getGrade()))};
					}
					defaultTableModel.addRow(obj);
				}
			}else{
				ControlKeyValue controlKeyValue = null;
				controlKeyValue = (ControlKeyValue) this.cmbDirection.getSelectedItem();
				if(lspType==UiUtil.getCodeByValue("EXPTYPE", "0").getId()){
					if(UiUtil.getCodeByValue("EXPDIRECTION", "0").getId()==Integer.parseInt(controlKeyValue.getId())){
						obj = new Object[] { null,1,"EXP0",ResourceUtil.srcStr(StringKeysObj.YELLOW)};
						defaultTableModel.addRow(obj);
						obj = new Object[] { null,2,"EXP1",ResourceUtil.srcStr(StringKeysObj.GREEN)};
						defaultTableModel.addRow(obj);
					}else{
						obj = new Object[] { null,1,"EXP0",ResourceUtil.srcStr(StringKeysObj.YELLOW)};
						defaultTableModel.addRow(obj);
						obj = new Object[] { null,2,"EXP1",ResourceUtil.srcStr(StringKeysObj.GREEN)};
						defaultTableModel.addRow(obj);
						obj = new Object[] { null,3,"EXP2",ResourceUtil.srcStr(StringKeysObj.YELLOW)};
						defaultTableModel.addRow(obj);
						obj = new Object[] { null,4,"EXP3",ResourceUtil.srcStr(StringKeysObj.GREEN)};
						defaultTableModel.addRow(obj);
						obj = new Object[] { null,5,"EXP4",ResourceUtil.srcStr(StringKeysObj.YELLOW)};
						defaultTableModel.addRow(obj);
						obj = new Object[] { null,6,"EXP5",ResourceUtil.srcStr(StringKeysObj.GREEN)};
						defaultTableModel.addRow(obj);
						obj = new Object[] { null,7,"EXP6",ResourceUtil.srcStr(StringKeysObj.YELLOW)};
						defaultTableModel.addRow(obj);
						obj = new Object[] { null,8,"EXP7",ResourceUtil.srcStr(StringKeysObj.GREEN)};
						defaultTableModel.addRow(obj);
					}
				}else{
					obj = new Object[] { null,1,"EXP0","BE"};
					defaultTableModel.addRow(obj);
					obj = new Object[] { null,2,"EXP1","AF1"};
					defaultTableModel.addRow(obj);
					obj = new Object[] { null,3,"EXP2","AF2"};
					defaultTableModel.addRow(obj);
					obj = new Object[] { null,4,"EXP3","AF3"};
					defaultTableModel.addRow(obj);
					obj = new Object[] { null,5,"EXP4","AF4"};
					defaultTableModel.addRow(obj);
					obj = new Object[] { null,6,"EXP5","EF"};
					defaultTableModel.addRow(obj);
					obj = new Object[] { null,7,"EXP6","CS6"};
					defaultTableModel.addRow(obj);
					obj = new Object[] { null,8,"EXP7","CS7"};
					defaultTableModel.addRow(obj);
				}
			
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		legTable.setModel(defaultTableModel);
	}
	
	/**
	 *让表格中的封装帧个数，缓存时间(ms)，pw标签成为下拉列表的格式
	 * 
	 * @throws Exception
	 */
	private void configE1LegTableUI() throws Exception {
		if(!action.equals(ResourceUtil.srcStr(StringKeysBtn.BTN_BATCH_DOWNLOAD))){
			if(lspType==UiUtil.getCodeByValue("EXPTYPE", "0").getId()){
				pinCountCombox = new JComboBox();
				pinCountCombox.addItem("EXP0");
				pinCountCombox.addItem("EXP1");
				pinCountCombox.addItem("EXP2");
				pinCountCombox.addItem("EXP3");
				pinCountCombox.addItem("EXP4");
				pinCountCombox.addItem("EXP5");
				pinCountCombox.addItem("EXP6");
				pinCountCombox.addItem("EXP7");
			
				TableColumn pinCountColumn = legTable.getColumn("EXP");
				pinCountColumn.setCellEditor(new DefaultCellEditor(pinCountCombox));

				cospinCountCombox = new JComboBox();
				cospinCountCombox.addItem(ResourceUtil.srcStr(StringKeysObj.YELLOW));
				cospinCountCombox.addItem(ResourceUtil.srcStr(StringKeysObj.GREEN));
				
				TableColumn prestoreTimeColumn = legTable.getColumn(ResourceUtil.srcStr(StringKeysLbl.LBL_COLOR));
				prestoreTimeColumn.setCellEditor(new DefaultCellEditor(cospinCountCombox));
			}else{
				pinCountCombox = new JComboBox();
				pinCountCombox.addItem("EXP0");
				pinCountCombox.addItem("EXP1");
				pinCountCombox.addItem("EXP2");
				pinCountCombox.addItem("EXP3");
				pinCountCombox.addItem("EXP4");
				pinCountCombox.addItem("EXP5");
				pinCountCombox.addItem("EXP6");
				pinCountCombox.addItem("EXP7");
			
				TableColumn pinCountColumn = legTable.getColumn("EXP");
				pinCountColumn.setCellEditor(new DefaultCellEditor(pinCountCombox));

				cospinCountCombox = new JComboBox();
				cospinCountCombox.addItem("BE");
				cospinCountCombox.addItem("AF0");
				cospinCountCombox.addItem("AF1");
				cospinCountCombox.addItem("AF2");
				cospinCountCombox.addItem("AF3");
				cospinCountCombox.addItem("AF4");
				cospinCountCombox.addItem("EF");
				cospinCountCombox.addItem("CS6");
				cospinCountCombox.addItem("CS7");
				
				TableColumn prestoreTimeColumn = legTable.getColumn("COS");
				prestoreTimeColumn.setCellEditor(new DefaultCellEditor(cospinCountCombox));

			}
		}
		if(null==qosMappingMode){
			pinCountCombox.setEnabled(true);
			cospinCountCombox.setEnabled(false);
		}
	}
	
	/**
	 * 添加监听
	 * 
	 * @author dzy
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private void addListener() {
		this.cmbDirection.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cmbDirectionActionPerformed();
			}
		});
//		保存时间
		this.btnSave.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					btnSaveActionPerformed();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}

		});
		// 取消按钮事件
		this.btnCanel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 关闭窗口
				dispose();
			}
		});
		this.cmbDirection.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				directionActionPerformed();
				
			}
		});
		this.cmbType.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				typeActionPerformed();
				
			}
		});
	}
	/**
	 * cmbDirection监听事件
	 */
	private void cmbDirectionActionPerformed() {
		if("输入".equals(((ControlKeyValue)cmbDirection.getSelectedItem()).getName())){
			pinCountCombox.setEnabled(false);
			cospinCountCombox.setEnabled(true);
		}else{
			pinCountCombox.setEnabled(true);
			cospinCountCombox.setEnabled(false);
		
		}
	}
	
	/**
	 * cmbType监听事件
	 */
	private void typeActionPerformed() {
		int expType;
		try {
		expType = Integer.parseInt(((ControlKeyValue)this.cmbType.getSelectedItem()).getId());
		this.cmbType = null;
		new EditeExpMappingDialog(null,this.expMappingPanel,"insert",expType);
		this.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	private void directionActionPerformed() {
		initTableDate();
	}
	
	/**
	 * 保存按钮事件
	 * @throws Exception
	 */
	protected void btnSaveActionPerformed() throws Exception{
		//收集界面数据
		int groupId = 0;
		String result = null;
		List<QosMappingMode> infos ;
		DispatchUtil portDispatch = null;
		DispatchUtil portLagDispatch = null;
		PortInst portInst = null;
		PortLagInfo portLagInfo = null;
		List<PortInst> portInstList = null;
		List<PortLagInfo> portLagInfoList = null;
		DispatchUtil expMappingPhbDispatch = null;
		QosMappingModeService_MB mappingModeService = null;
		QosMappingTemplateService_MB qosMappingTemplateService = null;
		PortLagService_MB portLagService = null;
		String beforeName = null;
		try {
			portDispatch = new DispatchUtil(RmiKeys.RMI_PORT);
			portLagDispatch = new DispatchUtil(RmiKeys.RMI_PORTLAG);
			infos = new ArrayList<QosMappingMode>();
			expMappingPhbDispatch = new DispatchUtil(RmiKeys.RMI_EXPMAPPINGPHB);
			qosMappingTemplateService = (QosMappingTemplateService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QOSMAPPINGTEMPLATESERVICE);
			mappingModeService = (QosMappingModeService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosMappingModeService);
			portLagService = (PortLagService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORTLAG);
			//验证名称是否存在
			if(this.qosMappingMode != null  && null != this.qosMappingMode.getQosMappingAttrList()&& 0 != this.qosMappingMode.getQosMappingAttrList().size()){
				beforeName = this.qosMappingMode.getName();
			}
			if(this.verifyName(this.txtName.getText().trim(), beforeName)){
				result =  ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST);
				return;
			}
			//收集数据
			this.qosMappingMode = getTableData();
			
			infos.add(qosMappingMode);
			//把模板添加全量的数据库
			if(!action.equals(ResourceUtil.srcStr(StringKeysBtn.BTN_BATCH_DOWNLOAD))){
				groupId = qosMappingTemplateService.saveOrUpdate(qosMappingMode.getQosMappingAttrList());
			}
			//如果选中网元 下发模板到网元
			if(null!=this.portTree.getCmbSite().getSelectedItem()&&null!=((ControlKeyValue)this.portTree.getCmbSite().getSelectedItem()).getObject()){
				//验证端口是否使用模板
				if(verifyModel(qosMappingMode)){
					//如果使用本模板,不修改模板,直接返回成功
					result = ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS);
				}
				//如果未使用模板新建模板并且修改端口引用
				else{
					result = expMappingPhbDispatch.excuteInsert(infos);
				}
				//如果下发成功
				if(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS).equals(result)){
					//如果选中端口修改端口引用
					portInstList = new ArrayList<PortInst>();
					portLagInfoList = new ArrayList<PortLagInfo>();
					if(0!=this.portTree.getNeTreePanel().getElement().size()){
						for (int j = 0; j < this.portTree.getNeTreePanel().getElement().size(); j++) {
							if(this.portTree.getNeTreePanel().getElement().get(j) instanceof Port){
								portInst = (PortInst) ((Port)this.portTree.getNeTreePanel().getElement().get(j)).getUserObject();
								if(0!=portInst.getLagId()){
									portLagInfo = new PortLagInfo();
									portLagInfo.setId(portInst.getLagId());
									portLagInfo = portLagService.selectByCondition(portLagInfo).get(0);
									portLagInfoList.add(portLagInfo);
								}else{
									portInstList.add((PortInst) ((Port)this.portTree.getNeTreePanel().getElement().get(j)).getUserObject());
								}
							}
						}
					}
					//查询入库的模板
					infos = mappingModeService.queryByCondition(infos.get(0));
					if(null!=infos&&infos.size()>0){
						qosMappingMode = infos.get(0);
					}
					//下发端口
					if(0!=portInstList.size()){
						for(PortInst portObj:portInstList){
							//设置端口和模板的关联
							this.setPortInstBusiId(portObj,qosMappingMode);
							result = portDispatch.excuteUpdate(portObj);
						}
					}
					//下发lag
					if(null!=portLagInfoList&&portLagInfoList.size()>0){
						for(PortLagInfo lagObj:portLagInfoList){
							this.setPortLagBusiId(lagObj,qosMappingMode);
							result = portLagDispatch.excuteUpdate(lagObj);
						}
					}
				}
			}else{
				if(0!=groupId){
					result = ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS);
				}
			}
			DialogBoxUtil.succeedDialog(this,result);
			this.expMappingPanel.getController().refresh();
			this.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(portLagService);
			UiUtil.closeService_MB(mappingModeService);
			UiUtil.closeService_MB(qosMappingTemplateService);
			this.dispose();
		}
		
	}
	
	/**
	 * 验证名字
	 * 
	 * @param type
	 * 			模板类型
	 * @param afterName
	 * 				修改后名字
	 * @param beforeName
	 * 				修改前名字
	 * @return
	 */
	public boolean verifyName(String afterName, String beforeName) {
		boolean flag = true;
		QosMappingTemplateService_MB qosMappingTemplateService = null;
		try {
			qosMappingTemplateService = (QosMappingTemplateService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QOSMAPPINGTEMPLATESERVICE);
			if(beforeName != null && afterName.equals(beforeName)){
				//beforeName不为null，说明是修改，如果两次名称相同，则无需查库验证
				flag = false;
			}else{
				flag = qosMappingTemplateService.nameRepetition(afterName, beforeName ,QosTemplateTypeEnum.COS_VLANPRI.getValue());
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,VerifyNameUtil.class);
		} finally {
			UiUtil.closeService_MB(qosMappingTemplateService);
		}
		return flag;
	}
	
	
	/**
	 * 为lag 赋值BusinessId
	 * @param lagObj lag对象
	 * @param qosMappingMode2
	 * 				Exp映射
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	private void setPortLagBusiId(PortLagInfo lagObj,
			QosMappingMode qosMappingMode) throws NumberFormatException, Exception {
		ControlKeyValue mode = (ControlKeyValue) this.cmbType.getSelectedItem();
 		ControlKeyValue direction = (ControlKeyValue) this.cmbDirection.getSelectedItem();
 		int businessId = qosMappingMode.getBusinessId();
 		//llsp输入
		if(UiUtil.getCodeByValue("EXPTYPE", "0").getId()==Integer.parseInt(mode.getId())&&
				UiUtil.getCodeByValue("EXPDIRECTION", "1").getId()==Integer.parseInt(direction.getId())){
			lagObj.setExpMappingLLspInput(businessId);
		}
		//llsp输出
		if(UiUtil.getCodeByValue("EXPTYPE", "0").getId()==Integer.parseInt(mode.getId())&&
				UiUtil.getCodeByValue("EXPDIRECTION", "0").getId()==Integer.parseInt(direction.getId())){
			lagObj.setExpMappingLLspOutput(businessId);
		}
		//elsp输入
		if(UiUtil.getCodeByValue("EXPTYPE", "1").getId()==Integer.parseInt(mode.getId())&&
				UiUtil.getCodeByValue("EXPDIRECTION", "1").getId()==Integer.parseInt(direction.getId())){
			lagObj.setExpMappingELspInput(businessId);
		}
		//elsp输出
		if(UiUtil.getCodeByValue("EXPTYPE", "1").getId()==Integer.parseInt(mode.getId())&&
				UiUtil.getCodeByValue("EXPDIRECTION", "0").getId()==Integer.parseInt(direction.getId())){
			lagObj.setExpMappingELspOutput(businessId);
		}
		
	}
	/**
	 * 验证模板在此网元是否被使用
	 * @param qosMappingMode
	 * 				Exp映射
	 * @return
	 * @throws Exception
	 */
	private boolean verifyModel(QosMappingMode qosMappingMode) throws Exception {
		int count = 0;
		boolean flag = false;
		QosMappingModeAttrService_MB mappingModeAttrService = null;
		try {
			List<QosMappingAttr> qosMappingAttrSelList = null;
			mappingModeAttrService = (QosMappingModeAttrService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosMappingModeAttrService);
			for(QosMappingAttr qosMappingAttr:qosMappingMode.getQosMappingAttrList()){
				qosMappingAttrSelList = mappingModeAttrService.isExit(qosMappingAttr);
				if(null!=qosMappingAttrSelList&&qosMappingAttrSelList.size()==1){
					count ++;
				}
			}
			//模板存在模板
			if(count == qosMappingMode.getQosMappingAttrList().size()){
				flag = true;
			}else{
				flag = false;
			}
		} catch (Exception e) {
          ExceptionManage.dispose(e, getClass());
		}finally{
			UiUtil.closeService_MB(mappingModeAttrService); 
		}
		return flag;
	}
	
	/**
	 * 为端口赋值businessId
	 * @param portInst 
	 * 			端口对象
	 * @param qosMappingMode
	 * 			Exp映射
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	private void setPortInstBusiId(PortInst portInst,
			QosMappingMode qosMappingMode) throws NumberFormatException, Exception {
		ControlKeyValue mode = (ControlKeyValue) this.cmbType.getSelectedItem();
 		ControlKeyValue direction = (ControlKeyValue) this.cmbDirection.getSelectedItem();
 		int businessId = qosMappingMode.getBusinessId();
 		//llsp输入
		if(UiUtil.getCodeByValue("EXPTYPE", "0").getId()==Integer.parseInt(mode.getId())&&
				UiUtil.getCodeByValue("EXPDIRECTION", "1").getId()==Integer.parseInt(direction.getId())){
				portInst.setExpMappingLLspInput(businessId);
		}
		//llsp输出
		if(UiUtil.getCodeByValue("EXPTYPE", "0").getId()==Integer.parseInt(mode.getId())&&
				UiUtil.getCodeByValue("EXPDIRECTION", "0").getId()==Integer.parseInt(direction.getId())){
			portInst.setExpMappingLLspOutput(businessId);
		}
		//elsp输入
		if(UiUtil.getCodeByValue("EXPTYPE", "1").getId()==Integer.parseInt(mode.getId())&&
				UiUtil.getCodeByValue("EXPDIRECTION", "1").getId()==Integer.parseInt(direction.getId())){
			portInst.setExpMappingELspInput(businessId);
		}
		//elsp输出
		if(UiUtil.getCodeByValue("EXPTYPE", "1").getId()==Integer.parseInt(mode.getId())&&
				UiUtil.getCodeByValue("EXPDIRECTION", "0").getId()==Integer.parseInt(direction.getId())){
			portInst.setExpMappingELspOutput(businessId);
		}
	}
	
	/**
	 * 收集数据
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private QosMappingMode getTableData() throws Exception{
		DefaultTableModel tableModel = (DefaultTableModel) legTable.getModel();
		Vector vector = tableModel.getDataVector();
		Iterator it = vector.iterator();
		int i = 1;
		QosMappingAttr qosMappingAttr = null;
		ControlKeyValue mode = (ControlKeyValue) this.cmbType.getSelectedItem();
 		ControlKeyValue direction = (ControlKeyValue) this.cmbDirection.getSelectedItem();
 		List<QosMappingAttr> qosMappingAttrList= new ArrayList<QosMappingAttr>();
 		SiteInst siteInst = null;
 		if(null!=this.portTree.getCmbSite().getSelectedItem()){
			siteInst = (SiteInst) ((ControlKeyValue)this.portTree.getCmbSite().getSelectedItem()).getObject();
		}
		if(null!=mappingAttrList&&mappingAttrList.size()>0&&!ResourceUtil.srcStr(StringKeysBtn.BTN_BATCH_DOWNLOAD).equals(action)){
			while (it.hasNext()) {
				Vector temp = (Vector) it.next();
				mappingAttrList.get(i-1).setSiteId(ConstantUtil.siteId);
				if (temp.get(0) != null) {
					mappingAttrList.get(i-1).setId(((QosMappingAttr) temp.get(0)).getId());
					
				}
				if(temp.get(2)!=null){
					mappingAttrList.get(i-1).setValue(Integer.parseInt(temp.get(2).toString().substring(3, 4)));
				}
				if(temp.get(3)!=null){
					if(555==Integer.parseInt(mode.getId())){
						mappingAttrList.get(i-1).setColor(EMappingColorEnum.from(ResourceUtil.srcStr(StringKeysObj.YELLOW).equals(temp.get(3))?"YELLOW":"GREEN"));
					}else{
						mappingAttrList.get(i-1).setGrade(QosCosLevelEnum.from((temp.get(3)+""))+"");
					}
					
				}
				mappingAttrList.get(i-1).setName(txtName.getText());
				mappingAttrList.get(i-1).setModel(Integer.parseInt(mode.getId()));
				mappingAttrList.get(i-1).setDirection(Integer.parseInt(direction.getId()));
				i += 1;
			}
			qosMappingMode.setName(txtName.getText());
			qosMappingMode.setQosMappingAttrList(mappingAttrList);
		}else{
			
			while (it.hasNext()) {
				qosMappingAttr = new QosMappingAttr();
				Vector temp = (Vector) it.next();
				if(temp.get(2)!=null){
					qosMappingAttr.setValue(Integer.parseInt(temp.get(2).toString().substring(3, 4)));
				}
				if(temp.get(3)!=null){
					//如果是llsp
					if(555==Integer.parseInt(mode.getId())){
						qosMappingAttr.setColor(EMappingColorEnum.from(ResourceUtil.srcStr(StringKeysObj.YELLOW).equals(temp.get(3))?"YELLOW":"GREEN"));
					}
					//ESLP
					else{
						qosMappingAttr.setGrade(QosCosLevelEnum.from((temp.get(3)+""))+"");
					}
				}
				qosMappingAttr.setMappingType(QosTemplateTypeEnum.EXP.getValue());
				qosMappingAttr.setName(txtName.getText());
				qosMappingAttr.setModel(Integer.parseInt(mode.getId()));
				qosMappingAttr.setDirection(Integer.parseInt(direction.getId()));
				if(null!=siteInst){
					qosMappingAttr.setSiteId(siteInst.getSite_Inst_Id());
				}
				qosMappingAttrList.add(qosMappingAttr);
			}
			qosMappingMode.setName(txtName.getText());
			qosMappingMode.setQosMappingAttrList(qosMappingAttrList);
			if(555==Integer.parseInt(mode.getId())){
				qosMappingMode.setType(QosTemplateTypeEnum.LLSP.getValue());
				qosMappingMode.setTypeName(QosTemplateTypeEnum.LLSP.toString());
			}else{
				qosMappingMode.setType(QosTemplateTypeEnum.ELSP.getValue());
				qosMappingMode.setTypeName(QosTemplateTypeEnum.ELSP.toString());
			}
			if(null!=siteInst){
				qosMappingMode.setSiteId(siteInst.getSite_Inst_Id());
			}
		}
		return qosMappingMode;
	}
	private JComboBox cospinCountCombox; //第二列
	private JComboBox pinCountCombox ;	//第一列
	private JLabel lblName;	//名字LBL
	private JTextField txtName;	//名字TXT
	private JLabel lblType; //类型LBL
	private JComboBox cmbType; //类型CMB
	private JLabel lblDirection; //输入输出LBL
	private JComboBox cmbDirection;//输入输出CMB
	private JLabel lblMessage; //错误信息
	
}
