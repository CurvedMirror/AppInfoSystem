package cn.appsys.service.backend;


import cn.appsys.dao.appinfo.AppInfoMapper;
import cn.appsys.pojo.AppInfo;
import cn.appsys.pojo.DataDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppServiceImpl implements AppService {

    @Autowired
    private AppInfoMapper appInfoMapper;

    @Override
    public List<AppInfo> getAppInfoList(String softwareName, String flatformId, String categoryLevel1, String categoryLevel2, String categoryLevel3, Integer currentPageNo, Integer pageSize) {
        currentPageNo = (currentPageNo-1)*pageSize;
        return appInfoMapper.getAppInfoList(softwareName,"1",flatformId,categoryLevel1,categoryLevel2,categoryLevel3,currentPageNo,pageSize);
    }

    @Override
    public int getAppInfoCount(String softwareName, String flatformId, String categoryLevel1, String categoryLevel2, String categoryLevel3) {
        return appInfoMapper.getAppInfoCount(softwareName,null,flatformId,categoryLevel1,categoryLevel2,categoryLevel3);
    }

    @Override
    public List<DataDictionary> getDataDictionaryList(String typeCode) {
        return appInfoMapper.getDataDictionaryList(typeCode);
    }

    @Override
    public AppInfo getAppInfo(Integer id, String APKName) {
        return appInfoMapper.getAppInfo(id,APKName);
    }

    @Override
    public boolean updateStatus(AppInfo appInfo) {
        return appInfoMapper.updateStatus(appInfo);
    }
}
