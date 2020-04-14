package org.gentar.biology.plan.attempt.phenotyping;

import org.gentar.EntityMapper;
import org.gentar.Mapper;
import org.gentar.biology.plan.attempt.phenotyping.stage.PhenotypingStage;
import org.gentar.biology.plan.attempt.phenotyping.stage.engine.events.EarlyAdultEvent;
import org.gentar.biology.plan.attempt.phenotyping.stage.engine.events.EarlyHaploessentialEvent;
import org.gentar.biology.plan.attempt.phenotyping.stage.engine.events.LateAdultEvent;
import org.gentar.biology.plan.attempt.phenotyping.stage.engine.events.LateHaploessentialEvent;
import org.gentar.biology.plan.attempt.phenotyping.stage.engine.state.EarlyAdultPhenotypingStageState;
import org.gentar.biology.plan.attempt.phenotyping.stage.engine.state.EarlyHaploessentialPhenotypingStageState;
import org.gentar.biology.plan.attempt.phenotyping.stage.engine.state.LateAdultPhenotypingStageState;
import org.gentar.biology.plan.attempt.phenotyping.stage.engine.state.LateHaploessentialPhenotypingStageState;
import org.gentar.biology.plan.attempt.phenotyping.stage.status_stamp.PhenotypingStageStatusStamp;
import org.gentar.biology.plan.attempt.phenotyping.stage.type.PhenotypingStageType;
import org.gentar.biology.status.StatusMapper;
import org.gentar.biology.status_stamps.StatusStampsDTO;
import org.gentar.common.state_machine.StatusTransitionDTO;
import org.gentar.common.state_machine.TransitionDTO;
import org.gentar.statemachine.EnumStateHelper;
import org.gentar.statemachine.ProcessEvent;
import org.gentar.statemachine.ProcessState;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PhenotypingStageMapper implements Mapper<PhenotypingStage, PhenotypingStageDTO> {
    private EntityMapper entityMapper;
    private TissueDistributionMapper tissueDistributionMapper;
    private StatusMapper statusMapper;
    private PhenotypingStageTypeMapper phenotypeStageTypeMapper;

    public PhenotypingStageMapper(
            EntityMapper entityMapper,
            TissueDistributionMapper tissueDistributionMapper,
            StatusMapper statusMapper,
            PhenotypingStageTypeMapper phenotypeStageTypeMapper
    ) {
        this.entityMapper = entityMapper;
        this.tissueDistributionMapper = tissueDistributionMapper;
        this.statusMapper = statusMapper;
        this.phenotypeStageTypeMapper = phenotypeStageTypeMapper;
    }

    @Override
    public PhenotypingStageDTO toDto(PhenotypingStage phenotypingStage) {
        PhenotypingStageDTO phenotypingStageDTO = entityMapper.toTarget(phenotypingStage, PhenotypingStageDTO.class);
        phenotypingStageDTO.setPhenotypingTypeName(phenotypeStageTypeMapper.toDto(phenotypingStage.getPhenotypingStageType()));
        phenotypingStageDTO.setStatusName(phenotypingStage.getStatus().getName());
        addStatusStamps(phenotypingStageDTO, phenotypingStage);
        phenotypingStageDTO.setTissueDistributionCentreDTOs(tissueDistributionMapper.toDtos(phenotypingStage.getTissueDistributions()));
        phenotypingStageDTO.setStatusTransitionDTO(buildStatusTransitionDTO(phenotypingStage));
        return phenotypingStageDTO;
    }

    private void addStatusStamps(PhenotypingStageDTO phenotypingStageDTO, PhenotypingStage phenotypingStage) {
        Set<PhenotypingStageStatusStamp> statusStamps = phenotypingStage.getPhenotypingStageStatusStamps();
        List<StatusStampsDTO> statusStampsDTOS = new ArrayList<>();
        if (statusStamps != null) {
            statusStamps.forEach(x -> {
                StatusStampsDTO statusStampsDTO = new StatusStampsDTO();
                statusStampsDTO.setStatusName(x.getStatus().getName());
                statusStampsDTO.setDate(x.getDate());
                statusStampsDTOS.add(statusStampsDTO);
            });
        }
        statusStampsDTOS.sort(Comparator.comparing(StatusStampsDTO::getDate));
        phenotypingStageDTO.setStatusStampsDTOS(statusStampsDTOS);
    }

    @Override
    public PhenotypingStage toEntity(PhenotypingStageDTO dto) {
        PhenotypingStage phenotypingStage = entityMapper.toTarget(dto, PhenotypingStage.class);
        setPhenotypingStageType(phenotypingStage, dto);
        setStatus(phenotypingStage, dto);
        setTissueDistributionCentre(phenotypingStage, dto);
        return phenotypingStage;
    }

    private void setPhenotypingStageType(PhenotypingStage phenotypingStage, PhenotypingStageDTO phenotypingStageDTO) {
        phenotypingStage.setPhenotypingStageType(phenotypeStageTypeMapper.toEntity(phenotypingStageDTO.getPhenotypingTypeName()));
    }

    private void setStatus(PhenotypingStage phenotypingStage, PhenotypingStageDTO phenotypingStageDTO) {
        phenotypingStage.setStatus(statusMapper.toEntity(phenotypingStageDTO.getStatusName()));
    }

    private void setTissueDistributionCentre(PhenotypingStage phenotypingStage, PhenotypingStageDTO phenotypingStageDTO) {
//        TissueDistribution tissueDistributions = tissueDistributionMapper.toEntities(phenotypingStageDTO.getTissueDistributionCentreDTOs());
//        phenotypingStage.setTissueDistributions(tissueDistributions);
    }

    @Override
    public Collection<PhenotypingStage> toEntities(Collection<PhenotypingStageDTO> dtos) {
        return null;
    }


    private StatusTransitionDTO buildStatusTransitionDTO(PhenotypingStage phenotypingStage) {
        StatusTransitionDTO statusTransitionDTO = new StatusTransitionDTO();
        statusTransitionDTO.setCurrentStatus(phenotypingStage.getStatus().getName());
        statusTransitionDTO.setTransitions(getTransitionsByPhenotypingStageType(phenotypingStage));
        return statusTransitionDTO;
    }

    private List<TransitionDTO> getTransitionsByPhenotypingStageType(PhenotypingStage phenotypingStage) {
        List<TransitionDTO> transitionDTOS = new ArrayList<>();
        String currentStatusName = phenotypingStage.getStatus().getName();
        PhenotypingStageType phenotypingStageType = phenotypingStage.getPhenotypingStageType();

        if (phenotypingStageType != null) {

            if (PhenotypingStageTypes.EARLY_ADULT.getTypeName().equalsIgnoreCase(phenotypingStageType.getName())) {
                setEarlyAdultPhenotypingStageTransitions(transitionDTOS, currentStatusName);
            } else if (PhenotypingStageTypes.LATE_ADULT.getTypeName().equalsIgnoreCase(phenotypingStageType.getName())) {
                setLateAdultPhenotypingStageTransitions(transitionDTOS, currentStatusName);
            } else if (PhenotypingStageTypes.EARLY_HAPLOESSENTIAL.getTypeName().equalsIgnoreCase(phenotypingStageType.getName())) {
                setEarlyHaploessentialPhenotypingStageTransitions(transitionDTOS, currentStatusName);
            } else if (PhenotypingStageTypes.LATE_HAPLOESSENTIAL.getTypeName().equalsIgnoreCase(phenotypingStageType.getName())) {
                setLateHaploessentialPhenotypingStageTransitions(transitionDTOS, currentStatusName);
            }

        }
        return transitionDTOS;
    }

    private void setEarlyAdultPhenotypingStageTransitions(List<TransitionDTO> transitionDTOS, String currentStatusName) {

        ProcessState phenotypingStageState = EarlyAdultPhenotypingStageState.getStateByInternalName(currentStatusName);
        if (phenotypingStageState != null) {
            List<ProcessEvent> phenotypingStageEvents = EnumStateHelper.getAvailableEventsByState(
                    EarlyAdultEvent.getAllEvents(), phenotypingStageState);

            setTransitions(transitionDTOS, phenotypingStageEvents);
        }
    }

    private void setLateAdultPhenotypingStageTransitions(List<TransitionDTO> transitionDTOS, String currentStatusName) {

        ProcessState phenotypingStageState = LateAdultPhenotypingStageState.getStateByInternalName(currentStatusName);
        if (phenotypingStageState != null) {
            List<ProcessEvent> phenotypingStageEvents = EnumStateHelper.getAvailableEventsByState(
                    LateAdultEvent.getAllEvents(), phenotypingStageState);

            setTransitions(transitionDTOS, phenotypingStageEvents);
        }
    }

    private void setEarlyHaploessentialPhenotypingStageTransitions(List<TransitionDTO> transitionDTOS, String currentStatusName) {

        ProcessState phenotypingStageState = EarlyHaploessentialPhenotypingStageState.getStateByInternalName(currentStatusName);
        if (phenotypingStageState != null) {
            List<ProcessEvent> phenotypingStageEvents = EnumStateHelper.getAvailableEventsByState(
                    EarlyHaploessentialEvent.getAllEvents(), phenotypingStageState);

            setTransitions(transitionDTOS, phenotypingStageEvents);
        }
    }

    private void setLateHaploessentialPhenotypingStageTransitions(List<TransitionDTO> transitionDTOS, String currentStatusName) {

        ProcessState phenotypingStageState = LateHaploessentialPhenotypingStageState.getStateByInternalName(currentStatusName);
        if (phenotypingStageState != null) {
            List<ProcessEvent> phenotypingStageEvents = EnumStateHelper.getAvailableEventsByState(
                    LateHaploessentialEvent.getAllEvents(), phenotypingStageState);

            setTransitions(transitionDTOS, phenotypingStageEvents);
        }
    }

    private void setTransitions(List<TransitionDTO> transitionDTOS, List<ProcessEvent> phenotypingStageEvents) {
        phenotypingStageEvents.forEach(x -> {
            TransitionDTO transition = new TransitionDTO();
            transition.setAction(x.getName());
            transition.setDescription(x.getDescription());
            transition.setNextStatus(x.getEndState().getName());
            transition.setNote(x.getTriggerNote());
            transition.setAvailable(x.isTriggeredByUser());
            transitionDTOS.add(transition);
        });
    }
}

