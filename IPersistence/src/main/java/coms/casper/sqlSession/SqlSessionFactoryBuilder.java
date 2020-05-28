package coms.casper.sqlSession;

import coms.casper.config.XMLConfigBuilder;
import coms.casper.model.Configuration;
import org.dom4j.DocumentException;

import java.beans.PropertyVetoException;
import java.io.InputStream;

public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(InputStream in) throws DocumentException, PropertyVetoException {
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder();
        Configuration configuration = xmlConfigBuilder.parseConfig(in);

        return new DefaultSqlSessionFactory(configuration);

    }
}
