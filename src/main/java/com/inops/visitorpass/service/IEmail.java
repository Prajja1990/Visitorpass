package com.inops.visitorpass.service;

import java.util.List;
import java.util.Optional;

import com.inops.visitorpass.entity.EMail;

public interface IEmail {

	Optional<List<EMail>> findAll();

	EMail save(EMail eMail);

	void delete(EMail eMail);

	void deleteAll(List<EMail> eMail);
}
