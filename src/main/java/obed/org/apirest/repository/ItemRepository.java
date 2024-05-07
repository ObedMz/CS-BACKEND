package obed.org.apirest.repository;

import obed.org.apirest.model.ItemDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends MongoRepository<ItemDTO, String> {

    Optional<ItemDTO> findById(String id);

    ItemDTO findByName(String name);
}
