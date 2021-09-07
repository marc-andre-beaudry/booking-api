package com.marc.campsite.service.exception;

import lombok.Getter;

@Getter
public class InvalidBookingException extends RuntimeException {

	private final String reason;

	public InvalidBookingException(String reason) {
		super(String.format("Invalid reservation due to [%s]", reason));
		this.reason = reason;
	}
}
