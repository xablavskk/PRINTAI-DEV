package com.printai.model;

public enum TipoPedido {
    /** Cliente envia arquivo STL/OBJ já pronto */
    COM_MODELO,
    /** Cliente descreve a necessidade; Maker define o modelo e parâmetros */
    SEM_MODELO
}
