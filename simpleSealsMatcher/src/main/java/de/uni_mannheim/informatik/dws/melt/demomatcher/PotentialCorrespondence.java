package de.uni_mannheim.informatik.dws.melt.demomatcher;

import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.CorrespondenceRelation;
import org.apache.jena.ontology.OntClass;
import java.util.Objects;

public class PotentialCorrespondence {
    private OntClass source;
    private OntClass target;
    private CorrespondenceRelation relation;    // not used for now
    private OntologyAgent generator;
    private boolean isExamined;

    public PotentialCorrespondence(OntClass source, OntClass target, OntologyAgent generator){
        this.source = source;
        this.target = target;
        this.relation = CorrespondenceRelation.EQUIVALENCE;
        this.generator = generator;
        this.isExamined = false;
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

    public boolean isExamined() {
        return isExamined;
    }

    /***
     * This should only be called by the target agent.
     */
    public void setExamined() {
        isExamined = true;
    }

    public OntologyAgent getGenerator() {
        return generator;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PotentialCorrespondence that = (PotentialCorrespondence) o;
        return Objects.equals(source, that.source) && Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target);
    }
}
