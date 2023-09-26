package de.uni_mannheim.informatik.dws.melt.demomatcher;

import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.CorrespondenceRelation;
import org.apache.jena.ontology.OntClass;
import java.util.Objects;

public class PotentialCorrespondence {
    private OntClass source;
    private OntClass target;
    private CorrespondenceRelation relation;
    private OntologyAgent generator;

    public PotentialCorrespondence(OntClass source, OntClass target, CorrespondenceRelation relation, OntologyAgent generator){
        this.source = source;
        this.target = target;
        this.relation = relation;
        this.generator = generator;
    }
    public OntClass getSource() {
        return source;
    }

    public OntClass getTarget() {
        return target;
    }

    public CorrespondenceRelation getRelation() {
        return relation;
    }
    public OntologyAgent getGenerator() {
        return generator;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PotentialCorrespondence that = (PotentialCorrespondence) o;
        return Objects.equals(source, that.source) && Objects.equals(target, that.target) && Objects.equals(generator, that.generator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target, generator);
    }
}
