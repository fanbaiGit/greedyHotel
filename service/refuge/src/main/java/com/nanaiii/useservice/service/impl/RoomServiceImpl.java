package com.nanaiii.useservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nanaiii.commonutils.R;
import com.nanaiii.useservice.entity.Room;
import com.nanaiii.useservice.mapper.RoomMapper;
import com.nanaiii.useservice.service.RoomService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author nanaiii
 * @since 2021-05-24
 */
@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {


    @Override
    public void op2python(Room room) {
        System.out.println("\njson IN op2python:"+JSON.toJSONString(room));
        try {
            URL url = new URL("http://127.0.0.1:5000/op");// 创建连接
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
//            out.append(String.valueOf(params));
            out.append(JSON.toJSONString(room));
            out.flush();
            out.close();
//            System.out.println("out:"+out);

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
//                System.out.println("result in op2python: "+result);
            }

        } catch (IOException e) {
            System.out.println("Exception occur when send http post request!\n"+e);
        }
    }

    @Override
    public void shutdown2python(String room_id) {
        System.out.println("\nJson IN shutdown2python:"+JSON.toJSONString(room_id));
        try {
            URL url = new URL("http://127.0.0.1:5000/shutdown");// 创建连接
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
//            out.append(String.valueOf(params));
            out.append(room_id);
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
//                System.out.println("result in jsonPost: "+result);
            }

        } catch (IOException e) {
            System.out.println("Exception occur when send http post request!\n"+e);
        }
    }

    @Override
    public Room python2java(String room_id) {
        try {
            URL url = new URL("http://127.0.0.1:5000/syc");// 创建连接
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
//            out.append(String.valueOf(params));
            out.append(room_id);
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

                if(!result.equals("ohh")) {
                    System.out.println("\nJson in python2java: " + JSONObject.parseObject(result));
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    Room room = new Room();
//                    if(jsonObject.getInteger("state")!=0){
//                        if(jsonObject.getDouble("tarTemp")>25)
//                            room.setState(2);
//                        else if(jsonObject.getDouble("tarTemp")<=25)
//                            room.setState(1);
//                    }
//                    else
//                        room.setState(0);
                    room.setRoomId(jsonObject.getString("roomId"));
                    room.setState(jsonObject.getInteger("state"));
                    room.setNowTemp(jsonObject.getDouble("nowTemp"));
                    room.setTarTemp(jsonObject.getDouble("tarTemp"));
                    room.setWindSpeed(jsonObject.getString("windSpeed"));
                    room.setIsDisabled(jsonObject.getBoolean("isDisabled"));

                    return room;
                }
                else{
                    System.out.println("\nJson in python2java: " + result);
                    return null;
                }
            }

        } catch (IOException e) {
            System.out.println("Exception occur when send http post request!\n"+e);
        }
        return null;
    }
}
