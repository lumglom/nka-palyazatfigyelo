package hu.szatmarbanda.palyazat.repository;

import hu.szatmarbanda.palyazat.model.Palyazat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PalyazatRepository extends JpaRepository<Palyazat, String> {
}