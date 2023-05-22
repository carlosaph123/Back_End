package co.shop.luxury.model;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import javax.persistence.*;
import java.io.Serializable;


@NamedQuery(name="Order.getAllOrders", query = "select o from Order o order by o.id desc")
@NamedQuery(name="Order.getOrderByUsername", query = "select o from Order o where o.createdBy=:username order by o.id desc")

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "orders   ")

public class Order implements Serializable {

    private static final long serialVersionUId=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "contactnumber")
    private String contactNumber;

    @Column(name = "paymethod")
    private String payMethod;

    @Column(name = "total")
    private Integer total;

    @Column(name = "productdetails", columnDefinition = "json")
    private String productDetails;

    @Column(name = "createdby")
    private String createdBy;



}
