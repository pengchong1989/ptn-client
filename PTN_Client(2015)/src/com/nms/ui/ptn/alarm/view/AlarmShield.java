package com.nms.ui.ptn.alarm.view;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import com.nms.db.bean.alarm.AlarmShieldInfo;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.path.SegmentService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.ResultString;
import com.nms.service.impl.util.SiteUtil;
import com.nms.service.impl.util.WhImplUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.NeTreePanel;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
/**
 *告警屏蔽 父类
 * @author Administrator
 *
 */
public class AlarmShield extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4733978281069462748L;
	private JLabel shieldModelLabel = null;//屏蔽模式
	private JComboBox shieldModelCombox = null;
	private PtnButton clearAlarm = null;//清除所有的告警
//	private JButton clearAlarmLine;//清除所有的线路
	private JScrollPane contentScrollPane = null;//告警代码
	private JScrollPane alarmLineScrollPane = null;//告警线路
	private JPanel contentPanel = null;
	private JPanel buttonPanel = null;
	private AlarmLineJpanel alarmLineJpanel = null;
	private JComboBox shieldAllAlarm = null;//屏蔽所有的告警
	private JLabel shieldModel = null;
	private PtnButton confirmButton = null;
	//从子类传进来判断是哪种AlarmLineJpanel
	private NeTreePanel neTreePanel = null;
	
	public void setNeTreePanel(NeTreePanel neTreePanel) {
		this.neTreePanel = neTreePanel;
	}

	public JPanel getContentPanel() {
		return contentPanel;
	}

	public JComboBox getShieldModelCombox() {
		return shieldModelCombox;
	}

	public AlarmLineJpanel getAlarmLineJpanel() {
		return alarmLineJpanel;
	}

	public PtnButton getClearAlarm() {
		return clearAlarm;
	}

	public JComboBox getShieldAllAlarm() {
		return shieldAllAlarm;
	}

	public PtnButton getConfirmButton() {
		return confirmButton;
	}

	public AlarmShield() {
		init();
	}

	/*
	 * 初始化界面和数据
	 */
	public void init() {
		initComponents();
		setLayout();
		addListention();
	}

	/**
	 * 初始化控件
	 */
	public void initComponents() {
		contentScrollPane = new JScrollPane();//告警代码
		contentScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		contentScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		alarmLineScrollPane = new JScrollPane();//告警线路
		alarmLineScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		alarmLineScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		shieldModelCombox = new JComboBox();
		if(null == neTreePanel){
			alarmLineJpanel = new AlarmLineJpanel(1);
			shieldModelCombox.setEnabled(true);
		}else{
			alarmLineJpanel = new AlarmLineJpanel(1,neTreePanel);
			//shieldModelCombox.setEnabled(false);
		}
		
		contentPanel = new JPanel();
		contentPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysObj.ALARM_SHILED)));
		shieldModelLabel = new JLabel(ResourceUtil.srcStr(StringKeysObj.ALARM_SHILED_MODEL));
		shieldModelCombox.addItem(ResourceUtil.srcStr(StringKeysObj.ALARM_SHILED_LINE_MODEL));
		shieldModelCombox.addItem(ResourceUtil.srcStr(StringKeysObj.ALARM_SHILED_ALARM_MODEL));
		shieldModelCombox.addItem(ResourceUtil.srcStr(StringKeysObj.ALARM_SHILEDLINE_MODEL));//按线路屏蔽
		shieldAllAlarm = new JComboBox();
		shieldModel = new JLabel(ResourceUtil.srcStr(StringKeysObj.ALARM_SHILEDALLMODEL_MODEL));
		shieldAllAlarm.addItem(" ");
		shieldAllAlarm.addItem(ResourceUtil.srcStr(StringKeysObj.ALARM_SHILEDALLALARM_MODEL));
		shieldAllAlarm.addItem(ResourceUtil.srcStr(StringKeysObj.ALARM_SHILEDALLLINE_MODEL));
		clearAlarm = new PtnButton(ResourceUtil.srcStr(StringKeysObj.ALARM_CLEARALARM_MODEL),false,RootFactory.CORE_MANAGE);
//		clearAlarmLine = new PtnButton(ResourceUtil.srcStr(StringKeysObj.ALARM_CLEARLINE_MODEL),false,RootFactory.CORE_MANAGE);
		confirmButton = new PtnButton(ResourceUtil.srcStr(StringKeysObj.ALARM_SURECLEAR),false,RootFactory.CORE_MANAGE);
		buttonPanel = new JPanel();
	}
	/*
	 * 工具按钮布局
	 */
	public void setButtonLayout() {
		GridBagLayout buttonLayout = new GridBagLayout();
		buttonLayout.columnWidths = new int[] { 40, 40, 40, 40,40,40,40,80 };
		buttonLayout.columnWeights = new double[] { 0, 0, 0, 0, 0,0,0,0.4 };
		buttonLayout.rowHeights = new int[] { 40 };
		buttonLayout.rowWeights = new double[] { 0 };
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		buttonPanel.setLayout(buttonLayout);
		// 操作菜单按钮布局
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		buttonLayout.setConstraints(shieldModelLabel, c);
		buttonPanel.add(shieldModelLabel);
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		buttonLayout.setConstraints(shieldModelCombox, c);
		buttonPanel.add(shieldModelCombox);
		
		
		c.gridx = 2;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		buttonLayout.setConstraints(shieldModel, c);
		buttonPanel.add(shieldModel);
		
		c.gridx = 3;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		buttonLayout.setConstraints(shieldAllAlarm, c);
		buttonPanel.add(shieldAllAlarm);
		
		c.gridx = 4;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		buttonLayout.setConstraints(clearAlarm, c);
		buttonPanel.add(clearAlarm);	
		c.gridx = 5;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 5, 0, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		buttonLayout.setConstraints(confirmButton, c);
		buttonPanel.add(confirmButton);
	}

	/**
	 * 页面布局
	 */
	public void setLayout() {
		setButtonLayout();
		GridBagLayout contentLayout = new GridBagLayout();
		contentPanel.setLayout(contentLayout);
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		contentLayout.setConstraints(buttonPanel, c);
		contentPanel.add(buttonPanel);
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 0.4;
		contentLayout.setConstraints(alarmLineJpanel, c);
		contentPanel.add(alarmLineJpanel);
		GridBagLayout panelLayout = new GridBagLayout();
		this.setLayout(panelLayout);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		panelLayout.setConstraints(contentPanel, c);
		this.add(contentPanel);
	}
	/**
	 * 保存事件
	 */
	private void addListention() {
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		}) ;
		
		shieldModelCombox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		      if(shieldModelCombox.getSelectedIndex() == 1){
		    	  alarmLineJpanel.newAlarmCodeLevel(2);
		      }else if(shieldModelCombox.getSelectedIndex() == 2){
		    	  alarmLineJpanel.newLineCodeLevel(3);
		      }else{
		    	  alarmLineJpanel.newAlarmLevel(1);		    	  
		      }
			}
		});
	}
	
	protected String clearAllAlarm(int inable,int clearOrShield,int siteId){
		
		AlarmShieldInfo alarmShieldInfo = new AlarmShieldInfo();
	    DispatchUtil alarmDispatch = null;
	    String result = "";
		try {
			alarmDispatch = new DispatchUtil(RmiKeys.RMI_ALARM);
			alarmShieldInfo.setNeShield(clearOrShield);
			if(inable ==1){
				//屏蔽告警
				alarmShieldInfo.setLineOrAlarmCode(1);
			}else{
				//屏蔽线路
				alarmShieldInfo.setLineOrAlarmCode(2);
			}
			alarmShieldInfo.setSiteId(siteId);
			alarmShieldInfo.setShieldType(1);
			result = alarmDispatch.alarmShield(alarmShieldInfo); 
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return result;
	}
}

