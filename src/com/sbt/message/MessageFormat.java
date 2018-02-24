package com.sbt.message;

public interface MessageFormat {
    public static final String MSG_DELIMITER = "|";
    String getMessageType();
    String buildMessage();
    boolean isSameAs(String mesg);
}
