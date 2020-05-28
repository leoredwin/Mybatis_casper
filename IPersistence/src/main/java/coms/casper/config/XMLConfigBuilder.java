package coms.casper.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import coms.casper.io.Resources;
import coms.casper.model.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class XMLConfigBuilder {

    private Configuration configuration;

    public XMLConfigBuilder() {
        this.configuration = new Configuration();
    }

    public Configuration parseConfig(InputStream in) throws DocumentException, PropertyVetoException {
        Document read = new SAXReader().read(in);
        Element rootElement = read.getRootElement();

        List<Element> list = rootElement.selectNodes("//property");

        Properties properties = new Properties();
        list.forEach(element -> {
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");

            properties.setProperty(name, value);
        });

        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setDriverClass(properties.getProperty("driverClass"));
        comboPooledDataSource.setJdbcUrl(properties.getProperty("jdbcUrl"));
        comboPooledDataSource.setUser(properties.getProperty("username"));
        comboPooledDataSource.setPassword(properties.getProperty("password"));

        configuration.setDataSource(comboPooledDataSource);


        List<Element> mapperList = rootElement.selectNodes("//mapper");

        for (Element element : mapperList) {
            String mapperPath = element.attributeValue("resource");
            InputStream resourceAdStream = Resources.getResourceAdStream(mapperPath);
            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configuration);
            xmlMapperBuilder.parse(resourceAdStream);
        }


        return configuration;
    }
}
