﻿package com.nms.ui.ptn.ne.msp.controller;

import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.ptn.path.protect.MspProtect;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.ptn.path.protect.MspProtectService_MB;
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
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.ptn.ne.msp.bean.MspPortInfo;
import com.nms.ui.ptn.ne.msp.view.EditMspDialog;
import com.nms.ui.ptn.ne.msp.view.MspPanel;

/**
 * MSP保护按钮控制类
 * 
 * @author Administrator
 * 
 */
public class MspController extends AbstractController {

	private MspPanel mspPanel = null; // msp主界面面板

	/**
	 * 创建一个新的实例
	 * 
	 * @param msp主界面面板
	 */
	public MspController(MspPanel mspPanel) {
		this.mspPanel = mspPanel;
	}

	/**
	 * 刷新界面数据
	 */
	@Override
	public void refresh() throws Exception {
		MspProtectService_MB mspProtectService = null;
		List<MspProtect> mspProtectList = null;
		try {

			mspProtectService = (MspProtectService_MB) ConstantUtil.serviceFactory.newService_MB(Services.MSPPROTECT);
			mspProtectList = mspProtectService.selectBySite(ConstantUtil.siteId);

			this.mspPanel.getMspPortPanel().clear();
			this.mspPanel.clear();
			this.mspPanel.initData(mspProtectList);

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(mspProtectService);
			mspProtectList = null;
		}

	}

	/**
	 * 打开新建窗口
	 */
	@Override
	public void openCreateDialog() {
		new EditMspDialog(null, this.mspPanel);
	}

	/**
	 * 打开修改窗口
	 */
	@Override
	public void openUpdateDialog() {
		new EditMspDialog(this.mspPanel.getSelect(), this.mspPanel);
	}

	/**
	 * 删除数据
	 */
	@Override
	public void delete() {
		DispatchUtil mspDispatch = null;
		List<MspProtect> mspList = null; 
		String result;
		try {
			mspList = new ArrayList<MspProtect>();
			mspDispatch=new DispatchUtil(RmiKeys.RMI_MSPPROTECT);		
			mspList = this.mspPanel.getAllSelect();
			result = mspDispatch.excuteDelete(mspList);
			DialogBoxUtil.succeedDialog(null, result);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		
		
	}

	/**
	 * 同步数据
	 */
	@Override
	public void synchro() {
		DispatchUtil mspDispatch = null;
		try {
			mspDispatch=new DispatchUtil(RmiKeys.RMI_MSPPROTECT);
			mspDispatch.synchro(ConstantUtil.siteId);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		
	}

	/**
	 * 点击table记录。 查看详细记录
	 */
	@Override
	public void initDetailInfo() {

		try {
			this.mspPanel.getMspPortPanel().clear();
			this.mspPanel.getMspPortPanel().initData(this.getMspPortInfoList(this.mspPanel.getSelect()));

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	};

	/**
	 * 根据msp保护对象获取port对象。显示在详细信息中
	 * 
	 * @param mspProtect
	 * @return
	 * @throws Exception
	 */
	private List<MspPortInfo> getMspPortInfoList(MspProtect mspProtect) throws Exception {
		List<MspPortInfo> mspPortInfoList = null;
		MspPortInfo mspPortInfo_work = null;
		MspPortInfo mspPortInfo_protect = null;
		PortService_MB portService = null;
		try {
			portService=(PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			mspPortInfoList = new ArrayList<MspPortInfo>();

			// 组织工作端口对象
			mspPortInfo_work = new MspPortInfo();
			mspPortInfo_work.setPortName(portService.getPortname(mspProtect.getWorkPortId()));
			mspPortInfo_work.setIdentity(ResourceUtil.srcStr(StringKeysLbl.LBL_WORK_PORT));
			mspPortInfo_work.setWorkPort(mspProtect.getWorkPortId() == mspProtect.getNowWorkPortId() ? true : false);
			mspPortInfo_work.setProtectStatus(mspProtect.getProtectStatus());
			mspPortInfo_work.setK1byte("");
			mspPortInfo_work.setK2byte("");

			// 组织保护端口对象
			mspPortInfo_protect = new MspPortInfo();
			mspPortInfo_protect.setPortName(portService.getPortname(mspProtect.getProtectPortId()));
			mspPortInfo_protect.setIdentity("保护端口");
			mspPortInfo_protect.setWorkPort(mspProtect.getProtectPortId() == mspProtect.getNowWorkPortId() ? true : false);
			mspPortInfo_protect.setProtectStatus(mspProtect.getProtectStatus());
			mspPortInfo_protect.setK1byte("");
			mspPortInfo_protect.setK2byte("");

			// 把工作和保护对象放入集合中
			mspPortInfoList.add(mspPortInfo_work);
			mspPortInfoList.add(mspPortInfo_protect);
		} catch (Exception e) {
			throw e;
		} finally {
			mspPortInfo_work = null;
			mspPortInfo_protect = null;
			UiUtil.closeService_MB(portService);
		}
		return mspPortInfoList;
	}

}
