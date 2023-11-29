package com.example.just_auth_template.controller;

import com.example.just_auth_template.util.JwtUtils;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 第三方登录
 * @author Tu_Yooo
 * @Date 2021/6/27 19:00
 */
@RestController
@RequestMapping("/oauth")
public class JustAuthController {



    /**
     * 获取授权链接并跳转到第三方授权页面
     *
     * @param response response
     * @throws IOException response可能存在的异常
     */
    @RequestMapping("/render/{source}")
    public Object renderAuth(HttpServletResponse response) throws IOException {
        AuthRequest authRequest = getAuthRequest();
        String token = AuthStateUtils.createState();
        //生成gitee的授权url
        String authorizeUrl = authRequest.authorize(token);
        //将这个url返回给前端Vue
        //由Vue去执行 授权页

        System.out.println(authorizeUrl);
        return authorizeUrl;
    }


    /**
     * 用户在确认第三方平台授权（登录）后， 第三方平台会重定向到该地址，并携带code、state等参数
     * @param callback 第三方回调时的入参
     * @return 第三方平台的用户信息
     */
    @RequestMapping("/callback/{source}")
    public void login(@PathVariable("source") String source, AuthCallback callback, HttpServletResponse response) throws IOException {
        AuthRequest authRequest = getAuthRequest();
        AuthResponse login = authRequest.login(callback);
        //此处可以获取到gitee给我传输过来的用户信息 可以打印看一下,可以根据自己系统的业务逻辑进行相应处理
        //由于我的是demo 所以不进行处理
        System.out.println(login.toString());
        //前后端分离 都是通过jwt token来判断当前用户是否有权限访问
        //我这里生成的一个假的jwt 实际业务中 怎么生成jwt 你们自己定 我这里直接返回了
        //生成JWT
        String jwt = JwtUtils.getJwtToken("admin");
        System.out.println(jwt);
        //设置当请求头中
        response.setHeader("token",jwt);
        //跳转到中转页面
        //这里的地址对应 vue项目中的 中转页面 
        //vue的中转页面 会将token从url中取出 记录到本地
        response.sendRedirect("http://47.93.239.83:9089/?token="+jwt);
    }


    /**
     * 获取授权Request
     *
     * @return AuthRequest
     */
    private AuthRequest getAuthRequest() {
        return new AuthGiteeRequest(AuthConfig.builder()
        //gitee中的id
                .clientId("88ff14b70015228fe73b0601355b7e6f73dd5448e8751ff5fac348b009686273")
                //gitee中的密钥
                .clientSecret("ab878bb6b2f1160d5e98fee178fda56ce86d7cd5b5ebfc77f6c87f5bde44fed2")
              //在gitee中申请的回调地址
                .redirectUri("http://47.93.239.83:9088/oauth/callback/gitee")
                .build());
    }
}

