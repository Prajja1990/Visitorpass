package com.inops.visitorpass.entity;

import javax.persistence.Entity;
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
@Table(name = "emailTemplateAssociation")
public class EmailTemplateAssociation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long associationId;
	
	private String code;
	private String name;
	
	@ManyToOne
	@JoinColumn(name = "templateId")
	private EmailTemplate emailTemplate;
	
}
