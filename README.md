# TASKMASTER

Trace your status of the tasks


Model: Task:id;
            title;
            description;
            status;
        Assignee:
             id;
             name;   
# routes
/tasks - >get all tasks 

/tasks(post) ->add new task

/tasks/{id}/state-> change the status of the task

/users/{name}/tasks -> get all the tasks assigned to the user

/tasks/{id}/assign/{assignee}->assigned the task to certain user and change the task status

#Deployed link

http://x88l.us-east-2.elasticbeanstalk.com/tasks

#issue
S3 bucket needs to have full access by the user. Otherwise it will not upload to the bucket.
