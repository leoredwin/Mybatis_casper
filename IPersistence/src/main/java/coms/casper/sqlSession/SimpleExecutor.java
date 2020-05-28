package coms.casper.sqlSession;

import coms.casper.config.BoundSql;
import coms.casper.model.Configuration;
import coms.casper.model.MappedStatement;
import coms.casper.utils.GenericTokenParser;
import coms.casper.utils.ParameterMapping;
import coms.casper.utils.ParameterMappingTokenHandler;
import coms.casper.utils.TokenHandler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor implements Executor {

    private PreparedStatement preparedStatement;

    private MappedStatement mappedStatement;

    private BoundSql boundSql;

    public SimpleExecutor(Configuration configuration, MappedStatement mappedStatement) throws Exception {
        Connection connection = configuration.getDataSource().getConnection();

        String sql = mappedStatement.getSql();

        this.boundSql = getBoundSql(sql);

        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());

        this.mappedStatement = mappedStatement;
        this.preparedStatement = preparedStatement;
    }

    private void setPreparedStatement(Object[] param) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, SQLException {
        String parameterType = mappedStatement.getParameterType();

        Class<?> parameterClass = getClassType(parameterType);

        List<ParameterMapping> mappingList = boundSql.getMappingList();

        for (int i = 0; i < mappingList.size(); i++) {
            ParameterMapping parameterMapping = mappingList.get(i);
            String content = parameterMapping.getContent();

            Field declaredField = parameterClass.getDeclaredField(content);
            declaredField.setAccessible(true);
            Object o = declaredField.get(param);
            preparedStatement.setObject(i + 1, o);
        }
    }

    @Override
    public <E> List<E> query(Object... params) throws Exception {
        setPreparedStatement(params);

        //执行
        ResultSet resultSet = preparedStatement.executeQuery();

        // 封装返回对象
        String resultType = mappedStatement.getResultType();

        Class<?> resultTypeClass = getClassType(resultType);


        ArrayList<Object> objects = new ArrayList<>();
        while (resultSet.next()) {
            Object o = resultTypeClass.newInstance();
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                Object object = resultSet.getObject(columnName);

                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(o, object);
            }
            objects.add(o);
        }

        return (List<E>) objects;
    }


    @Override
    public int update(Object... params) throws Exception {
        setPreparedStatement(params);
        return preparedStatement.executeUpdate();

    }

    private Class<?> getClassType(String parameterType) throws ClassNotFoundException {
        if (parameterType != null) {
            return Class.forName(parameterType);
        } else {
            return null;
        }
    }

    private BoundSql getBoundSql(String sql) {
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        String parseSql = genericTokenParser.parse(sql);

        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();

        return new BoundSql(parseSql, parameterMappings);
    }
}
