package cn.appsys.controller.backend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author jie
 * @date 2019/3/2 -14:31
 */
@Controller
@RequestMapping("/manager")
public class UserLoginController {
    @RequestMapping("/login")
    public String login(){
        return "backendlogin";
    }
}
