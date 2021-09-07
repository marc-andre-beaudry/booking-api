package com.marc.campsite.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.marc.campsite.repository.BookingEntity;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class BookingDto {

	private String id;

	@NotNull
	@Size(min = 2, max = 255)
	private String firstName;

	@NotNull
	@Size(min = 2, max = 255)
	private String lastName;

	@NotNull
	@Size(min = 2, max = 255)
	@Email
	private String email;

	@NotNull
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate arrivalDate;

	@NotNull
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate departureDate;

	// TODO, we could use some mapper library
	public static BookingDto fromBooking(BookingEntity entity) {
		BookingDto dto = new BookingDto();
		dto.setId(entity.getId());
		dto.setFirstName(entity.getFirstName());
		dto.setLastName(entity.getLastName());
		dto.setEmail(entity.getEmail());
		dto.setArrivalDate(entity.getDateRange().lower());
		dto.setDepartureDate(entity.getDateRange().upper());
		return dto;
	}
}
