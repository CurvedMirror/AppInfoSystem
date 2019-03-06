package cn.appsys.service.developer;


import cn.appsys.dao.appinfo.AppInfoMapper;
import cn.appsys.pojo.AppInfo;
import cn.appsys.pojo.DataDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppInfoServiceImpl implements AppInfoService {

    @Autowired
    private AppInfoMapper appInfoMapper;

    @Override
    public List<AppInfo> getAppInfoList(String softwareName, String status,String flatformId, String categoryLevel1, String categoryLevel2, String categoryLevel3, Integer currentPageNo, Integer pageSize) {
        currentPageNo = (currentPageNo-1)*pageSize;
        return appInfoMapper.getAppInfoList(softwareName,status,flatformId,categoryLevel1,categoryLevel2,categoryLevel3,currentPageNo,pageSize);
    }

    @Override
    public int getAppInfoCount(String softwareName, String status,String flatformId, String categoryLevel1, String categoryLevel2, String categoryLevel3) {
        return appInfoMapper.getAppInfoCount(softwareName,status,flatformId,categoryLevel1,categoryLevel2,categoryLevel3);
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
        return appInfoMapper.getAppInfo(id,APKName);
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
}
