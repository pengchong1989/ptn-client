package com.nms.ui.ptn.ne.l2cp.controller;

import java.awt.event.ActionEvent;
import java.util.List;
import com.nms.db.bean.ptn.L2cpInfo;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.ptn.L2CPService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.ptn.ne.l2cp.view.L2cpPanel;

public class L2cpController {

	private L2cpPanel view ;
	
	public L2cpController(L2cpPanel view){
		this.view = view;
		addListention();
		try {
			init();
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
	}

	private void addListention() {
		this.view.getConfirm().addActionListener(new MyActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				confirm();
			}

			@Override
			public boolean checking() {
				// TODO Auto-generated method stub
				return true;
			}
		});
		
        this.view.getSyncbutton().addActionListener(new MyActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sync();
			}

			@Override
			public boolean checking() {
				// TODO Auto-generated method stub
				return true;
			}
		});
		
		
	}

	private void init() {
		L2CPService_MB l2cpService = null;
		List<L2cpInfo> l2cpList = null;
		L2cpInfo L2cpInfo = null;
		try {
			l2cpService = (L2CPService_MB)ConstantUtil.serviceFactory.newService_MB(Services.L2CPSERVICE);
			l2cpList = l2cpService.selectAll(ConstantUtil.siteId);
			if(l2cpList != null && !l2cpList.isEmpty()){
				L2cpInfo = l2cpList.get(0);
				this.view.refresh(L2cpInfo);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}finally{
			UiUtil.closeService_MB(l2cpService);
		}
	}
	
//创建L2CP业务
	private void confirm() {
		L2CPService_MB l2cpService = null;
		L2cpInfo l2cpInfo = null;
		DispatchUtil l2cpDispatch = null;
		String result = "";
		try {
			l2cpService = (L2CPService_MB)ConstantUtil.serviceFactory.newService_MB(Services.L2CPSERVICE);
			l2cpInfo = new L2cpInfo();
			this.view.get(l2cpInfo);
			l2cpDispatch = new DispatchUtil(RmiKeys.RMI_L2CPSERVICE);
			//更新
			List<L2cpInfo> l2cp=l2cpService.selectAll(ConstantUtil.siteId);
			if(!l2cp.isEmpty()){
				result = l2cpDispatch.excuteUpdate(l2cpInfo);
				L2cpInfo l2cpBefore = new L2cpInfo();
				l2cpBefore=l2cp.get(0);
				this.insertOpeLog(EOperationLogType.L2CPUPDATE.getValue(), result, l2cpBefore, l2cpInfo);	
			}
			//增加
			else{
				result = l2cpDispatch.excuteInsert(l2cpInfo);
				this.insertOpeLog(EOperationLogType.L2CPINSERT.getValue(), result, null, l2cpInfo);		
			}
			DialogBoxUtil.succeedDialog(this.view, result);
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}finally{
			UiUtil.closeService_MB(l2cpService);
			l2cpDispatch = null;
		}
	}
	
	private void insertOpeLog(int operationType, String result, L2cpInfo oldL2cp, L2cpInfo newL2cp){		
		AddOperateLog.insertOperLog(this.view.getConfirm(), operationType, result, oldL2cp, newL2cp,newL2cp.getSiteId(),ResourceUtil.srcStr(StringKeysLbl.L2CP_MODEL),"l2cp");					
	}
	
	private void sync() {
		DispatchUtil l2cpDispath = null;
		try {
			
			l2cpDispath = new DispatchUtil(RmiKeys.RMI_L2CPSERVICE);
			String result = l2cpDispath.synchro(ConstantUtil.siteId);
			DialogBoxUtil.succeedDialog(null, result);
			//添加日志记录
			this.insertOpeLogSyn(EOperationLogType.SYNCL2CP.getValue(), result, null, null);	
			this.init();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			l2cpDispath = null;
		}		
   }
	
	private void insertOpeLogSyn(int operationType, String result, Object oldL2cp, Object newL2cp){		
		AddOperateLog.insertOperLog(this.view.getSyncbutton(), operationType, result, oldL2cp, newL2cp,ConstantUtil.siteId,ResourceUtil.srcStr(StringKeysLbl.L2CP_MODEL),"");					
	}
}
