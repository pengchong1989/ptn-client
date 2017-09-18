package com.nms.ui.ptn.ne.pdh.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.nms.db.bean.equipment.port.V35PortInfo;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.port.V35PortService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnPanel;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

public class V35View extends PtnPanel {
	private static final long serialVersionUID = 5673448152495549821L;
	private PtnButton OKButton;
	private PtnButton synchroButton;
	private JPanel buttonPanel = null;
	private JLabel lblTitle;
	private JPanel titlePanel;
	private JPanel contentPanel;
	private JScrollPane jScrollPane;
	private JLabel timeJLabel1;
	private JRadioButton timeJRadioButton1;
	private JLabel timeJLabel2;
	private JRadioButton timeJRadioButton2;
	private JLabel timeJLabel3;
	private JRadioButton timeJRadioButton3;
	private JLabel timeJLabel4;
	private JRadioButton timeJRadioButton4;
	private JLabel timeJLabel5;
	private JRadioButton timeJRadioButton5;
	private JLabel timeJLabel6;
	private JRadioButton timeJRadioButton6;
	private JLabel timeJLabel7;
	private JRadioButton timeJRadioButton7;
	private JLabel timeJLabel8;
	private JRadioButton timeJRadioButton8;
	private JLabel timeJLabel9;
	private JRadioButton timeJRadioButton9;
	private JLabel timeJLabel10;
	private JRadioButton timeJRadioButton10;
	private JLabel timeJLabel11;
	private JRadioButton timeJRadioButton11;
	private JLabel timeJLabel12;
	private JRadioButton timeJRadioButton12;
	private JLabel timeJLabel13;
	private JRadioButton timeJRadioButton13;
	private JLabel timeJLabel14;
	private JRadioButton timeJRadioButton14;
	private JLabel timeJLabel15;
	private JRadioButton timeJRadioButton15;
	private JLabel timeJLabel16;
	private JRadioButton timeJRadioButton16;
	private JLabel timeJLabel17;
	private JRadioButton timeJRadioButton17;
	private JLabel timeJLabel18;
	private JRadioButton timeJRadioButton18;
	private JLabel timeJLabel19;
	private JRadioButton timeJRadioButton19;
	private JLabel timeJLabel20;
	private JRadioButton timeJRadioButton20;
	private JLabel timeJLabel21;
	private JRadioButton timeJRadioButton21;
	private JLabel timeJLabel22;
	private JRadioButton timeJRadioButton22;
	private JLabel timeJLabel23;
	private JRadioButton timeJRadioButton23;
	private JLabel timeJLabel24;
	private JRadioButton timeJRadioButton24;
	private JLabel timeJLabel25;
	private JRadioButton timeJRadioButton25;
	private JLabel timeJLabel26;
	private JRadioButton timeJRadioButton26;
	private JLabel timeJLabel27;
	private JRadioButton timeJRadioButton27;
	private JLabel timeJLabel28;
	private JRadioButton timeJRadioButton28;
	private JLabel timeJLabel29;
	private JRadioButton timeJRadioButton29;
	private JLabel timeJLabel30;
	private JRadioButton timeJRadioButton30;
	private JLabel timeJLabel31;
	private JRadioButton timeJRadioButton31;
	private JLabel fourthLegJLabel;//V35是否接入第4路E1
	private JComboBox fourthLegJComboBox;
	private JLabel timeModelJLabel;//定时模式
	private JComboBox timeModelJComboBox;
	private JLabel frameJLabel;//成帧设置
	private JComboBox frameJComboBox;
	private V35PortInfo v35PortInfo;
	
	public V35View() {
		try {
			initComponents();
			setLayout();
			addButtonListener();
			setValue();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void addButtonListener() {
		try {
			this.OKButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					getValue();
				}
			});
			this.synchroButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						DispatchUtil v35PortDispatch = new DispatchUtil(RmiKeys.RMI_V35PORT);
						String result = v35PortDispatch.synchro(ConstantUtil.siteId);
						DialogBoxUtil.succeedDialog(null, result);
					} catch (Exception e) {
						ExceptionManage.dispose(e, V35View.class);
					}
					
				}
			});
		} catch (Exception e) {

			ExceptionManage.dispose(e,this.getClass());
		}
	}



	private void initComponents() {
		try {
			lblTitle = new JLabel(ResourceUtil.srcStr(StringKeysTab.TAB_V35_PORT));
			titlePanel = new JPanel();
			titlePanel.setBorder(BorderFactory.createEtchedBorder());
			titlePanel.setSize(60, ConstantUtil.INT_WIDTH_THREE);
			titlePanel.add(lblTitle);
			contentPanel = new JPanel();
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(contentPanel);
			jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			fourthLegJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_FOURTH_LEG));
			fourthLegJComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(this.fourthLegJComboBox, "basedInVlanId");
			timeModelJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIME_MODEL));
			timeModelJComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(this.timeModelJComboBox, "TIMEMODEL");
			frameJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_FRAME));
			frameJComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(this.frameJComboBox, "basedInVlanId");
			timeJLabel1 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"1");
			timeJRadioButton1 = new JRadioButton();
			timeJLabel2 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"2");
			timeJRadioButton2 = new JRadioButton();
			timeJLabel3 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"3");
			timeJRadioButton3 = new JRadioButton();
			timeJLabel4 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"4");
			timeJRadioButton4 = new JRadioButton();
			timeJLabel5 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"5");
			timeJRadioButton5 = new JRadioButton();
			timeJLabel6 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"6");
			timeJRadioButton6 = new JRadioButton();
			timeJLabel7 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"7");
			timeJRadioButton7 = new JRadioButton();
			timeJLabel8 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"8");
			timeJRadioButton8 = new JRadioButton();
			timeJLabel9 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"9");
			timeJRadioButton9 = new JRadioButton();
			timeJLabel10 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"10");
			timeJRadioButton10 = new JRadioButton();
			timeJLabel11 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"11");
			timeJRadioButton11 = new JRadioButton();
			timeJLabel12 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"12");
			timeJRadioButton12 = new JRadioButton();
			timeJLabel13 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"13");
			timeJRadioButton13 = new JRadioButton();
			timeJLabel14 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"14");
			timeJRadioButton14 = new JRadioButton();
			timeJLabel15 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"15");
			timeJRadioButton15 = new JRadioButton();
			timeJLabel16 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"16");
			timeJRadioButton16 = new JRadioButton();
			timeJLabel17 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"17");
			timeJRadioButton17 = new JRadioButton();
			timeJLabel18 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"18");
			timeJRadioButton18 = new JRadioButton();
			timeJLabel19 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"19");
			timeJRadioButton19 = new JRadioButton();
			timeJLabel20 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"20");
			timeJRadioButton20 = new JRadioButton();
			timeJLabel21 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"21");
			timeJRadioButton21 = new JRadioButton();
			timeJLabel22 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"22");
			timeJRadioButton22 = new JRadioButton();
			timeJLabel23 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"23");
			timeJRadioButton23 = new JRadioButton();
			timeJLabel24 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"24");
			timeJRadioButton24 = new JRadioButton();
			timeJLabel25 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"25");
			timeJRadioButton25 = new JRadioButton();
			timeJLabel26 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"26");
			timeJRadioButton26 = new JRadioButton();
			timeJLabel27 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"27");
			timeJRadioButton27 = new JRadioButton();
			timeJLabel28 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"28");
			timeJRadioButton28 = new JRadioButton();
			timeJLabel29 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"29");
			timeJRadioButton29 = new JRadioButton();
			timeJLabel30 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"30");
			timeJRadioButton30 = new JRadioButton();
			timeJLabel31 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMESLOT)+"31");
			timeJRadioButton31 = new JRadioButton();
			OKButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),false,RootFactory.CORE_MANAGE);
			synchroButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SYNCHRO),false,RootFactory.CORE_MANAGE);
			buttonPanel = new JPanel();
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void setLayout(){
		setGridBagLayout();
		setButtonLayout();
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		GridBagLayout contentLayout = new GridBagLayout();
		this.setLayout(contentLayout);
		contentLayout.columnWeights = new double[] { 1.0 };
		contentLayout.rowHeights = new int[] { 80, 300, 80};
		contentLayout.rowWeights = new double[] { 0, 0, 0};
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 0, 10, 0);
		c.fill = GridBagConstraints.BOTH;
		contentLayout.setConstraints(titlePanel, c);
		this.add(titlePanel);
		c.gridy = 1;
		c.insets = new Insets(0, 0, 10, 0);
		contentLayout.setConstraints(jScrollPane, c);
		this.add(jScrollPane);
		c.gridy = 2;
		c.insets = new Insets(0, 0, 10, 0);
		contentLayout.setConstraints(buttonPanel, c);
		this.add(buttonPanel);
	}
	
	
	private void setGridBagLayout()  {
		GridBagConstraints gridBagConstraints = null;
		try {
			gridBagConstraints = new GridBagConstraints();
			GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.columnWidths = new int[] { 80, 80, 80, 80,80, 80, 80, 80, 80, 80 , 80, 80  };
			gridBagLayout.columnWeights = new double[] { 0, 0, 0, 0 };
			gridBagLayout.rowHeights = new int[] { 15, 15, 15 };
			gridBagLayout.rowWeights = new double[] { 0, 0, 0 };
			contentPanel.setLayout(gridBagLayout);
			contentPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTab.TAB_V35_PORT)));
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.fill = GridBagConstraints.BOTH;

			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(fourthLegJLabel, gridBagConstraints);
			contentPanel.add(fourthLegJLabel);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(fourthLegJComboBox, gridBagConstraints);
			contentPanel.add(fourthLegJComboBox);
			
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(timeModelJLabel, gridBagConstraints);
			contentPanel.add(timeModelJLabel);
			gridBagConstraints.gridx = 4;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(timeModelJComboBox, gridBagConstraints);
			contentPanel.add(timeModelJComboBox);
			
			gridBagConstraints.gridx = 6;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(frameJLabel, gridBagConstraints);
			contentPanel.add(frameJLabel);
			gridBagConstraints.gridx = 7;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(frameJComboBox, gridBagConstraints);
			contentPanel.add(frameJComboBox);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(timeJLabel1, gridBagConstraints);
			contentPanel.add(timeJLabel1);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(timeJRadioButton1, gridBagConstraints);
			contentPanel.add(timeJRadioButton1);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(timeJLabel2, gridBagConstraints);
			contentPanel.add(timeJLabel2);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(timeJRadioButton2, gridBagConstraints);
			contentPanel.add(timeJRadioButton2);
			
			gridBagConstraints.gridx = 4;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(timeJLabel3, gridBagConstraints);
			contentPanel.add(timeJLabel3);
			gridBagConstraints.gridx = 5;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(timeJRadioButton3, gridBagConstraints);
			contentPanel.add(timeJRadioButton3);
			
			gridBagConstraints.gridx = 6;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(timeJLabel4, gridBagConstraints);
			contentPanel.add(timeJLabel4);
			gridBagConstraints.gridx = 7;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(timeJRadioButton4, gridBagConstraints);
			contentPanel.add(timeJRadioButton4);
			
			gridBagConstraints.gridx = 8;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(timeJLabel5, gridBagConstraints);
			contentPanel.add(timeJLabel5);
			gridBagConstraints.gridx = 9;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(timeJRadioButton5, gridBagConstraints);
			contentPanel.add(timeJRadioButton5);
			
			gridBagConstraints.gridx = 10;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(timeJLabel6, gridBagConstraints);
			contentPanel.add(timeJLabel6);
			gridBagConstraints.gridx = 11;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(timeJRadioButton6, gridBagConstraints);
			contentPanel.add(timeJRadioButton6);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(timeJLabel7, gridBagConstraints);
			contentPanel.add(timeJLabel7);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(timeJRadioButton7, gridBagConstraints);
			contentPanel.add(timeJRadioButton7);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(timeJLabel8, gridBagConstraints);
			contentPanel.add(timeJLabel8);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(timeJRadioButton8, gridBagConstraints);
			contentPanel.add(timeJRadioButton8);
			
			gridBagConstraints.gridx = 4;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(timeJLabel9, gridBagConstraints);
			contentPanel.add(timeJLabel9);
			gridBagConstraints.gridx = 5;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(timeJRadioButton9, gridBagConstraints);
			contentPanel.add(timeJRadioButton9);
			
			gridBagConstraints.gridx = 6;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(timeJLabel10, gridBagConstraints);
			contentPanel.add(timeJLabel10);
			gridBagConstraints.gridx = 7;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(timeJRadioButton10, gridBagConstraints);
			contentPanel.add(timeJRadioButton10);
			
			gridBagConstraints.gridx = 8;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(timeJLabel11, gridBagConstraints);
			contentPanel.add(timeJLabel11);
			gridBagConstraints.gridx = 9;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(timeJRadioButton11, gridBagConstraints);
			contentPanel.add(timeJRadioButton11);
			
			gridBagConstraints.gridx = 10;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(timeJLabel12, gridBagConstraints);
			contentPanel.add(timeJLabel12);
			gridBagConstraints.gridx = 11;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(timeJRadioButton12, gridBagConstraints);
			contentPanel.add(timeJRadioButton12);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(timeJLabel13, gridBagConstraints);
			contentPanel.add(timeJLabel13);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(timeJRadioButton13, gridBagConstraints);
			contentPanel.add(timeJRadioButton13);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(timeJLabel14, gridBagConstraints);
			contentPanel.add(timeJLabel14);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(timeJRadioButton14, gridBagConstraints);
			contentPanel.add(timeJRadioButton14);
			
			gridBagConstraints.gridx = 4;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(timeJLabel15, gridBagConstraints);
			contentPanel.add(timeJLabel15);
			gridBagConstraints.gridx = 5;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(timeJRadioButton15, gridBagConstraints);
			contentPanel.add(timeJRadioButton15);
			
			gridBagConstraints.gridx = 6;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(timeJLabel16, gridBagConstraints);
			contentPanel.add(timeJLabel16);
			gridBagConstraints.gridx = 7;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(timeJRadioButton16, gridBagConstraints);
			contentPanel.add(timeJRadioButton16);
			
			gridBagConstraints.gridx = 8;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(timeJLabel17, gridBagConstraints);
			contentPanel.add(timeJLabel17);
			gridBagConstraints.gridx = 9;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(timeJRadioButton17, gridBagConstraints);
			contentPanel.add(timeJRadioButton17);
			
			gridBagConstraints.gridx = 10;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(timeJLabel18, gridBagConstraints);
			contentPanel.add(timeJLabel18);
			gridBagConstraints.gridx = 11;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(timeJRadioButton18, gridBagConstraints);
			contentPanel.add(timeJRadioButton18);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(timeJLabel19, gridBagConstraints);
			contentPanel.add(timeJLabel19);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(timeJRadioButton19, gridBagConstraints);
			contentPanel.add(timeJRadioButton19);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(timeJLabel20, gridBagConstraints);
			contentPanel.add(timeJLabel20);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(timeJRadioButton20, gridBagConstraints);
			contentPanel.add(timeJRadioButton20);
			
			gridBagConstraints.gridx = 4;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(timeJLabel21, gridBagConstraints);
			contentPanel.add(timeJLabel21);
			gridBagConstraints.gridx = 5;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(timeJRadioButton21, gridBagConstraints);
			contentPanel.add(timeJRadioButton21);
			
			gridBagConstraints.gridx = 6;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(timeJLabel22, gridBagConstraints);
			contentPanel.add(timeJLabel22);
			gridBagConstraints.gridx = 7;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(timeJRadioButton22, gridBagConstraints);
			contentPanel.add(timeJRadioButton22);
			
			gridBagConstraints.gridx = 8;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(timeJLabel23, gridBagConstraints);
			contentPanel.add(timeJLabel23);
			gridBagConstraints.gridx = 9;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(timeJRadioButton23, gridBagConstraints);
			contentPanel.add(timeJRadioButton23);
			
			gridBagConstraints.gridx = 10;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(timeJLabel24, gridBagConstraints);
			contentPanel.add(timeJLabel24);
			gridBagConstraints.gridx = 11;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(timeJRadioButton24, gridBagConstraints);
			contentPanel.add(timeJRadioButton24);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(timeJLabel25, gridBagConstraints);
			contentPanel.add(timeJLabel25);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(timeJRadioButton25, gridBagConstraints);
			contentPanel.add(timeJRadioButton25);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(timeJLabel26, gridBagConstraints);
			contentPanel.add(timeJLabel26);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(timeJRadioButton26, gridBagConstraints);
			contentPanel.add(timeJRadioButton26);
			
			gridBagConstraints.gridx = 4;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(timeJLabel27, gridBagConstraints);
			contentPanel.add(timeJLabel27);
			gridBagConstraints.gridx = 5;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(timeJRadioButton27, gridBagConstraints);
			contentPanel.add(timeJRadioButton27);
			
			gridBagConstraints.gridx = 6;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(timeJLabel28, gridBagConstraints);
			contentPanel.add(timeJLabel28);
			gridBagConstraints.gridx = 7;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(timeJRadioButton28, gridBagConstraints);
			contentPanel.add(timeJRadioButton28);
			
			gridBagConstraints.gridx = 8;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(timeJLabel29, gridBagConstraints);
			contentPanel.add(timeJLabel29);
			gridBagConstraints.gridx = 9;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(timeJRadioButton29, gridBagConstraints);
			contentPanel.add(timeJRadioButton29);
			
			gridBagConstraints.gridx = 10;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(timeJLabel30, gridBagConstraints);
			contentPanel.add(timeJLabel30);
			gridBagConstraints.gridx = 11;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(timeJRadioButton30, gridBagConstraints);
			contentPanel.add(timeJRadioButton30);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 6;
			gridBagLayout.setConstraints(timeJLabel31, gridBagConstraints);
			contentPanel.add(timeJLabel31);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 6;
			gridBagLayout.setConstraints(timeJRadioButton31, gridBagConstraints);
			contentPanel.add(timeJRadioButton31);
			
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * 按钮所在panel布局
	 */
	private void setButtonLayout() {
		GridBagConstraints gridBagConstraints = null;
		GridBagLayout gridBagLayout = null;
		try {
			gridBagLayout = new GridBagLayout();
			gridBagConstraints = new GridBagConstraints();
			gridBagLayout.columnWidths = new int[] { 20, 20 };
			gridBagLayout.columnWeights = new double[] { 1.5, 0 };
			gridBagLayout.rowHeights = new int[] { 21,50 };
			gridBagLayout.rowWeights = new double[] { 0 };

			gridBagConstraints.insets = new Insets(5, 5, 5, 0);
			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(synchroButton, gridBagConstraints);
			buttonPanel.add(synchroButton);
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(OKButton, gridBagConstraints);
			buttonPanel.add(OKButton);
			buttonPanel.setLayout(gridBagLayout);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * 收集界面数据，并下发设备
	 * @return
	 */
	private void getValue(){
		if(v35PortInfo == null){
			v35PortInfo = new V35PortInfo();
		}
		v35PortInfo.setSiteId(ConstantUtil.siteId);
		ControlKeyValue fourthLeg = (ControlKeyValue) this.fourthLegJComboBox.getSelectedItem();
		ControlKeyValue timeModel = (ControlKeyValue) this.timeModelJComboBox.getSelectedItem();
		ControlKeyValue frame = (ControlKeyValue) this.frameJComboBox.getSelectedItem();
		v35PortInfo.setFourthLeg(Integer.parseInt(((Code) fourthLeg.getObject()).getCodeValue()));
		v35PortInfo.setTimeModel(Integer.parseInt(((Code) timeModel.getObject()).getCodeValue()));
		v35PortInfo.setFrame(Integer.parseInt(((Code) frame.getObject()).getCodeValue()));
		v35PortInfo.setTime1(timeJRadioButton1.isSelected()?1:0);
		v35PortInfo.setTime2(timeJRadioButton2.isSelected()?1:0);
		v35PortInfo.setTime3(timeJRadioButton3.isSelected()?1:0);
		v35PortInfo.setTime4(timeJRadioButton4.isSelected()?1:0);
		v35PortInfo.setTime5(timeJRadioButton5.isSelected()?1:0);
		v35PortInfo.setTime6(timeJRadioButton6.isSelected()?1:0);
		v35PortInfo.setTime7(timeJRadioButton7.isSelected()?1:0);
		v35PortInfo.setTime8(timeJRadioButton8.isSelected()?1:0);
		v35PortInfo.setTime9(timeJRadioButton9.isSelected()?1:0);
		v35PortInfo.setTime10(timeJRadioButton10.isSelected()?1:0);
		v35PortInfo.setTime11(timeJRadioButton11.isSelected()?1:0);
		v35PortInfo.setTime12(timeJRadioButton12.isSelected()?1:0);
		v35PortInfo.setTime13(timeJRadioButton13.isSelected()?1:0);
		v35PortInfo.setTime14(timeJRadioButton14.isSelected()?1:0);
		v35PortInfo.setTime15(timeJRadioButton15.isSelected()?1:0);
		v35PortInfo.setTime16(timeJRadioButton16.isSelected()?1:0);
		v35PortInfo.setTime17(timeJRadioButton17.isSelected()?1:0);
		v35PortInfo.setTime18(timeJRadioButton18.isSelected()?1:0);
		v35PortInfo.setTime19(timeJRadioButton19.isSelected()?1:0);
		v35PortInfo.setTime20(timeJRadioButton20.isSelected()?1:0);
		v35PortInfo.setTime21(timeJRadioButton21.isSelected()?1:0);
		v35PortInfo.setTime22(timeJRadioButton22.isSelected()?1:0);
		v35PortInfo.setTime23(timeJRadioButton23.isSelected()?1:0);
		v35PortInfo.setTime24(timeJRadioButton24.isSelected()?1:0);
		v35PortInfo.setTime25(timeJRadioButton25.isSelected()?1:0);
		v35PortInfo.setTime26(timeJRadioButton26.isSelected()?1:0);
		v35PortInfo.setTime27(timeJRadioButton27.isSelected()?1:0);
		v35PortInfo.setTime28(timeJRadioButton28.isSelected()?1:0);
		v35PortInfo.setTime29(timeJRadioButton29.isSelected()?1:0);
		v35PortInfo.setTime30(timeJRadioButton30.isSelected()?1:0);
		v35PortInfo.setTime31(timeJRadioButton31.isSelected()?1:0);
		String result = "";
		V35PortService_MB v35PortService = null;
		try {
			DispatchUtil v35PortDispatch = new DispatchUtil(RmiKeys.RMI_V35PORT);
			if(v35PortInfo.getId() >0){
				v35PortService = (V35PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.V35PORT);
				V35PortInfo v35PortBefore = v35PortService.selectByCondition(v35PortInfo).get(0);
				result = v35PortDispatch.excuteUpdate(v35PortInfo);
				AddOperateLog.insertOperLog(OKButton, EOperationLogType.V35PORTUPDATE.getValue(), result,
						v35PortBefore, v35PortInfo, ConstantUtil.siteId, "V35", "v35");
			}else if(v35PortInfo.getId() == 0){
				result = v35PortDispatch.excuteInsert(v35PortInfo);
				AddOperateLog.insertOperLog(OKButton, EOperationLogType.V35PORTINSERT.getValue(), result,
						null, v35PortInfo, ConstantUtil.siteId, "V35", "v35");
			}
			DialogBoxUtil.succeedDialog(null, result);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}  finally {
			UiUtil.closeService_MB(v35PortService);
		}
	}
	
	/**
	 * 界面初始化赋值
	 */
	private void setValue(){
		V35PortService_MB v35PortService = null;
		try {
			v35PortService = (V35PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.V35PORT);
			V35PortInfo info = new V35PortInfo();
			info.setSiteId(ConstantUtil.siteId);
			List<V35PortInfo> v35PortInfos = v35PortService.selectByCondition(info);
			if(v35PortInfos != null && v35PortInfos.size()>0){
				v35PortInfo = v35PortInfos.get(0);
				super.getComboBoxDataUtil().comboBoxSelectByValue(fourthLegJComboBox,v35PortInfo.getFourthLeg()+"");
				super.getComboBoxDataUtil().comboBoxSelectByValue(timeModelJComboBox,v35PortInfo.getTimeModel()+"");
				super.getComboBoxDataUtil().comboBoxSelectByValue(frameJComboBox,v35PortInfo.getFrame()+"");
				timeJRadioButton1.setSelected(v35PortInfo.getTime1()==1?true:false);
				timeJRadioButton2.setSelected(v35PortInfo.getTime2()==1?true:false);
				timeJRadioButton3.setSelected(v35PortInfo.getTime3()==1?true:false);
				timeJRadioButton4.setSelected(v35PortInfo.getTime4()==1?true:false);
				timeJRadioButton5.setSelected(v35PortInfo.getTime5()==1?true:false);
				timeJRadioButton6.setSelected(v35PortInfo.getTime6()==1?true:false);
				timeJRadioButton7.setSelected(v35PortInfo.getTime7()==1?true:false);
				timeJRadioButton8.setSelected(v35PortInfo.getTime8()==1?true:false);
				timeJRadioButton9.setSelected(v35PortInfo.getTime9()==1?true:false);
				timeJRadioButton10.setSelected(v35PortInfo.getTime10()==1?true:false);
				timeJRadioButton11.setSelected(v35PortInfo.getTime11()==1?true:false);
				timeJRadioButton12.setSelected(v35PortInfo.getTime12()==1?true:false);
				timeJRadioButton13.setSelected(v35PortInfo.getTime13()==1?true:false);
				timeJRadioButton14.setSelected(v35PortInfo.getTime14()==1?true:false);
				timeJRadioButton15.setSelected(v35PortInfo.getTime15()==1?true:false);
				timeJRadioButton16.setSelected(v35PortInfo.getTime16()==1?true:false);
				timeJRadioButton17.setSelected(v35PortInfo.getTime17()==1?true:false);
				timeJRadioButton18.setSelected(v35PortInfo.getTime18()==1?true:false);
				timeJRadioButton19.setSelected(v35PortInfo.getTime19()==1?true:false);
				timeJRadioButton20.setSelected(v35PortInfo.getTime20()==1?true:false);
				timeJRadioButton21.setSelected(v35PortInfo.getTime21()==1?true:false);
				timeJRadioButton22.setSelected(v35PortInfo.getTime22()==1?true:false);
				timeJRadioButton23.setSelected(v35PortInfo.getTime23()==1?true:false);
				timeJRadioButton24.setSelected(v35PortInfo.getTime24()==1?true:false);
				timeJRadioButton25.setSelected(v35PortInfo.getTime25()==1?true:false);
				timeJRadioButton26.setSelected(v35PortInfo.getTime26()==1?true:false);
				timeJRadioButton27.setSelected(v35PortInfo.getTime27()==1?true:false);
				timeJRadioButton28.setSelected(v35PortInfo.getTime28()==1?true:false);
				timeJRadioButton29.setSelected(v35PortInfo.getTime29()==1?true:false);
				timeJRadioButton30.setSelected(v35PortInfo.getTime30()==1?true:false);
				timeJRadioButton31.setSelected(v35PortInfo.getTime31()==1?true:false);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(v35PortService);
		}
	}
}
