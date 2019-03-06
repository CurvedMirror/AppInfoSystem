package cn.appsys.controller.backend;

import cn.appsys.pojo.BackendUser;
import cn.appsys.service.backend.BackendUserService;
import cn.appsys.tools.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author jie
 * @date 2019/3/2 -14:31
 */
@Controller
@RequestMapping("/manager")
public class UserLoginController {

    @Autowired
    private BackendUserService backendUserService;

    @RequestMapping(value = "/login")
    public String login() {
        return "backendlogin";
    }

    @RequestMapping(value = "/dologin", method = RequestMethod.POST)
    public String doLogin(@RequestParam String userCode, @RequestParam String userPassword, HttpServletRequest request, HttpSession session) {
        //调用service方法，进行用户匹配
        BackendUser user = null;
        user = backendUserService.login(userCode, userPassword);
        //登录成功
        if (null != user) {
            //放入session
            session.setAttribute(Constants.USER_SESSION, user);
            //页面跳转（main.jsp）
            return "redirect:/manager/backend/main";
        } else {
            //页面跳转（login.jsp）带出提示信息--转发
            request.setAttribute("error", "用户名或密码不正确");
            return "backendlogin";
        }
    }
    @RequestMapping(value="/backend/main")
    public String main(HttpSession session){
        if(session.getAttribute(Constants.USER_SESSION) == null){
            return "redirect:/manager/login";
        }
        return "backend/main";
    }
    @RequestMapping(value="/logout")
    public String logout(HttpSession session){
        //清除session
        session.removeAttribute(Constants.USER_SESSION);
        return "backendlogin";
    }
}
