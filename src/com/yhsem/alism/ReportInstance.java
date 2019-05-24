/**
 * <b>项目名：</b>YHZF<br/>
 * <b>包名：</b>com.yhsem.alism<br/>
 * <b>文件名：</b>ReportInstance.java<br/>
 * <b>日期：</b>2019-5-11-下午11:20:57<br/>
 * <b>Copyright 2012-2015 WangYi. All Rights Reserved.<br/>
 *
 */
package com.yhsem.alism;

import java.util.Date;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
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
 * <b>修改时间：</b>2019-5-11 下午11:20:57<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version 1.0<br/>
 * 
 */
public class ReportInstance {

    public ReportInstance() {
        super();
    }

    public String getRegionReportId(Delegator delegator, GenericValue account) throws Exception {
        return getRegionReportId(delegator, account, UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), -1),
                UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), -1L));
    }

    public String getRegionReportId(Delegator delegator, GenericValue account, Date startDate, Date endDate)
            throws Exception {
        String reportId = "";
        String startDateString = UtilDateTime.toDateString(startDate, "yyyy-MM-dd");
        String startEndString = UtilDateTime.toDateString(endDate, "yyyy-MM-dd");
        String body = "\"body\":{\"startDate\": \"" + startDateString + "\",\"endDate\": \"" + startEndString
                + "\",\"reportType\":" + SmConstant.RPT_TYPE_REGION + ",\"unitOfTime\": 5}";
        String r = SmHttpClientUtil.postRequest(SmConstant.HOST_URL + SmConstant.URI_GET_REPORT,
                account.getString("accountName"), account.getString("accountPwd"), account.getString("accountToken"),
                body);
        if (JSONObject.isValidObject(r)) {
            JSONObject json = JSONObject.parseObject(r);
            if (UtilValidate.areEqual("0", json.getJSONObject("header").getString("status"))) {
                // CREATED – 已建立报表任务
                // STARTED – 已经启动报表任务
                // FINISHED – 报表任务已经完成
                // FAILED – 报表生成失败
                if (!UtilValidate.areEqual("FAILED", json.getJSONObject("body").getString("status"))) {
                    reportId = json.getJSONObject("body").getString("taskId");
                }
            }
        }
        return reportId;
    }

    public String getKeywordReportId(Delegator delegator, GenericValue account) throws Exception {
        return getKeywordReportId(delegator, account, UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), -1),
                UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), -1L));
    }

    public String getKeywordReportId(Delegator delegator, GenericValue account, Date startDate, Date endDate)
            throws Exception {
        String reportId = "";
        String startDateString = UtilDateTime.toDateString(startDate, "yyyy-MM-dd");
        String startEndString = UtilDateTime.toDateString(endDate, "yyyy-MM-dd");
        String body = "\"body\":{\"startDate\": \"" + startDateString + "\",\"endDate\": \"" + startEndString
                + "\",\"reportType\":" + SmConstant.RPT_TYPE_REGION + ",\"unitOfTime\": 14}";
        String r = SmHttpClientUtil.postRequest(SmConstant.HOST_URL + SmConstant.URI_GET_REPORT,
                account.getString("accountName"), account.getString("accountPwd"), account.getString("accountToken"),
                body);
        if (JSONObject.isValidObject(r)) {
            JSONObject json = JSONObject.parseObject(r);
            if (UtilValidate.areEqual("0", json.getJSONObject("header").getString("status"))) {
                // CREATED – 已建立报表任务
                // STARTED – 已经启动报表任务
                // FINISHED – 报表任务已经完成
                // FAILED – 报表生成失败
                if (!UtilValidate.areEqual("FAILED", json.getJSONObject("body").getString("status"))) {
                    reportId = json.getJSONObject("body").getString("taskId");
                }
            }
        }
        return reportId;
    }

    /**
     * getReportStateAndFileIdByReportId(这里用一句话描述这个方法的作用)<br/>
     * (这里描述这个方法适用条件 – 可选)<br/>
     * 
     * @return 报告状态 [1：等待中; 2：处理中; 3：处理成功; 4：处理异常]
     * @param delegator
     * @param reportId
     * @return
     * @throws Exception
     */
    public Map<String, String> getReportStateAndFileIdByReportId(Delegator delegator, GenericValue account,
            String reportId) throws Exception {

        String status = "";
        String fileId = "";
        String body = "\"body\":{\"taskId\": \"" + reportId + "\"}";
        String r = SmHttpClientUtil.postRequest(SmConstant.HOST_URL + SmConstant.URI_GET_TASK_STATE,
                account.getString("accountName"), account.getString("accountPwd"), account.getString("accountToken"),
                body);
        if (JSONObject.isValidObject(r)) {
            JSONObject json = JSONObject.parseObject(r);
            if (UtilValidate.areEqual("0", json.getJSONObject("header").getString("status"))) {
                // CREATED – 已建立报表任务
                // STARTED – 已经启动报表任务
                // FINISHED – 报表任务已经完成
                // FAILED – 报表生成失败
                status = json.getJSONObject("body").getString("status");
                if (UtilValidate.areEqual("FINISHED", status)) {
                    status = "3";
                } else if (UtilValidate.areEqual("STARTED", status)) {
                    status = "2";
                } else if (UtilValidate.areEqual("CREATED", status)) {
                    status = "1";
                } else if (UtilValidate.areEqual("FAILED", status)) {
                    status = "4";
                }
                if (UtilValidate.areEqual("3", status)) {
                    fileId = json.getJSONObject("body").getString("fileId");
                }
            }
        }
        System.out.println(status);
        System.out.println(fileId);

        return UtilMisc.toMap("status", status, "fileId", fileId);
    }

    public static boolean getReportByFileId(Delegator delegator, GenericValue account, Long fileId) throws Exception {
        return SmHttpClientUtil.postRequestDownloadCsv(SmConstant.HOST_URL + SmConstant.URI_DOWNLOAD,
                account.getString("accountName"), account.getString("accountPwd"), account.getString("accountToken"),
                fileId);
    }

    public static void main(String[] args) {
        ReportInstance ri = new ReportInstance();

    }
}
