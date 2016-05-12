/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vk;

import java.io.*;
import java.net.*;
import java.util.*;

// JSON.Simple
import org.json.simple.parser.*;
import org.json.simple.*;

/**
 *
 * @author zooZooz
 */
public class VKLib {
    
    public static String get(String urlToRead) {
        URL url;
        HttpURLConnection connect;
        InputStreamReader streamReader;
        BufferedReader bufReader;
        String line;
        String result = "";
        
        try {
            url = new URL(urlToRead);
            connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("GET");
            
            streamReader = new InputStreamReader(connect.getInputStream());
            bufReader = new BufferedReader(streamReader);
            
            while((line = bufReader.readLine()) != null) {
                result += line;
            }
            bufReader.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    public static JSONObject parse(String url) {
        String jsonString = get(url);
        
        JSONObject retObj = null;
        JSONParser json = new JSONParser();
       
        try {
           retObj = (JSONObject) json.parse(jsonString);
        } catch (Exception e) {
           e.printStackTrace();
        }
        
       return retObj;
    }
    
    public static boolean isOut(String flags) {
        int flag = Integer.parseInt(flags);
        flags = Integer.toBinaryString(flag);
        flags = new StringBuilder(flags).reverse().toString();
        
        return (flags.charAt(1) == '1');
    }
    
    public static String getKey(Map map, String value) {
        Set<Map.Entry<String, String>> set = map.entrySet();
        
        for(Map.Entry<String, String> s : set) {
            if(s.getValue().equals(value)) {
                return s.getKey();
            }
        }
        
        return null;
    }
    
    public static String getPair(Map links, String firstId) {
        String secondId = null;
                    
        if(links.containsKey(firstId))
        {
            secondId = (String) links.get(firstId);
        }
        
        if(links.containsValue(firstId))
        {
            secondId = (String) VKLib.getKey(links, firstId);
        }
        
        return secondId;
    }
}
