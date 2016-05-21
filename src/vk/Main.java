/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vk;

import java.util.*;
import java.util.concurrent.*;
import java.net.URLEncoder;
import java.io.*;

import org.json.simple.*;

/**
 *
 * @author zooZooz
 */
public class Main {

    public static void main(String[] args) {
        Map<String, String> links = new HashMap<String, String>();
        boolean loop = true;
        Queue queue = new LinkedList();
        Vk vk = null;
        ServiceVk service = null;

        /*
         *
         *  Try to read some settings from configuration list / object / etc.
         *
         */
        int maxThreads = 10;
        String token = "";

        // trying to set vk-account
        try {
            vk = new Vk(token);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // trying to set service-thread
        try {
            service = new ServiceVk(vk);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        service.setName("service");
        service.setPriority(Thread.MIN_PRIORITY);
        service.start();
        
        String help = "Справка:\r\n!чат -- начать чат.\r\n!дальше -- искать нового собеседника.\r\n!стоп -- закончить чат.\r\n!очередь -- количество человек в очереди.\r\n!пары -- количество общающихся пар.\r\n[ADMIN]\r\n!стопбот -- остановить бота.";

        //ExecutorService threadPool = Executors.newFixedThreadPool(maxThreads);
        //System.out.println(vk.waitMessage());
        while (loop) {
            JSONArray message = vk.waitMessage();

            String text = message.get(6).toString();
            String from = message.get(3).toString();

            if (text.charAt(0) != '!') {
                String pair = VKLib.getPair(links, from);
                if (pair != null) {
                    vk.sendMessage(pair, text);
                } else {
                    vk.sendMessage(from, "Вы не в чате.\r\nДля помоши наберите !справка.");
                }
            } else {
                String[] command = text.split(" ");
                String pair = VKLib.getPair(links, from);

                switch (command[0]) {
                    case "!чат":
                        if (pair == null) {
                            queue.add(from);
                            vk.sendMessage(from, "Вы были добавлены в очередь.");
                        } else {
                            vk.sendMessage(from, "Вы уже в чате.");
                        }
                        break;
                    case "!дальше":
                        if (pair != null) {
                            links.remove(from);
                            links.remove(pair);
                            queue.add(from);
                            vk.sendMessage(from, "Ищем нового собеседника.");
                            vk.sendMessage(pair, "Собеседник завершил беседу.");
                        } else {
                            vk.sendMessage(from, "Вы не в чате.");
                        }
                        break;
                    case "!стоп":
                        if (pair != null) {
                            links.remove(from);
                            links.remove(pair);
                            vk.sendMessage(from, "Вы завершили беседу.");
                            vk.sendMessage(pair, "Собеседник завершил беседу.");
                        } else if (queue.contains(from)) {
                            queue.remove(from);
                            vk.sendMessage(from, "Вы были удалены из очереди.");
                        } else {
                            vk.sendMessage(from, "Вы не в чате.");
                        }
                        break;
                    case "!стопбот":
                        if(from.equals("135901159")) {
                            vk.sendMessage(from, "Бoт остановлен.");
                            loop = false;
                        } else {
                            vk.sendMessage(from, "Вы не админ.");
                        }
                        break;
                    case "!очередь":
                        vk.sendMessage(from, queue.size() + " человек в очереди.");
                        break;
                    case "!пары":
                        vk.sendMessage(from, links.size() + " пар в системе.");
                        break;
                    case "!справка":
                        vk.sendMessage(from, help);
                        break;
                    default:
                        vk.sendMessage(from, "Команда не найдена.");
                        break;
                }
            }
            
            if(queue.size() >= 2) {
                String first = queue.poll().toString();
                String second = queue.poll().toString();
                
                links.put(first, second);
                
                vk.sendMessage(first, "Чат начат.");
                vk.sendMessage(second, "Чат начат.");
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        service.interrupt();
    }
}
