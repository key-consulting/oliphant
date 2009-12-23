import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class TestVersioned
	{
	private Connection conn;
	private SessionFactory magicSessionFactory;
	private SessionFactory sessionFactory;
	private final int NB_ROWS = 2000;
	
	public void setUp() throws SQLException
		{
		conn = Utils.getJDBCConnection();

		sessionFactory = Utils.getSessionFactory();
		magicSessionFactory = Utils.getMagicSessionFactory();

		Session session = sessionFactory.getCurrentSession();
		Transaction tx = session.beginTransaction();   
		for (int i = 0; i<NB_ROWS; i++)
			{
			PersistentVersionedObject o = new PersistentVersionedObject();
			o.setId(i);
			o.setChampString("valeur string");
			o.setChampLong((long) 1);
			session.save(o);
			if (i%20  == 0)
				{
				session.flush();
				session.clear();
				}
			}
		session.flush();
		session.clear();
		tx.commit();
		
		Statement st = conn.createStatement();
		st.executeUpdate("DROP FUNCTION PersistentVersionedObject_notification()");
		st.executeUpdate("CREATE FUNCTION PersistentVersionedObject_notification() RETURNS TRIGGER AS $$ DECLARE a integer; BEGIN a = my_notify('PersistentVersionedObject#' || NEW.ID || '###' || NEW.VERSION); RETURN NULL; END; $$ LANGUAGE 'plpgsql';");
		st.executeUpdate("CREATE TRIGGER PersistentVersionedObject_update_trigger AFTER DELETE OR UPDATE ON persistentversionedobject FOR EACH ROW EXECUTE PROCEDURE PersistentVersionedObject_notification();");
		st.close();
		}
	
	public void simpleUpdate()
		{
		System.out.println("=== Simple update ===");		

		Session session = sessionFactory.getCurrentSession();
		
		Transaction tx = session.beginTransaction();
		PersistentVersionedObject o = (PersistentVersionedObject) session.load(PersistentVersionedObject.class, (long) 1);
		o.setChampString("valeur 1");
		tx.commit();

		System.out.println(sessionFactory.getStatistics());		
		}
	
	public void staleUpdate(boolean magic) throws SQLException
		{
		System.out.println("=== Stale update "+(magic ? "(magic) ": "")+"===");		

		SessionFactory factory = magic ? magicSessionFactory : sessionFactory;

		Session session = factory.getCurrentSession();
		
		Transaction tx = session.beginTransaction();
		PersistentVersionedObject o = (PersistentVersionedObject) session.load(PersistentVersionedObject.class, (long) 2);
		o.setChampString("valeur 2");	
		
		Statement st = conn.createStatement();
		st.executeUpdate("UPDATE persistentversionedobject SET version=22 WHERE id=2");
		st.close();
		
		tx.commit();

		System.out.println(factory.getStatistics());		
		}

	public static void main(String[] args) throws Exception
		{
		TestVersioned test = new TestVersioned();
		test.setUp();
		test.staleUpdate(false);
		test.staleUpdate(true);
		}
	}