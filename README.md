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


#Deployed link

http://x88l.us-east-2.elasticbeanstalk.com/tasks
