package com.nms.ui.manager.control;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;

import com.nms.db.bean.system.OperationLog;
import com.nms.db.bean.system.roleManage.RoleRelevance;
import com.nms.model.system.OperationLogService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.wait.WaitSwingworker;
import com.nms.ui.ptn.safety.roleManage.RoleRoot;

/**
 * 自定义按钮
 * 
 * @author kk
 * 
 */
public class PtnButton extends JButton {
	private static final long serialVersionUID = 1L;
	/**
	 * 权限标签    new   PtnButton是  比较，查找 此角色权限存到（内存中  ，常量）；
	 */
	private int rootLabel;
	/**
	 * 是否显示等待对话框。默认为true
	 */
	private boolean isWait = true;
	OperationLog operationLog=null;
	private JDialog dialog;

	/**
	 * 记录日志时，操作的key。如果为0时。不记录日志
	 */
	private int operateKey = 0;
	/**
	 * 操作日志结果     int result= 1 成功 ，2 失败
	 * @param text
	 */
	private int result=0;
	/**
	 * 创建一个新的实例
	 * 
	 * @param text
	 *            显示文本
	 */
	public PtnButton(String text) {
		super(text);
	}
	/**
	 * 创建一个新的实例
	 * 
	 * @param text
	 *            显示文本
	 * @param isWait
	 *            是否等待
	 * @param  int rootLabel  
	 * 需要验证  权限的                >0的 整数  
	 *            传人 权限监控标签
	 */
	public PtnButton(String text, boolean isWait,int label) {
		super(text);
		this.setWait(isWait);
		this.rootLabel=label;
		this.setEnabled(this.checkRoot(label));
	}

	/**
	 * 指定进度条的所有者对话框
	 * @param text
	 * @param isWait
	 * @param label
	 * @param jDialog
	 */
	public PtnButton(String text, boolean isWait,int label,PtnDialog jDialog) {
		super(text);
		this.setWait(isWait);
		this.rootLabel=label;
		this.setEnabled(this.checkRoot(label));
		dialog = jDialog;
	}
	
	/**
	 * 创建一个新的实例
	 * @param text
	 *            显示文本
	 * @param isWait
	 *            是否等待
	 */
	public PtnButton(String text, boolean isWait) {
		super(text);
		this.setWait(isWait);
	}
	/**
	 * 创建一个新的实例
	 * 
	 * @param text
	 *            显示文本
	 * @param  int rootLabel  >0的 整数  
	 *            传人 权限监控标签
	 */
	public PtnButton(String text, int label) {
		super(text);
		this.rootLabel=label;
		this.setEnabled(this.checkRoot(label));

	}

	/**
	 * 创建一个新的实例
	 * 
	 * @param text
	 *            显示文本
	 * @param isWait
	 *            是否等待
	 * @param operateKey
	 *            操作的key 对应StringOperate.java中的常量
	 * @param  int rootLabel  >0的 整数  
	 *            传人 权限监控标签
	 */
	public PtnButton(String text, boolean isWait, int operateKey,int label) {
		super(text);
		this.setWait(isWait);
		this.rootLabel=label;
		this.operateKey=operateKey;	
		this.setEnabled(this.checkRoot(label));
	}

	/**
	 * 给按钮添加事件
	 */
	public void addActionListener(final MyActionListener actionListener) {
		final PtnButton p= this;
		super.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				WaitSwingworker waitSwingworker = null;
				operationLog = new OperationLog();
				try {
					operationLog.setStartTime(DateUtil.getDate(DateUtil.FULLTIME));
					if (isWait){		
						waitSwingworker = new WaitSwingworker(actionListener,p,dialog);		
						waitSwingworker.execute();
						}
					else {
						actionListener.actionPerformed(actionEvent);
						/**
						 * 添加日志记录
						 */
//						insertOperationLog();
					}
					
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				} finally {
					waitSwingworker = null;
				}
			}

		});
	}
	/**
	 * 权限验证
	 * @param label  标签 与 此角色的   可操作权限集合 验证
	 */
	public void root(int label){
		//默认 为false   不可操作此按钮
		boolean flag=false;
		List<RoleRelevance> roleRelevanceList=ConstantUtil.roleRelevanceList;
		if(roleRelevanceList!=null){
			for(int i=0;i<roleRelevanceList.size();i++){
				RoleRelevance roleRelevance=roleRelevanceList.get(i);
				/**
				 * 标签验证： 权限集合中有  此  label 
				 * 可以操作  
				 */
				if(label==roleRelevance.getLabel()){
					flag=true;
				}
				
				
			}			
		}
		//不允许操作此按钮
		this.setEnabled(flag);
//		// 修改按钮操作属性之后：改为false，下次继续验证
		flag=false;
		this.rootLabel=0;
		
	}
	/**
	 * 添加操作日志记录
	 * 
	 */
//	public void insertOperationLog(){
//		OperationLogService_MB operationService=null;
//		try {			
//			if (getOperateKey()>0 && operationLog != null) {				
//				// 记录操作日志
//				operationService=(OperationLogService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OPERATIONLOGSERVIECE);
//				operationLog.setOverTime(DateUtil.getDate(DateUtil.FULLTIME));
//				operationLog.setUser_id(ConstantUtil.user.getUser_Id());
//				operationLog.setOperationType(this.getOperateKey());
//				operationLog.setOperationResult(result);
//				operationService.insertOperationLog(operationLog);
//			}
//		} catch (Exception e) {
//			ExceptionManage.dispose(e,this.getClass());
//		}finally{
//			UiUtil.closeService_MB(operationService);
//		}
//	}
	
	/**
	 * 设置是否显示等待对话框
	 * 
	 * @param isWait
	 *            true=显示 false=不显示
	 */
	public void setWait(boolean isWait) {
		this.isWait = isWait;
	}

	/**
	 * 获取当前的操作日志
	 * 
	 * @return
	 */
	public int getOperateKey() {
		return operateKey;
	}

	/**
	 * 设置操作的key 对应StringOperate.java中的常量
	 * 
	 * @param operateKey
	 */
	public void setOperateKey(int operateKey) {
		this.operateKey = operateKey;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public int getRootLabel() {
		return rootLabel;
	}

	public void setRootLabel(int rootLabel) {
		this.rootLabel = rootLabel;
	}
	/** -sy
	 * 权限验证  : 比对 传人参数 （权限标签）是否  存在于      登陆用户的权限之中 
	 * @param label		权限标签 		
	 */
	public boolean checkRoot(int label){
		RoleRoot roleRoot =new RoleRoot();
		return roleRoot.root( label);
	}
}
