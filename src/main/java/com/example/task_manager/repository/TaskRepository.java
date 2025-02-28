package com.example.task_manager.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.task_manager.entity.Task;
@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByUserProfile_Id(UUID userId, Pageable pageable);

    List<Task> findByAssignedUser(UUID assignedUser, Pageable pageable);
}
