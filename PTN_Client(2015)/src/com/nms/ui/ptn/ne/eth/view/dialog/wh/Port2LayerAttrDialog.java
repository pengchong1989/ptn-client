package com.nms.ui.ptn.ne.eth.view.dialog.wh;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

import com.nms.db.bean.equipment.port.Port2LayerAttr;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.CommonBean;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.port.Port2LayerAttrService_MB;
import com.nms.model.ptn.EthService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTip;
/**
 * @author guoqc
 */
public class Port2LayerAttrDialog extends PtnDialog{
	private static final long serialVersionUID = 6660404902413941985L;
	private PortInst portInst;
	private Port2LayerAttr port2LayerAttr;
	private PtnButton btnSave;// 确定按钮
	private JButton btnCanel;// 取消按钮
	private JPanel panelBtn;// 按钮布局
	private JLabel lblName;// 名称标签
	private JTextField txtName;// 名称对话框
	private JLabel lblMacEnable;// mac地址学习使能标签
	private JComboBox jcbMacEnable;// mac地址学习使能下拉框
	private JLabel lblMacCount;// mac地址学习条目数标签
	private JTextField txtMacCount;// mac地址学习条目数对话框
	private JLabel lblTpId;// 入匹配TPID类型标签
	private JComboBox jcbTpId;// 入匹配TPID类型下拉框
	private JPanel panelName;
	private JLabel portType;//端口类型
	private JComboBox portTypeJComboBox;
	private JLabel pVIDJLabel;//PVID
	private JComboBox pvidJComboBox;
	private JLabel qinqEnableJLabel;//qinq使能
	private JComboBox qinqEnableJComboBox;
	private JLabel qinqModelJLabel;//qinq模式
	private JComboBox qinqModelJComboBox;
	private JLabel vlanJLabel;//vlan配置
	private JTable vlanJTable;
	private JScrollPane vlanJScrollPane;
	private JLabel qinqJLabel;//qinq配置
	private JTable qinqJTable;
	private JScrollPane qinqJScrollPane;
	private JButton addButton;//添加
	private JButton deleteButton;//删除
	public Port2LayerAttrDialog(PortInst portInst) {
		this.portInst = portInst;
		this.port2LayerAttr = this.getPort2LayerAttr();
		this.setModal(true);
		super.setTitle(ResourceUtil.srcStr(StringKeysMenu.MENU_PORT_2LAYER_ATTR));
		try {
			this.initComponents();
			this.setLayout();
			this.initData();
			this.addListeners();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private Port2LayerAttr getPort2LayerAttr() {
		Port2LayerAttrService_MB service = null;
		try {
			Port2LayerAttr condition = new Port2LayerAttr();
			condition.setPortId(this.portInst.getPortId());
			condition.setSiteId(ConstantUtil.siteId);
			service = (Port2LayerAttrService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT_2LAYER_ATTR);
			List<Port2LayerAttr> attrList = service.selectByCondition(condition);
			if(attrList.size() > 0){
				return attrList.get(0);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
		return null;
	}

	private void initComponents()throws Exception{
		this.btnCanel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		this.btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE));
		this.panelBtn = new JPanel();
		this.lblName = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_NAME));
		this.txtName = new JTextField();
		this.txtName.setEditable(false);
		this.lblMacEnable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MAC_LEARN));
		this.jcbMacEnable = new JComboBox();
		this.lblMacCount = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MAC_COUNT));
		this.txtMacCount = new JTextField();
		this.lblTpId = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TP_ID));
		this.jcbTpId = new JComboBox();
		this.panelName = new JPanel();
		portType = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_TYPE));
		portTypeJComboBox = new JComboBox();
		pVIDJLabel = new JLabel("PVID");
		pvidJComboBox = new JComboBox();
		qinqEnableJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_QINQ_ENABLE));
		qinqEnableJComboBox = new JComboBox();
		qinqModelJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_QINQ_MODEL));
		qinqModelJComboBox = new JComboBox();
		vlanJLabel = new JLabel(ResourceUtil.srcStr(StringKeysPanel.PANEL_PW_PORT_CONFIGE));
		vlanJTable = new JTable();
		vlanJScrollPane = new  JScrollPane();
		vlanJScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		vlanJScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		vlanJScrollPane.setViewportView(vlanJTable);
		qinqJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_QINQ_CONFIG));
		qinqJTable = new JTable();
		qinqJScrollPane = new  JScrollPane();
		qinqJScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		qinqJScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		qinqJScrollPane.setViewportView(qinqJTable);
		initTable();
		setTbaleModel();
		addButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_ADD));
		deleteButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_DELETE));
	}

	private void setTbaleModel() {
		JComboBox tagJComboBox = new JComboBox();
		tagJComboBox.addItem("TAG");
		tagJComboBox.addItem("UNTAG");
		TableColumn pinCountColumn = vlanJTable.getColumn("VLAN MODEL");
		pinCountColumn.setCellEditor(new DefaultCellEditor(tagJComboBox));
	}

	private void initTable(){
		vlanJTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] { ResourceUtil.srcStr(StringKeysObj.ORDER_NUM), "Vlan ID","VLAN MODEL" }) {

			private static final long serialVersionUID = 714435537424144075L;
			Class[] types = new Class[] { java.lang.Object.class, java.lang.Object.class ,java.lang.Object.class};

			@Override
			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if (columnIndex == 0 || columnIndex == 1)
					return false;
				return true;
			}

		});
		vlanJTable.getTableHeader().setResizingAllowed(true);
		vlanJTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);

		TableColumn c = vlanJTable.getColumnModel().getColumn(0);
		c.setPreferredWidth(30);
		c.setMaxWidth(30);
		c.setMinWidth(30);
		
		qinqJTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] { ResourceUtil.srcStr(StringKeysObj.ORDER_NUM), ResourceUtil.srcStr(StringKeysLbl.LBL_IN_VLAN_MAX),ResourceUtil.srcStr(StringKeysLbl.LBL_OUT_VLAN_MAX),ResourceUtil.srcStr(StringKeysLbl.LBL_OUT_VLAN_CONFIG) }) {

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
		qinqJTable.getTableHeader().setResizingAllowed(true);
		qinqJTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		TableColumn c1 = qinqJTable.getColumnModel().getColumn(0);
		c1.setPreferredWidth(30);
		c1.setMaxWidth(30);
		c1.setMinWidth(30);
	}
	/**
	 * 设置布局
	 */
	private void setLayout()throws Exception {
		this.setButtonLayout();
		this.setTextLayout();
		
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 40,300,40 };
		componentLayout.columnWeights = new double[] { 0, 0 };
		componentLayout.rowHeights = new int[] {170, 40 };
		componentLayout.rowWeights = new double[] { 0, 0.2 };
		this.setLayout(componentLayout);                                                                                             

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);

		c.gridx = 1;
		c.gridy = 0;
		componentLayout.setConstraints(this.panelName, c);
		this.add(this.panelName);
	}

	/**
	 * 设置按钮布局
	 */
	private void setButtonLayout() throws Exception{
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 240, 30 };
		componentLayout.columnWeights = new double[] { 0, 0 };
		componentLayout.rowHeights = new int[] { 30 };
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
		componentLayout.columnWidths = new int[] { 90, 70,70,160 };
		componentLayout.columnWeights = new double[] { 0, 0 ,0,0};
		componentLayout.rowHeights = new int[] { 30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30};
		componentLayout.rowWeights = new double[] { 0 };
		this.panelName.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		componentLayout.setConstraints(this.lblName, c);
		this.panelName.add(this.lblName);

		c.gridx = 1;
		c.gridwidth = 3;
		componentLayout.setConstraints(this.txtName, c);
		this.panelName.add(this.txtName);
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.lblMacEnable, c);
		this.panelName.add(this.lblMacEnable);

		c.gridx = 1;
		c.gridwidth = 3;
		componentLayout.setConstraints(this.jcbMacEnable, c);
		this.panelName.add(this.jcbMacEnable);
		
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.lblMacCount, c);
		this.panelName.add(this.lblMacCount);

		c.gridx = 1;
		c.gridwidth = 3;
		componentLayout.setConstraints(this.txtMacCount, c);
		this.panelName.add(this.txtMacCount);
		
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.lblTpId, c);
		this.panelName.add(this.lblTpId);

		c.gridx = 1;
		c.gridwidth = 3;
		componentLayout.setConstraints(this.jcbTpId, c);
		this.panelName.add(this.jcbTpId);
		
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.portType, c);
		this.panelName.add(this.portType);

		c.gridx = 1;
		c.gridwidth = 3;
		componentLayout.setConstraints(this.portTypeJComboBox, c);
		this.panelName.add(this.portTypeJComboBox);
		
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.pVIDJLabel, c);
		this.panelName.add(this.pVIDJLabel);

		c.gridx = 1;
		c.gridwidth = 3;
		componentLayout.setConstraints(this.pvidJComboBox, c);
		this.panelName.add(this.pvidJComboBox);
		
		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.qinqEnableJLabel, c);
		this.panelName.add(this.qinqEnableJLabel);

		c.gridx = 1;
		c.gridwidth = 3;
		componentLayout.setConstraints(this.qinqEnableJComboBox, c);
		this.panelName.add(this.qinqEnableJComboBox);
		
		c.gridx = 0;
		c.gridy = 7;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.qinqModelJLabel, c);
		this.panelName.add(this.qinqModelJLabel);

		c.gridx = 1;
		c.gridwidth = 3;
		componentLayout.setConstraints(this.qinqModelJComboBox, c);
		this.panelName.add(this.qinqModelJComboBox);
		
		c.gridx = 0;
		c.gridy = 8;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.qinqJLabel, c);
		this.panelName.add(this.qinqJLabel);
		
		c.gridx = 1;
		c.gridheight = 3;
		c.gridwidth = 3;
		componentLayout.setConstraints(this.qinqJScrollPane, c);
		this.panelName.add(this.qinqJScrollPane);
		
		c.gridx = 1;
		c.gridy = 11;
		c.gridheight = 1;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.addButton, c);
		this.panelName.add(this.addButton);

		c.gridx = 2;
		c.weighty = 11;
		c.gridwidth = 1;
		c.gridheight = 1;
		componentLayout.setConstraints(this.deleteButton, c);
		this.panelName.add(this.deleteButton);
		
		c.gridx = 0;
		c.gridy = 12;
		c.gridwidth = 1;
		c.gridheight = 1;
		componentLayout.setConstraints(this.vlanJLabel, c);
		this.panelName.add(this.vlanJLabel);

		c.gridx = 1;
		c.gridwidth = 3;
		c.gridheight = 3;
		componentLayout.setConstraints(this.vlanJScrollPane, c);
		this.panelName.add(this.vlanJScrollPane);
		
		c.gridx = 3;
		c.gridy = 15;
		c.gridwidth = 1;
		c.gridheight = 1;
		componentLayout.setConstraints(this.panelBtn, c);
		this.panelName.add(this.panelBtn);
	}

	/**
	 * 初始化数据
	 * @throws Exception
	 */
	private void initData() throws Exception{
		try {
			this.jcbMacEnable.addItem(ResourceUtil.srcStr(StringKeysObj.ALLCONFIG_FID_ENABLED_NO));
			this.jcbMacEnable.addItem(ResourceUtil.srcStr(StringKeysObj.ALLCONFIG_FID_ENABLED));
			this.txtMacCount.setText("512");
			this.jcbTpId.addItem("8100");
			this.jcbTpId.addItem("88a8");
			this.jcbTpId.addItem("9100");
			this.jcbTpId.addItem("9200");
			this.portTypeJComboBox.addItem("ACCESS");
			this.portTypeJComboBox.addItem("TRUNK");
			this.portTypeJComboBox.addItem("HYBRID");
			this.qinqModelJComboBox.addItem("DOWN");
			this.qinqModelJComboBox.addItem("UP");
			super.getComboBoxDataUtil().comboBoxData(qinqEnableJComboBox, "ENABLEDSTATUE");
			if(this.port2LayerAttr.getId() > 0){
				//说明是修改
				this.txtName.setText(this.portInst.getPortName());
				this.jcbMacEnable.setSelectedIndex(this.port2LayerAttr.getMacEnable());
				this.txtMacCount.setText(this.port2LayerAttr.getMacCount()+"");
				this.jcbTpId.setSelectedIndex(this.port2LayerAttr.getTpIdType());
				this.portTypeJComboBox.setSelectedIndex(this.port2LayerAttr.getPortType());
				super.getComboBoxDataUtil().comboBoxSelectByValue(qinqEnableJComboBox, this.port2LayerAttr.getQinqEnable()+"");
				this.qinqModelJComboBox.setSelectedIndex(this.port2LayerAttr.getQinqModel());
				
				List<Integer> vlanIDs = this.querySecondEth();
				
				DefaultTableModel vlanDefaultTableModel = (DefaultTableModel) vlanJTable.getModel();
				String[] vlans = port2LayerAttr.getVlans().split(",");
				for(Integer vlanid: vlanIDs){
					boolean has = false;
					for (int i = 0; i < vlans.length; i++) {
						if(!vlans[i].equals("") && vlans[i].split(":")[0].equals(vlanid+"")){
							has = true;
							Object[] ob = new Object[]{""+(i+1),vlans[i].split(":")[0],vlans[i].split(":")[1]};
							vlanDefaultTableModel.addRow(ob);
							break;
						}
					}
					if(!has){
						Object[] ob = new Object[]{""+(vlanDefaultTableModel.getRowCount()+1),vlanid,"TAG"};
						vlanDefaultTableModel.addRow(ob);
					}
					has = false;
					pvidJComboBox.addItem(vlanid);
				}
				pvidJComboBox.addItem(1);
				pvidJComboBox.setSelectedItem(this.port2LayerAttr.getPvid());
				vlanJTable.setModel(vlanDefaultTableModel);	
				
				DefaultTableModel qinqDefaultTableModel = (DefaultTableModel) qinqJTable.getModel();
				String[] qinqs = port2LayerAttr.getQinqs().split(",");
				for (int i = 0; i < qinqs.length; i++) {
					if(!qinqs[i].equals("")){
						Object[] ob = new Object[]{""+(i+1),qinqs[i].split(":")[0],qinqs[i].split(":")[1],qinqs[i].split(":")[2]};
						qinqDefaultTableModel.addRow(ob);
					}
				}
				qinqJTable.setModel(qinqDefaultTableModel);	
				
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	/**
	 * 查询该端口下关联的二层业务
	 */
	private List<Integer> querySecondEth(){
		String portLine = "portLine"+(portInst.getNumber()/8+1);
		EthService_MB ethService = null;
		List<Integer> vlanIDs = null;
		try {
			ethService = (EthService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ETHSERVICE);
			vlanIDs = ethService.queryBySiteIdAndPortLine(portInst.getSiteId(), portLine,portInst.getNumber()%8);
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(ethService);
		}
		return vlanIDs;
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

		this.btnSave.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					btnSaveListener();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		});
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model = (DefaultTableModel) qinqJTable.getModel();
				Object[] object = new Object[]{""+(model.getRowCount()+1),"2","2","2"};
				model.addRow(object);
				qinqJTable.setModel(model);
			}
		});
		deleteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model = (DefaultTableModel) qinqJTable.getModel();
				int[] integers = qinqJTable.getSelectedRows();
				for (int i = 0; i < integers.length; i++) {
					model.removeRow(integers[i]-i);
				}
				model.fireTableDataChanged();
				qinqJTable.updateUI();
			}
		});
		vlanCheck(qinqJTable,true);
	}

	private void vlanCheck(final JTable table,final boolean isQinq){
		table.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent evt) {
				if(table.getSelectedColumn()>0){
					int selectR = table.getSelectedRow();
					int selectC = table.getSelectedColumn();
					//qinq内外层VLAN验证
					if(isQinq){
						int oldValue = Integer.parseInt(table.getValueAt(selectR, selectC).toString());
						try {
							//表格数字与范围验证
							if(table.isEditing()){
								table.getCellEditor().stopCellEditing();
							}
							int value = Integer.parseInt(table.getValueAt(selectR, selectC).toString());
							if(value <1 || value >4095){
								DialogBoxUtil.errorDialog(Port2LayerAttrDialog.this, "VLAN"+ResourceUtil.srcStr(StringKeysTip.TIP_PVID_1_4095));
								table.setValueAt(oldValue, selectR, selectC);
								table.updateUI();
							}
							if(selectC == 1){
								int vlan = Integer.parseInt(table.getValueAt(selectR, selectC+1).toString());
								if(value>vlan){
									DialogBoxUtil.errorDialog(Port2LayerAttrDialog.this, ResourceUtil.srcStr(StringKeysTip.TIP_OUTVALN_INVLAN));
									table.setValueAt(oldValue, selectR, selectC);
									table.updateUI();
								}
							}else if(selectC ==2){
								int vlan = Integer.parseInt(table.getValueAt(selectR, selectC-1).toString());
								if(value<vlan){
									DialogBoxUtil.errorDialog(Port2LayerAttrDialog.this, ResourceUtil.srcStr(StringKeysTip.TIP_OUTVALN_INVLAN));
									table.setValueAt(oldValue, selectR, selectC);
									table.updateUI();
								}
							}
						} catch (Exception e) {
							if (table.isEditing()) {
								table.getCellEditor().stopCellEditing();
							}
							DialogBoxUtil.errorDialog(Port2LayerAttrDialog.this, "VLAN"+ResourceUtil.srcStr(StringKeysTip.TIP_PVID_1_4095));
							table.setValueAt(oldValue, selectR, selectC);
							table.updateUI();
						}
					}
				}
			}
		});
	}
	
	/**
	 * 保存按钮事件
	 * @throws Exception
	 */
	protected void btnSaveListener() throws Exception{
		DispatchUtil protDispatchUtil = null;
		String result = null;
		try {
			if(this.checkData()){
				this.getData();
				Port2LayerAttr portLayerBefore = this.getPort2LayerAttr();
				protDispatchUtil = new DispatchUtil(RmiKeys.RMI_PORT_2LAYER_ATTR);
				result = protDispatchUtil.excuteUpdate(this.port2LayerAttr);
				DialogBoxUtil.succeedDialog(this,result);
				this.getQinqVlan(portLayerBefore, 0);
				this.getQinqVlan(this.port2LayerAttr, 1);
				AddOperateLog.insertOperLog(btnSave, EOperationLogType.PORT_2LAYER_ATTR.getValue(), result, 
						portLayerBefore, this.port2LayerAttr, ConstantUtil.siteId, this.portInst.getPortName()+"_layer", "port2Layer");
				this.dispose();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	/**
	 * 将界面数据做转换，便于日志记录
	 * @param portLayer
	 * type 0/1 修改前数据/修改后数据
	 */
	private void getQinqVlan(Port2LayerAttr portLayer, int type) {
		if(type == 1){
			portLayer.setTpIdTypeLog(this.jcbTpId.getSelectedItem().toString());
			portLayer.setQinqModelLog(this.qinqModelJComboBox.getSelectedItem().toString());
		}else{
			int value = portLayer.getTpIdType();
			if(value == 0){
				portLayer.setTpIdTypeLog("8100");
			}else if(value == 1){
				portLayer.setTpIdTypeLog("88a8");
			}else if(value == 2){
				portLayer.setTpIdTypeLog("9100");
			}else{
				portLayer.setTpIdTypeLog("9200");
			}
			portLayer.setQinqModelLog(portLayer.getQinqModel() == 0 ? "DOWN":"UP");
		}
		if(portLayer.getQinqs() != null && portLayer.getQinqs().trim().length() > 0){
			List<CommonBean> qinQVlanLimitList = new ArrayList<CommonBean>();
			String[] vlanIdArr = portLayer.getQinqs().trim().split(",");
			for (String vlanId : vlanIdArr) {
				CommonBean cb = new CommonBean();
				cb.setQinQVlanLimit(vlanId.substring(0, vlanId.length()-1));
				qinQVlanLimitList.add(cb);
			}
			portLayer.setQinQVlanLimit(qinQVlanLimitList);
		}
	}

	private boolean checkData() {
		try {
			int macCount = Integer.parseInt(this.txtMacCount.getText().trim());
			if(macCount < 1 || macCount > 30000){
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_MAC_COUNT_1_30000));
				return false;
			}
		} catch (Exception e) {
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_MAC_COUNT_1_30000));
			return false;
		}
		return true;
	}

	/**
	 * 获取界面的值
	 */
	private void getData() {
		this.port2LayerAttr.setMacEnable(this.jcbMacEnable.getSelectedIndex());
		this.port2LayerAttr.setMacCount(Integer.parseInt(this.txtMacCount.getText().trim()));
		this.port2LayerAttr.setTpIdType(this.jcbTpId.getSelectedIndex());
		this.port2LayerAttr.setPortType(this.portTypeJComboBox.getSelectedIndex());
		Object s = pvidJComboBox.getSelectedItem();
		this.port2LayerAttr.setPvid(Integer.parseInt(s.toString()));
		ControlKeyValue controlKeyValue = (ControlKeyValue) qinqEnableJComboBox.getSelectedItem();
		this.port2LayerAttr.setQinqEnable(Integer.parseInt(((Code)controlKeyValue.getObject()).getCodeValue()));
		this.port2LayerAttr.setQinqModel(this.qinqModelJComboBox.getSelectedIndex());
		
		//vlan表格数据收集
		DefaultTableModel vlans = (DefaultTableModel) vlanJTable.getModel();
		Vector vector = vlans.getDataVector();
		Iterator iterator = vector.iterator();
		StringBuffer vlansBuffer = new StringBuffer();
		int count = 0;
		while(iterator.hasNext()){
			Vector temp = (Vector) iterator.next();
			vlansBuffer.append(temp.get(1)+":"+temp.get(2)+":"+",");
			count++;
		}
		this.port2LayerAttr.setVlans(vlansBuffer.toString());
		this.port2LayerAttr.setVlanCount(count);
		
		//qinq表格数据收集
		DefaultTableModel qinqs = (DefaultTableModel) qinqJTable.getModel();
		Vector vectorqinqs = qinqs.getDataVector();
		Iterator iteratorqinqs = vectorqinqs.iterator();
		StringBuffer qinqsBuffer = new StringBuffer();
		count = 0;
		while(iteratorqinqs.hasNext()){
			Vector temp = (Vector) iteratorqinqs.next();
			qinqsBuffer.append(temp.get(1)+":"+temp.get(2)+":"+temp.get(3)+":"+",");
			count++;
		}
		this.port2LayerAttr.setQinqs(qinqsBuffer.toString());
		this.port2LayerAttr.setQinqCount(count);
	}
}
