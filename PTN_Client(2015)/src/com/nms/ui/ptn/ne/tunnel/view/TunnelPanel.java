/*
 * TunnelsPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package com.nms.ui.ptn.ne.tunnel.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import twaver.table.TTable;
import twaver.table.TTablePopupMenuFactory;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.enums.EManufacturer;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;
import com.nms.service.impl.base.DispatchBase;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.business.pw.PwQosQueuePanel;
import com.nms.ui.ptn.ne.tunnel.controller.TunnelNodeController;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
import com.nms.ui.ptn.systemconfig.dialog.qos.controller.QosConfigController;

/**
 * 
 * @author __USER__
 */
public class TunnelPanel extends ContentView<Tunnel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -94661767234858708L;
	/**
	 * 
	 */
	private JSplitPane splitPane;
	private JTabbedPane tabbedPane;
	private PwQosQueuePanel qosPanel;
	private LspTablePanel lspPanel;

	//private static TunnelPanel tunnelPanel;
	/** Creates new form LspPanl */
	public TunnelPanel() {
		super("tunnelNodeTable",RootFactory.CORE_MANAGE);
		init();
		getRefreshButton().doClick();
		//TunnelPanel.tunnelPanel = this;
	}
//
//	public static TunnelPanel getTunnelPanel() {
//		return tunnelPanel;
//	}

	public void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_TUNNEL_MANAGE)));
		initComponent();
		setLayout();
		this.setActionListention();
	}

	/**
	 * 添加监听
	 */
	private void setActionListention() {
		getTable().addElementClickedActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getSelect() == null) {
					// 清除详细面板数据
					qosPanel.clear();
					lspPanel.clear();
					return;
				} else {
					getController().initDetailInfo();
				}
			}
		});
		miUpdateQos.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Tunnel tunnel = null;
				QosConfigController qoscontroller = null;
				try {
					tunnel = getSelect();
					qoscontroller = new QosConfigController();
					qoscontroller.setNetwork(false);
					qoscontroller.openQosConfig(qoscontroller, "TUNNEL", tunnel, null,TunnelPanel.this);
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				} finally {
					tunnel = null;
					qoscontroller = null;
				}
			}

		});
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
		lspPanel = new LspTablePanel();
		miUpdateQos = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_QOS_UPDATE));
	}

	public void setTabbedPaneLayout() {
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_LSP_INFO), lspPanel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_QOS_INFO), qosPanel);
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
		getButtonPanel().add(this.getPrevPageBtn());
		getButtonPanel().add(this.getCurrPageLabel());
		getButtonPanel().add(this.getDivideLabel());
		getButtonPanel().add(this.getTotalPageLabel());
		getButtonPanel().add(this.getNextPageBtn());
	}

	@Override
	public void setController() {
		controller = new TunnelNodeController(this);
	}
	
	@Override
	public Dimension setDefaultSize() {
		return new Dimension(160, ConstantUtil.INT_WIDTH_THREE);
	}

	@Override
	public void setTablePopupMenuFactory() {
		TTablePopupMenuFactory menuFactory = new TTablePopupMenuFactory() {
			@Override
			public JPopupMenu getPopupMenu(TTable ttable, MouseEvent evt) {
				if (SwingUtilities.isRightMouseButton(evt)) {
					int count = ttable.getSelectedRows().length;
					if (count == 1) {
						JPopupMenu menu = new JPopupMenu();
						menu.add(miUpdateQos);
						checkRoot(miUpdateQos, RootFactory.CORE_MANAGE);
						menu.show(evt.getComponent(), evt.getX(), evt.getY());
						return menu;
					}
				}
				return null;
			}
		};
		super.setMenuFactory(menuFactory);
	}

	@Override
	public List<JButton> setNeedRemoveButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		needRemoveButtons.add(getExportButton());
		needRemoveButtons.add(getInportButton());
		needRemoveButtons.add(getFiterZero());
		return needRemoveButtons;
	}

	@Override
	public List<JButton> setAddButtons() {

		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		needRemoveButtons.add(this.getRotateButton());
		needRemoveButtons.add(this.getSynchroButton());
		int manufacturer=0;
		SiteService_MB siteServiceMB= null;
		SiteInst siteInst=null;
		try {
			siteServiceMB=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			siteInst=siteServiceMB.select(ConstantUtil.siteId);
			if(siteInst == null){
				throw new Exception("根据ID查询网元出错");
			}
			manufacturer=Integer.parseInt(UiUtil.getCodeById(Integer.parseInt(siteInst.getCellEditon())).getCodeValue());
		} catch (Exception e) {
			ExceptionManage.dispose(e,DispatchBase.class);
		}finally{
			UiUtil.closeService_MB(siteServiceMB);
		}
		if(manufacturer == EManufacturer.WUHAN.getValue()){
			needRemoveButtons.add(this.getConsistenceButton());
		}
		
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
	//	if(EManufacturer.CHENXIAO.getValue() == UiUtil.getManufacturer(ConstantUtil.siteId)){
		//	RotateNodeDialog rotateNodeDialog = new RotateNodeDialog(tunnel,this);
	//	}else{
			//List<ProtectRorateInfo> protectRorateInfoList = null;
		//	ProtectRorateInfoService protectRorateInfoService = null;//service 与数据库交互
		//	ProtectRorateInfo protectRorateInfo = null;//界面属性
			TunnelService_MB tunnelServiceMB = null;//将修改的Tunnel对象对数据库操作
			Tunnel protectTunnel = null;
			try {
				//protectRorateInfoService = (ProtectRorateInfoService) ConstantUtil.serviceFactory.newService(Services.PROTECTRORATE);
				tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
				
				//tunnel 界面传来的对象
				if(tunnel.getProtectTunnelId() > 0){
					protectTunnel = new Tunnel();
					protectTunnel.setTunnelId(tunnel.getProtectTunnelId());
					protectTunnel = tunnelServiceMB.select_nojoin(protectTunnel).get(0);
					
					//protectRorateInfo = new ProtectRorateInfo();
					//protectRorateInfo.setTunnelId(tunnel.getTunnelId());
					if(ConstantUtil.siteId == tunnel.getASiteId()){
						//protectRorateInfo.setSiteId(tunnel.getASiteId());
						//protectRorateInfo.setTunnelbusinessid(protectTunnel.getLspParticularList().get(0).getAtunnelbusinessid());
						
					}else{
						//protectRorateInfo.setSiteId(tunnel.getZSiteId());
						//protectRorateInfo.setTunnelbusinessid(protectTunnel.getLspParticularList().get(0).getZtunnelbusinessid());
						
					}
					//protectRorateInfoList = protectRorateInfoService.queryByProtectRorateInfo(protectRorateInfo);
					
					//if(protectRorateInfoList != null && protectRorateInfoList.size()>0){
						//protectRorateInfo = protectRorateInfoList.get(0);
					//}
					
					TunnelRoteDialog dialog = new TunnelRoteDialog(false,tunnel);//给界面复值。。。。
					
				//	UiUtil.showWindow(dialog, 450, 350);
					
					
				}else{
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_PROTECT_TUNNEL));
				}
				
			} catch (Exception e) {
				ExceptionManage.dispose(e,this.getClass());
			} finally {
				UiUtil.closeService_MB(tunnelServiceMB);
				tunnel = null;
				///protectRorateInfoService = null;
				//protectRorateInfoList = null;
			}
	//	}
		


	}

	
	public PwQosQueuePanel getQosPanel() {
		return qosPanel;
	}

	public LspTablePanel getLspPanel() {
		return lspPanel;
	}

	private JMenuItem miUpdateQos;
	

}