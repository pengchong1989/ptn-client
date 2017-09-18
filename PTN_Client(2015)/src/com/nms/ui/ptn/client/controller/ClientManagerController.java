package com.nms.ui.ptn.client.controller;

import java.util.List;

import com.nms.db.bean.client.Client;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.client.ClientService_MB;
import com.nms.model.util.Services;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.client.view.AddClientInfoDialog;
import com.nms.ui.ptn.client.view.ClientManagerPanel;

public class ClientManagerController extends AbstractController {
	private ClientManagerPanel view;

	public ClientManagerPanel getView() {
		return view;
	}

	public ClientManagerController(ClientManagerPanel view) {
		this.view = view;
	}

	/**
	 * 刷新
	 */
	public void refresh() throws Exception {

		ClientService_MB service = null;
		List<Client> clientList = null;
		try {
			service = (ClientService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CLIENTSERVICE);
			clientList = service.refresh();
			this.view.clear();
			this.view.initData(clientList);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
			clientList = null;
		}

	}

	/**
	 * 新建
	 */
	public void openCreateDialog() {
		new AddClientInfoDialog(this.getView(), true, null);
	}

	/**
	 * 修改
	 */
	public void openUpdateDialog() throws Exception {
		if (this.getView().getAllSelect().size() == 0) {
			DialogBoxUtil.errorDialog(this.getView(), ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
		} else {
			Client client = this.getView().getAllSelect().get(0);
			new AddClientInfoDialog(this.getView(), true, client);
		}
	}

	/**
	 * /删除
	 */
	public void delete() {
		ClientService_MB service = null;
		List<Client> clientList;

//		boolean flag = true;
		boolean delresult = true;
		try {
			service = (ClientService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CLIENTSERVICE);
			clientList = this.getView().getAllSelect();

			for (Client client : clientList) {
				delresult = service.delete(client);
			}
//			if (!flag) {
//				DialogBoxUtil.errorDialog(this.getView(), ResourceUtil.srcStr(StringKeysTip.TIP_SUBNET_DELETE_NODE));
//				return;
//			}
			//添加日志记录
			PtnButton deleteButton=(PtnButton) this.view.getDeleteButton();
			deleteButton.setOperateKey(EOperationLogType.CLENTDELETE.getValue());
			int operationResult=0;
			if(delresult){
				operationResult=1;
			}else{
				operationResult=2;
			}
			deleteButton.setResult(operationResult);
			this.view.getRefreshButton().doClick();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			clientList = null;
			UiUtil.closeService_MB(service);
		}
	}

}
