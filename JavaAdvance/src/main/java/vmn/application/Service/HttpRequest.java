package vmn.application.Service;

import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;

public class HttpRequest implements DataApp {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(String.valueOf(CallServerRecapt.class));


    public String PostRequestHCaptcha(String Data) throws IOException {
        logger.info("Request captcha to Server...");
        String result = "Error";
        String url = URL_SERVER_HCAPT + ":" + PORT_SERVER_HCAPT;
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
            logger.error("Error post request captcha: " + ignored.toString());
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

    public String GetRequest(String urlToRead){
        try{
            logger.info("Start get request from *" + urlToRead +"*");
            StringBuilder result = new StringBuilder();
            URL url = new URL(urlToRead);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    result.append(line);
                }
            }
            return result.toString();
        }catch (Exception ex){
            logger.error("Get request fail!");
        }
        return "Fail";
    }
}
