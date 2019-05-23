/**
 * <b>项目名：</b>YHZF<br/>
 * <b>包名：</b>com.yhsem.qihoo<br/>
 * <b>文件名：</b>ReportInstance.java<br/>
 * <b>日期：</b>2019-5-22-上午12:27:01<br/>
 * <b>Copyright 2012-2015 WangYi. All Rights Reserved.<br/>
 *
 */
package com.yhsem.qihoo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * <b>类名称：</b>ReportInstance<br/>
 * <b>类描述：</b>〈一句话功能简述〉<br/>
 * <b>类详细描述：</b>〈功能详细描述〉<br/>
 * <b>创建人：</b>WangYi<br/>
 * <b>修改人：</b>WangYi<br/>
 * <b>修改时间：</b>2019-5-22 上午12:27:01<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version <br/>
 * 
 */
public class ReportInstance {
    public AccountInstance ai;

    public ReportInstance() {
        super();
    }

    public ReportInstance(AccountInstance ai) {
        super();
        this.ai = ai;
    }

    public ReportInstance(GenericValue account) {
        super();
        AccountInstance ai = new AccountInstance(account.getString("accountName"), account.getString("accountPwd"),
                account.getString("accountToken"), account.getString("apiSecret"));
        this.ai = ai;
    }

    public boolean validateAccessToken() {
        if (this.ai.getAccessToken() == null) {
            this.ai.getAccessTokenFromServer();
            if (this.ai.getAccessToken() == null) {
                return false;
            }
        }
        return true;
    }

    public String getRegionReportId(Delegator delegator, GenericValue account) throws Exception {
        return getRegionReportId(delegator, account, UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), -1),
                UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), -1L));
    }

    public String getRegionReportId(Delegator delegator, GenericValue account, Date startDate, Date endDate)
            throws Exception {
        boolean at = validateAccessToken();
        if (!at) {
            return null;
        }

        AccountInstance ai = this.ai;

        String reportId = "";
        String startDateString = UtilDateTime.toDateString(startDate, "yyyy-MM-dd");
        String startEndString = UtilDateTime.toDateString(endDate, "yyyy-MM-dd");

        String url = "https://api.e.360.cn/2.0/report/cityCount";

        Map<String, String> headerPara = new HashMap<String, String>();
        headerPara.put("apiKey", ai.getApiKey());
        headerPara.put("accessToken", ai.getAccessToken());

        Map<String, String> bodyPara = new HashMap<String, String>();
        bodyPara.put("startDate", startDateString);
        bodyPara.put("endDate", startEndString);

        JSONObject jo = QihooHttpClientUtil.postRequest(url, headerPara, bodyPara);
        if (jo != null) {
            int totalPage = jo.getIntValue("totalPage");
            int totalNumber = jo.getIntValue("totalNumber");
            reportId = totalPage + "-" + totalNumber;
        }

        return reportId;
    }

    public String getKeywordReportId(Delegator delegator, GenericValue account) throws Exception {
        return getKeywordReportId(delegator, account, UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), -1),
                UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), -1L));
    }

    public String getKeywordReportId(Delegator delegator, GenericValue account, Date startDate, Date endDate)
            throws Exception {
        boolean at = validateAccessToken();
        if (!at) {
            return null;
        }

        AccountInstance ai = this.ai;

        String reportId = "";
        String startDateString = UtilDateTime.toDateString(startDate, "yyyy-MM-dd");
        String startEndString = UtilDateTime.toDateString(endDate, "yyyy-MM-dd");

        String url = "https://api.e.360.cn/2.0/report/keywordCount";

        Map<String, String> headerPara = new HashMap<String, String>();
        headerPara.put("apiKey", ai.getApiKey());
        headerPara.put("accessToken", ai.getAccessToken());

        Map<String, String> bodyPara = new HashMap<String, String>();
        bodyPara.put("startDate", startDateString);
        bodyPara.put("endDate", startEndString);

        JSONObject jo = QihooHttpClientUtil.postRequest(url, headerPara, bodyPara);
        if (jo != null) {
            int totalPage = jo.getIntValue("totalPage");
            int totalNumber = jo.getIntValue("totalNumber");
            reportId = totalPage + "-" + totalNumber;
        }

        return reportId;
    }

    public JSONObject getRegionReport(Delegator delegator, GenericValue account, int page) throws Exception {
        return getRegionReport(delegator, account, UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), -1),
                UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), -1L), page);
    }

    public JSONObject getRegionReport(Delegator delegator, GenericValue account, Date startDate, Date endDate, int page)
            throws Exception {
        boolean at = validateAccessToken();
        if (!at) {
            return null;
        }

        AccountInstance ai = this.ai;

        String startDateString = UtilDateTime.toDateString(startDate, "yyyy-MM-dd");
        String startEndString = UtilDateTime.toDateString(endDate, "yyyy-MM-dd");

        String url = "https://api.e.360.cn/2.0/report/city";

        Map<String, String> headerPara = new HashMap<String, String>();
        headerPara.put("apiKey", ai.getApiKey());
        headerPara.put("accessToken", ai.getAccessToken());

        Map<String, String> bodyPara = new HashMap<String, String>();
        bodyPara.put("startDate", startDateString);
        bodyPara.put("endDate", startEndString);
        bodyPara.put("page", page + "");

        JSONObject jo = QihooHttpClientUtil.postRequest(url, headerPara, bodyPara);

        return jo;
    }

    public JSONObject getKeywordReport(Delegator delegator, GenericValue account, int page) throws Exception {
        return getKeywordReport(delegator, account, UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), -1),
                UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), -1L), page);
    }

    public JSONObject getKeywordReport(Delegator delegator, GenericValue account, Date startDate, Date endDate, int page)
            throws Exception {
        boolean at = validateAccessToken();
        if (!at) {
            return null;
        }

        AccountInstance ai = this.ai;

        String startDateString = UtilDateTime.toDateString(startDate, "yyyy-MM-dd");
        String startEndString = UtilDateTime.toDateString(endDate, "yyyy-MM-dd");

        String url = "https://api.e.360.cn/2.0/report/keyword";

        Map<String, String> headerPara = new HashMap<String, String>();
        headerPara.put("apiKey", ai.getApiKey());
        headerPara.put("accessToken", ai.getAccessToken());

        Map<String, String> bodyPara = new HashMap<String, String>();
        bodyPara.put("startDate", startDateString);
        bodyPara.put("endDate", startEndString);
        bodyPara.put("page", page + "");

        JSONObject jo = QihooHttpClientUtil.postRequest(url, headerPara, bodyPara);

        return jo;
    }

}
