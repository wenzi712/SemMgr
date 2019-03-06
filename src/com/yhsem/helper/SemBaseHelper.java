/**
 * <b>项目名：</b>YHZF<br/>
 * <b>包名：</b>com.yhsem.helper<br/>
 * <b>文件名：</b>SemBaseHelper.java<br/>
 * <b>日期：</b>2019-3-5-上午10:58:31<br/>
 * <b>Copyright 2012-2015 WangYi. All Rights Reserved.<br/>
 *
 */
package com.yhsem.helper;

import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

/**
 * 
 * <b>类名称：</b>SemBaseHelper<br/>
 * <b>类描述：</b>〈一句话功能简述〉<br/>
 * <b>类详细描述：</b>〈功能详细描述〉<br/>
 * <b>创建人：</b>WangYi<br/>
 * <b>修改人：</b>WangYi<br/>
 * <b>修改时间：</b>2019-3-5 上午10:58:31<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version <br/>
 * 
 */
public class SemBaseHelper {
    public static final String module = SemBaseHelper.class.getName();

    /**
     * 获取所有可用账户<br/>
     * 
     * @param delegator
     * @return
     */
    public static List<GenericValue> getAllValidAccount(Delegator delegator) {
        try {
            return delegator.findByAnd("SemAccount", UtilMisc.toMap("valid", "1"),
                    UtilMisc.toList("channelId", "accountId"), false);
        } catch (GenericEntityException e) {
            Debug.logError("查询账户信息异常!", module);
            e.printStackTrace();
            return null;

        }
    }
}
