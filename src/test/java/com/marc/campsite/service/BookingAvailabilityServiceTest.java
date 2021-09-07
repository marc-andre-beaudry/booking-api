package com.marc.campsite.service;

import com.marc.campsite.UnitTestBase;
import com.marc.campsite.repository.BookingRepository;
import com.marc.campsite.service.exception.InvalidBookingAvailabilityRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookingAvailabilityServiceTest extends UnitTestBase {

	@Mock
	private BookingRepository bookingRepository;
	private BookingAvailabilityService bookingAvailabilityService;

	@BeforeEach
	void init() {
		bookingAvailabilityService = new BookingAvailabilityService(bookingRepository);
	}

	@Test
	public void test_get_booking_avail_no_dates_then_throw_exception() {
		assertThrows(InvalidBookingAvailabilityRequestException.class, () -> {
			bookingAvailabilityService.getBookingAvailabilities(null, null);
		});
	}

	@Test
	public void test_get_booking_avail_end_date_after_start_date_then_throw_exception() {
		assertThrows(InvalidBookingAvailabilityRequestException.class, () -> {
			bookingAvailabilityService.getBookingAvailabilities(LocalDate.now().plusWeeks(1), LocalDate.now());
		});
	}
}
