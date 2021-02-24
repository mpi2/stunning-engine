package org.gentar.biology.plan.attempt;

import org.gentar.biology.plan.Plan;

public class AttemptTypeChecker
{
    public static final String CRISPR_TYPE = "crispr";
    public static final String PHENOTYPING_TYPE = "adult and embryo phenotyping";
    public static final String NOT_DEFINED_TYPE = "Not defined";

    public static String getAttemptTypeName(Plan plan)
    {
        return plan.getAttemptType() == null ? NOT_DEFINED_TYPE : plan.getAttemptType().getName();
    }
}
