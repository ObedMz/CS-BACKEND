package obed.org.apirest.repository;

import obed.org.apirest.model.data.ItemDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ItemRepository extends MongoRepository<ItemDTO, String> {
    ItemDTO findByName(String name);
    @Query(value = "{}", fields = "{ '_id' : 1 }")
    List<String> findAllIds();
    void deleteAllByIdIn(Set<String> ids);
    List<ItemDTO> findByGroupIn(List<String> groups);
    List<ItemDTO> findByGroup(String group);
}
