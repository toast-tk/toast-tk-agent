package com.synaptix.toast.plugin.swing.agent.listener;

import java.awt.Component;

public interface IComponentCallback {
    public void onComponent(Component c, String name);
    public void onInit();
}
