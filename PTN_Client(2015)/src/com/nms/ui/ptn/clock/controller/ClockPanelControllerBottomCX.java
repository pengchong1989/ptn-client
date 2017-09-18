package com.nms.ui.ptn.clock.controller;

import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.ptn.clock.TodConfigInfo;
import com.nms.model.ptn.clock.TodDispositionInfoService_MB;
import com.nms.model.util.Services;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.ptn.clock.view.cx.dialog.TODCreateDialog;
import com.nms.ui.ptn.clock.view.cx.time.TabPanelTwoBottomTCX;

public class ClockPanelControllerBottomCX extends AbstractController {

	private TabPanelTwoBottomTCX tabPanelTwoBottomTCX = null;

	public ClockPanelControllerBottomCX(TabPanelTwoBottomTCX tabPanelTwoBottomTCX) {
		this.tabPanelTwoBottomTCX = tabPanelTwoBottomTCX;
	}

	/**
	 * <p>
	 * 数据修改
	 * </p>
	 */
	@Override
	public void openUpdateDialog() throws Exception {

		TodConfigInfo todConfigInfo = null;
		try {

			todConfigInfo = new TodConfigInfo();
			todConfigInfo =  tabPanelTwoBottomTCX.getSelect();
			new TODCreateDialog(tabPanelTwoBottomTCX,todConfigInfo);
		} catch (Exception e) {

			throw e;
		}
	}

	/**
	 * <p>
	 * 加载页面数据
	 * </p>
	 */
	@Override
	public void refresh() throws Exception {

		List<TodConfigInfo> list = null;
		TodConfigInfo todConfigInfo = null;
		TodDispositionInfoService_MB todDispositionInfoService=null;
		try {
			todDispositionInfoService=(TodDispositionInfoService_MB)ConstantUtil.serviceFactory.newService_MB(Services.TodDispositionInfoService);
			list = new ArrayList<TodConfigInfo>();
			todConfigInfo=new TodConfigInfo();
			todConfigInfo=todDispositionInfoService.select(ConstantUtil.siteId);
			list.add(todConfigInfo);
			
			this.tabPanelTwoBottomTCX.clear();
			this.tabPanelTwoBottomTCX.initData(list);
			this.tabPanelTwoBottomTCX.updateUI();
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(todDispositionInfoService);
		}

	}
}
