/**
 * <b>项目名：</b>YHZF<br/>
 * <b>包名：</b>com.yhsem.common<br/>
 * <b>文件名：</b>RptServices.java<br/>
 * <b>日期：</b>2019-3-5-上午11:20:31<br/>
 * <b>Copyright 2012-2015 WangYi. All Rights Reserved.<br/>
 *
 */
package com.yhsem.common;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.yhsem.alism.SmSemWorkers;
import com.yhsem.baidu.AccountInstance;
import com.yhsem.baidu.BaiduSemWorkers;
import com.yhsem.baidu.ReportInstance;
import com.yhsem.helper.SemBaseHelper;
import com.yhsem.helper.SemRptHelper;
import com.yhsem.qihoo.QihooSemWorkers;
import com.yhsem.sogou.SogouSemWorkers;

/**
 * 
 * <b>类名称：</b>RptServices<br/>
 * <b>类描述：</b>〈一句话功能简述〉<br/>
 * <b>类详细描述：</b>〈功能详细描述〉<br/>
 * <b>创建人：</b>WangYi<br/>
 * <b>修改人：</b>WangYi<br/>
 * <b>修改时间：</b>2019-3-5 上午11:20:31<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version <br/>
 * 
 */
public class RptServices {
    public static final String module = RptServices.class.getName();

    public static Map<String, Object> getDailyReport(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Date rptDate = new Date(UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), -1).getTime());

        // 获取所有可用账户
        List<GenericValue> acctList = SemBaseHelper.getAllValidAccount(delegator);
        if (UtilValidate.isNotEmpty(acctList)) {
            GenericValue userLogin = null;
            try {
                userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), true);
            } catch (GenericEntityException e1) {
                e1.printStackTrace();
            }
            for (Iterator<GenericValue> iterator = acctList.iterator(); iterator.hasNext();) {
                GenericValue acct = iterator.next();
                String accountId = acct.getString("accountId");
                String channelId = acct.getString("channelId");

                if (UtilValidate.areEqual("BD", channelId)) {
                    try {
                        AccountInstance account = new AccountInstance(acct.getString("accountName"),
                                acct.getString("accountPwd"), acct.getString("accountToken"));
                        ReportInstance report = new ReportInstance(account);

                        // 区域报告
                        GenericValue record1 = EntityUtil.getFirst(delegator.findByAnd("SemRptRecord", UtilMisc.toMap(
                                "accountId", accountId, "rptDate", rptDate, "rptTypeId", "REGION", "infoFlow", "0"),
                                UtilMisc.toList("accountId"), false));
                        String reportId = null;
                        // 数据为空则新建
                        if (UtilValidate.isEmpty(record1)) {
                            reportId = report.getRegionReportId(delegator, false);
                            ModelService createSemRptRecordService = dispatcher.getDispatchContext().getModelService(
                                    "createSemRptRecord");
                            Map<String, Object> paramMap = FastMap.newInstance();
                            paramMap.putAll(UtilMisc.toMap("recordId", delegator.getNextSeqId("SemRptRecord"),
                                    "accountId", accountId, "rptDate", rptDate, "rptTypeId", "REGION"));
                            paramMap.putAll(UtilMisc.toMap("infoFlow", "0", "isFinished", "0", "reportId", reportId,
                                    "requestTime", UtilDateTime.nowTimestamp()));
                            paramMap.put("userLogin", userLogin);
                            Map<String, Object> createSemRptRecordMap = createSemRptRecordService.makeValid(paramMap,
                                    ModelService.IN_PARAM);
                            dispatcher.runSync(createSemRptRecordService.name, createSemRptRecordMap);
                        }

                        // 关键词报告
                        GenericValue record2 = EntityUtil.getFirst(delegator.findByAnd("SemRptRecord", UtilMisc.toMap(
                                "accountId", accountId, "rptDate", rptDate, "rptTypeId", "KEYWORD", "infoFlow", "0"),
                                UtilMisc.toList("accountId"), false));
                        // 获取报告ID
                        String reportId2 = null;
                        if (UtilValidate.isEmpty(record2)) {
                            reportId2 = report.getKeywordReportId(delegator, false);

                            ModelService createSemRptRecordService = dispatcher.getDispatchContext().getModelService(
                                    "createSemRptRecord");
                            Map<String, Object> paramMap = FastMap.newInstance();
                            paramMap.putAll(UtilMisc.toMap("recordId", delegator.getNextSeqId("SemRptRecord"),
                                    "accountId", accountId, "rptDate", rptDate, "rptTypeId", "KEYWORD"));
                            paramMap.putAll(UtilMisc.toMap("infoFlow", "0", "isFinished", "0", "reportId", reportId2,
                                    "requestTime", UtilDateTime.nowTimestamp()));
                            paramMap.put("userLogin", userLogin);
                            Map<String, Object> createSemRptRecordMap = createSemRptRecordService.makeValid(paramMap,
                                    ModelService.IN_PARAM);
                            dispatcher.runSync(createSemRptRecordService.name, createSemRptRecordMap);
                        }

                        // 信息流
                        if (UtilValidate.areEqual("1", acct.getString("infoFlow"))) {
                            // 信息流区域报告
                            GenericValue record3 = EntityUtil.getFirst(delegator.findByAnd("SemRptRecord", UtilMisc
                                    .toMap("accountId", accountId, "rptDate", rptDate, "rptTypeId", "REGION",
                                            "infoFlow", "1"), UtilMisc.toList("accountId"), false));
                            String reportId3 = null;
                            if (UtilValidate.isEmpty(record3)) {
                                reportId3 = report.getRegionReportId(delegator, true);
                                ModelService createSemRptRecordService = dispatcher.getDispatchContext()
                                        .getModelService("createSemRptRecord");
                                Map<String, Object> paramMap = FastMap.newInstance();
                                paramMap.putAll(UtilMisc.toMap("recordId", delegator.getNextSeqId("SemRptRecord"),
                                        "accountId", accountId, "rptDate", rptDate, "rptTypeId", "REGION"));
                                paramMap.putAll(UtilMisc.toMap("infoFlow", "1", "isFinished", "0", "reportId",
                                        reportId3, "requestTime", UtilDateTime.nowTimestamp()));
                                paramMap.put("userLogin", userLogin);
                                Map<String, Object> createSemRptRecordMap = createSemRptRecordService.makeValid(
                                        paramMap, ModelService.IN_PARAM);
                                dispatcher.runSync(createSemRptRecordService.name, createSemRptRecordMap);
                            }

                            // 信息流关键词报告
                            GenericValue record4 = EntityUtil.getFirst(delegator.findByAnd("SemRptRecord", UtilMisc
                                    .toMap("accountId", accountId, "rptDate", rptDate, "rptTypeId", "KEYWORD",
                                            "infoFlow", "1"), UtilMisc.toList("accountId"), false));
                            // 获取报告ID
                            String reportId4 = null;
                            if (UtilValidate.isEmpty(record4)) {
                                reportId4 = report.getKeywordReportId(delegator, true);

                                ModelService createSemRptRecordService = dispatcher.getDispatchContext()
                                        .getModelService("createSemRptRecord");
                                Map<String, Object> paramMap = FastMap.newInstance();
                                paramMap.putAll(UtilMisc.toMap("recordId", delegator.getNextSeqId("SemRptRecord"),
                                        "accountId", accountId, "rptDate", rptDate, "rptTypeId", "KEYWORD"));
                                paramMap.putAll(UtilMisc.toMap("infoFlow", "1", "isFinished", "0", "reportId",
                                        reportId4, "requestTime", UtilDateTime.nowTimestamp()));
                                paramMap.put("userLogin", userLogin);
                                Map<String, Object> createSemRptRecordMap = createSemRptRecordService.makeValid(
                                        paramMap, ModelService.IN_PARAM);
                                dispatcher.runSync(createSemRptRecordService.name, createSemRptRecordMap);

                            }
                        }
                    } catch (Exception e) {
                        Debug.logError(e.getLocalizedMessage(), module);
                        e.printStackTrace();
                    }
                } else if (UtilValidate.areEqual("SG", channelId)) {
                    // 区域报告
                    try {
                        com.yhsem.sogou.ReportInstance report = new com.yhsem.sogou.ReportInstance(
                                acct.getString("accountName"), acct.getString("accountPwd"),
                                acct.getString("accountToken"));

                        GenericValue record1 = EntityUtil.getFirst(delegator.findByAnd("SemRptRecord", UtilMisc.toMap(
                                "accountId", accountId, "rptDate", rptDate, "rptTypeId", "REGION", "infoFlow", "0"),
                                UtilMisc.toList("accountId"), false));
                        String reportId = null;
                        // 数据为空则新建
                        if (UtilValidate.isEmpty(record1)) {
                            reportId = report.getRegionReportId(delegator);
                            ModelService createSemRptRecordService = dispatcher.getDispatchContext().getModelService(
                                    "createSemRptRecord");
                            Map<String, Object> paramMap = FastMap.newInstance();
                            paramMap.putAll(UtilMisc.toMap("recordId", delegator.getNextSeqId("SemRptRecord"),
                                    "accountId", accountId, "rptDate", rptDate, "rptTypeId", "REGION"));
                            paramMap.putAll(UtilMisc.toMap("infoFlow", "0", "isFinished", "0", "reportId", reportId,
                                    "requestTime", UtilDateTime.nowTimestamp()));
                            paramMap.put("userLogin", userLogin);
                            Map<String, Object> createSemRptRecordMap = createSemRptRecordService.makeValid(paramMap,
                                    ModelService.IN_PARAM);
                            dispatcher.runSync(createSemRptRecordService.name, createSemRptRecordMap);
                        }

                        // 关键词报告
                        GenericValue record2 = EntityUtil.getFirst(delegator.findByAnd("SemRptRecord", UtilMisc.toMap(
                                "accountId", accountId, "rptDate", rptDate, "rptTypeId", "KEYWORD", "infoFlow", "0"),
                                UtilMisc.toList("accountId"), false));
                        // 获取报告ID
                        String reportId2 = null;
                        if (UtilValidate.isEmpty(record2)) {
                            reportId2 = report.getKeywordReportId(delegator);

                            ModelService createSemRptRecordService = dispatcher.getDispatchContext().getModelService(
                                    "createSemRptRecord");
                            Map<String, Object> paramMap = FastMap.newInstance();
                            paramMap.putAll(UtilMisc.toMap("recordId", delegator.getNextSeqId("SemRptRecord"),
                                    "accountId", accountId, "rptDate", rptDate, "rptTypeId", "KEYWORD"));
                            paramMap.putAll(UtilMisc.toMap("infoFlow", "0", "isFinished", "0", "reportId", reportId2,
                                    "requestTime", UtilDateTime.nowTimestamp()));
                            paramMap.put("userLogin", userLogin);
                            Map<String, Object> createSemRptRecordMap = createSemRptRecordService.makeValid(paramMap,
                                    ModelService.IN_PARAM);
                            dispatcher.runSync(createSemRptRecordService.name, createSemRptRecordMap);
                        }
                    } catch (GenericEntityException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (UtilValidate.areEqual("SM", channelId)) {

                    // 区域报告
                    try {
                        com.yhsem.alism.ReportInstance report = new com.yhsem.alism.ReportInstance();

                        GenericValue record1 = EntityUtil.getFirst(delegator.findByAnd("SemRptRecord", UtilMisc.toMap(
                                "accountId", accountId, "rptDate", rptDate, "rptTypeId", "REGION", "infoFlow", "0"),
                                UtilMisc.toList("accountId"), false));
                        String reportId = null;
                        // 数据为空则新建
                        if (UtilValidate.isEmpty(record1)) {
                            reportId = report.getRegionReportId(delegator, acct);
                            ModelService createSemRptRecordService = dispatcher.getDispatchContext().getModelService(
                                    "createSemRptRecord");
                            Map<String, Object> paramMap = FastMap.newInstance();
                            paramMap.putAll(UtilMisc.toMap("recordId", delegator.getNextSeqId("SemRptRecord"),
                                    "accountId", accountId, "rptDate", rptDate, "rptTypeId", "REGION"));
                            paramMap.putAll(UtilMisc.toMap("infoFlow", "0", "isFinished", "0", "reportId", reportId,
                                    "requestTime", UtilDateTime.nowTimestamp()));
                            paramMap.put("userLogin", userLogin);
                            Map<String, Object> createSemRptRecordMap = createSemRptRecordService.makeValid(paramMap,
                                    ModelService.IN_PARAM);
                            dispatcher.runSync(createSemRptRecordService.name, createSemRptRecordMap);
                        }

                        // 关键词报告
                        GenericValue record2 = EntityUtil.getFirst(delegator.findByAnd("SemRptRecord", UtilMisc.toMap(
                                "accountId", accountId, "rptDate", rptDate, "rptTypeId", "KEYWORD", "infoFlow", "0"),
                                UtilMisc.toList("accountId"), false));
                        // 获取报告ID
                        String reportId2 = null;
                        if (UtilValidate.isEmpty(record2)) {
                            reportId2 = report.getKeywordReportId(delegator, acct);

                            ModelService createSemRptRecordService = dispatcher.getDispatchContext().getModelService(
                                    "createSemRptRecord");
                            Map<String, Object> paramMap = FastMap.newInstance();
                            paramMap.putAll(UtilMisc.toMap("recordId", delegator.getNextSeqId("SemRptRecord"),
                                    "accountId", accountId, "rptDate", rptDate, "rptTypeId", "KEYWORD"));
                            paramMap.putAll(UtilMisc.toMap("infoFlow", "0", "isFinished", "0", "reportId", reportId2,
                                    "requestTime", UtilDateTime.nowTimestamp()));
                            paramMap.put("userLogin", userLogin);
                            Map<String, Object> createSemRptRecordMap = createSemRptRecordService.makeValid(paramMap,
                                    ModelService.IN_PARAM);
                            dispatcher.runSync(createSemRptRecordService.name, createSemRptRecordMap);
                        }
                    } catch (GenericEntityException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (UtilValidate.areEqual("QH", channelId)) {
                    // 区域报告
                    try {
                        com.yhsem.qihoo.ReportInstance report = new com.yhsem.qihoo.ReportInstance(acct);

                        GenericValue record1 = EntityUtil.getFirst(delegator.findByAnd("SemRptRecord", UtilMisc.toMap(
                                "accountId", accountId, "rptDate", rptDate, "rptTypeId", "REGION", "infoFlow", "0"),
                                UtilMisc.toList("accountId"), false));
                        String reportId = null;
                        // 数据为空则新建
                        if (UtilValidate.isEmpty(record1)) {
                            reportId = report.getRegionReportId(delegator, acct);
                            ModelService createSemRptRecordService = dispatcher.getDispatchContext().getModelService(
                                    "createSemRptRecord");
                            Map<String, Object> paramMap = FastMap.newInstance();
                            paramMap.putAll(UtilMisc.toMap("recordId", delegator.getNextSeqId("SemRptRecord"),
                                    "accountId", accountId, "rptDate", rptDate, "rptTypeId", "REGION"));
                            paramMap.putAll(UtilMisc.toMap("infoFlow", "0", "isFinished", "0", "reportId", reportId,
                                    "requestTime", UtilDateTime.nowTimestamp()));
                            paramMap.put("userLogin", userLogin);
                            Map<String, Object> createSemRptRecordMap = createSemRptRecordService.makeValid(paramMap,
                                    ModelService.IN_PARAM);
                            dispatcher.runSync(createSemRptRecordService.name, createSemRptRecordMap);
                        }

                        // 关键词报告
                        GenericValue record2 = EntityUtil.getFirst(delegator.findByAnd("SemRptRecord", UtilMisc.toMap(
                                "accountId", accountId, "rptDate", rptDate, "rptTypeId", "KEYWORD", "infoFlow", "0"),
                                UtilMisc.toList("accountId"), false));
                        // 获取报告ID
                        String reportId2 = null;
                        if (UtilValidate.isEmpty(record2)) {
                            reportId2 = report.getKeywordReportId(delegator, acct);

                            ModelService createSemRptRecordService = dispatcher.getDispatchContext().getModelService(
                                    "createSemRptRecord");
                            Map<String, Object> paramMap = FastMap.newInstance();
                            paramMap.putAll(UtilMisc.toMap("recordId", delegator.getNextSeqId("SemRptRecord"),
                                    "accountId", accountId, "rptDate", rptDate, "rptTypeId", "KEYWORD"));
                            paramMap.putAll(UtilMisc.toMap("infoFlow", "0", "isFinished", "0", "reportId", reportId2,
                                    "requestTime", UtilDateTime.nowTimestamp()));
                            paramMap.put("userLogin", userLogin);
                            Map<String, Object> createSemRptRecordMap = createSemRptRecordService.makeValid(paramMap,
                                    ModelService.IN_PARAM);
                            dispatcher.runSync(createSemRptRecordService.name, createSemRptRecordMap);
                        }
                    } catch (GenericEntityException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

        }
        return ServiceUtil.returnSuccess();
    }

    /**
     * 检查报表请求是否有没有返回报表ID,没有则重新发送请求<br/>
     * 
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> checkFailureReportRequest(DispatchContext dctx,
            Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();

        List<GenericValue> recordList = SemRptHelper.getFailureRequestRptRecords(delegator);
        if (UtilValidate.isNotEmpty(recordList)) {

            for (Iterator<GenericValue> iterator = recordList.iterator(); iterator.hasNext();) {
                GenericValue record = iterator.next();

                Date rptDate = record.getDate("rptDate");
                try {
                    String accountId = record.getString("accountId");

                    GenericValue acct = delegator.findOne("SemAccount", UtilMisc.toMap("accountId", accountId), false);
                    String reportId = null;
                    if (UtilValidate.isNotEmpty(acct)) {
                        String channelId = acct.getString("channelId");
                        if (UtilValidate.areEqual("BD", channelId)) {
                            AccountInstance account = new AccountInstance(acct.getString("accountName"),
                                    acct.getString("accountPwd"), acct.getString("accountToken"));
                            ReportInstance report = new ReportInstance(account);

                            if (UtilValidate.areEqual("REGION", record.getString("rptTypeId"))) {// 区域报表
                                if (UtilValidate.areEqual("0", record.getString("infoFlow"))) {
                                    reportId = report.getRegionReportId(delegator, rptDate, rptDate, true);
                                } else {
                                    reportId = report.getRegionReportId(delegator, rptDate, rptDate, false);
                                }
                            } else {// 关键词报告
                                if (UtilValidate.areEqual("0", record.getString("infoFlow"))) {
                                    reportId = report.getKeywordReportId(delegator, rptDate, rptDate, true);
                                } else {
                                    reportId = report.getKeywordReportId(delegator, rptDate, rptDate, false);
                                }
                            }
                        } else if (UtilValidate.areEqual("SG", channelId)) {
                            com.yhsem.sogou.ReportInstance report = new com.yhsem.sogou.ReportInstance(
                                    acct.getString("accountName"), acct.getString("accountPwd"),
                                    acct.getString("accountToken"));
                            if (UtilValidate.areEqual("REGION", record.getString("rptTypeId"))) {// 区域报表
                                reportId = report.getRegionReportId(delegator,
                                        UtilDateTime.getDayStart(new Timestamp(rptDate.getTime())),
                                        UtilDateTime.getDayEnd(new Timestamp(rptDate.getTime())));
                            } else {// 关键词报告
                                reportId = report.getKeywordReportId(delegator,
                                        UtilDateTime.getDayStart(new Timestamp(rptDate.getTime())),
                                        UtilDateTime.getDayEnd(new Timestamp(rptDate.getTime())));
                            }
                        } else if (UtilValidate.areEqual("SM", channelId)) {
                            com.yhsem.alism.ReportInstance report = new com.yhsem.alism.ReportInstance();
                            if (UtilValidate.areEqual("REGION", record.getString("rptTypeId"))) {// 区域报表
                                reportId = report.getRegionReportId(delegator, acct,
                                        UtilDateTime.getDayStart(new Timestamp(rptDate.getTime())),
                                        UtilDateTime.getDayEnd(new Timestamp(rptDate.getTime())));
                            } else {// 关键词报告
                                reportId = report.getKeywordReportId(delegator, acct,
                                        UtilDateTime.getDayStart(new Timestamp(rptDate.getTime())),
                                        UtilDateTime.getDayEnd(new Timestamp(rptDate.getTime())));
                            }

                        } else if (UtilValidate.areEqual("QH", channelId)) {
                            com.yhsem.qihoo.ReportInstance report = new com.yhsem.qihoo.ReportInstance(acct);
                            if (UtilValidate.areEqual("REGION", record.getString("rptTypeId"))) {// 区域报表
                                reportId = report.getRegionReportId(delegator, acct,
                                        UtilDateTime.getDayStart(new Timestamp(rptDate.getTime())),
                                        UtilDateTime.getDayEnd(new Timestamp(rptDate.getTime())));
                            } else {// 关键词报告
                                reportId = report.getKeywordReportId(delegator, acct,
                                        UtilDateTime.getDayStart(new Timestamp(rptDate.getTime())),
                                        UtilDateTime.getDayEnd(new Timestamp(rptDate.getTime())));
                            }

                        }

                        if (UtilValidate.isNotEmpty(reportId)) {
                            boolean beganTransaction = false;
                            try {
                                beganTransaction = TransactionUtil.begin();

                                record.set("reportId", reportId);
                                record.set("requestTime", UtilDateTime.nowTimestamp());

                                delegator.store(record);
                            } catch (Exception e) {
                                e.printStackTrace();
                                TransactionUtil.rollback(beganTransaction, e.getMessage(), e);
                            } finally {
                                TransactionUtil.commit(beganTransaction);
                            }
                        }
                    }
                } catch (GenericEntityException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ServiceUtil.returnSuccess();
    }

    /**
     * 检查并处理已经提交申请报告但未处理完毕的台账<br/>
     * 
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> queryReportStatus(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();

        List<GenericValue> recordList = SemRptHelper.getQueryStatusRptRecords(delegator);
        if (UtilValidate.isNotEmpty(recordList)) {
            for (Iterator<GenericValue> iterator = recordList.iterator(); iterator.hasNext();) {
                GenericValue record = iterator.next();

                try {
                    String accountId = record.getString("accountId");
                    GenericValue acct = delegator.findOne("SemAccount", UtilMisc.toMap("accountId", accountId), false);
                    if (UtilValidate.isNotEmpty(acct)) {
                        String channelId = acct.getString("channelId");
                        String reportId = record.getString("reportId");
                        if (UtilValidate.isNotEmpty(reportId)) {
                            String statusId = null;
                            Map<String, String> smStatus = null;
                            if (UtilValidate.areEqual("BD", channelId)) {
                                AccountInstance account = new AccountInstance(acct.getString("accountName"),
                                        acct.getString("accountPwd"), acct.getString("accountToken"));
                                ReportInstance report = new ReportInstance(account);

                                statusId = report.getReportStateByReportId(delegator, reportId).toString();
                            } else if (UtilValidate.areEqual("SG", channelId)) {
                                com.yhsem.sogou.ReportInstance report = new com.yhsem.sogou.ReportInstance(
                                        acct.getString("accountName"), acct.getString("accountPwd"),
                                        acct.getString("accountToken"));
                                statusId = report.getReportStateByReportId(delegator, reportId).toString();
                            } else if (UtilValidate.areEqual("SM", channelId)) {
                                com.yhsem.alism.ReportInstance report = new com.yhsem.alism.ReportInstance();
                                smStatus = report.getReportStateAndFileIdByReportId(delegator, acct, reportId);
                            } else if (UtilValidate.areEqual("QH", channelId)) {
                                String page = reportId.substring(0, reportId.indexOf("-"));
                                smStatus = UtilMisc.toMap("status", "3", "page", page);
                            }
                            boolean beganTransaction = false;
                            try {
                                beganTransaction = TransactionUtil.begin();
                                if (UtilValidate.areEqual("SM", channelId)) {
                                    record.set("statusId", smStatus.get("status"));
                                    record.set("statusTime", UtilDateTime.nowTimestamp());

                                    record.set("fileUrl", smStatus.get("fileId"));
                                    record.set("fileUrlTime", UtilDateTime.nowTimestamp());
                                } else if (UtilValidate.areEqual("QH", channelId)) {
                                    record.set("statusId", smStatus.get("status"));
                                    record.set("statusTime", UtilDateTime.nowTimestamp());

                                    record.set("fileUrl", smStatus.get("page"));
                                    record.set("fileUrlTime", UtilDateTime.nowTimestamp());
                                } else {
                                    record.set("statusId", statusId);
                                    record.set("statusTime", UtilDateTime.nowTimestamp());
                                }
                                delegator.store(record);
                            } catch (Exception e) {
                                e.printStackTrace();
                                TransactionUtil.rollback(beganTransaction, e.getMessage(), e);
                            } finally {
                                TransactionUtil.commit(beganTransaction);
                            }
                        }
                    }
                } catch (GenericEntityException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ServiceUtil.returnSuccess();
    }

    /**
     * 检查处理未获取报告下载路径的台账<br/>
     * 
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> queryReportFileUrl(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();

        List<GenericValue> recordList = SemRptHelper.getQueryUrlRptRecords(delegator);
        if (UtilValidate.isNotEmpty(recordList)) {
            for (Iterator<GenericValue> iterator = recordList.iterator(); iterator.hasNext();) {
                GenericValue record = iterator.next();

                try {
                    String accountId = record.getString("accountId");
                    GenericValue acct = delegator.findOne("SemAccount", UtilMisc.toMap("accountId", accountId), false);
                    if (UtilValidate.isNotEmpty(acct)) {
                        String channelId = acct.getString("channelId");
                        String reportId = record.getString("reportId");
                        String fileUrl = null;
                        Map<String, String> smStatus = null;

                        if (UtilValidate.areEqual("BD", channelId)) {
                            AccountInstance account = new AccountInstance(acct.getString("accountName"),
                                    acct.getString("accountPwd"), acct.getString("accountToken"));
                            ReportInstance report = new ReportInstance(account);

                            fileUrl = report.getReportFileUrlByReportId(delegator, reportId);
                        } else if (UtilValidate.areEqual("SG", channelId)) {
                            com.yhsem.sogou.ReportInstance report = new com.yhsem.sogou.ReportInstance(
                                    acct.getString("accountName"), acct.getString("accountPwd"),
                                    acct.getString("accountToken"));
                            fileUrl = report.getReportFileUrlByReportId(delegator, reportId);
                        } else if (UtilValidate.areEqual("SM", channelId)) {
                            com.yhsem.alism.ReportInstance report = new com.yhsem.alism.ReportInstance();
                            smStatus = report.getReportStateAndFileIdByReportId(delegator, acct, reportId);
                            fileUrl = smStatus.get("fileId");
                        } else if (UtilValidate.areEqual("QH", channelId)) {
                            String page = reportId.substring(0, reportId.indexOf("-"));
                            fileUrl = page;
                        }
                        boolean beganTransaction = false;
                        try {
                            beganTransaction = TransactionUtil.begin();

                            record.set("fileUrl", fileUrl);
                            record.set("fileUrlTime", UtilDateTime.nowTimestamp());

                            delegator.store(record);
                        } catch (Exception e) {
                            e.printStackTrace();
                            TransactionUtil.rollback(beganTransaction, e.getMessage(), e);
                        } finally {
                            TransactionUtil.commit(beganTransaction);
                        }
                    }
                } catch (GenericEntityException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ServiceUtil.returnSuccess();
    }

    /**
     * 检查处理未下载处理的报表<br/>
     * 
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> processingNotFinishedReports(DispatchContext dctx,
            Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();

        List<GenericValue> recordList = SemRptHelper.getNotFinishedRptRecords(delegator);
        if (UtilValidate.isNotEmpty(recordList)) {
            GenericValue userLogin = null;
            try {
                userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), true);
            } catch (GenericEntityException e1) {
                e1.printStackTrace();
            }

            for (Iterator<GenericValue> iterator = recordList.iterator(); iterator.hasNext();) {
                GenericValue record = iterator.next();

                try {
                    String accountId = record.getString("accountId");
                    GenericValue acct = delegator.findOne("SemAccount", UtilMisc.toMap("accountId", accountId), false);
                    if (UtilValidate.isNotEmpty(acct)) {
                        String channelId = acct.getString("channelId");
                        Date rptDate = record.getDate("rptDate");
                        String fileUrl = record.getString("fileUrl");
                        String reportId = record.getString("reportId");
                        String rptTypeId = record.getString("rptTypeId");
                        boolean finished = false;
                        if (UtilValidate.areEqual("BD", channelId)) {
                            String infoFlow = acct.getString("infoFlow");
                            finished = BaiduSemWorkers.processingReport(dctx, userLogin, accountId, infoFlow, rptDate,
                                    rptTypeId, fileUrl);
                            // 处理百度数据
                        } else if (UtilValidate.areEqual("SG", channelId)) {
                            finished = SogouSemWorkers.processingReport(dctx, userLogin, accountId, rptDate, rptTypeId,
                                    fileUrl, reportId);
                        } else if (UtilValidate.areEqual("SM", channelId)) {
                            finished = SmSemWorkers
                                    .processingReport(dctx, userLogin, acct, rptDate, rptTypeId, fileUrl);
                        } else if (UtilValidate.areEqual("QH", channelId)) {
                            finished = QihooSemWorkers.processingReport(dctx, userLogin, acct, rptDate, rptTypeId,
                                    record);
                        }
                        if (finished) {
                            boolean beganTransaction = false;
                            try {
                                beganTransaction = TransactionUtil.begin();
                                record.set("isFinished", "1");
                                delegator.store(record);
                            } catch (Exception e) {
                                e.printStackTrace();
                                TransactionUtil.rollback(beganTransaction, e.getMessage(), e);
                            } finally {
                                TransactionUtil.commit(beganTransaction);
                            }
                        }
                    }
                } catch (GenericEntityException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ServiceUtil.returnSuccess();
    }
}
