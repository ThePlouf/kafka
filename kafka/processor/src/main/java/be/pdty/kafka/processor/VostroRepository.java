package be.pdty.kafka.processor;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface VostroRepository  extends CrudRepository<Vostro,String> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select v from Vostro v where v.account = :id")
	public Optional<Vostro> findByIdForTransaction(@Param("id") String id);
}
