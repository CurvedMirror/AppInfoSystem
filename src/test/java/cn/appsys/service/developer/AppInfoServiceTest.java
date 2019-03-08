package cn.appsys.service.developer;

import cn.appsys.pojo.DataDictionary;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author jie
 * @date 2019/3/4 -11:43
 */
public class AppInfoServiceTest {

    @Test
    public void getDataDictionaryList() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext-mybatis.xml");
        AppInfoService appInfoService = ctx.getBean(AppInfoService.class);

        List<DataDictionary> user_type = appInfoService.getDataDictionaryList("USER_TYPE");
        for (DataDictionary dataDictionary : user_type) {
            System.out.println(dataDictionary.getValueName());
        }
    }
}