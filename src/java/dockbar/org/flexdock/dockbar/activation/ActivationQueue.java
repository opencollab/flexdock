/*
 * Created on Apr 21, 2005
 */
package org.flexdock.dockbar.activation;



/**
 * @author Christopher Butler
 */
public class ActivationQueue extends Thread {
	private Animation deactivation;
	private Runnable postDeactivate;
	private Animation activation;
	private Runnable postActivate;
	
	public ActivationQueue(final Animation deactivation, final Runnable r1, final Animation activation, final Runnable r2) {
		this.deactivation = deactivation;
		this.postDeactivate = r1;
		this.activation = activation;
		this.postActivate = r2;
	}

	
	public void run() {
		if(deactivation!=null)
			deactivation.run();
		postDeactivate.run();
		if(activation!=null)
			activation.run();
		postActivate.run();
	}
	
	

}
