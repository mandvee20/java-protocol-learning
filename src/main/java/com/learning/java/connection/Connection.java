package com.learning.java.connection;

/**
 * @author Mandvee Vatsa
 * @date 22-May-2026 2:32:36 pm
 */

public interface Connection {

    public boolean makeConnection();

    public String readResponse();

    public void writeRequest();

}
