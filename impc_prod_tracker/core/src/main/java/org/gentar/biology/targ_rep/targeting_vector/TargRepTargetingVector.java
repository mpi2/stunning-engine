package org.gentar.biology.targ_rep.targeting_vector;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.gentar.BaseEntity;
import org.gentar.biology.targ_rep.allele.TargRepAllele;
import org.gentar.biology.targ_rep.ikmc_project.TargRepIkmcProject;
import org.gentar.biology.targ_rep.pipeline.TargRepPipeline;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor(access= AccessLevel.PUBLIC, force=true)
@Data
@Entity
public class TargRepTargetingVector extends BaseEntity
{
    @Id
    @SequenceGenerator(name = "targRepTargetingVectorSeq", sequenceName = "TARG_REP_TARGETING_VECTOR_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "targRepTargetingVectorSeq")
    private Long id;

    @ToString.Exclude
    @NotNull
    @ManyToOne(targetEntity= TargRepAllele.class)
    private TargRepAllele allele;

    @NotNull
    private String name;

    // Name for the ikmc project, if it's a new project name the system creates a new entry in
    // the targ_rep_ikmc_project table.
    private String ikmcProjectName;

    private String intermediateVector;

    @Column(columnDefinition = "boolean default true")
    private Boolean reportToPublic;

    @NotNull
    @ManyToOne(targetEntity= TargRepPipeline.class)
    private TargRepPipeline pipeline;

    @ManyToOne(targetEntity= TargRepIkmcProject.class)
    private TargRepIkmcProject ikmcProject;

    private String mgiAlleleNamePrediction;

    private String alleleTypePrediction;

    @Column(columnDefinition = "boolean default true")
    private Boolean productionCentreAutoUpdate;
}
