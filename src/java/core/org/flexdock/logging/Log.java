package org.flexdock.logging;

/**
 * This class provides static log methods that should be used for logging
 * from inside other flexdock classes.
 * 
 * @deprecated
 */
public class Log {

    /**
     * The default instance used for logging as long as the client
     * application does not set its own <code>Logger</code>.
     */
    private static Logger logger = new DefaultLogger();

    /**
     * Set a new <code>Logger</code> instance to perform the actual
     * logging.
     * @param newLogger Logger instance, may not be <code>null</code>
     */
    public static void setLogger(Logger newLogger) {
        if (newLogger == null) {
            throw new NullPointerException("Logger may not be null.");
        }
        logger = newLogger;
    }
    
    /**
     * Log an error.
     * An error is a condition that may cause flexdock to behave
     * unexpectedly afterwards.
     * @param message error message to log
     */    
    public static void error(String message) {
        logger.error(message);
    }

    /**
     * Log an error and provide a <code>Throwable</code> for details.
     * An error is a condition that may cause flexdock to behave
     * unexepctedly afterwards.
     * @param message error message to log
     * @param t details of the error
     */
    public static void error(String message, Throwable t) {
        logger.error(message, t);
    }

    /**
     * Log a warning.
     * A warning is a condition that will not cause flexdock to behave
     * unexpectedly but is considered severe enough that it should be
     * noticed in the log by support staff.
     * @param message warning message to log
     */
    public static void warn(String message) {
        logger.warn(message);
    }

    /**
     * Log a warning and provide a <code>Throwable</code> for details.
     * A warning is a condition that will not cause flexdock to behave
     * unexpectedly but is considered severe enough that it should be
     * noticed in the log by support staff.
     * @param message warning message to log
     * @param t details of the warning
     */
    public static void warn(String message, Throwable t) {
        logger.warn(message, t);
    }

    /**
     * Log a debug message.
     * A debug message is not expected to be logged in production systems.
     * It can be used to help in debugging functionality under development.
     * @param message debug message to log
     */
    public static void debug(String message) {
        logger.debug(message);
    }
    
    /**
     * Log a debug message and provide a <code>Throwable</code> for details.
     * A debug message is not expected to be logged in production systems.
     * It can be used to help in debugging functionality under development.
     * @param message debug message to log
     * @param t details of the debug message
     */
    public static void debug(String message, Throwable t) {
        logger.debug(message, t);
    }


    /**
     * Private constructor to prevent instantiation. 
     */
    private Log() {
    }

}
