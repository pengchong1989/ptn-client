package com.nms.ui.manager;


import javax.swing.JButton;

import com.nms.db.bean.system.OperationLog;
import com.nms.model.system.OperationLogService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysTip;

/**
 * 添加 操作日志
 * @author sy
 *
 */
public class AddOperateLog {
	/**
	 * 添加 操作日志  方法
	 * 				注：操作按钮监听必须 是      new MyActionListener()
	 * 						确保先执行此次操作，在将操作类型返回 父类（PtnButton）
	 * 									最后执行 PtnButton的 按钮监听，实现添加操作日志
	 * 		按钮监听    另外继承 方法@Override
			public boolean checking() {  （返回     ） return   true}  
	 * 		否则 	
	 * 	 		报错或者添加日志不成功
	 * @param ptnButton
	 * 			操作按钮（PtnButton ）
	 * @param operationKey
	 * 				操作类型
	 * 		向枚举类EOperationLogType  添加 操作类型（自己定义）
	 * 		列：EOperationLogType.ACDELETE.getValue()
	 * @param   String result
	 * 		此次操作 返回类型,如果不知，则result=  null 认为是 此次操作成功
	 * 			配置成功     ：成功   
	 * 				其他： 失败
	 */
	public static void insertOperLog(PtnButton ptnButton,int operateKey,String result){
		ptnButton.setOperateKey(operateKey);
		int operationResult=0;
		//  操作   结果   是否 为   ”配置成功“
		if(result==null||result.contains(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))){
			//成功
			operationResult=1;
		}else{
			//失败
			operationResult=2;
		}
		ptnButton.setResult(operationResult);
	}
	/**
	 * 自定义按钮或者 由右键等   引发的操作  
	 * 				添加 日志记录
	 * @param operateKey
	 * 		操作类型
	 * 
	 * 	列	：EOperationLogType.ACDELETE.getValue()
	 * @param operationKey
	 * 		操作 返回结果 
	 * 		配置成功或者为 null  
	 * 		 :  成功 --
	 * 		： 其他信息 为失败
	 */
	public static void insertOperate(int operateKey,String operationKey){
		OperationLogService_MB operationService=null;
		OperationLog operationLog=null;
		try {			
			// 记录操作日志
			operationLog=new OperationLog();
			operationService=(OperationLogService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OPERATIONLOGSERVIECE);
			operationLog.setOverTime(DateUtil.getDate(DateUtil.FULLTIME));
			operationLog.setUser_id(ConstantUtil.user.getUser_Id());
			if(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS).equals(operationKey)||operationKey==null){
				operationLog.setOperationResult(1);
			}else{
				operationLog.setOperationResult(2);
			}
			operationLog.setOperationType(operateKey);
			
			operationService.insertOperationLog(operationLog);
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,AddOperateLog.class);
		}finally{
			UiUtil.closeService_MB(operationService);
		}
	}
	
	/**
	 * 添加操作日志记录
	 * 将操作前和操作后的对象数据做对比
	 * @param ptnButton
	 * @param operateKey 操作类型
	 * 	      EOperationLogType.ACDELETE.getValue()
	 * @param result 操作 返回结果 
	 * 		     配置成功或者为 null  
	 * 		  :  成功 --
	 * 		      ： 其他信息 为失败
	 * @param object_before 操作前数据
	 * @param object_after 操作后数据
	 * @param siteId 网元id
	 * @param operationObjName 操作对象
	 * @param fileName 显示日志时需要加载的配置文件
	 */
	public static void insertOperLog(JButton ptnButton, int operateKey, String result, Object object_before, 
			Object object_after, int siteId, String operationObjName, String fileName) {
//		if(ptnButton != null){
//			ptnButton.setOperateKey(operateKey);
//		}
//		int operationResult=0;
//		//  操作   结果   是否 为   ”配置成功“
//		if(result==null||result.contains(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))){
//			//成功
//			operationResult=1;
//		}else{
//			//失败
//			operationResult=2;
//		}
//		if(ptnButton != null)
//		ptnButton.setResult(operationResult);
		 
		OperationLogService_MB operationService=null;
		OperationLog operationLog=null;
		try {			
			// 记录操作日志
			operationLog=new OperationLog();
			operationService=(OperationLogService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OPERATIONLOGSERVIECE);
			operationLog.setOperationObjName(operationObjName+","+fileName);
			operationLog.setSiteId(siteId);
			operationLog.setOverTime(DateUtil.getDate(DateUtil.FULLTIME));
			operationLog.setUser_id(ConstantUtil.user.getUser_Id());
			if(result==null || result.contains(ResourceUtil.srcStr(StringKeysBtn.BTN_EXPORT_ISUCCESS))){
				operationLog.setOperationResult(1);
			}else{
				operationLog.setOperationResult(2);
			}
			operationLog.setOperationType(operateKey);
			if(object_after != null){
				operationLog.setDataLogList(UiUtil.compare(object_before, object_after, operationLog.getDataLogList(), fileName));
			}
			if(fileName != null && fileName.equals("e1Port")){
				if(operationLog.getDataLogList().size() > 0)
				operationService.insertOperationLog(operationLog);
			}else{
				operationService.insertOperationLog(operationLog);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,AddOperateLog.class);
		}finally{
			UiUtil.closeService_MB(operationService);
		}
	}
}
