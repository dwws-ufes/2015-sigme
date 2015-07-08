package br.org.feees.sigme.core.controller;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.MapModel;

import br.ufes.inf.nemo.util.ejb3.controller.JSFController;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

@Named
@SessionScoped
public class RdfController extends JSFController {
	
	private String cityName;
	
	private MapModel mapModel;
	
	private String centerGmap = "30, 10";
		
	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCenterGmap() {
		return centerGmap;
	}

	public void setCenterGmap(String centerGmap) {
		this.centerGmap = centerGmap;
	}
	
	public MapModel getMapModel() {
		if (mapModel == null) {
			mapModel = new DefaultMapModel();
		}
		return mapModel;
	}

	public void setMapModel(MapModel mapModel) {
		this.mapModel = mapModel;
	}

	public void suggestDescription() {
		String name = cityName;
		if (name != null && name.length() > 3) {
			String query = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> " +
					"PREFIX dbpprop: <http://dbpedia.org/property/> " +
					"SELECT ?x ?latd ?latm ?lats ?longd ?longm ?longs ?latns ?longew " +
					"WHERE { " +
					"?x a dbpedia-owl:Place ; " +
					"dbpprop:latd ?latd ; " +
					"dbpprop:latm ?latm ; " +
					"dbpprop:lats ?lats ; " +
					"dbpprop:longd ?longd ; " +
					"dbpprop:longm ?longm ; " +
					"dbpprop:longs ?longs ; " +
					"dbpprop:latns ?latns ; " +
					"dbpprop:longew ?longew ;" +
					"dbpprop:name ?name . " +
					"FILTER (lcase(str(?name)) = \"" + name.toLowerCase() + "\") " +
					"}";
		
			QueryExecution queryExecution =	QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
			ResultSet results = queryExecution.execSelect();
			
			if (results.hasNext()) {
				QuerySolution querySolution = results.next();
				String lat = (Integer) querySolution.getLiteral("latd").getValue() + "\"" +
						(Integer) querySolution.getLiteral("latm").getValue() + "'" + 
						(Integer) querySolution.getLiteral("lats").getValue();
				
				String longe = (Integer) querySolution.getLiteral("longd").getValue() + "\"" +
						(Integer) querySolution.getLiteral("longm").getValue() + "'" + 
						(Integer) querySolution.getLiteral("longs").getValue();
				
				String ns = (String) querySolution.getLiteral("latns").getValue();
				String ew = (String) querySolution.getLiteral("longew").getValue();
				
				centerGmap = "";
				
				if (ns.charAt(0) == 'S') {
					centerGmap = "-";					
				}
				
				System.out.println(lat);
				
				centerGmap += convertHourToDecimal(lat) + ",";
				
				if (ew.charAt(0) == 'W') {
					centerGmap += "-";
				}
				
				System.out.println(longe);
				
				centerGmap += convertHourToDecimal(longe);
				
			}
		}
	}
	
	private String convertHourToDecimal(String degree) { 
	    //if(!degree.matches("(-)?[0-6][0-9]\"[0-6][0-9]\'[0-6][0-9](.[0-9]{1,5})?"))
	        //throw new IllegalArgumentException();
	    String[] strArray=degree.split("[\"']");
	    return Double.parseDouble(strArray[0])+Double.parseDouble(strArray[1])/60+Double.parseDouble(strArray[2])/3600 + "";
	}
}
