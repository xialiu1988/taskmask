# TASKMASTER

Trace your status of the tasks


Model: Task:id;
            title;
            description;
            status;
            
# routes
/tasks - >get all tasks 
/tasks(post) ->add new task
/tasks/{id}/state-> change the status of the task