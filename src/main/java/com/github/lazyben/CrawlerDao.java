package com.github.lazyben;

import java.sql.SQLException;

public interface CrawlerDao {
    String getALinkFromDatabaseAndDeleteIt() throws SQLException;

    boolean isLinkProcessed(String link) throws SQLException;

    void operateLinkBySqlIntoDatabase(String link, String sql) throws SQLException;

    String getALinkFromDatabase() throws SQLException;

    void storeIntoDatabase(String link, String content, String title) throws SQLException;
}
