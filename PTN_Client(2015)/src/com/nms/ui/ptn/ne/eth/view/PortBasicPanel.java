﻿package com.nms.ui.ptn.ne.eth.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.table.DefaultTableModel;

import twaver.table.TTable;
import twaver.table.TTablePopupMenuFactory;

import com.nms.db.bean.equipment.port.PortAttr;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.ptn.qos.QosInfo;
import com.nms.db.bean.ptn.qos.QosQueue;
import com.nms.db.bean.system.UdaGroup;
import com.nms.db.enums.EManufacturer;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.QosCosLevelEnum;
import com.nms.db.enums.QosTemplateTypeEnum;
import com.nms.model.equipment.port.PortAttrService_MB;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.ptn.qos.QosInfoService_MB;
import com.nms.model.ptn.qos.QosQueueService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.ne.eth.controller.PortController;
import com.nms.ui.ptn.ne.eth.view.dialog.cx.PortNniCXDialog;
import com.nms.ui.ptn.ne.eth.view.dialog.cx.PortSfpCXDialog;
import com.nms.ui.ptn.ne.eth.view.dialog.cx.PortUniCXDialog;
import com.nms.ui.ptn.ne.eth.view.dialog.wh.Port2LayerAttrDialog;
import com.nms.ui.ptn.ne.eth.view.dialog.wh.PortAttrWHDialog;
import com.nms.ui.ptn.ne.eth.view.dialog.wh.PortPriDialog;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
import com.nms.ui.ptn.systemconfig.dialog.qos.dialog.PortQosQueueCommonConfig;
import com.nms.ui.ptn.systemconfig.dialog.qos.dialog.QosQueueCXPortConfigPanel;
import com.nms.ui.ptn.systemconfig.dialog.qos.dialog.QosQueueWHPortConfigPanel;

/**
 * eth端口基本信息
 * @author __USER__
 */
public class PortBasicPanel extends ContentView<PortInst> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5491889308530635728L;
	public UdaGroup udaGroup;
	private JSplitPane splitPane;
	private JTabbedPane tabbedPane;
	private JTabbedPane expTabbedPane;//exp标签
	private JScrollPane sllPanelTab_attr;	//tab属性滚动面板
//	private JScrollPane priJScrollPane;	//tab属性滚动面板
	private JScrollPane sllPanelTab_uni;	//tabNNI滚动面板
	private JScrollPane sllPanelTab_nni;	//tabUNI滚动面板
	private JScrollPane colorModelConfig;
//	private JMenuItem priorityJMenuItem;//端口优先级
	private JMenuItem portDiscardJMenuItem;//端口丢弃流设置
	private JMenuItem Port2LayerAttrJMenuItem;//端口二层属性配置
	private PortAttrWHDialog attrWhDialog;
	private List<QosQueue> qosQueueList = new ArrayList<QosQueue>();

	/** Creates new form PortBasicPanel */
	public PortBasicPanel() {
		super("portInfoTable",RootFactory.CORE_MANAGE);
		init();

		qosJScrollPane = new JScrollPane();
		SiteService_MB siteService = null;
		try {
			ethPirJscrollPane = new JScrollPane();
			this.tableData();
			
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			if(siteService.getManufacturer(ConstantUtil.siteId) == EManufacturer.WUHAN.getValue()){
				attrWhDialog = new PortAttrWHDialog();
				attrWhDialog.lockHighAttr();
				this.sllPanelTab_attr.setViewportView(attrWhDialog);
				tabbedPane.add(ResourceUtil.srcStr(StringKeysLbl.LBL_HIGH_ATTR), this.sllPanelTab_attr);
				
				qosQueuePanel = new QosQueueWHPortConfigPanel();
				qosJScrollPane.setViewportView(qosQueuePanel);

			}else{
				//sfp
				portSfpCXDialog=new PortSfpCXDialog(null);
				portSfpCXDialog.lock();
				this.sllPanelTab_attr.setViewportView(portSfpCXDialog);
				tabbedPane.add(ResourceUtil.srcStr(StringKeysLbl.LBL_SFP_ATTR),this.sllPanelTab_attr);
				//nni
				portNniCXDialog=new PortNniCXDialog(null);
				portNniCXDialog.lock();
				this.sllPanelTab_nni.setViewportView(this.portNniCXDialog);
				tabbedPane.add(ResourceUtil.srcStr(StringKeysLbl.LBL_NNI_ATTR), this.sllPanelTab_nni);
				//uni
				portUniCXDialog=new PortUniCXDialog(null);
				portUniCXDialog.lock();
				this.sllPanelTab_uni.setViewportView(this.portUniCXDialog);
				tabbedPane.add(ResourceUtil.srcStr(StringKeysLbl.LBL_UNI_ATTR), this.sllPanelTab_uni);
				tabbedPane.setEnabled(false);
				
				qosQueuePanel = new QosQueueCXPortConfigPanel();
				qosJScrollPane.setViewportView(qosQueuePanel);
				//EXP映射
				tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_COSTOEXPTEMPLATE), this.expTabbedPane);
				
				colorModelConfig = new JScrollPane(colorModelConfigPanel);
				tabbedPane.add(ResourceUtil.srcStr(StringKeysLbl.LBL_QOS), this.colorModelConfig);

			}
			
			tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_QOS_CONFIG), qosJScrollPane);
			tabbedPane.add(ResourceUtil.srcStr(StringKeysMenu.MENU_PRIORITY_PRI), ethPirJscrollPane);
			tabbedPane.setEnabled(false);

			addListeners();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
		}
	}

	/**
	 * 初始化
	 */
	public void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_ETH_PORT_MANAGE)));
		initComponent();
		setLayout();
	}

	/**
	 * 初始化控件
	 */
	private void initComponent() {
		try {
//		priorityJMenuItem = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_PRIORITY_PRI));
		portDiscardJMenuItem = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_PORT_DISCOED));
		Port2LayerAttrJMenuItem = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_PORT_2LAYER_ATTR));
		tabbedPane = new JTabbedPane();
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		splitPane.setTopComponent(this.getContentPanel());
		splitPane.setBottomComponent(tabbedPane);
		
		this.sllPanelTab_attr=new JScrollPane();
		this.sllPanelTab_nni=new JScrollPane();
		this.sllPanelTab_uni=new JScrollPane();
//		this.priJScrollPane = new JScrollPane();
		int high = Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue() / 2;
		splitPane.setDividerLocation(high - 65);
			
		this.sllPanelTab_attr=new JScrollPane();
		this.sllPanelTab_nni=new JScrollPane();
		this.sllPanelTab_uni=new JScrollPane();
		splitPane.setDividerLocation(high - 65);
		
		this.expTabbedPane = new JTabbedPane();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		
		
	}

	/**
	 * 布局
	 */
	public void setLayout() {
		GridBagLayout panelLayout = new GridBagLayout();
		this.setLayout(panelLayout);
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		panelLayout.setConstraints(splitPane, c);
		this.add(splitPane);
	}

	/**
	 * 监听事件
	 */
	private void addListeners() {
		if(checkRoot(RootFactory.CORE_MANAGE)){
		getTable().addMouseListener(new MouseAdapter() {
		
			@Override
			public void mouseClicked(MouseEvent evt) {
				if (SwingUtilities.isLeftMouseButton(evt) && getSelect() != null) {
					tabbedPane.setEnabled(true);
					tabbedPane.removeAll();
					try {
						ConstantUtil.QOS_PORT_MAX=UiUtil.getMaxCir(getSelect().getPortName());
						if (evt.getClickCount() == 1) {
							doClick();
						} else {
							getController().openUpdateDialog();
						}
					} catch (Exception e) {
						ExceptionManage.dispose(e, this.getClass());
					}
				}
			}
		});
		}
		
		qosQueuePanel.getSaveButton().addActionListener(new MyActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				if(getSelect() == null){
					//如果没有选中一条记录,则提示用户
					returnTip("");
					return;
				}else{
					if(!getSelect().getPortType().equals("NONE")){
						int selectPortId = getSelect().getPortId();
						List<QosQueue> list = null;
						PortInst portInst = new PortInst();
						try {
							DispatchUtil qosqueuedispatch = new DispatchUtil(RmiKeys.RMI_QOSQUEUE);
							list = findByPortId(selectPortId);
							portInst=getSelect();
							PortInst portBefore = new PortInst();
							List<QosQueue> qosListBefore = portInst.getQosQueues();
							portBefore.setQosQueues(qosListBefore);
							list = defaultSaveToQosQueue(selectPortId, ConstantUtil.siteId, list);
							if(checkQosCir(selectPortId, list)){
								//通过
								portInst.setQosQueues(list);
								String isSuccess = qosqueuedispatch.excuteUpdate(portInst);
								DialogBoxUtil.succeedDialog(null, isSuccess);
								//添加日志记录
								AddOperateLog.insertOperLog(qosQueuePanel.getSaveButton(), EOperationLogType.PORTQOSUPDATE.getValue(), isSuccess,
										portBefore, portInst, ConstantUtil.siteId, portInst.getPortName()+"_QoS", "qosQueue");
							}else{
								returnTip("qos");
								doClick();
								return;
							}
						} catch (Exception e) {
							ExceptionManage.dispose(e,this.getClass());
						}
					}else{
						//如果是NONE,则提示用户,请选择类型
						returnTip("none");
						return;
					}
				}
			}
			
			public boolean checking() {
				return true;
			}
		});
		
		qosQueuePanel.getPortQosTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent et) {
				if (qosQueuePanel.getPortQosTable().getEditorComponent() == null) {
					return;
				}
				if (qosQueuePanel.getPortQosTable().getSelectedColumn() > 1) {
					this.commitTable(qosQueuePanel.getPortQosTable());
				}
			}

			/*
			 * 使表格数据瞬间变化
			 */
			public void commitTable(JTable table) {
				int selectR = -1;
				int selectC = -1;
				int newValue = 0;
				JSpinner spinner = null;
				try {
				if (table.getEditorComponent() instanceof JSpinner) {
						spinner = (JSpinner) table.getEditorComponent();
					JTextField ff = ((JSpinner.NumberEditor) (spinner.getComponents()[2])).getTextField();
					String value = ff.getText();
					((DefaultEditor) spinner.getEditor()).getTextField().setText(value);
						selectR = table.getSelectedRow();
						selectC = table.getSelectedColumn();
						for (char di : value.replace(",", "").toCharArray()) {
							if (!Character.isDigit(di)) {
								return;
							}
						}
						if ("".equals(value.replace(",", ""))) {
							newValue = 0;
						} else if (Long.parseLong(value.replace(",", "")) >= ConstantUtil.QOS_PORT_MAX) {
							newValue = ConstantUtil.QOS_PORT_MAX;
						} else if (Long.parseLong(value.replace(",", "")) <= 0) {
							newValue = 0;
						} else {
							newValue = Integer.parseInt(value.replace(",", ""));
						}
						if (selectC != 2 && selectC != 6 && selectC != 9) {
//							if (CodeConfigItem.getInstance().getWuhan() == 0) {
								spinner.setModel(new SpinnerNumberModel(newValue, 0, ConstantUtil.QOS_PORT_MAX, 1));
//							}
						} else if (selectC == 2) {
//							if (CodeConfigItem.getInstance().getWuhan() == 1) {
//								spinner.setModel(new SpinnerNumberModel(newValue, 0,ConstantUtil.QOS_PORT_MAX, 1));
//							}else{
								spinner.setModel(new SpinnerNumberModel(newValue, 0,ConstantUtil.QOS_PORT_MAX, 64));
//							}
						} else if (selectC == 6 || selectC == 9) {
//							if (CodeConfigItem.getInstance().getWuhan() == 0) {
								if (newValue >= 100) {
									newValue = 100;
//								}
								spinner.setModel(new SpinnerNumberModel(newValue, 0, 100, 1));
							}
						}

						spinner.commitEdit();
					if (table.isEditing()) {
						table.getCellEditor().stopCellEditing();
						}
						if (selectC == 2) {
//							if (CodeConfigItem.getInstance().getWuhan() == 1) {
//								if (newValue % 1 != 0) {
//									if (newValue > 1) {
//										newValue = ((newValue / 1)) * 1;
//									} else {
//										newValue = 1;
//									}
//									table.setValueAt(newValue, selectR, selectC);
//								}
//							}else{
								if (newValue % 64 != 0) {
									if (newValue > 64) {
										newValue = ((newValue / 64)) * 64;
									} else {
										newValue = 64;
									}
									table.setValueAt(newValue, selectR, selectC);
								}
//							}
							
						} else {
							table.setValueAt(newValue, selectR, selectC);
						}
					}
				} catch (ParseException e) {
					((DefaultEditor) spinner.getEditor()).getTextField().setText(Integer.valueOf(spinner.getValue().toString()) + "");
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		});
		
//		priorityJMenuItem.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				PortPriDialog PortPriDialog = new PortPriDialog(getSelect());
//				UiUtil.showWindow(PortPriDialog, 700, 400);
//			}
//		});
		
		portDiscardJMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PortDiscardDialog portDiscardDialog = new PortDiscardDialog();
				UiUtil.showWindow(portDiscardDialog, 700, 400);
			}
		});
		
		Port2LayerAttrJMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Port2LayerAttrDialog layerAttrDialog = new Port2LayerAttrDialog(getSelect());
				UiUtil.showWindow(layerAttrDialog, 590, 560);
			}
		});
	}

	private void doClick() {
		SiteService_MB siteService = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			int selectPortId = getSelect().getPortId();
			if(siteService.getManufacturer(ConstantUtil.siteId) == EManufacturer.WUHAN.getValue()){
				attrWhDialog = new PortAttrWHDialog(getSelect());
				attrWhDialog.lockHighAttr();
				sllPanelTab_attr.setViewportView(attrWhDialog);
				tabbedPane.add(ResourceUtil.srcStr(StringKeysLbl.LBL_HIGH_ATTR),sllPanelTab_attr);
				portPriDialog = new PortPriDialog(getSelect());
				ethPirJscrollPane.setViewportView(portPriDialog);
				
			}else{
				portSfpCXDialog=new PortSfpCXDialog(getSelect());
				portNniCXDialog=new PortNniCXDialog(getSelect());
				portUniCXDialog=new PortUniCXDialog(getSelect());
				portSfpCXDialog.lock();
				portNniCXDialog.lock();
				portUniCXDialog.lock();
				
				sllPanelTab_attr.setViewportView(portSfpCXDialog);
				sllPanelTab_nni.setViewportView(portNniCXDialog);
				sllPanelTab_uni.setViewportView(portUniCXDialog);
				
				tabbedPane.add(ResourceUtil.srcStr(StringKeysLbl.LBL_SFP_ATTR),sllPanelTab_attr);
				tabbedPane.add(ResourceUtil.srcStr(StringKeysLbl.LBL_NNI_ATTR), sllPanelTab_nni);
				tabbedPane.add(ResourceUtil.srcStr(StringKeysLbl.LBL_UNI_ATTR), sllPanelTab_uni);

				//EXP映射
				initEXPComponent();
				
				colorModelConfigPanel = new ColorModelConfigPanel(getSelect());
				colorModelConfig.setViewportView(colorModelConfigPanel);
				tabbedPane.add(ResourceUtil.srcStr(StringKeysLbl.LBL_QOS), colorModelConfig);
			}
			
			tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_QOS_CONFIG), qosJScrollPane);
			tabbedPane.add(ResourceUtil.srcStr(StringKeysMenu.MENU_PRIORITY_PRI), ethPirJscrollPane);
			qosQueueList = findByPortId(selectPortId);

			setQosQueueTableDatas(qosQueueList);
			tabbedPanelIsEnable();
			if(siteService.getManufacturer(ConstantUtil.siteId) != EManufacturer.WUHAN.getValue()){
				
				if("NNI".equals(getSelect().getPortType())){
					tabbedPane.setEnabledAt(1, true);
					tabbedPane.setEnabledAt(2, false);
					tabbedPane.setEnabledAt(4, false);
				}else if("UNI".equals(getSelect().getPortType())){
					tabbedPane.setEnabledAt(1, false);
					tabbedPane.setEnabledAt(2, true);
					tabbedPane.setEnabledAt(3, false);
				}else{
					tabbedPane.setEnabledAt(1, false);
					tabbedPane.setEnabledAt(2, false);
					tabbedPane.setEnabledAt(3, false);
					tabbedPane.setEnabledAt(4, false);
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
		} 
	}
	

	/**
	 * 修改端口的qos队列的cir值时验证带宽是否满足tunnel使用
	 * @param qosQueueList 
	 * @param selectPortId 
	 * 通过 返回true
	 * 不通过 返回false
	 */
	private boolean checkQosCir(int portId, List<QosQueue> qosQueueList_after) {
		TunnelService_MB tunnelService = null;
		QosInfoService_MB qosService = null;
		boolean result = true;
		try {
			//验证带宽之前,先判断端口带宽是否改变,如果不变就不必验证,如果改变并且改之后的带宽小于改之前的带宽就需要验证
			Map<Integer, Integer> qosMap_after = new HashMap<Integer, Integer>();
			for (QosQueue qos : qosQueueList) {
				for (QosQueue qosAfter : qosQueueList_after){
					if(qos.getCos() == qosAfter.getCos() && 
							qos.getCir() > qosAfter.getCir()){
						qosMap_after.put(qosAfter.getCos(), qosAfter.getCir());
					}
				}
			}
			//有需要验证的带宽
			if(qosMap_after.size() > 0){
				qosService = (QosInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosInfo);
				tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
				List<Tunnel> tunnelList = tunnelService.selectByPortIdAndSiteId(ConstantUtil.siteId, portId);
				List<Integer> tunnelIdList = new ArrayList<Integer>();
				for (Tunnel tunnel : tunnelList) {
					tunnelIdList.add(tunnel.getTunnelId());
				}
				List<QosInfo> qosList = qosService.selectByCondition("TUNNEL", tunnelIdList);
				//以tunnel已用的qos等级和带宽为准,验证修改的端口的qos带宽是否满足要求
				if(qosList.size() > 0){
					//验证前向
					result = this.checkCirByDirection(1, qosList, qosMap_after);
					if(result == true){
						//验证后向
						result = this.checkCirByDirection(2, qosList, qosMap_after);
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(qosService);
			UiUtil.closeService_MB(tunnelService);
		}
		return result;
	}

	/**
	 * 根据前后向去验证带宽
	 * 通过 返回true
	 * 不通过 返回false
	 */
	private boolean checkCirByDirection(int direction, List<QosInfo> qosList, 
			Map<Integer, Integer> qosMap_after) {
		Map<Integer, Integer> cos_cirMap = new HashMap<Integer, Integer>();
		for (QosInfo usedQos : qosList) {
			if(Integer.parseInt(usedQos.getDirection()) == direction){
				if(cos_cirMap.get(usedQos.getCos()) == null){
					cos_cirMap.put(usedQos.getCos(), 0+usedQos.getCir());
				}else{
					cos_cirMap.put(usedQos.getCos(), cos_cirMap.get(usedQos.getCos())+usedQos.getCir());
				}
			}
		}
		for(int key : qosMap_after.keySet()){
			if(cos_cirMap.get(key) != null){
				if(qosMap_after.get(key) < cos_cirMap.get(key)){
					//端口带宽小于端口所承载的tunnel带宽,则不满足
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 提示用户信息
	 * @param String
	 */
	private void returnTip(String type) {
		if(type.equals("")){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
		}else if(type.equals("none")){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_TYPE));
		}else if(type.equals("qos")){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_QOS_ALARM));
		}
	}

	/**
	 * 修改
	 * @throws Exception
	 */
	public void updatePortWindow() throws Exception {
		try {
			final PortInst portinst = this.getPort(String.valueOf(getSelect().getPortId()));

			final PortPropertyDialog portPropertyDialog = new PortPropertyDialog(portinst, portinst.getPortType(), this.getPortAttrMap(portinst.getPortId()));
			portPropertyDialog.setLocation(UiUtil.getWindowWidth(portPropertyDialog.getWidth()) / 2, UiUtil.getWindowHeight(portPropertyDialog.getHeight()) / 2);
			portPropertyDialog.getJButton().addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					PortInst updatePortInst = null;
					DispatchUtil portDispatch = null;
					String resultStr = null;
					try {
						updatePortInst = portPropertyDialog.getPortInst();
						if (null != updatePortInst) {
							portDispatch = new DispatchUtil(RmiKeys.RMI_PORT);
							resultStr = portDispatch.excuteUpdate(updatePortInst);
							DialogBoxUtil.succeedDialog(portPropertyDialog, resultStr);
							portPropertyDialog.dispose();
							tableData();
						}
					} catch (Exception e) {
						ExceptionManage.dispose(e,this.getClass());
					} finally {
						updatePortInst = null;
					}
				}
			});
			portPropertyDialog.setSize(new Dimension(780, 370));
			portPropertyDialog.setVisible(true);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 获取端口
	 * @param portId
	 * 			端口ID
	 * @return
	 * @throws Exception
	 */
	private PortInst getPort(String portId) throws Exception {
		PortInst portInst = null;
		PortService_MB portService = null;
		List<PortInst> portInstList = null;
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			portInst = new PortInst();
			portInst.setPortId(Integer.parseInt(portId));
			portInstList = portService.select(portInst);

			if (null != portInstList && portInstList.size() == 1) {
				portInst = portInstList.get(0);
			} else {
				throw new Exception("根据主键查询port出错。返回结果为空或者不等于1");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(portService);
			portInstList = null;
		}
		return portInst;
	}

	/**
	 * 根据portID 获取portAttr的map
	 * 
	 * @param portId
	 *            端口ID
	 * @return key-udaattrId value portAttr对象
	 * @throws Exception
	 */
	public Map<Integer, PortAttr> getPortAttrMap(int portId) throws Exception {
		Map<Integer, PortAttr> map = null;
		PortAttrService_MB portAttrService = null;
		PortAttr portAttr = null;
		try {
			portAttrService = (PortAttrService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PortAttr);
			portAttr = new PortAttr();
			map = portAttrService.selectForMap(portAttr);

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(portAttrService);
			portAttr = null;
		}
		return map;
	}

	/**
	 * 根据端口ID和类型查询qos队列
	 * 
	 * @param portId
	 * @return
	 * @throws Exception
	 */
	private List<QosQueue> findByPortId(int portId) throws Exception {
		QosQueueService_MB qosQueueService = null;
		QosQueue qos = null;
		List<QosQueue> list = null;
		try {
			qosQueueService = (QosQueueService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosQueue);
			qos = new QosQueue();
			qos.setObjId(portId);
			qos.setObjType("SECTION");
			list = qosQueueService.queryByCondition(qos);
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(qosQueueService);
		}

		return list;

	}

	/**
	 * 将端口的qos信息保存
	 * @throws Exception 
	 */
	@SuppressWarnings("rawtypes")
	private List<QosQueue> defaultSaveToQosQueue(int portId, int siteId, List<QosQueue> qosQueue) throws Exception {
		DefaultTableModel sectionATableModel = qosQueuePanel.getPortQosTableModel();
		Vector aDataVector = sectionATableModel.getDataVector();
		Iterator dataIterator = null;
		QosQueue queue = null;
		List<QosQueue> qosQueueList = null;
		SiteService_MB siteService = null;
		try {
			// A端
			qosQueueList = new ArrayList<QosQueue>();
			dataIterator = aDataVector.iterator();
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			while (dataIterator.hasNext()) {
				Vector vector = (Vector) dataIterator.next();
				queue = new QosQueue();
				queue.setServiceId(0);
				queue.setSiteId(siteId);
				queue.setObjType("SECTION");
				queue.setCos(QosCosLevelEnum.from(vector.get(1).toString()));
				queue.setCir(new Integer(vector.get(2).toString()));
				queue.setWeight(new Integer(vector.get(3).toString()));
				if(siteService.getManufacturer(ConstantUtil.siteId) == EManufacturer.WUHAN.getValue()){
					queue.setQueueType(vector.get(4).toString());
					queue.setGreenLowThresh(new Integer(vector.get(5).toString()));
					queue.setGreenHighThresh(new Integer(vector.get(6).toString()));
					queue.setWredEnable(vector.get(7).toString().equals("true") ? Boolean.TRUE : Boolean.FALSE);
					queue.setDisCard(new Integer(vector.get(9).toString()));
				}else{
					queue.setQueueType((String) qosQueuePanel.getPortQosQueueComboBox().getSelectedItem());
					queue.setGreenLowThresh(new Integer(vector.get(4).toString()));
					queue.setGreenHighThresh(new Integer(vector.get(5).toString()));
					queue.setGreenProbability(new Integer(vector.get(6).toString()));
					queue.setYellowLowThresh(new Integer(vector.get(7).toString()));
					queue.setYellowHighThresh(new Integer(vector.get(8).toString()));
					queue.setYellowProbability(new Integer(vector.get(9).toString()));
					queue.setWredEnable(vector.get(10).toString().equals("true") ? Boolean.TRUE : Boolean.FALSE);
				}
				
				queue.setMostBandwidth(ResourceUtil.srcStr(StringKeysObj.QOS_UNLIMITED));
				queue.setObjId(portId);
				qosQueueList.add(queue);
			}
			if (!qosQueue.isEmpty()) {
				for (int i = 0; i < qosQueueList.size(); i++) {
					qosQueueList.get(i).setId(qosQueue.get(i).getId());
				}
			}
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(siteService);
		}
		return qosQueueList;
	}

	/**
	 * 页面标签使能
	 */
	private void tabbedPanelIsEnable() {
		String portType = getSelect().getPortType();

		if (portType.equalsIgnoreCase("NONE")) {
			for (int i = 0; i < this.tabbedPane.getTabCount(); i++) {
				if (tabbedPane.getTitleAt(i).equals("NNI")) {
					tabbedPane.setEnabledAt(i, false);
				}
				if (tabbedPane.getTitleAt(i).equals("UNI")) {
					tabbedPane.setEnabledAt(i, false);
				}
			}

		} else {
			for (int i = 0; i < this.tabbedPane.getTabCount(); i++) {
				if (tabbedPane.getTitleAt(i).equals(portType)) {
					int index = i;
					if (portType.equals("NNI")) {
						index = i + 1;
					} else {
						index = i - 1;
					}
					tabbedPane.setEnabledAt(index, false);
				}
			}
		}
		
		if(getSelect().getLagId()>0){
			tabbedPane.setEnabledAt(tabbedPane.getComponentCount()-1, false);
		}

	}

	/**
	 * table数据绑定
	 * 
	 * @throws Exception
	 */
	public void tableData() throws Exception {
		PortService_MB portService = null;
		PortInst portInst = null;
		List<PortInst> allportInstList = null;
		List<PortInst> filterportList = null;
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			portInst = new PortInst();
			portInst.setSiteId(ConstantUtil.siteId);
			portInst.setPortType(ConstantUtil.portType);
			allportInstList = portService.select(portInst);
			// 得到wan和lan类型的端口
			filterportList = getPortInstbyPortType(allportInstList);
			clear();
			initData(filterportList);
			updateUI();
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(portService);
			portInst = null;
			filterportList = null;
			allportInstList = null;
		}
	}

	/**
	 * 按端口类型拿到数据
	 * @param portInstList
	 * @return
	 */
	private List<PortInst> getPortInstbyPortType(List<PortInst> portInstList) {
		List<PortInst> infoList = new ArrayList<PortInst>();
		for (PortInst info : portInstList) {
			if (info.getPortType().equalsIgnoreCase("NNI") || info.getPortType().equalsIgnoreCase("UNI") || info.getPortType().equalsIgnoreCase("NONE")) {
				infoList.add(info);
			}
		}
		return infoList;
	}

	/**
	 * qos列表赋值
	 * @throws Exception 
	 * 
	 */
	private void setQosQueueTableDatas(List<QosQueue> qosQueueList) throws Exception {
		qosQueuePanel.getPortQosTableModel().getDataVector().clear();
		qosQueuePanel.getPortQosTableModel().fireTableDataChanged();
		SiteService_MB siteService = null;
		try {
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			Object data[] = new Object[] {};
			int rowCount = 0;
			if (!qosQueueList.isEmpty()) {
				for (QosQueue qosQueue : qosQueueList) {
					if(siteService.getManufacturer(ConstantUtil.siteId) == EManufacturer.WUHAN.getValue()){
						data = new Object[] {ResourceUtil.srcStr(StringKeysTab.TAB_USER_PRIORITY_LEVEL)	+ ++rowCount, QosCosLevelEnum.from(qosQueue.getCos()), qosQueue.getCir(), 
								qosQueue.getWeight(), qosQueue.getQueueType(), qosQueue.getGreenLowThresh(), qosQueue.getGreenHighThresh(), 
								qosQueue.isWredEnable(), qosQueue.getMostBandwidth(),qosQueue.getDisCard() };
					}else{
						data = new Object[] { ++rowCount, QosCosLevelEnum.from(qosQueue.getCos()), qosQueue.getCir(), 
								qosQueue.getWeight(), qosQueue.getGreenLowThresh(), qosQueue.getGreenHighThresh(), 
								qosQueue.getGreenProbability(), qosQueue.getYellowLowThresh(), qosQueue.getYellowHighThresh(),
								qosQueue.getYellowProbability(), qosQueue.isWredEnable(), qosQueue.getMostBandwidth(),qosQueue.getDisCard() };
					}
					
					qosQueuePanel.getPortQosTableModel().addRow(data);
				}
			} else {
				qosQueuePanel.getPortQosTableModel().getDataVector().clear();
				qosQueuePanel.getPortQosTableModel().fireTableDataChanged();
				
				for (QosCosLevelEnum level : QosCosLevelEnum.values()) {
					if(siteService.getManufacturer(ConstantUtil.siteId) == EManufacturer.WUHAN.getValue()){
						data = new Object[] {ResourceUtil.srcStr(StringKeysTab.TAB_USER_PRIORITY_LEVEL)	+ ++rowCount, level.toString(), 0, 1, "SP", 50, 90, Boolean.FALSE, ResourceUtil.srcStr(StringKeysObj.QOS_UNLIMITED),50 };
					}else{
						data = new Object[] {++rowCount, level.toString(), 0, 16, 96, 128, 100, 64, 96, 100, Boolean.FALSE, ResourceUtil.srcStr(StringKeysObj.QOS_UNLIMITED) };
					}
					
					qosQueuePanel.getPortQosTableModel().addRow(data);
				}
			}
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(siteService);
		}
	}

	private PortQosQueueCommonConfig qosQueuePanel;
	private JScrollPane qosJScrollPane;
	private PortNniCXDialog portNniCXDialog; //晨晓nni界面
	private PortUniCXDialog portUniCXDialog;//晨晓uni界面
	private PortSfpCXDialog portSfpCXDialog;//晨晓sfp界面
	private ExpMappingPanel llspExpMappingPanel;
	private ExpMappingPanel elspExpMappingPanel;
	private ColorModelConfigPanel colorModelConfigPanel;
	private JScrollPane llspJScrollPane;
	private JScrollPane elspJScrollPane;
	private JScrollPane ethPirJscrollPane;//pri设置
	private PortPriDialog portPriDialog = null;
	
	
	
	@Override
	public void setTablePopupMenuFactory() {
		TTablePopupMenuFactory menuFactory = new TTablePopupMenuFactory() {
			@Override
			public JPopupMenu getPopupMenu(TTable ttable, MouseEvent evt) {
				if (SwingUtilities.isRightMouseButton(evt)) {
					SiteService_MB siteService = null;
					try {
						siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
						if(siteService.getManufacturer(ConstantUtil.siteId) == EManufacturer.WUHAN.getValue()){
							int count = ttable.getSelectedRows().length;
							if (count == 1) {
								JPopupMenu menu = new JPopupMenu();
								menu.add(portDiscardJMenuItem);
//								menu.add(priorityJMenuItem);
								menu.add(Port2LayerAttrJMenuItem);
								checkRoot(portDiscardJMenuItem, RootFactory.CORE_MANAGE);
//								checkRoot(priorityJMenuItem, RootFactory.CORE_MANAGE);
								checkRoot(Port2LayerAttrJMenuItem, RootFactory.CORE_MANAGE);
								menu.show(evt.getComponent(), evt.getX(), evt.getY());
								return menu;
							}
						}
					} catch (Exception e) {
						ExceptionManage.dispose(e, this.getClass());
					}finally{
						UiUtil.closeService_MB(siteService);
					}
				}
				return null;
			}
		};
		super.setMenuFactory(menuFactory);
	}

	@Override
	public void setController() {
		controller = new PortController(this);
	}

	@Override
	public Dimension setDefaultSize() {
		return new Dimension(160, ConstantUtil.INT_WIDTH_THREE);
	}

	@Override
	public List<JButton> setNeedRemoveButtons() {
		List<JButton> btList = new ArrayList<JButton>();
		btList.add(getAddButton());
		btList.add(getDeleteButton());
		btList.add(getSearchButton());
		return btList;
	}
	
	@Override
	public List<JButton> setAddButtons() {
		List<JButton> btList = new ArrayList<JButton>();
		btList.add(getConsistenceButton());
		return btList;
	}
	
	
	/**
	 * 映射模块初始化
	 */
	private void initEXPComponent() {
		try {
			llspExpMappingPanel = new ExpMappingPanel(this.getSelect(),UiUtil.getCodeByValue("EXPTYPE", "0").getId());
			elspExpMappingPanel = new ExpMappingPanel(this.getSelect(),UiUtil.getCodeByValue("EXPTYPE", "1").getId());
			llspJScrollPane = new JScrollPane(llspExpMappingPanel);
			elspJScrollPane = new JScrollPane(elspExpMappingPanel);
			expTabbedPane.removeAll();
			expTabbedPane.add(QosTemplateTypeEnum.LLSP.toString(), llspJScrollPane);
			expTabbedPane.add(QosTemplateTypeEnum.ELSP.toString(), elspJScrollPane);
			tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_COSTOEXPTEMPLATE), expTabbedPane);
		
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

}}
