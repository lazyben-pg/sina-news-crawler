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
import java.util.ArrayList;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) throws IOException {
        final ArrayList<String> linkPool = new ArrayList<>();
        final HashSet<String> processedLink = new HashSet<>();
        linkPool.add("https://sina.cn");
        while (!linkPool.isEmpty()) {
            String link = linkPool.remove(linkPool.size() - 1);
            if (processedLink.contains(link)) {
                continue;
            }
            if (link.contains("news.sina.cn") || "https://sina.cn".equals(link)) {
                System.out.println(link);
                if (link.startsWith("//")) {
                    link = "https:" + link;
                }
                //这是我们感兴趣的新闻，处理它
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet httpGet = new HttpGet(link);
                httpGet.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Safari/537.36");
                try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
                    HttpEntity entity1 = response1.getEntity();
                    final Document doc = Jsoup.parse(EntityUtils.toString(entity1));
                    ArrayList<Element> elements = doc.select("a");
                    for (Element element : elements) {
                        linkPool.add(element.attr("href"));
                    }
                    Elements articleTags = doc.select("article");
                    if (!articleTags.isEmpty()) {
                        System.out.println(articleTags.get(0).child(0).text());
                    }
                }
                processedLink.add(link);
            }
        }
    }
}

