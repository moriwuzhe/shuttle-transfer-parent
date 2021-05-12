package org.moriwuzhe.shuttle.common.logger;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum LoggerType {
    SLF4J("slf4j"),
    JCL("jcl"),
    LOG4J("log4j"),
    JDK("jdk"),
    LOG4J2("log4j2"),
    ;
    private String tpye;
    private final static Map<String, LoggerType> map =
            Arrays.stream(values())
                    .collect(Collectors.toMap(LoggerType::getTpye, loggerType -> loggerType, (loggerType, loggerType2) -> loggerType));

    public static boolean contain(Object key) {
        return map.containsKey(key);
    }
}
