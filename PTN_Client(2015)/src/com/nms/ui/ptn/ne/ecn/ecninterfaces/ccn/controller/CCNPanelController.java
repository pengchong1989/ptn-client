package com.nms.ui.ptn.ne.ecn.ecninterfaces.ccn.controller;

import java.util.List;

import com.nms.db.bean.ptn.ecn.CCN;
import com.nms.model.ptn.ecn.CCNService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.ne.ecn.ecninterfaces.ccn.view.CCNPanel;
import com.nms.ui.ptn.ne.ecn.ecninterfaces.ccn.view.SaveCCNDialog;

public class CCNPanelController extends AbstractController {

	private CCNPanel panel;

	public CCNPanelController(CCNPanel ccnPanel) {
		this.panel = ccnPanel;
	}

	@Override
	public void refresh() throws Exception {
//		CCNCXServiceImpl ccnXcServiceImpl = null;
		List<CCN> ccnList = null;
		CCN ccn = null;
		CCNService_MB ccnService=null;
		try {
			ccn = new CCN();
			ccnService = (CCNService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CCN);
			ccnList=ccnService.queryByNeID(ConstantUtil.siteId+"");
			this.panel.clear();
			this.panel.initData(ccnList);
			this.panel.updateUI();

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(ccnService);
			ccnList = null;
		}
	}

	@Override
	public void openCreateDialog() {
		SaveCCNDialog createdialog = new SaveCCNDialog(null, panel);
	}

	@Override
	public void openUpdateDialog() throws Exception {
		CCN ccn = this.panel.getSelect();
		SaveCCNDialog updatedialog = new SaveCCNDialog(ccn, panel);
	}

	@Override
	public void delete() throws Exception {
		DispatchUtil ccnDispatch = new DispatchUtil(RmiKeys.RMI_CCN);
		String message = "";
		try {
			int result = DialogBoxUtil.confirmDialog(this.panel, ResourceUtil.srcStr(StringKeysTip.TIP_IS_DELETE));
			if (result == 0) {
				List<CCN> ccn = this.panel.getAllSelect();
				message = ccnDispatch.excuteDelete(ccn);

				DialogBoxUtil.succeedDialog(this.panel, message);
				refresh();
			}
		} catch (Exception e) {
			DialogBoxUtil.errorDialog(this.panel, e.getMessage());
			throw e;
		} finally {
			ccnDispatch = null;

		}
	}

	@Override
	public void synchro() throws Exception {
		DispatchUtil ccnDispatch = new DispatchUtil(RmiKeys.RMI_CCN);
		try {
			ccnDispatch.synchro(ConstantUtil.siteId);
			this.refresh();
		} catch (Exception e) {
			throw e;
		}
	}
}
