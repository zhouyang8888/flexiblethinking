package com.ft.flexiblethinking.model.img;

import com.ft.flexiblethinking.model.data.RepoQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class QueryImage implements IQueryImages {
    @Autowired
    private RepoImages repo;

    public Image findByID(long id) {
        Optional<Image> oi = repo.findById(id);
        if (oi.isPresent()) {
            return oi.get();
        } else {
            return null;
        }
    }

    public Image saveOne(Image img) {
        return repo.save(img);
    }

    public List<Image> findByMD5(String md5) {
        return repo.findByMD5(md5);
    }
}
