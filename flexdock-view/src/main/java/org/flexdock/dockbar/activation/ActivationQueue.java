/*
 * Created on Apr 21, 2005
 */
package org.flexdock.dockbar.activation;

import org.flexdock.dockbar.DockbarManager;



/**
 * @author Christopher Butler
 */
public class ActivationQueue extends Thread {
    private DockbarManager manager;
    private Animation deactivation;
    private Runnable postDeactivate;
    private Animation activation;
    private Runnable postActivate;

    public ActivationQueue(DockbarManager mgr, Animation deactivation, Runnable r1, Animation activation, Runnable r2) {
        manager = mgr;
        this.deactivation = deactivation;
        this.postDeactivate = r1;
        this.activation = activation;
        this.postActivate = r2;
    }


    public void run() {
        manager.setAnimating(true);
        if(deactivation!=null)
            deactivation.run();
        postDeactivate.run();
        if(activation!=null)
            activation.run();
        postActivate.run();
        manager.setAnimating(false);
    }



}
