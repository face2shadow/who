package org.xy.thinking.mem;

public class DSMConstants {
	// 标识
	public static final String POSITIVE_FLAG = "+"; // 肯定
	public static final String NEGATIVE_FLAG = "-"; // 否定
	public static final String UNKNOW_FLAG = "*"; // 未知
	
	public static final String DSM_ACTION_STATUS_Y = "1";
	public static final String DSM_ACTION_STATUS_N = "0";

	//
	public static final String DRE_DIAGNOSIS_DATA = "DIAGNOSIS"; // 回写WORKBENCH
	public static final String DRE_VISIT_DATA = "VISIT"; // 启动时写入CONSULTID、APPID、ACCESSENTRANCESUBCODE、DIGTYPE、LONGITUDE、LATITUDE、APPUSERID、LOCATIONTYPE、ACCESSENTRANCECODE、DOCTORID
	public static final String DRE_PATIENT_DATA = "PATIENT"; // 启动时写入PATIENTID、AGE、SEX
	public static final String DRE_SYMPTOM_DATA = "SYMPTOM"; // 问答时的写入
	public static final String DRE_CHIEF_COMPLAINT_DATA = "CHIEF_COMPLAINT"; // 主诉时写入
	
	public static final String DSM_NODE_CREATOR = "RECSYS-RBT";
	public static final String DSM_SESSION_APPNAME = "DM";
	
	public static final String DSM_SYMPTOMS_UNKNOW_NAME = "DM0000";
	public static final String DSM_SYMPTOMS_UNKNOW_CODE = "DM0000";
	public static final String DSM_NODE_SYMPTOM  = "Symptom";
	public static final String DSM_NODE_PROPERTY = "Property";

	public static final String PRESCRIPTION_NODEPATH = "/DIALOG/PATIENT/VISIT/PRESCRIPTION";
	public static final String ORDER_NODEPATH = "/DIALOG/PATIENT/VISIT/ORDER";
	public static final String DSM_ACTION_PATH = "/DIALOG/ACTION";
	public static final String DSM_TOPIC_PATH = "/DIALOG/PACKAGE/TOPIC";
	public static final String DSM_TOPIC_FINISHED_PATH = "/DIALOG/PACKAGE/TOPIC_FINISHED";
	public static final String DSM_DISEASES_CONFIRM_PATH = "/DIALOG/PATIENT/VISIT/DISEASES/DIAG_CONFIRM";
	public static final String DSM_DISEASES_DIAG_POSSIBLE_PATH = "/DIALOG/PATIENT/VISIT/DISEASES/DIAG_POSSIBLE";
	public static final String DSM_DISEASES_WORKBENCH_PATH = "/DIALOG/PATIENT/VISIT/DISEASES/DIAG_WORKBENCH";
	public static final String DSM_SYMPTOMS_PATH = "/DIALOG/PATIENT/VISIT/SYMPTOMS";
	public static final String DSM_DISEASES_POSSIBLE_PATH = "/DIALOG/PATIENT/VISIT/DISEASES/POSSIBLE";
	public static final String DSM_PATIENT_PATH = "/DIALOG/PATIENT";
	public static final String DSM_PATIENT_VIST_PATH = "/DIALOG/PATIENT/VISIT";

	public static final String JSON_PROP = "JSON";
	public static final String TLK_BEFORE_PROP = "TLK_BEFORE";
	public static final String TLK_AFTER_PROP = "TLK_AFTER";
	public static final String WH_STATUS_PROP = "WH_STATUS";
	public static final String TLK_PROP = "TLK";
	public static final String NAME_PROP = "name";
	public static final String VALUE_PROP = "value";

	public static final String AI_FEATURE = "aiFeature";
	public static final String SUPPORT_AI_FEATURES = "supportAIFeatures";
	public static final String TEMPLATE_SUMMARY = "templateSummary";
	
	public static final String TRANSFER_CONSULT_ID = "TRANSFERCONSULTID";
	public static final String TRANSFER_DOCTOR_ID = "TRANSFERDOCTORID";
	public static final String DEPT = "DEPT";
	public static final String PATIENT_ID = "PATIENTID";
	public static final String DIG_TYPE = "DIGTYPE";
	public static final String WH_SOURCE = "WH_SOURCE";
	
	public static final String ONESHOT = "ONESHOT";
	public static final String DEFAULT = "DEFAULT";
	
	public static final String AGE = "AGE";
	public static final String AGE_CODE = "106";
	
	public static final String MAN_VAL = "0";
	public static final String MAN = "man";
	public static final String MAN_CODE = "103";
	
	public static final String WOMAN_VAL = "1";
	public static final String WOMAN = "woman";
	public static final String WOMAN_CODE = "104";
	
	
	public static final String WM ="WM"; // 西医
	public static final String TCM = "TCM"; // 中医
}