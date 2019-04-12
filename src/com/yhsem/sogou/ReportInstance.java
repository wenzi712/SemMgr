/**
 * <b>项目名：</b>YHZF<br/>
 * <b>包名：</b>com.yhsem.sogou<br/>
 * <b>文件名：</b>ReportInstance.java<br/>
 * <b>日期：</b>2019-3-10-下午6:42:29<br/>
 * <b>Copyright 2012-2015 WangYi. All Rights Reserved.<br/>
 *
 */
package com.yhsem.sogou;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.Delegator;

import com.opencsv.CSVReader;
import com.sogou.api.client.exception.SystemException;
import com.sogou.api.client.utils.FileDownloadUtils;
import com.sogou.api.client.utils.ZipUtils;
import com.sogou.api.client.wrapper.ReportServiceWrapper;
import com.sogou.api.sem.v1.report.GetReportIdRequest;
import com.sogou.api.sem.v1.report.GetReportIdResponse;
import com.sogou.api.sem.v1.report.GetReportPathRequest;
import com.sogou.api.sem.v1.report.GetReportPathResponse;
import com.sogou.api.sem.v1.report.GetReportStateRequest;
import com.sogou.api.sem.v1.report.GetReportStateResponse;
import com.sogou.api.sem.v1.report.ReportRequestType;

/**
 * 
 * <b>类名称：</b>ReportInstance<br/>
 * <b>类描述：</b>〈一句话功能简述〉<br/>
 * <b>类详细描述：</b>〈功能详细描述〉<br/>
 * <b>创建人：</b>WangYi<br/>
 * <b>修改人：</b>WangYi<br/>
 * <b>修改时间：</b>2019-3-10 下午6:42:29<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version <br/>
 * 
 */
public class ReportInstance {
    private final ReportServiceWrapper reportService;

    /**
     * @param reportService
     */
    public ReportInstance(ReportServiceWrapper reportService) {
        super();
        this.reportService = reportService;
    }

    public ReportInstance(String username, String password, String token) {
        this.reportService = new ReportServiceWrapper(username, password, token);
    }

    /**
     * getRegionReportId<br/>
     * 获取昨天日报<br/>
     * 
     * @return 返回服务器生成的报表 reportId
     * @throws Exception
     */
    public String getRegionReportId(Delegator delegator) throws Exception {
        return getRegionReportId(delegator, UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), -1),
                UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), -1L));
    }

    /**
     * getRegionReportId<br/>
     * 按照日期获取二级区域的日报结果<br/>
     * 
     * @param startTime
     *            开始时间,不能小于2008-10-31, 格式 yyyy-MM-dd HH:mm:ss
     * @param endTime
     *            开始时间,不能大于开始时间,格式 yyyy-MM-dd HH:mm:ss
     * @param infoFlow
     *            是否信息流
     * @return 返回服务器生成的报表 reportId
     * @throws Exception
     */
    public String getRegionReportId(Delegator delegator, Date startDate, Date endDate) throws Exception {
        String reportId = "";
        GetReportIdRequest getReportIdRequest = new GetReportIdRequest();
        ReportRequestType reportRequestType = new ReportRequestType();

        reportRequestType.setStartDate(startDate);
        reportRequestType.setEndDate(endDate);
        reportRequestType.setReportType(14);// 二级地域报告类型
        reportRequestType.setShowLocateMode(1);
        reportRequestType.getPerformanceData().add("cost");
        reportRequestType.getPerformanceData().add("consumePercent");
        reportRequestType.getPerformanceData().add("click");
        reportRequestType.getPerformanceData().add("clickPercent");
        reportRequestType.getPerformanceData().add("cpc");
        reportRequestType.getPerformanceData().add("ctr");
        reportRequestType.getPerformanceData().add("impression");
        reportRequestType.getPerformanceData().add("position");
        getReportIdRequest.setReportRequestType(reportRequestType);
        reportRequestType.getRegions().add(0001);// TODO: 观察是否需要修正这个参数
        getReportIdRequest.setReportRequestType(reportRequestType);

        GetReportIdResponse getReportIdResponse = reportService.getReportId(getReportIdRequest);
        if (null == getReportIdResponse || getReportIdResponse.getReportId() == null) {
            throw new SystemException("API return null report id");
        } else {
            reportId = getReportIdResponse.getReportId();
        }

        return reportId;
    }

    /**
     * getKeywordReportId<br/>
     * 获取昨日搜索词日报<br/>
     * 
     * @return 返回服务器生成的报表 reportId
     * @throws Exception
     */
    public String getKeywordReportId(Delegator delegator) throws Exception {

        return getKeywordReportId(delegator, UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), -1),
                UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), -1L));
    }

    /**
     * getKeywordReportId<br/>
     * 按照日期获取搜索词日报结果<br/>
     * 
     * @param startTime
     *            开始时间,不能小于2008-10-31, 格式 yyyy-MM-dd HH:mm:ss
     * @param endTime
     *            开始时间,不能大于开始时间,格式 yyyy-MM-dd HH:mm:ss
     * @param infoFlow
     *            是否信息流
     * @return 返回服务器生成的报表 reportId
     * @throws Exception
     */
    public String getKeywordReportId(Delegator delegator, Date startDate, Date endDate) throws Exception {
        String reportId = "";
        GetReportIdRequest getReportIdRequest = new GetReportIdRequest();
        ReportRequestType reportRequestType = new ReportRequestType();
        reportRequestType.setStartDate(startDate);
        reportRequestType.setEndDate(endDate);
        reportRequestType.setReportType(5);// 关键词报告类型
        reportRequestType.setShowLocateMode(1);
        reportRequestType.getPerformanceData().add("cost");
        reportRequestType.getPerformanceData().add("consumePercent");
        reportRequestType.getPerformanceData().add("click");
        reportRequestType.getPerformanceData().add("clickPercent");
        reportRequestType.getPerformanceData().add("cpc");
        reportRequestType.getPerformanceData().add("ctr");
        reportRequestType.getPerformanceData().add("impression");
        reportRequestType.getPerformanceData().add("position");
        getReportIdRequest.setReportRequestType(reportRequestType);
        reportRequestType.getRegions().add(0001);// TODO: 观察是否需要修正这个参数
        getReportIdRequest.setReportRequestType(reportRequestType);

        GetReportIdResponse getReportIdResponse = reportService.getReportId(getReportIdRequest);
        if (null == getReportIdResponse || getReportIdResponse.getReportId() == null) {
            throw new SystemException("API return null report id");
        } else {
            reportId = getReportIdResponse.getReportId();
        }

        System.out.println(reportId);
        return reportId;
    }

    /**
     * getReportStateByReportId<br/>
     * 按照报告ID获取报告服务器状态<br/>
     * <br/>
     * 
     * @param reportId
     *            报告ID
     * @return 报告状态 [1：等待中; 2：处理中; 3：处理成功; 4：处理异常]
     * @throws Exception
     */
    public Integer getReportStateByReportId(Delegator delegator, String reportId) throws Exception {
        Integer isGenerated = 0;

        GetReportStateRequest getReportStateRequest = new GetReportStateRequest();
        getReportStateRequest.setReportId(reportId);
        GetReportStateResponse getReportStateResponse = reportService.getReportState(getReportStateRequest);
        if (null == getReportStateResponse || null == getReportStateResponse.getIsGenerated()) {
            throw new SystemException("API return null report state");
        }
        // SOGOU报告状态 [1: 已完成 ;0: 处理中; -1:报表生成异常]

        if (getReportStateResponse.getIsGenerated() == 1) {
            isGenerated = 3;
        } else if (getReportStateResponse.getIsGenerated() == 0) {
            isGenerated = 2;
        } else {
            isGenerated = 4;
        }

        System.out.println(isGenerated);

        return isGenerated;
    }

    public String getReportFileUrlByReportId(Delegator delegator, String reportId) throws Exception {
        String reportFilePath = "";

        GetReportPathRequest getReportPathRequest = new GetReportPathRequest();
        getReportPathRequest.setReportId(reportId);

        GetReportPathResponse getReportPathResponse = reportService.getReportPath(getReportPathRequest);
        if (null == getReportPathResponse || null == getReportPathResponse.getReportFilePath()) {
            throw new SystemException("API return null report path");
        }

        reportFilePath = getReportPathResponse.getReportFilePath();

        System.out.println(reportFilePath);
        return reportFilePath;
    }

    public void testRegionReport(Delegator delegator) {
        try {
            String reportId = this.getRegionReportId(delegator);
            System.out.println(reportId);

            Thread.sleep(20 * 1000);

            Integer reportState = this.getReportStateByReportId(delegator, reportId);
            System.out.println("reportState : " + reportState);

            if (reportState == 1) {
                String url = this.getReportFileUrlByReportId(delegator, reportId);
                String reportFile = "D:/logs/" + reportId + ".gzip";
                String reportFileCsv = "D:/logs/" + reportId + ".csv";
                FileDownloadUtils.downloadFile(url, reportFile);

                ZipUtils.unGzipFile(reportFile, reportFileCsv);
                File file = new File(reportFileCsv);
                InputStream input = new FileInputStream(file);
                CSVReader c = new CSVReader(new InputStreamReader(input, "GBK"), ',');
                String[] nextLine;
                int n = 0;
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testKeywordReport(Delegator delegator) {
        try {
            String reportId = this.getKeywordReportId(delegator);
            Thread.sleep(20 * 1000);
            Integer reportState = this.getReportStateByReportId(delegator, reportId);
            System.out.println("reportState : " + reportState);

            if (reportState == 1) {
                String url = this.getReportFileUrlByReportId(delegator, reportId);
                String reportFile = "D:/logs/" + reportId + ".gzip";
                String reportFileCsv = "D:/logs/" + reportId + ".csv";
                FileDownloadUtils.downloadFile(url, reportFile);

                ZipUtils.unGzipFile(reportFile, reportFileCsv);
                File file = new File(reportFileCsv);
                InputStream input = new FileInputStream(file);
                CSVReader c = new CSVReader(new InputStreamReader(input, "GBK"), ',');
                String[] nextLine;
                int n = 0;
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ReportInstance report = new ReportInstance("Zhifa999@sohu.com", "YHzhifa-sogou*2019_888",
                "4d444ea1d123f50bb0f4ac369e029655");
    }
}
