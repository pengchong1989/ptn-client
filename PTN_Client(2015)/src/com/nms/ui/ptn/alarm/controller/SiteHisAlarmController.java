package com.nms.ui.ptn.alarm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.nms.db.bean.alarm.HisAlarmInfo;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.system.OperationLog;
import com.nms.db.enums.EObjectType;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.alarm.HisAlarmService_MB;
import com.nms.model.system.OperationLogService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.alarm.model.CurrentAlarmFilter;
import com.nms.ui.ptn.alarm.service.CSVUtil;
import com.nms.ui.ptn.alarm.view.SiteHisAlarmPanel;

/**
 * 历史告警方法处理类
 * 
 * @author wangwf
 * 
 */
public class SiteHisAlarmController {
	private SiteHisAlarmPanel view;
	private CurrentAlarmFilter filter;
	private List<HisAlarmInfo> hisAlarmInfos;

	public SiteHisAlarmController(SiteHisAlarmPanel view) {
		this.view = view;
	}

	/**
	 * 过滤查询
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private List<HisAlarmInfo> queryAlarmByFilter() throws Exception {
		// key为网元数据库id，value为槽位的集合
		Map<Integer, List<Integer>> siteId2SlotIds = null;
		HisAlarmService_MB service = null;
		List<HisAlarmInfo> hisAlarmInfos = new ArrayList<HisAlarmInfo>();
		try {
			service = (HisAlarmService_MB) ConstantUtil.serviceFactory.newService_MB(Services.HisAlarm);
			if (filter.getObjectType() != null && filter.getObjectType() == EObjectType.SITEINST) {
				List<Integer> siteIdList = new ArrayList<Integer>();
				for (SiteInst site : filter.getSiteInsts()) {
					siteIdList.add(Integer.valueOf(site.getSite_Inst_Id()));
				}
				hisAlarmInfos = service.queryHisBySites(siteIdList);
			} else if (filter.getObjectType() != null && filter.getObjectType() == EObjectType.SLOTINST) {
				siteId2SlotIds = new HashMap<Integer, List<Integer>>();
				// 封装网元与槽位的映射关系
				for (int i = 0; i < filter.getSlotInsts().size(); i++) {
					int siteId = filter.getSiteInsts().get(i).getSite_Inst_Id();
					if (siteId2SlotIds.get(siteId) == null) {
						siteId2SlotIds.put(siteId, new ArrayList<Integer>());
					}
					siteId2SlotIds.get(siteId).add(
							filter.getSlotInsts().get(i).getId());
				}
				for (Integer id : siteId2SlotIds.keySet()) {
					if (siteId2SlotIds.get(id) != null
							&& siteId2SlotIds.get(id).size() > 0) {
						List<HisAlarmInfo> hisAlarmInfoLists = service.queryHisBySlots(id, siteId2SlotIds.get(id));
						if (hisAlarmInfoLists != null
								&& hisAlarmInfoLists.size() > 0) {
							hisAlarmInfos.addAll(hisAlarmInfoLists);
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
			throw e;
		} finally {
			UiUtil.closeService_MB(service);
			siteId2SlotIds = null;
		}

		return hisAlarmInfos;
	}

	/**
	 * 刷新监听器处理方法
	 */
	public void refresh() {
		HisAlarmService_MB service = null;
		try {
			hisAlarmInfos = new ArrayList<HisAlarmInfo>();
			service = (HisAlarmService_MB) ConstantUtil.serviceFactory.newService_MB(Services.HisAlarm);
			// 根据过滤条件查询
			List<Integer> siteIdList = new ArrayList<Integer>();
			siteIdList.add(ConstantUtil.siteId);
			hisAlarmInfos = service.queryHisBySites(siteIdList);
			this.view.getBox().clear();
			// 将查询的数据，显示在前台
			this.view.initData(hisAlarmInfos);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
	}

	/**
	 *导出功能 
	 * @throws Exception
	 */
	public void export(){
		CSVUtil csvUtil=null;
		String[] s={};
		String path=null;
		OperationLog operationLog=null;
		OperationLogService_MB operationService=null;
		UiUtil uiUtil = new UiUtil();
		int comfirmResult = 0;
		String csvFilePath = "";
		try{
		csvUtil=new CSVUtil();
        operationLog=new OperationLog();
		operationLog.setOperationType(EOperationLogType.HISALARMEXPORT.getValue());
		if(ResourceUtil.language.equals("zh_CN")){
			path=csvUtil.showChooserWindow("save","选择文件",s);
		}else{
			path=csvUtil.showChooserWindow("save","Select File",s);
		}
		if(path != null && !"".equals(path)){
			csvFilePath = path + ".csv";
			if(uiUtil.isExistAlikeFileName(csvFilePath)){
				comfirmResult = DialogBoxUtil.confirmDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_FILEPATHHASEXIT));
				if(comfirmResult == 1){
					return ;
				}
			}
			operationLog.setStartTime(DateUtil.getDate(DateUtil.FULLTIME));	
			csvUtil.WriteHisCsv(csvFilePath,hisAlarmInfos,null);
			operationLog.setOverTime(DateUtil.getDate(DateUtil.FULLTIME));
			operationLog.setOperationResult(1);
			operationLog.setUser_id(ConstantUtil.user.getUser_Id());
			operationService=(OperationLogService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OPERATIONLOGSERVIECE);				
			operationService.insertOperationLog(operationLog);
		}
		}catch(Exception e){
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(operationService);
			uiUtil = null;
		}
	}
	
	public SiteHisAlarmPanel getView() {
		return view;
	}
}
