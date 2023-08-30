package generator.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName fg_shipmentinfo
 */
@TableName(value ="fg_shipmentinfo")
@Data
public class FgShipmentinfo implements Serializable {
    private Long id;

    private String shipmentNumber;

    private String pn;

    private String po;

    private Date shipmentDate;

    private String shipmentCar;

    private Long status;

    private String plant;

    private Double quantity;

    private Double pelletQty;

    private String shipmentType;

    private String remark;

    private Double boxcount;

    private String carno;

    private Date careta;

    private String lastComfirm;

    private String clientCode;

    private String clientPn;

    private String shipmentPlace;

    private String confirmTime;

    private String updateDatetime;

    private static final long serialVersionUID = 1L;
}