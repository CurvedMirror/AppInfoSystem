package cn.appsys.service.developer;


import cn.appsys.dao.appinfo.AppInfoMapper;
import cn.appsys.dao.appversion.AppVersionMapper;
import cn.appsys.pojo.AppInfo;
import cn.appsys.pojo.AppVersion;
import cn.appsys.pojo.DataDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class AppInfoServiceImpl implements AppInfoService {

    @Autowired
    private AppInfoMapper appInfoMapper;
    @Autowired
    private AppVersionMapper appVersionMapper;

    @Override
    public List<AppInfo> getAppInfoList(String softwareName, String status, String flatformId, String categoryLevel1, String categoryLevel2, String categoryLevel3, Integer currentPageNo, Integer pageSize) {
        currentPageNo = (currentPageNo - 1) * pageSize;
        return appInfoMapper.getAppInfoList(softwareName, status, flatformId, categoryLevel1, categoryLevel2, categoryLevel3, currentPageNo, pageSize);
    }

    @Override
    public int getAppInfoCount(String softwareName, String status, String flatformId, String categoryLevel1, String categoryLevel2, String categoryLevel3) {
        return appInfoMapper.getAppInfoCount(softwareName, status, flatformId, categoryLevel1, categoryLevel2, categoryLevel3);
    }

    @Override
    public List<DataDictionary> getDataDictionaryList(String typeCode) {
        return appInfoMapper.getDataDictionaryList(typeCode);
    }

    @Override
    public boolean addAppInfo(AppInfo appInfo) {
        return appInfoMapper.addAppInfo(appInfo);
    }

    @Override
    public AppInfo getAppInfo(Integer id, String APKName) {
        return appInfoMapper.getAppInfo(id, APKName);
    }

    @Override
    public AppInfo getAppInfoByID(Integer id) {
        return appInfoMapper.getAppInfoByID(id);
    }

    @Override
    public boolean appinfomodifysave(AppInfo appInfo) {
        return appInfoMapper.appinfomodifysave(appInfo);
    }

    @Override
    public boolean deleteAppLogo(Integer id) {
        return appInfoMapper.deleteAppLogo(id);
    }

    @Override
    public AppInfo getAppInfoById(Integer id) {
        return appInfoMapper.getAppInfoById(id);
    }

    @Override
    public boolean appsysdelAppInfo(Integer id) {
        boolean flag = false;
        //获取到版本列表
        List<AppVersion> appVersionList = appVersionMapper.getAppVersionList(id);
        for (AppVersion appVersion : appVersionList) {
            //删除apk文件
            File file = new File(appVersion.getApkLocPath());
            file.delete();
        }
        //删除app版本信息
        appVersionMapper.delVersion(id);
        //删除logo图片
        AppInfo appInfo = appInfoMapper.getAppInfoById(id);
        File file = new File(appInfo.getLogoLocPath());
        file.delete();
        //删除app基础信息
        if (appInfoMapper.delAppInfo(id)) {
            flag = true;
        }
        return flag;
    }

    @Override
    public boolean updateStatus(AppInfo appInfo) {
         /*
        点击下架的时候根据id获取appinfo，
        通过appinfo获取状态，如果状态是2或5就可以上架
        如果是4才能下架
         */
        switch (appInfo.getStatus()) {
            case 2:
                appInfo.setStatus(4);
                appInfoMapper.updateStatus(appInfo);
                break;
            case 5:
                appInfo.setStatus(4);
                appInfoMapper.updateStatus(appInfo);
                break;
            case 4:
                appInfo.setStatus(5);
                appInfoMapper.updateStatus(appInfo);
                break;
            default:
                return false;
        }

        return true;
    }
}
