1、包的下载

    jar包存放在resources目录下，sso-sdk-java-0.2.1.jar


2、下载完成后pom中引入依赖

    <dependency>
        <groupId>com.bianjie.sso</groupId>
        <artifactId>sso-sdk-java</artifactId>
        <version>0.2.1</version>
    </dependency>

3、接口调用

首先需要创建Client对象。

    Client client = Client.NewClient(String APPID, String APPSecret, String sdkURI);
参数：
    
    APPID:应用APPID

    APPSecret:下载的应用APPSecret

    sdkURI:SSO接口调用地址

（1）校验token的方法

    String resp = client.VerifyToken(String token);
参数：

    token:应用前端传入的cookie/session中存储的token
返回值：

    {
        "code":"001001008",
        "data":{
            "active":true,  #活跃状态
            "user_no":"C001200001"  #用户编号
            },
        "message":"success",
        "request_id":"6ad6a0370e618c9424dafcbc75aabca6"
    }

（2）获取用户信息的方法

    String resp = client.UserInfo(String userNo);
参数：

    userNo：用户编号
返回值：

    {
        "code":"001003010",
        "data":{
            "mobile":"13482490231", #用户手机号
            "no":"C001200001",  #用户编号
            "org_name":"QA_质量管理",   #机构名称
            "org_no":"C0012",   #机构编号
            "real_name":"QA_测试专用",  #人员名称
            "role_id":2,    #角色id
            "username":"xuyang@bianjie.ai"  #登录用户名
            },
        "message":"success",
        "request_id":"6ad6a0370e618c9424dafcbc75aabca6"
    }

（3）单点退出

    String resp = client.Logout(String userNo, String appId);  
参数：

    userNo：用户编号
    appId：应用APPID
返回值：

    {
        "code":"001003010",
        "message":"success",
        "request_id":"a3786f98526a3cf839e1f902dc1a733d"
    }

4、SSO系统主动退出登录时，会调用应用系统的回调域， 调用时数据参数是加密的，需要进行解密

解密方法：

    String decrypt = EncryptDecryptUtil.decrypt(String str,String appSecret);
参数：

    str： 加密的字符串
    appSecret:下载的应用appSecret

返回，解密后的数据：

    {
        "notify_type":"logout", #回调类型
        "user_no":"C001200001" #用户编号
    }