package com.nms.ui.ptn.business.dialog.pwpath;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EActiveStatus;
import com.nms.db.enums.EPwType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnComboBox;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnSpinner;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.ptn.business.pw.PwBusinessController;
import com.nms.ui.ptn.business.pw.PwBusinessPanel;
import com.nms.ui.ptn.ne.pw.controller.PwNodeController;
import com.nms.ui.ptn.ne.pw.view.PwPanel;

/**
 * pw的过滤查询
 * @author xuxx
 *
 */
public class AddPwFilterDialog extends PtnDialog{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6986834666103144398L;
	
	private PtnButton btnConfirm;//确认按钮
	private JButton btnCancel;//取消
	private PtnButton btnClear;//清除
	private JPanel butttonPanel;//按钮的布局
	
	private JLabel lblname;//pw名称
	private PtnTextField txtName;//名称输入文本框
	private JLabel lblType;//类型
	private JComboBox cmbType;//类型下拉列表
	private JLabel lblTunnelName;//承载的tunnel
	private JComboBox cmbTunnel;//tunnel的下拉列表
	private JLabel lblStatus;//激活状态
	private JComboBox cmbStatus;//状态下拉列表
	private JLabel lblFounder;//创建人
	private PtnTextField txtFounder;//创建人输入
	private JLabel lblASite;//A端网元
	private JComboBox cmbASite;//a端网元下拉列表
	private JLabel lblInRange;//输入标签范围
	private JLabel lblOutRange;//输出标签范围
	private JLabel lblASiteRange;//正向A段，输入标签范围
	private PtnSpinner spnInputMinRange;//正向A段，输入范围框最小值端
	private PtnSpinner spnInputMaxRange;//正向A段，输入范围框最大值端
	private JLabel lblZSiteRange;//反向Z端，输出标签范围
	private PtnSpinner spnOutputMinRange;//反向Z端，输出范围框最小值端
	private PtnSpinner spnOutputMaxRange;//反向Z端，输出范围框最大值端
	private PwInfo pwFilterCondition = null;//网络侧tunnel过滤条件
	private ContentView<PwInfo> contentview = null;//传递父类的pannel用于刷新页面操作
	private int isSingle = 1;//网络侧与但网元侧区分标记 1，网络侧  2，单网元侧
	private JLabel lblToIn = null;//输入标签范围中间的标志
	private JLabel lblToOut = null;//输出标签范围中间的标志
	private JLabel lblPort=null;//针对端口过滤
	private PtnComboBox cmbPort;//端口过滤下拉列表
	
	/**
	 * pw过滤构造方法
	 * @param isSingle 1，为网络端，2，为单网元侧
	 * @param filterCondition pw的过滤条件
	 * @param contentView pw控制面板
	 */
	public AddPwFilterDialog(int isSingle, PwInfo filterCondition,ContentView<PwInfo> contentView) {
		try{
			this.contentview = contentView;//tunnel的控制面板
			this.pwFilterCondition = filterCondition;//过滤条件的记录
			this.isSingle = isSingle;//1，网络侧 2，单网元端
			this.initCompompoments();//组件初始化
			this.setLayout();//布局设置
			this.addListener();//监听事件
			this.initDate();//下拉列表等数据初始化
			if (isSingle == 1) {
				UiUtil.showWindow(this, 350, 388);
			}else {
				UiUtil.showWindow(this, 350, 300);
			}
		}catch (Exception e){
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 初始化各个组件
	 * @throws Exception 
	 */
	private void initCompompoments() throws Exception{
		this.setTitle(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER));//dialog名称
		//dialog中的按钮
		this.btnClear=new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER_CLEAR));//清除
		this.btnCancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));//取消键
		this.btnConfirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),false);//确定
		this.butttonPanel = new JPanel();
		
		this.lblname = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAME));//pw名称
		this.txtName=new PtnTextField(false,50,new JLabel(),this.btnConfirm,this);
		this.lblFounder = new JLabel(ResourceUtil.srcStr(StringKeysObj.STRING_FOUNDER));//创建人
		this.txtFounder = new PtnTextField(false,50,new JLabel(),this.btnConfirm,this);
		this.lblASite = new JLabel(ResourceUtil.srcStr(StringKeysObj.STRING_SITE_NAME));//a端网元
		this.cmbASite = new JComboBox();
		this.lblInRange = new JLabel(ResourceUtil.srcStr(StringKeysLbl.INPUTLBL_RANGE));//单网元中输入标签范围
		this.lblASiteRange = new JLabel(ResourceUtil.srcStr(StringKeysLbl.ASITELBL_RANGE));//a端网元标签范围
		this.spnInputMinRange = new PtnSpinner(16,16,1040383,1);//最小值
		this.spnInputMaxRange = new PtnSpinner(1040383,16,1040383,1);//最大值
		this.lblOutRange = new JLabel(ResourceUtil.srcStr(StringKeysLbl.OUTPUTLBL_RANGE));//单网元中输出标签范围
		this.lblZSiteRange = new JLabel(ResourceUtil.srcStr(StringKeysLbl.ZSITELBL_RANGE));//Z端网元标签范围
		this.spnOutputMinRange = new PtnSpinner(16,16,1040383,1);//最小值
		this.spnOutputMaxRange = new PtnSpinner(1040383,16,1040383,1);//最大值
		this.lblType = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TYPE));//类型
		this.cmbType = new JComboBox();
		this.lblStatus = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ACTIVITY_STATUS));//激活状态
		this.cmbStatus = new JComboBox();
		this.lblTunnelName = new JLabel("Tunnel"+ResourceUtil.srcStr(StringKeysLbl.LBL_NAME));//tunnel名称下拉列表
		this.cmbTunnel = new JComboBox();
		this.lblToIn = new JLabel("-");//输入标签范围中间的标志
		this.lblToOut = new JLabel("-");//输出标签范围中间的标志
		this.lblPort= new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_NAME));
		this.cmbPort = new PtnComboBox();
	}
	
	/**
	 * 界面布局设置
	 */
	public void setLayout(){
		this.setButtonLayout();
		GridBagLayout layout=new GridBagLayout(); 
		layout.columnWidths=new int[]{20,120};
		layout.columnWeights=new double[]{0,0.1};
		layout.rowHeights=new int[]{30};
		layout.rowWeights=new double[]{0};
		this.setLayout(layout);
		GridBagConstraints c=new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets=new Insets(10,8,8,10);
		//1 pw名称
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight=1;
		c.gridwidth=1;
		layout.setConstraints(this.lblname, c);
		this.add(this.lblname);
		//pw名称输入
		c.gridx=1;
		c.gridwidth=1;
		layout.setConstraints(this.txtName, c);
		this.add(this.txtName);
		//2、pw类型下拉列表
		c.insets=new Insets(5,8,8,10);
		c.gridx=0;
		c.gridy=1;
		layout.setConstraints(this.lblType, c);
		this.add(this.lblType);
		c.gridx=1;
		layout.setConstraints(this.cmbType, c);
		this.add(this.cmbType);
		//3、tunnel名称
		c.gridx=0;
		c.gridy=2;
		layout.setConstraints(this.lblTunnelName, c);
		this.add(this.lblTunnelName);
		c.gridx=1;
		layout.setConstraints(this.cmbTunnel, c);
		this.add(this.cmbTunnel);
		//4、激活状态
		c.gridx=0;
		c.gridy=3;
		layout.setConstraints(this.lblStatus, c);
		this.add(this.lblStatus);
		c.gridx=1;
		layout.setConstraints(this.cmbStatus, c);
		this.add(this.cmbStatus);
		if (this.isSingle == 1) {
			this.netLayout(c,layout);
		}else {
			this.singleLayout(c,layout);
		}
	}

	/**
	 * 网络端布局
	 */
	private void netLayout(GridBagConstraints gridBagConstraints,GridBagLayout layot){
		//4、a端网元
		gridBagConstraints.gridx=0;
		gridBagConstraints.gridy=4;
		layot.setConstraints(this.lblASite, gridBagConstraints);
		this.add(this.lblASite);
		//端网元下拉
		gridBagConstraints.gridx=1;
		layot.setConstraints(this.cmbASite, gridBagConstraints);
		this.add(this.cmbASite);
		//6、端口名称
		gridBagConstraints.gridx=0;
		gridBagConstraints.gridy=6;
		layot.setConstraints(this.lblPort, gridBagConstraints);
		this.add(this.lblPort);
		//Z端网元下拉
		gridBagConstraints.gridx=1;
		layot.setConstraints(this.cmbPort, gridBagConstraints);
		this.add(this.cmbPort);

//		//7、入端标签范围
//		gridBagConstraints.gridx=0;
//		gridBagConstraints.gridy=7;
//		if (isSingle == 1) {
//			//网络端
//			layot.setConstraints(this.lblASiteRange, gridBagConstraints);
//			this.add(this.lblASiteRange);
//		}else {
//			//单网元侧
//			layot.setConstraints(this.lblInRange, gridBagConstraints);
//			this.add(this.lblInRange);
//		}
//		gridBagConstraints.gridx=1;
//		gridBagConstraints.insets=new Insets(5,8,8,140);
//		layot.setConstraints(this.spnInputMinRange, gridBagConstraints);
//		this.add(this.spnInputMinRange);
//		gridBagConstraints.insets=new Insets(5,118,8,10);
//		layot.setConstraints(lblToIn, gridBagConstraints);
//		this.add(lblToIn);
//		gridBagConstraints.insets=new Insets(5,130,8,10);
//		layot.setConstraints(this.spnInputMaxRange, gridBagConstraints);
//		this.add(this.spnInputMaxRange);
//		
//		gridBagConstraints.insets=new Insets(5,8,8,10);
//		//8,出端标签范围
//		gridBagConstraints.gridx=0;
//		gridBagConstraints.gridy=8;
//		if (isSingle == 1) {
//			//网络端
//			layot.setConstraints(this.lblZSiteRange, gridBagConstraints);
//			this.add(this.lblZSiteRange);
//		}else {
//			//单网元侧
//			layot.setConstraints(this.lblOutRange, gridBagConstraints);
//			this.add(this.lblOutRange);
//		}
//		gridBagConstraints.gridx=1;
//		gridBagConstraints.insets=new Insets(5,8,8,140);
//		layot.setConstraints(this.spnOutputMinRange, gridBagConstraints);
//		this.add(this.spnOutputMinRange);
//		gridBagConstraints.insets=new Insets(5,118,8,10);
//		layot.setConstraints(lblToOut, gridBagConstraints);
//		this.add(lblToOut);
//		gridBagConstraints.insets=new Insets(5,130,8,10);
//		layot.setConstraints(this.spnOutputMaxRange, gridBagConstraints);
//		this.add(this.spnOutputMaxRange);
//		gridBagConstraints.insets=new Insets(5,8,8,10);
		//8、创建者
		gridBagConstraints.gridx=0;
		gridBagConstraints.gridy=7;
		layot.setConstraints(this.lblFounder, gridBagConstraints);
		this.add(this.lblFounder);
		gridBagConstraints.gridx=1;
		layot.setConstraints(this.txtFounder, gridBagConstraints);
		this.add(this.txtFounder);
		//6、按钮
		gridBagConstraints.insets=new Insets(5,8,5,10);
		gridBagConstraints.gridx=0;
		gridBagConstraints.gridy=8;
		gridBagConstraints.gridwidth=10;
		layot.setConstraints(this.butttonPanel, gridBagConstraints);
		this.add(this.butttonPanel);
	}
	
	/**
	 * 单网元侧布局
	 */
	private void singleLayout(GridBagConstraints c,GridBagLayout layout){
		//4、端口名称
		c.gridx=0;
		c.gridy=4;
		layout.setConstraints(this.lblPort, c);
		this.add(this.lblPort);
		c.gridx=1;
		layout.setConstraints(this.cmbPort, c);
		this.add(this.cmbPort);
		//5、标签范围
//		c.gridx=0;
//		c.gridy=5;
//		if (isSingle == 1) {
//			//网络端
//			layout.setConstraints(this.lblASiteRange, c);
//			this.add(this.lblASiteRange);
//		}else {
//			//单网元侧
//			layout.setConstraints(this.lblInRange, c);
//			this.add(this.lblInRange);
//		}
//		c.gridx=1;
//		c.insets=new Insets(5,8,8,135);
//		layout.setConstraints(this.spnInputMinRange, c);
//		this.add(this.spnInputMinRange);
//		c.insets=new Insets(5,119,8,10);
//		layout.setConstraints(lblToIn, c);
//		this.add(lblToIn);
//		c.insets=new Insets(5,130,8,10);
//		layout.setConstraints(this.spnInputMaxRange, c);
//		this.add(this.spnInputMaxRange);
//		//6,出端标签范围
//		c.insets=new Insets(5,8,8,10);
//		c.gridx=0;
//		c.gridy=6;
//		if (isSingle == 1) {
//			//网络端
//			layout.setConstraints(this.lblZSiteRange, c);
//			this.add(this.lblZSiteRange);
//		}else {
//			//单网元侧
//			layout.setConstraints(this.lblOutRange, c);
//			this.add(this.lblOutRange);
//		}
		c.gridx=1;
		c.insets=new Insets(5,8,8,135);
		layout.setConstraints(this.spnOutputMinRange, c);
		this.add(this.spnOutputMinRange);
		c.insets=new Insets(5,119,8,10);
		layout.setConstraints(lblToOut, c);
		this.add(lblToOut);
		c.insets=new Insets(5,130,8,10);
		layout.setConstraints(this.spnOutputMaxRange, c);
		this.add(this.spnOutputMaxRange);
		c.insets=new Insets(5,8,8,10);
		//7、按钮
		c.gridx=0;
		c.gridy=5;
		c.gridwidth=2;
		layout.setConstraints(this.butttonPanel, c);
		this.add(this.butttonPanel);
		
	}
	
	/**
	 * 下拉列表等数据初始化
	 */
	private void initDate(){
		try {
			
			if ( null != this.pwFilterCondition ) {
				//pw名称
				if ( null != this.pwFilterCondition.getPwName()) {
					this.txtName.setText(this.pwFilterCondition.getPwName());
				}
				//pw创建者
				if ( null != this.pwFilterCondition.getCreateUser()) {
					this.txtFounder.setText(this.pwFilterCondition.getCreateUser());
				}
				//存储最小值最大值的标签范围
				if (this.pwFilterCondition.getInLblMaxValue()!=0) {
					this.spnInputMaxRange.getTxt().setText(this.pwFilterCondition.getInLblMaxValue()+"");
				}
				if (this.pwFilterCondition.getInLblMinValue() != 0) {
					this.spnInputMinRange.getTxt().setText(this.pwFilterCondition.getInLblMinValue()+"");
				}
				if (this.pwFilterCondition.getOutLblMaxValue() != 0) {
					this.spnOutputMaxRange.getTxt().setText(this.pwFilterCondition.getOutLblMaxValue()+"");
				}
				if (this.pwFilterCondition.getOutLblMinValue() != 0) {
					this.spnOutputMinRange.getTxt().setText(this.pwFilterCondition.getOutLblMinValue()+"");
				}
			}
			//状态下拉列表
			this.cmbStatus.addItem(new ControlKeyValue(0 + "", ResourceUtil.srcStr(StringKeysObj.STRING_ALL), ""));
			this.cmbStatus.addItem(new ControlKeyValue(EActiveStatus.ACTIVITY.getValue() + "", ResourceUtil.srcStr(StringKeysObj.ACTIVITY_YES), ""));
			this.cmbStatus.addItem(new ControlKeyValue(EActiveStatus.UNACTIVITY.getValue() + "", ResourceUtil.srcStr(StringKeysObj.ACTIVITY_NO), ""));
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbStatus, (this.pwFilterCondition.getPwStatus()+""));
			//tunnel名称下拉列表
			this.setTunnelBox(this.cmbTunnel);			
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbTunnel, (this.pwFilterCondition.getTunnelId()+""));
			//端口名称,单站侧和网络侧分别进行初始化下拉列表
			if (isSingle == 2)
			{
				this.setPortCombox(this.cmbPort);
			}
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbPort, (this.pwFilterCondition.getPortId()+""));
			if (isSingle == 1) {
				//类型
				this.comBoxPwtypeData(this.cmbType);//连接数据库获得类型选择框
				if (null != this.pwFilterCondition.getType()) {
					super.getComboBoxDataUtil().comboBoxSelect(this.cmbType, (this.pwFilterCondition.getType().getValue()+""));
				}
				setAZNetComboBox();//AZ端下拉列表设置
			}else {
				Code codeType = new Code();
				codeType.setId(0);
				codeType.setCodeName(ResourceUtil.srcStr(StringKeysObj.STRING_ALL));
				this.cmbType.addItem(new ControlKeyValue(codeType.getId() + "", codeType.getCodeName(), codeType));
				super.getComboBoxDataUtil().comboBoxData(this.cmbType, "PWTYPESITE");
				if (null != this.pwFilterCondition.getType() && this.pwFilterCondition.getType().getValue() != 0) {
					super.getComboBoxDataUtil().comboBoxSelectByValue(this.cmbType, this.pwFilterCondition.getType().getValue()+"");
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	/**
	 * 事件监听
	 */
	private void addListener(){
		
		/**
		 * 取消按钮事件
		 */
		this.btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		/**
		 * 确认按钮
		 */
		this.btnConfirm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//若不清空 则进行各项的赋值
				try {
					if (null != pwFilterCondition) {
						pwFilterCondition.setPwStatus(Integer.parseInt(((ControlKeyValue) cmbStatus.getSelectedItem()).getId()));//状态
						pwFilterCondition.setPwName(txtName.getText());//pw名称
						pwFilterCondition.setInLblMinValue(Integer.parseInt(spnInputMinRange.getTxtData()));//标签范围
						pwFilterCondition.setInLblMaxValue(Integer.parseInt(spnInputMaxRange.getTxtData()));
						pwFilterCondition.setOutLblMinValue(Integer.parseInt(spnOutputMinRange.getTxtData()));
						pwFilterCondition.setOutLblMaxValue(Integer.parseInt(spnOutputMaxRange.getTxtData()));
						pwFilterCondition.setTunnelId(Integer.parseInt(((ControlKeyValue) cmbTunnel.getSelectedItem()).getId()));//tunnel名称
						pwFilterCondition.setPortId(Integer.parseInt(((ControlKeyValue)cmbPort.getSelectedItem()).getId()));
						if (isSingle == 1) {
							//将选择的下拉列表存储到记录中
							pwFilterCondition.setType((EPwType) ((ControlKeyValue) cmbType.getSelectedItem()).getObject());//类型
							pwFilterCondition.setASiteId(Integer.parseInt(((ControlKeyValue) cmbASite.getSelectedItem()).getId()));//a端网元
							pwFilterCondition.setCreateUser(txtFounder.getText());//创建者
							((PwBusinessController)((PwBusinessPanel)contentview).getController()).setFilterCondition(pwFilterCondition);
						}
						if (isSingle == 2) {
							//单网元侧的类型
							Code codePwType = (Code) ((ControlKeyValue) cmbType.getSelectedItem()).getObject();
							if (null != codePwType.getCodeValue()) {
								pwFilterCondition.setType(EPwType.forms(Integer.parseInt(codePwType.getCodeValue())));
							}else {
								pwFilterCondition.setType(EPwType.forms(0));
							}
							((PwNodeController)((PwPanel)contentview).getController()).setFilterCondition(pwFilterCondition);
						}
					}
					contentview.getController().refresh();
				} catch (Exception e1) {
					ExceptionManage.dispose(e1, this.getClass());
				}
				dispose();
			}
		});
		
		/**
		 * 清除过滤设置
		 */
		this.btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtName.setText("");
				cmbStatus.setSelectedIndex(0);
				cmbType.setSelectedIndex(0);
				cmbTunnel.setSelectedIndex(0);
//				spnInputMinRange.getTxt().setText(16+"");
//				spnInputMaxRange.getTxt().setText(1040383+"");
//				spnOutputMinRange.getTxt().setText(16+"");
//				spnOutputMaxRange.getTxt().setText(1040383+"");
				cmbPort.setSelectedIndex(0);
				if (isSingle == 1) {
					cmbASite.setSelectedIndex(0);
					txtFounder.setText("");
					((PwBusinessController)((PwBusinessPanel)contentview).getController()).setFilterCondition(null);
				}else {
					((PwNodeController)((PwPanel)contentview).getController()).setFilterCondition(null);
				}
				pwFilterCondition = new PwInfo();
			}
		});
		
		this.cmbASite.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED) {
					ControlKeyValue controlKeyValue = (ControlKeyValue) e.getItem();
					setBussinessPortCombox(cmbPort, Integer.parseInt(controlKeyValue.getId()));
				}
			}
		});
	}
	
	/**
	 * 设置按钮布局
	 */
	private void setButtonLayout() {
		GridBagLayout layout=new GridBagLayout(); 
		layout.columnWidths=new int[]{20,100,20,20};
		layout.columnWeights=new double[]{0,0.1,0,0};
		layout.rowHeights=new int[]{20};
		layout.rowWeights=new double[]{0.1};
		this.butttonPanel.setLayout(layout);
		GridBagConstraints c=new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		//1 清除过滤按钮
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight=1;
		c.gridwidth=1;
		c.insets=new Insets(5,5,0,10);
		layout.setConstraints(this.btnClear, c);
		butttonPanel.add(this.btnClear);
		c.gridx=2;
		layout.setConstraints(this.btnConfirm, c);
		butttonPanel.add(this.btnConfirm);
		c.gridx=3;
		layout.setConstraints(this.btnCancel, c);
		butttonPanel.add(this.btnCancel);
		
	}

	
	/**
	 * AZ端网元下拉列表
	 */
	public void setAZNetComboBox(){
		// 在UiUtil中新加入一个方法
		try {
			this.setSiteBox(this.cmbASite);
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbASite, (this.pwFilterCondition.getASiteId()+""));
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());		
		}
	}

	/**
	 * 设置A、Z端网元的下拉列表 查询所有网元
	 * @param jComboBox a,z端的下拉列表
	 * @throws Exception
	 */
	private void setSiteBox(JComboBox comboBox){
		List<SiteInst> sites = null;
		SiteService_MB services = null;
		DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) comboBox.getModel();
		try {
			services = (SiteService_MB) ConstantUtil.serviceFactory
					.newService_MB(Services.SITE);
			sites = services.select();
			defaultComboBoxModel.addElement(new ControlKeyValue(0 + "", ResourceUtil.srcStr(StringKeysObj.STRING_ALL), ""));
			if (sites != null) {
				for (SiteInst site : sites) {
					defaultComboBoxModel.addElement(new ControlKeyValue(site.getSite_Inst_Id() + "", site.getCellId(), site));
				}
			}
			comboBox.setModel(defaultComboBoxModel);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(services);
		}
	}
	/**
	 * 查询tunnel表过得tunnel名称还是查询pwinfo表获得
	 */
	private void setTunnelBox(JComboBox comboBox){
		List<Tunnel> tunnels = null;
		TunnelService_MB tunnelServiceMB = null;
		DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) comboBox.getModel();
		try {
			tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			if (isSingle == 1) {
				tunnels = tunnelServiceMB.select();//网络侧的tunnel
			}else {
				tunnels = tunnelServiceMB.selectNodesBySiteId(ConstantUtil.siteId);//单网元侧的tunnel
			}
			
			defaultComboBoxModel.addElement(new ControlKeyValue(0 + "", ResourceUtil.srcStr(StringKeysObj.STRING_ALL), ""));
			if (null != tunnels) {
				for (Tunnel tunnel : tunnels) {
					defaultComboBoxModel.addElement(new ControlKeyValue(tunnel.getTunnelId() + "", tunnel.getTunnelName(), tunnel));
				}
			}
			comboBox.setModel(defaultComboBoxModel);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			tunnels = null;
			UiUtil.closeService_MB(tunnelServiceMB);
			defaultComboBoxModel = null;
		}
	}
	
	private List<Integer> removeRepeatedsites(List<Integer> sites) {
		List<Integer> siteList = sites;
		for (int i = 0; i < siteList.size() - 1; i++) {
			for (int j = siteList.size() - 1; j > i; j--) {
				if (siteList.get(j)==siteList.get(i)) {
					siteList.remove(j);
				}
			}
		}
		return siteList;
	}
	
	/**
	 * 查询端口 add by dxh
	 */
	private void setPortCombox(JComboBox comboBox)
	{
		PortService_MB portService = null;
		List<PortInst> ports = new ArrayList<PortInst>();
		DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) comboBox.getModel();
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			//分单站侧和网络侧分别进行处理
			if (isSingle == 2) 
			{
				ports = portService.selectNniPortname(ConstantUtil.siteId);
			}
			defaultComboBoxModel.addElement(new ControlKeyValue(0 + "", ResourceUtil.srcStr(StringKeysObj.STRING_ALL), ""));
			if (null != ports) {
				for (PortInst port : ports) {
					defaultComboBoxModel.addElement(new ControlKeyValue(port.getPortId() + "", port.getPortName(), port));
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			ports = null;
			UiUtil.closeService_MB(portService);
		}
	}
	
	private void setBussinessPortCombox(JComboBox comboBox, int siteId)
	{
		PortService_MB portService = null;
		List<PortInst> ports = null;
		DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) comboBox.getModel();
		defaultComboBoxModel.removeAllElements();
		try
		{
		    portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			if(siteId > 0)
			{
				ports=portService.selectNniPortname(siteId);
			}
			defaultComboBoxModel.addElement(new ControlKeyValue(0 + "", ResourceUtil.srcStr(StringKeysObj.STRING_ALL), ""));
			if (null != ports) {
				for (PortInst port : ports) {
					defaultComboBoxModel.addElement(new ControlKeyValue(port.getPortId() + "", port.getPortName(), port));
				}
			}
		}
		catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			ports = null;
			UiUtil.closeService_MB(portService);
		}
		
	}

	/**
	 * 对pw类型下拉列表进行赋值
	 * @param pwType
	 */
	private void comBoxPwtypeData(JComboBox pwType) {

		DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) pwType.getModel();
		for (EPwType type : EPwType.values()) {
			if (type != EPwType.NONE){
				defaultComboBoxModel.addElement(new ControlKeyValue(type.getValue() + "", type.toString(), type));
			}else {
				defaultComboBoxModel.addElement(new ControlKeyValue(EPwType.NONE + "", ResourceUtil.srcStr(StringKeysObj.STRING_ALL), type));
			}
		}
		pwType.setModel(defaultComboBoxModel);
		defaultComboBoxModel = null;
	}
}
