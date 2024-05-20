package obed.org.apirest.repository;

import obed.org.apirest.model.data.ItemDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends MongoRepository<ItemDTO, String> {
    ItemDTO findByName(String name);
    List<ItemDTO> findByGroupIn(List<String> groups);
    List<ItemDTO> findByGroup(String group);
}
