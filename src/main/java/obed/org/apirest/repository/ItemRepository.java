package obed.org.apirest.repository;

import obed.org.apirest.model.data.ItemDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends MongoRepository<ItemDTO, String> {
    ItemDTO findByName(String name);
}
