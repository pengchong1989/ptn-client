package com.nms.ui.ptn.clock.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.clock.FrequencySource;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

public class FrequencyStatusPanel extends ContentView<FrequencySource> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 509626400231210322L;
	private FrequencyStatusPanel frequencyStatusPanel;
	private JSplitPane splitPane;
	private JTabbedPane tabbedPane;
	private JButton jButton;
	private List<FrequencySource> frequencySources;
	public FrequencyStatusPanel() {
		super("clockStatusTable",RootFactory.CORE_MANAGE);
		frequencyStatusPanel = this;
		init();
		add();
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
//		tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_CLOCK_ATTRIBUTE), frequencyBasisJPanel);
	}
	
	@Override
	public void setController() {
		controller = new FrequencyStatusController(frequencyStatusPanel,frequencySources);
	}
	
	@Override
	public List<JButton> setAddButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		jButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SELECT));
		needRemoveButtons.add(jButton);
		return needRemoveButtons;
	}
	
	public void add(){
		jButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SiteService_MB siteService = null;
				SiteInst siteInst = null;
				DispatchUtil siteDispatch = null;
				String result = null;
				try {
					siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
					siteInst = siteService.select(ConstantUtil.siteId);
					siteInst.setStatusMark(20);
					siteDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
//					result = siteDispatch.siteStatus(siteInst);
					DialogBoxUtil.succeedDialog(frequencyStatusPanel, result);
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				} finally {
					UiUtil.closeService_MB(siteService);
				}
			}
		});
	}
	
	public void queryStatus(){
		try {
			this.getController().refresh();
			this.getController().initDetailInfo();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	@Override
	public List<JButton> setNeedRemoveButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		needRemoveButtons.add(getSearchButton());
		needRemoveButtons.add(getAddButton());
		needRemoveButtons.add(getDeleteButton());
		needRemoveButtons.add(getUpdateButton());
		needRemoveButtons.add(getSynchroButton());
		return needRemoveButtons;
	}


	public FrequencyStatusPanel getFrequencyStatusPanel() {
		return frequencyStatusPanel;
	}


	public void setFrequencyStatusPanel(FrequencyStatusPanel frequencyStatusPanel) {
		this.frequencyStatusPanel = frequencyStatusPanel;
	}

}
