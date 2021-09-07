package com.marc.campsite.repository;

import com.vladmihalcea.hibernate.type.range.PostgreSQLRangeType;
import com.vladmihalcea.hibernate.type.range.Range;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@TypeDef(typeClass = PostgreSQLRangeType.class, defaultForType = Range.class)
@NoArgsConstructor
@Setter
@Getter
@Table(name = "bookings")
public class BookingEntity extends Auditable {

	@Id
	@Column
	private String id;
	@Column
	private String firstName;
	@Column
	private String lastName;
	@Column
	private String email;
	@Column(columnDefinition = "daterange")
	private Range<LocalDate> dateRange;

	@Version
	private long version = 0L;
}
