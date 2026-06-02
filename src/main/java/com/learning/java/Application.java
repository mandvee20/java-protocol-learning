package com.learning.java;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Mandvee Vatsa
 * @date 22-May-2026 2:32:36 pm
 */

public class Application {

    public static void main(String[] args) {

        if (ArrayUtils.isNotEmpty(args) && StringUtils.isNotBlank(args[0])) {
            UserInputProcessor.getInstance().processRequest(args[0]);
        } else {
            Initiallizer.getInstance().printAvailableTopics();
            Initiallizer.getInstance().startAcceptingUserInputs();
        }

    }

}
