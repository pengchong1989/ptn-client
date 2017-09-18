﻿package com.nms.ui.ptn.statistics.poi;

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import javax.swing.JFileChooser;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.equipment.slot.SlotInst;
import com.nms.db.bean.path.Segment;
import com.nms.db.bean.report.PathStatisticsWidthRate;
import com.nms.db.bean.report.SSAlarm;
import com.nms.db.bean.report.SSCard;
import com.nms.db.bean.report.SSLabel;
import com.nms.db.bean.report.SSPort;
import com.nms.db.bean.system.OperationLog;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.path.SegmentService_MB;
import com.nms.model.system.OperationLogService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.statistics.xmlanalysis.ReadTableAttrsXml;

public class Export {
	/**
	 * 添加日志记录
	 */
	private static int operationType=0;

	/**
	 * add by stones
	 * **/
	public Workbook exportdata(Object infos, int s) throws Exception {
		Workbook wb =null;
		Sheet sheet1 = null;
		Row row0 = null;
		List headerName = null;
		SegmentService_MB segmentService=null;
		try{
			wb = new HSSFWorkbook();
			if(Services.SITE == s){
				segmentService=(SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
				operationType=EOperationLogType.SITESTATISTICSEXPORT.getValue();
				// 当表名是网元信息统计时，把导出excel的第一行设定为网元信息统计页面的表�?
				sheet1 = wb.createSheet(ResourceUtil.srcStr(StringKeysTab.TAB_SITEINFO));
				row0 = sheet1.createRow(0);
				List<SiteInst> siteinstlist = (List<SiteInst>) infos;
				SiteInst siteinst = null;
				// 读取xml文件，得到表�?
				headerName = ReadTableAttrsXml.readTableXml("siteinfoTable");
				// 设定excel表头
				for(int i = 0; i < headerName.size(); i ++) {
					row0.createCell(i).setCellValue(headerName.get(i).toString());
				}
				// 把网元统计页面数据写到excel
				if(infos!=null && siteinstlist.size()>0){
					for(int i = 0;i < siteinstlist.size();i++) {
						siteinst = siteinstlist.get(i);
						Row row = sheet1.createRow(i+1);
						row.createCell(0).setCellValue(i+1);
						row.createCell(1).setCellValue(siteinst.getCellId());
						row.createCell(2).setCellValue(siteinst.getCellType());
						row.createCell(3).setCellValue(siteinst.getVersions());
						row.createCell(4).setCellValue(siteinst.getCellDescribe());
						row.createCell(5).setCellValue(segmentService.selectBySite(siteinst.getSite_Inst_Id()).size());
						row.createCell(6).setCellValue(siteinst.getCreateTime());
					}				
				}
			}else if(Services.SEGMENT == s){
				operationType=EOperationLogType.SITEPHYCISEEXPORT.getValue();
				// 当表名是网元物理连接信息统计时，把导出excel的第一行设定为网元物理连接信息统计页面的表�?
				sheet1 = wb.createSheet(ResourceUtil.srcStr(StringKeysTab.TAB_SEGMENTNFO));
				row0 = sheet1.createRow(0);
				List<Segment> segmentlist = (List<Segment>) infos;
				Segment segment = null;
				// 读取xml文件，得到表�?
				headerName = ReadTableAttrsXml.readTableXml("segmentinfoTable");
				// 设定excel表头
				for(int i = 0; i < headerName.size(); i ++) {
					row0.createCell(i).setCellValue(headerName.get(i).toString());
				}
				// 把网元统计页面数据写到excel
				if(infos!=null && segmentlist.size()>0){
					for(int i = 0;i < segmentlist.size();i++) {
						segment = segmentlist.get(i);
						Row row = sheet1.createRow(i+1);
						row.createCell(0).setCellValue(i+1);
						row.createCell(1).setCellValue(segment.getNAME());
						row.createCell(2).setCellValue(segment.getBANDWIDTH());
						row.createCell(3).setCellValue(segment.getShowSiteAname());
						row.createCell(4).setCellValue(segment.getShowPortAname());
						row.createCell(5).setCellValue(segment.getShowSiteZname());
						row.createCell(6).setCellValue(segment.getShowPortZname());
					}				
				}			
			}else if(Services.SLOT == s){	
				operationType=EOperationLogType.SLOTPORT.getValue();
				// 当表名是槽位信息统计时，把导出excel的第一行设定为槽位信息统计页面的表�?
				sheet1 = wb.createSheet(ResourceUtil.srcStr(StringKeysTab.TAB_SLOTTNFO));
				row0 = sheet1.createRow(0);
				List<SlotInst> slotinstlist = (List<SlotInst>) infos;
				SlotInst slotinst = null;
				// 读取xml文件，得到表�?
				headerName = ReadTableAttrsXml.readTableXml("slotinfoTable");
				// 设定excel表头
				for(int i = 0; i < headerName.size(); i ++) {
					row0.createCell(i).setCellValue(headerName.get(i).toString());
				}
				// 把网元统计页面数据写到excel
				if(infos!=null && slotinstlist.size()>0){
					for(int i = 0;i < slotinstlist.size();i++) {
						slotinst = slotinstlist.get(i);
						Row row = sheet1.createRow(i+1);
						row.createCell(0).setCellValue(i+1);
						row.createCell(1).setCellValue(slotinst.getCellid());
						row.createCell(2).setCellValue(slotinst.getNumber());
						row.createCell(3).setCellValue(slotinst.getCardname());
					}
					
				}	
			}else if(Services.CARD == s){
				operationType=EOperationLogType.CARDEXPORT.getValue();
				sheet1 = wb.createSheet(ResourceUtil.srcStr(StringKeysTitle.TIT_CARD_INFO));
				row0 = sheet1.createRow(0);
				List<SSCard> list = (List<SSCard>) infos;
				SSCard ss = null;
				// 读取xml文件，得到表�?
				headerName = ReadTableAttrsXml.readTableXml("CardInfoPanel");
				// 设定excel表头
				for(int i = 0; i < headerName.size(); i ++) {
					row0.createCell(i).setCellValue(headerName.get(i).toString());
				}
				// 把网元统计页面数据写到excel
				if(infos!=null && list.size()>0){
					for(int i = 0;i < list.size();i++) {
						ss = list.get(i);
						Row row = sheet1.createRow(i+1);
						row.createCell(0).setCellValue(i+1);
						row.createCell(1).setCellValue(ss.getSiteName());
						row.createCell(2).setCellValue(ss.getCardId());
						row.createCell(3).setCellValue(ss.getCardType());
						row.createCell(4).setCellValue(ss.getVersion());
					}				
				}
			}else if(Services.LABELINFO == s){	
				operationType=EOperationLogType.LABELEXPORT.getValue();
				sheet1 = wb.createSheet(ResourceUtil.srcStr(StringKeysTitle.TIT_LABLE_INFO));
				row0 = sheet1.createRow(0);
				List<SSLabel> lists = (List<SSLabel>) infos;
				SSLabel ssla = null;
				// 读取xml文件，得到表�?
				headerName = ReadTableAttrsXml.readTableXml("LableInfoPanel");
				// 设定excel表头
				for(int i = 0; i < headerName.size(); i ++) {
					row0.createCell(i).setCellValue(headerName.get(i).toString());
				}
				// 把网元统计页面数据写到excel
				if(infos!=null && lists.size()>0){
					for(int i = 0;i < lists.size();i++) {
						ssla = lists.get(i);
						Row row = sheet1.createRow(i+1);
						row.createCell(0).setCellValue(i+1);
						row.createCell(1).setCellValue(ssla.getSiteName());
						row.createCell(2).setCellValue(ssla.getLspCount());
						row.createCell(3).setCellValue(ssla.getLspUsed());
						row.createCell(4).setCellValue(ssla.getLspCanUsed());
					}
				}		
			}else if(Services.STATISTICS == s){	
				//当表名是 告警统计  
				operationType=EOperationLogType.ALARMEXPORT.getValue();
				sheet1 = wb.createSheet(ResourceUtil.srcStr(StringKeysTitle.TIT_ALARM_INFO));
				row0 = sheet1.createRow(0);
				List<SSAlarm> listp = (List<SSAlarm>) infos;
				SSAlarm ssa = null;
				// 读取xml文件，得到表�?
				headerName = ReadTableAttrsXml.readTableXml("AlarmInfoPanel");
				// 设定excel表头
				for(int i = 0; i < headerName.size(); i ++) {
					row0.createCell(i).setCellValue(headerName.get(i).toString());
				}
				// 把网元统计页面数据写到excel
				if(infos!=null && listp.size()>0){
					for(int i = 0;i < listp.size();i++) {
						ssa = listp.get(i);
						Row row = sheet1.createRow(i+1);
						row.createCell(0).setCellValue(i+1);
						row.createCell(1).setCellValue(ssa.getName());
						row.createCell(2).setCellValue(ssa.getInstancy());
						row.createCell(3).setCellValue(ssa.getMostly());
						row.createCell(4).setCellValue(ssa.getSubordination());
						row.createCell(5).setCellValue(ssa.getClew());
						row.createCell(6).setCellValue(ssa.getUnknow());
						row.createCell(7).setCellValue(ssa.getAlarmCount());
					}
					
				}	
			}else if(Services.PORT == s){	
				operationType=EOperationLogType.PORTEXPORT.getValue();
				sheet1 = wb.createSheet(ResourceUtil.srcStr(StringKeysTitle.TIT_PORTINFO));
				row0 = sheet1.createRow(0);
				List<SSPort> listp = (List<SSPort>) infos;
				SSPort ssp = null;
				// 读取xml文件，得到表�?
				headerName = ReadTableAttrsXml.readTableXml("PortInfoPanel");
				// 设定excel表头
				for(int i = 0; i < headerName.size(); i ++) {
					row0.createCell(i).setCellValue(headerName.get(i).toString());
				}
				// 把网元统计页面数据写到excel
				if(infos!=null && listp.size()>0){
					for(int i = 0;i < listp.size();i++) {
						ssp = listp.get(i);
						Row row = sheet1.createRow(i+1);
						row.createCell(0).setCellValue(i+1);
						row.createCell(1).setCellValue(ssp.getSiteName());
						row.createCell(2).setCellValue(ssp.getNeType());
						row.createCell(3).setCellValue(ssp.getPortType());
						row.createCell(4).setCellValue(ssp.getPortCount());
						row.createCell(5).setCellValue(ssp.getPortUsed());
						row.createCell(6).setCellValue(ssp.getPortUnUsed());
						row.createCell(7).setCellValue(ssp.getUsedRate());
					}
					
				}	
			}else if(Services.Tunnel == s){
				operationType=EOperationLogType.SITEPHYPATHEXPORT.getValue();
				// 当表名是网元物理连接信息宽带统计时，把导出excel的第一行设定为网元物理连接信息统计页面的表�?
				sheet1 = wb.createSheet(ResourceUtil.srcStr(StringKeysTab.TAB_PATHTNFOWIDTH));
				row0 = sheet1.createRow(0);
				List<PathStatisticsWidthRate> pathlist = (List<PathStatisticsWidthRate>) infos;
				PathStatisticsWidthRate path = null;
				// 读取xml文件，得到表�?
				headerName = ReadTableAttrsXml.readTableXml("pathWidthTable");
				// 设定excel表头
				for(int i = 0; i < headerName.size(); i ++) {
					row0.createCell(i).setCellValue(headerName.get(i).toString());
				}
				// 把网元统计页面数据写到excel
				if(infos!=null && pathlist.size()>0){
					for(int i = 0;i < pathlist.size();i++) {
						path = pathlist.get(i);
						Row row = sheet1.createRow(i+1);
						row.createCell(0).setCellValue(i+1);
						row.createCell(1).setCellValue(path.getName());
						row.createCell(2).setCellValue(path.getQosType());
						row.createCell(3).setCellValue(path.getForWard_BE());
						row.createCell(4).setCellValue(path.getForWard_AF1());
						row.createCell(5).setCellValue(path.getForWard_AF2());
						row.createCell(6).setCellValue(path.getForWard_AF3());
						row.createCell(7).setCellValue(path.getForWard_AF4());
						row.createCell(8).setCellValue(path.getForWard_EF());
						row.createCell(9).setCellValue(path.getForWard_CS6());
						row.createCell(10).setCellValue(path.getForWard_CS7());
						row.createCell(11).setCellValue(path.getBackWard_BE());
						row.createCell(12).setCellValue(path.getBackWard_AF1());
						row.createCell(13).setCellValue(path.getBackWard_AF2());
						row.createCell(14).setCellValue(path.getBackWard_AF3());
						row.createCell(15).setCellValue(path.getBackWard_AF4());
						row.createCell(16).setCellValue(path.getBackWard_EF());
						row.createCell(17).setCellValue(path.getBackWard_CS6());
						row.createCell(18).setCellValue(path.getBackWard_CS7());
						
					
					}				
				}
			}
			//导出 性能统计
//			else if(Services.PerformanceTask == s){
//				operationType=EOperationLogType.EXPORTPERFORMANCE.getValue();
//				// 当表名是性能统计时，把导出excel的第一行设定为网元物理连接信息统计页面的表�?
//				sheet1 = wb.createSheet(ResourceUtil.srcStr(StringKeysTab.TAB_PATHTNFOWIDTH));
//				row0 = sheet1.createRow(0);
//				List<SSPerform> pathlist = (List<SSPerform>) infos;
//				SSPerform path = null;
//				// 读取xml文件，得到表�?
//			//	headerName = ReadTableAttrsXml.readTableXml("pathWidthTable");
//				// 设定excel表头
////				for(int i = 0; i < headerName.size(); i ++) {
////					row0.createCell(i).setCellValue(headerName.get(i).toString());
////				}
//				//for(int i = 0; i < 5; i ++) {
//					row0.createCell(0).setCellValue("序号");
//					row0.createCell(1).setCellValue("监控类型");
//					row0.createCell(2).setCellValue("性能值");
//					row0.createCell(3).setCellValue("监控时间");
//				//}
//				// 把网元统计页面数据写到excel
//				if(infos!=null && pathlist.size()>0){
//					for(int i = 0;i < pathlist.size();i++) {
//						path = pathlist.get(i);
//						Row row = sheet1.createRow(i+1);
//						row.createCell(0).setCellValue(i+1);
//						row.createCell(1).setCellValue(path.getPortName());
//						row.createCell(2).setCellValue(path.getPerformanceValue());
//						row.createCell(3).setCellValue(path.getPerformTime());
//					}				
//				}
//			}
		}catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(segmentService);
			sheet1 = null;
			row0 = null;
			headerName = null;
		}

		return wb;
	}
	
	public void savedate(Workbook wb,Component view) throws Exception {
		JFileChooser dlg = null; 
		int result = 0;  
		File saveFile=null;
		FileOutputStream flleout=null;
		OperationLogService_MB operationService=null;
		OperationLog operationLog=null;
		try{
			dlg = new JFileChooser();
			operationLog=new OperationLog();
			result = dlg.showSaveDialog(view);  
			if (result == JFileChooser.APPROVE_OPTION) { 
				 saveFile = dlg.getSelectedFile();
				String path = saveFile.toString();
				if(!saveFile.exists()) {
					path = saveFile.getAbsolutePath() + ".xls";
				}
				operationService=(OperationLogService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OPERATIONLOGSERVIECE);				
				operationLog.setStartTime(DateUtil.getDate(DateUtil.FULLTIME));
				 flleout = new FileOutputStream(path);	
				wb.write(flleout); 
				operationLog.setOverTime(DateUtil.getDate(DateUtil.FULLTIME));
				operationLog.setOperationResult(1);
				operationLog.setOperationType(operationType);
				operationLog.setUser_id(ConstantUtil.user.getUser_Id());
				operationService.insertOperationLog(operationLog);
				
			}
			
		}catch (Exception e) {
			throw e;
		}finally{
			if(flleout!=null){
				try {
				flleout.close();
				} catch (Exception e2) {
					throw e2;
				}finally{
					flleout = null;
				}
			}
			UiUtil.closeService_MB(operationService);
			operationType=0;
		}
	}
}
