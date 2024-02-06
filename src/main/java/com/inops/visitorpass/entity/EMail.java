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
@Table(name = "Email")
public class EMail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String smtpHost;
	private int smtpPort;
	private String username;
	private String password;
	private String fromAddress;
	private String useTLS;
	private String useSSL;
	
	public void setId(Long id) {
		this.id = id;
	}
	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}
	public void setSmtpPort(int smtpPort) {
		this.smtpPort = smtpPort;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	public void setUseTLS(String useTLS) {
		this.useTLS = useTLS;
	}
	public void setUseSSL(String useSSL) {
		this.useSSL = useSSL;
	}
	
}
