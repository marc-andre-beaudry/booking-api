package com.marc.campsite.service;

import com.marc.campsite.UnitTestBase;
import com.marc.campsite.service.exception.InvalidBookingException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookingValidationServiceTest extends UnitTestBase {

	private final BookingValidationService bookingValidationService = new BookingValidationService();

	@Test
	public void test_departure_date_same_arrival_date() {
		LocalDate now = LocalDate.of(2021, 9, 1);
		LocalDate arrival = LocalDate.of(2021, 9, 2);
		LocalDate departure = LocalDate.of(2021, 9, 2);

		// Given
		BookingDto bookingDto = new BookingDto();
		bookingDto.setArrivalDate(arrival);
		bookingDto.setDepartureDate(departure);

		InvalidBookingException invalidBookingException = assertThrows(InvalidBookingException.class, () -> {
			bookingValidationService.validateBooking(now, bookingDto);
		});
		assertEquals("Arrival date must be before departure date", invalidBookingException.getReason());
	}

	@Test
	public void test_departure_date_before_arrival_date() {
		LocalDate now = LocalDate.of(2021, 9, 1);
		LocalDate arrival = LocalDate.of(2021, 9, 3);
		LocalDate departure = LocalDate.of(2021, 9, 2);

		// Given
		BookingDto bookingDto = new BookingDto();
		bookingDto.setArrivalDate(arrival);
		bookingDto.setDepartureDate(departure);

		InvalidBookingException invalidBookingException = assertThrows(InvalidBookingException.class, () -> {
			bookingValidationService.validateBooking(now, bookingDto);
		});
		assertEquals("Arrival date must be before departure date", invalidBookingException.getReason());
	}

	@Test
	public void test_min_delay_booking() {
		LocalDate now = LocalDate.of(2021, 9, 1);
		LocalDate arrival = LocalDate.of(2021, 9, 1);
		LocalDate departure = LocalDate.of(2021, 9, 3);

		// Given
		BookingDto bookingDto = new BookingDto();
		bookingDto.setArrivalDate(arrival);
		bookingDto.setDepartureDate(departure);

		InvalidBookingException invalidBookingException = assertThrows(InvalidBookingException.class, () -> {
			bookingValidationService.validateBooking(now, bookingDto);
		});
		assertEquals("Earliest arrival date must be on 2021-09-02", invalidBookingException.getReason());
	}

	@Test
	public void test_max_delay_booking() {
		LocalDate now = LocalDate.of(2021, 9, 1);
		LocalDate arrival = LocalDate.of(2021, 10, 2);
		LocalDate departure = LocalDate.of(2021, 10, 3);

		// Given
		BookingDto bookingDto = new BookingDto();
		bookingDto.setArrivalDate(arrival);
		bookingDto.setDepartureDate(departure);

		InvalidBookingException invalidBookingException = assertThrows(InvalidBookingException.class, () -> {
			bookingValidationService.validateBooking(now, bookingDto);
		});
		assertEquals("Latest arrival date must be on 2021-10-01", invalidBookingException.getReason());
	}

	@Test
	public void test_max_booking_duration() {
		LocalDate now = LocalDate.of(2021, 9, 1);
		LocalDate arrival = LocalDate.of(2021, 9, 2);
		LocalDate departure = LocalDate.of(2021, 9, 6);

		// Given
		BookingDto bookingDto = new BookingDto();
		bookingDto.setArrivalDate(arrival);
		bookingDto.setDepartureDate(departure);

		InvalidBookingException invalidBookingException = assertThrows(InvalidBookingException.class, () -> {
			bookingValidationService.validateBooking(now, bookingDto);
		});
		assertEquals("Latest departure date must be on 2021-09-05", invalidBookingException.getReason());
	}
}
