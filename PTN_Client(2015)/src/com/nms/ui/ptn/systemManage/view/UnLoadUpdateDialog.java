package com.nms.ui.ptn.systemManage.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.nms.db.bean.system.UnLoadFactory;
import com.nms.db.bean.system.UnLoading;
import com.nms.db.enums.EOperationLogType;
import com.nms.rmi.ui.AutoDatabaseBackThread;
import com.nms.rmi.ui.util.ServerConstant;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.Ptnf;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnFileChooser;
import com.nms.ui.manager.control.PtnSpinner;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.systemManage.ReadUnloadXML;
import com.nms.ui.ptn.systemManage.controller.UnLoadingController;


/**
 * 修改按钮 打开的界面
 * @author sy
 *
 */

public class UnLoadUpdateDialog extends PtnDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private    UnLoadManagerPanel loadPanel=new UnLoadManagerPanel(this);
	private PtnButton confirm;
	private JButton cancel;
	private JLabel fileType;
	private JTextField fileFiled;
	private JButton fileButton;	
	private JLabel cellType;//激活
	private JLabel lblMessage;//提示信息
	private JLabel lblTransType;
	private JComboBox jcbTransType;//转储方式  ：sql  /excel
	private JLabel spillEntry;
	private JLabel holdEntry;
	private JCheckBox cellbox;
	private JTextField cellFiled;
	private PtnTextField spillFiled;
	private PtnTextField holdFiled;
	private UnLoadingController unloadController=null;
	private JCheckBox isAuto;//是否自动转储
	private JLabel startTime;//开始时间
	private JTextField startTimeText;
	private JLabel timeInterval;//时间间隔
	private PtnSpinner timeJSpinner;
	private Map<String,Runnable> threadMap ;
	public UnLoadUpdateDialog(UnLoadingController unloadController) {
		try {
		super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_TRANSFER));
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
		//保存，（显示转储信息）按钮
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
				UnLoadUpdateDialog.this.dispose();
			}
		});	
		
		if(isAuto != null){
			isAuto.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(isAuto.isSelected()){
						startTimeText.setEditable(true);
						timeJSpinner.setEnabled(true);
					}else{
						startTimeText.setEditable(false);
						timeJSpinner.setEnabled(false);
					}
				}
			});
		}
	}	
	
	private void initComponents() throws Exception {
		
		UnLoading unload=this.unloadController.getView().getSelect();
		lblMessage=new JLabel();		
		fileType = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_FILE_ROUTE));
		cellType = new JLabel(ResourceUtil.srcStr(StringKeysMenu.MENU_ACTIVATION));//
		this.lblTransType=new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TRANS_TYPE));
		this.jcbTransType=new JComboBox();
		this.jcbTransType.addItem(ResourceUtil.srcStr(StringKeysMenu.MENU_SQL_FILE));
		this.jcbTransType.addItem(ResourceUtil.srcStr(StringKeysMenu.MENU_EXCEL_FILE));
		spillEntry = new JLabel(ResourceUtil.srcStr(StringKeysBtn.BTN_UNSPILLENTRY));
		holdEntry = new JLabel(ResourceUtil.srcStr(StringKeysBtn.BTN_UNHOLDENTRY));
		if(unload.getUnloadType()==1 && unload.getFileWay().equals("")){
			unload.setFileWay(ServerConstant.AUTODATABACKALARM_FILE);
		}else if(unload.getUnloadType()==2 && unload.getFileWay().equals("")){
			unload.setFileWay(ServerConstant.AUTODATABACKPM_FILE);
		}else if(unload.getUnloadType()==3 && unload.getFileWay().equals("")) {
			unload.setFileWay(ServerConstant.AUTODATABACKOPERATION_FILE);
		}else if(unload.getUnloadType()==4 && unload.getFileWay().equals("")){
			unload.setFileWay(ServerConstant.AUTODATABACKLOGING_FILE);
		}
		fileFiled=new javax.swing.JTextField(unload.getFileWay());
		fileFiled.setEditable(false);//只读
		confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),false);
		cellFiled=new javax.swing.JTextField(unload.getCellType()+"");
		spillFiled=new PtnTextField(true,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage,this.confirm,this);
		spillFiled.setText(unload.getSpillEntry()+"");
		holdFiled=new PtnTextField(true,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH,this.lblMessage,this.confirm,this);
		holdFiled.setText(unload.getHoldEntry()+"");
		cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		fileButton=new javax.swing.JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_CHECKCATA));
		boolean flag=false;
		if(unload.getCellType()==0){
			flag=true;
		}
		 cellbox=new javax.swing.JCheckBox();
		 cellbox.setSelected(flag);
		//增加自动转储功能 
//		if(unload.getUnloadType()!=3 && unload.getUnloadType()!=4){
			isAuto = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_START));//是否自动转储
			startTime = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_START_TIME));//开始时间
			startTimeText = new JTextField();
			timeInterval = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMEINTERVAL));//时间间隔
			timeJSpinner = new PtnSpinner(unload.getTimeInterval(),1,Integer.MAX_VALUE,1);
			SimpleDateFormat fat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(unload.getIsAuto() == 1){
				isAuto.setSelected(true); 
				startTimeText.setEditable(true);
				timeJSpinner.setEnabled(true);
			}else{
				startTimeText.setEditable(false);
				timeJSpinner.setEnabled(false);
			}
			if(unload.getAutoStartTime()!= null && !unload.getAutoStartTime().equals("")){
				startTimeText.setText(unload.getAutoStartTime());
			}else{
				startTimeText.setText(fat.format(new Date()));
			}
//		}
	}
	private void setLayout() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 90,100,50,50};
		layout.columnWeights = new double[] { 0,0.2, 0.2,0};
		layout.rowHeights = new int[] { 20,40,40,40,40,20,20,20,20};
		layout.rowWeights = new double[] { 0.1, 0,0,0,0,0};
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();	
		
		//第一行
		//c.fill = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 4;
		c.insets = new Insets(5, 5, 5, 10);
		/**1  上边的距离
		 * 2   左边的距离
		 * 3   下边的距离
		 */
		layout.addLayoutComponent(this.lblMessage, c);
		this.add(lblMessage);
		//第二行
	
		c.fill = GridBagConstraints.HORIZONTAL ;
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.addLayoutComponent(this.spillEntry, c);
		this.add(spillEntry);
		
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 3;
		layout.addLayoutComponent(this.spillFiled, c);
		this.add(spillFiled);
		
		//3
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.addLayoutComponent(this.holdEntry, c);
		this.add(holdEntry);
		
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 3;
		layout.addLayoutComponent(this.holdFiled, c);
		this.add(holdFiled);
		
		
		/**
		 *加入可修改转储方式		
		 */
		//第思行
//		c.fill = GridBagConstraints.HORIZONTAL ;
//		c.gridx = 0;
//		c.gridy = 3;
//		c.gridheight = 1;
//		c.gridwidth = 1;
//		layout.addLayoutComponent(this.lblTransType, c);
//		this.add(lblTransType);
//		
//		c.gridx = 1;
//		c.gridy = 3;
//		c.gridheight = 1;
//		c.gridwidth = 2;
//		layout.addLayoutComponent(this.jcbTransType, c);
//		this.add(jcbTransType);

	
		//第五行

		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.addLayoutComponent(this.fileType, c);
		this.add(fileType);
		
		c.gridx =1;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth =2;
		layout.addLayoutComponent(this.fileFiled, c);
		this.add(fileFiled);
		
		c.gridx = 3;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.addLayoutComponent(this.fileButton, c);
		this.add(fileButton);
		
		//增加自动转储功能 
//		if(this.unloadController.getView().getSelect().getUnloadType()!=3 && this.unloadController.getView().getSelect().getUnloadType()!=4){
			c.gridx = 0;
			c.gridy = 4;
			c.gridheight = 1;
			c.gridwidth = 1;
			layout.addLayoutComponent(this.isAuto, c);
			this.add(isAuto);
			
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy =5;
			c.gridheight = 1;
			layout.addLayoutComponent(this.startTime, c);
			this.add(startTime);
			
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 1;
			c.gridy =5;
			c.gridheight = 1;
			c.gridwidth = 3;
			layout.addLayoutComponent(this.startTimeText, c);
			this.add(startTimeText);
			
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy =6;
			c.gridheight = 1;
			c.gridwidth = 2;
			layout.addLayoutComponent(this.timeInterval, c);
			this.add(timeInterval);
			
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 1;
			c.gridy =6;
			c.gridheight = 1;
			c.gridwidth = 3;
			layout.addLayoutComponent(this.timeJSpinner, c);
			this.add(timeJSpinner);
			
			//第6行
			c.gridx = 0;
			c.gridy = 7;
			c.gridheight = 1;
			c.gridwidth = 1;
			layout.addLayoutComponent(this.cellType, c);
			this.add(cellType);
			c.fill = GridBagConstraints.WEST;
			c.anchor = GridBagConstraints.WEST;
			c.gridx = 1;
			c.gridy =7;
			c.gridheight = 1;
			c.gridwidth = 1;
			layout.addLayoutComponent(this.cellbox, c);
			this.add(cellbox);
			//第7行
			c.fill = GridBagConstraints.EAST;
			c.anchor = GridBagConstraints.EAST;
			c.gridx =2;
			c.gridy = 8;
			c.gridheight = 2;
			c.gridwidth = 1;		
			layout.addLayoutComponent(confirm, c);
			this.add(confirm);
			c.fill = GridBagConstraints.WEST;
			c.anchor = GridBagConstraints.WEST;
			
			c.gridx =3;
			c.gridy = 8;
			c.gridheight = 2;
			c.gridwidth = 1;
			layout.addLayoutComponent(cancel, c);
			this.add(cancel);
			
//		}else{
//			//第6行
//			c.gridx = 0;
//			c.gridy = 4;
//			c.gridheight = 1;
//			c.gridwidth = 1;
//			layout.addLayoutComponent(this.cellType, c);
//			this.add(cellType);
//			c.fill = GridBagConstraints.WEST;
//			c.anchor = GridBagConstraints.WEST;
//			c.gridx = 1;
//			c.gridy =4;
//			c.gridheight = 1;
//			c.gridwidth = 1;
//			layout.addLayoutComponent(this.cellbox, c);
//			this.add(cellbox);
//			//第7行
//			c.fill = GridBagConstraints.EAST;
//			c.anchor = GridBagConstraints.EAST;
//			c.gridx =2;
//			c.gridy = 5;
//			c.gridheight = 2;
//			c.gridwidth = 1;		
//			layout.addLayoutComponent(confirm, c);
//			this.add(confirm);
//			c.fill = GridBagConstraints.WEST;
//			c.anchor = GridBagConstraints.WEST;
//			
//			c.gridx =3;
//			c.gridy = 5;
//			c.gridheight = 2;
//			c.gridwidth = 1;
//			layout.addLayoutComponent(cancel, c);
//			this.add(cancel);
//		}
	}
	
	public JTextField getCellFiled() {
		return cellFiled;
	}

	public void setCellFiled(JTextField cellFiled) {
		this.cellFiled = cellFiled;
	}

	public JTextField getSpillFiled() {
		return spillFiled;
	}

	public JTextField getHoldFiled() {
		return holdFiled;
	}

	public void setHoldFiled(PtnTextField holdFiled) {
		this.holdFiled = holdFiled;
	}

	public PtnButton getConfirm() {
		return confirm;
	}
	/**
	 * 文件转储路径选择
	 */
	private void fileButtonActionListener(){
		this.fileFiled.setText("");
		try {
			this.fileFiled.setText("");
			FileNameExtensionFilter filter=new FileNameExtensionFilter("sql文件","sql");
			JFileChooser chooser=new PtnFileChooser(PtnFileChooser.TYPE_FOLDER, fileFiled, filter);
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	/**
	 * ，保存事件
	 * 刷新转储管理列表信息
	 */
	private void confirmActionListener(){
		UnLoading unload=null;
		UnLoading unLoad=null;	
		List<UnLoading> list=null;
		ReadUnloadXML readUnloadXML=null;
		SimpleDateFormat format = null;
		String	 regex = "^(((20[0-3][0-9]-(0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|(20[0-3][0-9]-(0[2469]|11)-(0[1-9]|[12][0-9]|30))) (20|21|22|23|[0-1][0-9]):[0-5][0-9]:[0-5][0-9])$";
		long time = 0 ;
		try {
			threadMap = Ptnf.getPtnf().getThreadMap();
			if(null==spillFiled.getText()||null==holdFiled.getText()||null==this.fileType.getText()){
				DialogBoxUtil.confirmDialog(this.unloadController.getView(), ResourceUtil.srcStr(StringKeysBtn.BTN_UNDATA));
			}else{
				  unLoad=this.unloadController.getView().getSelect();
				  unload =new UnLoading();
				  unload.setUnloadType(unLoad.getUnloadType());				
				if(this.cellbox.isSelected()){					
					unload.setCellType(0);
				}else{
					unload.setCellType(1);
				}
				   unload.setSpillEntry(Integer.parseInt(this.getSpillFiled().getText()));
				   unload.setHoldEntry(Integer.parseInt(this.getHoldFiled().getText()));
				if(this.jcbTransType.getSelectedItem().equals(ResourceUtil.srcStr(StringKeysMenu.MENU_SQL_FILE))){
					//转储为sql文件
					unload.setUnloadMod(0);
				}else{
					//转储为excel文件
					unload.setUnloadMod(1);
				}
				if (this.getFileFiled().getText().trim().length() == 0) {
					DialogBoxUtil.succeedDialog(null,  ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_CHECKBACKUP_ROUTE));
					return;
				}
				unload.setFileWay(this.getFileFiled().getText());
				//自动转储配置
			   if(isAuto != null){
				unload.setIsAuto(this.isAuto.isSelected()?1:0);
				unload.setAutoStartTime(this.startTimeText.getText().trim());
				unload.setTimeInterval(Integer.parseInt(this.timeJSpinner.getTxtData()));
				
				}
			   if(isAuto != null ){
				   if(isAuto.isSelected()){
					   if(!startTimeText.getText().trim().matches(regex)){
						   DialogBoxUtil.succeedDialog(null,ResourceUtil.srcStr(StringKeysLbl.LBL_TIME_ERROR));
						   return ;
					   }
					   format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					   time = format.parse(startTimeText.getText().trim()).getTime();
					   //启动自动转储线程
					   //int label,int count   1:告警 2:性能 3:操作日志 4:登录日志
//					   AutoBackDataTask task = new AutoBackDataTask(unload.getUnloadType(),unload.getSpillEntry(),this.getFileFiled().getText());
					   //TimerTask task,long startTime,long cycleTime
//					   System.currentTimeMillis(),unload
					   AutoDatabaseBackThread autoThread = new AutoDatabaseBackThread(time,unload);
					   String threadName = "task_UnLoading_"+unLoad.getUnloadType();
					   Thread thread = new Thread(autoThread,threadName);
					   if(threadMap != null && threadMap.size() >0){
						   if(threadMap.get(thread.getName()) != null){
							   ((AutoDatabaseBackThread)threadMap.get(thread.getName())).stop();
							   threadMap.remove(thread.getName());
						   }
					   }
					   thread.start();
					   threadMap.put(thread.getName(), autoThread);
				   }else{
					   String threadName = "task_UnLoading_"+unLoad.getUnloadType();
					   if(threadMap != null && threadMap.size() >0){
						   if(threadMap.get(threadName) != null){
							   ((AutoDatabaseBackThread)threadMap.get(threadName)).stop();
							   threadMap.remove(threadName);
						   }
					   }
				   }
			   }
			   readUnloadXML=new ReadUnloadXML();
			   list=ReadUnloadXML.selectUnloadXML();
			   UnLoading old=new UnLoading();
			   for(int i=0;i<list.size();i++){
				   if(list.get(i).getUnloadType() == unLoad.getUnloadType()){
					   old=list.get(i);	
					   break;
				   }
				   				  
			   }
			   readUnloadXML.updateUnloadXML(unload);
			   
			   File fileMider = new File(unload.getFileWay());
			   if(!fileMider.exists()){
					 fileMider.mkdirs();
				 }
				//添加日志记录
			   this.confirm.setOperateKey(EOperationLogType.UNLOADUPDATE.getValue());
			   confirm.setResult(1);
			   this.insertOpeLog(EOperationLogType.UNLOADUPDATE.getValue(), ResultString.CONFIG_SUCCESS, old, unload);
			   this.unloadController.getView().getTextArea().setText("");
			   this.unloadController.getView().getTextArea().append(UnLoadFactory.trans(unload.getUnloadType())+ResourceUtil.srcStr(StringKeysBtn.BTN_UPDATESUCCESS_TRANFERPATH)+unload.getFileWay());
			   this.unloadController.getView().getTextArea().append("\n");
			   this.unloadController.refresh();				
			}		
		} catch (Exception e) {
			
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			unLoad=null;
			unload=null;
		}
		UnLoadUpdateDialog.this.dispose();		
	}
	
	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		AddOperateLog.insertOperLog(confirm, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysMenu.MENU_UNLOADING),"UnLoading");		
	}
	public JTextField getFileFiled() {
		return fileFiled;
	}
	public void setFileFiled(JTextField fileFiled) {
		this.fileFiled = fileFiled;
	}
	
}
