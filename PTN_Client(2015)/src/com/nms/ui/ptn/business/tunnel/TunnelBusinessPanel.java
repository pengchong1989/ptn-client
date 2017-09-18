﻿package com.nms.ui.ptn.business.tunnel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import twaver.table.TTable;
import twaver.table.TTablePopupMenuFactory;

import com.nms.db.bean.path.Segment;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.path.tunnel.Lsp;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.enums.EActiveStatus;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EServiceType;
import com.nms.model.path.SegmentService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.ResultString;
import com.nms.service.impl.util.SiteUtil;
import com.nms.service.impl.util.WhImplUtil;
import com.nms.ui.frame.ContentView;
import com.nms.ui.frame.ViewDataTable;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnMenuItem;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.business.pw.PwQosQueuePanel;
import com.nms.ui.ptn.business.testoam.TestOamBusinessController;
import com.nms.ui.ptn.business.topo.TopoPanel;
import com.nms.ui.ptn.ne.tunnel.view.TunnelRoteDialog;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
import com.nms.ui.ptn.systemconfig.dialog.qos.controller.QosConfigController;
import com.nms.ui.topology.Schematize;

/**
 * @author lepan
 */
public class TunnelBusinessPanel extends ContentView<Tunnel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8573672821821114967L;
	private JSplitPane splitPane;
	private JTabbedPane tabbedPane;
	private PwQosQueuePanel qosPanel;
	private LspNetworkTablePanel lspNetworkTablePanel;
	private JScrollPane oamScrollPane;
	private ViewDataTable<OamInfo> oamTable;
	private TopoPanel topoPanel;
//	private static TunnelBusinessPanel tunnelBusinessPanel;
	private Schematize schematize_panel = null;	//图形化显示界面
	private JMenuItem miUpdateTestOam;//右键修改按需oam
	private JMenuItem miDeleteTestOam;//右键删除按需oam
	private PtnMenuItem activateMenu;//激活
	private PtnMenuItem unActivateMenu;//去激活
	private JMenuItem creatTNP;
	private JMenuItem updateTNP;
	private JMenuItem deleteTNP;
	private JMenuItem apsEnableMenu;//aps使能
	private JMenuItem oamEnableMenu;//oam使能
	private JMenuItem cvEnableMenu;//cv使能
	private JMenuItem isReverseMenu;//是否返回
	private JMenuItem updateLabelMenu;//批量修改标签
	private JMenuItem addNodeMenu;//扩容
	private JMenuItem deleteNodeMenu;//缩容
	private TunnelBusinessPanel tunnelBusinessPanel;
	private OamMainInfoPanel oamMainInfoPanel;//OAM关键信息
	public TunnelBusinessPanel() {
		super("tunnelBusinessTable",RootFactory.CORE_MANAGE);
		tunnelBusinessPanel = this;
		init();
		//tunnelBusinessPanel = this;
	}
//
//	public static TunnelBusinessPanel getTunnelBusinessPanel() {
//		return tunnelBusinessPanel;
//	}

	public void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTab.TAB_TUNNEL)));
		initComponent();
		setLayout();
		setActionListention();
		try {
			getController().refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void setActionListention() {
		getTable().addElementClickedActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getSelect() == null) {
					//清除详细面板数据
					oamTable.clear();
					qosPanel.clear();
					oamMainInfoPanel.clear();
					topoPanel.clear();
					lspNetworkTablePanel.clear();
					schematize_panel.clear();
					return;
				} else {
					getController().initDetailInfo();
				}
			}
		});
		//		getTable().addElementDoubleClickedActionListener(new ActionListener() {
		//			
		//			@Override
		//			public void actionPerformed(ActionEvent arg0) {
		//				try {
		//					getController().openUpdateDialog();
		//				} catch (Exception e) {
		//					ExceptionManage.dispose(e,this.getClass());
		//				}
		//				
		//			}
		//		});

		miUpdateQos.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Tunnel tunnel = null;
				QosConfigController qoscontroller = null;
				try {
					tunnel = getSelect();
					qoscontroller = new QosConfigController();
					qoscontroller.openQosConfig(qoscontroller, "TUNNEL", tunnel, null,TunnelBusinessPanel.this);
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				} finally {
					tunnel = null;
					qoscontroller = null;
				}
			}

		});
		
		//新建或修改按需oam
		miUpdateTestOam.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Tunnel t = null;
				TestOamBusinessController oamcontroller = null;
				try {
					t = getSelect();
					if(t != null && t.getOamList().size() > 1){
						oamcontroller = new TestOamBusinessController();
						oamcontroller.openTestOamConfig(EServiceType.TUNNEL.toString(), t);
					}else{
						DialogBoxUtil.succeedDialog(TunnelBusinessPanel.this, ResourceUtil.srcStr(StringKeysTip.TIP_OAM_CONFIG));
						return;
					}
					
					controller.refresh();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				} finally {
					t = null;
					oamcontroller = null;
				}
			}

		});
		
		//删除按需oam
		miDeleteTestOam.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Tunnel t = null;
				TestOamBusinessController oamcontroller = null;
				try {
					t = getSelect();
					if(t != null){
						oamcontroller = new TestOamBusinessController();
						String message = oamcontroller.deleteTestOamConfig(EServiceType.TUNNEL.toString(), t);
						DialogBoxUtil.succeedDialog(TunnelBusinessPanel.this, message);
					}
					
					controller.refresh();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				} finally {
					t = null;
					oamcontroller = null;
				}
			}

		});
		
		//创建TNP
		creatTNP.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				TnpDialog tnpDialog = new TnpDialog(getSelect());
				UiUtil.showWindow(tnpDialog, 380, 400);
				try {
					controller.refresh();
				} catch (Exception e1) {
					ExceptionManage.dispose(e1,tunnelBusinessPanel.getClass());
				}
			}
		});
		
		//更新TNP
		updateTNP.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				TnpDialog tnpDialog = new TnpDialog(getSelect());
				UiUtil.showWindow(tnpDialog, 380, 400);
				try {
					controller.refresh();
				} catch (Exception e1) {
					ExceptionManage.dispose(e1,tunnelBusinessPanel.getClass());
				}
			}
		});
		
		//删除
		deleteTNP.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					DispatchUtil dispatchUtil = new DispatchUtil(RmiKeys.RMI_TUNNELPROTECT);
					boolean onlineFlag = false;
					List<Integer> allSiteIds =null;
					//判断是否有在线网元托管，存在不允许删除
					SiteUtil siteUtil = new SiteUtil();
					allSiteIds = new ArrayList<Integer>();
					int aSiteId = getSelect().getaSiteId();
					int zSiteId = getSelect().getzSiteId();
					if(1==siteUtil.SiteTypeOnlineUtil(aSiteId)){
						allSiteIds.add(aSiteId);
					}
					if(1==siteUtil.SiteTypeOnlineUtil(zSiteId)){
						allSiteIds.add(zSiteId);
					}									
					if(allSiteIds !=null && allSiteIds.size()!=0){
					   onlineFlag = true;
					}		
					if(onlineFlag){
						WhImplUtil wu = new WhImplUtil();
						String str=wu.getNeNames(allSiteIds);
						DialogBoxUtil.errorDialog(tunnelBusinessPanel, ResourceUtil.srcStr(StringKeysTip.TIP_NOT_DELETEONLINE)+""+str+ResourceUtil.srcStr(StringKeysTip.TIP_ONLINENOT_DELETEONLINE));
						return;
					}
				    String result=dispatchUtil.excuteDelete(getSelect());
				    Tunnel tunnelBefore = getSelect();
				    Tunnel tunnelAfter1 = this.getTunnelAfter(tunnelBefore.getTunnelId());
				    Tunnel tunnelAfter2 = this.getTunnelAfter(tunnelBefore.getProtectTunnelId());
				    TnpDialog tnpDialog = new TnpDialog(null);
				    tunnelBefore.getProtectTunnel().setTunnelType(tunnelBefore.getTunnelType());
				    tnpDialog.insertLog(new PtnButton(null), EOperationLogType.TNPDELETE1.getValue(), result, tunnelBefore.getProtectTunnel(), tunnelAfter2);
				    tunnelBefore.setProtectTunnel(null);
				    tnpDialog.insertLog(new PtnButton(null), EOperationLogType.TNPDELETE1.getValue(), result, tunnelBefore, tunnelAfter1);
					controller.refresh();
				} catch (Exception e2) {
					ExceptionManage.dispose(e2,tunnelBusinessPanel.getClass());
				}
			}
			
			private Tunnel getTunnelAfter(int tunnelId) {
				TunnelService_MB service = null;
				try {
					service = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
					return service.selectId(tunnelId);
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				} finally {
					UiUtil.closeService_MB(service);
				}
				return null;
			}
		});
		//激活按钮
		activateMenu.addActionListener(new MyActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					operateActive(getAllSelect(), EActiveStatus.ACTIVITY.getValue(), EOperationLogType.TUNNELACTIVE.getValue());
				} catch (Exception e1) {
					ExceptionManage.dispose(e1, tunnelBusinessPanel.getClass());
				}
			}
			
			@Override
			public boolean checking() {
				return true;
			}
		});
		//去激活按钮
		unActivateMenu.addActionListener(new MyActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					operateActive(getAllSelect(), EActiveStatus.UNACTIVITY.getValue(), EOperationLogType.TUNNELUNACTIVE.getValue());
				} catch (Exception e1) {
					ExceptionManage.dispose(e1, tunnelBusinessPanel.getClass());
				}
			}
			
			@Override
			public boolean checking() {
				return true;
			}
		});
		//aps使能
		this.apsEnableMenu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new EnableSwitchDialog(getAllSelect(), "aps");
				try {
					controller.refresh();
				} catch (Exception e) {
					ExceptionManage.dispose(e, tunnelBusinessPanel.getClass());
				}
			}
		});
		//oam使能
		this.oamEnableMenu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new EnableSwitchDialog(getAllSelect(), "oam");
				try {
					controller.refresh();
				} catch (Exception e) {
					ExceptionManage.dispose(e, tunnelBusinessPanel.getClass());
				}
			}
		});
		//cv使能
		this.cvEnableMenu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new EnableSwitchDialog(getAllSelect(), "cv");
				try {
					controller.refresh();
				} catch (Exception e) {
					ExceptionManage.dispose(e, tunnelBusinessPanel.getClass());
				}
			}
		});
		//是否返回
		this.isReverseMenu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new EnableSwitchDialog(getAllSelect(), "isReverse");
				try {
					controller.refresh();
				} catch (Exception e) {
					ExceptionManage.dispose(e, tunnelBusinessPanel.getClass());
				}
			}
		});
		//批量修改标签
		this.updateLabelMenu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new UpdateBatchLabelDialog(getAllSelect());
				try {
					controller.refresh();
				} catch (Exception e) {
					ExceptionManage.dispose(e, tunnelBusinessPanel.getClass());
				}
			}
		});
		//扩容
		this.addNodeMenu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Tunnel tunnel = getSelect();
				if(tunnel.getTunnelStatus() == EActiveStatus.ACTIVITY.getValue()){
					//任何路径都可以扩容
					new AddLspNodeDialog(tunnel);
					
					//如果是工作路径和保护路径成环才可以扩容
//					List<Lsp> mainLspList = tunnel.getLspParticularList();
//					List<Lsp> reserveLspList = tunnel.getProtectTunnel().getLspParticularList();
//					if(mainLspList.size() != reserveLspList.size()){
//						new AddLspNodeDialog(tunnel);
//					}else{
//						boolean flag = false;
//						for (int i = 0; i < mainLspList.size(); i++) {
//							Lsp mainLsp = mainLspList.get(i);
//							Lsp reserveLsp = reserveLspList.get(i);
////							if(mainLsp.getASiteId() != reserveLsp.getASiteId() || 
////									mainLsp.getZSiteId() != reserveLsp.getZSiteId()){
////								flag = true;
////								break;
////							}
//							if(mainLsp.getSegmentId() != reserveLsp.getSegmentId()){
//								flag = true;
//								break;
//							}
//						}
//						if(flag){
//							new AddLspNodeDialog(tunnel);
//						}else{
//							DialogBoxUtil.errorDialog(tunnelBusinessPanel, ResourceUtil.srcStr(StringKeysTip.TIP_TUNNEL_CAN_NOT_ADD_NODE));
//						}
//					}
					try {
						controller.refresh();
					} catch (Exception e) {
						ExceptionManage.dispose(e, tunnelBusinessPanel.getClass());
					}
				}else{
					DialogBoxUtil.errorDialog(tunnelBusinessPanel, ResourceUtil.srcStr(StringKeysTip.TIP_TUNNEL_CAN_NOT_ADD_NODE));
				}
			}
		});
		//缩容
		this.deleteNodeMenu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Tunnel tunnel = getSelect();
				if(tunnel.getTunnelStatus() == EActiveStatus.ACTIVITY.getValue()){
					boolean flag = false;
					//只要是中间网元并且只连接两个网元才可以进行此操作
					List<Lsp> mainLspList = tunnel.getLspParticularList();
					if(mainLspList.size() > 1){
						//lsp条目超过一条，说明有中间网元
						int aSiteId = tunnel.getASiteId();
						int zSiteId = tunnel.getZSiteId();
						List<Integer> xcSiteList = new ArrayList<Integer>();
						for (Lsp lsp : mainLspList) {
							if(lsp.getASiteId() != aSiteId && lsp.getASiteId() != zSiteId && 
									!xcSiteList.contains(lsp.getASiteId())){
								xcSiteList.add(lsp.getASiteId());
							}
							if(lsp.getZSiteId() != aSiteId && lsp.getZSiteId() != zSiteId &&
									!xcSiteList.contains(lsp.getZSiteId())){
								xcSiteList.add(lsp.getZSiteId());
							}
						}
						SegmentService_MB segmentService = null;
						try {
							segmentService = (SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
							for (int siteId : xcSiteList) {
								List<Segment> segmentList = segmentService.queryBySiteId(siteId);
								if(segmentList!= null && segmentList.size() == 2){
									flag = true;
									break;
								}
							}
						} catch (Exception e) {
							ExceptionManage.dispose(e, this.getClass());
						} finally {
							UiUtil.closeService_MB(segmentService);
						}
					}else{
						if(tunnel.getProtectTunnelId() > 0){
							List<Lsp> reserveLspList = tunnel.getProtectTunnel().getLspParticularList();
							if(reserveLspList.size() > 1){
								//lsp条目超过一条，说明有中间网元
								int aSiteId = tunnel.getProtectTunnel().getASiteId();
								int zSiteId = tunnel.getProtectTunnel().getZSiteId();
								List<Integer> xcSiteList = new ArrayList<Integer>();
								for (Lsp lsp : reserveLspList) {
									if(lsp.getASiteId() != aSiteId && lsp.getASiteId() != zSiteId && 
											!xcSiteList.contains(lsp.getASiteId())){
										xcSiteList.add(lsp.getASiteId());
									}
									if(lsp.getZSiteId() != aSiteId && lsp.getZSiteId() != zSiteId &&
											!xcSiteList.contains(lsp.getZSiteId())){
										xcSiteList.add(lsp.getZSiteId());
									}
								}
								SegmentService_MB segmentService = null;
								try {
									segmentService = (SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
									for (int siteId : xcSiteList) {
										List<Segment> segmentList = segmentService.queryBySiteId(siteId);
										if(segmentList!= null && segmentList.size() == 2){
											flag = true;
											break;
										}
									}
								} catch (Exception e) {
									ExceptionManage.dispose(e, this.getClass());
								} finally {
									UiUtil.closeService_MB(segmentService);
								}
							}
						}
					}
					if(flag){
						new DeleteLspNodeDialog(tunnel);
					}else{
						DialogBoxUtil.errorDialog(tunnelBusinessPanel, ResourceUtil.srcStr(StringKeysTip.TIP_TUNNEL_CAN_NOT_DELETE_NODE));
					}
					
					//如果是工作路径和保护路径成环才可以缩容
//					List<Lsp> mainLspList = tunnel.getLspParticularList();
//					List<Lsp> reserveLspList = tunnel.getProtectTunnel().getLspParticularList();
//					if(mainLspList.size() != reserveLspList.size()){
//						new DeleteLspNodeDialog(tunnel);
//					}else{
//						boolean flag = false;
//						for (int i = 0; i < mainLspList.size(); i++) {
//							Lsp mainLsp = mainLspList.get(i);
//							Lsp reserveLsp = reserveLspList.get(i);
//							if(mainLsp.getASiteId() != reserveLsp.getASiteId() || 
//									mainLsp.getZSiteId() != reserveLsp.getZSiteId()){
//								flag = true;
//								break;
//							}
//						}
//						if(flag){
//							new DeleteLspNodeDialog(tunnel);
//						}else{
//							DialogBoxUtil.errorDialog(tunnelBusinessPanel, ResourceUtil.srcStr(StringKeysTip.TIP_TUNNEL_CAN_NOT_DELETE_NODE));
//						}
//					}
					try {
						controller.refresh();
					} catch (Exception e) {
						ExceptionManage.dispose(e, tunnelBusinessPanel.getClass());
					}
				}else{
					DialogBoxUtil.errorDialog(tunnelBusinessPanel, ResourceUtil.srcStr(StringKeysTip.TIP_TUNNEL_CAN_NOT_DELETE_NODE));
				}
			}
		});
		
	}
	
	/**
	 * 激活/去激活
	 * @throws Exception 
	 * @throws RemoteException 
	 */
	private void operateActive(List<Tunnel> tunnelList, int statusValue, int logValue) throws RemoteException, Exception {
		int failCount = 0;
		String result = null;
		if(tunnelList != null && !tunnelList.isEmpty()){
			DispatchUtil dispatchUtil = new DispatchUtil(RmiKeys.RMI_TUNNEL);
			for (Tunnel tunnel : tunnelList) {
				tunnel.setTunnelStatus(statusValue);
				result = dispatchUtil.excuteUpdate(tunnel);
				if(result == null || !result.contains(ResultString.CONFIG_SUCCESS)){
					failCount++;
				}
				//添加日志记录*************************/
				AddOperateLog.insertOperLog(null, logValue, result, null, null, -1, tunnel.getTunnelName(), null);
				//************************************/
			}
			if(failCount == tunnelList.size() && failCount>1){
				result = ResultString.CONFIG_FAILED;
			}
			result = ResourceUtil.srcStr(StringKeysTip.TIP_BATCH_CREATE_RESULT);
			result = result.replace("{C}", (tunnelList.size()-failCount) + "");
			result = result.replace("{S}", failCount + "");
		}
		String offLineStr = this.getNotOnlineSiteIdNames(tunnelList);
		if(!offLineStr.equals("")){
			result += ","+offLineStr+ResultString.NOT_ONLINE_SUCCESS;
		}
		DialogBoxUtil.succeedDialog(this, result);
		this.controller.refresh();
	}

	private String getNotOnlineSiteIdNames(List<Tunnel> tunnelList) throws Exception {
		List<Integer> siteIds = null;
		TunnelService_MB tunnelService = null;
		Tunnel protectTunnel = null;
		String str = "";
		try {
			siteIds = new ArrayList<Integer>();
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			for (Tunnel tunnel : tunnelList) {
				if (tunnel.getProtectTunnelId() > 0) {
					protectTunnel = new Tunnel();
					protectTunnel.setTunnelId(tunnel.getProtectTunnelId());
					protectTunnel = tunnelService.select_nojoin(protectTunnel).get(0);
					for (Lsp lspParticular : protectTunnel.getLspParticularList()) {
						if (lspParticular.getASiteId() > 0) {
							if (!siteIds.contains(lspParticular.getASiteId())) {
								siteIds.add(lspParticular.getASiteId());
							}
						}
						if (lspParticular.getZSiteId() > 0) {
							if (!siteIds.contains(lspParticular.getZSiteId()) && lspParticular.getZPortId() > 0) {
								siteIds.add(lspParticular.getZSiteId());
							}
						}
					}

				}

				for (Lsp lspParticular : tunnel.getLspParticularList()) {
					if (lspParticular.getASiteId() > 0) {
						if (!siteIds.contains(lspParticular.getASiteId())) {
							siteIds.add(lspParticular.getASiteId());
						}
					}
					if (lspParticular.getZSiteId() > 0) {
						if (!siteIds.contains(lspParticular.getZSiteId()) && lspParticular.getZPortId() > 0) {
							siteIds.add(lspParticular.getZSiteId());
						}
					}
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(tunnelService);
		}
		WhImplUtil whImplUtil = new WhImplUtil();
		str = whImplUtil.getNeNames(siteIds);
		return str;
	}
	
	private void initComponent() {
		tabbedPane = new JTabbedPane();
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		int high = Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue() / 2;
		splitPane.setDividerLocation(high - 65);
		splitPane.setTopComponent(this.getContentPanel());
		splitPane.setBottomComponent(tabbedPane);
		qosPanel = new PwQosQueuePanel();
		lspNetworkTablePanel=new LspNetworkTablePanel();
		oamTable = new ViewDataTable<OamInfo>("pwBusinessOAMTable");
		oamTable.getTableHeader().setResizingAllowed(true);
		oamTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		oamTable.setTableHeaderPopupMenuFactory(null);
		oamTable.setTableBodyPopupMenuFactory(null);
		oamScrollPane = new JScrollPane();
		oamScrollPane.setViewportView(oamTable);
		oamScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		oamScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		topoPanel = new TopoPanel();
		miUpdateQos = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_QOS_UPDATE));
		schematize_panel=new Schematize();
		miUpdateTestOam = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_TEST_OAM_CONFIG));
		miDeleteTestOam = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_TEST_OAM_DELETE));
		creatTNP = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_CREATE_TNP));
		updateTNP = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_UPDATE_TNP));
		deleteTNP = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_DELETE_TNP));
		activateMenu = new PtnMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_ACTIVATION), true);
		unActivateMenu = new PtnMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_GO_ACTIVATION), true);
		this.apsEnableMenu = new JMenuItem(ResourceUtil.srcStr(StringKeysLbl.LBL_APS_ENABLE));
		this.oamEnableMenu = new JMenuItem(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_ENABLE));
		this.cvEnableMenu = new JMenuItem(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_CONNECT_TEST));
		this.isReverseMenu = new JMenuItem(ResourceUtil.srcStr(StringKeysLbl.LBL_BACK));
		this.updateLabelMenu = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_BATCH_UPDATE_LABEL));
		this.addNodeMenu = new JMenuItem(ResourceUtil.srcStr(StringKeysTitle.TIT_ADD_LSP_NODE));
		this.deleteNodeMenu = new JMenuItem(ResourceUtil.srcStr(StringKeysTitle.TIT_DELETE_LSP_NODE));
		oamMainInfoPanel = new OamMainInfoPanel();
	}

	public void setTabbedPaneLayout() {
		tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_TOPO_SHOW), topoPanel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_ROUTE_INFO), lspNetworkTablePanel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_QOS_INFO), qosPanel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_OAM_INFO), oamScrollPane);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_OAM_MAIN_INFO), oamMainInfoPanel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_SCHEMATIZE), this.schematize_panel);
	}

	public void setLayout() {
		setTabbedPaneLayout();
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
//		getButtonPanel().add(this.getPrevPageBtn());
//		getButtonPanel().add(this.getCurrPageLabel());
//		getButtonPanel().add(this.getDivideLabel());
//		getButtonPanel().add(this.getTotalPageLabel());
//		getButtonPanel().add(this.getNextPageBtn());
	}

	@Override
	public void setTablePopupMenuFactory() {
		TTablePopupMenuFactory menuFactory = new TTablePopupMenuFactory() {
			@Override
			public JPopupMenu getPopupMenu(TTable ttable, MouseEvent evt) {
				if (SwingUtilities.isRightMouseButton(evt)) {
					Tunnel tunnel = getSelect();
					int count = ttable.getSelectedRows().length;
					JPopupMenu menu = new JPopupMenu();
					if(count > 0){
						menu.add(activateMenu);
						menu.add(unActivateMenu);
						menu.add(apsEnableMenu);
						menu.add(oamEnableMenu);
						menu.add(cvEnableMenu);
						if(checkTunnelType(getAllSelect())){
							menu.add(isReverseMenu);
							checkRoot(isReverseMenu, RootFactory.CORE_MANAGE);
						}
						checkRoot(activateMenu, RootFactory.CORE_MANAGE);
						checkRoot(unActivateMenu, RootFactory.CORE_MANAGE);
						checkRoot(apsEnableMenu, RootFactory.CORE_MANAGE);
						checkRoot(oamEnableMenu, RootFactory.CORE_MANAGE);
						checkRoot(cvEnableMenu, RootFactory.CORE_MANAGE);
						if (count == 1) {
							menu.add(miUpdateQos);
							menu.add(miUpdateTestOam);
							menu.add(miDeleteTestOam);
							menu.add(addNodeMenu);
							menu.add(deleteNodeMenu);
							checkRoot(miUpdateTestOam, RootFactory.CORE_MANAGE);
							checkRoot(miDeleteTestOam, RootFactory.CORE_MANAGE);
							checkRoot(miUpdateQos, RootFactory.CORE_MANAGE);
							checkRoot(addNodeMenu, RootFactory.CORE_MANAGE);
							checkRoot(deleteNodeMenu, RootFactory.CORE_MANAGE);
							if(tunnel.getProtectTunnel() == null){
								menu.add(creatTNP);
								checkRoot(creatTNP, RootFactory.CORE_MANAGE);
							}else{
								menu.add(updateTNP);
								menu.add(deleteTNP);
								checkRoot(updateTNP, RootFactory.CORE_MANAGE);
								checkRoot(deleteTNP, RootFactory.CORE_MANAGE);
							}
							
						}
						if(count > 1){
							menu.add(updateLabelMenu);
							checkRoot(updateLabelMenu, RootFactory.CORE_MANAGE);
						}
						menu.show(evt.getComponent(), evt.getX(), evt.getY());
						return menu;
					}
				}
				return null;
			}
		};
		super.setMenuFactory(menuFactory);
	}

	/**
	 * 如果全部都是1:1，返回true，否则返回false
	 */
	private boolean checkTunnelType(List<Tunnel> allSelect) {
		for (Tunnel tunnel : allSelect) {
			if(tunnel.getProtectTunnel() == null){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public List<JButton> setNeedRemoveButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		needRemoveButtons.add(getSynchroButton());
		needRemoveButtons.add(getExportButton());
		needRemoveButtons.add(getInportButton());
		needRemoveButtons.add(getFiterZero());
		return needRemoveButtons;
	}

	@Override
	public List<JButton> setAddButtons() {

		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		needRemoveButtons.add(this.getRotateButton());
		needRemoveButtons.add(this.getSearchButton());
		return needRemoveButtons;

	}
	
	/**
	 * 设置倒换按钮
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private JButton getRotateButton() {
		JButton jButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_ROTATE),RootFactory.CORE_MANAGE);

		// 新建按钮事件
		jButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					rotateButtonListener();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		});

		return jButton;
	}

	private void rotateButtonListener() throws NumberFormatException, Exception {

		if (this.getAllSelect().size() != 1) {
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
			return;
		}

		Tunnel tunnel = this.getSelect();

		if (!"2".equals(UiUtil.getCodeById(Integer.parseInt(tunnel.getTunnelType())).getCodeValue())) {
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_1TO1));
			return;
		}
		
		new TunnelRoteDialog(true, tunnel);
	}
	
	@Override
	public void setController() {
		controller = new TunnelBusinessController(this);
	}

	public PwQosQueuePanel getQosPanel() {
		return qosPanel;
	}

	public ViewDataTable<OamInfo> getOamTable() {
		return oamTable;
	}

	public TopoPanel getTopoPanel() {
		return topoPanel;
	}

	public void setTopoPanel(TopoPanel topoPanel) {
		this.topoPanel = topoPanel;
	}
	
	public LspNetworkTablePanel getLspNetworkTablePanel() {
		return lspNetworkTablePanel;
	}

	public void setLspNetworkTablePanel(LspNetworkTablePanel lspNetworkTablePanel) {
		this.lspNetworkTablePanel = lspNetworkTablePanel;
	}
	
	public Schematize getSchematize_panel() {
		return schematize_panel;
	}

	public OamMainInfoPanel getOamMainInfoPanel() {
		return oamMainInfoPanel;
	}



	private JMenuItem miUpdateQos;
}
