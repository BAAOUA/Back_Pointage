package com.example.Pointage.MSC;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {
  @Autowired
  private EmployeeRepository empRepo;

  public List<Employee> findAllEmployees(){
    return (List<Employee>) empRepo.findAll();
  }

  public List<Employee> findAllByDate(LocalDate date){
    return empRepo.findByDate(date);
  }
  @
  public Employee Ajouter(Employee emp){
    return empRepo.save(emp);
  }
}
