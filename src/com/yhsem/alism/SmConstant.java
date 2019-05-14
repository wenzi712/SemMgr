/**
 * <b>项目名：</b>YHZF<br/>
 * <b>包名：</b>com.yhsem.alism<br/>
 * <b>文件名：</b>SmConstant.java<br/>
 * <b>日期：</b>2019-5-11-下午10:59:06<br/>
 * <b>Copyright 2012-2015 WangYi. All Rights Reserved.<br/>
 *
 */
package com.yhsem.alism;

import org.ofbiz.base.util.UtilProperties;

/**
 * 
 * <b>类名称：</b>SmConstant<br/>
 * <b>类描述：</b>〈一句话功能简述〉<br/>
 * <b>类详细描述：</b>〈功能详细描述〉<br/>
 * <b>创建人：</b>WangYi<br/>
 * <b>修改人：</b>WangYi<br/>
 * <b>修改时间：</b>2019-5-11 下午10:59:06<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version <br/>
 * 
 */
public class SmConstant {

    public static String RPT_FOLDER = UtilProperties.getPropertyValue("sem.properties", "SM_FOLDR_PATH");

    /**
     * @Fields HOST_URL : API请求地址 URL
     */
    public static final String HOST_URL = "https://e.sm.cn/api";

    /**
     * @Fields URI_GRT_ACCOUNT : 获取账号信息 URI
     */
    public static final String URI_GRT_ACCOUNT = "/account/getAccount";

    /**
     * @Fields URI_GET_REPORT : 请求报告 URI
     */
    public static final String URI_GET_REPORT = "/report/getReport";
    /**
     * @Fields URI_GET_TASK_STATE : 获取请求报告的处理状态 URI
     */
    public static final String URI_GET_TASK_STATE = "/task/getTaskState";
    /**
     * @Fields URI_DOWNLOAD : 下载报告URI
     */
    public static final String URI_DOWNLOAD = "/file/download";

    /**
     * @Fields RPT_TYPE_REGION : 二级地域报告类型
     */
    public static final String RPT_TYPE_REGION = "5";
    /**
     * @Fields RPT_TYPE_KEYWORDS : 关键词报告类型
     */
    public static final String RPT_TYPE_KEYWORD = "14";

}
