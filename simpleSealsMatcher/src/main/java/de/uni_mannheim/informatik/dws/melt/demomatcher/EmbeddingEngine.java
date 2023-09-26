package de.uni_mannheim.informatik.dws.melt.demomatcher;


import com.fasterxml.jackson.core.JacksonException;
import io.metaloom.qdrant.client.http.impl.HttpErrorException;

import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;

import java.util.List;

/***
 * See this <a href="https://github.com/metaloom/qdrant-java-client#usage---http">read me</a>.
 */
public class EmbeddingEngine {

    private static Object client;   // TODO: choose a vector database

    public void initOntology(OntModel ontology) throws JacksonException, HttpErrorException {
        // TODO: init database

        // TODO: init ontology
    }

    public void getOneEntityNotNegotiated() {
        // TODO: return one entity that has not been negotiated
    }

    public List<OntClass> getSimilar(OntClass component, float threshold){
        //TODO: return all similar components above a threshold
        return null;
    }


}