package mkoner.ads_dental_surgeries.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mkoner.ads_dental_surgeries.config.JwtUtil;
import mkoner.ads_dental_surgeries.dto.user.UserFilterDTO;
import mkoner.ads_dental_surgeries.dto.user.UserRequestDTO;
import mkoner.ads_dental_surgeries.dto.user.UserResponseDTO;
import mkoner.ads_dental_surgeries.dto.user.UserUpdateDTO;
import mkoner.ads_dental_surgeries.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for unit testing controller
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserResponseDTO userResponseDTO;
    private UserRequestDTO userRequestDTO;
    private UserUpdateDTO userUpdateDTO;

    @BeforeEach
    void setUp() {
        userRequestDTO = new UserRequestDTO("John", "Doe", "1234567890", "john@example.com", "P@12356me", "USER");
        userResponseDTO = new UserResponseDTO(1L, "John", "Doe", "1234567890", "john@example.com");
        userUpdateDTO = new UserUpdateDTO("John", "Doe", "1234567890", "john@example.com", "USER");
    }

    @Test
    @DisplayName("POST /api/users - should create user and return 201")
    @WithMockUser(roles = "OFFICE-MANAGER")
    void testCreateUser() throws Exception {


        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(userResponseDTO)));
    }

    @Test
    @DisplayName("GET /api/users/{id} - should return user by id")
    @WithMockUser(roles = "OFFICE-MANAGER")
    void testGetUserById() throws Exception {
        Long userId = 1L;

        when(userService.findUserById(userId)).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userResponseDTO)));
    }

    @Test
    @DisplayName("GET /api/users?fetchAll=true - should return all users")

    void testGetAllUsers() throws Exception {
        List<UserResponseDTO> responseList = List.of(userResponseDTO);

        when(userService.getAllUsers()).thenReturn(responseList);

        mockMvc.perform(get("/api/users?fetchAll=true"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseList)));
    }

    @Test
    @DisplayName("GET /api/users?fetchAll=false - should return paged filtered users")
    void testGetFilteredUsers() throws Exception {
        Page<UserResponseDTO> pagedUsers = new PageImpl<>(List.of(userResponseDTO));

        when(userService.getFilteredUsersWithPagination(any(UserFilterDTO.class), any(Pageable.class)))
                .thenReturn(pagedUsers);

        mockMvc.perform(get("/api/users")
                        .param("fetchAll", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("PUT /api/users/{id} - should update user")
    void testUpdateUser() throws Exception {
        Long userId = 1L;

        when(userService.updateUser(eq(userId), any(UserUpdateDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userResponseDTO)));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - should delete user")
    void testDeleteUser() throws Exception {
        Long userId = 1L;

        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
    }
}
