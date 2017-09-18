package com.nms.ui.ptn.ne.eth.controller;

import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.ResultString;
import com.nms.service.impl.util.SiteUtil;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysOperaType;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.ne.camporeData.CamporeDataDialog;
import com.nms.ui.ptn.ne.eth.view.PortBasicPanel;
import com.nms.ui.ptn.ne.eth.view.dialog.PortMainDialog;

public class PortController extends AbstractController{

	private PortBasicPanel view;
	public PortController(PortBasicPanel pBasicPanel) {
		super();
		this.view = pBasicPanel;
	}
	
	/**
	 * 刷新
	 */
	@Override
	public void refresh() throws Exception {
		this.view.tableData();
	}
	
	//修改
	@Override
	public void openUpdateDialog()throws Exception{

		if (this.view.getAllSelect().size() != 1) { 
			DialogBoxUtil.errorDialog(this.getView(), ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
		} else if (!checkLag()){
			DialogBoxUtil.errorDialog(this.getView(), ResourceUtil.srcStr(StringKeysTip.TIP_USEDBYLAG));
		}else {
			PortService_MB portService = null;
			try {
				portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
				PortInst condition = new PortInst();
				condition.setPortId(view.getSelect().getPortId());
				PortInst portInst = portService.select(condition).get(0);
				new PortMainDialog(portInst,this.view);
			} catch (Exception e) {
				ExceptionManage.dispose(e,this.getClass());
			}finally{
				UiUtil.closeService_MB(portService);
			}
		}	
	}
	
	/**
	 * 同步
	 */
	@Override
	public void synchro() {
		DispatchUtil portDispatch = null;
		try {
			portDispatch = new DispatchUtil(RmiKeys.RMI_PORT);
			String result = portDispatch.synchro(ConstantUtil.siteId);
			DialogBoxUtil.succeedDialog(null, result);
			//添加日志记录
			AddOperateLog.insertOperLog(null, EOperationLogType.ETHSYNCHRO.getValue(), result,
					null, null, ConstantUtil.siteId, ResourceUtil.srcStr(StringKeysOperaType.BTN_ETH_SYNCHRO), null);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			portDispatch = null;
		}
	}
	
	/**
	 * 核对LAG
	 * @return
	 */
	private boolean checkLag() {
//		PortInst portInst = this.view.getAllSelect().get(0);
//		if("UNI".equals(portInst.getPortType()) && portInst.getLagId()>0){
//			return false;
//		}
		return true;
	}
	
	/**
	 * 拿到视图
	 * @return
	 */
	public PortBasicPanel getView() {
		return view;
	};
	
	
	/**
	 * 一致性检测
	 */
	@Override
	public void consistence(){
		PortService_MB portService = null;
		try {
			SiteUtil siteUtil=new SiteUtil();
			if (0 == siteUtil.SiteTypeUtil(ConstantUtil.siteId)) {
				portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
				DispatchUtil dispatchUtil = new DispatchUtil(RmiKeys.RMI_PORT);
				List<PortInst> nePortInsts = (List<PortInst>) dispatchUtil.consistence(ConstantUtil.siteId);
				PortInst portInst = new PortInst();
				portInst.setSiteId(ConstantUtil.siteId);
				List<PortInst> allPortInsts = portService.select(portInst);
				List<PortInst> emsPortInsts = new ArrayList<PortInst>();
				for(PortInst inst : allPortInsts){
					if(inst.getPortName().contains("fe")|| inst.getPortName().contains("ge")||inst.getPortName().contains("fx")){
						emsPortInsts.add(inst);
					}
				}
				CamporeDataDialog camporeDataDialog = new CamporeDataDialog(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_LIST), emsPortInsts, nePortInsts, this);
				UiUtil.showWindow(camporeDataDialog, 700, 600);
			}else{
				DialogBoxUtil.errorDialog(this.view, ResultString.QUERY_FAILED);
			}
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(portService);
		}
	}
}