package com.ft.flexiblethinking.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RepoUsers extends CrudRepository<User, Long> {

    @Query(value="SELECT u.id FROM User u WHERE u.name=?1 AND u.md5code=?2", nativeQuery = true)
    public List<Long> findByNameAndCode(String name, String code);
}
