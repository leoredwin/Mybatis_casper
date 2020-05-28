package coms.casper.dao;

import coms.casper.model.User;

import java.util.List;

public interface UserDao {

    List<User> findAll() throws Exception;

    User findOne(User user);
}
