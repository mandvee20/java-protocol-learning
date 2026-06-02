package com.learning.java;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Mandvee Vatsa
 * @date 22-May-2026 2:32:36 pm
 */

public class Initiallizer {
    private static Initiallizer initiallizerObj = null;
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(0);

    private Initiallizer() {
        executor.setMaximumPoolSize(1);
        executor.setKeepAliveTime(20, TimeUnit.MILLISECONDS);
    }

    // Lazy Initialization with Double-Checked Locking
    public static Initiallizer getInstance() {
        if (Objects.isNull(initiallizerObj)) {
            synchronized (Initiallizer.class) {
                if (Objects.isNull(initiallizerObj)) {
                    initiallizerObj = new Initiallizer();
                }
            }
        }
        return initiallizerObj;
    }

    public void printAvailableTopics() {
        Arrays.asList(ConnectionType.values()).forEach(topic -> {
            System.out.println("Enter \"" + topic.getConnectionType() + "\" To Learn About " + topic);
        });
    }

    public void startAcceptingUserInputs() {
        executor.execute(new UserInputReader());
    }

    public void fetchConfigObject() {
        try {


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
