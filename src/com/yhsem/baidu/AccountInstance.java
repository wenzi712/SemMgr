/**
 * <b>项目名：</b>YHZF<br/>
 * <b>包名：</b>com.yhsem.baidu<br/>
 * <b>文件名：</b>AccountInstance.java<br/>
 * <b>日期：</b>2019-3-4-下午5:26:48<br/>
 * <b>Copyright 2012-2015 WangYi. All Rights Reserved.<br/>
 *
 */
package com.yhsem.baidu;

import java.util.Arrays;
import java.util.List;

import com.baidu.drapi.autosdk.core.CommonService;
import com.baidu.drapi.autosdk.core.ResHeader;
import com.baidu.drapi.autosdk.core.ResHeaderUtil;
import com.baidu.drapi.autosdk.core.ServiceFactory;
import com.baidu.drapi.autosdk.exception.ApiException;
import com.baidu.drapi.autosdk.sms.service.AccountInfo;
import com.baidu.drapi.autosdk.sms.service.AccountService;
import com.baidu.drapi.autosdk.sms.service.GetAccountInfoRequest;
import com.baidu.drapi.autosdk.sms.service.GetAccountInfoResponse;

/**
 *
 * <b>类名称：</b>AccountInstance<br/>
 * <b>类描述：</b>〈一句话功能简述〉<br/>
 * <b>类详细描述：</b>〈功能详细描述〉<br/>
 * <b>创建人：</b>WangYi<br/>
 * <b>修改人：</b>WangYi<br/>
 * <b>修改时间：</b>2019-3-4 下午5:26:48<br/>
 * <b>修改备注：</b><br/>
 *
 * @version <br/>
 *
 */
/**
 * 
 * <b>类名称：</b>AccountInstance<br/>
 * <b>类描述：</b>〈一句话功能简述〉<br/>
 * <b>类详细描述：</b>〈功能详细描述〉<br/>
 * <b>创建人：</b>WangYi<br/>
 * <b>修改人：</b>WangYi<br/>
 * <b>修改时间：</b>2019-2-22 上午10:42:45<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version <br/>
 * 
 */
public class AccountInstance {

    private CommonService factory;
    private AccountService accountService;

    private static String[] fields = { "userId", "cost" };
    private static String SUCCESS = "success";

    public AccountInstance() {
        try {
            this.factory = ServiceFactory.getInstance();
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    public AccountInstance(String username, String password, String token) {
        this.setFactory(username, password, token);
    }

    private void setFactory(String username, String password, String token) {
        try {
            this.factory = ServiceFactory.getInstance();
            factory.setUsername(username);
            factory.setPassword(password);
            factory.setToken(token);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    public CommonService getFactory() {
        return this.factory;
    }

    private AccountInfo getAccoutInfo() throws ApiException {
        GetAccountInfoRequest request = new GetAccountInfoRequest();
        request.setAccountFields(Arrays.asList(fields));
        GetAccountInfoResponse response = accountService.getAccountInfo(request);
        ResHeader rheader = ResHeaderUtil.getResHeader(accountService, true);
        if (SUCCESS.equals(rheader.getDesc()) && rheader.getStatus() == 0) {
            List<AccountInfo> datas = response.getData();
            return datas.get(0);
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            AccountInstance account = new AccountInstance("searchlab", "SearchUX2016",
                    "1085ffb557845b3d75d75b7762c8910a");

            account.accountService = account.factory.getService(AccountService.class);
            AccountInfo accountInfo = account.getAccoutInfo();
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

}
