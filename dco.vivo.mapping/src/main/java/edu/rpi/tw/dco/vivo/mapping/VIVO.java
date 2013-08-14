package edu.rpi.tw.dco.vivo.mapping;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public interface VIVO {

	static String NS = "http://vivoweb.org/ontology/core#";

    static URI
    	LINK_ANCHOR_TEXT = new URIImpl(NS + "linkAnchorText"),
    	LINK_URI = new URIImpl(NS + "linkURI"),
    	WEB_PAGE_OF = new URIImpl(NS + "webpageOf"),
    	WEB_PAGE = new URIImpl(NS + "webpage"),

    
    	ASSOCIATED_ROLE = new URIImpl(NS + "associatedRole"),
    	POSITION_IN_ORGANIZATION = new URIImpl(NS + "positionInOrganization"),
    	POSITION_FOR_PERSON = new URIImpl(NS + "positionForPerson"),
    	POSITION = new URIImpl(NS + "Position"),
    	
    	PREFERRED_TITLE = new URIImpl(NS + "preferredTitle"),
    	ROLE_OF = new URIImpl(NS + "roleOf"),
    	HAS_ROLE = new URIImpl(NS + "hasRole"),
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
		CURRENT_MEMBER_OF = new URIImpl(NS + "currentMemberOf"),
    	EMAIL = new URIImpl(NS + "email");
}