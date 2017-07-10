package io.toast.tk.agent.ui.panels;


public enum EnumError {
    NOTHING(0), FILE(1), DIRECTORY(2), URL(3), APIKEY(4), MAIL(5);

    int value;

    EnumError(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
