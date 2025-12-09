package com.example.diamante_azul.Controller;

import com.example.diamante_azul.Models.Cliente;
import com.example.diamante_azul.Service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/lista")
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteService.listarClientesActivos());
        return "clientes/lista"; // Mapea a /templates/clientes/lista.html
    }

    @GetMapping("/crear")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/crear"; // Mapea a /templates/clientes/crear.html
    }

    @PostMapping("/guardar")
    public String guardarCliente(@ModelAttribute Cliente cliente) {
        clienteService.guardarCliente(cliente);
        return "redirect:/clientes/lista";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model) {
        Optional<Cliente> clienteOpt = clienteService.buscarClientePorId(id);

        if (clienteOpt.isPresent()) {
            model.addAttribute("cliente", clienteOpt.get());
            return "clientes/editar"; // Mapea a /templates/clientes/editar.html
        } else {
            return "redirect:/clientes/lista";
        }
    }

    @PostMapping("/desactivar/{id}")
    public String desactivarCliente(@PathVariable Integer id) {
        try {
            clienteService.desactivarCliente(id);
        } catch (RuntimeException e) {
            System.err.println("Error al desactivar cliente: " + e.getMessage());
        }
        return "redirect:/clientes/lista";
    }
}