package com.community.chodae.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.community.chodae.domain.User;

public interface UserRepo extends JpaRepository<User, Long> {

	//쿼리메소드 : 메소드의 이름만으로 필요한 쿼리를 만들어내는 기능 지원  Select만 가능?
	//메소드 네이밍 rule : ex) find(엔티티이름)By(컬럼명)  
	//반환타입 : Collection<T> 형태로  주로 사용 ex) Page<T> , Slice<T> , List<T>
	Optional<User> findUserByNameAndEmail(String name, String email);
	
	Optional<User> findUserByLoginIdAndEmail(String loginId, String email);
	
	//update , delete, insert 시 @Transctional, @Modifying 어노테이션 필요하다. 
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE FROM User u set u.password = ?2 WHERE u.loginId =?1")
	int updatePassword(String id, String password);
	
	Optional<User> findUserByNickname(String nickname);
	
	
	// 소셜 회원이 아닌 회원을  로그인 아이디로 검색. 
	@EntityGraph(attributePaths = {"roleSet"}, type = EntityGraph.EntityGraphType.LOAD)
	@Query("SELECT u from User u WHERE u.social = :social and u.loginId = :loginId")
	Optional<User> findByLoginId(String loginId, boolean social);
	
	// 소셜 회원이 아닌 회원을  닉네임으로 검색. 
	@EntityGraph(attributePaths = {"roleSet"}, type = EntityGraph.EntityGraphType.LOAD)
	@Query("SELECT u from User u WHERE u.social = :social and u.nickname = :nickname")
	Optional<User> findByNickname(String nickname, boolean social);
	
	Boolean existsByLoginId(String loginId);
	
	User findByLoginIdAndPassword(String loginId,String password);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE FROM User u set u.refreshToken = :token WHERE u.nickname = :nickname")
	int updateRefreshToken(String token, String nickname);
	
	Optional<User> findById(Long id);
	User save(User user);
	Optional<User> findByEmail(String email);
	void delete(User user);

	//페이징 처리와 정렬 : 모든 쿼리메소드의 마지막 파라미터로 Pageable 인터페이스와 sort인터페이스 사용가능 
	//반환타입 : Slice 타입, Page 타입 , List 타입 이용 
//	List<User> findUserByNameContainingOrderByNameDesc(String name,Pageable paging);

	
//	@Query("SELECT u FROM User u WHERE u.name LIKE %?1% AND u.id > 0 ORDER BY u.id DESC")
//	List<User> findUserByNameContaining(String name);
//	@Query("SELECT u FROM #{#entityName} u WHERE u.name LIKE %:name% AND u.id > 0 ORDER BY u.id DESC")
//	List<User> findUserByNameContaining(@Param("name") String name);
//	@Query("SELECT u.id,u.name,u.email FROM #{#entityName} u WHERE u.name LIKE %:name% AND u.id > 0 ORDER BY u.id DESC")
//	List<Object[]> findUserByNameContaining(@Param("name") String name);
	
}
