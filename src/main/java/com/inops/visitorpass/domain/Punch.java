package com.inops.visitorpass.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Punch {
	
	private String eventTitel;
	private String openClosed;
	private String eventStatus;
	private Date eventStart;
	private Date eventEnd;
	private String comments;
	

}
