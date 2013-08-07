package edu.rpi.tw.dco.vivo.mapping;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public interface VIVO_CORE {

	static String NS = "http://vivoweb.org/ontology/core#";

    static URI
    	FAX_NUMBER = new URIImpl(NS + "faxNumber"),
    	PHONE_NUMBER = new URIImpl(NS + "phoneNumber"),
    	URL_LINK = new URIImpl(NS + "URLLink"),
    	WEB_PAGE = new URIImpl(NS + "webpage"),
		CURRENT_MEMBER_OF = new URIImpl(NS + "currentMemberOf"),
    	EMAIL = new URIImpl(NS + "email");
}
