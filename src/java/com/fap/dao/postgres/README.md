# PostgreSQL DAO Layer (placeholder)

This folder is reserved for the third DBMS — **PostgreSQL 18** — being
built by the remaining teammate.

## When the teammate's schema arrives, you'll need:

### 1. Add the JDBC driver
Drop `postgresql-<version>.jar` into:
```
web/WEB-INF/lib/postgresql-42.x.x.jar
```
NetBeans picks it up automatically because `lib.dir` already points there.

### 2. Add credentials to `web.xml`
Following the **same pattern** as MySQL and Derby:
```xml
<context-param>
    <param-name>postgres.driver</param-name>
    <param-value>org.postgresql.Driver</param-value>
</context-param>
<context-param>
    <param-name>postgres.url</param-name>
    <param-value>jdbc:postgresql://localhost:5432/&lt;DB_NAME&gt;</param-value>
</context-param>
<context-param>
    <param-name>postgres.username</param-name>
    <param-value>postgres</param-value>
</context-param>
<context-param>
    <param-name>postgres.password</param-name>
    <param-value>&lt;password&gt;</param-value>
</context-param>
```

### 3. Create `PostgresConnection.java`
Sibling to `MySQLConnection.java` and `DerbyConnection.java`. Same pattern:
```java
package com.fap.db;

public class PostgresConnection {
    public static Connection getConnection(ServletContext ctx) throws SQLException {
        String driver   = ctx.getInitParameter("postgres.driver");
        String url      = ctx.getInitParameter("postgres.url");
        String user     = ctx.getInitParameter("postgres.username");
        String password = ctx.getInitParameter("postgres.password");
        try { Class.forName(driver); }
        catch (ClassNotFoundException e) { throw new SQLException(e); }
        return DriverManager.getConnection(url, user, password);
    }
}
```

### 4. Create DAOs in THIS folder
For each Postgres table, write a DAO here that follows the same pattern
as `com/fap/dao/mysql/CourseDAO.java`:
- `PreparedStatement` for every query (SQL-injection safe)
- Private `map<Entity>()` helpers turning `ResultSet` → POJO
- Use `try-with-resources` on every Connection

### 5. Wire it into a Servlet
Same pattern as the MySQL/Derby servlets — no architectural changes
needed. The web layer is DB-agnostic.

## What NOT to do
- Don't import `org.postgresql.*` from anywhere outside this folder.
- Don't reuse `MySQLConnection` for Postgres (different drivers).
- Don't put credentials in source code — always read from `web.xml`.
