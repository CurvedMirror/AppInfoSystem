package cn.appsys.controller.developer;

import cn.appsys.pojo.AppCategory;
import cn.appsys.pojo.AppInfo;
import cn.appsys.pojo.DataDictionary;
import cn.appsys.pojo.DevUser;
import cn.appsys.service.developer.AppCategoryService;
import cn.appsys.service.developer.AppInfoService;
import cn.appsys.tools.Constants;
import cn.appsys.tools.PageSupport;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author jie
 * @date 2019/3/2 -20:01
 */
@Controller
@RequestMapping(value = "/dev/flatform/app")
public class AppController {

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private AppCategoryService appCategoryService;

    @RequestMapping("/list")
    public String getAppInfoList(@RequestParam(required = false) String querySoftwareName,
                                 @RequestParam(required = false) String queryFlatformId,
                                 @RequestParam(required = false) String queryCategoryLevel1,
                                 @RequestParam(required = false) String queryCategoryLevel2,
                                 @RequestParam(required = false) String queryCategoryLevel3,
                                 @RequestParam(required = false) String queryStatus,
                                 @RequestParam(value = "pageIndex", required = false) String pageIndex,
                                 Model model) {


        int currentPageNo = pageIndex == null ? 1 : Integer.parseInt(pageIndex);
        //总记录数
        int appInfoCount = appInfoService.getAppInfoCount(querySoftwareName, queryStatus, queryFlatformId, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3);
        PageSupport pages = new PageSupport();
        pages.setPageSize(Constants.pageSize);
        pages.setTotalCount(appInfoCount);
        pages.setCurrentPageNo(currentPageNo);

        //app列表
        List<AppInfo> appInfoList = appInfoService.getAppInfoList(querySoftwareName, queryStatus, queryFlatformId, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, currentPageNo, pages.getPageSize());
        //app状态集合
        List<DataDictionary> statusList = appInfoService.getDataDictionaryList("APP_STATUS");
        //所属平台集合
        List<DataDictionary> flatFormList = appInfoService.getDataDictionaryList("APP_FLATFORM");
        //获取一级分类
        List<AppCategory> categoryLevel1List = appCategoryService.getCategoryListByParentId(null);


        model.addAttribute("appInfoList", appInfoList);
        model.addAttribute("statusList", statusList);
        model.addAttribute("flatFormList", flatFormList);
        model.addAttribute("categoryLevel1List", categoryLevel1List);
        model.addAttribute("pages", pages);
        model.addAttribute("queryStatus", queryStatus);
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
        return "developer/appinfolist";
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

    @RequestMapping("/appinfoadd")
    public String appInfoAdd() {
        return "developer/appinfoadd";
    }

    @RequestMapping(value = "/appinfoaddsave", method = RequestMethod.POST)
    public String addSave(AppInfo appInfo, HttpServletRequest request, HttpSession session,
                          @RequestParam(value = "a_logoPicPath", required = false) MultipartFile attach) {

        String logoPicPath = null;
        String logoLocPath = null;

        if (!attach.isEmpty()) {
            String path = request.getSession().getServletContext().getRealPath("statics" + File.separator + "uploadfiles");
            String oldFileName = attach.getOriginalFilename();
            String preFix = FilenameUtils.getExtension(oldFileName);
            int fileSize = 5000000;
            if (attach.getSize() > fileSize) {
                //上传大小不得超过 50k
                request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_4);
                return "developer/appinfoadd";
            } else if ("jpg".equalsIgnoreCase(preFix) || "png".equalsIgnoreCase(preFix)
                    || "jepg".equalsIgnoreCase(preFix)) {
                String fileName = UUID.randomUUID()+appInfo.getAPKName()+".jpg";
                File targetFile = new File(path,fileName);
                if (!targetFile.exists()){
                    targetFile.mkdirs();
                }
                try {
                    attach.transferTo(targetFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_2);
                    return "developer/appinfoadd";
                }
                logoPicPath = request.getContextPath()+"/statics/uploadfiles/"+fileName;
                logoLocPath = path+File.separator+fileName;
            }else {
                request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_3);
                return "developer/appinfoadd";
            }
        }
        appInfo.setCreatedBy(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());
        appInfo.setCreationDate(new Date());
        appInfo.setLogoPicPath(logoPicPath);
        appInfo.setLogoLocPath(logoLocPath);
        appInfo.setDevId(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());
        appInfo.setStatus(1);
        try {
            if(appInfoService.addAppInfo(appInfo)){
                return "redirect:/dev/flatform/app/list";
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "developer/appinfoadd";
    }

    /**
     * 根据typeCode查询出相应的数据字典列表
     * @param
     * @return
     */
    @RequestMapping(value="/datadictionarylist",method=RequestMethod.GET)
    @ResponseBody
    public List<DataDictionary> getDataDicList (@RequestParam String tcode){
        return  appInfoService.getDataDictionaryList(tcode);
    }
    /**
     * 判断APKName是否唯一
     * @param
     * @return
     */
    @RequestMapping(value="/apkexist.json",method=RequestMethod.POST)
    @ResponseBody
    public Object apkNameIsExit(@RequestParam String APKName){
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(StringUtils.isNullOrEmpty(APKName)){
            resultMap.put("APKName", "empty");
        }else{
            AppInfo appInfo = appInfoService.getAppInfo(null, APKName);
            if(null != appInfo) {
                resultMap.put("APKName", "exist");
            } else {
                resultMap.put("APKName", "noexist");
            }
        }
        return JSONArray.toJSONString(resultMap);
    }

    @RequestMapping(value = "/appinfomodify/{id}",method = RequestMethod.GET)
    public String appinfomodify(@PathVariable int id, Model model){
        AppInfo appinfo = appInfoService.getAppInfoByID(id);
        model.addAttribute(appinfo);
        return "developer/appinfomodify";
    }

    @RequestMapping(value = "/appinfomodifysave",method = RequestMethod.POST)
    public String appinfomodifysave(){

        return "";
    }
}
