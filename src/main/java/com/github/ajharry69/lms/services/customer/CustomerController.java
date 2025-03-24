package com.github.ajharry69.lms.services.customer;

import com.github.ajharry69.lms.services.customer.model.Customer;
import com.github.ajharry69.lms.services.customer.model.CustomerRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customers", description = "Operations related to customers")
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Subscribe to LMS as a customer")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successful subscription"
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
    public ResponseEntity<Customer> subscribeCustomer(@Valid @RequestBody CustomerRequest request) {
        var customer = customerService.subscribeCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }
}
