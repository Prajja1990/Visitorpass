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
	private String sourceIntegrationQuery;

	@Lob
	private String sourceFields;

	@Lob
	private String destinationIntegrationQuery;

	@Lob
	private String destinationFields;
	
	@Lob
	private String databaseName;

	@Lob
	private String driverClassName;

	@Lob
	private String url;

	@Lob
	private String username;

	@Lob
	private String password;

	public void setId(Long id) {
		this.id = id;
	}

	public void setSourceIntegrationQuery(String sourceIntegrationQuery) {
		this.sourceIntegrationQuery = sourceIntegrationQuery;
	}

	public void setSourceFields(String sourceFields) {
		this.sourceFields = sourceFields;
	}

	public void setDestinationIntegrationQuery(String destinationIntegrationQuery) {
		this.destinationIntegrationQuery = destinationIntegrationQuery;
	}

	public void setDestinationFields(String destinationFields) {
		this.destinationFields = destinationFields;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}
					
}
