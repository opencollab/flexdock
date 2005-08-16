package org.flexdock.logging;

/**
 * If a client application wants control over where internal flexdock
 * logging statements are written it should supply an instance of this
 * interface to {@link org.flexdock.logging.Log#setLogger(Logger)} at
 * application startup time.
 */
public interface Logger {

    /**
     * Log an error.
     * An error is a condition that may cause flexdock to behave
     * unexpectedly afterwards.
     * @param message error message to log
     */
    void error(String message);

    /**
     * Log an error and provide a <code>Throwable</code> for details.
     * An error is a condition that may cause flexdock to behave
     * unexepctedly afterwards.
     * @param message error message to log
     * @param t details of the error
     */
    void error(String message, Throwable t);

    /**
     * Log a warning.
     * A warning is a condition that will not cause flexdock to behave
     * unexpectedly but is considered severe enough that it should be
     * noticed in the log by support staff.
     * @param message warning message to log
     */
    void warn(String message);

    /**
     * Log a warning and provide a <code>Throwable</code> for details.
     * A warning is a condition that will not cause flexdock to behave
     * unexpectedly but is considered severe enough that it should be
     * noticed in the log by support staff.
     * @param message warning message to log
     * @param t details of the warning
     */
    void warn(String message, Throwable t);

    /**
     * Log a debug message.
     * A debug message is not expected to be logged in production systems.
     * It can be used to help in debugging functionality under development.
     * @param message debug message to log
     */
    void debug(String message);

    /**
     * Log a debug message and provide a <code>Throwable</code> for details.
     * A debug message is not expected to be logged in production systems.
     * It can be used to help in debugging functionality under development.
     * @param message debug message to log
     * @param t details of the debug message
     */
    void debug(String message, Throwable t);

}
