package mkoner.ads_dental_surgeries.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mkoner.ads_dental_surgeries.dto.user.UserFilterDTO;
import mkoner.ads_dental_surgeries.dto.user.UserRequestDTO;
import mkoner.ads_dental_surgeries.dto.user.UserResponseDTO;
import mkoner.ads_dental_surgeries.dto.user.UserUpdateDTO;
import mkoner.ads_dental_surgeries.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<?> getUsers(
            @RequestParam(name = "fetchAll", defaultValue = "false") boolean fetchAll,
            @ModelAttribute UserFilterDTO filterDTO,
            @PageableDefault(size = 10, sort = "userId") Pageable pageable) {

        if(fetchAll) {
            return ResponseEntity.ok(userService.getAllUsers());
        }
        else {
            return ResponseEntity.ok(userService.getFilteredUsersWithPagination(filterDTO, pageable));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}

