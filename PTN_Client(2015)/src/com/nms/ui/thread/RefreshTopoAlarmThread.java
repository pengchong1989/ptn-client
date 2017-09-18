package com.nms.ui.thread;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.model.alarm.CurAlarmService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.dispatch.rmi.bean.ServiceBean;
import com.nms.ui.Ptnf;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.util.TopologyUtil;

/**
 * 刷新拓扑图线程，包括 拓扑上的告警，网元连接状态，告警灯显示
 * 
 * @author kk
 * 
 */
public class RefreshTopoAlarmThread implements Runnable {

	private int alarmNum = 0; // 告警数量
	private int alarmMaxId = 0; // 告警最大ID号
	private Ptnf ptnf=null;
	private int connectioCount = 0;

	public RefreshTopoAlarmThread(Ptnf ptnf) {
		Thread.currentThread().setName("RefreshTopoAlarmThread");
		this.ptnf=ptnf;
	}

	@Override
	public void run() {
		
		TopologyUtil topologyUtil = new TopologyUtil();
		CurAlarmService_MB curAlarmService = null;
		try {
			
			while (true) {
				try {
					curAlarmService = (CurAlarmService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CurrentAlarm);
					// 验证告警是否有变化
					if (this.alarmIsChange(curAlarmService)) {
						if(Ptnf.getPtnf().isToop()){//当前面板是否为toop
							// 刷新拓扑图告警
							ExceptionManage.infor("RefreshTopoAlarmThread", this.getClass());
							topologyUtil.updateSiteInstAlarm(curAlarmService);
						}
						this.ptnf.refreshAlarmNum(curAlarmService);
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}finally{
					UiUtil.closeService_MB(curAlarmService);
				}
				
				//用于测试服务器是否断掉
//				try { 
//					if(!verificationRmi()){
//						connectioCount ++;
//						ptnf.getLoginLabel().setText(ptnf.getLogLabel()+"  "+ResourceUtil.srcStr(StringKeysLbl.LBL_DCN_CONNECTIONERROR)+connectioCount);
//						//每次尝试5次连接，还不成功就重新注册RMI
////						if(connectioCount % 5 == 0){
////							ServerConstant.registry = LocateRegistry.createRegistry(ConstantUtil.RMI_PORT);
////							ServiceInitUtil serviceInitUtil = new ServiceInitUtil(ServerConstant.registry,1);
////						}
//					}else{
//						connectioCount = 0;
//						ptnf.getLoginLabel().setText(ptnf.getLogLabel()+"   "+ResourceUtil.srcStr(StringKeysLbl.LBL_DCN_CONNECTION));
//					}
//					ptnf.getNeCountLable().setText(neCount());
//					
//				} catch (Exception e) {
//					ExceptionManage.dispose(e, this.getClass());
//				}
				Thread.sleep(3000);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 告警是否变化 验证条件为：告警数量、告警最大ID号
	 * 
	 * @author kk
	 * @return false 没变化 true 有变化 需要更新拓扑图
	 * @throws Exception
	 */
	private boolean alarmIsChange(CurAlarmService_MB curAlarmService){

		boolean flag = false;
		int alarmNum = 0; // 查询告警数量
		int alarmMaxId = 0; // 查询告警最大ID号
//		CurAlarmService curAlarmService = null;
		try {
//			curAlarmService = (CurAlarmService) ConstantUtil.serviceFactory.newService(Services.CurrentAlarm);
			alarmNum = curAlarmService.selectCount();
			alarmMaxId = curAlarmService.selectMaxId();

			if (this.alarmNum != alarmNum || this.alarmMaxId != alarmMaxId) {
				flag = true;
				this.alarmMaxId = alarmMaxId;
				this.alarmNum = alarmNum;
			}
			// System.out.println("本次查询alarmNum=" + alarmNum + ",alarmMaxId=" + alarmMaxId);
			// System.out.println("上次查询alarmNum=" + this.alarmNum + ",alarmMaxId=" + this.alarmMaxId);

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
//			UiUtil.closeService(curAlarmService);
		}
		return flag;
	}

	//用于定时来跟新客户端是否是连上服务器
	private boolean isLine(){
		 String host = ConstantUtil.serviceIp;
		 try {
			 InetAddress address = null;
			 if (host != null && host.trim().length() > 0) {
				 address = InetAddress.getByName(host);
				 if (address.isReachable(5000)){
					 return true;
				 }
			 }
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return false;
	 }
	
	/**
	 * 检查RMI连接是否正常
	 * @return
	 * @throws Exception
	 */
	private boolean verificationRmi(){
		DispatchUtil dispatchUtil = null;
		ServiceBean serviceBean = null;
		boolean flag = false;
		try {
			if(!isLine()){
				return false;
			}
			dispatchUtil = new DispatchUtil(RmiKeys.RMI_INIT);
			serviceBean = dispatchUtil.init();
			if (null != serviceBean) {
				// 1为成功
				if (serviceBean.getConnectionResult() == 1) {
					ConstantUtil.serviceBean = serviceBean;
					flag = true;
				} else {
					flag = false;
				}
			}

		} catch (Exception e) {
			flag = false;
		} finally {
			dispatchUtil = null;
			serviceBean = null;
		}
		return flag;
	}
	
	/**
	 *网元统计 
	 * @return
	 */
	private String neCount(){
		
		String neLabel = "";
		SiteService_MB siteService = null;
		List<SiteInst> siteList = null;
		int onLineCount = 0;
		int breakLineCount = 0;
		int offLineCount = 0;
		int neCount = 0;
		try {
			siteList = new ArrayList<SiteInst>();
			siteService = (SiteService_MB)ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			siteList = siteService.select();
			if(siteList != null && siteList.size()>0){
				neCount = siteList.size();
				for(SiteInst siteInst : siteList){
					if(siteInst.getLoginstatus() ==1){
						onLineCount ++;
					}else{
						// == 370代表虚拟网元
						if(siteInst.getSiteType()==370){
							offLineCount ++;
						}else{
							breakLineCount ++;
						}
					}
				}
			}
			neLabel = ResourceUtil.srcStr(StringKeysLbl.LBL_NECOUNT)+neCount+"  |"+ResourceUtil.srcStr(StringKeysLbl.LBL_ONLINECOUNT) + onLineCount+
			                                                                 "  |"+ResourceUtil.srcStr(StringKeysLbl.LBL_BREAKLINECOUNT)+breakLineCount+
			                                                                 "  |"+ResourceUtil.srcStr(StringKeysLbl.LBL_OFFLINECOUNT)+offLineCount;
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}finally{
			UiUtil.closeService_MB(siteService);
		}
		return neLabel;
	}
}
