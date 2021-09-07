package com.marc.campsite.exception;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AppErrorDto {

	private LocalDateTime timestamp;
	private int errorCode;
	private String message;
	private List<String> extraDetails;

	public AppErrorDto() {
		this.timestamp = LocalDateTime.now();
	}

	public AppErrorDto(AppErrorCode appErrorCode) {
		this.timestamp = LocalDateTime.now();
		this.errorCode = appErrorCode.getCode();
		this.message = appErrorCode.getMessage();
	}
}
