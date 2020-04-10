package com.completable.executor.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.completable.executor.entity.User;
import com.completable.executor.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;

	Object target;
	Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	@Async
	public CompletableFuture<List<User>> saveUsers(MultipartFile file) throws Exception {
		long start = System.currentTimeMillis();
		List<User> users = parseCSVFile(file);
		LOGGER.info("saving list of users of size {} ", users.size(), "" + Thread.currentThread().getName());
		users = repository.saveAll(users);
		long end = System.currentTimeMillis();
		LOGGER.info("total time { }", (end - start));
		return CompletableFuture.completedFuture(users);
	}

	@Async
	public CompletableFuture<List<User>> findAllUsers() {
		LOGGER.info("get list of user by id :   " + Thread.currentThread().getName());
		List<User> users = repository.findAll();
		return CompletableFuture.completedFuture(users);
	}
	

	List<User> parseCSVFile(final MultipartFile file) throws Exception {
		final List<User> users = new ArrayList<User>();
		try {
			try (final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
				String line;
				while ((line = br.readLine()) != null) {
					final String[] data = line.split(",");
					final User user = new User();
					user.setName(data[0]);
					user.setEmail(data[1]);
					user.setGender(data[2]);
					users.add(user);
				}
				return users;
			}
		}

		catch (final IOException e) {

			LOGGER.error("Failed to parse csv FIle {} ", e);
			throw new Exception("Failed to parse csv FIle {}", e);
		}
	}

}
