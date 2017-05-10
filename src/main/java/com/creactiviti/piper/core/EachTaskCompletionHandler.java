/* 
 * Copyright (C) Creactiviti LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Arik Cohen <arik@creactiviti.com>, Apr 2017
 */
package com.creactiviti.piper.core;

import java.util.Date;

import com.creactiviti.piper.core.job.MutableJobTask;
import com.creactiviti.piper.core.task.TaskExecution;
import com.creactiviti.piper.core.task.TaskExecutionRepository;
import com.creactiviti.piper.core.task.TaskStatus;

/**
 * 
 * @author Arik Cohen
 * @since Apr 24, 2017
 */
public class EachTaskCompletionHandler implements TaskCompletionHandler {

  private final TaskExecutionRepository jobTaskRepository;
  private final TaskCompletionHandler taskCompletionHandler;
  
  public EachTaskCompletionHandler(TaskExecutionRepository aJobTaskRepository, TaskCompletionHandler aTaskCompletionHandler) {
    jobTaskRepository = aJobTaskRepository;
    taskCompletionHandler = aTaskCompletionHandler;
  }
  
  @Override
  public void handle (TaskExecution aJobTask) {
    MutableJobTask mtask = MutableJobTask.createForUpdate(aJobTask);
    mtask.setStatus(TaskStatus.COMPLETED);
    mtask.setEndTime(new Date());
    long updateSubTask = jobTaskRepository.completeSubTask(mtask);
    if(updateSubTask == 0) {
      taskCompletionHandler.handle(jobTaskRepository.findOne(aJobTask.getParentId()));
    }
  }

  @Override
  public boolean canHandle(TaskExecution aJobTask) {
    return aJobTask.getParentId()!=null;
  }

}