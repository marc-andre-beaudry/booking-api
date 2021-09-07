package com.marc.campsite.controller;

import com.marc.campsite.service.BookingDto;
import com.marc.campsite.service.BookingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Api("Booking Api")
@RestController
@RequestMapping("bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

	private final BookingService bookingService;

	// Note, this endpoint should normally not be exposed to public, but only
	// available for admins, for troubleshooting for example.
	@ApiOperation(value = "Get all bookings")
	@GetMapping("")
	public ResponseEntity<List<BookingDto>> getBookings() {
		return ResponseEntity.ok(bookingService.getBookings());
	}

	@ApiOperation(value = "Get booking by id", notes = "Retrieves the booking information given its uuid.")
	@GetMapping("/{id}")
	public ResponseEntity<BookingDto> getBookingById(
			@ApiParam(value = "Unique identifier for a booking, following uuid format.") @PathVariable String id) {
		return ResponseEntity.ok(bookingService.getBookingById(id));
	}

	@ApiOperation(value = "Create a new booking", notes = """
			Books the campsite for the given time range provided.
			The constraints are the following:
			    1) The campsite can be reserved for max 3 days.
			    2) The campsite can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance.
			    3) Required firstName, lastName and email.
			""")
	@PostMapping("")
	public ResponseEntity<BookingDto> createBooking(@Valid @RequestBody BookingDto bookingDto) {
		BookingDto newBooking = bookingService.createBooking(bookingDto);
		return ResponseEntity.created(getLocationUri(newBooking)).body(newBooking);
	}

	@ApiOperation(value = "Edit an existing booking", notes = "Edit an existing booking given its uuid provided at creation. Same constraints apply as for the booking creation.")
	@PutMapping("/{id}")
	public BookingDto editReservation(
			@ApiParam(value = "Unique identifier for a booking, following uuid format.") @PathVariable String id,
			@Valid @RequestBody BookingDto bookingDto) {
		return bookingService.editBooking(id, bookingDto);
	}

	@ApiOperation(value = "Cancel an existing booking", notes = "Cancel an existing booking given its uuid provided at creation. Bookings can be cancelled anytime.")
	@DeleteMapping("/{id}")
	public void cancelReservation(
			@ApiParam(value = "Unique identifier for a booking, following uuid format.") @PathVariable String id) {
		bookingService.cancelReservation(id);
	}

	private URI getLocationUri(BookingDto bookingDto) {
		try {
			return new URI(String.format("/bookings/%s", bookingDto.getId()));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
