package com.inops.visitorpass.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Tblmuster")
public class Muster {

	@EmbeddedId
	private MusterId musterId;

	@Column(name = "Shiftid")
	private String shiftId;

	@Column(name = "Attid")
	private String attendanceId;

	@Column(name = "Leavetypeid")
	private String leaveTypeId;

	@Column(name = "Hrsworked")
	private int hoursWorked;

	@Column(name = "Extrahours")
	private int extraHours;

	@Column(name = "Latepunch")
	private int latePunch;

	@Column(name = "SingleOT")
	private int singleOt;

	@Column(name = "DoubleOT")
	private int doubleOt;

	@Column(name = "Earlyout")
	private int earlyOut;

	@Column(name = "Firstin")
	// @Column(name = "in1")
	private String firstInPunch;

	@Column(name = "Lastout")
	// @Column(name = "out1")
	private String lastOutPunch;
					
	@Column(name = "Adjshrthrs")
	private String shortHrs;
	
	@Transient
	private String empName;
	@Transient
	private String department;
	@Transient
	private String cdare;
	@Transient
	private LocalDate attendanceDate;

}
