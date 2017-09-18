package com.nms.ui.ptn.ne.statusData.view.ethOamTrace;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.oamStatus.OamStatusInfo;
import com.nms.db.bean.ptn.oamStatus.OamTraceHops;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
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
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.ne.statusData.view.ethOamPing.PingOrderControllerDialog;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

public class EthOamTraceQueryPanel extends ContentView<OamTraceHops>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PtnButton jButton;
	private EthOamTraceQueryPanel view;
	private JButton controllerJButton;
	public EthOamTraceQueryPanel() {
		super("OamTraceHops",RootFactory.CORE_MANAGE);
		view = this;
		init();
		add();
	}

	public void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.OAM_TRACE_VPWS)));
		setLayout();
		try {
			getController().refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	@Override
	public List<JButton> setAddButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		jButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SELECT),true);
		controllerJButton = new JButton("控制命令");
		controllerJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				PingOrderControllerDialog controllerDialog = new PingOrderControllerDialog(1);
				UiUtil.showWindow(controllerDialog, 450, 350);
			}
		});
		needRemoveButtons.add(jButton);
		needRemoveButtons.add(controllerJButton);
		return needRemoveButtons;
	}
	
	public void add(){
		jButton.addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SiteService_MB siteService = null;
				SiteInst siteInst = null;
				DispatchUtil siteDispatch = null;
				OamStatusInfo result = null;
				try {
					siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
					siteInst = siteService.select(ConstantUtil.siteId);
					siteInst.setStatusMark(49);
					siteDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
					result = siteDispatch.oamStatus(siteInst);
					if(null!=result&&result.getOamTraceHops() != null && result.getOamTraceHops().size()>0){
						DialogBoxUtil.succeedDialog(null, ResultString.CONFIG_SELECT);
						initValue(result.getOamTraceHops());
						view.updateUI();
						view.initData(result.getOamTraceHops());
						this.insertOpeLog(EOperationLogType.ETHOAMTRACE.getValue(),  ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS), null,null);					
					}else{
						DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_FIND_DETAIL_FAIL));
						this.insertOpeLog(EOperationLogType.ETHOAMTRACE.getValue(),  ResourceUtil.srcStr(StringKeysTip.TIP_FIND_DETAIL_FAIL), null,null);							
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				} finally {
					UiUtil.closeService_MB(siteService);
				}
			}

			private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
				AddOperateLog.insertOperLog(jButton, operationType, result, oldMac, newMac, ConstantUtil.siteId,ResourceUtil.srcStr(StringKeysPanel.OAM_TRACE_VPWS),"");				
			}
			
			@Override
			public boolean checking() {
				// TODO Auto-generated method stub
				return true;
			}
		});
	}
	
	public void initValue(List<OamTraceHops> oamTraceHopsList){
		PortService_MB portService = null;
		PortInst portInst = null;
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			for(OamTraceHops oamTraceHops : oamTraceHopsList){
				portInst = new PortInst();
				portInst.setSiteId(ConstantUtil.siteId);
				portInst.setNumber(oamTraceHops.getInPort());
				if(oamTraceHops.getInPort() == 0){
					oamTraceHops.setInPortName(ResourceUtil.srcStr(StringKeysObj.LSP_TYPE_NO));
				}else{
					portInst = portService.select(portInst).get(0);
					oamTraceHops.setInPortName(portInst.getPortName());
				}
				
				portInst = new PortInst();
				portInst.setSiteId(ConstantUtil.siteId);
				if(oamTraceHops.getOutPort() == 0){
					oamTraceHops.setOutPortName(ResourceUtil.srcStr(StringKeysObj.LSP_TYPE_NO));
				}else{
					portInst.setNumber(oamTraceHops.getOutPort());
					portInst = portService.select(portInst).get(0);
					oamTraceHops.setOutPortName(portInst.getPortName());
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(portService);
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
	public void setController() {
		controller = new EthOamTraceQueryController();
		
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
		needRemoveButtons.add(getExportButton());
		return needRemoveButtons;
	}
	

}
