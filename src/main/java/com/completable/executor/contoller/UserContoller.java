package com.completable.executor.contoller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.completable.executor.entity.User;
import com.completable.executor.service.UserService;

@RestController
public class UserContoller {

	@Autowired
	private UserService userService;

	@PostMapping(value = "/users", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = "application/json")
	public ResponseEntity saveUsers(@RequestParam(value = "files") MultipartFile[] files) throws Exception {

		for (MultipartFile file : files) {
			userService.saveUsers(file);
		}
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}


	@GetMapping(value = "/users", produces = "application/json")
	public CompletableFuture<ResponseEntity> findAllUsers() {
		
		/*Here we use only one Thread to get data*/
		return userService.findAllUsers().thenApply(ResponseEntity::ok);
	}

	@GetMapping(value = "/getUsersByThread", produces = "application/json")
	public ResponseEntity getUsers() {
		
		/*Here we use the 2 Thread (CorePoolSize(2)) to get data*/
		CompletableFuture<List<User>> users1 = userService.findAllUsers();
		CompletableFuture<List<User>> users2 = userService.findAllUsers();
		CompletableFuture<List<User>> users3 = userService.findAllUsers();
		//combine the 3 tasks 
		CompletableFuture.allOf(users1, users2, users3).join();
		return ResponseEntity.status(HttpStatus.OK).build();

	}

}
