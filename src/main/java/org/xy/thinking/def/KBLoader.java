package org.xy.thinking.def;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xy.model.KBSection;
import org.xy.thinking.ThinkingBrain;
import org.xy.thinking.ThinkingDiagnosis;
import org.xy.thinking.diagnosis.SectionUtils;

public abstract class KBLoader {
    private static final Logger log = LoggerFactory.getLogger(KBLoader.class);
	private static KBDefinitionMap definitions =  new KBDefinitionMap();
	public static KBDefinitionMap getDefinitions() {
		return definitions;
	}
	public static void loadDKDFromFileSystem(String path) {
		File file = new File(path);
		File files[] = file.listFiles();

		for (File f: files) {
			if (f.isDirectory()==false) 
			{				
				String filename = f.getPath();				
				loadDKDFromString(readFile(filename),0);
				log.debug("Load DKD from "+filename);
			}
		}
	}
	public static String readFile(String path) {
		StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(path));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s);
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
	}

    public static void loadDKDFromString(String contents, Integer mDate) {
    	KBFile def = new KBFile();
        def.parse(contents);
        def.setTimeStamp(mDate);
        def.setLastUpdate(System.currentTimeMillis());

        for (KBSection section: def.getSections()) {
        	String ftype = section.getFileType();
        	String code = SectionUtils.getFieldText(section.getDeclarationLine(), 1);
        	
        	definitions.put(ftype,code, section);
        }

    }


}
