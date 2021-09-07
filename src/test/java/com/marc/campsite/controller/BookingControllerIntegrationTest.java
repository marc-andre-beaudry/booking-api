package com.marc.campsite.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marc.campsite.IntegrationTestBase;
import com.marc.campsite.service.BookingDto;
import com.marc.campsite.utils.BookingDtoFixture;
import com.marc.campsite.utils.ObjectMapperUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext
public class BookingControllerIntegrationTest extends IntegrationTestBase {

	private static final ObjectMapper OBJECT_MAPPER = ObjectMapperUtil.createObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void test_create_basic_booking_then_retrieve() throws Exception {
		BookingDto bookingDto = BookingDtoFixture.bookingDto(LocalDate.now().plusWeeks(1),
				LocalDate.now().plusWeeks(1).plusDays(1));
		String requestBody = OBJECT_MAPPER.writeValueAsString(bookingDto);
		MvcResult result = mockMvc
				.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody))
				.andExpect(status().isCreated()).andReturn();

		BookingDto createdBooking = OBJECT_MAPPER.readValue(result.getResponse().getContentAsString(),
				BookingDto.class);

		mockMvc.perform(get("/bookings/" + createdBooking.getId())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(createdBooking.getId()));

		mockMvc.perform(get("/bookings")).andExpect(status().isOk()).andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	public void test_create_then_edit_basic_booking() throws Exception {
		BookingDto bookingDto = BookingDtoFixture.bookingDto(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
		String requestBody = OBJECT_MAPPER.writeValueAsString(bookingDto);
		MvcResult result = mockMvc
				.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody))
				.andExpect(status().isCreated()).andReturn();
		BookingDto createdBooking = OBJECT_MAPPER.readValue(result.getResponse().getContentAsString(),
				BookingDto.class);

		createdBooking.setDepartureDate(createdBooking.getDepartureDate().plusDays(1));
		String editRequestBody = OBJECT_MAPPER.writeValueAsString(createdBooking);
		mockMvc.perform(put("/bookings/" + createdBooking.getId()).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(editRequestBody)).andExpect(status().isOk());
	}

	@Test
	public void test_basic_book_validations() throws Exception {
		// Given a booking dto with missing firstName
		BookingDto missingFirstName = BookingDtoFixture.bookingDto(LocalDate.now().plusWeeks(1),
				LocalDate.now().plusWeeks(1).plusDays(1));
		missingFirstName.setFirstName("");

		mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(OBJECT_MAPPER.writeValueAsString(missingFirstName))).andExpect(status().isBadRequest());

		// Given a booking dto with missing lastName
		BookingDto missingLastName = BookingDtoFixture.bookingDto(LocalDate.now().plusWeeks(1),
				LocalDate.now().plusWeeks(1).plusDays(1));
		missingLastName.setLastName("");

		mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(OBJECT_MAPPER.writeValueAsString(missingLastName))).andExpect(status().isBadRequest());

		// Given a booking dto with missing email
		BookingDto missingEmail = BookingDtoFixture.bookingDto(LocalDate.now().plusWeeks(1),
				LocalDate.now().plusWeeks(1).plusDays(1));
		missingEmail.setEmail("");

		mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(OBJECT_MAPPER.writeValueAsString(missingEmail))).andExpect(status().isBadRequest());

		// Given a booking dto with invalid email
		BookingDto invalidEmail = BookingDtoFixture.bookingDto(LocalDate.now().plusWeeks(1),
				LocalDate.now().plusWeeks(1).plusDays(1));
		invalidEmail.setEmail("email-address-no-domain");

		mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(OBJECT_MAPPER.writeValueAsString(invalidEmail))).andExpect(status().isBadRequest());

		// Given a booking dto with missing arrival date
		BookingDto missingArrivalDate = BookingDtoFixture.bookingDto(LocalDate.now().plusWeeks(1),
				LocalDate.now().plusWeeks(1).plusDays(1));
		missingArrivalDate.setArrivalDate(null);

		mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(OBJECT_MAPPER.writeValueAsString(missingArrivalDate))).andExpect(status().isBadRequest());

		// Given a booking dto with missing departure date
		BookingDto missingDepartureDate = BookingDtoFixture.bookingDto(LocalDate.now().plusWeeks(1),
				LocalDate.now().plusWeeks(1).plusDays(1));
		missingDepartureDate.setArrivalDate(null);

		mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(OBJECT_MAPPER.writeValueAsString(missingDepartureDate))).andExpect(status().isBadRequest());
	}

	@Test
	public void cancel_unknown_booking_then_return_not_found() throws Exception {
		mockMvc.perform(delete("/bookings/abcd")).andExpect(status().isNotFound());
	}

	@Test
	public void try_create_overlapping_booking_then_return_bad_request() throws Exception {
		LocalDate arrivalDate = LocalDate.now().plusWeeks(2);
		LocalDate departureDate = LocalDate.now().plusWeeks(2).plusDays(1);

		BookingDto bookingDto = new BookingDto();
		bookingDto.setArrivalDate(arrivalDate);
		bookingDto.setDepartureDate(departureDate);
		bookingDto.setEmail("marc-andre-beaudry@fakedomain.com");
		bookingDto.setFirstName("Marc-Andre");
		bookingDto.setLastName("Beaudry");

		String requestBody = OBJECT_MAPPER.writeValueAsString(bookingDto);

		mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void try_concurrent_overlapping_booking_then_only_one_succeed() throws Exception {
		LocalDate arrivalDate = LocalDate.now().plusWeeks(2).plusDays(1);
		LocalDate departureDate = LocalDate.now().plusWeeks(2).plusDays(3);

		BookingDto bookingDto = new BookingDto();
		bookingDto.setArrivalDate(arrivalDate);
		bookingDto.setDepartureDate(departureDate);
		bookingDto.setEmail("marc-andre-beaudry@fakedomain.com");
		bookingDto.setFirstName("Marc-Andre");
		bookingDto.setLastName("Beaudry");

		String requestBody = OBJECT_MAPPER.writeValueAsString(bookingDto);

		List<CompletableFuture<MvcResult>> completableFutures = IntStream.range(0, 10)
				.mapToObj(i -> createBookingFuture(requestBody)).collect(Collectors.toList());

		CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]));

		List<MvcResult> results = completableFutures.stream().map(future -> {
			try {
				return future.get();
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toList());

		List<Integer> statuses = results.stream().map(result -> result.getResponse().getStatus())
				.collect(Collectors.toList());

		List<Integer> createdStatus = statuses.stream().filter(status -> HttpStatus.CREATED.value() == status)
				.collect(Collectors.toList());
		List<Integer> badRequest = statuses.stream().filter(status -> HttpStatus.BAD_REQUEST.value() == status)
				.collect(Collectors.toList());

		assertEquals(1, createdStatus.size());
		assertEquals(9, badRequest.size());
	}

	private CompletableFuture<MvcResult> createBookingFuture(String requestBody) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return mockMvc
						.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody))
						.andReturn();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
}
