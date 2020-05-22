/******************************************************************************
 Copyright 2019 EMBL - European Bioinformatics Institute

 Licensed under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License. You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 either express or implied. See the License for the specific
 language governing permissions and limitations under the
 License.
 */
package org.gentar.biology.project;

import org.gentar.audit.history.History;
import org.gentar.biology.ChangeResponse;
import org.gentar.biology.plan.*;
import org.gentar.biology.plan.mappers.PlanMinimumCreationMapper;
import org.gentar.biology.plan.type.PlanTypeName;
import org.gentar.biology.project.mappers.ProjectCreationMapper;
import org.gentar.biology.project.mappers.ProjectCsvRecordMapper;
import org.gentar.biology.project.mappers.ProjectResponseMapper;
import org.gentar.exceptions.UserOperationFailedException;
import org.gentar.helpers.CsvWriter;
import org.gentar.helpers.PlanLinkBuilder;
import org.gentar.helpers.LinkUtil;
import org.gentar.common.history.HistoryDTO;
import org.gentar.audit.history.HistoryMapper;
import org.gentar.helpers.ProjectCsvRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.gentar.biology.project.search.filter.ProjectFilter;
import org.gentar.biology.project.search.filter.ProjectFilterBuilder;
import org.gentar.biology.project.helper.ProjectUtilities;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
class ProjectController
{
    private ProjectService projectService;
    private HistoryMapper historyMapper;
    private CsvWriter<ProjectCsvRecord> csvWriter;
    private ProjectCsvRecordMapper projectCsvRecordMapper;
    private UpdateProjectRequestProcessor updateProjectRequestProcessor;
    private PlanService planService;
    private ProjectCreationMapper projectCreationMapper;
    private PlanMinimumCreationMapper planMinimumCreationMapper;
    private ProjectResponseMapper projectResponseMapper;


    private static final String PROJECT_NOT_FOUND_ERROR =
        "Project %s does not exist or you don't have access to it.";

    ProjectController(
        ProjectService projectService,
        HistoryMapper historyMapper,
        CsvWriter<ProjectCsvRecord> csvWriter,
        ProjectCsvRecordMapper projectCsvRecordMapper,
        UpdateProjectRequestProcessor updateProjectRequestProcessor,
        PlanService planService,
        ProjectCreationMapper projectCreationMapper,
        PlanMinimumCreationMapper planMinimumCreationMapper,
        ProjectResponseMapper projectResponseMapper)
    {
        this.projectService = projectService;
        this.historyMapper = historyMapper;
        this.csvWriter = csvWriter;
        this.projectCsvRecordMapper = projectCsvRecordMapper;
        this.updateProjectRequestProcessor = updateProjectRequestProcessor;
        this.planService = planService;
        this.projectCreationMapper = projectCreationMapper;
        this.planMinimumCreationMapper = planMinimumCreationMapper;
        this.projectResponseMapper = projectResponseMapper;
    }

    /**
     * Get all the projects in the system.
     * @return A collection of {@link ProjectDTO} objects.
     */
    @GetMapping
    public ResponseEntity findAll(
        Pageable pageable,
        PagedResourcesAssembler assembler,
        @RequestParam(value = "tpns", required = false) List<String> tpns,
        @RequestParam(value = "markerSymbols", required = false) List<String> markerSymbols,
        @RequestParam(value = "genes", required = false) List<String> genes,
        @RequestParam(value = "intentions", required = false) List<String> intentions,
        @RequestParam(value = "workUnitNames", required = false) List<String> workUnitNames,
        @RequestParam(value = "workGroupNames", required = false) List<String> workGroupNames,
        @RequestParam(value = "consortiaNames", required = false) List<String> consortia,
        @RequestParam(value = "assignmentNames", required = false) List<String> assignmentNames,
        @RequestParam(value = "summaryStatusNames", required = false) List<String> summaryStatusNames,
        @RequestParam(value = "privacyNames", required = false) List<String> privaciesNames,
        @RequestParam(value = "externalReferences", required = false) List<String> externalReferences,
        @RequestParam(value = "imitsMiPlanIds", required = false) List<String> imitsMiPlans)
    {
        ProjectFilter projectFilter = ProjectFilterBuilder.getInstance()
            .withTpns(tpns)
            .withMarkerSymbols(markerSymbols)
            .withIntentions(intentions)
            .withGenes(genes)
            .withAssignments(assignmentNames)
            .withSummaryStatusNames(summaryStatusNames)
            .withPrivacies(privaciesNames)
            .withWorkUnitNames(workUnitNames)
            .withConsortiaNames(consortia)
            .withWorkGroupNames(workGroupNames)
            .withExternalReference(externalReferences)
            .withImitsMiPlanId(imitsMiPlans)
            .build();
        Page<Project> projectsPage = projectService.getProjects(projectFilter, pageable);
        Page<ProjectResponseDTO> projectDtos = projectsPage.map(this::getDTO);
        PagedModel pr =
            assembler.toModel(
                projectDtos,
                linkTo(ProjectController.class).withSelfRel());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Link", LinkUtil.createLinkHeader(pr));

        return new ResponseEntity<>(pr, responseHeaders, HttpStatus.OK);
    }

    /**
     * Creates a new project in the system.
     * @param projectCreationDTO Request with data of the project to be created.
     * @return {@link ProjectDTO} representing the project created in the system.
     */
    @PostMapping
    public ChangeResponse createProject(@RequestBody ProjectCreationDTO projectCreationDTO)
    {
        Project projectToBeCreated = projectCreationMapper.toEntity(projectCreationDTO);
        Project createdProject = projectService.createProject(projectToBeCreated);
        Plan planToBeCreated = planMinimumCreationMapper.toEntity(
            projectCreationDTO.getPlanMinimumCreationDTO());
        projectService.associatePlanToProject(planToBeCreated, createdProject);
        planService.createPlan(planToBeCreated);
        return buildChangeResponse(createdProject);
    }

    private ChangeResponse buildChangeResponse(Project project)
    {
        List<HistoryDTO> historyList = historyMapper.toDtos(projectService.getProjectHistory(project));
        return buildChangeResponse(project, historyList);
    }
    private ChangeResponse buildChangeResponse(Project project, List<HistoryDTO> historyList)
    {
        ChangeResponse changeResponse = new ChangeResponse();
        changeResponse.setHistoryDTOs(historyList);

        changeResponse.add(linkTo(ProjectController.class).slash(project.getTpn()).withSelfRel());
        return changeResponse;
    }

    @PutMapping(value = {"/{tpn}"})
    public HistoryDTO updateProject(@PathVariable String tpn, @RequestBody ProjectDTO projectDTO)
    {
        Project currentProject = projectService.getNotNullProjectByTpn(tpn);
        Project newProject = getProjectToUpdate(currentProject, projectDTO);
        History history = projectService.updateProject(currentProject, newProject);
        HistoryDTO historyDTO = new HistoryDTO();
        if (history != null)
        {
            historyDTO = historyMapper.toDto(history);
        }
        return historyDTO;
    }

    private Project getProjectToUpdate(Project currentProject, ProjectDTO projectDTO)
    {
        Project newProject = new Project(currentProject);
        return updateProjectRequestProcessor.getProjectToUpdate(newProject, projectDTO);
    }

    /**
     * Get all the projects in the system.
     * @return A collection of {@link ProjectDTO} objects.
     */
    @GetMapping("/exportProjects")
    public void exportProjects(
        HttpServletResponse response,
        @RequestParam(value = "tpns", required = false) List<String> tpns,
        @RequestParam(value = "markerSymbols", required = false) List<String> markerSymbols,
        @RequestParam(value = "genes", required = false) List<String> genes,
        @RequestParam(value = "intentions", required = false) List<String> intentions,
        @RequestParam(value = "workUnitNames", required = false) List<String> workUnitNames,
        @RequestParam(value = "workGroupNames", required = false) List<String> workGroupNames,
        @RequestParam(value = "consortiaNames", required = false) List<String> consortia,
        @RequestParam(value = "statuses", required = false) List<String> statuses,
        @RequestParam(value = "privacyNames", required = false) List<String> privaciesNames,
        @RequestParam(value = "externalReferences", required = false) List<String> externalReferences) throws IOException
    {
        ProjectFilter projectFilter = ProjectFilterBuilder.getInstance()
            .withTpns(tpns)
            .withMarkerSymbols(markerSymbols)
            .withIntentions(intentions)
            .withGenes(genes)
            .withAssignments(statuses)
            .withPrivacies(privaciesNames)
            .withWorkUnitNames(workUnitNames)
            .withWorkGroupNames(workGroupNames)
            .withConsortiaNames(consortia)
            .withExternalReference(externalReferences)
            .build();
        var projectsPage = projectService.getProjects(projectFilter);
        List<ProjectCsvRecord> projectCsvRecords = projectCsvRecordMapper.toDtos(projectsPage);
        csvWriter.writeListToCsv(response.getWriter(), projectCsvRecords, ProjectCsvRecord.HEADERS);
    }

    private ProjectResponseDTO getDTO(Project project)
    {
        ProjectResponseDTO projectResponseDTO = null;
        if (project != null)
        {
            projectResponseDTO = projectResponseMapper.toDto(project);
            projectResponseDTO.add(
                PlanLinkBuilder.buildPlanLinks(project, PlanTypeName.PRODUCTION, "productionPlans"));
            projectResponseDTO.add(
                PlanLinkBuilder.buildPlanLinks(project, PlanTypeName.PHENOTYPING, "phenotypingPlans"));
        }
        return projectResponseDTO;
    }

    /**
     * Get a specific project.
     * @param tpn tpn Project identifier.
     * @return Entity with the project information.
     */
    @GetMapping(value = {"/{tpn}"})
    public EntityModel<?> findOne(@PathVariable String tpn)
    {
        EntityModel<ProjectResponseDTO> entityModel = null;
        Project project = projectService.getProjectByTpn(tpn);
        ProjectResponseDTO projectResponseDTO = getDTO(project);

        if (projectResponseDTO != null)
        {
            entityModel = new EntityModel<>(projectResponseDTO);
        }
        else
        {
            throw new UserOperationFailedException(String.format(PROJECT_NOT_FOUND_ERROR, tpn));
        }

        return entityModel;
    }

    @GetMapping(value = {"/{tpn}/history"})
    public List<HistoryDTO> getProjectHistory(@PathVariable String tpn)
    {
        Project project = ProjectUtilities.getNotNullProjectByTpn(tpn);

        return historyMapper.toDtos(projectService.getProjectHistory(project));
    }
}
