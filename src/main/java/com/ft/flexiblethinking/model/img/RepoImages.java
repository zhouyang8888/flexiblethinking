package com.ft.flexiblethinking.model.img;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Iterator;
import java.util.List;

@org.springframework.stereotype.Repository
public interface RepoImages extends CrudRepository<Image, Long> {

    @Query(value="select * from images t where t.md5=?1", nativeQuery=true)
    public List<Image> findByMD5(String md5);
}
