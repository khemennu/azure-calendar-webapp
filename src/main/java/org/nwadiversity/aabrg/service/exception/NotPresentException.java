package org.nwadiversity.aabrg.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Azure Resource Not Found")
public class NotPresentException extends RuntimeException {

	private static final long serialVersionUID = 8996935433062413138L;

	public NotPresentException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotPresentException(String message) {
		super(message);
	}
}
