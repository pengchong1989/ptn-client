package com.nms.ui.ptn.configinfo;

import java.util.List;

import com.nms.db.bean.alarm.WarningLevel;
import com.nms.model.alarm.WarningLevelService_MB;
import com.nms.model.util.Services;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.ptn.configinfo.dialog.UpdateAlarmDescDialog;

/**
 * 告警描述控制类
 * 
 * @author kk
 * 
 */
public class AlarmDescController extends AbstractController {

	private AlarmDescPanel alarmDescPanel = null;

	public AlarmDescController(AlarmDescPanel alarmDescPanel) {
		this.alarmDescPanel = alarmDescPanel;
	}

	@Override
	public void refresh() throws Exception {
		WarningLevelService_MB warningLevelService = null;
		List<WarningLevel> warningLevelList = null;
		try {
			warningLevelService = (WarningLevelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.WarningLevel);
			warningLevelList = warningLevelService.select();
			this.alarmDescPanel.clear();
			this.alarmDescPanel.initData(warningLevelList);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(warningLevelService);
			warningLevelList = null;
		}
	}
	
	@Override
	public void openUpdateDialog(){
		UpdateAlarmDescDialog updateAlarmDescDialog = new UpdateAlarmDescDialog(this.alarmDescPanel, true, this.alarmDescPanel.getSelect().getId());
		updateAlarmDescDialog.setLocation(UiUtil.getWindowWidth(updateAlarmDescDialog.getWidth()), UiUtil.getWindowHeight(updateAlarmDescDialog.getHeight()));
		updateAlarmDescDialog.setVisible(true);
	}

}
