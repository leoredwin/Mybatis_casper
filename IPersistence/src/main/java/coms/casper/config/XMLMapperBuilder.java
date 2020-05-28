package coms.casper.config;

import coms.casper.model.Configuration;
import coms.casper.model.MappedStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

public class XMLMapperBuilder {

    private Configuration configuration;


    public XMLMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public void parse(InputStream inputStream) throws DocumentException {
        Document document = new SAXReader().read(inputStream);

        Element rootElement = document.getRootElement();

        List<Element> list = rootElement.selectNodes("//select");
        String namespace = rootElement.attributeValue("namespace");
        list.forEach(element -> {
            String id = element.attributeValue("id");
            String resultType = element.attributeValue("resultType");
            String parameterType = element.attributeValue("parameterType");

            String sqlTxt = element.getTextTrim();

            MappedStatement mappedStatement = new MappedStatement();

            mappedStatement.setId(id);
            mappedStatement.setResultType(resultType);
            mappedStatement.setParameterType(parameterType);
            mappedStatement.setSql(sqlTxt);
            String key = namespace + "." + id;
            configuration.getMappedStatementMap().put(key, mappedStatement);

        });
    }
}
