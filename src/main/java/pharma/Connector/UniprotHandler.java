package pharma.Connector;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pharma.Term.UniprotTerm;

public class UniprotHandler extends DefaultHandler {

	/// Triggers
	boolean bEntry = false;
	boolean bAccession = false;
	boolean bProtein = false;
	boolean bRecommendedName = false;
	boolean bAlternativeName = false;
	
	
	private ArrayList<UniprotTerm> uniprotTermList = new ArrayList<UniprotTerm>();
	private UniprotTerm uniprotTerm = null;
	
	public ArrayList<UniprotTerm> getTerms() {
		return uniprotTermList;
	}
	
	/**
	 * Sets the triggers for the keywords: add here the exact ones
	 */
	@Override
	public void startElement(String uri, 
			   String localName, String qName, Attributes attributes) throws SAXException {
	
		if(qName.equalsIgnoreCase("entry")) {
			bEntry = true;
		} else if(qName.equalsIgnoreCase("accession")) {
			bAccession = true;
		} else if(qName.equalsIgnoreCase("protein")) {
			bProtein = true;
		} else if(qName.equalsIgnoreCase("recommendedName")) {
			bRecommendedName = true;
		} else if(qName.equalsIgnoreCase("alternativeName")) {
			bAlternativeName = true;
		}
	}

	/**
	 * Sets the triggers for the end of a term
	 */
	@Override
	public void endElement(
				String uri, String localName, String qName) throws SAXException {
		
		if(qName.equalsIgnoreCase("entry")) {
			bEntry = false;
		} else if(qName.equalsIgnoreCase("accession")) {
			bAccession = false;
		} else if(qName.equalsIgnoreCase("protein")) {
			bProtein = false;
		} else if(qName.equalsIgnoreCase("recommendedName")) {
			bRecommendedName = false;
		} else if(qName.equalsIgnoreCase("alternativeName")) {
			bAlternativeName = false;
		}
		 
	}
	
	
	/**
	 * Specifies what should happen on trigger
	 * SAX processes XML sequentially, we assume a well-formed XML.
	 */
	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
	
		/// If we get to "entry", a new term begins, reset everything
		if(bEntry) {
			if(uniprotTerm != null) {
				uniprotTermList.add(uniprotTerm);
				System.out.println("Entry closed..");				
			}
			uniprotTerm = new UniprotTerm();
			System.out.println("Entry found..");
			bEntry = false;
			bAccession = false;
			bProtein = false;
			bRecommendedName = false;
			bAlternativeName = false;
		}
		
		/// Next the fields of a term. We assume they are inside of an "entry"
		if(bAccession) {
			System.out.println("Acc found..");
			uniprotTerm.setIri(new String(ch, start, length));
			System.out.println(uniprotTerm.getIri());
		}
		
		if(bProtein && bRecommendedName) {
			
			System.out.println("Protein found..");
			
			if(uniprotTerm.getLabel() == null)
				uniprotTerm.setLabel(new String(ch, start, length));
			else
				uniprotTerm.setLabel(uniprotTerm.getLabel() + " -- " + new String(ch, start, length));
		}
		
		if(bProtein && bAlternativeName) {
			
			if(uniprotTerm.getSynonym() == null)
				uniprotTerm.setSynonym(new String(ch, start, length));
			else
				uniprotTerm.setSynonym(uniprotTerm.getSynonym() + " -- " + new String(ch, start, length));
		}
		
		
	}
	    
	
	
}
