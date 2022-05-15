package vmn.application.Main;

import net.minidev.json.*;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import vmn.application.Config.GetId;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class MappingAdmin {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(String.valueOf(GetIdController.class));

    @GetMapping("/admin.php")
    public String getIdTask(
            @RequestParam(value = "googlekey",defaultValue = "googlekey") String googlekey,
            @RequestParam(value = "pageurl",defaultValue = "pageurl") String pageurl) throws IOException {

//        //Demo Capt
        String reponse = "";
        try{
//            String generated_pass_UUID = null;
//            String site_key = "51829642-2cda-4b09-896c-594f89d700cc";
//            String site_url = "http://democaptcha.com/demo-form-eng/hcaptcha.html";
//            String domain = "http://democaptcha.com";
            String site_key;
            String site_url;
            String domain = "";

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

            String Data = "{\"site_key\"" + ": \"" + site_key + "\"," +
                    "\"site_url\": " + "\"" + site_url + "\"," +
                    "\"host\": " + "\"" + domain + "\"}";

            reponse = sendingPostRequest(Data);
//        try{
//            Object obj = JSONValue.parse(reponse);
//            JSONObject jsonObject = (JSONObject) obj;
//            boolean status = (boolean) jsonObject.get("error");
//            if(!status){
//                JSONObject content = (JSONObject) jsonObject.get("content");
//                generated_pass_UUID = (String) content.get("generated_pass_UUID");
//            }else {
//                generated_pass_UUID = "error";
//            }
//        }catch (Exception ex){
//
//        }
        }catch (Exception ex)
        {
            logger.error("Get Id Mapping fail: " + ex.toString());
            reponse = "SERVER ERROR!";
        }
        return reponse;
    }

    private String sendingPostRequest(String Data) throws IOException {
        String result = "Error";
        String url = "http://127.0.0.1:8081";
        BufferedReader in = null;
        DataOutputStream wr = null;
        HttpURLConnection con = null;

        try{
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();

            // Setting basic post request
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Content-Type","application/json");

            // Send post request
            con.setDoOutput(true);
            wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(Data);
            wr.flush();


            int responseCode = con.getResponseCode();

            logger.info("nSending 'POST' request to URL : " + url);
            logger.info("Post Data : " + Data);
            logger.info("Response Code : " + responseCode);

            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            //StringBuffer response = new StringBuffer();
            StringBuilder response = new StringBuilder();

            String output;
            while ((output = in.readLine()) != null) {
                response.append(output);
            }

            result = response.toString();
        }catch (Exception ignored){

        }finally {
            if(con != null){
                con.disconnect();
            }
            if(wr != null){
                wr.close();
            }
            if(in != null){
                in.close();
            }
        }

        return result;
    }
}
