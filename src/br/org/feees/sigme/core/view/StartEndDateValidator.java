package br.org.feees.sigme.core.view;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.validation.ValidationException;

@FacesValidator("sigmeStartEndDateValidator")
public class StartEndDateValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

		if (value == null) {
			return;
		}
		
		Date end = (Date) value;
		UIInput cnt = (UIInput) component.getAttributes().get("attendanceStartDate");
		Date start = new Date((long) cnt.getSubmittedValue());
		
		if (start != null && end != null) {
			if (start.compareTo(end) < 0) {
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, I18n.getString("facesValidator.sigmeTaxCodeValidator.invalidTaxCode.summary"), I18n.getString("facesValidator.sigmeTaxCodeValidator.invalidTaxCode.detail"));
				context.addMessage(component.getId(), msg);
				throw new ValidatorException(msg);
			}
		}

	}

}
