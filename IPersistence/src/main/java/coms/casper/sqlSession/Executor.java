package coms.casper.sqlSession;

import coms.casper.model.Configuration;
import coms.casper.model.MappedStatement;

import java.sql.SQLException;
import java.util.List;

public interface Executor {

    <E> List<E> query(Object... params) throws Exception;

    int update(Object... params) throws Exception;
}
