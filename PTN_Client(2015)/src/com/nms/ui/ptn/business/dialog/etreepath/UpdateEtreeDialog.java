/*
 * AddPWDialog.java
 *
 * Created on __DATE__, __TIME__
 */

package com.nms.ui.ptn.business.dialog.etreepath;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import twaver.Element;
import twaver.Node;
import twaver.PopupMenuGenerator;
import twaver.TView;
import twaver.network.TNetwork;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.path.eth.EtreeInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.model.ptn.path.eth.EtreeInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.ptn.business.dialog.tunnel.TunnelTopoPanel;
import com.nms.ui.ptn.business.etree.EtreeBusinessPanel;

/**
 * 
 * @author __USER__
 */
public class UpdateEtreeDialog extends javax.swing.JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7179311066459197219L;
	private EtreeBusinessPanel etreeBusPanel;
	private int serviceId;
	private List<EtreeInfo> etreeServiceList = null; // 要更新的业务
	List<Tunnel> tunnelList = new ArrayList<Tunnel>();

	Vector<Object> branchVector = new Vector<Object>();
	Vector<Object> selPwVector = new Vector<Object>();
	private TunnelTopoPanel tunnelTopoPanel=null;

	public UpdateEtreeDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
	}

	/** Creates new form AddPWDialog */
	public UpdateEtreeDialog(EtreeBusinessPanel jPanel1, boolean modal, int serviceId) {
		this.setModal(modal);
		this.etreeBusPanel = jPanel1;
		this.serviceId = serviceId;
		try {
			this.etreeServiceList = getEtree(this.serviceId);
		} catch (Exception e1) {
			ExceptionManage.dispose(e1,this.getClass());
		}
		initComponents();
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		JPanel btnJpanel = new JPanel();
		btnJpanel.setLayout(new GridBagLayout());
		leftBtn.setText(ResourceUtil.srcStr(StringKeysBtn.BTN_LEFT_SHIFT));
		leftBtn.setEnabled(false);
		rightBtn.setText(ResourceUtil.srcStr(StringKeysBtn.BTN_RIGHT_SHIFT));
		rightBtn.setEnabled(false);
		addComponent(btnJpanel, leftBtn, 0, 0, 0.0, 0.0, 1, 1, GridBagConstraints.BOTH, new Insets(5, 5, 10, 5), GridBagConstraints.NORTH,
				gridBagConstraints);
		addComponent(btnJpanel, rightBtn, 0, 1, 0.0, 0.0, 1, 1, GridBagConstraints.BOTH, new Insets(10, 5, 5, 5), GridBagConstraints.NORTH,
				gridBagConstraints);
		//
		rightPanel = new JPanel();
		buttomPanel = new JPanel();

		JPanel leftPwPanel = new JPanel();

		leftPwPanel.setLayout(new GridBagLayout());
		JLabel leftPwLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PW_LIST));
		JScrollPane pwScroll = new JScrollPane();
		pwScroll.setPreferredSize(new Dimension(200, 100));
		pwList = new JList();
		pwScroll.setViewportView(pwList);
		addComponent(leftPwPanel, leftPwLabel, 0, 0, 0.0, 0.0, 1, 1, GridBagConstraints.BOTH, new Insets(5, 0, 0, 5), GridBagConstraints.WEST,
				gridBagConstraints);
		addComponent(leftPwPanel, pwScroll, 0, 1, 0.0, 0.0, 1, 1, GridBagConstraints.BOTH, new Insets(10, 0, 0, 5), GridBagConstraints.WEST,
				gridBagConstraints);
		//
		JPanel rightSelPwPanel = new JPanel();
		rightSelPwPanel.setLayout(new GridBagLayout());
		JLabel rightSelLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OPTED_PW));
		JScrollPane selPwScroll = new JScrollPane();
		selPwList = new JList();
		selPwScroll.setViewportView(selPwList);
		selPwScroll.setPreferredSize(new Dimension(200, 100));
		addComponent(rightSelPwPanel, rightSelLabel, 0, 0, 0.0, 0.0, 1, 1, GridBagConstraints.BOTH, new Insets(5, 5, 0, 5), GridBagConstraints.WEST,
				gridBagConstraints);
		addComponent(rightSelPwPanel, selPwScroll, 0, 1, 0.0, 0.0, 1, 1, GridBagConstraints.BOTH, new Insets(10, 5, 0, 5), GridBagConstraints.WEST,
				gridBagConstraints);
		//
		addComponent(buttomPanel, leftPwPanel, 0, 0, 0.1, 0.0, 1, 1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), GridBagConstraints.WEST,
				gridBagConstraints);

		addComponent(buttomPanel, btnJpanel, 0, 1, 0.1, 0.0, 1, 1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER,
				gridBagConstraints);

		addComponent(buttomPanel, rightSelPwPanel, 0, 2, 0.1, 0.0, 1, 1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), GridBagConstraints.EAST,
				gridBagConstraints);

		//
		rightPanel.setLayout(new BorderLayout());
		tunnelTopoPanel=new TunnelTopoPanel();
		rightPanel.add(tunnelTopoPanel, BorderLayout.CENTER);
		rightPanel.add(buttomPanel, BorderLayout.SOUTH);
		jSplitPane1.setRightComponent(rightPanel);

		int width = jSplitPane1.getWidth();
		jSplitPane1.setDividerLocation(width / 10 * 3);

		List<Integer> pwIdList = null;
		try {
			pwIdList = new ArrayList<Integer>();

			for (EtreeInfo info : etreeServiceList) {
				pwIdList.add(info.getPwId());
			}

			tunnelList = getTunnelByPwIdList(pwIdList);
			initData();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			pwIdList = null;
		}
	}

	private List<EtreeInfo> getEtree(int serviceId) throws Exception {
		EtreeInfoService_MB etreeservice = null;
		List<EtreeInfo> etreeInfoList = null;
		EtreeInfo info = null;
		try {
			etreeservice = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);
			info = new EtreeInfo();
			info.setServiceId(serviceId);
			etreeInfoList = etreeservice.select(info);
			return etreeInfoList;
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(etreeservice);
			etreeInfoList = null;
			info = null;
		}
	}

	private void initData() throws Exception {
		showTunnelTopo();
		if (etreeServiceList != null && !etreeServiceList.isEmpty()) {
			etreeNameTextField.setText(etreeServiceList.get(0).getName());
			if (etreeServiceList.get(0).getActiveStatus() == 1) {
				activeCheckBox.setSelected(true);
			} else {
				activeCheckBox.setSelected(false);
			}

		}
		Map<Integer, Integer> rootAcIdAndSiteIdmap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> branchAcIdAndSiteIdMap = new HashMap<Integer, Integer>();

		try {
			for (EtreeInfo info : etreeServiceList) {
				rootAcIdAndSiteIdmap.put(info.getaAcId(), info.getRootSite());
				branchAcIdAndSiteIdMap.put(info.getzAcId(), info.getBranchSite());

				ControlKeyValue kv = new ControlKeyValue("" + info.getPwId(), "SiteId" + "(" + info.getRootSite() + "," + info.getBranchSite() + ")"
						+ "//" + "pwId=" + info.getPwId());
				selPwVector.add(kv);
			}
			selPwList.setListData(selPwVector);

			if (!rootAcIdAndSiteIdmap.isEmpty()) {
				for (Integer rootacId : rootAcIdAndSiteIdmap.keySet()) {

					rootTextField.setText("SiteId=" + rootAcIdAndSiteIdmap.get(rootacId) + "//" + "acId=" + rootacId);
				}
			}
			if (!branchAcIdAndSiteIdMap.isEmpty()) {
				ControlKeyValue kv = null;
				for (Integer branchId : branchAcIdAndSiteIdMap.keySet()) {
					kv = new ControlKeyValue("SiteId" + branchId, "SiteId=" + branchAcIdAndSiteIdMap.get(branchId) + "//" + "acId=" + branchId, null);
					branchVector.add(kv);
				}
				branchList.setListData(branchVector);
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			rootAcIdAndSiteIdmap = null;
			branchAcIdAndSiteIdMap = null;
		}

	}

	private void addComponent(JPanel panel, JComponent component, int gridx, int gridy, double weightx, double weighty, int gridwidth,
			int gridheight, int fill, Insets insets, int anchor, GridBagConstraints gridBagConstraints) {
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;
		gridBagConstraints.weightx = weightx;
		gridBagConstraints.weighty = weighty;
		gridBagConstraints.gridwidth = gridwidth;
		gridBagConstraints.gridheight = gridheight;
		gridBagConstraints.fill = fill;
		gridBagConstraints.insets = insets;
		gridBagConstraints.anchor = anchor;
		panel.add(component, gridBagConstraints);
	}

	public void setAText(String text) {
		this.rootTextField.setText(text);
	}

	/**
	 * 初始化topo
	 * 
	 * @param jComboBox
	 * @param defaultValue
	 * @throws Exception
	 */
	public void showTunnelTopo() throws Exception {

		TNetwork network = null;
		if (tunnelList != null && tunnelList.size() > 0) {
			tunnelTopoPanel.boxData(tunnelList);
		}

		network = tunnelTopoPanel.getNetWork();
		@SuppressWarnings("unchecked")
		List<Element> elementList = network.getDataBox().getAllElements();
		for (Element e : elementList) {
			if (e instanceof Node) {
				for (EtreeInfo info : etreeServiceList) {
					if (((SiteInst) e.getUserObject()).getSite_Inst_Id() == info.getRootSite()) {
						e.setBusinessObject("root");
						e.addAttachment("topoTitle");
						break;
					}
					if (((SiteInst) e.getUserObject()).getSite_Inst_Id() == info.getBranchSite()) {
						e.setBusinessObject("branch");
						e.addAttachment("topoTitle");
						break;
					}
				}
			}
		}
		//
		network.setPopupMenuGenerator(new PopupMenuGenerator() {
			@Override
			public JPopupMenu generate(TView tview, MouseEvent mouseEvent) {
				JPopupMenu menu = new JPopupMenu();

				if (!tview.getDataBox().getSelectionModel().isEmpty()) {
					final Element element = tview.getDataBox().getLastSelectedElement();
					if (element instanceof Node) {
						if (element.getBusinessObject() != null) {
							JMenuItem chooseRootItem = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_SELECT_ROOT));
							JMenuItem chooseBranchItem = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_SELECT_LEAF));
							JMenuItem choosePortItem = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_SELECT_PORT));
							JMenuItem undoItem = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_REMOVE_LEAF));

							// 拓扑上的右键菜单
							addMenuItem(element, menu, chooseRootItem, chooseBranchItem, undoItem, choosePortItem);

						} else {
							return menu;
						}
					}
				}
				return menu;
			}

		});
	}

	private List<Tunnel> getTunnelByPwIdList(List<Integer> pwIdList) {
		List<PwInfo> pwList = new ArrayList<PwInfo>();
		List<Tunnel> tunnelList = new ArrayList<Tunnel>();
		PwInfo pwinfo = null;
		Tunnel tunnel = null;
		TunnelService_MB tunnelservice = null;
		PwInfoService_MB pwService = null;
		try {
			pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			for (Integer pwId : pwIdList) {
				pwinfo = new PwInfo();
				pwinfo.setPwId(pwId);
				pwList.addAll(pwService.select(pwinfo));
			}

			tunnelservice = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			for (PwInfo pw : pwList) {
				tunnel = new Tunnel();
				tunnel.setTunnelId(pw.getTunnelId());
				tunnelList.addAll(tunnelservice.select(tunnel));
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(tunnelservice);
			UiUtil.closeService_MB(pwService);
			pwList = null;
		}
		return tunnelList;
	}

	/*
	 * 拓扑上的右键菜单
	 */
	protected void addMenuItem(Element element, JPopupMenu menu, JMenuItem chooseRootItem, JMenuItem chooseBranchItem, JMenuItem undoItem,
			JMenuItem choosePortItem) {
		chooseRootItem.setEnabled(false);
		chooseBranchItem.setEnabled(false);
		undoItem.setEnabled(false);
		choosePortItem.setEnabled(false);
		menu.add(chooseRootItem);
		menu.add(chooseBranchItem);
		menu.add(undoItem);
		menu.add(choosePortItem);
	}

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		this.dispose();
	}

	private void oKButtonActionPerformed(java.awt.event.ActionEvent evt) {
		DispatchUtil etreeDispatch = null;
		String etreeName = etreeNameTextField.getText();
		for (EtreeInfo info : etreeServiceList) {
			info.setName(etreeName);
			info.setActiveStatus(activeCheckBox.isSelected() ? 1 : 0);
		}
		try {			
			etreeDispatch = new DispatchUtil(RmiKeys.RMI_ETREE);
			String message = etreeDispatch.excuteUpdate(etreeServiceList);
			DialogBoxUtil.succeedDialog(this, message);
			this.dispose();
			this.etreeBusPanel.getController().refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
		
		}

	}

	// GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	@SuppressWarnings("serial")
	private void initComponents() {

		titleLabel = new javax.swing.JLabel();
		jPanel2 = new javax.swing.JPanel();
		oKButton = new PtnButton("",false);
		cancelButton = new javax.swing.JButton();
		jSplitPane1 = new javax.swing.JSplitPane();
		jPanel3 = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();
		etreeNameTextField = new javax.swing.JTextField();
		branchLabel = new javax.swing.JLabel();
		rootLabel = new javax.swing.JLabel();
		rootTextField = new javax.swing.JTextField();
		jLabel5 = new javax.swing.JLabel();
		activeCheckBox = new javax.swing.JCheckBox();
		jScrollPane1 = new javax.swing.JScrollPane();
		branchList = new javax.swing.JList();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		titleLabel.setFont(new java.awt.Font("黑体",0,14));
		titleLabel.setText("\u66f4\u65b0ETREE");

		jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		oKButton.setText("\u786e\u8ba4");
		oKButton.addActionListener(new MyActionListener() {

			@Override
			public boolean checking() {
				return true;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				oKButtonActionPerformed(e);
				
			}
		});

		cancelButton.setText("\u53d6\u6d88");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				jPanel2Layout.createSequentialGroup().addContainerGap(657, Short.MAX_VALUE).addComponent(oKButton).addGap(18, 18, 18).addComponent(
						cancelButton).addGap(7, 7, 7)));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				jPanel2Layout.createSequentialGroup().addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
						jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(oKButton).addComponent(
								cancelButton)).addContainerGap()));

		jSplitPane1.setPreferredSize(new java.awt.Dimension(482, 1002));

		jPanel3.setPreferredSize(new java.awt.Dimension(200, 100));

		jLabel2.setFont(new java.awt.Font("黑体", 0, 14));
		jLabel2.setText("\u540d   \u79f0");

		etreeNameTextField.setHorizontalAlignment(SwingConstants.LEFT);

		branchLabel.setFont(new java.awt.Font("黑体", 0, 14));
		branchLabel.setText("\u53f6\u5b50\u7aef\u53e3");

		rootLabel.setFont(new java.awt.Font("黑体", 0, 14));
		rootLabel.setText("\u6839\u7aef\u53e3");

		rootTextField.setEditable(false);
		rootTextField.setHorizontalAlignment(SwingConstants.LEFT);
		rootTextField.setText("");

		jLabel5.setFont(new java.awt.Font("黑体", 0, 14));
		jLabel5.setText("\u6fc0\u6d3b\u72b6\u6001");

		activeCheckBox.setFont(new java.awt.Font("黑体", 0, 14));
		activeCheckBox.setText("\u662f\u5426\u6fc0\u6d3b");

		branchList.setModel(new javax.swing.AbstractListModel() {
			String[] strings = {};

			@Override
			public int getSize() {
				return strings.length;
			}

			@Override
			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		branchList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		jScrollPane1.setViewportView(branchList);

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel3Layout.createSequentialGroup().addContainerGap().addGroup(
						jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
								jPanel3Layout.createSequentialGroup().addGroup(
										jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
												jPanel3Layout.createSequentialGroup().addComponent(jLabel2).addGap(11, 11, 11)).addGroup(
												jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
														branchLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE).addComponent(
														rootLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE))).addGroup(
										jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(rootTextField,
												javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 109,
												Short.MAX_VALUE).addComponent(etreeNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 109,
												Short.MAX_VALUE).addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING,
												javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))).addGroup(
								jPanel3Layout.createSequentialGroup()
										.addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE).addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(activeCheckBox,
												javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))).addGap(21, 21, 21)));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel3Layout.createSequentialGroup().addGap(26, 26, 26).addGroup(
						jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel2).addComponent(
								etreeNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(18, 18, 18).addGroup(
						jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(rootLabel).addComponent(
								rootTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(18, 18, 18).addGroup(
						jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(branchLabel).addComponent(
								jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(17, 17, 17).addGroup(
								jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel5).addComponent(
										activeCheckBox)).addContainerGap(148, Short.MAX_VALUE)));

		jSplitPane1.setLeftComponent(jPanel3);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(titleLabel,
				javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(jPanel2,
				javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(jSplitPane1,
				javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup().addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
						jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE).addPreferredGap(
						javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)));

		pack();
	}// </editor-fold>
	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				UpdateEtreeDialog dialog = new UpdateEtreeDialog(new javax.swing.JFrame(), true);
				dialog.addWindowListener(new java.awt.event.WindowAdapter() {
					@Override
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					}
				});
				dialog.setVisible(true);
			}
		});
	}

	// GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JCheckBox activeCheckBox;
	private javax.swing.JLabel branchLabel;
	private javax.swing.JList branchList;
	private javax.swing.JButton cancelButton;
	private javax.swing.JTextField etreeNameTextField;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JSplitPane jSplitPane1;
	private PtnButton oKButton;
	private javax.swing.JLabel rootLabel;
	private javax.swing.JTextField rootTextField;
	private javax.swing.JLabel titleLabel;
	// End of variables declaration//GEN-END:variables

	private JPanel rightPanel;
	private JPanel buttomPanel = new JPanel();
	private JList pwList = new JList();

	private JList selPwList = new JList();
	private final JButton leftBtn = new JButton();
	private final JButton rightBtn = new JButton();
}