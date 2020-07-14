package roc.tarek.mobileappws.io.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import roc.tarek.mobileappws.io.entity.AddressEntity;
import roc.tarek.mobileappws.io.entity.UserEntity;

import java.util.List;

@Repository
public interface AddressRepository extends PagingAndSortingRepository<AddressEntity, Long> {

    Iterable<AddressEntity> findAllByUserEntity(UserEntity user);

    AddressEntity findByAddressId(String addressId);
}
