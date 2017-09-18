package com.nms.ui.ptn.alarm.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;

/**
 * 定时获取设备告警信息的任务
 * 
 * @author Administrator
 * 
 */
public class AlarmTimerTask implements ActionListener {
	private DispatchUtil service = null;
	public AlarmTimerTask() {
		try {
			service = new DispatchUtil(RmiKeys.RMI_ALARM);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		List<Integer> siteAddressList = null;
		try {
			siteAddressList = getAllSiteAddressList();
			service.executeQueryHisAlarmBySites(siteAddressList);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	/*
	 * 获取所有网元的数据库id
	 */
	public List<Integer> getAllSiteAddressList() throws Exception {
		List<Integer> siteList = new ArrayList<Integer>();
		List<SiteInst> sites = null;
		SiteService_MB services = null;
		try {
			services = (SiteService_MB) ConstantUtil.serviceFactory
					.newService_MB(Services.SITE);
			sites = services.select();
			if (sites != null) {
				for (SiteInst site : sites) {
					siteList.add(Integer.valueOf(site.getSite_Inst_Id()));
				}
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
			throw e;
		} finally {
			UiUtil.closeService_MB(services);
		}
		return siteList;
	}
}
