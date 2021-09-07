package com.marc.campsite.service;

import com.marc.campsite.UnitTestBase;
import com.marc.campsite.repository.BookingEntity;
import com.marc.campsite.repository.BookingRepository;
import com.marc.campsite.service.exception.BookingNotFoundException;
import com.marc.campsite.utils.BookingDtoFixture;
import com.marc.campsite.utils.BookingEntityFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookingServiceTest extends UnitTestBase {

	@Mock
	private BookingRepository bookingRepository;
	@Mock
	private BookingValidationService bookingValidationService;
	private BookingService bookingService;

	@BeforeEach
	void init() {
		bookingService = new BookingService(bookingRepository, bookingValidationService);
	}

	@Test
	public void test_get_bookings() {
		String id = "abcd";
		LocalDate arrivalDate = LocalDate.now().plusWeeks(1);
		LocalDate departureDate = LocalDate.now().plusWeeks(1).plusDays(3);
		BookingEntity bookingEntity = BookingEntityFixture.bookingEntity(id, arrivalDate, departureDate);

		when(bookingRepository.findAll()).thenReturn(Collections.singletonList(bookingEntity));

		List<BookingDto> bookings = bookingService.getBookings();
		assertEquals(1, bookings.size());
		assertEquals(id, bookings.get(0).getId());
		assertEquals(arrivalDate, bookings.get(0).getArrivalDate());
		assertEquals(departureDate, bookings.get(0).getDepartureDate());
	}

	@Test
	public void test_get_booking_by_id_unknown_then_throw_not_found() {
		String id = "abcd";
		when(bookingRepository.findById(eq(id))).thenReturn(Optional.empty());
		BookingNotFoundException exception = assertThrows(BookingNotFoundException.class, () -> {
			bookingService.getBookingById(id);
		});
		assertEquals(id, exception.getBookingId());
	}

	@Test
	public void test_get_booking_by_id_then_return_dto() {
		String id = "abcd";
		LocalDate arrivalDate = LocalDate.now().plusWeeks(1);
		LocalDate departureDate = LocalDate.now().plusWeeks(1).plusDays(3);
		BookingEntity bookingEntity = BookingEntityFixture.bookingEntity(id, arrivalDate, departureDate);

		when(bookingRepository.findById(eq(id))).thenReturn(Optional.of(bookingEntity));
		BookingDto bookingDto = bookingService.getBookingById(id);
		assertEquals(id, bookingDto.getId());
		assertEquals(arrivalDate, bookingDto.getArrivalDate());
		assertEquals(departureDate, bookingDto.getDepartureDate());
	}

	@Test
	public void test_create_booking() {
		BookingDto newBooking = BookingDtoFixture.bookingDto(LocalDate.now().plusWeeks(1),
				LocalDate.now().plusWeeks(1).plusDays(1));
		when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
		BookingDto persistedBooking = bookingService.createBooking(newBooking);
		verify(bookingValidationService).validateBooking(eq(newBooking));
		verify(bookingRepository).save(any());
		assertNotNull(persistedBooking.getId());
	}

	@Test
	public void test_edit_unknown_booking_then_throw_not_found() {
		BookingDto editBooking = BookingDtoFixture.bookingDto(LocalDate.now().plusWeeks(1),
				LocalDate.now().plusWeeks(1).plusDays(1));
		String id = "abcd";
		BookingNotFoundException exception = assertThrows(BookingNotFoundException.class, () -> {
			bookingService.editBooking(id, editBooking);
		});
		assertEquals(id, exception.getBookingId());
	}

	@Test
	public void test_edit_booking() {
		BookingDto editBooking = BookingDtoFixture.bookingDto(LocalDate.now().plusWeeks(1),
				LocalDate.now().plusWeeks(1).plusDays(1));
		String id = "abcd";
		BookingEntity bookingEntity = BookingEntityFixture.bookingEntity(id, editBooking.getArrivalDate(),
				editBooking.getDepartureDate());

		when(bookingRepository.findById(eq(id))).thenReturn(Optional.of(bookingEntity));
		when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
		BookingDto editedBooking = bookingService.editBooking(id, editBooking);
		verify(bookingValidationService).validateBooking(eq(editBooking));
		verify(bookingRepository).save(any());
		assertNotNull(editedBooking.getId());
	}

	@Test
	public void test_cancel_unknown_booking_then_throw_not_found() {
		String id = "abcd";
		BookingNotFoundException exception = assertThrows(BookingNotFoundException.class, () -> {
			bookingService.cancelReservation(id);
		});
		assertEquals(id, exception.getBookingId());
	}

	@Test
	public void test_cancel_booking_then_entity_is_deleted() {
		String id = "abcd";
		BookingDto existingBooking = BookingDtoFixture.bookingDto(LocalDate.now().plusWeeks(1),
				LocalDate.now().plusWeeks(1).plusDays(1));
		BookingEntity bookingEntity = BookingEntityFixture.bookingEntity(id, existingBooking.getArrivalDate(),
				existingBooking.getDepartureDate());
		when(bookingRepository.findById(eq(id))).thenReturn(Optional.of(bookingEntity));
		bookingService.cancelReservation(id);
		verify(bookingRepository).delete(eq(bookingEntity));
	}
}
