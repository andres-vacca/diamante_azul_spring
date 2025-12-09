package com.example.diamante_azul.Service;

import com.example.diamante_azul.Models.Rol;
import com.example.diamante_azul.Repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public Rol findByNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }

    public Rol findById(Integer id) {
        Optional<Rol> rol = rolRepository.findById(id);
        return rol.orElse(null);
    }

    public List<Rol> findAll() {
        return rolRepository.findAll();
    }

    public Rol save(Rol rol) {
        return rolRepository.save(rol);
    }

    public void deleteById(Integer id) {
        rolRepository.deleteById(id);
    }

    public boolean existsByNombre(String nombre) {
        return rolRepository.existsByNombre(nombre);
    }

    // Método para crear roles por defecto si no existen
    public void crearRolesPorDefecto() {
        if (!existsByNombre("Administrador")) {
            Rol adminRole = new Rol();
            adminRole.setNombre("Administrador");
            save(adminRole);
        }

        if (!existsByNombre("Empleado")) {
            Rol empleadoRole = new Rol();
            empleadoRole.setNombre("Empleado");
            save(empleadoRole);
        }

        if (!existsByNombre("Cliente")) {
            Rol clienteRole = new Rol();
            clienteRole.setNombre("Cliente");
            save(clienteRole);
        }
    }
}