package com.xx88ll.seattle.taskmask;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@EnableScan
public interface TaskmasterRepository extends CrudRepository<Task, String> {

}