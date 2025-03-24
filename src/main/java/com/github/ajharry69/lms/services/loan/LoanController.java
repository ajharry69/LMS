package com.github.ajharry69.lms.services.loan;

import com.github.ajharry69.lms.services.loan.model.LoanRequest;
import com.github.ajharry69.lms.services.loan.model.LoanResponse;
import com.github.ajharry69.lms.services.loan.model.LoanStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Operations related to loans")
public class LoanController {
    private final LoanService loanService;

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Request loan")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successful loan request"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request payload",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Unknown customer",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "412",
                            description = "Customer has an ongoing loan request that has already been rejected",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<LoanResponse> requestLoan(@Valid @RequestBody LoanRequest request) {
        var response = loanService.requestLoan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/{loanId}/status", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Get loan status")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful loan status retrieval"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Invalid/unrecognized loan ID",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    ),
            }
    )
    public ResponseEntity<LoanStatusResponse> getLoanStatus(@PathVariable Long loanId) {
        var response = loanService.getLoanStatus(loanId);
        return ResponseEntity.ok(response);
    }
}
