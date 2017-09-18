package com.nms.ui.ptn.ne.exp.controller;

import java.util.List;

import com.nms.db.bean.ptn.qos.QosMappingMode;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.ptn.qos.QosMappingModeService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.ptn.ne.exp.view.ExpMainPanel;
import com.nms.ui.ptn.ne.exp.view.UpdateExpDialog;

public class ExpMainContorller extends AbstractController{

	private ExpMainPanel expMainPanel;
	public ExpMainContorller(ExpMainPanel expMainPanel) {
		this.expMainPanel=expMainPanel;
	}

	@Override
	public void refresh() throws Exception {
		QosMappingModeService_MB qosMappingModeService = null;
		List<QosMappingMode> qosMappingList=null;
		QosMappingMode qosMappingMode=new QosMappingMode();
		try {
			qosMappingModeService = (QosMappingModeService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosMappingModeService);
			qosMappingMode.setSiteId(ConstantUtil.siteId);
			qosMappingMode.setTypeName("EXP_PHB");
			qosMappingList=qosMappingModeService.queryByCondition(qosMappingMode);
			this.expMainPanel.clear();
			this.expMainPanel.initData(qosMappingList);
			this.expMainPanel.updateUI();

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(qosMappingModeService);
		}

	}

	@Override
	public void openUpdateDialog() throws Exception{
		QosMappingMode qosMappingMode=this.expMainPanel.getSelect();
		new UpdateExpDialog(qosMappingMode,expMainPanel);
		
	}
	@Override
	public void synchro() {
		DispatchUtil eDispatch = null;
		try {
			eDispatch = new DispatchUtil(RmiKeys.RMI_EXPMAPPINGPHB);
			String result = eDispatch.synchro(ConstantUtil.siteId);
			DialogBoxUtil.succeedDialog(null, result);
			//添加日志记录
			AddOperateLog.insertOperLog(null, EOperationLogType.SYNCEXP.getValue(), result,
					null, null, ConstantUtil.siteId, "EXP_PHB", null);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			eDispatch = null;
		}
	}

}
