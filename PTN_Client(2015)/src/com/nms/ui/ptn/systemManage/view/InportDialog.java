package com.nms.ui.ptn.systemManage.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.nms.db.bean.system.UnLoadFactory;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.alarm.HisAlarmService_MB;
import com.nms.model.util.Services;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnFileChooser;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.ptn.systemManage.controller.UnLoadingController;


/**
 * 导入按钮 打开的界面
 * 選擇.sql文件  
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
public class InportDialog extends PtnDialog {
		private final String ALARM_TABLE="history_alarm";//历史告警  ： 表
		private final String PERFORMANCE_TABLE="history_performance";//历史性能  ： 表
		private final String OPERATIONLOG_TABLE="operation_log";//操作日志  ： 表
		private final String LOGIN_TABLE="login_log";//登录日志  ： 表
		private JComboBox typeBox;
		private PtnButton confirm;
		private JButton cancel;
		private JLabel fileType;
		private JTextField fileFiled;
		private JButton fileButton;	
		private JLabel cellType;//对象类型
		private UnLoadingController unloadController=null;
		
		public InportDialog(UnLoadingController unloadController) {
			 
			try {
				super.setTitle("导入SQL文件");
			    this.unloadController=unloadController;
				init();
			} catch (Exception e) {
				ExceptionManage.dispose(e,this.getClass());
			}
		}
		
		private void init() throws Exception {
			initComponents();
			setLayout();
			addListener();
		}
		private void addListener() {		
			
			//文件选择按钮
			fileButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					fileButtonActionListener();
					
				}});
			//开始，（显示导入信息）按钮
			confirm.addActionListener(new MyActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					confirmActionListener();
				}
				@Override
				public boolean checking() {
					return true;
				}});
			// 取消按钮
			cancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					InportDialog.this.dispose();
				}
			});	
		}	
		/*
		 * 初始化組件
		 */
		private void initComponents() throws Exception {
		
			typeBox =new javax.swing.JComboBox();
			typeBox.addItem(ResourceUtil.srcStr(StringKeysBtn.BTN_HISTORY_ALARM));
			typeBox.addItem(ResourceUtil.srcStr(StringKeysTab.TAB_HISPERFOR));
			//typeBox.addItem(ResourceUtil.srcStr(StringKeysBtn.BTN_HISTORY_DAY_PERFORMANCE));
			typeBox.addItem(ResourceUtil.srcStr(StringKeysBtn.BTN_LOGIN_LOG));
			typeBox.addItem(ResourceUtil.srcStr(StringKeysBtn.BTN_LOGIN_OPER_LOG));
			//typeBox.addItem("日志记录");
			fileType = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_FILE_ROUTE));
			cellType = new JLabel(ResourceUtil.srcStr(StringKeysBtn.BTN_INPORT_TYPE));//
			fileFiled=new javax.swing.JTextField();
			fileFiled.setEditable(false);//只读
			confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_INPORT_START),true);
			cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
			fileButton=new javax.swing.JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_SELECT_FILE));							
		}
		private void setLayout() {
			GridBagLayout layout = new GridBagLayout();
			layout.columnWidths = new int[] { 20,20,20};
			layout.columnWeights = new double[] { 0,0.2,0};
			layout.rowHeights = new int[] { 10,20,10,10};
			layout.rowWeights = new double[] { 0, 0, 0,0};
			this.setLayout(layout);
			GridBagConstraints c = new GridBagConstraints();
			
		/*	c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy = 0;
			c.gridheight = 2;
			c.gridwidth = 6;
			c.insets = new Insets(5, 5, 5, 0);
		//	loadPanel.setSize(5, 5);
			layout.addLayoutComponent(loadPanel, c);
			this.add(loadPanel);*/
			
		//第一行
			c.fill = GridBagConstraints.BOTH ;
			c.gridx = 0;
			c.gridy = 0;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.insets = new Insets(10, 10, 5, 5);//(1,2,3,4)
			layout.addLayoutComponent(this.cellType, c);
			this.add(cellType);
			
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy =0;
			c.gridheight = 1;
			c.gridwidth = 2;
			c.insets = new Insets(10, 10, 5, 5);//(1,2,3,4)
			layout.addLayoutComponent(this.typeBox, c);
			this.add(typeBox);
			
			//第2行

			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 1;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.insets = new Insets(10, 10, 5,5);
			layout.addLayoutComponent(this.fileType, c);
			this.add(fileType);
			
			c.fill = GridBagConstraints.BOTH;
			c.gridx =1;
			c.gridy = 1;
			c.gridheight = 1;
			c.gridwidth =1;
			c.insets = new Insets(10, 10, 5, 5);
			layout.addLayoutComponent(this.fileFiled, c);
			this.add(fileFiled);
			
		//	c.fill = GridBagConstraints.BOTH;
			c.gridx = 2;
			c.gridy = 1;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.insets = new Insets(10, 10, 5, 5);
			layout.addLayoutComponent(this.fileButton, c);
			this.add(fileButton);
			//第6行
			
			
		
			//第3行
			//c.fill = GridBagConstraints.WEST ;
			//c.fill = GridBagConstraints.CENTER;
			c.fill = GridBagConstraints.EAST;
			c.anchor = GridBagConstraints.EAST;
			c.gridx =1;
			c.gridy = 3;
			c.gridheight = 2;
			c.gridwidth = 1;
			c.insets = new Insets(10, 10, 5, 5);		
			layout.addLayoutComponent(confirm, c);
			this.add(confirm);
//			c.fill = GridBagConstraints.BOTH;
			c.fill = GridBagConstraints.WEST;
			c.anchor = GridBagConstraints.WEST;
			c.gridx =2;
			c.gridy = 3;
			c.gridheight = 2;
			c.gridwidth = 1;
			c.insets = new Insets(10, 10, 5, 5);
		//	c.fill = GridBagConstraints.CENTER;
			layout.addLayoutComponent(cancel, c);
			this.add(cancel);
		}
		
		

		public PtnButton getConfirm() {
			return confirm;
		}
		/**
		 * 文件路径选择
		 */
		@SuppressWarnings("unused")
		private void fileButtonActionListener(){			
			this.fileFiled.setText("");
			FileNameExtensionFilter filter=new FileNameExtensionFilter("sql文件","sql");
			JFileChooser chooser = new PtnFileChooser(1, fileFiled, filter);
		}
		/**
		 * ，开始事件
		 * 刷新转储管理列表信息
		 */
		private void confirmActionListener(){
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String table = null;
			List<String> sqlList = null;
			HisAlarmService_MB service = null;
			try {
				String typebox = (String) typeBox.getSelectedItem();
				if(typebox.equals(UnLoadFactory.trans(1))){//历史告警
					table = this.ALARM_TABLE;
				}
//				if(typebox.equals(UnLoadFactory.trans(2))||typebox.equals(UnLoadFactory.trans(4))){//历史性能
				else if(typebox.equals(UnLoadFactory.trans(2))){//历史性能
					table = this.PERFORMANCE_TABLE;
				}
				else if(typebox.equals(UnLoadFactory.trans(3))){
					table = this.OPERATIONLOG_TABLE;//操作日志
				}
				else if(typebox.equals(UnLoadFactory.trans(4))){
					table = this.LOGIN_TABLE;//操作日志
				}
				
				BufferedReader reader=null;
				service = (HisAlarmService_MB) ConstantUtil.serviceFactory.newService_MB(Services.HisAlarm);
				boolean flag = service.selectTable(table);
				/**
				 *    数据库中存在  指定表
				 */
				if(flag){
					if(null !=this.getFileFiled().getText() && !this.getFileFiled().getText().equals(""))
					{
						File file = new File(this.getFileFiled().getText()+"");
						reader = new BufferedReader(new FileReader(file));
						String readLine = null;
						readLine = reader.readLine();
						if(null!= readLine){//文件不为空。
							this.unloadController.getView().getTextArea().setText("");
							this.unloadController.getView().getTextArea().append(ResourceUtil.srcStr(StringKeysBtn.BTN_INPORT_START_FIRST));
							this.unloadController.getView().getTextArea().append("\n");
							sqlList = new ArrayList<String>();
							while(null!=readLine){
								if(readLine.length()>12){
									String sqlTable = readLine.substring(12);
									if(sqlTable.startsWith(table)||sqlTable.startsWith(table.toUpperCase())){
										if(readLine.contains(table)){
											sqlList.add(readLine+"");
										}	
									}
								}
								readLine = reader.readLine();
							}								
							if(sqlList.size()>0){
								int result = service.insertSql(sqlList);
								//添加日志记录
								if(result != 0 && sqlList.size() > result){
									confirm.setResult(1);
									this.unloadController.getView().getTextArea().append(ResourceUtil.srcStr(StringKeysBtn.BTN_INPORT_NOTALL_SUCCESS)+dateFormat.format(new Date()));
									this.unloadController.getView().getTextArea().append("\n");
								}else if(sqlList.size() == result){
									confirm.setResult(1);
									//如果全部导入成功就将文件里的数据全部清除
									//clearFileData(file);
									
									this.unloadController.getView().getTextArea().append(ResourceUtil.srcStr(StringKeysBtn.BTN_INPORT_SUCCESS)+dateFormat.format(new Date()));
									this.unloadController.getView().getTextArea().append("\n");
								}else if(result == 0){
									confirm.setResult(2);
									this.unloadController.getView().getTextArea().append(ResourceUtil.srcStr(StringKeysBtn.BTN_INPORT_FAIL_KEY));
									this.unloadController.getView().getTextArea().append("\n");
								}
								
							}else{
								confirm.setResult(2);
								this.unloadController.getView().getTextArea().append(ResourceUtil.srcStr(StringKeysBtn.BTN_INPORT_FAIL_NOTYPE));
								this.unloadController.getView().getTextArea().append("\n");
							}
							this.unloadController.getView().getTextArea().append(ResourceUtil.srcStr(StringKeysBtn.BTN_INPORT_FINESH));
							this.unloadController.getView().getTextArea().append("\n");
							reader.close();
						}
						else{	
							confirm.setResult(2);
							this.unloadController.getView().getTextArea().setText("");
							this.unloadController.getView().getTextArea().append(ResourceUtil.srcStr(StringKeysBtn.BTN_INPORT_FAIL_NULL));
							this.unloadController.getView().getTextArea().append("\n");
						}
					}else
					{
						 DialogBoxUtil.errorDialog(null,ResourceUtil.srcStr(StringKeysLbl.LBL_SELECT_FILE));
						return ;
					}
					
				}
				this.confirm.setOperateKey(EOperationLogType.UNLOADINPORT.getValue());	
			    confirm.setResult(1);
				this.insertOpeLog(EOperationLogType.UNLOADINPORT.getValue(), ResultString.CONFIG_SUCCESS, null, null);
			} catch (Exception e) {
				ExceptionManage.dispose(e,this.getClass());
			}finally{
				sqlList=null;
				UiUtil.closeService_MB(service);
			}
			InportDialog.this.dispose();		
		}
		
		private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
			AddOperateLog.insertOperLog(confirm, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysMenu.MENU_UNLOADING),"");		
		}
		
		/**
		 * @param file 需要清除的文件
		 */
		private  void clearFileData(File file) {
			FileWriter fileWriter = null;
			BufferedWriter bufferedWriter=null;		
			try {
				fileWriter = new FileWriter(file);
				bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write("");
				bufferedWriter.flush();
			} catch (Exception e) {
				ExceptionManage.dispose(e, getClass());
			}finally{
				 // 关闭流		
				 if (null != bufferedWriter) {
					 try {
						 bufferedWriter.close();
					 } catch (Exception e) {
						 ExceptionManage.dispose(e,this.getClass());
					 } finally {
						 bufferedWriter = null;
					}
				 }	
				 // 关闭流		
				 if (null != fileWriter) {
					 try {
						 fileWriter.close();
					 } catch (Exception e) {
						 ExceptionManage.dispose(e,this.getClass());
					 } finally {
						 fileWriter = null;
					 }
				 }	
			}
		}
		
		public JTextField getFileFiled() {
			return fileFiled;
		}
		public void setFileFiled(JTextField fileFiled) {
			this.fileFiled = fileFiled;
		}		
}
