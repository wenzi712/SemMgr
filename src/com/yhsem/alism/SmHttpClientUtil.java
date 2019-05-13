/**
 * <b>项目名：</b>YHZF<br/>
 * <b>包名：</b>com.yhsem<br/>
 * <b>文件名：</b>SmHttpClientUtil.java<br/>
 * <b>日期：</b>2019-5-11-下午10:55:55<br/>
 * <b>Copyright 2012-2015 WangYi. All Rights Reserved.<br/>
 *
 */
package com.yhsem.alism;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ofbiz.base.util.UtilValidate;

/**
 * 
 * <b>类名称：</b>SmHttpClientUtil<br/>
 * <b>类描述：</b>〈一句话功能简述〉<br/>
 * <b>类详细描述：</b>〈功能详细描述〉<br/>
 * <b>创建人：</b>WangYi<br/>
 * <b>修改人：</b>WangYi<br/>
 * <b>修改时间：</b>2019-5-11 下午10:55:55<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version <br/>
 * 
 */
public class SmHttpClientUtil {
    public static String module = SmHttpClientUtil.class.getName();

    public static String postRequest(String url, String username, String password, String token, String body) {
        String r = "";
        DefaultHttpClient httpsclient = new DefaultHttpClient();
        try {
            HttpPost httpost = new HttpPost(url);
            httpost.addHeader("Content-Type", "application/json");
            String header = "\"header\":{ \"username\": \"" + username + "\",\"password\": \"" + password
                    + "\", \"token\": \"" + token + "\"}";
            String s = "{" + body + "," + header + "}";
            s = new String(s.getBytes("UTF-8"), "UTF-8");
            StringEntity strEntity = new StringEntity(s, Consts.UTF_8);
            strEntity.setContentEncoding("UTF-8");
            strEntity.setContentType("application/json");

            httpost.setEntity(strEntity);

            HttpResponse response = httpsclient.execute(httpost);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = null;
            StringBuffer result = new StringBuffer();
            try {
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            r = result.toString();
            r = new String(r.getBytes("GBK"), "UTF-8");
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return r;
    }

    public static boolean postRequestDownloadCsv(String url, String username, String password, String token, Long fileId) {
        boolean r = true;
        
        if (UtilValidate.isEmpty(fileId) || UtilValidate.areEqual(0L, fileId)) {
            return false;
        }
        
        DefaultHttpClient httpsclient = new DefaultHttpClient();
        try {
            HttpPost httpost = new HttpPost(url);
            httpost.addHeader("Content-Type", "application/json");
            String header = "\"header\":{ \"username\": \"" + username + "\",\"password\": \"" + password
                    + "\", \"token\": \"" + token + "\"}";
            String body = "\"body\":{\"fileId\": " + fileId + "}";
            String s = "{" + body + "," + header + "}";
            s = new String(s.getBytes("UTF-8"), "UTF-8");

            StringEntity strEntity = new StringEntity(s, Consts.UTF_8);
            httpost.setEntity(strEntity);

            HttpResponse response = httpsclient.execute(httpost);

            InputStream input = response.getEntity().getContent();
            String reportFileCsv = "D:/logs/" + fileId + ".csv";
            File file = new File(reportFileCsv);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileout = new FileOutputStream(file);
            /**
             * 根据实际运行效果 设置缓冲区大小
             */
            byte[] buffer = new byte[4096];
            int ch = 0;
            while ((ch = input.read(buffer)) != -1) {
                fileout.write(buffer, 0, ch);
            }
            input.close();
            fileout.flush();
            fileout.close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            r = false;
        } catch (IOException e) {
            e.printStackTrace();
            r = false;
        }
        return r;
    }

    public static void main(String[] args) throws Exception {
        // String url = "https://e.sm.cn/api/account/getAccount";
        // String body = "\"body\":{\"requestData\": [\"account_all\"]}";
        String url = "https://e.sm.cn/api/file/download";
        String body = "\"body\":{\"fileId\": \"1152921504741118403\"}";
        // String s = postRequest(url, "雍禾植发1", "YHZFsm2019789",
        // "2bf3bf9c-4737-48d4-b75b-fbe7a9ac48cb", body);
        postRequestDownloadCsv(url, "雍禾植发1", "YHZFsm2019789", "2bf3bf9c-4737-48d4-b75b-fbe7a9ac48cb",
                1152921504741118403L);
    }
}
