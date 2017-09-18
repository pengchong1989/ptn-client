package com.nms.ui.topology.routebusiness.view;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import twaver.network.TNetwork;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.RouteBusiness;
import com.nms.db.bean.ptn.path.ces.CesInfo;
import com.nms.db.bean.ptn.path.eth.ElanInfo;
import com.nms.db.bean.ptn.path.eth.ElineInfo;
import com.nms.db.bean.ptn.path.eth.EtreeInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.enums.EServiceType;
import com.nms.model.ptn.path.ces.CesInfoService_MB;
import com.nms.model.ptn.path.eth.ElanInfoService_MB;
import com.nms.model.ptn.path.eth.ElineInfoService_MB;
import com.nms.model.ptn.path.eth.EtreeInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.business.topo.TopoPanel;
import com.nms.ui.topology.Schematize;

/**
 * 查询路由业务窗口
 * @author dzy
 *
 */
public class RouteBusinessDialog extends PtnDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8598606292116359113L;
	private SiteInst siteInst; //搜索网元
	private TNetwork network = null;
	/**
	 * 通过网元创建实例
	 * @param siteInst
	 */
	public RouteBusinessDialog(SiteInst siteInst,String type) {
		try {
			this.siteInst = siteInst;
			this.initComponent(type);
			this.addListener();
			UiUtil.showWindow(this, 900, 580);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}
	
	/**
	 * 添加监听事件
	 */
	private void addListener() {
		//添加table点击行事件
		this.routeBusinessTablePanel.getTable().addElementClickedActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshTopo();
			}
		});
	}
	
	/**
	 * 刷新拓扑方法
	 */
	private void refreshTopo() {
		if (this.routeBusinessTablePanel.getTable().getSelect() == null) {
			// 清除详细面板数据
			this.network.removeAll();
			schematize_panel.clear();
			return;
		} else {
			try {
				schematize_panel.clear();
				showTopoByTunnel();
			} catch (Exception e) {
				ExceptionManage.dispose(e,this.getClass());
			}
		}
	}

	/**
	 * 初始化控件
	 */
	private void initComponent(String type) {
		if("XC".equals(type)){
			this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_XCROUTEBUSINESS));
		}else{
			this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_HOMEROUTEBUSINESS));
		}
		tabbedPane = new JTabbedPane();
		topoPanel = new TopoPanel();
		jSplitPane = new JSplitPane();
		schematize_panel = new Schematize();
		tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_TOPO_SHOW),topoPanel); //拓扑面板
		tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_SCHEMATIZE), this.schematize_panel); //图形化视图
		routeBusinessTablePanel = new RouteBusinessTablePanel(this.siteInst,type); //路由业务信息列表
		this.jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		this.jSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		this.jSplitPane.setOneTouchExpandable(true);
		int high = Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue() / 2;
		this.jSplitPane.setDividerLocation(high-100);
		this.jSplitPane.setTopComponent(routeBusinessTablePanel);
		this.jSplitPane.setBottomComponent(tabbedPane);
		
		//TunnelTopology：：：（TunnelTopoPanel）单例模式，只有这里用到了：   去掉似乎没有影响功能
		//TunnelTopology.getTopology().removePopMenu();
		this.add(this.jSplitPane);
	}

	/** 
	 * 显示拓扑和图形化显示
	 * @throws Exception
	 */
	private void showTopoByTunnel() throws Exception {
		PwInfo pwInfo;
		Tunnel tunnel;
		ElineInfo elineInfo;
		List<Tunnel> tunnelList;
		List<ElineInfo> elineList;
		List<ElanInfo> elanInfoList;
		List<EtreeInfo> etreeInfoList;
		CesInfo cesInfo;
		RouteBusiness routeBusiness;
		PwInfoService_MB pwInfoService = null;
		TunnelService_MB tunnelService = null;
		ElineInfoService_MB elineService = null;
		ElanInfoService_MB elanInfoService= null;
		EtreeInfoService_MB etreeService = null;
		CesInfoService_MB cesInfoService = null;
		try {
			pwInfoService= (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			tunnelService= (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			elineService= (ElineInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Eline);
			elanInfoService= (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
			etreeService= (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);
			cesInfoService= (CesInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CesInfo);
			routeBusiness = this.routeBusinessTablePanel.getTable().getSelect();
			if(EServiceType.TUNNEL.getValue()==routeBusiness.getType()){//TUNNEL
				tunnel = new Tunnel();
				tunnel.setTunnelId(routeBusiness.getBusiId());
				tunnelList = tunnelService.select(tunnel);
				if(null!=tunnelList&&tunnelList.size()>0){
					topoPanel.initData(tunnelList.get(0));
					schematize_panel.initData(tunnelList.get(0));
				}
			}else if(EServiceType.PW.getValue()==routeBusiness.getType()){ //PW
				pwInfo = new PwInfo();
				pwInfo.setPwId(routeBusiness.getBusiId());
				pwInfo = pwInfoService.selectBypwid_notjoin(pwInfo);
				if(null!=pwInfo){
					topoPanel.initData(pwInfo);
					schematize_panel.initData(pwInfo);
				}
			}else if(EServiceType.ELINE.getValue()==routeBusiness.getType()){//ELINE
				elineInfo = new ElineInfo();
				elineInfo.setId(routeBusiness.getBusiId());
				elineList = elineService.selectByCondition(elineInfo);
				if(null!=elineList&&elineList.size()>0){
					topoPanel.initData(elineList.get(0));
					schematize_panel.initData(elineList.get(0));
				}
			}else if(EServiceType.ELAN.getValue()==routeBusiness.getType()){//ELAN
				elanInfoList = elanInfoService.selectByServiceId(routeBusiness.getBusiId());
				if(null!=elanInfoList&&elanInfoList.size()>0){
					topoPanel.initData(elanInfoList.get(0));
					schematize_panel.initData(elanInfoList.get(0));
				}
			}else if(EServiceType.ETREE.getValue()==routeBusiness.getType()){//ETREE
				etreeInfoList = etreeService.selectByServiceId(routeBusiness.getBusiId());
				if(null!=etreeInfoList&&etreeInfoList.size()>0){
					topoPanel.initData(etreeInfoList.get(0));
					schematize_panel.initData(etreeInfoList.get(0));
				}
			}else if(EServiceType.CES.getValue()==routeBusiness.getType()){//CES
				cesInfo = new CesInfo();
				cesInfo.setId(routeBusiness.getBusiId());
				cesInfo = cesInfoService.selectByid(cesInfo);
				if(null!=cesInfo){
					topoPanel.initData(cesInfo);
					schematize_panel.initData(cesInfo);
				}
			}
			//TunnelTopoPanel单例模式，只有这里用到了：   去掉似乎没有影响功能
		//	network = TunnelTopoPanel.getTunnelTopoPanel().getNetWork();
		//	network.doLayout(TWaverConst.LAYOUT_CIRCULAR);
		} catch (Exception e) {
			throw e;
		}finally{
			network=null;
			UiUtil.closeService_MB(pwInfoService);
			UiUtil.closeService_MB(tunnelService);
			UiUtil.closeService_MB(elineService);
			UiUtil.closeService_MB(etreeService);
			UiUtil.closeService_MB(elanInfoService);
			UiUtil.closeService_MB(cesInfoService);
			routeBusiness=null;
			elineInfo=null;
			tunnelList=null;
			elineList=null;
			elanInfoList=null;
			etreeInfoList=null;
			cesInfo=null;
		}
	}
	
	private RouteBusinessTablePanel routeBusinessTablePanel; //基本信息列表
	private JSplitPane jSplitPane;	//切分面板
	private TopoPanel topoPanel;	//拓扑面板
	private JTabbedPane tabbedPane;  //标签面板
	private Schematize schematize_panel = null; // 图形化显示界面

}
