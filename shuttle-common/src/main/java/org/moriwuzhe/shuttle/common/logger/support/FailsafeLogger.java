package org.moriwuzhe.shuttle.common.logger.support;

import org.moriwuzhe.shuttle.common.logger.Logger;
import org.moriwuzhe.shuttle.common.utils.StringUtils;

public class FailsafeLogger implements Logger {

    private Logger logger;

    public FailsafeLogger(Logger logger) {
        this.logger = logger;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    private String appendContextMessage(String msg, Object... args) {
        return StringUtils.bracketReplace(msg, args);
    }

    @Override
    public void trace(String msg, Throwable e, Object... args) {
        try {
            logger.trace(appendContextMessage(msg, args), e);
        } catch (Throwable t) {
        }
    }

    @Override
    public void trace(Throwable e) {
        try {
            logger.trace(e);
        } catch (Throwable t) {
        }
    }

    @Override
    public void trace(String msg, Object... args) {
        try {
            this.trace(msg, parseThrowable(args), args);
        } catch (Throwable t) {
        }
    }

    @Override
    public void debug(String msg, Throwable e, Object... args) {
        try {
            logger.debug(appendContextMessage(msg, args), e);
        } catch (Throwable t) {
        }
    }

    @Override
    public void debug(Throwable e) {
        try {
            logger.debug(e);
        } catch (Throwable t) {
        }
    }

    @Override
    public void debug(String msg, Object... args) {
        try {
            this.debug(msg, parseThrowable(args), args);
        } catch (Throwable t) {
        }
    }

    @Override
    public void info(String msg, Throwable e, Object... args) {
        try {
            logger.info(appendContextMessage(msg, args), e);
        } catch (Throwable t) {
        }
    }

    @Override
    public void info(String msg, Object... args) {
        try {
            this.info(msg, parseThrowable(args), args);
        } catch (Throwable t) {
        }
    }

    @Override
    public void warn(String msg, Throwable e, Object... args) {
        try {
            logger.warn(appendContextMessage(msg, args), e);
        } catch (Throwable t) {
        }
    }

    @Override
    public void warn(String msg, Object... args) {
        try {
            this.warn(msg, parseThrowable(args), args);
        } catch (Throwable t) {
        }
    }

    @Override
    public void error(String msg, Throwable e, Object... args) {
        try {
            logger.error(appendContextMessage(msg, args), e);
        } catch (Throwable t) {
        }
    }

    @Override
    public void error(String msg, Object... args) {
        try {
            this.error(msg, parseThrowable(args), args);
        } catch (Throwable t) {
        }
    }

    @Override
    public void error(Throwable e) {
        try {
            logger.error(e);
        } catch (Throwable t) {
        }
    }

    @Override
    public void info(Throwable e) {
        try {
            logger.info(e);
        } catch (Throwable t) {
        }
    }

    @Override
    public void warn(Throwable e) {
        try {
            logger.warn(e);
        } catch (Throwable t) {
        }
    }

    @Override
    public boolean isTraceEnabled() {
        try {
            return logger.isTraceEnabled();
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean isDebugEnabled() {
        try {
            return logger.isDebugEnabled();
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean isInfoEnabled() {
        try {
            return logger.isInfoEnabled();
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean isWarnEnabled() {
        try {
            return logger.isWarnEnabled();
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean isErrorEnabled() {
        try {
            return logger.isErrorEnabled();
        } catch (Throwable t) {
            return false;
        }
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

}
