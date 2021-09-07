package com.marc.campsite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, String> {

	// spotless:off
    // TODO Quite bad readability due to colon escaping, should probably move to resource file
    @Query(
            value = "SELECT d\\:\\:date\n" +
                    "FROM generate_series(\n" +
                    "             :startDate\\:\\:timestamp without time zone,\n" +
                    "            :endDate\\:\\:timestamp without time zone,\n" +
                    "             '1 day'\n" +
                    "         ) AS gs(d)\n" +
                    "WHERE NOT EXISTS(\n" +
                    "        SELECT\n" +
                    "        FROM bookings\n" +
                    "        WHERE bookings.date_range @> d\\:\\:date\n" +
                    "    );",
            nativeQuery = true)
    List<Date> findAvailableDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    //spotless:on
}
