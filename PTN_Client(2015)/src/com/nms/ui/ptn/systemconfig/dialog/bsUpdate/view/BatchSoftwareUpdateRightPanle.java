package com.nms.ui.ptn.systemconfig.dialog.bsUpdate.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import twaver.table.TTable;
import twaver.table.TTablePopupMenuFactory;
import com.nms.db.bean.equipment.card.CardInst;
import com.nms.db.bean.equipment.manager.UpgradeManage;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.card.CardService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.ResultString;
import com.nms.service.impl.util.WhImplUtil;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
import com.nms.ui.ptn.systemconfig.dialog.bsUpdate.contriller.BatchSoftwareUpdateRightController;

/**
 * 网元批量升级右边面板
 * @author tp
 *
 */
public class BatchSoftwareUpdateRightPanle extends ContentView<SiteInst>{


	private static final long serialVersionUID = 1L;
	private JSplitPane splitPane;
//	private JPanel jPanel;
	private JLabel typeJLabel;
	private JComboBox typeComboBox;//升级类型
	private PtnButton confirm;//升级按钮
	private JPanel upgradePanel;
	private BatchSoftwareUpdateRightPanle view;
	private List<Integer> integers;
	private List<UpgradeManage> upgradeManages;
	private int type =1;
	private JMenuItem setRbootTime;//右键设置重启时间
	private JMenuItem cancelRbootTime;//右键取消重启时间 
	private JMenuItem querySummary;//右键查询摘要
	private JTabbedPane jTabbedPane;
	private SummaryPanel summaryPanel;
	private Map<Integer,List<UpgradeManage>> maps;
	private PtnButton ptnButton;//需弹出进度条
	public BatchSoftwareUpdateRightPanle() {
		super("siteInstTable",RootFactory.CORE_MANAGE);
		view = this;
		init();
		
	}
	
	public void init(){
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_SITE_INFORMATION)));
		initComponent();
		setLayout();
		addActionListener();
	}
	
	private void initComponent() {
		summaryPanel = new SummaryPanel();
		jTabbedPane = new JTabbedPane();
		jTabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_SITE_SUMMARY), summaryPanel);
		
		
		upgradePanel = new JPanel();
		upgradePanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_UPGRADE)));	
		
		confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_UPGRADE),false,RootFactory.DEPLOYMODU);//升级按钮
		splitPane = new JSplitPane();
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		int high = Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue() / 2;
		splitPane.setDividerLocation(high-120);
		splitPane.setBottomComponent(jTabbedPane);
		splitPane.setTopComponent(this.getContentPanel());	

		typeJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TYPE));
		typeComboBox = new JComboBox();
		typeComboBox.addItem(ResourceUtil.srcStr(StringKeysLbl.MCU_PANEL));
		typeComboBox.addItem(ResourceUtil.srcStr(StringKeysLbl.E1_PANEL));		
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		upgradePanel.setLayout(flowLayout);
		upgradePanel.add(typeJLabel);
		upgradePanel.add(typeComboBox);
		upgradePanel.add(confirm);
		setRbootTime = new JMenuItem(ResourceUtil.srcStr(StringKeysLbl.LBL_SET_TIME));
		cancelRbootTime = new JMenuItem(ResourceUtil.srcStr(StringKeysLbl.LBL_CANCLE_RBOOT_TIME));
		querySummary = new JMenuItem(ResourceUtil.srcStr(StringKeysBtn.BTN_UPGRADE_SOFTWARE));
		maps = new HashMap<Integer, List<UpgradeManage>>();
		ptnButton = new PtnButton("",true);
	}
	
	private void setLayout() {
		GridBagLayout panelLayout = new GridBagLayout();
		panelLayout.rowHeights = new int[] { 40 };
		panelLayout.rowWeights = new double[] { 0 };
		panelLayout.rowHeights = new int[] { 10, 530 };
		panelLayout.columnWeights = new double[] { 0, 1.0 };
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
		panelLayout.setConstraints(upgradePanel, c);
		this.add(upgradePanel);
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		panelLayout.setConstraints(splitPane, c);
		this.add(splitPane);
	}
	
	public void addActionListener(){
				
		confirm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ConstantUtil.isCancleRun = false;
				if (view.getAllSelect().size() == 0) {
					DialogBoxUtil.errorDialog(view, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
					return;
				}
				String typeCom = typeComboBox.getSelectedItem().toString();		
				try {
					 List<SiteInst> siteInstList =  view.getAllSelect();
					 //判断是否为在线网元，非在线网元不支持升级！			
					 //判断是否含有703类型的设备，如果含有，则提示703类型的设备不支持升级
//					  for(int i=0;i<siteInstList.size();i++){
//						  if(siteInstList.get(i).getCellType().contains("703")){
//							  DialogBoxUtil.errorDialog(view, ResourceUtil.srcStr(StringKeysTip.TIP_NOT_TYPE));
//							  return;
//						  }							
//					  }
//					  view.setSoftwareType(view.setTypeNum(typeCom, siteInstList));
					 //判断选中的设备是否都已加载了所选择的板卡
					String str1=getUnloadSiteIdNames(siteInstList,typeCom);
					if(!str1.equals("")){
						DialogBoxUtil.errorDialog(view, str1+ResourceUtil.srcStr(StringKeysTip.TIP_UNLOAD_CARD));
					    return;	
					}
					
					final BatchUpgradeVersionsDialog dialog = new BatchUpgradeVersionsDialog(view,view.getType());
					dialog.setSize(new Dimension(400, 130));
					dialog.setLocation(UiUtil.getWindowWidth(dialog.getWidth()), UiUtil.getWindowHeight(dialog.getHeight()));
//					dialog.addWindowListener(new WindowAdapter() {
//						@Override
//						public void windowClosed(WindowEvent e) {
//							dialog.dispose();
//						}
//					});
					dialog.setVisible(true);
					} catch (Exception e) {
						ExceptionManage.dispose(e, this.getClass());
					}

			}
		});
		
		//设置重启时间
		setRbootTime.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					List<SiteInst> siteInsts = view.getAllSelect();
					RebootTimeDialog rebootTimeDialog = new RebootTimeDialog(siteInsts);
					UiUtil.showWindow(rebootTimeDialog, 400, 150);
					List<SiteInst> insts = view.getTable().getAllElement();
					view.clear();
					view.initData(insts);
					view.updateUI();
				} catch (Exception e2) {
					ExceptionManage.dispose(e2, this.getClass());
				}
				
			}
		});
		
		//取消重启时间
		cancelRbootTime.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				List<SiteInst> siteInsts = view.getAllSelect();
				DispatchUtil restartTimeDispatch = null;
				try {
					restartTimeDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					long l = simpleDateFormat.parse("0000-00-00 00:00:00").getTime();
					for(SiteInst siteInst : siteInsts){
						siteInst.setL(l);
					}
					String result = restartTimeDispatch.taskRboot(siteInsts)+"\n";
					DialogBoxUtil.succeedDialog(null, result);
					List<SiteInst> insts = view.getTable().getAllElement();
					view.clear();
					view.initData(insts);
					view.updateUI();
					UiUtil.insertOperationLog(EOperationLogType.CANCLEREBOOTTIME.getValue(), result);
				} catch (Exception e2) {
					ExceptionManage.dispose(e2, this.getClass());
				}
			}
		});
		
		//查询摘要
		querySummary.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ptnButton.doClick();
			}
		});
		ptnButton.addActionListener(new MyActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				List<SiteInst> siteInsts = view.getAllSelect();
				DispatchUtil siteDispatch = null;
				try {
					siteDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
					maps.clear();
					for(SiteInst siteInst : siteInsts){
						if(maps.get(siteInst.getSite_Inst_Id()) == null){
							siteInst.setCardNumber(view.getType()+"");
							List<UpgradeManage> upgradeManages = siteDispatch.softwareSummary(siteInst);
							if(upgradeManages != null){
								maps.put(siteInst.getSite_Inst_Id(), upgradeManages);
							}
						}
					}
					List<UpgradeManage> upgradeManages = new ArrayList<UpgradeManage>();
					for(Integer siteId:maps.keySet()){
						upgradeManages.addAll(maps.get(siteId));
					}
					view.summaryPanel.getTable().clear();
					view.summaryPanel.initData(upgradeManages);
					UiUtil.insertOperationLog(EOperationLogType.BATCHDOWNSSERVISON.getValue(),ResultString.CONFIG_SUCCESS);
				} catch (Exception e2) {
					ExceptionManage.dispose(e2, this.getClass());
				}
			}
			
			@Override
			public boolean checking() {
				return true;
			}
		});
	}
	
		
	/**
	 * 获取已选择但未加载板卡的网元
	 */
	private String getUnloadSiteIdNames(List<SiteInst> siteInstList,String siteName) throws Exception {
		List<Integer> siteIds = null;
		List<Integer> siteIds1 = null;
		List<Integer> ids = null;
		CardService_MB cardService = null;
		String str = "";
		try {
			siteIds1 = new ArrayList<Integer>();
			for (SiteInst siteInst : siteInstList) {
				 siteIds1.add(siteInst.getSite_Inst_Id());					
			}
			cardService = (CardService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CARD);
			CardInst cardinst = new CardInst();
			if(siteName.equals("E1支路卡")){
			  cardinst.setCardName("E1支路卡");	
			}else{
			  cardinst.setCardName("XCTS1");
			}
			siteIds = new ArrayList<Integer>();
			ids = new ArrayList<Integer>();
			for(int i=0;i<siteInstList.size();i++){	
				ids = cardService.select(cardinst,siteInstList.get(i));
				if(ids != null && ids.size()>0){
				siteIds.add(ids.get(0));
				}
			}
			for(int j=0;j<siteIds.size();j++){
			  if(siteIds1.contains(siteIds.get(j))){
				siteIds1.remove(siteIds.get(j));
			   }	
			}
			str = new WhImplUtil().getNeNames(siteIds1);						
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(cardService);			
		}
		
		return str;
	}
	
	
	@Override
	public void setTablePopupMenuFactory() {
		TTablePopupMenuFactory menuFactory = new TTablePopupMenuFactory() {
			@Override
			public JPopupMenu getPopupMenu(TTable ttable, MouseEvent evt) {
				if (SwingUtilities.isRightMouseButton(evt)) {
					JPopupMenu menu = new JPopupMenu();
					menu.add(setRbootTime);
					menu.add(cancelRbootTime);
					menu.add(querySummary);
					checkRoot(setRbootTime, RootFactory.CORE_MANAGE);
					checkRoot(cancelRbootTime, RootFactory.CORE_MANAGE);
					checkRoot(querySummary, RootFactory.CORE_MANAGE);
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
				return null;
			}
		};
		super.setMenuFactory(menuFactory);
	}
	
	@Override
	public List<JButton> setNeedRemoveButtons() {
		List<JButton> buttons = new ArrayList<JButton>();
		buttons.add(this.getAddButton());
		buttons.add(this.getUpdateButton());
		buttons.add(this.getDeleteButton());
		buttons.add(this.getRefreshButton());
		buttons.add(this.getSearchButton());
		buttons.add(this.getSynchroButton());
		return buttons;
	}
	
	@Override
	public void setController() {
		super.controller = new BatchSoftwareUpdateRightController(this);
	}

	public List<Integer> getIntegers() {
		return integers;
	}

	public void setIntegers(List<Integer> integers) {
		this.integers = integers;
	}
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	public List<UpgradeManage> getUpgradeManages() {
		return upgradeManages;
	}

	public void setUpgradeManages(List<UpgradeManage> upgradeManages) {
		this.upgradeManages = upgradeManages;
	}
}
	
	
