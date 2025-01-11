package com.williamfq.xhat.filters.base

abstract class Filter {
    // Método abstracto que debe ser implementado por cada filtro
    abstract fun applyFilter(input: Any): Any

    // Método para obtener una descripción del filtro (opcional)
    open fun getDescription(): String {
        return "Base Filter"
    }
}
