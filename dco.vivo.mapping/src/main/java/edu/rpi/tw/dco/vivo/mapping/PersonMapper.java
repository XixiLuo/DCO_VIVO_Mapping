package edu.rpi.tw.dco.vivo.mapping;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.*;
import java.util.Collection;
import java.util.LinkedList;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

public class PersonMapper {
	private static final String NS = "http://tw.rpi.edu/dco-vivo/",
			MAILING_ID_ROOT = NS + "mailadd/", WEBPAGE_ID_ROOT = NS
					+ "webpage/", ROLE_ID_ROOT = NS + "role/",
			POSITION_ID_ROOT = NS + "position/", ORGANIZATION_ID_ROOT = NS
					+ "organization/", PERSON_ID_ROOT = NS + "person/";

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

	// try to read data from dco_dupal database and write to an rdf file
	private void map(final File file) throws Exception {
		Collection<org.openrdf.model.Statement> rdf = new LinkedList<org.openrdf.model.Statement>();

		Connection conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/dco_portal", "root", "123456");

		ResultSet rset;

		rset = executeQuery(conn, "select uid, name, mail from users");
		// Statement stmt = conn.createStatement();

		// String strSql = "select uid, name, mail from users";
		// System.out.println("The SQL query is: " + strSql); // Echo For
		// debugging
		// System.out.println();

		// ResultSet rset = stmt.executeQuery(strSql);

		// System.out.println("The records selected are:");
		// int rowCount = 0;
		//int i = 1;
		while (rset.next()) {
			Integer uid = rset.getInt("uid");
			String uFirstName = null;
			String uLastName = null;

			if (uid == 0)
				continue;

			URI self = VF.createURI(PERSON_ID_ROOT + uid);

			// each user is an instance of FOAF:Person
			rdf.add(VF.createStatement(self, RDF.TYPE, FOAF.PERSON));
			addDOCId(self, rdf);


			mapName(rset, rdf, self);
			mapEmail(rset, rdf, self);

			ResultSet rset2 = executeQuery(conn,
					"select value from profile_values where fid=1 and uid= "
							+ uid);
			uFirstName = mapFirstName(rset2, rdf, self);
			rset2.close();

			ResultSet rset3 = executeQuery(conn,
					"select value from profile_values where fid=2 and uid= "
							+ uid);
			uLastName = mapLastName(rset3, rdf, self);
			rset3.close();

			rdf.add(VF.createStatement(self, RDFS.LABEL,
					VF.createLiteral(uLastName + ", " + uFirstName)));

			ResultSet rset4 = executeQuery(conn,
					"select value from profile_values where fid=9 and uid= "
							+ uid);
			mapPrefix(rset4, rdf, self);
			rset4.close();

			ResultSet rset5 = executeQuery(conn,
					"select * from profile_values where fid=10 and uid= " + uid);
			mapOrganization(rset5, rdf, self, conn, uid);
			rset5.close();

			ResultSet rset7 = executeQuery(conn,
					"select value from profile_values where fid=16 and uid= "
							+ uid);
			mapSkype(rset7, rdf, self);
			rset7.close();

			ResultSet rset8 = executeQuery(conn,
					"select value from profile_values where fid=19 and uid= "
							+ uid);
			mapTelephone(rset8, rdf, self);
			rset8.close();

			ResultSet rset9 = executeQuery(conn,
					"select value from profile_values where fid=20 and uid= "
							+ uid);
			mapFax(rset9, rdf, self);
			rset9.close();

			ResultSet rset10 = executeQuery(conn,
					"select value from profile_values where fid=21 and uid= "
							+ uid);
			mapLanguage(rset10, rdf, self);
			rset10.close();

			ResultSet rset11 = executeQuery(conn,
					"select value from profile_values where fid=27 and uid= "
							+ uid);
			mapPersonalURL(rset11, rdf, self);
			rset11.close();

			ResultSet rset12 = executeQuery(conn,
					"select value from profile_values where fid=29 and uid= "
							+ uid);
			ResultSet rset13 = executeQuery(conn,
					"select value from profile_values where fid=30 and uid= "
							+ uid);
			ResultSet rset14 = executeQuery(conn,
					"select value from profile_values where fid=31 and uid= "
							+ uid);
			ResultSet rset15 = executeQuery(conn,
					"select value from profile_values where fid=32 and uid= "
							+ uid);
			ResultSet rset16 = executeQuery(conn,
					"select value from profile_values where fid=34 and uid= "
							+ uid);
			ResultSet rset17 = executeQuery(conn,
					"select value from profile_values where fid=51 and uid= "
							+ uid);
			ResultSet rset18 = executeQuery(conn,
					"select value from profile_values where fid=49 and uid= "
							+ uid);
			mapMailingAddress(rset12, rset13, rset14, rset15, rset16, rset17,
					rset18, rdf, self);
			rset12.close();
			rset13.close();
			rset14.close();
			rset15.close();
			rset16.close();
			rset17.close();
			rset18.close();

			ResultSet rset19 = executeQuery(conn,
					"select value from profile_values where fid=44 and uid= "
							+ uid);
			mapHomeCountry(rset19, rdf, self);
			rset19.close();

			ResultSet rset20 = executeQuery(conn,
					"select tid from term_user where uid= " + uid);
			mapAffiliationRole(rset20, rdf, self, conn, uid);
			rset20.close();
			
			ResultSet rset21 = executeQuery(conn,
					"select value from profile_values where fid=47 and uid= "
							+ uid);
			mapExpertise(rset21, rdf, self);
			rset21.close();

			// System.out.println();// Echo For debugging
			// System.out.println("The uid is: " + uid);// Echo For debugging

			// if (++i > 10) break;
			
			

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

	private void mapExpertise(final ResultSet rset21,
			final Collection<org.openrdf.model.Statement> rdf, final URI self) throws SQLException {
		String overview = null;
		if (rset21.next()) {
			overview = rset21.getString("value");
			rdf.add(VF.createStatement(self, VIVO.OVERVIEW,
					VF.createLiteral(overview)));
		}
	}

	private void mapAffiliationRole(final ResultSet rset20,
			final Collection<org.openrdf.model.Statement> rdf, final URI self,
			final Connection conn, Integer uid) throws SQLException {
		/*
		 * while(rset20.next()) { Integer tid = rset20.getInt("tid"); ResultSet
		 * rset21 = executeQuery(conn, "select * from term_data where tid= " +
		 * tid); System.out.println("The tid is: " + tid); // Echo For debugging
		 * if(rset21.next()){ String name = rset21.getString("name"); String
		 * description = rset21.getString("description");
		 * System.out.println("name is: " + name);
		 * System.out.println("description is: " + description); } ResultSet
		 * rset22 = executeQuery(conn,
		 * "select parent from term_hierarchy where tid= " + tid);
		 * if(rset22.next()){ Integer pid=rset22.getInt("parent"); if(pid != 0){
		 * ResultSet rset23 = executeQuery(conn,
		 * "select * from term_data where tid= " + pid); if(rset23.next()){
		 * String name = rset23.getString("name"); String description =
		 * rset23.getString("description");
		 * System.out.println("and its parent name is: " + name);
		 * System.out.println("and its parent description is: " + description);
		 * 
		 * } } } }
		 */

		while (rset20.next()) {
			Integer tid = rset20.getInt("tid");
			ResultSet rset21 = executeQuery(conn,
					"select * from term_data where tid= " + tid);
			String tName = null;
			if (rset21.next()) {
				tName = rset21.getString("name");
				String tDescription = rset21.getString("description");
			}

			ResultSet rset22 = executeQuery(conn,
					"select parent from term_hierarchy where tid= " + tid);
			if (rset22.next()) {
				Integer pid = rset22.getInt("parent");
				if (pid != 0) {
					ResultSet rset23 = executeQuery(conn,
							"select * from term_data where tid= " + pid);
					if (rset23.next()) {
						String pName = rset23.getString("name");

						String pDescription = rset23.getString("description");

						URI role = addNewRole(tName, rdf);

						URI org = addNewOrganization(pName, rdf);

						org.openrdf.model.Statement euo = VF.createStatement(
								self, VIVO.CURRENT_MEMBER_OF, org);
						if (!rdf.contains(euo)) {
							rdf.add(euo);
						}

						org.openrdf.model.Statement eou = VF.createStatement(
								org, VIVO.HAS_CURRENT_MEMBER, self);
						if (!rdf.contains(eou)) {
							rdf.add(eou);
						}

						org.openrdf.model.Statement eor = VF.createStatement(
								org, VIVO.HAS_ROLE, role);
						if (!rdf.contains(eor)) {
							rdf.add(eor);
						}

						org.openrdf.model.Statement ero = VF.createStatement(
								role, VIVO.ROLE_OF, org);
						if (!rdf.contains(ero)) {
							rdf.add(ero);
						}

						// ------------------
						URI position = addNewPosition(tName, rdf);

						org.openrdf.model.Statement e2 = VF.createStatement(
								position, VIVO.POSITION_FOR_PERSON, self);
						if (!rdf.contains(e2)) {
							rdf.add(e2);
						}

						org.openrdf.model.Statement e3 = VF.createStatement(
								position, VIVO.POSITION_IN_ORGANIZATION, org);
						if (!rdf.contains(e3)) {
							rdf.add(e3);
						}

						org.openrdf.model.Statement e4 = VF.createStatement(
								position, VIVO.ASSOCIATED_ROLE, role);
						if (!rdf.contains(e4)) {
							rdf.add(e4);
						}

					}
					rset23.close();
				} else {
					addNewOrganization(tName, rdf);
				}
			}
			rset21.close();
			rset22.close();
		}
	}

	private void mapHomeCountry(final ResultSet rset19,
			final Collection<org.openrdf.model.Statement> rdf, final URI self)
			throws SQLException {
		if (rset19.next()) {
			String nationality = rset19.getString("value");
			if (nationality.length() != 0) {
				rdf.add(VF.createStatement(self, GEO.HAS_NATIONALITY,
						VF.createLiteral(nationality)));
			}
		}
	}

	private void mapMailingAddress(final ResultSet rset12, ResultSet rset13,
			ResultSet rset14, ResultSet rset15, ResultSet rset16,
			ResultSet rset17, ResultSet rset18,
			final Collection<org.openrdf.model.Statement> rdf, final URI self)
			throws SQLException {

		String addressLine1 = null;
		String addressLine2 = null;
		String addressLine3 = null;
		String addressCity = null;
		String addressPostalCode = null;
		String addressState = null;
		String addressCountry = null;

		if (rset12.next())
			addressLine1 = rset12.getString("value");

		if (rset13.next())
			addressLine2 = rset13.getString("value");

		if (rset14.next())
			addressLine3 = rset14.getString("value");

		if (rset15.next())
			addressCity = rset15.getString("value");

		if (rset16.next())
			addressPostalCode = rset16.getString("value");

		if (rset17.next())
			addressState = rset17.getString("value");

		if (rset18.next())
			addressCountry = rset18.getString("value");

		String addressL = addressLine1 + addressCity + addressState;

		URI mailAdd = addNewMailingAddress(rdf, addressL);

		boolean on = false;

		if (addressLine1 != null) {
			if (addressLine1.length() != 0) {
				rdf.add(VF.createStatement(mailAdd, VIVO.ADDRESS_LINE_1,
						VF.createLiteral(addressLine1)));
				on = true;
			}
		}

		if (addressLine2 != null) {
			if (addressLine2.length() != 0) {
				rdf.add(VF.createStatement(mailAdd, VIVO.ADDRESS_LINE_2,
						VF.createLiteral(addressLine2)));
				on = true;
			}
		}

		if (addressLine3 != null) {
			if (addressLine3.length() != 0) {
				rdf.add(VF.createStatement(mailAdd, VIVO.ADDRESS_LINE_3,
						VF.createLiteral(addressLine3)));
				on = true;
			}
		}

		if (addressCity != null) {
			if (addressCity.length() != 0) {
				rdf.add(VF.createStatement(mailAdd, VIVO.ADDRESS_CITY,
						VF.createLiteral(addressCity)));
				on = true;
			}
		}

		if (addressPostalCode != null) {
			if (addressPostalCode.length() != 0)
				rdf.add(VF.createStatement(mailAdd, VIVO.ADDRESS_POSTAL_CODE,
						VF.createLiteral(addressPostalCode)));
		}

		if (addressState != null) {
			if (addressState.length() != 0)
				rdf.add(VF.createStatement(mailAdd, VIVO.ADDRESS_STATE,
						VF.createLiteral(addressState)));

		}

		if (addressCountry != null) {
			if (addressCountry.length() != 0)
				rdf.add(VF.createStatement(mailAdd, VIVO.ADDRESS_COUNTRY,
						VF.createLiteral(addressCountry)));
		}

		if (on)
			rdf.add(VF.createStatement(self, VIVO.MAILING_ADDRESS, mailAdd));

	}

	private URI addNewMailingAddress(
			Collection<org.openrdf.model.Statement> rdf, String addressL) {
		URI mailadd = VF.createURI(MAILING_ID_ROOT + addressL.hashCode());
		org.openrdf.model.Statement e = VF.createStatement(mailadd, RDF.TYPE,
				VIVO.ADDRESS);

		// check redundancy
		if (!rdf.contains(e)) {
			rdf.add(e);
			rdf.add(VF.createStatement(mailadd, RDFS.LABEL,
					VF.createLiteral(addressL)));
			addDOCId(mailadd, rdf);
		}

		return mailadd;
	}

	private void mapPersonalURL(final ResultSet rset11,
			final Collection<org.openrdf.model.Statement> rdf, final URI self)
			throws SQLException {
		if (rset11.next()) {
			String url = rset11.getString("value");

			if (url.indexOf(':') < 0)
				return;

			URI webpage = addNewWebPage(rdf, url);

			rdf.add(VF.createStatement(self, VIVO.WEB_PAGE, webpage));
			rdf.add(VF.createStatement(webpage, VIVO.WEB_PAGE_OF, self));
			rdf.add(VF.createStatement(webpage, VIVO.LINK_ANCHOR_TEXT,
					VF.createLiteral(url)));
			rdf.add(VF.createStatement(webpage, VIVO.LINK_URI,
					VF.createLiteral(url)));
		}
	}

	private URI addNewWebPage(Collection<org.openrdf.model.Statement> rdf,
			String url) {
		URI webpage = VF.createURI(WEBPAGE_ID_ROOT + url.hashCode());
		org.openrdf.model.Statement e = VF.createStatement(webpage, RDF.TYPE,
				VIVO.URL_LINK);
		if (!rdf.contains(e)) {
			rdf.add(e);
			rdf.add(VF.createStatement(webpage, RDFS.LABEL,
					VF.createLiteral(url)));
			addDOCId(webpage, rdf);
		}
		return webpage;
	}

	private void addDOCId(URI url,
			Collection<org.openrdf.model.Statement> rdf) {
		DCOId di = new DCOId();
		String s = url.toString();
		di.generateDCOId(s);
		String dcoId = di.getDCOId();
		System.out.println("added an DCOId: " + dcoId);	
		rdf.add(VF.createStatement(url, DCO.DCO_ID, 
				VF.createLiteral(dcoId)));
	}

	private void mapLanguage(final ResultSet rset10,
			final Collection<org.openrdf.model.Statement> rdf, final URI self) {
		// TODO Auto-generated method stub

	}

	private void mapFax(final ResultSet rset9,
			final Collection<org.openrdf.model.Statement> rdf, final URI self)
			throws SQLException {
		if (rset9.next()) {
			String faxNumber = rset9.getString("value");
			if (!faxNumber.isEmpty()) {
				rdf.add(VF.createStatement(self, VIVO.FAX_NUMBER,
						VF.createLiteral(faxNumber)));
			}
		}
	}

	private void mapTelephone(final ResultSet rset8,
			final Collection<org.openrdf.model.Statement> rdf, final URI self)
			throws SQLException {
		if (rset8.next()) {
			String phoneNumber = rset8.getString("value");
			if (!phoneNumber.isEmpty()) {
				rdf.add(VF.createStatement(self, VIVO.PHONE_NUMBER,
						VF.createLiteral(phoneNumber)));
			}
		}
	}

	private void mapSkype(final ResultSet rset7,
			final Collection<org.openrdf.model.Statement> rdf, final URI self)
			throws SQLException {
		if (rset7.next()) {
			String skypeID = rset7.getString("value");
			if (!skypeID.isEmpty()) {
				rdf.add(VF.createStatement(self, FOAF.SKYPE_ID,
						VF.createLiteral(skypeID)));
			}
		}
	}

	private void mapOrganization(final ResultSet rset5,
			final Collection<org.openrdf.model.Statement> rdf, final URI self,
			Connection conn, Integer uid) throws SQLException {
		if (rset5.next()) {
			String organization = rset5.getString("value");
			Integer fid = rset5.getInt("fid");

			// add organizations. to do: need to check!

			// create an new organization

			URI org = addNewOrganization(organization, rdf);

			rdf.add(VF.createStatement(self, VIVO.CURRENT_MEMBER_OF, org));

			// add organization url
			ResultSet rset6 = executeQuery(conn,
					"select value from profile_values where fid=14 and uid= "
							+ uid);
			mapOrganizationURL(rset6, rdf, org);
			rset6.close();

			// add organization country

		}
	}

	private URI addNewOrganization(String sOrganization,
			Collection<org.openrdf.model.Statement> rdf) {
		// URI org = VF.createURI(ORGANIZATION_ID_ROOT + uid + "-" + fid);
		URI org = VF.createURI(ORGANIZATION_ID_ROOT + sOrganization.hashCode());
		org.openrdf.model.Statement e = VF.createStatement(org, RDF.TYPE,
				FOAF.ORGANIZATION);
		org.openrdf.model.Statement e1 = VF.createStatement(org, RDFS.LABEL,
				VF.createLiteral(sOrganization));

		// check redundancy
		if (!rdf.contains(e)) {
			rdf.add(e);
			rdf.add(VF.createStatement(org, RDFS.LABEL,
				VF.createLiteral(sOrganization)));
			addDOCId(org, rdf);
		}

		if (!rdf.contains(e1)) {
			rdf.add(e1);
			addDOCId(org, rdf);
		}
		return org;
	}

	private URI addNewRole(String sRole,
			Collection<org.openrdf.model.Statement> rdf) {
		// URI role = VF.createURI(ROLE_ID_ROOT + uid + "-" + tid);
		URI role = VF.createURI(ROLE_ID_ROOT + sRole.hashCode());
		org.openrdf.model.Statement e = VF.createStatement(role, RDF.TYPE,
				VIVO.ROLE);

		// check redundancy
		if (!rdf.contains(e)) {
			rdf.add(e);
			rdf.add(VF.createStatement(role, RDFS.LABEL,
					VF.createLiteral(sRole)));
			addDOCId(role, rdf);
		}
		return role;
	}

	private URI addNewPosition(String sPosition,
			Collection<org.openrdf.model.Statement> rdf) {
		// URI position = VF.createURI(POSITION_ID_ROOT + uid + "-" + tid);
		URI position = VF.createURI(POSITION_ID_ROOT + sPosition.hashCode());
		org.openrdf.model.Statement e1 = VF.createStatement(position, RDF.TYPE,
				VIVO.POSITION);
		if (!rdf.contains(e1)) {
			rdf.add(e1);
			rdf.add(VF.createStatement(position, RDFS.LABEL,
					VF.createLiteral(sPosition)));
			addDOCId(position, rdf);
		}
		return position;
	}

	private void mapOrganizationURL(final ResultSet rset6,
			final Collection<org.openrdf.model.Statement> rdf, final URI org)
			throws SQLException {
		if (rset6.next()) {
			String sUrl = rset6.getString("value");

			if (sUrl.indexOf(':') < 0)
				return;

			URI webpage = addNewWebPage(rdf, sUrl);

			rdf.add(VF.createStatement(org, VIVO.WEB_PAGE, webpage));
			rdf.add(VF.createStatement(webpage, VIVO.WEB_PAGE_OF, org));
			rdf.add(VF.createStatement(webpage, VIVO.LINK_ANCHOR_TEXT,
					VF.createLiteral(sUrl)));
			rdf.add(VF.createStatement(webpage, VIVO.LINK_URI,
					VF.createLiteral(sUrl)));
		}
	}

	// to do: vivo:preferredTitle(if it is Prof., Dr., etc.) bibo:prefixName(if
	// it is Mr., Mrs., Ms., Mme., etc. )
	private void mapPrefix(final ResultSet rset4,
			final Collection<org.openrdf.model.Statement> rdf, final URI self)
			throws SQLException {
		if (rset4.next()) {
			String prefixName = rset4.getString("value");
			if (prefixName.length() > 0) {
				rdf.add(VF.createStatement(self, VIVO.PREFERRED_TITLE,
						VF.createLiteral(prefixName)));
			}
		}
	}

	private String mapLastName(final ResultSet rset3,
			final Collection<org.openrdf.model.Statement> rdf, final URI self)
			throws SQLException {
		String lastName = null;
		if (rset3.next()) {
			lastName = rset3.getString("value");
			rdf.add(VF.createStatement(self, FOAF.LAST_NAME,
					VF.createLiteral(lastName)));
		}
		return lastName;
	}

	private String mapFirstName(final ResultSet rset2,
			final Collection<org.openrdf.model.Statement> rdf, final URI self)
			throws SQLException {
		String firstName = null;
		if (rset2.next()) {
			firstName = rset2.getString("value");
			rdf.add(VF.createStatement(self, FOAF.FIRST_NAME,
					VF.createLiteral(firstName)));
		}
		return firstName;
	}

	private void mapEmail(final ResultSet rset,
			final Collection<org.openrdf.model.Statement> rdf, final URI self)
			throws SQLException {
		String email = rset.getString("mail");
		rdf.add(VF.createStatement(self, VIVO.EMAIL, VF.createLiteral(email)));
	}

	private void mapName(final ResultSet rset,
			final Collection<org.openrdf.model.Statement> rdf, final URI self)
			throws SQLException {
		String name = rset.getString("name");
		rdf.add(VF.createStatement(self, FOAF.NICK, VF.createLiteral(name)));
		// rdf.add(VF.createStatement(self, RDFS.LABEL,
		// VF.createLiteral(name)));
	}

	private ResultSet executeQuery(Connection conn, String strSql)
			throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(strSql);

		System.out.println("The SQL query is: " + strSql);
		System.out.println();

		return rset;

	}

}
