package com.github.lazyben;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.sql.*;

public class JdbcCrawlerDao implements CrawlerDao {
    private final Connection connection;

    @SuppressFBWarnings("DMI_CONSTANT_DB_PASSWORD")
    public JdbcCrawlerDao() {
        try {
            this.connection = DriverManager.getConnection("jdbc:h2:file:./target/news", "root", "root");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getALinkFromDatabaseAndDeleteIt() throws SQLException {
        String link = getALinkFromDatabase();
        if (link != null) {
            operateLinkBySqlIntoDatabase(link, "delete from link_to_be_processed where link = ?");
            return link;
        }
        return null;
    }

    @Override
    public boolean isLinkProcessed(String link) throws SQLException {
        ResultSet resultSet = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from link_already_processed where link = ?")) {
            preparedStatement.setString(1, link);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
        return false;
    }

    public void operateLinkBySqlIntoDatabase(String link, String sql) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, link);
            preparedStatement.executeUpdate();
        }
    }

    private String getALinkFromDatabase() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from link_to_be_processed limit 1"); ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        }
        return null;
    }

    @Override
    public void storeIntoDatabase(String link, String content, String title) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("insert into news (url, content, title, created_at, modified_at) values (?,?,?,now(),now())")) {
            preparedStatement.setString(1, link);
            preparedStatement.setString(2, content);
            preparedStatement.setString(3, title);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void insertLinkToBeProcessed(String link) throws SQLException {
        operateLinkBySqlIntoDatabase(link, "insert into link_to_be_processed values (?)");
    }

    @Override
    public void insertLinkAlreadyProcessed(String link) throws SQLException {
        operateLinkBySqlIntoDatabase(link, "insert into link_already_processed values (?)");
    }

