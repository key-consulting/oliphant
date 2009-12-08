import java.util.HashMap;

public class NotifyFlushEventListener extends EventListener
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

	public Serializable ProcessLoadEvent(Event event, boolean throwStaleException) throws HibernateException
		{
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
		updateStaleUidsAndVersions(session);
		if (isKnownToBeStaleInSession(object, session))
			{
			if (isKnownToBeStaleInL2(object))
				{
				// l'objet est stale en L2, on le retire du cache L2
				session.getSessionFactory().evict(object);
				// void 	evict(Class persistentClass, Serializable id)
				//          Evict an entry from the second-level cache.
				// void 	evictEntity(String entityName, Serializable id)
				//          Evict an entry from the second-level cache.
				}
			throw new StaleObjectStateException(object.class, id); // pas toujours pour un load, configurable
			}
		return null;
		}

	public boolean isKnownToBeStaleInL2(Object object)
		{
	 	EntityPersister persister = source.getFactory().getEntityPersister(entityName);
		Versioning.getVersion(Object[] fields, persister) // Extract the optimisitc locking value out of the entity state snapshot.
		// comparer version L2 et derniere version connue
		// retourner toujours false pour les non versionnes
		}

	public boolean isKnownToBeStaleInSession(Object object, Session session)
		{
		String uid = uid(object);
		updateStaleUidsAndVersions(session);
		if ((staleIds.ContainsKey(session)) && (staleIds.get(session).ContainsKey(uid))) {return true;}
		if ((versions.ContainsKey(uid)) && (version.get(uid) != object.get...) {return true;}
		return false;
		}

	/*public ... getStaleObjectsClassesAndIds(Session session)
		{
		TODO (Peut etre ?)
		if (session.get(c, id) !== null) {throw new StaleObjectStateException(c, id);
		}

	private getClassName(String tableName)
		{
		for (Iterator iter = cfg.getClassMappings(); iter.hasNext(); )
			{
			PersistentClass pc = (PersistentClass) iter.next();
			if (tableName.equalsIgnoreCase(pc.getTable().getName()))
				{
				className = pc.getClassName();
				}
			}
		}*/

	private string uid(Object object) // Unique Identifier pour l'objet, qui est aussi utilisé dans les notifications du SGBD. Pourrait etre simplement <nom table><separateur><pk> mais il peut etre necessaire pour faire face a des restrictions par exemple du NOTIFY d'utiliser un SHA1 ou autre.
		{
		}

	private void updateStaleUidsAndVersions(Session session)
		{
		if (!staleUids.ContainsKey(session)) {staleUids.put(session, new HashMap());} // pas d'event sur l'initialisation d'une session donc on ne peut le faire que la
		Map newStaleUids = getLatestUpdates(session, session.connection());
		for (int i; i<newStaleUids; i++)
		foreach (uid, version)
			{
			if (version>0) {versions.put(uid, version);}
						
			}
		// on ajoute tout aux dirtyIds pour cette session
		}

	// PostgreSQL
	private Map getLatestUpdates(Session session, PGConnection conn)
		{
		org.postgresql.PGNotification notifications[] = pgconn.getNotifications();
		string[] latestUpdates;
		if (notifications != null)
			{
			for (int i=0; i<notifications.length; i++) {latestUpdates.add(notifications[i].getPayload());}
			}
		}

	// Oracle
	private Map getLatestUpdates(Session session, OracleConnection conn)
		{
		Map updates = new HashMap()
		
		// TODO
		return new string[];
		}

	private garbageCollector()
		{
		//fait le tour des sessions et retire celles qui ne sont plus open
		}
	}

// Pour utiliser ce listener, dans la conf :
//<hibernate-configuration>
//    <session-factory>
//        ...
//        <event type="flush">
//            <listener class="ce listener"/>
//            <listener class="org.hibernate.event.def.DefaultFlushEventListener"/>
//        </event>
//    </session-factory>
//</hibernate-configuration>