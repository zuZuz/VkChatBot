/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vk;

import java.io.*;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 *
 * @author zooZooz
 */

class ServiceVk extends Thread
{
    private Vk vk;
    private String script = "";
    
    public ServiceVk(Vk vk) throws FileNotFoundException
    {
        this.vk = vk;
        
        try {
            Scanner in = new Scanner(new File("scripts/friends.vk"));
            StringBuffer data = new StringBuffer();
            while(in.hasNext())
                data.append(in.nextLine()).append("\n");
            script += data.toString();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Scripts not found");
        }
        
        try {
            script = URLEncoder.encode(script, "UTF-8");
        } catch (Exception e) { }
    }
    
    @Override
    public void run()
    {
        do
        {
            if(!Thread.interrupted()) {
                
                // this
                vk.execute("execute", "code=" + script);
                
            }
            else {
                break;
            }
            
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                break;
            }
                
        } while (true);
        
        vk.setStatus("Off");
        vk.execute("account.setOffline", "");
    }
}