/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;

import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.PepfarEarlyRetentionQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Defines @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition} for pepfar early
 * indicator report
 */

@Component
public class Eri3monthsCohortQueries {
	
	@Autowired
	private HivMetadata hivMetadata;
	
	@Autowired
	private TxNewCohortQueries txNewCohortQueries;
	
	@Autowired
	private TxPvlsCohortQueries txPvlsCohortQueries;
	
	/**
	 * Get all patients who have 3 months ART retention after initiation
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPatientsRetainedOnArtForXMonthsFromArtInitiation() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("patientsRetentionFor3MonthsOnART");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location" + "", "Location", Location.class));
		cd.setQuery(PepfarEarlyRetentionQueries.getPatientsRetainedOnArt3MonthsAfterArtInitiation(hivMetadata
		        .getARVPharmaciaEncounterType().getEncounterTypeId(), hivMetadata.getAdultoSeguimentoEncounterType()
		        .getEncounterTypeId(), hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
		    hivMetadata.getARVPlanConcept().getConceptId(), hivMetadata.getstartDrugsConcept().getConceptId(), hivMetadata
		            .gethistoricalDrugStartDateConcept().getConceptId(), hivMetadata.getARTProgram().getProgramId(),
		    hivMetadata.getTransferredFromOtherHealthFacilityWorkflowState().getProgramWorkflowStateId()));
		return cd;
	}
	
	/**
	 * Get pregnant women who ahave more than 3 months retention on ART
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPregnantWomenRetainedOnArtFor3MonthsFromArtInitiation() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Pregnant women retain on ART for more than 3 months from ART initiation date");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location" + "", "Location", Location.class));
		cd.addSearch("all", EptsReportUtils.map(getPatientsRetainedOnArtForXMonthsFromArtInitiation(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("pregnant", EptsReportUtils.map(txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.setCompositionString("all AND pregnant");
		return cd;
		
	}
	
	/**
	 * Get breastfeeding women who have more than 3 months ART retention
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getBreastfeedingWomenRetainedOnArtFor3MonthsFromArtInitiation() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Breastfeeding women retain on ART for more than 3 months from ART initiation date");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location" + "", "Location", Location.class));
		cd.addSearch("all", EptsReportUtils.map(getPatientsRetainedOnArtForXMonthsFromArtInitiation(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("breastfeeding", EptsReportUtils.map(txNewCohortQueries.getTxNewBreastfeedingComposition(),
		    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
		cd.setCompositionString("all AND breastfeeding");
		return cd;
		
	}
	
	/**
	 * Get Children (0-14, excluding pregnant and breastfeeding women)
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getChildrenRetaineOnArtFor3MonthsFromArtInitiation() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Children having ART retention for than 3 months");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location" + "", "Location", Location.class));
		cd.addSearch("all", EptsReportUtils.map(getPatientsRetainedOnArtForXMonthsFromArtInitiation(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("children", EptsReportUtils.map(txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(0, 14),
		    "endDate=${endDate},location=${location}"));
		cd.addSearch("pregnant", EptsReportUtils.map(getPregnantWomenRetainedOnArtFor3MonthsFromArtInitiation(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("breastfeeding", EptsReportUtils.map(getBreastfeedingWomenRetainedOnArtFor3MonthsFromArtInitiation(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.setCompositionString("all AND children AND NOT(pregnant OR breastfeeding)");
		return cd;
	}
	
	/**
	 * Get Adults (14+, excluding pregnant and breastfeeding women)
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getAdultsRetaineOnArtFor3MonthsFromArtInitiation() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Adults having ART retention for than 3 months");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location" + "", "Location", Location.class));
		cd.addSearch("all", EptsReportUtils.map(getPatientsRetainedOnArtForXMonthsFromArtInitiation(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("adults", EptsReportUtils.map(txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(15, 200),
		    "endDate=${endDate},location=${location}"));
		cd.addSearch("pregnant", EptsReportUtils.map(getPregnantWomenRetainedOnArtFor3MonthsFromArtInitiation(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("breastfeeding", EptsReportUtils.map(getBreastfeedingWomenRetainedOnArtFor3MonthsFromArtInitiation(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.setCompositionString("all AND adults AND NOT(pregnant OR breastfeeding)");
		return cd;
	}
}
