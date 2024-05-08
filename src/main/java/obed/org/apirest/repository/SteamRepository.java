package obed.org.apirest.repository;

import obed.org.apirest.model.data.SteamDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SteamRepository extends MongoRepository<SteamDTO, String> {
}
