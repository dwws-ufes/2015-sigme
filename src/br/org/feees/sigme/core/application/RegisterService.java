package br.org.feees.sigme.core.application;

import java.io.Serializable;

import javax.ejb.Local;

import br.org.feees.sigme.core.domain.EmailConfirmation;
import br.org.feees.sigme.core.domain.Spiritist;
import br.org.feees.sigme.core.exceptions.EmailAlreadyRegisteredException;
import br.org.feees.sigme.core.exceptions.SystemNotConfiguredException;

/**
 * Local EJB interface for the "Register" use case.
 * 
 * TODO: document this.
 * 
 * @author Vitor E. Silva Souza (vitorsouza@gmail.com)
 */
@Local
public interface RegisterService extends Serializable {
	/**
	 * First step of registration process, sends a confirmation code to the given e-mail address so the user can verify
	 * that he owns that address and it's working properly.
	 * 
	 * @param emailConfirmation
	 *          Email confirmation object that holds the given e-mail address.
	 * @throws EmailAlreadyRegisteredException
	 *           If the visitor is trying to register with an e-mail address that has already been registered.
	 * @throws SystemNotConfiguredException
	 *           If the current configuration could not be loaded.
	 */
	void sendConfirmation(EmailConfirmation emailConfirmation) throws EmailAlreadyRegisteredException, SystemNotConfiguredException;
	//void saveRegister(Spiritist spiritist);
}

