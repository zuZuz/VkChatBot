/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vk;

import java.net.URLEncoder;
import java.util.*;

// JSON.simple
import org.json.simple.*;

/**
 *
 * @author zooZooz
 */
public class Vk {
    private String URLTpl;
    private JSONObject longPoll;
    
    public Vk(String token) {
        this.URLTpl = "https://api.vk.com/method/%s?%s&access_token=" + token;
        
        setLongPoll();
    }
    
    private void setLongPoll() {
        String url = String.format(URLTpl,
                "messages.getLongPollServer", "");

        JSONObject data = VKLib.parse(url);
        data = (JSONObject) data.get("response");

        this.longPoll = data;
    }
    
    public JSONArray longPoll() {
        JSONObject response;
        JSONObject data = this.longPoll;
        
        do {
            String Url = String.format(
                    "http://%s?act=a_check&key=%s&ts=%s&wait=25&mode=2", 
                    data.get("server"), data.get("key"), data.get("ts") );
            response = VKLib.parse(Url);

            if(response.containsKey("error")) {
                this.setLongPoll();
            }
        
        } while(response.containsKey("error"));
        
        data.put("ts", response.get("ts"));
        
        JSONArray updates = (response.containsKey("updates")) ? 
                (JSONArray) response.get("updates") :
                null;
        
        return updates;
    }
    
    public boolean sendMessage(String to, String message) {
        Random rand = new Random();
        Long asd = rand.nextLong();
        message += "\r\n(rand: " + asd.toString() + ")";
        
        try {
            message = URLEncoder.encode(message, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        String url = String.format(URLTpl,
                "messages.send", "user_id=" + to + "&message=" + message);
        
        JSONObject response = VKLib.parse(url);
        System.out.println(response);
        
        return (!response.containsKey("error"));
    }
    
    public boolean setStatus(String text) {
        try {
            text = URLEncoder.encode(text, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        String url = String.format(URLTpl, 
                "status.set", "text=" + text);
        
        JSONObject response = VKLib.parse(url);
        
        return (!response.containsKey("error"));
    }
    
    public JSONArray waitMessage() {
        do
        {
            JSONArray updates = this.longPoll();

            for(int i = 0; i < updates.size(); i++)
            {
                JSONArray update = (JSONArray) updates.get(i);

                String code = update.get(0).toString();
                String flag = update.get(2).toString();

                if(code.equals("4") && !VKLib.isOut(flag)) {
                    return update;
                }
            }
        } while(true);
    }
    
    public JSONObject execute(String method, String params) {
        String url = String.format(URLTpl, 
                method, params);
        
        JSONObject response = VKLib.parse(url);
        
        return response;
    }
}
