package com.nms.ui.ptn.safety;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.JScrollPane;

import twaver.Node;
import twaver.TDataBox;
import twaver.table.TTreeTable;

import com.nms.db.bean.system.OperationDataLog;
import com.nms.db.bean.system.OperationLog;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysObj;

public class ShowDataLogDialog extends PtnDialog {
	private static final long serialVersionUID = -6528988602080443352L;
	private OperationLog operationLog = null;
	private List<OperationDataLog> logList = null;
	private TDataBox box = new TDataBox();
	private TTreeTable table = new LogDataTable(box);
	
	public ShowDataLogDialog(OperationLog log) {
		this.operationLog = log;
		this.logList = log.getDataLogList();
		this.initComponent();
		this.setLayout();
		this.initData();
		this.addActionListener();
		UiUtil.showWindow(this, 500, 600);
	}

	private void initComponent() {
		table.getTree().setLazyLoader(null);
		table.getTree().setRootVisible(false);
		table.setElementClass(Node.class);
		if(ResourceUtil.language.equals("zh_CN")){
			table.registerElementClassXML(Node.class, "/config/compareLogData/compareLog.xml");
		}else{
			table.registerElementClassXML(Node.class, "/config/compareLogData/compareLog_en.xml");
		}
		table.setRowHeight(18);
		table.setShowGrid(false);
		table.setEditable(false);
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(150);
		table.getColumnModel().getColumn(2).setPreferredWidth(150);
	}
	
	private void setLayout() {
		this.add(new JScrollPane(table), BorderLayout.CENTER);
	}
	
	private void initData() {
		Node element = new Node();
		String fileName = this.operationLog.getOperationObjName().substring(this.operationLog.getOperationObjName().lastIndexOf(",")+1, 
				this.operationLog.getOperationObjName().length());
		element.setName(this.operationLog.getOperationObjName().substring(0, this.operationLog.getOperationObjName().lastIndexOf(",")));
		element.setUserObject(this.operationLog);
		OperationDataLog rootLog = new OperationDataLog();
		if(this.operationLog.getSiteId() > 0){
			rootLog.setValue_before(ResourceUtil.srcStr(StringKeysObj.STRING_SITE_NAME)+":"+this.getsiteName(this.operationLog.getSiteId()));
		}else{
			rootLog.setValue_before(ResourceUtil.srcStr(StringKeysObj.SYSTEM_LOCK));
		}
		rootLog.setValue_after(rootLog.getValue_before());
		element.setBusinessObject(rootLog);
		this.box.addElement(element);
		Node parentLog = null;
		for (int i = 0; i < this.logList.size(); i++) {
			OperationDataLog log = new OperationDataLog();
			log.setFieldNameId(this.logList.get(i).getFieldNameId());
			log.setValue_before(this.logList.get(i).getValue_before());
			log.setValue_after(this.logList.get(i).getValue_after());
			UiUtil.convertOperationLog(log, fileName);
			Node nodeEnable = new Node();
			
			if(log.getFieldNameId() != null){
				this.box.addElement(nodeEnable);
			}
//			if(log.getValue_after().equals("") && log.getValue_before().equals("")){
			if(log.getValue_after().equals(log.getFieldNameId())){
				 log.setValue_after(null);
				 log.setValue_before(null);
				nodeEnable.setParent(element);
				parentLog = nodeEnable;
			}else{
				if(parentLog != null){
					nodeEnable.setParent(parentLog);
				}else{
					nodeEnable.setParent(element);
				}
			}
			nodeEnable.setName(log.getFieldNameId());
			nodeEnable.setBusinessObject(log);
		}
	}
	
	private String getsiteName(int siteId) {
		SiteService_MB service = null;
		try {
			service = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			return service.getSiteName(siteId);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
		return "";
	}
	
	private void addActionListener() {
		this.addComponentListener(new ComponentAdapter(){
			@Override
			public void componentResized(ComponentEvent arg0) {
				table.setRowHeight(getHeight()/30);
				table.getColumnModel().getColumn(0).setPreferredWidth(getWidth()*2/5);
				table.getColumnModel().getColumn(1).setPreferredWidth(getWidth()*3/10);
				table.getColumnModel().getColumn(2).setPreferredWidth(getWidth()*3/10);
			}
		});
	}
	
	public static void main(String[] args) {
		String f = "11,aa,33,pw";
		System.out.println(f.substring(f.lastIndexOf(",")+1, f.length()));
		System.out.println(f.substring(0, f.lastIndexOf(",")));
	}
}
