package io.toast.tk.agent.ui;


import java.io.File;
import java.util.Properties;

public class PropertiesHolder {

    private final Properties p;
    private final File file;

    public PropertiesHolder(Properties p, File file){
        this.p = p;
        this.file = file;
    }

    public File getFile(){
        return file;
    }

    public Properties getProperties(){
        return p;
    }
}
