package com.inops.visitorpass.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
//@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "registeredEvents")
public class RegisteredEvents {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String userId;
	private String userName;
	

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "scheduleEventId", nullable = false, updatable = true)
	private ScheduleEvent scheduleEvent;

	public void setId(Long id) {
		this.id = id;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setScheduleEvent(ScheduleEvent scheduleEvent) {
		this.scheduleEvent = scheduleEvent;
	}

	
	
}
