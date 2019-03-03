package cn.appsys.controller.developer;

import cn.appsys.pojo.DevUser;
import cn.appsys.service.developer.DevUserService;
import cn.appsys.tools.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author jie
 * @date 2019/3/2 -14:38
 */
@Controller
@RequestMapping("/dev")
public class DevLoginController {

    @Autowired
    private DevUserService devUserService;

    @RequestMapping("/login")
    public String login(){
        return "devlogin";
    }

    @RequestMapping("/dologin")
    public String dologin(String devCode, String devPassword, HttpSession session, HttpServletRequest request){
        DevUser devUser = devUserService.doLogin(devCode,devPassword);
        if (null != devUser) {
            session.setAttribute(Constants.DEV_USER_SESSION,devUser);
            return "redirect:/dev/flatform/main";
        }else {
            request.setAttribute("error","用户名或密码不正确");
            return "devlogin";
        }
    }

    @RequestMapping(value="/flatform/main")
    public String main(HttpSession session){
        if(session.getAttribute(Constants.DEV_USER_SESSION) == null){
            return "redirect:/dev/login";
        }
        return "developer/main";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "/devlogin";
    }
}
