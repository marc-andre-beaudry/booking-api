package com.marc.campsite.exception;

import com.marc.campsite.service.exception.BookingNotFoundException;
import com.marc.campsite.service.exception.InvalidBookingAvailabilityRequestException;
import com.marc.campsite.service.exception.InvalidBookingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		AppErrorDto appErrorDto = new AppErrorDto(AppErrorCode.BOOKING_INVALID);
		List<String> fieldsErrors = ex.getBindingResult().getFieldErrors().stream()
				.map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
				.collect(Collectors.toList());
		appErrorDto.setExtraDetails(fieldsErrors);
		return handleExceptionInternal(ex, appErrorDto, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(value = {BookingNotFoundException.class})
	public ResponseEntity<Object> handleBookingNotFoundException(BookingNotFoundException ex, WebRequest request) {
		AppErrorDto appErrorDto = new AppErrorDto(AppErrorCode.BOOKING_NOT_FOUND);
		return handleExceptionInternal(ex, appErrorDto, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(value = {InvalidBookingException.class})
	public ResponseEntity<Object> handleInvalidBookingException(InvalidBookingException ex, WebRequest request) {
		AppErrorDto appErrorDto = new AppErrorDto(AppErrorCode.BOOKING_INVALID);
		appErrorDto.setExtraDetails(Collections.singletonList(ex.getReason()));
		return handleExceptionInternal(ex, appErrorDto, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(value = {InvalidBookingAvailabilityRequestException.class})
	public ResponseEntity<Object> handleInvalidBookingAvailabilityRequest(InvalidBookingAvailabilityRequestException ex,
			WebRequest request) {
		AppErrorDto appErrorDto = new AppErrorDto(AppErrorCode.BOOKING_AVAILABILITY_INVALID);
		appErrorDto.setExtraDetails(Collections.singletonList(ex.getMessage()));
		return handleExceptionInternal(ex, appErrorDto, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(value = {DataIntegrityViolationException.class})
	public ResponseEntity<Object> handleInvalidBookingException(DataIntegrityViolationException ex,
			WebRequest request) {
		AppErrorDto appErrorDto = new AppErrorDto(AppErrorCode.BOOKING_DATE_CONFLICT);
		return handleExceptionInternal(ex, appErrorDto, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(value = {OptimisticLockingFailureException.class})
	public ResponseEntity<Object> handleOptimisticLockingFailureException(OptimisticLockingFailureException ex,
			WebRequest request) {
		AppErrorDto appErrorDto = new AppErrorDto(AppErrorCode.BOOKING_CONCURRENT_MODIFICATION);
		return handleExceptionInternal(ex, appErrorDto, new HttpHeaders(), HttpStatus.CONFLICT, request);
	}
}
