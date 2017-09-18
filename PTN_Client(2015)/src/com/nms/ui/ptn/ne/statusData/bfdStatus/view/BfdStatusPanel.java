package com.nms.ui.ptn.ne.statusData.bfdStatus.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.BfdStatus;
import com.nms.db.bean.ptn.SiteStatusInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.ne.statusData.bfdStatus.controller.BfdStatusController;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

public class BfdStatusPanel extends ContentView<BfdStatus>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6085820139836807260L;
	private PtnButton jButton;
	private BfdStatusPanel view;
	public BfdStatusPanel() {
		super("bfdStatus",RootFactory.CORE_MANAGE);
		view = this;
		init();
		add();
	}
	
	public void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_BFDSTATUS)));
		setLayout();
		try {
			getController().refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	@Override
	public List<JButton> setAddButtons() {
		List<JButton> needAddButtons = new ArrayList<JButton>();
		jButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SELECT),true);
		needAddButtons.add(jButton);
		return needAddButtons;
	}
	
	public void add(){
		jButton.addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SiteService_MB siteService = null;
				SiteInst siteInst = null;
				DispatchUtil siteDispatch = null;
				SiteStatusInfo result = null;
				try {
					siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
					siteInst = siteService.select(ConstantUtil.siteId);
					siteInst.setStatusMark(67);
					siteDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
					result = siteDispatch.siteStatus(siteInst);
					if(null!=result&& result.getBfdStatusList()!= null){
						DialogBoxUtil.succeedDialog(null, ResultString.CONFIG_SELECT);						
						initValue(result.getBfdStatusList());
						view.updateUI();
						view.initData(result.getBfdStatusList());
						this.insertOpeLog(EOperationLogType.BFDSELECT.getValue(),  ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS), null,null);
					}else{
						DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_FIND_DETAIL_FAIL));
						this.insertOpeLog(EOperationLogType.BFDSELECT.getValue(),  ResourceUtil.srcStr(StringKeysTip.TIP_FIND_DETAIL_FAIL), null,null);			
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				} finally {
					UiUtil.closeService_MB(siteService);
				}
			}

			private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
			   AddOperateLog.insertOperLog(jButton, operationType, result, oldMac, newMac, ConstantUtil.siteId,ResourceUtil.srcStr(StringKeysPanel.PANEL_BFDSTATUS),"");
			}
			
			@Override
			public boolean checking() {
				// TODO Auto-generated method stub
				return true;
			}
		});
	}
	
	public void initValue(List<BfdStatus> bfdStatusList){
		TunnelService_MB tunnelService = null;
		Tunnel tunnel = null;
		PwInfoService_MB pwService = null;
		PortService_MB portService = null;
		PortInst portInst = null;
		try {
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			for(BfdStatus bfdStatus : bfdStatusList){
				//在这里抓异常 可以防止每条bfd 状态信息 各不影响
				try {
					tunnel = tunnelService.selectBySiteIdAndServiceId(ConstantUtil.siteId, bfdStatus.getLspId());
					if(bfdStatus.getPwId()!=0){
					   String pwName= pwService.selectBysiteIdAndServiceId(ConstantUtil.siteId, bfdStatus.getPwId()).get(0).getPwName();
					   bfdStatus.setPwName(pwName);
					}else{
					   bfdStatus.setPwName("");	
					}
					bfdStatus.setLspName(tunnel.getTunnelName());			
					if(bfdStatus.getPortId()>0 && bfdStatus.getPortId()<26){
						portInst = new PortInst();
						portInst.setSiteId(ConstantUtil.siteId);
						portInst.setNumber(bfdStatus.getPortId());
						portInst = portService.select(portInst).get(0);
						bfdStatus.setPortName(portInst.getPortName());
					}else if(bfdStatus.getPortId()==0){
						bfdStatus.setPortName("无");
					}else if(bfdStatus.getPortId()==101){
						bfdStatus.setPortName("LAG1");
					}else if(bfdStatus.getPortId()==102){
						bfdStatus.setPortName("LAG2");
					}else if(bfdStatus.getPortId()==103){
						bfdStatus.setPortName("LAG3");
					}else if(bfdStatus.getPortId()==104){
						bfdStatus.setPortName("LAG4");
					}else if(bfdStatus.getPortId()==105){
						bfdStatus.setPortName("LAG5");
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(pwService);
			UiUtil.closeService_MB(tunnelService);
			UiUtil.closeService_MB(portService);
			tunnel = null;
			portInst=null;
		}
	}
	
	private void setLayout() {
		GridBagLayout panelLayout = new GridBagLayout();
		this.setLayout(panelLayout);
		panelLayout.rowHeights = new int[] {400, 10};
		panelLayout.rowWeights = new double[] {0, 0};
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		panelLayout.setConstraints(this.getContentPanel(), c);
		this.add(this.getContentPanel());
	}
	
	
	@Override
	public List<JButton> setNeedRemoveButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		needRemoveButtons.add(getSearchButton());
		needRemoveButtons.add(getAddButton());
		needRemoveButtons.add(getDeleteButton());
		needRemoveButtons.add(getUpdateButton());
		needRemoveButtons.add(getSynchroButton());
		needRemoveButtons.add(getRefreshButton());
		return needRemoveButtons;
	}

	@Override
	public void setController() {
		controller = new BfdStatusController(this);
		
	}
	
}
