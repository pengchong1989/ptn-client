package com.nms.ui.ptn.ne.loopProtRotate.controller;

import java.util.List;

import com.nms.db.bean.ptn.path.protect.LoopProtectInfo;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.ptn.path.protect.WrappingProtectService_MB;
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
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.ne.loopProtRotate.view.LoopProtRotateNodePanel;

public class LoopProtRotateNodeController extends AbstractController {
	private LoopProtRotateNodePanel view;
	
	public LoopProtRotateNodeController(LoopProtRotateNodePanel rotateNodePanel) {
		this.view = rotateNodePanel;
	}

	/**
	 * 刷新界面数据
	 */
	@Override
	public void refresh() throws Exception {
		WrappingProtectService_MB protService = (WrappingProtectService_MB) ConstantUtil.serviceFactory.
		newService_MB(Services.WRAPPINGPROTECT);
		try {
			LoopProtectInfo prot = new LoopProtectInfo();
			prot.setSiteId(ConstantUtil.siteId);
			List<LoopProtectInfo> loopList = protService.select(prot);
			this.view.clear();
			this.view.initData(loopList);
			this.view.updateUI();
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(protService);
		}
	}
	
	@Override
	public void synchro(){
		DispatchUtil dispatch = null;
		try {
			dispatch = new DispatchUtil(RmiKeys.RMI_LOOPPROROTATE);
			String result = dispatch.synchro(ConstantUtil.siteId);
			DialogBoxUtil.succeedDialog(this.view, result);
			AddOperateLog.insertOperLog(null, EOperationLogType.SYSLOOPPROTECT.getValue(), result, 
					null, null, ConstantUtil.siteId, ResourceUtil.srcStr(StringKeysTitle.TIT_LOOPPROTECT), null);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
			dispatch = null;
		}
	}
}