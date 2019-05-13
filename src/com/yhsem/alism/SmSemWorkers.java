/**
 * <b>项目名：</b>YHZF<br/>
 * <b>包名：</b>com.yhsem.alism<br/>
 * <b>文件名：</b>SmSemWorkers.java<br/>
 * <b>日期：</b>2019-5-13-上午9:54:12<br/>
 * <b>Copyright 2012-2015 WangYi. All Rights Reserved.<br/>
 *
 */
package com.yhsem.alism;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import com.opencsv.CSVReader;

/**
 * 
 * <b>类名称：</b>SmSemWorkers<br/>
 * <b>类描述：</b>〈一句话功能简述〉<br/>
 * <b>类详细描述：</b>〈功能详细描述〉<br/>
 * <b>创建人：</b>WangYi<br/>
 * <b>修改人：</b>WangYi<br/>
 * <b>修改时间：</b>2019-3-13 上午9:54:12<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version <br/>
 * 
 */
public class SmSemWorkers {
    public static final String module = SmSemWorkers.class.getName();

    public static boolean processingReport(DispatchContext dctx, GenericValue userLogin, GenericValue acct,
            Date rptDate, String rptTypeId, String fileUrl) throws Exception {
        if (UtilValidate.areEqual("REGION", rptTypeId)) {
            return processingRegionReport(dctx, userLogin, acct, rptDate, fileUrl);
        } else if (UtilValidate.areEqual("KEYWORD", rptTypeId)) {
            return processingKeywordReport(dctx, userLogin, acct, rptDate, fileUrl);
        }
        return false;
    }

    public static boolean processingRegionReport(DispatchContext dctx, GenericValue userLogin, GenericValue acct,
            Date rptDate, String fileId) throws Exception {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        String accountId = acct.getString("accountId");
        List<GenericValue> recordList = delegator.findByAnd("SemRegionRpt",
                UtilMisc.toMap("accountId", accountId, "rptDate", rptDate), null, false);
        if (UtilValidate.isEmpty(recordList)) {
            // Boolean b = ReportInstance.getReportByFileId(delegator, acct,
            // Long.valueOf(fileId));
            // if (!b) {
            // return false;
            // }
            String reportFileCsv = SmConstant.RPT_FOLDER + fileId + ".csv";
            File file = new File(reportFileCsv);
            InputStream input = new FileInputStream(file);
            CSVReader c = new CSVReader(new InputStreamReader(input, "UTF-8"), ',');
            String[] nextLine;
            int n = 0;
            while ((nextLine = c.readNext()) != null) {
                n++;
                if (n > 1) {
                    String rptId = delegator.getNextSeqId("SemRegionRpt");

                    ModelService createSemRegionRptService = dispatcher.getDispatchContext().getModelService(
                            "createSemRegionRpt");
                    Map<String, Object> paramMap = FastMap.newInstance();

                    paramMap.put("rptId", rptId);
                    paramMap.put("accountId", accountId);
                    paramMap.put("rptDate", rptDate);
                    paramMap.put("regionId", null);
                    paramMap.put("regionName", nextLine[4]);
                    paramMap.put("cityId", null);
                    paramMap.put("cityName", nextLine[5]);
                    paramMap.put("impression", Long.valueOf(nextLine[6]));
                    paramMap.put("click", Long.valueOf(nextLine[7]));
                    paramMap.put("cost", new BigDecimal(nextLine[8]));
                    String ctr = nextLine[9].replace("%", "");
                    paramMap.put("ctr", Double.valueOf(ctr) / 100);
                    paramMap.put("cpc", new BigDecimal(nextLine[10]));
                    paramMap.put("cpm", BigDecimal.ZERO);
                    paramMap.put("position", BigDecimal.ZERO);
                    paramMap.put("conversion", BigDecimal.ZERO);
                    paramMap.put("bridgeConversion", BigDecimal.ZERO);
                    paramMap.put("locatingMethod", null);
                    paramMap.put("planId", null);
                    paramMap.put("planName", null);
                    paramMap.put("infoFlow", "0");

                    paramMap.put("userLogin", userLogin);
                    Map<String, Object> createSemRegionRptMap = createSemRegionRptService.makeValid(paramMap,
                            ModelService.IN_PARAM);
                    dispatcher.runSync(createSemRegionRptService.name, createSemRegionRptMap);
                }
            }
            c.close();
            input.close();
        }
        return true;
    }

    public static boolean processingKeywordReport(DispatchContext dctx, GenericValue userLogin, GenericValue acct,
            Date rptDate, String fileId) throws Exception {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        String accountId = acct.getString("accountId");

        List<GenericValue> recordList = delegator.findByAnd("SemKeyWordRpt",
                UtilMisc.toMap("accountId", accountId, "rptDate", rptDate), null, false);
        if (UtilValidate.isEmpty(recordList)) {
            Boolean b = ReportInstance.getReportByFileId(delegator, acct, Long.valueOf(fileId));
            if (!b) {
                return false;
            }
            String reportFileCsv = SmConstant.RPT_FOLDER + fileId + ".csv";
            File file = new File(reportFileCsv);
            InputStream input = new FileInputStream(file);
            CSVReader c = new CSVReader(new InputStreamReader(input, "UTF-8"), ',');
            String[] nextLine;
            int n = 0;
            while ((nextLine = c.readNext()) != null) {
                n++;
                if (n > 1) {
                    String rptId = delegator.getNextSeqId("SemKeyWordRpt");

                    ModelService createSemKeyWordRptService = dispatcher.getDispatchContext().getModelService(
                            "createSemKeyWordRpt");
                    Map<String, Object> paramMap = FastMap.newInstance();

                    paramMap.put("rptId", rptId);
                    paramMap.put("accountId", accountId);
                    paramMap.put("rptDate", rptDate);
                    paramMap.put("planId", nextLine[3]);
                    paramMap.put("planName", nextLine[4]);
                    paramMap.put("unitId", nextLine[5]);
                    paramMap.put("unitName", nextLine[6].replace("&", ""));
                    paramMap.put("keyWordId", nextLine[7]);
                    paramMap.put("keyWord", nextLine[8]);

                    paramMap.put("impression", Long.valueOf(nextLine[9]));
                    paramMap.put("click", Long.valueOf(nextLine[10]));
                    paramMap.put("cost", new BigDecimal(nextLine[11]));
                    String ctr = nextLine[12].replace("%", "");
                    paramMap.put("ctr", Double.valueOf(ctr) / 100);
                    paramMap.put("cpc", new BigDecimal(nextLine[13]));
                    paramMap.put("cpm", BigDecimal.ZERO);
                    paramMap.put("position", Double.valueOf(nextLine[14]));
                    paramMap.put("conversion", BigDecimal.ZERO);
                    paramMap.put("bridgeConversion", BigDecimal.ZERO);
                    paramMap.put("locatingMethod", null);
                    paramMap.put("infoFlow", "0");

                    paramMap.put("userLogin", userLogin);
                    Map<String, Object> createSemKeyWordRptMap = createSemKeyWordRptService.makeValid(paramMap,
                            ModelService.IN_PARAM);
                    dispatcher.runSync(createSemKeyWordRptService.name, createSemKeyWordRptMap);
                }
            }
            c.close();
        }
        return true;
    }
}
