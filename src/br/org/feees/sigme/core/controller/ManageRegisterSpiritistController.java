package br.org.feees.sigme.core.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ValidationException;

import org.feees.sigme.people.domain.Address;
import org.feees.sigme.people.domain.City;
import org.feees.sigme.people.domain.ContactType;
import org.feees.sigme.people.domain.Telephone;
import org.feees.sigme.people.persistence.CityDAO;

import br.org.feees.sigme.core.application.ManageRegisterSpiritistService;
import br.org.feees.sigme.core.application.ManageSpiritistsService;
import br.org.feees.sigme.core.application.SessionInformation;
import br.org.feees.sigme.core.domain.Attendance;
import br.org.feees.sigme.core.domain.Institution;
import br.org.feees.sigme.core.domain.InvalidTaxCodeException;
import br.org.feees.sigme.core.domain.Spiritist;
import br.org.feees.sigme.core.persistence.InstitutionDAO;
import br.org.feees.sigme.core.persistence.SpiritistDAO;
import br.ufes.inf.nemo.util.ejb3.application.CrudException;
import br.ufes.inf.nemo.util.ejb3.application.CrudService;
import br.ufes.inf.nemo.util.ejb3.application.CrudValidationError;
import br.ufes.inf.nemo.util.ejb3.application.filters.Criterion;
import br.ufes.inf.nemo.util.ejb3.application.filters.CriterionType;
import br.ufes.inf.nemo.util.ejb3.application.filters.LikeFilter;
import br.ufes.inf.nemo.util.ejb3.application.filters.ManyToManyFilter;
import br.ufes.inf.nemo.util.ejb3.controller.CrudController;
import br.ufes.inf.nemo.util.ejb3.persistence.exceptions.MultiplePersistentObjectsFoundException;
import br.ufes.inf.nemo.util.ejb3.persistence.exceptions.PersistentObjectNotFoundException;

@Named
@SessionScoped
public class ManageRegisterSpiritistController extends CrudController<Spiritist>{
	
	/** Serialization id. */
	private static final long serialVersionUID = 1L;

	/** The logger. */
	private static final Logger logger = Logger.getLogger(ManageSpiritistsController.class.getCanonicalName());

	@EJB
	private SpiritistDAO spiritistDAO;
	
	/** The "Manage Register Spiritist" service. */
	@EJB
	private ManageRegisterSpiritistService manageRegisterSpiritistService;
	
	/** The DAO for City objects. */
	@EJB
	private CityDAO cityDAO;

	/** The DAO for Institution objects. */
	@EJB
	private InstitutionDAO institutionDAO;
	
	/** The DAO for Spiritis objects. */
	@EJB
	private SpiritistDAO spiritisDAO;

	/** Output: the list of telephone numbers. */
	private List<Telephone> telephones;

	/** Input: a telephone being added or edited. */
	private Telephone telephone;

	/** Output: the list of attendances. */
	private List<Attendance> attendances;

	/** Input: the attendance being added or edited. */
	private Attendance attendance;
	
	/** Input: the new password to set. */
	private String newPassword;
	
	@Inject
	private SessionController sessionController;
	
	@Override
	protected CrudService<Spiritist> getCrudService() {
		manageRegisterSpiritistService.authorize();
		return manageRegisterSpiritistService;
	}
	@Override
	protected Spiritist createNewEntity() {
		//return sessionController.getCurrentUser();
		logger.log(Level.FINER, "Initializing an empty spiritist...");

		// Create an empty entity.
		Spiritist newEntity = new Spiritist();
		newEntity.setAddress(new Address());

		// Create empty telephone and attendance lists.
		telephones = new ArrayList<Telephone>();
		attendances = new ArrayList<Attendance>();
		
		return newEntity;
	}

	/** @see br.ufes.inf.nemo.util.ejb3.controller.CrudController#checkSelectedEntity() */
	@Override
	protected void checkSelectedEntity() {
		logger.log(Level.FINER, "Checking selected spiritist ({0})...", selectedEntity);
		
		// The address must not be null.
		if (selectedEntity.getAddress() == null)
			selectedEntity.setAddress(new Address());

		// Create the list of telephones with the already existing telephones. Also check for null.
		if (selectedEntity.getTelephones() == null)
			selectedEntity.setTelephones(new TreeSet<Telephone>());
		//telephones = new ArrayList<Telephone>(selectedEntity.getTelephones());
		telephones = new ArrayList<Telephone>();
		// Same for attendances.
		if (selectedEntity.getAttendances() == null)
			selectedEntity.setAttendances(new TreeSet<Attendance>());
		//attendances = new ArrayList<Attendance>(selectedEntity.getAttendances());
		attendances = new ArrayList<Attendance>();
	}

	

	/** @see br.ufes.inf.nemo.util.ejb3.controller.CrudController#prepEntity() */
	@Override
	protected void prepEntity() {
		logger.log(Level.FINER, "Preparing spiritist for storage ({0})...", selectedEntity);

		// Sets the new password.
		selectedEntity.setPassword(newPassword);

		// Inserts telephone and attendance lists in the entity.
		selectedEntity.setTelephones(new TreeSet<Telephone>(telephones));
		selectedEntity.setAttendances(new TreeSet<Attendance>(attendances));
	}

	@Override
	protected void initFilters() {
		// TODO Auto-generated method stub
		
	}
	
	/** @see br.ufes.inf.nemo.util.ejb3.controller.CrudController#summarizeSelectedEntity() */
	@Override
	protected String summarizeSelectedEntity() {
		return (selectedEntity == null) ? "" : selectedEntity.getShortName();
	}
	
	/**
	 * Analyzes what has been written so far in the city field and, if not empty, looks for cities that start with the
	 * given name and returns them in a list, so a dynamic pop-up list can be displayed. This method is intended to be
	 * used with AJAX.
	 * 
	 * @param query What has been written so far in the city field.
	 * 
	 * @return The list of City objects whose names match the specified query.
	 */
	public List<City> suggestCities(String query) {
		// Checks if something was indeed typed in the field.
		if (query.length() > 0) {
			// Uses the DAO to find the query and returns.
			List<City> cities = cityDAO.findByName(query);
			logger.log(Level.FINE, "Suggestion for cities beginning with \"{0}\" returned {1} results", new Object[] { query, cities.size() });
			return cities;
		}
		return null;
	}
	
	/**
	 * Analyzes what has been written so far in the institution field and, if not empty, looks for institutions that start
	 * with the given name or acronym and returns them in a list, so a dynamic pop-up list can be displayed. This method
	 * is intended to be used with AJAX.
	 * 
	 * @param event
	 *          The AJAX event.
	 * @return The list of institutions to be displayed in the drop-down auto-completion field.
	 */
	public List<Institution> suggestInstitutions(String event) {
		String param = event;
		if (param.length() > 0) {
			List<Institution> suggestions = institutionDAO.findByNameOrAcronym(param);
			logger.log(Level.FINE, "Suggestion for institutions with name or acronym beginning with \"{0}\" returned {1} results", new Object[] { param, suggestions.size() });
			return suggestions;
		}
		return null;
	}
	
	/** Getter for telephones. */
	public List<Telephone> getTelephones() {
		return telephones;
	}

	/** Setter for telephones. */
	public void setTelephones(List<Telephone> telephones) {
		this.telephones = telephones;
	}

	/** Getter for telephone. */
	public Telephone getTelephone() {
		return telephone;
	}

	/** Setter for telephone. */
	public void setTelephone(Telephone telephone) {
		this.telephone = telephone;
		logger.log(Level.FINEST, "Telephone \"{0}\" has been selected", telephone);
	}
	
	/** 
	 * Getter for the type attribute of the telephone, created because PrimeFaces p:selectOneMenu complains of the EL 
	 * #{manageSpiritistsAction.telephone.type} if telephone is null. This method checks for nulls.
	 * 
	 * See: http://forum.primefaces.org/viewtopic.php?f=3&t=14128&p=43494#p43494 
	 */
	public ContactType getTelephoneType() {
		return (telephone == null) ? null : telephone.getType();
	}
	
	/** 
	 * Setter for the type attribute of the telephone, created because PrimeFaces p:selectOneMenu complains of the EL 
	 * #{manageSpiritistsAction.telephone.type} if telephone is null. This method checks for nulls.
	 * 
	 * See: http://forum.primefaces.org/viewtopic.php?f=3&t=14128&p=43494#p43494 
	 */
	public void setTelephoneType(ContactType type) {
		if (telephone != null) telephone.setType(type);
	}

	/**
	 * Creates a new and empty telephone so the telephone fields can be filled. 
	 * 
	 * This method is intended to be used with AJAX, through the PrimeFaces Collector component.
	 */
	public void newTelephone() {
		telephone = new Telephone();
		logger.log(Level.FINEST, "Empty telephone created as selected telephone");
	}
	
	/**
	 * TODO: document this method.
	 */
	public void resetTelephone() {
		telephone = null;
		logger.log(Level.FINEST, "Telephone has been reset -- no telephone is selected");
	}
	
	/** Getter for attendances. */
	public List<Attendance> getAttendances() {
		return attendances;
	}

	/** Setter for attendances. */
	public void setAttendances(List<Attendance> attendances) {
		this.attendances = attendances;
	}

	/** Getter for attendance. */
	public Attendance getAttendance() {
		return attendance;
	}

	/** Setter for attendance. */
	public void setAttendance(Attendance attendance) {
		this.attendance = attendance;
		logger.log(Level.FINEST, "Attendance \"{0}\" has been selected", attendance);
	}

	/**
	 * Creates a new and empty attendance so the attendance fields can be filled. 
	 * 
	 * This method is intended to be used with AJAX, through the PrimeFaces Collector component.
	 */
	public void newAttendance() {
		attendance = new Attendance();
		attendance.setSpiritist(selectedEntity);
		logger.log(Level.FINEST, "Empty attendance created as selected attendance");
	}
	
	/**
	 * TODO: document this method.
	 */
	public void resetAttendance() {
		attendance = null;
		logger.log(Level.FINEST, "Attendance has been reset -- no attendance is selected");
	}
	
	public void onPageLoad() {
		selectedEntity = sessionController.getCurrentUser();
		checkSelectedEntity();
	}
	
	@Override
	public String list() {
		selectedEntity = null;
				
		return "/login.xhtml?faces-redirect=" + getFacesRedirect();
	}
	
}
