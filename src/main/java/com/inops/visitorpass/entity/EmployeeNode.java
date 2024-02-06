package com.inops.visitorpass.entity;

import javax.persistence.CascadeType;
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
import lombok.experimental.Accessors;

@Getter
//@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "EmployeeNode")
public class EmployeeNode {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long employeeNodeId;

	@ManyToOne(fetch = FetchType.EAGER, optional = false ,cascade = CascadeType.ALL)
	@JoinColumn(name = "employeeId", nullable = false, updatable = true)
	private Employee childEmployee;
	
	private String type;
	private String department;	
	
	@ManyToOne
	@JoinColumn(name = "organigramId")
	private Organigram organigramChild;

	public void setEmployeeNodeId(Long employeeNodeId) {
		this.employeeNodeId = employeeNodeId;
	}

	public void setChildEmployee(Employee childEmployee) {
		this.childEmployee = childEmployee;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public void setOrganigramChild(Organigram organigramChild) {
		this.organigramChild = organigramChild;
	}

	
	
}
