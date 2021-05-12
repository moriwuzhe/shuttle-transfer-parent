package org.moriwuzhe.shuttle.common.logger.jcl;

import org.apache.commons.logging.Log;
import org.moriwuzhe.shuttle.common.config.Version;
import org.moriwuzhe.shuttle.common.logger.Logger;
import org.moriwuzhe.shuttle.common.utils.StringUtils;

import java.io.Serializable;

/**
 * Adaptor to commons logging, depends on commons-logging.jar. For more information about commons logging, pls. refer to
 * <a target="_blank" href="http://www.apache.org/">http://www.apache.org/</a>
 */
public class JclLogger implements Logger, Serializable {

    private static final long serialVersionUID = 1L;

    private final Log logger;

    public JclLogger(Log logger) {
        this.logger = logger;
    }

    private String appendContextMessage(String msg, Object... args) {
        return "[SHUTTLE] " + StringUtils.bracketReplace(msg, args) + ", shuttle version: " + Version.getVersion();
    }

    private Throwable parseThrowable(Object[] args) {
        if (args != null && args.length > 0) {
            Object arg = args[args.length - 1];
            if (arg instanceof Throwable) {
                return (Throwable) arg;
            }
        }
        return null;
    }

    @Override
    public void trace(String msg, Object... args) {
        this.trace(msg, parseThrowable(args), args);
    }

    @Override
    public void trace(Throwable e) {
        logger.trace(e);
    }

    @Override
    public void trace(String msg, Throwable e, Object... args) {
        logger.trace(appendContextMessage(msg, args), e);
    }

    @Override
    public void debug(String msg, Object... args) {
        this.debug(msg, parseThrowable(args), args);
    }

    @Override
    public void debug(Throwable e) {
        logger.debug(e);
    }

    @Override
    public void debug(String msg, Throwable e, Object... args) {
        logger.debug(appendContextMessage(msg, args), e);
    }

    @Override
    public void info(String msg, Object... args) {
        this.info(msg, parseThrowable(args), args);
    }

    @Override
    public void info(Throwable e) {
        logger.info(e);
    }

    @Override
    public void info(String msg, Throwable e, Object... args) {
        logger.info(appendContextMessage(msg, args), e);
    }

    @Override
    public void warn(String msg, Object... args) {
        this.warn(msg, parseThrowable(args), args);
    }

    @Override
    public void warn(Throwable e) {
        logger.warn(e);
    }

    @Override
    public void warn(String msg, Throwable e, Object... args) {
        logger.warn(appendContextMessage(msg, args), e);
    }

    @Override
    public void error(String msg, Object... args) {
        this.error(msg, parseThrowable(args), args);
    }

    @Override
    public void error(Throwable e) {
        logger.error(e);
    }

    @Override
    public void error(String msg, Throwable e, Object... args) {
        logger.error(appendContextMessage(msg, args), e);
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

}
