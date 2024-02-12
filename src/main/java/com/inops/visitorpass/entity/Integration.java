package com.inops.visitorpass.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
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
@Table(name = "Integration")
public class Integration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Lob
	private String integrationQuery;

	@Lob
	private String sourceFields;

	@Lob
	private String destinationFields;

	public void setId(Long id) {
		this.id = id;
	}

	public void setIntegrationQuery(String integrationQuery) {
		this.integrationQuery = integrationQuery;
	}

	public void setSourceFields(String sourceFields) {
		this.sourceFields = sourceFields;
	}

	public void setDestinationFields(String destinationFields) {
		this.destinationFields = destinationFields;
	}

}
