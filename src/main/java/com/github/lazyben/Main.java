package com.github.lazyben;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.*;

public class Main {
    @SuppressFBWarnings("DMI_CONSTANT_DB_PASSWORD")
    public static void main(String[] args) throws IOException, SQLException {
        Connection connection = DriverManager.getConnection("jdbc:h2:file:./target/news", "root", "root");
        String link;
        while ((link = getALinkFromDatabaseAndDeleteIt(connection)) != null) {
            if (isLinkProcessed(connection, link)) {
                continue;
            }
            if (isInterestingLink(link)) {
                Document doc = GetNewsPageHtmlAndParse(link);
                selectHrefInPageAndStoreIntoDatabase(connection, doc);
                storeIntoDatabaseIfItIsNewsPage(connection, doc, link);
                operateLinkBySqlIntoDatabase(connection, link, "insert into link_already_processed values (?)");
            }
        }
    }

    private static String getALinkFromDatabaseAndDeleteIt(Connection connection) throws SQLException {
        String link = getALinkFromDatabase(connection);
        if (link != null) {
            operateLinkBySqlIntoDatabase(connection, link, "delete from link_to_be_processed where link = ?");
            return link;
        }
        return null;
    }

    private static void selectHrefInPageAndStoreIntoDatabase(Connection connection, Document doc) throws SQLException {
        for (Element aTag : doc.select("a")) {
            String href = aTag.attr("href");
            operateLinkBySqlIntoDatabase(connection, href, "insert into link_to_be_processed values (?)");
        }
    }

    private static boolean isLinkProcessed(Connection connection, String link) throws SQLException {
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

    private static void operateLinkBySqlIntoDatabase(Connection connection, String link, String sql) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, link);
            preparedStatement.executeUpdate();
        }
    }

    private static String getALinkFromDatabase(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from link_to_be_processed limit 1"); ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        }
        return null;
    }

    private static void storeIntoDatabaseIfItIsNewsPage(Connection connection, Document doc, String link) {
        Elements articleTags = doc.select("article");
        Elements article = doc.select(".art_p");
        if (!articleTags.isEmpty()) {
            //System.out.println(article);
            System.out.println(articleTags.get(0).child(0).text());
        }
    }

    private static Document GetNewsPageHtmlAndParse(String link) throws IOException {
        if (link.startsWith("//")) {
            link = "https:" + link;
        }
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(link);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Safari/537.36");
        try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
            HttpEntity entity1 = response1.getEntity();
            return Jsoup.parse(EntityUtils.toString(entity1));
        }
    }

    private static boolean isInterestingLink(String link) {
        return (isNewsPage(link) || isIndexPage(link)) && isNotLoginPage(link);
    }

    private static boolean isNotLoginPage(String link) {
        return !link.contains("passport");
    }

    private static boolean isIndexPage(String link) {
        return "https://sina.cn".equals(link);
    }

    private static boolean isNewsPage(String link) {
        return link.contains("news.sina.cn");
    }
}

