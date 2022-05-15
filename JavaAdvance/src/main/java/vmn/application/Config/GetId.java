package vmn.application.Config;

public class GetId {
    private final String key;
    private final String datajson;

    public GetId(String key,String datajson) {
        this.key = key;
        this.datajson = datajson;
    }

    public String getKey(){
        return key;
    }

    public String getDatajson(){
        return datajson;
    }
}
