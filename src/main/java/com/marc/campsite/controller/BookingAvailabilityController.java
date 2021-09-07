package com.marc.campsite.controller;

import com.marc.campsite.service.BookingAvailabilityDto;
import com.marc.campsite.service.BookingAvailabilityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Api("Booking Availability Api")
@RestController
@RequestMapping("booking-availabilities")
@RequiredArgsConstructor
@Slf4j
public class BookingAvailabilityController {

	private final BookingAvailabilityService bookingAvailabilityService;

	@ApiOperation(value = "Get booking availability", notes = "The default range is one month. You can specify different time range to retrieve booking availability for the campsite.")
	@GetMapping("")
	public BookingAvailabilityDto getBookingAvailability(
			@ApiParam(value = "Starting date to query booking availability. The date is is ISO format, i.e 2000-10-31") @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@ApiParam(value = "End date to query booking availability. The date is is ISO format, i.e 2000-10-31") @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		// Default range is one month
		if (startDate == null && endDate == null) {
			startDate = LocalDate.now();
			endDate = LocalDate.now().plusMonths(1);
		}
		return bookingAvailabilityService.getBookingAvailabilities(startDate, endDate);
	}
}
