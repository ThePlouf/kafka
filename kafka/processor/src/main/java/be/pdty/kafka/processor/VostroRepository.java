package be.pdty.kafka.processor;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface VostroRepository  extends CrudRepository<Vostro,String> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select v from Vostro v where v.account = :id")
	Optional<Vostro> findByIdForTransaction(@Param("id") String id);
}
