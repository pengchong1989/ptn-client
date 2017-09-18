package com.nms.ui.ptn.portlag.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.table.DefaultTableModel;

import com.nms.db.bean.ptn.port.PortLagInfo;
import com.nms.db.bean.ptn.qos.QosQueue;
import com.nms.db.enums.EManufacturer;
import com.nms.db.enums.QosCosLevelEnum;
import com.nms.model.ptn.qos.QosQueueService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.SiteUtil;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.portlag.PortLagController;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
import com.nms.ui.ptn.systemconfig.dialog.qos.dialog.PortQosQueueCommonConfig;
import com.nms.ui.ptn.systemconfig.dialog.qos.dialog.QosQueueCXPortConfigPanel;

public class PortLagPanel extends ContentView<PortLagInfo> {

	private static final long serialVersionUID = -5567062465842134923L;
	private String exportQueue = "1-1,1-1,1-1,1-1,1-1,1-1,1-1,1-1";// 默认出口队列调度策略
	private JSplitPane splitPane;
	private JTabbedPane tabbedPane;
	private PortQosQueueCommonConfig qosQueuePanel;
	private JScrollPane qosJScrollPane;
	private LagPortTablePanel lagPortTablePanel;
	
	public PortLagPanel() throws Exception {
		super(ConstantUtil.siteId,"portLag" ,"cxportLag",RootFactory.CORE_MANAGE);
		init();
	}

	public void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTitle.TIT_LAG_MANAGE)));

		try {
			getController().refresh();

			initComponent();
			setLayout();
			addListeners();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void initComponent() {
		ConstantUtil.QOS_PORT_MAX = 100000;
		tabbedPane = new JTabbedPane();
		// tabbedPane.setMaximumSize(new Dimension(800,500));
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		splitPane.setTopComponent(this.getContentPanel());
		splitPane.setBottomComponent(tabbedPane);

		int high = Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue() / 2;
		splitPane.setDividerLocation(high - 65);

		qosQueuePanel = new QosQueueCXPortConfigPanel();
		qosJScrollPane = new JScrollPane();
		qosJScrollPane.setViewportView(qosQueuePanel);
		
		lagPortTablePanel = new LagPortTablePanel();
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_PORT_INFO), lagPortTablePanel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_QOS_CONFIG), qosJScrollPane);
		tabbedPane.setEnabledAt(1, false);
	}

	public void setLayout() {
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

	@Override
	public List<JButton> setNeedRemoveButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
//		needRemoveButtons.add(getUpdateButton());
		needRemoveButtons.add(getSearchButton());
		return needRemoveButtons;
	}

	private void addListeners() {
		getTable().addElementClickedActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getSelect() == null) {
					// 清除详细面板数据
					lagPortTablePanel.clear();
//					qosQueuePanel.getQosTable().
					tabbedPane.setSelectedIndex(0);
					tabbedPane.setEnabledAt(1, false);
					return;
				} else {
					SiteUtil siteUtil  = new SiteUtil();
					try {
						int manufacturer = siteUtil.getManufacturer(ConstantUtil.siteId);
						if (manufacturer == EManufacturer.WUHAN.getValue()) {
							tabbedPane.setEnabledAt(1, false);
						} else {
							tabbedPane.setEnabledAt(1, true);
						}
					
					} catch (Exception e) {
						ExceptionManage.dispose(e,this.getClass());
					}
					
					getController().initDetailInfo();
				}
			}
		});
		

		qosQueuePanel.getSaveButton().addActionListener(new MyActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// 取点击portlag的
				int selectPortId = getSelect().getId();
				List<QosQueue> list = null;
				PortLagInfo portLagInfo=null;
//				PortInst portInst = new PortInst();
				// PortQosQueueDispatch portQosQueueDispatch = new PortQosQueueDispatch();
				try {
					DispatchUtil qosqueuedispatch = new DispatchUtil(RmiKeys.RMI_QOSQUEUE);
					
					list = findByPortId(selectPortId);
					portLagInfo=getSelect();

					list = defaultSaveToQosQueue(selectPortId, ConstantUtil.siteId, list);
					portLagInfo.setQosQueueList(list);
					String isSuccess = qosqueuedispatch.excuteUpdate(portLagInfo);
					DialogBoxUtil.succeedDialog(null, isSuccess);

				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
				
			}

			@Override
			public boolean checking() {
				return true;
			}
		});
		qosQueuePanel.getPortQosTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent et) {
				if (qosQueuePanel.getPortQosTable().getEditorComponent() == null) {
					return;
				}
				if (qosQueuePanel.getPortQosTable().getSelectedColumn() > 1) {
					this.commitTable(qosQueuePanel.getPortQosTable());
				}
			}

			/*
			 * 使表格数据瞬间变化
			 */
			public void commitTable(JTable table) {

				int selectR = -1;
				int selectC = -1;
				int oldValue = 0;
				int newValue = 0;
				JSpinner spinner = null;
				try {
					if (table.getEditorComponent() instanceof JSpinner) {
						spinner = (JSpinner) table.getEditorComponent();
						JTextField ff = ((JSpinner.NumberEditor) (spinner.getComponents()[2])).getTextField();
						String value = ff.getText();
						((DefaultEditor) spinner.getEditor()).getTextField().setText(value);
						selectR = table.getSelectedRow();
						selectC = table.getSelectedColumn();
						if (selectR >= 0 && selectC >= 0) {
							oldValue = Integer.valueOf(table.getValueAt(selectR, selectC) + "");
						}
						for (char di : value.replace(",", "").toCharArray()) {
							if (!Character.isDigit(di)) {
								return;
							}
						}
						if ("".equals(value.replace(",", ""))) {
							newValue = 0;
						} else if (Long.parseLong(value.replace(",", "")) >= ConstantUtil.QOS_PORT_MAX) {
							newValue = ConstantUtil.QOS_PORT_MAX;
						} else if (Long.parseLong(value.replace(",", "")) <= 0) {
							newValue = 0;
						} else {
							newValue = Integer.parseInt(value.replace(",", ""));
						}
						if (selectC != 2 && selectC != 6 && selectC != 9) {
							spinner.setModel(new SpinnerNumberModel(newValue, 0, ConstantUtil.QOS_PORT_MAX, 1));
						} else if (selectC == 2) {
							spinner.setModel(new SpinnerNumberModel(newValue, 0, ConstantUtil.QOS_PORT_MAX, 64));
						} else if (selectC == 6 || selectC == 9) {
							if (newValue >= 100) {
								newValue = 100;
							}
							spinner.setModel(new SpinnerNumberModel(newValue, 0, 100, 1));
						}

						spinner.commitEdit();
						if (table.isEditing()) {
							table.getCellEditor().stopCellEditing();
						}
						if (selectC == 2) {
							if (newValue % 64 != 0) {
								if (newValue > 64) {
									newValue = ((newValue / 64)) * 64;
								} else {
									newValue = 64;
								}
								table.setValueAt(newValue, selectR, selectC);
							}
						} else {
							table.setValueAt(newValue, selectR, selectC);
						}
					}
				} catch (ParseException e) {
					((DefaultEditor) spinner.getEditor()).getTextField().setText(Integer.valueOf(spinner.getValue().toString()) + "");

					ExceptionManage.dispose(e,this.getClass());
				}
			}
		});
	}
	
	/**
	 * 根据端口ID和类型查询qos队列
	 * 
	 * @param portId
	 * @return
	 * @throws Exception
	 */
	private List<QosQueue> findByPortId(int portId) throws Exception {
		QosQueueService_MB qosQueueService = null;
		QosQueue qos = null;
		List<QosQueue> list = null;
		try {
			qosQueueService = (QosQueueService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosQueue);
			qos = new QosQueue();
			qos.setObjId(portId);
			qos.setObjType("LAG");
			list = qosQueueService.queryByCondition(qos);
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(qosQueueService);
		}

		return list;

	}
	
	/*
	 * 将端口的qos信息保存
	 */
	private List<QosQueue> defaultSaveToQosQueue(int lagid, int siteId, List<QosQueue> qosQueue) {
		DefaultTableModel sectionATableModel = qosQueuePanel.getPortQosTableModel();
		Vector aDataVector = sectionATableModel.getDataVector();
		Iterator dataIterator = null;
		QosQueue queue = null;
		List<QosQueue> qosQueueList = null;
		// A端
		qosQueueList = new ArrayList<QosQueue>();
		dataIterator = aDataVector.iterator();
		while (dataIterator.hasNext()) {
			Vector vector = (Vector) dataIterator.next();
			queue = new QosQueue();
			queue.setServiceId(0);
			queue.setSiteId(siteId);
			queue.setObjType("LAG");
			queue.setQueueType((String) qosQueuePanel.getPortQosQueueComboBox().getSelectedItem());
			queue.setCos(QosCosLevelEnum.from(vector.get(1).toString()));
			queue.setCir(new Integer(vector.get(2).toString()));
			queue.setWeight(new Integer(vector.get(3).toString()));
			queue.setGreenLowThresh(new Integer(vector.get(4).toString()));
			queue.setGreenHighThresh(new Integer(vector.get(5).toString()));
			queue.setGreenProbability(new Integer(vector.get(6).toString()));
			queue.setYellowLowThresh(new Integer(vector.get(7).toString()));
			queue.setYellowHighThresh(new Integer(vector.get(8).toString()));
			queue.setYellowProbability(new Integer(vector.get(9).toString()));
			queue.setWredEnable(vector.get(10).toString().equals("true") ? Boolean.TRUE : Boolean.FALSE);
			queue.setMostBandwidth(ResourceUtil.srcStr(StringKeysObj.QOS_UNLIMITED));
			queue.setObjId(lagid);
			qosQueueList.add(queue);
		}
		if (!qosQueue.isEmpty()) {

			for (int i = 0; i < qosQueueList.size(); i++) {
				qosQueueList.get(i).setId(qosQueue.get(i).getId());
			}
		}

		return qosQueueList;
	}
	

	@Override
	public void setTablePopupMenuFactory() {
		setMenuFactory(null);
	}

	@Override
	public void setController() {
		controller = new PortLagController(this);
	}

	@Override
	public Dimension setDefaultSize() {
		return new Dimension(160, ConstantUtil.INT_WIDTH_THREE);
	}

	public String getExportQueue() {
		return exportQueue;
	}

	public void setExportQueue(String exportQueue) {
		this.exportQueue = exportQueue;
	}

	public LagPortTablePanel getLagPortTablePanel() {
		return lagPortTablePanel;
	}

	public PortQosQueueCommonConfig getQosQueuePanel() {
		return qosQueuePanel;
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

}
