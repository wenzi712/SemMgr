/**
 * <b>项目名：</b>YHZF<br/>
 * <b>包名：</b>com.yhsem.kst<br/>
 * <b>文件名：</b>MsgEvent.java<br/>
 * <b>日期：</b>2019-4-12-下午4:21:58<br/>
 * <b>Copyright 2012-2015 WangYi. All Rights Reserved.<br/>
 *
 */
package com.yhsem.kst;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

import com.alibaba.fastjson.JSON;

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
        String result = "success";
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        Debug.logError("============================================", module);
        Debug.logError("0 Data: com.yhsem.kst.MsgEvent.receivingData", module);
        
        Map<String, String[]> paraMap = request.getParameterMap();
        if (paraMap != null && !paraMap.isEmpty()) {
            String data = paraMap.get("data")[0]; // json格式字符串

            Object parse = JSON.parse(data);
            if (parse != null) {
                @SuppressWarnings("unchecked")
                LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) parse;
                Object chat = map.get("visitorInfo");
                Object account = map.get("visitorCard");
                // synchronized (account) {
                Debug.logError("1 chat: " + JSON.toJSONString(chat), module);
                Debug.logError("2 account: " + JSON.toJSONString(account), module);
                // }
            }
        } else {
            result = "error";
        }
        return "success";
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
