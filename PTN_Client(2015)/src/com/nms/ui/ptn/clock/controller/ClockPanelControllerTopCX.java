package com.nms.ui.ptn.clock.controller;

import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.ptn.clock.PortConfigInfo;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.ptn.clock.PortDispositionInfoService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.ptn.clock.view.cx.dialog.TCCreateDialog;
import com.nms.ui.ptn.clock.view.cx.time.TabPanelTwoTCX;

public class ClockPanelControllerTopCX extends AbstractController{

	private TabPanelTwoTCX tabPanelTwoTCX = null;
	private List<PortConfigInfo> list = null;

	public ClockPanelControllerTopCX(TabPanelTwoTCX tabPanelTwoTCX) {
		this.tabPanelTwoTCX = tabPanelTwoTCX;
	}
	
	
	/**
	 * <p>
	 * 点击创建按钮弹出对话框
	 * </p>
	 */
	@Override
	public void openCreateDialog() throws Exception {
		try {
			
			new TCCreateDialog(tabPanelTwoTCX,null);
		} catch (Exception e) {

			throw e;
		}
	};

	/**
	 * <p>
	 * 数据修改
	 * </p>
	 */
	@Override
	public void openUpdateDialog() throws Exception {

		PortConfigInfo portConfigInfo = null;
		try {

			portConfigInfo = new PortConfigInfo();
			portConfigInfo=tabPanelTwoTCX.getSelect();
			new TCCreateDialog(tabPanelTwoTCX,portConfigInfo);
		} catch (Exception e) {
			throw e;
		}finally{
			portConfigInfo = null;
		}
	}

	/**
	 * <p>
	 * 数据删除
	 * </p>
	 */
	@Override
	public void delete() throws Exception {
		DispatchUtil timePortConfigDispatch;
		List<PortConfigInfo> portConfigInfoList=null;
		try {
			timePortConfigDispatch = new DispatchUtil(RmiKeys.RMI_TIMEPORTCONFIG);
			portConfigInfoList=new ArrayList<PortConfigInfo>();
			portConfigInfoList=tabPanelTwoTCX.getAllSelect();
			String result=timePortConfigDispatch.excuteDelete(portConfigInfoList);
			//添加日志记录
//			AddOperateLog.insertOperLog(  this.tabPanelTwoTCX.getDeleteButton(), EOperationLogType.PORTDELETE.getValue(), result);
			DialogBoxUtil.succeedDialog(null, result);
			this.refresh();
		} catch (Exception e) {
			throw e;
		}finally{
			timePortConfigDispatch = null;
			portConfigInfoList = null;
		}
	}

	/**
	 * <p>
	 * 加载页面数据
	 * </p>
	 */
	@Override
	public void refresh() throws Exception {
		List<PortConfigInfo> list = null;
		PortDispositionInfoService_MB portDispositionInfoService=null;
		try {
			portDispositionInfoService=(PortDispositionInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PortDispositionInfoService);
			list = new ArrayList<PortConfigInfo>();
			list=portDispositionInfoService.select(ConstantUtil.siteId);
			this.tabPanelTwoTCX.clear();
			this.tabPanelTwoTCX.initData(list);
			this.tabPanelTwoTCX.updateUI();
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(portDispositionInfoService);
		}
	}

	/**
	 * <p>
	 * 同步功能
	 * </p>
	 */
	@Override
	public void synchro() throws Exception {
		DispatchUtil timePortConfigDispatch;
		try{
			timePortConfigDispatch = new DispatchUtil(RmiKeys.RMI_TIMEPORTCONFIG);
			//TimePortConfigDispatch TimePortConfigDispatch=new TimePortConfigDispatch();
			timePortConfigDispatch.synchro(ConstantUtil.siteId);
			//添加日志记录
			PtnButton deleteButton=(PtnButton) this.tabPanelTwoTCX.getSynchroButton();
			deleteButton.setOperateKey(EOperationLogType.PORTSYNCHRO.getValue());
			deleteButton.setResult(1);
			this.refresh();
		} catch (Exception e) {

			throw e;
		}
	}

	public List<PortConfigInfo> getList() {
		return list;
	}

	public void setList(List<PortConfigInfo> list) {
		this.list = list;
	}
	
}
