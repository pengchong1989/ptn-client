package com.nms.ui.ptn.business.dialog.tunnel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import twaver.Element;
import twaver.Link;
import twaver.Node;
import twaver.TDataBox;
import twaver.TUIManager;
import twaver.TWaverConst;
import twaver.network.TNetwork;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.path.protect.DualProtect;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Lsp;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.oam.OamInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.ptn.port.DualProtectService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ListingFilter;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.TopoAttachment;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.util.TopologyUtil;

public class TunnelTopoPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4910624294536104233L;
	private final TDataBox box = new TDataBox();
	private final TNetwork network = new TNetwork(box);
	//private static TunnelTopoPanel tunnelTopoPanel;

	/**
	 * 初始化网元提示标题
	 */
	 {
		TUIManager.registerAttachment("topoTitle", TopoAttachment.class);
	}

	public TunnelTopoPanel() {
		super(new BorderLayout());
		try {
			//this.tunnelTopoPanel = this;
			init();
			this.add(network);
			this.setVisible(true);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * pw编辑页面 通过pw类型初始化拓扑
	 * @param pwType  pw类型
	 */
	public TunnelTopoPanel(JComboBox pwType) {
		super(new BorderLayout());
		try {
			init(pwType);
		//	this.tunnelTopoPanel = this;
			network.setEnsureVisibleOnSelected(true);
			this.add(network);
			this.setVisible(true);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	private void init() throws Exception {
		TunnelService_MB tunnelServiceMB = null;
		List<Tunnel> tunnelList = null;
		Node node_a = null;
		Node node_z = null;
		ListingFilter filter=null;
		Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
//		DualProtectService dualProtectService = null;
		SiteService_MB siteServiceMB = null;
		SiteInst siteInst;
//		OamInfoService oamInfoService = null;
		try {
			box.clear();
			filter=new ListingFilter();
//			dualProtectService = (DualProtectService) ConstantUtil.serviceFactory.newService(Services.DUALPROTECTSERVICE);
			tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			siteServiceMB = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			tunnelList = (List<Tunnel>) filter.filterList(tunnelServiceMB.selectAll());
//			oamInfoService = (OamInfoService) ConstantUtil.serviceFactory.newService(Services.OamInfo);
			
			//如果tunnel不为空 、通过tunnel创建tunnel和网元的拓扑
			if (tunnelList != null && tunnelList.size() > 0) {
				for (Tunnel tunnel:tunnelList) {
					if (nodeMap.get(tunnel.getASiteId()) == null) {
						siteInst = siteServiceMB.select(tunnel.getASiteId());
						node_a = this.createNode(tunnel.getASiteId(), siteInst.getSiteX(),  siteInst.getSiteY());

						nodeMap.put(tunnel.getASiteId(), node_a);
						box.addElement(node_a);
					}
					if (nodeMap.get(tunnel.getZSiteId()) == null) {
						siteInst = siteServiceMB.select(tunnel.getZSiteId());
						node_z = this.createNode(tunnel.getZSiteId(), siteInst.getSiteX(),  siteInst.getSiteY());
						
						nodeMap.put(tunnel.getZSiteId(), node_z);
						box.addElement(node_z);
					}
					node_a = nodeMap.get(tunnel.getASiteId());
					node_z = nodeMap.get(tunnel.getZSiteId());
					box.addElement(this.createLink(node_a, node_z, tunnel, Color.green));
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
//			UiUtil.closeService(oamInfoService);
//			UiUtil.closeService(dualProtectService);
			UiUtil.closeService_MB(siteServiceMB);
			UiUtil.closeService_MB(tunnelServiceMB);
		}
	}
	
	/**
	 * 初始化网元和tunnel数据
	 * @param pwType	pw类型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void init(JComboBox pwType) throws Exception {
		TunnelService_MB tunnelServiceMB = null;
		List<Tunnel> tunnelList = null;
		Node node_a = null;
		Node node_z = null;
		ListingFilter filter=null;
		Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
		List<DualProtect> dualProtectList = null;
		DualProtectService_MB dualProtectServiceMB = null;
		SiteService_MB siteServiceMB = null;
		SiteInst siteInst;
		List<Tunnel> dualTunnel;
		OamInfoService_MB oamInfoServiceMB = null;
		try {
			box.clear();
			dualTunnel = new ArrayList<Tunnel>();
			filter=new ListingFilter();
			dualProtectServiceMB = (DualProtectService_MB) ConstantUtil.serviceFactory.newService_MB(Services.DUALPROTECTSERVICE);
			tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			siteServiceMB = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			tunnelList = (List<Tunnel>) filter.filterList(tunnelServiceMB.selectAllNotProtect());
			oamInfoServiceMB = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			//过滤非eth条件下 的tunnel  ，因为双规保护占用的tunnel只能用于eth时的pw
			if(!"ETH".equals(pwType.getSelectedItem().toString())){
				dualProtectList = dualProtectServiceMB.selectBySite(ConstantUtil.siteId);
				for(DualProtect dualProtect:dualProtectList){
					dualTunnel.add(dualProtect.getBreakoverTunnel());
					if(null!=dualProtect.getRelevanceTunnelList()){
						for(Tunnel tunnel:dualProtect.getRelevanceTunnelList()){
							dualTunnel.add(tunnel);
						}
					}
				}
			}
			//过滤掉非eth 双规保护应用的 tunnel
			for (Tunnel tunnel : dualTunnel) {
				tunnelList.remove(tunnel);
			}
			//如果tunnel不为空 、通过tunnel创建tunnel和网元的拓扑
			if (tunnelList != null && tunnelList.size() > 0) {
				for (Tunnel tunnel:tunnelList) {
					if (nodeMap.get(tunnel.getASiteId()) == null) {
						siteInst = siteServiceMB.select(tunnel.getASiteId());
						node_a = this.createNode(tunnel.getASiteId(), siteInst.getSiteX(),  siteInst.getSiteY());

						nodeMap.put(tunnel.getASiteId(), node_a);
						box.addElement(node_a);
					}
					if (nodeMap.get(tunnel.getZSiteId()) == null) {
						siteInst = siteServiceMB.select(tunnel.getZSiteId());
						node_z = this.createNode(tunnel.getZSiteId(), siteInst.getSiteX(),  siteInst.getSiteY());
						nodeMap.put(tunnel.getZSiteId(), node_z);
						box.addElement(node_z);
					}
					node_a = nodeMap.get(tunnel.getASiteId());
					node_z = nodeMap.get(tunnel.getZSiteId());
					box.addElement(this.createLink(node_a, node_z, tunnel, Color.green));
				}		
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(oamInfoServiceMB);
			UiUtil.closeService_MB(dualProtectServiceMB);
			UiUtil.closeService_MB(siteServiceMB);
			UiUtil.closeService_MB(tunnelServiceMB);
		}
	}

	public TNetwork getNetWork() {
		return network;
	}

//	public static TunnelTopoPanel getTunnelTopoPanel() {
//		if (tunnelTopoPanel == null) {
//			tunnelTopoPanel = new TunnelTopoPanel();
//		}
//		return tunnelTopoPanel;
//	}

	public void boxData(List<Tunnel> tunnelList) throws Exception {

		// TunnelService tunnelService = null;
		List<Lsp> lspPartList = null;
		Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
		Node node_a = null;
		Node node_z = null;
		int x = 20;
		int y = 60;
		if (null != tunnelList && !tunnelList.isEmpty()) {
			for (Tunnel tunnel : tunnelList) {
				lspPartList = tunnel.getLspParticularList();
				for (int j = 0; j < lspPartList.size(); j++) {
					if (nodeMap.get(lspPartList.get(j).getASiteId()) == null) {
						node_a = this.createNode(lspPartList.get(j).getASiteId(), x, y);
						node_a.setBusinessObject("");

						nodeMap.put(lspPartList.get(j).getASiteId(), node_a);
						box.addElement(node_a);
					}

					if (nodeMap.get(lspPartList.get(j).getZSiteId()) == null) {
						x += 80;
						node_z = this.createNode(lspPartList.get(j).getZSiteId(), x, y);
						node_z.setBusinessObject("");
						nodeMap.put(lspPartList.get(j).getZSiteId(), node_z);
						box.addElement(node_z);
					}
					node_a = nodeMap.get(lspPartList.get(j).getASiteId());
					node_z = nodeMap.get(lspPartList.get(j).getZSiteId());
					box.addElement(this.createLink(node_a, node_z, lspPartList.get(j), Color.green));

				}

			}
		}
		
		nodeMap = null;
	}

	public void boxData(int tunnelId) throws Exception {

		Tunnel tunnel = null;
		TunnelService_MB tunnelServiceMB = null;
		List<Tunnel> tunnelList = null;
		List<Lsp> lspPartList = null;
		Node node_a = null;
		Node node_z = null;
		int x = 0;
		int y = 0;
		Tunnel protectTunnel = null;
		try {
			box.clear();
			if (tunnelId > 0) {
				tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
				tunnel = new Tunnel();
				tunnel.setTunnelId(tunnelId);
				tunnelList = tunnelServiceMB.select(tunnel);

				if (tunnelList != null && tunnelList.size() == 1) {
					tunnel = tunnelList.get(0);
					// 创建工作路径拓扑
					lspPartList = tunnel.getLspParticularList();
					for (int j = 0; j < lspPartList.size(); j++) {
						if (j == 0) {
							node_a = this.createNode(lspPartList.get(j).getASiteId(), x, y);
							node_a.setBusinessObject("A");
							node_a.addAttachment("topoTitle");
							node_z = this.createNode(lspPartList.get(j).getZSiteId(), x, y);
							if (lspPartList.size() == 1) {
								node_z.setBusinessObject("Z");
								node_z.addAttachment("topoTitle");
							}
							box.addElement(node_a);
							box.addElement(node_z);
							box.addElement(this.createLink(node_a, node_z, lspPartList.get(j), Color.green));
						} else {
							node_a = node_z;
							node_z = this.createNode(lspPartList.get(j).getZSiteId(), x, y);

							if (lspPartList.get(j).getZSiteId() == tunnel.getZSiteId()) {
								node_z.setBusinessObject("Z");
								node_z.addAttachment("topoTitle");
							}
							box.addElement(node_z);
							box.addElement(this.createLink(node_a, node_z, lspPartList.get(j), Color.green));
						}
					}

					// 创建保护路径拓扑
					// 如果此tunnel是1:1保护tunnel 就创建保护tunnel拓扑
					if ("2".equals(UiUtil.getCodeById(Integer.parseInt(tunnel.getTunnelType())).getCodeValue())) {
						protectTunnel = tunnel.getProtectTunnel();
						for (Lsp lsp : protectTunnel.getLspParticularList()) {
							node_a = this.createProtectNode(lsp.getASiteId());
							node_z = this.createProtectNode(lsp.getZSiteId());
							box.addElement(this.createLink(node_a, node_z, lsp, Color.yellow));
						}
					}

				} else {
					throw new Exception("根据主键查询返回结果错误");
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			tunnel = null;
			UiUtil.closeService_MB(tunnelServiceMB);
			tunnelList = null;
			lspPartList = null;
			node_a = null;
			node_z = null;
		}
	}

	/**
	 * 创建保护node
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private Node createProtectNode(int siteId) throws Exception {
		Node result = null;
		List<Element> elementList = null;
		SiteInst siteInst = null;
		try {
			elementList = this.box.getAllElements();

			// 遍历所有元素 如果已经存在节点 就返回 如果没有就新建
			for (Element element : elementList) {
				if (element instanceof Node) {
					Node node = (Node) element;
					siteInst = (SiteInst) node.getUserObject();
					if (siteInst.getSite_Inst_Id() == siteId) {
						result = node;
						break;
					}
				}
			}

			if (null == result) {
				result = this.createNode(siteId, 0, 0);
				this.box.addElement(result);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			elementList = null;
			siteInst = null;
		}
		return result;
	}

	/**
	 * 根据pw的路径加载pw拓扑图
	 * 
	 * @param pwinfo
	 * @throws Exception
	 */
	public void boxData(PwInfo pwInfo) throws Exception {
		Node node_a = null;
		Node node_z = null;
		int x = 20;
		int y = 60;
		try {
			box.clear();
			if (null != pwInfo) {

				node_a = this.createNode(pwInfo.getASiteId(), x, y);
				node_a.setBusinessObject("A");
				node_a.addAttachment("topoTitle");
				x += 80;
				node_z = this.createNode(pwInfo.getZSiteId(), x, y);
				node_z.setBusinessObject("Z");
				node_z.addAttachment("topoTitle");

				box.addElement(node_a);
				box.addElement(node_z);
				box.addElement(this.createLink(node_a, node_z, pwInfo, Color.green));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			node_a = null;
			node_z = null;
		}
	}

	/**
	 * 根据pw集合的路径加载pw拓扑图
	 * 
	 * @param pwinfoList
	 * @throws Exception
	 */
	public void boxDataByPws(List<PwInfo> pwinfoList) throws Exception {

		Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
		Node node_a = null;
		Node node_z = null;
		int x = 20;
		int y = 60;
		box.clear();
		if (null != pwinfoList && !pwinfoList.isEmpty()) {

			for (PwInfo pwInfo : pwinfoList) {
				if (nodeMap.get(pwInfo.getASiteId()) == null) {
					node_a = this.createNode(pwInfo.getASiteId(), x, y);
					node_a.setBusinessObject("");

					nodeMap.put(pwInfo.getASiteId(), node_a);
					box.addElement(node_a);
				}

				if (nodeMap.get(pwInfo.getZSiteId()) == null) {
					x += 80;
					node_z = this.createNode(pwInfo.getZSiteId(), x, y);
					node_z.setBusinessObject("");
					nodeMap.put(pwInfo.getZSiteId(), node_z);
					box.addElement(node_z);
				}
				node_a = nodeMap.get(pwInfo.getASiteId());
				node_z = nodeMap.get(pwInfo.getZSiteId());
				box.addElement(this.createLink(node_a, node_z, pwInfo, Color.green));

			}
		}
	}

	/**
	 * 创建node
	 */
	private Node createNode(int siteId, int x, int y) throws Exception {
		SiteInst siteInst = null;
		SiteService_MB siteServiceMB = null;
		Node node = null;
		TopologyUtil topologyUtil=new TopologyUtil();
		try {
			siteServiceMB = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			siteInst = siteServiceMB.selectById(siteId);

			if (siteInst != null) {
				node = new Node();
				node.setName(siteInst.getCellId());
				node.setLocation(x, y);
				topologyUtil.setNodeImage(node, siteInst);
				node.setUserObject(siteInst);
			}
			return node;
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(siteServiceMB);
		}
	}

	private Link createLink(Node nodea, Node nodez, Object object, Color color) {
		try {
			Link link = new Link();
			link.setFrom(nodea);
			link.setTo(nodez);
			link.setLinkType(TWaverConst.LINE_TYPE_DEFAULT);
			link.putLinkColor(color);
			link.putLinkFlowingWidth(3);
			link.putLinkWidth(3);
			link.setUserObject(object);
			link.putLinkBundleExpand(false);
			return link;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据pw集合的路径加载pw拓扑图
	 * 
	 * @param pwinfoList
	 * @throws Exception
	 */
	public void boxDataByPwsOther(List<PwInfo> pwinfoList, List<Node> selectNodeList) throws Exception {

		Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
		Node node_a = null;
		Node node_z = null;
		int x = 20;
		int y = 60;
		box.clear();
		if (null != pwinfoList && !pwinfoList.isEmpty()) {

			for (PwInfo pwInfo : pwinfoList) {
				if (nodeMap.get(pwInfo.getASiteId()) == null) {
					node_a = this.createNode(pwInfo.getASiteId(), x, y);
					// node_a.setBusinessObject("");
					node_a.addAttachment("topoTitle");
					node_a.setBusinessObject(ResourceUtil.srcStr(StringKeysObj.OBJ_SELECTED));
					nodeMap.put(pwInfo.getASiteId(), node_a);
					box.addElement(node_a);
					selectNodeList.add(node_a);
				}

				if (nodeMap.get(pwInfo.getZSiteId()) == null) {
					x += 80;
					node_z = this.createNode(pwInfo.getZSiteId(), x, y);
					// node_z.setBusinessObject("");
					node_z.addAttachment("topoTitle");
					node_z.setBusinessObject(ResourceUtil.srcStr(StringKeysObj.OBJ_SELECTED));
					nodeMap.put(pwInfo.getZSiteId(), node_z);
					box.addElement(node_z);
					selectNodeList.add(node_z);
				}
				node_a = nodeMap.get(pwInfo.getASiteId());
				node_z = nodeMap.get(pwInfo.getZSiteId());
				box.addElement(this.createLinkELAN(node_a, node_z, pwInfo));
			}
		}
	}

	private Link createLinkELAN(Node nodea, Node nodez, Object object) throws Exception {
		try {
			Link link = new Link();
			link.setFrom(nodea);
			link.setTo(nodez);
			link.setLinkType(TWaverConst.LINE_TYPE_DEFAULT);
			link.putLinkColor(Color.BLUE);
			link.putLinkFlowingWidth(3);
			link.putLinkWidth(3);
			link.setUserObject(object);
			link.putLinkBundleExpand(false);
			return link;
		} catch (Exception e) {
			throw e;
		}
	}

	public TDataBox getBox() {
		return box;
	}

}
