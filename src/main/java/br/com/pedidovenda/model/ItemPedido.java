package br.com.pedidovenda.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "ITEM_PEDIDO")
public class ItemPedido implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ITEM_PEDIDO")
    private Long id;

    @Column(name = "QUANTIDADE", nullable = false, length = 3)
    private Integer quantidade;

    @Column(name = "VALOR_UNITARIO", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    @ManyToOne
    @JoinColumn(name = "ID_PRODUTO_FK", nullable = false)
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "ID_PEDIDO_FK", nullable = false)
    private Pedido pedido;

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemPedido that = (ItemPedido) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}