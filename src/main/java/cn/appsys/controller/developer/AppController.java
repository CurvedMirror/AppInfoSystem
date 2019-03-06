package cn.appsys.controller.developer;

import cn.appsys.pojo.*;
import cn.appsys.service.developer.AppCategoryService;
import cn.appsys.service.developer.AppInfoService;
import cn.appsys.service.developer.AppVersionService;
import cn.appsys.tools.Constants;
import cn.appsys.tools.ExcelUtil;
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
import javax.servlet.http.HttpServletResponse;
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

    @Autowired
    private AppVersionService appVersionService;

    @RequestMapping("/list")
    public String getAppInfoList(@RequestParam(required = false) String querySoftwareName,
                                 @RequestParam(required = false) String queryFlatformId,
                                 @RequestParam(required = false) String queryCategoryLevel1,
                                 @RequestParam(required = false) String queryCategoryLevel2,
                                 @RequestParam(required = false) String queryCategoryLevel3,
                                 @RequestParam(required = false) String queryStatus,
                                 @RequestParam(value = "pageIndex", required = false) String pageIndex,
                                 Model model,HttpSession session) {


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

        session.setAttribute("appInfoListToExcel",appInfoList);
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
                String fileName = UUID.randomUUID() + appInfo.getAPKName() + ".jpg";
                File targetFile = new File(path, fileName);
                if (!targetFile.exists()) {
                    targetFile.mkdirs();
                }
                try {
                    attach.transferTo(targetFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_2);
                    return "developer/appinfoadd";
                }
                logoPicPath = request.getContextPath() + "/statics/uploadfiles/" + fileName;
                logoLocPath = path + File.separator + fileName;
            } else {
                request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_3);
                return "developer/appinfoadd";
            }
        }
        appInfo.setCreatedBy(((DevUser) session.getAttribute(Constants.DEV_USER_SESSION)).getId());
        appInfo.setCreationDate(new Date());
        appInfo.setLogoPicPath(logoPicPath);
        appInfo.setLogoLocPath(logoLocPath);
        appInfo.setDevId(((DevUser) session.getAttribute(Constants.DEV_USER_SESSION)).getId());
        appInfo.setStatus(1);
        try {
            if (appInfoService.addAppInfo(appInfo)) {
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
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/datadictionarylist", method = RequestMethod.GET)
    @ResponseBody
    public List<DataDictionary> getDataDicList(@RequestParam String tcode) {
        return appInfoService.getDataDictionaryList(tcode);
    }

    /**
     * 判断APKName是否唯一
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/apkexist.json", method = RequestMethod.POST)
    @ResponseBody
    public Object apkNameIsExit(@RequestParam String APKName) {
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (StringUtils.isNullOrEmpty(APKName)) {
            resultMap.put("APKName", "empty");
        } else {
            AppInfo appInfo = appInfoService.getAppInfo(null, APKName);
            if (null != appInfo) {
                resultMap.put("APKName", "exist");
            } else {
                resultMap.put("APKName", "noexist");
            }
        }
        return JSONArray.toJSONString(resultMap);
    }

    @RequestMapping(value = "/appinfomodify/{id}", method = RequestMethod.GET)
    public String appinfomodify(@PathVariable int id, Model model) {
        AppInfo appinfo = appInfoService.getAppInfoByID(id);
        model.addAttribute(appinfo);
        return "developer/appinfomodify";
    }

    @RequestMapping(value = "/appinfomodifysave", method = RequestMethod.POST)
    public String appinfomodifysave(AppInfo appInfo, HttpSession session,
                                    HttpServletRequest request,
                                    @RequestParam(value = "attach", required = false) MultipartFile attach) {
        String logoPicPath = null;
        String logoLocPath = null;
        String APKName = appInfo.getAPKName();
        if (!attach.isEmpty()) {
            String path = request.getSession().getServletContext().getRealPath("statics" + File.separator + "uploadfiles");
            String oldFileName = attach.getOriginalFilename();
            String prefix = FilenameUtils.getExtension(oldFileName);
            int filesize = 500000;
            if (attach.getSize() > filesize) {
                request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_4);
                return "developer/appinfomodify";
            } else if ("jpg".equalsIgnoreCase(prefix) || "png".equalsIgnoreCase(prefix)
                    || "jepg".equalsIgnoreCase(prefix) || "pneg".equalsIgnoreCase(prefix)) {
                String fileName = UUID.randomUUID() + appInfo.getAPKName() + ".jpg";
                File targetFile = new File(path, fileName);
                if (!targetFile.exists()) {
                    targetFile.mkdirs();
                }
                try {
                    attach.transferTo(targetFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_2);
                    return "developer/appinfomodify";
                }
                logoPicPath = request.getContextPath() + "/statics/uploadfiles/" + fileName;
                logoLocPath = path + File.separator + fileName;
            } else {
                request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_3);
                return "developer/appinfomodify";
            }
        }
        appInfo.setModifyBy(((DevUser) session.getAttribute(Constants.DEV_USER_SESSION)).getId());
        appInfo.setModifyDate(new Date());
        appInfo.setLogoLocPath(logoLocPath);
        appInfo.setLogoPicPath(logoPicPath);
        try {
            if (appInfoService.appinfomodifysave(appInfo)) {
                return "redirect:/dev/flatform/app/list";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "developer/appinfomodify";
    }

    @RequestMapping(value = "/delfile", method = RequestMethod.POST)
    @ResponseBody
    public Object delFile(@RequestParam(value = "flag", required = false) String flag,
                          @RequestParam(value = "id", required = false) String id) {
        HashMap<String, String> resultMap = new HashMap<String, String>();
        String fileLocPath;
        if (flag == null || "".equals(flag) ||
                id == null || "".equals(id)) {
            resultMap.put("result", "failed");
        } else if ("logo".equals(flag)) {
            try {
                fileLocPath = (appInfoService.getAppInfo(Integer.parseInt(id), null)).getLogoLocPath();
                File file = new File(fileLocPath);
                if (file.exists()) {
                    if (file.delete()) {
                        if (appInfoService.deleteAppLogo(Integer.parseInt(id))) {
                            resultMap.put("result", "success");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("apk".equals(flag)) {
            //删除apk文件（操作app_version）
            try {
                fileLocPath = (appVersionService.getAppVersionById(Integer.parseInt(id))).getApkLocPath();
                File file = new File(fileLocPath);
                if (file.exists()) {
                    if (file.delete()) {
                        if (appVersionService.deleteApkFile(Integer.parseInt(id))) {
                            resultMap.put("result", "success");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return JSONArray.toJSONString(resultMap);
    }

    /**
     * 增加appversion信息（跳转到新增app版本页面）
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/appversionadd", method = RequestMethod.GET)
    public String addVersion(@RequestParam(value = "id") String appId,
                             @RequestParam(value = "error", required = false) String fileUploadError,
                             AppVersion appVersion, Model model) {
        if ("error1".equals(fileUploadError)) {
            fileUploadError = Constants.FILEUPLOAD_ERROR_1;
        } else if ("error2".equals(fileUploadError)) {
            fileUploadError = Constants.FILEUPLOAD_ERROR_2;
        } else if ("error3".equals(fileUploadError)) {
            fileUploadError = Constants.FILEUPLOAD_ERROR_3;
        }
        appVersion.setAppId(Integer.parseInt(appId));
        List<AppVersion> appVersionList = null;
        try {
            appVersionList = appVersionService.getAppVersionList(Integer.parseInt(appId));
            appVersion.setAppName((appInfoService.getAppInfo(Integer.parseInt(appId), null)).getSoftwareName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("appVersionList", appVersionList);
        model.addAttribute(appVersion);
        model.addAttribute("fileUploadError", fileUploadError);
        return "developer/appversionadd";
    }

    /**
     * 保存新增appversion数据（子表）-上传该版本的apk包
     *
     * @param
     * @param appVersion
     * @param session
     * @param request
     * @param attach
     * @return
     */
    @RequestMapping(value = "/addversionsave", method = RequestMethod.POST)
    public String addVersionSave(AppVersion appVersion, HttpSession session, HttpServletRequest request,
                                 @RequestParam(value = "a_downloadLink", required = false) MultipartFile attach) {
        String downloadLink = null;
        String apkLocPath = null;
        String apkFileName = null;
        if (!attach.isEmpty()) {
            String path = request.getSession().getServletContext().getRealPath("statics" + File.separator + "uploadfiles");
            String oldFileName = attach.getOriginalFilename();
            String prefix = FilenameUtils.getExtension(oldFileName);
            if (prefix.equalsIgnoreCase("apk")) {
                String apkName = null;
                try {
                    apkName = appInfoService.getAppInfo(appVersion.getAppId(), null).getAPKName();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                if (apkName == null || "".equals(apkName)) {
                    return "redirect:/dev/flatform/app/appversionadd?id=" + appVersion.getAppId()
                            + "&error=error1";
                }
                apkFileName = apkName + "-" + appVersion.getVersionNo() + ".apk";
                File targetFile = new File(path, apkFileName);
                if (!targetFile.exists()) {
                    targetFile.mkdirs();
                }
                try {
                    attach.transferTo(targetFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "redirect:/dev/flatform/app/appversionadd?id=" + appVersion.getAppId()
                            + "&error=error2";
                }
                downloadLink = request.getContextPath() + "/statics/uploadfiles/" + apkFileName;
                apkLocPath = path + File.separator + apkFileName;
            } else {
                return "redirect:/dev/flatform/app/appversionadd?id=" + appVersion.getAppId()
                        + "&error=error3";
            }
        }
        appVersion.setCreatedBy(((DevUser) session.getAttribute(Constants.DEV_USER_SESSION)).getId());
        appVersion.setCreationDate(new Date());
        appVersion.setDownloadLink(downloadLink);
        appVersion.setApkLocPath(apkLocPath);
        appVersion.setApkFileName(apkFileName);
        try {
            if (appVersionService.appsysAdd(appVersion)) {
                return "redirect:/dev/flatform/app/list";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/dev/flatform/app/appversionadd?id=" + appVersion.getAppId();
    }

    @RequestMapping(value = "/appversionmodify", method = RequestMethod.GET)
    public String modifyAppVersion(@RequestParam("vid") String versionId,
                                   @RequestParam("aid") String appId,
                                   Model model) {
        AppVersion appVersion = appVersionService.getAppVersionById(Integer.parseInt(versionId));
        List<AppVersion> appVersionList = appVersionService.getAppVersionList(Integer.parseInt(appId));
        model.addAttribute(appVersion);
        model.addAttribute("appVersionList", appVersionList);
        return "developer/appversionmodify";
    }

    @RequestMapping(value = "/appversionmodifysave", method = RequestMethod.POST)
    public String modifyAppVersionSave(AppVersion appVersion, HttpSession session,
                                       HttpServletRequest request,
                                       @RequestParam(value = "attach") MultipartFile attach) {
        String apkLocPath = null;
        String downloadLink = null;
        String apkFileName = null;

        if (!attach.isEmpty()) {
            String path = request.getSession().getServletContext().getRealPath("statics" + File.separator + "uploadfiles");
            String oldFileName = attach.getOriginalFilename();
            String prefix = FilenameUtils.getExtension(oldFileName);
            int fileSize = 5000000;
            if (attach.getSize() > fileSize) {
                request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_4);
                return "developer/appversionmodify";
            } else if ("apk".equalsIgnoreCase(prefix)) {
                String apkName = appInfoService.getAppInfo(appVersion.getAppId(), null).getAPKName();
                apkFileName = apkName + "-" + appVersion.getVersionNo() + ".apk";
                File targetFile = new File(path,apkFileName);
                if (!targetFile.exists()){
                    targetFile.mkdirs();
                }
                try {
                    attach.transferTo(targetFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    request.setAttribute("fileUploadError",Constants.FILEUPLOAD_ERROR_2);
                    return "developer/appversionmodify";
                }
                downloadLink = request.getContextPath() + "/statics/uploadfiles/" + apkFileName;
                apkLocPath = path + File.separator + apkFileName;
            }else {
                request.setAttribute("fileUploadError",Constants.FILEUPLOAD_ERROR_3);
                return "developer/appversionmodify";
            }
        }
        appVersion.setModifyBy(((DevUser) session.getAttribute(Constants.DEV_USER_SESSION)).getId());
        appVersion.setModifyDate(new Date());
        appVersion.setDownloadLink(downloadLink);
        appVersion.setApkLocPath(apkLocPath);
        appVersion.setApkFileName(apkFileName);
        if (appVersionService.modifyVersion(appVersion)){
            return "redirect:/dev/flatform/app/list";
        }
        return "developer/appinfomodify";
    }
    @RequestMapping(value="/export")
    public void export2(HttpServletResponse response,HttpSession session) throws Exception{
        String[] titles ={"id","软件名称","APK名称","软件大小(单位M)","所属平台","一级分类","二级分类","三级分类",
        "状态","下载次数","最新版本号"};
        String[] valueKey ={"id","softwareName","APKName","softwareSize","flatformName","categoryLevel1Name","categoryLevel2Name",
        "categoryLevel3Name","statusName","versionNo"};
        List<AppInfo> appInfoList = (List<AppInfo>) session.getAttribute("appInfoListToExcel");
        ExcelUtil.exportData(response, "app信息", titles, valueKey, appInfoList);

    }
    @RequestMapping(value = "/appview/{id}")
    public String appview(@PathVariable int id, Model model){
        AppInfo appinfo = appInfoService.getAppInfoById(id);
        List<AppVersion> appVersionList = appVersionService.getAppVersionList(id);
        model.addAttribute(appinfo);
        model.addAttribute("appVersionList",appVersionList);
        return "developer/appinfoview";
    }
}
