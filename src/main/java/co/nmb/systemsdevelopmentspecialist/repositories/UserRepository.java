package co.nmb.systemsdevelopmentspecialist.repositories;

import co.nmb.systemsdevelopmentspecialist.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("Select u from User u WHERE u.username = ?1")
    User findByUsername(String username);
}
