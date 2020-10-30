package com.github.lazyben;

import java.sql.SQLException;

public interface CrawlerDao {
    String getALinkFromDatabaseAndDeleteIt() throws SQLException;

    boolean isLinkProcessed(String link) throws SQLException;

    void storeIntoDatabase(String link, String content, String title) throws SQLException;

    void insertLinkToBeProcessed(String link) throws SQLException;

    void insertLinkAlreadyProcessed(String link) throws SQLException;
}
