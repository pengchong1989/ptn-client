package com.nms.ui.ptn.safety.controller;

import java.util.List;

import com.nms.db.bean.system.loginlog.LoginLog;
import com.nms.db.bean.system.user.UserInst;
import com.nms.model.system.loginlog.LoginLogServiece_Mb;
import com.nms.model.system.user.UserInstServiece_Mb;
import com.nms.model.util.Services;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.safety.UserOnLinePanel;
/**
 * 在线用户
 * 按钮事件
 * @author sy
 *
 */
public class UserOnLinePanelController  extends AbstractController {
	private UserOnLinePanel panel;
	public UserOnLinePanelController(UserOnLinePanel userOnLinePanel) {
		this.panel = userOnLinePanel;
	}
	//刷新
	public void refresh() throws Exception {
		LoginLogServiece_Mb loginlogServiece=null;
		List<LoginLog> LoginLogList = null;
		try {
			loginlogServiece=(LoginLogServiece_Mb)ConstantUtil.serviceFactory.newService_MB(Services.LOGINLOGSERVIECE);
			LoginLogList=loginlogServiece.selectOnLine();
			this.panel.clear();
			this.panel.initData(LoginLogList);
			this.panel.updateUI();
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(loginlogServiece);
		}
	}
	/**
	 * 注销用户 
	 * @throws Exception
	 */
	public void logOut()throws Exception{
		UserInstServiece_Mb userInstServiece=null;		
		LoginLogServiece_Mb loginlogServiece = null;
		LoginLog loginlog = this.panel.getSelect();
		try {
			if(!(loginlog.getUser_name().equals(ConstantUtil.user.getUser_Name()))){
				UserInst userinst=new UserInst();
				userinst.setUser_Name(loginlog.getUser_name());
				userInstServiece = (UserInstServiece_Mb)ConstantUtil.serviceFactory.newService_MB(Services.UserInst);
				List<UserInst> userlist=userInstServiece.select(userinst);
				if(userlist.size()>0){
					UserInst userInst=userlist.get(0);
					loginlog.setUser_id(userInst.getUser_Id());
				}
				loginlogServiece = (LoginLogServiece_Mb)ConstantUtil.serviceFactory.newService_MB(Services.LOGINLOGSERVIECE);
				loginlogServiece.updateExitLoginLog(loginlog, 1);
			}else{
				DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_DEFAULTUSER));
			}
			this.refresh();
					
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(userInstServiece);
			UiUtil.closeService_MB(loginlogServiece);
		}
	}
	
}