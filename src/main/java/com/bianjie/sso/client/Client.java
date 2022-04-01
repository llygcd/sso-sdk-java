package com.bianjie.sso.client;

import com.alibaba.fastjson.JSON;
import com.bianjie.sso.common.Constant;
import com.bianjie.sso.utils.EncryptDecryptUtil;
import com.bianjie.sso.utils.HttpUtil;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class Client {
    private String AppId;
    private String AppSecret;
    private String SdkURI;

    public Client(String appId, String appSecret, String sdkURI) {
        AppId = appId;
        AppSecret = appSecret;
        SdkURI = sdkURI;
    }

    /**
     * 获取Client对象
     *
     * @param appId     应用 appId
     * @param appSecret 应用 appSecret
     * @param sdkURI    SSO后台调用地址
     * @return
     */
    public static Client NewClient(String appId, String appSecret, String sdkURI) {
        return new Client(appId, appSecret, sdkURI == null || sdkURI == "" ? Constant.sdkURI : sdkURI);
    }

    /**
     * 校验token
     *
     * @param token 应用前端传入的cookie/session中存储的token
     * @return
     */
    public String VerifyToken(String token) {
        //处理origData
        Map<String, String> origDataMap = new HashMap<>();
        origDataMap.put("token", token);
        String origData = JSON.toJSONString(origDataMap);

        //处理encrypted
        String encrypted = null;
        try {
            EncryptDecryptUtil instance = EncryptDecryptUtil.getInstance();
            encrypted = instance.privateSignature(origData, this.AppSecret);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //处理请求路径
        String uri = this.SdkURI + "/open/api/v1/verify/token";
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("app_id", this.AppId);
        requestMap.put("orig_data", origData);
        requestMap.put("sign_data", encrypted);

        //发送请求
        String requestJson = JSON.toJSONString(requestMap);
        String s = HttpUtil.doPut(uri, requestJson);

        return s;
    }

    /**
     * 获取用户信息
     *
     * @param usrNo 用户编号
     * @return
     */
    public String UserInfo(String usrNo) {
        //处理origData
        Map<String, String> origDataMap = new HashMap<>();
        origDataMap.put("user_no", usrNo);
        String origData = JSON.toJSONString(origDataMap);

        //处理encrypted
        String encrypted = null;
        try {
            EncryptDecryptUtil instance = EncryptDecryptUtil.getInstance();
            encrypted = instance.privateSignature(origData, this.AppSecret);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //处理请求路径
        String uri = this.SdkURI + "/open/api/v1/userinfo" + "?app_id=" + HttpUtil.transfUri(this.AppId)
                + "&orig_data=" + HttpUtil.transfUri(origData) + "&sign_data=" + HttpUtil.transfUri(encrypted);
        String s = HttpUtil.doGet(uri);
        return s;
    }

    /**
     * 获取机构下的所有人员信息
     *
     * @param orgNo 机构编号
     * @return
     */
    public String GetUsers(String orgNo) {
        //处理origData
        Map<String, String> origDataMap = new HashMap<>();
        origDataMap.put("org_no", orgNo);
        String origData = JSON.toJSONString(origDataMap);

        //处理encrypted
        String encrypted = null;
        try {
            EncryptDecryptUtil instance = EncryptDecryptUtil.getInstance();
            encrypted = instance.privateSignature(origData, this.AppSecret);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //处理请求路径
        String uri = this.SdkURI + "/open/api/v1/users?app_id=" + HttpUtil.transfUri(this.AppId)
                + "&orig_data=" + HttpUtil.transfUri(origData) + "&sign_data=" + HttpUtil.transfUri(encrypted);
        String s = HttpUtil.doGet(uri);
        return s;
    }

    /**
     * 单点退出
     *
     * @param userNo 用户编号
     * @param appId  应用appId
     * @return
     */
    public String Logout(String userNo, String appId) {
        //处理origData
        Map<String, String> origDataMap = new HashMap<>();
        origDataMap.put("user_no", userNo);
        origDataMap.put("app_no", appId);
        String origData = JSON.toJSONString(origDataMap);

        //处理encrypted
        String encrypted = null;
        try {
            EncryptDecryptUtil instance = EncryptDecryptUtil.getInstance();
            encrypted = instance.privateSignature(origData, this.AppSecret);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //处理请求路径
        String uri = this.SdkURI + "/open/api/v1/logout?app_id=" + HttpUtil.transfUri(this.AppId)
                + "&orig_data=" + HttpUtil.transfUri(origData) + "&sign_data=" + HttpUtil.transfUri(encrypted);
        String s = HttpUtil.doGet(uri);

        return s;
    }

    /**
     * 查看app状态
     *
     * @param redirectUrl 重定向地址
     * @return
     */
    public String AppStatus(String redirectUrl) {
        String appUri = "/open/api/v1/apps/" + this.AppId + "/status";
        String uri = this.SdkURI + appUri;

        //处理请求路径
        String url = uri + "?redirect_uri=" + HttpUtil.transfUri(redirectUrl);
        String s = HttpUtil.doGet(url);
        return s;
    }
}
