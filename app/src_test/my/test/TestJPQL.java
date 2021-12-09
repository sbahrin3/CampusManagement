package my.test;

import java.util.Enumeration;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import edu.emory.mathcs.backport.java.util.Arrays;
import educate.db.DbPersistence;
import educate.enrollment.entity.Student;

public class TestJPQL {
	
	public static void main(String[] args) throws Exception {
		
		String[] ids = {"1", "2"};
		List <String> values = Arrays.asList(ids);
		
		DbPersistence db = new DbPersistence();
		EntityManager em = db.getEntityManager();
		String q = "select s from Student s where s.id in :values";
		
		Query query = em.createQuery(q);
		query.setParameter("values", values);
		
		
		List list = query.getResultList();
		
	}

}
