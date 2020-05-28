package coms.casper.test;

import coms.casper.dao.UserDao;
import coms.casper.io.Resources;
import coms.casper.model.User;
import coms.casper.sqlSession.SqlSession;
import coms.casper.sqlSession.SqlSessionFactory;
import coms.casper.sqlSession.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class IPersistenceTest {

    @Test
    public static void test() throws Exception {
        InputStream resourceAdStream = Resources.getResourceAdStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAdStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        UserDao mapper = sqlSession.getMapper(UserDao.class);
        List<User> all = mapper.findAll();


    }
}
