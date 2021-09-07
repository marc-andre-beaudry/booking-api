package com.marc.campsite.service.exception;

import lombok.Getter;

@Getter
public class InvalidBookingAvailabilityRequestException extends RuntimeException {

	private final String reason;

	public InvalidBookingAvailabilityRequestException(String reason) {
		super(String.format("Invalid booking availability request due to [%s]", reason));
		this.reason = reason;
	}
}
