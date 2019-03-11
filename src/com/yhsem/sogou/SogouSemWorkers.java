/**
 * <b>项目名：</b>YHZF<br/>
 * <b>包名：</b>com.yhsem.sogou<br/>
 * <b>文件名：</b>SogouSemWorkers.java<br/>
 * <b>日期：</b>2019-3-10-下午7:36:57<br/>
 * <b>Copyright 2012-2015 WangYi. All Rights Reserved.<br/>
 *
 */
package com.yhsem.sogou;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.opencsv.CSVReader;
import com.sogou.api.client.utils.FileDownloadUtils;
import com.sogou.api.client.utils.ZipUtils;

/**
 * 
 * <b>类名称：</b>SogouSemWorkers<br/>
 * <b>类描述：</b>〈一句话功能简述〉<br/>
 * <b>类详细描述：</b>〈功能详细描述〉<br/>
 * <b>创建人：</b>WangYi<br/>
 * <b>修改人：</b>WangYi<br/>
 * <b>修改时间：</b>2019-3-10 下午7:36:57<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version <br/>
 * 
 */
public class SogouSemWorkers {
    public static final String module = SogouSemWorkers.class.getName();
    public static String FOLDER = UtilProperties.getPropertyValue("sem.properties", "SOGOU_FOLDR_PATH");

    public static boolean processingReport(DispatchContext dctx, GenericValue userLogin, String accountId,
            Date rptDate, String rptTypeId, String fileUrl, String reportId) throws IOException,
            GenericServiceException, GenericEntityException {
        if (UtilValidate.areEqual("REGION", rptTypeId)) {
            return processingRegionReport(dctx, userLogin, accountId, rptDate, fileUrl, reportId);
        } else if (UtilValidate.areEqual("KEYWORD", rptTypeId)) {
            return processingKeywordReport(dctx, userLogin, accountId, rptDate, fileUrl, reportId);
        }
        return false;
    }

    public static boolean processingRegionReport(DispatchContext dctx, GenericValue userLogin, String accountId,
            Date rptDate, String fileUrl, String reportId) throws IOException, GenericServiceException,
            GenericEntityException {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        List<GenericValue> recordList = delegator.findByAnd("SemRegionRpt",
                UtilMisc.toMap("accountId", accountId, "rptDate", rptDate), null, false);
        if (UtilValidate.isEmpty(recordList)) {
            String reportFile = FOLDER + reportId + ".gzip";
            String reportFileCsv = FOLDER + reportId + ".csv";
            FileDownloadUtils.downloadFile(fileUrl, reportFile);

            ZipUtils.unGzipFile(reportFile, reportFileCsv);
            File file = new File(reportFileCsv);
            InputStream input = new FileInputStream(file);
            CSVReader c = new CSVReader(new InputStreamReader(input, "GBK"), ',');

            String[] nextLine;
            int n = 0;
            while ((nextLine = c.readNext()) != null) {
                n++;
                if (n > 2) {
                    String rptId = delegator.getNextSeqId("SemRegionRpt");

                    ModelService createSemRegionRptService = dispatcher.getDispatchContext().getModelService(
                            "createSemRegionRpt");
                    Map<String, Object> paramMap = FastMap.newInstance();

                    paramMap.put("rptId", rptId);
                    paramMap.put("accountId", accountId);
                    paramMap.put("rptDate", rptDate);
                    paramMap.put("regionId", null);
                    paramMap.put("regionName", nextLine[3]);
                    paramMap.put("cityId", null);
                    paramMap.put("cityName", nextLine[4]);
                    paramMap.put("impression", Long.valueOf(0));
                    paramMap.put("click", Long.valueOf(nextLine[9]));
                    paramMap.put("cost", new BigDecimal(nextLine[6]));
                    paramMap.put("ctr", Double.valueOf(0));
                    paramMap.put("cpc", new BigDecimal(nextLine[8]));
                    paramMap.put("cpm", new BigDecimal(0));
                    paramMap.put("position", Double.valueOf(0));
                    paramMap.put("conversion", new BigDecimal(0));
                    paramMap.put("bridgeConversion", BigDecimal.ZERO);
                    paramMap.put("planId", null);
                    paramMap.put("planName", null);
                    paramMap.put("locatingMethod", nextLine[5]);

                    paramMap.put("userLogin", userLogin);
                    Map<String, Object> createSemRegionRptMap = createSemRegionRptService.makeValid(paramMap,
                            ModelService.IN_PARAM);
                    dispatcher.runSync(createSemRegionRptService.name, createSemRegionRptMap);
                }
            }
            c.close();
        }
        return true;
    }

    public static boolean processingKeywordReport(DispatchContext dctx, GenericValue userLogin, String accountId,
            Date rptDate, String fileUrl, String reportId) throws IOException, GenericServiceException,
            GenericEntityException {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        List<GenericValue> recordList = delegator.findByAnd("SemKeyWordRpt",
                UtilMisc.toMap("accountId", accountId, "rptDate", rptDate), null, false);
        if (UtilValidate.isEmpty(recordList)) {
            String reportFile = FOLDER + reportId + ".gzip";
            String reportFileCsv = FOLDER + reportId + ".csv";
            FileDownloadUtils.downloadFile(fileUrl, reportFile);

            ZipUtils.unGzipFile(reportFile, reportFileCsv);
            File file = new File(reportFileCsv);

            InputStream input = new FileInputStream(file);
            CSVReader c = new CSVReader(new InputStreamReader(input, "GBK"), ',');

            String[] nextLine;
            int n = 0;
            while ((nextLine = c.readNext()) != null) {
                n++;
                if (n > 2) {
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

                    paramMap.put("impression", Long.valueOf(0));
                    paramMap.put("click", Long.valueOf(nextLine[11]));
                    paramMap.put("cost", new BigDecimal(nextLine[9]));
                    paramMap.put("ctr", Double.valueOf(0));
                    paramMap.put("cpc", new BigDecimal(nextLine[10]));
                    paramMap.put("cpm", new BigDecimal(0));
                    paramMap.put("position", Double.valueOf(0));
                    paramMap.put("conversion", new BigDecimal(0));
                    paramMap.put("bridgeConversion", BigDecimal.ZERO);
                    paramMap.put("locatingMethod", null);

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
