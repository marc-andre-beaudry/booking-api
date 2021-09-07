package com.marc.campsite.service.exception;

import lombok.Getter;

@Getter
public class BookingNotFoundException extends RuntimeException {

	private final String bookingId;

	public BookingNotFoundException(String bookingId) {
		super(String.format("Booking with id [%s] is not found", bookingId));
		this.bookingId = bookingId;
	}
}
