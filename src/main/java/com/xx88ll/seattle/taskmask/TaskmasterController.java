package com.xx88ll.seattle.taskmask;


import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
            // Publish a message to an Amazon SNS topic.
           String msg = "You received this because Task is finished--from AWS .";
            AmazonSNSClient snsClient = new AmazonSNSClient();
            String phoneNumber = "+13528701223";
            Map<String, MessageAttributeValue> smsAttributes =
                    new HashMap<>();

            sendMessage(snsClient, msg, phoneNumber, smsAttributes);
        }
        else{
            theTask.setStatus("Finished");
        }
        taskmasterRepository.save(theTask);
        return ResponseEntity.ok(theTask);
    }



    public static void sendMessage(AmazonSNSClient snsClient, String message,
                                      String phoneNumber, Map<String, MessageAttributeValue> smsAttributes) {
        PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(phoneNumber)
                .withMessageAttributes(smsAttributes));
        System.out.println(result);
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


    @CrossOrigin
    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getOnetask(@PathVariable String id){

        Task tt = taskmasterRepository.findById(id).get();
        return ResponseEntity.ok(tt);
    }


    @CrossOrigin
    @PostMapping("/tasks/{id}/images")
    public ResponseEntity<Task> uploadFile(@PathVariable String id,
            @RequestPart(value = "file") MultipartFile file
    ){

        String pic = this.s3Client.uploadFile(file);
        Task tt = taskmasterRepository.findById(id).get();
        tt.setImageUrl(pic);

        String[] picSplit = pic.split("/");
        String fileName = picSplit[picSplit.length-1];
        tt.setThumbnailUrl("https://taskmasterimageresized.s3-us-west-2.amazonaws.com/resized-" + fileName);
        taskmasterRepository.save(tt);
        return ResponseEntity.ok(tt);

    }


}