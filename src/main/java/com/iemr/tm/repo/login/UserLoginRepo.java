package com.iemr.tm.repo.login;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.tm.data.login.Users;

@Repository
public interface UserLoginRepo extends CrudRepository<Users, Long> {

	@Query(" SELECT u FROM Users u WHERE u.userID = :userID AND u.Deleted = false ")
	public Users getUserByUserID(@Param("userID") Long userID);

	@Query(nativeQuery = true,value = "select rolename from m_role where roleid in (select roleid from m_userservicerolemapping where userid=:userID)")
	List<String> getRoleNamebyUserId(@Param("userID") Long userID);
	
}
