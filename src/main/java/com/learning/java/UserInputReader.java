package com.learning.java;

import java.util.Objects;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Mandvee Vatsa
 * @date 22-May-2026 2:32:36 pm
 */

public class UserInputReader implements Runnable {

	private Scanner scanner;
	private UserInputProcessor processor;

	UserInputReader() {
		scanner = new Scanner(System.in);
		processor = UserInputProcessor.getInstance();
	}

	@Override
	public void run() {
		System.out.println("Try Writing Input on Console");

		try {
			while (true) {
				String input = scanner.nextLine();
				if (StringUtils.isNotBlank(input)) {
					processor.processRequest(input);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (Objects.nonNull(scanner))
				scanner.close();
		}

	}

}
