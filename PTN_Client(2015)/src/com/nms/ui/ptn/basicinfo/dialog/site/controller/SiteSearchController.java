package com.nms.ui.ptn.basicinfo.dialog.site.controller;

import java.util.Iterator;
import java.util.List;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.basicinfo.dialog.site.CopySiteDialog;
import com.nms.ui.ptn.basicinfo.dialog.site.SiteSearchForIP;
import com.nms.ui.ptn.basicinfo.dialog.site.SiteSearchTablePanel;
/**
 * 搜索网元controller
 * @author dzy
 *
 */
public class SiteSearchController  extends AbstractController{
	private SiteSearchTablePanel view; //主页面
	List<SiteInst> siteInstList = null;	//网元集合
	public SiteSearchTablePanel getView() {
		return view;
	}
	public void setView(SiteSearchTablePanel view) {
		this.view = view;
	}
	
	/**
	 * 设置控制面板
	 * @param siteSearchTablePanel
	 * 							主页面
	 */
	public SiteSearchController(SiteSearchTablePanel siteSearchTablePanel) {
		this.setView(siteSearchTablePanel);
		
	}

	/**
	 * 搜索
	 */
	@Override
	public void search()throws Exception{
		new SiteSearchForIP(this.view,true);
	}
	
	/**
	 * 网元搜索
	 * @param ip 
	 * 			IP地址
	 * @param proSeries
	 * @return
	 */
	public String siteSearch(String ip, int proSeries) {
		DispatchUtil siteDispatch ;
		String result = null;
		try {
			siteDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
			siteInstList = siteDispatch.siteSearch(ip, proSeries);
			if(0==siteInstList.size()){
				result = ResourceUtil.srcStr(StringKeysTip.TIP_IPERROR_SEARCH);
			}
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			
		}
		return result;
	}
	
	/**
	 * 创建
	 */
	@Override
	public void openCreateDialog()throws Exception{
		if (this.getView().getAllSelect().size() == 0) {
			DialogBoxUtil.errorDialog(this.getView(), ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
		} else {
			new CopySiteDialog(true,this.getView().getSelect(),this.getView());
		}
		refresh();
	};
	
	/**
	 * 刷新
	 */
	@Override
	public void refresh() throws Exception {
		List<SiteInst> exitSiteList = null;
		//获取存在的网元
		exitSiteList = this.exitSite();
		//如果搜索网元不为空，除去现有的网元
		if(null!=siteInstList){
			Iterator<SiteInst> ite = siteInstList.iterator();  
				while(ite.hasNext()){
					SiteInst siteInst = ite.next();  
					if(0!=exitSiteList.size()){
						for(SiteInst exitSite:exitSiteList){
							if(siteInst.getCellDescribe().equals(exitSite.getCellDescribe()))
								ite.remove();
						}
					}
				}
		}
		this.view.clear();
		this.view.initData(siteInstList);
		this.view.updateUI();
		exitSiteList = null;
	}
	
	/**
	 * 搜索现存在的网元
	 * @return
	 */
	public List<SiteInst> exitSite(){
		SiteService_MB siteService=null;
		List<SiteInst> exitSiteList = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			exitSiteList = siteService.select();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(siteService);
		}
		return exitSiteList;
	}
}