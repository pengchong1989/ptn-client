﻿package com.nms.ui.ptn.performance.controller;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import com.nms.db.bean.alarm.CurrentAlarmInfo;
import com.nms.db.bean.alarm.WarningLevel;
import com.nms.db.bean.system.UnLoading;
import com.nms.db.enums.EObjectType;
import com.nms.model.alarm.CurAlarmService_MB;
import com.nms.model.system.PerformanceRamService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.ServerConstant;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.ptn.performance.model.PerformanceRAMInfo;
import com.nms.ui.ptn.systemManage.ReadUnloadXML;

public class PerformanceRamTimerTask extends TimerTask{
	
	@Override
	public void run() {
		dispathPerformRam();
	}
	
	private void dispathPerformRam(){
		
	  PerformanceRamService_MB performanceRamServiceMB = null;
	  PerformanceRAMInfo performanceRAMInfo = null;
      String filePath = getFilePath();
      File file = new File(filePath);
      long time = 0l;
		try {
			performanceRamServiceMB = (PerformanceRamService_MB)ConstantUtil.serviceFactory.newService_MB(Services.PERFORMANCERAM);
			performanceRAMInfo = performanceRamServiceMB.select(ConstantUtil.user.getUser_Name());
			time = fileNameTime(file);
			if(performanceRAMInfo.getTimeValue() != null && !performanceRAMInfo.getTimeValue().equals("")){
				//是否超过设置的天数
				if(time >0){
					if(System.currentTimeMillis() - time > Long.parseLong(performanceRAMInfo.getTimeValue())*60*60*1000 ){
						alarmPerformance(1002,ResourceUtil.srcStr(StringKeysLbl.LBL_PERFORMANCETIME),"PERFORMANCETIME",207);
					}
				}
			}
			if(performanceRAMInfo.getRamValue() !=null && !performanceRAMInfo.getRamValue().equals("")){
				//是否超过设置的内存大小
				if(Double.parseDouble(performanceRAMInfo.getRamValue())< getDirSize(file) )
				alarmPerformance(1001,ResourceUtil.srcStr(StringKeysLbl.LBL_PERFORMANCERAM),"PERFORMANCERAMERROR",206);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(performanceRamServiceMB);
		}
		
	}
	
	/**
	 * 获取性能存储文件目录地址
	 * @return
	 */
	private String getFilePath(){
		String filePath = "";
		try {
			List<UnLoading> unloadList = ReadUnloadXML.selectUnloadXML();
			if(unloadList != null && unloadList.size()>0){
				for(UnLoading unLoading : unloadList){
					if(unLoading.getUnloadType() == 2){
						filePath = unLoading.getFileWay();
					}
				}
			}
         if(filePath.equals("")){
        	 filePath = ServerConstant.AUTODATABACKPM_FILE;
          }
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return filePath;
	}
	
	
	/**
	 * 用于判断一个文件所占用的内存大小
	 * @param file
	 * @return
	 */
	private double getDirSize(File file) {   
		try {
			//判断文件是否存在     
			if (file.exists()) {     
				//如果是目录则递归计算其内容的总大小    
				if (file.isDirectory()) {     
					File[] children = file.listFiles();     
					double size = 0; 
					for (File f : children)     
						size += getDirSize(f);     
					return size;     
				} else {//如果是文件则直接返回其大小,以“兆”为单位   
					double size = (double) file.length() / 1024 / 1024;        
					return size;     
				}     
			}    
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return 0.0;
    }
	
	
	
	private long fileNameTime(File file){
		String fileName = "";
		long times = 0l;
		try {
			if (file.exists()) {     
				//如果是目录则递归计算其内容的总大小    
				if (file.isDirectory()) {
					File[] children = file.listFiles(); 
					if(children.length >0){
						for(File f : children){
							fileName = f.getName();
							if(fileName.contains("_")){
								times = DateUtil.updateTimeToLong(fileName.split("_")[1].split("\\.")[0],"yyyyMMddHHmmss");
								break;
							}
						}
					}
				}     
			} 
		} catch (Exception e) {
		}
		return times;
	}
	
	
	/***
	 * 
	 * 
	 * @param curAlarmService
	 */
	
	private void alarmPerformance(int codeValue,String alarmDesc,String warningName,int id){
		CurAlarmService_MB alarmServiceMB = null;
		try {
			alarmServiceMB = (CurAlarmService_MB)ConstantUtil.serviceFactory.newService_MB(Services.CurrentAlarm);
			CurrentAlarmInfo losAlarm = null;
			losAlarm = new CurrentAlarmInfo();
			losAlarm.setAlarmCode(codeValue);
			losAlarm.setAlarmDesc(alarmDesc);
			losAlarm.setAlarmLevel(2);
			losAlarm.setObjectName("EMS服务器_"+warningName);
			losAlarm.setObjectType(EObjectType.EMSCLIENT);
			losAlarm.setWarningLevel_temp(2);
			WarningLevel level = new WarningLevel();
			level.setId(id);
			level.setManufacturer(1);
			level.setWarningcode(codeValue);
			level.setWarningdescribe(alarmDesc);
			level.setWarninglevel(2);
			level.setWarninglevel_temp(2);
			level.setWarningname(warningName);
			level.setWarningnote(alarmDesc);
			losAlarm.setWarningLevel(level);
			losAlarm.setRaisedTime(new Date());
			alarmServiceMB.saveOrUpdate(losAlarm);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(alarmServiceMB);
		}
	}
}
