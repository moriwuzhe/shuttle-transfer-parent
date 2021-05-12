package org.moriwuzhe.shuttle.common.logger.jcl;

import org.apache.commons.logging.LogFactory;
import org.moriwuzhe.shuttle.common.logger.Level;
import org.moriwuzhe.shuttle.common.logger.Logger;
import org.moriwuzhe.shuttle.common.logger.LoggerAdapter;

import java.io.File;

public class JclLoggerAdapter implements LoggerAdapter {

    private Level level;
    private File file;

    @Override
    public Logger getLogger(String key) {
        return new JclLogger(LogFactory.getLog(key));
    }

    @Override
    public Logger getLogger(Class<?> key) {
        return new JclLogger(LogFactory.getLog(key));
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

}
