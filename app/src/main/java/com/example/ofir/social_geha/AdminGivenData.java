package com.example.ofir.social_geha;

import java.io.Serializable;
import java.util.List;

/**
 * Encapsulates the data the admin assigned to a user on the admin side website
 * This class is a fancy POJO (because it can be reconstructed by Firebase)
 */
public class AdminGivenData implements Serializable {
    private String id;
    private String name;
    private String phone;
    private String user_code;
    private String kind;
    private List<String> departments;

    public List<String> getDepartments() {
        return departments;
    }

    public String getId() {
        return id;
    }

    public String getKind() {
        return kind;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getUser_code() {
        return user_code;
    }
}
