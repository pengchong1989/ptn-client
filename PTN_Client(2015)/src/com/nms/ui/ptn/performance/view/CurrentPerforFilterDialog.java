﻿package com.nms.ui.ptn.performance.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import twaver.DataBoxSelectionEvent;
import twaver.DataBoxSelectionListener;
import twaver.Element;
import twaver.Node;
import twaver.TDataBox;
import twaver.VisibleFilter;
import twaver.list.TList;

import com.nms.db.bean.equipment.slot.SlotInst;
import com.nms.db.bean.perform.Capability;
import com.nms.db.enums.EMonitorCycle;
import com.nms.db.enums.EObjectType;
import com.nms.model.perform.CapabilityService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.NeTreePanel;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.performance.model.CurrentPerformanceFilter;

public class CurrentPerforFilterDialog extends PtnDialog {

	private static final long serialVersionUID = 2219190791950771503L;
	private PtnButton confirm;
	private JButton cancel;
	private JButton clear;
	private JLabel lblTaskObj;
	private TList tlist;
	private JScrollPane treePane;
	private JScrollPane typePane;
	private JLabel lblPerforType;
	private TDataBox treeBox;
	private TDataBox typeBox;
	private TList tlType;
	private JCheckBox cbType;
	private JCheckBox cbPerType;
	private JLabel lblCycle;
	private JCheckBox rb15min;
	private JComboBox selectTimeType; 
	private JCheckBox rb24hour;
	private JComboBox selectTimeTypeOther; 
	private ButtonGroup group;
	private String filterInfo;
	private JLabel lblObjectType;
	private JComboBox cbObjectType;
	private JLabel filterZero;
	private JCheckBox filterZeroBox;
	private JLabel  lblDesc;
	private JPanel rb15jpanl;
	private JPanel rb24jpanl;
    private JPanel buttonPanel;
    private Map<String,List<Capability>> portMap=new HashMap<String,List<Capability>>();
    private Map<String,List<Capability>> tmsMap=new HashMap<String,List<Capability>>();
	private Map<String,List<Capability>> tmpMap=new HashMap<String,List<Capability>>();
	private Map<String,List<Capability>> ethMap=new HashMap<String,List<Capability>>();
	private Map<String,List<Capability>> llidMap=new HashMap<String,List<Capability>>();
	private Map<String,List<Capability>> mplsMap=new HashMap<String,List<Capability>>();
	private Map<String,List<Capability>> pdhMap=new HashMap<String,List<Capability>>();
	private Map<String,List<Capability>> phyMap=new HashMap<String,List<Capability>>();
	private Map<String,List<Capability>> ponMap=new HashMap<String,List<Capability>>();
	private Map<String,List<Capability>> pwtdmMap=new HashMap<String,List<Capability>>();
	private Map<String,List<Capability>> stm1Map=new HashMap<String,List<Capability>>();
	private Map<String,List<Capability>> tvc12Map=new HashMap<String,List<Capability>>();
	private Map<String,List<Capability>> allMap=new HashMap<String,List<Capability>>();
	private List<Capability> portList=new ArrayList<Capability>();
	private List<Capability> tmsList=new ArrayList<Capability>();
	private List<Capability> tmpList=new ArrayList<Capability>();
	private List<Capability> ethList=new ArrayList<Capability>();
	private List<Capability> llidList=new ArrayList<Capability>();
	private List<Capability> mplsList=new ArrayList<Capability>();
	private List<Capability> pdhList=new ArrayList<Capability>();
	private List<Capability> phyList=new ArrayList<Capability>();
	private List<Capability> ponList=new ArrayList<Capability>();
	private List<Capability> pwtdList=new ArrayList<Capability>();
	private List<Capability> stm1List=new ArrayList<Capability>();
	private List<Capability> vc12List=new ArrayList<Capability>();
	private List<Capability> oamList=new ArrayList<Capability>();
	private NeTreePanel neTreePanel=null;	//网元树
	private Map<String,List<Capability>> oamMap=new HashMap<String,List<Capability>>();
	
	public CurrentPerforFilterDialog() {
		this.setModal(true);
		init();
	}

	public void init() {
		initComponents();
		setLayout();
		initData();
		addListener();
	}

	private void initData() {
		initType();
	}

	public PtnButton getConfirm() {
		return confirm;
	}

	private void addListener() {

		// 性能类型全选复选框
		cbType.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				if (cbType.isSelected()) {
					typeBox.selectAll();
					cbPerType.setSelected(false);
				} else {
					typeBox.getSelectionModel().clearSelection();
					cbPerType.setSelected(false);
				}
			}
		});
		
		// 性能类别全选复选框
		cbPerType.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				if (cbPerType.isSelected()) {
					treeBox.selectAll();
				} else {
					treeBox.getSelectionModel().clearSelection();
				}
			}
		});

		  typeBox.getSelectionModel().addDataBoxSelectionListener(new DataBoxSelectionListener() {
				@Override
				public void selectionChanged(DataBoxSelectionEvent e) {

					Iterator it =e.getBoxSelectionModel().selection();
					treeBox.clear();
					boolean fals=it.hasNext();
					//增加性能类别
					if(fals){
						while(it.hasNext()){
							Node node=(Node)it.next();
							if(node.getDisplayName().equalsIgnoreCase("PORT")){
								List<Capability> perforList=portMap.get("PORT");
								initTreeData(perforList);
							}else if(node.getDisplayName().equalsIgnoreCase("TMP/TMC")){
								List<Capability> perforList=tmpMap.get("TMP/TMC");
								initTreeData(perforList);
							}else if(node.getDisplayName().equalsIgnoreCase("TMS")){
								List<Capability> perforList=tmsMap.get("TMS");
								initTreeData(perforList);
							}else if(node.getDisplayName().equalsIgnoreCase("ETH")){
								List<Capability> perforList=ethMap.get("ETH");
								initTreeData(perforList);
							}else if(node.getDisplayName().equalsIgnoreCase("LLID")){
								List<Capability> perforList=llidMap.get("LLID");
								initTreeData(perforList);
							}else if(node.getDisplayName().equalsIgnoreCase("MPLS")){
								List<Capability> perforList= mplsMap.get("MPLS");
								initTreeData(perforList);
							}else if(node.getDisplayName().equalsIgnoreCase("PDH")){
								List<Capability> perforList= pdhMap.get("PDH");
								initTreeData(perforList);
							}else if(node.getDisplayName().equalsIgnoreCase("PHY")){
								List<Capability> perforList= phyMap.get("PHY");
								initTreeData(perforList);
							}else if(node.getDisplayName().equalsIgnoreCase("PON")){
								List<Capability> perforList=ponMap.get("PON");
								initTreeData(perforList);
							}else if(node.getDisplayName().equalsIgnoreCase("PWTDM")){
								List<Capability> perforList= pwtdmMap.get("PWTDM");
								initTreeData(perforList);
							}else if(node.getDisplayName().equalsIgnoreCase("STM1")){
								List<Capability> perforList=stm1Map.get("STM1");
								initTreeData(perforList);
							}else if(node.getDisplayName().equalsIgnoreCase("VC12")){
								List<Capability> perforList=tvc12Map.get("VC12");
								initTreeData(perforList);
							}else if(node.getDisplayName().equalsIgnoreCase("1731")){
								List<Capability> perforList=oamMap.get("1731");
								initTreeData(perforList);
							}
						}
					}
					else{
							//增加性能类别
							List<Capability> perforList=allMap.get("ALL");
							if(perforList!=null){
								for (Capability type : perforList) {
									if (type.getCapabilitytype() != null && !"".equals(type.getCapabilitytype())) {
										Node nodeType = new Node();
										if(ResourceUtil.language.equals("zh_CN")){
											nodeType.setName(type.getCapabilitydesc());
											nodeType.setDisplayName(type.getCapabilitydesc());
										}else{
											nodeType.setName(type.getCapabilitydesc_en());
											nodeType.setDisplayName(type.getCapabilitydesc_en());
										}
										nodeType.setUserObject(type);
										treeBox.addElement(nodeType);
									}
								}
							}
					}
				}
			});
		
		// 设置性能类型是否可见
		tlType.addVisibleFilter(new VisibleFilter() {
			@Override
			public boolean isVisible(Element element) {
				return true;
			}
		});
		
		// 设置性能类别是否可见
		tlist.addVisibleFilter(new VisibleFilter() {
			public boolean isVisible(Element element) {
				return true;
			}
		});

		// 取消按钮
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				CurrentPerforFilterDialog.this.dispose();
			}
		});

		// 清除按钮
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CurrentPerforFilterDialog.this.clear();
			}
		});


		
		rb24hour.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if(!rb24hour.isSelected()){
					selectTimeTypeOther.setEnabled(false);
				}else{
					selectTimeTypeOther.setEnabled(true);
					selectTimeType.setEditable(false);
					selectTimeType.setEnabled(false);
				}
			}
		});
		
		rb15min.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if(!rb15min.isSelected()){
					selectTimeType.setEnabled(false);
				}else{
					selectTimeType.setEnabled(true);
					selectTimeTypeOther.setEditable(false);
					selectTimeTypeOther.setEnabled(false);
				}
			}
		});
		
		// 对象类型下拉列表的选项改变事件
		this.cbObjectType.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent itemEvent) {
				if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
					if (itemEvent.getItem().equals(ResourceUtil.srcStr(StringKeysObj.NET_BASE))) {
						neTreePanel.setLevel(2);
					} else if (itemEvent.getItem().equals(ResourceUtil.srcStr(StringKeysObj.BOARD))){
						neTreePanel.setLevel(3);
					} else{
						neTreePanel.setLevel(4);
					}
				}
			}
		});
	}

	private List<Capability> removeRepeatedType(List<Capability> perforTypeList,int label) {
		List<Capability> NorepeatedCapability = perforTypeList;
		//增加性能类型
		if(label==1){
			for (int i = 0; i < NorepeatedCapability.size() - 1; i++) {
				for (int j = NorepeatedCapability.size() - 1; j > i; j--) {
					if (NorepeatedCapability.get(j).getCapabilitytype().equals(NorepeatedCapability.get(i).getCapabilitytype())) {
						NorepeatedCapability.remove(j);
					}
				}
			}
		}else{
			//增加性能类别
			for (int i = 0; i < NorepeatedCapability.size() - 1; i++) {
				for (int j = NorepeatedCapability.size() - 1; j > i; j--) {
					if (NorepeatedCapability.get(j).getCapabilitydesc().equals(NorepeatedCapability.get(i).getCapabilitydesc())) {
						NorepeatedCapability.remove(j);
					}
				}
			}
		}
		return NorepeatedCapability;
	}

	/*
	 * 初始化性能类型
	 */
	private void initType() {
		CapabilityService_MB service = null;
		List<Capability> perforTypeList = null;
		List<Capability> perforList = null;
		try {
			service = (CapabilityService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Capability);
			perforTypeList = service.select();
			perforList=new ArrayList<Capability>();
			perforList.addAll(perforTypeList);
			perforTypeList = removeRepeatedType(perforTypeList,1);
			perforList=removeRepeatedType(perforList,2);
			//增加性能类别
			for (Capability type : perforList) {
				if (type.getCapabilitytype() != null && !"".equals(type.getCapabilitytype())) {
					Node nodeType = new Node();
					if(ResourceUtil.language.equals("zh_CN")){
						nodeType.setName(type.getCapabilitydesc());
						nodeType.setDisplayName(type.getCapabilitydesc());
					}else{
						nodeType.setName(type.getCapabilitydesc_en());
						nodeType.setDisplayName(type.getCapabilitydesc_en());
					}
					nodeType.setUserObject(type);
					treeBox.addElement(nodeType);
					if(type.getCapabilitytype().equalsIgnoreCase("PORT")){
						portList.add(type);
					}else if(type.getCapabilitytype().equalsIgnoreCase("TMP/TMC")){
						tmpList.add(type);
					}else if(type.getCapabilitytype().equalsIgnoreCase("TMS")){
						tmsList.add(type);
					}else if(type.getCapabilitytype().equalsIgnoreCase("ETH")){
						ethList.add(type);
					}else if(type.getCapabilitytype().equalsIgnoreCase("LLID")){
						llidList.add(type);
					}else if(type.getCapabilitytype().equalsIgnoreCase("MPLS")){
						mplsList.add(type);
					}else if(type.getCapabilitytype().equalsIgnoreCase("PDH")){
						pdhList.add(type);
					}else if(type.getCapabilitytype().equalsIgnoreCase("PHY")){
						phyList.add(type);
					}else if(type.getCapabilitytype().equalsIgnoreCase("PON")){
						 ponList.add(type);
					}else if(type.getCapabilitytype().equalsIgnoreCase("PWTDM")){
						pwtdList.add(type);
					}else if(type.getCapabilitytype().equalsIgnoreCase("STM1")){
						stm1List.add(type);
					}else if(type.getCapabilitytype().equalsIgnoreCase("VC12")){
						vc12List.add(type);
					}else if(type.getCapabilitytype().equalsIgnoreCase("1731")){
						oamList.add(type);
					}
				}
			}
			  allMap.put("ALL", perforList);
			  portMap.put("PORT", portList);
			  tmsMap.put("TMS", tmsList);
			  tmpMap.put("TMP/TMC", tmpList);
			  ethMap.put("ETH", ethList);
			  llidMap.put("LLID", llidList);
			  mplsMap.put("MPLS", mplsList);
			  pdhMap.put("PDH", pdhList);
			  phyMap.put("PHY", phyList);
			  ponMap.put("PON", ponList);
			  pwtdmMap.put("PWTDM", pwtdList);
			  stm1Map.put("STM1", stm1List);
			  tvc12Map.put("VC12", vc12List);
			  oamMap.put("1731", oamList);
			//增加性能类型
			for (Capability type : perforTypeList) {
				if (type.getCapabilitytype() != null && !"".equals(type.getCapabilitytype())) {
					Node node = new Node();
					node.setName(type.getCapabilitytype());
					node.setDisplayName(type.getCapabilitytype());
					node.setUserObject(type);
					typeBox.addElement(node);
				}
			}
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
	}

	/**
	 * 验证输入数据的正确性
	 * 
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("rawtypes")
	public boolean validateParams() throws Exception {
		if (!this.neTreePanel.verifySelect()) {
			DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_MONITORING_OBJ));
			return false;
		}
		Iterator it = typeBox.getSelectionModel().selection();
		if (!it.hasNext()) {
			DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_PERFORMANCE_TYPE));
			return false;
		}
		it = treeBox.getSelectionModel().selection();
		if (!it.hasNext()) {
			DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PERFORMANCE_TYPE));
			return false;
		}
		if (!rb15min.isSelected() && !rb24hour.isSelected()) {
			JOptionPane.showMessageDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_MONITORING_PERIOD));
			return false;
		}
		return true;
	}

	// 获取当前对话框的过滤对象
	@SuppressWarnings("rawtypes")
	public CurrentPerformanceFilter get() throws Exception {
		List<SlotInst> slotInstList=null;
		List<Integer> slotInstsCardAddress = null;
		List<Integer> siteIdList = null;
		List<String> site_slotInsts = null;
		CurrentPerformanceFilter filter = new CurrentPerformanceFilter();
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (cbObjectType.getSelectedItem().equals(ResourceUtil.srcStr(StringKeysObj.NET_BASE))) {
			filter.setSiteInsts(this.neTreePanel.getPrimaryKeyList("site"));
			filter.setObjectType(EObjectType.SITEINST);
		} else if (cbObjectType.getSelectedItem().equals(ResourceUtil.srcStr(StringKeysObj.BOARD))) {
			//如果选中的板卡，把槽位的mastercardaddress属性和网元主键/mastercardaddress 加载到过滤条件中
			slotInstsCardAddress = new ArrayList<Integer>();
			site_slotInsts = new ArrayList<String>();
			slotInstList=this.neTreePanel.getSelectSlotInst();
			siteIdList = new ArrayList<Integer>();
			for(SlotInst slotInst : slotInstList){
				slotInstsCardAddress.add(Integer.valueOf(slotInst.getMasterCardAddress()));
				site_slotInsts.add(slotInst.getSiteId() + "/" + slotInst.getMasterCardAddress());
				siteIdList.add(slotInst.getSiteId());
			}
//			filter.setSiteInsts(siteIdList);   
			filter.setSiteInsts(this.neTreePanel.getPrimaryKeyList("site"));
			filter.setSlotInsts(slotInstsCardAddress);
			filter.setSite_slotInsts(site_slotInsts);
			filter.setObjectType(EObjectType.SLOTINST);
		}else if (cbObjectType.getSelectedItem().equals(ResourceUtil.srcStr(StringKeysObj.PORT))) {
			//如果选中的端口，把端口的主键id和number属性和网元主键加载到过滤条件中
			filter.setSiteInsts(this.neTreePanel.getPrimaryKeyList("site"));
			filter.setPortInsts(this.neTreePanel.getPrimaryKeyList("port"));
			filter.setObjectType(EObjectType.PORT);
		}
		
		// 添加性能类型条件
		Iterator it = typeBox.getSelectionModel().selection();
		
		if (it.hasNext()) {
			StringBuilder strTypeBuilder = new StringBuilder();
			while (it.hasNext()) {
				Node node = (Node) it.next();
				if (node.getUserObject() instanceof Capability) {
					Capability capability = (Capability) node.getUserObject();
					filter.getCapabilityIdList().add(Integer.valueOf(capability.getId()));
					strTypeBuilder.append(capability.getCapabilitytype()).append(",");
				}
			}
			String str = strTypeBuilder.toString();
			str = str.substring(0, str.length() - 1);
			filter.setTypeStr(str);
		}
		
		// 添加性能类别条件
		it = treeBox.getSelectionModel().selection();
		if (it.hasNext()) {
			StringBuilder strTypeBuilder = new StringBuilder();
			while (it.hasNext()) {
				Node node = (Node) it.next();
				if (node.getUserObject() instanceof Capability) {
					Capability capability = (Capability) node.getUserObject();
					filter.getCapabilityNameList().add(capability.getCapabilitydesc());
					strTypeBuilder.append(capability.getCapabilitydesc()).append(",");
				}
			}
			String str = strTypeBuilder.toString();
			str = str.substring(0, str.length() - 1);
		}
		if (rb24hour.isSelected()) {
			filter.setMonitorCycle(EMonitorCycle.HOUR24);
			if(selectTimeTypeOther.getSelectedIndex()==0){
				filter.setPerformanceCount(255);
				filter.setPerformanceBeginCount(0);
				filter.setPerformanceType(32);
				filter.setPerformanceMonitorTime(porseTime(100));
			}else if(selectTimeTypeOther.getSelectedIndex()==1){
				filter.setPerformanceCount(255);
				filter.setPerformanceBeginCount(1);
				filter.setPerformanceType(33);
				filter.setPerformanceMonitorTime(porseTime(101));
			}
		} else if (rb15min.isSelected()) {
			filter.setMonitorCycle(EMonitorCycle.MIN15);
			filter.setPerformanceType(0);
			if(selectTimeType.getSelectedIndex()==0){
				filter.setPerformanceCount(0);
				filter.setPerformanceBeginCount(0);
				filter.setPerformanceMonitorTime(porseTime(0));
			}else {
				filter.setPerformanceCount(1);
				filter.setPerformanceBeginCount(selectTimeType.getSelectedIndex());
				filter.setPerformanceBeginDataTime(testTime(selectTimeType.getSelectedIndex()));
				filter.setPerformanceMonitorTime(porseTime(selectTimeType.getSelectedIndex()));
			}
			
		} else {
			filter.setMonitorCycle(null);
		}
		filter.setPerformanceOverTime(df.format(new Date()));
		//当零值过滤选择上 就将显示性能值为0的
		if(filterZeroBox.isSelected()){
			filter.setFilterZero(0);
		}else{
			filter.setFilterZero(1);
		}
		return filter;
	}

	// 清除面板信息
	private void clear() {
		this.neTreePanel.clear();
		typeBox.getSelectionModel().clearSelection();
		cbType.setSelected(false);
		cbPerType.setSelected(false);
		treeBox.getSelectionModel().clearSelection();
		filterZeroBox.setSelected(false);
		group.clearSelection();
		
		if(selectTimeTypeOther.isEnabled()){
			selectTimeTypeOther.setEnabled(false);
		}
		if(selectTimeType.isEnabled()){
			selectTimeType.setEnabled(false);
		}
	}

	private void initComponents() {
		this.setTitle(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER));
		lblTaskObj = new JLabel(ResourceUtil.srcStr(StringKeysObj.MONITORING_OBJ));
		lblObjectType = new JLabel(ResourceUtil.srcStr(StringKeysObj.OBJ_TYPE));
		cbObjectType = new JComboBox();
		cbObjectType.addItem(ResourceUtil.srcStr(StringKeysObj.NET_BASE));  //网元
		cbObjectType.addItem(ResourceUtil.srcStr(StringKeysObj.BOARD));     //板卡
		//端口
		cbObjectType.addItem(ResourceUtil.srcStr(StringKeysObj.PORT));      //端口
		this.neTreePanel=new NeTreePanel(false,2,null,false);
		lblPerforType = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PROPERTY_TYPE));
		
		
		typeBox = new TDataBox();
		tlType = new TList(typeBox);
		tlType.setTListSelectionMode(TList.CHECK_SELECTION);
		tlType.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		
		cbType = new JCheckBox(ResourceUtil.srcStr(StringKeysBtn.BTN_ALLSELECT));
		typePane = new JScrollPane();
		typePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		typePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		typePane.setViewportView(tlType);
		
		
		lblCycle = new JLabel(ResourceUtil.srcStr(StringKeysObj.MONITORING_PERIOD));
		group = new ButtonGroup();
		
		lblDesc=new JLabel(ResourceUtil.srcStr(StringKeysObj.CAPABILITYDESC));
		treeBox=new TDataBox();
		treePane=new JScrollPane();
		tlist=new TList(treeBox);
		tlist.setTListSelectionMode(TList.CHECK_SELECTION);
		tlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		treePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		treePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		treePane.setViewportView(tlist);
		cbPerType = new JCheckBox(ResourceUtil.srcStr(StringKeysBtn.BTN_ALLSELECT));
		rb15min=new JCheckBox();
		rb15min.setSelected(true);
		selectTimeType=new JComboBox(); 
		selectTimeType.addItem(ResourceUtil.srcStr(StringKeysObj.NOWFifteMINUTES));
		String now=ResourceUtil.srcStr(StringKeysObj.NOWTIMEMINUTES);
		String nowfifte=ResourceUtil.srcStr(StringKeysObj.TIMEMINUTES);
		for(int i=1;i<17;i++){
			selectTimeType.addItem(now+i+nowfifte);
		}
		
		rb24hour=new JCheckBox();
		selectTimeTypeOther=new JComboBox(); 
		selectTimeTypeOther.addItem(ResourceUtil.srcStr(StringKeysObj.NOWHOURS));
		selectTimeTypeOther.addItem(ResourceUtil.srcStr(StringKeysObj.ONETWOHOURS));
		selectTimeTypeOther.setEnabled(false);
		
		group.add(rb15min);
		group.add(rb24hour);
		rb15jpanl=new JPanel();
		rb15jpanl.add(rb15min);
		rb15jpanl.add(selectTimeType);
		rb24jpanl=new JPanel();
		rb24jpanl.add(rb24hour);
		rb24jpanl.add(selectTimeTypeOther);
		setCompentLayout(rb15jpanl,rb15min,selectTimeType);
		setCompentLayout(rb24jpanl,rb24hour,selectTimeTypeOther);
		confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),true);
		cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		
		buttonPanel=new JPanel();
		buttonPanel.add(confirm);
		buttonPanel.add(cancel);
		setCompentLayoutButton(buttonPanel,confirm,cancel);
		clear = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER_CLEAR));
		filterZero=new JLabel(ResourceUtil.srcStr(StringKeysBtn.PERFORMFITERZERO));
		filterZeroBox=new JCheckBox(ResourceUtil.srcStr(StringKeysBtn.PERFORMFITERZEROBOX));
	}
	/**
	 *  按钮所在panel布局
	 */
	private void setCompentLayout(JPanel jpenl,JCheckBox checkbox,JComboBox combox) {
		GridBagConstraints gridBagConstraints=null;
		GridBagLayout gridBagLayout = null;
		try {
			gridBagLayout = new GridBagLayout();
			gridBagConstraints = new GridBagConstraints();
			gridBagLayout.columnWidths=new int[]{15,15};
			gridBagLayout.columnWeights=new double[]{0,0};
			gridBagLayout.rowHeights=new int[]{21};
			gridBagLayout.rowWeights=new double[]{0};
			
			gridBagConstraints.insets=new Insets(5,5,5,5);
			gridBagConstraints= new GridBagConstraints();
			gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(checkbox, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(combox, gridBagConstraints);
			
			jpenl.setLayout(gridBagLayout);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	/**
	 *  按钮所在按钮布局
	 */
	private void setCompentLayoutButton(JPanel jpenl,JButton button1,JButton button2) {
		GridBagConstraints gridBagConstraints=null;
		GridBagLayout gridBagLayout = null;
		try {
			gridBagLayout = new GridBagLayout();
			gridBagConstraints = new GridBagConstraints();
			gridBagLayout.columnWidths=new int[]{15,15};
			gridBagLayout.columnWeights=new double[]{0,0};
			gridBagLayout.rowHeights=new int[]{21};
			gridBagLayout.rowWeights=new double[]{0};
			
			gridBagConstraints.insets=new Insets(5,5,5,5);
			gridBagConstraints= new GridBagConstraints();
			gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(button1, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(button2, gridBagConstraints);
			
			jpenl.setLayout(gridBagLayout);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	private void setLayout() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 40, 40, 40, 40,230 };
		layout.columnWeights = new double[] { 0, 0, 0, 0, 0.3 };
		layout.rowHeights = new int[] { 20, 20, 20, 20, 20, 20, 20, 20, 20,20,20,20};
		layout.rowWeights = new double[] { 0, 0, 0, 0.3, 0.2,0, 0, 0, 0.2,0,0,0,0};
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.setConstraints(lblObjectType, c);
		this.add(lblObjectType);

		
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(cbObjectType, c);
		this.add(cbObjectType);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(lblTaskObj, c);
		this.add(lblTaskObj);
		
		
		//添加    网元，板块 ，，树Panel
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 2;
		c.gridwidth = 4;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(this.neTreePanel, c);
		this.add(neTreePanel);

		
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(lblPerforType, c);
		this.add(lblPerforType);
		c.gridx = 1;
		c.gridy = 4;
		c.gridheight = 2;
		c.gridwidth = 5;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(typePane, c);
		this.add(typePane);
		
		c.gridx = 1;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(cbType, c);
		this.add(cbType);
		
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(lblDesc, c);
		this.add(lblDesc);
		
		c.gridx = 1;
		c.gridy = 7;
		c.gridheight = 2;
		c.gridwidth = 5;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(treePane, c);
		this.add(treePane);
		
		c.gridx = 1;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(cbPerType, c);
		this.add(cbPerType);
		
		
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 10;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(lblCycle, c);
		this.add(lblCycle);
		
		c.gridx = 1;
		c.gridy = 10;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5,5,5,5);
		layout.addLayoutComponent(rb15jpanl, c);
		this.add(rb15jpanl);
		
		c.gridx = 3;
		c.gridy = 10;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5,5,5,5);
		layout.addLayoutComponent(rb24jpanl, c);
		this.add(rb24jpanl);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 11;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(filterZero, c);
		this.add(filterZero);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = 11;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(filterZeroBox, c);
		this.add(filterZeroBox);
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 12;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(clear, c);
		this.add(clear);
		
		c.gridx = 4;
		c.gridy = 12;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(buttonPanel, c);
		this.add(buttonPanel);
	}

	public String getFilterInfo() {
		return filterInfo;
	}

	public void setFilterInfo(String filterInfo) {
		this.filterInfo = filterInfo;
	}
	/**
	 *处理时间 
	 * @param preseTime
	 * @return
	 */
	private long testTime(int size) {
		SimpleDateFormat fat = null;
		SimpleDateFormat fat2 =null;
		SimpleDateFormat fat3 =null;
		String preseTimeString = null;
		String preseTimeYear=null;
		String[] str = null;
		Date date=null;
		long filterTime=0;
		Date dateNow=null;
		long dateLong=0;
		long  preseTime=0; 
		try {
			dateNow = new Date();
			dateLong = dateNow.getTime();
			preseTime = dateLong-size*15 * 60 * 1000;
			fat = new SimpleDateFormat("HH:mm");
			fat2 = new SimpleDateFormat("yyyy-MM-dd");
			fat3 =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
			date = new Date(preseTime);
			preseTimeYear = fat2.format(date);
			preseTimeString = fat.format(date);
			str = preseTimeString.split(":");
			int i = Integer.valueOf(str[1]) / 15;
			preseTimeYear = preseTimeYear + " " + str[0] + ":";
			if (i == 0) {
				preseTimeString = preseTimeYear  + "00";
			}
			if (i == 1) {
				preseTimeString = preseTimeYear + "15";
			}
			if (i == 2) {
				preseTimeString = preseTimeYear + "30";
			}
			if (i == 3) {
				preseTimeString = preseTimeYear + "45";
			}
			
			filterTime=fat3.parse(preseTimeString).getTime();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			fat = null;
			fat2 = null;
			preseTimeYear=null;
			str = null;
			date=null;
			preseTimeString=null;
		}
		return filterTime;
	}
	/**
	 *处理时间 
	 * @param preseTime
	 * @return
	 */
	private String porseTime(int size) {
		SimpleDateFormat fat = null;
		SimpleDateFormat fat2 =null;
		String preseTimeString = null;
		String preseTimeYear=null;
		Date date=null;
		Date dateNow=null;
		long dateLong=0;
		long  preseTime=0; 
		try {
			dateNow = new Date();
			dateLong = dateNow.getTime();
			if(size>=100){
				fat2 = new SimpleDateFormat("yyyy-MM");
				fat=new SimpleDateFormat("d ");
				preseTime=dateLong-size*24*60 * 60 * 1000;
				date=new Date(preseTime);
				preseTimeYear = fat2.format(date);
				if(size==100){
					preseTimeString=preseTimeYear ;
				}if(size==101){
					date=new Date(preseTime);
					preseTimeString=fat.format(date);
					preseTimeString=preseTimeYear+"-"+"";
				}
			}else{
				fat2 =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				long parseTime = testTime(size);
				preseTimeString = fat2.format(new Date(parseTime));
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			fat = null;
			fat2 = null;
			preseTimeYear=null;
			date=null;
		}
		return preseTimeString;
	}
	/**
	 * 初始化性能类别
	 * @param perforList
	 */
	private void initTreeData(List<Capability> perforList){
		for (Capability type : perforList) {
			if (type.getCapabilitytype() != null && !"".equals(type.getCapabilitytype())) {
				Node nodeType = new Node();
				if(ResourceUtil.language.equals("zh_CN")){
					nodeType.setName(type.getCapabilitydesc());
					nodeType.setDisplayName(type.getCapabilitydesc());
				}else{
					nodeType.setName(type.getCapabilitydesc_en());
					nodeType.setDisplayName(type.getCapabilitydesc_en());
				}
				nodeType.setUserObject(type);
				treeBox.addElement(nodeType);
			}
		}
	}
}