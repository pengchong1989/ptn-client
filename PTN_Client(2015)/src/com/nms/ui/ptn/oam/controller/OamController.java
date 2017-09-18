﻿package com.nms.ui.ptn.oam.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.table.DefaultTableModel;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.path.Segment;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.oam.OamMepInfo;
import com.nms.db.bean.ptn.oam.OamMipInfo;
import com.nms.db.bean.ptn.path.eth.ElineInfo;
import com.nms.db.bean.ptn.path.pw.MsPwInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Lsp;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.bean.ptn.port.PortLagInfo;
import com.nms.db.enums.EManufacturer;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EPwType;
import com.nms.db.enums.EServiceType;
import com.nms.db.enums.OamTypeEnum;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.oam.OamInfoService_MB;
import com.nms.model.ptn.path.eth.ElineInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.ptn.port.AcPortInfoService_MB;
import com.nms.model.ptn.port.PortLagService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.util.ComboBoxDataUtil;
import com.nms.ui.ptn.basicinfo.dialog.segment.AddSegment;
import com.nms.ui.ptn.business.dialog.eline.AddElineDialog;
import com.nms.ui.ptn.business.dialog.pwpath.AddPDialog;
import com.nms.ui.ptn.business.dialog.tunnel.AddTunnelPathDialog;
import com.nms.ui.ptn.business.dialog.tunnel.action.TunnelAction;
import com.nms.ui.ptn.oam.Node.view.dialog.OamInfoSingleDialog;
import com.nms.ui.ptn.oam.view.OamBasicPanel;
import com.nms.ui.ptn.oam.view.OamInfoCXPanel;
import com.nms.ui.ptn.oam.view.OamInfoPanel;
import com.nms.ui.ptn.oam.view.OamInfoWHPanel;
import com.nms.ui.ptn.oam.view.dialog.OamInfoDialog;

public class OamController{
	private OamInfoDialog dialog = null; // oam 窗口
	private int aManufacturer; 	//a端网元厂商
	private int zManufacturer; 	//z端网元厂商
	private int defaultValue = 1;	//mepId;
	private String megId ; 	//megId;
	private int isSingle; //业务区分  0=网络侧，1=单网络侧
	private String busiType; 	//业务类型
	private Object obj; 	//业务对象
	String resultStr = null; //返回字符串
	private OamInfo oamA;
	private OamInfo oamZ;
	/**
	 * 创建一个新的实例
	 * 
	 * @param oamInfoDialog oam编辑页面
	 */
	public OamController(OamInfoDialog dialog,JDialog jdialog) {
		this.dialog = dialog;
		this.obj = this.dialog.getObj();
		this.isSingle = this.dialog.getIsSingle();
		this.busiType = this.dialog.getBusiType();
		this.addListeners(jdialog);
	}

	/**
	 * 打开oam配置面板
	 */
	public void init (JDialog dialog)  {
		int width = 460;
		int height = 0;
		try{
			//初始化所有业务名称下拉菜单
			this.setBusiName();
			this.dialog.getOamPanel();
			oamA = new OamInfo();
			oamZ = new OamInfo(); 
			oamA.setOamMep( new OamMepInfo());
			oamZ.setOamMep( new OamMepInfo());
			//初始化各业务对象（单网元）
			initBusiObjOam();
			//初始化各业务面板
			if (this.busiType.equals(EServiceType.SECTION.toString())) {
				this.initSegmentData(this.dialog.getOamPanel(), (Segment)obj,oamA,oamZ);
			} else if (this.busiType.equals(EServiceType.TUNNEL.toString())) {
				AddTunnelPathDialog tunnelDialog=(AddTunnelPathDialog) dialog;
				this.initTunnelData( this.dialog.getOamPanel(), (Tunnel)obj,oamA,oamZ,tunnelDialog);
			} else if (this.busiType.equals(EServiceType.PW.toString())) {
				AddPDialog addPDialog=(AddPDialog) dialog;
				this.initPWData( this.dialog.getOamPanel(), (PwInfo)obj,oamA,oamZ,addPDialog);
			} else if (this.busiType.equals(EServiceType.ELINE.toString())) {
				this.initELineData( this.dialog.getOamPanel(), (ElineInfo)obj,oamA,oamZ);
			}
			//初始化窗口大小
			height =  this.dialog.getDialogHeight() + 80;
			width =  this.dialog.getDialogWidth()+70;
			if(0 == this.isSingle &&(EServiceType.TUNNEL.toString().equals(this.busiType)|| EServiceType.PW.toString().equals(this.busiType)))
				width += 200;
			
			if(ResourceUtil.language.equals("zh_CN")){
				UiUtil.showWindow(this.dialog, width,height);
			}else{
				if(busiType.equals("SECTION")){
					UiUtil.showWindow(this.dialog, width+220,height);
				}else{
					UiUtil.showWindow(this.dialog, width+50,height);
				}
			}
			
		}catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			oamA = null;
			oamZ = null;
		}
	}
	
	/**
	  * 单网元测创建业务对象
	  */
	private void initBusiObjOam() {
		if(this.isSingle ==0||null==this.obj)
			return;
		List<OamInfo> oamInfoList = new ArrayList<OamInfo>();
		OamInfo oamInfo = new OamInfo();
		oamInfo = (OamInfo) this.obj;
		oamInfoList.add(oamInfo);
		if (this.busiType.equals("SECTION")){
			this.obj = new Segment();
			((Segment)obj).setOamList(oamInfoList);
			((Segment)obj).setASITEID(oamInfo.getOamMep().getSiteId());
		} else if (this.busiType.equals("TUNNEL")){
			this.obj = new Tunnel();
			((Tunnel)obj).setOamList(oamInfoList);
			if(null!=oamInfo.getOamMep()){
				((Tunnel)obj).setASiteId(oamInfo.getOamMep().getSiteId());
			}else{
				((Tunnel)obj).setASiteId(oamInfo.getOamMip().getSiteId());
			}
		} else if (this.busiType.equals("PW")){
			this.obj = new PwInfo();
			((PwInfo)obj).setOamList(oamInfoList);
			((PwInfo)obj).setASiteId(oamInfo.getOamMep().getSiteId());
		}else{
			this.obj = new ElineInfo();
			((ElineInfo)obj).setOamList(oamInfoList);
			((ElineInfo)obj).setaSiteId(oamInfo.getOamMep().getSiteId());
		}
	}

	/**
	 * 初始化Eline
	 * @param panel 		oam主面板
	 * @param ElineInfo	Eline对象
	 * @param oamA			A端OAM
	 * @param oamZ			Z端OAM
	 * @throws Exception 
	 */
	private void initELineData(OamInfoPanel panel, ElineInfo elineInfo,OamInfo oamA, OamInfo oamZ) throws Exception {
		SiteService_MB siteService = null;
		try {
			 siteService =(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			if(0==isSingle){
				this.aManufacturer = siteService.getManufacturer(elineInfo.getaSiteId());
				this.zManufacturer = siteService.getManufacturer(elineInfo.getzSiteId());
			}else{
				this.aManufacturer = siteService.getManufacturer(ConstantUtil.siteId);
			}
			if(null!=elineInfo&&null!=elineInfo.getOamList()&&(elineInfo.getOamList()).size()>0&&
					((elineInfo.getOamList()).get(0).getId()>0||null!=(elineInfo.getOamList()).get(0).getNeDirect()
							&&!"".equals((elineInfo.getOamList()).get(0).getNeDirect()))){
				for(OamInfo oamInfo:((ElineInfo) obj).getOamList()){ //修改时初始化
					if(oamInfo.getOamMep().getSiteId()==((ElineInfo) obj).getaSiteId()){
						oamA = oamInfo;
					}else{
						oamZ = oamInfo;
					}
				}
				initMainPanel(panel, oamA, oamZ, obj);
			}else{
				initMainPanel(panel,obj);//新建修改时初始化
			}
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(siteService);
		}
	}

	/**
	 * 初始化PW
	 * @param panel		oam主面板
	 * @param pwInfo	PW对象
	 * @param oamA		A端OAM
	 * @param oamZ		Z端OAM
	 * @throws Exception
	 */
	private void initPWData(OamInfoPanel panel, PwInfo pwInfo,OamInfo oamA, OamInfo oamZ,AddPDialog addPDialog) throws Exception {
		SiteService_MB siteService = null;
		try {
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			if(0==isSingle){
				this.aManufacturer = siteService.getManufacturer(pwInfo.getASiteId());
				this.zManufacturer = siteService.getManufacturer(pwInfo.getZSiteId());
			}else{
				this.aManufacturer = siteService.getManufacturer(ConstantUtil.siteId);
			}
			if(null!=pwInfo&&null!=pwInfo.getOamList()&&(pwInfo.getOamList()).size()>0&&
					((pwInfo.getOamList()).get(0).getId()>0||null!=(pwInfo.getOamList()).get(0).getNeDirect()
							&&!"".equals((pwInfo.getOamList()).get(0).getNeDirect()))){
				for(OamInfo oamInfo:pwInfo.getOamList()){									//修改时初始化
					if(oamInfo.getOamMep().getSiteId()==pwInfo.getASiteId()){
						oamA = oamInfo;
					}else{
						oamZ = oamInfo;
					}
				}
				initMainPanel(panel, oamA, oamZ, obj);
				if(pwInfo.getMsPwInfos()!= null && pwInfo.getMsPwInfos().size()>0){
					int i = 0;
					for(MsPwInfo msPwInfo : pwInfo.getMsPwInfos()){
						this.dialog.getOamPanel().getMipTableModel().addRow( new Object[]{++i,siteService.getSiteName(msPwInfo.getSiteId()),msPwInfo.getMipId()+"",7});
					}
				}else if(0==isSingle && addPDialog.getMipMap().size()>0){
					int i = 0;
					for(Integer integer : addPDialog.getMipMap().keySet()){
						this.dialog.getOamPanel().getMipTableModel().addRow( new Object[]{++i,siteService.getSiteName(integer),addPDialog.getMipMap().get(integer)+"",7});
					}
				}
			}else{
				initMainPanel(panel,obj);																//新建时初始化
				if(addPDialog != null){
					if(addPDialog.getSiteInsts().size()>0){
						generateMip(addPDialog.getSiteInsts(), this.dialog.getOamPanel().getMipTableModel());
					}
				}
				if(addPDialog != null && addPDialog.getPwInfo() != null && addPDialog.getPwInfo().getPwId()>0 && addPDialog.getPwInfo().getMsPwInfos() != null){
					List<SiteInst> siteInsts = new ArrayList<SiteInst>();
					for(MsPwInfo msPwInfo : addPDialog.getPwInfo().getMsPwInfos()){
						SiteInst siteInst = siteService.select(msPwInfo.getSiteId());
						siteInsts.add(siteInst);
					}
					generateMip(siteInsts, this.dialog.getOamPanel().getMipTableModel());
				}
			}
		} catch (Exception e) {
          throw e;
		}finally{
			UiUtil.closeService_MB(siteService);
		}
	}
	
	private void generateMip(List<SiteInst> siteInsts, DefaultTableModel mipTableModel) {
		mipTableModel.rowsRemoved(null);
		Object data[] = null;
		for (int j = 0; j < siteInsts.size(); j++) {
			data = new Object[] { 1+j, siteInsts.get(j).getCellId(), j+4 ,7};
			mipTableModel.addRow(data);
		}
	}
	
	/**
	 * 初始化Tunnel
	 * @param panel		oam主面板
	 * @param tunnel	Tunnel对象
	 * @param oamA		A端OAM
	 * @param oamZ		Z端OAM
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	private void initTunnelData(OamInfoPanel panel, Tunnel tunnel, OamInfo oamA, OamInfo oamZ ,AddTunnelPathDialog tunnelDialog) throws NumberFormatException, Exception {
		TunnelAction tunnelAction=null;
		checkProtect(dialog, (Tunnel) obj);
		SiteService_MB siteService = null;
		OamInfoService_MB oamInfoService = null;
		try {
			siteService = ( SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			if(0==isSingle){
				this.aManufacturer = siteService.getManufacturer(tunnel.getASiteId());
				this.zManufacturer = siteService.getManufacturer(tunnel.getZSiteId());
			}else{
				this.aManufacturer = siteService.getManufacturer(ConstantUtil.siteId);
			}
			if(null!=tunnel&&null!=tunnel.getOamList()&&(tunnel.getOamList()).size()>0&&
					((tunnel.getOamList()).get(0).getId()>0||null!=(tunnel.getOamList()).get(0).getNeDirect()
							&&!"".equals((tunnel.getOamList()).get(0).getNeDirect()))&&null!=tunnel.getOamList().get(0).getOamMep()){
				int i = 0;
				for(OamInfo oamInfo:tunnel.getOamList()){									//修改时初始化
					if(null!=oamInfo.getOamMep()){
						if(oamInfo.getOamMep().getSiteId()==tunnel.getASiteId()){
							oamA = oamInfo;
						}else if(oamInfo.getOamMep().getSiteId()==tunnel.getZSiteId()){
							oamZ = oamInfo;
						}
					}if(null!=oamInfo.getOamMip()){
						this.dialog.getOamPanel().getMipTableModel().addRow( new Object[]{++i,siteService.getSiteName(oamInfo.getOamMip().getSiteId()),oamInfo.getOamMip().getMipId(),oamInfo.getOamMip().getTc()});
					}
				}
				initMainPanel(panel, oamA, oamZ, obj);
			}else{
				//新建时初始化
				oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
				this.megId = oamInfoService.generateMegId();
				initBasicPanel((OamBasicPanel)panel.getOamBasicPanel(), obj);
				if(this.aManufacturer == EManufacturer.WUHAN.getValue()){
					initWHPanel((OamInfoWHPanel)panel.getaNEPanel(), obj);
				}else{
					initCXPanel((OamInfoCXPanel)panel.getaNEPanel(), obj);
				}
				if(0==isSingle){
					if(this.zManufacturer == EManufacturer.WUHAN.getValue()){
						initWHPanel((OamInfoWHPanel)panel.getzNEPanel(), obj);
					}else{
						initCXPanel((OamInfoCXPanel)panel.getzNEPanel(), obj);
					}
				}
				List<Segment> Segments = new ArrayList<Segment>();
				List<Lsp> realLspList = new ArrayList<Lsp>();
				
				if(0==this.isSingle){
					// 存在中间点
					Segments = tunnelDialog.getWorkSg();
					//保存真实的lsp
					tunnelAction=new TunnelAction();
					if(tunnel.getTunnelId()>0){
						realLspList.addAll(tunnel.getLspParticularList());
					}else{
						realLspList.addAll(tunnelAction.getLSPfromRoute(Segments,tunnelDialog));
					}
					if (realLspList.size() > 1) {
						generateMip(realLspList, tunnel, ++defaultValue, tunnelDialog.getMipList(), this.dialog.getOamPanel().getMipTableModel());
					}
				}
				
			}
			// 存在保护
			if (null!=tunnel&&null!=tunnel.getTunnelType()&&!"".equals(tunnel.getTunnelType())&&Integer.parseInt(tunnel.getTunnelType())>0&&
					"2".equals(UiUtil.getCodeById(Integer.parseInt(tunnel.getTunnelType())).getCodeValue())) {
				//		initTunnelDate(this.dialog.getOamProtectPanel(), tunnel.getProtectTunnel(), null, null);
				setProtecteInfoFromTunnel(tunnel.getProtectTunnel(),tunnelDialog);
				//panel.getOamBasicPanel().getChbCvCircleEnbled().setSelected(true);
			}
			tunnelAction=null;
			
		} catch (Exception e) {
		}finally{
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(oamInfoService);
		}
	}

	/**
	 * 初始化段数据
	 * @param panel 		主面板
	 * @param segment 	业务对象
	 * 	@param oamA		A端OAM
	 * @param oamZ			Z端OAM
	 * @throws Exception
	 */
	private void initSegmentData(OamInfoPanel panel, Segment segment,OamInfo oamA, OamInfo oamZ) throws Exception {
		SiteService_MB siteService = null;
		try {
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			if(0 == isSingle){
				this.aManufacturer = siteService.getManufacturer(segment.getASITEID());
				this.zManufacturer = siteService.getManufacturer(segment.getZSITEID());
			}else{
				this.aManufacturer = siteService.getManufacturer(ConstantUtil.siteId);
			}
//		if(null == segment.getOamList() || segment.getOamList().size() == 1){		
//			oamA.getOamMep().setObjType("SECTION");
//			oamA.getOamMep().setServiceId(segment.getId());
//			oamA.getOamMep().setSiteId(segment.getASITEID());
//			oamA.getOamMep().setObjId(segment.getAPORTID());
//
//			oamZ.getOamMep().setObjType("SECTION");
//			oamZ.getOamMep().setServiceId(segment.getId());
//			oamZ.getOamMep().setSiteId(segment.getZSITEID());
//			oamZ.getOamMep().setObjId(segment.getZPORTID());
//		}
			if(null!=segment&&null!=segment.getOamList()&&(segment.getOamList()).size()>0){
//				((segment.getOamList()).get(0).getId()>0||null!=(segment.getOamList()).get(0).getNeDirect()
//				&&!"".equals((segment.getOamList()).get(0).getNeDirect()))){
//		if(0 == isSingle){
				for(OamInfo oamInfo : segment.getOamList()){						
					if(oamInfo.getOamMep().getSiteId() == segment.getASITEID()){
						oamA = oamInfo;
						if(oamA.getOamMep().getLocalMepId() > 0){
							this.defaultValue = oamInfo.getOamMep().getLocalMepId();
						}
					}else{
						oamZ = oamInfo;
						if(oamZ.getOamMep().getLocalMepId() > 0){
							this.defaultValue = oamInfo.getOamMep().getLocalMepId();
						}
					}
				}
				this.initMainPanel(panel, oamA, oamZ, obj);
//		}else{
//			if(oamA.getOamMep().getId() == 0){
//				this.initMainPanel(panel,obj);
//			}else{
//				this.initMainPanel(panel, oamA, oamZ, obj);
//			}
//		}
			}else{
				initMainPanel(panel,obj);//新建时初始化
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}finally{
			UiUtil.closeService_MB(siteService);
		}
	}
	
	/**
	 * 监听事件
	 */
	private void addListeners(final JDialog jdialog) {
		// 取消按钮事件
		this.dialog.getBtnCanel().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				dialog.dispose();
			}
		});

		// 保存按钮事件
		this.dialog.getBtnSave().addActionListener(new MyActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					btnSaveAction(jdialog);
				} catch (Exception e1) {
					ExceptionManage.dispose(e1, getClass());
				}
			}

			@Override
			public boolean checking() {
				// TODO Auto-generated method stub
				return true;
			}
		});
	}
	
	/**
	 * 保存按钮事件
	 */
	protected void btnSaveAction(JDialog jdialog) {
		List <OamInfo> oamList = new ArrayList<OamInfo>();
		try {
			if(1 == this.isSingle){
				if(null == this.dialog.getOamPanel().getOamBasicPanel().getCmbName().getSelectedItem()&&
						Integer.parseInt(((ControlKeyValue)(((OamInfoSingleDialog)this.dialog).getCmbOamType().getSelectedItem())).getId()) == OamTypeEnum.MEP.getValue()){
					DialogBoxUtil.errorDialog(this.dialog, ResourceUtil.srcStr(StringKeysTip.TIP_NOT_FULL));
					return;
				}
				if(Integer.parseInt(((ControlKeyValue)(((OamInfoSingleDialog)this.dialog).getCmbOamType().getSelectedItem())).getId()) == OamTypeEnum.MIP.getValue())
					if(null == ((OamInfoSingleDialog)this.dialog).getOamMipPanel().getCmbServicesCombo().getSelectedItem()){
						DialogBoxUtil.errorDialog(this.dialog, ResourceUtil.srcStr(StringKeysTip.TIP_NOT_FULL));
						return;
					}
			}
			oamList = this.dialog.getOamPanel().getOam();
			if(check(oamList)){
				DialogBoxUtil.succeedDialog(this.dialog, ResourceUtil.srcStr(StringKeysTip.TIP_MEPID_DIFFERENT));
				return;
			}
			
			if(0 == this.isSingle){
				if (this.busiType.equals("SECTION")) {
					AddSegment segmentDialog=(AddSegment) jdialog;
					segmentDialog.setOamList(oamList);
				} else if (this.busiType.equals("PW")) {
					AddPDialog pwDialog=(AddPDialog) jdialog;
					pwDialog.setOamList(oamList);
					if(dialog.getOamPanel().getMipTable().isEditing()){
						dialog.getOamPanel().getMipTable().getCellEditor().stopCellEditing();
					}
					if(dialog.getOamPanel().getMipTableModel().getDataVector().size()>0){
						if (checkMepAndMipIdIsRepeat("pw",this.dialog.getOamPanel(),oamList,jdialog)) {
							return;
						}
					}
				} else if (this.busiType.equals("ELINE")) {
					AddElineDialog elineDialog=(AddElineDialog) jdialog;
					elineDialog.setOamList(oamList);
				}else if (this.busiType.equals("TUNNEL")) {
					AddTunnelPathDialog tunnelDialog=(AddTunnelPathDialog)jdialog;
					saveTunnel(oamList,tunnelDialog);
				}
			}else{
				if(null != oamList&&oamList.size()>0){
					if(EServiceType.SECTION.toString().equals(this.busiType))
						saveSegmentAction(oamList.get(0));
					if(EServiceType.TUNNEL.toString().equals(this.busiType))
						savelTunnelAction(oamList.get(0));
					if(EServiceType.PW.toString().equals(this.busiType))
						savePWAction(oamList.get(0));
					if(EServiceType.ELINE.toString().equals(this.busiType))
						saveELineAction(oamList.get(0));
					if(null!=this.resultStr){
						DialogBoxUtil.succeedDialog(this.dialog, this.resultStr);
					}else{
						return;
					}
					
				}
			}
			dialog.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	/**
	 * 保存eline
	 * @param oamInfo
	 * @throws Exception
	 */
	private void saveELineAction(OamInfo oamInfo) throws Exception {

		ElineInfo elineInfo = null;
		AcPortInfoService_MB acInfoService = null;
		PortLagService_MB portLagService = null;
		SiteService_MB siteService = null;
		PortService_MB portService = null;
		OamInfoService_MB oamInfoService = null;
		try {
			 siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			 portService=(PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			acInfoService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			elineInfo = (ElineInfo) ((ControlKeyValue) (this.dialog.oamPanel.getOamBasicPanel().getCmbName().getSelectedItem())).getObject();
			oamInfo.getOamMep().setObjType(this.busiType);
			oamInfo.getOamMep().setServiceId(elineInfo.getId());
			if (ConstantUtil.siteId == elineInfo.getaSiteId()) {
				oamInfo.getOamMep().setObjId(elineInfo.getaXcId());
			} else if (ConstantUtil.siteId == elineInfo.getzSiteId()) {
				oamInfo.getOamMep().setObjId(elineInfo.getzXcId());
			}
			
			// ac主键
			int acid = 0;
			if (ConstantUtil.siteId == elineInfo.getaSiteId()) {
				acid = elineInfo.getaAcId();
				oamInfo.setOamType(OamTypeEnum.AMEP);
			} else if (ConstantUtil.siteId == elineInfo.getzSiteId()) {
				acid = elineInfo.getzAcId();
				oamInfo.setOamType(OamTypeEnum.ZMEP);
			}
			// 通过主键去数据库查询AC对象
			AcPortInfo acPortInfo = new AcPortInfo();
			List<AcPortInfo> acPortInfoList = new ArrayList<AcPortInfo>();
			acPortInfo.setId(acid);
			acPortInfoList = acInfoService.queryByAcPortInfo(acPortInfo);
			if (null == acPortInfoList || acPortInfoList.size() != 1) {
				throw new Exception("查询AC出错");
			}
			acPortInfo = acPortInfoList.get(0);
			
			// 创建AC设备节点路径。
			oamInfo.setOamObjType("AC");
			if (acPortInfo.getPortId() > 0) {
				oamInfo.setOamPath("ne/interfaces/eth/" + portService.getPortname(acPortInfo.getPortId()) + "/" + acPortInfo.getAcBusinessId());
			} else {
				portLagService = (PortLagService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORTLAG);
				PortLagInfo portLagInfo = new PortLagInfo();
				portLagInfo.setId(acPortInfo.getLagId());
				List<PortLagInfo> portLagInfoList = portLagService.selectByCondition(portLagInfo);
				
				if (null == portLagInfoList || portLagInfoList.size() != 1) {
					throw new Exception("查询lag出错");
				}
				oamInfo.setOamPath("ne/interfaces/lag/" + portLagInfoList.get(0).getLagID() + "/" + acPortInfo.getAcBusinessId());
				portLagInfoList = null;
				portLagInfo = null;
			}
			int manufacturer = 0;
			manufacturer = siteService.getManufacturer(oamInfo.getOamMep().getSiteId());
			if (manufacturer == EManufacturer.WUHAN.getValue()) {
				oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
				oamInfoService.saveOrUpdate(oamInfo);
				resultStr = ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS);
//				oamInfoService = null;
			} else {
				DispatchUtil oamCXServiceImpl = new DispatchUtil(RmiKeys.RMI_OAM);
				if (oamInfo.getOamMep().getId() > 0) {
					resultStr = oamCXServiceImpl.excuteUpdate(oamInfo);
					this.dialog.btnSave.setOperateKey(EOperationLogType.ELINEOAMUPDATE.getValue());
				} else {
					resultStr = oamCXServiceImpl.excuteInsert(oamInfo);
					this.dialog.btnSave.setOperateKey(EOperationLogType.ELINEOAMINSERT.getValue());
				}
				oamCXServiceImpl = null;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(portService);
			UiUtil.closeService_MB(acInfoService);
			UiUtil.closeService_MB(portLagService);
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(oamInfoService);
		}
	}
	/**
	 * 保存tunnel
	 * @param oamList oam信息
	 * @throws Exception
	 */
	private void saveTunnel(List <OamInfo> oamList,JDialog jdialog) throws Exception {
		List <OamInfo> oamList_protect = null ;
		//工作侧
		if(dialog.getOamPanel().getMipTable().isEditing()){
			dialog.getOamPanel().getMipTable().getCellEditor().stopCellEditing();
		}
		if(dialog.getOamPanel().getMipTableModel().getDataVector().size()>0){
			if (checkMepAndMipIdIsRepeat("tunnel",this.dialog.getOamPanel(),oamList,jdialog)) {
				return;
			}
			setMip(oamList ,"work",jdialog);
		}
		//设置邻端
		AddTunnelPathDialog tunnelDialog=(AddTunnelPathDialog) jdialog;
		tunnelDialog.setOamList(oamList);
		
		//保护侧
		if("2".equals(UiUtil.getCodeById(Integer.parseInt(((Tunnel)this.dialog.getObj()).getTunnelType())).getCodeValue())){
			oamList_protect = this.dialog.getOamProtectPanel().getOam();
			if (checkMepAndMipIdIsRepeat("tunnel",this.dialog.getOamProtectPanel(),oamList_protect,jdialog)) {
				return;
			}
			setMip(oamList_protect ,"protect",jdialog);
			//设置邻端
			tunnelDialog.setOamList_protect(oamList_protect);
		}
	}

	/**
	 * 给MIP赋值
	 * @param oamList  oamList
	 * @param type 类型  work=工作路径，protect=保护路径
	 */
		// 工作路径上存在中间点
	private void setMip(List <OamInfo> oamList,String type,JDialog jdialog ) {
		Map<Integer, String> map;
		List<Segment> segmentList;
		AddTunnelPathDialog tunnelDialog=(AddTunnelPathDialog) jdialog;
		int megId = 0;
		if(null != oamList && oamList.size()>0 && null !=oamList.get(0).getOamMep())
			megId = oamList.get(0).getOamMep().getMegId();
		if("work".equals(type)){
			map =tunnelDialog.getMipList();
			segmentList = tunnelDialog.getWorkSg();
		}else{
			map = tunnelDialog.getMipList_pro();
			segmentList = tunnelDialog.getProSg();
		}
		if (map.size() != 0) {
			OamInfo oam = null;
			OamMipInfo oamMip = null;
			for (int siteId : map.keySet()) {
				//工作修改的赋值				
				if(type.equals("work")){
					if(tunnelDialog.getOamList()!=null && tunnelDialog.getOamList().size()>0){
				       for(int i=0;i<tunnelDialog.getOamList().size();i++){					
					     if(tunnelDialog.getOamList().get(i).getOamMip()!=null 
							 && siteId==tunnelDialog.getOamList().get(i).getOamMip().getSiteId()){	
							 oam = new OamInfo();
							 oamMip = new OamMipInfo();	
							 oamMip.setId(tunnelDialog.getOamList().get(i).getOamMip().getId());
							 oamMip.setObjType(EServiceType.TUNNEL.toString());
							 oamMip.setSiteId(siteId);
							 oamMip.setMipId(Integer.parseInt(map.get(siteId).split(",")[0]));
							 oamMip.setMegId(megId);
							 oamMip.setTc(Integer.parseInt(map.get(siteId).split(",")[1]));
							 oam.setOamType(OamTypeEnum.MIP);
							 setOamMip(segmentList ,oamList, oamMip,type);
							 if (((Tunnel)this.obj).getTunnelId() != 0) {
								 oamMip.setServiceId(((Tunnel)this.obj).getTunnelId());
							 }					
							oam.setOamMip(oamMip);
							oamList.add(oam);
						  }
				      } 				    
			      }else{						  			
					 oam = new OamInfo();
					 oamMip = new OamMipInfo();	
					 oamMip.setObjType(EServiceType.TUNNEL.toString());
					 oamMip.setSiteId(siteId);
					 oamMip.setMipId(Integer.parseInt(map.get(siteId).split(",")[0]));
					 oamMip.setMegId(megId);
					 oamMip.setTc(Integer.parseInt(map.get(siteId).split(",")[1]));
					 oam.setOamType(OamTypeEnum.MIP);
					 setOamMip(segmentList ,oamList, oamMip,type);
					 if (((Tunnel)this.obj).getTunnelId() != 0) {
						 oamMip.setServiceId(((Tunnel)this.obj).getTunnelId());
					 }					
					oam.setOamMip(oamMip);
					oamList.add(oam);																												  
			      }
		   }
				//保护修改的赋值
				if(type.equals("protect")){
					if(tunnelDialog.getOamList_protect()!=null && tunnelDialog.getOamList_protect().size()>0){
				       for(int i=0;i<tunnelDialog.getOamList_protect().size();i++){					
					     if(tunnelDialog.getOamList_protect().get(i).getOamMip()!=null 
							 && siteId==tunnelDialog.getOamList_protect().get(i).getOamMip().getSiteId()){	
							 oam = new OamInfo();
							 oamMip = new OamMipInfo();	
							 oamMip.setId(tunnelDialog.getOamList_protect().get(i).getOamMip().getId());
							 oamMip.setObjType(EServiceType.TUNNEL.toString());
							 oamMip.setSiteId(siteId);
							 oamMip.setMipId(Integer.parseInt(map.get(siteId).split(",")[0]));
							 oamMip.setMegId(megId);
							 oamMip.setTc(Integer.parseInt(map.get(siteId).split(",")[1]));
							 oam.setOamType(OamTypeEnum.MIP);
							 setOamMip(segmentList ,oamList, oamMip,type);
							 if (((Tunnel)this.obj).getTunnelId() != 0) {
								 oamMip.setServiceId(((Tunnel)this.obj).getTunnelId());
							 }					
							oam.setOamMip(oamMip);
							oamList.add(oam);
						  }
				      } 
					}else{
						 oam = new OamInfo();
						 oamMip = new OamMipInfo();	
						 oamMip.setObjType(EServiceType.TUNNEL.toString());
						 oamMip.setSiteId(siteId);
						 oamMip.setMipId(Integer.parseInt(map.get(siteId).split(",")[0]));
						 oamMip.setMegId(megId);
						 oamMip.setTc(Integer.parseInt(map.get(siteId).split(",")[1]));
						 oam.setOamType(OamTypeEnum.MIP);
						 setOamMip(segmentList ,oamList, oamMip,type);
						 if (((Tunnel)this.obj).getTunnelId() != 0) {
							 oamMip.setServiceId(((Tunnel)this.obj).getTunnelId());
						 }					
						oam.setOamMip(oamMip);
						oamList.add(oam);										
					 }				
				}
	
			}
		}
		segmentList = null;
		map = null;
	}

	/**
	 * 判断是否有保护路径
	 * @param dialog	oam配置窗口
	 * @param tunnel	tunnel对象
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	private void checkProtect(OamInfoDialog dialog, Tunnel tunnel) throws NumberFormatException, Exception {
		if(1 == this.isSingle)
			return;
		
		if ("1".equals(UiUtil.getCodeById(Integer.parseInt(tunnel.getTunnelType())).getCodeValue())) {
			this.dialog.getMainPanel().setEnabledAt(1, false);
		} else {
			this.dialog.getMainPanel().setEnabledAt(1, true);
		}
	}
	
	/**
	 * 初始化主面板
	 * @param panel 主面板
	 * @param obj 业务对象
	 * @throws Exception
	 */
	private void initMainPanel(OamInfoPanel panel, Object obj) throws Exception {
		OamInfoService_MB oamInfoService = null;
		try {
			oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			this.megId = oamInfoService.generateMegId();
			initBasicPanel(panel.getOamBasicPanel(), obj);
			if(this.aManufacturer == EManufacturer.WUHAN.getValue()){
				initWHPanel((OamInfoWHPanel)panel.getaNEPanel(), obj);
			}else{
				initCXPanel((OamInfoCXPanel)panel.getaNEPanel(), obj);
			}
			if(0==isSingle){
				if(this.zManufacturer == EManufacturer.WUHAN.getValue()){
					initWHPanel((OamInfoWHPanel)panel.getzNEPanel(), obj);
				}else{
					initCXPanel((OamInfoCXPanel)panel.getzNEPanel(), obj);
				}
			}
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(oamInfoService);
		}
	}

	/**
	 * 新建时初始化基本信息面板
	 * @param oamBasicPanel
	 * @param obj
	 * @throws Exception
	 */
	private void initBasicPanel(OamBasicPanel oamBasicPanel, Object obj) throws Exception {
		ComboBoxDataUtil comboBoxDataUtil=new ComboBoxDataUtil();
		comboBoxDataUtil.comboBoxData(oamBasicPanel.getCmbCvCircle(), "OAMCVCYCLE");
		oamBasicPanel.getTxtMegLvl().setText("7");
		oamBasicPanel.getCmbCvCircle().setEnabled(true);
		oamBasicPanel.getChbCvCircleEnbled().setSelected(true);
	}

	/**
	 * 初始化CX OAM面板
	 * @param pannel OAM面板
	 * @param obj 业务对象
	 * @throws Exception
	 */
	private void initCXPanel(OamInfoCXPanel pannel, Object obj) throws Exception {
		pannel.getTxtMepId().setText(++this.defaultValue + "");
		pannel.getTxtLoopBackOverTime().setText("5");
		pannel.getTxtMegId().setText(megId.substring(0, this.megId.toString().indexOf("*")));
		if(1 == this.isSingle){
			pannel.getTxtRemoteMepId().setText(++this.defaultValue + "");
		}
	}
	
	/**
	 * 初始化武汉OAM面板
	 * @param pannel OAM面板
	 * @param obj 业务对象
	 * @throws Exception
	 */
	private void initWHPanel(OamInfoWHPanel pannel, Object obj) throws Exception {
		OamInfoService_MB oamInfoService = null;
		try {
			if(this.megId == null){
				oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
				this.megId = oamInfoService.generateMegId();
			}
			ComboBoxDataUtil comboBoxDataUtil=new ComboBoxDataUtil();
			pannel.getTxtMepId().setText(++defaultValue + "");
			pannel.getTxtMegICC().setText(this.megId.substring(0, megId.toString().indexOf("*")));
			pannel.getTxtMegUMC().setText(1+"");
			comboBoxDataUtil.comboBoxData(pannel.getCmbTC(), "TC");
			pannel.getCmbTC().setSelectedIndex(7);
			comboBoxDataUtil.comboBoxData(pannel.getCmbPWTC(), "TC");
			pannel.getCmbPWTC().setSelectedIndex(7);
			(dialog.getOamPanel().getOamBasicPanel()).getTxtMegLvl().setText("7");
			if(1 == this.isSingle){
				pannel.getTxtRemoteMepId().setText(++this.defaultValue + "");
			}
//		Tunnel tunnel = (Tunnel) obj;
//		pannel.setId(oam.getOamMep().getId());
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(oamInfoService);
		}
	}

	/**
	 * 初始化保护侧Tunnel OAM
	 * @param tunnel  保护侧Tunnel
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	private void setProtecteInfoFromTunnel(Tunnel tunnel,AddTunnelPathDialog tunnelDialog) throws NumberFormatException, Exception {
		TunnelAction tunnelAction=null;
		OamInfo oamA = null;
		OamInfo oamZ = null;
		List<Segment> Segments_protect = new ArrayList<Segment>();
		List<Lsp> realLsp_protectList = new ArrayList<Lsp>();
		SiteService_MB siteService = null;
		OamInfoService_MB oamInfoService = null;
		try {
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			int i = 0;
			//修改
			if(null!=tunnel.getOamList()&&tunnel.getOamList().size()>0&&
					(tunnel.getOamList().get(0).getId()>0||null!=tunnel.getOamList().get(0).getNeDirect()
							&&!"".equals(tunnel.getOamList().get(0).getNeDirect()))){
				for(OamInfo oamInfo:tunnel.getOamList()){
					if(null!=oamInfo.getOamMep()){
						if(oamInfo.getOamMep().getSiteId()==tunnel.getASiteId()){
							oamA = oamInfo;
						}else{
							oamZ = oamInfo;
						}
					}
					if(null!=oamInfo.getOamMip()){
						this.dialog.getOamProtectPanel().getMipTableModel().addRow( new Object[]{++i,siteService.getSiteName(oamInfo.getOamMip().getSiteId()),oamInfo.getOamMip().getMipId(),oamInfo.getOamMip().getTc()});
					}
				}
				initMainPanel(this.dialog.getOamProtectPanel(), oamA, oamZ, tunnel);
				
			}else{//新建
				oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
				this.megId = oamInfoService.generateMegId();
				initBasicPanel(this.dialog.getOamProtectPanel().getOamBasicPanel(), tunnel);
				this.dialog.getOamProtectPanel().getOamBasicPanel().getChbCvCircleEnbled().setSelected(true);
				if(this.aManufacturer == EManufacturer.WUHAN.getValue()){
					initWHPanel((OamInfoWHPanel)this.dialog.getOamProtectPanel().getaNEPanel(), tunnel);
					((OamInfoWHPanel)this.dialog.getOamProtectPanel().getaNEPanel()).getChbApsEnable().setSelected(true);
				}else{
					initCXPanel((OamInfoCXPanel)this.dialog.getOamProtectPanel().getaNEPanel(), tunnel);
				}
				if(this.zManufacturer == EManufacturer.WUHAN.getValue()){
					initWHPanel((OamInfoWHPanel)this.dialog.getOamProtectPanel().getzNEPanel(), tunnel);
					((OamInfoWHPanel)this.dialog.getOamProtectPanel().getzNEPanel()).getChbApsEnable().setSelected(true);
				}else{
					initCXPanel((OamInfoCXPanel)this.dialog.getOamProtectPanel().getzNEPanel(), tunnel);
				}
				// 存在中间点
				Segments_protect =tunnelDialog.getProSg();
				//保存真实的lsp
				tunnelAction=new TunnelAction();
				if(tunnel.getTunnelId()>0){
					realLsp_protectList.addAll(tunnel.getLspParticularList());
				}else{
					realLsp_protectList.addAll(tunnelAction.getLSPfromRoute(Segments_protect,tunnelDialog));
				}
				if (realLsp_protectList.size() > 1) {
					generateMip(realLsp_protectList, tunnel, ++defaultValue,tunnelDialog.getMipList_pro(), this.dialog.getOamProtectPanel().getMipTableModel());
				}
			}
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(oamInfoService);
		}
	}

	/**
	 * 生成mip
	 * @param lspP 
	 * @param tunnel tunnel
	 * @param value
	 * @param mipList
	 * @param tableModel
	 * @throws Exception
	 */
	private void generateMip(List<Lsp> lspP, Tunnel tunnel, int value, Map<Integer, String> mipList, DefaultTableModel tableModel) throws Exception {
		tableModel.rowsRemoved(null);
		Object data[] = null;
		Map<Integer, Integer> mpList = new HashMap<Integer, Integer>();
		SiteService_MB siteService = null;;
		try {
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			for(int i = 0; i < lspP.size(); i++)
			{
				for(int j = i+1; j<lspP.size(); j++){
					int persiteId = lspP.get(i).getASiteId();
					int aftersiteId = lspP.get(i).getZSiteId();
					if(persiteId == lspP.get(j).getASiteId() || persiteId == lspP.get(j).getZSiteId()){
						mpList.put(persiteId,persiteId);
					}
					
					if(aftersiteId == lspP.get(j).getASiteId() || aftersiteId == lspP.get(j).getZSiteId()){
						mpList.put(aftersiteId, aftersiteId);
					}
				}
			}
			mipList.clear();
			for (int mp : mpList.keySet()) {
				if (mp == tunnel.getASiteId() || mp == tunnel.getZSiteId()) {
					continue;
				} else {
					mipList.put(mp, mp+",7");
				}
			}
			int i = 0;
			for (int siteId : mipList.keySet()) {
				data = new Object[] { ++i, siteService.getSiteName(siteId), value ,7};
				mipList.put(siteId, value+",7");
				tableModel.addRow(data);
				value = value + 1;
			}
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(siteService);
		}
	}

	/**
	 * 初始化面板
	 * @param dialog  OAM窗口
	 * @param oamA a端 OAM
	 * @param oamZ z端OAM
	 * @param obj	业务对象
	 * @throws Exception
	 */
	private void initMainPanel(OamInfoPanel panel, OamInfo oamA, OamInfo oamZ, Object obj) throws Exception {
		initBasicPaneDate(panel.getOamBasicPanel(),oamA);
		try {
			if(panel.getaNEPanel() instanceof OamInfoCXPanel){
				initCXOamPanel((OamInfoCXPanel)panel.getaNEPanel(), oamA, obj);
			}else{
				if(oamA.getId() == 0){
					initWHPanel((OamInfoWHPanel)panel.getaNEPanel(), oamA);
				}else{
					initWHOamPanel((OamInfoWHPanel)panel.getaNEPanel(), oamA, obj);
				}
			}
			if(0 == isSingle){
				if(panel.getzNEPanel() instanceof OamInfoCXPanel){
					initCXOamPanel((OamInfoCXPanel)panel.getzNEPanel(), oamZ, obj);
				}else{
					if(oamZ.getId() == 0){
						initWHPanel((OamInfoWHPanel)panel.getzNEPanel(), oamZ);
					}else{
						initWHOamPanel((OamInfoWHPanel)panel.getzNEPanel(), oamZ, obj);
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		
	}

	/**
	 * 修改时初始化基础面板
	 * @param panel  
	 * @param oamA
	 * @throws Exception
	 */
	private void initBasicPaneDate(OamBasicPanel panel, OamInfo oamA) throws Exception {
		ComboBoxDataUtil comboBoxDataUtil=new ComboBoxDataUtil();
		panel.getTxtMegLvl().setText(oamA.getOamMep().getMel()+"");
		if (oamA.getOamMep().isCv()) {
			panel.getCmbCvCircle().setEnabled(true);
			panel.getChbCvCircleEnbled().setSelected(true);
			comboBoxDataUtil.comboBoxData(panel.getCmbCvCircle(), "OAMCVCYCLE");
			comboBoxDataUtil.comboBoxSelect(panel.getCmbCvCircle(), oamA.getOamMep().getCvCycle() + "");
		} else {
			panel.getCmbCvCircle().removeAllItems();
			panel.getCmbCvCircle().setEnabled(false);
			panel.getChbCvCircleEnbled().setSelected(false);
			comboBoxDataUtil.comboBoxData(panel.getCmbCvCircle(), "OAMCVCYCLE");
			comboBoxDataUtil.comboBoxSelect(panel.getCmbCvCircle(), oamA.getOamMep().getCvCycle() + "");
		}
	}

	/**
	 * 给CX OAM面板赋值
	 * @param panel CX OAM面板
	 * @param oam OAM bean
	 * @param obj 业务对象
	 */
	private void initCXOamPanel(OamInfoCXPanel panel, OamInfo oam,Object obj) {
		panel.getTxtMegId().setText(oam.getOamMep().getMegId()+"");
		panel.getTxtMepId().setText(oam.getOamMep().getLocalMepId()+"");
		panel.getChbLMEnable().setSelected(oam.getOamMep().isLm());
		panel.getChbDMEnable().setSelected(oam.getOamMep().isDm());
		panel.getChbNELock().setSelected(oam.getOamMep().isLck());
		panel.getChbCSFEnable().setSelected(oam.getOamMep().isCsfEnable());
		panel.getTxtLoopBackOverTime().setText(oam.getOamMep().getLpbOutTime()+"");
		if(1 == this.isSingle)
		panel.getTxtRemoteMepId().setText(oam.getOamMep().getRemoteMepId()+"");
		panel.setId(oam.getOamMep().getId());
	}
	/**
	 * 给武汉 OAM面板赋值
	 * @param panel 武汉 OAM面板
	 * @param oam OAM bean
	 * @param obj 业务对象
	 * @throws Exception 
	 */
	private void initWHOamPanel(OamInfoWHPanel panel, OamInfo oam, Object obj) throws Exception {
		ComboBoxDataUtil comboBoxDataUtil = new ComboBoxDataUtil();
		panel.getTxtMegICC().setText(oam.getOamMep().getMegIcc());
		panel.getTxtMegUMC().setText(oam.getOamMep().getMegUmc());
		panel.getTxtMepId().setText(oam.getOamMep().getLocalMepId()+"");
		panel.getChbLMEnable().setSelected(oam.getOamMep().isLm());
		panel.getChbSCCEnable().setSelected(oam.getOamMep().isSccTest());
		panel.getChbSSMEnable().setSelected(oam.getOamMep().isSsm());
		panel.getOamEnable().setSelected(oam.getOamMep().isOamEnable());
		comboBoxDataUtil.comboBoxData(panel.getCmbTC(), "TC");
		comboBoxDataUtil.comboBoxSelect(panel.getCmbTC(), oam.getOamMep().getLspTc() + "");
		comboBoxDataUtil.comboBoxData(panel.getCmbPWTC(), "TC");
		comboBoxDataUtil.comboBoxSelect(panel.getCmbPWTC(), oam.getOamMep().getPwTc() + "");
		if(1 == this.isSingle)
			panel.getTxtRemoteMepId().setText(oam.getOamMep().getRemoteMepId()+"");
		if(oam.getOamMep().getObjType().equals(EServiceType.SECTION.toString())){
//			if(oam.getOamMep().getId() == 0){
//				panel.getTxtSourceMac().setText("00-00-00-00-00-01");
//				panel.getTxtEndMac().setText("00-00-00-00-00-01");
//			}else{
				panel.getTxtSourceMac().setText(oam.getOamMep().getSourceMac());
				panel.getTxtEndMac().setText(oam.getOamMep().getEndMac());
//			}
		}
		if (obj instanceof Segment || obj instanceof Tunnel) {
			panel.getChbApsEnable().setSelected(oam.getOamMep().isAps());
		}  else if (obj instanceof PwInfo) {
			panel.getChbCSFEnable().setSelected(oam.getOamMep().isCsfEnable());
			comboBoxDataUtil.comboBoxSelect(panel.getCmbPWTC(), oam.getOamMep().getPwTc()+ "");
		} 
		panel.setId(oam.getOamMep().getId());
	}

	/**
	 * 提交验证
	 * @return
	 */
	public boolean check(List<OamInfo> oamList){
		if(oamList.size()==2){
			if(oamList.get(0).getOamMep().getLocalMepId() == oamList.get(1).getOamMep().getLocalMepId()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Segment保存
	 * @throws Exception 
	 */
	public String saveSegmentAction(OamInfo oamInfo) throws Exception {
		PortInst portInst = (PortInst) ((ControlKeyValue)this.dialog.getOamPanel().getOamBasicPanel().getCmbName().getSelectedItem()).getObject();
		oamInfo.getOamMep().setObjId(portInst.getPortId());
		oamInfo.getOamMep().setObjNameLog(portInst.getPortName());
		oamInfo.setOamType(OamTypeEnum.AMEP);
		DispatchUtil tmsOamDispatch = new DispatchUtil(RmiKeys.RMI_TMSOAMDISPATCH);
		int operationType=0;//操作日志类型
		if (oamInfo.getOamMep().getId() > 0) {
			OamInfo oamBefore = new OamInfo();
			OamInfoService_MB oamService = null;
			PortService_MB portService = null;
			try {
				oamService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
				portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
				OamMepInfo oammepInfoBefore = oamService.queryMep(oamInfo.getOamMep());
				oamBefore.setOamMep(oammepInfoBefore);
				oammepInfoBefore.setObjNameLog(portService.getPortname(oammepInfoBefore.getObjId()));
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			} finally {
				UiUtil.closeService_MB(oamService);
				UiUtil.closeService_MB(portService);
			}
			resultStr = tmsOamDispatch.excuteUpdate(oamInfo);
			AddOperateLog.insertOperLog(this.dialog.getBtnSave(), EOperationLogType.SEGMENTOAMUPDATE.getValue(), resultStr, 
				oamBefore, oamInfo, ConstantUtil.siteId, ResourceUtil.srcStr(StringKeysObj.STRING_SEGMENT_OAM_CONFIG), "segmentOAM");
		} else {
			resultStr = tmsOamDispatch.excuteInsert(oamInfo);
			AddOperateLog.insertOperLog(this.dialog.getBtnSave(), EOperationLogType.SEGMENTOAMINSERT.getValue(), resultStr, 
				null, oamInfo, ConstantUtil.siteId, ResourceUtil.srcStr(StringKeysObj.STRING_SEGMENT_OAM_CONFIG), "segmentOAM");
		}
		portInst = null;
		return this.resultStr;
	}

	/**
	 * 单网元tunnel保存
	 */
	public String savelTunnelAction(OamInfo oamInfo) {
		Tunnel tunnel = null;
		DispatchUtil tunnelDispatch;
		TunnelService_MB tunnelServiceMB = null;
		List<OamInfo> oamList ;
		DispatchUtil oamDispatch ;
		int manufacturer = 0;
		SiteService_MB siteService=null;
		int operationType=0;//操作日志类型
		try {
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			tunnelDispatch = new  DispatchUtil(RmiKeys.RMI_TUNNEL);
			if(((OamInfoSingleDialog)this.dialog).getCmbOamType().getSelectedItem().toString().trim().equals(OamTypeEnum.MIP.toString())){
				if(((OamInfoSingleDialog)this.dialog).getOamMipPanel().getMip(oamInfo)){
					tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
					oamList = new ArrayList<OamInfo>();					
					tunnel = tunnelServiceMB.selectByTunnelIdAndSiteId(ConstantUtil.siteId, oamInfo.getOamMip().getServiceId());
					oamInfo.setOamType(OamTypeEnum.MIP);
					oamInfo.setOamObjType("XC");
					oamInfo.setOamPath("ne/protocols/mpls/xc/" + oamInfo.getOamMip().getObjId());					
					oamList.add(oamInfo);
					tunnel.setOamList(oamList);  
					manufacturer = siteService.getManufacturer(ConstantUtil.siteId); 
					if (manufacturer == EManufacturer.WUHAN.getValue()) {
						oamInfo.getOamMip().setObjNameLog(tunnel.getTunnelName());
						OamInfo oamBefore = null;
						if (oamInfo.getOamMip().getId() > 0) {
							OamInfoService_MB oamService = null;
							try {
								oamService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
								oamBefore = oamService.queryMipOam(oamInfo.getOamMip()).get(0);
								oamBefore.getOamMip().setObjNameLog(tunnelServiceMB.selectByTunnelIdAndSiteId(ConstantUtil.siteId, oamBefore.getOamMip().getServiceId()).getTunnelName());
							} catch (Exception e) {
								ExceptionManage.dispose(e, this.getClass());
							} finally {
								UiUtil.closeService_MB(oamService);
							}
							operationType=EOperationLogType.TUNNELOAMUPDATE.getValue();
						} else {
							operationType=EOperationLogType.TUNNELOAMINSERT.getValue();
						}
						this.resultStr = tunnelDispatch.excuteUpdate(tunnel);
						oamInfo.setOamMep(null);
						AddOperateLog.insertOperLog(this.dialog.getBtnSave(), operationType, resultStr, oamBefore,
							oamInfo, ConstantUtil.siteId, ResourceUtil.srcStr(StringKeysObj.STRING_TUNNEL_OAM_CONFIG)+"_MIP", "tunnelMipOAM");
					} else {
						oamDispatch = new DispatchUtil(RmiKeys.RMI_OAM);
						if (oamInfo.getOamType() == OamTypeEnum.MIP) {
							if (oamInfo.getOamMip().getId() > 0) {
								this.resultStr = oamDispatch.excuteUpdate(oamInfo);
								operationType=EOperationLogType.TUNNELOAMUPDATE.getValue();
							} else {
								resultStr = oamDispatch.excuteInsert(oamInfo);
								operationType=EOperationLogType.TUNNELOAMINSERT.getValue();
							}
						} else {
							if (oamInfo.getOamMep().getId() > 0) {
								this.resultStr = oamDispatch.excuteUpdate(oamInfo);
								operationType=EOperationLogType.TUNNELOAMUPDATE.getValue();
							} else {
								resultStr = oamDispatch.excuteInsert(oamInfo);
								operationType=EOperationLogType.TUNNELOAMINSERT.getValue();
							}
						}
					}
				}
			}else{
				//MEP
				tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
				oamList = new ArrayList<OamInfo>();
			
				tunnel = (Tunnel) ((((ControlKeyValue)((OamInfoSingleDialog)this.dialog).getOamPanel().getOamBasicPanel().getCmbName().getSelectedItem())).getObject());
				oamInfo.setOamObjType(EServiceType.TUNNEL.toString());
				if(tunnel.getaSiteId() == oamInfo.getOamMep().getSiteId()){
					oamInfo.setOamType(OamTypeEnum.AMEP);
				}else{
					oamInfo.setOamType(OamTypeEnum.ZMEP);
				}
				oamInfo.getOamMep().setServiceId(tunnel.getTunnelId());
				for (Lsp lsp : tunnel.getLspParticularList()) {
					if (lsp.getASiteId() == ConstantUtil.siteId) {
						oamInfo.getOamMep().setObjId(lsp.getAtunnelbusinessid());
						break;
					}
					if (lsp.getZSiteId() == ConstantUtil.siteId) {
						oamInfo.getOamMep().setObjId(lsp.getZtunnelbusinessid());
						break;
					}
				}
				oamList.add(oamInfo);
				tunnel.setOamList(oamList);  
				manufacturer = siteService.getManufacturer(ConstantUtil.siteId);
				if (manufacturer == EManufacturer.WUHAN.getValue()) {
					oamInfo.getOamMep().setObjNameLog(tunnel.getTunnelName());
					OamInfo oamBefore = null;
					if (oamInfo.getOamMep().getId() > 0) {
						oamBefore = new OamInfo();
						OamInfoService_MB oamService = null;
						try {
							oamService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
							OamMepInfo oammepInfoBefore = oamService.queryMep(oamInfo.getOamMep());
							oamBefore.setOamMep(oammepInfoBefore);
							oammepInfoBefore.setObjNameLog(tunnelServiceMB.selectByTunnelIdAndSiteId(ConstantUtil.siteId, oammepInfoBefore.getServiceId()).getTunnelName());
						} catch (Exception e) {
							ExceptionManage.dispose(e, this.getClass());
						} finally {
							UiUtil.closeService_MB(oamService);
						}
						operationType=EOperationLogType.TUNNELOAMUPDATE.getValue();
					} else {
						operationType=EOperationLogType.TUNNELOAMINSERT.getValue();
					}
					this.resultStr = tunnelDispatch.excuteUpdate(tunnel);
					AddOperateLog.insertOperLog(this.dialog.getBtnSave(), operationType, resultStr, oamBefore,
							oamInfo, ConstantUtil.siteId, ResourceUtil.srcStr(StringKeysObj.STRING_TUNNEL_OAM_CONFIG)+"_MEP", "tunnelMepOAM");
				} else {
					oamDispatch = new DispatchUtil(RmiKeys.RMI_OAM);
					if (oamInfo.getOamType() == OamTypeEnum.MIP) {
						if (oamInfo.getOamMip().getId() > 0) {
							this.resultStr = oamDispatch.excuteUpdate(oamInfo);
							operationType=EOperationLogType.TUNNELOAMUPDATE.getValue();
						} else {
							resultStr = oamDispatch.excuteInsert(oamInfo);
							operationType=EOperationLogType.TUNNELOAMINSERT.getValue();
						}
					} else {
						if (oamInfo.getOamMep().getId() > 0) {
							this.resultStr = oamDispatch.excuteUpdate(oamInfo);
							operationType=EOperationLogType.TUNNELOAMUPDATE.getValue();
						} else {
							resultStr = oamDispatch.excuteInsert(oamInfo);
							operationType=EOperationLogType.TUNNELOAMINSERT.getValue();
						}
					}
				}
			}
			
//			tunnelService = (TunnelService) ConstantUtil.serviceFactory.newService(Services.Tunnel);
//			oamList = new ArrayList<OamInfo>();
//			if(Integer.parseInt(((ControlKeyValue)(((OamInfoSingleDialog)this.dialog).getCmbOamType().getSelectedItem())).getId()) == OamTypeEnum.MEP.getValue()){
//				tunnel = (Tunnel) ((((ControlKeyValue)((OamInfoSingleDialog)this.dialog).getOamPanel().getOamBasicPanel().getCmbName().getSelectedItem())).getObject());
//				oamInfo.setOamObjType(EServiceType.TUNNEL.toString());
//				if(tunnel.getaSiteId() == oamInfo.getOamMep().getSiteId()){
//					oamInfo.setOamType(OamTypeEnum.AMEP);
//				}else{
//					oamInfo.setOamType(OamTypeEnum.ZMEP);
//				}
//				oamInfo.getOamMep().setServiceId(tunnel.getTunnelId());
//				for (Lsp lsp : tunnel.getLspParticularList()) {
//					if (lsp.getASiteId() == ConstantUtil.siteId) {
//						oamInfo.getOamMep().setObjId(lsp.getAtunnelbusinessid());
//						break;
//					}
//					if (lsp.getZSiteId() == ConstantUtil.siteId) {
//						oamInfo.getOamMep().setObjId(lsp.getZtunnelbusinessid());
//						break;
//					}
//				}
//			} else {
//				tunnel = tunnelService.selectByTunnelIdAndSiteId(ConstantUtil.siteId, oamInfo.getOamMip().getServiceId());
//				oamInfo.setOamType(OamTypeEnum.MIP);
//				oamInfo.setOamObjType("XC");
//				oamInfo.setOamPath("ne/protocols/mpls/xc/" + oamInfo.getOamMip().getObjId());
//			}
//			oamList.add(oamInfo);
//			tunnel.setOamList(oamList);  
//			manufacturer = siteService.getManufacturer(ConstantUtil.siteId);
//			if (manufacturer == EManufacturer.WUHAN.getValue()) {
//				this.resultStr = tunnelDispatch.excuteUpdate(tunnel);
//				if (oamInfo.getOamMep().getId() > 0) {
//					operationType=EOperationLogType.TUNNELOAMUPDATE.getValue();
//				} else {
//					operationType=EOperationLogType.TUNNELOAMINSERT.getValue();
//				}
//			} else {
//				oamDispatch = new DispatchUtil(RmiKeys.RMI_OAM);
//				if (oamInfo.getOamType() == OamTypeEnum.MIP) {
//					if (oamInfo.getOamMip().getId() > 0) {
//						this.resultStr = oamDispatch.excuteUpdate(oamInfo);
//						operationType=EOperationLogType.TUNNELOAMUPDATE.getValue();
//					} else {
//						resultStr = oamDispatch.excuteInsert(oamInfo);
//						operationType=EOperationLogType.TUNNELOAMINSERT.getValue();
//					}
//				} else {
//					if (oamInfo.getOamMep().getId() > 0) {
//						this.resultStr = oamDispatch.excuteUpdate(oamInfo);
//						operationType=EOperationLogType.TUNNELOAMUPDATE.getValue();
//					} else {
//						resultStr = oamDispatch.excuteInsert(oamInfo);
//						operationType=EOperationLogType.TUNNELOAMINSERT.getValue();
//					}
//				}
//			}
//			UiUtil.insertOperationLog(operationType, resultStr);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(tunnelServiceMB);
		}
		return this.resultStr;
		
	}

	/**
	 * 单网元PW保存 
	 * @throws Exception
	 */
	public String savePWAction(OamInfo oamInfo) throws Exception {
		PwInfo pwInfo = (PwInfo)((ControlKeyValue)this.dialog.getOamPanel().getOamBasicPanel().getCmbName().getSelectedItem()).getObject();
		oamInfo.getOamMep().setObjType(this.busiType);
		oamInfo.getOamMep().setServiceId(pwInfo.getPwId());
		SiteService_MB siteService = null;
		PwInfoService_MB pwInfoService = null;
		int operationType=0;//操作日志类型
		try {
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			if (ConstantUtil.siteId == pwInfo.getASiteId()) {
				oamInfo.getOamMep().setObjId(pwInfo.getApwServiceId());
			} else if (ConstantUtil.siteId == pwInfo.getZSiteId()) {
				oamInfo.getOamMep().setObjId(pwInfo.getZpwServiceId());
			}
			if (EPwType.forms(pwInfo.getType().getValue()) == EPwType.ETH) {
				oamInfo.setOamObjType("PWETH");
				oamInfo.setOamPath("ne/interfaces/pweth/" + oamInfo.getOamMep().getObjId());
			} else if (EPwType.forms(pwInfo.getType().getValue()) == EPwType.PDH) {
				oamInfo.setOamObjType("PWPDH");
				oamInfo.setOamPath("ne/interfaces/pwpdh/" + oamInfo.getOamMep().getObjId());
			} else if (EPwType.forms(pwInfo.getType().getValue()) == EPwType.SDH) {
				oamInfo.setOamObjType("PWSDH");
				oamInfo.setOamPath("ne/interfaces/pwsdh/" + oamInfo.getOamMep().getObjId());
			} else if (EPwType.forms(pwInfo.getType().getValue()) == EPwType.SDH_PDH) {
				if (oamInfo.getOamMep().getSiteId() == pwInfo.getASiteId()) {
					oamInfo.setOamObjType("PWSDH");
					oamInfo.setOamPath("ne/interfaces/pwsdh/" + oamInfo.getOamMep().getObjId());
				} else {
					oamInfo.setOamObjType("PWPDH");
					oamInfo.setOamPath("ne/interfaces/pwpdh/" + oamInfo.getOamMep().getObjId());
				}
			} else if (EPwType.forms(pwInfo.getType().getValue()) == EPwType.PDH_SDH) {
				if (oamInfo.getOamMep().getSiteId() == pwInfo.getASiteId()) {
					oamInfo.setOamObjType("PWPDH");
					oamInfo.setOamPath("ne/interfaces/pwpdh/" + oamInfo.getOamMep().getObjId());
				} else {
					oamInfo.setOamObjType("PWSDH");
					oamInfo.setOamPath("ne/interfaces/pwsdh/" +oamInfo.getOamMep().getObjId());
				}
			}
			
			DispatchUtil pwDispatch = new DispatchUtil(RmiKeys.RMI_PW);
			pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			PwInfo pwInfoSel = pwInfoService.select(ConstantUtil.siteId, oamInfo.getOamMep().getObjId());
			if (pwInfoSel.getASiteId() == ConstantUtil.siteId) {
				oamInfo.setOamType(OamTypeEnum.AMEP);
			} else {
				oamInfo.setOamType(OamTypeEnum.ZMEP);
			}
			List<OamInfo> oamList = new ArrayList<OamInfo>();
			oamList.add(oamInfo);
			pwInfoSel.setOamList(oamList);
			
			int manufacturer = 0;
			manufacturer = siteService.getManufacturer(oamInfo.getOamMep().getSiteId());
			if (manufacturer == EManufacturer.WUHAN.getValue()) {
				oamInfo.getOamMep().setObjNameLog(pwInfoSel.getPwName());
				OamInfo oamBefore = null;
				if (oamInfo.getOamMep().getId() > 0) {
					oamBefore = new OamInfo();
					OamInfoService_MB oamService = null;
					try {
						oamService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
						OamMepInfo oammepInfoBefore = oamService.queryMep(oamInfo.getOamMep());
						oamBefore.setOamMep(oammepInfoBefore);
						oammepInfoBefore.setObjNameLog(pwInfoService.select(ConstantUtil.siteId, oamBefore.getOamMep().getObjId()).getPwName());
					} catch (Exception e) {
						ExceptionManage.dispose(e, this.getClass());
					} finally {
						UiUtil.closeService_MB(oamService);
					}
					operationType=EOperationLogType.PWOAMUPDATE.getValue();
				} else {
					operationType=EOperationLogType.PWOAMINSERT.getValue();
				}
				resultStr = pwDispatch.excuteUpdate(pwInfoSel);
				AddOperateLog.insertOperLog(this.dialog.getBtnSave(), operationType, resultStr, oamBefore,
						oamInfo, ConstantUtil.siteId, ResourceUtil.srcStr(StringKeysObj.STRING_PW_OAM_CONFIG), "pwOAM");
			} else {
				DispatchUtil oamCXServiceImpl = new DispatchUtil(RmiKeys.RMI_OAM);
				if (oamInfo.getOamMep().getId() > 0) {
					resultStr = oamCXServiceImpl.excuteUpdate(oamInfo);
					operationType = EOperationLogType.PWOAMUPDATE.getValue();
//					this.dialog.getBtnSave().setOperateKey(EOperationLogType.PWOAMUPDATE.getValue());
				} else {
					resultStr = oamCXServiceImpl.excuteInsert(oamInfo);
					operationType = EOperationLogType.PWOAMINSERT.getValue();
//					this.dialog.getBtnSave().setOperateKey(EOperationLogType.PWOAMINSERT.getValue());
				}
			}
			return this.resultStr;
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(pwInfoService);
		}
	}

	/**
	 * 给mip赋值
	 * @param segmentList 段集合
	 * @param list	oam集合
	 * @param oamMip  oamMip对象
	 * @param type 工作类型 work=工作路径 protect=保护路径
	 */
	private void setOamMip(List<Segment> segmentList,List<OamInfo> list,OamMipInfo oamMip,String type) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (OamInfo oamInfo : list) {
			if(null!=oamInfo.getOamMep()){
				map.put(oamInfo.getNeDirect(), oamInfo.getOamMep().getLocalMepId());
			}
		}
		oamMip.setAMId(map.get("a"));
		oamMip.setZMId(map.get("z"));
		map = null;
	}
	
	// 判断MIP和MEPId是否重复
		@SuppressWarnings("rawtypes")
		private boolean checkMepAndMipIdIsRepeat(String type,OamInfoPanel panel,List<OamInfo> oamList,JDialog jdialog) throws Exception {
			Vector dataVector = panel.getMipTableModel().getDataVector();
			Iterator dataIterator = dataVector.iterator();
			Map<Integer, String> mipMap = new HashMap<Integer, String>();
			int mip = 0;
			int siteId = 0;
			int tc = 0;
			while (dataIterator.hasNext()) {
				Vector vector = (Vector) dataIterator.next();
				siteId = this.getSiteId(vector.get(1).toString());
				mip = Integer.valueOf(vector.get(2).toString());
				tc = Integer.valueOf(vector.get(3).toString());
				for(OamInfo oamInfo:oamList){
					if(null!=oamInfo.getOamMep()&&oamInfo.getOamMep().getLocalMepId() == mip){
						DialogBoxUtil.errorDialog(dialog, ResourceUtil.srcStr(StringKeysTip.TIP_MEPIDANDMIPIDISREPEAT));
						return true;
					}
				}
				mipMap.put(siteId, mip+","+tc);
			}
			if("pw".equals(type)){
				AddPDialog addPDialog=(AddPDialog) jdialog;
				addPDialog.setMipMap(mipMap);
			}else if("tunnel".equals(type)){
			AddTunnelPathDialog tunnelDialog=(AddTunnelPathDialog) jdialog;
			tunnelDialog.setMipList(mipMap);
			}
			
			return false;
		}
		
		/**
		 * 根据网元标识ID返回网元主键
		 * @param  name 网元名称
		 * @return
		 * @throws Exception
		 */
		private int getSiteId(String name) throws Exception{
			SiteService_MB siteService=null;
			SiteInst siteInst=new SiteInst();
			List<SiteInst> siteList=new ArrayList<SiteInst>();
			try {
				siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
				siteInst.setCellId(name.toString());
				siteList = siteService.select(siteInst);
				if( null != siteList  && siteList.size()==1){
					return siteList.get(0).getSite_Inst_Id();
				}else{
					throw new Exception("根据cellid查询siteinst出错");
				}
			} catch (Exception e) {
				throw e;
			}finally{
				UiUtil.closeService_MB(siteService);
			}
		}
	
	/**
	 * 初始化业务名称下拉列表
	 */
	private void setBusiName(){
		DefaultComboBoxModel defaultComboBoxModel = null;
		try {
			defaultComboBoxModel = new DefaultComboBoxModel();
			if(0==this.isSingle){
				if(EServiceType.SECTION.toString().equals(this.busiType)){
					defaultComboBoxModel.addElement(new ControlKeyValue("", ((Segment)this.obj).getNAME(), obj));
				}
				if(EServiceType.TUNNEL.toString().equals(this.busiType)){
					defaultComboBoxModel.addElement(new ControlKeyValue("", ((Tunnel)this.obj).getTunnelName(), obj));
					dialog.getOamProtectPanel().getOamBasicPanel().getCmbName().setEnabled(false);
				}
				if(EServiceType.PW.toString().equals(this.busiType)){
					defaultComboBoxModel.addElement(new ControlKeyValue("", ((PwInfo)this.obj).getPwName(), obj));
				}
				if(EServiceType.ELINE.toString().equals(this.busiType)){
					defaultComboBoxModel.addElement(new ControlKeyValue("", ((ElineInfo)this.obj).getName(), obj));
				}
				dialog.getOamPanel().getOamBasicPanel().getCmbName().setEnabled(false);
				
			}else{
				if(EServiceType.SECTION.toString().equals(this.busiType)){
					initSegmentServiceCmb(defaultComboBoxModel);
				}
				if(EServiceType.TUNNEL.toString().equals(this.busiType)){
					initTunnelServiceCmb(defaultComboBoxModel);
				}
				if(EServiceType.PW.toString().equals(this.busiType)){
					initPWServiceCmb(defaultComboBoxModel);
				}
				if(EServiceType.ELINE.toString().equals(this.busiType)){
					initELineServiceCmb(defaultComboBoxModel);
				}
			}
			// 把model添加到下拉列表中
			dialog.getOamPanel().getOamBasicPanel().getCmbName().setModel(defaultComboBoxModel);
		}catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
		}
	}

	/**
	 * 初始化ELine下拉列表
	 * @param defaultComboBoxModel  数据模型
	 */
	private void initELineServiceCmb(DefaultComboBoxModel defaultComboBoxModel) throws Exception {
		ElineInfoService_MB elineService = null;
		List<ElineInfo> elineList = null;
		ElineInfo elineInfo;
		try {
			elineService = (ElineInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Eline);
			if(null!=obj&&0!=((OamInfo)this.obj).getOamMep().getObjId()){
				elineInfo = new ElineInfo();
				elineInfo.setId(((OamInfo)this.obj).getOamMep().getServiceId());
				elineList = elineService.selectByCondition_nojoin(elineInfo);
			}else{
				elineList = elineService.selectNodeBySite(ConstantUtil.siteId);
			}
			for (ElineInfo info : elineList) {
				if (info.getOamList().size() == 0) {
					defaultComboBoxModel.addElement(new ControlKeyValue(info.getId() + "", info.getName(), info));
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(elineService);
			elineList = null;
			elineInfo = null;
		}
	}
	
	/**
	 * 初始化PW下拉列表
	 * @param defaultComboBoxModel  数据模型
	 */
	private void initPWServiceCmb(DefaultComboBoxModel defaultComboBoxModel) {
		PwInfoService_MB pwInfoService = null;
		List<PwInfo> pwInfoList = null;
		try {
			pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			//修改PwInfoService_MB
			if(null!=obj&&0!=((OamInfo)this.obj).getOamMep().getObjId()){
				List<Integer> integers = new ArrayList<Integer>();
				integers.add(((OamInfo)this.obj).getOamMep().getServiceId());
				pwInfoList = pwInfoService.selectServiceIdsByPwIds(integers);
				if(null==pwInfoList||pwInfoList.size()==0)
					return;
				defaultComboBoxModel.addElement(new ControlKeyValue(pwInfoList.get(0).getID() + "", pwInfoList.get(0).getPwName(), pwInfoList.get(0)));
				defaultComboBoxModel.setSelectedItem(new ControlKeyValue(pwInfoList.get(0).getID() + "", pwInfoList.get(0).getPwName(), pwInfoList.get(0)));
			}else{//新建
				pwInfoList = pwInfoService.selectNodeBySiteid(ConstantUtil.siteId);
				for (PwInfo pwInfo : pwInfoList) {
					if (pwInfo.getIsSingle() == 1) {
						if (pwInfo.getOamList().size() == 0) {
							defaultComboBoxModel.addElement(new ControlKeyValue(pwInfo.getID() + "", pwInfo.getPwName(), pwInfo));
						}
					} else {
						if (pwInfo.getOamList().size() == 0) {
							defaultComboBoxModel.addElement(new ControlKeyValue(pwInfo.getID() + "", pwInfo.getPwName(), pwInfo));
						}
						if (pwInfo.getOamList().size() == 1 && (pwInfo.getOamList().get(0).getOamMep().getSiteId() != ConstantUtil.siteId)) {
							defaultComboBoxModel.addElement(new ControlKeyValue(pwInfo.getID() + "", pwInfo.getPwName(), pwInfo));
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(pwInfoService);
			pwInfoList = null;
		}
	}

	/**
	 * 初始化Tunnel下拉列表
	 * @param defaultComboBoxModel  数据模型
	 */
	private void initTunnelServiceCmb(DefaultComboBoxModel defaultComboBoxModel) {

		TunnelService_MB tunnelServiceMB = null;
		List<Tunnel> tunnelList = null;
		Tunnel tunnel;
		try {
			tunnel = new Tunnel();
			tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			//修改
			if(null!=obj){
				if(null!=((OamInfo)this.obj).getOamMep()&&0!=((OamInfo)this.obj).getOamMep().getServiceId()){
					tunnel.setTunnelId(((OamInfo)this.obj).getOamMep().getServiceId());
					tunnelList = tunnelServiceMB.select_nojoin(tunnel);
					if(null==tunnelList || tunnelList.size()==0)
						return;
					defaultComboBoxModel.addElement(new ControlKeyValue(tunnelList.get(0).getTunnelId() + "", tunnelList.get(0).getTunnelName(), tunnelList.get(0)));
					defaultComboBoxModel.setSelectedItem(new ControlKeyValue(tunnelList.get(0).getTunnelId() + "", tunnelList.get(0).getTunnelName(), tunnelList.get(0)));
				}
			}else{//新建
				tunnelList = tunnelServiceMB.selectNodesBySiteId(ConstantUtil.siteId);
				for (Tunnel info : tunnelList) {
					if(!(info.getaSiteId() == 0 && info.getzSiteId() ==0)){
						if(info.getIsSingle()==1){
							if(	info.getOamList().size() == 0){
								if ("1".equals(UiUtil.getCodeById(Integer.parseInt(info.getTunnelType())).getCodeValue())) {
									defaultComboBoxModel.addElement(new ControlKeyValue(info.getTunnelId() + "", info.getTunnelName(), info));
								} else {
									defaultComboBoxModel.addElement(new ControlKeyValue(info.getTunnelId() + "", info.getTunnelName() + "_work", info));
									
								}
							}
							if(null!=info.getProtectTunnel()&&info.getProtectTunnel().getOamList().size() == 0){
								defaultComboBoxModel.addElement(new ControlKeyValue(info.getProtectTunnel().getTunnelId() + "", info.getProtectTunnel().getTunnelName(), info.getProtectTunnel()));
							}
						}else{
							initTunnel(info, defaultComboBoxModel);
							if(null!=info.getProtectTunnel()){
								initTunnel(info.getProtectTunnel(), defaultComboBoxModel);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(tunnelServiceMB);
			tunnelList = null;
			tunnel = null;
		}
	}

	private void initTunnel(Tunnel tunnel,DefaultComboBoxModel defaultComboBoxModel) throws NumberFormatException, Exception{
		boolean hasOam = false;
		for(OamInfo oamInfo : tunnel.getOamList()){
			if(oamInfo.getOamMep() != null && oamInfo.getOamMep().getSiteId() == ConstantUtil.siteId){
				hasOam = true;
				break;
			}
		}
		if(!hasOam&&(tunnel.getaSiteId() == ConstantUtil.siteId || tunnel.getzSiteId() == ConstantUtil.siteId)){
			if(Integer.parseInt(tunnel.getTunnelType()) == 0){
				defaultComboBoxModel.addElement(new ControlKeyValue(tunnel.getTunnelId() + "", tunnel.getTunnelName(), tunnel));
			}else if ("1".equals(UiUtil.getCodeById(Integer.parseInt(tunnel.getTunnelType())).getCodeValue())) {
				defaultComboBoxModel.addElement(new ControlKeyValue(tunnel.getTunnelId() + "", tunnel.getTunnelName(), tunnel));
			} else {
				defaultComboBoxModel.addElement(new ControlKeyValue(tunnel.getTunnelId() + "", tunnel.getTunnelName() + "_work", tunnel));
			}
		}
	}
	/**
	 * 初始化段和eline下拉列表
	 * @param defaultComboBoxModel  数据模型
	 */
	private void initSegmentServiceCmb(DefaultComboBoxModel defaultComboBoxModel) {
		OamInfoService_MB oamInfoService = null;
		List<OamInfo> oamList = null;
		OamInfo oamInfo = null;
		List<PortInst> portInstList = null;
		PortService_MB portService = null;
		PortInst portInst = null;
		OamMepInfo oamMepInfo = null;
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			//修改
			if(null!=obj&&0!=((OamInfo)this.obj).getOamMep().getObjId()){
//				portService = (PortService) ConstantUtil.serviceFactory.newService(Services.PORT);
				portInst = portService.selectPortybyid(((OamInfo)this.obj).getOamMep().getObjId());
				defaultComboBoxModel.addElement(new ControlKeyValue(portInst.getPortId()+"", portInst.getPortName()+"", portInst));
				defaultComboBoxModel.setSelectedItem(new ControlKeyValue(portInst.getPortId()+"", portInst.getPortName()+"", portInst));
			}else{//新建
				oamInfo = new OamInfo();
				oamMepInfo = new OamMepInfo();
				oamMepInfo.setSiteId(ConstantUtil.siteId);
				oamMepInfo.setObjType("SECTION");
				oamInfo.setOamMep(oamMepInfo);
				oamList = oamInfoService.queryBySiteId(oamInfo, OamTypeEnum.AMEP);
				portInst = new PortInst();
				portInst.setPortType("NNI");
				portInst.setSiteId(ConstantUtil.siteId);
				portInstList = portService.select(portInst);
				boolean b = false;
				for (PortInst inst : portInstList) {
					for (OamInfo info : oamList) {
						if (info.getOamMep().getObjId() == inst.getPortId()) {
							b = true;
						}
					}
					if (!b) {
						defaultComboBoxModel.addElement(new ControlKeyValue(inst.getPortId() + "", inst.getPortName(), inst));
					}
					b = false;
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(portService);
			UiUtil.closeService_MB(oamInfoService);
			oamList = null;
			oamInfo = null;
			portInstList = null;
			portInst = null;
		}
	}
	
}
