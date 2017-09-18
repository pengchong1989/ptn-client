﻿package com.nms.ui.ptn.basicinfo.dialog.segment.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.nms.db.bean.path.Segment;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.oam.OamMepInfo;
import com.nms.db.bean.ptn.path.protect.LoopProtectInfo;
import com.nms.db.bean.ptn.path.tunnel.Lsp;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.ptn.qos.QosInfo;
import com.nms.db.bean.ptn.qos.QosQueue;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EQosDirection;
import com.nms.db.enums.EServiceType;
import com.nms.db.enums.OamTypeEnum;
import com.nms.model.path.SegmentService_MB;
import com.nms.model.ptn.oam.OamInfoService_MB;
import com.nms.model.ptn.path.protect.WrappingProtectService_MB;
import com.nms.model.ptn.path.tunnel.LspInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.ptn.qos.QosInfoService_MB;
import com.nms.model.ptn.qos.QosQueueService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.ResultString;
import com.nms.service.impl.util.SiteUtil;
import com.nms.service.impl.util.WhImplUtil;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ListingFilter;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.basicinfo.SegmentPanel;
import com.nms.ui.ptn.basicinfo.dialog.segment.AddSegment;
import com.nms.ui.ptn.basicinfo.dialog.segment.AddSegmentFilterDialog;
import com.nms.ui.ptn.basicinfo.dialog.segment.SearchSegmentDialog;
import com.nms.ui.ptn.oam.view.dialog.OamInfoDialog;
import com.nms.ui.ptn.systemconfig.dialog.qos.controller.QosConfigController;

/**
 * @author lepan
 */
public class SegmentPanelController extends AbstractController {
	private final SegmentPanel view;
	private Segment segmentFilterCondition;
	public SegmentPanelController(SegmentPanel view) {
		this.view = view;
	}
	@Override
	public void openCreateDialog() throws Exception {
		try {
			AddSegment dialog = new AddSegment(this.getView(), true, 0);
			dialog.setLocation(UiUtil.getWindowWidth(dialog.getWidth()), UiUtil.getWindowHeight(dialog.getHeight()));
			dialog.setVisible(true);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	@Override
	public void openUpdateDialog() throws Exception {
		try {
			int id = this.getView().getSelect().getId();
			AddSegment dialog = new AddSegment(this.getView(), true, id);
			dialog.setLocation(UiUtil.getWindowWidth(dialog.getWidth()), UiUtil.getWindowHeight(dialog.getHeight()));
			dialog.setVisible(true);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
			throw e;
		}
	}

	@Override
	public void openFilterDialog() throws Exception {
		AddSegmentFilterDialog addSegmentFilterDialog = new AddSegmentFilterDialog(segmentFilterCondition, this);
		UiUtil.showWindow(addSegmentFilterDialog, 400, 300);
	}

	@Override
	public boolean deleteChecking(){
		String mes = null;
		List<Segment> segments = null;
		boolean resultFlag = false;
		boolean onlineResult = false;
		try {
			segments = view.getAllSelect();			
            //判断在线网元是否托管	
			SiteUtil siteUtil = new SiteUtil();
			List<Integer> allSiteIds = new ArrayList<Integer>();
			List<Integer> siteIds = new ArrayList<Integer>();
			for(Segment sg: segments)
			{
				allSiteIds.add(sg.getASITEID());
				allSiteIds.add(sg.getZSITEID());
			}
			for( int i=0; i<allSiteIds.size()-1; i++) 
			{     
		      for(int j=allSiteIds.size() -1; j>i; j--)
		      {     
		           if(allSiteIds.get(j)==allSiteIds.get(i))
		           {     
		        	   allSiteIds.remove(j);     
		           }      
		        }      
			} 
			for(int i=0;i<allSiteIds.size();i++){
				if(1 == ((SiteUtil) siteUtil).SiteTypeOnlineUtil(allSiteIds.get(i))){
					siteIds.add(allSiteIds.get(i));
				}				
			}
			if(siteIds !=null && siteIds.size()!=0){
				onlineResult = true;
			}
			if(onlineResult){
				WhImplUtil wu = new WhImplUtil();
				String str=wu.getNeNames(siteIds);
				DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_NOT_DELETEONLINE)+""+str+ResourceUtil.srcStr(StringKeysTip.TIP_ONLINENOT_DELETEONLINE));
				this.insertOpeLog(EOperationLogType.SEGMENTDELETE4.getValue(), ResultString.CONFIG_FAILED, null, null);	
				return false;
			}
				
							
			for (Segment s : segments) {
				//如果为true，说明该条段的某个网元或两个网元有按需oam，不能删除
				if(checkIsOam(s)){
					resultFlag = true;
					break;
				}
			}
			if(resultFlag){
				DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_CLEAN_OAM));
				UiUtil.insertOperationLog(EOperationLogType.SEGMENTDELETE1.getValue());
				return false;
			}			
			mes = this.checkIsUsed(segments);
			if (mes != null && !"".equals(mes)) {
				DialogBoxUtil.errorDialog(SegmentPanelController.this.getView(), mes + "," + ResourceUtil.srcStr(StringKeysTip.TIP_DELETE_SEGMENT_TUNNEL));
				this.insertOpeLog(EOperationLogType.SEGMENTDELETE2.getValue(), ResultString.CONFIG_FAILED, null, null);	
				return false;
			} 
			mes = this.checkIsUsedForLoop(segments);
			if (mes != null && !"".equals(mes)) {
				DialogBoxUtil.errorDialog(SegmentPanelController.this.getView(), mes + "," + ResourceUtil.srcStr(StringKeysTip.TIP_DELETE_SEGMENT_RING));
				this.insertOpeLog(EOperationLogType.SEGMENTDELETE2.getValue(), ResultString.CONFIG_FAILED, null, null);			
				return false;
			} 
//			mes = this.checkIsUsedByQinq(segments);
//			if(mes != null && !"".equals(mes)){
//				DialogBoxUtil.errorDialog(SegmentPanelController.this.view, mes + "," + ResourceUtil.srcStr(StringKeysTip.TIP_DELETE_SEGMENT_QINQ));
//				UiUtil.insertOperationLog(EOperationLogType.SEGMENTDELETE3.getValue());
//				return false;
//			}
			resultFlag = true;			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return resultFlag;
	}

	
		
//	/**
//	 * 判断此段是否被Qinq使用
//	 * @param segments
//	 * @return
//	 */
//	private String checkIsUsedByQinq(List<Segment> segments) {
//		StringBuffer strBuff = new StringBuffer();
//		String ret = null;
//		QinQChildInstService childService = null;
//		QinqChildInst child = null;
//		List<QinqChildInst> childList = null;
//		try {
//			childService = (QinQChildInstService) ConstantUtil.serviceFactory.newService(Services.QinQChild);
//			for (Segment segment : segments) {
//
//				child = new QinqChildInst();
//				child.setSegmentId(segment.getId());
//				childList = childService.select(child);
//
//				if (childList != null && childList.size() > 0) {
//					strBuff.append(segment.getNAME()).append("、");
//				}
//			}
//			if (strBuff.toString().contains("、")) {
//				ret = strBuff.toString().substring(0, strBuff.toString().length() - 1);
//			}
//		} catch (Exception e) {
//			ExceptionManage.dispose(e,this.getClass());
//		} finally {
//			UiUtil.closeService(childService);
//			child = null;
//			childList = null;
//		}
//		return ret;
//	}

	private String checkIsUsedForLoop(List<Segment> segments) {
		StringBuffer strBuff = new StringBuffer();
		String ret = null;
		WrappingProtectService_MB wrappingProtectService = null;
		List<LoopProtectInfo> loopProtectInfoList = null;
		LoopProtectInfo loopProtectInfo;
		try {
			loopProtectInfo = new LoopProtectInfo();
			loopProtectInfoList = new ArrayList<LoopProtectInfo>();
			wrappingProtectService = (WrappingProtectService_MB) ConstantUtil.serviceFactory.newService_MB(Services.WRAPPINGPROTECT);
			for (Segment segment : segments) {
				Segment westSegment = new Segment();
				loopProtectInfoList = wrappingProtectService.select(loopProtectInfo);
				if (loopProtectInfoList != null && loopProtectInfoList.size() > 0) {
					for(LoopProtectInfo loopProtectInfoResult :loopProtectInfoList){
						westSegment = loopProtectInfoResult.getWestSegment();
						if(null!=westSegment){
							if(westSegment.getId() == segment.getId()){
								strBuff.append(segment.getNAME()).append("、");
							}
						}
					}
				}
			}
			if (strBuff.toString().contains("、")) {
				ret = strBuff.toString().substring(0, strBuff.toString().length() - 1);
			}		
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(wrappingProtectService);
			loopProtectInfo = null;
			loopProtectInfoList = null;
		}
		return ret;
	}
	private boolean checkIsOam(Segment s) {
		OamMepInfo mep = null;
		OamInfoService_MB service = null;
		boolean flag = false;
		try {
			mep = new OamMepInfo();
			mep.setObjId(s.getAPORTID());
			mep.setObjType("SECTION_TEST");
			service = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			flag = service.queryByObjIdAndType(mep);
			if(flag){
				return flag;
			}else{
				mep.setObjId(s.getZPORTID());
				mep.setObjType("SECTION_TEST");
				flag = service.queryByObjIdAndType(mep);
			}
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			mep=null;
			UiUtil.closeService_MB(service);
		}
		return flag;
	}

	@Override
	public void delete() throws Exception {
		DispatchUtil segmentDispatch = null;
		List<Segment> segments = null;
		String result = null;
		try {
			segmentDispatch = new DispatchUtil(RmiKeys.RMI_SEGMENT);
			segments = view.getAllSelect();
			result = segmentDispatch.excuteDelete(segments);
			//添加日志记录
			for(int i=0;i<segments.size();i++){
			  this.insertOpeLog(EOperationLogType.SEGEMENTDELETE.getValue(), result, null, segments.get(i));	
			}
			DialogBoxUtil.succeedDialog(SegmentPanelController.this.getView(), result);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			segmentDispatch = null;
			segments = null;
			result=null;
		}
	}

	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		Segment se=(Segment)newMac;
		String segmentName=se.getNAME();
		AddOperateLog.insertOperLog(null, operationType, result, oldMac, newMac, 0,segmentName,"");		
	}
	
	/**
	 * 判断列表中的段是否被使用
	 * 
	 * @param segments
	 * @return
	 */
	private String checkIsUsed(List<Segment> segments) throws Exception {
		StringBuffer strBuff = new StringBuffer();
		String ret = null;
		LspInfoService_MB lspService = null;
		Lsp lsp = null;
		List<Lsp> lspList = null;
		try {
			lspService = (LspInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.LSPINFO);
			for (Segment segment : segments) {

				lsp = new Lsp();
				lsp.setSegmentId(segment.getId());
				lspList = lspService.select(lsp);
				if (lspList != null && lspList.size() > 0) {
					strBuff.append(segment.getNAME()).append("、");
				}
			}
			if (strBuff.toString().contains("、")) {
				ret = strBuff.toString().substring(0, strBuff.toString().length() - 1);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(lspService);
			lsp = null;
			lspList = null;
			
		}
		return ret;
	}

	@Override
	public void clearFilter() throws Exception {
		segmentFilterCondition = null;
		this.refresh();
		 this.insertOpeLog(EOperationLogType.SEGMENTCLEARSELECT.getValue(), ResultString.CONFIG_SUCCESS, null, null);
		
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public void refresh() throws Exception {
		SegmentService_MB service = null;
		List<Segment> segmentList = null;
		ListingFilter filter=null;
		try {
			filter=new ListingFilter();
			service = (SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
			if(segmentFilterCondition == null){
				segmentFilterCondition = new Segment();
			}					
			segmentList=(List<Segment>) filter.filterList(service.select(segmentFilterCondition));
			view.clear();
			view.initData(segmentList);
			view.getAqosPanel().clear();
			view.getZqosPanel().clear();
			view.getOamTable().clear();
			view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
			segmentList = null;
			filter=null;
		}
	}

	public SegmentPanel getView() {
		return view;
	}

	/**
	 * 打开配置QoS对话框
	 */
	public void openConfigQoSDialog() throws Exception {
		Segment segment = null;
		QosConfigController controller = null;
		try {
			segment = this.view.getSelect();
			controller = new QosConfigController();
			controller.openQosConfig(controller, "SECTION", segment, null,this.view .getDialog());
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			segment = null;
		}
	}

	/**
	 * 打开配置OAM对话框
	 */
	public void openConfigOAMDialog() throws Exception {
		new OamInfoDialog( this.view.getSelect(), EServiceType.SECTION.toString(), 0, true,null);
	}

	/**
	 * 选中一条段后，初始化tabber页的详细信息
	 */
	@Override
	public void initDetailInfo() {
		Segment segment = null;
		List<QosQueue> qosQueueList = null;
		QosQueue qosQueue = null;
		OamInfoService_MB oamService = null;
		List<OamInfo> oamInfos = null;
		OamInfo oamA = null;
		OamInfo oamZ = null;
		OamMepInfo oamMepA = null;
		OamMepInfo oamMepZ = null;
		QosQueueService_MB qosService = null;
		TunnelService_MB tunnelService = null;
		List<Tunnel> tunnelList = null;
		QosInfoService_MB qosInfoService = null;
		List<QosInfo> qosInfoList = null;
		// key为同一条段上所有tunnel的前向qos的cos值，value为对应的cir和
		Map<Integer, Integer> beforeCirMap = null;
		// key为同一条段上所有tunnel的后向qos的cos值，value为对应的cir和
		Map<Integer, Integer> afterCirMap = null;
		try {
			segment = view.getSelect();
			// 初始化qos信息
			qosService = (QosQueueService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosQueue);
			qosQueue = new QosQueue();
			qosQueue.setSiteId(segment.getASITEID());
			qosQueue.setObjType("SECTION");		
			qosQueue.setObjId(segment.getAPORTID());
			qosQueueList = qosService.queryByCondition(qosQueue);

			// 配置CIR带宽值
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			qosInfoService = (QosInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosInfo);
			tunnelList = tunnelService.selectByPort(segment.getAPORTID(), segment.getZPORTID());
			if (tunnelList != null && tunnelList.size() > 0) {
				beforeCirMap = new HashMap<Integer, Integer>();
				afterCirMap = new HashMap<Integer, Integer>();
				for (Tunnel tunnelInfo : tunnelList) {
					// 根据段查找qos信息
					qosInfoList = qosInfoService.getQosByObj(EServiceType.TUNNEL.toString(), tunnelInfo.getTunnelId());
					if (qosInfoList != null && qosInfoList.size() > 0) {
						for (QosInfo info : qosInfoList) {
							Integer value = 0;
							if (Integer.parseInt(info.getDirection()) == EQosDirection.FORWARD.getValue()) {
								if (beforeCirMap.get(info.getCos()) == null) {
									beforeCirMap.put(info.getCos(), info.getCir());
								} else {
									value = beforeCirMap.get(info.getCos());
									value = value + +info.getCir();
									beforeCirMap.put(info.getCos(), value);
								}
							} else if (Integer.parseInt(info.getDirection()) == EQosDirection.BACKWARD.getValue()) {
								if (afterCirMap.get(info.getCos()) == null) {
									afterCirMap.put(info.getCos(), info.getCir());
								} else {
									value = afterCirMap.get(info.getCos());
									value = value + +info.getCir();
									afterCirMap.put(info.getCos(), value);
								}
							}
						}
					}
				}
			}
			for (QosQueue queue : qosQueueList) {
				if (queue.getObjId() == segment.getAPORTID()) {
					// 设置前向的带宽
					if (beforeCirMap == null || beforeCirMap.get(queue.getCos()) == null) {
						queue.setUsedBand(0);
					} else {
						queue.setUsedBand(beforeCirMap.get(queue.getCos()));
					}
					queue.setRestBand(queue.getCir() - queue.getUsedBand());
				}
			}
			view.getAqosPanel().clear();
			view.getAqosPanel().initData(qosQueueList);

			qosQueue.setSiteId(segment.getZSITEID());
			qosQueue.setObjType("SECTION");
			qosQueue.setObjId(segment.getZPORTID());
			
			qosQueueList = qosService.queryByCondition(qosQueue);
			for (QosQueue queue : qosQueueList) {
				if (queue.getObjId() == segment.getZPORTID()) {
					// 设置后向的带宽
					if (afterCirMap == null || afterCirMap.get(queue.getCos()) == null) {
						queue.setUsedBand(0);
					} else {
						queue.setUsedBand(afterCirMap.get(queue.getCos()));
					}
					queue.setRestBand(queue.getCir() - queue.getUsedBand());
				}
			}
			view.getZqosPanel().clear();
			view.getZqosPanel().initData(qosQueueList);
			// 初始化oam信息
			oamService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			oamInfos = new ArrayList<OamInfo>();
			oamA = new OamInfo();
			oamZ = new OamInfo();
			oamMepA = new OamMepInfo();
			oamMepZ = new OamMepInfo();
			oamMepA.setServiceId(segment.getId());
			oamMepA.setSiteId(segment.getASITEID());
			oamMepA.setObjType("SECTION");
			oamMepZ.setObjType("SECTION");
			oamMepZ.setServiceId(segment.getId());
			oamMepZ.setSiteId(segment.getZSITEID());
			oamA.setOamMep(oamMepA);
			oamZ.setOamMep(oamMepZ);
			OamInfo aoamInfo = oamService.queryByCondition(oamA, OamTypeEnum.AMEP);
			OamInfo zoamInfo = oamService.queryByCondition(oamZ, OamTypeEnum.ZMEP);
			if (aoamInfo.getOamMep() != null) {
				oamInfos.add(aoamInfo);
			}
			if (aoamInfo.getOamMep() != null) {
				oamInfos.add(zoamInfo);
			}
			view.getOamTable().initData(oamInfos);
			view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(oamService);
			UiUtil.closeService_MB(qosService);
			UiUtil.closeService_MB(tunnelService);
			UiUtil.closeService_MB(qosInfoService);
		}
	}

	@Override
	public void search() {
		new SearchSegmentDialog(this.view);
	}
}
