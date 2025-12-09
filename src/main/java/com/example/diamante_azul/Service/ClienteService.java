package com.example.diamante_azul.Service;

import com.example.diamante_azul.Models.Cliente;
import com.example.diamante_azul.Repository.ClienteRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarClientesActivos() {
        return clienteRepository.findByEstado("ACTIVO");
    }

    public Optional<Cliente> buscarClientePorId(Integer id) {
        return clienteRepository.findById(id);
    }
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll(); // Usa el método base de JpaRepository
    }

    // Usado para CREAR (si ID es null) o ACTUALIZAR (si ID existe)
    public Cliente guardarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void desactivarCliente(Integer id) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(id);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            cliente.setEstado("INACTIVO");
            clienteRepository.save(cliente);
        } else {
            throw new RuntimeException("Cliente no encontrado con ID: " + id);
        }
    }
}