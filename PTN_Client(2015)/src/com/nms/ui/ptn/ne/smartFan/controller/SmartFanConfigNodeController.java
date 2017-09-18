﻿package com.nms.ui.ptn.ne.smartFan.controller;

import java.util.List;

import com.nms.db.bean.ptn.SmartFan;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.ptn.SmartFanService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.ne.smartFan.view.SmartFanConfigNodePanel;
import com.nms.ui.ptn.ne.smartFan.view.dialog.SmartFanConfigDialog;

public class SmartFanConfigNodeController extends AbstractController {
	private SmartFanConfigNodePanel view;
	
	public SmartFanConfigNodeController(SmartFanConfigNodePanel smartFanConfigNodePanel) {
		this.view = smartFanConfigNodePanel;
	}

	/**
	 * 刷新界面数据
	 */
	@Override
	public void refresh() throws Exception {
		SmartFanService_MB fanService = null;
		try {
			fanService = (SmartFanService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SMARTFNASERVICE);
			List<SmartFan> fanList = fanService.selectAll(ConstantUtil.siteId);
			this.view.clear();
			this.view.initData(fanList);
			this.view.updateUI();
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(fanService);
		}
	}
	
	/**
	 * 修改风扇
	 */
	@Override
	public void openUpdateDialog(){
		if (this.view.getAllSelect().size() != 1) {
			DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
		}else {
			try {
				new SmartFanConfigDialog(this.view.getSelect());
				this.refresh();
			} catch (Exception e) {
				ExceptionManage.dispose(e,this.getClass());
			}
		}	
	}
	
	@Override
	public void synchro(){
		try {
			DispatchUtil dispatch = new DispatchUtil(RmiKeys.RMI_SMARTFAN);
			String result = dispatch.synchro(ConstantUtil.siteId);
			AddOperateLog.insertOperLog(null, EOperationLogType.SMARTFANCONFIG.getValue(), result, 
					null, null, ConstantUtil.siteId, ResourceUtil.srcStr(StringKeysTab.TAB_SMARTFAN), null);
			DialogBoxUtil.succeedDialog(this.view, result);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
}