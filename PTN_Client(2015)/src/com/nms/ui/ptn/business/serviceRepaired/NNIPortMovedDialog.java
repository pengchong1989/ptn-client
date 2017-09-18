package com.nms.ui.ptn.business.serviceRepaired;

import java.util.List;

import twaver.Node;
import twaver.TDataBox;

import com.nms.db.bean.equipment.port.PortAttr;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.path.Segment;
import com.nms.db.bean.ptn.AllConfigInfo;
import com.nms.db.bean.ptn.CommonBean;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.oam.OamMepInfo;
import com.nms.db.bean.ptn.path.protect.LoopProtectInfo;
import com.nms.db.bean.ptn.path.tunnel.Lsp;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.ptn.qos.QosQueue;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.OamTypeEnum;
import com.nms.drive.service.impl.CoderUtils;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.path.SegmentService_MB;
import com.nms.model.ptn.AllConfigService_MB;
import com.nms.model.ptn.oam.OamInfoService_MB;
import com.nms.model.ptn.path.protect.WrappingProtectService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.ptn.qos.QosQueueService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysTip;

public class NNIPortMovedDialog extends PortMovedDialog {
	private static final long serialVersionUID = -717782534723240714L;

	public NNIPortMovedDialog() {
		super();
		this.setTitle(ResourceUtil.srcStr(StringKeysMenu.MENU_NNI_PORT_MOVED));
		UiUtil.showWindow(this, 500, 400);
	}

	private void initData() {
		this.initSourcePortJList();
		this.initEndPortJList();
	}

	/**
	 * 初始化原端口
	 */
	private void initSourcePortJList() {
		this.initPortJList("NNI", super.sourcePortBox);
	}

	/**
	 * 初始化可选端口
	 */
	private void initEndPortJList() {
		this.initPortJList("NONE", super.needChooseBox);
	}

	private void initPortJList(String portType, TDataBox box) {
		SiteInst site = super.siteInst;
		PortService_MB service = null;
		try {
			service = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			PortInst port = new PortInst();
			port.setSiteId(site.getSite_Inst_Id());
			port.setPortType(portType);
			List<PortInst> portList = service.select(port);
			if(portList != null){
				for (PortInst portInst : portList) {
					Node node = new Node(portInst.getPortId());
					node.setName(portInst.getPortName());
					node.setDisplayName(portInst.getPortName());
					node.setUserObject(portInst);
					node.setSelected(false);
					box.addElement(node);
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
	}
	
	@Override
	protected void selectedSiteActionListener(){
		super.selectedSiteActionListener();
		this.initData();
	}
	
	@Override
	protected void confirmActionListener(){
		boolean flag = super.validated();
		if(flag){
			String result = null;
			for (int i = 0; i < super.sourcePortList.size(); i++) {
				StringBuffer sb = new StringBuffer();
				try {
					PortInst sourcePort = super.sourcePortList.get(i);
					PortInst endPort = super.selectedPortList.get(i);
					//自身属性
					sb.append(this.copyPortAttr(sourcePort, endPort));
					//段
					sb.append(this.copySegment(sourcePort, endPort));
					//按需OAM
					sb.append(this.copyTestOAM(sourcePort, endPort));
					//Tunnel
					sb.append(this.copyTunnel(sourcePort, endPort));
					//全局配置块
					sb.append(this.copyAllConfig(sourcePort, endPort));
					//环网保护
					sb.append(this.copyLoopProtect(sourcePort, endPort));
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}
				String resultSub = sb.toString();
				if(!resultSub.isEmpty()){
					result = resultSub.substring(0, resultSub.length()-1)+" ";
				}
			}
			if(result == null){
				result = ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS);
			}else{
				result += ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_FAIL);
			}
			DialogBoxUtil.succeedDialog(this, result);
			for (int i = 0; i < super.sourcePortList.size(); i++) {
				CommonBean cb = new CommonBean();
				cb.setSourcePortName(sourcePortList.get(i).getPortName());
				cb.setSelectedPortName(selectedPortList.get(i).getPortName());
				AddOperateLog.insertOperLog(super.getConfirm(), EOperationLogType.NNIPORTMOVED.getValue(), result,
						null, cb, sourcePortList.get(i).getSiteId(), ResourceUtil.srcStr(StringKeysMenu.MENU_NNI_PORT_MOVED), "NNIPortMoved");
			}
			this.dispose();
		}
	}

	/**
	 * 端口属性迁移
	 * @throws Exception 
	 */
	private String copyPortAttr(PortInst sourcePort, PortInst endPort){
		String result = "PORT,";
		PortService_MB service = null;
		try {
			endPort.setPortType(sourcePort.getPortType());
			endPort.setJobStatus(sourcePort.getJobStatus());
			endPort.setManageStatus(sourcePort.getManageStatus());
			endPort.setIsOccupy(sourcePort.getIsOccupy());
			endPort.setIsEnabled_code(sourcePort.getIsEnabled_code());
			endPort.setIsEnabled_QinQ(sourcePort.getIsEnabled_QinQ());
			endPort.setMacAddress(sourcePort.getMacAddress());
			endPort.setIsEnabledLaser(sourcePort.getIsEnabledLaser());
			endPort.setIsEnabledAlarmReversal(sourcePort.getIsEnabledAlarmReversal());
			endPort.setServicePort(sourcePort.getServicePort());
			PortAttr sourceAttr = sourcePort.getPortAttr();
			PortAttr endAttr = endPort.getPortAttr();
			int id = endAttr.getId();
			int portId = endAttr.getPortId();
			CoderUtils.copy(sourceAttr, endAttr);
			endAttr.setId(id);
			endAttr.setPortId(portId);
			this.copyQoSQueue(sourcePort.getQosQueues(), endPort.getQosQueues(), endPort);
			//释放原端口
			service = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			sourcePort.setIsOccupy(0);
			service.saveOrUpdate(sourcePort);
			DispatchUtil portDispatch = new DispatchUtil(RmiKeys.RMI_PORT);
			String flag = portDispatch.excuteUpdate(endPort);
			if(!ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS).equals(flag)){
				service.saveOrUpdate(endPort);
			}else{
				result = "";
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
		return result;
	}

	/**
	 * QoS队列迁移
	 * @param endPort 
	 */
	private void copyQoSQueue(List<QosQueue> sourceList, List<QosQueue> endList, PortInst endPort) {
		QosQueueService_MB qosQueueService = null;
		try {
			for (int i = 0; i < sourceList.size(); i++) {
				QosQueue sourceQueue = sourceList.get(i);
				QosQueue endQueue = endList.get(i);
				int id = endQueue.getId();
				CoderUtils.copy(sourceQueue, endQueue);
				endQueue.setId(id);
				endQueue.setObjId(endPort.getPortId());
			}
			qosQueueService = (QosQueueService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosQueue);
			qosQueueService.saveOrUpdate(endList);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(qosQueueService);
		}
	}

	/**
	 * 段的关联portId迁移
	 * @param endPort 
	 * @param sourcePort 
	 * @throws Exception 
	 */
	private String copySegment(PortInst sourcePort, PortInst endPort){
		SegmentService_MB service = null;
		String result = ResourceUtil.srcStr(StringKeysLbl.LBL_SEGMENT)+",";
		try {
			int siteId = sourcePort.getSiteId();
			service = (SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
			Segment segmentCondition = new Segment();
			segmentCondition.setASITEID(siteId);
			segmentCondition.setAPORTID(sourcePort.getPortId());
			List<Segment> segmentList = service.selectBySiteIdAndPort(segmentCondition);
			if(segmentList != null && !segmentList.isEmpty()){
				Segment segment = segmentList.get(0);
				if(segment.getASITEID() == siteId){
					segment.setAPORTID(endPort.getPortId());
					segment.setaSlotNumber(endPort.getSlotNumber());
				}else if(segment.getZSITEID() == siteId){
					segment.setZPORTID(endPort.getPortId());
					segment.setzSlotNumber(endPort.getSlotNumber());
				}
				this.copyTMSOAM(segment, sourcePort, endPort);
				DispatchUtil segmentDispatch = new DispatchUtil(RmiKeys.RMI_SEGMENT);
				String flag = segmentDispatch.excuteUpdate(segment);
				if(flag.contains(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))){
					result = "";
				}
			}else{
				result = "";
			}
		}catch(Exception e){
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
		return result;
	}

	/**
	 * TMSOAM关联的objId迁移
	 * @param endPort 
	 * @param sourcePort 
	 * @param segment 
	 * @throws Exception 
	 */
	private void copyTMSOAM(Segment segment, PortInst sourcePort, PortInst endPort) throws Exception {
		OamInfoService_MB service = null;
		try {
			service = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			OamMepInfo mep = new OamMepInfo();
			mep.setServiceId(segment.getId());
			mep.setSiteId(sourcePort.getSiteId());
			mep.setObjType("SECTION");
			mep.setObjId(sourcePort.getPortId());
			OamMepInfo aMepInfo = service.queryMep(mep);
			if(aMepInfo != null && aMepInfo.getId() > 0){
				aMepInfo.setObjId(endPort.getPortId());
				service.updateObjIdById(aMepInfo);
				OamInfo aOam = new OamInfo();
				aOam.setOamType(OamTypeEnum.AMEP);
				aOam.setOamMep(aMepInfo);
				segment.getOamList().add(aOam);
			}
			if(segment.getASITEID() == sourcePort.getSiteId()){
				mep.setSiteId(segment.getZSITEID());
				mep.setObjId(segment.getZPORTID());
			}else{
				mep.setSiteId(segment.getASITEID());
				mep.setObjId(segment.getAPORTID());
			}
			OamMepInfo zMepInfo = service.queryMep(mep);
			if(zMepInfo != null && zMepInfo.getId() > 0){
				OamInfo zOam = new OamInfo();
				zOam.setOamType(OamTypeEnum.ZMEP);
				zOam.setOamMep(zMepInfo);
				segment.getOamList().add(zOam);
			}
		} finally {
			UiUtil.closeService_MB(service);
		}
	}

	/**
	 * 按需OAM的迁移
	 * @param endPort 
	 * @param sourcePort 
	 * @throws Exception 
	 */
	private String copyTestOAM(PortInst sourcePort, PortInst endPort){
		OamInfoService_MB service = null;
		String result = ResourceUtil.srcStr(StringKeysLbl.LBL_TEST_OAM)+",";
		try {
			service = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			OamMepInfo mep = new OamMepInfo();
			mep.setSiteId(sourcePort.getSiteId());
			mep.setObjType("SECTION_TEST");
			mep.setObjId(sourcePort.getPortId());
			mep = service.queryMep(mep);
			if(mep != null && mep.getId() > 0){
				mep.setObjId(endPort.getPortId());
				OamInfo oam = new OamInfo();
				oam.setOamType(OamTypeEnum.AMEP);
				oam.setOamMep(mep);
				service.updateObjIdById(mep);
				DispatchUtil testOAMDispatch = new DispatchUtil(RmiKeys.RMI_TMSOAMCONFIG);
				String flag = testOAMDispatch.excuteUpdate(oam);
				if(flag.contains(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))){
					result = "";
				}
			}else{
				result = "";
			}
		}catch(Exception e){
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
		return result;
	}

	/**
	 * Tunnel关联的portId迁移
	 * @param endPort 
	 * @param sourcePort 
	 * @throws Exception 
	 */
	private String copyTunnel(PortInst sourcePort, PortInst endPort){
		TunnelService_MB service = null;
		String result = "TUNNEL,";
		try {
			int siteId = sourcePort.getSiteId();
			int portId = sourcePort.getPortId();
			int endPortId = endPort.getPortId();
			service = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			List<Tunnel> tunnelList = service.selectByPortIdAndSiteId(siteId, portId);
			if(tunnelList != null && !tunnelList.isEmpty()){
				Tunnel tunnelInfo = null;
				for(int i = 0; i < tunnelList.size(); i++) {
					Tunnel tunnel = tunnelList.get(i);
					this.updateTunnel(tunnel, siteId, portId, endPortId);
					List<Lsp> lspList = tunnel.getLspParticularList();
					for (int j = 0; j < lspList.size(); j++) {
						Lsp lsp = lspList.get(j);
						boolean flag = this.updateLsp(lsp, siteId, portId, endPortId);
						if(flag){
							break;
						}
					}
					service.update(tunnel);
					tunnelInfo = tunnel;
				}
				if(tunnelInfo != null){
					DispatchUtil tunnelDispatch = new DispatchUtil(RmiKeys.RMI_TUNNEL);
					String flag = tunnelDispatch.excuteUpdate(tunnelInfo);
					if(flag.contains(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))){
						result = "";
					}
				}else{
					result = "";
				}
			}else{
				result = "";
			}
		}catch(Exception e){
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
		return result;
	}

	/**
	 * 修改Tunnel的portId
	 */
	private void updateTunnel(Tunnel t, int siteId, int portId, int endPortId) {
		if(t.getaSiteId() == siteId && t.getaPortId() == portId){
			t.setaPortId(endPortId);
		}else if(t.getzSiteId() == siteId && t.getzPortId() == portId){
			t.setzPortId(endPortId);
		}
	}
	
	/**
	 * 修改Lsp的portId
	 */
	private boolean updateLsp(Lsp lsp, int siteId, int portId, int endPortId) {
		if(lsp.getASiteId() == siteId && lsp.getAPortId() == portId){
			lsp.setAPortId(endPortId);
			return true;
		}else if(lsp.getZSiteId() == siteId && lsp.getZPortId() == portId){
			lsp.setZPortId(endPortId);
			return true;
		}
		return false;
	}

	/**
	 * 全局配置块管理的portId迁移
	 * @param endPort 
	 * @param sourcePort 
	 * @throws Exception 
	 */
	private String copyAllConfig(PortInst sourcePort, PortInst endPort){
		AllConfigService_MB service = null;
		String result = ResourceUtil.srcStr(StringKeysLbl.LBL_GLOBAL_CONFIG)+",";
		try {
			service = (AllConfigService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ALLCONFIG);
			List<AllConfigInfo> allConfigList = service.select(sourcePort.getSiteId());
			if(allConfigList != null && allConfigList.size() == 1){
				AllConfigInfo config = allConfigList.get(0);
				boolean flag = false;
				if(config.getMirrorByPort() == sourcePort.getNumber()){
					config.setMirrorByPort(endPort.getNumber());
					flag = true;
				}
				if(config.getMirrorPort() == sourcePort.getNumber()){
					config.setMirrorPort(endPort.getNumber());
					flag = true;
				}
				if(config.getAlarmPort() == sourcePort.getNumber()){
					config.setAlarmPort(endPort.getNumber());
					flag = true;
				}
				if(flag){
					service.update(config);
					DispatchUtil allConfigDispatch = new DispatchUtil(RmiKeys.RMI_ALLCONFIG);
					String flag1 = allConfigDispatch.excuteUpdate(config);
					if(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS).equals(flag1)){
						result = "";
					}
				}else{
					result = "";
				}
			}else{
				result = "";
			}
		}catch(Exception e){
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
		return result;
	}

	/**
	 * 环网保护关联的portId迁移
	 * @param endPort 
	 * @param sourcePort 
	 * @throws Exception 
	 */
	private String copyLoopProtect(PortInst sourcePort, PortInst endPort) {
		WrappingProtectService_MB service = null;
		String result = "Wrapping,";
		try {
			service = (WrappingProtectService_MB) ConstantUtil.serviceFactory.newService_MB(Services.WRAPPINGPROTECT);
			LoopProtectInfo loopCondition = new LoopProtectInfo();
			loopCondition.setSiteId(sourcePort.getSiteId());
			List<LoopProtectInfo> loopList = service.select(loopCondition);
			if(loopList != null && !loopList.isEmpty()){
				boolean flag = false;
				for (int i = 0; i < loopList.size(); i++) {
					LoopProtectInfo loop = loopList.get(i);
					if(sourcePort.getSiteId() == loop.getSiteId()){
						if(loop.getWestPort() == sourcePort.getPortId()){
							loop.setWestPort(endPort.getPortId());
							flag = true;
						}else if(loop.getEastPort() == sourcePort.getPortId()){
							loop.setEastPort(endPort.getPortId());
							flag = true;
						}
					}
				}
				if(flag){
					service.update(loopList);
					DispatchUtil loopDispatch = new DispatchUtil(RmiKeys.RMI_WRAPPING);
					String flag1 = loopDispatch.excuteUpdate(loopList);
					if(flag1.contains(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))){
						result = "";
					}
				}else{
					result = "";
				}
			}else{
				result = "";
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
		return result;
	}
}
