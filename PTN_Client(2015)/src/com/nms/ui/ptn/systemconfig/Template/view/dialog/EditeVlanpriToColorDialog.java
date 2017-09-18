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
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.port.PortLagInfo;
import com.nms.db.bean.ptn.qos.QosMappingAttr;
import com.nms.db.bean.ptn.qos.QosMappingMode;
import com.nms.db.enums.EMappingColorEnum;
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
import com.nms.ui.ptn.systemconfig.Template.view.PortTreePanel;
import com.nms.ui.ptn.systemconfig.Template.view.VlanpriToColorPanel;
/**
 * vlanpri到颜色映射界面
 * @author dzy
 *
 */
public class EditeVlanpriToColorDialog extends PtnDialog{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -740925019329390534L;
	private String action;//操作方式insert、update
	private List<String> typeList;//网元树，要显示的端口类型集合（eth,sdh,pdh,lag）
	
	/**
	 * 创建一个实例
	 * @param qosMappingMode	模板
	 * @param vlanpriToColorPanel	vlanpriToColor面板
	 * @param action	操作方式insert、update
	 * @throws Exception
	 */
	public EditeVlanpriToColorDialog( QosMappingMode qosMappingMode,VlanpriToColorPanel vlanpriToColorPanel,String action)throws Exception{
		try {
			this.vlanpriToColorPanel = vlanpriToColorPanel;
			this.qosMappingMode = qosMappingMode;
			this.action = action;
			initComponents();
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
	 * 初始化数据
	 */
	private void initData(){
		initTableDate();
		if(null!=qosMappingMode){
			this.txtName.setText(qosMappingMode.getName());
			if(null!=qosMappingMode.getQosMappingAttrList()&&qosMappingMode.getQosMappingAttrList().size()>0){
				mappingAttrList=qosMappingMode.getQosMappingAttrList();
			}
		}else{
			qosMappingMode = new QosMappingMode();
		}
	}
	
	/**
	 * 初始化控件
	 * @throws Exception
	 */
	@SuppressWarnings("serial")
	private void initComponents() throws Exception {
		this.typeList=new ArrayList<String>();
		this.typeList.add("ETH");
		this.typeList.add("LAG");
		portTree = new PortTreePanel(action,typeList,2);
		panelBtn=new JPanel();
		upPanel=new JPanel();
		legTable=new JTable();
		if(null!=this.qosMappingMode){
			this.setTitle(ResourceUtil.srcStr(StringKeysLbl.LBL_VLANPRITOCOLORMODEL_UPDATE));
		}else{
			this.setTitle(ResourceUtil.srcStr(StringKeysLbl.LBL_VLANPRITOCOLORMODEL_INSERT));
		}
		this.btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE));
		this.btnCanel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		legJScrollPanel = new JScrollPane();
		legJScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		legJScrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		legJScrollPanel.setSize(590, 400);
		legTable.getTableHeader().setResizingAllowed(true);
		legTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		legTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "",ResourceUtil.srcStr(StringKeysObj.ORDER_NUM), "VLANPRI", ResourceUtil.srcStr(StringKeysLbl.LBL_COLOR) }) {
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
		this.lblMessage = new JLabel();
		lblName=new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAME));
		txtName=new PtnTextField(true, PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.btnSave, this);
		if(ResourceUtil.srcStr(StringKeysBtn.BTN_BATCH_DOWNLOAD).equals(action)){
			txtName.setEnabled(false);
		}
		
	}
	
	/**
	 * 设置主布局
	 */
	private void setLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] {650,200 };
		componentLayout.columnWeights = new double[] { 0.2,0 };
		componentLayout.rowHeights = new int[] { 50,400,50};
		componentLayout.rowWeights = new double[] { 0,0,0};
		this.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		//第一行 选这项面板
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;        
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.upPanel, c);
		this.add(this.upPanel);
		
		//第二行 table面板
		c.gridy=1;
		componentLayout.setConstraints(this.legJScrollPanel, c);
		this.add(this.legJScrollPanel);
		
		//第二列 网元 和端口树
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 2;     
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.portTree, c);
		this.add(this.portTree);
		
		//第3行按钮面板
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
		componentLayout.columnWidths = new int[] {50, 590, 0};
		componentLayout.columnWeights = new double[] { 0.2, 0.5, 0};
		componentLayout.rowHeights = new int[] {30};
		componentLayout.rowWeights = new double[] { 0 };
		this.upPanel.setLayout(componentLayout);
		
		GridBagConstraints gridCon = new GridBagConstraints();
		gridCon.fill = GridBagConstraints.HORIZONTAL;
		gridCon.insets = new Insets(5, 5, 5, 5);
		gridCon.anchor = GridBagConstraints.CENTER;

		gridCon.gridx = 0;
		gridCon.gridy = 0;
		componentLayout.setConstraints(this.lblName, gridCon);
		this.upPanel.add(this.lblName);
		gridCon.gridx = 1;
		componentLayout.setConstraints(this.txtName, gridCon);
		this.upPanel.add(this.txtName);

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
		//保存
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.btnSave, c);
		this.panelBtn.add(this.btnSave);
		//取消
		c.gridx = 2;
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
			if(null!=this.qosMappingMode){
				List<QosMappingAttr> qosMappingAttrList=qosMappingMode.getQosMappingAttrList();
				if(null!=qosMappingAttrList&&qosMappingAttrList.size()>0){
					for(int i=0;i<qosMappingAttrList.size();i++){
						obj = new Object[] { qosMappingAttrList.get(i),i+1,"VLANPRI"+String.valueOf(qosMappingAttrList.get(i).getValue()),"YELLOW".equals(EMappingColorEnum.from(qosMappingAttrList.get(i).getColor()).toString())?ResourceUtil.srcStr(StringKeysObj.YELLOW):ResourceUtil.srcStr(StringKeysObj.GREEN)};
						defaultTableModel.addRow(obj);
					}
				}
				
			}else{
				obj = new Object[] { null,1,"VLANPRI0",ResourceUtil.srcStr(StringKeysObj.GREEN)};
				defaultTableModel.addRow(obj);
				obj = new Object[] { null,2,"VLANPRI1",ResourceUtil.srcStr(StringKeysObj.GREEN)};
				defaultTableModel.addRow(obj);
				obj = new Object[] { null,3,"VLANPRI2",ResourceUtil.srcStr(StringKeysObj.GREEN)};
				defaultTableModel.addRow(obj);
				obj = new Object[] { null,4,"VLANPRI3",ResourceUtil.srcStr(StringKeysObj.GREEN)};
				defaultTableModel.addRow(obj);
				obj = new Object[] { null,5,"VLANPRI4",ResourceUtil.srcStr(StringKeysObj.GREEN)};
				defaultTableModel.addRow(obj);
				obj = new Object[] { null,6,"VLANPRI5",ResourceUtil.srcStr(StringKeysObj.GREEN)};
				defaultTableModel.addRow(obj);
				obj = new Object[] { null,7,"VLANPRI6",ResourceUtil.srcStr(StringKeysObj.GREEN)};
				defaultTableModel.addRow(obj);
				obj = new Object[] { null,8,"VLANPRI7",ResourceUtil.srcStr(StringKeysObj.GREEN)};
				defaultTableModel.addRow(obj);
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
		cospinCountCombox = new JComboBox();
		cospinCountCombox.addItem(ResourceUtil.srcStr(StringKeysObj.YELLOW));
		cospinCountCombox.addItem(ResourceUtil.srcStr(StringKeysObj.GREEN));
		TableColumn prestoreTimeColumn = legTable.getColumn(ResourceUtil.srcStr(StringKeysLbl.LBL_COLOR));
		prestoreTimeColumn.setCellEditor(new DefaultCellEditor(cospinCountCombox));
		//如果是批量下载
		if(action.equals(ResourceUtil.srcStr(StringKeysBtn.BTN_BATCH_DOWNLOAD))){
			cospinCountCombox.setEnabled(false);
		}
		pinCountCombox  = new JComboBox();
		pinCountCombox.addItem("VLANPRI0");
		pinCountCombox.addItem("VLANPRI1");
		pinCountCombox.addItem("VLANPRI2");
		pinCountCombox.addItem("VLANPRI3");
		pinCountCombox.addItem("VLANPRI4");
		pinCountCombox.addItem("VLANPRI5");
		pinCountCombox.addItem("VLANPRI6");
		pinCountCombox.addItem("VLANPRI7");
		TableColumn pinColumn = legTable.getColumn("VLANPRI");
		pinColumn.setCellEditor(new DefaultCellEditor(pinCountCombox));
		pinCountCombox.setEnabled(false);
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
		this.btnSave.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					btnSaveListener();
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
	}
	
	/**
	 * 保存按钮事件
	 * @throws Exception
	 */
	protected void btnSaveListener() throws Exception{
		//收集界面数据
		int groupId = 0;
		String result = null;
		List<QosMappingMode> infos ;
		DispatchUtil portDispatch = null;
		DispatchUtil portLagDispatch = null;
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
			if(this.verifyName(this.txtName.getText(),beforeName)){
				result =  ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST);
				return;
			}
			//收集数据
			QosMappingMode  qosMappingMode = getTableData();
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
					if(this.portTree.getNeTreePanel().getSelectPortInst()!=null&&0!=this.portTree.getNeTreePanel().getSelectPortInst().size()){
						for (PortInst portInst:this.portTree.getNeTreePanel().getSelectPortInst()) {
//							if(this.portTree.getNeTreePanel().getElement().get(j) instanceof Port){
//								portInst = (PortInst) ((Port)this.portTree.getNeTreePanel().getElement().get(j)).getUserObject();
								if(0!=portInst.getLagId()){
									portLagInfo = new PortLagInfo();
									portLagInfo.setId(portInst.getLagId());
									portLagInfo = portLagService.selectByCondition(portLagInfo).get(0);
									portLagInfoList.add(portLagInfo);
								}else{
									portInstList.add(portInst);
								}
							}
					//	}
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
							portObj.setMappingVlanpriToColor(qosMappingMode.getBusinessId());
							result = portDispatch.excuteUpdate(portObj);
						}
					}
					//下发lag
					if(null!=portLagInfoList&&portLagInfoList.size()>0){
						for(PortLagInfo lagObj:portLagInfoList){
							lagObj.setMappingVlanpriToColor(qosMappingMode.getBusinessId());
							result = portLagDispatch.excuteUpdate(lagObj);
						}
					}
				}
			}else{
				if(0!=groupId){
					result = ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS);
				}
			}
			this.vlanpriToColorPanel.getController().refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(portLagService);
			UiUtil.closeService_MB(mappingModeService);
			UiUtil.closeService_MB(qosMappingTemplateService);
			DialogBoxUtil.succeedDialog(this,result);
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
				flag = qosMappingTemplateService.nameRepetition(afterName, beforeName ,QosTemplateTypeEnum.VLANPRI_COLOR.getValue());
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,VerifyNameUtil.class);
		} finally {
			UiUtil.closeService_MB(qosMappingTemplateService);
		}
		return flag;
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
		if(null!=this.qosMappingMode){
			
		}
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
					mappingAttrList.get(i-1).setValue(Integer.parseInt(temp.get(2).toString().substring(7, 8)));
				}
				if(temp.get(3)!=null){
					mappingAttrList.get(i-1).setColor(EMappingColorEnum.from(ResourceUtil.srcStr(StringKeysObj.YELLOW).equals(temp.get(3))?"YELLOW":"GREEN"));
				}
				mappingAttrList.get(i-1).setName(txtName.getText());
				mappingAttrList.get(i-1).setModel(QosTemplateTypeEnum.from("VLANPRI_COLOR"));
				i += 1;
			
			}
			this.qosMappingMode.setName(txtName.getText());
			this.qosMappingMode.setQosMappingAttrList(mappingAttrList);
		}else{
			while (it.hasNext()) {
				qosMappingAttr = new QosMappingAttr();
				Vector temp = (Vector) it.next();
				if(temp.get(2)!=null){
					qosMappingAttr.setValue(Integer.parseInt(temp.get(2).toString().substring(7, 8)));
				}
				if(temp.get(3)!=null){
					qosMappingAttr.setColor(EMappingColorEnum.from(ResourceUtil.srcStr(StringKeysObj.YELLOW).equals(temp.get(3))?"YELLOW":"GREEN"));
				}
				qosMappingAttr.setMappingType(QosTemplateTypeEnum.VLANPRI_COLOR.getValue());
				qosMappingAttr.setName(txtName.getText());
				if(null!=siteInst){
					qosMappingAttr.setSiteId(siteInst.getSite_Inst_Id());
				}
				qosMappingAttrList.add(qosMappingAttr);
			}
			this.qosMappingMode.setName(txtName.getText());
			this.qosMappingMode.setQosMappingAttrList(qosMappingAttrList);
			this.qosMappingMode.setType(QosTemplateTypeEnum.VLANPRI_COLOR.getValue());
			this.qosMappingMode.setTypeName(QosTemplateTypeEnum.VLANPRI_COLOR.toString());
			if(null!=siteInst){
				this.qosMappingMode.setSiteId(siteInst.getSite_Inst_Id());
			}
		}
		return qosMappingMode;
	}
	private JTable legTable;  //列表
	private JScrollPane legJScrollPanel; //列表面板
	private QosMappingMode qosMappingMode;// qosMappingMode对象
	private List<QosMappingAttr> mappingAttrList;// 列表参数
	private JPanel upPanel;// 第一部分面板
	private PtnButton btnSave; // 确定按钮
	private JButton btnCanel;//取消按钮
	private JPanel panelBtn; // 按钮布局
	private PortTreePanel portTree; // 按钮布局
	private VlanpriToColorPanel vlanpriToColorPanel;// 主面板
	private JComboBox pinCountCombox;// 第一列
	private JComboBox cospinCountCombox;// 第二列
	private JLabel lblName;//名字lbl
	private PtnTextField txtName;//名字txt
	private JLabel lblMessage;//提示信息 lbl
	
}
