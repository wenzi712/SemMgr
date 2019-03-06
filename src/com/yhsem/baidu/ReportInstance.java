/**
 * <b>项目名：</b>YHZF<br/>
 * <b>包名：</b>com.yhsem.baidu<br/>
 * <b>文件名：</b>ReportInstance.java<br/>
 * <b>日期：</b>2019-3-4-下午5:26:07<br/>
 * <b>Copyright 2012-2015 WangYi. All Rights Reserved.<br/>
 *
 */
package com.yhsem.baidu;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.Delegator;

import com.baidu.drapi.autosdk.core.CommonService;
import com.baidu.drapi.autosdk.core.ResHeader;
import com.baidu.drapi.autosdk.core.ResHeaderUtil;
import com.baidu.drapi.autosdk.core.ServiceFactory;
import com.baidu.drapi.autosdk.exception.ApiException;
import com.baidu.drapi.autosdk.sms.service.GetProfessionalReportIdData;
import com.baidu.drapi.autosdk.sms.service.GetProfessionalReportIdRequest;
import com.baidu.drapi.autosdk.sms.service.GetProfessionalReportIdResponse;
import com.baidu.drapi.autosdk.sms.service.GetReportFileUrlData;
import com.baidu.drapi.autosdk.sms.service.GetReportFileUrlRequest;
import com.baidu.drapi.autosdk.sms.service.GetReportFileUrlResponse;
import com.baidu.drapi.autosdk.sms.service.GetReportStateData;
import com.baidu.drapi.autosdk.sms.service.GetReportStateRequest;
import com.baidu.drapi.autosdk.sms.service.GetReportStateResponse;
import com.baidu.drapi.autosdk.sms.service.ReportRequestType;
import com.baidu.drapi.autosdk.sms.service.ReportService;
import com.baidu.drapi.autosdk.util.DownloadUtil;
import com.opencsv.CSVReader;

/**
 * 
 * <b>类名称：</b>ReportInstance<br/>
 * <b>类描述：</b>〈一句话功能简述〉<br/>
 * <b>类详细描述：</b>〈功能详细描述〉<br/>
 * <b>创建人：</b>WangYi<br/>
 * <b>修改人：</b>WangYi<br/>
 * <b>修改时间：</b>2019-2-22 上午10:44:15<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version <br/>
 * 
 */
public class ReportInstance {

    private CommonService factory;
    private final AccountInstance account;
    private ReportService reportService;

    private static String SUCCESS = "success";

    public ReportInstance(AccountInstance account) {
        this.account = account;
        try {
            factory = ServiceFactory.getInstance();
            factory.setUsername(account.getFactory().getUsername());
            factory.setPassword(account.getFactory().getPassword());
            factory.setToken(account.getFactory().getToken());
            reportService = this.factory.getService(ReportService.class);
        } catch (ApiException e) {
            e.printStackTrace();
        }

    }

    /**
     * getRegionReportId<br/>
     * 获取昨天日报<br/>
     * 
     * @return 返回服务器生成的报表 reportId
     * @throws Exception
     */
    public String getRegionReportId(Delegator delegator, boolean infoFlow) throws Exception {
        return getRegionReportId(delegator, UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), -1),
                UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), -1), infoFlow);
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
    public String getRegionReportId(Delegator delegator, Date startDate, Date endDate, boolean infoFlow)
            throws Exception {
        String reportId = null;
        GetProfessionalReportIdRequest request = new GetProfessionalReportIdRequest();

        try {
            ReportRequestType type = new ReportRequestType();
            List<String> performanceData = Arrays.asList(new String[] { "cost", "cpc", "click", "impression", "ctr",
                    "cpm", "position", "conversion" });
            type.setPerformanceData(performanceData);
            type.setLevelOfDetails(2);
            type.setUnitOfTime(5);
            type.setReportType(5);
            type.setStartDate(startDate);
            type.setEndDate(endDate);
            if (infoFlow) {
                type.setPlatform(23);// 信息流
            }

            request.setReportRequestType(type);
            GetProfessionalReportIdResponse response = reportService.getProfessionalReportId(request);

            if (response != null) {
                List<GetProfessionalReportIdData> datas = response.getData();
                if (datas != null) {
                    System.out.println("response.getData() : " + datas);
                    ResHeader rheader = ResHeaderUtil.getResHeader(reportService, true);
                    if (SUCCESS.equals(rheader.getDesc()) && rheader.getStatus() == 0) {
                        reportId = datas.get(0).getReportId();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(reportId);
        return reportId;
    }

    /**
     * getKeywordReportId<br/>
     * 获取昨日关键词日报<br/>
     * 
     * @return 返回服务器生成的报表 reportId
     * @throws Exception
     */
    public String getKeywordReportId(Delegator delegator, boolean infoFlow) throws Exception {
        return getKeywordReportId(delegator, UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), -1),
                UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), -1), infoFlow);
    }

    /**
     * getKeywordReportId<br/>
     * 按照日期获取关键词日报结果<br/>
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
    public String getKeywordReportId(Delegator delegator, Date startDate, Date endDate, boolean infoFlow)
            throws Exception {
        String reportId = null;
        GetProfessionalReportIdRequest request = new GetProfessionalReportIdRequest();

        try {
            ReportRequestType type = new ReportRequestType();
            List<String> performanceData = Arrays.asList(new String[] { "cost", "cpc", "click", "impression", "ctr",
                    "cpm", "position", "conversion" });
            type.setPerformanceData(performanceData);
            type.setLevelOfDetails(6);
            type.setUnitOfTime(5);
            type.setReportType(9);
            type.setStartDate(startDate);
            type.setEndDate(endDate);
            if (infoFlow) {
                type.setPlatform(23);// 信息流
            }

            request.setReportRequestType(type);
            GetProfessionalReportIdResponse response = reportService.getProfessionalReportId(request);

            if (response != null) {
                List<GetProfessionalReportIdData> datas = response.getData();
                if (datas != null) {
                    System.out.println("response.getData() : " + datas);
                    ResHeader rheader = ResHeaderUtil.getResHeader(reportService, true);
                    if (SUCCESS.equals(rheader.getDesc()) && rheader.getStatus() == 0) {
                        reportId = datas.get(0).getReportId();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
     * @return 报告状态 [1：等待中; 2：处理中; 3：处理成功]
     * @throws Exception
     */
    public Integer getReportStateByReportId(Delegator delegator, String reportId) throws Exception {
        Integer isGenerated = 0;
        GetReportStateRequest request = new GetReportStateRequest();
        request.setReportId(reportId);
        GetReportStateResponse response = reportService.getReportState(request);
        List<GetReportStateData> datas = response.getData();
        System.out.println(datas);
        ResHeader rheader = ResHeaderUtil.getResHeader(reportService, true);
        if (SUCCESS.equals(rheader.getDesc()) && rheader.getStatus() == 0) {
            isGenerated = datas.get(0).getIsGenerated();
        }
        System.out.println(isGenerated);

        return isGenerated;
    }

    public String getReportFileUrlByReportId(Delegator delegator, String reportId) throws Exception {
        String reportFilePath = "";
        GetReportFileUrlRequest request = new GetReportFileUrlRequest();
        request.setReportId(reportId);
        GetReportFileUrlResponse response = reportService.getReportFileUrl(request);
        List<GetReportFileUrlData> datas = response.getData();
        System.out.println(datas);
        ResHeader rheader = ResHeaderUtil.getResHeader(reportService, true);
        if (SUCCESS.equals(rheader.getDesc()) && rheader.getStatus() == 0) {
            reportFilePath = datas.get(0).getReportFilePath();
        }
        System.out.println(reportFilePath);

        return reportFilePath;
    }

    public void testRegionReport(Delegator delegator, boolean infoFlow) {
        try {
            String reportId = this.getRegionReportId(delegator, infoFlow);
            // String reportId = "bcc39f6d1879ad99db634c3d9c443c18";
            Thread.sleep(20 * 1000);
            Integer reportState = this.getReportStateByReportId(delegator, reportId);
            System.out.println("reportState : " + reportState);

            if (reportState == 3) {
                String url = this.getReportFileUrlByReportId(delegator, reportId);
                byte[] b = DownloadUtil.downloadFile(url);
                CSVReader c = new CSVReader(new StringReader(new String(b, "GBK")), '\t');
                String[] nextLine;
                int n = 0;
                while ((nextLine = c.readNext()) != null) {
                    n++;
                    if (n > 0) {
                        System.out.print("\n");
                        for (int i = 0; i < nextLine.length; i++) {
                            System.out.print("\t");
                            System.out.print(nextLine[i]);
                        }
                    }
                }
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void _testRegionReport(Delegator delegator, boolean infoFlow) {
    }

    public void testKeywordReport(Delegator delegator, boolean infoFlow) {
        try {
            String reportId = this.getKeywordReportId(delegator, infoFlow);
            // String reportId = "bcc39f6d1879ad99db634c3d9c443c18";
            Thread.sleep(20 * 1000);
            Integer reportState = this.getReportStateByReportId(delegator, reportId);
            System.out.println("reportState : " + reportState);

            if (reportState == 3) {
                String url = this.getReportFileUrlByReportId(delegator, reportId);
                byte[] b = DownloadUtil.downloadFile(url);
                CSVReader c = new CSVReader(new StringReader(new String(b, "GBK")), '\t');
                String[] nextLine;
                int n = 0;
                while ((nextLine = c.readNext()) != null) {
                    n++;
                    if (n > 0) {
                        System.out.print("\n");
                        for (int i = 0; i < nextLine.length; i++) {
                            System.out.print("\t");
                            System.out.print(nextLine[i]);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void _testKeywordReport(Delegator delegator, boolean infoFlow) {
        try {
            String reportId = this.getKeywordReportId(delegator, infoFlow);
            // String reportId = "bcc39f6d1879ad99db634c3d9c443c18";
            Thread.sleep(20 * 1000);
            Integer reportState = this.getReportStateByReportId(delegator, reportId);
            System.out.println("reportState : " + reportState);

            if (reportState == 3) {
                String url = this.getReportFileUrlByReportId(delegator, reportId);
                byte[] b = DownloadUtil.downloadFile(url);
                CSVReader c = new CSVReader(new StringReader(new String(b, "GBK")), '\t');
                String[] nextLine;
                int n = 0;
                // KeywordReportDataBean tmp;
                // while ((nextLine = c.readNext()) != null) {
                // n++;
                // if (n > 1) {
                // tmp = new KeywordReportDataBean(nextLine[0], nextLine[1],
                // nextLine[2], nextLine[3],
                // nextLine[4], nextLine[5], nextLine[6], nextLine[7],
                // nextLine[8], nextLine[9],
                // nextLine[10], nextLine[11], nextLine[12],
                // Integer.valueOf(nextLine[13]),
                // Integer.valueOf(nextLine[14]), Double.valueOf(nextLine[15]),
                // nextLine[16], nextLine[17]);
                // System.out.println(tmp.toString());
                // }
                // }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
