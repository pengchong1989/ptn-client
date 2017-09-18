package com.nms.ui.ptn.business.dialog.loopProtect;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.CommonBean;
import com.nms.db.bean.ptn.path.protect.LoopProRotate;
import com.nms.db.bean.ptn.path.protect.LoopProtectInfo;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.path.protect.WrappingProtectService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.NeTreePanel;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

/**
 * 环网保护倒换对话框
 * @author guoqc
 */
public class LoopProRotateDialog extends PtnDialog{
	private static final long serialVersionUID = 1L;
	private LoopProtectInfo loopPro = null;// 选中的环网对象
	private LoopProRotate rotate = null;
	private List<SiteInst> siteList = new ArrayList<SiteInst>();
	private int isSingle = 0;//0/1 = 网络侧/单站侧
	
	/**
	 *  新建一个环网倒换对话框
	 *  网络层 tunnel倒换
	 *  @param loopProtectInfo
	 */
	public LoopProRotateDialog(boolean flag, LoopProtectInfo loopProtectInfo, int isSingle){
		this.loopPro = loopProtectInfo;
		rotate = new LoopProRotate();
		this.isSingle = isSingle;
		if(this.isSingle == 0){
			this.getSiteList();
		}
		this.init();
	}
	
	/**
	 * 获取当前环所包含的网元信息
	 */
	private void getSiteList() {
		if(this.loopPro != null){
			SiteService_MB siteServiceMB = null;
			try {
				List<Integer> siteIdList = new ArrayList<Integer>();
				this.getAllSiteId(false, siteIdList);
				siteServiceMB = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
				List<SiteInst> siteInstList = siteServiceMB.select();
				for (int siteId : siteIdList) {
					for (SiteInst siteInst : siteInstList) {
						if(siteInst.getSite_Inst_Id() == siteId){
							siteList.add(siteInst);
							break;
						}
					}
				}
			} catch (Exception e) {
				ExceptionManage.dispose(e,this.getClass());
			} finally {
				UiUtil.closeService_MB(siteServiceMB);
			}
		}
	}

	/**
	 * 初始化
	 */
	private void init(){
		this.setTitle(ResourceUtil.srcStr(StringKeysBtn.BTN_LOOPPRO_ROTATE));
		this.initComponent();
		this.setLayout();
		this.addListener();
		if(this.isSingle == 0){
			UiUtil.showWindow(this, 400, 500);
		}else{
			UiUtil.showWindow(this, 400, 400);
		}
	}
	
	/**
	 * 实例化组件
	 */
	private void initComponent(){
		this.clearRotate = new JRadioButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CLEARROTATE));//清除倒换
		this.clearRotate.setName("0");
		this.clearRotate.setSelected(true);
		this.lockWorkChannel = new JRadioButton(ResourceUtil.srcStr(StringKeysBtn.BTN_LOCKWORKCHANNEL));//锁定工作信道
		this.lockWorkChannel.setName("1");
		this.lockCurrState = new JRadioButton(ResourceUtil.srcStr(StringKeysBtn.BTN_LOCKCURRSTATE));//锁定当前状态
		this.lockCurrState.setName("2");
		this.forcedRotate = new JRadioButton(ResourceUtil.srcStr(StringKeysBtn.BTN_FORCEROTATE));//强制倒换
		this.forcedRotate.setName("3");
		this.manualRotate = new JRadioButton(ResourceUtil.srcStr(StringKeysBtn.BTN_MANUALROTATE));//人工倒换
		this.manualRotate.setName("4");
		this.rotateBtnGroup = new ButtonGroup();
		this.rotateBtnGroup.add(this.clearRotate);
		this.rotateBtnGroup.add(this.lockWorkChannel);
		this.rotateBtnGroup.add(this.lockCurrState);
		this.rotateBtnGroup.add(this.forcedRotate);
		this.rotateBtnGroup.add(this.manualRotate);
		this.westRotate = new JRadioButton(ResourceUtil.srcStr(StringKeysBtn.BTN_WESTROTATE));//西向倒换
		this.westRotate.setName("1");
		this.westRotate.setSelected(true);
		this.eastRotate = new JRadioButton(ResourceUtil.srcStr(StringKeysBtn.BTN_EASTROTATE));//东向倒换
		this.eastRotate.setName("2");
		this.direcBtnGroup = new ButtonGroup();
		this.direcBtnGroup.add(this.westRotate);
		this.direcBtnGroup.add(this.eastRotate);
		this.allRorate = new JCheckBox(ResourceUtil.srcStr(StringKeysBtn.BTN_ALLROTATE));//倒换所有环
		this.saveBtn = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),false,RootFactory.CORE_MANAGE);
		this.cancelBtn = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		this.buttonPanel = new JPanel();
		if(this.isSingle == 0){
			this.neTreePanel=new NeTreePanel(2, this.siteList, null, 0);
		}
	}
	
	/**
	 *  主界面布局
	 */
	private void setLayout(){
		this.setButtonLayout();
		GridBagLayout componentLayout = new GridBagLayout();
		if(this.isSingle == 0){
			componentLayout.rowHeights = new int[] {30, 30, 30, 30, 30, 30, 80, 30, 30};
			componentLayout.columnWidths = new int[] { 50, 150, 200};
		}else{
			componentLayout.rowHeights = new int[] {30, 30, 30, 30, 30, 30, 30};
			componentLayout.columnWidths = new int[] { 50, 150, 100};
		}
		componentLayout.columnWeights = new double[] { 0, 0, 0 };
		componentLayout.rowWeights = new double[] { 0.1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		this.setLayout(componentLayout);
	
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		//第一行
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.insets = new Insets(30, 10, 15, 10);
		componentLayout.setConstraints(this.clearRotate, c);
		this.add(this.clearRotate);
		c.gridx = 2;
		c.gridy = 0;
		componentLayout.setConstraints(this.lockWorkChannel, c);
		this.add(this.lockWorkChannel);
		// 第二行
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(10, 10, 15, 10);
		componentLayout.setConstraints(this.lockCurrState, c);
		this.add(this.lockCurrState);
		c.gridx = 2;
		c.gridy = 1;
		componentLayout.setConstraints(this.forcedRotate, c);
		this.add(this.forcedRotate);
		// 第三行
		c.gridx = 1;
		c.gridy = 2;
		componentLayout.setConstraints(this.manualRotate, c);
		this.add(this.manualRotate);
		// 第四行 
		c.gridx = 1;
		c.gridy = 3;
		componentLayout.setConstraints(this.westRotate, c);
		this.add(this.westRotate);
		c.gridx = 2;
		c.gridy = 3;
		componentLayout.setConstraints(this.eastRotate, c);
		this.add(this.eastRotate);
		// 第五行
		c.gridx = 1;
		c.gridy = 4;
		componentLayout.setConstraints(this.allRorate, c);
		this.add(this.allRorate);
		//第六行 
		if(this.isSingle == 0){
			c.gridx = 1;
			c.gridy = 5;
			c.gridwidth = 2;
			c.gridheight = 2;
			c.insets = new Insets(5, 5, 5, 55);
			componentLayout.setConstraints(this.neTreePanel, c);
			this.add(this.neTreePanel);
		}
		
		//第七行
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 2;
		if(this.isSingle == 0){
			c.gridy = 7;
		}else{
			c.gridy = 5;
		}
		c.gridwidth = 1;
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
				LoopProRotateDialog.this.dispose();			
			}

			@Override
			public boolean checking() {
				return false;
			}
			
		});			
	
		this.cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LoopProRotateDialog.this.dispose();
			}
		});
	}

	/**
	 * 保存按钮事件
	 */
	private void saveAction(){
		SiteService_MB siteService = null;
		try {
			LoopProRotate rotate = this.getLoopProRotate();//倒换对象
			DispatchUtil rotateDispatch = new DispatchUtil(RmiKeys.RMI_LOOPPROROTATE);
			String result = rotateDispatch.excuteUpdate(rotate);
			if(!result.equals(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))){
				result = ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_FAIL);
			}
			DialogBoxUtil.succeedDialog(this, result);
			List<Integer> siteIdList = rotate.getSiteIdList();
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			List<CommonBean> cbList = new ArrayList<CommonBean>();
			for (Integer siteId : siteIdList) {
				CommonBean cb = new CommonBean();
				cb.setPortName(siteService.getSiteName(siteId));
				cbList.add(cb);
			}
			rotate.setSiteNameListLog(cbList);
			rotate.setSiteIdList(null);
			for (Integer siteId : siteIdList) {
				AddOperateLog.insertOperLog(saveBtn, EOperationLogType.LOOPPROROTATE.getValue(), result,
						null, rotate, siteId, ResourceUtil.srcStr(StringKeysBtn.BTN_LOOPPRO_ROTATE), "loopRotate");
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
		}
	}
	
	/**
	 * 获取倒换对象
	 */
	private LoopProRotate getLoopProRotate() {
		JRadioButton radioButton = null;
		int actionType = 0;
		int direction = 1;
		try {
			//遍历所有radiobutton 获取选中的button
			Enumeration<AbstractButton> elements = this.rotateBtnGroup.getElements();
			while (elements.hasMoreElements()) {
				radioButton = (JRadioButton) elements.nextElement();
				if (radioButton.isSelected()) {
					actionType = Integer.parseInt(radioButton.getName());
					rotate.setActionTypeLog(radioButton.getText());
					break;
				}
			}
			Enumeration<AbstractButton> elements1 = this.direcBtnGroup.getElements();
			while (elements1.hasMoreElements()) {
				radioButton = (JRadioButton) elements1.nextElement();
				if (radioButton.isSelected()) {
					direction = Integer.parseInt(radioButton.getName());
					rotate.setDirectionLog(radioButton.getText());
					break;
				}
			}
			rotate.setActionType(actionType);
			rotate.setDirection(direction);
			List<Integer> siteIdList = new ArrayList<Integer>();
			if(this.isSingle == 0){
				siteIdList = this.neTreePanel.getPrimaryKeyList("site");
			}else{
				siteIdList.add(ConstantUtil.siteId);
			}
			
			if(this.allRorate.isSelected()){
				//倒换所有
				if(siteIdList.size() > 0){
					this.rotate.setSiteIdList(siteIdList);
				}else{
					this.getAllSiteId(true, this.rotate.getSiteIdList());
				}
				rotate.setRingId(255);
				rotate.setAllRotateLog(true);
			}else{
				//倒换当前环网保护
				if(siteIdList.size() > 0){
					this.rotate.setSiteIdList(siteIdList);
				}else{
					this.getAllSiteId(false, this.rotate.getSiteIdList());
				}
				rotate.setRingId(this.loopPro.getLoopId());
				rotate.setAllRotateLog(false);
			}
		}catch(Exception e){
			ExceptionManage.dispose(e, this.getClass());
		}
		return rotate;
	}

	private void getAllSiteId(boolean flag, List<Integer> siteIdList) throws Exception {
		WrappingProtectService_MB protectService = (WrappingProtectService_MB) ConstantUtil.serviceFactory.newService_MB(Services.WRAPPINGPROTECT);
		try {
			LoopProtectInfo loopProtectInfo_select = new LoopProtectInfo();
			List<LoopProtectInfo> loopProList = protectService.select(loopProtectInfo_select);
			if(flag){
				//获取所有网元
				for (LoopProtectInfo l : loopProList) {
					if(!siteIdList.contains(l.getSiteId())){
						siteIdList.add(l.getSiteId());
					}
				}
			}else{
				//获取当前环网保护所包含的网元
				loopProtectInfo_select.setLoopId(this.loopPro.getLoopId());
				for (LoopProtectInfo l : loopProList) {
					if(!siteIdList.contains(l.getSiteId())){
						siteIdList.add(l.getSiteId());
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(protectService);
		}
	}

	private JRadioButton clearRotate;//清除倒换
	private JRadioButton lockWorkChannel;//锁定工作信道
	private JRadioButton lockCurrState;//锁定当前状态
	private JRadioButton forcedRotate;//强制倒换
	private JRadioButton manualRotate;//人工倒换
	private ButtonGroup rotateBtnGroup;
	private JRadioButton westRotate;//西向倒换
	private JRadioButton eastRotate;//东向倒换
	private ButtonGroup direcBtnGroup;
	private JCheckBox allRorate;//倒换所有环
	private PtnButton saveBtn;//确认
	private JButton cancelBtn;//取消
	private JPanel buttonPanel;
	private NeTreePanel neTreePanel;
}