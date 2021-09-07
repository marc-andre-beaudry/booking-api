package com.marc.campsite.service;

import com.marc.campsite.IntegrationTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles("test")
public class BookingServiceIntegrationTest extends IntegrationTestBase {

	@Autowired
	private BookingService bookingService;

	@Test
	public void test_create_booking_then_modifying_same_booking() {
		BookingDto newBooking = new BookingDto();
		newBooking.setArrivalDate(LocalDate.now().plusDays(1));
		newBooking.setDepartureDate(LocalDate.now().plusDays(4));
		newBooking.setEmail("marc-andre-beaudry@fakedomain.com");
		newBooking.setFirstName("Marc-Andre");
		newBooking.setLastName("Beaudry");

		newBooking = bookingService.createBooking(newBooking);

		// Modify booking
		newBooking.setArrivalDate(newBooking.getArrivalDate().plusDays(1));
		newBooking.setDepartureDate(newBooking.getDepartureDate().minusDays(1));
		newBooking.setEmail("alternate-email@fakedomain.com");
		BookingDto editBooking = bookingService.editBooking(newBooking.getId(), newBooking);
		assertEquals(newBooking.getId(), editBooking.getId());
	}

	@Test
	public void test_create_overlapping_booking_then_throws_exception() {
		LocalDate arrivalDate1 = LocalDate.now().plusWeeks(1);
		LocalDate departureDate1 = LocalDate.now().plusWeeks(1).plusDays(3);

		BookingDto bookingDto1 = new BookingDto();
		bookingDto1.setArrivalDate(arrivalDate1);
		bookingDto1.setDepartureDate(departureDate1);
		bookingDto1.setEmail("marc-andre-beaudry@fakedomain.com");
		bookingDto1.setFirstName("Marc-Andre");
		bookingDto1.setLastName("Beaudry");

		LocalDate arrivalDate2 = LocalDate.now().plusWeeks(1).plusDays(1);
		LocalDate departureDate2 = LocalDate.now().plusWeeks(1).plusDays(2);

		BookingDto bookingDto2 = new BookingDto();
		bookingDto2.setArrivalDate(arrivalDate2);
		bookingDto2.setDepartureDate(departureDate2);
		bookingDto2.setEmail("marc-andre-beaudry@fakedomain.com");
		bookingDto2.setFirstName("Marc-Andre");
		bookingDto2.setLastName("Beaudry");

		assertThrows(DataIntegrityViolationException.class, () -> {
			bookingService.createBooking(bookingDto1);
			bookingService.createBooking(bookingDto2);
		});
	}

	@Test
	public void test_concurrent_edits_should_fail() {

		LocalDate arrivalDate1 = LocalDate.now().plusWeeks(2);
		LocalDate departureDate1 = LocalDate.now().plusWeeks(2).plusDays(3);

		BookingDto bookingDto1 = new BookingDto();
		bookingDto1.setArrivalDate(arrivalDate1);
		bookingDto1.setDepartureDate(departureDate1);
		bookingDto1.setEmail("marc-andre-beaudry@fakedomain.com");
		bookingDto1.setFirstName("Marc-Andre");
		bookingDto1.setLastName("Beaudry");
		BookingDto createdBooking = bookingService.createBooking(bookingDto1);

		// Change booking date by 1 day
		createdBooking.setDepartureDate(createdBooking.getDepartureDate().minusDays(1));

		CompletableFuture<BookingDto> future1 = CompletableFuture
				.supplyAsync(() -> bookingService.editBooking(createdBooking.getId(), createdBooking));
		CompletableFuture<BookingDto> future2 = CompletableFuture
				.supplyAsync(() -> bookingService.editBooking(createdBooking.getId(), createdBooking));
		CompletableFuture<BookingDto> future3 = CompletableFuture
				.supplyAsync(() -> bookingService.editBooking(createdBooking.getId(), createdBooking));
		CompletableFuture<BookingDto> future4 = CompletableFuture
				.supplyAsync(() -> bookingService.editBooking(createdBooking.getId(), createdBooking));
		CompletableFuture.allOf(future1, future2, future3, future4);

		boolean hadOptimisticLookingException = false;
		try {
			future1.get();
			future2.get();
			future3.get();
			future4.get();
		} catch (InterruptedException | ExecutionException e) {
			if (e.getCause() instanceof OptimisticLockingFailureException) {
				hadOptimisticLookingException = true;
			}
		}
		assertTrue(hadOptimisticLookingException);
	}
}
