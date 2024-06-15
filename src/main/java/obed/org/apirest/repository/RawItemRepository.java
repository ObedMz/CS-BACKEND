package obed.org.apirest.repository;

import obed.org.apirest.model.data.RawSteamDto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RawItemRepository extends MongoRepository<RawSteamDto, Long> {
}
