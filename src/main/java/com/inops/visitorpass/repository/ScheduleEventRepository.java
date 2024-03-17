package com.inops.visitorpass.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inops.visitorpass.entity.ScheduleEvent;

public interface ScheduleEventRepository extends JpaRepository<ScheduleEvent, Long> {

	List<ScheduleEvent> findAllByUserId(String userId);
	
	List<ScheduleEvent> findAllByEventStatus(String status);
	
	List<ScheduleEvent> findAllByEventStatusAndFromDateGreaterThan(String status,LocalDateTime date);
	
	List<ScheduleEvent> findAllByEventStatusAndFromDateLessThan(String status,LocalDateTime date);
}
