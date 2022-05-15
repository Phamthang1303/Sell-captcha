package vmn.application.Service;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.slf4j.LoggerFactory;
import vmn.application.Config.GetId;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class CallServerRecapt implements DataApp{

    /**
     * Declare variable
     */
    private AtomicInteger TotalUserActive = new AtomicInteger(0);
    private boolean statuskill = false;
    private HttpRequest request = new HttpRequest();
    private ConcurrentHashMap<String, String> hashMapIdTask = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, LocalDateTime> controlid = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, AtomicLong> queue = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> keyuser = new ConcurrentHashMap<>();
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(String.valueOf(CallServerRecapt.class));

    /**
     * Method private
     */
    private String SolveCaptcha(String id) throws IOException {
        String answerCaptcha = "";
        try {
//            for (int i = 0; i < 24; i++){
//                String url = URL_SERVER_RECAPT + ":" + PORT_SERVER_RECAPT + "/res.php?key=" + KEY_RECAPTCHA + "&action=get&id=" + id;
//                answerCaptcha = request.GetRequest(url);
//
//                if (answerCaptcha.length() < 3)
//                {
//                    return answerCaptcha;
//                }
//                else
//                {
//                    if (answerCaptcha.substring(0, 3) == "OK|")
//                    {
//                        return answerCaptcha;
//                    }
//                    else if (answerCaptcha != "CAPCHA_NOT_READY")
//                    {
//                        return answerCaptcha;
//                    }
//                }
//                Thread.sleep(5000);
//            }
//
//            return "Time out";
            String url = URL_SERVER_RECAPT + ":" + PORT_SERVER_RECAPT + "/res.php?key=" + KEY_RECAPTCHA + "&action=get&id=" + id;
            answerCaptcha = request.GetRequest(url);
            if(answerCaptcha.substring(0, 3).equals("OK|")){
                hashMapIdTask.remove(id);
            }
        }catch (Exception ex){
            logger.error( "RECAPTCHA: Error solve captcha: " + ex.toString());
            answerCaptcha = "SERVER ERROR!";
        }
        return answerCaptcha;
    }

    private void killId(){
        try{
            while (statuskill){
                if(controlid != null && controlid.size() > 0){
                    for (String id: controlid.keySet()) {
                        long seconds = ChronoUnit.SECONDS.between(controlid.get(id),LocalDateTime.now());
                        if(seconds > 120){
                            logger.info("RECAPTCHA: Kill id task : " + id);
                            controlid.remove(id);
                            hashMapIdTask.remove(id);
                        }
                    }
                }
                Thread.sleep(5000);
            }
        }catch (Exception ex){
            logger.error("RECAPTCHA: Error kill id: " + ex.toString());
        }
    }

    /**
     * Method public
     */
    public String GetQueueID(String key, String googlekey, String pageurl){
        String id = "";
        try {
            logger.info("RECAPTCHA: start get queue id for *" + key + "*");
            String url = URL_SERVER_RECAPT + ":" + PORT_SERVER_RECAPT + "/in.php?key=" + KEY_RECAPTCHA + "&method=userrecaptcha&googlekey=" + googlekey + "&pageurl=" + pageurl;

            while (true){
                id = request.GetRequest(url);
                if(id.contains("OK|")){
                    id = id.substring(3);
                    if(!hashMapIdTask.containsKey(id) && id != "") {
                        hashMapIdTask.putIfAbsent(id, key);
                        controlid.putIfAbsent(id,LocalDateTime.now());
                        break;
                    }
                }

                Thread.sleep(1000);
            }

            if(hashMapIdTask.containsKey(id)){
                logger.info("RECAPTCHA: Id for *" + key + "* : " + id);
                return "OK|" + id;
            }
        }catch (Exception ex){
            logger.error("RECAPTCHA: Error get queue id: " + ex.toString());
            id = "SERVER ERROR!";
        }

        return id;
    }

    public String GetResponseAnswer(String key, String Id){
        String answer = "";
        try{
            logger.info("RECAPTCHA: Start get response answer...");
            if(keyuser.containsKey(key)){
                if(!queue.containsKey(key)){
                queue.put(key,new AtomicLong(0));
                }

                if(queue.get(key).intValue() < keyuser.get(key)){
                    queue.get(key).incrementAndGet();
                    TotalUserActive.incrementAndGet();
                    if(hashMapIdTask != null && hashMapIdTask.containsKey(Id)){
                        if(key.equals(hashMapIdTask.get(Id))){
                            logger.info("RECAPTCHA: " + key + " being get answer : " + queue.get(key).intValue());
                            answer = SolveCaptcha(Id);
                        }else {
                            answer = "Key does not match!";
                        }
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
            logger.error("RECAPTCHA: Error get queue id: " + ex.toString());
            answer = "SERVER ERROR!";
        }
        return answer;
    }

    public void ReadKey(){
        try {
            if(keyuser != null){
                keyuser.clear();
            }
            File myObj = new File("fileKeyRecapt.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                try{
                    String data = myReader.nextLine();
                    String datakey = data.split(",")[0];
                    int datalimit = Integer.parseInt(data.split(",")[1]);
                    keyuser.put(datakey,datalimit);
                }catch (Exception ex){

                }
            }
            myReader.close();
            logger.info("RECAPTCHA: Update key - All key: " + keyuser.size());
        } catch (FileNotFoundException e) {
            logger.error( "RECAPTCHA: Error read Key file: " + e.toString());
        }
    }

    public String getQueue(String key){
        if(queue.containsKey(key)){
            logger.info("RECAPTCHA: " + key + "-active: " + queue.get(key).intValue());
            return String.valueOf(queue.get(key).intValue());
        }else {
            return "Apikey does not exit!";
        }
    }

    public String getAllQueue(){
        logger.info("RECAPTCHA: Total user active: " + String.valueOf(TotalUserActive.intValue()));
        return String.valueOf(TotalUserActive.intValue());
    }

    public void killIdTask(String clear){
        try{
            if(clear.equals("on")){
                if(!statuskill){
                    logger.info("RECAPTCHA: Turn on kill task id!");
                    statuskill = true;
                    killId();
                }
            }else if(clear.equals("off")){
                statuskill = false;
                logger.info("RECAPTCHA: Turn off kill task id!");
            }

        }catch (Exception ex){
            logger.error("RECAPTCHA: Error kill id task: " + ex.toString());
        }
    }
}
