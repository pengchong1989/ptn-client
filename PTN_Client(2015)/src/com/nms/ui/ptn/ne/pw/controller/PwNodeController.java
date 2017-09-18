package com.nms.ui.ptn.ne.pw.controller;

import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.ptn.oam.OamMepInfo;
import com.nms.db.bean.ptn.path.pw.MsPwInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.pw.PwNniInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.ptn.qos.QosInfo;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.ptn.BfdInfoService_MB;
import com.nms.model.ptn.oam.OamInfoService_MB;
import com.nms.model.ptn.path.pw.MsPwInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.pw.PwNniInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.ResultString;
import com.nms.service.impl.util.SiteUtil;
import com.nms.service.impl.util.WhImplUtil;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.CheckingUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.business.dialog.pwpath.AddPwFilterDialog;
import com.nms.ui.ptn.ne.camporeData.CamporeDataDialog;
import com.nms.ui.ptn.ne.pw.view.PwAddDialog;
import com.nms.ui.ptn.ne.pw.view.PwPanel;
import com.nms.ui.ptn.systemconfig.dialog.qos.ComparableSort;

public class PwNodeController extends AbstractController {

	private PwPanel view;
	private PwInfo pwFilterCondition = null;
	private int total;
	private int now = 0;
	private List<PwInfo> infos = null;
	public PwNodeController(PwPanel pwnPanel) {
		this.setView(pwnPanel);
	}

	@Override
	public void refresh() throws Exception {
		this.searchAndrefreshdata();
	}

	// 创建
	@Override
	public void openCreateDialog() throws Exception {
		new PwAddDialog(null, this.getView());
	};

	// 删除
	@Override
	public void delete() throws Exception {

		List<PwInfo> pwInfoList = null;
		boolean flag = true;
		DispatchUtil pwDispatch = null;
		String resultStr = null;
		try {
			pwInfoList = this.getView().getAllSelect();

			pwDispatch = new DispatchUtil(RmiKeys.RMI_PW);
			resultStr = pwDispatch.excuteDelete(pwInfoList);
			DialogBoxUtil.succeedDialog(this.getView(), resultStr);
			//添加日志记录
			PtnButton deleteButton=(PtnButton) this.view.getDeleteButton();
			deleteButton.setOperateKey(EOperationLogType.PWDELETE.getValue());
			int operationResult=0;
			if(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS).equals(resultStr)){
				operationResult=1;
			}else{
				operationResult=2;
			}
			deleteButton.setResult(operationResult);

			this.view.getRefreshButton().doClick();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			pwInfoList = null;
			pwDispatch = null;
			resultStr = null;
		}

	};
	
	@Override
	public boolean deleteChecking() {
		List<PwInfo> pwInfoList = null;
		boolean flag = false;
		List<Integer> siteIds = null;
		try {
			pwInfoList = this.getView().getAllSelect();

			for (PwInfo pwinfo : pwInfoList) {
				if (pwinfo.getIsSingle() == 0) {
					flag = true;
					break;
				}
			}
			if (flag) {
				DialogBoxUtil.errorDialog(this.getView(), ResourceUtil.srcStr(StringKeysTip.TIP_DELETE_NODE));
				return false;
			}else{
				//判断是否为在线托管网元
				SiteUtil siteUtil = new SiteUtil();
				if(1==siteUtil.SiteTypeOnlineUtil(ConstantUtil.siteId)){
					WhImplUtil wu = new WhImplUtil();
					siteIds = new ArrayList<Integer>();
					siteIds.add(ConstantUtil.siteId);
					String str=wu.getNeNames(siteIds);
					DialogBoxUtil.errorDialog(this.getView(), ResourceUtil.srcStr(StringKeysTip.TIP_NOT_DELETEONLINE)+""+str+ResourceUtil.srcStr(StringKeysTip.TIP_ONLINENOT_DELETEONLINE));
					return false;  		    		
					}
			}
			
			//删除pw之前先验证该pw是否有按需oam，没有才可删除，否则提示不能删除
			for (PwInfo pw : pwInfoList) {
				//如果为true，说明该条pw有按需oam，不能删除
				if(checkIsOam(pw)){
					flag = true;
					break;
				}
			}
			if(flag){
				DialogBoxUtil.errorDialog(this.getView(), ResourceUtil.srcStr(StringKeysTip.TIP_CLEAN_OAM));
				return false;
			}
			//删除pw之前先验证该pw是否有BFD，没有才可删除，否则提示不能删除
			for (PwInfo pw : pwInfoList) {
				//如果为true，说明该条pw有BFD，不能删除
				if(checkIsBfd(pw)){
					flag = true;
					break;
				}
			}
			if(flag){
				DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_CLEAN_BFD));
				return false;
			}
			
			
			flag = true;
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			pwInfoList = null;
			siteIds = null;
		}
		return flag;
	}
	private boolean checkIsBfd(PwInfo pw) {
		boolean flag=false;
		BfdInfoService_MB bfdService = null;
		List<Integer> pwIds=null;
		List<Integer> pwIds2=null;
		try {
			pwIds=new ArrayList<Integer>();
			pwIds2=new ArrayList<Integer>();
			bfdService = (BfdInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.BFDMANAGEMENT);
			pwIds=bfdService.queryPwIds(pw.getASiteId(), 2);
			pwIds2=bfdService.queryPwIds(pw.getZSiteId(), 2);
            //a
			if(pwIds.contains(pw.getApwServiceId())||pwIds2.contains(pw.getZpwServiceId())){
				flag=true;				
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		finally
		{
			pwIds2=null;
			pwIds=null;
			UiUtil.closeService_MB(bfdService);
		}
		return flag;
	}

	private boolean checkIsOam(PwInfo pw) {
		OamMepInfo mep = null;
		OamInfoService_MB service = null;
		boolean flag = false;
		try {
			mep = new OamMepInfo();
			mep.setObjId(pw.getPwId());
			mep.setObjType("PW_TEST");
			service = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			flag = service.queryByObjIdAndType(mep);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
		return flag;
	}

	private void searchAndrefreshdata() {
		
		PwInfoService_MB pwinfoService = null;
		List<PwInfo> needs = new ArrayList<PwInfo>();
		try {
			pwinfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			//若 filterCondition清空，则进入原有的为过滤的方法
			if (null == this.pwFilterCondition) {
				infos = pwinfoService.selectNodeBySiteid(ConstantUtil.siteId);//原有的未过滤的查询方法
				/********************单独查询多段PW***********************/
				infos.addAll(findMsPwInfo(pwinfoService));
			}else {
				this.pwFilterCondition.setASiteId(ConstantUtil.siteId);
				this.pwFilterCondition.setZSiteId(ConstantUtil.siteId);
				infos = pwinfoService.selectNodeByCondition(this.pwFilterCondition);
				/********************单独查询多段PW***********************/
				infos.addAll(pwFilter(pwinfoService,pwFilterCondition));
			}
			
			if(infos.size() ==0){
				now = 0;
				view.getNextPageBtn().setEnabled(false);
				view.getGoToJButton().setEnabled(false);
			}else{
				now = 1;
				if (infos.size() % ConstantUtil.flipNumber == 0) {
					total = infos.size() / ConstantUtil.flipNumber;
				} else {
					total = infos.size() / ConstantUtil.flipNumber + 1;
				}
				if (total == 1) {
					view.getNextPageBtn().setEnabled(false);
					view.getGoToJButton().setEnabled(false);
				}else{
					view.getNextPageBtn().setEnabled(true);
					view.getGoToJButton().setEnabled(true);
				}
				if (infos.size() - (now - 1) * ConstantUtil.flipNumber > ConstantUtil.flipNumber) {
					needs = infos.subList((now - 1) * ConstantUtil.flipNumber, ConstantUtil.flipNumber);
				} else {
					needs = infos.subList((now - 1) * ConstantUtil.flipNumber, infos.size() - (now - 1) * ConstantUtil.flipNumber);
				}
			}
			view.getCurrPageLabel().setText(now+"");
			view.getTotalPageLabel().setText(total + "");
			view.getPrevPageBtn().setEnabled(false);
			
			this.convertShow(needs);
			this.view.clear();
			this.view.getQosPanel().clear();
			this.view.getPwVlanTablePanel().clear();
			this.view.initData(needs);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(pwinfoService);
		}
	}

	
	private List<PwInfo> pwFilter(PwInfoService_MB pwinfoService,PwInfo pwFilterCondition)
	{
		List<PwInfo> allList = null;
		List<PwInfo> msPwinfo = null;
		TunnelService_MB tunnelServiceMB = null;
		List<Tunnel> tunnelList = null;
		List<Integer> tunnelIds = null;
		try 
		{
			allList = new ArrayList<PwInfo>();
			msPwinfo = findMsPwInfo(pwinfoService);
			tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			//先通过端口号查tunnel，再通过tunnel查pw，然后过滤pw
			tunnelList = tunnelServiceMB.findTunnelByPortId(pwFilterCondition.getPortId());
			tunnelIds = new ArrayList<Integer>();
			if(null !=tunnelList && !tunnelList.isEmpty())
			{
				for(Tunnel tunnel : tunnelList)
				{
					tunnelIds.add(tunnel.getTunnelId());
				}
			}
			
			if(msPwinfo != null && msPwinfo.size() >0)
			{
				for(PwInfo msPwInst : msPwinfo )
				{
					//多段时 将端口的ID都存放在了后标签的入和出中
					if(isFilter(pwFilterCondition,msPwInst,tunnelIds)){
						allList.add(msPwInst);
					}
				}
			}
			
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		}finally
		{
			msPwinfo = null;
			UiUtil.closeService_MB(tunnelServiceMB);
			tunnelList = null;
		}
		return allList ;
	}
	
	
	private boolean isFilter(PwInfo pwInst,PwInfo mspw,List<Integer> tunnelIds){
		boolean falg = false;
		try 
		{
			//mspw 中包含的多段信息只有一条
			if(pwInst.getPortId() > 0 )
			{
				
				if(null != mspw.getMsPwInfos() &&(tunnelIds.contains(mspw.getMsPwInfos().get(0).getFrontTunnelId()) || tunnelIds.contains(mspw.getMsPwInfos().get(0).getBackTunnelId())))
				{
					falg = true;
				}else
				{
					return false;
				}
			}
			if( null != pwInst.getPwName() && !"".equals(pwInst.getPwName()))
			{
				if(mspw.getPwName().contains(pwInst.getPwName()))
				{
					falg = true;
				}else{
					return false;
				}
			}
			if(pwInst.getTunnelId() >0)
			{
				for(MsPwInfo msPwInst : mspw.getMsPwInfos())
				{
					if(msPwInst.getFrontTunnelId() == pwInst.getTunnelId() || msPwInst.getBackTunnelId() == pwInst.getTunnelId()){
						falg = true;
						break;
					}
					falg = false;
				}
				if(!falg)
				{
					return false;
				}
			}
			if(pwInst.getPwStatus() >0)
			{
				if(pwInst.getPwStatus() == mspw.getPwStatus()){
					falg = true;
				}else{
					return false;
				}
			}
			if(pwInst.getInLblMinValue() >0 && pwInst.getInLblMaxValue() >0)
			{
				for(MsPwInfo msPwInst : mspw.getMsPwInfos())
				{
					if((msPwInst.getFrontInlabel() >= pwInst.getInLblMinValue() && msPwInst.getFrontInlabel() <= pwInst.getInLblMaxValue())
						||(msPwInst.getBackInlabel() >= pwInst.getInLblMinValue() && msPwInst.getFrontInlabel() <= pwInst.getBackInlabel())
					){
						falg = true;
					}else
					{
						falg = false;
					}
				}
				if(!falg)
				{
					return false;
				}
			}
			if(pwInst.getOutLblMinValue() >0 && pwInst.getOutLblMaxValue() >0)
			{
				for(MsPwInfo msPwInst : mspw.getMsPwInfos())
				{
					if((msPwInst.getFrontOutlabel() >= pwInst.getOutLblMinValue() && msPwInst.getFrontOutlabel() <= pwInst.getOutLblMaxValue())
						||(msPwInst.getBackOutlabel() >= pwInst.getOutLblMinValue() && msPwInst.getBackOutlabel() <= pwInst.getOutLblMaxValue())
					){
						falg = true;
					}else
					{
						falg = false;	
					}
				}
				if(!falg)
				{
					return false;
				}
			}
			if(pwInst.getType() != null && !pwInst.getType().name().equals("NONE"))
			{
				if(pwInst.getType() != null && pwInst.getType() == mspw.getType())
				{
					falg = true;
				}else
				{
					return false;
				}
			}
			
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		}
		return falg;
	}
	
	
	
	private List<PwInfo> findMsPwInfo(PwInfoService_MB pwinfoService)
	{
		List<PwInfo> allList = null;
		MsPwInfoService_MB msPwInfoService = null;
		PwInfo pwInfo = null;
		List<MsPwInfo> msPwInfoList = null;
		List<MsPwInfo> msPwInfos = null;
		try 
		{
			allList = new ArrayList<PwInfo>();
			msPwInfoService = (MsPwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.MSPWSERVICE);
			msPwInfos = msPwInfoService.selectBySiteId(ConstantUtil.siteId);
			if(msPwInfos != null && msPwInfos.size() >0)
			{
				for(MsPwInfo msPwInfo : msPwInfos){
					pwInfo = pwinfoService.selectByPwIdForMulti(msPwInfo.getPwId(),1);
					msPwInfoList = new ArrayList<MsPwInfo>();
					msPwInfoList.add(msPwInfo);
					pwInfo.setMsPwInfos(msPwInfoList);
					allList.add(pwInfo);
				}
			}
			
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		}finally
		{
			UiUtil.closeService_MB(msPwInfoService);
		}
		return allList;
	}
	/**
	 * 转换成列表显示数据
	 * 
	 * @param pwInfoList
	 * @throws Exception
	 */
	private void convertShow(List<PwInfo> pwInfoList) throws Exception {
		String pwnniname = "";
		TunnelService_MB tunnelServiceMB = null;
		PwNniInfoService_MB acbufferService = null;
		try {
			tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			acbufferService = (PwNniInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwNniBuffer);
			for (PwInfo pwinfo : pwInfoList) {
				// 查询对应的端口配置
				PwNniInfo pwNniInfo = new PwNniInfo();
				int pwnniId = pwinfo.getASiteId() == ConstantUtil.siteId ? pwinfo.getaPortConfigId() : pwinfo.getzPortConfigId();
				if (pwnniId != 0) {
					pwNniInfo.setId(pwnniId);
					pwNniInfo = acbufferService.select(pwNniInfo).get(0);
					pwnniname = pwNniInfo.getName();
				}
				pwinfo.putClientProperty("inLabelValue", pwinfo.getASiteId() == ConstantUtil.siteId ? pwinfo.getInlabelValue() : pwinfo.getOutlabelValue());
				pwinfo.putClientProperty("outLabelValue", pwinfo.getASiteId() == ConstantUtil.siteId ? pwinfo.getOutlabelValue() : pwinfo.getInlabelValue());
				pwinfo.putClientProperty("siteName", pwinfo.getASiteId() == ConstantUtil.siteId ? pwinfo.getAoppositeId() : pwinfo.getZoppositeId());
				if(pwinfo.getTunnelId() == 0)
				{
					pwinfo.putClientProperty("tunnelName","");
				}else
				{
					pwinfo.putClientProperty("tunnelName", tunnelServiceMB.getTunnelName(pwinfo.getTunnelId()));	
				}
				pwinfo.putClientProperty("serviceType", (UiUtil.getCodeByValue("BUSINESSTYPE", pwinfo.getBusinessType())).getCodeName());
				pwinfo.putClientProperty("portConfigName", pwnniname);
				pwinfo.putClientProperty("issingle", pwinfo.getIsSingle() == 0 ? ResourceUtil.srcStr(StringKeysObj.OBJ_NO) : ResourceUtil.srcStr(StringKeysObj.OBJ_YES));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(tunnelServiceMB);
			UiUtil.closeService_MB(acbufferService);
		}
	}

	// 修改
	@Override
	public void openUpdateDialog() throws Exception {
		if (this.getView().getAllSelect().size() == 0) {
			DialogBoxUtil.errorDialog(this.getView(), ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
		} else {
			PwInfo pwInfo = this.getView().getAllSelect().get(0);

//			if (pwInfo.getIsSingle() == 0 && (null == pwInfo.getMsPwInfos()|| pwInfo.getMsPwInfos().isEmpty())) {
			if (pwInfo.getIsSingle() == 0) {
				DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_UPDATE_NODE));
				return;
			}else
			{
				new PwAddDialog(pwInfo, this.getView());
			}
		}
	}

	/**
	 * 选中一条记录后，查看详细信息
	 */
	@Override
	public void initDetailInfo() {
		try {
			this.initVlanTable();
			this.initQosInfos();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * 更新vlan表格数据
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	private void initVlanTable() throws Exception {
		PwInfo pwInfo = null;
		List<PwNniInfo> pwNniInfos = null;
		try {
			pwInfo = this.view.getSelect();
			pwNniInfos = new ArrayList<PwNniInfo>();
			if (pwInfo.getASiteId() == ConstantUtil.siteId) {
				pwNniInfos.add(pwInfo.getaPwNniInfo());
			} else {
				pwNniInfos.add(pwInfo.getzPwNniInfo());
			}

			this.view.getPwVlanTablePanel().clear();
			this.view.getPwVlanTablePanel().initData(pwNniInfos);

		} catch (Exception e) {
			throw e;
		} finally {
			pwInfo = null;
			pwNniInfos = null;
		}

	}

	@SuppressWarnings("unchecked")
	private void initQosInfos() throws Exception {
		List<QosInfo> qosList = null;
		PwInfo pwInfo = null;
		try {
			pwInfo = this.view.getSelect();
			ComparableSort sort = new ComparableSort();
			qosList = (List<QosInfo>) sort.compare(pwInfo.getQosList());
			this.view.getQosPanel().clear();
			this.view.getQosPanel().initData(qosList);
			this.view.updateUI();
		} catch (Exception e) {
			throw e;
		} finally {
			qosList = null;
			pwInfo = null;
		}
	}

	public void setView(PwPanel view) {
		this.view = view;
	}

	public PwPanel getView() {
		return view;
	}

	@Override
	public void synchro() {
		DispatchUtil pwDispatch = null;
		try {
			pwDispatch = new DispatchUtil(RmiKeys.RMI_PW);
			String result = pwDispatch.synchro(ConstantUtil.siteId);
			DialogBoxUtil.succeedDialog(null, result);
			//添加日志记录
			PtnButton deleteButton=(PtnButton) this.view.getSynchroButton();
			deleteButton.setOperateKey(EOperationLogType.PWSYNCHRO.getValue());
			deleteButton.setResult(1);

			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			pwDispatch = null;
		}
	}
	
	
	
	/**
	 * 一致性检测
	 */
	@Override
	public void consistence() throws Exception{
		DispatchUtil  pwDispatch = null;
		PwInfoService_MB pwinfoService = null;
		List<Object> emsList = new ArrayList<Object>();
		try {
			SiteUtil siteUtil=new SiteUtil();
			if (0 == siteUtil.SiteTypeUtil(ConstantUtil.siteId)) {
				pwinfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
				pwDispatch = new DispatchUtil(RmiKeys.RMI_PW);
				List<Object> neList =  (List<Object>)pwDispatch.consistence(ConstantUtil.siteId);
				//普通pw
				emsList.addAll(pwinfoService.selectNodeBySiteid(ConstantUtil.siteId));
				//添加多段PW
				emsList.addAll(findMsPwInfo(pwinfoService));
				CamporeDataDialog dialog = new CamporeDataDialog("pw列表", emsList, neList, this);
				UiUtil.showWindow(dialog, 700, 600);
			}else{
				DialogBoxUtil.errorDialog(this.view, ResultString.QUERY_FAILED);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}finally{
			pwDispatch = null;
			UiUtil.closeService_MB(pwinfoService);
		}
	}
	
	/**
	 * 对单网元侧的tunnel进行过滤查询
	 *	添加tunnel过滤
	 */
	@Override
	public void openFilterDialog() throws Exception {
		if (null == this.pwFilterCondition) {
			this.pwFilterCondition = new PwInfo();//若pwFilterCondition已经清空 ，重新创建实例，以供下次是用
		}
		new AddPwFilterDialog(2,this.pwFilterCondition,this.view);
	};

	/**
	 * 清除tunnel过滤
	 */
	@Override
	public void clearFilter() throws Exception {
		this.pwFilterCondition = null;
		this.refresh();
	}
	
	public void setFilterCondition(PwInfo pwFilterCondition) {
		this.pwFilterCondition = pwFilterCondition;
	}
	
	public PwInfo getFilterCondition() {
		return pwFilterCondition;
	}
	
	@Override
	public void prevPage() throws Exception {
		now = now - 1;
		if (now == 1) {
			view.getPrevPageBtn().setEnabled(false);
		}
		view.getNextPageBtn().setEnabled(true);

		flipRefresh();
	}

	@Override
	public void goToAction() throws Exception {

		if (CheckingUtil.checking(view.getGoToTextField().getText(), CheckingUtil.NUM1_9)) {// 判断填写是否为数字
			Integer goi = Integer.parseInt(view.getGoToTextField().getText());
			if(goi>= total){
				goi = total;
				view.getNextPageBtn().setEnabled(false);
			}
			if(goi == 1){
				view.getPrevPageBtn().setEnabled(false);
			}
			if(goi > 1){
				view.getPrevPageBtn().setEnabled(true);
			}
			if(goi<total){
				view.getNextPageBtn().setEnabled(true);
			}
			now = goi;
			flipRefresh();
		}else{
			DialogBoxUtil.errorDialog(view, ResourceUtil.srcStr(StringKeysTip.MESSAGE_NUMBER));
		}
		
	
	}

	@Override
	public void nextPage() throws Exception {
		now = now + 1;
		if (now == total) {
			view.getNextPageBtn().setEnabled(false);
		}
		view.getPrevPageBtn().setEnabled(true);
		flipRefresh();
	}

	private void flipRefresh() {
		view.getCurrPageLabel().setText(now + "");
		List<PwInfo> needs = null;
		if (now * ConstantUtil.flipNumber > infos.size()) {
			needs = infos.subList((now - 1) * ConstantUtil.flipNumber, infos.size());
		} else {
			needs = infos.subList((now - 1) * ConstantUtil.flipNumber, now * ConstantUtil.flipNumber);
		}
		try {
			this.convertShow(needs);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		this.view.clear();
		this.view.getQosPanel().clear();
		this.view.getPwVlanTablePanel().clear();
		this.view.initData(needs);
		this.view.updateUI();
	}
	
}
