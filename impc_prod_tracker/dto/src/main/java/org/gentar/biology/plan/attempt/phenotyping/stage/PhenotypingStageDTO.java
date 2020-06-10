package org.gentar.biology.plan.attempt.phenotyping.stage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.gentar.biology.status_stamps.StatusStampsDTO;
import org.gentar.common.state_machine.StatusTransitionDTO;
import java.time.LocalDate;
import java.util.List;

@Data
@RequiredArgsConstructor
public class PhenotypingStageDTO
{
    private Long id;
    private String psn;
    private LocalDate phenotypingExperimentsStarted;
    private Boolean doNotCountTowardsCompleteness;
    private LocalDate initialDataReleaseDate;
    private String statusName;
    private String phenotypingTypeName;
    private String phenotypingExternalRef;

    @JsonProperty("statusDates")
    private List<StatusStampsDTO> statusStampsDTOS;

    @JsonProperty("tissueDistributions")
    private List<TissueDistributionDTO> tissueDistributionCentreDTOs;

    @JsonProperty("statusTransition")
    private StatusTransitionDTO statusTransitionDTO;
}