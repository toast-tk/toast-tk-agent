package com.synaptix.toast.core.guice.plugin;

import com.google.inject.Module;

public interface IModulesDiscoveryManager {

    public Iterable<Module> getModules();
    
}
