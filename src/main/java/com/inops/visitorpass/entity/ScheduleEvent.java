package com.inops.visitorpass.entity;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Getter
//@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scheduleEvent")
public class ScheduleEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String eventTitle;
	private String people;
	private LocalDateTime fromDate;
	private LocalDateTime toDate;
	private String location;
	private String eventType;
	private String eventStatus;
	private boolean status;
	private String userId;
	@Lob
	private String body;

	private int eventHeadCount;
	private int addedHeadCount;
	private String rejectionComments;
	private String division;
	private int rating;
	private String openClosed;

	@Transient
	private String[] selectedPeople;

	@OneToMany(mappedBy = "scheduleEvent", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private Set<RegisteredEvents> registered;

	public void setId(Long id) {
		this.id = id;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public void setPeople(String people) {
		this.people = people;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public void setEventStatus(String eventStatus) {
		this.eventStatus = eventStatus;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setSelectedPeople(String[] selectedPeople) {
		this.selectedPeople = selectedPeople;
	}

	public void setFromDate(LocalDateTime fromDate) {
		this.fromDate = fromDate;
	}

	public void setToDate(LocalDateTime toDate) {
		this.toDate = toDate;
	}

	public void setEventHeadCount(int eventHeadCount) {
		this.eventHeadCount = eventHeadCount;
	}

	public void setRejectionComments(String rejectionComments) {
		this.rejectionComments = rejectionComments;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public void setRating(int rating) {
		Random random = new Random();
		// Generate a random integer between 1 and 5 (inclusive)
		int randomNumber = random.nextInt(5) + 1;
		this.rating = randomNumber;
	}

	public void setRegistered(Set<RegisteredEvents> registered) {
		this.registered = registered;
	}

	public void setOpenClosed(String openClosed) {
		this.openClosed = openClosed;
	}

}
