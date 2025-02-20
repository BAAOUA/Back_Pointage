package com.example.Pointage;

import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.example.Pointage.Configuration.JwtService;
import com.example.Pointage.MSC.Controller;
import com.example.Pointage.MSC.CustomUserDetailServices;
import com.example.Pointage.MSC.Employee;
import com.example.Pointage.MSC.EmployeeService;
import com.example.Pointage.Services.LoginDAO;

@ExtendWith(MockitoExtension.class)
@Web
@AutoConfigureMockMvc
public class ControllerIntegrationTest {

    private static final Logger logger = LogManager.getLogger(ControllerIntegrationTest.class);

    @MockBean
    private CustomUserDetailServices userDetails;
    @MockBean
    private EmployeeService empService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private AuthenticationManager authManager;

    @InjectMocks
    private Controller controller;
    @Autowired
    private MockMvc mockMvc;

    
    public MockMultipartFile getFile(Boolean valide) throws IOException{
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employees");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Nom");
        headerRow.createCell(1).setCellValue("Prénom");
        if(valide){
            headerRow.createCell(2).setCellValue("Date");
            headerRow.createCell(3).setCellValue("Heure d'Entrée");
        }
        headerRow.createCell(4).setCellValue("Heure de Sortie");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        workbook.close();
        return new MockMultipartFile("data", "employees.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", byteArray);
        
    }

//Récuperer les employees
    @Test
    void testGetEmployeesWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/employees"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void testGetEmployeesWithoutDate() throws Exception {
        
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(1L, "baaoua", "baaoua", LocalDate.parse("2025-01-28"), LocalTime.of(8, 30), LocalTime.of(17, 0)));
        employees.add(new Employee(2L, "brahim", "brahim", LocalDate.parse("2025-02-07"), LocalTime.of(8, 43), LocalTime.of(16, 50)));
        given(empService.findAllEmployees()).willReturn(employees);

        mockMvc.perform(get("/employees"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nom").value("baaoua"))
            .andExpect(jsonPath("$[1].prenom").value("brahim"));
    }
    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void testGetEmployeesWithDate() throws Exception {
        // Given
        String dateStr = "2025-02-07";
        LocalDate date = LocalDate.parse(dateStr);
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(1L, "baaoua", "brahim", date, LocalTime.of(8, 0), LocalTime.of(17, 0)));
        given(empService.findAllByDate(date)).willReturn(employees);

        mockMvc.perform(get("/employees").param("date", dateStr))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].nom").value("baaoua"))
            .andExpect(jsonPath("$[0].prenom").value("brahim"));
    }
// Ajouter les employer
    @Test
    void testAddEmployeesEmptyFileWithoutAuthentication() throws Exception {
        mockMvc.perform(multipart("/employees/add").param("data", ""))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockUser(roles = {"USER"})
    void testAddEmployeesEmptyFileIfUser() throws Exception {
        mockMvc.perform(multipart("/employees/add").param("data", ""))
                .andExpect(status().isForbidden());
    }
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testAddEmployeesEmptyFile() throws Exception {
        mockMvc.perform(multipart("/employees/add").param("data", ""))
                .andExpect(status().isBadRequest());
    }
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testAddEmployeesValidFile() throws Exception {
        mockMvc.perform(multipart("/employees/add").file(getFile(true)))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testAddEmployeesInvalidFile() throws Exception {
        mockMvc.perform(multipart("/employees/add").file(getFile(false)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Le fichier ne respecte pas le format requis"));
    }
    @Test
    void testLoginInvalid() throws Exception {
        LoginDAO loginDAO = new LoginDAO("brahim", "admin123");
        given(userDetails.getAuthUser(loginDAO)).willReturn(false);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\" : \"brahim\", \"password\": \"admin123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Nom d'utilisateur ou mot de passe incorrect."));
    }
    @Test
    void testLoginValide() throws Exception {
        LoginDAO loginDAO = new LoginDAO("admin", "admin123");
        given(userDetails.getAuthUser(loginDAO)).willReturn(true);

        Authentication authentication = mock(Authentication.class);
        given(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(authentication);
        given(authentication.isAuthenticated()).willReturn(true);
        

        given(jwtService.generateAccessToken(anyString())).willReturn("accessTokenValide");
        given(jwtService.generateRefreshToken("admin")).willReturn("refreshTokenValide");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\" : \"admin\", \"password\": \"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"accessToken\":\"accessTokenValide\",\"refreshToken\":\"refreshTokenValide\"}"));
    }
    @Test
    void testLoginErreur() throws Exception {
        LoginDAO loginDAO = new LoginDAO("admin", "admin123");
        given(userDetails.getAuthUser(loginDAO)).willReturn(true);

        Authentication authetication = mock(Authentication.class);
        given(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(authetication);
        given(authetication.isAuthenticated()).willReturn(false);

        mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\": \"admin\", \"password\": \"admin123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("erreur de login"));
    }

    @Test
    void testRefreshTokenException() throws Exception {
        given(jwtService.isTokenValide(anyString())).willThrow(new RuntimeException("token invalide"));

        mockMvc.perform(post("/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"refreshToken\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"token invalide\"}"));
    }
    
    @Test
    void testRefreshTokenSuccess() throws Exception {
        //String token = "\"refreshToken\"";
        String username = "admin";
        given(jwtService.isTokenValide(anyString())).willReturn(true);
        given(jwtService.extractUsername(anyString())).willReturn(username);
        given(jwtService.generateAccessToken(username)).willReturn("newAccessToken");
        given(jwtService.generateRefreshToken(username)).willReturn("newRefreshToken");

        mockMvc.perform(post("/auth/refresh-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"token\":\"refreshToken\"}"))
            .andExpect(status().isOk())
            .andExpect(content().json("{\"accessToken\":\"newAccessToken\",\"refreshToken\":\"newRefreshToken\"}"));

    }
}
