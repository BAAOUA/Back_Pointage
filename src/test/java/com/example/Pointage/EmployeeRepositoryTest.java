package com.example.Pointage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.Pointage.MSC.Employee;
import com.example.Pointage.MSC.EmployeeRepository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class EmployeeRepositoryTest {

    private static final Logger logger = LogManager.getLogger(EmployeeRepositoryTest.class);

    @Autowired
    private EmployeeRepository empRepo;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setNom("brahim");
        employee.setPrenom("brahim");
        employee.setDate(LocalDate.of(2025, 01, 27));
        employee.setHeureEntree(LocalTime.of(8, 40));
        employee.setHeureSortie(LocalTime.of(16, 40));
    }

    @Test
    @Order(1)
    @Rollback(value = false)
    public void saveEmployeeTest(){
        Employee emp = empRepo.save(employee);
        assertThat(emp.getId()).isGreaterThan(0);
        assertThat(emp.getId()).isNotNull();
        assertThat(emp.getNom()).isEqualTo("brahim");
        assertThat(emp.getPrenom()).isEqualTo("brahim");
        assertThat(emp.getDate()).isEqualTo(LocalDate.of(2025, 1, 27));
        assertThat(emp.getHeureEntree()).isEqualTo(LocalTime.of(8, 40));
        assertThat(emp.getHeureSortie()).isEqualTo(LocalTime.of(16, 40));
        logger.info("Ajouter un employee");
    }
    @Test
    @Order(2)
    void getEmployeeTest(){
        Employee emp = empRepo.findById(1L).get();
        assertThat(emp.getId()).isEqualTo(1L);
        logger.info("Un seumle employee");
    }
    @Test
    @Order(3)
    void getAllEmployeesTest(){
        List<Employee> emps = (List<Employee>) empRepo.findAll();
        assertThat(emps.size()).isGreaterThan(0);
        logger.info("Liste des employees");
    }
    @Test
    @Order(4)
    void updateEmpoyeeTest(){
        Employee emp = empRepo.findById(1L).get();
        emp.setPrenom("mohammed");
        Employee newEmp = empRepo.save(emp);
        assertThat(newEmp).isNotNull();
        assertThat(newEmp.getPrenom()).isEqualTo("mohammed");
        logger.info("Modification de l'utilisateur");
    }
    @Test
    @Order(5)
    void findByDateTest(){
        List<Employee> emps = empRepo.findByDate(employee.getDate());
        assertThat(emps.size()).isGreaterThan(0);
        logger.info("Recherche par date");
    }
    @Test
    @Order(6)
    void deleteEmployeesTest(){
        Employee emp = empRepo.findById(1L).get();
        empRepo.delete(emp);
        emp = null;
        Optional<Employee> res = empRepo.findById(1L);
        if(res.isPresent()){
            emp = res.get();
        }
        assertThat(emp).isNull();
        logger.info("Employee est supprimé");
    }
}
