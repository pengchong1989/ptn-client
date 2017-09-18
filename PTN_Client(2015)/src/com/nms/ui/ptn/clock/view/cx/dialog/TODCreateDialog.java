package com.nms.ui.ptn.clock.view.cx.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.nms.db.bean.ptn.clock.TodConfigInfo;
import com.nms.model.ptn.clock.TodDispositionInfoService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.ptn.clock.view.cx.time.TabPanelTwoBottomTCX;
import com.nms.ui.ptn.clock.view.cx.time.TextFiledKeyListener;
import com.nms.ui.ptn.clock.view.cx.time.TextfieldFocusListener;
/**
 * 
 * @author zhangkun
 *
 */
public class TODCreateDialog extends PtnDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8993189519556331871L;
	
	private JLabel port=null;//端口
	private JTextField port_=null;
	private JLabel interfaceType=null;//接口类型
	private JComboBox interfaceType_=null;
	private JLabel priority1=null;//优先级1
	private JTextField priority1_=null;
	private JLabel clockType=null;//时钟类型
	private JTextField clockType_=null;
	private JLabel clockAccuracy=null;//时钟精度
	private JTextField clockAccuracy_=null;
	private JLabel clockVariance=null;//时钟方差
	private JTextField clockVariance_=null;
	private JLabel priority2=null;//优先级2
	private JTextField priority2_=null;
	private JButton confirm=null;
	private JButton cancel=null;
	private JPanel buttonPanel=null;
	private GridBagLayout gridBagLayout=null;
	private TabPanelTwoBottomTCX tabPanelTwoBottomTCX=null;
	private JDialog jDialog=null;
	private TodConfigInfo todConfigInfo=null;
	
	
	public TODCreateDialog(TabPanelTwoBottomTCX tabPanelTwoBottomTCX,TodConfigInfo todConfigInfo) throws Exception{
		try {
			this.todConfigInfo=todConfigInfo;
			this.tabPanelTwoBottomTCX=tabPanelTwoBottomTCX;
			init();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	/**
	 *初始化界面 
	 * @throws Exception
	 */
	private void init() throws Exception{
		try {
			gridBagLayout = new GridBagLayout();
			port=new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT));
			port_ = new JTextField();
			port_.setEditable(false);
			interfaceType = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_INTERFACE_TYPE));
			interfaceType_ = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(this.interfaceType_, "intefaceModel");
			interfaceType_.setEnabled(false);
			
			priority1=new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PRIORITY_1));
			priority1_=new JTextField();
			clockType=new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CLOCK_TYPE));
			clockType_=new JTextField();
			clockAccuracy=new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CLOCK_ACCURACY));
			clockAccuracy_=new JTextField();
			clockVariance=new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CLOCK_VARIANCE));
			clockVariance_=new JTextField();
			priority2=new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PRIORITY_2));
			priority2_=new JTextField();
			confirm = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM));
			cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL)); 
			buttonPanel = new JPanel();
			setButtonLayout();/* 按钮所在panel布局 */
			buttonPanel.add(confirm);
			buttonPanel.add(cancel);
			
			setGridBagLayout();/* 主窗口布局 */
			this.setLayout(gridBagLayout);
			this.add(port);
			this.add(port_);
			this.add(interfaceType);
			this.add(interfaceType_);
			this.add(priority1);
			this.add(priority1_);
			this.add(clockType);
			this.add(clockType_);
			this.add(clockAccuracy);
			this.add(clockAccuracy_);
			this.add(clockVariance);
			this.add(clockVariance_);
			this.add(priority2);
			this.add(priority2_);
			this.add(buttonPanel);
			this.setTitle(ResourceUtil.srcStr(StringKeysLbl.LBL_UPDATETOD));
			initData();
			addButtonListener();
			addFocusListenerForTextfield();/*textfield聚焦事件监听，当离开此textfield判断值是否在指定范围内*/
		    addKeyListenerForTextfield();/*textfield添加键盘事件监听，只允许数字输入*/
			UiUtil.showWindow(this, 400, 400);
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 给各个组件赋初始值
	 */
	private void initData() {
		try {
			this.port_.setText(todConfigInfo.getPort());
			super.getComboBoxDataUtil().comboBoxSelect(this.interfaceType_,String.valueOf(todConfigInfo.getInterfaceType()));
			this.priority1_.setText(todConfigInfo.getPriority1());
			this.clockType_.setText(todConfigInfo.getClockType());
			this.clockAccuracy_.setText(todConfigInfo.getClockAccuracy());
			this.clockVariance_.setText(todConfigInfo.getClockVariance());
			this.priority2_.setText(todConfigInfo.getPriority2());
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		
	}
	/**
	 * <p>
	 * 主窗口布局
	 * </p>
	 * 
	 * @throws Exception
	 */
	private void setGridBagLayout() throws Exception{
		GridBagConstraints gridBagConstraints = null;
		try {
			gridBagConstraints=new GridBagConstraints();
			gridBagLayout.columnWidths = new int[] {30,200};
			gridBagLayout.columnWeights = new double[] {0,0,0};
			gridBagLayout.rowHeights = new int[] { 35, 35, 35, 35, 35, 35, 35, 35};
			gridBagLayout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0};
			gridBagConstraints.insets = new Insets(5, 10, 0, 0); 
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(port, gridBagConstraints);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(interfaceType, gridBagConstraints);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(priority1, gridBagConstraints);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(clockType, gridBagConstraints);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(clockAccuracy, gridBagConstraints);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(clockVariance, gridBagConstraints);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 6;
			gridBagLayout.setConstraints(priority2, gridBagConstraints);
			/***************************************************/
			gridBagConstraints.insets = new Insets(5, 30, 0, 0);
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(port_, gridBagConstraints);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(interfaceType_, gridBagConstraints);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(priority1_, gridBagConstraints);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(clockType_, gridBagConstraints);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(clockAccuracy_, gridBagConstraints);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(clockVariance_, gridBagConstraints);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 6;
			gridBagLayout.setConstraints(priority2_, gridBagConstraints);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 7;
			gridBagLayout.setConstraints(buttonPanel, gridBagConstraints);
		} catch (Exception e) {
			throw e; 
		}
		
	}
	/**
	 *  按钮所在panel布局
	 */
	private void setButtonLayout() throws Exception {
		GridBagConstraints gridBagConstraints=null;
		GridBagLayout gridBagLayout = null;
		try {
			gridBagLayout = new GridBagLayout();
			gridBagConstraints = new GridBagConstraints();
			gridBagLayout.columnWidths=new int[]{20,20};
			gridBagLayout.columnWeights=new double[]{0,0};
			gridBagLayout.rowHeights=new int[]{21};
			gridBagLayout.rowWeights=new double[]{0};
			
			gridBagConstraints.insets=new Insets(5,10,5,0);
			gridBagConstraints= new GridBagConstraints();
			gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(confirm, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 25, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(cancel, gridBagConstraints);
			
			buttonPanel.setLayout(gridBagLayout);
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * <p>
	 * 按钮添加监听
	 * </p>
	 * 
	 * @throws Exception
	 */
	private void addButtonListener() throws Exception {
		jDialog=this;
		try {
			this.confirm.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					btnSaveListener();
				}
			});
			
			this.cancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					jDialog.dispose();
				}
			});
		} catch (Exception e) {

			throw e;
		}
	}
	
/**
 * 保存事件
 */
	private void btnSaveListener() {
		TodDispositionInfoService_MB todDispositionInfoService=null;
		try {
			todDispositionInfoService=(TodDispositionInfoService_MB)ConstantUtil.serviceFactory.newService_MB(Services.TodDispositionInfoService);
			todConfigInfo.setPort(this.port_.getText());
			 //管理接口类型
		    ControlKeyValue interfaceTypekey_broad = (ControlKeyValue) this.interfaceType_.getSelectedItem();
		    todConfigInfo.setInterfaceType(Integer.parseInt(interfaceTypekey_broad.getId()));
		    todConfigInfo.setPriority1(this.priority1_.getText());
		    todConfigInfo.setClockType(this.clockType_.getText());
		    todConfigInfo.setClockAccuracy(this.clockAccuracy_.getText());
		    todConfigInfo.setClockVariance(this.clockVariance_.getText());
		    todConfigInfo.setPriority2(this.priority2_.getText());
		    //操作数据库/刷新组界面/影藏界面
		    todDispositionInfoService.update(todConfigInfo);
		    this.tabPanelTwoBottomTCX.controller.refresh();
		    jDialog.dispose();
		    
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(todDispositionInfoService);
		}
	}
	/**
	 * <p>
	 * textfield添加监听，只允许输入数字
	 * </p>
	 * @throws Exception
	 */
	private void addKeyListenerForTextfield()throws Exception{
		
		TextFiledKeyListener textFIledKeyListener=null;
		try{
			/* 为1：只接受数字 **/
			textFIledKeyListener = new TextFiledKeyListener(1);
			priority1_.addKeyListener(textFIledKeyListener);
			clockType_.addKeyListener(textFIledKeyListener);
			clockAccuracy_.addKeyListener(textFIledKeyListener);
			clockVariance_.addKeyListener(textFIledKeyListener);
			priority2_.addKeyListener(textFIledKeyListener);
		}catch(Exception e){
			
			throw e;
		}
	}
	
	/**
	 * <p>
	 * 判断输入数值是否在指定区间内
	 * </p>
	 * @throws Exception
	 */
	private void addFocusListenerForTextfield()throws Exception{
		
		TextfieldFocusListener textfieldFocusListener=null;
		String whichTextTield=null;
		try{
			
			whichTextTield=ResourceUtil.srcStr(StringKeysLbl.LBL_PRIORITY_1);
			textfieldFocusListener = new TextfieldFocusListener(whichTextTield,1,priority1_);
			priority1_.addFocusListener(textfieldFocusListener);
			
			whichTextTield=ResourceUtil.srcStr(StringKeysLbl.LBL_CLOCK_TYPE);
			textfieldFocusListener = new TextfieldFocusListener(whichTextTield,1,clockType_);
			clockType_.addFocusListener(textfieldFocusListener);
			
			whichTextTield=ResourceUtil.srcStr(StringKeysLbl.LBL_CLOCK_ACCURACY);
			textfieldFocusListener = new TextfieldFocusListener(whichTextTield,1,clockAccuracy_);
			clockAccuracy_.addFocusListener(textfieldFocusListener);
			
			whichTextTield=ResourceUtil.srcStr(StringKeysLbl.LBL_CLOCK_VARIANCE);
			textfieldFocusListener = new TextfieldFocusListener(whichTextTield,1,clockVariance_);
			clockVariance_.addFocusListener(textfieldFocusListener);
			
			whichTextTield=ResourceUtil.srcStr(StringKeysLbl.LBL_PRIORITY_2);
			textfieldFocusListener = new TextfieldFocusListener(whichTextTield,1,priority2_);
			priority2_.addFocusListener(textfieldFocusListener);
			
		}catch(Exception e){
			
			throw e;
		}
	}
}
