package com.nms.ui.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import twaver.Link;
import twaver.Node;
import twaver.TDataBox;
import twaver.TWaverConst;
import twaver.network.TNetwork;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.path.Segment;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.path.SegmentService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.util.TopologyUtil;

public class SegmentBasedQinqTopoPanel extends JPanel {
	private static final long serialVersionUID = 526086044364508234L;
	private TDataBox box = new TDataBox();
	private TNetwork network = new TNetwork(box);
	private boolean isExist;

	public SegmentBasedQinqTopoPanel() {
		super(new BorderLayout());
		try {
			init();
			network.doLayout(TWaverConst.LAYOUT_CIRCULAR);
			this.add(network);
			this.setVisible(true);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private List<Segment> getSegment() {
		SegmentService_MB service = null;
		List<Segment> segmentList_temp = null;
		int aPortId = 0;
		int zPortId = 0;
		List<Segment> segmentList = new ArrayList<Segment>();
		try {
			service = (SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
			segmentList_temp = service.select();
			//只选择QinQ使能的端口所建的段
			for (Segment segment : segmentList_temp) {
				aPortId = segment.getAPORTID();
				zPortId = segment.getZPORTID();
				if(this.queryQinQByPortId(aPortId) == 1 && this.queryQinQByPortId(zPortId) == 1){
				//等于1,说明使能,只有A,Z两端端口都使能,此段才可用
					if(!segmentList.contains(segment)){
						segmentList.add(segment);
					}
				}
			}
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
		return segmentList;

	}

	/**
	 * 查询端口Qinq是否使能  0/1 = 未使能/使能
	 * @param portId
	 * @return
	 */
	private int queryQinQByPortId(int portId) {
		PortService_MB portService = null;
		PortInst port = null;
		List<PortInst> portList = null;
		int qingValue = 0;
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			port = new PortInst();
			port.setPortId(portId);
			portList = portService.select(port);
			if(portList != null && portList.size() == 1){
				qingValue = portList.get(0).getIsEnabled_QinQ();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(portService);
			port = null;
			portList = null;
		}
		return qingValue;
	}

	public void init() throws Exception {
		List<Segment> segmentList = null;
		Node node_a = null;
		Node node_z = null;
		int x = 20;
		int y = 60;
		try {
			box.clear();
			segmentList = getSegment();
			if (segmentList != null && segmentList.size() > 0) {
				for (int i = 0; i < segmentList.size(); i++) {
					isExist = false;
					node_a = this.createNode(segmentList.get(i).getASITEID(), x, y);
					if (!isExist) {
						box.addElement(node_a);
						x += 80;
					}
					node_z = this.createNode(segmentList.get(i).getZSITEID(), x, y);
					if (!isExist) {
						box.addElement(node_z);
						x = 20;
						y += 80;
					}
					box.addElement(this.createLink(node_a, node_z, segmentList.get(i)));
				}

			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			node_a = null;
			node_z = null;
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
	private Node createNode(int siteId, int x, int y) throws Exception {
		SiteInst siteInst = null;
		SiteService_MB siteService = null;
		List<SiteInst> siteInstList = null;
		Node node = null;
		try {
			siteInst = new SiteInst();
			siteInst.setSite_Inst_Id(siteId);
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			siteInstList = siteService.select(siteInst);

			if (siteInstList != null && siteInstList.size() == 1) {
				siteInst = siteInstList.get(0);
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
				node.setLocation(x, y);
				TopologyUtil topologyUtil=new TopologyUtil();
				topologyUtil.setNodeImage(node, siteInst);
				node.setUserObject(siteInst);
				isExist = false;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
		}
		return node;
	}

	private Link createLink(Node nodea, Node nodez, Segment segment) throws Exception {
		Link link = null;
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
			link.putLinkWidth(5);
			link.setUserObject(segment);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return link;
	}
}
