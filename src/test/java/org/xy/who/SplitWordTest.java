package org.xy.who;

import java.util.List;

import org.xy.utils.SplitWord;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SplitWordTest extends TestCase {
    public static Test suite()
    {
        return new TestSuite( SplitWordTest.class );
    }
    
    public void testSplitBasic() {
    	
    	String s = "患者的生育史";
    	//SplitWord.loadUserDict();
    	List<String> words = SplitWord.ikCutWord(s);
    	for (String w: words) {
    		System.out.println(w);
    	}
    }

}
