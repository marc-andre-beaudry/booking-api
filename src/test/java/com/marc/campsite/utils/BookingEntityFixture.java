package com.marc.campsite.utils;

import com.marc.campsite.repository.BookingEntity;
import com.vladmihalcea.hibernate.type.range.Range;

import java.time.LocalDate;

public class BookingEntityFixture {

	public static BookingEntity bookingEntity(String id, LocalDate arrivalDate, LocalDate departureDate) {
		BookingEntity bookingEntity = new BookingEntity();
		bookingEntity.setId(id);
		bookingEntity.setDateRange(Range.closedOpen(arrivalDate, departureDate));
		bookingEntity.setEmail("marc-andre-beaudry@fakedomain.com");
		bookingEntity.setFirstName("Marc-Andre");
		bookingEntity.setLastName("Beaudry");
		return bookingEntity;
	}
}
