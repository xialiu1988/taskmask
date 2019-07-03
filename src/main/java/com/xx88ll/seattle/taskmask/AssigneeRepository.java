package com.xx88ll.seattle.taskmask;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface AssigneeRepository extends CrudRepository<Assignee,String> {
     Assignee findByName(String name);
}
