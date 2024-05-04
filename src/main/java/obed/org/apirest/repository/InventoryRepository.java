package obed.org.apirest.repository;

import obed.org.apirest.entities.InventoryItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InventoryRepository extends MongoRepository<InventoryItem, String> {
    
}
