package com.nms.ui.ptn.statistics.prot;

import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.bean.ptn.port.Acbuffer;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.port.AcPortInfoService_MB;
import com.nms.model.util.ExportExcel;
import com.nms.model.util.Services;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ListingFilter;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;


public class LspPortController extends AbstractController{

	private LspPortPanel view;
	
	public LspPortController(LspPortPanel view){
		this.view = view;
	}
	@Override
	public void refresh() throws Exception {
		this.searchAndrefreshdata();
		
	}
	//导出统计数据保存到excel
	@Override
	public void export() throws Exception {
		List<PortInst> infos = null;
		String result=null;
		ExportExcel export=null;
		// 得到页面信息
		try {
			infos =  this.view.getTable().getAllElement();
			export=new ExportExcel();
			//得到bean的集合转为  String[]的List
			List<String[]> beanData=export.tranListString(infos,"cirCount");
			//导出页面的信息-Excel
			result=export.exportExcel(beanData, "cirCount");
			//添加操作日志记录
			this.insertOpeLog(EOperationLogType.LSPPORTEXPORT.getValue(),ResultString.CONFIG_SUCCESS, null, null);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			infos = null;
			result=null;
			export=null;
		}
	}
	
	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		AddOperateLog.insertOperLog(null, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysMenu.TAB_LINKCOUNT_T),"");		
	}
	// 页面初始化数据、点击刷新按钮刷新数据
	private void searchAndrefreshdata() {
		List<PortInst> infos = null;
		ListingFilter filter=null;
		SiteService_MB siteService = null;
		PortService_MB portService = null;
		List<SiteInst> siteList = null;
		List<PortInst> portList = null;
		List<PortInst> portListAll = null;
		PortInst portInst = null;
		AcPortInfoService_MB acInfoService = null;
		try {
			acInfoService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			siteService = (SiteService_MB)ConstantUtil.serviceFactory.newService_MB(Services.SITE);
		    portService = (PortService_MB)ConstantUtil.serviceFactory.newService_MB(Services.PORT);
		    
		    filter=new ListingFilter();
		    portListAll = new ArrayList<PortInst>();
		    siteList = siteService.select();
		    if(siteList != null && siteList.size()>0){
		     for(SiteInst siteInfo : siteList){
		    	portInst =	new PortInst();
				portInst.setSiteId(siteInfo.getSite_Inst_Id());
				portList = portService.select(portInst);
				if(portList != null && portList.size()>0){
				for(PortInst portInfo : portList){
					if(portInfo.getPortType().equals("UNI")){
						getAcPortInfoByPort(portInfo,acInfoService);
						portInfo.setCirCount(setPortRate(portInfo));
						portListAll.add(portInfo);
					}
				}
			  }
		     }
		   }
			infos = (List<PortInst>) filter.filterList(portListAll);
			this.view.clear();
			this.view.initData(infos);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally {
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(portService);
			UiUtil.closeService_MB(acInfoService);
			filter=null;
			siteList = null;
			portList = null;
			portListAll = null;
			portInst = null;
		}
	}
	public LspPortPanel getView() {
		return view;
	}
	public void setView(LspPortPanel view) {
		this.view = view;
	}
	/**
	 * 通过端口号查找ac
	 * @return
	 */
	private void getAcPortInfoByPort(PortInst portInst,AcPortInfoService_MB acInfoService) {
		AcPortInfo acportInfo = null;
		int cirCount = 0;
		List<Acbuffer> bufferList = null;
		try {
			acportInfo = new AcPortInfo();
			acportInfo.setPortId(portInst.getPortId());
			bufferList = new ArrayList<Acbuffer>();
			List<AcPortInfo> acPortInfoList = acInfoService.queryByAcPortInfo(acportInfo);
			if(acPortInfoList != null && acPortInfoList.size()>0){
				for(AcPortInfo acportInst : acPortInfoList){
//					bufferList = acportInst.getBufferList();
					//QOS业务的线路根据ac中简单QoS的cir来判断
					cirCount += acportInst.getSimpleQos().getCir();
//					if(bufferList!= null && bufferList.size()>0){
//						for(Acbuffer acbuffer : bufferList){
//							cirCount += acbuffer.getCir();
//						}
//					}
				}
				portInst.setIsOccupy(1);
			}
			portInst.setUseCirBandwidth(cirCount);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally{
			 acportInfo = null;
			 bufferList = null;
		}
	}
	/**
	 * @param portInst
	 *            端口信息
	 * @return 端口的速率
	 */
	private int setPortRate(PortInst portInst){
		
		/**
		 * 自动协商/1000M全双工/100M全双工/10M全双工 fe 端口的 “自动协商”相当于 “100M” ge 端口的 “自动协商”相当于 “1000M” xg 端口的 “自动协商”相当于 “10000M” fx 端口的 “自动协商”相当于 “1000M”
		 */
		Code portWorkModel = null;
		try {
			portWorkModel = UiUtil.getCodeById(portInst.getPortAttr().getWorkModel() == 0 ? 70 : portInst.getPortAttr().getWorkModel());
			// 端口为"自动协商"且 端口属于”fe“类型的
			if(portWorkModel.getCodeName().contains("半双工")){
				portWorkModel.setCodeName(ResourceUtil.srcStr(StringKeysLbl.LBLZIDONG_NAME));
			}
			if (portInst.getPortName().contains("fe") || portInst.getPortName().contains("ge") || portInst.getPortName().contains("xg") || portInst.getPortName().contains("fx")) {
				if (portWorkModel.getCodeName().equals(ResourceUtil.srcStr(StringKeysLbl.LBLZIDONG_NAME)) && portInst.getPortName().contains("fe")) {
					return 100*1024;
				}
				if (portWorkModel.getCodeName().equals(ResourceUtil.srcStr(StringKeysLbl.LBLZIDONG_NAME)) && portInst.getPortName().contains("ge")) {
					return 1000*1024;
				}
				if (portWorkModel.getCodeName().equals(ResourceUtil.srcStr(StringKeysLbl.LBLZIDONG_NAME)) && portInst.getPortName().contains("xg")) {
					return 10000*1024;
				}
				if (portWorkModel.getCodeName().equals(ResourceUtil.srcStr(StringKeysLbl.LBLZIDONG_NAME)) && portInst.getPortName().contains("fx")) {
					return 1000*1024;
				} else {
					return 0;
				}
			} else {
				return 0;
			}
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			portWorkModel = null;
		}
		return 0;
	}
}
