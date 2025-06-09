package mkoner.ads_dental_surgeries.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mkoner.ads_dental_surgeries.dto.user.UserFilterDTO;
import mkoner.ads_dental_surgeries.dto.user.UserRequestDTO;
import mkoner.ads_dental_surgeries.dto.user.UserResponseDTO;
import mkoner.ads_dental_surgeries.dto.user.UserUpdateDTO;
import mkoner.ads_dental_surgeries.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Create a new user",
            description = "Only users with the OFFICE-MANAGER role can create a new user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<UserResponseDTO> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User details to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRequestDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody UserRequestDTO dto
    ) {
        log.info("Request to create user {}", dto);
        var user = userService.createUser(dto);
        log.info("Successfully created user {}", user.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Operation(
            summary = "Get a user by ID",
            description = "Only accessible to OFFICE-MANAGER"
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "ID of the user to retrieve", required = true)
            @PathVariable Long id
    ) {
        log.info("Request to get  user {}", id);
        var user = userService.findUserById(id);
        log.info("Successfully get user {}", user.userId());
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Get users with optional filters and pagination",
            description = "Set `fetchAll=true` to ignore pagination and return all users",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return users"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<?> getUsers(
            @Parameter(description = "Whether to fetch all users without pagination", example = "false")
            @RequestParam(name = "fetchAll", defaultValue = "false") boolean fetchAll,

            @Parameter(description = "Filter criteria for users", required = false)
            @ModelAttribute UserFilterDTO filterDTO,

            @Parameter(description = "Pagination and sorting criteria", required = false)
            @PageableDefault(size = 10, sort = "userId") Pageable pageable
    ) {
        if (fetchAll) {
            log.info("Request to fetch all users");
            return ResponseEntity.ok(userService.getAllUsers());
        } else {
            log.info("Request to fetch users with optional filters and pagination {} {}", filterDTO, pageable);
            return ResponseEntity.ok(userService.getFilteredUsersWithPagination(filterDTO, pageable));
        }
    }

    @Operation(
            summary = "Update an existing user",
            description = "Updates user details by ID, only Office Manager can perform this action",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return updated user"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "ID of the user to update", required = true)
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated user details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserUpdateDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody UserUpdateDTO dto
    ) {
        log.info("Request to update user {} {}", id, dto);
        var user = userService.updateUser(id, dto);
        log.info("Successfully updated user {}", user.userId());
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Delete a user",
            description = "Delete user, throw exception if user has an associated appointment, only OFFICE MANAGER can perform this action",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted"),
                    @ApiResponse(responseCode = "400", description = "Invalid input, user has associated appointments"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to delete", required = true)
            @PathVariable Long id
    ) {
        log.info("Request to delete user {}", id);
        userService.deleteUser(id);
        log.info("Successfully deleted user {}", id);
        return ResponseEntity.noContent().build();
    }
}

