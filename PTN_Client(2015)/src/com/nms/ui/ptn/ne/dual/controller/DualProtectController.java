package com.nms.ui.ptn.ne.dual.controller;

import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.ptn.path.protect.DualProtect;
import com.nms.db.bean.ptn.path.protect.DualTunnel;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.ptn.port.DualProtectService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.ne.dual.view.DualProtectPanel;
import com.nms.ui.ptn.ne.dual.view.EditDualProtectDialog;

/**
 * 双规保护按钮控制类
 * @author dzy
 *
 */
public class DualProtectController extends AbstractController{
	private DualProtectPanel dualProtectPanel = null; // msp主界面面板
	/**
	 * 创建一个新的实例
	 * @param dualProtectPanel 主界面面板
	 */
	public DualProtectController(DualProtectPanel dualProtectPanel) {
		this.dualProtectPanel = dualProtectPanel;
	}
	/**
	 * 刷新
	 */
	@Override
	public void refresh() throws Exception {
		DualProtectService_MB dualProtectServiceMB = null;
		List<DualProtect> dualProtectList = null;
		try {

			dualProtectServiceMB = (DualProtectService_MB) ConstantUtil.serviceFactory.newService_MB(Services.DUALPROTECTSERVICE);
			dualProtectList = dualProtectServiceMB.selectBySite(ConstantUtil.siteId);
			this.dualProtectPanel.clear();
			this.dualProtectPanel.initData(dualProtectList);

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(dualProtectServiceMB);
		}
	}
	/**
	 * 打开新建窗口
	 */
	@Override
	public void openCreateDialog() {
		new EditDualProtectDialog(null, this.dualProtectPanel);
	}

	/**
	 * 打开修改窗口
	 */
	@Override
	public void openUpdateDialog() {
		new EditDualProtectDialog(this.dualProtectPanel.getSelect(), this.dualProtectPanel);
	}

	/**
	 * 删除数据
	 */
	@Override
	public void delete() {
		DispatchUtil dualDispatch = null;
		List<DualProtect> dualProtect = null; 
		String result;
		try {
			dualDispatch=new DispatchUtil(RmiKeys.RMI_DUALPROTECT);
			dualProtect = this.dualProtectPanel.getAllSelect();
			result = dualDispatch.excuteDelete(dualProtect);
			DialogBoxUtil.succeedDialog(null, result);
			this.refresh();
			//添加操作日志
			PtnButton deleteButton=(PtnButton) this.dualProtectPanel.getDeleteButton();
			deleteButton.setOperateKey(EOperationLogType.DUALPROTECTDELETE.getValue());
			int operationResult=0;
			if(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS).equals(result)){
				operationResult=1;
			}else{
				operationResult=2;
			}
			deleteButton.setResult(operationResult);
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
			mspDispatch=new DispatchUtil(RmiKeys.RMI_DUALPROTECT);
			mspDispatch.synchro(ConstantUtil.siteId);
			this.refresh();
			//添加日志记录
			PtnButton deleteButton=(PtnButton) this.dualProtectPanel.getSynchroButton();
			deleteButton.setOperateKey(EOperationLogType.DUALPROTECTSYNCHRO.getValue());
			deleteButton.setResult(1);
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
			this.dualProtectPanel.getDualTunnelPanel().clear();
			this.dualProtectPanel.getDualTunnelPanel().initData(this.getDualInfoList(this.dualProtectPanel.getSelect()));

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}
	/**
	 * 转换 DualTunnel 对象
	 * @param dualProtect  dualProtect对象
	 * @return
	 * @throws Exception
	 */
	private List<DualTunnel> getDualInfoList(DualProtect dualProtect) throws Exception {
		
		List<DualTunnel> dualTunnelList = new ArrayList<DualTunnel>();
		DualTunnel dualTunnelBreakOver = new DualTunnel();
		DualTunnel dualTunnelRelation ;
		dualTunnelBreakOver.setTunnelName(dualProtect.getBreakoverTunnel().getTunnelName());
		dualTunnelBreakOver.setProtectType(dualProtect.getProtectType());
		dualTunnelBreakOver.setRole(UiUtil.getCodeByValue("ROLE", "1").getId());
		dualTunnelList.add(dualTunnelBreakOver);
		if(null!=dualProtect.getRelevanceTunnelList()&&dualProtect.getRelevanceTunnelList().size()>0){
			for(Tunnel tunnel:dualProtect.getRelevanceTunnelList()){
				dualTunnelRelation = new DualTunnel();
				dualTunnelRelation.setTunnelName(tunnel.getTunnelName());
				dualTunnelRelation.setProtectType(dualProtect.getProtectType());
				dualTunnelRelation.setRole(UiUtil.getCodeByValue("ROLE", "0").getId());
				dualTunnelList.add(dualTunnelRelation);
			}
		}
		return dualTunnelList;
	}
}
