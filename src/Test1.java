import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class Test1 {
	private Connection conn;
	private SessionFactory sessionFactory;
	private final int NB_ROWS = 2000;
	
	public void setUp() throws SQLException {
		conn = Utils.getJDBCConnection();

		Statement st = conn.createStatement();
		st.executeUpdate("DELETE from objetpersistent");
		st.close();

		sessionFactory = Utils.getSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		Transaction tx = session.beginTransaction();   
		for (int i = 0; i<NB_ROWS; i++) {
			ObjetPersistent o = new ObjetPersistent();
			o.setId(i);
			o.setChampString("valeur string");
			o.setChampLong((long) 1);
			session.save(o);
			if (i%20  == 0) {
				session.flush();
				session.clear();
				}
		    }
		session.flush();
		session.clear();
		tx.commit();
	}
	
	public void simpleUpdate() {
		System.out.println("=== Simple update ===");		

		Session session = sessionFactory.getCurrentSession();
		
		Transaction tx = session.beginTransaction();
		ObjetPersistent o = (ObjetPersistent) session.load(ObjetPersistent.class, (long) 1);
		o.setChampString("valeur 1");
		tx.commit();

		System.out.println(sessionFactory.getStatistics());		
	}
	
	public void staleUpdate() throws SQLException {
		System.out.println("=== Stale update ===");		

		Session session = sessionFactory.getCurrentSession();
		
		Transaction tx = session.beginTransaction();
		ObjetPersistent o = (ObjetPersistent) session.load(ObjetPersistent.class, (long) 2);
		o.setChampString("valeur 2");	
		
		Statement st = conn.createStatement();
		st.executeUpdate("UPDATE objetpersistent SET champstring='valeur 3' WHERE id=2");
		st.close();
		
		tx.commit();

		System.out.println(sessionFactory.getStatistics());		
	}

	public void simpleUpdateBenchmark() {
	}
	
	public void staleUpdateBenchmark() {
	}

	public static void main(String[] args) throws Exception {
		Test1 test = new Test1();
		test.setUp();
		test.simpleUpdate();
		test.staleUpdate();
	}
}