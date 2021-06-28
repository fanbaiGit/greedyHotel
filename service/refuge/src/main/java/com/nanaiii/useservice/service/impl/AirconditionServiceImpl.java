package com.nanaiii.useservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.nanaiii.useservice.entity.Airconditioning;
import com.nanaiii.useservice.mapper.AirconditionMapper;
import com.nanaiii.useservice.service.AirconditionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

@Service
public class AirconditionServiceImpl extends ServiceImpl<AirconditionMapper, Airconditioning> implements AirconditionService {

    @Override
    public void startAircondition(Airconditioning airconditioning) {
        System.out.println("\njson:"+ JSON.toJSONString(airconditioning));
        try {
            URL url = new URL("http://127.0.0.1:5000/start");// 创建连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST"); // 设置请求方式
            connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
            connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
            connection.connect();
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码
            out.append(JSON.toJSONString(airconditioning));
            out.flush();
            out.close();

            int code = connection.getResponseCode();
            InputStream is = null;
            if (code == 200) {
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
            }

            // 读取响应
            int length = (int) connection.getContentLength();// 获取长度
            if (length != -1) {
                byte[] data = new byte[length];
                byte[] temp = new byte[512];
                int readLen = 0;
                int destPos = 0;
                while ((readLen = is.read(temp)) > 0) {
                    System.arraycopy(temp, 0, data, destPos, readLen);
                    destPos += readLen;
                }
                String result = new String(data, "UTF-8"); // utf-8编码
                System.out.println("result in startAircondition: "+result);
            }

        } catch (IOException e) {
            System.out.println("Exception occur when send http post request: "+e);
        }
    }
}
