package com.nms.ui.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

import javax.swing.JPanel;

import twaver.Link;
import twaver.Node;
import twaver.TDataBox;
import twaver.TWaverConst;
import twaver.network.TNetwork;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.path.Segment;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.path.SegmentService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.util.TopologyUtil;

public class SegmentTopoPanel extends JPanel {
	private static final long serialVersionUID = 526086044364508234L;
	private TDataBox box = new TDataBox();
	private TNetwork network = new TNetwork(box);
	private boolean isExist;

	public SegmentTopoPanel() {
		super(new BorderLayout());
		try {
			init();
//	     	network.doLayout(TWaverConst.LAYOUT_CIRCULAR);
			this.add(network);
			this.setVisible(true);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	@SuppressWarnings("unchecked")
	private List<Segment> getSegment() {
		SegmentService_MB service = null;
		List<Segment> segmentList = null;
		ListingFilter filter=null;
		try {
			filter=new ListingFilter();
			service = (SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
			segmentList =(List<Segment>) filter.filterList( service.selectNoOam());
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
			filter=null;
		}
		return segmentList;

	}

	public void init() throws Exception {
		List<Segment> segmentList = null;
		Node node_a = null;
		Node node_z = null;
		SiteService_MB siteService = null;
		try {
			box.clear();
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			segmentList = getSegment();
			if (segmentList != null && segmentList.size() > 0) {
				for (int i = 0; i < segmentList.size(); i++) {
					isExist = false;
					node_a = this.createNode(segmentList.get(i).getASITEID(),siteService);
					if (!isExist) {
						box.addElement(node_a);
					}
					node_z = this.createNode(segmentList.get(i).getZSITEID(),siteService);
					if (!isExist) {
						box.addElement(node_z);
					}
					box.addElement(this.createLink(node_a, node_z, segmentList.get(i)));
				}

			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
			node_a = null;
			node_z = null;
			segmentList = null;
		}

	}

	public TDataBox getBox() {
		return box;
	}

	public void setBox(TDataBox box) {
		this.box = box;
	}

	public TNetwork getNetwork() {
		return network;
	}

	public void setNetwork(TNetwork network) {
		this.network = network;
	}

	/**
	 * 创建node
	 */
	@SuppressWarnings("rawtypes")
	private Node createNode(int siteId,SiteService_MB siteService) throws Exception {
		SiteInst siteInst = null;
		Node node = null;
		try {
			siteInst = new SiteInst();
			
			siteInst = siteService.selectById(siteId);

			if (siteInst != null ) {
				List elements = box.getAllElements();
				for (int j = 0; j < elements.size(); j++) {
					if (elements.get(j) instanceof Node) {
						Node elementNode = (Node) elements.get(j);
						if (elementNode.getName().equals(siteInst.getCellId())) {
							isExist = Boolean.TRUE;
							return elementNode;
						}
					}
				}
				node = new Node();
				node.setName(siteInst.getCellId());
				node.setLocation(siteInst.getSiteX(), siteInst.getSiteY());
				TopologyUtil topologyUtil=new TopologyUtil();
				topologyUtil.setNodeImage(node, siteInst);
				node.setUserObject(siteInst);
				isExist = false;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} 
		return node;
	}

	private Link createLink(Node nodea, Node nodez, Segment segment) throws Exception {
		Link link = null;
		TopologyUtil topologyUtil=new TopologyUtil();
		try {
			link = new Link();
			link.setFrom(nodea);
			link.setTo(nodez);
			link.putLinkBundleExpand(false);
			link.putLinkBundleSize(1);
			StringBuilder str = new StringBuilder();
			str.append("From : ").append(segment.getShowSiteAname()).append("/").append(segment.getAPORTID()).append("<----->To :").append(segment.getShowSiteZname()).append("/").append(segment.getZPORTID()).append("</html>");
			link.setLinkType(TWaverConst.LINE_TYPE_DEFAULT);
			// link.putLinkFlowingWidth(5);
			// link.putLinkFlowing(true);
			// link.putLinkFlowingColor(Color.RED);
			// link.putLinkColor(Color.white);
			// link.putLinkOutlineColor(Color.black);
			link.putLinkColor(Color.green);
			link.putLinkOutlineColor(Color.white);
			
			topologyUtil.setLinkWidth(segment, link);
			
			link.setUserObject(segment);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return link;
	}

}
