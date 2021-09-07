package com.marc.campsite.service;

import com.marc.campsite.repository.BookingEntity;
import com.marc.campsite.repository.BookingRepository;
import com.marc.campsite.service.exception.BookingNotFoundException;
import com.vladmihalcea.hibernate.type.range.Range;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookingService {

	private final BookingRepository bookingRepository;
	private final BookingValidationService bookingValidationService;

	@Transactional(readOnly = true)
	public BookingDto getBookingById(@NonNull String id) {
		return bookingRepository.findById(id).map(BookingDto::fromBooking)
				.orElseThrow(() -> new BookingNotFoundException(id));
	}

	@Transactional(readOnly = true)
	public List<BookingDto> getBookings() {
		return bookingRepository.findAll().stream().map(BookingDto::fromBooking).collect(Collectors.toList());
	}

	@Transactional
	@CacheEvict(cacheNames = "bookingAvailabilities", allEntries = true)
	public BookingDto createBooking(@NonNull BookingDto bookingDto) {
		bookingValidationService.validateBooking(bookingDto);
		BookingEntity entity = createReservationEntity(bookingDto);
		BookingEntity persistedEntity = bookingRepository.save(entity);
		return BookingDto.fromBooking(persistedEntity);
	}

	@Transactional
	@CacheEvict(cacheNames = "bookingAvailabilities", allEntries = true)
	public BookingDto editBooking(@NonNull String id, @NonNull BookingDto editedReservation) {
		bookingValidationService.validateBooking(editedReservation);
		BookingEntity bookingEntity = bookingRepository.findById(id)
				.orElseThrow(() -> new BookingNotFoundException(id));
		BookingEntity entity = editReservationEntity(bookingEntity, editedReservation);
		BookingEntity persistedEntity = bookingRepository.save(entity);
		return BookingDto.fromBooking(persistedEntity);
	}

	@Transactional
	@CacheEvict(cacheNames = "bookingAvailabilities", allEntries = true)
	public void cancelReservation(@NonNull String id) {
		BookingEntity bookingEntity = bookingRepository.findById(id)
				.orElseThrow(() -> new BookingNotFoundException(id));
		bookingRepository.delete(bookingEntity);
	}

	private BookingEntity createReservationEntity(BookingDto bookingDto) {
		BookingEntity newEntity = new BookingEntity();
		newEntity.setId(UUID.randomUUID().toString());
		newEntity.setFirstName(bookingDto.getFirstName());
		newEntity.setLastName(bookingDto.getLastName());
		newEntity.setEmail(bookingDto.getEmail());
		Range<LocalDate> range = Range.closedOpen(bookingDto.getArrivalDate(), bookingDto.getDepartureDate());
		newEntity.setDateRange(range);
		return newEntity;
	}

	private BookingEntity editReservationEntity(BookingEntity entity, BookingDto bookingDto) {
		Range<LocalDate> range = Range.closedOpen(bookingDto.getArrivalDate(), bookingDto.getDepartureDate());
		entity.setFirstName(bookingDto.getFirstName());
		entity.setLastName(bookingDto.getLastName());
		entity.setEmail(bookingDto.getEmail());
		entity.setDateRange(range);
		return entity;
	}
}
