package cl.paris.marketplace.ms.clientes.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// --- Imports de Swagger agregados ---
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import cl.paris.marketplace.ms.clientes.dto.MetodoPagoRequest;
import cl.paris.marketplace.ms.clientes.dto.MetodoPagoResponse;
import cl.paris.marketplace.ms.clientes.service.ClienteService;
import jakarta.validation.Valid;

@Tag(name = "Clientes", description = "Gestión de clientes y sus métodos de pago")
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // ==========================================
    // ENDPOINTS: MÉTODOS DE PAGO
    // ==========================================
    
    @Operation(summary = "Agregar método de pago", description = "Registra un nuevo método de pago para el usuario autenticado.")
    @ApiResponse(responseCode = "201", description = "Método de pago creado exitosamente")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    examples = @ExampleObject(
                            name = "Ejemplo Request",
                            value = "{\n  \"tokenTarjeta\": \"tok_1234567890abcdef\",\n  \"tipo\": \"CREDITO\"\n}"
                    )
            )
    )
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')") 
    @PostMapping("/metodos-pago")
    public ResponseEntity<MetodoPagoResponse> agregarMetodoPago(
            @Valid @RequestBody MetodoPagoRequest request,
            Authentication authentication 
    ) {
        // Extraemos el ID del token 
        UUID usuarioId = UUID.fromString(authentication.getCredentials().toString());
        
        // Pasamos el request limpio y el ID recién extraído al Service
        MetodoPagoResponse response = clienteService.agregarMetodoPago(request, usuarioId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar métodos de pago", description = "Devuelve la lista de métodos de pago de un usuario específico.")
    @ApiResponse(responseCode = "200", description = "Métodos de pago obtenidos correctamente")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and #usuarioId.toString() == authentication.credentials)") 
    @GetMapping("/usuario/{usuarioId}/metodos-pago")
    public ResponseEntity<List<MetodoPagoResponse>> listarMetodosPagoUsuario(@PathVariable UUID usuarioId) {
        List<MetodoPagoResponse> response = clienteService.listarMetodosPagoUsuario(usuarioId);
        return ResponseEntity.ok(response);
    }
}