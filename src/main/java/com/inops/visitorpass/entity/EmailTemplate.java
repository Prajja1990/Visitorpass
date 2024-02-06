package com.inops.visitorpass.entity;

import java.util.ArrayList;
import java.util.List;

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

import org.primefaces.model.DualListModel;

import com.inops.visitorpass.domain.Kvp;

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
@Table(name = "EmailTemplate")
public class EmailTemplate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long templateId;

	private String templateName;
	private String selectionType;

	@Lob
	private String subject;

	@Lob
	private String body;

	@OneToMany(mappedBy = "emailTemplate", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<EmailTemplateAssociation> associations = new ArrayList<>();

	@Transient
	private List<Kvp> target = new ArrayList<>();

	@Transient
	private DualListModel<Kvp> pickSelectedTypes = new DualListModel<>(target, target);

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public void setSelectionType(String selectionType) {
		this.selectionType = selectionType;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setPickSelectedTypes(DualListModel<Kvp> pickSelectedTypes) {
		this.pickSelectedTypes = pickSelectedTypes;
	}

	public void setAssociations(List<EmailTemplateAssociation> associations) {
		this.associations = associations;
	}

	public void setTarget(List<Kvp> target) {
		this.target = target;
	}

}
