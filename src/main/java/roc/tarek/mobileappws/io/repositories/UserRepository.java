package roc.tarek.mobileappws.io.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import roc.tarek.mobileappws.io.entity.UserEntity;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {
    public UserEntity findByEmail(String email);
    public UserEntity findByUserId(String userId);
}
