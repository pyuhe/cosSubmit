package com.mall_of329.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * @author huangRong
 * @date 2021/10/25 22:57
 */
public  class ipAddress {
    //使用腾讯的接口通过ip拿到城市信息
    private static final String KEY = "MO4BZ-V5564-MTBU7-XR6ZN-4P3NZ-33BLA";

    /**
     * 返回IP地址所对应的城市信息
     * @param ip
     * @return
     */
    public static String getCityInfo(String ip) {
        String s = sendGet(ip, KEY);
        Map map = JSONObject.parseObject(s, Map.class);
        String message = (String) map.get("message");
        if ("query ok".equals(message)) {
            Map result = (Map) map.get("result");
            Map addressInfo = (Map) result.get("ad_info");
            String nation = (String) addressInfo.get("nation");
            String province = (String) addressInfo.get("province");
            //  String district = (String) addressInfo.get("district");
            String city = (String) addressInfo.get("city");
            String address = nation + "-" + province + "-" + city;
            return address;
        } else {
            System.out.println(message);
            return null;
        }
    }

    /**
     * 根据在腾讯位置服务上申请的key进行请求操作
     * @param ip
     * @param key
     * @return
     */
    public static String sendGet(String ip, String key) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = "https://apis.map.qq.com/ws/location/v1/ip?ip=" + ip + "&key=" + key;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
//            for (Map.Entry entry : map.entrySet()) {
//                System.out.println(key + "--->" + entry);
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 获取客户端的IP地址
     * @param request
     * @return
     */
    public String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknow".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (ip.equals("127.0.0.1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ip = inet.getHostAddress();
            }
        }
        // 多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;

    }
}