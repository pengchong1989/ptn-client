package com.nms.ui.ptn.ne.smartFan.view.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.nms.db.bean.ptn.SmartFan;
import com.nms.db.enums.EOperationLogType;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
/**
 * @author guoqc
 */
public class SmartFanConfigDialog extends PtnDialog {
	private static final long serialVersionUID = -4147934782855305867L;
	private SmartFan fan;
	private SmartFan fanBefore;//修改前的数据，log日志用到
	
	public SmartFanConfigDialog(SmartFan smartFan) {
		this.setTitle(ResourceUtil.srcStr(StringKeysTab.TAB_SMARTFAN));
		this.fan = smartFan;
		this.fanBefore = new SmartFan();
		this.fanBefore.setWorkType(smartFan.getWorkType());
		this.fanBefore.setGearLevel(smartFan.getGearLevel());
		this.initComponent();
		this.setLayout();
		this.addListener();
		this.initData();
		UiUtil.showWindow(this, 350, 300);
	}

	private void initComponent() {
		this.workModelLbl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SMARTFANMODEL));//风扇模式:
		this.smartModel = new JRadioButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SMARTMODEL));//智能模式
		this.smartModel.setName("1");
		this.smartModel.setSelected(true);
		this.manualModel = new JRadioButton(ResourceUtil.srcStr(StringKeysBtn.BTN_MANUALMODEL));//自选模式
		this.manualModel.setName("0");
		this.modelBtnGroup = new ButtonGroup();
		this.modelBtnGroup.add(this.smartModel);
		this.modelBtnGroup.add(this.manualModel);
		this.fanGearLbl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SMARTFANGEAR));//风扇档位:
		this.smartSelect = new JRadioButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SMART));//智能
		this.smartSelect.setName("0");
		this.gear1Select = new JRadioButton(ResourceUtil.srcStr(StringKeysBtn.BTN_GEAR1));//档位1
		this.gear1Select.setName("1");
		this.gear2Select = new JRadioButton(ResourceUtil.srcStr(StringKeysBtn.BTN_GEAR2));//档位2
		this.gear2Select.setName("2");
		this.gear3Select = new JRadioButton(ResourceUtil.srcStr(StringKeysBtn.BTN_GEAR3));//档位3
		this.gear3Select.setName("3");
		this.gearBtnGroup = new ButtonGroup();
		this.gearBtnGroup.add(this.smartSelect);
		this.gearBtnGroup.add(this.gear1Select);
		this.gearBtnGroup.add(this.gear2Select);
		this.gearBtnGroup.add(this.gear3Select);
		this.saveBtn = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),false,RootFactory.CORE_MANAGE);
		this.cancelBtn = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		this.buttonPanel = new JPanel();
	}
	
	/**
	 *  主界面布局
	 */
	private void setLayout(){
		this.setButtonLayout();
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 40, 40, 100, 130, 40};
		componentLayout.columnWeights = new double[] { 0, 0, 0 };
		componentLayout.rowHeights = new int[] {30, 30, 30, 30, 30, 30};
		componentLayout.rowWeights = new double[] { 0.1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		this.setLayout(componentLayout);
	
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		//第一行
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.insets = new Insets(30, 10, 10, 10);
		componentLayout.setConstraints(this.workModelLbl, c);
		this.add(this.workModelLbl);
		c.gridx = 2;
		c.gridy = 0;
		componentLayout.setConstraints(this.smartModel, c);
		this.add(this.smartModel);
		c.gridx = 3;
		c.gridy = 0;
		componentLayout.setConstraints(this.manualModel, c);
		this.add(this.manualModel);
		// 第二行
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(10, 10, 15, 10);
		componentLayout.setConstraints(this.fanGearLbl, c);
		this.add(this.fanGearLbl);
		c.gridx = 2;
		c.gridy = 1;
		c.insets = new Insets(10, 10, 15, 10);
		componentLayout.setConstraints(this.smartSelect, c);
		this.add(this.smartSelect);
		c.gridx = 3;
		c.gridy = 1;
		componentLayout.setConstraints(this.gear1Select, c);
		this.add(this.gear1Select);
		//第三行
		c.gridx = 2;
		c.gridy = 2;
		componentLayout.setConstraints(this.gear2Select, c);
		this.add(this.gear2Select);
		c.gridx = 3;
		c.gridy = 2;
		componentLayout.setConstraints(this.gear3Select, c);
		this.add(this.gear3Select);
		//第五行 
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 3;
		c.gridy = 4;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.buttonPanel, c);
		this.add(this.buttonPanel);
	}
	
	private void setButtonLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] {250, 50, 50};
		componentLayout.columnWeights = new double[] {0.1, 0, 0};
		componentLayout.rowHeights = new int[] {30};
		componentLayout.rowWeights = new double[] {0};
		this.setLayout(componentLayout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.saveBtn, c);
		buttonPanel.add(this.saveBtn);
		c.gridx=2;
		componentLayout.setConstraints(this.cancelBtn, c);
		buttonPanel.add(this.cancelBtn);
		
	}
	
	/**
	 * 添加监听事件
	 */
	private void addListener(){
		this.saveBtn.addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAction();
				dispose();			
			}

			@Override
			public boolean checking() {
				return false;
			}
			
		});			
	
		this.cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		this.smartModel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				lockSelectedItems(1);
			}
		});
		
		this.manualModel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				lockSelectedItems(0);
			}
		});
	}

	/**
	 * 选中智能模式,档位1/2/3 不可选
	 * 选中自选模式,智能选择不可选
	 */
	private void lockSelectedItems(int i) {
		//1代表选中智能模式
		if(i== 1){
			this.gear1Select.setEnabled(false);
			this.gear2Select.setEnabled(false);
			this.gear3Select.setEnabled(false);
			this.smartSelect.setEnabled(true);
			this.smartSelect.setSelected(true);
			
		}else{
		//0代表选中自选模式
			this.gear1Select.setSelected(true);
			this.gear1Select.setEnabled(true);
			this.gear2Select.setEnabled(true);
			this.gear3Select.setEnabled(true);
			this.smartSelect.setEnabled(false);
		}
	}

	/**
	 * 保存按钮事件
	 */
	private void saveAction(){
		try {
			this.getSmartFan();
			DispatchUtil dispatch = new DispatchUtil(RmiKeys.RMI_SMARTFAN);
			String result = dispatch.excuteUpdate(fan);
			this.fanBefore.setWorkTypeLog(this.fanBefore.getWorkType()==1 ? 
					ResourceUtil.srcStr(StringKeysBtn.BTN_SMARTMODEL):ResourceUtil.srcStr(StringKeysBtn.BTN_MANUALMODEL));
			AddOperateLog.insertOperLog(this.saveBtn, EOperationLogType.SMARTFANCONFIGUPDATE.getValue(), result,
					this.fanBefore, this.fan, ConstantUtil.siteId, "fan"+this.getName(this.fan.getId()), "smartFan");
			DialogBoxUtil.succeedDialog(this, result);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	private int getName(int id){
		if(id%3 == 0){
			return 1;
		}else if(id%3 == 1){
			return 3;
		}else{
			return 2;
		}
	}
	
	/**
	 * 获取对象
	 */
	private void getSmartFan() {
		JRadioButton radioButton = null;
		int workType = 1;
		int fanLevel = 0;
		try {
			//遍历所有radiobutton 获取选中的button
			Enumeration<AbstractButton> elements = this.modelBtnGroup.getElements();
			while (elements.hasMoreElements()) {
				radioButton = (JRadioButton) elements.nextElement();
				if (radioButton.isSelected()) {
					workType = Integer.parseInt(radioButton.getName());
					this.fan.setWorkTypeLog(radioButton.getText());
					break;
				}
			}
			Enumeration<AbstractButton> elements1 = this.gearBtnGroup.getElements();
			while (elements1.hasMoreElements()) {
				radioButton = (JRadioButton) elements1.nextElement();
				if (radioButton.isSelected()) {
					fanLevel = Integer.parseInt(radioButton.getName());
					this.fan.setGearLevelLog(radioButton.getText());
					break;
				}
			}
			fan.setWorkType(workType);
			fan.setGearLevel(fanLevel);
		}catch(Exception e){
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	/**
	 * 初始化数据
	 */
	private void initData() {
		if(this.fan.getWorkType() == 1){
			this.smartModel.setSelected(true);
			this.smartSelect.setSelected(true);
			this.gear1Select.setEnabled(false);
			this.gear2Select.setEnabled(false);
			this.gear3Select.setEnabled(false);
			this.fanBefore.setGearLevelLog(this.smartSelect.getText());
		}else{
			this.manualModel.setSelected(true);
			this.smartSelect.setEnabled(false);
			if(this.fan.getGearLevel() == 1){
				this.gear1Select.setSelected(true);
				this.fanBefore.setGearLevelLog(this.gear1Select.getText());
			}else if(this.fan.getGearLevel() == 2){
				this.gear2Select.setSelected(true);
				this.fanBefore.setGearLevelLog(this.gear2Select.getText());
			}else if(this.fan.getGearLevel() == 3){
				this.gear3Select.setSelected(true);
				this.fanBefore.setGearLevelLog(this.gear3Select.getText());
			}
		}
	}

	private JLabel workModelLbl;//风扇模式:
	private JRadioButton smartModel;//智能模式
	private JRadioButton manualModel;//自选模式
	private ButtonGroup modelBtnGroup;
	private JLabel fanGearLbl;//风扇档位:
	private JRadioButton smartSelect;//智能
	private JRadioButton gear1Select;//档位1
	private JRadioButton gear2Select;//档位2
	private JRadioButton gear3Select;//档位3
	private ButtonGroup gearBtnGroup;
	private PtnButton saveBtn;//确认
	private JButton cancelBtn;//取消
	private JPanel buttonPanel;
}