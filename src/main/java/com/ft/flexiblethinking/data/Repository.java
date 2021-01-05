package com.ft.flexiblethinking.data;

import org.springframework.data.repository.CrudRepository;

@org.springframework.stereotype.Repository
public interface Repository extends CrudRepository<Question, Long> {
}
