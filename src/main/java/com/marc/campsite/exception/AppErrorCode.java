package com.marc.campsite.exception;

import lombok.Getter;

@Getter
public enum AppErrorCode {

	// spotless:off
    GENERIC_SERVER_ERROR(1000, "An internal server error occurred, please reach out to support"),
    BOOKING_NOT_FOUND(1001, "The specified booking was not found"),
    BOOKING_DATE_CONFLICT(1002, "The booking requested dates are already taken"),
    BOOKING_CONCURRENT_MODIFICATION(1003, "The booking was modified concurrently"),
    BOOKING_INVALID(1004, "Invalid booking"),
    BOOKING_AVAILABILITY_INVALID(1004, "Invalid booking availability request");
    // spotless:on

	private final int code;
	private final String message;

	AppErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
