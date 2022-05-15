package vmn.application.Main;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import vmn.application.Service.CallServerHcapt;
import vmn.application.Service.CallServerRecapt;
import java.io.IOException;

@RestController
public class GetIdController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(String.valueOf(GetIdController.class));
    private final CallServerHcapt callHcapt = new CallServerHcapt();
    private final CallServerRecapt callRecapt = new CallServerRecapt();

    @GetMapping("/in.php")
    public String getIdTask(
            @RequestParam(value = "key",defaultValue = "key") String key,
            @RequestParam(value = "method",defaultValue = "method") String method,
            @RequestParam(value = "googlekey",defaultValue = "googlekey") String googlekey,
            @RequestParam(value = "pageurl",defaultValue = "pageurl") String pageurl) throws IOException {

        String idTask = "";
        try{
            String apiKey;
            String site_key;
            String site_url;
            String domain = "";
            //51829642-2cda-4b09-896c-594f89d700cc
            //http://democaptcha.com/demo-form-eng/hcaptcha.html

            if(!key.equals("key")){
                apiKey = key;
            }else {
                return "API_KEY NOT FOUND!";
            }

            if(!googlekey.equals("googlekey")){
                site_key = googlekey;
            }else {
                return "GOOGLEKEY NOT FOUND!";
            }

            if(!pageurl.equals("pageurl")){
                site_url = pageurl;
            }else {
                return "PAGEURL NOT FOUND!";
            }

            if(site_url.contains("http://")){
                site_url = site_url.trim();
                domain = site_url.substring(7);
                int index = domain.indexOf('/');
                domain = domain.substring(0,index);
                domain = "http://" + domain;
            }else if(site_url.contains("https://")){
                site_url = site_url.trim();
                domain = site_url.substring(8);
                int index = domain.indexOf('/');
                domain = domain.substring(0,index);
                domain = "https://" + domain;
            }

            if(!method.equals("method")){
                if(method.equals("hcaptcha")){
                    String Data = "{\"site_key\"" + ": \"" + site_key + "\"," +
                            "\"site_url\": " + "\"" + site_url + "\"," +
                            "\"host\": " + "\"" + domain + "\"}";

                    idTask = callHcapt.GetQueueID(apiKey,Data);

                    if(idTask != null && idTask != ""){
                        return idTask;
                    }
                }else if(method.equals("userrecaptcha")){
                    idTask = callRecapt.GetQueueID(key, googlekey, pageurl);
                }
            }

        }catch (Exception ex){
            logger.error("Get Id Mapping fail: " + ex.toString());
            idTask = "SERVER ERROR!";
        }

        return idTask;
    }


    @GetMapping("/res.php")
    public String getReponseCaptchaAnswer(
            @RequestParam(value = "key",defaultValue = "key") String key,
            @RequestParam(value = "method",defaultValue = "method") String method,
            @RequestParam(value = "id",defaultValue = "id") String id){
        String reponse = "";
        try{
            String generated_pass_UUID = null;
            String apiKey = null;
            String idTask = null;

            if(!key.equals("key")){
                apiKey = key;
            }else {
                return "API_KEY NOT FOUND!";
            }

            if(!id.equals("id")){
                idTask = id;
            }else {
                return "ID NOT FOUND!";
            }

            if(!method.equals("action")){
                if(method.equals("hcaptcha")){
                    reponse = callHcapt.GetResponseAnswer(apiKey,idTask);
                }else if(method.equals("recaptcha")){
                    reponse = callRecapt.GetResponseAnswer(apiKey,idTask);
                }
            }

        }catch (Exception ex){
            logger.error("Get Response Answer Mapping fail: " + ex.toString());
            reponse = "SERVER ERROR!";
        }
        return reponse;
    }

    @GetMapping("/updatekey.php")
    public String updateKeyUser(
            @RequestParam(value = "method",defaultValue = "method") String method,
            @RequestParam(value = "action",defaultValue = "action") String action){
        String reponse = "";
        try{
            if(action.equals("admin")){
                if(method.equals("method")){
                    callHcapt.ReadKey();
                    callRecapt.ReadKey();
                    return "Done 2!";
                }else if(method.equals("hcaptcha")){
                    callHcapt.ReadKey();
                    return "Update key Hcapt Done!";
                }else if(method.equals("recaptcha")){
                    callRecapt.ReadKey();
                    return "Update key Recapt Done!";
                }
            }else {
                return "Error!";
            }

        }catch (Exception ex){
            logger.error("Get Response Answer Mapping fail: " + ex.toString());
            reponse = "SERVER ERROR!";
        }
        return reponse;
    }

    @GetMapping("/checkthread.php")
    public String checkThread(
            @RequestParam(value = "method",defaultValue = "method") String method,
            @RequestParam(value = "key",defaultValue = "key") String key){
        String reponse = "";
        try{
            if(!key.equals("key")){
                if(method.equals("method")){
                    return "Method null!";
                }else if(method.equals("hcaptcha")){
                    return "Queue Hcaptcha: " + callHcapt.getQueue(key);
                }else if(method.equals("recaptcha")){
                    return "Queue Recaptcha: " + callRecapt.getQueue(key);
                }

            }else {
                return "Key does not exits!!";
            }

        }catch (Exception ex){
            logger.error("Get Response Answer Mapping fail: " + ex.toString());
            reponse = "SERVER ERROR!";
        }
        return reponse;
    }

    @GetMapping("/checkallthread.php")
    public String checkAllThread(
            @RequestParam(value = "method",defaultValue = "method") String method){
        String reponse = "";
        try{
            if(method.equals("method")){
                return "Method null!";
            }else if(method.equals("hcaptcha")){
                return "Queue Hcaptcha All: " + callHcapt.getAllQueue();
            }else if(method.equals("recaptcha")){
                return "Queue Recaptcha All: " + callRecapt.getAllQueue();
            }
        }catch (Exception ex){
            logger.error("Get Response Answer Mapping fail: " + ex.toString());
            reponse = "SERVER ERROR!";
        }
        return reponse;
    }

    @GetMapping("/cleartask.php")
    public String clearTask(
            @RequestParam(value = "method",defaultValue = "method") String method,
            @RequestParam(value = "clear",defaultValue = "on") String clear){
        try{
            if(method.equals("method")){
                callHcapt.killIdTask(clear);
                callRecapt.killIdTask(clear);
                return "Task ID clear " + clear + "!";
            }else if(method.equals("hcaptcha")){
                callHcapt.killIdTask(clear);
                return "Task ID Hcaptcha clear " + clear + "!";
            }else if(method.equals("recaptcha")){
                callRecapt.killIdTask(clear);
                return "Task ID Recaptcha clear " + clear + "!";
            }
        }catch (Exception ex){
            logger.error("Get Response Answer Mapping fail: " + ex.toString());
            return "SERVER ERROR!";
        }
        return "Task ID clear " + clear + "!";
    }
}
