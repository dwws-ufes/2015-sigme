package br.org.feees.sigme.core.application;

import javax.ejb.Local;

import br.org.feees.sigme.core.domain.Spiritist;
import br.ufes.inf.nemo.util.ejb3.application.CrudService;


@Local
public interface ManageRegisterSpiritistService extends CrudService<Spiritist>{}
