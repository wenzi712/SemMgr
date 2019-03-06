/**
 * <b>项目名：</b>YHZF<br/>
 * <b>包名：</b>com.yhsem.helper<br/>
 * <b>文件名：</b>SemRptHelper.java<br/>
 * <b>日期：</b>2019-3-4-下午5:41:20<br/>
 * <b>Copyright 2012-2015 WangYi. All Rights Reserved.<br/>
 *
 */
package com.yhsem.helper;

import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

/**
 * 
 * <b>类名称：</b>SemRptHelper<br/>
 * <b>类描述：</b>〈一句话功能简述〉<br/>
 * <b>类详细描述：</b>〈功能详细描述〉<br/>
 * <b>创建人：</b>WangYi<br/>
 * <b>修改人：</b>WangYi<br/>
 * <b>修改时间：</b>2019-3-4 下午5:41:20<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version <br/>
 * 
 */
public class SemRptHelper {
    public static final String module = SemRptHelper.class.getName();

    /**
     * 获取曾经发送请求但未正常返回报告的台账<br/>
     * 
     * @param delegator
     * @return
     */
    public static List<GenericValue> getFailureRequestRptRecords(Delegator delegator) {
        try {
            EntityCondition ec1 = EntityCondition.makeCondition("reportId", EntityOperator.EQUALS, null);
            EntityCondition ec2 = EntityCondition.makeCondition("reportId", EntityOperator.EQUALS, "");
            EntityCondition ec = EntityCondition.makeCondition(UtilMisc.toList(ec1, ec2), EntityOperator.OR);

            return delegator.findList("SemRptRecord", ec, null, UtilMisc.toList("requestTime"), null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取发送请求但获取状态的台账<br/>
     * 
     * @param delegator
     * @return
     */
    public static List<GenericValue> getQueryStatusRptRecords(Delegator delegator) {
        try {
            EntityCondition ec1 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null);
            EntityCondition ec2 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "3");
            EntityCondition ec = EntityCondition.makeCondition(UtilMisc.toList(ec1, ec2), EntityOperator.OR);
            return delegator.findList("SemRptRecord", ec, null, UtilMisc.toList("requestTime"), null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取服务器已经处理但未返回报告地址的台账<br/>
     * 
     * @param delegator
     * @return
     */
    public static List<GenericValue> getQueryUrlRptRecords(Delegator delegator) {
        try {
            EntityCondition ec1 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "3");
            EntityCondition ec2 = EntityCondition.makeCondition("fileUrl", EntityOperator.EQUALS, null);
            EntityCondition ec = EntityCondition.makeCondition(UtilMisc.toList(ec1, ec2));
            return delegator.findList("SemRptRecord", ec, null, UtilMisc.toList("requestTime"), null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 返回URL,但是标记为未处理结束的台账<br/>
     * 
     * @param delegator
     * @return
     */
    public static List<GenericValue> getNotFinishedRptRecords(Delegator delegator) {
        try {
            EntityCondition ec1 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "3");
            EntityCondition ec2 = EntityCondition.makeCondition("fileUrl", EntityOperator.NOT_EQUAL, null);
            EntityCondition ec3 = EntityCondition.makeCondition("isFinished", EntityOperator.EQUALS, "0");
            EntityCondition ec = EntityCondition.makeCondition(UtilMisc.toList(ec1, ec2, ec3));
            return delegator.findList("SemRptRecord", ec, null, UtilMisc.toList("requestTime"), null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return null;
        }
    }
}
