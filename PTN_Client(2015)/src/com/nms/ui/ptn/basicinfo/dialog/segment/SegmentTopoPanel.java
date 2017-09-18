package com.nms.ui.ptn.basicinfo.dialog.segment;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;

import twaver.Node;
import twaver.TDataBox;
import twaver.TUIManager;
import twaver.network.TNetwork;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.TopoAttachment;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.util.TopologyUtil;

public class SegmentTopoPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TDataBox box=new TDataBox();
	private TNetwork network=new TNetwork(box);
	//private static SegmentTopoPanel segmenttopopanel;
	private Node node_a = null;
	private Node node_z = null;
	
	public Node getNode_a() {
		return node_a;
	}

	public void setNode_a(Node nodeA) {
		node_a = nodeA;
	}

	public Node getNode_z() {
		return node_z;
	}

	public void setNode_z(Node nodeZ) {
		node_z = nodeZ;
	}

	 {
		TUIManager.registerAttachment("topoTitle", TopoAttachment.class);
	}
	
	public SegmentTopoPanel(){
		super(new BorderLayout());
		try {
		//	segmenttopopanel = this;
			this.add(network);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	public TNetwork getNetWork(){
		return network;
	}
//	
//	public static SegmentTopoPanel getSegmentTopoPanel(){
//		if(segmenttopopanel==null){
//			segmenttopopanel=new SegmentTopoPanel();
//		}
//		return segmenttopopanel;
//	}
//	
	public void boxData() throws Exception{
		
		SiteService_MB siteService = null;
		List<SiteInst> siteInstList = null;
		TopologyUtil topologyUtil=new TopologyUtil();
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			siteInstList = siteService.select();
			

			if (siteInstList != null) {
				for (int i = 0; i < siteInstList.size(); i++) {
					Node node = new Node();
					node.setName(siteInstList.get(i).getCellId());
					node.setLocation(siteInstList.get(i).getSiteX(), siteInstList.get(i).getSiteY());
					
					topologyUtil.setNodeImage(node, siteInstList.get(i));
					
					node.setUserObject(siteInstList.get(i));
					box.addElement(node);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(siteService);
		}
	}
}
