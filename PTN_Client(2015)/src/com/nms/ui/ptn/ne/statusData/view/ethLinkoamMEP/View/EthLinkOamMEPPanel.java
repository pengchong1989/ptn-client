package com.nms.ui.ptn.ne.statusData.view.ethLinkoamMEP.View;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.oamStatus.OamMepInst;
import com.nms.db.bean.ptn.oamStatus.OamStatusInfo;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.shlef.SiteService_MB;
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
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;

/**
 * 
 * @author zk
 *function：接入链路以太网oam对端MEP信息 的界面
 */
public class EthLinkOamMEPPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2698407911478544934L;
	private JLabel mepIDLabel;//对端MEP ID
	private JTextField mepIDField;
	private JLabel mepMacLabel; //对端MEP MAC
	private JTextField mepMacField;
	private JLabel mdNameLabel;//MD name
	private JTextField mdNameFiled;
	private JLabel mdLevelLabel;//MD Level
	private JTextField mdLevelField;
	private JLabel maNameLabel;//MA name
	private JTextField maNameField;
	private JLabel id ; 
	private JTextField idTextField ;
	private PtnButton findButton;
	private JPanel centerJPanel;
	private JPanel buttonJpanel;
	
	public EthLinkOamMEPPanel(){
		init();
		setsetLayout();
		addListener();
	}
	private  void init() {
		
		this.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.ETHOAMMEPINFO))); 
		mepIDLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MEPIP));
		mepIDField = new JTextField();
		mepIDField.setEditable(false);
		
		mepMacLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.ETHOAMMEPMAC)); 
        mepMacField = new JTextField();
        mepMacField.setEditable(false);
        mdNameLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MDNAME));
		mdNameFiled = new JTextField();
		mdNameFiled.setEditable(false);
		
		mdLevelLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MDLEVEL));
		mdLevelField = new JTextField();
		mdLevelField.setEditable(false);
		
		maNameLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MANAME));
		maNameField = new JTextField();
		maNameField.setEditable(false);
			
		id = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NMUBER_ID));
		idTextField = new JTextField();
		idTextField.setText(1+"");
		
		findButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SELECT),true);
		centerJPanel = new JPanel();
		buttonJpanel = new JPanel();
	}
	
	private void setsetLayout() {
			//主面板
			GridBagConstraints	gridBagConstraints = new GridBagConstraints();
			GridBagLayout	gridBagLayout = new GridBagLayout();
			gridBagLayout.rowHeights = new int[]{100,60,100};
			gridBagLayout.rowWeights = new double[]{0,0.1};
			gridBagLayout.columnWidths = new int[]{300};
			gridBagLayout.columnWeights = new double[]{1};
			this.setLayout(gridBagLayout);
			addComponent(this, centerJPanel, 0, 1, 0, 0, 1, 1, GridBagConstraints.BOTH, new Insets(5,5,5,5), GridBagConstraints.CENTER, gridBagConstraints);
			addComponent(this, buttonJpanel, 0, 2, 0, 0, 1, 1, GridBagConstraints.BOTH, new Insets(5,5,5,5), GridBagConstraints.CENTER, gridBagConstraints);
	       //centerJpanel 面板布局
			gridBagLayout = new GridBagLayout();
			centerJPanel.setLayout(gridBagLayout);
			centerJPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.ETHOAMMEPINFO)));
			Insets insert = new Insets(0, 50, 10, 20); 
			addComponent(centerJPanel, mepIDLabel, 0, 0, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
			addComponent(centerJPanel, mepIDField, 1, 0, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
			addComponent(centerJPanel, mepMacLabel, 2, 0,0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
			addComponent(centerJPanel, mepMacField, 3, 0, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
			addComponent(centerJPanel, mdNameLabel, 0, 1, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
			addComponent(centerJPanel, mdNameFiled, 1, 1, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
			addComponent(centerJPanel, mdLevelLabel, 2, 1, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
			addComponent(centerJPanel, mdLevelField, 3, 1, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
			addComponent(centerJPanel, maNameLabel, 0, 2, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
			addComponent(centerJPanel, maNameField, 1, 2, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
			addComponent(centerJPanel, id, 2, 2, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
			addComponent(centerJPanel, idTextField, 3, 2, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		// 按钮布局
			GridBagLayout buttonLayout = new GridBagLayout();
			buttonLayout.columnWidths = new int[]{60,60,60,6};
			buttonLayout.columnWeights = new double[]{1.0,0,0,0};
			buttonLayout.rowHeights = new int[]{60};
			buttonLayout.rowWeights = new double[]{1};
			buttonJpanel.setLayout(buttonLayout);
			addComponent(buttonJpanel, findButton, 2, 0, 0, 0, 1, 1, GridBagConstraints.WEST, new Insets(5,5,5,20), GridBagConstraints.WEST, gridBagConstraints);
			
	}

	private void addComponent(JPanel panel, JComponent component, int gridx, int gridy, double weightx, double weighty, 
			                   int gridwidth, int gridheight,int fill, Insets insets, int anchor,GridBagConstraints gridBagConstraints){
		
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;
		gridBagConstraints.weightx = weightx;
		gridBagConstraints.weighty = weighty;
		gridBagConstraints.gridwidth = gridwidth;
		gridBagConstraints.gridheight = gridheight;
		gridBagConstraints.fill = fill;
		gridBagConstraints.insets = insets;
		gridBagConstraints.anchor = anchor;
		panel.add(component, gridBagConstraints);
	}
	private void addListener() {
		// functin:条目数ID文本框增加监听事件
		idTextField.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				//区间是否在1-6之间
				String message = ResourceUtil.srcStr(StringKeysLbl.LBL_PARMATE_LABEL);
				 if( !idTextField.getText().trim().equals("") && idTextField.getText().trim().length() < 8){
					 int inputNumber = Integer.parseInt(idTextField.getText().trim());
					 if(inputNumber <1 || inputNumber >26){
						 JOptionPane.showMessageDialog(null, message);
						 idTextField.setText("1");
					 }
				 }else{
					 JOptionPane.showMessageDialog(null, message);
						 idTextField.setText("1");
					 }
				 }
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
			}
		});
		
		//确定按钮事件
		findButton.addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SiteService_MB siteServie = null;
				try {
					SiteInst siteInfo = new SiteInst();
					siteServie = (SiteService_MB)ConstantUtil.serviceFactory.newService_MB(Services.SITE);
					siteInfo = siteServie.select(ConstantUtil.siteId);
					siteInfo.setStatusMark(57);
					siteInfo.setParameter(Integer.parseInt(idTextField.getText().trim()));
					
					DispatchUtil siteDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
					OamStatusInfo oamStatusInfo	= siteDispatch.oamStatus(siteInfo);
					
				    if(oamStatusInfo != null && oamStatusInfo.getOamMepInst() != null ){
				    	refresh(oamStatusInfo.getOamMepInst());
				    	DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
				    	this.insertOpeLog(EOperationLogType.ETHLINKOAMMEP.getValue(),ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS), null,null);
				    }else{
						DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_FIND_DETAIL_FAIL));
						this.insertOpeLog(EOperationLogType.ETHLINKOAMMEP.getValue(), ResourceUtil.srcStr(StringKeysTip.TIP_FIND_DETAIL_FAIL), null,null);
				    }
				} catch (Exception e1) {
					ExceptionManage.dispose(e1,this.getClass());
				} finally {
					UiUtil.closeService_MB(siteServie);
				}
			}

			private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
			   AddOperateLog.insertOperLog(findButton, operationType, result, oldMac, newMac, ConstantUtil.siteId,ResourceUtil.srcStr(StringKeysLbl.ETHOAMMEPINFO),"");
			}
			
			@Override
			public boolean checking() {
				return true;
			}
		});
	}
	/**
	 *function:为界面赋值
	 * 
	 */
	private void refresh(OamMepInst oamMepInst) {
		
		try {
			//对端MEP ID
			mepIDField.setText(oamMepInst.getMepId());
			//对端MEP MAC
			mepMacField.setText(oamMepInst.getMepMac());
			//MD name
			mdNameFiled.setText(oamMepInst.getMdName());
			//MD Level
			mdLevelField.setText(oamMepInst.getMdLevel());
			//MA name
			maNameField.setText(oamMepInst.getMaName());
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		
	}
}
