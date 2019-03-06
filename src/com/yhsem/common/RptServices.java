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

import com.yhsem.baidu.AccountInstance;
import com.yhsem.baidu.BaiduSemWorkers;
import com.yhsem.baidu.ReportInstance;
import com.yhsem.helper.SemBaseHelper;
import com.yhsem.helper.SemRptHelper;

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
        Date rptDate = new Date(UtilDateTime.nowDate().getTime());

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

                } else if (UtilValidate.areEqual("SM", channelId)) {

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
                Timestamp startDate = UtilDateTime.getDayStart(UtilDateTime.getTimestamp(rptDate.getTime()), -1);
                Timestamp endDate = UtilDateTime.getDayStart(UtilDateTime.getTimestamp(rptDate.getTime()));
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
                                    reportId = report.getRegionReportId(delegator, startDate, endDate, true);
                                } else {
                                    reportId = report.getRegionReportId(delegator, startDate, endDate, false);
                                }
                            } else {// 关键词报告
                                if (UtilValidate.areEqual("0", record.getString("infoFlow"))) {
                                    reportId = report.getKeywordReportId(delegator, startDate, endDate, true);
                                } else {
                                    reportId = report.getKeywordReportId(delegator, startDate, endDate, false);
                                }
                            }
                        } else if (UtilValidate.areEqual("SG", channelId)) {

                        } else if (UtilValidate.areEqual("SM", channelId)) {

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
                        String statusId = null;

                        if (UtilValidate.areEqual("BD", channelId)) {
                            AccountInstance account = new AccountInstance(acct.getString("accountName"),
                                    acct.getString("accountPwd"), acct.getString("accountToken"));
                            ReportInstance report = new ReportInstance(account);

                            statusId = report.getReportStateByReportId(delegator, reportId).toString();
                        } else if (UtilValidate.areEqual("SG", channelId)) {

                        } else if (UtilValidate.areEqual("SM", channelId)) {

                        }
                        boolean beganTransaction = false;
                        try {
                            beganTransaction = TransactionUtil.begin();

                            record.set("statusId", statusId);
                            record.set("statusTime", UtilDateTime.nowTimestamp());

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

                        if (UtilValidate.areEqual("BD", channelId)) {
                            AccountInstance account = new AccountInstance(acct.getString("accountName"),
                                    acct.getString("accountPwd"), acct.getString("accountToken"));
                            ReportInstance report = new ReportInstance(account);

                            fileUrl = report.getReportFileUrlByReportId(delegator, reportId);
                        } else if (UtilValidate.areEqual("SG", channelId)) {

                        } else if (UtilValidate.areEqual("SM", channelId)) {

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
                        String rptTypeId = record.getString("rptTypeId");
                        boolean finished = false;
                        if (UtilValidate.areEqual("BD", channelId)) {
                            finished = BaiduSemWorkers.processingReport(dctx, userLogin, accountId, rptDate, rptTypeId,
                                    fileUrl);
                            // 处理百度数据
                        } else if (UtilValidate.areEqual("SG", channelId)) {

                        } else if (UtilValidate.areEqual("SM", channelId)) {

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
