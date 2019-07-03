package com.xx88ll.seattle.taskmask;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@EnableScan
public interface TaskmasterRepository extends CrudRepository<Task, String> {

        Iterable<Task> findAllByassignee(Assignee assignee);
}