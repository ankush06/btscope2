package com.example.myapplication1;
import android.app.Application;
public class BTinterface extends Application{
    String inputDevice_MAC;



    public String getInputDevice_MAC() {
        return inputDevice_MAC;
    }

    public void setInputDevice_MAC(String inputDevice_MAC) {
        this.inputDevice_MAC = inputDevice_MAC;
    }
}
