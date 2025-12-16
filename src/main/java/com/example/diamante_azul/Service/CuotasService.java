package com.example.diamante_azul.Service;

import com.example.diamante_azul.Models.Cuotas;
import com.example.diamante_azul.Repository.CuotasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CuotasService {

    @Autowired
    private CuotasRepository cuotasRepository;

    public List<Cuotas> findAll() {
        return cuotasRepository.findAll();
    }
    public List<Cuotas> findByUsuarioClienteId(Integer usuarioId) {
        return cuotasRepository.findByUsuarioCliente_Id(usuarioId);
    }

    public Optional<Cuotas> findById(Long id) {
        return cuotasRepository.findById(id);
    }

    public Cuotas save(Cuotas cuota) {
        return cuotasRepository.save(cuota);
    }

    public void deleteById(Long id) {
        cuotasRepository.deleteById(id);
    }

    public List<Cuotas> findByEstadoCuota(String estadoCuota) {
        return cuotasRepository.findByEstadoCuota(estadoCuota);
    }

    public List<Cuotas> findCuotasVencidas() {
        return cuotasRepository.findByFechaVencimientoBeforeAndEstadoCuota(LocalDate.now(), "PENDIENTE");
    }

    public List<Cuotas> findCuotasProximasAVencer(int dias) {
        LocalDate fechaLimite = LocalDate.now().plusDays(dias);
        return cuotasRepository.findByFechaVencimientoBetweenAndEstadoCuota(LocalDate.now(), fechaLimite, "PENDIENTE");
    }

    public Long countAll() {
        return cuotasRepository.count();
    }

    public Long countByEstado(String estado) {
        return cuotasRepository.countByEstadoCuota(estado);
    }

    public Long countCuotasVencidas() {
        return cuotasRepository.countByFechaVencimientoBeforeAndEstadoCuota(LocalDate.now(), "PENDIENTE");
    }

    public List<Cuotas> findByClienteId(Integer clienteId) {
        return cuotasRepository.findByClienteId(clienteId);
    }


}