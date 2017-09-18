package com.nms.ui.ptn.business.serviceRepaired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import twaver.Node;
import twaver.TDataBox;

import com.nms.db.bean.equipment.port.PortAttr;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.AclInfo;
import com.nms.db.bean.ptn.AllConfigInfo;
import com.nms.db.bean.ptn.CccInfo;
import com.nms.db.bean.ptn.CommonBean;
import com.nms.db.bean.ptn.path.eth.DualInfo;
import com.nms.db.bean.ptn.path.eth.ElineInfo;
import com.nms.db.bean.ptn.path.eth.EtreeInfo;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.bean.ptn.port.PortLagInfo;
import com.nms.db.bean.ptn.qos.QosQueue;
import com.nms.db.enums.EOperationLogType;
import com.nms.drive.service.impl.CoderUtils;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.ptn.AclService_MB;
import com.nms.model.ptn.AllConfigService_MB;
import com.nms.model.ptn.CccService_MB;
import com.nms.model.ptn.path.eth.DualInfoService_MB;
import com.nms.model.ptn.path.eth.ElineInfoService_MB;
import com.nms.model.ptn.path.eth.EtreeInfoService_MB;
import com.nms.model.ptn.port.AcPortInfoService_MB;
import com.nms.model.ptn.port.PortLagService_MB;
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

public class UNIPortMovedDialog extends PortMovedDialog {
	private static final long serialVersionUID = 3467089051056748114L;
	
	public UNIPortMovedDialog() {
		super();
		this.setTitle(ResourceUtil.srcStr(StringKeysMenu.MENU_UNI_PORT_MOVED));
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
		this.initPortJList("UNI", super.sourcePortBox);
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
					//portLag
					sb.append(this.copyPortLag(sourcePort, endPort));
					//AC
					sb.append(this.copyAcInfo(sourcePort, endPort));
					//VPWS
					sb.append(this.copyVPWS(sourcePort));
					//VPLS
					sb.append(this.copyVPLS(sourcePort));
					//双规
					sb.append(this.copyDual(sourcePort));
					//CCC
					sb.append(this.copyCCC(sourcePort));
					//ACL
					sb.append(this.copyACL(sourcePort, endPort));
					//全局配置块
					sb.append(this.copyAllConfig(sourcePort, endPort));
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
				AddOperateLog.insertOperLog(super.getConfirm(), EOperationLogType.UNIPORTMOVED.getValue(), result,
						null, cb, sourcePortList.get(i).getSiteId(), ResourceUtil.srcStr(StringKeysMenu.MENU_UNI_PORT_MOVED), "NNIPortMoved");
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
			endPort.setLagId(sourcePort.getLagId());
			endPort.setLagNumber(sourcePort.getLagNumber());
			endPort.setMacAddress(sourcePort.getMacAddress());
			endPort.setIsEnabled_QinQ(sourcePort.getIsEnabled_QinQ());
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
			DispatchUtil portDispatch = new DispatchUtil(RmiKeys.RMI_PORT);;
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
	 * 端口聚合迁移
	 */
	private String copyPortLag(PortInst sourcePort, PortInst endPort) {
		PortLagService_MB service = null;
		String result = "LAG,";
		try {
			service = (PortLagService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORTLAG);
			PortLagInfo portLagInfo = new PortLagInfo();
			portLagInfo.setSiteId(sourcePort.getSiteId());
			List<PortLagInfo> lagList = service.selectPortByCondition(portLagInfo);
			PortLagInfo lagInfo = null;
			for (int i = 0; i < lagList.size(); i++) {
				PortLagInfo lag = lagList.get(i);
				String portMember = lag.getPortLagMember();
				boolean flag = false;
				if(lag.getFlowControl() == sourcePort.getPortId()){
					lag.setFlowControl(endPort.getPortId());
					flag = true;
				}
				if(lag.getFloodBate() == sourcePort.getPortId()){
					lag.setFloodBate(endPort.getPortId());
					flag = true;
				}
				if(portMember.contains(sourcePort.getNumber()+"")){
					portMember = portMember.replaceAll(sourcePort.getNumber()+"", endPort.getNumber()+"");
					lag.setPortLagMember(portMember);
					flag = true;
				}
				if(flag){
					service.updateLag(lag);
					lagInfo = lag;
				}
			}
			if(lagInfo != null){
				DispatchUtil portDispatch = new DispatchUtil(RmiKeys.RMI_PORTLAG);
				String flag = portDispatch.excuteUpdate(lagInfo);
				if(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS).equals(flag)){
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

	/**
	 * AcInfo迁移
	 */
	private String copyAcInfo(PortInst sourcePort, PortInst endPort) {
		AcPortInfoService_MB service = null;
		String result = "AC,";
		try {
			service = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			AcPortInfo acInfo = new AcPortInfo();
			acInfo.setSiteId(sourcePort.getSiteId());
			acInfo.setPortId(sourcePort.getPortId());
			List<AcPortInfo> acInfoList = service.queryByAcPortInfo(acInfo);
			if(acInfoList != null && !acInfoList.isEmpty()){
				for (int i = 0; i < acInfoList.size(); i++) {
					AcPortInfo ac = acInfoList.get(i);
					ac.setPortId(endPort.getPortId());
					service.saveOrUpdate(ac.getBufferList(), ac);
				}
				result = "";
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
	 * VPWS业务迁移
	 */
	private String copyVPWS(PortInst sourcePort) {
		ElineInfoService_MB service = null;
		String result = "VPWS,";
		try {
			service = (ElineInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Eline);
			List<ElineInfo> elineList = service.selectBySiteId(sourcePort.getSiteId());
			if(elineList != null && !elineList.isEmpty()){
				ElineInfo eline = elineList.get(0);
				DispatchUtil elineDispatch = new DispatchUtil(RmiKeys.RMI_ELINE);
				String flag = elineDispatch.excuteUpdate(eline);
				if(flag.contains(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))){
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

	/**
	 * VPLS业务迁移
	 */
	private String copyVPLS(PortInst sourcePort) {
		EtreeInfoService_MB service = null;
		String result = "VPLS,";
		try {
			service = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);
			Map<Integer, List<EtreeInfo>> etreeMap = service.selectBySiteAndisSingle(sourcePort.getSiteId(), -1);
			if(etreeMap != null && !etreeMap.isEmpty()){
				List<EtreeInfo> etreeList = null;
				for (int key : etreeMap.keySet()) {
					etreeList = etreeMap.get(key);
					break;
				}
				DispatchUtil etreeDispatch = new DispatchUtil(RmiKeys.RMI_ETREE);
				EtreeInfo condition = new EtreeInfo();
				condition.setServiceId(etreeList.get(0).getServiceId());
				etreeList = service.select(condition);
				String flag = etreeDispatch.excuteUpdate(etreeList);
				if(flag.contains(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))){
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

	/**
	 * 双规保护关联ac迁移
	 * @param sourcePort
	 * @return
	 */
	private String copyDual(PortInst sourcePort) {
		String result = "DUAL,";
		DualInfoService_MB service = null;
		try {
			service = (DualInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.DUALINFO);
			Map<Integer,List<DualInfo>> dualInfoMap = service.select(1, sourcePort.getSiteId());
			List<DualInfo> dualInfoList = new ArrayList<DualInfo>();
			this.getDualList(dualInfoList, dualInfoMap);
			if(dualInfoList.size() == 0){
				dualInfoMap = service.select(0, sourcePort.getSiteId());
				this.getDualList(dualInfoList, dualInfoMap);
				if(dualInfoList.size() > 0){
					DispatchUtil etreeDispatch = new DispatchUtil(RmiKeys.RMI_DUAL);
					String flag = etreeDispatch.excuteUpdate(dualInfoList);
					if(flag.contains(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))){
						result = "";
					}
				}else{
					result = "";
				}
			}else{
				DispatchUtil etreeDispatch = new DispatchUtil(RmiKeys.RMI_DUAL);
				String flag = etreeDispatch.excuteUpdate(dualInfoList);
				if(flag.contains(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))){
					result = "";
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
		return result;
	}
	
	private void getDualList(List<DualInfo> dualInfoList, Map<Integer, List<DualInfo>> dualInfoMap) {
		if(dualInfoMap != null && dualInfoMap.size() >0){
			for (Map.Entry<Integer, List<DualInfo>> entrySet : dualInfoMap.entrySet()) 
			{
				if(entrySet.getValue().size() >1)
				{
					for(DualInfo dualInfo : entrySet.getValue())
					{
						if(dualInfo.getPwProtect() != null)
						{
							dualInfoList.add(dualInfo);
						}
					}
				}else{
					dualInfoList.add(entrySet.getValue().get(0));	
				}
			}
		}
	}

	/**
	 * ccc关联ac迁移
	 * @param sourcePort
	 * @return
	 */
	private String copyCCC(PortInst sourcePort) {
		String result = "CCC,";
		CccService_MB service = null;
		try {
			service = (CccService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CCCMANAGEMENT);
			CccInfo condition = new CccInfo();
			condition.setaSiteId(sourcePort.getSiteId());
			List<CccInfo> cccList = service.filterSelect(condition);
			if(cccList != null && cccList.size() > 0){
				DispatchUtil etreeDispatch = new DispatchUtil(RmiKeys.RMI_CCC);
				String flag = etreeDispatch.excuteUpdate(cccList.get(0));
				if(flag.contains(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))){
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
	
	/**
	 * ACL关联的portId迁移
	 * @param endPort 
	 * @param sourcePort 
	 * @throws Exception 
	 */
	private String copyACL(PortInst sourcePort, PortInst endPort){
		AclService_MB service = null;
		String result = "ACL,";
		try {
			service = (AclService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ACLSERCVICE);
			List<AclInfo> aclList = service.select(sourcePort.getSiteId());
			if(aclList != null && !aclList.isEmpty()){
				AclInfo aclInfo = null;
				for (int i = 0; i < aclList.size(); i++) {
					AclInfo acl = aclList.get(i);
					if(acl.getPortNumber() == sourcePort.getNumber()){
						acl.setPortNumber(endPort.getNumber());
						service.update(acl);
						aclInfo = acl;
					}
				}
				if(aclInfo != null){
					DispatchUtil aclDispatch = new DispatchUtil(RmiKeys.RMI_ACL);
					String flag = aclDispatch.excuteInsert(aclInfo);
					if(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS).equals(flag)){
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
}
