package com.kodekonveyor.authentication;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity,Long>{

	List<UserEntity> findByAuth0id(String auth0id);
}
