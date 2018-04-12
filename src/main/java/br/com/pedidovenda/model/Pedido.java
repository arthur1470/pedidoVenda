package br.com.pedidovenda.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "PEDIDO")
public class Pedido implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PEDIDO")
    private Long id;

    @NotNull
    @Column(name = "DATA_CRIACAO", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCriacao;

    @NotNull
    @Column(name = "DATA_ENTREGA", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataEntrega;

    @Column(name = "OBSERVACAO", columnDefinition = "text")
    private String observacao;

    @NotNull
    @Column(name = "VALOR_FRETE", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorFrete = BigDecimal.ZERO;

    @NotNull
    @Column(name = "VALOR_DESCONTO", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @NotNull
    @Column(name = "VALOR_TOTAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @NotNull
    @Column(name = "FORMA_PAGAMENTO", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private FormaPagamento formaPagamento;

    @NotNull
    @Column(name = "STATUS", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private StatusPedido status = StatusPedido.ORCAMENTO;

    @Embedded
    private EnderecoEntrega enderecoEntrega;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "ID_VENDEDOR_FK", nullable = false)
    private Usuario vendedor;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "ID_CLIENTE_FK", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Date getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(Date dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public BigDecimal getValorFrete() {
        return valorFrete;
    }

    public void setValorFrete(BigDecimal valorFrete) {
        this.valorFrete = valorFrete;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public EnderecoEntrega getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(EnderecoEntrega enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    public Usuario getVendedor() {
        return vendedor;
    }

    public void setVendedor(Usuario vendedor) {
        this.vendedor = vendedor;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Transient
    public boolean isNovo() {
        return getId() == null;
    }

    @Transient
    public boolean isExistente() {
        return !isNovo();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Transient
    public BigDecimal getValorSubtotal() {
        return this.getValorTotal().subtract(this.getValorFrete()).add(this.getValorDesconto());
    }

    public void recalcularValorTotal() {
        BigDecimal total = BigDecimal.ZERO;

        total = total.add(this.getValorFrete()).subtract(this.getValorDesconto());

        for (ItemPedido item : this.getItens()) {
            if (item.getProduto() != null && item.getProduto().getId() != null) {
                total = total.add(item.getValorTotal());
            }
        }

        this.setValorTotal(total);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pedido other = (Pedido) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public void adicionarItemVazio() {
        if (this.isOrcamento()) {

            Produto produto = new Produto();

            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setPedido(this);

            this.getItens().add(0, item);
        }
    }

    @Transient
    public boolean isOrcamento() {
        return StatusPedido.ORCAMENTO.equals(this.getStatus());
    }

    public void removerItemVazio() {
        ItemPedido primeiroItem = this.getItens().get(0);
        if (primeiroItem != null && primeiroItem.getId() == null) {
            this.getItens().remove(0);
        }
    }

    @Transient
    public boolean isValorTotalNegativo() {
        return this.getValorTotal().compareTo(BigDecimal.ZERO) < 0;
    }

    @Transient
    public boolean isEmitido() {
        return StatusPedido.EMITIDO.equals(this.getStatus());
    }

    @Transient
    public boolean isNaoEmissivel() {
        return !this.isEmissivel();
    }

    @Transient
    private boolean isEmissivel() {
        return this.isExistente() && this.isOrcamento();
    }
}
