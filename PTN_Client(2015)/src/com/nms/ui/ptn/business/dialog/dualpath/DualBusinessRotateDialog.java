package com.nms.ui.ptn.business.dialog.dualpath;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.nms.db.bean.ptn.SiteRoate;
import com.nms.db.bean.ptn.path.eth.DualInfo;
import com.nms.db.bean.ptn.path.protect.LoopProRotate;
import com.nms.db.enums.EOperationLogType;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.business.dual.WHRoatePanel;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

/**
 *  tunnel倒换对话框（网络层，包括 单网元）
 * @author sy
 *
 */
public class DualBusinessRotateDialog extends PtnDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DualInfo dualInfo;// 选中的 dual对象
	private WHRoatePanel whPanel;//武汉 dual  倒换
	private SiteRoate Roate;//倒换对象
	private String rotateTypeLog;//倒换方式，log日志用到

	/**
	 *  新建一个新的pwProtect倒换对话框
	 * @param pwprotect
	 *     pwprotect 对象
	 */
	public DualBusinessRotateDialog(boolean flag,DualInfo dualInfo){
		this.dualInfo=dualInfo;
		init();
	}
	/*
	 * 初始化
	 */
	private void init(){
		this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_PW_PROTECT_ROTATE));
		initComponent();
		setBussinessLayout();
		addListener();
		
		if(ResourceUtil.language.equals("zh_CN")){
			UiUtil.showWindow(this, 350, 300);
		}else{
			UiUtil.showWindow(this, 600, 300);
		}
		
				
	}
	/**
	 * 实例化组件
	 */
	private void initComponent(){
		this.mainPanel=new JPanel();
		this.panelButton=new javax.swing.JPanel();
		this.whPanel=new WHRoatePanel(this.dualInfo,true);		
		this.btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),true,RootFactory.CORE_MANAGE);
		this.btnCanel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
	}
	/**
	 *  主界面布局（分网络层和单网元）
	 */
	private void setBussinessLayout(){
		this.add(this.mainPanel);
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 120};
		componentLayout.columnWeights = new double[] { 0.1 };				
		componentLayout.rowHeights = new int[] { 10,110, 10};
		componentLayout.rowWeights = new double[] {0, 0.2, 0.0 };
		this.mainPanel.setLayout(componentLayout);
		GridBagConstraints c = new GridBagConstraints();
		addComponent(this.mainPanel, this.panelButton, 0, 2, 0, 0, 2, 1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, c);
		addComPanel(c);
		setLayoutButton();
	}
	/***
	 * 按钮布局
	 */
	private void setLayoutButton() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 320, 40,40 };
		componentLayout.columnWeights = new double[] { 0.1, 0, 0 };
		componentLayout.rowHeights = new int[] { 20 };
		componentLayout.rowWeights = new double[] {0};
		this.panelButton.setLayout(componentLayout);
		GridBagConstraints c = new GridBagConstraints();		
		addComponent(this.panelButton, btnSave, 1, 0, 0, 0, 1, 1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, c);
		addComponent(this.panelButton, btnCanel, 2, 0, 0, 0, 1, 1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, c);
	}
	/**
	 * 向主面板中添加Panel
	 *    承载主界面布局，添加组件
	 */
	private void addComPanel(GridBagConstraints c){
		
	addComponent(this.mainPanel, this.whPanel, 0, 1, 0.1, 0.1, 1,1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5),GridBagConstraints.CENTER, c);
	
	}
	/**
	 * 添加事件
	 */
	private void addListener(){
		/*
		 * 保存事件
		 */
		this.btnSave.addActionListener(new MyActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAction();
				DualBusinessRotateDialog.this.dispose();			
			}
			
			@Override
			public boolean checking() {
				return true;
			}
		});			
		//取消事件	
		this.btnCanel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				DualBusinessRotateDialog.this.dispose();
				
			}
		});
	}

	/**
	 * 保存按钮
	 *   提取数据
	 */
	private void saveAction(){
		String result="";
		DispatchUtil protectRotateDispatch = null;
		List<SiteRoate> siteRoateList=null;
		try {
			protectRotateDispatch = new DispatchUtil(RmiKeys.RMI_PROTECTROTATE);
			siteRoateList=new ArrayList<SiteRoate>();
					whAction(this.whPanel,false);
		
			if(this.Roate!=null){
				siteRoateList.add(this.Roate);
			}
			result=protectRotateDispatch.excuteUpdate(siteRoateList);
			LoopProRotate log = new LoopProRotate();
			log.setActionTypeLog(this.rotateTypeLog);
			log.setAllRotateLog(this.whPanel.getSiteRorate().isSelected());
			AddOperateLog.insertOperLog(this.btnSave, EOperationLogType.DUALROTATE.getValue(), result,
					null, log, this.dualInfo.getPwProtect().getSiteId(), this.dualInfo.getName(), "dualRotate");
			DialogBoxUtil.succeedDialog(null, result);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
		}
	}
	
	/**
	 * 武汉倒换：提取数据
	 * @param whPanel
	 * @return result
	 *  返回 ，武汉倒换的操作结果
	 */
	private String whAction(WHRoatePanel whPanel,boolean node) {		
		Enumeration<AbstractButton> elements = null;
		JRadioButton radioButton = null;
		String result = "";
		try {
			// 遍历所有radiobutton 获取选中的button
			elements = whPanel.getButtonGroup().getElements();
			while (elements.hasMoreElements()) {
				radioButton = (JRadioButton) elements.nextElement();
				if (radioButton.isSelected()) {
					this.rotateTypeLog = radioButton.getText();
					break;
				}
			}
			int ratate=0;
			if (null != radioButton) {
				ratate = Integer.parseInt(radioButton.getName());				
				this.Roate=new SiteRoate();
				this.Roate.setRoate(ratate);
				this.Roate.setType("pw");
				this.Roate.setSiteRoate(whPanel.getSiteRorate().isSelected()==true?1:0);
				this.Roate.setTypeId(this.dualInfo.getPwProtect().getId());
				this.Roate.setSiteId(this.dualInfo.getPwProtect().getSiteId());
				this.Roate.setDualInfo(this.dualInfo);
			}
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			elements = null;
			radioButton = null;
		}
		return result;
	}
	private JPanel mainPanel;//主界面面板
	private JPanel panelButton;// 按钮 面板
	private PtnButton btnSave;//确认
	private JButton btnCanel;//取消
}
