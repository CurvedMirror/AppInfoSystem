package cn.appsys.controller.backend;

import cn.appsys.pojo.AppCategory;
import cn.appsys.pojo.AppInfo;
import cn.appsys.pojo.AppVersion;
import cn.appsys.pojo.DataDictionary;
import cn.appsys.service.backend.AppService;
import cn.appsys.service.developer.AppCategoryService;
import cn.appsys.service.developer.AppVersionService;
import cn.appsys.tools.Constants;
import cn.appsys.tools.PageSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author jie
 * @date 2019/3/6 -21:30
 */
@Controller
@RequestMapping(value="/manager/backend/app")
public class AppCheckController {
    @Autowired
    private AppService appService;

    @Autowired
    private AppCategoryService appCategoryService;

    @Autowired
    private AppVersionService appVersionService;
    /**
     * 根据条件分页查询AppinfoList
     * @param model model
     * @param session session
     * @param querySoftwareName 要查询的软件名
     * @param queryCategoryLevel1  要查询的一级分类
     * @param queryCategoryLevel2 要查询的二级分类
     * @param queryCategoryLevel3 要查询的三级分类
     * @param queryFlatformId 要查询的所属平台的id
     * @param pageIndex 页码
     * @return AppinfoList
     */
    @RequestMapping(value="/list")
    public String getAppInfoList(Model model, HttpSession session,
                                 @RequestParam(value="querySoftwareName",required=false) String querySoftwareName,
                                 @RequestParam(value="queryCategoryLevel1",required=false) String queryCategoryLevel1,
                                 @RequestParam(value="queryCategoryLevel2",required=false) String queryCategoryLevel2,
                                 @RequestParam(value="queryCategoryLevel3",required=false) String queryCategoryLevel3,
                                 @RequestParam(value="queryFlatformId",required=false) String queryFlatformId,
                                 @RequestParam(value="pageIndex",required=false) String pageIndex){


        int currentPageNo = pageIndex == null ? 1 : Integer.parseInt(pageIndex);
        //总记录数
        int appInfoCount = appService.getAppInfoCount(querySoftwareName, queryFlatformId, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3);
        PageSupport pages = new PageSupport();
        pages.setPageSize(Constants.pageSize);
        pages.setTotalCount(appInfoCount);
        pages.setCurrentPageNo(currentPageNo);

        //app列表
        List<AppInfo> appInfoList = appService.getAppInfoList(querySoftwareName, queryFlatformId, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, currentPageNo, pages.getPageSize());

        //所属平台集合
        List<DataDictionary> flatFormList = appService.getDataDictionaryList("APP_FLATFORM");
        //获取一级分类
        List<AppCategory> categoryLevel1List = appCategoryService.getCategoryListByParentId(null);

        session.setAttribute("appInfoListToExcel",appInfoList);
        model.addAttribute("appInfoList", appInfoList);
        model.addAttribute("flatFormList", flatFormList);
        model.addAttribute("categoryLevel1List", categoryLevel1List);
        model.addAttribute("pages", pages);
        model.addAttribute("querySoftwareName", querySoftwareName);
        model.addAttribute("queryCategoryLevel1", queryCategoryLevel1);
        model.addAttribute("queryCategoryLevel2", queryCategoryLevel2);
        model.addAttribute("queryCategoryLevel3", queryCategoryLevel3);
        model.addAttribute("queryFlatformId", queryFlatformId);

        List<AppCategory> categoryLevel2List;
        List<AppCategory> categoryLevel3List;

        //二级分类列表和三级分类列表---回显
        if (queryCategoryLevel2 != null && !"".equals(queryCategoryLevel2)) {
            categoryLevel2List = appCategoryService.getCategoryListByParentId(queryCategoryLevel1);
            model.addAttribute("categoryLevel2List", categoryLevel2List);
        }
        if (queryCategoryLevel3 != null && !"".equals(queryCategoryLevel3)) {
            categoryLevel3List = appCategoryService.getCategoryListByParentId(queryCategoryLevel2);
            model.addAttribute("categoryLevel3List", categoryLevel3List);
        }
        return "backend/applist";
    }

    /**
     * 根据parentId查询出相应的分类级别列表
     *
     * @param pid
     * @return
     */
    @RequestMapping(value = "/categorylevellist.json", method = RequestMethod.GET)
    @ResponseBody
    public List<AppCategory> getAppCategoryList(@RequestParam String pid) {
        if ("".equals(pid)) {
            pid = null;
        }
        return appCategoryService.getCategoryListByParentId(pid);
    }

    /**
     * 跳转到APP信息审核页面
     * @param appId
     * @param versionId
     * @param model
     * @return
     */
    @RequestMapping(value="/check",method=RequestMethod.GET)
    public String check(@RequestParam(value="aid",required=false) String appId,
                        @RequestParam(value="vid",required=false) String versionId,
                        Model model){
        AppInfo appInfo = appService.getAppInfo(Integer.valueOf(appId), null);
        AppVersion appVersion = appVersionService.getAppVersionById(Integer.valueOf(versionId));
        model.addAttribute(appVersion);
        model.addAttribute(appInfo);
        return "backend/appcheck";
    }

    @RequestMapping(value="/checksave",method=RequestMethod.POST)
    public String checkSave(AppInfo appInfo){
        try {
            if(appService.updateStatus(appInfo)){
                return "redirect:/manager/backend/app/list";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "backend/appcheck";
    }
}
