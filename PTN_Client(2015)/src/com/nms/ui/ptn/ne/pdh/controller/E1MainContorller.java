package com.nms.ui.ptn.ne.pdh.controller;

import java.util.List;

import com.nms.db.bean.equipment.port.E1Info;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.port.E1InfoService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.ptn.ne.pdh.view.E1MainPanel;
import com.nms.ui.ptn.ne.pdh.view.UpdateE1Dialog;

public class E1MainContorller extends AbstractController {

	private E1MainPanel panel;

	public E1MainContorller(E1MainPanel panel) {
		this.panel = panel;
	}

	@Override
	public void refresh() throws Exception {
		E1InfoService_MB e1InfoService = null;
		List<E1Info> e1InfoList = null;
		E1Info e1Info = new E1Info();
		try {
			e1InfoService = (E1InfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.E1Info);
			e1Info.setSiteId(ConstantUtil.siteId);
			e1InfoList = e1InfoService.selectByCondition(e1Info);
			this.panel.clear();
			this.panel.initData(e1InfoList);
			this.panel.updateUI();

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(e1InfoService);
			e1InfoList = null;
		}

	}

	@Override
	public void openUpdateDialog() {
		E1Info e1Info = this.panel.getSelect();
		new UpdateE1Dialog(e1Info, this.panel);
		
	}
	@Override
	public void synchro() {
		DispatchUtil eDispatch = null;
		try {
			eDispatch = new DispatchUtil(RmiKeys.RMI_E1);
			String result = eDispatch.synchro(ConstantUtil.siteId);
			DialogBoxUtil.succeedDialog(null, result);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			eDispatch = null;
		}
	}

}
