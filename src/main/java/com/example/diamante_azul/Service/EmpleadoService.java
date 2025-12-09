package com.example.diamante_azul.Service;

import com.example.diamante_azul.Models.Empleado;
import com.example.diamante_azul.Repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    public List<Empleado> findAll() {
        return empleadoRepository.findAll();
    }

    public Optional<Empleado> findById(Integer id) {
        return empleadoRepository.findById(id);
    }

    public Empleado save(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    public void deleteById(Integer id) {
        empleadoRepository.deleteById(id);
    }

    public Optional<Empleado> findByDocumento(String documento) {
        return empleadoRepository.findByDocumento(documento);
    }

    public Optional<Empleado> findByEmail(String email) {
        return empleadoRepository.findByEmail(email);
    }

    public List<Empleado> findByEstadoEmpleado(String estadoEmpleado) {
        return empleadoRepository.findByEstadoEmpleado(estadoEmpleado);
    }

    public List<Empleado> findByCargo(String cargo) {
        return empleadoRepository.findByCargo(cargo);
    }

    public List<Empleado> searchEmpleados(String searchTerm) {
        return empleadoRepository.findByNombreOrApellidoOrDocumentoContaining(searchTerm);
    }

    public Long countActiveEmployees() {
        return empleadoRepository.countActiveEmployees();
    }

    public boolean existsByDocumento(String documento) {
        return empleadoRepository.findByDocumento(documento).isPresent();
    }

    public boolean existsByEmail(String email) {
        return empleadoRepository.findByEmail(email).isPresent();
    }
}