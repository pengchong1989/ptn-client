package com.nms.ui.ptn.ne.pri.controller;

import java.util.List;

import com.nms.db.bean.ptn.qos.QosMappingMode;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.ptn.qos.QosMappingModeService_MB;
import com.nms.model.util.Services;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.ptn.ne.pri.view.PriMainPanel;
import com.nms.ui.ptn.ne.pri.view.UpdatePriDialog;

public class PriMainContorller extends AbstractController{

	private PriMainPanel priMainPanel;
	
	public PriMainContorller(PriMainPanel priMainPanel) {
		this.priMainPanel=priMainPanel;
	}

	@Override
	public void refresh() throws Exception {
		QosMappingModeService_MB qosMappingModeService = null;
		List<QosMappingMode> qosMappingList=null;
		QosMappingMode qosMappingMode=new QosMappingMode();
		try {
			qosMappingModeService = (QosMappingModeService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosMappingModeService);
			qosMappingMode.setSiteId(ConstantUtil.siteId);
			qosMappingMode.setTypeName("PHB_PRI");
			qosMappingList=qosMappingModeService.queryByCondition(qosMappingMode);
			this.priMainPanel.clear();
			this.priMainPanel.initData(qosMappingList);
			this.priMainPanel.updateUI();

		} catch (Exception e) {
			throw e;
		} finally {
			 qosMappingList = null;
			 qosMappingMode = null;
			UiUtil.closeService_MB(qosMappingModeService);
		}

	}

	@Override
	public void openUpdateDialog() throws Exception{
		QosMappingMode qosMappingMode=this.priMainPanel.getSelect();
		new UpdatePriDialog(qosMappingMode,priMainPanel);
		
	}
	@Override
	public void synchro() {
//		DispatchUtil eDispatch = null;
		try {
//			eDispatch = new DispatchUtil(RmiKeys.RMI_E1);
//			eDispatch.synchro(ConstantUtil.siteId);
			DialogBoxUtil.succeedDialog(priMainPanel,ResultString.CONFIG_SUCCESS);
			AddOperateLog.insertOperLog(null, EOperationLogType.PRIMANAGER.getValue(), ResultString.CONFIG_SUCCESS, 
					null, null, ConstantUtil.siteId, "PHB_PRI", null);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
//			eDispatch = null;
		}
	}

}
