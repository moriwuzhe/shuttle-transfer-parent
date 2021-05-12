package org.moriwuzhe.shuttle.common.logger.log4j;

import org.apache.log4j.Level;
import org.moriwuzhe.shuttle.common.config.Version;
import org.moriwuzhe.shuttle.common.logger.Logger;
import org.moriwuzhe.shuttle.common.logger.support.FailsafeLogger;
import org.moriwuzhe.shuttle.common.utils.StringUtils;

public class Log4jLogger implements Logger {

    private static final String FQCN = FailsafeLogger.class.getName();

    private final org.apache.log4j.Logger logger;

    public Log4jLogger(org.apache.log4j.Logger logger) {
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
        logger.log(FQCN, Level.TRACE, e == null ? null : e.getMessage(), e);
    }

    @Override
    public void trace(String msg, Throwable e, Object... args) {
        logger.log(FQCN, Level.TRACE, appendContextMessage(msg, args), e);
    }

    @Override
    public void debug(String msg, Object... args) {
        this.debug(msg, parseThrowable(args), args);
    }

    @Override
    public void debug(Throwable e) {
        logger.log(FQCN, Level.DEBUG, e == null ? null : e.getMessage(), e);
    }

    @Override
    public void debug(String msg, Throwable e, Object... args) {
        logger.log(FQCN, Level.DEBUG, appendContextMessage(msg, args), e);
    }

    @Override
    public void info(String msg, Object... args) {
        this.info(msg, parseThrowable(args), args);
    }

    @Override
    public void info(Throwable e) {
        logger.log(FQCN, Level.INFO, e == null ? null : e.getMessage(), e);
    }

    @Override
    public void info(String msg, Throwable e, Object... args) {
        logger.log(FQCN, Level.INFO, appendContextMessage(msg, args), e);
    }

    @Override
    public void warn(String msg, Object... args) {
        this.warn(msg, parseThrowable(args), args);
    }

    @Override
    public void warn(Throwable e) {
        logger.log(FQCN, Level.WARN, e == null ? null : e.getMessage(), e);
    }

    @Override
    public void warn(String msg, Throwable e, Object... args) {
        logger.log(FQCN, Level.WARN, appendContextMessage(msg, args), e);
    }

    @Override
    public void error(String msg, Object... args) {
        this.error(msg, parseThrowable(args), args);
    }

    @Override
    public void error(Throwable e) {
        logger.log(FQCN, Level.ERROR, e == null ? null : e.getMessage(), e);
    }

    @Override
    public void error(String msg, Throwable e, Object... args) {
        logger.log(FQCN, Level.ERROR, appendContextMessage(msg, args), e);
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
        return logger.isEnabledFor(Level.WARN);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isEnabledFor(Level.ERROR);
    }

}
