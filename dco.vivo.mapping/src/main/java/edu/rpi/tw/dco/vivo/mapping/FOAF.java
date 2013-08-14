package edu.rpi.tw.dco.vivo.mapping;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public interface FOAF {
    static String NS = "http://xmlns.com/foaf/0.1/";

    static URI
    	SKYPE_ID = new URIImpl(NS + "skypeID"),
    	ORGANIZATION = new URIImpl(NS + "Organization"),
    	LAST_NAME = new URIImpl(NS + "lastName"),
		FIRST_NAME = new URIImpl(NS + "firstName"),
    	NAME = new URIImpl(NS + "name"),
    	NICK = new URIImpl(NS + "nick"),
    	PERSON = new URIImpl(NS + "Person");
}
