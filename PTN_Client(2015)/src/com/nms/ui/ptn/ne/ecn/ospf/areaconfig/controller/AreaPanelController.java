package com.nms.ui.ptn.ne.ecn.ospf.areaconfig.controller;

import java.util.List;

import com.nms.db.bean.ptn.ecn.OSPFAREAInfo;
import com.nms.model.ptn.ecn.OSPFAREAService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.ne.ecn.ospf.areaconfig.view.AreaConfigPanel;
import com.nms.ui.ptn.ne.ecn.ospf.areaconfig.view.SaveAreaDialog;

public class AreaPanelController extends AbstractController {

	private AreaConfigPanel panel;

	public AreaPanelController(AreaConfigPanel areaConfigPanel) {
		this.panel = areaConfigPanel;
	}

	@Override
	public void refresh() throws Exception {
		List<OSPFAREAInfo> oSPFAREAInfoList = null;
		OSPFAREAService_MB oSPFAREAService = null;
		try {
			oSPFAREAService = (OSPFAREAService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OSPFAREA);
			oSPFAREAInfoList = null;
			oSPFAREAInfoList = oSPFAREAService.queryByNeID(ConstantUtil.siteId);

			this.panel.clear();
			this.panel.initData(oSPFAREAInfoList);
			this.panel.updateUI();

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(oSPFAREAService);
			oSPFAREAInfoList = null;
		}
	}

	@Override
	public void openCreateDialog() {
		OSPFAREAInfo OSPFAREAInfo = this.panel.getSelect();
		SaveAreaDialog updatesdhdialog = new SaveAreaDialog(null, panel);
	}

	@Override
	public void openUpdateDialog() throws Exception {
		OSPFAREAInfo OSPFAREAInfo = this.panel.getSelect();
		SaveAreaDialog updatesdhdialog = new SaveAreaDialog(OSPFAREAInfo, panel);
	}

	@Override
	public void delete() throws Exception {
		// System.out.println("删除");
		DispatchUtil OSPFAREADispatch = null;
		try {
			List<OSPFAREAInfo> OSPFAREAInfo = this.panel.getAllSelect();
			OSPFAREADispatch = new DispatchUtil(RmiKeys.RMI_OSPFAREA);
			OSPFAREADispatch.excuteDelete(OSPFAREAInfo);

			DialogBoxUtil.succeedDialog(this.panel, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
			refresh();
		} catch (Exception e) {
			DialogBoxUtil.errorDialog(this.panel, e.getMessage());
			throw e;
		} finally {
			OSPFAREADispatch = null;

		}

	}

	@Override
	public void synchro() {
		DispatchUtil ospfAREADispatch = null;
	
		try {
			ospfAREADispatch = new DispatchUtil(RmiKeys.RMI_OSPFAREA);
			ospfAREADispatch.synchro(ConstantUtil.siteId);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			ospfAREADispatch = null;
		
		}
	}

}
