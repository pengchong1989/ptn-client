﻿package com.nms.ui.ptn.alarm.controller;

import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.system.OperationLog;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.alarm.CurAlarmService_MB;
import com.nms.model.system.OperationLogService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.SiteUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.db.bean.alarm.CurrentAlarmInfo;
import com.nms.ui.ptn.alarm.service.CSVUtil;
import com.nms.ui.ptn.alarm.view.SiteCurrentAlarmPanel;

/**
 * 当前告警事件处理类
 * 
 * @author wangwf
 * 
 */
public class SiteCurrentAlarmController {
	private SiteCurrentAlarmPanel view;
	private List<CurrentAlarmInfo> currInfos;

	public SiteCurrentAlarmController(SiteCurrentAlarmPanel view) {
		this.view = view;
		refresh();
	}

	/**
	 * 刷新按钮事件处理方法 先设置过滤条件后，才能显示刷新结果
	 */
	public void refresh() {
		CurAlarmService_MB service = null;
		List<Integer> siteIdList = null;
		try {
			currInfos = new ArrayList<CurrentAlarmInfo>();
			service = (CurAlarmService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CurrentAlarm);
			siteIdList = new ArrayList<Integer>();
			siteIdList.add(ConstantUtil.siteId);
			currInfos = service.queryCurBySites(siteIdList);
			
			this.view.getBox().clear();
			this.view.initData(currInfos);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
			siteIdList = null;
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
				csvUtil.WriteCsv(csvFilePath,currInfos);
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
		}
	}
	
	/**
	 * 同步设备当前告警
	 */
	public void synchro(){
		try {
			DispatchUtil alarmOperationImpl = new DispatchUtil(RmiKeys.RMI_ALARM);
			SiteUtil siteUtil = new SiteUtil();
			if(siteUtil.isNeOnLine(ConstantUtil.siteId))
			{
				alarmOperationImpl.synchroCurrentAlarm(ConstantUtil.siteId);
				DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
			}
			else
			{
				DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_UP_DOWN));
			}
			
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	/**
	 * 设置试图
	 * @return
	 */
	public SiteCurrentAlarmPanel getView() {
		return view;
	}
}
