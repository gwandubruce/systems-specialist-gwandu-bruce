package co.nmb.systemsdevelopmentspecialist.services;

import co.nmb.systemsdevelopmentspecialist.models.Employee;
import co.nmb.systemsdevelopmentspecialist.models.SalaryStatus;
import co.nmb.systemsdevelopmentspecialist.repositories.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class EmployeeService {


    @Autowired
    private EmployeeRepository employeeRepository;
    private final Logger logger =  LoggerFactory.getLogger(EmployeeService.class);


    public Employee updateEmployee (Employee employee) {

        Optional<Employee> employeeExisting = employeeRepository.findByAccountNumber(employee.getAccountNumber());

          Employee  employeeToUpdate = employeeExisting.orElseThrow(()-> new NoSuchElementException(" Employee Does Not Exist"));
            employeeToUpdate.setAccountNumber(employee.getAccountNumber());
            logger.info("@@@@@@@@@@@@@@@@@@@@@ Account Number modified ************");

        return employeeRepository.save(employeeToUpdate);
    }


    public Employee addEmployee (Employee employee) {

        Optional<Employee> employeeExisting = Optional.ofNullable(employeeRepository.findByAccountNumber(employee.getAccountNumber()).get()) ;
        if (employeeExisting.isPresent()) {

            throw new IllegalArgumentException("The Employee Already exist");

        }
        employee.setSalaryStatus(SalaryStatus.PENDING);

        return employeeRepository.save(employee);
    }

    public Page<Employee> findByPagination(int pageNo, int size) {

        Pageable pageable = PageRequest.of(pageNo-1,size);
        return employeeRepository.findAll(pageable);
    }


    public Employee getEmployee(Long id) {
        return employeeRepository.findById(id).get();
    }


    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }


    public List<Employee> listAllEmployees() {
        return employeeRepository.findAll();
    }


    public Optional<Employee> findByAccountNumber(String accountNumber) {
        return employeeRepository.findByAccountNumber(accountNumber);
    }



}
