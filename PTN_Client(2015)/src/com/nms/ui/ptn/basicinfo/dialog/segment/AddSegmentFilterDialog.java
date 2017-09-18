package com.nms.ui.ptn.basicinfo.dialog.segment;

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
import com.nms.db.bean.path.Segment;
import com.nms.db.bean.system.code.Code;
import com.nms.db.bean.system.code.CodeGroup;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.manager.AddOperateLog;
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
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.ptn.basicinfo.dialog.segment.controller.SegmentPanelController;

/**
 * 段的过滤查询
 * @author Administrator
 *
 */
public class AddSegmentFilterDialog extends PtnDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2152480969696186416L;
	private JLabel lblname;//名称
	private PtnTextField txtName;//名称输入文本框
	private PtnButton btnConfirm;//确认按钮
	private JButton btnCancel;//取消
	private PtnButton btnClear;//清除
	private JPanel butttonPanel;//按钮的布局
	private JLabel lblASite;//A端网元
	private JLabel lblZSite;//Z端网元
	private JComboBox cmbASite;//a端网元下拉列表
	private JComboBox cmbZSite;//z端网元下拉列表
	private JLabel lblFounder;//创建人
	private PtnTextField txtFounder;//创建人输入
	private JLabel speedJLabel;//速率
	private JComboBox speedComboBox;
	private Segment segmentFilterCondition;
	private SegmentPanelController segmentPanelController;
	private AddSegmentFilterDialog addSegmentFilterDialog;
	public AddSegmentFilterDialog(Segment segmentFilterCondition,SegmentPanelController segmentPanelController) {
		addSegmentFilterDialog = this;
		this.segmentFilterCondition = segmentFilterCondition;
		this.segmentPanelController = segmentPanelController;
		try {
			this.initCompompoments();//组件初始化
			this.setLayout();//布局设置
			this.addListener();//监听事件
			this.initDate();//下拉列表等数据初始化
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		
	}
	
	private void initCompompoments() throws Exception {
		this.setTitle(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER));//dialog名称
		this.btnClear=new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER_CLEAR));//清除
		this.btnCancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));//取消键
		this.btnConfirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),false);//确定
		this.butttonPanel = new JPanel();
		
		this.lblname = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAME));//名称
		this.txtName=new PtnTextField(false,50,new JLabel(),this.btnConfirm,this);
		this.lblFounder = new JLabel(ResourceUtil.srcStr(StringKeysObj.STRING_FOUNDER));//创建人
		this.txtFounder = new PtnTextField(false,50,new JLabel(),this.btnConfirm,this);
		this.lblASite = new JLabel(ResourceUtil.srcStr(StringKeysObj.STRING_A_SITE_NAME));//a端网元
		this.cmbASite = new JComboBox();
		this.lblZSite = new JLabel(ResourceUtil.srcStr(StringKeysObj.STRING_Z_SITE_NAME)); //z端网元
		this.cmbZSite=new JComboBox();
		this.speedJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SET_RATE));//速率
		this.speedComboBox = new JComboBox();
		
	}
	private void setLayout() {
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
		//1 名称
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight=1;
		c.gridwidth=1;
		layout.setConstraints(this.lblname, c);
		this.add(this.lblname);
		//名称输入
		c.gridx=1;
		c.gridwidth=1;
		layout.setConstraints(this.txtName, c);
		this.add(this.txtName);
		
		//A端名称
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight=1;
		c.gridwidth=1;
		layout.setConstraints(this.lblASite, c);
		this.add(this.lblASite);
		c.gridx=1;
		c.gridwidth=1;
		layout.setConstraints(this.cmbASite, c);
		this.add(this.cmbASite);
		
		//Z端名称
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight=1;
		c.gridwidth=1;
		layout.setConstraints(this.lblZSite, c);
		this.add(this.lblZSite);
		c.gridx=1;
		c.gridwidth=1;
		layout.setConstraints(this.cmbZSite, c);
		this.add(this.cmbZSite);
		
		//速率
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight=1;
		c.gridwidth=1;
		layout.setConstraints(this.speedJLabel, c);
		this.add(this.speedJLabel);
		c.gridx=1;
		c.gridwidth=1;
		layout.setConstraints(this.speedComboBox, c);
		this.add(this.speedComboBox);
		
		//创建人
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight=1;
		c.gridwidth=1;
		layout.setConstraints(this.lblFounder, c);
		this.add(this.lblFounder);
		c.gridx=1;
		c.gridwidth=1;
		layout.setConstraints(this.txtFounder, c);
		this.add(this.txtFounder);
		
		//按钮
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight=1;
		c.gridwidth=2;
		layout.setConstraints(this.butttonPanel, c);
		this.add(this.butttonPanel);
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
	private void addListener() {
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addSegmentFilterDialog.dispose();
			}
		});
		
		btnClear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				txtName.setText("");
				cmbASite.setSelectedIndex(0);
				cmbZSite.setSelectedIndex(0);
				speedComboBox.setSelectedIndex(0);
				txtFounder.setText("");
				//segmentFilterCondition = new Segment();
				segmentFilterCondition.setNAME(null);
				segmentFilterCondition.setASITEID(0);
				segmentFilterCondition.setZSITEID(0);
				segmentFilterCondition.setSpeedSegment(null);
				segmentFilterCondition.setCREATUSER(null);
			}
		});
		
		btnConfirm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				confirmActionPerformed();
			}
		});
	}
	
	private void confirmActionPerformed(){
		//名称
		if(txtName.getText().length()>0){
			segmentFilterCondition.setNAME(txtName.getText());
		}
		//A端
		ControlKeyValue asite = (ControlKeyValue) cmbASite.getSelectedItem();
		if(!"0".equals(asite.getId())){
			SiteInst asiteInst = (SiteInst) asite.getObject();
			segmentFilterCondition.setASITEID(asiteInst.getSite_Inst_Id());
		}else{
			segmentFilterCondition.setASITEID(0);
		}
		//Z端
		ControlKeyValue zsite = (ControlKeyValue) cmbZSite.getSelectedItem();
		if(!"0".equals(zsite.getId())){
			SiteInst ZsiteInst = (SiteInst) zsite.getObject();
			segmentFilterCondition.setZSITEID(ZsiteInst.getSite_Inst_Id());
		}else{
			segmentFilterCondition.setZSITEID(0);
		}
		//速率
		ControlKeyValue speed = (ControlKeyValue) speedComboBox.getSelectedItem();
		if(speedComboBox.getSelectedIndex()>0){
			segmentFilterCondition.setSpeedSegment(speed.getId());
		}else{
			segmentFilterCondition.setSpeedSegment("");
		}
		//创建人
		if(txtFounder.getText() != null && txtFounder.getText().length()>0){
			segmentFilterCondition.setCREATUSER(txtFounder.getText());
		}
		this.dispose();
		try {
			segmentPanelController.refresh();
			this.insertOpeLog(EOperationLogType.SEGMENTSELECT.getValue(), ResultString.CONFIG_SUCCESS, null, null);			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		AddOperateLog.insertOperLog(btnConfirm, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysTab.TAB_SEGMENT),"");		
	}
	
	private void initDate() {
		setSiteBox(cmbASite);
		setSiteBox(cmbZSite);
		CodeGroup codeGroup = null;
		List<Code> codeList = null;
		try {
			codeGroup = UiUtil.getCodeGroupByIdentity("WORKMODE");
			codeList = codeGroup.getCodeList();
			this.speedComboBox.addItem(new ControlKeyValue(0 + "", ResourceUtil.srcStr(StringKeysObj.STRING_ALL)));
			for(Code code : codeList){
				this.speedComboBox.addItem(new ControlKeyValue(code.getId() + "", code.getCodeName()));
			}
			
			//名称
			if(segmentFilterCondition.getNAME() != null){
				txtName.setText(segmentFilterCondition.getNAME());
			}
			//创建者
			if ( null != this.segmentFilterCondition.getCREATUSER()) {
				this.txtFounder.setText(this.segmentFilterCondition.getCREATUSER());
			}
			//A,Z端
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbASite, (this.segmentFilterCondition.getASITEID()+""));
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbZSite, (this.segmentFilterCondition.getZSITEID()+""));
			//速率
			super.getComboBoxDataUtil().comboBoxSelect(this.speedComboBox, (this.segmentFilterCondition.getSpeedSegment()+""));
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
			services = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
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
	
}
