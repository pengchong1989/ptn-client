package com.nms.ui.ptn.ne.arp.controller;

import java.util.List;

import com.nms.db.bean.ptn.ARPInfo;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.ptn.ARPInfoService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.ne.arp.view.ARPAddDialog;
import com.nms.ui.ptn.ne.arp.view.ARPPanel;

public class ARPController extends AbstractController {
	private ARPPanel view;
	
	public ARPController(ARPPanel arpPanel) {
		this.view = arpPanel;
	}

	@Override
	public void refresh() throws Exception {
		List<ARPInfo> ARPInfoList = null;
		try {
			ARPInfoList = this.getARPInfoList();
			this.view.clear();
			this.view.initData(ARPInfoList);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private List<ARPInfo> getARPInfoList() {
		ARPInfoService_MB service = null;
		List<ARPInfo> arpList = null;
		try {
			service = (ARPInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ARP);
			arpList = service.queryBySiteId(ConstantUtil.siteId);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
		return arpList;
	}

	@Override
	public void openCreateDialog() {
		new ARPAddDialog(null, this.view);
	}

	@Override
	public void openUpdateDialog() throws Exception {
		List<ARPInfo> infos = null;
		try {
			infos = this.view.getAllSelect();
			if (infos == null || infos.size() == 0) {
				DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
			} else {
				new ARPAddDialog(infos.get(0), this.view);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	@Override
	public void delete() throws Exception {
		List<ARPInfo> arpList = null;
		DispatchUtil arpDispatch = null;
		String resultStr = null;
		try {
			arpList = this.view.getAllSelect();
			arpDispatch = new DispatchUtil(RmiKeys.RMI_ARP);
			resultStr = arpDispatch.excuteDelete(arpList);
			DialogBoxUtil.succeedDialog(this.view, resultStr);
			//添加日志记录
			PtnButton deleteButton=(PtnButton) this.view.getDeleteButton();
			deleteButton.setOperateKey(EOperationLogType.DELETEARP.getValue());
			int operationResult=0;
			if(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS).equals(resultStr)){
				operationResult=1;
			}else{
				operationResult=2;
			}
			deleteButton.setResult(operationResult);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	@Override
	public void synchro() {
		DispatchUtil arpDispatch = null;
		try {
			arpDispatch = new DispatchUtil(RmiKeys.RMI_ARP);
			String result = arpDispatch.synchro(ConstantUtil.siteId);
			DialogBoxUtil.succeedDialog(this.view, result);
			//添加日志记录
			PtnButton deleteButton=(PtnButton) this.view.getSynchroButton();
			deleteButton.setOperateKey(EOperationLogType.SYNCHROARP.getValue());
			deleteButton.setResult(1);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
}
