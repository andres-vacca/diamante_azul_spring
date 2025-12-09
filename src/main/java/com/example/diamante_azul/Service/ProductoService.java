package com.example.diamante_azul.Service;


import com.example.diamante_azul.Models.Producto;
import com.example.diamante_azul.Repository.ProductoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Autowired
    public ProductoService(ProductoRepository productoRepository){
        this.productoRepository = productoRepository;
    }

    public List<Producto> findAll(){
        return productoRepository.findAll();
    }
    public Producto guardarProducto(Producto producto){
        return  productoRepository.save(producto);
    }

    public List<Producto> findByEstado(String estado){
        return productoRepository.findByEstadoProducto(estado);
    }

    public Optional<Producto> findById(Integer id){
        return productoRepository.findById(id);
    }

    public List<Producto> findByNombre(String nombre){
        return productoRepository.findByNombre(nombre);
    }


    @Transactional
    public void softDeleteById(Integer id) {
        // Usamos el método flexible pasando el estado 'INACTIVO'
        productoRepository.actualizarEstadoProducto(id, "INACTIVO");
    }

    public List<Producto> listarProductosActivos() {
        // Llama al repositorio para obtener todos los productos con estado = "ACTIVO"
        return productoRepository.findByEstadoProducto("ACTIVO");
    }

    @Transactional
    public void actualizarEstadoProducto(Integer idProducto, String nuevoEstado) {
        // 🛑 Asegúrate de que este método tiene LLAVES, no un punto y coma
        productoRepository.actualizarEstadoProducto(idProducto, nuevoEstado);
    }
}
