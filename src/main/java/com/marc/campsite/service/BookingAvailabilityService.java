package com.marc.campsite.service;

import com.marc.campsite.repository.BookingRepository;
import com.marc.campsite.service.exception.InvalidBookingAvailabilityRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingAvailabilityService {

	private final BookingRepository bookingRepository;

	@Transactional(readOnly = true)
	@Cacheable("bookingAvailabilities")
	public BookingAvailabilityDto getBookingAvailabilities(LocalDate startDate, LocalDate endDate) {
		validateBookingAvailabilityRequest(startDate, endDate);

		List<LocalDate> availableDates = bookingRepository.findAvailableDates(startDate, endDate).stream()
				.map(Date::toLocalDate).collect(Collectors.toList());

		BookingAvailabilityDto bookingAvailabilityDto = new BookingAvailabilityDto();
		bookingAvailabilityDto.setAvailableDates(availableDates);
		bookingAvailabilityDto.setStartDate(startDate);
		bookingAvailabilityDto.setEndDate(endDate);
		return bookingAvailabilityDto;
	}

	public void validateBookingAvailabilityRequest(LocalDate startDate, LocalDate endDate) {
		if (startDate == null || endDate == null) {
			throw new InvalidBookingAvailabilityRequestException(
					"You must supply both startDate and endDate, or let default range to be used.");
		}
		if (startDate.isAfter(endDate)) {
			throw new InvalidBookingAvailabilityRequestException("start date cannot be after end date");
		}
	}
}
