package com.glface.modules.controller;

import com.alibaba.fastjson.JSONObject;
import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.R;
import com.glface.common.utils.HttpClientUtil;
import com.glface.common.web.ApiCode;
import com.glface.model.SysMenu;
import com.glface.model.SysUser;
import com.glface.modules.service.AccountService;
import com.glface.modules.service.MenuService;
import com.glface.modules.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 微信扫描登录
 */
@Slf4j
@RestController
@RequestMapping("/system/wx")
public class WxController {
    private final static Logger logger = LoggerFactory.getLogger(WxController.class);

    @Value("${myself.appid}")
    private String  appid ;

    @Value("${myself.appsecret}")
    private String  appsecret ;

    @Value("${myself.callBack}")
    private String  callBack ;

    @Value("${myself.bindCallBack}")
    private String  bindCallBack ;

    @Value("${myself.callBackEms}")
    private String  callBackEms ;

    @Value("${myself.bindCallBackEms}")
    private String  bindCallBackEms ;

    @Value("${myself.scope}")
    private String  scope;

    @Resource
    private UserService userService;
    @Resource
    private MenuService menuService;
    @Resource
    private AccountService accountService;

    @RequestMapping(value = "/getCode")
    public R<Object> getCode() throws UnsupportedEncodingException {
        String oauthUrl = "https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
        String redirect_uri = URLEncoder.encode(callBack, "utf-8"); ;
        oauthUrl =  oauthUrl.replace("APPID",appid).replace("REDIRECT_URI",redirect_uri).replace("SCOPE",scope);
        Object data = new DynamicBean.Builder()
                .setPV("oauthUrl", oauthUrl)
                .build().getObject();
        return R.ok(data);
    }

    /**
     * 微信绑定
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/getBindCode")
    public R<Object> getBindCode() throws UnsupportedEncodingException {
        String oauthUrl = "https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
        String redirect_uri = URLEncoder.encode(bindCallBack, "utf-8"); ;
        oauthUrl =  oauthUrl.replace("APPID",appid).replace("REDIRECT_URI",redirect_uri).replace("SCOPE",scope);
        Object data = new DynamicBean.Builder()
                .setPV("oauthUrl", oauthUrl)
                .build().getObject();
        return R.ok(data);
    }

    @RequestMapping(value = "/ems/getCode")
    public R<Object> getEmsCode() throws UnsupportedEncodingException {
        String oauthUrl = "https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
        String redirect_uri = URLEncoder.encode(callBackEms, "utf-8"); ;
        oauthUrl =  oauthUrl.replace("APPID",appid).replace("REDIRECT_URI",redirect_uri).replace("SCOPE",scope);
        Object data = new DynamicBean.Builder()
                .setPV("oauthUrl", oauthUrl)
                .build().getObject();
        return R.ok(data);
    }

    /**
     * 微信绑定
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/ems/getBindCode")
    public R<Object> getEmsBindCode() throws UnsupportedEncodingException {
        String oauthUrl = "https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
        String redirect_uri = URLEncoder.encode(bindCallBackEms, "utf-8"); ;
        oauthUrl =  oauthUrl.replace("APPID",appid).replace("REDIRECT_URI",redirect_uri).replace("SCOPE",scope);
        Object data = new DynamicBean.Builder()
                .setPV("oauthUrl", oauthUrl)
                .build().getObject();
        return R.ok(data);
    }
    /**
     * 微信扫码后回调
     * @param code
     * @param state
     * @return
     */
    @RequestMapping("/login")
    public R<Object> loginByWx(String code, String state,HttpServletRequest request){
        logger.info("进入授权回调,code:{},state:{}",code,state);
        //1.通过code获取access_token
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        url = url.replace("APPID",appid).replace("SECRET",appsecret).replace("CODE",code);
        String tokenInfoStr =  HttpClientUtil.doGetStr(url);
        JSONObject tokenInfoObject = JSONObject.parseObject(tokenInfoStr);
        logger.info("tokenInfoObject:{}",tokenInfoObject);
        if(tokenInfoObject==null||!tokenInfoObject.containsKey("openid")){
            return R.fail(ApiCode.ACCOUNT_WX_USER_INFOR_FAIL.getMsg());
        }
        //2.通过access_token和openid获取用户信息
        String userInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
        userInfoUrl = userInfoUrl.replace("ACCESS_TOKEN",tokenInfoObject.getString("access_token")).replace("OPENID",tokenInfoObject.getString("openid"));
        String userInfoStr =  HttpClientUtil.doGetStr(userInfoUrl);
        logger.info("userInfoObject:{}",userInfoStr);
        JSONObject userInfo = JSONObject.parseObject(userInfoStr);
        String unionid = userInfo.getString("unionid");
        SysUser user = accountService.loginByUnionid(unionid, request);
        if(user == null){
            Object data = new DynamicBean.Builder()
                    .setPV("unionid", unionid)
                    .build().getObject();
            return R.ok(data);
        }
        //保存用户信息
        user.setHeadimgurl(userInfo.getString("headimgurl"));
        user.setCity(userInfo.getString("city"));
        user.setCountry(userInfo.getString("country"));
        user.setOpenid(userInfo.getString("openid"));
        userService.update(user);
        //菜单权限
        List<SysMenu> menuList;
        if (user.isAdmin()) {
            menuList = menuService.findAll();
        } else {
            menuList = userService.findMenusByUserId(user.getId());
        }

        List<Object> permissions = new DynamicBean.Builder()
                .setPV("permission", null)
                .build()
                .copyList(menuList);
        // 构造响应数据
        Object data = new DynamicBean.Builder()
                .setPV("token", user.getToken())
                .setPV("id", user.getId())
                .setPV("nickname", user.getNickname(), String.class)
                .setPV("access", permissions)
                .setPV("code", user.getOfficeCode())
                .setPV("isAdmin",user.isAdmin())
                .setPV("headimgurl",user.getHeadimgurl())
                .setPV("account", user.getAccount()).build().getObject();
        return R.ok(data);
    }

    @RequestMapping("/bind")
    public R<Object> bind(String code, String state,HttpServletRequest request){
        logger.info("进入授权回调,code:{},state:{}",code,state);
        //1.通过code获取access_token
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        url = url.replace("APPID",appid).replace("SECRET",appsecret).replace("CODE",code);
        String tokenInfoStr =  HttpClientUtil.doGetStr(url);
        JSONObject tokenInfoObject = JSONObject.parseObject(tokenInfoStr);
        logger.info("tokenInfoObject:{}",tokenInfoObject);
        if(tokenInfoObject==null||!tokenInfoObject.containsKey("openid")){
            return R.fail(ApiCode.ACCOUNT_WX_USER_INFOR_FAIL.getMsg());
        }
        //2.通过access_token和openid获取用户信息
        String userInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
        userInfoUrl = userInfoUrl.replace("ACCESS_TOKEN",tokenInfoObject.getString("access_token")).replace("OPENID",tokenInfoObject.getString("openid"));
        String userInfoStr =  HttpClientUtil.doGetStr(userInfoUrl);
        logger.info("userInfoObject:{}",userInfoStr);
        JSONObject userInfo = JSONObject.parseObject(userInfoStr);
        String unionid = userInfo.getString("unionid");
        //已绑定其他用户
        SysUser bindedUser = userService.findUserByUnionid(unionid);
        if(bindedUser!=null){
            return R.fail(ApiCode.ACCOUNT_WX_USER_BINDED.getMsg());
        }
        SysUser user = com.glface.modules.sys.utils.UserUtils.getUser();
        //保存用户信息
        user.setHeadimgurl(userInfo.getString("headimgurl"));
        user.setCity(userInfo.getString("city"));
        user.setCountry(userInfo.getString("country"));
        user.setOpenid(userInfo.getString("openid"));
        user.setUnionid(unionid);
        userService.update(user);
        return R.ok();
    }

    @RequestMapping("/unbind")
    public R<Object> unbind(){
        SysUser user = com.glface.modules.sys.utils.UserUtils.getUser();
        //保存用户信息
        user.setHeadimgurl(null);
        user.setCity(null);
        user.setCountry(null);
        user.setOpenid(null);
        user.setUnionid(null);
        userService.update(user);
        return R.ok();
    }
}
