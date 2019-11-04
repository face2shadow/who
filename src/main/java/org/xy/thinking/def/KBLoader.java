package org.xy.thinking.def;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.xy.model.KBSection;
import org.xy.thinking.diagnosis.SectionUtils;

public abstract class KBLoader {
	private static KBDefinitionMap definitions = new KBDefinitionMap();

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
			}
		}
	}

	public static String readFile(String path) {
		StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));
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
	public static void loadDKDFromString(String contents, long mDate) {
		KBFile def = new KBFile();
		def.parse(contents);

		def.setTimeStamp(mDate);
		def.setLastUpdate(System.currentTimeMillis());

		for (KBSection section : def.getSections()) {
			String ftype = section.getFileType();
			if (section.getDeclarationLine()==null) {
				System.out.println("No Declaration Line...\n" +contents);
			}
			String code = SectionUtils.getFieldText(section.getDeclarationLine(), 1);

			definitions.put(ftype, code, section);
		}

	}
	
}
