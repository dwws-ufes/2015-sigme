package br.org.feees.sigme.core.persistence;

import javax.ejb.Local;

import br.org.feees.sigme.core.domain.EmailConfirmation;
import br.ufes.inf.nemo.util.ejb3.persistence.BaseDAO;

/**
 * Interface for a DAO for objects of the EmailConfirmation domain class.
 * 
 * Using a mini CRUD framework for EJB3, basic DAO operation definitions are inherited from the superclass, whereas
 * operations that are specific to the managed domain class (if any) are specified in this class.
 * 
 * @author Vitor E. Silva Souza (vitorsouza@gmail.com)
 * @see br.org.feees.sigme.core.domain.EmailConfirmation
 * @see br.ufes.inf.nemo.util.ejb3.persistence.BaseDAO
 */
@Local
public interface EmailConfirmationDAO extends BaseDAO<EmailConfirmation> {}
