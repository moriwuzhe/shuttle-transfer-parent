package org.moriwuzhe.shuttle.common.logger.jdk;

import org.moriwuzhe.shuttle.common.config.Version;
import org.moriwuzhe.shuttle.common.logger.Logger;
import org.moriwuzhe.shuttle.common.utils.StringUtils;

import java.util.logging.Level;

public class JdkLogger implements Logger {

    private final java.util.logging.Logger logger;

    public JdkLogger(java.util.logging.Logger logger) {
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
        logger.log(Level.FINER, e.getMessage(), e);
    }

    @Override
    public void trace(String msg, Throwable e, Object... args) {
        logger.log(Level.FINER, appendContextMessage(msg, args), e);
    }

    @Override
    public void debug(String msg, Object... args) {
        this.debug(msg, parseThrowable(args), args);
    }

    @Override
    public void debug(Throwable e) {
        logger.log(Level.FINE, e.getMessage(), e);
    }

    @Override
    public void debug(String msg, Throwable e, Object... args) {
        logger.log(Level.FINE, appendContextMessage(msg, args), e);
    }

    @Override
    public void info(String msg, Object... args) {
        this.info(msg, parseThrowable(args), args);
    }

    @Override
    public void info(String msg, Throwable e, Object... args) {
        logger.log(Level.INFO, appendContextMessage(msg, args), e);
    }

    @Override
    public void warn(String msg, Object... args) {
        this.warn(msg, parseThrowable(args), args);
    }

    @Override
    public void warn(String msg, Throwable e, Object... args) {
        logger.log(Level.WARNING, appendContextMessage(msg, args), e);
    }

    @Override
    public void error(String msg, Object... args) {
        this.error(msg, parseThrowable(args), args);
    }

    @Override
    public void error(String msg, Throwable e, Object... args) {
        logger.log(Level.SEVERE, appendContextMessage(msg, args), e);
    }

    @Override
    public void error(Throwable e) {
        logger.log(Level.SEVERE, e.getMessage(), e);
    }

    @Override
    public void info(Throwable e) {
        logger.log(Level.INFO, e.getMessage(), e);
    }

    @Override
    public void warn(Throwable e) {
        logger.log(Level.WARNING, e.getMessage(), e);
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isLoggable(Level.FINER);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isLoggable(Level.WARNING);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

}
