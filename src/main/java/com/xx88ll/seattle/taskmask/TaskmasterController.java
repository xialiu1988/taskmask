package com.xx88ll.seattle.taskmask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class TaskmasterController {

    @Autowired
    TaskmasterRepository taskmasterRepository;

    @GetMapping("/tasks")
    public ResponseEntity<Iterable<Task>> getTasks(Model m){
        Iterable<Task> findTask = taskmasterRepository.findAll();
        return ResponseEntity.ok(findTask);
    }

    @PostMapping("/tasks")
    public ResponseEntity<String> addTask(@RequestParam String description,@RequestParam String title, @RequestParam String status){
        Task newTask = new Task(title, description, status);
        taskmasterRepository.save(newTask);
        return ResponseEntity.ok("Done");
    }

    //update status of the specific task
    @PostMapping("/tasks/{id}/state")
    public ResponseEntity<Task> updateStatus(@PathVariable String id){
        Task theTask =taskmasterRepository.findById(id).get();
        if(theTask.getStatus().toLowerCase().equals("available")){
            theTask.setStatus("Assigned");
        }else if(theTask.getStatus().toLowerCase().equals("assigned")){
            theTask.setStatus("Accepted");
        }else if(theTask.getStatus().toLowerCase().equals("accepted")){
            theTask.setStatus("Finished");
        }
        else{
            theTask.setStatus("Finished");
        }
        taskmasterRepository.save(theTask);
        return ResponseEntity.ok(theTask);
    }



}