package org.xy.thinking.diagnosis;

public abstract class DiagnosisConstants {
    public static final String MALE_CODE = "103";
    public static final String FEMALE_CODE = "104";
    public static final String AGE_M_CODE = "106";
    public final static String QUESTION_PREFIX = "EXP|";
    public final static String TOPIC_DIG_IN_PROCESS = "DIG_IN_PR";
    public final static String TOPIC_DIF_IN_PROCESS = "DIF_IN_PR";
    public final static String TOPIC_EMR_IN_PROCESS = "EMR_IN_PR";
    public final static String TOPIC_DIG_COMPLETE = "DIG_CMP";

    public static final int STAGE_PAT_SURVEY_IN_PROCESS = 0;
    public static final int STAGE_PAT_SURVEY_FINISHED = 6;
    //public static final int STAGE_VISIT_SURVEY_IN_PROCESS = 3;
    public static final int STAGE_VISIT_SURVEY_FINISHED = 5;
    public static final int STAGE_DIAGNOSIS_IN_PROCESS = 1;
    public static final int STAGE_DIAGNOSIS_FINISHED = 2;
    //public static final int STAGE_HISTORY_SURVEY_IN_PROCESS = 4;
    public static final int STAGE_HISTORY_SURVEY_FINISHED = 7;
    public static final int STAGE_DIAGNOSIS_DIFFERENTIAL_IN_PROCESS = 8;
    public static final int STAGE_DIAGNOSIS_DIFFERENTIAL_FINISHED = 9;
    public static final int STAGE_PRESCRIPTION_FINISHED = 10;
    public static final int STAGE_PURCHASE_FINISHED = 11;
    public static final int STAGE_MISSION_COMPLETED = 4096;
}
