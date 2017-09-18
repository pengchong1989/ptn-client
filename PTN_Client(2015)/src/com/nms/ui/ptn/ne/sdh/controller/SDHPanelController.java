package com.nms.ui.ptn.ne.sdh.controller;

import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.equipment.port.PortStm;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.port.PortStmService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.ptn.ne.sdh.view.SDHPanel;
import com.nms.ui.ptn.ne.sdh.view.UpdateSDHDialog;

public class SDHPanelController extends AbstractController {

	
	private SDHPanel panel;

	public SDHPanelController(SDHPanel sdhpanel) {
		this.panel = sdhpanel;
	}

	@Override
	public void refresh() throws Exception {
		PortStmService_MB portstmservice = null;
		List<PortStm> portStms = null;
		try {
			portStms= new ArrayList<PortStm>();
			portstmservice = (PortStmService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORTSTM);

			portStms = portstmservice.queryBySiteid(ConstantUtil.siteId);

			this.panel.clear();
			this.panel.initData(portStms);
			this.panel.updateUI();

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(portstmservice);
			portStms = null;
		}
	}
	
	@Override
	public void openUpdateDialog(){
		PortStm portstm = this.panel.getSelect();
		UpdateSDHDialog updatesdhdialog=new UpdateSDHDialog(portstm ,panel);
	}
	
	@Override
	public void synchro() {
		DispatchUtil portStmDispatch = null;
		try {
			portStmDispatch = new DispatchUtil(RmiKeys.RMI_PORTSTM);
			portStmDispatch.synchro(ConstantUtil.siteId);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			portStmDispatch = null;
		}
	}
}
