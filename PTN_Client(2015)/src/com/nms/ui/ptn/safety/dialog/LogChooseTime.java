package com.nms.ui.ptn.safety.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.nms.model.system.OperationLogService_MB;
import com.nms.model.system.loginlog.LoginLogServiece_Mb;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnCalendarField;
import com.nms.ui.manager.control.PtnDateDocument;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

/**
 * 时间选择    对话框
 * @author sy
 *
 */
public class LogChooseTime extends PtnDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PtnButton confirm=null;
	private JButton cancel=null;
	//时间选择器
	private PtnCalendarField chooseTime;
	private JLabel lblChooseTime;
	private JPanel buttonPanel;
	/**
	 * 区别 是 登陆 日志还是 操作日志
	 * 		1为 登陆日志
	 * 			2  为 操作日志
	 */
	private  int chooseLogInface;
	
	public LogChooseTime(int chooseLogInface){
		this.setModal(true);
		this.chooseLogInface=chooseLogInface;
		init();
		
	}
	public void init(){
		this.setTitle(ResourceUtil.srcStr(StringKeysBtn.BTN_REMOVE_DATA));
		initComponents();
		setLayout();
		addListener();
		showWindow();
	}
	private void showWindow() {
		UiUtil.showWindow(this, 300, 160);
	}
	private void addListener() {
		
		//取消按钮
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				LogChooseTime.this.dispose();
			}
		});
		confirm.addActionListener(new MyActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				confimAction();
			}

			@Override
			public boolean checking() {
				
				return true;
			}
		});
		
	}
	/**
	 * 确认事件
	 */
	public void confimAction(){
		LoginLogServiece_Mb loginLogService=null;
		OperationLogService_MB operationLogService=null;
		String resultKey=null;
		try {
			
			String conFirm= ResourceUtil.srcStr(StringKeysTip.TIP_REMOVER_DATALOG)+" "+chooseTime.getText()+" "+ResourceUtil.srcStr(StringKeysTip.TIP_REMOVER_DATALOGLAST);
			int resultSet = DialogBoxUtil.confirmDialog(this,conFirm);
			if(resultSet==0){
				if(chooseLogInface==1){
					loginLogService=(LoginLogServiece_Mb) ConstantUtil.serviceFactory.newService_MB(Services.LOGINLOGSERVIECE);
					loginLogService.delete(chooseTime.getText());					
//					AddOperateLog.insertOperLog(confirm, EOperationLogType.REMOVELOGINLOG.getValue(), resultKey);
				}
				else if(chooseLogInface==2){
					operationLogService=(OperationLogService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OPERATIONLOGSERVIECE);
					operationLogService.delete(chooseTime.getText());				
				}
			}
		
		} catch (Exception e) {
			resultKey="fail";//操作异常  --即失败
			ExceptionManage.dispose(e, LogChooseTime.class);
			
		}finally{
			//添加日志记录
//	    	AddOperateLog.insertOperLog(confirm, EOperationLogType.REMOVEOPERATION.getValue(), resultKey);
			UiUtil.closeService_MB(loginLogService);
			UiUtil.closeService_MB(operationLogService);
		}
		LogChooseTime.this.dispose();
	}
	public void initComponents(){
		confirm=new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),false,RootFactory.SATY_MANAGE);
		cancel=new javax.swing.JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		lblChooseTime=new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysBtn.BTN_CHOOSE_TIME));
		chooseTime=new PtnCalendarField(new PtnDateDocument());
		buttonPanel=new javax.swing.JPanel();
	}
	//页面布局
	public void setButtonLayout(){
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] {  15,15  };
		layout.columnWeights = new double[] { 0,0 };
		layout.rowHeights = new int[] { 20};
		layout.rowWeights = new double[] {0};
		this.setLayout(layout);
		
		GridBagConstraints c = new GridBagConstraints();
		//第一行
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		c.gridx=0;
		c.gridy=0;
		layout.addLayoutComponent(confirm, c);
		buttonPanel.add(confirm);
		
		c.gridx=1;
		layout.addLayoutComponent(cancel, c);
		buttonPanel.add(cancel);
	}
	private void setLayout() {
		setButtonLayout();
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] {  30, 30,   };
		layout.columnWeights = new double[] { 0, 0};
		layout.rowHeights = new int[] { 20, 20};
		layout.rowWeights = new double[] {0, 0};
		this.setLayout(layout);
		//this.pack();
		GridBagConstraints c = new GridBagConstraints();
		//第一行
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(lblChooseTime, c);
		this.add(lblChooseTime);
		
		c.gridx =1;
		layout.addLayoutComponent(chooseTime, c);
		this.add(chooseTime);
		
		c.gridx=1;
		c.gridy=1;
		layout.addLayoutComponent(buttonPanel, c);
		this.add(buttonPanel);
	
	}
}
