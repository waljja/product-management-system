package generator.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName fg_inventory
 */
@TableName(value ="fg_inventory")
@Data
public class FgInventory implements Serializable {
    private Long id;

    private String partnumber;

    private String pn650;

    private String po;

    private String batch;

    private String uid;

    private Double quantity;

    private Double tagsQuantity;

    private String stock;

    private Boolean status;

    private String wo;

    private Date recTime;

    private Integer plant;

    private String createUser;

    private Date createTime;

    private Long uidId;

    private Long replaceStatus;

    private String rollbackReason;

    private static final long serialVersionUID = 1L;
}