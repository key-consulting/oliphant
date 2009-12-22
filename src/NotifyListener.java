import java.util.HashMap;

public class NotifyListener extends EventListener
	{
	// Events supported by hibernate :
	// AutoFlushEventListener 	Defines the contract for handling of session auto-flush events.
	// DeleteEventListener 	Defines the contract for handling of deletion events generated from a session.
	// DirtyCheckEventListener 	Defines the contract for handling of session dirty-check events.
	// EvictEventListener 	Defines the contract for handling of evict events generated from a session.
	// FlushEntityEventListener 	 
	// FlushEventListener 	Defines the contract for handling of session flush events.
	// Initializable 	An event listener that requires access to mappings to initialize state at initialization time.
	// InitializeCollectionEventListener 	Defines the contract for handling of collection initialization events generated by a session.
	// LoadEventListener 	Defines the contract for handling of load events generated from a session.
	// LockEventListener 	Defines the contract for handling of lock events generated from a session.
	// MergeEventListener 	Defines the contract for handling of merge events generated from a session.
	// PersistEventListener 	Defines the contract for handling of create events generated from a session.
	// PostDeleteEventListener 	Called after deleting an item from the datastore
	// PostInsertEventListener 	Called after insterting an item in the datastore
	// PostLoadEventListener 	Occurs after an an entity instance is fully loaded.
	// PostUpdateEventListener 	Called after updating the datastore
	// PreDeleteEventListener 	Called before deleting an item from the datastore
	// PreInsertEventListener 	Called before inserting an item in the datastore
	// PreLoadEventListener 	Called before injecting property values into a newly loaded entity instance.
	// PreUpdateEventListener 	Called before updating the datastore
	// RefreshEventListener 	Defines the contract for handling of refresh events generated from a session.
	// ReplicateEventListener 	Defines the contract for handling of replicate events generated from a session.
	// SaveOrUpdateEventListener 	Defines the contract for handling of update events generated from a session.

	private Map staleUids = new HashMap(); // Map de session -> Map de UID (string identifiant table + objet) -> true ?
					       // Selon comment se fait la comparaison des objets session, il pourra etre necessaire
					       // de faire un objet qui prend une session dans le constructeur et definit un nouvel equal
					       // qui verifie juste si on a affaire a la meme instance de session
	private Map versions = new HashMap(); // Map de UID -> derniere version connue
	private SessionFactory sessionFactory;
	private SpecificNotifyListener specificNotifyListener;

	public Serializable ProcessLoadEvent(Event event, boolean throwStaleException) throws HibernateException
		{
		if (sessionFactory == null) {sessionFactory = event.getSession().getSessionFactory();} // our first event, initialize the sessionFactory
		// il est peut etre deja stale. on regarde si dans versions on a cet objet et si c'est le cas on compare a notre version, sinon on considère comme clean (pas moyen de savoir). Si version correcte on retire de staleIds de cette session. Si version ancienne, on staleobjectstateexception, et on garde le staleId. Si pas de version connue, on garde la version comme plus recente connue.
		// Cas pas de versionnage : on retire l'objet des dirtyIds de cette session. On a pas de moyen de savoir si il est stale.
		}

	public Serializable onPostLoad(PostLoadEvent event) throws HibernateException
		{
		ProcessLoadEvent(event, true);
		return processEvent(event);
		}

	public Serializable onRefresh(RefreshEvent event) throws HibernateException
		{
		ProcessLoadEvent(event, true);
		processEvent(event);
		}

	public Serializable onPersist(PersistEvent event) throws HibernateException
		{
		return processEvent(event);
		}

	public Serializable onDirtyCheck(DirtyCheckEvent event) throws HibernateException
		{
		return processEvent(event);
		}

	public Serializable onFlush(FlushEvent event) throws HibernateException
		{
		return processEvent(event);
		}

	public Serializable onCommit(CommitEvent event) throws HibernateException
		{
		return processEvent(event);
		}

	public Serializable processEvent(Event event) throws HibernateException
		{
		Object object = event.getObject();
		Session session = event.getSession();
		updateStaleUidsAndVersions();
		if (isKnownToBeStaleInSession(object, session))
			{
			if (isKnownToBeStaleInL2(object))
				{
				sessionFactory.evict(session.getEntityName(object), session.getIdentifier(object));
				}
			throw new StaleObjectStateException(object.class, id); // TODO: Should be optional for loads
			}
		return null;
		}

	public boolean isKnownToBeStaleInL2(Object object)
		{
		return false;
		// TODO : check cache, maybe do it in a separate cache manager ?
		/*
	 	EntityPersister persister = sessionFactory.getEntityPersister(entityName);
		if (persister.isVersionned())
			{
			object.getVersion()
			sessionFactory.
			// comparer version L2 et derniere version connue. Comment recuperer version L2 ?
			}
		else
			{
			// We can't know what version is in L2, so we can't know if it's stale
			return false;
			}
		*/
		}

	public boolean isKnownToBeStaleInSession(Object object, Session session)
		{
		String uid = uid(object);
		updateStaleUidsAndVersions();
	 	EntityPersister persister = sessionFactory.getEntityPersister(entityName);
		//if ((staleIds.ContainsKey(session)) && (staleIds.get(session).ContainsKey(uid))) {return true;}
		if (versions.ContainsKey(uid))
			{
			Field[] fields = object.getDeclaredFields();
			version = Versioning.getVersion(Object[] fields, persister) // Extract the optimistic locking value out of the entity state snapshot.
			&& (!versions.get(uid).equals(object.get...)) {return true;}
			}
		return false;
		}

	private string uid(Object object) // Unique Identifier for the object, used in database notifications
		{
		return object.class."#".object.getId();
		}

	private void updateStaleUidsAndVersions()
		{
		List<Notification> updates = specificNotifyListener.getLatestUpdates();
		for (int i=0; i<updates.length(); i++)
			{
			Notofication notif = updates[i];
			if (notif.getVersion()) {versions.put(notif.getUid(), Notif.getVersion());}
			List<Session> sessions = sessionFactory.getSessions();
			for (int j=0; j<sessions.length(); j++)
				{
				Session session = sessions[j];
				if (!staleUids.ContainsKey(session)) {staleUids.put(session, new HashMap());}
				staleUids.get(session).put(uid, notif.getUid());
				}
			}
		}
 
	private garbageCollector()
		{
		// TODO: remove the keys of closed sessions from the staleUids Map
		}
	}

public class Notification
	{
	private long Version;
	private String uid;

	public Notification(String[] infos)
		{
		uid = infos[0];
		version = infos[1];
		}
	
	public long getVersion()
		{
		return version;
		}

	public void setVersion(long v)
		{
		version = v;
		}

	public String getUid()
		{
		return uid;
		}

	public void setUid(String u)
		{
		uid = u;
		}
	}

public class MagicSessionFactory extends SessionFactory
	{
	private List<Session> sessions;

	public MagicSessionFactory(SessionFactory)
		{
		}

	public List<Session> getSessions()
		{
		return sessions;
		}

	public Session openSession()
		{
		Session session = super.openSession();
		sessions.Add(session);
		return session;
		}

	public Session openSession(Connection connection)
		{
		super.openSession(connection);
		}

	public Session openSession(Connection connection, boolean flushBeforeCompletionEnabled, boolean autoCloseSessionEnabled, ConnectionReleaseMode connectionReleaseMode)
		{
		super.openSession(connection, flushBeforeCompletionEnabled, autoCloseSessionEnabled, connectionReleaseMode);
		}

	public Session openSession(Connection connection, Interceptor sessionLocalInterceptor)
		{
		super.openSession(connection, sessionLocalInterceptor);
		}

	public Session openSession(Interceptor sessionLocalInterceptor)
		{
		super.openSession(sessionLocalInterceptor);
		}
	}

public class MagicAnnotationConfiguration extends AnnotationConfiguration
	{
	public MagicAnnotationConfiguration()
		{
		super();
		NotifyListener listener = new NotifyListener();
		this.getSessionEventListenerConfig().setLoadEventListener(listener);
		this.getSessionEventListenerConfig().setFlushEventListener(listener);
		}

	public MagicSessionFactory buildSessionFactory()
		{
		SessionFactory sf = super.buildSessionFactory();
		return (MagicSessionFactory) sf;
		}
	}

public interface SpecificNotifyListener
	{
	public void setUp(); // Setup the notification system (create triggers, subscribe to update notifications, etc ?)
	public List<Notification> getLatestUpdates(); // Return the latest notifications
	public void tearDown(); // Close the system properly (remove triggers, unsubscribe, etc ?)
	}

/************************************
 ***            Oracle            ***
 ************************************/
public class OracleNotifyListener implements DatabaseChangeListener, SpecificNotifyListener
	{
	public void setUp()
		{
		Properties prop = new Properties();
		prop.setProperty(OracleConnection.DCN_NOTIFY_ROWIDS, "true");
		prop.setProperty(OracleConnection.DCN_IGNORE_INSERTOP, "true");
		prop.setProperty(OracleConnection.DCN_NOTIFY_CHANGELAG, 0);

		/* DCN_IGNORE_DELETEOP
			If set to true, DELETE operations will not generate any database change event.
		DCN_IGNORE_INSERTOP
			If set to true, INSERT operations will not generate any database change event.
		DCN_IGNORE_UPDATEOP
			If set to true, UPDATE operations will not generate any database change event.
		DCN_NOTIFY_CHANGELAG
			Specifies the number of transactions by which the client is willing to lag behind.
			Note: If this option is set to any value other than 0, then ROWID level granularity of information will not be available in the events, even if the DCN_NOTIFY_ROWIDS option is set to true.
		DCN_NOTIFY_ROWIDS
			Database change events will include row-level details, such as operation type and ROWID.
		DCN_QUERY_CHANGE_NOTIFICATION
			Activates query change notification instead of object change notification.
			Note: This option is available only when running against an 11.0 database.
		NTF_LOCAL_HOST
			Specifies the IP address of the computer that will receive the notifications from the server.
		NTF_LOCAL_TCP_PORT
			Specifies the TCP port that the driver should use for the listener socket.
		NTF_QOS_PURGE_ON_NTFN
			Specifies if the registration should be expunged on the first notification event.
		NTF_QOS_RELIABLE
			Specifies whether or not to make the notifications persistent, which comes at a performance cost.
		NTF_TIMEOUT
			Specifies the time in seconds after which the registration will be automatically expunged by the database. */

		DatabaseChangeRegistration dcr = conn.registerDatabaseChangeNotification(prop);
		DCNDemoListener listener = new DCNDemoListener();
		dcr.addListener(listener);
		}

	public void onDatabaseChangeNotification(DatabaseChangeEvent e)
		{
		System.out.println(e.toString());
		}

	public void tearDown()
		{
		}
	}

/************************************
 ***            PostgreSQL        ***
 ************************************/
private Class PostgreSQLNotifyListener() implements SpecificNotifyListener
	{
	public void setUp()
		{
		}

	private Map getLatestUpdates(PGConnection conn)
		{
		List<Notifications> notifs;

		File file = new File("/var/lib/postgre/data/my_notify");
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		DataInputStream dis = new DataInputStream(bis);

		while (dis.available() != 0)
			{
			String line = dis.readLine();
			System.out.println(line);
			notifs.Add(new Notification(line.split("###"));
			}

		fis.close();
		bis.close();
		dis.close();

		return notifs;
		}
	/*
	private Map getLatestUpdates(PGConnection conn)
		{
		org.postgresql.PGNotification notifications[] = pgconn.getNotifications();
		string[] latestUpdates;
		if (notifications != null)
			{
			for (int i=0; i<notifications.length; i++) {latestUpdates.add(notifications[i].getPayload());}
			}
		}
	*/

	public void tearDown()
		{
		}
	}
