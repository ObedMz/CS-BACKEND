package obed.org.apirest.repository;

import obed.org.apirest.model.ItemData;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

@Repository
public interface ItemRepository extends MongoRepository<ItemData, String> {

    ItemData findByitemname(String itemname);

    List<ItemData> findByCustomQuery(Query query);
}
