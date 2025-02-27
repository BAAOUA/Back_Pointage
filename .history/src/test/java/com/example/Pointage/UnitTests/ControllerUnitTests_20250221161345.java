package com.example.Pointage.UnitTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.Pointage.Configuration.JwtService;
import com.example.Pointage.MSC.Controller;
import com.example.Pointage.MSC.CustomUserDetailServices;
import com.example.Pointage.MSC.Employee;
import com.example.Pointage.MSC.EmployeeService;
import com.example.Pointage.Services.LoginDAO;
import com.example.Pointage.Services.ResponseData;

@ExtendWith(SpringExtension.class)
public class ControllerUnitTests {

    @Mock
    private CustomUserDetailServices userDetails;
    @Mock
    private EmployeeService empService;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authManager;

    @InjectMocks
    private Controller controller;

    private MockMvc mockMvc;
    
    @BeforeEach
    public void start() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void testLoginInvalid() {
        LoginDAO loginDAO = new LoginDAO("user", "password");
        given(userDetails.getAuthUser(loginDAO)).willReturn(false);

        ResponseEntity<?> response = controller.login(loginDAO);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo("Nom d'utilisateur ou mot de passe incorrect.");
    }

    @Test
    void testLoginValid() {
        LoginDAO loginDAO = new LoginDAO("admin", "admin123");
        given(userDetails.getAuthUser(loginDAO)).willReturn(true);
        Authentication authentication = mock(Authentication.class);
        given(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(authentication);
        given(authentication.isAuthenticated()).willReturn(true);
        
        ResponseEntity<?> response = controller.login(loginDAO);
        
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).asString().contains("accessToken");
    }
    @Test
    void testRefreshTokenEmpty() {
        //String token = "refreshToken";
        ResponseEntity<?> response = controller.refreshToken("");
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo(Map.of("error", "Refresh token inconnue"));
    }
    @Test
    void testRefreshTokenException() {
        String token = "refreshToken";
        given(jwtService.isTokenValide(anyString())).willThrow(new RuntimeException("Erreur de validation du token"));
        ResponseEntity<?> response = controller.refreshToken(token);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo(Map.of("error", "Erreur de validation du token"));
    }
    @Test
    void testRefreshTokenSuccess() {
        String token = "refreshToken";
        given(jwtService.isTokenValide(anyString())).willReturn(true);
        given(jwtService.extractUsername(token)).willReturn("user");

        given(jwtService.generateAccessToken("user")).willReturn("accesToken");
        given(jwtService.generateRefreshToken("user")).willReturn("refreshToken");
        ResponseEntity<?> response = controller.refreshToken(token);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(new ResponseData(null, null"accesToken", "refreshToken"));
    }

    @Test
    void testGetEmployeesWithoutDate() {
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(1L, "baaoua", "baaoua", LocalDate.parse("2025-01-28"), LocalTime.of(8, 30), LocalTime.of(17, 0)));
        employees.add(new Employee(2L, "brahim", "brahim", LocalDate.parse("2025-02-07"), LocalTime.of(8, 43), LocalTime.of(16, 50)));
        given(empService.findAllEmployees()).willReturn(employees);

        ResponseEntity<?> response = controller.getEmployees(null);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(empService).findAllEmployees();
    }
    @Test
    void testAddEmployee(){
        ResponseEntity<?> response = controller.addEmployees(null);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo("Pas de fichier envoyé");
    }
    @Test
    void testAddEmployeesValidFile() throws IOException{
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employees");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Nom");
        headerRow.createCell(1).setCellValue("Prénom");
        headerRow.createCell(2).setCellValue("Date");
        headerRow.createCell(3).setCellValue("Heure d'Entrée");
        headerRow.createCell(4).setCellValue("Heure de Sortie");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        MockMultipartFile valideFile = new MockMultipartFile("data", "employees.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", byteArray);
        workbook.close();

        ResponseEntity<?> response = controller.addEmployees(valideFile);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }
}
