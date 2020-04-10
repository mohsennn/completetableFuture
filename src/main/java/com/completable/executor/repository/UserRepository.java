package com.completable.executor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.completable.executor.entity.User;

public interface UserRepository  extends JpaRepository<User, Integer>{

}
