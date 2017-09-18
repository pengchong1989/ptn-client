package com.nms.ui.ptn.ne.discardFlow.controller;

import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.shlef.SiteService_MB;
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
import com.nms.ui.ptn.ne.discardFlow.view.DiscardFlowPanel;

public class DiscardFlowController extends AbstractController{
	
	private DiscardFlowPanel discardFlowPanel;
	
	public DiscardFlowController(DiscardFlowPanel discardFlowPanel){
		this.discardFlowPanel = discardFlowPanel;
	}

	@Override
	public void refresh() throws Exception {
	     SiteInst initInfo = allFiledInitiInfo();
	     List<SiteInst> initInfoList = new ArrayList<SiteInst>();
	     initInfoList.add(initInfo);
	     this.discardFlowPanel.clear();
	     this.discardFlowPanel.initData(initInfoList);
	     this.discardFlowPanel.updateUI();
	}

	/**
	 *function:创建丢弃流 
	 */
	@Override
	public void openCreateDialog() throws Exception{
		
		if(this.discardFlowPanel.getAllSelect().size()>0){
			SiteInst siteInfo = this.discardFlowPanel.getAllSelect().get(0);
			if(siteInfo.getIsCreateDiscardFlow() == 0){
				DialogBoxUtil.errorDialog(this.discardFlowPanel, ResourceUtil.srcStr(StringKeysTip.TIP_DISCRAD_CREATE));
				return ;
			}
			DispatchUtil siteDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
			siteInfo.setIsCreateDiscardFlow(0);
			siteInfo.setControlType("discardFlow");
		    String result =	siteDispatch.createOrDeleteDiscardFlow(siteInfo);
		    this.refresh();
		    DialogBoxUtil.succeedDialog(null, result);
		    this.insertOpeLog(EOperationLogType.DISCARDSINGINSERT.getValue(), result, null,null);
		}else{
			DialogBoxUtil.errorDialog(this.discardFlowPanel, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
		}
	}
	/**
	 * function:删除丢弃流
	 */
	@Override
	public void delete() throws Exception {
		  List<Integer> siteIds = null;
          if(this.discardFlowPanel.getAllSelect().size() > 0){
        	  SiteInst siteInst = this.discardFlowPanel.getAllSelect().get(0);
        	//判断是否为在线脱管网元
  			SiteUtil siteUtil = new SiteUtil();
  			if(1==siteUtil.SiteTypeOnlineUtil(ConstantUtil.siteId)){
  				WhImplUtil wu = new WhImplUtil();
  				siteIds = new ArrayList<Integer>();
  				siteIds.add(ConstantUtil.siteId);
  				String str=wu.getNeNames(siteIds);
  				DialogBoxUtil.errorDialog(this.discardFlowPanel, ResourceUtil.srcStr(StringKeysTip.TIP_NOT_DELETEONLINE)+""+str+ResourceUtil.srcStr(StringKeysTip.TIP_ONLINENOT_DELETEONLINE));
  				this.insertOpeLog(EOperationLogType.DELETEDCDQL.getValue(), ResultString.CONFIG_FAILED, null,null);
  				return ;  		    		
  				}
  			  //判断是否已删除
        	  if(siteInst.getIsCreateDiscardFlow() == 1){
        		  DialogBoxUtil.errorDialog(this.discardFlowPanel, ResourceUtil.srcStr(StringKeysTip.TIP_DISCRAD_DELETE));
  				return ; 
        	  }
        	  DispatchUtil siteDispath = new DispatchUtil(RmiKeys.RMI_SITE);
        	  siteInst.setIsCreateDiscardFlow(1);
        	  siteInst.setControlType("discardFlow");
        	  String result = siteDispath.createOrDeleteDiscardFlow(siteInst);
        	  this.refresh();
        	  DialogBoxUtil.succeedDialog(null, result);
        	  this.insertOpeLog(EOperationLogType.DISCARDSINGDELETE.getValue(), result, null,null);
          }else{
        	  DialogBoxUtil.errorDialog(this.discardFlowPanel,ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
          }	
	}
	
	
	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
	   AddOperateLog.insertOperLog(null, operationType, result, oldMac, newMac,ConstantUtil.siteId,ResourceUtil.srcStr(StringKeysPanel.DISCARDFLOWMANAGE),"");			
	}
	/**
	 * function:获取指定的网元信息
	 * @return
	 */
	private SiteInst allFiledInitiInfo() {
		SiteInst initInfo = new SiteInst();
		SiteService_MB siteService = null;
		try {
			siteService = (SiteService_MB)ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			initInfo = siteService.select(ConstantUtil.siteId);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
		}
		return initInfo;
	}

}
