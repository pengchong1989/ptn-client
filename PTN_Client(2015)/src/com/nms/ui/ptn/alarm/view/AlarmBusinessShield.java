package com.nms.ui.ptn.alarm.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.nms.db.bean.alarm.AlarmShieldInfo;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.ResultString;
import com.nms.service.impl.util.SiteUtil;
import com.nms.service.impl.util.WhImplUtil;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.NeTreePanel;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;

public class AlarmBusinessShield extends AlarmShield{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JSplitPane splitPane;
	private NeTreePanel leftPane = null;
	private JPanel rightPane;
	private JScrollPane scrollPane;
	
	public AlarmBusinessShield() {
		init();
		setLayout();
		addListention();
		//initData();
	}
	public void initData(){
		try {
			if (!this.leftPane.verifySelect()) {
				DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_MONITORING_OBJ));
				return ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void init() {
		leftPane = new NeTreePanel(true,2,null,false);
		super.setNeTreePanel(leftPane);
		super.initComponents();
		super.setLayout();
		rightPane = super.getContentPanel();
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(rightPane);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		//splitPane.setOneTouchExpandable(true);
		splitPane.setLeftComponent(leftPane);
		splitPane.setRightComponent(rightPane);
	}
	
	public void setLayout() {
		GridBagLayout panelLayout = new GridBagLayout();
		this.setLayout(panelLayout);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		panelLayout.setConstraints(splitPane, c);
		this.add(splitPane);	
	}
	
	private void addListention() {
		super.getConfirmButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (leftPane.getSelectSiteInst().size() == 0) {
						DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_SITE));
					}else{
						siteConfirmListener();					
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}		
			}
		}) ;
		
		super.getShieldModelCombox().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		      if(getShieldModelCombox().getSelectedIndex() == 1){
		    	  getAlarmLineJpanel().newAlarmCodeLevel(2);
		      }else if(getShieldModelCombox().getSelectedIndex() == 2){
		    	  getAlarmLineJpanel().newLineCodeLevel(3);
		      }else{
		    	  getAlarmLineJpanel().newAlarmLevel(1);		    	  
		      }
			}
		});

		super.getClearAlarm().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (!leftPane.verifySelect()) {
						DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_MONITORING_OBJ));
					}else{
						siteClearAlarmListener();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}				
			}			
		});
		
		//根据选择的网元数目来决定告警屏蔽的方式
		leftPane.getTree().addTreeNodeClickedActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
//					if(leftPane.getSelectSiteInst().size() == 1){
//						getShieldModelCombox().setEnabled(true);
//						if(getShieldModelCombox().getSelectedIndex() == 0){
//							getAlarmLineJpanel().newAlarmLevel(1);
//						}			
//					}else{
//						getShieldModelCombox().setSelectedIndex(1);
//						getAlarmLineJpanel().newAlarmCodeLevel(2);
//						 getShieldModelCombox().setEnabled(false);
//						//alarmLineJpanel.gettDataBox().clear();
//					}
					if(getShieldModelCombox().getSelectedIndex() == 1){
				    	  getAlarmLineJpanel().newAlarmCodeLevel(2);
				      }else if(getShieldModelCombox().getSelectedIndex() == 2){
				    	  getAlarmLineJpanel().newLineCodeLevel(3);
				      }else{
				    	  getAlarmLineJpanel().newAlarmLevel(1);		    	  
				      }
				} catch (Exception e1) {
					e1.printStackTrace();
				}			
			}
		});
	}
	
	private void siteConfirmListener() {
		SiteUtil siteUtil = new SiteUtil();			
		AlarmShieldInfo alarmShieldInfo = new AlarmShieldInfo();
	    DispatchUtil alarmDispatch = null;
	    String result = "";	
	    String str = "";
	    String str1 = "";	
	    String str2 = "";	
		List<Integer> siteIds = new ArrayList<Integer>();
		List<Integer> siteIds1 = new ArrayList<Integer>(); //返回成功
		List<Integer> siteIds2 = new ArrayList<Integer>(); //返回失败
		try {
			for(SiteInst siteInst : leftPane.getSelectSiteInst()){
				//判断是否是非在线网元
				if(1==siteUtil.SiteTypeUtil(siteInst.getSite_Inst_Id())){			
					siteIds.add(siteInst.getSite_Inst_Id());
				}else{
					//屏蔽所有模式
					if(getShieldAllAlarm().getSelectedIndex()== 1 || getShieldAllAlarm().getSelectedIndex() ==2){					
						result=clearAllAlarm(getShieldAllAlarm().getSelectedIndex(),1,siteInst.getSite_Inst_Id());
						String type = "";
						if(getShieldAllAlarm().getSelectedIndex()== 1){
							type = ResourceUtil.srcStr(StringKeysObj.ALARM_SHILEDALLALARM_MODEL);
						}else if(getShieldAllAlarm().getSelectedIndex()== 2){
							type = ResourceUtil.srcStr(StringKeysObj.ALARM_SHILEDALLLINE_MODEL);
						}
						if(result.equals(ResultString.CONFIG_SUCCESS)){
							siteIds1.add(siteInst.getSite_Inst_Id());
						}else{
							siteIds2.add(siteInst.getSite_Inst_Id());
						}
						AddOperateLog.insertOperLog(super.getConfirmButton(), EOperationLogType.ALARMSHIELD.getValue(), result, null, null, 
								siteInst.getSite_Inst_Id(),type, "");
					}else{
						//屏蔽指定告警
						String type = "";
						alarmDispatch = new DispatchUtil(RmiKeys.RMI_ALARM);
						alarmShieldInfo = getAlarmLineJpanel().getAlarmShieldInfo(siteInst,type);
						alarmShieldInfo.setSiteId(siteInst.getSite_Inst_Id());	
						result = alarmDispatch.alarmShield(alarmShieldInfo); 
						if(result.equals(ResultString.CONFIG_SUCCESS)){
							siteIds1.add(siteInst.getSite_Inst_Id());
						}else{
							siteIds2.add(siteInst.getSite_Inst_Id());
						}
						AddOperateLog.insertOperLog(super.getConfirmButton(), EOperationLogType.ALARMSHIELD.getValue(), result, null, alarmShieldInfo.getAlarmShieldTypeList().get(0), 
								siteInst.getSite_Inst_Id(),ResourceUtil.srcStr(StringKeysObj.NET_BASE), "AlarmShieldInfo_t");
					}	
				}
			}
			WhImplUtil wu = new WhImplUtil();
			//根据返回结果来显示提示内容
			if(null != siteIds && siteIds.size() > 0){				
				str=ResultString.CONFIG_SUCCESS+","+wu.getNeNames(siteIds)+ResultString.NOT_ONLINE_SUCCESS;
			}
			if(null != siteIds1 && siteIds1.size() > 0){				
				str1=wu.getNeName(siteIds1)+"号网元"+ResultString.CONFIG_SUCCESS;
			}
			if(null != siteIds2 && siteIds2.size() > 0){				
				str2=wu.getNeName(siteIds2)+"号网元"+ResultString.CONFIG_FAILED;
			}
			DialogBoxUtil.succeedDialog(null, str1+" "+str2+"\n"+str);
		} catch (Exception e1) {
			e1.printStackTrace();
		}finally{			
			alarmDispatch = null;
			siteUtil = null;
		}	
	}
	
	private void siteClearAlarmListener() {
		String result = "";	
		String result1 = "";
		SiteService_MB siteService = null;
		List<Integer> siteIds = null;
		siteIds = new ArrayList<Integer>();
		try {
			if(!leftPane.getSelectSiteInst().isEmpty() && null != leftPane.getSelectSiteInst()){
				for(SiteInst siteInst : leftPane.getSelectSiteInst()){
					result=clearAllAlarm(1,0,siteInst.getSite_Inst_Id());
					result1=clearAllAlarm(2,0,siteInst.getSite_Inst_Id());
					if(result.equals(result1) && result.equals(ResultString.CONFIG_SUCCESS)){
						siteIds.add(siteInst.getSite_Inst_Id());
					}else{
						siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
						siteInst=siteService.selectById(siteInst.getSite_Inst_Id());
						DialogBoxUtil.succeedDialog(null, siteInst.getCellId()+ResultString.CONFIG_FAILED);
					}
					AddOperateLog.insertOperLog(super.getClearAlarm(), EOperationLogType.ALARMSHIELD.getValue(), result, null, null, siteInst.getSite_Inst_Id(),
							ResourceUtil.srcStr(StringKeysObj.ALARM_CLEARALARM_MODEL), "");
				}
				if(null != siteIds){
					WhImplUtil wu = new WhImplUtil();
					String str=wu.getNeName(siteIds);
					DialogBoxUtil.succeedDialog(null, str+"号网元"+ResultString.CONFIG_SUCCESS);
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_MONITORING_OBJ));
		}finally{			
			UiUtil.closeService_MB(siteService);
			siteIds = null;
		}
	}
}
