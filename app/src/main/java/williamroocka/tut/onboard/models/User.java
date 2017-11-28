package williamroocka.tut.onboard.models;

import java.io.Serializable;

public class User implements Serializable {
    public Long id;
    public String name;
    public String email;
    public String date_of_birth;
    public String gender;
    public String approved;
    public String token;
    public String password_hash;
    public Department department;
    public String created_at;
    public String updated_at;
}
