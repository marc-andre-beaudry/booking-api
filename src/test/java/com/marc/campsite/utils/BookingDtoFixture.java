package com.marc.campsite.utils;

import com.marc.campsite.service.BookingDto;

import java.time.LocalDate;

public class BookingDtoFixture {

	public static BookingDto bookingDto(LocalDate arrivalDate, LocalDate departureDate) {
		BookingDto bookingDto = new BookingDto();
		bookingDto.setArrivalDate(arrivalDate);
		bookingDto.setDepartureDate(departureDate);
		bookingDto.setEmail("marc-andre-beaudry@fakedomain.com");
		bookingDto.setFirstName("Marc-Andre");
		bookingDto.setLastName("Beaudry");
		return bookingDto;
	}
}
