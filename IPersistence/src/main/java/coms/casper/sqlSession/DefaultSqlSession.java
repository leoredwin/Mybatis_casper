package coms.casper.sqlSession;

import coms.casper.model.Configuration;

import java.lang.reflect.*;
import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(String statementId, Object... params) throws Exception {

        SimpleExecutor simpleExecutor = new SimpleExecutor(configuration, configuration.getMappedStatementMap().get(statementId));
        List<Object> query = simpleExecutor.query(params);
        return (List<E>) query;
    }

    @Override
    public <T> T selectOne(String statementId, Object... params) throws Exception {
        List<Object> objects = selectList(statementId, params);
        if (objects.size() == 1) {
            return (T) objects.get(0);
        } else {
            throw new RuntimeException("查询结果为空或者结果过多");
        }
    }

    @Override
    public int update(String statementId, Object... params) throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor(configuration, configuration.getMappedStatementMap().get(statementId));
        return simpleExecutor.update(params);
    }

    @Override
    public int delete(String statementId, Object... params) throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor(configuration, configuration.getMappedStatementMap().get(statementId));
        return simpleExecutor.update(params);
    }

    @Override
    public <T> T create(String statementId, Object... params) throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor(configuration, configuration.getMappedStatementMap().get(statementId));
        simpleExecutor.update(params);
        return (T) params;
    }

    @Override
    public <T> T getMapper(Class<?> mapperClass) {
        Object o = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();
                String statementId = className + "." + methodName;

                Type genericReturnType = method.getGenericReturnType();

                if (genericReturnType instanceof ParameterizedType) {
                    return selectList(statementId, args);
                }

                return selectOne(statementId, args);
            }
        });
        return (T) o;
    }
}
