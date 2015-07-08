package br.org.feees.sigme.core.view;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.org.feees.sigme.core.domain.Institution;
import br.org.feees.sigme.core.domain.Spiritist;
import br.org.feees.sigme.core.persistence.InstitutionDAO;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

@WebServlet(name = "ListSpiritistInRdfServlet", urlPatterns = { "/rdf/" })
public class ListSpiritistInRdfServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@EJB
	private InstitutionDAO institutionDAO;
	
	/** The logger. */
	private static final Logger logger = Logger.getLogger(LogoutServlet.class.getCanonicalName());
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/xml");
		
		List<Institution> list = institutionDAO.retrieveAll();
		
		Model model = ModelFactory.createDefaultModel();
		String	myNS = "http://localhost:8080/Sigme/rdf/";
		String grNS = "http://dbpedia.org/ontology/";
		model.setNsPrefix("ReligiousBuilding", grNS);
		Resource grReligiousBuilding = ResourceFactory.createResource(grNS + "ReligiousBuilding");
		Property address = ResourceFactory.createProperty(grNS + "Building");
				
		for (Institution i : list) {			
			model.createResource(myNS + i.getAcronym())
				.addProperty(RDF.type, grReligiousBuilding)
				.addProperty(RDFS.label, i.getName());
		}
		
		try (PrintWriter out = resp.getWriter()) {
			model.write(out, "RDF/XML");
		}
	}

}
