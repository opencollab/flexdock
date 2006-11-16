package org.flexdock.logging;

/**
 * Abstract base class for implementing <code>Logger</code>
 * instances that don't care about the difference of warnings
 * and errors.<br>
 * All debug messages are swallowed, all warnings and errors are passed on
 * to the abstract method {@link #log(String, Throwable)}.
 * 
 * @deprecated
 */
public abstract class SimpleLogger implements Logger {

    public abstract void log(String message, Throwable t);

    public void debug(String message, Throwable t) {
        // ignore debug messages
    }

    public void debug(String message) {
        // ignore debug messages
    }

    public void error(String message, Throwable t) {
        log(message, t);
    }

    public void error(String message) {
        log(message, null);        
    }

    public void warn(String message, Throwable t) {
        log(message, t);        
    }

    public void warn(String message) {
        log(message, null);        
    }
    
}
