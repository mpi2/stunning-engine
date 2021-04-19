package org.gentar.biology.targ_rep.es_cell;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.gentar.BaseEntity;
import org.gentar.biology.targ_rep.allele.TargRepAllele;
import org.gentar.biology.targ_rep.es_cell.mutation_subtype.TargRepEsCellMutationSubtype;
import org.gentar.biology.targ_rep.ikmc_project.TargRepIkmcProject;
import org.gentar.biology.targ_rep.pipeline.TargRepPipeline;
import org.gentar.biology.targ_rep.strain.TargRepStrain;
import org.gentar.biology.targ_rep.targeting_vector.TargRepTargetingVector;
import org.gentar.organization.work_unit.WorkUnit;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor(access= AccessLevel.PUBLIC, force=true)
@Data
@Entity
public class TargRepEsCell extends BaseEntity
{
    @Id
    @SequenceGenerator(name = "targRepEsCellSeq", sequenceName = "TARG_REP_ES_CELL_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "targRepEsCellSeq")
    private Long id;

    @ToString.Exclude
    @NotNull
    @ManyToOne(targetEntity= TargRepAllele.class)
    private TargRepAllele allele;

    @ToString.Exclude
    @NotNull
    @ManyToOne(targetEntity= TargRepTargetingVector.class)
    private TargRepTargetingVector targetingVector;

    private String parentalCellLine;

    @NotNull
    @Column(unique=true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String comment;

    // Name for the ikmc project, if it's a new project name the system creates a new entry in
    // the targ_rep_ikmc_project table.
    private String ikmcProjectName;

    @NotNull
    @ManyToOne(targetEntity= TargRepPipeline.class)
    private TargRepPipeline pipeline;

    @Column(columnDefinition = "boolean default true")
    private Boolean reportToPublic;

    @NotNull
    @ManyToOne(targetEntity= TargRepStrain.class)
    private TargRepStrain strain;

    private String userQcMapTest;

    private String userQckaryotype;

    private String userQcTvBackboneAssay;

    private String userQcLoxpConfirmation;

    private String userQcSouthernBlot;

    private String userQcLossOfWtAllele;

    private String userQcNewCountQpcr;

    private String userQcLaczSrPcr;

    private String userQcMutantSpecificSrPcr;

    private String userQcFivePrimeCassetteIntegrity;

    private String userQcNeoSrPcr;

    private String userQcFivePrimeLrPcr;

    private String userQcThreePrimeLrPcr;

    @Column(columnDefinition = "TEXT")
    private String userQcComment;

    @ManyToOne(targetEntity= TargRepEsCellMutationSubtype.class)
    private TargRepEsCellMutationSubtype mutationSubtype;

    private Boolean productionCentreAutoUpdate;

    private String userQcLoxpSrpcrAndSequencing;

    private String userQcKaryotypeSpread;

    private String userQcKaryotypePcr;

    @ManyToOne(targetEntity= WorkUnit.class)
    private WorkUnit userQcMouseClinicId;

    private String userQcChr1;

    private String userQcChr11;

    private String userQcChr8;

    private String userQcChry;

    private String qcLaczQpcr;

    @ManyToOne(targetEntity= TargRepIkmcProject.class)
    private TargRepIkmcProject ikmcProject;
}

