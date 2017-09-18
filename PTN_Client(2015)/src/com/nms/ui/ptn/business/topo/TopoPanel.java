﻿package com.nms.ui.ptn.business.topo;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import twaver.Element;
import twaver.Link;
import twaver.Node;
import twaver.TDataBox;
import twaver.TWaverConst;
import twaver.network.TNetwork;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.path.Segment;
import com.nms.db.bean.ptn.path.ces.CesInfo;
import com.nms.db.bean.ptn.path.eth.DualInfo;
import com.nms.db.bean.ptn.path.eth.ElanInfo;
import com.nms.db.bean.ptn.path.eth.ElineInfo;
import com.nms.db.bean.ptn.path.eth.EtreeInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.qinq.QinqChildInst;
import com.nms.db.bean.ptn.path.qinq.QinqInst;
import com.nms.db.bean.ptn.path.tunnel.Lsp;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.path.SegmentService_MB;
import com.nms.model.ptn.path.eth.DualInfoService_MB;
import com.nms.model.ptn.path.eth.ElanInfoService_MB;
import com.nms.model.ptn.path.eth.EtreeInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.util.TopologyUtil;

public class TopoPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3177336439441599681L;
	private final TDataBox box = new TDataBox();
	private final TNetwork network = new TNetwork(box);

	public TopoPanel() {
		init();
	}

	private void init() {
		setLayout();
		clear();
	}

	public void clear() {
		box.clear();
		box.clearAllElementAlarmState();
	}

	public void initData(Object object) {
		this.clear();
		Set<Integer> siteIdList = new HashSet<Integer>();
		// key为起点网元id，value为对端网元id
		int asiteId = 0;
		int zsiteId = 0;
		int networkLayout=0;
		List<Integer[]> siteIdArray=new ArrayList<Integer[]>();
		List<Integer[]> protectArray=new ArrayList<Integer[]>();
		EtreeInfoService_MB etreeService = null;
		ElanInfoService_MB elanInfoService = null;
		DualInfoService_MB dualInfoService = null;
		try {
			if (object instanceof Tunnel) {
				// tunnel的topo图
				Tunnel tunnel = (Tunnel) object;
				asiteId = tunnel.getASiteId();
				zsiteId = tunnel.getZSiteId();
				siteIdList.add(tunnel.getASiteId());
				siteIdList.add(tunnel.getZSiteId());
				this.setSiteArray(tunnel, siteIdArray);
				if("1".equals(UiUtil.getCodeById(Integer.parseInt(tunnel.getTunnelType())).getCodeValue())){
					networkLayout=TWaverConst.LAYOUT_EAST;
				}else{
					this.setSiteArray(tunnel.getProtectTunnel(), protectArray);
					networkLayout=TWaverConst.LAYOUT_CIRCULAR;
					
				}
			} else if (object instanceof PwInfo) {
				PwInfo pwInfo = (PwInfo) object;
				asiteId = pwInfo.getASiteId();
				zsiteId = pwInfo.getZSiteId();
				this.getAllSiteId(pwInfo, siteIdArray,"pw");
				networkLayout=TWaverConst.LAYOUT_EAST;
			} else if (object instanceof CesInfo) {
				CesInfo cesInfo = (CesInfo) object;
				asiteId = cesInfo.getaSiteId();
				zsiteId = cesInfo.getzSiteId();
				this.getAllSiteId(cesInfo, siteIdArray, "ces");
				networkLayout=TWaverConst.LAYOUT_EAST;
			} else if (object instanceof ElineInfo) {
				ElineInfo elineInfo = (ElineInfo) object;
				asiteId = elineInfo.getaSiteId();
				zsiteId = elineInfo.getzSiteId();
				this.getAllSiteId(elineInfo, siteIdArray, "eline");
				networkLayout=TWaverConst.LAYOUT_EAST;
			} else if (object instanceof EtreeInfo) {
				EtreeInfo etreeInfo = (EtreeInfo) object;
				asiteId=etreeInfo.getRootSite();
				etreeService = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);
				List<EtreeInfo> etreeInfoList = etreeService.select(etreeInfo);
				for (int i = 0; i < etreeInfoList.size(); i++) {
					this.getAllSiteId(etreeInfoList.get(i), siteIdArray, "etree");
				}
				networkLayout=TWaverConst.LAYOUT_EAST;
			}else if(object instanceof ElanInfo){
				ElanInfo elanInfo=(ElanInfo) object;
				elanInfoService = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
				List<ElanInfo> elanInfoList=elanInfoService.select(elanInfo.getServiceId());
				for(int i = 0 ; i < elanInfoList.size() ; i++){
					this.getAllSiteId(elanInfoList.get(i), siteIdArray, "elan");
				}
				networkLayout=TWaverConst.LAYOUT_CIRCULAR;
			}else if(object instanceof QinqInst){
				QinqInst qinq = (QinqInst)object;
				asiteId = qinq.getaSiteId();
				zsiteId = qinq.getzSiteId();
				siteIdList.add(qinq.getaSiteId());
				siteIdList.add(qinq.getzSiteId());
				this.setSiteArrayForQinq(qinq, siteIdArray);
				networkLayout=TWaverConst.LAYOUT_EAST;
			}else if(object instanceof DualInfo){
				DualInfo dualInfo =(DualInfo) object;
				dualInfoService = (DualInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.DUALINFO);
				List<DualInfo> dualInfoList = dualInfoService.select(dualInfo);
				for(int i = 0 ; i < dualInfoList.size() ; i++){
					this.getAllSiteId(dualInfoList.get(i), siteIdArray, "dual");
				}
				networkLayout=TWaverConst.LAYOUT_CIRCULAR;
			}

			// 创建节点
			siteIdList.clear();
			for (Integer[] siteId :siteIdArray) {
				if(!siteIdList.contains(siteId[0])){
					siteIdList.add(siteId[0]);
				}
				if(!siteIdList.contains(siteId[1])){
					siteIdList.add(siteId[1]);
				}
			}
			//保护tunnel
			if(protectArray.size()>0){
				for (Integer[] siteId :protectArray) {
					if(!siteIdList.contains(siteId[0])){
						siteIdList.add(siteId[0]);
					}
					if(!siteIdList.contains(siteId[1])){
						siteIdList.add(siteId[1]);
					}
				}
			}
			addSiteNode(siteIdList, asiteId, zsiteId, object);
			// 创建link
			for (Integer[] siteId :siteIdArray) {
				addLink(siteId[0], siteId[1],false);
			}
			// 创建保护link
			for (Integer[] siteId :protectArray) {
				addLink(siteId[0], siteId[1],true);
			}
			network.doLayout(networkLayout);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(etreeService);
			UiUtil.closeService_MB(elanInfoService);
			UiUtil.closeService_MB(dualInfoService);
		}

	}
	
	/**
	 * 把此qinq下的所有网元id放到List<Integer[]>中， integer[0]为A端ID integer[1]为Z端ID 
	 * @param qinq
	 * @param siteIdArray
	 * @throws Exception
	 */
	private void setSiteArrayForQinq(QinqInst qinq, List<Integer[]> siteIdArray) throws Exception {
		try{
			for(QinqChildInst child : qinq.getQinqChildInst()){
				siteIdArray.add(new Integer[]{child.getaSiteId(),child.getzSiteId()});
			}
		}catch (Exception e) {
			throw e;
		}
	}

	public void getAllSiteId(Object object ,List<Integer[]> siteIdArray,String type){
		if("pw".equals(type)){
			PwInfo pwInfo = (PwInfo) object;
			setSiteArray(pwInfo, siteIdArray);
		}else if("ces".equals(type)){
			CesInfo cesInfo = (CesInfo) object;
			siteIdArray.add(new Integer[]{cesInfo.getaSiteId(),cesInfo.getzSiteId()});
		}else if("eline".equals(type)){
			ElineInfo elineInfo = (ElineInfo) object;
			siteIdArray.add(new Integer[]{elineInfo.getaSiteId(),elineInfo.getzSiteId()});
		}else if("etree".equals(type)){
			EtreeInfo etreeInfo = (EtreeInfo) object;
			siteIdArray.add(new Integer[]{etreeInfo.getRootSite(),etreeInfo.getBranchSite()});
		}else if("elan".equals(type)){
			ElanInfo elanInfo = (ElanInfo) object;
			siteIdArray.add(new Integer[]{elanInfo.getaSiteId(),elanInfo.getzSiteId()});
		}else if("dual".equals(type)){
			DualInfo dualInfo = (DualInfo) object;
			if(dualInfo.getBranchMainSite()>0){
				siteIdArray.add(new Integer[]{dualInfo.getRootSite(),dualInfo.getBranchMainSite()});
			}
			if(dualInfo.getBranchProtectSite()>0){
				siteIdArray.add(new Integer[]{dualInfo.getRootSite(),dualInfo.getBranchProtectSite()});
			}
		}
	}
	
	private void setSiteArray(PwInfo pwInfo,List<Integer[]> siteIdArray){
		if("1".equals(pwInfo.getBusinessType())){
			siteIdArray.add(new Integer[]{pwInfo.getASiteId(),pwInfo.getMsPwInfos().get(0).getSiteId()});
			for (int i = 0; i < pwInfo.getMsPwInfos().size()-1; i++) {
				siteIdArray.add(new Integer[]{pwInfo.getMsPwInfos().get(i).getSiteId(),pwInfo.getMsPwInfos().get(i+1).getSiteId()});
			}
			siteIdArray.add(new Integer[]{pwInfo.getMsPwInfos().get(pwInfo.getMsPwInfos().size()-1).getSiteId(),pwInfo.getZSiteId()});
		}else{
			siteIdArray.add(new Integer[]{pwInfo.getASiteId(),pwInfo.getZSiteId()});
		}
	}
	/**
	
	* 转换保护的网元id集合   
	
	* @author kk
	
	* @param   
	
	* @return 
	
	* @Exception 异常对象
	 */
	public void convertProtectSite(Tunnel tunnel , List<Integer> siteIds){
		
		for(Lsp lsp:tunnel.getLspParticularList()){
			if(!siteIds.contains(lsp.getASiteId())){
				siteIds.add(lsp.getASiteId());
			}
			if(!siteIds.contains(lsp.getZSiteId())){
				siteIds.add(lsp.getZSiteId());
			}
		}
		
	}

	/**
	 * 通过pwid获取所有网元id，并放入siteIdMap中
	 * 
	 * @param pwid
	 * @param siteIdMap
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private void getAllSiteId(int pwid, List<Integer[]> siteIdArray) throws Exception {
		PwInfoService_MB pwInfoService = null;
		PwInfo pwinfo = null;
		TunnelService_MB tunnelService = null;
		Tunnel tunnel = null;
		try {
			pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			pwinfo = pwInfoService.selectByPwId(pwid);

			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);

			tunnel = new Tunnel();
			tunnel.setTunnelId(pwinfo.getTunnelId());
			tunnel = tunnelService.selectNodeByTunnelId(tunnel).get(0);
			this.setSiteArray(tunnel, siteIdArray);
			
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(pwInfoService);
			UiUtil.closeService_MB(tunnelService);
			pwinfo = null;
			tunnel = null;
		}

	}
	
	/**
	 * 把此tunnel下的所有网元id放到List<Integer[]>中， integer[0]为A端ID integer[1]为Z端ID 
	 * @param tunnel
	 * @param siteIdArray
	 * @throws Exception
	 */
	private void setSiteArray(Tunnel tunnel,List<Integer[]> siteIdArray) throws Exception{
		try{
			for(Lsp lspParticular : tunnel.getLspParticularList()){
				siteIdArray.add(new Integer[]{lspParticular.getASiteId(),lspParticular.getZSiteId()});
			}
		}catch (Exception e) {
			throw e;
		}
	}

	private void setLayout() {
		GridBagLayout layout = new GridBagLayout();
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 0, 0, 0);
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		layout.addLayoutComponent(network, c);
		this.add(network);

	}

	/**
	 * 创建网元节点
	 * 
	 * @param siteId
	 *            网元数据库id
	 */
	private void addSiteNode(Set<Integer> siteIdList, int asiteId, int zsiteId, Object object) {
		SiteService_MB siteService = null;
		List<SiteInst> siteInstList = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			siteInstList = siteService.select();
			for (SiteInst siteInst : siteInstList) {
				if (siteIdList.contains(siteInst.getSite_Inst_Id())) {
					if (object instanceof EtreeInfo) {
						if (asiteId == siteInst.getSite_Inst_Id()) {
							box.addElement(createNode(siteInst, "root"));
						}else {
							box.addElement(createNode(siteInst, null));
						}
					} else if(object instanceof ElanInfo) {
						box.addElement(createNode(siteInst, null));
					} else{
						if (asiteId == siteInst.getSite_Inst_Id()) {
							box.addElement(createNode(siteInst, "A"));
						} else if (zsiteId == siteInst.getSite_Inst_Id()) {
							box.addElement(createNode(siteInst, "Z"));
						} else {
							box.addElement(createNode(siteInst, null));
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
		}
	}

	private Node createNode(SiteInst siteInst, String attachmentName) {
		Node node = null;
		TopologyUtil topologyUtil=new TopologyUtil();
		try {
			node = new Node();
			node.setName(siteInst.getCellId());
			topologyUtil.setNodeImage(node, siteInst);
			node.setUserObject(siteInst.getSite_Inst_Id());
			if (attachmentName != null) {
				node.setBusinessObject(attachmentName);
				node.addAttachment("topoTitle");
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return node;
	}

	@SuppressWarnings("unchecked")
	private void addLink(int asiteId, int zsiteId,boolean isProtect) {
		Node nodea = null;
		Node nodez = null;
		List<Element> elements = box.getAllElements();
		for (Element element : elements) {
			if (element instanceof Node) {
				if (Integer.parseInt(element.getUserObject().toString()) == asiteId) {
					nodea = (Node) element;
				} else if (Integer.parseInt(element.getUserObject().toString()) == zsiteId) {
					nodez = (Node) element;
				}
			}
		}
		box.addElement(createLink(nodea, nodez,isProtect));
	}

	/**
	 * 创建link的基础属性
	 */
	private Link createLink(Node nodea, Node nodez,boolean isProtect) {
		try {
			Link link = new Link();
			link.setFrom(nodea);
			link.setTo(nodez);
			link.setLinkType(TWaverConst.LINE_TYPE_DEFAULT);
			
			if(isProtect){
				link.putLinkColor(Color.YELLOW);
			}else{
				link.putLinkColor(Color.GREEN);
			}
			
			link.putLinkFlowingWidth(3);
			link.putLinkWidth(3);
			return link;
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return null;
	}

	public List<Segment> getAllSegment() {
		SegmentService_MB service = null;
		List<Segment> segmentList = null;
		try {
			service = (SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
			segmentList = service.select();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
		return segmentList;
	}

}
