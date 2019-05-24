/**
 * <b>项目名：</b>YHZF<br/>
 * <b>包名：</b>com.yhsem.qihoo<br/>
 * <b>文件名：</b>QihooSemWorkers.java<br/>
 * <b>日期：</b>2019-5-22-上午12:27:42<br/>
 * <b>Copyright 2012-2015 WangYi. All Rights Reserved.<br/>
 *
 */
package com.yhsem.qihoo;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * <b>类名称：</b>QihooSemWorkers<br/>
 * <b>类描述：</b>〈一句话功能简述〉<br/>
 * <b>类详细描述：</b>〈功能详细描述〉<br/>
 * <b>创建人：</b>WangYi<br/>
 * <b>修改人：</b>WangYi<br/>
 * <b>修改时间：</b>2019-5-22 上午12:27:42<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version <br/>
 * 
 */
public class QihooSemWorkers {

    public static final String module = QihooSemWorkers.class.getName();

    public static boolean processingReport(DispatchContext dctx, GenericValue userLogin, GenericValue acct,
            Date rptDate, String rptTypeId, GenericValue record) throws Exception {
        if (UtilValidate.areEqual("REGION", rptTypeId)) {
            return processingRegionReport(dctx, userLogin, acct, rptDate, record);
        } else if (UtilValidate.areEqual("KEYWORD", rptTypeId)) {
            return processingKeywordReport(dctx, userLogin, acct, rptDate, record);
        }
        return false;
    }

    public static boolean processingRegionReport(DispatchContext dctx, GenericValue userLogin, GenericValue acct,
            Date rptDate, GenericValue record) throws Exception {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        String accountId = acct.getString("accountId");
        List<GenericValue> recordList = delegator.findByAnd("SemRegionRpt",
                UtilMisc.toMap("accountId", accountId, "rptDate", rptDate), null, false);
        if (UtilValidate.isEmpty(recordList)) {
            String reportId = record.getString("reportId");
            String _page = reportId.substring(0, reportId.indexOf("-"));
            int page = Integer.parseInt(_page);
            ReportInstance pi = new ReportInstance(acct);
            for (int i = 1; i <= page; i++) {
                JSONObject jo = pi.getRegionReport(delegator, acct, rptDate, rptDate, i);
                if (jo.containsKey("cityList")) {
                    JSONArray cityList = jo.getJSONArray("cityList");
                    for (int j = 0; j < cityList.size(); j++) {
                        JSONObject rpt = (JSONObject) cityList.get(j);

                        String rptId = delegator.getNextSeqId("SemRegionRpt");

                        ModelService createSemRegionRptService = dispatcher.getDispatchContext().getModelService(
                                "createSemRegionRpt");
                        Map<String, Object> paramMap = FastMap.newInstance();

                        paramMap.put("rptId", rptId);
                        paramMap.put("accountId", accountId);
                        paramMap.put("rptDate", rptDate);
                        paramMap.put("regionId", null);
                        paramMap.put("regionName", rpt.getString("provinceName"));
                        paramMap.put("cityId", rpt.getString("cityId"));
                        paramMap.put("cityName", rpt.getString("cityName"));

                        paramMap.put("impression", rpt.getLong("views"));
                        paramMap.put("click", rpt.getLong("clicks"));
                        paramMap.put("cost", rpt.getBigDecimal("totalCost"));
                        paramMap.put("ctr", Double.valueOf(0));
                        paramMap.put("cpc", BigDecimal.ZERO);
                        paramMap.put("cpm", BigDecimal.ZERO);
                        paramMap.put("position", BigDecimal.ZERO);
                        paramMap.put("conversion", BigDecimal.ZERO);
                        paramMap.put("bridgeConversion", BigDecimal.ZERO);
                        paramMap.put("locatingMethod", null);

                        paramMap.put("planId", rpt.getString("campaignId"));
                        paramMap.put("planName", rpt.getString("campaignName"));
                        paramMap.put("infoFlow", "0");

                        paramMap.put("userLogin", userLogin);
                        Map<String, Object> createSemRegionRptMap = createSemRegionRptService.makeValid(paramMap,
                                ModelService.IN_PARAM);
                        dispatcher.runSync(createSemRegionRptService.name, createSemRegionRptMap);
                    }
                }
            }

        }
        return true;
    }

    public static boolean processingKeywordReport(DispatchContext dctx, GenericValue userLogin, GenericValue acct,
            Date rptDate, GenericValue record) throws Exception {

        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        String accountId = acct.getString("accountId");
        List<GenericValue> recordList = delegator.findByAnd("SemKeyWordRpt",
                UtilMisc.toMap("accountId", accountId, "rptDate", rptDate), null, false);
        if (UtilValidate.isEmpty(recordList)) {
            String reportId = record.getString("reportId");
            String _page = reportId.substring(0, reportId.indexOf("-"));
            int page = Integer.parseInt(_page);
            ReportInstance pi = new ReportInstance(acct);
            for (int i = 1; i <= page; i++) {
                JSONObject jo = pi.getKeywordReport(delegator, acct, rptDate, rptDate, i);
                if (jo.containsKey("keywordList")) {
                    JSONArray keywordList = jo.getJSONArray("keywordList");
                    for (int j = 0; j < keywordList.size(); j++) {
                        JSONObject rpt = (JSONObject) keywordList.get(j);

                        String rptId = delegator.getNextSeqId("SemKeyWordRpt");

                        ModelService createSemKeyWordRptService = dispatcher.getDispatchContext().getModelService(
                                "createSemKeyWordRpt");
                        Map<String, Object> paramMap = FastMap.newInstance();

                        paramMap.put("rptId", rptId);
                        paramMap.put("accountId", accountId);
                        paramMap.put("rptDate", rptDate);
                        paramMap.put("impression", rpt.getLong("views"));
                        paramMap.put("click", rpt.getLong("clicks"));
                        paramMap.put("cost", rpt.getBigDecimal("totalCost"));
                        paramMap.put("ctr", Double.valueOf(0));
                        paramMap.put("cpc", BigDecimal.ZERO);
                        paramMap.put("cpm", BigDecimal.ZERO);
                        paramMap.put("position", BigDecimal.ZERO);
                        paramMap.put("conversion", BigDecimal.ZERO);
                        paramMap.put("bridgeConversion", BigDecimal.ZERO);
                        paramMap.put("locatingMethod", null);

                        paramMap.put("planId", rpt.getString("campaignId"));
                        paramMap.put("planName", rpt.getString("campaignName"));
                        paramMap.put("unitId", rpt.getString("groupId"));
                        paramMap.put("unitName", rpt.getString("groupName"));
                        paramMap.put("keyWordId", rpt.getString("keywordId"));
                        paramMap.put("keyWord", rpt.getString("keyword"));
                        paramMap.put("infoFlow", "0");

                        paramMap.put("userLogin", userLogin);
                        Map<String, Object> createSemKeyWordRptMap = createSemKeyWordRptService.makeValid(paramMap,
                                ModelService.IN_PARAM);
                        dispatcher.runSync(createSemKeyWordRptService.name, createSemKeyWordRptMap);
                    }
                }
            }

        }
        return true;
    }

}
