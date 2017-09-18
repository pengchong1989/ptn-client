package com.nms.ui.ptn.business.loopProtect;

import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.path.Segment;
import com.nms.db.bean.ptn.path.protect.LoopProtectInfo;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.ptn.path.protect.WrappingProtectService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.SiteUtil;
import com.nms.service.impl.util.WhImplUtil;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ListingFilter;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.basicinfo.dialog.segment.SearchSegmentDialog;
import com.nms.ui.ptn.business.dialog.loopProtect.AddLoopProtectDialog;
import com.nms.ui.ptn.business.dialog.loopProtect.UpdateLoopProtectDialog;

public class LoopProtectController extends AbstractController {

	private LoopProtectPanel view;

	public LoopProtectController(LoopProtectPanel loopProtectPanel) {
		view = loopProtectPanel;
	}

	/**
	 * 刷新
	 */
	@Override
	public void refresh() throws Exception {
		List<LoopProtectInfo> loopProtectInfoList = null;
		WrappingProtectService_MB protectServiceMB = null;
		LoopProtectInfo loopProtectInfo_select = null;
		List<LoopProtectInfo> loopProtectInfoList_select = null;
		ListingFilter listingFilter = null;
		List<LoopProtectInfo> loopProtectInfoList_ui = null;
		try {
			loopProtectInfoList_ui = new ArrayList<LoopProtectInfo>();
			listingFilter = new ListingFilter();
			// 从数据库中查询所有
			protectServiceMB = (WrappingProtectService_MB) ConstantUtil.serviceFactory.newService_MB(Services.WRAPPINGPROTECT);
			loopProtectInfoList = protectServiceMB.select();

			// 遍历所有数据
			if (loopProtectInfoList.size() > 0) {
				for (LoopProtectInfo loopProtectInfo : loopProtectInfoList) {
					// 根据组ID找出此组下的所有数据
					loopProtectInfo_select = new LoopProtectInfo();
					loopProtectInfo_select.setLoopId(loopProtectInfo.getLoopId());
					loopProtectInfoList_select = protectServiceMB.select(loopProtectInfo_select);

					// 如果登陆用户对此组数据有权限 那么添加到界面中
					if (listingFilter.filterByList(loopProtectInfoList_select)) {
						loopProtectInfoList_ui.add(loopProtectInfo);
					}
				}
			}

			this.view.clear();
			this.view.getSegmentTablePanel().clear();
			this.view.getLoopProtectTopoPanel().clear();
			this.view.initData(loopProtectInfoList_ui);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			loopProtectInfoList = null;
			UiUtil.closeService_MB(protectServiceMB);
			loopProtectInfo_select = null;
			loopProtectInfoList_select = null;
			listingFilter = null;
			loopProtectInfoList_ui = null;
		}

	}

	/**
	 * 新建
	 */
	@Override
	public void openCreateDialog() throws Exception {
		try {
			AddLoopProtectDialog addpwdialog = new AddLoopProtectDialog(this.view, true, null);
			addpwdialog.setLocation(UiUtil.getWindowWidth(addpwdialog.getWidth()), UiUtil.getWindowHeight(addpwdialog.getHeight()));
			addpwdialog.setVisible(true);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
			throw e;
		}
	}

	/**
	 * 修改
	 */
	@Override
	public void openUpdateDialog() throws Exception {
		List<LoopProtectInfo> loopProtectInfoList = new ArrayList<LoopProtectInfo>();
//		loopProtectInfoList = this.getLoopProtectInfoList();
		loopProtectInfoList.add(this.view.getSelect());
		UpdateLoopProtectDialog updateLoopProtectDialog = new UpdateLoopProtectDialog(loopProtectInfoList, this.view);
		UiUtil.showWindow(updateLoopProtectDialog, 450, 400);
	}

	/**
	 * 得到数据集合
	 */
	public List<LoopProtectInfo> getLoopProtectInfoList() {
		List<LoopProtectInfo> loopProtectInfoList = null;
		WrappingProtectService_MB protectService = null;
		try {
			LoopProtectInfo loopProtectInfo = this.view.getSelect();
			LoopProtectInfo info = new LoopProtectInfo();
			info.setLoopId(loopProtectInfo.getLoopId());
			protectService = (WrappingProtectService_MB) ConstantUtil.serviceFactory.newService_MB(Services.WRAPPINGPROTECT);
			loopProtectInfoList = protectService.select(info);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(protectService);
		}
		return loopProtectInfoList;
	}

	/**
	 * 选中一条记录后，查看详细信息
	 */
	@Override
	public void initDetailInfo() {
		try {
			initTopoPanel();
			initSegemntDate();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 初始化段数据
	 */
	private void initSegemntDate() {
		List<LoopProtectInfo> loopProtectInfoList = null;
		List<Segment> segmentList = null;
		try {
			view.getSegmentTablePanel().clear();
			loopProtectInfoList = getLoopProtectInfoList();
			segmentList = new ArrayList<Segment>();
			for (LoopProtectInfo loopProtectInfo : loopProtectInfoList) {
				Segment segment = loopProtectInfo.getWestSegment();
				segmentList.add(segment);
			}
			view.getSegmentTablePanel().initData(segmentList);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 初始化拓扑
	 */
	private void initTopoPanel() {
		List<LoopProtectInfo> loopProtectInfoList = null;
		List<Segment> segmentList = null;
		try {
			view.getLoopProtectTopoPanel().clear();
			loopProtectInfoList = getLoopProtectInfoList();
			segmentList = new ArrayList<Segment>();
			for (LoopProtectInfo loopProtectInfo : loopProtectInfoList) {
				Segment segment = loopProtectInfo.getWestSegment();
				segmentList.add(segment);
			}
			view.getLoopProtectTopoPanel().init(segmentList);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	// 删除
	@Override
	public void delete() throws Exception {

		List<LoopProtectInfo> loopProtectInfoList = null;
		DispatchUtil wrappingDispatch = null;
		String resultStr = null;
		List<Integer> allSiteIds =null;
		List<Integer> siteIds = null;
		boolean onlineFlag = false;
		try {
			loopProtectInfoList = this.getView().getAllSelect();
			SiteUtil siteUtil = new SiteUtil();
			allSiteIds = new ArrayList<Integer>();
			siteIds = new ArrayList<Integer>();
			for (LoopProtectInfo loopProtectInfo : getDeleteLoopProtectInfo(loopProtectInfoList)) {
				if (!siteIds.contains(loopProtectInfo.getSiteId())) {
					allSiteIds.add(loopProtectInfo.getSiteId());
				}
			}
			for(int i=0;i<allSiteIds.size();i++){
				if(1==siteUtil.SiteTypeOnlineUtil(allSiteIds.get(i))){
				   siteIds.add(allSiteIds.get(i));			    		
				}
		     }
			if(siteIds !=null && siteIds.size()!=0){
			   onlineFlag = true;
			}
			if(onlineFlag){
				WhImplUtil wu = new WhImplUtil();
				String str=wu.getNeNames(siteIds);
				DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_NOT_DELETEONLINE)+""+str+ResourceUtil.srcStr(StringKeysTip.TIP_ONLINENOT_DELETEONLINE));
				return ;
			}
			
									
			wrappingDispatch = new DispatchUtil(RmiKeys.RMI_WRAPPING);
			resultStr = wrappingDispatch.excuteDelete(loopProtectInfoList);
			DialogBoxUtil.succeedDialog(this.getView(), resultStr);
			// 添加日志记录
			for(LoopProtectInfo loop : loopProtectInfoList){
				AddOperateLog.insertOperLog(null, EOperationLogType.LOOPPROTECTDELETE.getValue(), resultStr,
						null, null, -1, loop.getName(), null);
			}
			this.view.getRefreshButton().doClick();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			loopProtectInfoList = null;
			resultStr = null;
			allSiteIds=null;
			siteIds= null;
		}

	}

	public LoopProtectPanel getView() {
		return view;
	}

	public void setView(LoopProtectPanel view) {
		this.view = view;
	};

	/**
	 * 搜索
	 */
	@Override
	public void search() throws Exception {
		try {

			new SearchSegmentDialog(this.view);

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
		}
	}
	/**
	 * 获得删除相关的环成员
	 * @param loopProtectInfoList
	 * @return
	 */
	private List<LoopProtectInfo> getDeleteLoopProtectInfo(List<LoopProtectInfo> loopProtectInfoList){
		List<LoopProtectInfo> loopProtectInfos = null;
		WrappingProtectService_MB wrappingProtectService = null;
		try {
			loopProtectInfos = new ArrayList<LoopProtectInfo>();
			wrappingProtectService = (WrappingProtectService_MB) ConstantUtil.serviceFactory.newService_MB(Services.WRAPPINGPROTECT);
			for(LoopProtectInfo loopProtectInfo : loopProtectInfoList){
				LoopProtectInfo info = new LoopProtectInfo();
				info.setLoopId(loopProtectInfo.getLoopId());
				for(LoopProtectInfo info2 : wrappingProtectService.select(info)){
					loopProtectInfos.add(info2);
				}
			}
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(wrappingProtectService);
		}
		return loopProtectInfos;
	}
}
