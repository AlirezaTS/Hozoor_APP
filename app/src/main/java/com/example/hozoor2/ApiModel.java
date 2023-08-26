package com.example.hozoor2;

import com.google.gson.annotations.SerializedName;

public class ApiModel {
    public class register {
        @SerializedName("status")
        String status;
        @SerializedName("token")
        String token;
        public String getStatus() {
            return status;
        }
        public String getToken() {
            return token;
        }
    }
    public class checkInOut {
        @SerializedName("status")
        String status;
        public String getStatus(){
            return status;
        }
    }

    public class checkToken{
        @SerializedName("name")
        String name;
        public String getName(){
            return name;
        }
        @SerializedName("status")
        String status;
        public String getStatus(){
            return status;
        }
        @SerializedName("type")
        boolean type;
        public boolean getType(){
            return type;
        }
    }
}
