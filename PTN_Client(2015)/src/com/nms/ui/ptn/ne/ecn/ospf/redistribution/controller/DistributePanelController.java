package com.nms.ui.ptn.ne.ecn.ospf.redistribution.controller;

import java.util.List;

import com.nms.db.bean.ptn.ecn.OspfRedistribute;
import com.nms.model.ptn.ecn.RedistributeService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.ptn.ne.ecn.ospf.redistribution.view.DistributePanel;
import com.nms.ui.ptn.ne.ecn.ospf.redistribution.view.SaveDistributeDialog;

public class DistributePanelController extends AbstractController {

	private DistributePanel panel;

	public DistributePanelController(DistributePanel distributePanel) {
		this.panel = distributePanel;
	}

	@Override
	public void refresh() throws Exception {
		List<OspfRedistribute> ospfRedistributeList = null;
		RedistributeService_MB redistributeService = null;
		try {
			redistributeService = (RedistributeService_MB) ConstantUtil.serviceFactory.newService_MB(Services.REDISTRIBUTE);
			ospfRedistributeList = redistributeService.queryByNeID(ConstantUtil.siteId);
			this.panel.clear();
			this.panel.initData(ospfRedistributeList);
			this.panel.updateUI();

		} catch (Exception e) {
			throw e;
		} finally {
			ospfRedistributeList = null;
			UiUtil.closeService_MB(redistributeService);
		}
	}

	@Override
	public void openCreateDialog() throws Exception {
		SaveDistributeDialog updatedialog = new SaveDistributeDialog(null, panel);
	}

	@Override
	public void openUpdateDialog() throws Exception {
		OspfRedistribute ospfRedistribute = this.panel.getSelect();
		SaveDistributeDialog updatedialog = new SaveDistributeDialog(ospfRedistribute, panel);
	}

	@Override
	public void delete() throws Exception {
		// System.out.println("删除");
		DispatchUtil redistributeDispatch = null;
		String result="";
		try {
			List<OspfRedistribute> ospfRedistribute = this.panel.getAllSelect();
			redistributeDispatch = new DispatchUtil(RmiKeys.RMI_REDISTRIBUTE);
			 result=redistributeDispatch.excuteDelete(ospfRedistribute);			
			DialogBoxUtil.succeedDialog(this.panel,result);
			refresh();
		} catch (Exception e) {
			DialogBoxUtil.errorDialog(this.panel, e.getMessage());
			throw e;
		} finally {
			redistributeDispatch = null;

		}

	}

	@Override
	public void synchro() {
		DispatchUtil redistributeDispatch = null;
		try {
			redistributeDispatch = new DispatchUtil(RmiKeys.RMI_REDISTRIBUTE);
			redistributeDispatch.synchro(ConstantUtil.siteId);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			redistributeDispatch = null;
		}
	}

}
