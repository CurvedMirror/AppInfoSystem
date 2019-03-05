package cn.appsys.service.developer;

import cn.appsys.dao.appinfo.AppInfoMapper;
import cn.appsys.dao.appversion.AppVersionMapper;
import cn.appsys.pojo.AppVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppVersionServiceImpl implements AppVersionService {

    @Autowired
    private AppVersionMapper appVersionMapper;
    @Autowired
    private AppInfoMapper appInfoMapper;

    @Override
    public AppVersion getAppVersionById(Integer id) {
        return appVersionMapper.getAppVersionById(id);
    }

    @Override
    public boolean deleteApkFile(Integer id) {
        return appVersionMapper.deleteApkFile(id);
    }

    @Override
    public boolean appsysAdd(AppVersion appVersion) {
        if(appVersionMapper.add(appVersion)){
            Integer  versionId = appVersion.getId();
            appInfoMapper.updateVersionId(versionId, appVersion.getAppId());
            return true;
        }
        return false;
    }

    @Override
    public List<AppVersion> getAppVersionList(Integer appId) {
        return appVersionMapper.getAppVersionList(appId);
    }
}
