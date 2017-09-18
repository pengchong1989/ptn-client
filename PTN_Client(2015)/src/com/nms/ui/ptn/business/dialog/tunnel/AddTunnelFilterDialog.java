package com.nms.ui.ptn.business.dialog.tunnel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.enums.EActiveStatus;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.ptn.business.tunnel.TunnelBusinessController;
import com.nms.ui.ptn.business.tunnel.TunnelBusinessPanel;
import com.nms.ui.ptn.ne.tunnel.controller.TunnelNodeController;
import com.nms.ui.ptn.ne.tunnel.view.TunnelPanel;
/**
 * tunnel的过滤条件查询对话框
 * @author xuxiaoxin
 *
 */
public class AddTunnelFilterDialog extends PtnDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8110204295587819838L;
	
	private PtnButton btnConfirm;//确认按钮
	private JButton btnCancel;//取消
	private PtnButton btnClear;//清除
	private JPanel butttonPanel;//按钮的布局
	
	private JLabel lblname;//名称
	private PtnTextField txtName;//名称输入文本框
	private JLabel lblType;//类型
	private JComboBox cmbType;//类型下拉列表
	private JLabel lblStatus;//激活状态
	private JComboBox cmbStatus;//状态下拉列表
	private JLabel lblFounder;//创建人
	private PtnTextField txtFounder;//创建人输入
	private JLabel lblASite;//A端网元
	private JLabel lblZSite;//Z端网元
	private JComboBox cmbASite;//a端网元下拉列表
	private JComboBox cmbZSite;//z端网元下拉列表
	private JLabel lblRole;//角色
	private JComboBox cmbRole;//角色下拉列表
	private Tunnel tunnelFilterCondition = null;//网络侧tunnel过滤条件
	private ContentView<Tunnel> contentview = null;//传递父类的pannel用于刷新页面操作
	private int isSingle = 1;//网络侧与但网元侧区分标记 1，网络断 2，单网元侧
	
	/**
	 * 构造方法进行各项初始化
	 * @param isSingle 区分网络侧和单网元侧
	 * @param filterCondition tunnel的过滤条件
	 */
	public AddTunnelFilterDialog( int isSingle,Tunnel filterCondition,ContentView<Tunnel> contentView){
		try{
			this.contentview = contentView;//tunnel的控制面板
			this.tunnelFilterCondition = filterCondition;//过滤条件的记录
			this.isSingle = isSingle;//1，网络断 2，单网元端
			this.initCompompoments();//组件初始化
			this.setLayout();//布局设置
			this.addListener();//监听事件
			this.initDate();//下拉列表等数据初始化
			if (isSingle == 1) {
				UiUtil.showWindow(this, 340, 290);
			}else {
				UiUtil.showWindow(this, 320, 218);
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
		//网络侧
		this.lblname = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAME));//tunnel名称
		this.txtName=new PtnTextField(false,50,new JLabel(),this.btnConfirm,this);
		this.lblFounder = new JLabel(ResourceUtil.srcStr(StringKeysObj.STRING_FOUNDER));//创建人
		this.txtFounder = new PtnTextField(false,50,new JLabel(),this.btnConfirm,this);
		this.lblASite = new JLabel(ResourceUtil.srcStr(StringKeysObj.STRING_A_SITE_NAME));//a端网元
		this.cmbASite=new JComboBox();
		this.lblZSite = new JLabel(ResourceUtil.srcStr(StringKeysObj.STRING_Z_SITE_NAME)); //z端网元
		this.cmbZSite=new JComboBox();
		this.lblType = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TYPE));//类型
		this.cmbType = new JComboBox();
		this.lblStatus = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ACTIVITY_STATUS));//激活状态
		this.cmbStatus = new JComboBox();
		this.butttonPanel=new JPanel();
		
		//单网元侧
		this.lblRole = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ROLE));//角色
		this.cmbRole = new JComboBox();
		
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
		//1 tunnel名称
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight=1;
		c.gridwidth=1;
		layout.setConstraints(this.lblname, c);
		this.add(this.lblname);
		c.insets=new Insets(5,8,8,10);
		//tunnel名称
		c.gridx=1;
		c.gridwidth=1;
		layout.setConstraints(this.txtName, c);
		this.add(this.txtName);
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
		//2、A断网元
		gridBagConstraints.gridx=0;
		gridBagConstraints.gridy=1;
		layot.setConstraints(this.lblASite, gridBagConstraints);
		this.add(this.lblASite);
		//端网元下拉
		gridBagConstraints.gridx=1;
		layot.setConstraints(this.cmbASite, gridBagConstraints);
		this.add(this.cmbASite);
		//3、Z端网元		
		gridBagConstraints.gridx=0;
		gridBagConstraints.gridy=2;
		layot.setConstraints(this.lblZSite, gridBagConstraints);
		this.add(this.lblZSite);
		//Z端网元下拉
		gridBagConstraints.gridx=1;
		layot.setConstraints(this.cmbZSite, gridBagConstraints);
		this.add(this.cmbZSite);
		//4、类型
		gridBagConstraints.gridx=0;
		gridBagConstraints.gridy=3;
		layot.setConstraints(this.lblType, gridBagConstraints);
		this.add(this.lblType);
		gridBagConstraints.gridx=1;
		layot.setConstraints(this.cmbType, gridBagConstraints);
		this.add(this.cmbType);
		//6 、创建人
		gridBagConstraints.gridx=0;
		gridBagConstraints.gridy=5;
		layot.setConstraints(this.lblFounder, gridBagConstraints);
		this.add(this.lblFounder);
		gridBagConstraints.gridx=1;
		layot.setConstraints(this.txtFounder, gridBagConstraints);
		this.add(this.txtFounder);
		//5、激活状态
		gridBagConstraints.gridx=0;
		gridBagConstraints.gridy=4;
		layot.setConstraints(this.lblStatus, gridBagConstraints);
		this.add(this.lblStatus);
		gridBagConstraints.gridx=1;
		layot.setConstraints(this.cmbStatus, gridBagConstraints);
		this.add(this.cmbStatus);
		//6、按钮
		gridBagConstraints.gridx=0;
		gridBagConstraints.gridy=6;
		gridBagConstraints.gridwidth=2;
		layot.setConstraints(this.butttonPanel, gridBagConstraints);
		this.add(this.butttonPanel);
	}
	
	/**
	 * 单网元侧布局
	 */
	private void singleLayout(GridBagConstraints c,GridBagLayout layout){
		//2、类型 
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth=1;
		layout.setConstraints(this.lblType, c);
		this.add(this.lblType);
		c.gridx = 1;
		layout.setConstraints(this.cmbType, c);
		this.add(this.cmbType);
		//3、角色
		c.gridx = 0;
		c.gridy = 2;
		layout.setConstraints(this.lblRole, c);
		this.add(this.lblRole);
		c.gridx = 1;
		layout.setConstraints(this.cmbRole, c);
		this.add(this.cmbRole);
		//4、激活状态
		c.gridx = 0;
		c.gridy = 3;
		layout.setConstraints(this.lblStatus, c);
		this.add(this.lblStatus);
		c.gridx = 1;
		layout.setConstraints(this.cmbStatus, c);
		this.add(this.cmbStatus);
		//5、按钮
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth=2;
		layout.setConstraints(this.butttonPanel, c);
		this.add(this.butttonPanel);
	}
	
	/**
	 * 下拉列表等数据初始化
	 */
	private void initDate(){
		try {
			if ( null != this.tunnelFilterCondition && null != this.tunnelFilterCondition.getTunnelName()) {
				this.txtName.setText(this.tunnelFilterCondition.getTunnelName());
			}
			if ( null != this.tunnelFilterCondition && null != this.tunnelFilterCondition.getCreateUser()) {
				this.txtFounder.setText(this.tunnelFilterCondition.getCreateUser());
			}
			this.cmbType.addItem(new ControlKeyValue(0 + "", ResourceUtil.srcStr(StringKeysObj.STRING_ALL), ""));
			super.getComboBoxDataUtil().comboBoxData(this.cmbType, "PROTECTTYPE");//连接数据库获得类型选择框
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbType, this.tunnelFilterCondition.getTunnelType());
			//状态下拉列表
			this.cmbStatus.addItem(new ControlKeyValue(0 + "", ResourceUtil.srcStr(StringKeysObj.STRING_ALL), ""));
			this.cmbStatus.addItem(new ControlKeyValue(EActiveStatus.ACTIVITY.getValue() + "", ResourceUtil.srcStr(StringKeysObj.ACTIVITY_YES), ""));
			this.cmbStatus.addItem(new ControlKeyValue(EActiveStatus.UNACTIVITY.getValue() + "", ResourceUtil.srcStr(StringKeysObj.ACTIVITY_NO), ""));
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbStatus, (this.tunnelFilterCondition.getTunnelStatus()+""));
			if (isSingle == 1) {
				setAZNetComboBox();//AZ端下拉列表设置
			}else{
				setSingleRoleComboBox();//单网元侧下拉列表
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
				if (null != tunnelFilterCondition) {
					tunnelFilterCondition.setTunnelStatus(Integer.parseInt(((ControlKeyValue) cmbStatus.getSelectedItem()).getId()));//状态
					tunnelFilterCondition.setTunnelType(((ControlKeyValue) cmbType.getSelectedItem()).getId());//类型
					tunnelFilterCondition.setTunnelName(txtName.getText());//tunnel名称
					if (isSingle == 1) {
						//将选择的下拉列表存储到记录中
						tunnelFilterCondition.setASiteId(Integer.parseInt(((ControlKeyValue) cmbASite.getSelectedItem()).getId()));//a端网元
						tunnelFilterCondition.setZSiteId(Integer.parseInt(((ControlKeyValue) cmbZSite.getSelectedItem()).getId()));//z端网元
						tunnelFilterCondition.setCreateUser(txtFounder.getText());//创建者
						((TunnelBusinessController)((TunnelBusinessPanel)contentview).getController()).setFilterCondition(tunnelFilterCondition);
					}
					if (isSingle == 2) {
						tunnelFilterCondition.setRole(((ControlKeyValue) cmbRole.getSelectedItem()).getId());
						((TunnelNodeController)((TunnelPanel)contentview).getController()).setFilterCondition(tunnelFilterCondition);
					}
				}
				try {
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
				if (isSingle == 1) {
					cmbASite.setSelectedIndex(0);
					cmbZSite.setSelectedIndex(0);
					txtFounder.setText("");
					((TunnelBusinessController)((TunnelBusinessPanel)contentview).getController()).setFilterCondition(null);
				}else {
					cmbRole.setSelectedIndex(0);
					((TunnelNodeController)((TunnelPanel)contentview).getController()).setFilterCondition(null);
				}
				tunnelFilterCondition = null;
			}
		});
	}
	
	/**
	 * 设置按钮布局
	 */
	private void setButtonLayout() {
		GridBagLayout layout=new GridBagLayout(); 
		layout.columnWidths=new int[]{20,160,20,20};
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
		c.insets=new Insets(0,5,0,10);
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
			this.setSiteBox(this.cmbZSite);
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbASite, (this.tunnelFilterCondition.getASiteId()+""));
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbZSite, (this.tunnelFilterCondition.getZSiteId()+""));
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());		
		}
	}

	/**
	 * 单网元侧角色设置
	 */
	private void setSingleRoleComboBox(){
		try {
			this.cmbRole.addItem(new ControlKeyValue(0 + "", ResourceUtil.srcStr(StringKeysObj.STRING_ALL), ""));
			super.getComboBoxDataUtil().comboBoxData(this.cmbRole, "TUNNELROLE");
			this.cmbRole.setSelectedIndex(0);
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbRole, (this.tunnelFilterCondition.getRole()+""));
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
		SiteService_MB servicesMb = null;
		DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) comboBox.getModel();;
		try {
			servicesMb = (SiteService_MB) ConstantUtil.serviceFactory
					.newService_MB(Services.SITE);
			sites = servicesMb.select();
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
			UiUtil.closeService_MB(servicesMb);
		}
	}
}
