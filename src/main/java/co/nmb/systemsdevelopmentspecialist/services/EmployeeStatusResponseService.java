
package co.nmb.systemsdevelopmentspecialist.services;

import co.nmb.systemsdevelopmentspecialist.models.Employee;
import co.nmb.systemsdevelopmentspecialist.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeStatusResponseService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    public String checkingStatus(Employee employee){
        
        Optional<Employee> client = employeeRepository.findByAccountNumber(employee.getSalaryStatus().name());

        String status = "";

        if(client.isPresent()){

            Employee emp = client.get();

           status = emp.getSalaryStatus() !=null? emp.getSalaryStatus().name() : "UNKNOWN";
        }
       
        return status;
    }
}
