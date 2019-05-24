/**
 * <b>项目名：</b>YHZF<br/>
 * <b>包名：</b>com.yhsem.qihoo<br/>
 * <b>文件名：</b>QihooHttpClientUtil.java<br/>
 * <b>日期：</b>2019-5-22-上午12:48:47<br/>
 * <b>Copyright 2012-2015 WangYi. All Rights Reserved.<br/>
 *
 */
package com.yhsem.qihoo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * <b>类名称：</b>QihooHttpClientUtil<br/>
 * <b>类描述：</b>〈一句话功能简述〉<br/>
 * <b>类详细描述：</b>〈功能详细描述〉<br/>
 * <b>创建人：</b>WangYi<br/>
 * <b>修改人：</b>WangYi<br/>
 * <b>修改时间：</b>2019-5-22 上午12:48:47<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version <br/>
 * 
 */
public class QihooHttpClientUtil {

    public static String module = QihooHttpClientUtil.class.getName();

    public static JSONObject postRequest(String url, Map<String, String> headerPara, Map<String, String> bodyPara) {
        JSONObject r = null;
        DefaultHttpClient httpsclient = new DefaultHttpClient();
        try {
            HttpPost httpost = new HttpPost(url);
            httpost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            if (headerPara.size() > 0) {
                for (Map.Entry<String, String> header : headerPara.entrySet()) {
                    httpost.addHeader(header.getKey(), header.getValue());
                }
            }

            List<BasicNameValuePair> nameValuePairList = new ArrayList<BasicNameValuePair>();
            nameValuePairList.add(new BasicNameValuePair("format", "json"));
            if (bodyPara.size() > 0) {
                for (Map.Entry<String, String> para : bodyPara.entrySet()) {
                    nameValuePairList.add(new BasicNameValuePair(para.getKey(), para.getValue()));
                }
            }

            UrlEncodedFormEntity httpEntity = new UrlEncodedFormEntity(nameValuePairList);
            httpost.setEntity(httpEntity);

            HttpResponse response = httpsclient.execute(httpost);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                String reponseLine = EntityUtils.toString(response.getEntity());
                if (!StringUtils.isEmpty(reponseLine) && JSONObject.isValid(reponseLine)) {
                    JSONObject jsonObject = JSON.parseObject(reponseLine);

                    if (!jsonObject.containsKey("failures")) {
                        r = jsonObject;
                    }
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return r;
    }

    public static void test1() {
        String url = "https://api.e.360.cn/account/clientLogin";

        Map<String, String> headerPara = new HashMap<String, String>();
        headerPara.put("apiKey", "35cb5a516317ae9104315b5af8bdc71b");

        Map<String, String> bodyPara = new HashMap<String, String>();
        bodyPara.put("username", "1517270513@qq.com");
        bodyPara.put("passwd", "e5d96535831dabe7966c8ca85440831c2a9ec3edc7fbf606e848dd345f44223e");

        JSONObject r = QihooHttpClientUtil.postRequest(url, headerPara, bodyPara);
        if (r != null) {
            System.out.println(r.toJSONString());
        }
    }

    public static void test2() {
        String url = "https://api.e.360.cn/2.0/report/cityCount";

        Map<String, String> headerPara = new HashMap<String, String>();
        headerPara.put("apiKey", "35cb5a516317ae9104315b5af8bdc71b");
        headerPara.put("accessToken", "da4af07d3947b4b9dac70a428a91e2dbbc638bed011152eb7");

        Map<String, String> bodyPara = new HashMap<String, String>();
        bodyPara.put("startDate", "2019-05-20");
        bodyPara.put("endDate", "2019-05-20");

        JSONObject r = QihooHttpClientUtil.postRequest(url, headerPara, bodyPara);
        if (r != null) {
            System.out.println(r.toJSONString());
        }
    }

    public static void test3() {
        String url = "https://api.e.360.cn/2.0/report/city";

        Map<String, String> headerPara = new HashMap<String, String>();
        headerPara.put("apiKey", "35cb5a516317ae9104315b5af8bdc71b");
        headerPara.put("accessToken", "224413a6a07d3c66581bde3ef4806dc0838ca4db7c61dca76");

        Map<String, String> bodyPara = new HashMap<String, String>();
        bodyPara.put("startDate", "2019-05-22");
        bodyPara.put("endDate", "2019-05-22");
        bodyPara.put("page", "1");// 默认为1

        JSONObject r = QihooHttpClientUtil.postRequest(url, headerPara, bodyPara);
        if (r != null) {
            System.out.println(r.getJSONArray("cityList").size());
        }
    }

    public static void main(String[] args) throws Exception {
        test3();
    }

}
