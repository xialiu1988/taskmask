package com.xx88ll.seattle.taskmask;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface TaskmasterRepository extends CrudRepository<Task, String> {

}