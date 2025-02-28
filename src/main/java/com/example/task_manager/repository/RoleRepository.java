package com.example.task_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.task_manager.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

}
