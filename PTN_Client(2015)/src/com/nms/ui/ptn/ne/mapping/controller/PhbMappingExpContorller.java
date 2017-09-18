package com.nms.ui.ptn.ne.mapping.controller;

import java.util.ArrayList;
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
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.ne.mapping.view.PhbMappingExpInfoPanel;
import com.nms.ui.ptn.ne.mapping.view.UpdateMappingDialog;

public class PhbMappingExpContorller extends AbstractController {

	private PhbMappingExpInfoPanel mappingPanel;
//	private QosMappingMode qosMapping = null;
	
	public PhbMappingExpContorller(PhbMappingExpInfoPanel phbMappingExpInfoPanel) {
		this.mappingPanel = phbMappingExpInfoPanel;
	}


	@Override
	public void refresh() throws Exception {
		QosMappingModeService_MB mappingService = null;
		List<QosMappingMode> InfoList = new ArrayList<QosMappingMode>();
		QosMappingMode qosMapping = null;
		try {
			mappingService = (QosMappingModeService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosMappingModeService);
			qosMapping = new QosMappingMode();
			qosMapping.setSiteId(ConstantUtil.siteId);
			qosMapping.setTypeName("PHB_EXP");
			InfoList = mappingService.queryByCondition(qosMapping);
			this.mappingPanel.clear();
			this.mappingPanel.initData(InfoList);
			this.mappingPanel.updateUI();

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(mappingService);
			InfoList = null;
			qosMapping = null;
		}
	}
	
	@Override
	public void openUpdateDialog()throws Exception {
		if (this.mappingPanel.getAllSelect().size() == 1) {
			try {
				QosMappingMode m = this.mappingPanel.getSelect();
				new UpdateMappingDialog(m, this.mappingPanel);
			} catch (Exception e) {
				ExceptionManage.dispose(e,this.getClass());
			}
		}else{
			DialogBoxUtil.errorDialog(this.mappingPanel, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
		}
	}
	
	@Override
	public void synchro() {
		DispatchUtil phbDispatch=null;
		try {

			phbDispatch = new DispatchUtil(RmiKeys.RMI_PHBMAPPINGEXP);
			String result = phbDispatch.synchro(ConstantUtil.siteId);
			DialogBoxUtil.succeedDialog(null, result);
			AddOperateLog.insertOperLog(null, EOperationLogType.SYNCPHB.getValue(), result,
					null, null, ConstantUtil.siteId, "PHB_EXP", null);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
		}
	}

}
