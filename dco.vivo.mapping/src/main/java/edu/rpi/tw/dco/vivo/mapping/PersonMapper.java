package edu.rpi.tw.dco.vivo.mapping;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.*;
import java.util.Collection;
import java.util.LinkedList;

import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

public class PersonMapper {	
	private static final String
	        NS = "http://tw.rpi.edu/dco-vivo/",
			PERSON_ID_ROOT = NS + "person/";
	
	private static final ValueFactory VF = new ValueFactoryImpl();
	
	public static void main(String[] args) {
        File outputFile = new File("/tmp/out.rdf");
        
		try {
			new PersonMapper().map(outputFile);
		} catch (Throwable t) {
			t.printStackTrace(System.err);
			System.exit(1);
		}
	}
	
	//try to read data from dco_dupal database and write to an rdf file 
	private void map(final File file) throws Exception {
		Collection<org.openrdf.model.Statement> rdf = new LinkedList<org.openrdf.model.Statement>();
		
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dco_portal", "root", "123456"); 

        ResultSet rset;
        
        rset = executeQuery(conn, "select uid, name, mail from users");
        //Statement stmt = conn.createStatement();

        // String strSql = "select uid, name, mail from users";
        //System.out.println("The SQL query is: " + strSql); // Echo For debugging
        //System.out.println();

        //ResultSet rset = stmt.executeQuery(strSql);

        // System.out.println("The records selected are:");
        // int rowCount = 0;
        while(rset.next()) { 
       	   Integer uid = rset.getInt("uid");
       	   URI self = VF.createURI(PERSON_ID_ROOT + uid);
       	   
       	   //each user is an instance of FOAF:Person
       	   rdf.add(VF.createStatement(self, RDF.TYPE, FOAF.PERSON));
	   
           mapName(rset, rdf, self);
	       mapEmail(rset, rdf, self);
	   
       	   ResultSet rset2 = executeQuery(conn, "select value from profile_values where fid=1 and uid= " + uid);
       	   mapFirstName(rset2, rdf, self);
       	   rset2.close();

       	   ResultSet rset3 = executeQuery(conn, "select value from profile_values where fid=2 and uid= " + uid);
       	   mapLastName(rset3, rdf, self);
       	   rset3.close();
       	   
       	   ResultSet rset4 = executeQuery(conn, "select value from profile_values where fid=9 and uid= " + uid);
    	   mapPrefix(rset4, rdf, self);
    	   rset4.close();
       	   
    	   ResultSet rset5 = executeQuery(conn, "select value from profile_values where fid=10 and uid= " + uid);
    	   mapOrganization(rset5, rdf, self, conn, uid);
    	   rset5.close();

    	   ResultSet rset7 = executeQuery(conn, "select value from profile_values where fid=16 and uid= " + uid);
    	   mapSkype(rset7, rdf, self);
    	   rset7.close();
    	   
    	   ResultSet rset8 = executeQuery(conn, "select value from profile_values where fid=19 and uid= " + uid);
    	   mapTelephone(rset8, rdf, self);
    	   rset8.close();

    	   ResultSet rset9 = executeQuery(conn, "select value from profile_values where fid=20 and uid= " + uid);
    	   mapFax(rset9, rdf, self);
    	   rset9.close();
    	   
    	   ResultSet rset10 = executeQuery(conn, "select value from profile_values where fid=21 and uid= " + uid);
    	   mapLanguage(rset10, rdf, self);
    	   rset10.close();
    	   
    	   ResultSet rset11 = executeQuery(conn, "select value from profile_values where fid=27 and uid= " + uid);
    	   mapPersonalURL(rset11, rdf, self);
    	   rset11.close();
    	   
    	   ResultSet rset12 = executeQuery(conn, "select value from profile_values where fid=29 and uid= " + uid);
    	   ResultSet rset13 = executeQuery(conn, "select value from profile_values where fid=30 and uid= " + uid);
    	   ResultSet rset14 = executeQuery(conn, "select value from profile_values where fid=31 and uid= " + uid);
    	   ResultSet rset15 = executeQuery(conn, "select value from profile_values where fid=32 and uid= " + uid);
    	   ResultSet rset16 = executeQuery(conn, "select value from profile_values where fid=34 and uid= " + uid);
    	   ResultSet rset17 = executeQuery(conn, "select value from profile_values where fid=51 and uid= " + uid);
    	   ResultSet rset18 = executeQuery(conn, "select value from profile_values where fid=49 and uid= " + uid);
    	   mapMailingAddress(rset12, rset13, rset14, rset15, rset16, rset17, rset18, rdf, self);
    	   rset12.close();
    	   rset13.close();
    	   rset14.close();
    	   rset15.close();
    	   rset16.close();
    	   rset17.close();
    	   rset18.close();
    	   
    	   ResultSet rset19 = executeQuery(conn, "select value from profile_values where fid=44 and uid= " + uid);
    	   mapHomeCountry(rset19, rdf, self);
    	   rset19.close();

        }
        rset.close();
        
        System.out.println("generated RDF statements:");
        for (org.openrdf.model.Statement st : rdf) {
            System.out.println("\t" + st);	
        }
        
        OutputStream out = new FileOutputStream(file);
        try {
            RDFWriter w = Rio.createWriter(RDFFormat.RDFXML, out);
            w.startRDF();
            for (org.openrdf.model.Statement st : rdf) {
                w.handleStatement(st);
            }
            w.endRDF();
        } finally {
        	out.close();
        }
	}
	
	private void mapHomeCountry(final ResultSet rset19,
			final Collection<org.openrdf.model.Statement> rdf, final URI self) throws SQLException {
		if(rset19.next()){
			String nationality = rset19.getString("value");
			if(nationality.length()!=0){
				rdf.add(VF.createStatement(self, GEO.HAS_NATIONALITY, VF.createLiteral(nationality)));	
			}
		}
	}

	private void mapMailingAddress(final ResultSet rset12,
			ResultSet rset13, ResultSet rset14, ResultSet rset15, ResultSet rset16, ResultSet rset17, ResultSet rset18, final Collection<org.openrdf.model.Statement> rdf, final URI self) throws SQLException {
		BNode bn = VF.createBNode();
		rdf.add(VF.createStatement(bn, RDF.TYPE, VIVO_CORE.ADDRESS));	
		
		if(rset12.next()){
			String addressLine1 = rset12.getString("value");
			if(addressLine1.length()!=0){
				rdf.add(VF.createStatement(bn, VIVO_CORE.ADDRESS_LINE_1, VF.createLiteral(addressLine1)));	
			}
		}
		
		if(rset13.next()){
			String addressLine2 = rset13.getString("value");
			if(addressLine2.length()!=0){
				rdf.add(VF.createStatement(bn, VIVO_CORE.ADDRESS_LINE_2, VF.createLiteral(addressLine2)));	
			}
		}

		
		if(rset14.next()){
			String addressLine3 = rset14.getString("value");
			if(addressLine3.length()!=0){
				rdf.add(VF.createStatement(bn, VIVO_CORE.ADDRESS_LINE_3, VF.createLiteral(addressLine3)));	
			}
		}
			
		
		if(rset15.next()){
			String addressCity = rset15.getString("value");
			if(addressCity.length()!=0){
				rdf.add(VF.createStatement(bn, VIVO_CORE.ADDRESS_CITY, VF.createLiteral(addressCity)));	
			}
		}
		
		if(rset16.next()){
			String addressPostalCode = rset16.getString("value");
			if(addressPostalCode.length()!=0){
				rdf.add(VF.createStatement(bn, VIVO_CORE.ADDRESS_POSTAL_CODE, VF.createLiteral(addressPostalCode)));	
			}
		}
		
		if(rset17.next()){
			String addressState = rset17.getString("value");
			if(addressState.length()!=0){
				rdf.add(VF.createStatement(bn, VIVO_CORE.ADDRESS_STATE, VF.createLiteral(addressState)));	
			}
		}
		
		if(rset18.next()){
			String addressCountry = rset18.getString("value");
			if(addressCountry.length()!=0){
				rdf.add(VF.createStatement(bn, VIVO_CORE.ADDRESS_COUNTRY, VF.createLiteral(addressCountry)));	
			}
		}
		
		rdf.add(VF.createStatement(self, VIVO_CORE.MAILING_ADDRESS, bn));	

	}

	private void mapPersonalURL(final ResultSet rset11,
			final Collection<org.openrdf.model.Statement> rdf, final URI self) throws SQLException {
		if(rset11.next()){
			String url = rset11.getString("value");
			
			if (url.indexOf(':') < 0) return;
			
			URI personalURL = VF.createURI(url);
			org.openrdf.model.Statement e = VF.createStatement(personalURL, RDF.TYPE, VIVO_CORE.URL_LINK);
			if(!rdf.contains(e)){
				rdf.add(e);
			}
			
			rdf.add(VF.createStatement(self, VIVO_CORE.WEB_PAGE, personalURL));	
		}		
	}

	private void mapLanguage(final ResultSet rset10,
			final Collection<org.openrdf.model.Statement> rdf, final URI self) {
		// TODO Auto-generated method stub
		
	}

	private void mapFax(final ResultSet rset9,
			final Collection<org.openrdf.model.Statement> rdf, final URI self) throws SQLException{
		if(rset9.next()){
			String faxNumber = rset9.getString("value");
			if(!faxNumber.isEmpty()){
				rdf.add(VF.createStatement(self, VIVO_CORE.FAX_NUMBER, VF.createLiteral(faxNumber)));	
			}
		}				
	}
	
	
	private void mapTelephone(final ResultSet rset8,
			final Collection<org.openrdf.model.Statement> rdf, final URI self) throws SQLException{
		if(rset8.next()){
			String phoneNumber = rset8.getString("value");
			if(!phoneNumber.isEmpty()){
				rdf.add(VF.createStatement(self, VIVO_CORE.PHONE_NUMBER, VF.createLiteral(phoneNumber)));	
			}
		}				
	}

	private void mapSkype(final ResultSet rset7,
			final Collection<org.openrdf.model.Statement> rdf, final URI self) throws SQLException{
		if(rset7.next()){
			String skypeID = rset7.getString("value");
			if(!skypeID.isEmpty()){
				rdf.add(VF.createStatement(self, FOAF.SKYPE_ID, VF.createLiteral(skypeID)));	
			}
		}				
	}

	private void mapOrganization(final ResultSet rset5,
			final Collection<org.openrdf.model.Statement> rdf, final URI self, Connection conn, Integer uid ) throws SQLException {
		if(rset5.next()){
			String Organization = rset5.getString("value");
			
			//add organizations. to do: need to check!
			URI org = VF.createURI(FOAF.ORGANIZATION_ID_ROOT + Organization);
			org.openrdf.model.Statement e = VF.createStatement(org, RDF.TYPE, FOAF.ORGANIZATION);
			if(!rdf.contains(e)){
				rdf.add(e);
			}
			
			rdf.add(VF.createStatement(self, VIVO_CORE.CURRENT_MEMBER_OF, org));
			
			//add organization url
			ResultSet rset6 = executeQuery(conn, "select value from profile_values where fid=14 and uid= " + uid);
			mapOrganizationURL(rset6, rdf, org);
	    	rset6.close();
	    	
	    	//add organization country

		}				
	}

	private void mapOrganizationURL(final ResultSet rset6,
			final Collection<org.openrdf.model.Statement> rdf, final URI org) throws SQLException {
		if(rset6.next()){
			String url = rset6.getString("value");
			
			if (url.indexOf(':') < 0) return;
			
			URI orgURL = VF.createURI(url);
			org.openrdf.model.Statement e = VF.createStatement(orgURL, RDF.TYPE, VIVO_CORE.URL_LINK);
			if(!rdf.contains(e)){
				rdf.add(e);
			}
			
			rdf.add(VF.createStatement(org, VIVO_CORE.WEB_PAGE, orgURL));	
		}		
	}

	//to do:  vivo:preferredTitle(if it is Prof., Dr., etc.)  bibo:prefixName(if it is Mr., Mrs., Ms., Mme., etc. )
	private void mapPrefix(final ResultSet rset4,
			final Collection<org.openrdf.model.Statement> rdf, final URI self) throws SQLException {
		if(rset4.next()){
			String prefixName = rset4.getString("value");
			rdf.add(VF.createStatement(self, BIBO.PREFIX_NAME, VF.createLiteral(prefixName)));	
		}		
	}

	private void mapLastName(final ResultSet rset3,
			final Collection<org.openrdf.model.Statement> rdf, final URI self) throws SQLException {
		if(rset3.next()){
			String lastName = rset3.getString("value");
			rdf.add(VF.createStatement(self, FOAF.LAST_NAME, VF.createLiteral(lastName)));	
		}
	}

	private void mapFirstName(final ResultSet rset2,
			final Collection<org.openrdf.model.Statement> rdf, final URI self) throws SQLException{
		if(rset2.next()){
			String firstName = rset2.getString("value");
			rdf.add(VF.createStatement(self, FOAF.FIRST_NAME, VF.createLiteral(firstName)));	
		}
	}

	private void mapEmail(final ResultSet rset,
			final Collection<org.openrdf.model.Statement> rdf, final URI self) throws SQLException {
		String email = rset.getString("mail");
		rdf.add(VF.createStatement(self, VIVO_CORE.EMAIL, VF.createLiteral(email)));		
	}

	private void mapName(final ResultSet rset,
			                   final Collection<org.openrdf.model.Statement> rdf,
			                   final URI self) throws SQLException {
		String name = rset.getString("name");
		rdf.add(VF.createStatement(self, FOAF.NICK, VF.createLiteral(name)));
	}
	
	private ResultSet executeQuery(Connection conn, String strSql) throws SQLException{
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(strSql);
		
		System.out.println("The SQL query is: " + strSql); // Echo For debugging
        System.out.println();
		return rset;
		
	}
	
}
