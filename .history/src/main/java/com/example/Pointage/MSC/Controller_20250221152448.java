package com.example.Pointage.MSC;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Pointage.Configuration.JwtService;
import com.example.Pointage.Services.LoginDAO;
import com.example.Pointage.Services.ResponseData;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@CrossOrigin(origins="http://localhost:3000", allowedHeaders = "*")
public class Controller {

  private static final Logger logger = LogManager.getLogger(Controller.class);

  @Autowired
  private EmployeeService empService;
  @Autowired
  private AuthenticationManager authManager;
  @Autowired
  private JwtService jwtService;
  private CustomUserDetailServices usserDetaisServices;
  public Controller(){
    this.usserDetaisServices = new CustomUserDetailServices();
  }

  @PostMapping("/auth/login")
  public ResponseEntity<?> login(@RequestBody LoginDAO loginDAO ){
    if(!usserDetaisServices.getAuthUser(loginDAO)){
      return ResponseEntity.badRequest().body("Nom d'utilisateur ou mot de passe incorrect.");
    }
    ResponseData response = new ResponseData();
    response.setUsername(null);
    Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(loginDAO.getUsername(), loginDAO.getPassword()));
    if(authentication.isAuthenticated()){
      response.setAccessToken(jwtService.generateAccessToken(loginDAO.getUsername()));
      response.setRefreshToken(jwtService.generateRefreshToken(loginDAO.getUsername()));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }else{
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("erreur de login");
    }
    return ResponseEntity.ok().body(response);
  }
  @PostMapping("/auth/logout")
  public ResponseEntity<?> logout() {
      if(SecurityContextHolder.getContext().getAuthentication() != null){
        System.out.println("Clear context !!!!");
        SecurityContextHolder.clearContext();
      }
      logger.info("Logout");
      return ResponseEntity.ok().body("Deconexion avec succes");
  }
  @PostMapping("/auth/refresh-token")
  public ResponseEntity<?> refreshToken(@RequestBody Object token){
    String refreshToken = token.toString();
    try{
      if (refreshToken == null || refreshToken.isEmpty()) {
        return ResponseEntity.badRequest().body(Map.of("error", "Refresh token inconnue"));
      }
      if(jwtService.isTokenValide(refreshToken)){
        String username = jwtService.extractUsername(refreshToken);
        return ResponseEntity.ok().body(new ResponseData(null, null,jwtService.generateAccessToken(username), jwtService.generateRefreshToken(username)));
      }
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
    return ResponseEntity.badRequest().body(Map.of("error", "Erreur de génération de refresh token"));
  }

  @GetMapping("/employees")
  public ResponseEntity<?> getEmployees(@RequestParam(value = "date", required = false) String date) {
    if(date != null){
      LocalDate localDate = LocalDate.parse(date);
      return ResponseEntity.ok().body(empService.findAllByDate(localDate));
    }
    return ResponseEntity.ok().body(empService.findAllEmployees());
  }
  
  public boolean valideExcel(Row row ){{
    String[] headers = {"Nom", "Prénom", "Date", "Heure d'Entrée", "Heure de Sortie"};
    if(row == null) return false;
    for(int i = 0; i<headers.length ; i++){
      Cell cel = row.getCell(i);
      if(cel == null) return false;
      if(!cel.getStringCellValue().equals(headers[i])){
        System.out.println("celle " + cel.getStringCellValue() + "  n'est pas valide pour " + headers[i]);
        return false;
      }
    }
    return true;
  }

  }

  @PostMapping("/employees/add")
  public ResponseEntity<?> addEmployees(@RequestParam("data") MultipartFile data) {
      if(data == null){
        return ResponseEntity.badRequest().body("Pas de fichier envoyé");
      }
      try{
        Workbook xlsx = new XSSFWorkbook(data.getInputStream());
        Sheet sheet = xlsx.getSheetAt(0);

        if(!valideExcel(sheet.getRow(0))){
          xlsx.close();
          return ResponseEntity.badRequest().body("Le fichier ne respecte pas le format requis");
        }
        for(int i=1;i<sheet.getPhysicalNumberOfRows() ;i++) {    
          Row row = sheet.getRow(i);
          Employee employee = new Employee();
          employee.setNom(row.getCell(0).getStringCellValue());
          employee.setPrenom(row.getCell(1).getStringCellValue());
          employee.setDate(getLocalDate(row.getCell(2)));
          employee.setHeureEntree(getLocalTime(row.getCell(3)));
          employee.setHeureSortie(getLocalTime(row.getCell(4)));
          empService.Ajouter(employee);
        }
        xlsx.close();
      }catch(IOException ex){
        return ResponseEntity.badRequest().body("Un erreur est servenu");
      }
      return ResponseEntity.ok().build();
  }
  
  public LocalDate getLocalDate(Cell cell){
    if(cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)){
      Date date = cell.getDateCellValue();
      return new java.sql.Date(date.getTime()).toLocalDate();
      //return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    return null;
  }
  public LocalTime getLocalTime(Cell cell){
    if(cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)){
      Date date = cell.getDateCellValue();
      return new java.sql.Time(date.getTime()).toLocalTime();
    }
    return null;
  }

}
