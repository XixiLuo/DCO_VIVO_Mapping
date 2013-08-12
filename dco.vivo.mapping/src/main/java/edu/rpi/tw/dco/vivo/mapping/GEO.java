package edu.rpi.tw.dco.vivo.mapping;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public interface GEO {
	static String NS = "http://aims.fao.org/aos/geopolitical.owl#";

    static URI
    	
    	HAS_NATIONALITY = new URIImpl(NS + "hasNationality");
}
