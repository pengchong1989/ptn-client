package com.nms.ui.ptn.ne.pwnni.controller;

import java.util.List;

import com.nms.db.bean.ptn.path.pw.PwNniInfo;
import com.nms.model.ptn.path.pw.PwNniInfoService_MB;
import com.nms.model.util.Services;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.ne.pwnni.view.PwNNIPanel;

public class PwNNIController extends AbstractController {

	private PwNNIPanel view;

	public PwNNIController(PwNNIPanel pwnNniPanel) {
		this.setView(pwnNniPanel);
	}

	@Override
	public void refresh() throws Exception {
		this.searchAndrefreshdata();
	}

	// 删除
	@Override
	public void delete() throws Exception {

		PwNniInfoService_MB pwNniservice = null;
		PwNniInfo pwNniBuffer = null;
		try {
			pwNniservice = (PwNniInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwNniBuffer);
			for (int i = 0; i < this.getView().getSelectInfo().size(); i++) {
				pwNniBuffer = this.getView().getSelectInfo().get(i);
				if (pwNniBuffer.getPwId() != 0) {
					DialogBoxUtil.succeedDialog(this.getView(), ResourceUtil.srcStr(StringKeysTip.TIP_PWNNIBUFFERHASBEUSED));
					return;
				}
				pwNniservice.delete(pwNniBuffer.getId());
			}
			DialogBoxUtil.succeedDialog(this.getView(), ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
			this.searchAndrefreshdata();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(pwNniservice);
			pwNniBuffer = null;
		}

	};

	// 修改
	@Override
	public void openUpdateDialog() throws Exception {
		if (this.getView().getSelectInfo().size() != 1) {
			DialogBoxUtil.errorDialog(this.getView(), ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
		} else {
			PwNniInfo info = this.getView().getSelectInfo().get(0);

			// AddPwNniBuffer addpwNnidialog = new AddPwNniBuffer(this.getView(), true, info);
			// addpwNnidialog.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_PWNNI));
			// addpwNnidialog.setLocation(UiUtil.getWindowWidth(addpwNnidialog.getWidth())/2,
			// UiUtil.getWindowHeight(addpwNnidialog.getHeight())/2);
			// addpwNnidialog.setSize(new Dimension(650, 350));
			// addpwNnidialog.setVisible(true);
			// this.searchAndrefreshdata();
		}
	}

	// 创建
	@Override
	public void openCreateDialog() throws Exception {
		// AddPwNniBuffer addpwNnidialog = new AddPwNniBuffer(this.getView(), true, null);
		// addpwNnidialog.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_CREATE_PWNNI));
		// addpwNnidialog.setLocation(UiUtil.getWindowWidth(addpwNnidialog.getWidth())/2,
		// UiUtil.getWindowHeight(addpwNnidialog.getHeight())/2);
		// addpwNnidialog.setSize(new Dimension(650, 350));
		// addpwNnidialog.setVisible(true);
		// this.searchAndrefreshdata();
	};

	private void searchAndrefreshdata() {

		PwNniInfoService_MB pwNniService = null;
		List<PwNniInfo> list = null;
		PwNniInfo info = null;

		try {
			pwNniService = (PwNniInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwNniBuffer);
			info = new PwNniInfo();
			info.setSiteId(ConstantUtil.siteId);
			list = pwNniService.select(info);

			view.clear();
			view.initData(list);
			view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(pwNniService);
			list = null;
		}
	}

	public void setView(PwNNIPanel view) {
		this.view = view;
	}

	public PwNNIPanel getView() {
		return view;
	};

}
