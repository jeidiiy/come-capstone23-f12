package io.f12.notionlinkedblog.common.exceptions.runtimeexception;

public class IllegalDatabaseStateException extends RuntimeException {
	public IllegalDatabaseStateException() {
		super();
	}

	public IllegalDatabaseStateException(String message) {
		super(message);
	}
}
