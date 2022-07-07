package co.nmb.systemsdevelopmentspecialist.services;

import co.nmb.systemsdevelopmentspecialist.models.Employee;
import co.nmb.systemsdevelopmentspecialist.models.SalaryStatus;
import co.nmb.systemsdevelopmentspecialist.repositories.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class FileUploadService {

    @Autowired
    private static EmployeeRepository employeeRepository;

    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    public FileUploadService(EmployeeRepository employeeRepository) {
        FileUploadService.employeeRepository = employeeRepository;
    }

    public static Map<String, Map<String, String>> readFile(MultipartFile file, String paymentCategory) {

        String firstError = "error encoutered please try again";

        Map<String, String> status = new HashMap<>();
        Map<String, Map<String, String>> statuses = new HashMap<>();



        try {

            // instantiate BufferedReader object from multipart file using the helper method

            BufferedReader bufferedReader = multipartFileToBufferedReader(file, "UTF-8");

//            boolean initial = true;
            String line;

            if (paymentCategory.equalsIgnoreCase("BULK")){

                READ_LOOP:
                        while ((line = bufferedReader.readLine()) != null) {


                            String[] data = line.split(",");
                            String fullName = data[0].trim();
                            String accountNumber = data[1].trim();
                            String debitAccountNumber = data[2].trim();
                            BigDecimal salary = data[3] != null ? BigDecimal.valueOf(Double.parseDouble(data[3])) : BigDecimal.ZERO;

                            Optional<Employee> employeeOptional = employeeRepository.findByAccountNumber(accountNumber);
                            Employee employee;

                            if (!employeeOptional.isPresent()) {

                                employee = new Employee();
                                employee.setAccountNumber(accountNumber);
                                employee.setDatePaid(LocalDate.now());
                                employee.setSalaryStatus(SalaryStatus.PENDING);
                                employee.setFullName(fullName);
                                employee.setDebitAccountNumber(debitAccountNumber);
                                employee.setSalary(salary);
                                employee.setAccountBalance(salary);
                                employee.setPaymentCategory("BULK");

                            } else {
                                employee = employeeOptional.get();
                                employee.setAccountBalance(salary.add(employee.getAccountBalance()));
                                employee.setSalary(salary);
                                employee.setDebitAccountNumber(debitAccountNumber);
                                employee.setDatePaid(LocalDate.now());
                                employee.setAccountNumber(accountNumber);
                                employee.setFullName(fullName);
                                employee.setSalaryStatus(SalaryStatus.PENDING);
                                employee.setPaymentCategory("BULK");

                            }

                            Employee savedEmployee = employeeRepository.save(employee);

                            Map<String, String> record = new HashMap<String, String>() {{
                                put("FULLNAME", employee.getFullName());
                                put("SALARY", employee.getSalary().toString());
                            }};

                            if (savedEmployee == null) {
                                record.put("STATUS", SalaryStatus.FAILED.name());
                            } else {
                                record.put("STATUS", employee.getSalaryStatus().name());
                            }


                            statuses.put(employee.getId().toString(), record);




                        }
        } else if(paymentCategory.equalsIgnoreCase("INDIVIDUAL")) {

                throw new IllegalArgumentException("Please Choose Option BULK to Process Bulk Payments");

            }
        } catch (Exception e) {

            logger.debug("Failed to read the file");
            e.printStackTrace();
        }

        return statuses;
    }

// helper method to convert multipart file to buffered reader

    private static BufferedReader multipartFileToBufferedReader(MultipartFile multipartFile, String charsetName) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }
        try {
            InputStream inputStream = multipartFile.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charsetName);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            return bufferedReader;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
