package com.nms.ui.ptn.ne.ethService.controller;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import com.nms.db.bean.ptn.EthServiceInfo;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.ptn.EthService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.ResultString;
import com.nms.service.impl.util.SiteUtil;
import com.nms.service.impl.util.WhImplUtil;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.ne.ethService.view.EthServiceDialog;
import com.nms.ui.ptn.ne.ethService.view.EthServicePanel;

public class EthServiceController extends AbstractController{
	
	private  EthServicePanel view ;

	public EthServiceController(EthServicePanel ethServicePanel) {
		this.view = ethServicePanel;
	}
	@Override
	public void refresh() throws Exception {
		EthService_MB ethService = null;
		List<EthServiceInfo> ethServiceInfoList = null;
		try {
			this.view.clear();
			ethServiceInfoList = new ArrayList<EthServiceInfo>();
			ethService = (EthService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ETHSERVICE);
			ethServiceInfoList = ethService.select(ConstantUtil.siteId);				
			this.view.initData(ethServiceInfoList);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(ethService);
			ethServiceInfoList = null;
		}		
	}
	
	//新建
	@Override
	public void openCreateDialog() throws Exception {
		if(view.getTable().getAllElement().size() < 255){
			EthServiceDialog dialog = new EthServiceDialog(null,this);
			dialog.setSize(new Dimension(500,400));
			dialog.setLocation(UiUtil.getWindowWidth(dialog.getWidth()), UiUtil.getWindowHeight(dialog.getHeight()));
			dialog.setResizable(false);
			dialog.setVisible(true);
		}else{                                  
			DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_EXCEED_MAXVALUE));
		}
	};
	
	@Override
	// 修改
	public void openUpdateDialog() throws Exception {
		try {
			if (this.view.getAllSelect().size() == 0) {
				DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
			}else{
				EthServiceDialog dialog = new EthServiceDialog(view.getSelect(),this);
				dialog.setSize(new Dimension(500,400));
				dialog.setLocation(UiUtil.getWindowWidth(dialog.getWidth()), UiUtil.getWindowHeight(dialog.getHeight()));
				dialog.setResizable(false);
				dialog.setVisible(true);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	};
	@Override
	// 删除
	public void delete() throws Exception {
		DispatchUtil dispath = null; 
		try {
			if (this.view.getAllSelect().size() == 0) {
				DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
			}else{				
				dispath = new DispatchUtil(RmiKeys.RMI_ETHSERVICE);
				String result = dispath.excuteDelete(this.view.getAllSelect());
				DialogBoxUtil.succeedDialog(view, result);
				for(int i=0;i<this.view.getAllSelect().size();i++){
			    	this.insertOpeLog(EOperationLogType.DELETESERVICE.getValue(), result, null,null);
				}
				refresh();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	};
	
	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){	
		AddOperateLog.insertOperLog(null, operationType, result, oldMac, newMac,ConstantUtil.siteId,ResourceUtil.srcStr(StringKeysPanel.ETHSERVICE_TABLE),"");		
	}
	
	@Override
	public boolean deleteChecking() {
		boolean flag = false;
		List<Integer> siteIds = null;
		try {
			if (this.view.getAllSelect().size() == 0) {
				DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
				return false;
			}
			SiteUtil siteUtil = new SiteUtil();
			if(1==siteUtil.SiteTypeOnlineUtil(ConstantUtil.siteId)){
				WhImplUtil wu = new WhImplUtil();
				siteIds = new ArrayList<Integer>();
				siteIds.add(ConstantUtil.siteId);
				String str=wu.getNeNames(siteIds);
				DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_NOT_DELETEONLINE)+""+str+ResourceUtil.srcStr(StringKeysTip.TIP_ONLINENOT_DELETEONLINE));
				for(int i=0;i<this.view.getAllSelect().size();i++){
			    	this.insertOpeLog(EOperationLogType.DELETESERVICE1.getValue(), ResultString.CONFIG_FAILED, null,this.view.getAllSelect().get(i));
				}
				return false;  		    		
				}
			flag = true;
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			siteIds = null;
		}
		return flag;
	}
	
	
	@Override
	public void synchro(){
		try {
			DispatchUtil dispatch = new DispatchUtil(RmiKeys.RMI_ETHSERVICE);
			String result = dispatch.synchro(ConstantUtil.siteId);
			DialogBoxUtil.succeedDialog(this.view, result);
			this.insertOpeLog(EOperationLogType.SYSSERVICE.getValue(), result, null,null);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
}
