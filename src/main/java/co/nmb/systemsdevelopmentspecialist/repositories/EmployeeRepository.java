package co.nmb.systemsdevelopmentspecialist.repositories;

import co.nmb.systemsdevelopmentspecialist.models.Employee;
import co.nmb.systemsdevelopmentspecialist.models.SalaryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {


    List<Employee> findBySalaryStatus(SalaryStatus status);

    Optional<Employee> findByAccountNumber(String accountNumber);
}
