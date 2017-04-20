package io.toast.tk.agent.ui.exception;


import io.toast.tk.agent.ui.i18n.CommonMessages;

public class PropertyException extends Exception {

    public PropertyException(){
        super(CommonMessages.PROPERTIES_NOT_STORED);
    }
}
