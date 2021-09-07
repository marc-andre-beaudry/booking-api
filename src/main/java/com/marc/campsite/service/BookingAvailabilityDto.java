package com.marc.campsite.service;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BookingAvailabilityDto {

	private LocalDate startDate;
	private LocalDate endDate;
	private List<LocalDate> availableDates;
}
