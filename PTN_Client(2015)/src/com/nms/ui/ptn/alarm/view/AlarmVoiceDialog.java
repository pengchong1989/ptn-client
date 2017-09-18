﻿package com.nms.ui.ptn.alarm.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.nms.db.enums.EOperationLogType;
import com.nms.model.alarm.AlarmVoiceService_MB;
import com.nms.model.alarm.CurAlarmService_MB;
import com.nms.model.util.Services;
import com.nms.ui.Ptnf;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnSpinner;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.util.TopologyUtil;
import com.nms.ui.ptn.alarm.model.AlarmVoiceInfo;
import com.nms.ui.ptn.basicinfo.NetWorkInfoPanel;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
import com.nms.ui.topology.NetworkElementPanel;
import com.nms.ui.topology.ShelfTopology;
/**
 * 告警声音对话框
 * @author Administrator
 *
 */
public class AlarmVoiceDialog extends PtnDialog {

	private static final long serialVersionUID = 4092296164233361468L;
	private final GridBagLayout gridBagLayout = new GridBagLayout();// 网格布局
	private final GridBagConstraints g = new GridBagConstraints();
	private List<AlarmVoiceInfo> voiceList = null;
	private String[] soundPathArr = new String[]{"config/sounds/critical.wav","config/sounds/major.wav",
			"config/sounds/minor.wav","config/sounds/warning.wav"};
	private int[] colorRGBArr = {-1564897,-26368,-1447651,-7368720};
	
	public AlarmVoiceDialog() {
		init();
	}
	
	/**
	 * 初始化
	 */
	private void init() {
		try{
			voiceList = getAlarmVoiceList();
			this.setTitle(ResourceUtil.srcStr(StringKeysTab.TAB_ALARM_VOICE));
			this.initComponents();
			this.setLayout();
			this.initData();
			this.addActionListener();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * 从数据库查询之前的告警声音设置
	 * @return
	 */
	private List<AlarmVoiceInfo> getAlarmVoiceList() {
		List<AlarmVoiceInfo> alarmVoiceList = null;
 		AlarmVoiceService_MB service = null;
		try {
			service = (AlarmVoiceService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ALARMVOICE);
			alarmVoiceList = service.queryAllVoice();
			if(alarmVoiceList.size() == 0){
				AlarmVoiceInfo voice = null;
				for (int i = 0; i < 4; i++) {
					voice = new AlarmVoiceInfo();
					voice.setAlarmType(i+1);
					voice.setAlarmContinueTime(60);
					voice.setAlarmSwitch(1);
					voice.setAlarmSoundPath(soundPathArr[i]);
					voice.setAlarmColorRGB(colorRGBArr[i]);
					alarmVoiceList.add(voice);
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
		
		return alarmVoiceList;
	}

	/**
	 * 初始化组件
	 * @throws Exception 
	 */
	private void initComponents() throws Exception {
		alarmLevelLabel = new JLabel(ResourceUtil.srcStr(StringKeysObj.ALARM_LEVEL));
		continueTimeLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CONTINUE_TIME));
		voiceSwitchLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ALARM_SWITCH));
		urgencyLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_UrgencyAlarm));
		majorLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MajorAlarm));
		minorLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MinorAlarm));
		promptLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PromptAlarm));
		urgencyVoiceJcb = new JCheckBox();
		majorVoiceJcb = new JCheckBox();
		minorVoiceJcb = new JCheckBox();
		promptVoiceJcb = new JCheckBox();
		urgencyTimeJsp = new PtnSpinner(urgencyVoiceJcb,300,60,60,1);
		majorTimeJsp = new PtnSpinner(majorVoiceJcb,300,60,60,1);
		minorTimeJsp = new PtnSpinner(minorVoiceJcb,300,60,60,1);
		promptTimeJsp = new PtnSpinner(promptVoiceJcb,300,60,60,1);
		alarmSoundLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ALARM_VOICE_SELECTED));
		urgencySoundField = new JTextField("config/sounds/warning.wav");
		urgencySoundField.setEnabled(false);
		majorSoundField = new JTextField("config/sounds/minor.wav");
		majorSoundField.setEnabled(false);
		minorSoundField = new JTextField("config/sounds/major.wav");
		minorSoundField.setEnabled(false);
		promptSoundField = new JTextField("config/sounds/critical.wav");
		promptSoundField.setEnabled(false);
		urgencySoundBtn = new JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_SELECTED));
		majorSoundBtn = new JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_SELECTED));
		minorSoundBtn = new JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_SELECTED));
		promptSoundBtn = new JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_SELECTED));
		fileChooser = new JFileChooser();
		colorLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_COLOR_SELECTED));
		urgencyColorBtn = new JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_SELECTED));
		urgencyColorBtn.setBackground(new Color(-1564897));//红色
		majorColorBtn = new JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_SELECTED));
		majorColorBtn.setBackground(new Color(-26368));//橙色
		minorColorBtn = new JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_SELECTED));
		minorColorBtn.setBackground(new Color(-1447651));//黄色
		promptColorBtn = new JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_SELECTED));
		promptColorBtn.setBackground(new Color(-7368720));//紫色
		saveBtn = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),false,RootFactory.ALARM_MANAGE);
		resetBtn = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_RESET_VOICE));
		cancelBtn = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		panelBtn = new JPanel();
	}

	/**
	 * 设置布局
	 */
	private void setLayout() {
		this.setBtnLayOut();
		gridBagLayout.columnWidths = new int[] { 20, 80, 20, 120, 20, 50, 20, 180, 20, 30, 20, 20, 20};
		gridBagLayout.rowHeights = new int[] {40, 40, 40, 40, 40, 30 ,60};
		this.setLayout(gridBagLayout);
		g.fill = GridBagConstraints.HORIZONTAL;
		g.anchor = GridBagConstraints.CENTER;
		g.insets = new Insets(5, 5, 5, 5);
		// 第1行
		g.gridx = 1;
		g.gridy = 0;
		gridBagLayout.setConstraints(this.alarmLevelLabel, g);
		this.add(this.alarmLevelLabel);
		g.gridx = 3;
		g.insets = new Insets(5, 25, 5, 5);
		gridBagLayout.setConstraints(this.continueTimeLabel, g);
		this.add(this.continueTimeLabel);
		g.gridx = 5;
		g.insets = new Insets(5, 5, 5, 5);
		gridBagLayout.setConstraints(this.voiceSwitchLabel, g);
		this.add(this.voiceSwitchLabel);
		g.gridx = 7;
		g.insets = new Insets(5, 60, 5, 5);
		gridBagLayout.setConstraints(this.alarmSoundLabel, g);
		this.add(this.alarmSoundLabel);
		g.gridx = 11;
		g.insets = new Insets(5, 20, 5, 5);
		gridBagLayout.setConstraints(this.colorLabel, g);
		this.add(this.colorLabel);
		// 第2行
		g.gridx = 1;
		g.gridy = 1;
		g.insets = new Insets(5, 5, 5, 5);
		gridBagLayout.setConstraints(this.urgencyLabel, g);
		this.add(this.urgencyLabel);
		g.gridx = 3;
		gridBagLayout.setConstraints(this.urgencyTimeJsp, g);
		this.add(this.urgencyTimeJsp);
		g.gridx = 5;
		g.insets = new Insets(5, 20, 5, 5);
		gridBagLayout.setConstraints(this.urgencyVoiceJcb, g);
		this.add(this.urgencyVoiceJcb);
		g.gridx = 7;
		gridBagLayout.setConstraints(this.urgencySoundField, g);
		this.add(this.urgencySoundField);
		g.gridx = 9;
		gridBagLayout.setConstraints(this.urgencySoundBtn, g);
		this.add(this.urgencySoundBtn);
		g.gridx = 11;
		g.fill = GridBagConstraints.NONE;
		gridBagLayout.setConstraints(this.urgencyColorBtn, g);
		this.add(this.urgencyColorBtn);
		// 第3行
		g.gridx = 1;
		g.gridy = 2;
		g.insets = new Insets(5, 5, 5, 5);
		g.fill = GridBagConstraints.HORIZONTAL;
		gridBagLayout.setConstraints(this.majorLabel, g);
		this.add(this.majorLabel);
		g.gridx = 3;
		gridBagLayout.setConstraints(this.majorTimeJsp, g);
		this.add(this.majorTimeJsp);
		g.gridx = 5;
		g.insets = new Insets(5, 20, 5, 5);
		gridBagLayout.setConstraints(this.majorVoiceJcb, g);
		this.add(this.majorVoiceJcb);
		g.gridx = 7;
		gridBagLayout.setConstraints(this.majorSoundField, g);
		this.add(this.majorSoundField);
		g.gridx = 9;
		gridBagLayout.setConstraints(this.majorSoundBtn, g);
		this.add(this.majorSoundBtn);
		g.gridx = 11;
		g.fill = GridBagConstraints.NONE;
		gridBagLayout.setConstraints(this.majorColorBtn, g);
		this.add(this.majorColorBtn);
		// 第4行
		g.gridx = 1;
		g.gridy = 3;
		g.insets = new Insets(5, 5, 5, 5);
		g.fill = GridBagConstraints.HORIZONTAL;
		gridBagLayout.setConstraints(this.minorLabel, g);
		this.add(this.minorLabel);
		g.gridx = 3;
		gridBagLayout.setConstraints(this.minorTimeJsp, g);
		this.add(this.minorTimeJsp);
		g.gridx = 5;
		g.insets = new Insets(5, 20, 5, 5);
		gridBagLayout.setConstraints(this.minorVoiceJcb, g);
		this.add(this.minorVoiceJcb);
		g.gridx = 7;
		gridBagLayout.setConstraints(this.minorSoundField, g);
		this.add(this.minorSoundField);
		g.gridx = 9;
		g.fill = GridBagConstraints.NONE;
		gridBagLayout.setConstraints(this.minorSoundBtn, g);
		this.add(this.minorSoundBtn);
		g.gridx = 11;
		gridBagLayout.setConstraints(this.minorColorBtn, g);
		this.add(this.minorColorBtn);
		// 第5行
		g.gridx = 1;
		g.gridy = 4;
		g.insets = new Insets(5, 5, 5, 5);
		g.fill = GridBagConstraints.HORIZONTAL;
		gridBagLayout.setConstraints(this.promptLabel, g);
		this.add(this.promptLabel);
		g.gridx = 3;
		gridBagLayout.setConstraints(this.promptTimeJsp, g);
		this.add(this.promptTimeJsp);
		g.gridx = 5;
		g.insets = new Insets(5, 20, 5, 5);
		gridBagLayout.setConstraints(this.promptVoiceJcb, g);
		this.add(this.promptVoiceJcb);
		g.gridx = 7;
		gridBagLayout.setConstraints(this.promptSoundField, g);
		this.add(this.promptSoundField);
		g.gridx = 9;
		gridBagLayout.setConstraints(this.promptSoundBtn, g);
		this.add(this.promptSoundBtn);
		g.gridx = 11;
		g.fill = GridBagConstraints.NONE;
		gridBagLayout.setConstraints(this.promptColorBtn, g);
		this.add(this.promptColorBtn);
		// 第6行
		g.gridx = 1;
		g.gridy = 6;
		g.gridwidth = 12;
		g.fill = GridBagConstraints.HORIZONTAL;
		gridBagLayout.setConstraints(this.panelBtn, g);
		this.add(this.panelBtn);
		
	}

	/**
	 * 设置按钮布局
	 */
	private void setBtnLayOut() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 430, 20, 20 };
		componentLayout.rowHeights = new int[] { 60 };
		this.panelBtn.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.saveBtn, c);
		this.panelBtn.add(this.saveBtn);

		c.gridx = 1;
		c.anchor = GridBagConstraints.CENTER;
		componentLayout.setConstraints(this.resetBtn, c);
		this.panelBtn.add(this.resetBtn);
		
		c.gridx = 2;
		c.anchor = GridBagConstraints.CENTER;
		componentLayout.setConstraints(this.cancelBtn, c);
		this.panelBtn.add(this.cancelBtn);
		
	}

	/**
	 * 初始化数据
	 * @throws Exception 
	 */
	private void initData() throws Exception {
		setValueToUI(voiceList);
	}
	
	/**
	 * 监听事件
	 */
	private void addActionListener() {
		/**
		 * 保存按钮监听事件
		 * 收集界面数据
		 */
		this.saveBtn.addActionListener(new MyActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					confirmData();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}

			@Override
			public boolean checking() {
				
				return true;
			}
		});
		
		/**
		 * 重置按钮监听事件
		 * 恢复原始设置
		 */
		this.resetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					resetData();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		});

		this.cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		
		//告警声音选择
		this.urgencySoundBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				alarmSoundChange(1);
			}
		});
		
		this.majorSoundBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				alarmSoundChange(2);
			}
		});

		this.minorSoundBtn.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent arg0) {
				alarmSoundChange(3);
			}
		});

		this.promptSoundBtn.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent arg0) {
				alarmSoundChange(4);
			}
		});
		
		//告警颜色选择
		this.urgencyColorBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				colorChange(1, urgencyColorBtn);
			}
		});
		
		this.majorColorBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				colorChange(2, majorColorBtn);
			}
		});
		
		this.minorColorBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				colorChange(3, minorColorBtn);
			}
		});
		
		this.promptColorBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				colorChange(4, promptColorBtn);
			}
		});
	}	
	
	/**
	 * 告警颜色选择
	 * @param colorBtn 
	 */
	private void colorChange(int level, JButton colorBtn) {
		AlarmColorChooseDialog colorDialog = new AlarmColorChooseDialog();
		Color color = colorDialog.getColor();
		if(color != null){
			colorBtn.setBackground(color);
		}
	}

	/**
	 * 告警声音选择
	 */
	private void alarmSoundChange(int level) {
		int result = this.fileChooser.showOpenDialog(this);
		String filePath = null;
		if(result == JFileChooser.APPROVE_OPTION){
			filePath = this.fileChooser.getSelectedFile().getAbsolutePath();
		}
		if(filePath != null){
			if(level == 1){
				//紧急告警
				this.urgencySoundField.setText(filePath);
			}else if(level == 2){
				//重要告警
				this.majorSoundField.setText(filePath);
			}else if(level == 3){
				//次要告警
				this.minorSoundField.setText(filePath);
			}else if(level == 4){
				//提示告警
				this.promptSoundField.setText(filePath);
			}
		}
	}
	
	/**
	 * 收集界面数据，入库并且设置声音的状态
	 * @throws Exception
	 */
	private void confirmData() throws Exception{
		//收集数据
		for (AlarmVoiceInfo voice : voiceList) {
			
			int type = voice.getAlarmType();
			if(type == 1){
				getValueFromUI(voice,urgencyTimeJsp,urgencyVoiceJcb,urgencySoundField,urgencyColorBtn);
			}
			else if(type == 2){
				getValueFromUI(voice,majorTimeJsp,majorVoiceJcb,majorSoundField,majorColorBtn);
			}
			else if(type == 3){
				getValueFromUI(voice,minorTimeJsp,minorVoiceJcb,minorSoundField,minorColorBtn);
			}
			else if(type == 4){
				getValueFromUI(voice,promptTimeJsp,promptVoiceJcb,promptSoundField,promptColorBtn);
			}
			
			//判断声音文件的类型是否正确
			String filename = voice.getAlarmSoundPath();
			String fileType = filename.substring(filename.lastIndexOf(".")+1);
			if(!fileType.equalsIgnoreCase("wav"))
			{
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NOVIDEO));
				return;
			}
		}
		
		//入库
		String status = this.insertData();
		//添加日志记录
		this.saveBtn.setOperateKey(EOperationLogType.ALARMDESCUPDATE.getValue());
		int operationResult=0;
		if(ResourceUtil.srcStr(StringKeysTip.TIP_SAVE_SUCCEED).equals(status)){
			operationResult=1;
		}else{
			operationResult=2;
		}
		saveBtn.setResult(operationResult);
		DialogBoxUtil.succeedDialog(this, status);
		this.dispose();
	}
	
	/**
	 * 将界面收集的数据入库
	 */
	private String insertData() {
		int result = 0;
		int id = 0;
		String status = null;
		AlarmVoiceService_MB service = null;
		try {
			service = (AlarmVoiceService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ALARMVOICE);
			for (AlarmVoiceInfo voice : voiceList) {
				id = voice.getId();
				if(id == 0){
					result += service.insertAllVoice(voice);	
				}else{
					result += service.updateAllVoice(voice);
				}
			}
			if(result == 4){
				status = ResourceUtil.srcStr(StringKeysTip.TIP_SAVE_SUCCEED);
				//如果保存成功,修改相应的告警颜色
				this.refreshTopoAlarm();
			}else{
				status = ResourceUtil.srcStr(StringKeysTip.TIP_SAVE_FAIL);
				this.resetData();
				this.insertData();
			}
		}catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally {
			UiUtil.closeService_MB(service);
		}
		return status;
	}

	/**
	 * 修改ConstantUtil里告警颜色的常量,并刷新拓扑告警颜色
	 */
	private void refreshTopoAlarm() {
		//修改告警颜色数值常量
		for (AlarmVoiceInfo alarmVoice : this.voiceList) {
			int type = alarmVoice.getAlarmType();
			int color = alarmVoice.getAlarmColorRGB();
			int time = alarmVoice.getAlarmContinueTime();
			String path = alarmVoice.getAlarmSoundPath();
			if(type == 1){
				ConstantUtil.URGENCYCOLOR = color;
				ConstantUtil.URGENCY_TIME = time;
				ConstantUtil.URGENCYSOUNDPATH = path;
			}else if(type == 2){
				ConstantUtil.MAJORCOLOR = color;
				ConstantUtil.MAJOR_TIME = time;
				ConstantUtil.MAJORSOUNDPATH = path;
			}else if(type == 3){
				ConstantUtil.MINORCOLOR = color;
				ConstantUtil.MINOR_TIME = time;
				ConstantUtil.MINORSOUNDPATH = path;
			}else if(type == 4){
				ConstantUtil.PROMPTCOLOR = color;
				ConstantUtil.WARNING_TIME = time;
				ConstantUtil.WARNINGSOUNDPATH = path;
			}
		}
		//刷新告警灯
		CurAlarmService_MB curAlarmService = null;
		try {
			curAlarmService = (CurAlarmService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CurrentAlarm);
			Ptnf.getPtnf().refreshAlarmNum(curAlarmService);
			//刷新当前页面告警
			JTabbedPane tabPanel = Ptnf.getPtnf().getjTabbedPane1();
			Component comp = tabPanel.getSelectedComponent();
			if(comp instanceof NetworkElementPanel){
				//主拓扑
				try {
					new TopologyUtil().updateSiteInstAlarm(curAlarmService);
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}else if(comp instanceof NetWorkInfoPanel){
				//在线网元/网元配置
				JPanel treeNodePanel = ((NetWorkInfoPanel)comp).getjPanel1();
				if(treeNodePanel instanceof ShelfTopology){
					//网元机架
					try {
						((ShelfTopology) treeNodePanel).refreshAlarm();
					} catch (Exception e) {
						ExceptionManage.dispose(e,this.getClass());
					}
				}else if(treeNodePanel instanceof SiteCurrentAlarmPanel){
					//当前告警
					((SiteCurrentAlarmPanel)treeNodePanel).getController().refresh();
				}else if(treeNodePanel instanceof SiteHisAlarmPanel){
					//历史告警
					((SiteHisAlarmPanel)treeNodePanel).getController().refresh();
				}
			}else if(comp instanceof CurrentAlarmPanel){
				//当前告警
				((CurrentAlarmPanel)comp).getController().refresh();
			}else if(comp instanceof HisAlarmPanel){
				//历史告警
				((HisAlarmPanel)comp).getController().refresh();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally
		{
			UiUtil.closeService_MB(curAlarmService);
		}
	}

	/**
	 * 收集界面数据给voice赋值
	 * @param voice
	 * @param jsp
	 * @param jcb
	 * @param soundField 
	 * @param promptColorBtn2 
	 * @throws NumberFormatException
	 * @throws Exception
	 */

	private void getValueFromUI(AlarmVoiceInfo voice,PtnSpinner jsp ,JCheckBox jcb,JTextField soundField, JButton colorBtn) 
	   throws NumberFormatException, Exception {
		voice.setAlarmContinueTime(Integer.parseInt(jsp.getTxtData()));
		voice.setAlarmSwitch(jcb.isSelected() == false ? 0 : 1);
		voice.setAlarmSoundPath(soundField.getText());
		voice.setAlarmColorRGB(colorBtn.getBackground().getRGB());
	}

	/**
	 * 恢复初始化数据
	 * 一键还原
	 */
	private void resetData() throws Exception{
		for (int i = 0; i < voiceList.size(); i++) {
			AlarmVoiceInfo info = voiceList.get(i);
			info.setAlarmContinueTime(60);
			info.setAlarmSwitch(1);
			info.setAlarmSoundPath(soundPathArr[i]);
			info.setAlarmColorRGB(colorRGBArr[i]);
		}
		setValueToUI(voiceList);
	}

	/**
	 * 界面赋值
	 * @param voiceList
	 * @throws Exception 
	 */
	private void setValueToUI(List<AlarmVoiceInfo> voiceList) throws Exception {
		for (AlarmVoiceInfo voice : voiceList) {
			int type = voice.getAlarmType();
			int time = voice.getAlarmContinueTime();
			String soundPath = voice.getAlarmSoundPath();
			int colorRGB = voice.getAlarmColorRGB();
			if(type == 1){
				setValue(urgencyTimeJsp,time,urgencySoundField,soundPath, urgencyColorBtn, colorRGB);
			}
			else if(type == 2){
				setValue(majorTimeJsp,time,majorSoundField,soundPath, majorColorBtn, colorRGB);
			}
			else if(type == 3){
				setValue(minorTimeJsp,time,minorSoundField,soundPath, minorColorBtn, colorRGB);
			}
			else if(type == 4){
				setValue(promptTimeJsp,time,promptSoundField,soundPath, promptColorBtn, colorRGB);
			}
		}
	}

	/**
	 * 给界面赋值
	 * @param jsp
	 * @param time
	 * @param soundPath 
	 * @param field 
	 * @param colorBtn 
	 * @param colorRGB 
	 * @param voiceSwitch
	 */
	private void setValue(PtnSpinner jsp , int time, JTextField field, String soundPath, JButton colorBtn, int colorRGB) {
		try {
			jsp.setTxtData(time+"");
			field.setText(soundPath);
			colorBtn.setBackground(new Color(colorRGB));
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private JLabel alarmLevelLabel;//告警级别
	private JLabel continueTimeLabel;//持续时间(s)
	private JLabel voiceSwitchLabel;//声音开关
	private JLabel urgencyLabel;//紧急告警
	private JLabel majorLabel;//重要告警
	private JLabel minorLabel;//次要告警
	private JLabel promptLabel;//提示告警
	private PtnSpinner urgencyTimeJsp;//紧急告警持续时间
	private PtnSpinner majorTimeJsp;//重要告警持续时间
	private PtnSpinner minorTimeJsp;//次要告警持续时间
	private PtnSpinner promptTimeJsp;//提示告警持续时间
	private JCheckBox urgencyVoiceJcb;//紧急告警声音开关
	private JCheckBox majorVoiceJcb;//重要告警声音开关
	private JCheckBox minorVoiceJcb;//次要告警声音开关
	private JCheckBox promptVoiceJcb;//提示告警声音开关
	private JLabel alarmSoundLabel;//告警声音选择
	private JTextField urgencySoundField;//紧急告警声音选择
	private JTextField majorSoundField;//重要告警声音选择
	private JTextField minorSoundField;//次要告警声音选择
	private JTextField promptSoundField;//提示告警声音选择
	private JButton urgencySoundBtn;
	private JButton majorSoundBtn;
	private JButton minorSoundBtn;
	private JButton promptSoundBtn;
	private JLabel colorLabel;//告警颜色选择
	private JButton urgencyColorBtn;//紧急告警颜色
	private JButton majorColorBtn;//重要告警颜色
	private JButton minorColorBtn;//次要告警颜色
	private JButton promptColorBtn;//提示告警颜色
	private PtnButton saveBtn;//保存
	private JButton resetBtn;//重置
	private JButton cancelBtn;//取消
	private JPanel panelBtn;//按钮面板
	private JFileChooser fileChooser;//文件选择对话框
}
