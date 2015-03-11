package com.synaptix.toast.plugin.swing.agent.listener;

import java.awt.Component;

/**
 * Created by Sallah Kokaina on 17/11/2014.
 */
public interface IComponentCallback {
    public void onComponent(Component c, String name);
    public void onInit();
}
