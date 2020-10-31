package com.github.lazyben;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;

public class MybatisDao implements CrawlerDao {
    private final SqlSessionFactory sqlSessionFactory;

    public MybatisDao() {
        try {
            String resource = "db/mybatis/config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized String getALinkFromDatabaseAndDeleteIt() throws SQLException {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            String link = session.selectOne("MyMapper.getALinkFromDatabase");
            if (link != null) {
                session.delete("MyMapper.deleteALink", link);
            }
            return link;
        }
    }

    @Override
    public boolean isLinkProcessed(String link) throws SQLException {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            Integer count = session.selectOne("MyMapper.countAProcessedLink", link);
            return count != 0;
        }
    }

    @Override
    public void storeIntoDatabase(String link, String content, String title) throws SQLException {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.insert("MyMapper.insertANews", new News(link, content, title));
        }
    }

    @Override
    public void insertLinkToBeProcessed(String link) throws SQLException {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            HashMap<String, String> map = new HashMap<>();
            map.put("tableName", "link_to_be_processed");
            map.put("link", link);
            session.insert("MyMapper.insertALink", map);
        }
    }

    @Override
    public void insertLinkAlreadyProcessed(String link) throws SQLException {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            HashMap<String, String> map = new HashMap<>();
            map.put("tableName", "link_already_processed");
            map.put("link", link);
            session.insert("MyMapper.insertALink", map);
        }
    }
}
