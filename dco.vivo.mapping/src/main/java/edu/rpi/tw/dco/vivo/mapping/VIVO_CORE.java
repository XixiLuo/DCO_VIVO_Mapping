package edu.rpi.tw.dco.vivo.mapping;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public interface VIVO_CORE {

	static String NS = "http://vivoweb.org/ontology/core#";

    static URI
    	ROLE_OF = new URIImpl(NS + "roleOf"),
    	HAS_ROLE = new URIImpl(NS + "hasRole"),
    	ROLE_ID_ROOT = new URIImpl(NS + "Role/"),
    	ROLE = new URIImpl(NS + "Role"),
    	HAS_CURRENT_MEMBER = new URIImpl(NS + "hasCurrentMember"),
    	CURRENTLY_HEADED_BY = new URIImpl(NS + "currentlyHeadedBy"),
    	CURRENTLY_MEMBER_OF = new URIImpl(NS + "currentMemberOf"),
    	CURRENTLY_HEAD_OF = new URIImpl(NS + "currentlyHeadOf"),
    	HOME_COUNTRY = new URIImpl(NS + "Country"), 
    	ADDRESS_STATE = new URIImpl(NS + "addressState"),
    	ADDRESS_COUNTRY = new URIImpl(NS + "addressCountry"),
    	ADDRESS_POSTAL_CODE = new URIImpl(NS + "addressPostalCode"),
    	ADDRESS_CITY = new URIImpl(NS + "addressCity"),
		ADDRESS_LINE_3 = new URIImpl(NS + "address3"),
		ADDRESS_LINE_2 = new URIImpl(NS + "address2"),
    	ADDRESS_LINE_1 = new URIImpl(NS + "address1"),
    	ADDRESS = new URIImpl(NS + "Address"),
    	MAILING_ADDRESS = new URIImpl(NS + "mailingAddress"),
    	FAX_NUMBER = new URIImpl(NS + "faxNumber"),
    	PHONE_NUMBER = new URIImpl(NS + "phoneNumber"),
    	URL_LINK = new URIImpl(NS + "URLLink"),
    	WEB_PAGE = new URIImpl(NS + "webpage"),
		CURRENT_MEMBER_OF = new URIImpl(NS + "currentMemberOf"),
    	EMAIL = new URIImpl(NS + "email");
}