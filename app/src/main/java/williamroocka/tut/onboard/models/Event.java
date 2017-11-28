package williamroocka.tut.onboard.models;

import java.io.Serializable;


public class Event implements Serializable {
    public Long id;
    public String latitude;
    public String longitude;
    public String type;
    public Long user_id;
    public String created_at;
    public String updated_at;
}
