/**
 * <b>项目名：</b>YHZF<br/>
 * <b>包名：</b>com.yhsem.baidu<br/>
 * <b>文件名：</b>BaiduSemWorkers.java<br/>
 * <b>日期：</b>2019-3-5-上午11:19:20<br/>
 * <b>Copyright 2012-2015 WangYi. All Rights Reserved.<br/>
 *
 */
package com.yhsem.baidu;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.baidu.drapi.autosdk.util.DownloadUtil;
import com.opencsv.CSVReader;

/**
 * 
 * <b>类名称：</b>BaiduSemWorkers<br/>
 * <b>类描述：</b>〈一句话功能简述〉<br/>
 * <b>类详细描述：</b>〈功能详细描述〉<br/>
 * <b>创建人：</b>WangYi<br/>
 * <b>修改人：</b>WangYi<br/>
 * <b>修改时间：</b>2019-3-5 上午11:19:20<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version <br/>
 * 
 */
public class BaiduSemWorkers {
    public static final String module = BaiduSemWorkers.class.getName();

    public static boolean processingReport(DispatchContext dctx, GenericValue userLogin, String accountId,
            Date rptDate, String rptTypeId, String fileUrl) throws IOException, GenericServiceException,
            GenericEntityException {
        if (UtilValidate.areEqual("REGION", rptTypeId)) {
            return processingRegionReport(dctx, userLogin, accountId, rptDate, fileUrl);
        } else if (UtilValidate.areEqual("KEYWORD", rptTypeId)) {
            return processingKeywordReport(dctx, userLogin, accountId, rptDate, fileUrl);
        }
        return false;
    }

    public static boolean processingRegionReport(DispatchContext dctx, GenericValue userLogin, String accountId,
            Date rptDate, String fileUrl) throws IOException, GenericServiceException, GenericEntityException {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        List<GenericValue> recordList = delegator.findByAnd("SemRegionRpt",
                UtilMisc.toMap("accountId", accountId, "rptDate", rptDate), null, false);
        if (UtilValidate.isEmpty(recordList)) {
            byte[] b = DownloadUtil.downloadFile(fileUrl);
            CSVReader c = new CSVReader(new StringReader(new String(b, "GBK")), '\t');
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
                    paramMap.put("regionId", nextLine[3]);
                    paramMap.put("regionName", nextLine[4]);
                    paramMap.put("cityId", nextLine[5]);
                    paramMap.put("cityName", nextLine[6]);
                    paramMap.put("impression", Long.valueOf(nextLine[7]));
                    paramMap.put("click", Long.valueOf(nextLine[8]));
                    paramMap.put("cost", new BigDecimal(nextLine[9]));
                    String ctr = nextLine[10].replace("%", "");
                    paramMap.put("ctr", Double.valueOf(ctr) / 100);
                    paramMap.put("cpc", new BigDecimal(nextLine[11]));
                    paramMap.put("cpm", new BigDecimal(nextLine[12]));
                    paramMap.put("position", Double.valueOf(nextLine[14]));
                    paramMap.put("conversion", new BigDecimal(nextLine[13]));
                    paramMap.put("bridgeConversion", BigDecimal.ZERO);
                    paramMap.put("locatingMethod", null);
                    paramMap.put("planId", null);
                    paramMap.put("planName", null);

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
            Date rptDate, String fileUrl) throws IOException {
        return true;
    }
}
