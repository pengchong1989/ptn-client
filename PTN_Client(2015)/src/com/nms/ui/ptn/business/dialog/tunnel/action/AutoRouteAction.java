package com.nms.ui.ptn.business.dialog.tunnel.action;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.nms.db.enums.EOperationLogType;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;

/*
 * 实现自动路由的算法，找两点之间的最短距离
 * 
 * author dxh
 * */
public class AutoRouteAction
{
	public class Way
	{
		int from;
		int to;
		int cost;		
	}
	
	@SuppressWarnings("rawtypes")
	Map map = new HashMap();
	@SuppressWarnings("rawtypes")
	List richedway = new ArrayList();
	List<String> routeStr = null;
	@SuppressWarnings("rawtypes")
	Map routemap = new HashMap();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addRoute(int city1, int city2, int cost)
	{
		List ctylist1 = (List)map.get(city1);
		if(ctylist1 == null)
		{
			ctylist1 = new ArrayList();
			map.put(city1, ctylist1);
		}
		Way way1 = new Way();
		way1.from = city1;
		way1.to = city2;
		way1.cost = cost;
		
		/*不存在线路则添加*/
		if(!ctylist1.contains(way1))
		{
			ctylist1.add(way1);
		}
		
		List ctylist2 = (List)map.get(city2);
		if(ctylist2 == null)
		{
			ctylist2 = new ArrayList();
			map.put(city2, ctylist2);
		}
		Way way2 = new Way();
		way2.from = city2;
		way2.to = city1;
		way2.cost = cost;
		
		/*不存在线路则添加*/
		if(!ctylist2.contains(way2))
		{
			ctylist2.add(way2);
		}
	}
	
	/*计算最短路径，最短时间*/
	@SuppressWarnings("unchecked")
	public void run(int from, int to)
	{
		int temptime = 0;
		if(richedway.contains(from))
		{
			return;
		}
		richedway.add(from);
		
		if(richedway.size() >1)
		{
			@SuppressWarnings("rawtypes")
			List iniList = (List) map.get(richedway.get(0));
			for(int j = 0; j<iniList.size(); j++)
			{
				Way w = (Way)iniList.get(j);
				if(w.to == Integer.valueOf(richedway.get(1).toString()))
				{
					temptime += w.cost;
				}
			}
			
			//所有经过的端用时加起来
			for(int i = 1; i < richedway.size(); i++)
			{
				@SuppressWarnings("rawtypes")
				List toList = (List)map.get(richedway.get(i));
				for(int j = 0; j<toList.size(); j++)
				{
					Way w = (Way)toList.get(j);
					if(i+1 < richedway.size())
					{
						if(w.to == Integer.valueOf(richedway.get(i+1).toString()))
						{
							temptime += w.cost;
						}
					}
				}
			}
		}
		
		//到达
		if(from == to)
		{
			String route = richedway.get(0).toString();
			for(int i = 1; i< richedway.size(); i++)
			{
				route += "-"+richedway.get(i).toString();
			}
			routeStr.add(route);
			
			routemap.put(temptime, route);
			temptime = 0;
			//到达后退回去，走下一路线
			richedway.remove(richedway.size()-1);			
			return;			
		}
		
		//没达到
		//获得from端能够到达to端列表
		@SuppressWarnings("rawtypes")
		List routeList = (List) map.get(from);
		for(@SuppressWarnings("rawtypes")
		Iterator it = routeList.iterator(); it.hasNext();)
		{
			Way way  = (Way)it.next();
			run(way.to, to);
		}
		
		richedway.remove(richedway.size()-1);
	}
	
	public boolean containsAny(String str, String searchChars) 
	{ 
	   if(str.length()!=str.replaceAll(searchChars, "").length())
	   {
		   return true;
	   }
	   else
	   {
		   return false;
	   }
	}
	
	public List<String> show(int city1, List<Integer> nepass, List<Integer> segpass, List<Integer> segpropass, int city2)
	{
		List<String> routeList = new ArrayList<String>();
		show(city1, city2);
		//先增加最短的一条
		int indexShortest=0;
		List<String> TempList = new ArrayList<String>();
		TempList.addAll(routeStr);
		
		for(int i=0;i<TempList.size();i++)
		{
			if(findSegmentNum(TempList.get(i).toString()) < findSegmentNum(TempList.get(indexShortest).toString()))
			{
				indexShortest=i;
			}
		}
	
		//验证是否包含必经网元，不包含的删除掉
		if(nepass.size() > 0)
		{
			Iterator<String> sListIterator = TempList.iterator();  
			while(sListIterator.hasNext())
			{  
			    String e = sListIterator.next();  
			    String[] eList = e.split("-");
			    int icount=0;
			    for(int j = 0; j < nepass.size(); j++)
				{
			    	for(String element:eList)
			    	{
			    		if(element.equals(nepass.get(j).toString()))
	    				{
			    			icount++;
					    	break;
	    				}
			    	}
				}
			    if(icount != nepass.size())
			    {
			    	sListIterator.remove(); 
			    }
			    
			}
		}
		 
		 //验证工作路径是否包含必经段,不包含的删除掉
		if(segpass.size()>0)
	    {
			//分别判断是否包含正的或反的,将都不包含的删除掉
			Iterator<String> itr = TempList.iterator();  
			while(itr.hasNext())
	    	{ 
				String e = itr.next();  
				for(int i = 0; i < segpass.size()/2; i++)
				{
					String sgBefore = segpass.get(2*i) + "-" + segpass.get(2*i + 1);
					String sgAfter = segpass.get(2*i + 1) + "-" + segpass.get(2*i);
					if(e.toString().indexOf(sgBefore) == -1 && e.toString().indexOf(sgAfter) == -1)
					{
						itr.remove(); 
						break;
					}
				}
		    }
		}
		
		 //验证保护路径是否包含必经段,不包含的删除掉
		if(segpropass != null && segpropass.size()>0)
	    {
			//分别判断是否包含正的或反的,将都不包含的删除掉
			Iterator<String> itr = TempList.iterator();  
			while(itr.hasNext())
	    	{ 
				String e = itr.next();  
				for(int i = 0; i < segpropass.size()/2; i++)
				{
					String sgBefore = segpropass.get(2*i) + "-" + segpropass.get(2*i + 1);
					String sgAfter = segpropass.get(2*i + 1) + "-" + segpropass.get(2*i);
					if(e.toString().indexOf(sgBefore) == -1 && e.toString().indexOf(sgAfter) == -1)
					{
						itr.remove(); 
						break;
					}
				}
		    }
		}
		
		if(TempList.size() == 0)
		{
			DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_NO_ROUTE));
			return null;
		}
		
		
		if(TempList.size() > 0 )
		{
			//再增加包含必经网元的最短的一条
			String strTemp = findShortestWay(TempList);
			routeList.add(strTemp);
		}
		
		return routeList;
	}
	

	private int findSegmentNum(String str)
	{
		int cout=0;
		for(int i=0;i<str.length();i++)
		{
			if(str.charAt(i)=='-')
			{
				cout++;
			}
		}
		
		return cout;
	}
	
	//找出最短的一条路由
	@SuppressWarnings("rawtypes")
	private String findShortestWay(List listTemp)
	{
		int indexShortest=0;
		if(listTemp != null && listTemp.size() == 1)
		{
			return listTemp.get(0).toString();
		}
		
		for(int i=0;i<listTemp.size();i++)
		{
			if(findSegmentNum(listTemp.get(i).toString()) < findSegmentNum(listTemp.get(indexShortest).toString()))
			{
				indexShortest=i;
			}
		}
		
		return listTemp.get(indexShortest).toString();
	}
	
	private String findAnotherWay(List listTemp)
	{
		int indexShortest=0;
		if(listTemp != null && listTemp.size() == 1)
		{
			return listTemp.get(0).toString();
		}
		
		if(listTemp != null && listTemp.size() > 1)
		{
			String shortest = findShortestWay(listTemp);
			int iCount = 0;
			for(int i=0;i<listTemp.size();i++)
			{
				if(listTemp.get(i).toString().equals(shortest))
				{
					iCount++;
				}
			}
			if(iCount > 1)
			{
				return shortest;
			}
			return listTemp.get(1).toString();
		}
		
		return "";
	}
	
	//输出用时最短的线路
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List show(int city1, int city2)
	{
		List<String> listRoute = new ArrayList<String>();
		routeStr = new ArrayList();
		run(city1,city2);
		List listTemp = new ArrayList();
		
		listTemp.addAll(routeStr);
		
		if(listTemp.size() == 0)
		{
			DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_NO_ROUTE));
			return null;
		}
		
		 
		String temp = findShortestWay(listTemp);
		listRoute.add(temp);
		return listRoute;
	}
	
	public List show(int city1,String type, int city2)
	{
		List<String> listRoute = new ArrayList<String>();
		routeStr = new ArrayList();
		run(city1,city2);
		List listTemp = new ArrayList();
		
		listTemp.addAll(routeStr);
		
		if(listTemp.size() == 0)
		{
			DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_NO_ROUTE));
			return null;
		}
		
		 
		String temp = findShortestWay(listTemp);
		String temp2 = findAnotherWay(listTemp);
		if(type.equals("work"))
		{
			listRoute.add(temp);
		}
		else if(type.equals("protect"))
		{
			listRoute.add(temp2);
		}
			
		return listRoute;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args)
	{
		AutoRouteAction mymap=new AutoRouteAction();
		mymap.addRoute(6, 2, 1);
		mymap.addRoute(6, 2, 1);
		mymap.addRoute(2, 3, 1);
		mymap.addRoute(2, 3, 1);
		mymap.addRoute(2, 1, 1);
		mymap.addRoute(3, 1, 1);
//		mymap.addRoute(6, 1, 1);
		
		List lsit = mymap.show(6, 1);
		System.out.println(lsit.size());
//		mymap.addRoute(66, 33, 1);
//		mymap.addRoute(66, 55, 1);
		
//		List pass = new ArrayList<Integer>();
//		pass.add(2);
//		pass.add(1);
//		pass.add(3);
	}

}
