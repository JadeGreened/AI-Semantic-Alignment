package de.uni_mannheim.informatik.dws.melt.demomatcher;

import java.util.Set;

public class Belief {
    private Set<PotentialCorrespondence> correspondences;
    private PotentialCorrespondence belief;
    public Belief(Set<PotentialCorrespondence> correspondences, PotentialCorrespondence belief){
        this.correspondences = correspondences;
        this.belief = belief;
    }
    public Set<PotentialCorrespondence> getCorrespondences() {
        return correspondences;
    }
    public PotentialCorrespondence getBelief() {
        return belief;
    }
}
