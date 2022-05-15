package vmn.application.Config;

import java.util.List;

public class ReponseServer {
    private  final List<String> content;
    private final String error;
    private final String message;

    public ReponseServer(String error, List<String> content, String message) {
        this.error = error;
        this.content = content;
        this.message = message;
    }

    public String getError(){
        return error;
    }

    public List<String> getContent(){
        return content;
    }

    public String getMessage(){
        return message;
    }
}
