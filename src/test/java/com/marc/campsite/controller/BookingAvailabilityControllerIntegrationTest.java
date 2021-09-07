package com.marc.campsite.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marc.campsite.IntegrationTestBase;
import com.marc.campsite.service.BookingAvailabilityDto;
import com.marc.campsite.service.BookingDto;
import com.marc.campsite.utils.BookingDtoFixture;
import com.marc.campsite.utils.ObjectMapperUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext
public class BookingAvailabilityControllerIntegrationTest extends IntegrationTestBase {

	private static final ObjectMapper OBJECT_MAPPER = ObjectMapperUtil.createObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void test_create_basic_booking_then_verify_availability() throws Exception {
		BookingDto bookingDto = BookingDtoFixture.bookingDto(LocalDate.now().plusWeeks(1),
				LocalDate.now().plusWeeks(1).plusDays(1));
		String requestBody = OBJECT_MAPPER.writeValueAsString(bookingDto);
		mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody))
				.andExpect(status().isCreated());

		MvcResult result = mockMvc.perform(get("/booking-availabilities")).andExpect(status().isOk()).andReturn();
		BookingAvailabilityDto bookingAvailabilityDto = OBJECT_MAPPER
				.readValue(result.getResponse().getContentAsString(), BookingAvailabilityDto.class);

		// It shouldn't contain the booked arrival day
		boolean containsArrivalDate = bookingAvailabilityDto.getAvailableDates().contains(bookingDto.getArrivalDate());
		assertFalse(containsArrivalDate);

		// It should contain the departure day as the range is open
		boolean containsDepartureDate = bookingAvailabilityDto.getAvailableDates()
				.contains(bookingDto.getDepartureDate());
		assertTrue(containsDepartureDate);

		// It should contain availabilities
		assertFalse(bookingAvailabilityDto.getAvailableDates().isEmpty());
	}

	@Test
	public void test_invalid_booking_availability_request() throws Exception {
		String startDate = LocalDate.now().plusWeeks(1).toString();
		String endDate = LocalDate.now().toString();
		String url = String.format("/booking-availabilities?startDate=%s&=endDate=%s", startDate, endDate);
		mockMvc.perform(get(url)).andExpect(status().isBadRequest());
	}
}
