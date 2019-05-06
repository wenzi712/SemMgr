/**
 * <b>项目名：</b>YHZF<br/>
 * <b>包名：</b>com.yhsem.kst<br/>
 * <b>文件名：</b>MsgEvent.java<br/>
 * <b>日期：</b>2019-4-12-下午4:21:58<br/>
 * <b>Copyright 2012-2015 WangYi. All Rights Reserved.<br/>
 *
 */
package com.yhsem.kst;

import java.sql.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.LocalDispatcher;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * <b>类名称：</b>MsgEvent<br/>
 * <b>类描述：</b>〈一句话功能简述〉<br/>
 * <b>类详细描述：</b>〈功能详细描述〉<br/>
 * <b>创建人：</b>WangYi<br/>
 * <b>修改人：</b>WangYi<br/>
 * <b>修改时间：</b>2019-4-12 下午4:21:58<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version <br/>
 * 
 */
public class MsgEvent {
    public static final String module = MsgEvent.class.getName();

    /**
     * 
     */
    public MsgEvent() {
        // TODO 自动生成的构造函数存根
    }

    public static String receivingData(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        Map<String, String[]> paraMap = request.getParameterMap();
        if (UtilValidate.isNotEmpty(paraMap) && UtilValidate.isNotEmpty(paraMap.get("data"))) {
            String data = paraMap.get("data")[0]; // json格式字符串
            Object parse = JSON.parse(data);
            if (UtilValidate.isNotEmpty(parse)) {
                JSONArray ja = (JSONArray) parse;
                // TODO: 使用事务控制进行数据操作
                // start
                boolean r = true;
                boolean beganTransaction = false;
                try {
                    beganTransaction = TransactionUtil.begin();
                    r = processingAllData(delegator, ja);
                } catch (GenericEntityException e) {
                    try {
                        TransactionUtil.rollback(beganTransaction, e.getLocalizedMessage(), e);
                    } catch (GenericEntityException e2) {
                        Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
                    }
                } finally {
                    try {
                        TransactionUtil.commit(beganTransaction);
                    } catch (GenericEntityException e) {
                        Debug.logError(e, e.getLocalizedMessage(), module);
                    }
                }

                if (r) {
                    request.setAttribute("message", "ok");
                    return "success";
                } else {
                    request.setAttribute("message", "ok");
                    return "success";
                }
                // end
            } else {
                Debug.logError("parse receivingData Error", module);

                request.setAttribute("message", "error");
                return "error";
            }
        } else {
            request.setAttribute("message", "error");
            return "error";
        }
    }

    /**
     * processingAllData<br/>
     * 处理所有数据<br/>
     * 
     * @param delegator
     * @param ja
     * @return
     */
    public static boolean processingAllData(Delegator delegator, JSONArray ja) {
        if (ja.size() > 0) {
            // 循环存储数据
            boolean ro = true;
            for (int i = 0; i < ja.size(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                ro = processingData(delegator, jo);
                if (!ro) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * processingData<br/>
     * 处理单个数据<br/>
     * 
     * @param delegator
     * @param jo
     * @return
     */
    public static boolean processingData(Delegator delegator, JSONObject jo) {
        JSONObject visitorInfo = jo.getJSONObject("visitorInfo");
        JSONObject visitorCard = jo.getJSONObject("visitorCard");
        boolean r1 = processingVisitorInfoData(delegator, visitorInfo);
        boolean r2 = processingVisitorCardData(delegator, visitorCard);
        // 只有处理失败才返回错误
        if (r1 && r2) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * processingVisitorInfoData<br/>
     * 处理访客会话信息<br/>
     * 
     * @param delegator
     * @param visitorInfo
     * @return
     */
    public static boolean processingVisitorInfoData(Delegator delegator, JSONObject visitorInfo) {
        String recId = UtilValidate.isNotEmpty(visitorInfo.getString("recId")) ? visitorInfo.getString("recId") : null;// 主键
        Debug.logError("recId : " + recId, module);

        GenericValue infoGv = null;
        try {
            infoGv = delegator.findOne("SemVisitorInfo", false, UtilMisc.toMap("recId", "recId"));
            if (UtilValidate.isEmpty(infoGv)) {
                infoGv = delegator.makeValue("SemVisitorInfo", UtilMisc.toMap("recId", "recId"));

                String visitorId = UtilValidate.isNotEmpty(visitorInfo.getString("visitorId")) ? visitorInfo
                        .getString("visitorId") : null;
                String visitorName = UtilValidate.isNotEmpty(visitorInfo.getString("visitorName")) ? visitorInfo
                        .getString("visitorName") : null;
                Date curEnterTime = UtilValidate.isNotEmpty(visitorInfo.getSqlDate("curEnterTime")) ? visitorInfo
                        .getSqlDate("curEnterTime") : null;
                int curStayTime = UtilValidate.isNotEmpty(visitorInfo.getInteger("curStayTime")) ? visitorInfo
                        .getInteger("curStayTime") : 0;
                String sourceIp = UtilValidate.isNotEmpty(visitorInfo.getString("sourceIp")) ? visitorInfo
                        .getString("sourceIp") : null;
                String sourceProvince = UtilValidate.isNotEmpty(visitorInfo.getString("sourceProvince")) ? visitorInfo
                        .getString("sourceProvince") : null;
                String sourceIpInfo = UtilValidate.isNotEmpty(visitorInfo.getString("sourceIpInfo")) ? visitorInfo
                        .getString("sourceIpInfo") : null;
                String requestType = UtilValidate.isNotEmpty(visitorInfo.getString("requestType")) ? visitorInfo
                        .getString("requestType") : null;
                String endType = UtilValidate.isNotEmpty(visitorInfo.getString("endType")) ? visitorInfo
                        .getString("endType") : null;
                Date diaStartTime = UtilValidate.isNotEmpty(visitorInfo.getSqlDate("diaStartTime")) ? visitorInfo
                        .getSqlDate("diaStartTime") : null;
                Date diaEndTime = UtilValidate.isNotEmpty(visitorInfo.getSqlDate("diaEndTime")) ? visitorInfo
                        .getSqlDate("diaEndTime") : null;
                String terminalType = UtilValidate.isNotEmpty(visitorInfo.getString("terminalType")) ? visitorInfo
                        .getString("terminalType") : null;
                int visitorSendNum = UtilValidate.isNotEmpty(visitorInfo.getInteger("visitorSendNum")) ? visitorInfo
                        .getInteger("visitorSendNum") : 0;
                int csSendNum = UtilValidate.isNotEmpty(visitorInfo.getInteger("csSendNum")) ? visitorInfo
                        .getInteger("csSendNum") : 0;
                String sourceUrl = UtilValidate.isNotEmpty(visitorInfo.getString("sourceUrl")) ? visitorInfo
                        .getString("sourceUrl") : null;
                String sourceType = UtilValidate.isNotEmpty(visitorInfo.getString("sourceType")) ? visitorInfo
                        .getString("sourceType") : null;
                String searchEngine = UtilValidate.isNotEmpty(visitorInfo.getString("searchEngine")) ? visitorInfo
                        .getString("searchEngine") : null;
                String keyword = UtilValidate.isNotEmpty(visitorInfo.getString("keyword")) ? visitorInfo
                        .getString("keyword") : null;
                String firstCsId = UtilValidate.isNotEmpty(visitorInfo.getString("firstCsId")) ? visitorInfo
                        .getString("firstCsId") : null;
                String joinCsIds = UtilValidate.isNotEmpty(visitorInfo.getString("joinCsIds")) ? visitorInfo
                        .getString("joinCsIds") : null;
                String dialogType = UtilValidate.isNotEmpty(visitorInfo.getString("dialogType")) ? visitorInfo
                        .getString("dialogType") : null;
                Date firstVisitTime = UtilValidate.isNotEmpty(visitorInfo.getSqlDate("firstVisitTime")) ? visitorInfo
                        .getSqlDate("firstVisitTime") : null;
                Date preVisitTime = UtilValidate.isNotEmpty(visitorInfo.getSqlDate("preVisitTime")) ? visitorInfo
                        .getSqlDate("preVisitTime") : null;
                int totalVisitTime = UtilValidate.isNotEmpty(visitorInfo.getInteger("totalVisitTime")) ? visitorInfo
                        .getInteger("totalVisitTime") : 0;
                String diaPage = UtilValidate.isNotEmpty(visitorInfo.getString("diaPage")) ? visitorInfo
                        .getString("diaPage") : null;
                String curFirstViewPage = UtilValidate.isNotEmpty(visitorInfo.getString("curFirstViewPage")) ? visitorInfo
                        .getString("curFirstViewPage") : null;
                int curVisitorPages = UtilValidate.isNotEmpty(visitorInfo.getInteger("curVisitorPages")) ? visitorInfo
                        .getInteger("curVisitorPages") : 0;
                int preVisitPages = UtilValidate.isNotEmpty(visitorInfo.getInteger("preVisitPages")) ? visitorInfo
                        .getInteger("preVisitPages") : 0;
                String operatingSystem = UtilValidate.isNotEmpty(visitorInfo.getString("operatingSystem")) ? visitorInfo
                        .getString("operatingSystem") : null;
                String browser = UtilValidate.isNotEmpty(visitorInfo.getString("browser")) ? visitorInfo
                        .getString("browser") : null;
                String info = UtilValidate.isNotEmpty(visitorInfo.getString("info")) ? visitorInfo.getString("info")
                        : null;
                String siteName = UtilValidate.isNotEmpty(visitorInfo.getString("siteName")) ? visitorInfo
                        .getString("siteName") : null;
                int siteId = UtilValidate.isNotEmpty(visitorInfo.getInteger("siteId")) ? visitorInfo
                        .getInteger("siteId") : 0;

                // 不处理对话数据
                // JSONArray dialogs = visitorInfo.getJSONArray("dialogs");
                infoGv.setString("visitorId", visitorId);

                delegator.create(infoGv);
            }
        } catch (GenericEntityException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
            return false;
        }

        // 只有处理失败才返回错误

        return true;
    }

    /**
     * processingVisitorCardData<br/>
     * 处理访客卡片信息<br/>
     * 
     * @param delegator
     * @param visitorCard
     * @return
     */
    public static boolean processingVisitorCardData(Delegator delegator, JSONObject visitorCard) {
        String visitorId = UtilValidate.isNotEmpty(visitorCard.getString("visitorId")) ? visitorCard
                .getString("visitorId") : null;
        Debug.logError("visitorId : " + visitorId, module);

        GenericValue cardGv = null;
        try {
            cardGv = delegator.findOne("SemVisitorCard", false, UtilMisc.toMap("visitorId", "visitorId"));
            if (UtilValidate.isEmpty(cardGv)) {
                cardGv = delegator.makeValue("SemVisitorCard", UtilMisc.toMap("visitorId", "visitorId"));
                String yongHeUserId = UtilValidate.isNotEmpty(visitorCard.getString("yongHeUserID")) ? visitorCard
                        .getString("yongHeUserID") : null;

                String linkman = UtilValidate.isNotEmpty(visitorCard.getString("linkman")) ? visitorCard
                        .getString("linkman") : null;
                String cusType = UtilValidate.isNotEmpty(visitorCard.getString("cusType")) ? visitorCard
                        .getString("cusType") : null;
                String compName = UtilValidate.isNotEmpty(visitorCard.getString("compName")) ? visitorCard
                        .getString("compName") : null;
                String webUrl = UtilValidate.isNotEmpty(visitorCard.getString("webUrl")) ? visitorCard
                        .getString("webUrl") : null;
                String mobile = UtilValidate.isNotEmpty(visitorCard.getString("mobile")) ? visitorCard
                        .getString("mobile") : null;
                String phone = UtilValidate.isNotEmpty(visitorCard.getString("phone")) ? visitorCard.getString("phone")
                        : null;
                String qq = UtilValidate.isNotEmpty(visitorCard.getString("qq")) ? visitorCard.getString("qq") : null;
                String msn = UtilValidate.isNotEmpty(visitorCard.getString("msn")) ? visitorCard.getString("msn")
                        : null;
                String email = UtilValidate.isNotEmpty(visitorCard.getString("email")) ? visitorCard.getString("email")
                        : null;
                String address = UtilValidate.isNotEmpty(visitorCard.getString("address")) ? visitorCard
                        .getString("address") : null;
                String birthday = UtilValidate.isNotEmpty(visitorCard.getString("birthday")) ? visitorCard
                        .getString("birthday") : null;
                String channelType = UtilValidate.isNotEmpty(visitorCard.getString("channelType")) ? visitorCard
                        .getString("channelType") : null;
                String remark = UtilValidate.isNotEmpty(visitorCard.getString("remark")) ? visitorCard
                        .getString("remark") : null;
                String loginName = UtilValidate.isNotEmpty(visitorCard.getString("loginName")) ? visitorCard
                        .getString("loginName") : null;
                String userName = UtilValidate.isNotEmpty(visitorCard.getString("userName")) ? visitorCard
                        .getString("userName") : null;
                String nickName = UtilValidate.isNotEmpty(visitorCard.getString("nickName")) ? visitorCard
                        .getString("nickName") : null;

                String col1 = UtilValidate.isNotEmpty(visitorCard.getString("col1")) ? visitorCard.getString("col1")
                        : null;
                String col2 = UtilValidate.isNotEmpty(visitorCard.getString("col2")) ? visitorCard.getString("col2")
                        : null;
                String col3 = UtilValidate.isNotEmpty(visitorCard.getString("col3")) ? visitorCard.getString("col3")
                        : null;
                String col4 = UtilValidate.isNotEmpty(visitorCard.getString("col4")) ? visitorCard.getString("col4")
                        : null;
                String col5 = UtilValidate.isNotEmpty(visitorCard.getString("col5")) ? visitorCard.getString("col5")
                        : null;
                String col6 = UtilValidate.isNotEmpty(visitorCard.getString("col6")) ? visitorCard.getString("col6")
                        : null;
                String col7 = UtilValidate.isNotEmpty(visitorCard.getString("col7")) ? visitorCard.getString("col7")
                        : null;
                String col8 = UtilValidate.isNotEmpty(visitorCard.getString("col8")) ? visitorCard.getString("col8")
                        : null;
                String col9 = UtilValidate.isNotEmpty(visitorCard.getString("col9")) ? visitorCard.getString("col9")
                        : null;

                Date addtime = UtilValidate.isNotEmpty(visitorCard.getSqlDate("addtime")) ? visitorCard
                        .getSqlDate("addtime") : null;
                Date lastChangeTime = UtilValidate.isNotEmpty(visitorCard.getSqlDate("lastChangeTime")) ? visitorCard
                        .getSqlDate("lastChangeTime") : null;

                cardGv.setString("yongHeUserId", yongHeUserId);
                delegator.create(cardGv);
            }
        } catch (GenericEntityException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
            return false;
        }

        // 只有处理失败才返回错误

        return true;
    }

    /**
     * main(这里用一句话描述这个方法的作用)<br/>
     * (这里描述这个方法适用条件 – 可选)<br/>
     * 
     * @param args
     */
    public static void main(String[] args) {
        // TODO 自动生成的方法存根

    }

}
