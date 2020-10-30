package com.github.lazyben;

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
import java.util.stream.Collectors;

public class Crawler {
    final CrawlerDao dao = new MybatisDao();

    public static void main(String[] args) throws IOException, SQLException {
        new Crawler().run();
    }

    public void run() throws SQLException, IOException {
        String link;
        while ((link = dao.getALinkFromDatabaseAndDeleteIt()) != null) {
            if (dao.isLinkProcessed(link)) {
                continue;
            }
            if (isInterestingLink(link)) {
                Document doc = GetNewsPageHtmlAndParse(link);
                selectHrefInPageAndStoreIntoDatabase(doc);
                storeIntoDatabaseIfItIsNewsPage(doc, link);
                dao.insertLinkAlreadyProcessed(link);

            }
        }
    }

    private void selectHrefInPageAndStoreIntoDatabase(Document doc) throws SQLException {
        for (Element aTag : doc.select("a")) {
            String href = aTag.attr("href");
            if (href.startsWith("//")) {
                href = "https:" + href;
            }
            if (!href.toLowerCase().startsWith("javascript")) {
                dao.insertLinkToBeProcessed(href);
            }
        }
    }

    private void storeIntoDatabaseIfItIsNewsPage(Document doc, String link) throws SQLException {
        Elements articleTags = doc.select("article");
        if (!articleTags.isEmpty()) {
            String content = articleTags.select("p").stream().map(Element::text).collect(Collectors.joining("\n"));
            String title = articleTags.get(0).child(0).text();
            System.out.println(title);
            dao.storeIntoDatabase(link, content, title);
        }
    }


    private Document GetNewsPageHtmlAndParse(String link) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(link);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Safari/537.36");
        try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
            HttpEntity entity1 = response1.getEntity();
            return Jsoup.parse(EntityUtils.toString(entity1));
        }
    }

    private boolean isInterestingLink(String link) {
        return (isNewsPage(link) || isIndexPage(link)) && isNotLoginPage(link);
    }

    private boolean isNotLoginPage(String link) {
        return !link.contains("passport");
    }

    private boolean isIndexPage(String link) {
        return "https://sina.cn".equals(link);
    }

    private boolean isNewsPage(String link) {
        return link.contains("news.sina.cn");
    }
}

