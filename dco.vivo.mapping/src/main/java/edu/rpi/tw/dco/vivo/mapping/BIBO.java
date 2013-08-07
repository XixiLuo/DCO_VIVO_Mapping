package edu.rpi.tw.dco.vivo.mapping;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public interface BIBO {
	static String NS = "http://purl.org/ontology/bibo/";

    static URI
    	
    	PREFIX_NAME = new URIImpl(NS + "prefixName");
}
