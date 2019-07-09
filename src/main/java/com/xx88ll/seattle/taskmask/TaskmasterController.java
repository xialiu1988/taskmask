package com.xx88ll.seattle.taskmask;


import com.amazonaws.services.dynamodbv2.xspec.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Controller
public class TaskmasterController {
    @Autowired
    S3Client s3Client;

    @Autowired
    TaskmasterRepository taskmasterRepository;
    @Autowired
    AssigneeRepository assigneeRepository;

    @GetMapping("/tasks")
    public ResponseEntity<Iterable<Task>> getTasks(Model m){
        Iterable<Task> findTask = taskmasterRepository.findAll();
        return ResponseEntity.ok(findTask);
    }

    @PostMapping("/tasks")
    public ResponseEntity<String> addTask(@RequestParam String description,@RequestParam String title, @RequestParam String status,@RequestParam String name){
        Task newTask = new Task(title, description, status);
        if(!name.isEmpty()) {
            System.out.println("runnning");

            if(assigneeRepository.findByName(name)!=null){
                Assignee theAssignee=assigneeRepository.findByName(name);
                System.out.println(theAssignee.getName());
                newTask.setAssignee(theAssignee);
            }
            else{
                assigneeRepository.save(new Assignee(name));
                Assignee theOne = assigneeRepository.findByName(name);
                newTask.setAssignee(theOne);
            }

        }
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


    @GetMapping("/users/{name}/tasks")
    public ResponseEntity<Iterable<Task>>getTheUserTasks(@PathVariable String name){
          Assignee theAssignee=assigneeRepository.findByName(name);
         System.out.println(theAssignee.getName());
          Iterable<Task> lists=taskmasterRepository.findAllByassignee(theAssignee);
        return ResponseEntity.ok(lists);

    }


    @PutMapping("/tasks/{id}/assign/{assignee}")
    public ResponseEntity<Task> updateAssigneetoTask(@PathVariable String id,@PathVariable String assignee){
        Task theTask = taskmasterRepository.findById(id).get();
        if(!assignee.isEmpty()){

           if(assigneeRepository.findByName(assignee)!=null){
               Assignee theAssignee=assigneeRepository.findByName(assignee);
               theTask.setAssignee(theAssignee);
           }
            else{
                assigneeRepository.save(new Assignee(assignee));
                Assignee theOne = assigneeRepository.findByName(assignee);
                theTask.setAssignee(theOne);
           }

            theTask.setStatus("Assigned");
            taskmasterRepository.save(theTask);
        }

        return ResponseEntity.ok(theTask);
    }



    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getOnetask(@PathVariable String id){

        Task tt = taskmasterRepository.findById(id).get();
        return ResponseEntity.ok(tt);
    }




    @PostMapping("/tasks/{id}/images")
    public ResponseEntity<Task> uploadFile(@PathVariable String id,
            @RequestPart(value = "file") MultipartFile file
    ){

        String pic = this.s3Client.uploadFile(file);
        Task tt = taskmasterRepository.findById(id).get();
        tt.setImageUrl(pic);
        taskmasterRepository.save(tt);
        return ResponseEntity.ok(tt);

    }



}