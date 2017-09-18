package com.nms.ui.ptn.ne.protect.loopProtect.controller;

import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.path.protect.LoopProtectInfo;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.port.PortService_MB;
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
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.ne.protect.loopProtect.AddLoopNodeDialog;
import com.nms.ui.ptn.ne.protect.loopProtect.LoopProtectPanelSingle;
/**
 * 单网元环保护信息
 * @author dzy
 *
 */
public class LoopProtectPanelSingleController extends AbstractController {
	private LoopProtectPanelSingle view ;
	public  LoopProtectPanelSingleController(LoopProtectPanelSingle loopProtectPanelSingle){
		this.setView(loopProtectPanelSingle);
	}
	@Override
	public void refresh() throws Exception {
		WrappingProtectService_MB service = null;
		List<LoopProtectInfo> loopProtectList = null;
		LoopProtectInfo LoopProtectInfo;
		try {
			LoopProtectInfo = new LoopProtectInfo();
			LoopProtectInfo.setSiteId(ConstantUtil.siteId);
			service = (WrappingProtectService_MB) ConstantUtil.serviceFactory.newService_MB(Services.WRAPPINGPROTECT);
			loopProtectList = service.select(LoopProtectInfo);
			this.view.clear();			
			this.view.initData(loopProtectList);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
			loopProtectList = null;
		}

	}
	/**
	 * 初始化数据
	 * @throws Exception
	 */
	public void initDetailInfo(){
		try {
			initInfoData();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	/**
	 * 详细信息
	 * @throws Exception
	 */
	private void initInfoData() throws Exception {
		LoopProtectInfo loopProtectInfo = null;
		LoopProtectInfo loopProtectWest = null;
		LoopProtectInfo loopProtectEast = null;
		PortService_MB service = null;
		ArrayList<LoopProtectInfo> loopProtectList;
		PortInst westPort;
		PortInst eastPort;
		try {
			loopProtectList = new ArrayList<LoopProtectInfo>();
			loopProtectInfo = this.view.getSelect();
			loopProtectWest = new LoopProtectInfo();
			service = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			
			westPort = new PortInst();
			westPort.setPortId(loopProtectInfo.getWestPort());
			westPort = service.select(westPort).get(0);
			
			
			loopProtectWest.putClientProperty("port",westPort.getPortName());
			loopProtectWest.putClientProperty("position","西向");
			loopProtectWest.putClientProperty("nodeIdOppo",loopProtectInfo.getWestNodeId());
			loopProtectWest.putClientProperty("ap",loopProtectInfo.getApsenable()==1?"使能":"不使能");
			loopProtectList.add(loopProtectWest);
			
			eastPort = new PortInst();
			eastPort.setPortId(loopProtectInfo.getEastPort());
			eastPort = service.select(eastPort).get(0);
			
			loopProtectEast = new LoopProtectInfo();
			loopProtectEast.putClientProperty("port",eastPort.getPortName());
			loopProtectEast.putClientProperty("position","东向");
			loopProtectEast.putClientProperty("nodeIdOppo",loopProtectInfo.getEastNodeId());
			loopProtectEast.putClientProperty("ap",loopProtectInfo.getApsenable()==1?"使能":"不使能");
			loopProtectList.add(loopProtectEast);
			
			this.view.getLoopInfoPanel().clear();
			this.view.getLoopInfoPanel().initData(loopProtectList);
			this.view.getLoopInfoPanel().updateUI();

		} catch (Exception e) {
			throw e;
		} finally {
			loopProtectInfo = null;
			loopProtectList = null;
			UiUtil.closeService_MB(service);
		}
	}
	//创建
	public void openCreateDialog()throws Exception{
		AddLoopNodeDialog addLoopNodeDialog = new AddLoopNodeDialog(true,null);
		addLoopNodeDialog.setLocation(UiUtil.getWindowWidth(addLoopNodeDialog.getWidth()), UiUtil.getWindowHeight(addLoopNodeDialog.getHeight()));
		addLoopNodeDialog.setVisible(true);
		this.refresh();
	}
	/**
	 * 修改
	 */
	public void openUpdateDialog() throws Exception {
		AddLoopNodeDialog addLoopNodeDialog = new AddLoopNodeDialog(true,this.view);
		addLoopNodeDialog.setLocation(UiUtil.getWindowWidth(addLoopNodeDialog.getWidth()), UiUtil.getWindowHeight(addLoopNodeDialog.getHeight()));
		addLoopNodeDialog.setVisible(true);
		this.refresh();
	}
	/**
	 * 删除
	 */
	public void delete() throws Exception {
		List<LoopProtectInfo> loopProtectInfoList = null;
		boolean flag = true;
		DispatchUtil wrappingDispatch = null;
		String resultStr = null;
		try {
			loopProtectInfoList = this.getView().getAllSelect();
			for (LoopProtectInfo loopProtectInfo : loopProtectInfoList) {
				if (loopProtectInfo.getIsSingle() == 0) {
					flag = false;
					break;
				}
			}
			if (!flag) {
				DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_DELETE_NODE));
			}else{
				wrappingDispatch = new DispatchUtil(RmiKeys.RMI_WRAPPING);
				resultStr = wrappingDispatch.excuteDelete(loopProtectInfoList);
				DialogBoxUtil.succeedDialog(this.getView(), resultStr);
				this.view.getRefreshButton().doClick();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			loopProtectInfoList = null;
			resultStr = null;
			loopProtectInfoList = null;
		}
	}
	/**
	 * 同步 
	 */
	public void synchro() throws Exception {
		DispatchUtil wrappingDispatch = null;
		try {
			wrappingDispatch = new DispatchUtil(RmiKeys.RMI_WRAPPING);
			wrappingDispatch.synchro(ConstantUtil.siteId);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			wrappingDispatch = null;
		}
	}
	public LoopProtectPanelSingle getView() {
		return view;
	}
	public void setView(LoopProtectPanelSingle view) {
		this.view = view;
	}
}
