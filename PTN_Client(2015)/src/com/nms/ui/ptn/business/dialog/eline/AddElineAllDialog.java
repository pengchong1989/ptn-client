﻿package com.nms.ui.ptn.business.dialog.eline;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.path.Segment;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.path.eth.ElineInfo;
import com.nms.db.bean.ptn.path.pw.MsPwInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Lsp;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.bean.ptn.qos.QosInfo;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EActiveStatus;
import com.nms.db.enums.EManufacturer;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EPwType;
import com.nms.db.enums.EServiceType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.ptn.port.AcPortInfoService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.AutoNamingUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.VerifyNameUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.business.dialog.tunnel.AddTunnelPathDialog;
import com.nms.ui.ptn.business.eline.ElineBusinessPanel;
import com.nms.ui.ptn.ne.ac.controller.AcHandlerController;
import com.nms.ui.ptn.ne.ac.view.AddACDialog;

/**
 * 快速配置业务弹出界面
 * 
 * @author kk
 * 
 */
public class AddElineAllDialog extends AddTunnelPathDialog {

	private static final long serialVersionUID = 1L;
	private SiteInst siteInst_a = null; // A端网元对象
	private SiteInst siteInst_z = null; // Z端网元对象
	private ElineBusinessPanel elineBusinessPanel;

	public AddElineAllDialog(ElineBusinessPanel elineBusinessPanel) {
		super();
		super.getPtnSpinnerNumber().setEnabled(false);
		try {
			this.setTitle(ResourceUtil.srcStr(StringKeysBtn.BTN_ADD_RAPID) + "Eline");
			this.elineBusinessPanel = elineBusinessPanel;
			// 添加监听
			this.addListener();

			// 显示窗体
			UiUtil.showWindow(this, 1000, 620);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * 重写父类添加控件方法
	 */
	@Override
	public List<Component[]> addComponent() {
		List<Component[]> list = new ArrayList<Component[]>();
		Component[] components_a = null;
		Component[] components_z = null;
		try {

			this.initComponent();
			components_a = new Component[] { this.lblAcname_a, this.cmbAcname_a, this.btnConfig_a };
			components_z = new Component[] { this.lblAcname_z, this.cmbAcname_z, this.btnConfig_z };

			list.add(components_a);
			list.add(components_z);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

		return list;
	}

	/**
	 * 添加监听事件
	 */
	private void addListener() {
		// 移除按钮本来的事件
		if (super.getBtnSave().getActionListeners().length == 1) {
			super.getBtnSave().removeActionListener(super.getBtnSave().getActionListeners()[0]);
		}
		// 添加新的事件
		super.getBtnSave().addActionListener(new MyActionListener() {

			@Override
			public boolean checking() {
			//	AddTunnelPathDialog addTunnelPathDialog = AddTunnelPathDialog.getDialog();
				if (checkValue() && getTunnelAction().checking(AddElineAllDialog.this)) {
					// 验证是否选择了AC
					if (cmbAcname_a.getSelectedIndex() == -1 || cmbAcname_z.getSelectedIndex() == -1) {
						DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_AC));
						return false;
					} else {
						return true;
					}
				} else {
					return false;
				}
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					saveEline();
				} catch (Exception exception) {
					ExceptionManage.dispose(exception,this.getClass());
				}
				
			}
		});

		// A端按钮添加监听
		this.btnConfig_a.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				showAddAc("a");
			}
		});

		// Z端按钮添加监听
		this.btnConfig_z.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				showAddAc("z");
			}
		});

	}

	/**
	 * 显示创建AC界面
	 * 
	 * @param type
	 *            a或者是z
	 */
	private void showAddAc(String type) {
		int siteId = 0;
		AddACDialog dialog = null;
		AcHandlerController addacController = null;
		SiteService_MB siteService = null;
		try {
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);

			if ("a".equals(type)) {
				if (null == this.siteInst_a) {
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PLEASE_SELECT_A));
					return;
				}
				siteId = this.siteInst_a.getSite_Inst_Id();
			} else {
				if (null == this.siteInst_z) {
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PLEASE_SELECT_Z));
					return;
				}
				siteId = this.siteInst_z.getSite_Inst_Id();
			}

			dialog = new AddACDialog(null,siteId);
			addacController = new AcHandlerController(null, dialog, siteId);
			addacController.initData(true,siteId);
			if (siteService.getManufacturer(siteId) == EManufacturer.WUHAN.getValue()) {
				dialog.getStep1().setVisible(true);
			} else {
				dialog.getStep1_cx().setVisible(true);
			}

			// 刷新下拉列表
			this.comboBoxData(type);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
		}
	}

	/**
	 * 初始化控件
	 */
	private void initComponent() {
		this.lblAcname_a = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_A_MARGIN_AC));
		this.lblAcname_z = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_Z_MARGIN_AC));
		this.cmbAcname_a = new JComboBox();
		this.cmbAcname_z = new JComboBox();
		this.btnConfig_a = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CREATE));
		this.btnConfig_z = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CREATE));
	}

	/**
	 * AC下拉列表赋值
	 * 
	 * @param type
	 *            a 或者 a
	 */
	private void comboBoxData(String type) {
		JComboBox comboBox = null;
		int siteId = 0;
		AcPortInfoService_MB acInfoService = null;
		List<AcPortInfo> acPortInfoList = null;
		DefaultComboBoxModel defaultComboBoxModel = null;
		try {
			if ("a".equals(type)) {
				comboBox = this.cmbAcname_a;
				siteId = this.siteInst_a.getSite_Inst_Id();
			} else {
				comboBox = this.cmbAcname_z;
				siteId = this.siteInst_z.getSite_Inst_Id();
			}

			// 从数据库查询ac集合
			acInfoService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			acPortInfoList = acInfoService.selectBySiteId(siteId);

			// 清空下拉列表 重新赋值
			defaultComboBoxModel = new DefaultComboBoxModel();
			for (AcPortInfo acPortInfo : acPortInfoList) {
				if (acPortInfo.getIsUser() == 0)
					defaultComboBoxModel.addElement(new ControlKeyValue(acPortInfo.getId() + "", acPortInfo.getName(), acPortInfo));
			}
			comboBox.setModel(defaultComboBoxModel);

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(acInfoService);
		}

	}

	/**
	 * 保存eline
	 * 
	 * @throws Exception
	 */
	private void saveEline() throws Exception {
		Tunnel tunnel = null;
		Tunnel tunnel_db = null;
		DispatchUtil tunnelDispatch = new DispatchUtil(RmiKeys.RMI_TUNNEL);
		String result = null;
		PwInfo pwinfo = null;
		PwInfo pwinfo_db = null;
		DispatchUtil pwDispatch = new DispatchUtil(RmiKeys.RMI_PW);
		ElineInfo elineInfo = null;
		DispatchUtil elineDispatch = new DispatchUtil(RmiKeys.RMI_ELINE);
		try {

			// 获取界面上的值
			tunnel = this.getTunnel(this);
			// 验证数据库中有没有此类型的tunnel 如果没有返回null
			tunnel_db = this.existTunnel(tunnel);
			if (null == tunnel_db) {
				// 调用新增tunnel方法
				List<Tunnel> tunnelList = new ArrayList<Tunnel>();
				tunnelList.add(tunnel);
				result = tunnelDispatch.excuteInsert(tunnelList);
				if(tunnel.getProtectTunnel() != null){
					tunnel.getProtectTunnel().setTunnelType(tunnel.getTunnelType());
					tunnel.getProtectTunnel().setRole(null);
					this.insertTunnelLog(super.getBtnSave(), EOperationLogType.TUNNELINSERT.getValue(), result, null, tunnel.getProtectTunnel());
				}
				tunnel.setProtectTunnel(null);
				tunnel.setRole(null);
				this.insertTunnelLog(super.getBtnSave(), EOperationLogType.TUNNELINSERT.getValue(), result, null, tunnel);
				if (result.contains(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))) {
					// 重新取tunnel对象
					tunnel_db = this.existTunnel(tunnel);
				} else {
					DialogBoxUtil.errorDialog(null, result);
					return;
				}
			}
			// 处理pw
			pwinfo = this.createPwinfo(tunnel_db);
			// 验证数据库中有没有此类型的pwinfo 如果没有返回null
			pwinfo_db = this.existPwInfo(pwinfo);

			if (null == pwinfo_db) {
				// 调用新增pwinfo方法
				List<PwInfo> pwList = new ArrayList<PwInfo>();
				pwList.add(pwinfo);
				result = pwDispatch.excuteInsert(pwList);
				PwInfoService_MB pwService = null;
				try {
					pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
					pwinfo = pwService.select(pwinfo).get(0);
					this.insertPwLog(EOperationLogType.PWINSERT.getValue(), result, null, pwinfo);
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				} finally {
					UiUtil.closeService_MB(pwService);
				}
				if (result.contains(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))) {
					// 重新取pwinfo对象
					pwinfo_db = this.existPwInfo(pwinfo);
				} else {
					DialogBoxUtil.errorDialog(null, result);
					return;
				}
			}
			// 创建eline对象。
			elineInfo = this.createElineInfo(pwinfo_db);
			result = elineDispatch.excuteInsert(elineInfo);
			//添加日志记录
			AddOperateLog.insertOperLog(super.getBtnSave(), EOperationLogType.ELINEINSERT.getValue(), result, 
					null, elineInfo, elineInfo.getaSiteId(), elineInfo.getName(), "eline");
			AddOperateLog.insertOperLog(super.getBtnSave(), EOperationLogType.ELINEINSERT.getValue(), result, 
					null, elineInfo, elineInfo.getzSiteId(), elineInfo.getName(), "eline");
			//添加日志记录
			int operationResult=0;
			if(result.contains(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))){
				operationResult=1;
			}else{
				operationResult=2;
			}
			super.getBtnSave().setResult(operationResult);
			super.getBtnSave().setOperateKey(EOperationLogType.ADDRAPID.getValue());
			DialogBoxUtil.succeedDialog(null, result);
			if (result.contains(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))) {
				// 刷新eline列表 关闭本窗口
				this.elineBusinessPanel.getController().refresh();
				this.dispose();
			} else {
				return;
			}

		} catch (Exception e) {
			throw e;
		} finally {
			//addTunnelPathDialog = null;
			tunnel = null;
			tunnel_db = null;
			tunnelDispatch = null;
			result = null;
			pwinfo = null;
			pwinfo_db = null;
			pwDispatch = null;
			elineInfo = null;
			elineDispatch = null;
		}
	}
	
	private void insertTunnelLog(PtnButton ptnButton, int operationType, String message, Tunnel tunnelBefore, Tunnel tunnel){
		List<Integer> siteIdList = new ArrayList<Integer>();
		SiteService_MB siteService = null;
		PortService_MB portService = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			if(tunnelBefore != null){
				tunnelBefore.setShowSiteAname(siteService.getSiteName(tunnelBefore.getaSiteId()));
				tunnelBefore.setShowSiteZname(siteService.getSiteName(tunnelBefore.getzSiteId()));
				for(Lsp lsp : tunnelBefore.getLspParticularList()){
					lsp.setaSiteName(siteService.getSiteName(lsp.getASiteId()));
					lsp.setzSiteName(siteService.getSiteName(lsp.getZSiteId()));
					lsp.setaPortName(portService.getPortname(lsp.getAPortId()));
					lsp.setzPortName(portService.getPortname(lsp.getZPortId()));
				}
				this.getOamSiteName(tunnelBefore.getOamList(), tunnelBefore.getaSiteId(), tunnelBefore.getzSiteId(), tunnelBefore.getShowSiteAname(), tunnelBefore.getShowSiteZname(), siteService);
			}
			tunnel.setShowSiteAname(siteService.getSiteName(tunnel.getaSiteId()));
			tunnel.setShowSiteZname(siteService.getSiteName(tunnel.getzSiteId()));
			this.getOamSiteName(tunnel.getOamList(), tunnel.getaSiteId(), tunnel.getzSiteId(), tunnel.getShowSiteAname(), tunnel.getShowSiteZname(), siteService);
			for(Lsp lsp : tunnel.getLspParticularList()){
				lsp.setaSiteName(siteService.getSiteName(lsp.getASiteId()));
				lsp.setzSiteName(siteService.getSiteName(lsp.getZSiteId()));
				lsp.setaPortName(portService.getPortname(lsp.getAPortId()));
				lsp.setzPortName(portService.getPortname(lsp.getZPortId()));
			}
			for(Lsp lsp : tunnel.getLspParticularList()){
				if(!siteIdList.contains(lsp.getASiteId())){
					siteIdList.add(lsp.getASiteId());
					AddOperateLog.insertOperLog(ptnButton, operationType, message, tunnelBefore, tunnel, lsp.getASiteId(), tunnel.getTunnelName(), "Tunnel");
				}
				if(!siteIdList.contains(lsp.getZSiteId())){
					siteIdList.add(lsp.getZSiteId());
					AddOperateLog.insertOperLog(ptnButton, operationType, message, tunnelBefore, tunnel, lsp.getZSiteId(), tunnel.getTunnelName(), "Tunnel");
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(portService);
		}
	}
	
	private void getOamSiteName(List<OamInfo> oamList, int aSiteId, int zSiteId, String aSiteName, String zSiteName, SiteService_MB siteService){
		if(oamList != null && oamList.size() > 0){
			for (OamInfo oamInfo : oamList) {
				if(oamInfo.getOamMep() != null){
					if(oamInfo.getOamMep().getSiteId() == aSiteId){
						oamInfo.getOamMep().setSiteName(aSiteName);
					}else if(oamInfo.getOamMep().getSiteId() == zSiteId){
						oamInfo.getOamMep().setSiteName(zSiteName);
					}
				}
				if(oamInfo.getOamMip() != null){
					oamInfo.getOamMip().setSiteName(siteService.getSiteName(oamInfo.getOamMip().getSiteId()));
				}
			}
		}
	}
	
	private void insertPwLog(int operationType, String result, PwInfo oldPw, PwInfo newPw){
		SiteService_MB siteService = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			this.getOamSiteName(newPw.getOamList(), newPw.getASiteId(), newPw.getZSiteId(), newPw.getShowaSiteName(), newPw.getShowzSiteName(), siteService);
			AddOperateLog.insertOperLog(super.getBtnSave(), operationType, result, oldPw, newPw, newPw.getASiteId(), newPw.getPwName(), "pwInfo");
			AddOperateLog.insertOperLog(super.getBtnSave(), operationType, result, oldPw, newPw, newPw.getZSiteId(), newPw.getPwName(), "pwInfo");
			if(newPw.getMsPwInfos() != null && newPw.getMsPwInfos().size() > 0){
				for (MsPwInfo msPw : newPw.getMsPwInfos()) {
					AddOperateLog.insertOperLog(super.getBtnSave(), operationType, result, oldPw, newPw, msPw.getSiteId(), newPw.getPwName(), "pwInfo");
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
		}
	}

	
    //验证eline的名称是否存在
	private boolean checkValue() {
		boolean flag = false;
		try {
			VerifyNameUtil verifyNameUtil=new VerifyNameUtil();
			if (verifyNameUtil.verifyName(EServiceType.ELINE.getValue(), super.getjTextField1().getText().trim(), null)) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
				return false;
			}
			
			flag = true;
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
		return flag;
	}
	
	

	/**
	 * 创建eline对象
	 * 
	 * @param pwinfo
	 *            pw对象
	 * @return
	 * @throws Exception
	 */
	private ElineInfo createElineInfo(PwInfo pwinfo) throws Exception {
		ElineInfo elineInfo = null;
		ControlKeyValue controlKeyValue_ac_a = null;
		ControlKeyValue controlKeyValue_ac_z = null;
		try {
			elineInfo = new ElineInfo();
			elineInfo.setPwId(pwinfo.getPwId());
			elineInfo.setPwName(pwinfo.getPwName());
			elineInfo.setServiceType(EServiceType.ELINE.getValue());
			elineInfo.setActiveStatus(pwinfo.getPwStatus());
			elineInfo.setCreateTime(DateUtil.getDate(DateUtil.FULLTIME));
			elineInfo.setCreateUser(pwinfo.getCreateUser());
			elineInfo.setaSiteId(pwinfo.getASiteId());
			elineInfo.setzSiteId(pwinfo.getZSiteId());
			elineInfo.setIsSingle(pwinfo.getIsSingle());
			AutoNamingUtil namingUtil = new AutoNamingUtil();
			elineInfo.setName(namingUtil.autoNaming(elineInfo, null, null)+"");
			controlKeyValue_ac_a = (ControlKeyValue) this.cmbAcname_a.getSelectedItem();
			controlKeyValue_ac_z = (ControlKeyValue) this.cmbAcname_z.getSelectedItem();

			if(elineInfo.getaSiteId() == ((AcPortInfo)controlKeyValue_ac_a.getObject()).getSiteId()){
				elineInfo.setaAcId(Integer.parseInt(controlKeyValue_ac_a.getId()));
				elineInfo.setzAcId(Integer.parseInt(controlKeyValue_ac_z.getId()));
				elineInfo.setaAcName(((AcPortInfo)controlKeyValue_ac_a.getObject()).getName());
				elineInfo.setzAcName(((AcPortInfo)controlKeyValue_ac_z.getObject()).getName());
			}else{
				elineInfo.setaAcId(Integer.parseInt(controlKeyValue_ac_z.getId()));
				elineInfo.setzAcId(Integer.parseInt(controlKeyValue_ac_a.getId()));
				elineInfo.setaAcName(((AcPortInfo)controlKeyValue_ac_z.getObject()).getName());
				elineInfo.setzAcName(((AcPortInfo)controlKeyValue_ac_a.getObject()).getName());
			}
		} catch (Exception e) {
			throw e;
		} finally {
			controlKeyValue_ac_a = null;
			controlKeyValue_ac_z = null;
		}
		return elineInfo;
	}

	/**
	 * 创建pw对象
	 * 
	 * @param tunnel
	 * @return
	 * @throws Exception
	 */
	private PwInfo createPwinfo(Tunnel tunnel) throws Exception {
		PwInfo pwInfo = null;
		List<QosInfo> qosInfoList = null;
		SiteService_MB siteService=null;
		try {
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			pwInfo = new PwInfo();
			pwInfo.setPayload(496);
			pwInfo.setPwStatus(tunnel.getTunnelStatus());
			pwInfo.setTunnelId(tunnel.getTunnelId());
			pwInfo.setASiteId(tunnel.getASiteId());
			pwInfo.setZSiteId(tunnel.getZSiteId());
			pwInfo.setCreateTime(DateUtil.getDate(DateUtil.FULLTIME));
			pwInfo.setCreateUser(tunnel.getCreateUser());
			pwInfo.setType(EPwType.ETH);
			pwInfo.setIsSingle(tunnel.getIsSingle());
			AutoNamingUtil namingUtil = new AutoNamingUtil();
			pwInfo.setPwName(namingUtil.autoNaming(pwInfo, null, null)+"");
			pwInfo.setAoppositeId(siteService.getSiteID(tunnel.getZSiteId()));
			pwInfo.setZoppositeId(siteService.getSiteID(tunnel.getASiteId()));
			pwInfo.setBusinessType("0");
			qosInfoList = new ArrayList<QosInfo>();
			for (QosInfo qosInfo : tunnel.getQosList()) {
				qosInfo.setId(0);
				qosInfo.setSiteId(0);
				qosInfo.setGroupId(0);
				qosInfo.setCir(0);
				qosInfo.setPir(0);
				qosInfoList.add(qosInfo);
			}
			pwInfo.setQosList(qosInfoList);

		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(siteService);
		}
		return pwInfo;
	}

	/**
	 * 以页面的tunnel对象为条件。 查询出有没有与此tunnel相匹配的。
	 * 
	 * @param tunnel
	 *            页面的tunnel对象
	 * @return 如果有匹配的。返回匹配的tunnel对象。没有返回null
	 * @throws Exception
	 */
	private Tunnel existTunnel(Tunnel tunnel) throws Exception {
		TunnelService_MB tunnelService = null;
		try {
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);

			return tunnelService.selectExistTunnel(tunnel);

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(tunnelService);
		}
	}

	/**
	 * 以页面的pwinfo对象为条件。 查询出有没有与此pwinfo相匹配的。
	 * 
	 * @param pwinfo
	 *            页面的pwinfo对象
	 * @return 如果有匹配的。返回匹配的pwinfo对象。没有返回null
	 * @throws Exception
	 */
	private PwInfo existPwInfo(PwInfo pwInfo) throws Exception {
		PwInfoService_MB pwInfoService = null;
		try {
			pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			return pwInfoService.selectExistPwinfo(pwInfo);
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(pwInfoService);
		}
	}

	/**
	 * 获取界面上的值组成的tunnel对象
	 * 
	 * @return tunnel对象
	 * @throws Exception
	 */
	private Tunnel getTunnel(AddTunnelPathDialog addTunnelPathDialog) throws Exception {
		List<Lsp> lspParticularList = new ArrayList<Lsp>();
		Code code_type = null;
		Tunnel tunnel = null;
		int workindex = 0;
		List<Segment> Segments = null;
		List<Segment> proSegments = null;
		try {
			code_type = (Code) ((ControlKeyValue) super.getCmbType().getSelectedItem()).getObject();

			tunnel = super.getTunnel();
			Segments = super.getWorkSg();
			super.getTunnelAction().initPort(addTunnelPathDialog, Segments);
			super.getTunnelAction().setSegmenttoTunnel(addTunnelPathDialog, Segments);
			// 给lsp的前后向标签赋值
			List<Lsp> lspList = super.getTunnelAction().setLabelToLsp(super.getTunnelAction().getLSPfromRoute(Segments,addTunnelPathDialog), addTunnelPathDialog.getLabelWorkList());
			lspParticularList.addAll(lspList);
			tunnel.setOamList(super.getOamList());
			tunnel.setQosList(super.getQosList());

			tunnel.setAPortId(super.getPortInst_a().getPortId());
			tunnel.setASiteId(super.getPortInst_a().getSiteId());
			tunnel.setDirection(super.getjTextArea1().getText());
			tunnel.setTunnelStatus(super.getjCheckBox1().isSelected() == true ? EActiveStatus.ACTIVITY.getValue() : EActiveStatus.UNACTIVITY.getValue());
			tunnel.setTunnelName("tunnel/" + super.getjTextField1().getText());
			tunnel.setZPortId(super.getPortInst_z().getPortId());
			tunnel.setZSiteId(super.getPortInst_z().getSiteId());
			tunnel.setIsReverse(super.getjCheckBox2().isSelected() == true ? 1 : 0);
			tunnel.setCreateUser(ConstantUtil.user.getUser_Name());
			tunnel.setLspParticularList(lspParticularList);
			tunnel.setTunnelType(code_type.getId() + "");
			if (super.getTxtWaitTime().getTxtData().trim().length() > 0) {
				tunnel.setWaittime(Integer.parseInt(super.getTxtWaitTime().getTxtData()));
			}
			// dialog.getTxtDelayTime().getTxt().getText()
			// if (super.getTxtDelayTime().getText().trim().length() > 0) {
			if (super.getTxtDelayTime().getTxtData().trim().length() > 0) {
				tunnel.setDelaytime(Integer.parseInt(super.getTxtDelayTime().getTxtData()));
			}

			tunnel.setApsenable(super.getChkAps().isSelected() == true ? 1 : 0);
			if ("2".equals(code_type.getCodeValue())) {
				int proindex = 0;
				if (workindex == 0) {
					proindex = 1;
				}
				proSegments = super.getProSg();
				super.getTunnelAction().getProtectTunnel(tunnel, proSegments, addTunnelPathDialog);

			}

		} catch (Exception e) {
			throw e;
		}finally{
			 code_type = null;
			 workindex = 0;
			 Segments = null;
			 proSegments = null;
		}
		return tunnel;
	}

	public void setSiteInst_a(SiteInst siteInst_a) {
		this.siteInst_a = siteInst_a;
		this.comboBoxData("a");
	}

	public void setSiteInst_z(SiteInst siteInst_z) {
		this.siteInst_z = siteInst_z;
		this.comboBoxData("z");
	}

	private JLabel lblAcname_a; // A端显示文本
	private JLabel lblAcname_z; // Z端显示文本
	private JComboBox cmbAcname_a; // A端下拉列表
	private JComboBox cmbAcname_z; // Z端下拉列表
	private JButton btnConfig_a; // A端新建按钮
	private JButton btnConfig_z; // Z端新建按钮

}
