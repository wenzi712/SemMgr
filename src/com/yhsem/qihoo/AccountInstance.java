/**
 * <b>项目名：</b>YHZF<br/>
 * <b>包名：</b>com.yhsem.qihoo<br/>
 * <b>文件名：</b>AccountInstance.java<br/>
 * <b>日期：</b>2019-5-22-上午12:25:51<br/>
 * <b>Copyright 2012-2015 WangYi. All Rights Reserved.<br/>
 *
 */
package com.yhsem.qihoo;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * <b>类名称：</b>AccountInstance<br/>
 * <b>类描述：</b>〈一句话功能简述〉<br/>
 * <b>类详细描述：</b>〈功能详细描述〉<br/>
 * <b>创建人：</b>WangYi<br/>
 * <b>修改人：</b>WangYi<br/>
 * <b>修改时间：</b>2019-5-22 上午12:25:51<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version <br/>
 * 
 */
public class AccountInstance {
    public static final String module = AccountInstance.class.getName();

    /**
     * 
     */
    public AccountInstance() {
        super();
        // TODO 自动生成的构造函数存根
    }

    /**
     * @param username
     * @param passwd
     * @param apiKey
     */
    public AccountInstance(String username, String passwd, String apiKey, String apiSecret) {
        super();
        this.username = username;
        this.passwd = passwd;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    private String username;
    private String passwd;
    private String apiKey;
    private String uid;
    private String accessToken;
    private String apiSecret;

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *            the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the passwd
     */
    public String getPasswd() {
        return passwd;
    }

    /**
     * @param passwd
     *            the passwd to set
     */
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    /**
     * @return the apiKey
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * @param apiKey
     *            the apiKey to set
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * @return the uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param uid
     *            the uid to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return the accessToken
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @param accessToken
     *            the accessToken to set
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * @return the apiSecret
     */
    public String getApiSecret() {
        return apiSecret;
    }

    /**
     * @param apiSecret
     *            the apiSecret to set
     */
    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    /**
     * 获取账号加密密码值<br/>
     * 
     * @return
     * @throws Exception
     */
    public String getAesEncryptPwd() throws Exception {
        return Encrypt.getAesEncrypt(this.getPasswd(), this.getApiSecret());
    }

    /**
     * 从服务器获取账号的 AccessToken<br/>
     * 
     * @return
     */
    public void getAccessTokenFromServer() {
        String url = "https://api.e.360.cn/account/clientLogin";

        Map<String, String> headerPara = new HashMap<String, String>();
        headerPara.put("apiKey", this.getApiKey());

        Map<String, String> bodyPara = new HashMap<String, String>();
        bodyPara.put("username", this.getUsername());
        try {
            bodyPara.put("passwd", this.getAesEncryptPwd());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject jo = QihooHttpClientUtil.postRequest(url, headerPara, bodyPara);
        if (jo != null) {
            this.setUid(jo.getString("uid"));
            this.setAccessToken(jo.getString("accessToken"));
        }
    }

    public static void main(String[] args) {
        AccountInstance ai = new AccountInstance("1517270513@qq.com", "YHMD360#11", "35cb5a516317ae9104315b5af8bdc71b",
                "0c52e57bbc196312f743bbb913da3b84");
        try {
            ai.getAccessTokenFromServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
