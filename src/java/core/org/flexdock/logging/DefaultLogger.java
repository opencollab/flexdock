package org.flexdock.logging;

/**
 * Default logging implementation for flexdock.<br>
 * All log statements are sent to <code>System.err</code>.
 * Since this class inherits from <code>SimpleLogger</code>,
 * only warnings and errors are logged, no debug messages.
 * 
 * @deprecated
 */
class DefaultLogger extends SimpleLogger {

    public void log(String message, Throwable t) {
        if (message != null) {
            System.err.println(message);
        }
        if (t != null) {
            t.printStackTrace();
        }
    }

}
