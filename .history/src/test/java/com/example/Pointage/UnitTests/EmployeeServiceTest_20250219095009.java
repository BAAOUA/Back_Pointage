package com.example.Pointage.UnitTests;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.Pointage.MSC.Employee;
import com.example.Pointage.MSC.EmployeeRepository;
import com.example.Pointage.MSC.EmployeeService;

@ExtendWith(SpringExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository empRepo;

    @InjectMocks
    private EmployeeService empService;

    private Employee employee1;
    private Employee employee2;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(empService)
                .build();

        employee1 = new Employee(1L, "baaoua", "baaoua", LocalDate.parse("2025-01-28"), LocalTime.of(8, 30), LocalTime.of(17, 0));
        employee2 = new Employee(2L, "brahim", "brahim", LocalDate.parse("2025-02-07"), LocalTime.of(8, 43), LocalTime.of(16, 50));
    }

    @Test
    void testFindAllEmployees() {
        List<Employee> employees = Arrays.asList(employee1, employee2);
        given(empRepo.findAll()).willReturn(employees);

        List<Employee> result = empService.findAllEmployees();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(employee1, employee2);

        verify(empRepo).findAll();
    }

    @Test
    void testFindAllByDate() {
        String dateString = "2025-01-27";
        //LocalDate date = LocalDate.of(2025, 1, 27);
        LocalDate date = LocalDate.parse(dateString);

        given(empRepo.findByDate(date)).willReturn(Arrays.asList(employee1));

        List<Employee> result = empService.findAllByDate(date);

        // Vérifications
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(employee1);

        verify(empRepo).findByDate(date);
    }

    @Test
    
    void testAjouter() {
        given(empRepo.save(any(Employee.class))).willReturn(employee2);

        Employee result = empService.Ajouter(employee1);

        assertThat(result).isNotNull();
        assertThat(result.getNom()).isEqualTo("brahim");
        assertThat(result.getPrenom()).isEqualTo("brahim");

        verify(empRepo).save(employee1);
    }
}
