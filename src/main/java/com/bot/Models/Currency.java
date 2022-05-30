package com.bot.Models;

import javax.persistence.*;

@Entity
@Table(name = "table_currency")
public class Currency {

    private Long id;

    private String ccy;

    private String base_ccy;

    private double buy;

    private double sale;

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    @Column(name = "ccy")
    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    @Column(name = "base_ccy")
    public String getBase_ccy() {
        return base_ccy;
    }

    public void setBase_ccy(String base_ccy) {
        this.base_ccy = base_ccy;
    }

    @Column(name = "buy")
    public double getBuy() {
        return buy;
    }

    public void setBuy(double buy) {
        this.buy = buy;
    }

    @Column(name = "sale")
    public double getSale() {
        return sale;
    }

    public void setSale(double sale) {
        this.sale = sale;
    }

    @Override
    public String toString() {
        return "Курс : " +
                ccy +
                " - " + base_ccy +
                ", покупка = " + buy +
                ", продаж = " + sale;
    }
}
