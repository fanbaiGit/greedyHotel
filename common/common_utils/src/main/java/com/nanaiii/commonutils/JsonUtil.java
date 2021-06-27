package com.nanaiii.commonutils;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName JsonUtil
 * @Description json工具类
 * @Author AlphaJunS
 * @Date 2020/3/27 18:59
 * @Version 1.0
 */
public class JsonUtil {

    /**
     * @Author AlphaJunS
     * @Date 19:06 2020/3/27
     * @Description json转map
     * @param json
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    public static Map<String,Object> jsonToMap(String json) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,Object> map;
        try {
            map = (Map<String,Object>)objectMapper.readValue(json, Map.class);
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }
        return map;
    }

    /**
     * @Author AlphaJunS
     * @Date 19:07 2020/3/27
     * @Description json转List<Map<String,?>>
     * @param json
     * @return java.util.List<java.util.Map<java.lang.String,?>>
     */
    public static List<Map<String,?>> jsonToMapList(String json) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, Map.class);
        List<Map<String, ?>> mapList;
        try {
            mapList = (List<Map<String,?>>)objectMapper.readValue(json, javaType);
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }
        return mapList;
    }

    /**
     * @Author AlphaJunS
     * @Date 19:09 2020/3/27
     * @Description map转json
     * @param map
     * @return java.lang.String
     */
    public static String mapToJson(Map<String,Object> map) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(map);
        return jsonString;
    }

    /**
     * @Author AlphaJunS
     * @Date 19:10 2020/3/27
     * @Description 对象转json
     * @param object
     * @return java.lang.String
     */
    public static String objectToJson(Object object) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonList;
        try {
            jsonList = objectMapper.writeValueAsString(object);
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }
        return jsonList;
    }

    /**
     * @Author AlphaJunS
     * @Date 20:52 2020/3/27
     * @Description List<Map<String,?>>转json
     * @param cardInfoList
     * @return java.lang.String
     */
    public static String mapListToJson(List<Map<String, Object>> cardInfoList) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonList;
        try {
            jsonList = objectMapper.writeValueAsString(cardInfoList);
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }
        return jsonList;
    }

    /**
     * @Author AlphaJunS
     * @Date 20:57 2020/3/27
     * @Description 数组转json
     * @param args
     * @return java.lang.String
     */
    public static String arrayToJson(String[] args) throws Exception{
        // 先讲数组转化为map，然后map转json
        Map<String,String> map = new HashMap<String,String>();
        for(int i=0; i<args.length; i++){
            map.put(i+"", args[i]);
        }
        String json = JsonUtil.objectToJson(map);
        return json;
    }

}