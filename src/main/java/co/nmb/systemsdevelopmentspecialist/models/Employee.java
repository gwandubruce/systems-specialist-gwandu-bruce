package co.nmb.systemsdevelopmentspecialist.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Employee implements Serializable {


    @Id
    @GenericGenerator(name = "native_generator", strategy = "native")
    @GeneratedValue(generator = "native_generator")
    private Long id;

    private String fullName;

    private String accountNumber;

    private String debitAccountNumber;

    private BigDecimal salary;

    @Enumerated(EnumType.STRING)
    private SalaryStatus salaryStatus;

    private LocalDate datePaid;

    private BigDecimal accountBalance;

    private String paymentCategory;
}
