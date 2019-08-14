package org.xy.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;
//
//import com.huaban.analysis.jieba.JiebaSegmenter;
//import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
//import com.huaban.analysis.jieba.SegToken;
//import com.huaban.analysis.jieba.WordDictionary;

public class SplitWord {
	private static final Analyzer analyzer = new IKAnalyzer();
	public static List<String> ikCutWord(String s){
		StringReader re = new StringReader(s);
		IKSegmenter ik = new IKSegmenter(re,true);
		List<String> ret = new ArrayList<String>();
		 Lexeme lex = null;
		 try {
			while((lex=ik.next())!=null){
				//System.out.print(lex.getLexemeText()+" ");
				ret.add(lex.getLexemeText());
			}

			System.out.println(" ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TokenStream ts = analyzer.tokenStream("", s);/
		//CharTermAttri
		//while (ts.incrementToken()) {//
		//	System.out.println(ts.)
		//}
		return ret;
	}
//	public static void loadUserDict() {
//		File f = new File(
//				SplitWord.class.getClassLoader().getResource("dicts/jieba.txt").getPath()
//		);
//		System.out.println(f.getAbsolutePath());
//		Path path = Paths.get(f.getAbsolutePath());
//
//		WordDictionary.getInstance().loadUserDict(path);
//	}
//	public static List<String> split(String sentence){
//		List<String> ret = new ArrayList<String>();
//		JiebaSegmenter segmenter = new JiebaSegmenter();
//		List<SegToken> tokens = segmenter.process(sentence, SegMode.INDEX);
//		for (SegToken tk : tokens) {
//			
//			ret.add(tk.word );
//		}
//		System.out.println("");
//		return ret;
//	}
	public static String list2string(Object[] list, int lastCount) {
		if (list == null) return "";
		String s = "";
		int skip = list.length - lastCount;
		for (Object tmp: list) {
			if (skip>0) {
				skip -= 1; continue;
			}
			s+=tmp + " ";
		}
		return s.trim();
	}
}
