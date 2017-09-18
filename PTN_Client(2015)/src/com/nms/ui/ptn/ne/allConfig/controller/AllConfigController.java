package com.nms.ui.ptn.ne.allConfig.controller;

import java.awt.event.ActionEvent;
import java.util.List;

import com.nms.db.bean.ptn.AllConfigInfo;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.ptn.AllConfigService_MB;
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
import com.nms.ui.ptn.ne.allConfig.view.AllConifgPanel;

public class AllConfigController {

	private AllConifgPanel allConifgPanel;
	private AllConfigInfo wholeConfigInfo = new AllConfigInfo();
	
	public AllConfigController(AllConifgPanel allConifgPanel) {
		this.allConifgPanel = allConifgPanel;
		addListention();
		try {
			init();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	/**
	 * 初始化界面值
	 */
	private void init() {
		
		AllConfigService_MB allConfigService = null;
		List<AllConfigInfo> wholeConfigInfoList = null;
		try {
			allConfigService = (AllConfigService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ALLCONFIG);
			wholeConfigInfoList = allConfigService.select(ConstantUtil.siteId);
			if(wholeConfigInfoList != null && wholeConfigInfoList.size()>0){
				wholeConfigInfo = wholeConfigInfoList.get(0);	
			}
			this.allConifgPanel.refresh(wholeConfigInfo);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(allConfigService);
		}
	}
	
	private void addListention() {
		
		this.allConifgPanel.getOkButton().addActionListener(new MyActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!AllConfigController.this.allConifgPanel.check())
				{
					return;
				}
				wholeConfigInfo = AllConfigController.this.allConifgPanel.pageSetValue(wholeConfigInfo);
				AllConfigService_MB allConfigService = null;
				String result = "";
				try {
					DispatchUtil allConfigDispatch = new DispatchUtil(RmiKeys.RMI_ALLCONFIG);
					allConfigService = (AllConfigService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ALLCONFIG);
					if(allConfigService.select(wholeConfigInfo.getSiteId()).size()>0){
						result = allConfigDispatch.excuteUpdate(wholeConfigInfo);
					}else{
						result = allConfigDispatch.excuteInsert(wholeConfigInfo);
					}
					AddOperateLog.insertOperLog(allConifgPanel.getOkButton(), EOperationLogType.ALLCONFIGINSERT.getValue(),result,
							null, wholeConfigInfo, ConstantUtil.siteId, ResourceUtil.srcStr(StringKeysLbl.LBL_GLOBAL_CONFIG), "allConfig");
					DialogBoxUtil.succeedDialog(allConifgPanel, result);
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				} finally {
					UiUtil.closeService_MB(allConfigService);
				}
			}

			@Override
			public boolean checking() {
				return true;
			}
		});
		
		this.allConifgPanel.getSynchroButton().addActionListener(new MyActionListener() 
		{
			public void actionPerformed(ActionEvent e) {
				synchro();
			}

			@Override
			public boolean checking() {
				return true;
			}
		});
	}
	
	public void synchro() {
		DispatchUtil allcfgDispatch = null;
		try {
			
			allcfgDispatch = new DispatchUtil(RmiKeys.RMI_ALLCONFIG);
			String result = allcfgDispatch.synchro(ConstantUtil.siteId);
			DialogBoxUtil.succeedDialog(null, result);
			//添加日志记录
			AddOperateLog.insertOperLog(null, EOperationLogType.SYNCALLCONFIG.getValue(), result, 
					null, null, ConstantUtil.siteId, ResourceUtil.srcStr(StringKeysLbl.LBL_ALL_CONFIG), null);
			this.init();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			allcfgDispatch = null;
		}
	}
	
	public AllConifgPanel getAllConifgPanel() {
		return allConifgPanel;
	}
	public void setAllConifgPanel(AllConifgPanel allConifgPanel) {
		this.allConifgPanel = allConifgPanel;
	}
	
}
