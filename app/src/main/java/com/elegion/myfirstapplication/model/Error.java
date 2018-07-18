package com.elegion.myfirstapplication.model;

import com.google.gson.annotations.SerializedName;

public class Error {

    @SerializedName("errors")
    private Error.DataBean mData;

    public Error.DataBean getData() {
        return mData;
    }

    public void setData(Error.DataBean data) {
        mData = data;
    }

    public static class DataBean {
        @SerializedName("email")
        private String[] mEmail;

        @SerializedName("name")
        private String[] mName;

        @SerializedName("password")
        private String[] mPassword;

        public String[] getEmail() {
            return mEmail;
        }

        public void setEmail(String[] email) {
            this.mEmail = email;
        }

        public String[] getName() {
            return mName;
        }

        public void setName(String[] name) {
            this.mName = name;
        }

        public String[] getPassword() {
            return mPassword;
        }

        public void setPassword(String[] password) {
            this.mPassword = password;
        }
    }
}
