package org.xy.who;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xy.thinking.db.dao.KDictDao;
import org.xy.thinking.db.model.KDict;
import org.xy.thinking.db.model.KNode;
import org.xy.thinking.service.ThinkingDatabaseService;

@ContextConfiguration(locations = { "classpath*:application.xml" })  
@RunWith(SpringJUnit4ClassRunner.class) 
public class DbTest {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	ThinkingDatabaseService service;
	
	@Test
	public void selectTest() {
		KDict item = service.selectDictByCode("118493");
		System.out.println(item.getName());
	}
	@Test
	public void insertTest() {
		KNode item = new KNode();
		item.setCode("SAMPLE");
		item.setCaseId(0);
		item.setValue("SAMPLE1");
		item.setValue("This is new value");
		item.setType("TEST");
		service.insertOrUpdate(item);
		System.out.println(item.getId()+ " HHHH");
	}
}
