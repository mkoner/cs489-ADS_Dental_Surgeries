package mkoner.ads_dental_surgeries.repository;

import mkoner.ads_dental_surgeries.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
}

