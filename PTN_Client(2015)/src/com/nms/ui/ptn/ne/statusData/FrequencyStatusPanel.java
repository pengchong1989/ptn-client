package com.nms.ui.ptn.ne.statusData;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.SiteStatusInfo;
import com.nms.db.bean.ptn.clock.FrequencySource;
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
import com.nms.ui.ptn.clock.controller.FrequencyStatusController;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

public class FrequencyStatusPanel extends ContentView<FrequencySource> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 509626400231210322L;
	private FrequencyStatusPanel view;
	private JSplitPane splitPane;
	private JTabbedPane tabbedPane;
	private PtnButton jButton;
	public FrequencyStatusPanel() {
		super("clockStatusTable",RootFactory.CORE_MANAGE);
		view = this;
		init();
		addli();
	}

	public void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_CLOCKSTATUS)));
		initComponent();
		setLayout();
	}
	
	private void initComponent() {
		tabbedPane = new JTabbedPane();
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		int high = Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue() / 2;
		splitPane.setDividerLocation(high - 65);
		splitPane.setTopComponent(this.getContentPanel());
//		splitPane.setBottomComponent(tabbedPane);
	}
	
	public void setLayout() {
		setTabbedPaneLayout();
		GridBagLayout panelLayout = new GridBagLayout();
		this.setLayout(panelLayout);
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		panelLayout.setConstraints(splitPane, c);
		this.add(splitPane);
	}
	
	public void setTabbedPaneLayout() {
		tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_CLOCK_ATTRIBUTE), view);
	}
	

	
	@Override
	public List<JButton> setAddButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		jButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SELECT),true);
		needRemoveButtons.add(jButton);
		return needRemoveButtons;
	}
	
	
	public void addli(){
		jButton.addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SiteService_MB siteService = null;
				SiteInst siteInst = null;
				DispatchUtil siteDispatch = null;
				SiteStatusInfo result = null;
				List<FrequencySource> infos = null;
				try {
					siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
					siteInst = siteService.select(ConstantUtil.siteId);
					siteInst.setStatusMark(21);
					siteDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
					result = siteDispatch.siteStatus(siteInst);
					if(null!=result&&result.getFrequencySources() != null){
						infos = result.getFrequencySources();
						view.updateUI();
						view.initData(initvalue(infos));
						DialogBoxUtil.succeedDialog(view, ResultString.CONFIG_SELECT);
						this.insertOpeLog(EOperationLogType.CLOCKSTATUS.getValue(),  ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS), null,null);									
					}else{
						DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_FIND_DETAIL_FAIL));
						this.insertOpeLog(EOperationLogType.CLOCKSTATUS.getValue(),  ResourceUtil.srcStr(StringKeysTip.TIP_FIND_DETAIL_FAIL), null,null);	
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				} finally {
					UiUtil.closeService_MB(siteService);
				}
			}
			private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){			
				AddOperateLog.insertOperLog(jButton, operationType, result, oldMac, newMac, ConstantUtil.siteId,ResourceUtil.srcStr(StringKeysPanel.PANEL_CLOCKSTATUS),"");				
			}
			
			@Override
			public boolean checking() {
				// TODO Auto-generated method stub
				return true;
			}
		});
	}
	
	public List<FrequencySource> initvalue(List<FrequencySource> infos){
		List<FrequencySource> frequencySourceList = null;
		PortService_MB portService = null;
		PortInst portInst = null;
		if(infos != null){
			frequencySourceList = new ArrayList<FrequencySource>();
			SiteService_MB siteService = null;
			try {
				siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
				portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
				for(FrequencySource frequencySource : infos){
//					if(frequencySource.getClockName() != 255){
						portInst = new PortInst();
						portInst.setSiteId(ConstantUtil.siteId);
						
						String siteType = siteService.getSiteType(ConstantUtil.siteId);
//						if("710A".equals(siteType) && frequencySource.getClockName()>10){
//							portInst.setNumber(frequencySource.getClockName()+4);
//						}else{
							if(("703-1A".equals(siteType) || "703-2A".equals(siteType) || "703B".equals(siteType))
									&& frequencySource.getClockName() > 4){
								portInst.setNumber(frequencySource.getClockName()+2);
							}else if("703-4A".equals(siteType) && frequencySource.getClockName() > 2){
								portInst.setNumber(frequencySource.getClockName()+2);
							}else{
								portInst.setNumber(frequencySource.getClockName());
							}
//						}
						if(frequencySource.getClockName() == 0){
							frequencySource.setClockNameString(ResourceUtil.srcStr(StringKeysObj.OUTER_CLOCK));
							frequencySourceList.add(frequencySource);
						}else{
							List<PortInst> portList = portService.select(portInst);
							if(portList.size() > 0){
								portInst = portList.get(0);
								if(portInst.getPortName().contains("e1")){
									frequencySource.setClockNameString("E1");
								}else{
									frequencySource.setClockNameString(portInst.getPortName());								
								}
								frequencySourceList.add(frequencySource);
							}
						}
					}
//				}
			} catch (Exception e) {
				ExceptionManage.dispose(e,this.getClass());
			} finally {
				UiUtil.closeService_MB(portService);
				UiUtil.closeService_MB(siteService);
			}
		}
		if(frequencySourceList != null){
			return frequencySourceList;
		}
		return null;
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
		controller = new FrequencyStatusController(this);
	}
	
	public FrequencyStatusPanel getFrequencyStatusPanel() {
		return view;
	}


	public void setFrequencyStatusPanel(FrequencyStatusPanel frequencyStatusPanel) {
		this.view = frequencyStatusPanel;
	}

	public JButton getJButton() {
		return jButton;
	}

	public void setJButton(PtnButton button) {
		jButton = button;
	}
}
