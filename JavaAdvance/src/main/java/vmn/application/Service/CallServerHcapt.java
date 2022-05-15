package vmn.application.Service;

import javafx.concurrent.Task;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.slf4j.LoggerFactory;
import vmn.application.Config.GetId;
import vmn.application.Main.GetIdController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CallServerHcapt {

    /**
     * Declare variable
     */
    private AtomicInteger TotalUserActive = new AtomicInteger(0);
    private boolean statuskill = false;
    private String ApiKey;
    private String Id;
    private String DataJson;
    HttpRequest request = new HttpRequest();
    private ConcurrentHashMap<String, GetId> hashMapRequest = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, LocalDateTime> controlid = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, AtomicLong> queue = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> keyuser = new ConcurrentHashMap<>();
    //private ConcurrentLinkedQueue<String, Integer> queue = new ConcurrentLinkedQueue<String, Integer>();

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(String.valueOf(CallServerHcapt.class));

    /**
     * Method private
     */

    private String CreateID(){
        try{
            int max = 9999999;
            int min = 1000000;
            Random rand = new Random();
            int id = rand.nextInt((max - min) + 1) + min;
            return String.valueOf(id);
        }catch (Exception ex){
            logger.error("HCAPT: Error creating id: " + ex.toString());
            return "";
        }
    }

    private String SolveCaptcha(String id,GetId getID) throws IOException {
        String generated_pass_UUID = "";
        try {
            String Data = getID.getDatajson();
            String responseJson = request.PostRequestHCaptcha(Data);
            try{
                Object obj = JSONValue.parse(responseJson);
                JSONObject jsonObject = (JSONObject) obj;
                boolean status = (boolean) jsonObject.get("error");
                if(!status){
                    JSONObject content = (JSONObject) jsonObject.get("content");
                    generated_pass_UUID = (String) content.get("generated_pass_UUID");
                    if(!generated_pass_UUID.contains("null")){
                        generated_pass_UUID = "OK|" + generated_pass_UUID;
                        logger.info("HCAPT: Solve successful!!");
                        logger.info("HCAPT: Kill id task " + id);
                        hashMapRequest.remove(id);
                    }else {
                        generated_pass_UUID = "CAPCHA_NOT_READY!";
                    }

                }else {
                    generated_pass_UUID = "CAPCHA_NOT_READY!";
                }
            }catch (Exception ex){
                generated_pass_UUID = "CAPCHA_NOT_READY_2!";
            }
        }catch (Exception ex){
            logger.error("HCAPT: Error solve captcha: " + ex.toString());
            generated_pass_UUID = "SERVER ERROR!";
        }
        return generated_pass_UUID;
    }

    private void killId(){
        try{
            while (statuskill){
                if(controlid != null && controlid.size() > 0){
                    for (String id: controlid.keySet()) {
                        long seconds = ChronoUnit.SECONDS.between(controlid.get(id),LocalDateTime.now());
                        if(seconds > 120){
                            logger.info("HCAPT: Kill id task : " + id);
                            controlid.remove(id);
                            hashMapRequest.remove(id);
                        }
                    }
                }
                Thread.sleep(5000);
            }
        }catch (Exception ex){
            logger.error("HCAPT: Error kill id: " + ex.toString());
        }
    }

    /**
     * Method public
     */
    public String GetQueueID(String key, String dataJson){
        try {
            logger.info("HCAPT: Start get queue id for *" + key + "*");
            GetId getId = new GetId(key,dataJson);
            String id = "";
            while (true){
                id = CreateID();
                if(!hashMapRequest.containsKey(id) && id != "") {
                    hashMapRequest.putIfAbsent(id, getId);
                    controlid.putIfAbsent(id,LocalDateTime.now());
                    break;
                }
            }

            if(hashMapRequest.containsKey(id)){
                logger.info("HCAPT: Id for *" + key + "* : " + id);
                return "OK|" + id;
            }
        }catch (Exception ex){
            logger.error("HCAPT: Error get queue id: " + ex.toString());
        }

        return "";
    }

    public String GetResponseAnswer(String key, String Id){
        String answer = "";
        try{
            logger.info("HCAPT: Start get response answer...");
            if(keyuser.containsKey(key)){
                if(!queue.containsKey(key)){
                queue.put(key,new AtomicLong(0));
                }
                if(queue.get(key).intValue() < keyuser.get(key)){
                    queue.get(key).incrementAndGet();
                    TotalUserActive.incrementAndGet();

                    logger.info("HCAPT: " + key + " being get answer : " + queue.get(key).intValue());
                    if(hashMapRequest != null && hashMapRequest.containsKey(Id)){
                        answer = SolveCaptcha(Id,hashMapRequest.get(Id));
                    }else {
                        answer = "Id Task does not exits!";
                    }

                    TotalUserActive.decrementAndGet();
                    queue.get(key).decrementAndGet();
                }else {
                    answer = "Limit solver!";
                }
            }else {
                answer = "ApiKey does not exist!";
            }
        }catch (Exception ex)
        {
            logger.error("HCAPT: Error get queue id: " + ex.toString());
            answer = "SERVER ERROR!";
        }
        return answer;
    }

    public void ReadKey(){
        try {
            if(keyuser != null){
                keyuser.clear();
            }
            File myObj = new File("fileKeyHcapt.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                try{
                    String data = myReader.nextLine();
                    String datakey = data.split(",")[0];
                    int datalimit = Integer.parseInt(data.split(",")[1]);
                    keyuser.put(datakey,datalimit);
                }catch (Exception ex){
                    logger.error( "HCAPT: Error read key " + myReader.nextLine() + ": " + ex.toString());
                }
            }
            myReader.close();
            logger.info("HCAPT: Update key - All key: " + keyuser.size());
        } catch (FileNotFoundException e) {
            logger.error( "HCAPT: Error read Key file: " + e.toString());
        }
    }

    public String getQueue(String key){
        if(queue.containsKey(key)){
            logger.error("HCAPT: " + key + "-active: " + queue.get(key).intValue());
            return String.valueOf(queue.get(key).intValue());
        }else {
            return "Apikey does not exit!";
        }
    }

    public String getAllQueue(){
        logger.error("HCAPT: Total user active: " + String.valueOf(TotalUserActive.intValue()));
        return String.valueOf(TotalUserActive.intValue());
    }

    public void killIdTask(String clear){
        try{
            if(clear.equals("on")){
                if(!statuskill){
                    logger.info("HCAPT: Turn on kill task id!");
                    statuskill = true;
                    killId();
                }
            }else if(clear.equals("off")){
                logger.info("HCAPT: Turn off kill task id!");
                statuskill = false;
            }

        }catch (Exception ex){
            logger.error("HCAPT: Error kill id task: " + ex.toString());
        }
    }
}
