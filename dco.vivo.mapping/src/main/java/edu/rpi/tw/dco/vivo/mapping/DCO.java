package edu.rpi.tw.dco.vivo.mapping;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public interface DCO {
	static String NS = "http://deepcarbon.net/ontology/schema#";

    static URI
    	
    	DCO_ID = new URIImpl(NS + "dcoId");
}
