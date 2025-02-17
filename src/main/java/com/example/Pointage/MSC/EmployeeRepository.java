package com.example.Pointage.MSC;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Long> {

  @Query("SELECT e FROM Employee e WHERE e.date = :date")
  List<Employee> findByDate(LocalDate date);
  
}
