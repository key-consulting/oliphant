/*******************************************************************************

   Copyright (C) 2009-1010 Key Consulting

   This file is part of Oliphant.

   Oliphant is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published
   by the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Oliphant is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with Oliphant.  If not, see <http://www.gnu.org/licenses/>.

*******************************************************************************/

package fr.keyconsulting.oliphant;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import org.postgresql.PGNotification;
import org.postgresql.PGConnection;

class PostgreSQLNotifyListener implements SpecificNotifyListener
	{
	private PGConnection pgConn;
	private Connection conn;

	public void setUp(Configuration config)
		{
		try
			{
			conn = DriverManager.getConnection(config.getProperty("hibernate.connection.url"), config.getProperty("hibernate.connection.username"), config.getProperty("hibernate.connection.password"));
			pgConn = (PGConnection) conn;
			Statement stmt = conn.createStatement();
			for (Iterator it = config.getClassMappings(); it.hasNext();)
				{
				PersistentClass p = (PersistentClass) it.next();
				String table = p.getTable().getName();
				stmt.executeUpdate("CREATE OR REPLACE FUNCTION oliphant_"+table+"() RETURNS TRIGGER AS $$ DECLARE a integer; BEGIN a = send_notify('oliphant', '"+table+"#' || NEW.ID || '###' || NEW.VERSION); RETURN NULL; END; $$ LANGUAGE 'plpgsql';");
				stmt.executeUpdate("CREATE OR REPLACE TRIGGER oliphant_"+table+"_trg AFTER DELETE OR UPDATE ON "+table+" FOR EACH ROW EXECUTE PROCEDURE oliphant_"+table+"();");
				}
			stmt.execute("LISTEN oliphant");
			stmt.close();
			}
		catch (SQLException sqle)
			{
			sqle.printStackTrace();
			}
		}

	public List<Notification> getLatestUpdates()
		{
		List<Notification> notifs = new ArrayList<Notification>();

		try
			{
			// issue a dummy query to contact the backend
			// and receive any pending notifications.
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT 1");
			rs.close();
			stmt.close();

			PGNotification notifications[] = pgConn.getNotifications();
			if (notifications != null)
				{
				for (int i=0; i<notifications.length; i++)
					{
					System.out.println("Notif from PostgreSQL : "+notifications[i].getParameter());
					notifs.add(new Notification(notifications[i].getParameter().split("###")));
					}
				}
			}
		catch (SQLException sqle)
			{
			sqle.printStackTrace();
			}

		return notifs;
		}

	public void tearDown()
		{
		try
			{
			//stmt.executeUpdate("DROP FUNCTION oliphant_"+name+"()");
			conn.close();
			}
		catch (SQLException sqle)
			{
			sqle.printStackTrace();
			}
		}
	}
