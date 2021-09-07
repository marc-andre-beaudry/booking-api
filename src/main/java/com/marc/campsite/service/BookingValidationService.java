package com.marc.campsite.service;

import com.marc.campsite.service.exception.InvalidBookingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class BookingValidationService {

	private final Period MAX_RESERVATION_DURATION = Period.ofDays(3);
	private final Period MIN_DELAY_BEFORE_RESERVATION = Period.ofDays(1);
	private final Period MAX_DELAY_BEFORE_RESERVATION = Period.ofMonths(1);

	public void validateBooking(BookingDto bookingDto) {
		validateBooking(LocalDate.now(), bookingDto);
	}

	void validateBooking(LocalDate currentDate, BookingDto bookingDto) {
		if (!bookingDto.getArrivalDate().isBefore(bookingDto.getDepartureDate())) {
			throw new InvalidBookingException("Arrival date must be before departure date");
		}
		LocalDate minDate = currentDate.plus(MIN_DELAY_BEFORE_RESERVATION);
		if (minDate.isAfter(bookingDto.getArrivalDate())) {
			throw new InvalidBookingException("Earliest arrival date must be on " + minDate);
		}
		LocalDate maxDate = currentDate.plus(MAX_DELAY_BEFORE_RESERVATION);
		if (maxDate.isBefore(bookingDto.getArrivalDate())) {
			throw new InvalidBookingException("Latest arrival date must be on " + maxDate);
		}
		LocalDate maxReservationDate = bookingDto.getArrivalDate().plus(MAX_RESERVATION_DURATION);
		if (!maxReservationDate.isEqual(bookingDto.getDepartureDate())
				&& maxReservationDate.isBefore(bookingDto.getDepartureDate())) {
			throw new InvalidBookingException("Latest departure date must be on " + maxReservationDate);
		}
	}
}
