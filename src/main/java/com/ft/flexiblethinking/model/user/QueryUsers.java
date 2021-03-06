package com.ft.flexiblethinking.model.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryUsers implements IQueryUsers{

    @Autowired
    private RepoUsers repo;

    /**
     * @param name
     * @param md5code
     * @return  -1: NOT exist.
     *          >=0: real user id.
     */
    public long checkExists(String name, String md5code){
        List<Long> ids = repo.findByNameAndCode(name, md5code);
        if (ids == null || ids.isEmpty()) return -1;
        else return ids.get(0);
    }

    public long checkExists(String name){
        List<Long> ids = repo.findByName(name);
        if (ids == null || ids.isEmpty()) return -1;
        else return ids.get(0);
    }

    public long save(String name, String md5code) {
        User u = new User();
        u.setName(name);
        u.setMd5code(md5code);
        u = repo.save(u);
        return u.getId();
    }
}
