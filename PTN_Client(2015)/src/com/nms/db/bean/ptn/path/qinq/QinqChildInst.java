package com.nms.db.bean.ptn.path.qinq;

import java.util.List;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.ui.frame.ViewDataObj;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;

public class QinqChildInst extends ViewDataObj {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2083129284288869657L;
	private int qinqId;//qinq主键id
	private int id;//主键id
	private int aSiteId;
	private int zSiteId;
	private int aPortId;
	private int zPortId;
	private int aServiceId;
	private int zServiceId;
	private int segmentId;
	private int pathStatus;//0/1 = 未激活/激活
	private int aSiteVlanIdRule;//A端VLAN标签处理规则：删除/增加/透传=0/1/2
	private int zSiteVlanIdRule;//Z端VLAN标签处理规则：删除/增加/透传=0/1/2
	
	@Override
	public void putObjectProperty() {
		String aSiteName = this.getSiteNameBySiteId(getaSiteId());
		String aportName = this.getPortNameByPortId(getaPortId(),getaSiteId());
		String zSiteName = this.getSiteNameBySiteId(getzSiteId());
		String zportName = this.getPortNameByPortId(getzPortId(),getzSiteId());
		this.putClientProperty("id", getId());
		this.putClientProperty("qinqChildName", aSiteName+"/"+aportName+"-"+zSiteName+"/"+zportName);
		this.putClientProperty("aSiteName",aSiteName);
		this.putClientProperty("zSiteName",zSiteName);
		this.putClientProperty("aPortName",aportName);
		this.putClientProperty("zPortName",zportName);
	}

	private String getPortNameByPortId(int portId, int siteId) {
		PortService_MB portService = null;
		List<PortInst> portList = null;
		PortInst portinst = null;
		String portName = null;
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			portinst = new PortInst();
			portinst.setPortId(portId);
			portinst.setSiteId(siteId);
			portList = portService.select(portinst);
			if(portList != null && portList.size() == 1){
				portName = portList.get(0).getPortName();
			}else{
				portName = portId+"";
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(portService);
			portList = null;
			portinst = null;
		}
		
		return portName;
	}

	private String getSiteNameBySiteId(int siteId) {
		SiteService_MB siteService = null;
		String siteName = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			siteName = siteService.select(siteId).getCellId();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
		}
		return siteName;
	}

	public int getQinqId() {
		return qinqId;
	}

	public void setQinqId(int qinqId) {
		this.qinqId = qinqId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getaSiteId() {
		return aSiteId;
	}

	public void setaSiteId(int aSiteId) {
		this.aSiteId = aSiteId;
	}

	public int getzSiteId() {
		return zSiteId;
	}

	public void setzSiteId(int zSiteId) {
		this.zSiteId = zSiteId;
	}

	public int getaPortId() {
		return aPortId;
	}

	public void setaPortId(int aPortId) {
		this.aPortId = aPortId;
	}

	public int getzPortId() {
		return zPortId;
	}

	public void setzPortId(int zPortId) {
		this.zPortId = zPortId;
	}

	public int getSegmentId() {
		return segmentId;
	}

	public void setSegmentId(int segmentId) {
		this.segmentId = segmentId;
	}


	public int getPathStatus() {
		return pathStatus;
	}

	public void setPathStatus(int pathStatus) {
		this.pathStatus = pathStatus;
	}

	public int getaSiteVlanIdRule() {
		return aSiteVlanIdRule;
	}

	public void setaSiteVlanIdRule(int aSiteVlanIdRule) {
		this.aSiteVlanIdRule = aSiteVlanIdRule;
	}

	public int getzSiteVlanIdRule() {
		return zSiteVlanIdRule;
	}

	public void setzSiteVlanIdRule(int zSiteVlanIdRule) {
		this.zSiteVlanIdRule = zSiteVlanIdRule;
	}

	public int getaServiceId() {
		return aServiceId;
	}

	public void setaServiceId(int aServiceId) {
		this.aServiceId = aServiceId;
	}

	public int getzServiceId() {
		return zServiceId;
	}

	public void setzServiceId(int zServiceId) {
		this.zServiceId = zServiceId;
	}
	
}
